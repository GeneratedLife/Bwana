package bwana.vision;

import java.util.Arrays;

/**
 * Derives a rule from an area of the image you point at, instead of from a single
 * pixel and guesswork.
 * <p>
 * Sampling one pixel gives a target but says nothing about the spread, so the
 * tolerances end up being found by dragging sliders until it looks right — which
 * is slow and tends to land on values that work for one frame. Given a rectangle
 * covering the thing you want, this measures the spread directly.
 * <p>
 * Three details that matter:
 * <ul>
 * <li><b>Hue is averaged as an angle</b>, via the mean of unit vectors. Averaging
 * 350 and 10 arithmetically gives 180 — cyan for two reds.</li>
 * <li><b>Dark and washed-out pixels are excluded from the hue average.</b> Their
 * hue is numerically unstable, so letting them vote drags the centre somewhere
 * arbitrary. They still count for saturation and value.</li>
 * <li><b>Tolerances come from a percentile, not the maximum</b>, so one stray
 * bright pixel in the selection cannot blow the band wide open.</li>
 * </ul>
 */
public final class RuleFitter {

	/** Below these, a pixel's hue is treated as meaningless. */
	private static final float MIN_SAT_FOR_HUE = 12.0F;
	private static final float MIN_VAL_FOR_HUE = 12.0F;

	/** Cover this share of the selection; the rest are treated as outliers. */
	private static final double COVERAGE = 0.92D;

	public static final class Fit {
		public final int targetRgb;
		public final int hueTolerance;
		public final int satTolerance;
		public final int valTolerance;
		public final int pixels;
		/** Pixels whose hue was too unstable to use for the hue centre. */
		public final int unstableHuePixels;

		/** Share of the selection the rule keeps, 0-100. Only set by the discriminative fit. */
		public int recall = -1;
		/** Share of the rest of the frame the rule wrongly takes, 0-100. */
		public int fallout = -1;

		Fit(int targetRgb, int hueTolerance, int satTolerance, int valTolerance,
				int pixels, int unstableHuePixels) {
			this.targetRgb = targetRgb;
			this.hueTolerance = hueTolerance;
			this.satTolerance = satTolerance;
			this.valTolerance = valTolerance;
			this.pixels = pixels;
			this.unstableHuePixels = unstableHuePixels;
		}
	}

	private RuleFitter() {
	}

	/**
	 * @param x0 selection bounds in viewport coordinates, inclusive
	 * @return a fit, or null if the selection was empty
	 */
	public static Fit fit(Frame frame, int x0, int y0, int x1, int y1) {
		if (frame == null) {
			return null;
		}
		int var5 = Math.max(0, Math.min(x0, x1));
		int var6 = Math.max(0, Math.min(y0, y1));
		int var7 = Math.min(frame.width - 1, Math.max(x0, x1));
		int var8 = Math.min(frame.height - 1, Math.max(y0, y1));
		if (var7 < var5 || var8 < var6) {
			return null;
		}

		int var9 = (var7 - var5 + 1) * (var8 - var6 + 1);
		float[] var10 = new float[3];
		float[] var11 = new float[var9];
		float[] var12 = new float[var9];
		float[] var13 = new float[var9];
		int var14 = 0;

		double var15 = 0.0D;
		double var17 = 0.0D;
		int var19 = 0;

		for (int var20 = var6; var20 <= var8; var20++) {
			for (int var21 = var5; var21 <= var7; var21++) {
				int var22 = frame.getRgb(var21, var20);
				ColorRule.toHsv(var22, var10);
				var11[var14] = var10[0];
				var12[var14] = var10[1];
				var13[var14] = var10[2];
				var14++;
				if (var10[1] >= MIN_SAT_FOR_HUE && var10[2] >= MIN_VAL_FOR_HUE) {
					double var23 = (double) var10[0] * Math.PI / 180.0D;
					var15 += Math.cos(var23);
					var17 += Math.sin(var23);
					var19++;
				}
			}
		}
		if (var14 == 0) {
			return null;
		}

		float var25;
		if (var19 > 0) {
			double var26 = Math.atan2(var17, var15) * 180.0D / Math.PI;
			if (var26 < 0.0D) {
				var26 += 360.0D;
			}
			var25 = (float) var26;
		} else {
			// everything in the selection was too dark or too grey to have a hue
			var25 = 0.0F;
		}

		float var27 = mean(var12, var14);
		float var28 = mean(var13, var14);

		int var29 = percentileHueDeviation(var11, var12, var13, var14, var25);
		int var30 = percentileDeviation(var12, var14, var27);
		int var31 = percentileDeviation(var13, var14, var28);

		// a band of zero would match only the exact average; keep a usable floor
		if (var29 < 3) {
			var29 = 3;
		}
		if (var30 < 4) {
			var30 = 4;
		}
		if (var31 < 4) {
			var31 = 4;
		}

		int var32 = ColorRule.hsvToRgb(var25, var27, var28);
		return new Fit(var32, var29, var30, var31, var14, var14 - var19);
	}

