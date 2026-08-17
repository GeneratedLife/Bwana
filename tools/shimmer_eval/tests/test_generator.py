import numpy as np
import pytest

from shimmer_eval.generator import SceneConfig, generate

SMALL = SceneConfig(
    width=96,
    height=72,
    num_frames=24,
    num_objects=1,
    object_radius=7.0,
    num_static_clutter=3,
    num_shimmer_distractors=0,
    num_motion_distractors=0,
    num_occluders=0,
    seed=0,
)


def test_shapes_and_range():
    seq = generate(SMALL)
    assert seq.frames.shape == (24, 72, 96)
    assert seq.frames.dtype == np.float32
    assert seq.frames.min() >= 0.0 and seq.frames.max() <= 1.0


def test_ground_truth_row_per_object_per_frame():
    seq = generate(SMALL.with_(num_objects=3))
    assert len(seq.gt) == 24 * 3
    assert {g.obj_id for g in seq.gt} == {0, 1, 2}
    for frame in range(24):
        assert len([g for g in seq.gt if g.frame == frame]) == 3


def test_same_seed_is_reproducible_and_different_seed_is_not():
    a = generate(SMALL)
    b = generate(SMALL)
    c = generate(SMALL.with_(seed=1))
    assert np.array_equal(a.frames, b.frames)
    assert [g.box.as_tuple() for g in a.gt] == [g.box.as_tuple() for g in b.gt]
    assert not np.array_equal(a.frames, c.frames)


def test_ground_truth_box_lands_on_the_object():
    """The brightest pixel of a clean scene must sit inside the labelled box."""
    cfg = SMALL.with_(
        num_static_clutter=0, noise_sigma=0.0, background_level=0.0, base_intensity=0.9
    )
    seq = generate(cfg)
    for frame in (0, 5, 12, 23):
        gt = next(g for g in seq.gt if g.frame == frame)
        img = seq.frames[frame]
        y, x = np.unravel_index(int(np.argmax(img)), img.shape)
        assert gt.box.x0 <= x <= gt.box.x1
        assert gt.box.y0 <= y <= gt.box.y1


def test_object_stays_inside_the_frame():
    seq = generate(SMALL.with_(speed=4.0, num_frames=120))
    for g in seq.gt:
        assert g.box.x0 >= -0.5 and g.box.y0 >= -0.5
        assert g.box.x1 <= seq.config.width + 0.5
        assert g.box.y1 <= seq.config.height + 0.5


def test_stationary_unshimmering_object_yields_static_frames():
    cfg = SMALL.with_(speed=0.0, shimmer_amp=0.0, noise_sigma=0.0)
    seq = generate(cfg)
    assert np.allclose(seq.frames[0], seq.frames[-1])


def test_shimmer_produces_temporal_variation_at_the_object():
    cfg = SMALL.with_(speed=0.0, noise_sigma=0.0, shimmer_amp=0.8)
    seq = generate(cfg)
    gt = next(g for g in seq.gt if g.frame == 0)
    cy, cx = int(gt.box.cy), int(gt.box.cx)
    interior = seq.frames[:, cy, cx].std()
    corner = seq.frames[:, 0, 0].std()
    assert interior > 0.02
    assert corner == pytest.approx(0.0, abs=1e-6)


def test_speed_recorded_in_ground_truth():
    seq = generate(SMALL.with_(speed=2.5))
    assert all(g.speed == pytest.approx(2.5, abs=1e-6) for g in seq.gt)


def test_occluder_marks_some_frames_invisible():
    cfg = SMALL.with_(
        num_occluders=1,
        occluder_size=(40, 72),
        num_frames=160,
        speed=1.5,
    )
    seq = generate(cfg)
    visibility = {g.visible for g in seq.gt}
    assert visibility == {True, False}, "a wide occluder should hide the object sometimes"


def test_no_occluders_means_always_visible():
    seq = generate(SMALL.with_(num_occluders=0))
    assert all(g.visible for g in seq.gt)


def test_object_template_matches_ground_truth_box():
    seq = generate(SMALL)
    gt = next(g for g in seq.gt if g.frame == 0 and g.obj_id == 0)
    template = seq.object_template(0, 0)
    expected_h = int(round(gt.box.y1)) - int(round(gt.box.y0))
    expected_w = int(round(gt.box.x1)) - int(round(gt.box.x0))
    assert template.shape == (expected_h, expected_w)


def test_object_template_rejects_unknown_object():
    seq = generate(SMALL)
    with pytest.raises(ValueError):
        seq.object_template(obj_id=99, frame=0)


def test_write_labels_roundtrip(tmp_path):
    import json

    from shimmer_eval.types import GtObject

    seq = generate(SMALL)
    path = tmp_path / "labels.jsonl"
    seq.write_labels(str(path))

    lines = path.read_text().splitlines()
    assert json.loads(lines[0])["config"]["width"] == 96
    restored = [GtObject.from_json(json.loads(line)) for line in lines[1:]]
    assert len(restored) == len(seq.gt)
    assert restored[0].box.as_tuple() == pytest.approx(
        sorted(seq.gt, key=lambda g: (g.frame, g.obj_id))[0].box.as_tuple()
    )


def test_distractors_are_not_ground_truth():
    """Distractors must never appear as scoreable objects."""
    seq = generate(SMALL.with_(num_shimmer_distractors=3, num_motion_distractors=3))
    assert {g.obj_id for g in seq.gt} == {0}
