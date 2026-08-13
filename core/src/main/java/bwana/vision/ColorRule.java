package bwana.vision;

/**
 * One configurable colour test.
 * <p>
 * Two modes, because they fail in different ways:
 * <ul>
 * <li><b>RGB</b> compares each channel against the target and passes if every
 * channel is within {@link #rgbTolerance}. Predictable, but sensitive to
 * lighting — the same object lit differently reads as a different colour.</li>
 * <li><b>HSV</b> compares hue, saturation and value separately. Loosening
 * saturation and value while keeping hue tight tracks "the same colour under
 * different light", which is usually what you actually want in a 3D scene.</li>
 * </ul>
 * Hue is circular, so its comparison wraps around 360.
 */
public final class ColorRule {

	public static final int MODE_RGB = 0;
	public static final int MODE_HSV = 1;

	/** Below these, hue is numerical noise and is not tested. */
	public static final float UNSTABLE_SAT = 12.0F;
	public static final float UNSTABLE_VAL = 12.0F;

	public String name;

	/** Target colour, packed 0xRRGGBB. */
	public int targetRgb;

	public int mode = MODE_RGB;

	/** Max allowed difference per channel, 0-255. */
	public int rgbTolerance = 24;

	/** Max hue difference in degrees, 0-180. */
	public int hueTolerance = 12;

	/** Max saturation difference, 0-100. */
	public int satTolerance = 40;

	/** Max value/brightness difference, 0-100. */
	public int valTolerance = 40;

	/** Regions smaller than this many pixels are discarded as noise. */
	public int minArea = 12;

	/**
	 * Radius in pixels for closing gaps in the mask before regions are found.
	 * <p>
	 * A tree canopy is not solid — sky and grass show through the leaves — so the
	 * colour mask comes back as a scatter of blobs and one tree is reported as five
	 * regions. Closing (grow by this radius, then shrink back by it) bridges gaps
	 * narrower than twice the radius while leaving the outline roughly where it
	 * was. It also erases specks smaller than the radius, which removes most
	 * false-positive noise for free.
	 * <p>
	 * 0 disables it. 1-2 suits foliage; larger starts merging genuinely separate
	 * objects.
	 */
	public int gapFill = 0;

	public boolean enabled = true;

	/** Colour used to draw this rule's boxes in the debug view. */
	public int overlayRgb = 0x00FF00;

	private final float[] targetHsv = new float[3];

	public ColorRule(String name, int targetRgb) {
		this.name = name;
		this.setTarget(targetRgb);
	}

	public void setTarget(int rgb) {
		this.targetRgb = rgb & 0xFFFFFF;
		toHsv(this.targetRgb, this.targetHsv);
	}

	/** Recompute cached HSV; call after mutating {@link #targetRgb} directly. */
	public void refresh() {
		toHsv(this.targetRgb & 0xFFFFFF, this.targetHsv);
	}

	public boolean matches(int rgb, float[] scratch) {
		if (this.mode == MODE_RGB) {
			int var3 = (rgb >> 16 & 0xFF) - (this.targetRgb >> 16 & 0xFF);
			int var4 = (rgb >> 8 & 0xFF) - (this.targetRgb >> 8 & 0xFF);
			int var5 = (rgb & 0xFF) - (this.targetRgb & 0xFF);
			if (var3 < 0) {
				var3 = -var3;
			}
			if (var4 < 0) {
				var4 = -var4;
			}
			if (var5 < 0) {
				var5 = -var5;
			}
			return var3 <= this.rgbTolerance && var4 <= this.rgbTolerance && var5 <= this.rgbTolerance;
		}

		toHsv(rgb, scratch);
		float var6 = scratch[0] - this.targetHsv[0];
		if (var6 < 0.0F) {
			var6 = -var6;
		}
		// hue is a circle: 350 and 10 are 20 apart, not 340
		if (var6 > 180.0F) {
			var6 = 360.0F - var6;
		}
		// A pixel this dark or this washed out has no meaningful hue -- the value
		// is numerical noise. Testing it either admits nonsense or, worse, forces
		// the hue band open wide enough to keep shadowed parts of the target,
		// which switches hue off for every other pixel too. Skip the test instead
		// and let saturation and value decide.
		if (scratch[1] < UNSTABLE_SAT || scratch[2] < UNSTABLE_VAL) {
			var6 = 0.0F;
		}
		float var7 = scratch[1] - this.targetHsv[1];
		if (var7 < 0.0F) {
			var7 = -var7;
		}
		float var8 = scratch[2] - this.targetHsv[2];
		if (var8 < 0.0F) {
			var8 = -var8;
		}
		return var6 <= (float) this.hueTolerance
			&& var7 <= (float) this.satTolerance
			&& var8 <= (float) this.valTolerance;
	}

