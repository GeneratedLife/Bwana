"""Synthetic scene generator with exact ground truth.

The point of synthesising the data is that we know the answer. Every frame is
emitted alongside the exact box of every object, so detection and tracking
accuracy are measurable rather than eyeballed.

Scene model
-----------
An *object* is a Gaussian blob that simultaneously

  * moves, at ``speed`` px/frame in a fixed direction, bouncing off the walls, and
  * shimmers, its brightness modulated by ``shimmer_amp * sin(2*pi*f*t + phi(x,y))``.

The phase ``phi`` varies spatially across the blob, so it sparkles rather than
pulsing as one flat disc — that spatial incoherence is what makes real
shimmering things hard, and what a naive fixed template chokes on.

Three kinds of distractor exist so a detector cannot score well by cheating:

  * static clutter — bright blobs that neither move nor shimmer,
  * shimmer-only distractors — shimmer in place, never move,
  * motion-only distractors — move at the same speed, never shimmer.

A detector that keys on "anything that changes" will light up on the last two.
"""

from __future__ import annotations

import json
import math
from dataclasses import dataclass, replace
from typing import Iterable

import numpy as np

from .imageops import box_mean
from .types import Box, GtObject


@dataclass(frozen=True)
class SceneConfig:
    width: int = 240
    height: int = 180
    num_frames: int = 160
    fps: float = 30.0

    num_objects: int = 2
    object_radius: float = 9.0
    base_intensity: float = 0.55
    speed: float = 1.2
    shimmer_amp: float = 0.6
    shimmer_hz: float = 6.0
    shimmer_phase_spread: float = 2.2
    shimmer_phase_smooth: int = 2

    num_static_clutter: int = 14
    clutter_intensity: float = 0.4
    num_shimmer_distractors: int = 2
    num_motion_distractors: int = 2

    num_occluders: int = 1
    occluder_size: tuple[int, int] = (18, 70)
    occluder_intensity: float = 0.12
    occlusion_visible_threshold: float = 0.5

    background_level: float = 0.18
    noise_sigma: float = 0.02

    seed: int = 0

    def with_(self, **kwargs) -> "SceneConfig":
        """Return a copy with fields overridden (used by the sweep)."""
        return replace(self, **kwargs)

    def to_json(self) -> dict:
        d = dict(self.__dict__)
        d["occluder_size"] = list(self.occluder_size)
        return d


@dataclass
class Sequence:
    frames: np.ndarray  # (T, H, W) float32 in [0, 1]
    gt: list[GtObject]
    config: SceneConfig

    @property
    def num_frames(self) -> int:
        return int(self.frames.shape[0])

    def gt_by_frame(self) -> dict[int, list[GtObject]]:
        out: dict[int, list[GtObject]] = {i: [] for i in range(self.num_frames)}
        for g in self.gt:
            out.setdefault(g.frame, []).append(g)
        return out

    def object_template(self, obj_id: int = 0, frame: int = 0) -> np.ndarray:
        """Crop the pixels inside an object's ground-truth box.

        Used to give the template-matching baseline a fair, correctly-centred
        template rather than a strawman one.
        """
        for g in self.gt:
            if g.obj_id == obj_id and g.frame == frame:
                b = g.box.clipped(self.config.width, self.config.height)
                return self.frames[
                    frame,
                    int(round(b.y0)) : int(round(b.y1)),
                    int(round(b.x0)) : int(round(b.x1)),
                ].copy()
        raise ValueError(f"no ground truth for object {obj_id} at frame {frame}")

    def write_labels(self, path: str) -> None:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(json.dumps({"config": self.config.to_json()}) + "\n")
            for g in sorted(self.gt, key=lambda r: (r.frame, r.obj_id)):
                fh.write(json.dumps(g.to_json()) + "\n")


