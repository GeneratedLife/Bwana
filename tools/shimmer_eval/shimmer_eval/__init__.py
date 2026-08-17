"""Detection and tracking accuracy harness for moving, shimmering objects.

Pipeline: ``generator`` (frames + exact ground truth) -> ``detectors``
(frame -> boxes) -> ``tracker`` (boxes -> identities) -> ``metrics``
(vs ground truth) -> ``sweep`` (accuracy as a function of scene difficulty).
"""

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
from .types import Box, Detection, GtObject, TrackOutput

__all__ = [
    "Box",
    "Detection",
    "Detector",
    "GtObject",
    "IouTracker",
    "Metrics",
    "SceneConfig",
    "Sequence",
    "ShimmerDftDetector",
    "ShimmerMotionDetector",
    "TemplateMatchDetector",
    "TemporalVarianceDetector",
    "TrackOutput",
    "evaluate_detections",
    "evaluate_tracks",
    "generate",
    "run_tracker",
]
