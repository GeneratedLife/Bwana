"""Frame-to-detections stages.

All detectors share one streaming interface — ``push(frame) -> [Detection]`` —
because that is how they would be used against a live capture.

Latency is explicit. A detector that needs ``k`` frames of history reports its
findings stamped with the frame at the *centre* of its window, not the newest
one, so its boxes are not systematically dragged behind a moving object. That
costs ``lag`` frames of latency and leaves ``warmup`` frames at the start and
``cooldown`` frames at the end with no output; the evaluation excludes those
rather than charging them as misses.

What each detector is for
-------------------------
The target signature in this harness is a *conjunction*: the object moves
**and** it shimmers. Distractors are deliberately built to satisfy exactly one
half of that (shimmer in place, or move without shimmering), so a detector that
tests only one half is caught by the other's decoys.

``TemplateMatchDetector``
    Normalised cross-correlation against a fixed crop. The obvious first
    approach, and the one to beat. A fixed template cannot match an appearance
    that is redrawn every frame, and a smooth blob template correlates happily
    with any smooth blob, so it pays in both recall and precision.

``TemporalVarianceDetector``
    Per-pixel standard deviation over a sliding window: "anything that
    changed". Good recall, poor precision — every distractor changes.

``ShimmerDftDetector``
    Projects each pixel's time series onto the shimmer frequency (Hann-windowed
    DFT bin). Isolates shimmer from motion far better than a raw variance, but
    it still cannot tell a shimmering mover from a shimmering statue.

``ShimmerMotionDetector``
    The conjunction. Multiplies the shimmer channel by an independent motion
    channel, where the motion channel is built by first low-passing in time
    over exactly one shimmer period — a box filter whose length is one period
    has a null at that frequency, so it removes the shimmer outright — and then
    differencing across a baseline. Static shimmer contributes nothing to the
    motion channel and motion contributes little to the shimmer channel, so the
    product fires only where both hold.

Known limit, deliberately left visible
--------------------------------------
Every temporal detector here works per pixel, so it assumes the object stays
roughly put during its window. Once the object crosses more than about its own
radius within the window, its time series is dominated by the blob arriving and
leaving rather than by shimmer, and accuracy falls off a cliff. The speed sweep
is set up to show exactly where. The fix is motion compensation — analysing in
a co-moving frame using the tracker's velocity estimate — which is left as the
documented next step rather than hidden behind tuned defaults.
"""

from __future__ import annotations

import math
from abc import ABC, abstractmethod
from collections import deque

import numpy as np

from .imageops import box_mean, label_components, peak_relative_boxes
from .types import Box, Detection


def greedy_nms(items: list[tuple[Box, float]], iou_threshold: float) -> list[tuple[Box, float]]:
    """Keep the highest-scoring boxes, dropping any that overlap a kept one."""
    kept: list[tuple[Box, float]] = []
    for box, score in sorted(items, key=lambda t: t[1], reverse=True):
        if all(box.iou(k) <= iou_threshold for k, _ in kept):
            kept.append((box, score))
    return kept


class Detector(ABC):
    """Streaming detector interface."""

    name: str = "detector"
    warmup: int = 0
    cooldown: int = 0

    def __init__(self) -> None:
        self._frame_index = 0

    def reset(self) -> None:
        self._frame_index = 0

    def push(self, frame: np.ndarray) -> list[Detection]:
        index = self._frame_index
        self._frame_index += 1
        return self._process(np.asarray(frame, dtype=np.float64), index)

    @abstractmethod
    def _process(self, frame: np.ndarray, index: int) -> list[Detection]:
        ...

    def run(self, frames: np.ndarray) -> list[Detection]:
        """Reset and run over a whole (T, H, W) array."""
        self.reset()
        out: list[Detection] = []
        for f in frames:
            out.extend(self.push(f))
        return out


