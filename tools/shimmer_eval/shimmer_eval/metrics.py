"""Scoring against ground truth.

Detection quality (precision / recall / F1 at an IoU threshold) and tracking
quality (ID switches, fragmentation, MOTA / MOTP, mostly-tracked and
mostly-lost counts) are reported separately, because they fail for different
reasons: a detector can be excellent while a tracker still swaps identities
every time two objects cross.

Note on matching: assignment is greedy by descending IoU rather than optimal
(Hungarian). It is within a hair of optimal for well-separated objects and
keeps the harness numpy-only, but it means figures are not bit-identical to
official MOTChallenge tooling.

Frames where an object's ground truth is marked ``visible=False`` (behind an
occluder) are excluded from recall, so a tracker is not charged for missing
something that is not on screen.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Sequence

from .types import Box, Detection, GtObject, TrackOutput


def match_boxes(
    gt_boxes: Sequence[Box], pred_boxes: Sequence[Box], iou_threshold: float
) -> list[tuple[int, int, float]]:
    """Greedy IoU matching. Returns ``(gt_index, pred_index, iou)`` triples."""
    pairs: list[tuple[float, int, int]] = []
    for gi, g in enumerate(gt_boxes):
        for pi, p in enumerate(pred_boxes):
            iou = g.iou(p)
            if iou >= iou_threshold:
                pairs.append((iou, gi, pi))
    pairs.sort(key=lambda t: t[0], reverse=True)

    used_gt: set[int] = set()
    used_pred: set[int] = set()
    matches: list[tuple[int, int, float]] = []
    for iou, gi, pi in pairs:
        if gi in used_gt or pi in used_pred:
            continue
        used_gt.add(gi)
        used_pred.add(pi)
        matches.append((gi, pi, iou))
    return matches


@dataclass
class Metrics:
    tp: int = 0
    fp: int = 0
    fn: int = 0
    id_switches: int = 0
    fragmentations: int = 0
    num_gt: int = 0
    mostly_tracked: int = 0
    partially_tracked: int = 0
    mostly_lost: int = 0
    iou_sum: float = 0.0
    center_sq_error_sum: float = 0.0
    frames_scored: int = 0

    @property
    def precision(self) -> float:
        denom = self.tp + self.fp
        return self.tp / denom if denom else 0.0

    @property
    def recall(self) -> float:
        denom = self.tp + self.fn
        return self.tp / denom if denom else 0.0

    @property
    def f1(self) -> float:
        p, r = self.precision, self.recall
        return 2 * p * r / (p + r) if (p + r) else 0.0

    @property
    def motp(self) -> float:
        """Mean IoU over true positives (higher is better)."""
        return self.iou_sum / self.tp if self.tp else 0.0

    @property
    def center_rmse(self) -> float:
        return (self.center_sq_error_sum / self.tp) ** 0.5 if self.tp else float("nan")

    @property
    def mota(self) -> float:
        if not self.num_gt:
            return 0.0
        return 1.0 - (self.fn + self.fp + self.id_switches) / self.num_gt

    def as_dict(self) -> dict:
        return {
            "tp": self.tp,
            "fp": self.fp,
            "fn": self.fn,
            "precision": round(self.precision, 4),
            "recall": round(self.recall, 4),
            "f1": round(self.f1, 4),
            "motp_iou": round(self.motp, 4),
            "center_rmse": round(self.center_rmse, 3)
            if self.tp
            else float("nan"),
            "id_switches": self.id_switches,
            "fragmentations": self.fragmentations,
            "mota": round(self.mota, 4),
            "mostly_tracked": self.mostly_tracked,
            "partially_tracked": self.partially_tracked,
            "mostly_lost": self.mostly_lost,
            "num_gt": self.num_gt,
            "frames_scored": self.frames_scored,
        }


def _visible_gt_by_frame(gt: Sequence[GtObject]) -> dict[int, list[GtObject]]:
    out: dict[int, list[GtObject]] = {}
    for g in gt:
        if g.visible:
            out.setdefault(g.frame, []).append(g)
    return out


def evaluate_tracks(
    gt: Sequence[GtObject],
    tracks: Sequence[TrackOutput],
    num_frames: int,
    *,
    iou_threshold: float = 0.5,
    warmup: int = 0,
    cooldown: int = 0,
) -> Metrics:
    """Full detection + identity scoring of tracker output.

    ``warmup``/``cooldown`` frames are excluded at each end, so a detector is
    not charged for frames its window cannot cover.
    """
    gt_frames = _visible_gt_by_frame(gt)
    track_frames: dict[int, list[TrackOutput]] = {}
    for t in tracks:
        track_frames.setdefault(t.frame, []).append(t)

    m = Metrics()
    last_track_for_gt: dict[int, int] = {}
    matched_before: set[int] = set()
    was_matched_last_frame: dict[int, bool] = {}
    visible_count: dict[int, int] = {}
    matched_count: dict[int, int] = {}

    for frame in range(warmup, max(warmup, num_frames - cooldown)):
        m.frames_scored += 1
        gts = gt_frames.get(frame, [])
        preds = track_frames.get(frame, [])
        m.num_gt += len(gts)
        for g in gts:
            visible_count[g.obj_id] = visible_count.get(g.obj_id, 0) + 1

        matches = match_boxes([g.box for g in gts], [p.box for p in preds], iou_threshold)
        matched_gt = {gi for gi, _, _ in matches}
        matched_pred = {pi for _, pi, _ in matches}

        m.tp += len(matches)
        m.fp += len(preds) - len(matched_pred)
        m.fn += len(gts) - len(matched_gt)

        seen_this_frame: set[int] = set()
        for gi, pi, iou in matches:
            g, p = gts[gi], preds[pi]
            m.iou_sum += iou
            dx = g.box.cx - p.box.cx
            dy = g.box.cy - p.box.cy
            m.center_sq_error_sum += dx * dx + dy * dy

            matched_count[g.obj_id] = matched_count.get(g.obj_id, 0) + 1
            seen_this_frame.add(g.obj_id)

            previous = last_track_for_gt.get(g.obj_id)
            if previous is not None and previous != p.track_id:
                m.id_switches += 1
            last_track_for_gt[g.obj_id] = p.track_id

            if g.obj_id in matched_before and not was_matched_last_frame.get(g.obj_id, False):
                m.fragmentations += 1
            matched_before.add(g.obj_id)

        for g in gts:
            was_matched_last_frame[g.obj_id] = g.obj_id in seen_this_frame

    for obj_id, visible in visible_count.items():
        if visible <= 0:
            continue
        ratio = matched_count.get(obj_id, 0) / visible
        if ratio >= 0.8:
            m.mostly_tracked += 1
        elif ratio <= 0.2:
            m.mostly_lost += 1
        else:
            m.partially_tracked += 1

    return m


def evaluate_detections(
    gt: Sequence[GtObject],
    detections: Sequence[Detection],
    num_frames: int,
    *,
    iou_threshold: float = 0.5,
    warmup: int = 0,
    cooldown: int = 0,
) -> Metrics:
    """Detection-only scoring: no identity, so ID metrics stay zero."""
    as_tracks = [
        TrackOutput(frame=d.frame, track_id=-1, box=d.box, score=d.score)
        for d in detections
    ]
    m = evaluate_tracks(
        gt,
        as_tracks,
        num_frames,
        iou_threshold=iou_threshold,
        warmup=warmup,
        cooldown=cooldown,
    )
    m.id_switches = 0
    m.fragmentations = 0
    return m
