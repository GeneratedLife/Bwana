import numpy as np
import pytest

from shimmer_eval.detectors import (
    ShimmerDftDetector,
    ShimmerMotionDetector,
    TemplateMatchDetector,
    TemporalVarianceDetector,
    greedy_nms,
    normalised_cross_correlation,
)
from shimmer_eval.generator import SceneConfig, generate
from shimmer_eval.metrics import evaluate_detections
from shimmer_eval.types import Box

EASY = SceneConfig(
    width=128,
    height=96,
    num_frames=48,
    num_objects=1,
    object_radius=8.0,
    speed=0.8,
    shimmer_amp=0.7,
    num_static_clutter=2,
    num_shimmer_distractors=0,
    num_motion_distractors=0,
    num_occluders=0,
    noise_sigma=0.01,
    seed=0,
)


def test_ncc_peaks_at_the_planted_location():
    rng = np.random.default_rng(0)
    image = rng.normal(0.5, 0.05, size=(40, 50))
    template = rng.normal(0.5, 0.3, size=(6, 7))
    image[10:16, 20:27] = template

    ncc = normalised_cross_correlation(image, template)
    y, x = np.unravel_index(int(np.argmax(ncc)), ncc.shape)
    assert (y, x) == (10, 20)
    assert ncc[y, x] == pytest.approx(1.0, abs=1e-6)


def test_ncc_output_shape_is_valid_alignment():
    ncc = normalised_cross_correlation(np.zeros((20, 30)), np.ones((4, 5)))
    assert ncc.shape == (20 - 4 + 1, 30 - 5 + 1)


def test_ncc_rejects_oversized_template():
    with pytest.raises(ValueError):
        normalised_cross_correlation(np.zeros((4, 4)), np.zeros((5, 5)))


def test_greedy_nms_keeps_best_and_drops_overlap():
    a = Box(0, 0, 10, 10)
    b = Box(1, 1, 11, 11)  # heavy overlap with a
    c = Box(50, 50, 60, 60)
    kept = greedy_nms([(a, 0.5), (b, 0.9), (c, 0.1)], iou_threshold=0.3)
    assert [box for box, _ in kept] == [b, c]


@pytest.mark.parametrize(
    "factory",
    [
        lambda: TemporalVarianceDetector(window=6),
        lambda: ShimmerDftDetector(shimmer_hz=6.0, fps=30.0),
        lambda: ShimmerMotionDetector(shimmer_hz=6.0, fps=30.0),
    ],
)
def test_windowed_detectors_emit_nothing_during_warmup(factory):
    detector = factory()
    frames = generate(EASY).frames
    for i in range(detector.window - 1):
        assert detector.push(frames[i]) == []


@pytest.mark.parametrize(
    "factory",
    [
        lambda: TemporalVarianceDetector(window=6),
        lambda: ShimmerDftDetector(shimmer_hz=6.0, fps=30.0),
        lambda: ShimmerMotionDetector(shimmer_hz=6.0, fps=30.0),
    ],
)
def test_emitted_frame_indices_respect_warmup_and_cooldown(factory):
    detector = factory()
    seq = generate(EASY)
    dets = detector.run(seq.frames)
    assert dets, "detector produced nothing at all"
    indices = [d.frame for d in dets]
    assert min(indices) >= detector.warmup
    assert max(indices) <= seq.num_frames - 1 - detector.cooldown


def test_reset_clears_history():
    detector = ShimmerMotionDetector(shimmer_hz=6.0, fps=30.0)
    frames = generate(EASY).frames
    detector.run(frames)
    detector.reset()
    assert detector.push(frames[0]) == []


def test_conjunction_detector_finds_a_moving_shimmering_object():
    seq = generate(EASY)
    detector = ShimmerMotionDetector(shimmer_hz=seq.config.shimmer_hz, fps=seq.config.fps)
    dets = detector.run(seq.frames)
    m = evaluate_detections(
        seq.gt,
        dets,
        seq.num_frames,
        warmup=detector.warmup,
        cooldown=detector.cooldown,
    )
    assert m.recall > 0.8
    assert m.precision > 0.8