class _ResponseDetector(Detector):
    """Shared response-map -> mask -> components -> boxes pipeline.

    Detection and extent estimation are deliberately separated. A strict global
    threshold (``k_sigma``) decides *whether* something is there, which is what
    keeps precision up; the box is then sized by ``box_alpha``, relative to each
    component's own peak, which is what keeps IoU stable as objects change size,
    contrast or speed. Both are single global constants — the sweep runs with
    the same values everywhere, not retuned per scene.
    """

    def __init__(
        self,
        *,
        window: int,
        blur_radius: int = 4,
        k_sigma: float = 5.0,
        abs_floor: float = 1e-4,
        min_area: int = 20,
        max_detections: int = 8,
        nms_iou: float = 0.2,
        box_alpha: float = 0.6,
    ) -> None:
        super().__init__()
        if window < 2:
            raise ValueError("window must be >= 2")
        self.window = window
        self.blur_radius = blur_radius
        self.k_sigma = k_sigma
        self.abs_floor = abs_floor
        self.min_area = min_area
        self.max_detections = max_detections
        self.nms_iou = nms_iou
        self.box_alpha = box_alpha
        self.lag = (window - 1) // 2
        self.warmup = window - 1 - self.lag
        self.cooldown = self.lag
        self._buf: deque[np.ndarray] = deque(maxlen=window)

    def reset(self) -> None:
        super().reset()
        self._buf.clear()

    def _process(self, frame: np.ndarray, index: int) -> list[Detection]:
        self._buf.append(frame)
        if len(self._buf) < self.window:
            return []
        response = self._response(np.stack(tuple(self._buf)))
        return self._boxes_from_response(response, index - self.lag)

    @abstractmethod
    def _response(self, stack: np.ndarray) -> np.ndarray:
        """Map a (window, H, W) stack to a non-negative response image."""

    def _boxes_from_response(self, response: np.ndarray, index: int) -> list[Detection]:
        energy = box_mean(response, self.blur_radius) if self.blur_radius > 0 else response
        threshold = max(float(energy.mean() + self.k_sigma * energy.std()), self.abs_floor)
        mask = energy > threshold
        if not mask.any():
            return []

        comps = peak_relative_boxes(
            energy,
            label_components(mask),
            min_area=self.min_area,
            alpha=self.box_alpha,
        )
        if not comps:
            return []

        kept = greedy_nms([(b, s) for b, s, _ in comps], self.nms_iou)
        return [Detection(frame=index, box=b, score=s) for b, s in kept[: self.max_detections]]


class TemporalVarianceDetector(_ResponseDetector):
    """Per-pixel standard deviation over a sliding window."""

    name = "temporal_variance"

    def __init__(self, *, window: int = 6, **kwargs) -> None:
        kwargs.setdefault("k_sigma", 5.0)
        super().__init__(window=window, **kwargs)

    def _response(self, stack: np.ndarray) -> np.ndarray:
        return stack.std(axis=0)


def _hann(n: int) -> np.ndarray:
    """Hann window with no zero-valued endpoints (they waste two frames)."""
    return np.hanning(n + 2)[1:-1]


class ShimmerDftDetector(_ResponseDetector):
    """Magnitude of the DFT bin at the shimmer frequency, per pixel.

    ``shimmer_hz``/``fps`` tune the detector to a known shimmer rate, which is a
    legitimate design point for a known target.

    Note that sweeping the scene's ``shimmer_hz`` does *not* measure robustness
    to a wrong assumption: :func:`shimmer_eval.sweep.make_detector` hands the
    detector the scene's true rate, so both move together and the sweep reports
    performance across rates, not tuning error. To measure mismatch, build the
    detector explicitly with a ``shimmer_hz`` that differs from the scene's.
    """

    name = "shimmer_dft"

    def __init__(
        self,
        *,
        shimmer_hz: float = 6.0,
        fps: float = 30.0,
        window: int | None = None,
        **kwargs,
    ) -> None:
        period = max(2.0, fps / max(shimmer_hz, 1e-6))
        kwargs.setdefault("k_sigma", 4.0)
        super().__init__(window=window or max(4, int(round(period))), **kwargs)
        self.shimmer_hz = shimmer_hz
        self.fps = fps
        taps = np.arange(self.window, dtype=np.float64)
        weights = _hann(self.window)
        self._kernel = (
            weights * np.exp(-2j * math.pi * shimmer_hz * taps / fps)
        ) / weights.sum()

    def _response(self, stack: np.ndarray) -> np.ndarray:
        return np.abs(np.tensordot(self._kernel, stack, axes=(0, 0)))


