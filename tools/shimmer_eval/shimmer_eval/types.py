"""Core value types shared by the generator, detectors, tracker and metrics.

Boxes are axis-aligned, in pixel coordinates, with ``x1``/``y1`` exclusive.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Sequence


@dataclass(frozen=True)
class Box:
    x0: float
    y0: float
    x1: float
    y1: float

    @property
    def w(self) -> float:
        return max(0.0, self.x1 - self.x0)

    @property
    def h(self) -> float:
        return max(0.0, self.y1 - self.y0)

    @property
    def area(self) -> float:
        return self.w * self.h

    @property
    def cx(self) -> float:
        return 0.5 * (self.x0 + self.x1)

    @property
    def cy(self) -> float:
        return 0.5 * (self.y0 + self.y1)

    @property
    def center(self) -> tuple[float, float]:
        return (self.cx, self.cy)

    @classmethod
    def from_center(cls, cx: float, cy: float, half_w: float, half_h: float) -> "Box":
        return cls(cx - half_w, cy - half_h, cx + half_w, cy + half_h)

    def intersection_area(self, other: "Box") -> float:
        w = min(self.x1, other.x1) - max(self.x0, other.x0)
        h = min(self.y1, other.y1) - max(self.y0, other.y0)
        if w <= 0.0 or h <= 0.0:
            return 0.0
        return w * h

    def iou(self, other: "Box") -> float:
        inter = self.intersection_area(other)
        if inter <= 0.0:
            return 0.0
        union = self.area + other.area - inter
        if union <= 0.0:
            return 0.0
        return inter / union

    def distance_to(self, other: "Box") -> float:
        dx = self.cx - other.cx
        dy = self.cy - other.cy
        return (dx * dx + dy * dy) ** 0.5

    def clipped(self, width: float, height: float) -> "Box":
        return Box(
            max(0.0, self.x0),
            max(0.0, self.y0),
            min(width, self.x1),
            min(height, self.y1),
        )

    def as_tuple(self) -> tuple[float, float, float, float]:
        return (self.x0, self.y0, self.x1, self.y1)


@dataclass(frozen=True)
class Detection:
    """A single-frame detection produced by a detector."""

    frame: int
    box: Box
    score: float


@dataclass(frozen=True)
class GtObject:
    """Ground truth for one object in one frame.

    ``visible`` is False when the object is mostly hidden behind an occluder.
    Invisible rows are excluded from recall so a tracker is not punished for
    failing to detect something that is not on screen, but they are kept in the
    file so identity across an occlusion gap can be scored.
    """

    frame: int
    obj_id: int
    box: Box
    visible: bool
    shimmer_amp: float
    shimmer_hz: float
    speed: float

    def to_json(self) -> dict:
        return {
            "frame": self.frame,
            "obj_id": self.obj_id,
            "bbox": list(self.box.as_tuple()),
            "visible": self.visible,
            "shimmer_amp": self.shimmer_amp,
            "shimmer_hz": self.shimmer_hz,
            "speed": self.speed,
        }

    @classmethod
    def from_json(cls, d: dict) -> "GtObject":
        return cls(
            frame=int(d["frame"]),
            obj_id=int(d["obj_id"]),
            box=Box(*[float(v) for v in d["bbox"]]),
            visible=bool(d["visible"]),
            shimmer_amp=float(d["shimmer_amp"]),
            shimmer_hz=float(d["shimmer_hz"]),
            speed=float(d["speed"]),
        )


@dataclass(frozen=True)
class TrackOutput:
    """A tracker's report for one confirmed track in one frame."""

    frame: int
    track_id: int
    box: Box
    score: float
    coasting: bool = False


def group_by_frame(items: Sequence) -> dict[int, list]:
    """Bucket any sequence of frame-stamped records into ``{frame: [...]}``."""
    out: dict[int, list] = {}
    for it in items:
        out.setdefault(it.frame, []).append(it)
    return out
