"""Small numpy image helpers: integral-image box filters and connected components.

Kept dependency-free (numpy only) so the harness runs anywhere without scipy
or OpenCV.
"""

from __future__ import annotations

import numpy as np

from .types import Box


def box_mean(img: np.ndarray, radius: int) -> np.ndarray:
    """Mean over a (2*radius+1)^2 window, with edge windows truncated (not padded).

    Truncating rather than zero-padding avoids darkening the border, which would
    otherwise create a false low-response frame around every image.
    """
    if radius <= 0:
        return img.astype(np.float64, copy=True)

    a = img.astype(np.float64, copy=False)
    h, w = a.shape
    ii = np.zeros((h + 1, w + 1), dtype=np.float64)
    ii[1:, 1:] = a.cumsum(axis=0).cumsum(axis=1)

    rows = np.arange(h)
    cols = np.arange(w)
    y0 = np.clip(rows - radius, 0, h)
    y1 = np.clip(rows + radius + 1, 0, h)
    x0 = np.clip(cols - radius, 0, w)
    x1 = np.clip(cols + radius + 1, 0, w)

    total = (
        ii[np.ix_(y1, x1)]
        - ii[np.ix_(y0, x1)]
        - ii[np.ix_(y1, x0)]
        + ii[np.ix_(y0, x0)]
    )
    counts = (y1 - y0)[:, None] * (x1 - x0)[None, :]
    return total / counts


def label_components(mask: np.ndarray, max_iter: int = 512) -> np.ndarray:
    """Label 8-connected components of a boolean mask.

    Uses iterative max-propagation of a unique seed id, which is fully
    vectorised and converges in roughly (component diameter) iterations. That is
    cheap for the compact blobs this harness deals with; ``max_iter`` caps the
    worst case for pathologically snake-shaped regions, in which case a region
    may be reported as several components.

    Returns an int array of the same shape: -1 for background, and a
    dense 0..n-1 label per component.
    """
    if mask.ndim != 2:
        raise ValueError("mask must be 2-D")
    if not mask.any():
        return np.full(mask.shape, -1, dtype=np.int64)

    seeds = np.arange(mask.size, dtype=np.int64).reshape(mask.shape)
    lab = np.where(mask, seeds, -1)

    for _ in range(max_iter):
        padded = np.pad(lab, 1, mode="constant", constant_values=-1)
        nxt = np.maximum.reduce(
            [
                lab,
                padded[:-2, 1:-1],
                padded[2:, 1:-1],
                padded[1:-1, :-2],
                padded[1:-1, 2:],
                padded[:-2, :-2],
                padded[:-2, 2:],
                padded[2:, :-2],
                padded[2:, 2:],
            ]
        )
        nxt = np.where(mask, nxt, -1)
        if np.array_equal(nxt, lab):
            break
        lab = nxt

    out = np.full(mask.shape, -1, dtype=np.int64)
    ys, xs = np.nonzero(mask)
    _, inverse = np.unique(lab[ys, xs], return_inverse=True)
    out[ys, xs] = inverse
    return out


def peak_relative_boxes(
    energy: np.ndarray,
    labels: np.ndarray,
    *,
    min_area: int = 1,
    alpha: float = 0.6,
    pad: int = 16,
) -> list[tuple[Box, float, int]]:
    """Size each detection by where its response falls to ``alpha`` of its own peak.

    Taking the bounding box of a fixed-threshold mask does not work here: the
    threshold is global, so the surviving core shrinks as an object gets
    fainter or moves faster, and the reported box ends up sized by contrast
    rather than by the object. Measuring the extent relative to each
    component's *own* peak is invariant to both, which keeps box accuracy
    roughly constant across object size and speed.

    ``labels`` locates the components (from a strict threshold, for precision);
    the extent is then re-measured on ``energy`` in a padded window around each
    one. Returns ``(box, peak_value, seed_area)`` sorted by descending peak.
    """
    height, width = energy.shape
    ys, xs = np.nonzero(labels >= 0)
    if ys.size == 0:
        return []

    ids = labels[ys, xs]
    areas = np.bincount(ids, minlength=int(ids.max()) + 1)

    out: list[tuple[Box, float, int]] = []
    for label in range(len(areas)):
        area = int(areas[label])
        if area < min_area:
            continue

        selected = ids == label
        comp_y, comp_x = ys[selected], xs[selected]
        values = energy[comp_y, comp_x]
        peak_at = int(np.argmax(values))
        peak = float(values[peak_at])
        if peak <= 0.0:
            continue
        py, px = int(comp_y[peak_at]), int(comp_x[peak_at])

        y0 = max(0, int(comp_y.min()) - pad)
        y1 = min(height, int(comp_y.max()) + pad + 1)
        x0 = max(0, int(comp_x.min()) - pad)
        x1 = min(width, int(comp_x.max()) + pad + 1)

        local = label_components(energy[y0:y1, x0:x1] > alpha * peak)
        target = local[py - y0, px - x0]
        if target < 0:
            continue
        yy, xx = np.nonzero(local == target)
        out.append(
            (
                Box(
                    float(x0 + xx.min()),
                    float(y0 + yy.min()),
                    float(x0 + xx.max()) + 1.0,
                    float(y0 + yy.max()) + 1.0,
                ),
                peak,
                area,
            )
        )

    out.sort(key=lambda t: t[1], reverse=True)
    return out


def write_pgm(path: str, img: np.ndarray) -> None:
    """Write a float image in [0,1] as a binary PGM (viewable, stdlib-only)."""
    a = np.clip(img, 0.0, 1.0)
    data = (a * 255.0 + 0.5).astype(np.uint8)
    h, w = data.shape
    with open(path, "wb") as fh:
        fh.write(b"P5\n%d %d\n255\n" % (w, h))
        fh.write(data.tobytes())
