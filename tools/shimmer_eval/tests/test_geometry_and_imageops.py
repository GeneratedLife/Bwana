import numpy as np
import pytest

from shimmer_eval.imageops import box_mean, label_components, peak_relative_boxes
from shimmer_eval.types import Box


def test_box_basics():
    b = Box(10, 20, 30, 60)
    assert b.w == 20 and b.h == 40
    assert b.area == 800
    assert b.center == (20.0, 40.0)
    assert Box.from_center(20, 40, 10, 20) == b


def test_iou_identical_and_disjoint():
    b = Box(0, 0, 10, 10)
    assert b.iou(b) == pytest.approx(1.0)
    assert b.iou(Box(20, 20, 30, 30)) == 0.0
    # Touching edges are not overlapping.
    assert b.iou(Box(10, 0, 20, 10)) == 0.0


def test_iou_half_overlap():
    a = Box(0, 0, 10, 10)
    b = Box(5, 0, 15, 10)
    # intersection 50, union 150
    assert a.iou(b) == pytest.approx(50 / 150)


def test_box_clipped():
    assert Box(-5, -5, 15, 15).clipped(10, 10) == Box(0, 0, 10, 10)


def test_box_mean_matches_naive():
    rng = np.random.default_rng(0)
    img = rng.normal(size=(9, 11))
    radius = 2
    got = box_mean(img, radius)
    for y in range(img.shape[0]):
        for x in range(img.shape[1]):
            y0, y1 = max(0, y - radius), min(img.shape[0], y + radius + 1)
            x0, x1 = max(0, x - radius), min(img.shape[1], x + radius + 1)
            assert got[y, x] == pytest.approx(img[y0:y1, x0:x1].mean())


def test_box_mean_preserves_constant_including_border():
    img = np.full((7, 7), 3.0)
    assert np.allclose(box_mean(img, 2), 3.0)


def test_box_mean_zero_radius_is_identity():
    img = np.arange(12.0).reshape(3, 4)
    assert np.allclose(box_mean(img, 0), img)


def test_label_components_separates_and_merges():
    mask = np.zeros((10, 10), dtype=bool)
    mask[1:3, 1:3] = True
    mask[6:9, 6:9] = True
    labels = label_components(mask)
    assert labels[mask].min() >= 0
    assert labels[~mask].max() == -1
    assert len(np.unique(labels[mask])) == 2
    # The two blobs must not share a label.
    assert labels[1, 1] != labels[7, 7]


def test_label_components_diagonal_is_one_component():
    mask = np.zeros((6, 6), dtype=bool)
    mask[1, 1] = True
    mask[2, 2] = True  # 8-connected to (1,1)
    labels = label_components(mask)
    assert labels[1, 1] == labels[2, 2]


def test_label_components_empty_mask():
    labels = label_components(np.zeros((4, 4), dtype=bool))
    assert (labels == -1).all()


def _gaussian(shape, cy, cx, sigma, peak=1.0):
    ys = np.arange(shape[0])[:, None]
    xs = np.arange(shape[1])[None, :]
    return peak * np.exp(-((xs - cx) ** 2 + (ys - cy) ** 2) / (2 * sigma**2))


def test_peak_relative_box_brackets_the_blob():
    energy = _gaussian((41, 41), 20, 20, sigma=4.0)
    seeds = label_components(energy > 0.9)
    boxes = peak_relative_boxes(energy, seeds, min_area=1, alpha=0.5)
    assert len(boxes) == 1
    box, peak, _ = boxes[0]
    assert peak == pytest.approx(1.0)
    # Half-maximum of a Gaussian is at sigma*sqrt(2 ln 2) ~= 1.177 sigma.
    expected_half_width = 4.0 * np.sqrt(2 * np.log(2))
    assert box.cx == pytest.approx(20.5, abs=0.6)
    assert box.w / 2 == pytest.approx(expected_half_width, abs=1.5)


def test_peak_relative_box_is_invariant_to_contrast():
    """The whole reason for peak-relative sizing: a fainter object is not a smaller one."""
    bright = _gaussian((41, 41), 20, 20, sigma=4.0, peak=1.0)
    faint = _gaussian((41, 41), 20, 20, sigma=4.0, peak=0.2)

    boxes = []
    for energy in (bright, faint):
        seeds = label_components(energy > 0.9 * energy.max())
        boxes.append(peak_relative_boxes(energy, seeds, min_area=1, alpha=0.5)[0][0])

    assert boxes[0].w == pytest.approx(boxes[1].w)
    assert boxes[0].h == pytest.approx(boxes[1].h)


def test_peak_relative_box_scales_with_the_object():
    small = _gaussian((61, 61), 30, 30, sigma=3.0)
    large = _gaussian((61, 61), 30, 30, sigma=9.0)

    widths = []
    for energy in (small, large):
        seeds = label_components(energy > 0.9)
        widths.append(peak_relative_boxes(energy, seeds, min_area=1, alpha=0.5)[0][0].w)

    assert widths[1] == pytest.approx(3 * widths[0], rel=0.2)


def test_peak_relative_boxes_min_area_filter():
    energy = np.zeros((20, 20))
    energy[0, 0] = 1.0
    energy[10:13, 10:13] = 1.0
    seeds = label_components(energy > 0.5)
    assert len(peak_relative_boxes(energy, seeds, min_area=4, alpha=0.5)) == 1


def test_peak_relative_boxes_sorted_by_peak():
    energy = _gaussian((41, 81), 20, 15, 4.0, peak=0.4) + _gaussian((41, 81), 20, 60, 4.0, peak=1.0)
    seeds = label_components(energy > 0.35)
    boxes = peak_relative_boxes(energy, seeds, min_area=1, alpha=0.5)
    assert len(boxes) == 2
    assert boxes[0][1] > boxes[1][1]
    assert boxes[0][0].cx > boxes[1][0].cx


def test_peak_relative_boxes_empty_input():
    energy = np.zeros((10, 10))
    assert peak_relative_boxes(energy, label_components(energy > 0.5), min_area=1) == []
