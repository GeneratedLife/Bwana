package bwana.vision;

/**
 * Accumulated examples of what a target is and is not.
 * <p>
 * A single fit sees one frame, so it can only learn from whatever happened to be
 * in shot. That is how a tree rule ends up matching shaded grass: the frame it was
 * tuned on had grass between the branches and no counter-example anywhere. Samples
 * collected across several frames — a tree in sun, a tree in shade, and explicitly
 * "this dark grass is not a tree" — describe the target far better than any one
 * view of it can.
 * <p>
 * Only pixel colours are kept, not positions or frames: what a tree looks like
 * does not depend on where it was standing.
 */
public final class SampleSet {

	private int[] positive = new int[8192];
	private int positiveCount;
	private int[] negative = new int[8192];
	private int negativeCount;

	/** Number of separate boxes added, for the UI to report. */
	private int positiveBoxes;
	private int negativeBoxes;

	public int getPositiveCount() {
		return this.positiveCount;
	}

	public int getNegativeCount() {
		return this.negativeCount;
	}

	public int getPositiveBoxes() {
		return this.positiveBoxes;
	}

	public int getNegativeBoxes() {
		return this.negativeBoxes;
	}

	public boolean isEmpty() {
		return this.positiveCount == 0;
	}

	public void clear() {
		this.positiveCount = 0;
		this.negativeCount = 0;
		this.positiveBoxes = 0;
		this.negativeBoxes = 0;
	}

	/** Add every pixel in the rectangle as an example. Bounds are inclusive. */
	public void add(Frame frame, int x0, int y0, int x1, int y1, boolean isPositive) {
		if (frame == null) {
			return;
		}
		int var6 = Math.max(0, Math.min(x0, x1));
		int var7 = Math.max(0, Math.min(y0, y1));
		int var8 = Math.min(frame.width - 1, Math.max(x0, x1));
		int var9 = Math.min(frame.height - 1, Math.max(y0, y1));
		if (var8 < var6 || var9 < var7) {
			return;
		}
		for (int var10 = var7; var10 <= var9; var10++) {
			for (int var11 = var6; var11 <= var8; var11++) {
				int var12 = frame.getRgb(var11, var10);
				if (isPositive) {
					if (this.positiveCount == this.positive.length) {
						this.positive = grow(this.positive);
					}
					this.positive[this.positiveCount++] = var12;
				} else {
					if (this.negativeCount == this.negative.length) {
						this.negative = grow(this.negative);
					}
					this.negative[this.negativeCount++] = var12;
				}
			}
		}
		if (isPositive) {
			this.positiveBoxes++;
		} else {
			this.negativeBoxes++;
		}
	}

	/**
	 * Add the whole frame except the given rectangle as negative examples,
	 * subsampled.
	 * <p>
	 * Subsampled because a full frame is 171,000 pixels and would swamp a few
	 * thousand hand-picked ones; every fourth pixel describes the background just
	 * as well for this purpose.
	 */
	public void addBackground(Frame frame, int x0, int y0, int x1, int y1) {
		if (frame == null) {
			return;
		}
		for (int var6 = 0; var6 < frame.height; var6 += 2) {
			for (int var7 = 0; var7 < frame.width; var7 += 2) {
				if (var7 >= x0 && var7 <= x1 && var6 >= y0 && var6 <= y1) {
					continue;
				}
				if (this.negativeCount == this.negative.length) {
					this.negative = grow(this.negative);
				}
				this.negative[this.negativeCount++] = frame.getRgb(var7, var6);
			}
		}
		this.negativeBoxes++;
	}

	int[] positives() {
		return this.positive;
	}

	int[] negatives() {
		return this.negative;
	}

	private static int[] grow(int[] array) {
		int[] var1 = new int[array.length * 2];
		System.arraycopy(array, 0, var1, 0, array.length);
		return var1;
	}
}