	/**
	 * Convert packed RGB into hue 0-360, saturation 0-100, value 0-100.
	 * <p>
	 * Hand-rolled rather than {@code Color.RGBtoHSB} so the units are the ones
	 * shown in the UI, and so this allocates nothing per pixel.
	 */
	public static void toHsv(int rgb, float[] out) {
		float var2 = (float) (rgb >> 16 & 0xFF) / 255.0F;
		float var3 = (float) (rgb >> 8 & 0xFF) / 255.0F;
		float var4 = (float) (rgb & 0xFF) / 255.0F;

		float var5 = var2 > var3 ? var2 : var3;
		if (var4 > var5) {
			var5 = var4;
		}
		float var6 = var2 < var3 ? var2 : var3;
		if (var4 < var6) {
			var6 = var4;
		}
		float var7 = var5 - var6;

		float var8 = 0.0F;
		if (var7 > 0.0F) {
			if (var5 == var2) {
				var8 = 60.0F * ((var3 - var4) / var7);
			} else if (var5 == var3) {
				var8 = 60.0F * ((var4 - var2) / var7 + 2.0F);
			} else {
				var8 = 60.0F * ((var2 - var3) / var7 + 4.0F);
			}
			if (var8 < 0.0F) {
				var8 += 360.0F;
			}
		}
		out[0] = var8;
		out[1] = var5 <= 0.0F ? 0.0F : var7 / var5 * 100.0F;
		out[2] = var5 * 100.0F;
	}

	/**
	 * Inverse of {@link #toHsv}: hue 0-360, saturation 0-100, value 0-100 into a
	 * packed RGB. Needed so a colour computed as an average in HSV space can be
	 * stored back as a target.
	 */
	public static int hsvToRgb(float h, float s, float v) {
		float var3 = v / 100.0F;
		float var4 = s / 100.0F;
		float var5 = var3 * var4;
		float var6 = ((h % 360.0F) + 360.0F) % 360.0F / 60.0F;
		float var7 = var5 * (1.0F - Math.abs(var6 % 2.0F - 1.0F));
		float var8 = var3 - var5;

		float var9;
		float var10;
		float var11;
		if (var6 < 1.0F) {
			var9 = var5;
			var10 = var7;
			var11 = 0.0F;
		} else if (var6 < 2.0F) {
			var9 = var7;
			var10 = var5;
			var11 = 0.0F;
		} else if (var6 < 3.0F) {
			var9 = 0.0F;
			var10 = var5;
			var11 = var7;
		} else if (var6 < 4.0F) {
			var9 = 0.0F;
			var10 = var7;
			var11 = var5;
		} else if (var6 < 5.0F) {
			var9 = var7;
			var10 = 0.0F;
			var11 = var5;
		} else {
			var9 = var5;
			var10 = 0.0F;
			var11 = var7;
		}
		int var12 = clamp255((int) ((var9 + var8) * 255.0F + 0.5F));
		int var13 = clamp255((int) ((var10 + var8) * 255.0F + 0.5F));
		int var14 = clamp255((int) ((var11 + var8) * 255.0F + 0.5F));
		return (var12 << 16) | (var13 << 8) | var14;
	}

	private static int clamp255(int v) {
		if (v < 0) {
			return 0;
		}
		return v > 255 ? 255 : v;
	}

	public ColorRule copy() {
		ColorRule var1 = new ColorRule(this.name, this.targetRgb);
		var1.mode = this.mode;
		var1.rgbTolerance = this.rgbTolerance;
		var1.hueTolerance = this.hueTolerance;
		var1.satTolerance = this.satTolerance;
		var1.valTolerance = this.valTolerance;
		var1.minArea = this.minArea;
		var1.gapFill = this.gapFill;
		var1.enabled = this.enabled;
		var1.overlayRgb = this.overlayRgb;
		return var1;
	}
}
