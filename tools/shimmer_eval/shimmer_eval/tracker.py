"""Detection-to-track association.

A constant-velocity tracker with greedy gated association. Deliberately simple
and readable: the harness is here to measure detectors, so the tracker should be
a fair, predictable baseline rather than a black box.

Association score between a predicted track box and a detection:

  * overlapping boxes score ``1 + IoU`` — an IoU match always wins;
  * otherwise, centres within the gate score ``1 - d/gate``.

The distance fallback matters here because the objects move. A fast object's
box may not overlap its own box from the previous frame at all, and an
IoU-only tracker would drop identity every few frames for reasons that have
nothing to do with the detector under test.

Tracks coast on prediction while unmatched, which is what carries an identity
across an occlusion. By default a coasting track emits no box (it has no
evidence to report); set ``emit_coasting=True`` to see the predictions.
"""

from __future__ import annotations

from dataclasses import dataclass

from .types import Box, Detection, TrackOutput


@dataclass
class _Track:
    track_id: int
    cx: float
    cy: float
    half_w: float
    half_h: float
    score: float
    vx: float = 0.0
    vy: float = 0.0
    hits: int = 1
    misses: int = 0
    age: int = 0
    confirmed: bool = False

    def predicted_center(self) -> tuple[float, float]:
        return (self.cx + self.vx, self.cy + self.vy)

    def predicted_box(self) -> Box:
        px, py = self.predicted_center()
        return Box.from_center(px, py, self.half_w, self.half_h)

    @property
    def box(self) -> Box:
        return Box.from_center(self.cx, self.cy, self.half_w, self.half_h)


class IouTracker:
    def __init__(
        self,
        *,
        min_iou: float = 0.2,
        gate_scale: float = 1.75,
        min_hits: int = 3,
        max_age: int = 12,
        velocity_alpha: float = 0.5,
        position_alpha: float = 0.7,
        size_alpha: float = 0.3,
        emit_coasting: bool = False,
    ) -> None:
        self.min_iou = min_iou
        self.gate_scale = gate_scale
        self.min_hits = min_hits
        self.max_age = max_age
        self.velocity_alpha = velocity_alpha
        self.position_alpha = position_alpha
        self.size_alpha = size_alpha
        self.emit_coasting = emit_coasting
        self._tracks: list[_Track] = []
        self._next_id = 1

    def reset(self) -> None:
        self._tracks = []
        self._next_id = 1

    def _pair_score(self, track: _Track, det: Detection) -> float:
        predicted = track.predicted_box()
        iou = predicted.iou(det.box)
        if iou >= self.min_iou:
            return 1.0 + iou
        gate = self.gate_scale * max(predicted.w, predicted.h, 1.0)
        distance = predicted.distance_to(det.box)
        if distance <= gate:
            return 1.0 - distance / gate
        return -1.0

    def update(self, frame: int, detections: list[Detection]) -> list[TrackOutput]:
        for track in self._tracks:
            track.age += 1

        pairs = []
        for ti, track in enumerate(self._tracks):
            for di, det in enumerate(detections):
                score = self._pair_score(track, det)
                if score > 0.0:
                    pairs.append((score, ti, di))
        pairs.sort(key=lambda p: p[0], reverse=True)

        used_tracks: set[int] = set()
        used_dets: set[int] = set()
        for _, ti, di in pairs:
            if ti in used_tracks or di in used_dets:
                continue
            used_tracks.add(ti)
            used_dets.add(di)
            self._apply_match(self._tracks[ti], detections[di])

        for ti, track in enumerate(self._tracks):
            if ti not in used_tracks:
                px, py = track.predicted_center()
                track.cx, track.cy = px, py
                track.misses += 1

        for di, det in enumerate(detections):
            if di in used_dets:
                continue
            self._tracks.append(
                _Track(
                    track_id=self._next_id,
                    cx=det.box.cx,
                    cy=det.box.cy,
                    half_w=det.box.w / 2.0,
                    half_h=det.box.h / 2.0,
                    score=det.score,
                    confirmed=self.min_hits <= 1,
                )
            )
            self._next_id += 1

        self._tracks = [t for t in self._tracks if t.misses <= self.max_age]

        out: list[TrackOutput] = []
        for track in self._tracks:
            if not track.confirmed:
                continue
            coasting = track.misses > 0
            if coasting and not self.emit_coasting:
                continue
            out.append(
                TrackOutput(
                    frame=frame,
                    track_id=track.track_id,
                    box=track.box,
                    score=track.score,
                    coasting=coasting,
                )
            )
        return out

    def _apply_match(self, track: _Track, det: Detection) -> None:
        measured_vx = det.box.cx - track.cx
        measured_vy = det.box.cy - track.cy
        track.vx += self.velocity_alpha * (measured_vx - track.vx)
        track.vy += self.velocity_alpha * (measured_vy - track.vy)

        px, py = track.predicted_center()
        track.cx = px + self.position_alpha * (det.box.cx - px)
        track.cy = py + self.position_alpha * (det.box.cy - py)

        track.half_w += self.size_alpha * (det.box.w / 2.0 - track.half_w)
        track.half_h += self.size_alpha * (det.box.h / 2.0 - track.half_h)

        track.score = det.score
        track.hits += 1
        track.misses = 0
        if track.hits >= self.min_hits:
            track.confirmed = True


def run_tracker(
    detections: list[Detection], num_frames: int, tracker: IouTracker | None = None
) -> list[TrackOutput]:
    """Run a tracker across every frame index, including frames with no detections."""
    tracker = tracker or IouTracker()
    tracker.reset()
    by_frame: dict[int, list[Detection]] = {}
    for d in detections:
        by_frame.setdefault(d.frame, []).append(d)

    out: list[TrackOutput] = []
    for f in range(num_frames):
        out.extend(tracker.update(f, by_frame.get(f, [])))
    return out
