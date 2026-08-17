import pytest

from shimmer_eval.metrics import evaluate_detections, evaluate_tracks, match_boxes
from shimmer_eval.types import Box, Detection, GtObject, TrackOutput


def gt(frame, obj_id, cx, cy, visible=True, size=10.0):
    return GtObject(
        frame=frame,
        obj_id=obj_id,
        box=Box.from_center(cx, cy, size / 2, size / 2),
        visible=visible,
        shimmer_amp=0.5,
        shimmer_hz=6.0,
        speed=1.0,
    )


def track(frame, track_id, cx, cy, size=10.0):
    return TrackOutput(
        frame=frame,
        track_id=track_id,
        box=Box.from_center(cx, cy, size / 2, size / 2),
        score=1.0,
    )


def test_match_boxes_pairs_best_first():
    gts = [Box(0, 0, 10, 10), Box(100, 100, 110, 110)]
    preds = [Box(100, 100, 110, 110), Box(1, 1, 11, 11)]
    matches = match_boxes(gts, preds, iou_threshold=0.5)
    assert sorted((g, p) for g, p, _ in matches) == [(0, 1), (1, 0)]


def test_match_boxes_respects_threshold():
    assert match_boxes([Box(0, 0, 10, 10)], [Box(8, 8, 18, 18)], iou_threshold=0.5) == []


def test_match_boxes_is_one_to_one():
    gts = [Box(0, 0, 10, 10)]
    preds = [Box(0, 0, 10, 10), Box(1, 1, 11, 11)]
    assert len(match_boxes(gts, preds, 0.5)) == 1


def test_perfect_tracking_scores_one():
    ground = [gt(f, 0, 10 + f, 20) for f in range(10)]
    tracks = [track(f, 7, 10 + f, 20) for f in range(10)]
    m = evaluate_tracks(ground, tracks, 10)
    assert m.precision == pytest.approx(1.0)
    assert m.recall == pytest.approx(1.0)
    assert m.f1 == pytest.approx(1.0)
    assert m.mota == pytest.approx(1.0)
    assert m.id_switches == 0
    assert m.fragmentations == 0
    assert m.mostly_tracked == 1
    assert m.center_rmse == pytest.approx(0.0)


def test_missed_object_counts_as_false_negative():
    ground = [gt(f, 0, 10, 20) for f in range(10)]
    m = evaluate_tracks(ground, [], 10)
    assert m.fn == 10 and m.tp == 0 and m.fp == 0
    assert m.recall == 0.0
    assert m.mostly_lost == 1
    assert m.mota == pytest.approx(0.0)


def test_spurious_track_counts_as_false_positive():
    tracks = [track(f, 1, 200, 200) for f in range(10)]
    ground = [gt(f, 0, 10, 20) for f in range(10)]
    m = evaluate_tracks(ground, tracks, 10)
    assert m.fp == 10 and m.fn == 10
    assert m.mota == pytest.approx(1.0 - 20 / 10)


def test_id_switch_is_counted_once():
    ground = [gt(f, 0, 10 + f, 20) for f in range(10)]
    tracks = [track(f, 1 if f < 5 else 2, 10 + f, 20) for f in range(10)]
    m = evaluate_tracks(ground, tracks, 10)
    assert m.id_switches == 1
    assert m.tp == 10


def test_invisible_ground_truth_is_not_scored():
    ground = [gt(f, 0, 10, 20, visible=(f < 5)) for f in range(10)]
    m = evaluate_tracks(ground, [], 10)
    assert m.num_gt == 5
    assert m.fn == 5


def test_identity_across_an_occlusion_gap_is_not_a_switch():
    ground = [gt(f, 0, 10 + f, 20, visible=not (4 <= f < 7)) for f in range(12)]
    tracks = [track(f, 3, 10 + f, 20) for f in range(12) if not (4 <= f < 7)]
    m = evaluate_tracks(ground, tracks, 12)
    assert m.id_switches == 0
    assert m.fragmentations == 0


def test_fragmentation_counts_an_interrupted_trajectory():
    ground = [gt(f, 0, 10 + f, 20) for f in range(12)]
    tracks = [track(f, 3, 10 + f, 20) for f in range(12) if not (4 <= f < 7)]
    m = evaluate_tracks(ground, tracks, 12)
    assert m.fragmentations == 1
    assert m.id_switches == 0


def test_warmup_and_cooldown_exclude_frames():
    ground = [gt(f, 0, 10, 20) for f in range(10)]
    tracks = [track(f, 1, 10, 20) for f in range(3, 8)]
    m = evaluate_tracks(ground, tracks, 10, warmup=3, cooldown=2)
    assert m.frames_scored == 5
    assert m.tp == 5 and m.fn == 0 and m.fp == 0


def test_mostly_tracked_thresholds():
    ground = [gt(f, 0, 10, 20) for f in range(10)]
    half = [track(f, 1, 10, 20) for f in range(5)]
    m = evaluate_tracks(ground, half, 10)
    assert m.partially_tracked == 1
    assert m.mostly_tracked == 0 and m.mostly_lost == 0


def test_evaluate_detections_zeroes_identity_metrics():
    ground = [gt(f, 0, 10 + f, 20) for f in range(10)]
    dets = [
        Detection(frame=f, box=Box.from_center(10 + f, 20, 5, 5), score=1.0)
        for f in range(10)
    ]
    m = evaluate_detections(ground, dets, 10)
    assert m.f1 == pytest.approx(1.0)
    assert m.id_switches == 0 and m.fragmentations == 0


def test_center_rmse_reflects_offset():
    ground = [gt(f, 0, 10, 20) for f in range(4)]
    tracks = [track(f, 1, 13, 24) for f in range(4)]  # offset (3, 4) -> 5
    m = evaluate_tracks(ground, tracks, 4, iou_threshold=0.1)
    assert m.center_rmse == pytest.approx(5.0)


def test_metrics_as_dict_is_serialisable():
    m = evaluate_tracks([gt(0, 0, 10, 20)], [track(0, 1, 10, 20)], 1)
    d = m.as_dict()
    assert d["tp"] == 1 and d["precision"] == 1.0
    assert set(d) >= {"mota", "id_switches", "recall", "f1", "motp_iou"}
