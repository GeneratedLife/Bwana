# shimmer_eval

A harness for measuring how accurately a **moving, shimmering** object can be
detected and tracked.

The data is synthetic on purpose: synthesising it is what gives you exact
ground truth, and without ground truth "accuracy" is not a computable quantity.
Every frame is emitted alongside the exact box of every object, so the output is
a precision/recall curve against scene difficulty rather than a state string.

```
generator  ->  detector    ->  tracker      ->  metrics   ->  sweep
frames +       frame ->        boxes ->         vs ground     accuracy vs
ground truth   boxes          identities        truth         difficulty
```

Four stages, four files, no stage aware of the others. You can replace the
detector without touching anything else — that is the point of the split.

## Quick start

```bash
cd tools/shimmer_eval
python3 -m pip install -r requirements.txt

python3 -m shimmer_eval.cli demo                    # every detector, baseline scene
python3 -m shimmer_eval.cli sweep --param speed \
    --values 0.25,0.5,1,2,3,4,6 --out speed.csv     # accuracy vs difficulty
python3 -m shimmer_eval.cli dump --out /tmp/frames  # PGM frames + labels.jsonl
python3 -m pytest                                   # 88 tests
```

## The problem, and why the obvious approach fails

The target's signature is a **conjunction**: it moves *and* it shimmers. The
scene therefore contains decoys that satisfy exactly one half —

| | moves | shimmers |
|---|---|---|
| **target** | yes | yes |
| shimmer-only distractor | no | yes |
| motion-only distractor | yes | no |
| static clutter | no | no |

— so a detector that tests only one half is caught by the other's decoys, and
cannot score well by accident.

Measured on the baseline scene (240x180, 160 frames, 2 targets, 2 decoys of
each kind, 14 clutter blobs, 1 occluder; mean over seeds 0-2):

| detector | precision | recall | F1 | MOTA | ID switches |
|---|---|---|---|---|---|
| `template_match` | 0.60 | 0.14 | 0.17 | -0.56 | 3.0 |
| `temporal_variance` | 0.39 | 0.79 | 0.52 | -0.26 | 0.7 |
| `shimmer_dft` | 0.26 | 0.74 | 0.38 | -1.25 | 3.0 |
| `shimmer_motion` | 0.92 | 0.98 | 0.95 | 0.91 | 0.3 |

Read this as four failure modes, not a leaderboard:

- **`template_match`** — normalised cross-correlation against a fixed crop of
  the real object. Recall collapses to 0.14 because a fixed template cannot
  match an appearance that is redrawn every frame. This is the approach worth
  ruling out first, and it is given every advantage: a correctly-centred
  template taken from the actual target.
- **`temporal_variance`** — per-pixel standard deviation, i.e. "anything that
  changed". Recall 0.79 at precision 0.39: it finds the target and everything
  else too.
- **`shimmer_dft`** — per-pixel DFT bin at the shimmer frequency. Isolates
  shimmer from motion, but cannot tell a shimmering mover from a shimmering
  statue, so the decoys wreck its precision.
- **`shimmer_motion`** — the conjunction. Multiplies a shimmer channel by an
  independent motion channel. The motion channel first low-passes in time over
  exactly one shimmer period: a box filter of length P has a spectral null at
  1/P, so this *removes* the shimmer rather than merely attenuating it, and a
  shimmering statue contributes nothing to it.

### Shimmer is signal, not noise

Sweeping shimmer amplitude with everything else fixed (120 frames, mean over
seeds 0-1):

| shimmer_amp | `template_match` F1 | `shimmer_motion` F1 |
|---|---|---|
| 0.0 | 0.32 | 0.55 |
| 0.2 | 0.29 | 0.67 |
| 0.4 | 0.22 | 0.84 |
| 0.6 | 0.17 | 0.92 |
| 0.8 | 0.14 | 0.94 |

The curves cross. Template matching degrades monotonically as the object
shimmers harder; the conjunction detector *improves*, because the shimmer is
the most discriminative thing about the target. Any approach that treats the
shimmer as noise to be averaged away is throwing away its best cue.

## Known limit: speed