def _lone_subject(**overrides):
    """A scene containing exactly one blob and nothing else."""
    empty = {
        "num_objects": 0,
        "num_shimmer_distractors": 0,
        "num_motion_distractors": 0,
        "num_static_clutter": 0,
    }
    return EASY.with_(**{**empty, **overrides})


def _peak_response(cfg, **detector_kwargs) -> float:
    """Strongest raw response in a scene, with the absolute floor disabled.

    The floor is left out so this measures the detector's actual discrimination
    rather than a calibration constant.
    """
    seq = generate(cfg)
    detector = ShimmerMotionDetector(
        shimmer_hz=cfg.shimmer_hz, fps=cfg.fps, abs_floor=0.0, **detector_kwargs
    )
    dets = detector.run(seq.frames)
    return max((d.score for d in dets), default=0.0)


def test_conjunction_response_is_far_weaker_on_a_shimmering_statue():
    """A stationary shimmerer satisfies half the signature; it must score far lower.

    Asserted as a margin rather than as zero detections, because the threshold
    is adaptive: on a scene containing nothing else, whatever is strongest wins.
    The claim that matters is that a real target outscores the decoy by enough
    for a shared threshold to separate them.
    """
    target = _peak_response(_lone_subject(num_objects=1))
    statue = _peak_response(_lone_subject(num_shimmer_distractors=1))
    assert statue < 0.5 * target


def test_conjunction_response_is_far_weaker_on_a_plain_mover():
    target = _peak_response(_lone_subject(num_objects=1))
    mover = _peak_response(_lone_subject(num_motion_distractors=1))
    assert mover < 0.7 * target


def test_conjunction_detector_picks_the_target_out_of_a_decoy_crowd():
    """The claim in context: with both decoy types present, detections land on the target."""
    cfg = EASY.with_(num_shimmer_distractors=2, num_motion_distractors=2)
    seq = generate(cfg)
    detector = ShimmerMotionDetector(shimmer_hz=cfg.shimmer_hz, fps=cfg.fps)
    m = evaluate_detections(
        seq.gt,
        detector.run(seq.frames),
        seq.num_frames,
        warmup=detector.warmup,
        cooldown=detector.cooldown,
    )
    assert m.precision > 0.8
    assert m.recall > 0.8


def test_frequency_mismatch_costs_the_tuned_detector():
    """Tuning to the wrong rate must actually hurt, and only an explicit
    mismatch shows it — the sweep tunes the detector to the scene."""
    cfg = EASY.with_(shimmer_hz=6.0)
    seq = generate(cfg)

    def f1_for(assumed_hz: float) -> float:
        detector = ShimmerMotionDetector(shimmer_hz=assumed_hz, fps=cfg.fps)
        return evaluate_detections(
            seq.gt,
            detector.run(seq.frames),
            seq.num_frames,
            warmup=detector.warmup,
            cooldown=detector.cooldown,
        ).f1

    assert f1_for(6.0) > f1_for(1.5) + 0.2


def test_variance_detector_is_fooled_by_both_distractors():
    """Contrast case: 'anything that changed' cannot reject either decoy."""
    cfg = EASY.with_(
        num_objects=0,
        num_shimmer_distractors=1,
        num_motion_distractors=1,
        num_static_clutter=0,
    )
    seq = generate(cfg)
    assert len(TemporalVarianceDetector().run(seq.frames)) > 0


def test_template_matching_degrades_as_shimmer_rises():
    """The headline claim: a fixed template cannot hold a shimmering target."""
    calm = EASY.with_(shimmer_amp=0.0)
    lively = EASY.with_(shimmer_amp=0.9)

    recalls = []
    for cfg in (calm, lively):
        seq = generate(cfg)
        detector = TemplateMatchDetector(seq.object_template(0, 0))
        m = evaluate_detections(seq.gt, detector.run(seq.frames), seq.num_frames)
        recalls.append(m.recall)

    calm_recall, lively_recall = recalls
    assert calm_recall > 0.9, "template matching should be near-perfect without shimmer"
    assert lively_recall < calm_recall - 0.3


def test_detector_rejects_bad_window():
    with pytest.raises(ValueError):
        TemporalVarianceDetector(window=1)
