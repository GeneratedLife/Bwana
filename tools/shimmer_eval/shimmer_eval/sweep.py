"""Run detectors over generated scenes and sweep scene parameters.

A single accuracy number is close to useless — it tells you nothing about where
the approach breaks. The sweep varies one scene parameter at a time over
several seeds and reports the curve, so the answer is "F1 holds above 0.9 until
the object moves faster than ~3 px/frame" rather than "F1 = 0.82".
"""

from __future__ import annotations

import csv
from dataclasses import dataclass
from typing import Callable, Iterable, Sequence as TSequence

import numpy as np

from .detectors import (
    Detector,
    ShimmerDftDetector,
    ShimmerMotionDetector,
    TemplateMatchDetector,
    TemporalVarianceDetector,
)
from .generator import SceneConfig, Sequence, generate
from .metrics import Metrics, evaluate_detections, evaluate_tracks
from .tracker import IouTracker, run_tracker

DETECTOR_NAMES = (
    "template_match",
    "temporal_variance",
    "shimmer_dft",
    "shimmer_motion",
)


def make_detector(name: str, sequence: Sequence) -> Detector:
    """Build a detector for a scene.

    The template baseline gets a correctly-centred crop of the real object, and
    the frequency-tuned detectors get the scene's true shimmer rate. Both are
    best-case setups on purpose — the comparison is only interesting if each
    approach is given its best shot.
    """
    cfg = sequence.config
    if name == "template_match":
        return TemplateMatchDetector(sequence.object_template(obj_id=0, frame=0))
    if name == "temporal_variance":
        return TemporalVarianceDetector()
    if name == "shimmer_dft":
        return ShimmerDftDetector(shimmer_hz=cfg.shimmer_hz, fps=cfg.fps)
    if name == "shimmer_motion":
        return ShimmerMotionDetector(shimmer_hz=cfg.shimmer_hz, fps=cfg.fps)
    raise ValueError(f"unknown detector: {name!r}")


@dataclass
class RunResult:
    detector: str
    seed: int
    detection: Metrics
    tracking: Metrics

    def row(self) -> dict:
        out = {"detector": self.detector, "seed": self.seed}
        for key, value in self.detection.as_dict().items():
            out[f"det_{key}"] = value
        for key, value in self.tracking.as_dict().items():
            out[f"trk_{key}"] = value
        return out


def run_once(
    cfg: SceneConfig,
    detector_name: str,
    *,
    iou_threshold: float = 0.5,
    tracker_factory: Callable[[], IouTracker] | None = None,
    sequence: Sequence | None = None,
) -> RunResult:
    seq = sequence if sequence is not None else generate(cfg)
    detector = make_detector(detector_name, seq)
    detections = detector.run(seq.frames)
    warmup = getattr(detector, "warmup", 0)
    cooldown = getattr(detector, "cooldown", 0)

    tracker = (tracker_factory or IouTracker)()
    tracks = run_tracker(detections, seq.num_frames, tracker=tracker)

    # A tracker needs min_hits frames to confirm, so give it that on top of the
    # detector's own warm-up before scoring identities.
    track_warmup = warmup + tracker.min_hits

    return RunResult(
        detector=detector_name,
        seed=cfg.seed,
        detection=evaluate_detections(
            seq.gt,
            detections,
            seq.num_frames,
            iou_threshold=iou_threshold,
            warmup=warmup,
            cooldown=cooldown,
        ),
        tracking=evaluate_tracks(
            seq.gt,
            tracks,
            seq.num_frames,
            iou_threshold=iou_threshold,
            warmup=track_warmup,
            cooldown=cooldown,
        ),
    )


def run_config(
    cfg: SceneConfig,
    detectors: TSequence[str] = DETECTOR_NAMES,
    *,
    seeds: TSequence[int] = (0,),
    iou_threshold: float = 0.5,
) -> list[RunResult]:
    """Run every detector over every seed for one scene configuration."""
    results: list[RunResult] = []
    for seed in seeds:
        seq = generate(cfg.with_(seed=seed))
        for name in detectors:
            results.append(
                run_once(
                    cfg.with_(seed=seed),
                    name,
                    iou_threshold=iou_threshold,
                    sequence=seq,
                )
            )
    return results


def sweep(
    base: SceneConfig,
    parameter: str,
    values: Iterable,
    *,
    detectors: TSequence[str] = DETECTOR_NAMES,
    seeds: TSequence[int] = (0, 1, 2),
    iou_threshold: float = 0.5,
) -> list[dict]:
    """Vary one scene parameter, averaging each point over ``seeds``."""
    if not hasattr(base, parameter):
        raise ValueError(f"SceneConfig has no parameter {parameter!r}")

    rows: list[dict] = []
    for value in values:
        cfg = base.with_(**{parameter: value})
        results = run_config(cfg, detectors, seeds=seeds, iou_threshold=iou_threshold)
        for name in detectors:
            subset = [r for r in results if r.detector == name]
            rows.append(
                {
                    "parameter": parameter,
                    "value": value,
                    "detector": name,
                    "det_f1_mean": float(np.mean([r.detection.f1 for r in subset])),
                    "det_f1_std": float(np.std([r.detection.f1 for r in subset])),
                    "det_precision_mean": float(
                        np.mean([r.detection.precision for r in subset])
                    ),
                    "det_recall_mean": float(np.mean([r.detection.recall for r in subset])),
                    "trk_mota_mean": float(np.mean([r.tracking.mota for r in subset])),
                    "trk_mota_std": float(np.std([r.tracking.mota for r in subset])),
                    "trk_idsw_mean": float(
                        np.mean([r.tracking.id_switches for r in subset])
                    ),
                    "trk_frag_mean": float(
                        np.mean([r.tracking.fragmentations for r in subset])
                    ),
                    "trk_center_rmse_mean": float(
                        np.mean(
                            [
                                r.tracking.center_rmse
                                for r in subset
                                if r.tracking.tp > 0
                            ]
                        )
                    )
                    if any(r.tracking.tp > 0 for r in subset)
                    else float("nan"),
                    "trk_mostly_tracked_mean": float(
                        np.mean([r.tracking.mostly_tracked for r in subset])
                    ),
                    "seeds": len(seeds),
                }
            )
    return rows


def write_csv(rows: TSequence[dict], path: str) -> None:
    if not rows:
        return
    with open(path, "w", newline="", encoding="utf-8") as fh:
        writer = csv.DictWriter(fh, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)


def format_table(rows: TSequence[dict], columns: TSequence[str]) -> str:
    """Render rows as a fixed-width table."""
    if not rows:
        return "(no rows)"

    def cell(value) -> str:
        if isinstance(value, float):
            return "nan" if value != value else f"{value:.3f}"
        return str(value)

    widths = {
        c: max(len(c), *(len(cell(r.get(c, ""))) for r in rows)) for c in columns
    }
    lines = [" ".join(c.ljust(widths[c]) for c in columns)]
    lines.append(" ".join("-" * widths[c] for c in columns))
    for r in rows:
        lines.append(" ".join(cell(r.get(c, "")).ljust(widths[c]) for c in columns))
    return "\n".join(lines)
