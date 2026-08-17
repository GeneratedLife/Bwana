"""Command line entry points.

    python -m shimmer_eval.cli demo
    python -m shimmer_eval.cli sweep --param speed --values 0.5,1,2,3,4,6
    python -m shimmer_eval.cli dump --out frames/
"""

from __future__ import annotations

import argparse
import os

from .generator import SceneConfig, generate
from .imageops import write_pgm
from .sweep import DETECTOR_NAMES, format_table, run_config, sweep, write_csv

SWEEPABLE = (
    "shimmer_amp",
    "shimmer_hz",
    "speed",
    "object_radius",
    "noise_sigma",
    "num_objects",
    "num_static_clutter",
    "num_motion_distractors",
    "num_shimmer_distractors",
    "num_occluders",
    "base_intensity",
)


def _parse_values(text: str) -> list:
    values = []
    for part in text.split(","):
        part = part.strip()
        if not part:
            continue
        try:
            values.append(int(part))
        except ValueError:
            values.append(float(part))
    return values


def _parse_seeds(text: str) -> tuple[int, ...]:
    return tuple(int(p) for p in text.split(",") if p.strip())


def _base_config(args) -> SceneConfig:
    cfg = SceneConfig()
    overrides = {}
    for field in ("width", "height", "num_frames"):
        value = getattr(args, field, None)
        if value is not None:
            overrides[field] = value
    return cfg.with_(**overrides) if overrides else cfg


def cmd_demo(args) -> int:
    cfg = _base_config(args)
    seeds = _parse_seeds(args.seeds)
    print(f"scene: {cfg.width}x{cfg.height} x {cfg.num_frames} frames, seeds={list(seeds)}")
    print(
        f"objects={cfg.num_objects} speed={cfg.speed} shimmer={cfg.shimmer_amp}@{cfg.shimmer_hz}Hz "
        f"distractors: {cfg.num_motion_distractors} moving / {cfg.num_shimmer_distractors} shimmering, "
        f"clutter={cfg.num_static_clutter}, occluders={cfg.num_occluders}\n"
    )

    results = run_config(cfg, DETECTOR_NAMES, seeds=seeds, iou_threshold=args.iou)
    rows = [r.row() for r in results]

    print("detection (per-frame boxes, no identity)")
    print(
        format_table(
            rows,
            ["detector", "seed", "det_precision", "det_recall", "det_f1", "det_motp_iou", "det_fp", "det_fn"],
        )
    )
    print("\ntracking (identities over time)")
    print(
        format_table(
            rows,
            [
                "detector",
                "seed",
                "trk_mota",
                "trk_id_switches",
                "trk_fragmentations",
                "trk_center_rmse",
                "trk_mostly_tracked",
                "trk_mostly_lost",
            ],
        )
    )
    return 0


def cmd_sweep(args) -> int:
    cfg = _base_config(args)
    seeds = _parse_seeds(args.seeds)
    values = _parse_values(args.values)
    detectors = tuple(d.strip() for d in args.detectors.split(",") if d.strip())

    rows = sweep(
        cfg,
        args.param,
        values,
        detectors=detectors,
        seeds=seeds,
        iou_threshold=args.iou,
    )
    print(
        format_table(
            rows,
            [
                "parameter",
                "value",
                "detector",
                "det_f1_mean",
                "det_f1_std",
                "det_precision_mean",
                "det_recall_mean",
                "trk_mota_mean",
                "trk_idsw_mean",
                "trk_center_rmse_mean",
            ],
        )
    )
    if args.out:
        write_csv(rows, args.out)
        print(f"\nwrote {args.out}")
    return 0


def cmd_dump(args) -> int:
    cfg = _base_config(args).with_(seed=args.seed)
    seq = generate(cfg)
    os.makedirs(args.out, exist_ok=True)
    for i, frame in enumerate(seq.frames):
        write_pgm(os.path.join(args.out, f"frame_{i:04d}.pgm"), frame)
    labels = os.path.join(args.out, "labels.jsonl")
    seq.write_labels(labels)
    print(f"wrote {seq.num_frames} frames and {labels}")
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog="shimmer_eval", description=__doc__)
    parser.add_argument("--width", type=int, default=None)
    parser.add_argument("--height", type=int, default=None)
    parser.add_argument("--num-frames", type=int, default=None, dest="num_frames")
    parser.add_argument("--iou", type=float, default=0.5, help="IoU threshold for a match")
    sub = parser.add_subparsers(dest="command", required=True)

    demo = sub.add_parser("demo", help="run every detector on the baseline scene")
    demo.add_argument("--seeds", default="0,1,2")
    demo.set_defaults(func=cmd_demo)

    sw = sub.add_parser("sweep", help="vary one scene parameter and report the curve")
    sw.add_argument("--param", required=True, choices=SWEEPABLE)
    sw.add_argument("--values", required=True, help="comma separated, e.g. 0,0.2,0.4")
    sw.add_argument("--seeds", default="0,1,2")
    sw.add_argument("--detectors", default=",".join(DETECTOR_NAMES))
    sw.add_argument("--out", default=None, help="write results to this CSV")
    sw.set_defaults(func=cmd_sweep)

    dump = sub.add_parser("dump", help="write PGM frames + labels.jsonl for inspection")
    dump.add_argument("--out", required=True)
    dump.add_argument("--seed", type=int, default=0)
    dump.set_defaults(func=cmd_dump)

    return parser


def main(argv: list[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    return args.func(args)


if __name__ == "__main__":
    raise SystemExit(main())