	// ---- discriminative fit ----

	/** Deviation bin width, in degrees for hue and percent for the others. */
	private static final int BIN = 4;
	private static final int HUE_BINS = 180 / BIN + 1;
	private static final int SAT_BINS = 100 / BIN + 1;
	private static final int VAL_BINS = 100 / BIN + 1;

	/**
	 * Refuse fits that keep less of the positives than this.
	 * <p>
	 * Deliberately low. A box dragged over a tree contains grass, wall and sky
	 * between the branches, so a large share of the positive pixels are not the
	 * target at all. A high floor forces the bands wide enough to include that
	 * contamination — which is precisely how a tree rule ends up matching the whole
	 * scene. Keeping the clean third of a contaminated selection is the right
	 * answer, and this has to allow it.
	 */
	private static final double MIN_RECALL = 0.20D;

	/**
	 * How much more a wrongly-matched background pixel costs than a correctly
	 * matched target pixel gains.
	 * <p>
	 * With equal weighting, "keep 95% and wrongly take 60%" scores about the same
	 * as "keep 40% and take nothing", and the optimiser has no reason to prefer the
	 * useful one. Detection is worth far more when it is precise, so misses are
	 * cheaper than false alarms.
	 */
	private static final double FALLOUT_WEIGHT = 3.0D;