class ShimmerMotionDetector(_ResponseDetector):
    """Shimmer energy AND motion energy — the conjunction the targets satisfy.

    The motion channel low-passes over exactly one shimmer period before
    differencing. A box filter of length P has a spectral null at 1/P, so this
    removes the shimmer from the motion channel outright rather than merely
    attenuating it, which is what stops a shimmering statue leaking through.
    """

    name = "shimmer_motion"

    def __init__(
        self,
        *,
        shimmer_hz: float = 6.0,
        fps: float = 30.0,
        motion_baseline: int | None = None,
        window: int | None = None,
        **kwargs,
    ) -> None:
        period = max(2, int(round(max(2.0, fps / max(shimmer_hz, 1e-6)))))
        baseline = motion_baseline if motion_baseline is not None else period
        kwargs.setdefault("blur_radius", 5)
        kwargs.setdefault("k_sigma", 6.0)
        super().__init__(window=window or (period + baseline), **kwargs)
        self.shimmer_hz = shimmer_hz
        self.fps = fps
        self.period = period
        self.baseline = self.window - period

        taps = np.arange(self.window, dtype=np.float64)
        weights = _hann(self.window)
        self._kernel = (
            weights * np.exp(-2j * math.pi * shimmer_hz * taps / fps)
        ) / weights.sum()

    def _response(self, stack: np.ndarray) -> np.ndarray:
        shimmer = np.abs(np.tensordot(self._kernel, stack, axes=(0, 0)))
        # One-period box filters at each end of the window: shimmer-free, so
        # any difference between them is displacement.
        early = stack[: self.period].mean(axis=0)
        late = stack[-self.period :].mean(axis=0)
        motion = np.abs(late - early)

        shimmer = box_mean(shimmer, self.blur_radius)
        motion = box_mean(motion, self.blur_radius)
        return np.sqrt(shimmer * motion)


def _window_sums(img: np.ndarray, th: int, tw: int) -> np.ndarray:
    """Sum over every ``th`` x ``tw`` window, 'valid' alignment."""
    h, w = img.shape
    ii = np.zeros((h + 1, w + 1), dtype=np.float64)
    ii[1:, 1:] = img.cumsum(axis=0).cumsum(axis=1)
    return (
        ii[th : h + 1, tw : w + 1]
        - ii[0 : h - th + 1, tw : w + 1]
        - ii[th : h + 1, 0 : w - tw + 1]
        + ii[0 : h - th + 1, 0 : w - tw + 1]
    )


def normalised_cross_correlation(image: np.ndarray, template: np.ndarray) -> np.ndarray:
    """NCC of ``template`` over ``image``, 'valid' alignment, values in [-1, 1]."""
    th, tw = template.shape
    h, w = image.shape
    if th > h or tw > w:
        raise ValueError("template larger than image")

    t = template - template.mean()
    t_norm = float(np.sqrt((t * t).sum()))
    if t_norm <= 1e-12:
        return np.zeros((h - th + 1, w - tw + 1))

    fh, fw = h + th - 1, w + tw - 1
    corr_full = np.fft.irfft2(
        np.fft.rfft2(image, s=(fh, fw)) * np.fft.rfft2(t[::-1, ::-1], s=(fh, fw)),
        s=(fh, fw),
    )
    numerator = corr_full[th - 1 : th - 1 + (h - th + 1), tw - 1 : tw - 1 + (w - tw + 1)]

    n = th * tw
    sums = _window_sums(image, th, tw)
    sq_sums = _window_sums(image * image, th, tw)
    variance = np.maximum(sq_sums - (sums * sums) / n, 0.0)
    denominator = np.sqrt(variance) * t_norm

    out = np.zeros_like(numerator)
    valid = denominator > 1e-12
    out[valid] = numerator[valid] / denominator[valid]
    return np.clip(out, -1.0, 1.0)


class TemplateMatchDetector(Detector):
    """Fixed-template NCC — the baseline this harness exists to argue against."""

    name = "template_match"

    def __init__(
        self,
        template: np.ndarray,
        *,
        threshold: float = 0.8,
        max_detections: int = 8,
        nms_iou: float = 0.2,
    ) -> None:
        super().__init__()
        self.template = np.asarray(template, dtype=np.float64)
        self.threshold = threshold
        self.max_detections = max_detections
        self.nms_iou = nms_iou

    def _process(self, frame: np.ndarray, index: int) -> list[Detection]:
        th, tw = self.template.shape
        if th > frame.shape[0] or tw > frame.shape[1]:
            return []

        ncc = normalised_cross_correlation(frame, self.template)
        ys, xs = np.nonzero(ncc >= self.threshold)
        if ys.size == 0:
            return []

        scores = ncc[ys, xs]
        order = np.argsort(scores)[::-1][: self.max_detections * 50]
        candidates = [
            (
                Box(float(xs[i]), float(ys[i]), float(xs[i] + tw), float(ys[i] + th)),
                float(scores[i]),
            )
            for i in order
        ]
        kept = greedy_nms(candidates, self.nms_iou)
        return [Detection(frame=index, box=b, score=s) for b, s in kept[: self.max_detections]]
