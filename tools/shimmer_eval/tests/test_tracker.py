import pytest

from shimmer_eval.tracker import IouTracker, run_tracker
from shimmer_eval.types import Box, Detection


def moving_detections(
    n_frames: int, x0: float = 10.0, dx: float = 3.0, y: float = 20.0, size: float = 10.0
) -> list[Detection]:
    return [
        Detection(
            frame=f,
            box=Box.from_center(x0 + dx * f, y, size / 2, size / 2),
            score=0.9,
        )
        for f in range(n_frames)
    ]


def test_single_object_keeps_one_id():
    dets = moving_detections(30)
    tracks = run_tracker(dets, 30)
    ids = {t.track_id for t in tracks}
    assert len(ids) == 1
    assert len(tracks) == 30 - 3 + 1  # min_hits frames to confirm


def test_confirmation_requires_min_hits():
    tracker = IouTracker(min_hits=4)
    dets = moving_detections(10)
    out = []
    for f in range(10):
        out.extend(tracker.update(f, [dets[f]]))
    assert min(t.frame for t in out) == 3  # 4th frame is the first confirmed one


def test_identity_survives_a_detection_gap():
    """Coasting across an occlusion is the whole point of max_age."""
    dets = [d for d in moving_detections(40) if not (15 <= d.frame < 23)]
    tracks = run_tracker(dets, 40, IouTracker(max_age=12))
    assert len({t.track_id for t in tracks}) == 1


def test_identity_is_dropped_after_max_age():
    dets = [d for d in moving_detections(60) if not (15 <= d.frame < 45)]
    tracks = run_tracker(dets, 60, IouTracker(max_age=5))
    assert len({t.track_id for t in tracks}) == 2


def test_coasting_boxes_are_suppressed_by_default():
    dets = [d for d in moving_detections(40) if not (15 <= d.frame < 23)]
    tracks = run_tracker(dets, 40, IouTracker(max_age=12))
    assert all(not t.coasting for t in tracks)
    assert {t.frame for t in tracks}.isdisjoint(range(15, 23))


def test_coasting_boxes_can_be_emitted():
    dets = [d for d in moving_detections(40) if not (15 <= d.frame < 23)]
    tracks = run_tracker(dets, 40, IouTracker(max_age=12, emit_coasting=True))
    coasting = [t for t in tracks if t.coasting]
    assert coasting
    assert all(15 <= t.frame < 23 for t in coasting)


def test_velocity_prediction_carries_a_fast_object():
    """A fast object's boxes never overlap frame to frame; only prediction saves it."""
    dets = moving_detections(30, dx=14.0, size=10.0)
    tracks = run_tracker(dets, 30, IouTracker(min_iou=0.2, gate_scale=1.75))
    assert len({t.track_id for t in tracks}) == 1


def test_two_separated_objects_get_distinct_ids():
    a = moving_detections(25, x0=10.0, dx=2.0, y=20.0)
    b = moving_detections(25, x0=10.0, dx=2.0, y=90.0)
    tracks = run_tracker(a + b, 25)
    assert len({t.track_id for t in tracks}) == 2
    for frame in range(5, 25):
        assert len({t.track_id for t in tracks if t.frame == frame}) == 2


def test_no_detections_produces_no_tracks():
    assert run_tracker([], 20) == []


def test_reset_clears_state():
    tracker = IouTracker()
    for f, d in enumerate(moving_detections(10)):
        tracker.update(f, [d])
    tracker.reset()
    out = tracker.update(0, [])
    assert out == []


def test_track_box_follows_the_detection():
    dets = moving_detections(20, x0=10.0, dx=3.0)
    tracks = run_tracker(dets, 20)
    last = max(tracks, key=lambda t: t.frame)
    expected = dets[last.frame].box
    assert last.box.cx == pytest.approx(expected.cx, abs=2.0)
    assert last.box.cy == pytest.approx(expected.cy, abs=2.0)