	/**
	 * Fit a rule that keeps the selection and rejects the rest of the frame.
	 * <p>
	 * {@link #fit} only describes what is inside the box, so it cannot know that
	 * its tolerances also admit half the scene — which is what happens when the
	 * thing you selected shares a hue with its surroundings. This version scores
	 * candidate tolerances against the background and picks the ones that separate
	 * best, so the discriminating axis is found rather than deduced.
	 * <p>
	 * <b>How it stays fast.</b> Scoring every combination directly would mean
	 * re-testing 171,000 pixels per candidate. Instead each pixel's distance from
	 * the centre is binned once into a 3-D histogram of hue/saturation/value
	 * deviations, separately for selection and background. A 3-D prefix sum over
	 * that histogram then answers "how many pixels fall within these three
	 * tolerances" in constant time, so every candidate is evaluated in one lookup.
	 * <p>
	 * Candidates are ranked by Youden's J — the share of the selection kept minus
	 * the share of the background wrongly taken. That deliberately prefers a rule
	 * that keeps 80% of a tree and none of the lawn over one that keeps 100% of the
	 * tree and half the lawn.
	 */
	public static Fit fitAgainstBackground(Frame frame, int x0, int y0, int x1, int y1) {
		Fit var5 = fit(frame, x0, y0, x1, y1);
		if (var5 == null) {
			return null;
		}
		int var6 = Math.max(0, Math.min(x0, x1));
		int var7 = Math.max(0, Math.min(y0, y1));
		int var8 = Math.min(frame.width - 1, Math.max(x0, x1));
		int var9 = Math.min(frame.height - 1, Math.max(y0, y1));

		float[] var10 = new float[3];
		ColorRule.toHsv(var5.targetRgb, var10);
		float var11 = var10[0];
		float var12 = var10[1];
		float var13 = var10[2];

		int var14 = HUE_BINS * SAT_BINS * VAL_BINS;
		int[] var15 = new int[var14];
		int[] var16 = new int[var14];
		int var17 = 0;
		int var18 = 0;

		float[] var19 = new float[3];
		for (int var20 = 0; var20 < frame.height; var20++) {
			for (int var21 = 0; var21 < frame.width; var21++) {
				ColorRule.toHsv(frame.getRgb(var21, var20), var19);

				float var22 = Math.abs(var19[0] - var11);
				if (var22 > 180.0F) {
					var22 = 360.0F - var22;
				}
				// matches the matcher: no reliable hue means the hue test is
				// skipped, which is deviation zero rather than maximum
				if (var19[1] < MIN_SAT_FOR_HUE || var19[2] < MIN_VAL_FOR_HUE) {
					var22 = 0.0F;
				}
				int var23 = (int) var22 / BIN;
				int var24 = (int) Math.abs(var19[1] - var12) / BIN;
				int var25 = (int) Math.abs(var19[2] - var13) / BIN;
				if (var23 >= HUE_BINS) {
					var23 = HUE_BINS - 1;
				}
				if (var24 >= SAT_BINS) {
					var24 = SAT_BINS - 1;
				}
				if (var25 >= VAL_BINS) {
					var25 = VAL_BINS - 1;
				}
				int var26 = (var23 * SAT_BINS + var24) * VAL_BINS + var25;

				if (var21 >= var6 && var21 <= var8 && var20 >= var7 && var20 <= var9) {
					var15[var26]++;
					var17++;
				} else {
					var16[var26]++;
					var18++;
				}
			}
		}
		if (var17 == 0 || var18 == 0) {
			// selection covers everything; nothing to discriminate against
			return var5;
		}

		prefix3d(var15);
		prefix3d(var16);

		double var27 = -1.0D;
		int var29 = -1;
		int var30 = -1;
		int var31 = -1;
		for (int var32 = 0; var32 < HUE_BINS; var32++) {
			for (int var33 = 0; var33 < SAT_BINS; var33++) {
				for (int var34 = 0; var34 < VAL_BINS; var34++) {
					int var35 = (var32 * SAT_BINS + var33) * VAL_BINS + var34;
					double var36 = (double) var15[var35] / (double) var17;
					if (var36 < MIN_RECALL) {
						continue;
					}
					double var38 = (double) var16[var35] / (double) var18;
					double var40 = var36 - FALLOUT_WEIGHT * var38;
					if (var40 > var27) {
						var27 = var40;
						var29 = var32;
						var30 = var33;
						var31 = var34;
					}
				}
			}
		}
		if (var29 < 0) {
			return var5;
		}

		int var42 = (var29 * SAT_BINS + var30) * VAL_BINS + var31;
		Fit var43 = new Fit(var5.targetRgb,
			Math.max(3, (var29 + 1) * BIN),
			Math.max(4, (var30 + 1) * BIN),
			Math.max(4, (var31 + 1) * BIN),
			var5.pixels, var5.unstableHuePixels);
		var43.recall = (int) Math.round((double) var15[var42] * 100.0D / (double) var17);
		var43.fallout = (int) Math.round((double) var16[var42] * 100.0D / (double) var18);
		return var43;
	}

