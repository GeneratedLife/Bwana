"""End-to-end checks: the whole pipeline, and the claims the harness exists to make."""

import pytest

from shimmer_eval.cli import main
from shimmer_eval.generator import SceneConfig
from shimmer_eval.sweep import format_table, make_detector, run_config, run_once, sweep, write_csv

FAST = SceneConfig(
    width=112,
    height=84,
    num_frames=44,
    num_objects=1,
    object_radius=8.0,
    speed=0.8,
    num_static_clutter=3,
    num_shimmer_distractors=1,
    num_motion_distractors=1,
    num_occluders=0,
    seed=0,
)


def test_run_once_produces_both_metric_sets():
    result = run_once(FAST, "shimmer_motion")
    assert result.detection.frames_scored > 0
    assert result.tracking.frames_scored > 0
    row = result.row()
    assert row["detector"] == "shimmer_motion"
    assert "det_f1" in row and "trk_mota" in row


def test_conjunction_beats_the_naive_baselines_end_to_end():
    """The harness's headline result, asserted rather than eyeballed."""
    scores = {
        name: run_once(FAST, name).detection.f1
        for name in ("template_match", "temporal_variance", "shimmer_dft", "shimmer_motion")
    }
    assert scores["shimmer_motion"] > scores["template_match"]
    assert scores["shimmer_motion"] > scores["temporal_variance"]
    assert scores["shimmer_motion"] > scores["shimmer_dft"]


def test_decoys_cost_the_single_cue_detectors_precision():
    with_decoys = FAST.with_(num_shimmer_distractors=2, num_motion_distractors=2)
    without = FAST.with_(num_shimmer_distractors=0, num_motion_distractors=0)

    naive_drop = (
        run_once(without, "temporal_variance").detection.precision
        - run_once(with_decoys, "temporal_variance").detection.precision
    )
    conjunction_drop = (
        run_once(without, "shimmer_motion").detection.precision
        - run_once(with_decoys, "shimmer_motion").detection.precision
    )
    assert naive_drop > conjunction_drop


def test_accuracy_falls_off_as_the_object_outruns_the_window():
    """The documented speed limit must actually show up in the numbers."""
    slow = run_once(FAST.with_(speed=0.4), "shimmer_motion").detection.f1
    fast = run_once(FAST.with_(speed=6.0), "shimmer_motion").detection.f1
    assert slow > 0.8
    assert fast < slow - 0.3


def test_run_config_covers_every_detector_and_seed():
    results = run_config(FAST, ("shimmer_motion", "temporal_variance"), seeds=(0, 1))
    assert len(results) == 4
    assert {r.seed for r in results} == {0, 1}
    assert {r.detector for r in results} == {"shimmer_motion", "temporal_variance"}


def test_sweep_returns_one_row_per_value_and_detector():
    rows = sweep(
        FAST,
        "shimmer_amp",
        [0.0, 0.8],
        detectors=("shimmer_motion",),
        seeds=(0,),
    )
    assert len(rows) == 2
    assert {r["value"] for r in rows} == {0.0, 0.8}
    assert all(r["parameter"] == "shimmer_amp" for r in rows)


def test_sweep_rejects_unknown_parameter():
    with pytest.raises(ValueError):
        sweep(FAST, "not_a_field", [1], detectors=("shimmer_motion",), seeds=(0,))


def test_make_detector_rejects_unknown_name():
    from shimmer_eval.generator import generate

    with pytest.raises(ValueError):
        make_detector("nope", generate(FAST))


def test_write_csv_and_format_table(tmp_path):
    rows = [{"a": 1, "b": 2.5}, {"a": 3, "b": float("nan")}]
    path = tmp_path / "out.csv"
    write_csv(rows, str(path))
    text = path.read_text()
    assert text.splitlines()[0] == "a,b"
    assert len(text.splitlines()) == 3

    table = format_table(rows, ["a", "b"])
    assert "nan" in table
    assert table.splitlines()[0].startswith("a")


def test_format_table_handles_no_rows():
    assert format_table([], ["a"]) == "(no rows)"


def test_cli_demo_runs(capsys):
    assert main(["--width", "96", "--height", "72", "--num-frames", "36", "demo", "--seeds", "0"]) == 0
    out = capsys.readouterr().out
    assert "detection" in out and "tracking" in out
    assert "shimmer_motion" in out


def test_cli_sweep_writes_csv(tmp_path, capsys):
    out_path = tmp_path / "sweep.csv"
    code = main(
        [
            "--width", "96", "--height", "72", "--num-frames", "36",
            "sweep",
            "--param", "shimmer_amp",
            "--values", "0.0,0.8",
            "--seeds", "0",
            "--detectors", "shimmer_motion",
            "--out", str(out_path),
        ]
    )
    assert code == 0
    assert out_path.exists()
    assert len(out_path.read_text().splitlines()) == 3
    assert "shimmer_amp" in capsys.readouterr().out


def test_cli_dump_writes_frames_and_labels(tmp_path, capsys):
    out_dir = tmp_path / "frames"
    code = main(
        ["--width", "64", "--height", "48", "--num-frames", "5", "dump", "--out", str(out_dir)]
    )
    assert code == 0
    assert sorted(p.name for p in out_dir.glob("*.pgm")) == [
        f"frame_{i:04d}.pgm" for i in range(5)
    ]
    labels = out_dir / "labels.jsonl"
    assert labels.exists()
    # The CLI uses the default scene, not FAST: header line + one row per
    # object per frame.
    expected_rows = 5 * SceneConfig().num_objects
    assert len(labels.read_text().splitlines()) == 1 + expected_rows
    assert "wrote" in capsys.readouterr().out
