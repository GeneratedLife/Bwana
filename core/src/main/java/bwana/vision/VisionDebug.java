package bwana.vision;

/**
 * The intermediate stages of one rule's scan, kept so the debug views can show
 * what the detector actually saw rather than only its conclusions.
 * <p>
 * Produced only while a debug rule is selected — see
 * {@link Vision#setDebugRule}. Normal detection never allocates these.
 */
public final class VisionDebug {

	public final String ruleName;
	public final int width;
	public final int height;

	/**
	 * 1 where the colour test passed, before the min-area filter. Comparing this
	 * against {@link #labels} shows exactly what the filter discarded.
	 */
	public final byte[] mask;

	/** 1-based region ordinal per pixel for regions that survived, 0 elsewhere. */
	public final int[] labels;

	/** Pixels the colour test matched, including ones later filtered out. */
	public final int maskCount;

	/** Regions that survived the min-area filter. */
	public final int regionCount;

	public VisionDebug(String ruleName, int width, int height, byte[] mask, int[] labels,
			int maskCount, int regionCount) {
		this.ruleName = ruleName;
		this.width = width;
		this.height = height;
		this.mask = mask;
		this.labels = labels;
		this.maskCount = maskCount;
		this.regionCount = regionCount;
	}
}