	/**
	 * Fit a rule from accumulated examples rather than one rectangle.
	 * <p>
	 * Same optimisation as {@link #fitAgainstBackground}, but the positives and
	 * negatives are whatever you collected — possibly from different frames, with
	 * different lighting, and with explicit counter-examples. That is what stops a
	 * tree rule from matching shaded grass: it has been shown shaded grass and told
	 * it is not a tree.
	 *
	 * @return null if there are no positive samples
	 */
	public static Fit fitFromSamples(SampleSet samples) {
		if (samples == null || samples.getPositiveCount() == 0) {
			return null;
		}
		int[] var1 = samples.positives();
		int var2 = samples.getPositiveCount();
		int[] var3 = samples.negatives();
		int var4 = samples.getNegativeCount();

		// centre of the positives, hue averaged as an angle and ignoring pixels
		// too dark or grey to have a meaningful one
		float[] var5 = new float[3];
		double var6 = 0.0D;
		double var8 = 0.0D;
		int var10 = 0;
		double var11 = 0.0D;
		double var13 = 0.0D;
		for (int var15 = 0; var15 < var2; var15++) {
			ColorRule.toHsv(var1[var15], var5);
			var11 += (double) var5[1];
			var13 += (double) var5[2];
			if (var5[1] >= MIN_SAT_FOR_HUE && var5[2] >= MIN_VAL_FOR_HUE) {
				double var16 = (double) var5[0] * Math.PI / 180.0D;
				var6 += Math.cos(var16);
				var8 += Math.sin(var16);
				var10++;
			}
		}
		float var30;
		if (var10 > 0) {
			double var18 = Math.atan2(var8, var6) * 180.0D / Math.PI;
			if (var18 < 0.0D) {
				var18 += 360.0D;
			}
			var30 = (float) var18;
		} else {
			var30 = 0.0F;
		}
		float var20 = (float) (var11 / (double) var2);
		float var21 = (float) (var13 / (double) var2);
		int var22 = ColorRule.hsvToRgb(var30, var20, var21);

		if (var4 == 0) {
			// nothing to discriminate against: fall back to describing the positives
			int[] var31 = new int[var2];
			System.arraycopy(var1, 0, var31, 0, var2);
			return describeOnly(var31, var2, var30, var20, var21, var22, var2 - var10);
		}

		int var23 = HUE_BINS * SAT_BINS * VAL_BINS;
		int[] var24 = new int[var23];
		int[] var25 = new int[var23];
		binInto(var24, var1, var2, var30, var20, var21);
		binInto(var25, var3, var4, var30, var20, var21);
		prefix3d(var24);
		prefix3d(var25);

		double var26 = -1.0D;
		int var28 = -1;
		int var29 = -1;
		int var32 = -1;
		for (int var33 = 0; var33 < HUE_BINS; var33++) {
			for (int var34 = 0; var34 < SAT_BINS; var34++) {
				for (int var35 = 0; var35 < VAL_BINS; var35++) {
					int var36 = (var33 * SAT_BINS + var34) * VAL_BINS + var35;
					double var37 = (double) var24[var36] / (double) var2;
					if (var37 < MIN_RECALL) {
						continue;
					}
					double var39 = (double) var25[var36] / (double) var4;
					double var41 = var37 - FALLOUT_WEIGHT * var39;
					if (var41 > var26) {
						var26 = var41;
						var28 = var33;
						var29 = var34;
						var32 = var35;
					}
				}
			}
		}
		if (var28 < 0) {
			int[] var43 = new int[var2];
			System.arraycopy(var1, 0, var43, 0, var2);
			return describeOnly(var43, var2, var30, var20, var21, var22, var2 - var10);
		}

		int var44 = (var28 * SAT_BINS + var29) * VAL_BINS + var32;
		Fit var45 = new Fit(var22,
			Math.max(3, (var28 + 1) * BIN),
			Math.max(4, (var29 + 1) * BIN),
			Math.max(4, (var32 + 1) * BIN),
			var2, var2 - var10);
		var45.recall = (int) Math.round((double) var24[var44] * 100.0D / (double) var2);
		var45.fallout = (int) Math.round((double) var25[var44] * 100.0D / (double) var4);
		return var45;
	}

	/** Percentile spread of the positives, used when there is nothing to reject. */
	private static Fit describeOnly(int[] rgb, int count, float hueCentre, float satCentre,
			float valCentre, int targetRgb, int unstable) {
		float[] var7 = new float[3];
		float[] var8 = new float[count];
		float[] var9 = new float[count];
		float[] var10 = new float[count];
		for (int var11 = 0; var11 < count; var11++) {
			ColorRule.toHsv(rgb[var11], var7);
			var8[var11] = var7[0];
			var9[var11] = var7[1];
			var10[var11] = var7[2];
		}
		int var12 = percentileHueDeviation(var8, var9, var10, count, hueCentre);
		int var13 = percentileDeviation(var9, count, satCentre);
		int var14 = percentileDeviation(var10, count, valCentre);
		return new Fit(targetRgb, Math.max(3, var12), Math.max(4, var13), Math.max(4, var14),
			count, unstable);
	}

