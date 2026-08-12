package bwana.vision;

/**
 * Finds the dominant colours in a frame.
 * <p>
 * The renderer does not paint flat colours — a tree canopy is hundreds of shades
 * produced by lighting and dithering, so a naive "count distinct RGB values" gives
 * you thousands of buckets with a handful of pixels each and tells you nothing.
 * <p>
 * So colours are quantised to 5 bits per channel (32,768 buckets) before counting,
 * which collapses those shades into one entry. The colour reported for a bucket is
 * the <b>average of the pixels that fell into it</b>, not the bucket's centre —
 * that way the value you sample is a colour that actually occurs, and is a sane
 * starting target for a rule.
 */
public final class Palette {

	/** 5 bits per channel: enough to separate hues, coarse enough to merge shading. */
	private static final int BITS = 5;
	private static final int SHIFT = 8 - BITS;
	private static final int BUCKETS = 1 << (BITS * 3);

	public static final class Entry {
		/** Average colour of the pixels in this bucket, packed 0xRRGGBB. */
		public final int rgb;
		public final int count;
		/** Share of the frame, 0-100. */
		public final double percent;

		Entry(int rgb, int count, double percent) {
			this.rgb = rgb;
			this.count = count;
			this.percent = percent;
		}
	}

	private final int[] counts = new int[BUCKETS];
	private final int[] sumR = new int[BUCKETS];
	private final int[] sumG = new int[BUCKETS];
	private final int[] sumB = new int[BUCKETS];

	/**
	 * @param topN how many entries to return, most common first
	 */
	public Entry[] dominant(Frame frame, int topN) {
		if (frame == null) {
			return new Entry[0];
		}
		java.util.Arrays.fill(this.counts, 0);
		java.util.Arrays.fill(this.sumR, 0);
		java.util.Arrays.fill(this.sumG, 0);
		java.util.Arrays.fill(this.sumB, 0);

		int[] var3 = frame.pixels;
		int var4 = frame.width * frame.height;
		for (int var5 = 0; var5 < var4; var5++) {
			int var6 = var3[var5];
			int var7 = var6 >> 16 & 0xFF;
			int var8 = var6 >> 8 & 0xFF;
			int var9 = var6 & 0xFF;
			int var10 = (var7 >> SHIFT << (BITS * 2)) | (var8 >> SHIFT << BITS) | (var9 >> SHIFT);
			this.counts[var10]++;
			this.sumR[var10] += var7;
			this.sumG[var10] += var8;
			this.sumB[var10] += var9;
		}

		// partial selection: we only ever want a handful, so skip a full sort
		int[] var11 = new int[topN];
		int[] var12 = new int[topN];
		int var13 = 0;
		for (int var14 = 0; var14 < BUCKETS; var14++) {
			int var15 = this.counts[var14];
			if (var15 == 0) {
				continue;
			}
			if (var13 < topN) {
				int var16 = var13++;
				while (var16 > 0 && var12[var16 - 1] < var15) {
					var12[var16] = var12[var16 - 1];
					var11[var16] = var11[var16 - 1];
					var16--;
				}
				var12[var16] = var15;
				var11[var16] = var14;
			} else if (var15 > var12[topN - 1]) {
				int var17 = topN - 1;
				while (var17 > 0 && var12[var17 - 1] < var15) {
					var12[var17] = var12[var17 - 1];
					var11[var17] = var11[var17 - 1];
					var17--;
				}
				var12[var17] = var15;
				var11[var17] = var14;
			}
		}

		Entry[] var18 = new Entry[var13];
		for (int var19 = 0; var19 < var13; var19++) {
			int var20 = var11[var19];
			int var21 = this.counts[var20];
			int var22 = this.sumR[var20] / var21;
			int var23 = this.sumG[var20] / var21;
			int var24 = this.sumB[var20] / var21;
			var18[var19] = new Entry((var22 << 16) | (var23 << 8) | var24, var21,
				(double) var21 * 100.0D / (double) var4);
		}
		return var18;
	}
}