@dataclass
class _Mover:
    cx: float
    cy: float
    vx: float
    vy: float
    radius: float
    amp: float
    hz: float
    intensity: float
    phase: np.ndarray  # (2R+1, 2R+1) spatial phase offsets
    obj_id: int
    is_target: bool

    def step(self, width: int, height: int) -> None:
        self.cx += self.vx
        self.cy += self.vy
        # Bounce off the walls, keeping the whole blob on screen.
        lo_x, hi_x = self.radius, width - self.radius
        lo_y, hi_y = self.radius, height - self.radius
        if self.cx < lo_x:
            self.cx = 2 * lo_x - self.cx
            self.vx = -self.vx
        elif self.cx > hi_x:
            self.cx = 2 * hi_x - self.cx
            self.vx = -self.vx
        if self.cy < lo_y:
            self.cy = 2 * lo_y - self.cy
            self.vy = -self.vy
        elif self.cy > hi_y:
            self.cy = 2 * hi_y - self.cy
            self.vy = -self.vy

    @property
    def box(self) -> Box:
        return Box.from_center(self.cx, self.cy, self.radius, self.radius)

    @property
    def speed(self) -> float:
        return math.hypot(self.vx, self.vy)


def _phase_field(rng: np.random.Generator, half: int, spread: float, smooth: int) -> np.ndarray:
    """A smooth random phase field over the blob's local patch."""
    size = 2 * half + 1
    raw = rng.normal(0.0, 1.0, size=(size, size))
    if smooth > 0:
        raw = box_mean(raw, smooth)
        std = raw.std()
        if std > 1e-9:
            raw = raw / std
    return (raw * spread).astype(np.float64)


def _draw_blob(
    canvas: np.ndarray,
    mover: _Mover,
    t_seconds: float,
) -> None:
    """Additively render one shimmering Gaussian blob into ``canvas``."""
    height, width = canvas.shape
    sigma = max(mover.radius / 2.0, 0.5)
    half = (mover.phase.shape[0] - 1) // 2

    ix, iy = int(round(mover.cx)), int(round(mover.cy))
    x0, x1 = max(0, ix - half), min(width, ix + half + 1)
    y0, y1 = max(0, iy - half), min(height, iy + half + 1)
    if x0 >= x1 or y0 >= y1:
        return

    xs = np.arange(x0, x1, dtype=np.float64)
    ys = np.arange(y0, y1, dtype=np.float64)
    dx = xs[None, :] - mover.cx
    dy = ys[:, None] - mover.cy
    envelope = np.exp(-(dx * dx + dy * dy) / (2.0 * sigma * sigma))

    # Slice the object-local phase field to match the (possibly clipped) patch.
    phase = mover.phase[
        y0 - (iy - half) : y0 - (iy - half) + (y1 - y0),
        x0 - (ix - half) : x0 - (ix - half) + (x1 - x0),
    ]

    if mover.amp > 0.0:
        modulation = 1.0 + mover.amp * np.sin(2.0 * math.pi * mover.hz * t_seconds + phase)
    else:
        modulation = 1.0

    canvas[y0:y1, x0:x1] += mover.intensity * modulation * envelope