Every temporal detector here works per pixel, so it assumes the object stays
roughly put during its window. Once the object crosses more than about its own
radius within the window, each pixel's time series is dominated by the blob
arriving and leaving rather than by shimmer. Sweeping speed at a fixed radius
of 9 px and a 10-frame window (120 frames, mean over seeds 0-1):

| speed (px/frame) | 0.25 | 0.5 | 1 | 2 | 3 | 4 | 6 |
|---|---|---|---|---|---|---|---|
| F1 | 1.00 | 0.97 | 0.93 | 0.78 | 0.60 | 0.10 | 0.01 |
| MOTA | 1.00 | 0.94 | 0.86 | 0.54 | 0.26 | -0.14 | -0.08 |

The cliff sits where the object traverses roughly one diameter per window, as
predicted. This is left visible rather than hidden behind tuned defaults,
because it is the honest boundary of the approach.

**The fix, not yet implemented:** motion compensation — warping the window into
the object's co-moving frame using the tracker's velocity estimate before
applying the temporal filter. That closes the loop from tracker back to
detector, which is why it is a deliberate next step rather than a tweak.

## Design notes

**Latency is explicit.** A detector needing `k` frames of history stamps its
findings with the frame at the *centre* of its window, not the newest one, so
boxes are not systematically dragged behind a moving object. That costs `lag`
frames of latency and leaves `warmup` frames at the start and `cooldown` at the
end with no output; the evaluation excludes those instead of charging them as
misses.

**Detection and extent estimation are separated.** A strict global threshold
(`k_sigma`) decides *whether* something is there, which keeps precision up. The
box is then sized by `box_alpha`, relative to each component's *own* peak. The
naive alternative — bounding box of the thresholded mask — sizes the box by
contrast rather than by the object, so reported boxes shrink as a target gets
fainter or faster and IoU collapses for reasons unrelated to localisation.
Peak-relative sizing holds mean IoU at 0.6-0.75 across radius 6→14 px and speed
0.8→2.4 px/frame.

**Occlusion is scored honestly.** Ground truth marked `visible=False` is
excluded from recall, so a tracker is not charged for missing something that is
off screen — but the rows stay in the file, so identity *across* the gap is
still scored. Tracks coast on prediction while unmatched, which is what carries
an ID through an occlusion; coasting tracks emit no box by default, since they
have no evidence to report.

**Both detector baselines are given their best shot.** The template matcher
gets a correctly-centred crop of the real object; the frequency-tuned detectors
get the scene's true shimmer rate. Thresholds were tuned per detector to their
own best F1 on the baseline scene. A rigged comparison would prove nothing.

## Caveats

- Matching is **greedy by descending IoU, not Hungarian**. Within a hair of
  optimal for well-separated objects, but figures are not bit-identical to
  official MOTChallenge tooling.
- `MOTA` is unbounded below — a detector emitting many false positives scores
  arbitrarily negative. That is standard, and is why precision/recall are
  reported alongside it.
- Sweeping the scene's `shimmer_hz` does **not** measure robustness to a wrong
  assumption: `make_detector` hands the tuned detectors the scene's true rate,
  so both move together. To measure tuning error, construct the detector
  explicitly with a `shimmer_hz` differing from the scene's (see
  `test_frequency_mismatch_costs_the_tuned_detector`).
- Everything is greyscale and single-scale. Colour and scale changes are not
  modelled.
- `abs_floor` is an absolute response floor, so it is contrast-dependent. It is
  left low by default so it does not distort amplitude and intensity sweeps;
  raise it if you need a detector that can report "nothing here" rather than
  "whatever was strongest".

## Layout

| file | role |
|---|---|
| `generator.py` | scene model, rendering, exact ground truth |
| `detectors.py` | the four detectors + NCC |
| `tracker.py` | constant-velocity greedy-association tracker |
| `metrics.py` | P/R/F1, MOTA/MOTP, ID switches, fragmentation, MT/ML |
| `imageops.py` | integral-image box filter, connected components, PGM writer |
| `sweep.py` | run configs, sweep parameters, CSV + table output |
| `cli.py` | `demo`, `sweep`, `dump` |
