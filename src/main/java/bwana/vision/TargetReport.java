package bwana.vision;

/**
 * Whether one named target is on screen right now, and where.
 * <p>
 * This is the thing other tools should consume. {@link Vision#getDetections()}
 * gives every region of every rule and leaves the caller to work out which
 * matters; this answers "is the tree visible, and where do I click" directly.
 */
public final class TargetReport {

	public final String name;

	/** True while the target is on screen, allowing for the loss grace period. */
	public final boolean visible;

	/** How many separate regions matched. Several trees produce several regions. */
	public final int regionCount;

	/** Largest matching region, or null when not visible. */
	public final Detection largest;

	public final long time;

	TargetReport(String name, boolean visible, int regionCount, Detection largest, long time) {
		this.name = name;
		this.visible = visible;
		this.regionCount = regionCount;
		this.largest = largest;
		this.time = time;
	}

	/** Screen x to aim at, or -1 when not visible. */
	public int getScreenX() {
		return this.largest == null ? -1 : this.largest.getScreenCentroidX();
	}

	public int getScreenY() {
		return this.largest == null ? -1 : this.largest.getScreenCentroidY();
	}

	public String toString() {
		if (!this.visible || this.largest == null) {
			return this.name + ": not visible";
		}
		return this.name + ": " + this.regionCount + " region(s), largest "
			+ this.largest.width + "x" + this.largest.height
			+ " at screen " + this.getScreenX() + "," + this.getScreenY()
			+ " (area " + this.largest.area + ")";
	}
}