def _occluder_rects(
    rng: np.random.Generator, cfg: SceneConfig
) -> list[tuple[int, int, int, int]]:
    rects = []
    ow, oh = cfg.occluder_size
    for _ in range(cfg.num_occluders):
        x = int(rng.integers(cfg.width // 6, max(cfg.width // 6 + 1, cfg.width - ow)))
        y = int(rng.integers(0, max(1, cfg.height - oh)))
        rects.append((x, y, min(cfg.width, x + ow), min(cfg.height, y + oh)))
    return rects


def _occluded_fraction(box: Box, rects: Iterable[tuple[int, int, int, int]]) -> float:
    if box.area <= 0:
        return 0.0
    covered = 0.0
    for rx0, ry0, rx1, ry1 in rects:
        covered += box.intersection_area(Box(rx0, ry0, rx1, ry1))
    return min(1.0, covered / box.area)


def generate(cfg: SceneConfig) -> Sequence:
    """Render a full sequence and its ground truth."""
    rng = np.random.default_rng(cfg.seed)
    half = max(1, int(math.ceil(3.0 * (cfg.object_radius / 2.0))))

    def new_mover(
        *, obj_id: int, is_target: bool, amp: float, speed: float, intensity: float
    ) -> _Mover:
        angle = rng.uniform(0.0, 2.0 * math.pi)
        return _Mover(
            cx=float(rng.uniform(cfg.object_radius, cfg.width - cfg.object_radius)),
            cy=float(rng.uniform(cfg.object_radius, cfg.height - cfg.object_radius)),
            vx=speed * math.cos(angle),
            vy=speed * math.sin(angle),
            radius=cfg.object_radius,
            amp=amp,
            hz=cfg.shimmer_hz,
            intensity=intensity,
            phase=_phase_field(
                rng, half, cfg.shimmer_phase_spread, cfg.shimmer_phase_smooth
            ),
            obj_id=obj_id,
            is_target=is_target,
        )

    targets = [
        new_mover(
            obj_id=i,
            is_target=True,
            amp=cfg.shimmer_amp,
            speed=cfg.speed,
            intensity=cfg.base_intensity,
        )
        for i in range(cfg.num_objects)
    ]

    # Distractors carry negative ids: they are never scored as ground truth, so
    # any detection landing on one becomes a false positive.
    shimmer_only = [
        new_mover(
            obj_id=-(i + 1),
            is_target=False,
            amp=cfg.shimmer_amp,
            speed=0.0,
            intensity=cfg.base_intensity,
        )
        for i in range(cfg.num_shimmer_distractors)
    ]
    motion_only = [
        new_mover(
            obj_id=-(100 + i),
            is_target=False,
            amp=0.0,
            speed=cfg.speed,
            intensity=cfg.base_intensity,
        )
        for i in range(cfg.num_motion_distractors)
    ]

    # Static background clutter, rendered once.
    background = np.full((cfg.height, cfg.width), cfg.background_level, dtype=np.float64)
    for _ in range(cfg.num_static_clutter):
        cx = float(rng.uniform(0, cfg.width))
        cy = float(rng.uniform(0, cfg.height))
        sigma = float(rng.uniform(cfg.object_radius * 0.4, cfg.object_radius * 1.3))
        amp = float(rng.uniform(0.3, 1.0)) * cfg.clutter_intensity
        ys = np.arange(cfg.height, dtype=np.float64)[:, None]
        xs = np.arange(cfg.width, dtype=np.float64)[None, :]
        background += amp * np.exp(
            -((xs - cx) ** 2 + (ys - cy) ** 2) / (2.0 * sigma * sigma)
        )

    rects = _occluder_rects(rng, cfg)

    frames = np.empty((cfg.num_frames, cfg.height, cfg.width), dtype=np.float32)
    gt: list[GtObject] = []

    for t in range(cfg.num_frames):
        canvas = background.copy()
        seconds = t / cfg.fps

        for mover in (*targets, *shimmer_only, *motion_only):
            _draw_blob(canvas, mover, seconds)

        for rx0, ry0, rx1, ry1 in rects:
            canvas[ry0:ry1, rx0:rx1] = cfg.occluder_intensity

        if cfg.noise_sigma > 0.0:
            canvas += rng.normal(0.0, cfg.noise_sigma, size=canvas.shape)

        frames[t] = np.clip(canvas, 0.0, 1.0).astype(np.float32)

        for mover in targets:
            box = mover.box
            occluded = _occluded_fraction(box, rects)
            gt.append(
                GtObject(
                    frame=t,
                    obj_id=mover.obj_id,
                    box=box,
                    visible=occluded < cfg.occlusion_visible_threshold,
                    shimmer_amp=mover.amp,
                    shimmer_hz=mover.hz,
                    speed=mover.speed,
                )
            )

        for mover in (*targets, *motion_only):
            mover.step(cfg.width, cfg.height)

    return Sequence(frames=frames, gt=gt, config=cfg)
