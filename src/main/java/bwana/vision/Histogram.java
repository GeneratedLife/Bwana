package bwana.vision;

/**
 * Distribution of hue, saturation and value across a frame, counted twice: once
 * for every pixel, and once for only the pixels a rule matched.
 * <p>
 * This exists to solve a problem a single colour sample cannot. Two objects can
 * share a hue — tree canopy and grass are both green — so no hue tolerance
 * separates them. What separates them is where they sit on the saturation and
 * value axes, and that is invisible until you look at the distribution. Seeing
 * two humps in the value histogram tells you a cut exists and roughly where; one
 * hump tells you value will not separate them and you need another axis.
 * <p>
 * The "all" series is the scene; the "matched" series is what your rule is
 * currently taking out of it.
 */
public final class Histogram {

	public static final int HUE_BINS = 60;
	public static final int SAT_BINS = 50;
	public static final int VAL_BINS = 50;

	public final int[] hueAll = new int[HUE_BINS];
	public final int[] hueMatched = new int[HUE_BINS];
	public final int[] satAll = new int[SAT_BINS];
	public final int[] satMatched = new int[SAT_BINS];
	public final int[] valAll = new int[VAL_BINS];
	public final int[] valMatched = new int[VAL_BINS];

	public int total;
	public int matched;

	private Histogram() {
	}

	public static Histogram compute(Frame frame, ColorRule rule) {
		Histogram var2 = new Histogram();
		if (frame == null) {
			return var2;
		}
		float[] var3 = new float[3];
		float[] var4 = new float[3];
		int[] var5 = frame.pixels;
		int var6 = frame.width * frame.height;
		var2.total = var6;

		for (int var7 = 0; var7 < var6; var7++) {
			int var8 = var5[var7] & 0xFFFFFF;
			ColorRule.toHsv(var8, var3);

			int var9 = (int) (var3[0] / 360.0F * (float) HUE_BINS);
			if (var9 >= HUE_BINS) {
				var9 = HUE_BINS - 1;
			}
			int var10 = (int) (var3[1] / 100.0F * (float) SAT_BINS);
			if (var10 >= SAT_BINS) {
				var10 = SAT_BINS - 1;
			}
			int var11 = (int) (var3[2] / 100.0F * (float) VAL_BINS);
			if (var11 >= VAL_BINS) {
				var11 = VAL_BINS - 1;
			}

			var2.hueAll[var9]++;
			var2.satAll[var10]++;
			var2.valAll[var11]++;

			if (rule != null && rule.matches(var8, var4)) {
				var2.hueMatched[var9]++;
				var2.satMatched[var10]++;
				var2.valMatched[var11]++;
				var2.matched++;
			}
		}
		return var2;
	}

	public static int max(int[] bins) {
		int var1 = 0;
		for (int var2 = 0; var2 < bins.length; var2++) {
			if (bins[var2] > var1) {
				var1 = bins[var2];
			}
		}
		return var1;
	}
}