	/** Bin each colour's distance from the centre into the 3-D deviation histogram. */
	private static void binInto(int[] bins, int[] rgb, int count,
			float hueCentre, float satCentre, float valCentre) {
		float[] var6 = new float[3];
		for (int var7 = 0; var7 < count; var7++) {
			ColorRule.toHsv(rgb[var7], var6);
			float var8 = Math.abs(var6[0] - hueCentre);
			if (var8 > 180.0F) {
				var8 = 360.0F - var8;
			}
			// matches the matcher: no reliable hue means the hue test is skipped,
			// which is deviation zero rather than maximum
			if (var6[1] < MIN_SAT_FOR_HUE || var6[2] < MIN_VAL_FOR_HUE) {
				var8 = 0.0F;
			}
			int var9 = (int) var8 / BIN;
			int var10 = (int) Math.abs(var6[1] - satCentre) / BIN;
			int var11 = (int) Math.abs(var6[2] - valCentre) / BIN;
			if (var9 >= HUE_BINS) {
				var9 = HUE_BINS - 1;
			}
			if (var10 >= SAT_BINS) {
				var10 = SAT_BINS - 1;
			}
			if (var11 >= VAL_BINS) {
				var11 = VAL_BINS - 1;
			}
			bins[(var9 * SAT_BINS + var10) * VAL_BINS + var11]++;
		}
	}

	/** In-place inclusive prefix sum over all three axes. */
	private static void prefix3d(int[] counts) {
		int var1;
		int var2;
		int var3;
		for (var1 = 0; var1 < HUE_BINS; var1++) {
			for (var2 = 0; var2 < SAT_BINS; var2++) {
				for (var3 = 1; var3 < VAL_BINS; var3++) {
					counts[(var1 * SAT_BINS + var2) * VAL_BINS + var3] +=
						counts[(var1 * SAT_BINS + var2) * VAL_BINS + var3 - 1];
				}
			}
		}
		for (var1 = 0; var1 < HUE_BINS; var1++) {
			for (var2 = 1; var2 < SAT_BINS; var2++) {
				for (var3 = 0; var3 < VAL_BINS; var3++) {
					counts[(var1 * SAT_BINS + var2) * VAL_BINS + var3] +=
						counts[(var1 * SAT_BINS + var2 - 1) * VAL_BINS + var3];
				}
			}
		}
		for (var1 = 1; var1 < HUE_BINS; var1++) {
			for (var2 = 0; var2 < SAT_BINS; var2++) {
				for (var3 = 0; var3 < VAL_BINS; var3++) {
					counts[(var1 * SAT_BINS + var2) * VAL_BINS + var3] +=
						counts[((var1 - 1) * SAT_BINS + var2) * VAL_BINS + var3];
				}
			}
		}
	}

	private static float mean(float[] values, int count) {
		double var2 = 0.0D;
		for (int var4 = 0; var4 < count; var4++) {
			var2 += (double) values[var4];
		}
		return (float) (var2 / (double) count);
	}

	private static int percentileDeviation(float[] values, int count, float centre) {
		float[] var3 = new float[count];
		for (int var4 = 0; var4 < count; var4++) {
			var3[var4] = Math.abs(values[var4] - centre);
		}
		Arrays.sort(var3);
		int var5 = (int) ((double) (count - 1) * COVERAGE);
		return (int) Math.ceil((double) var3[var5]);
	}

	/** Same, but on the hue circle and ignoring pixels with no reliable hue. */
	private static int percentileHueDeviation(float[] hues, float[] sats, float[] vals,
			int count, float centre) {
		float[] var5 = new float[count];
		int var6 = 0;
		for (int var7 = 0; var7 < count; var7++) {
			if (sats[var7] < MIN_SAT_FOR_HUE || vals[var7] < MIN_VAL_FOR_HUE) {
				continue;
			}
			float var8 = Math.abs(hues[var7] - centre);
			if (var8 > 180.0F) {
				var8 = 360.0F - var8;
			}
			var5[var6++] = var8;
		}
		if (var6 == 0) {
			return 180;
		}
		float[] var9 = new float[var6];
		System.arraycopy(var5, 0, var9, 0, var6);
		Arrays.sort(var9);
		int var10 = (int) ((double) (var6 - 1) * COVERAGE);
		return (int) Math.ceil((double) var9[var10]);
	}
}
