package bwana.vision;

/**
 * One connected region of pixels that matched a {@link ColorRule}.
 * <p>
 * Coordinates are offered in three frames of reference, because tools need
 * different ones and converting by hand is where mistakes happen:
 * <ul>
 * <li><b>viewport</b> — relative to the 512x334 game view</li>
 * <li><b>canvas</b> — relative to the game canvas, i.e. viewport plus (8, 11)</li>
 * <li><b>screen</b> — absolute desktop pixels, for anything that drives a mouse</li>
 * </ul>
 * Screen coordinates depend on where the window is, which is only knowable on the
 * Swing thread, so they are filled from the last position the UI reported. If the
 * window is dragged between that report and this detection they will be stale by
 * that much; they are exact for a stationary window.
 */
public final class Detection {

	public final String ruleName;

	/** Viewport-relative bounding box. */
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	/** Number of matching pixels, which is not width*height for a ragged shape. */
	public final int area;

	/** Centre of mass of the matching pixels, viewport-relative. */
	public final int centroidX;
	public final int centroidY;

	private final int canvasOriginX;
	private final int canvasOriginY;
	private final int screenOriginX;
	private final int screenOriginY;

	Detection(String ruleName, int x, int y, int width, int height, int area,
			int centroidX, int centroidY,
			int canvasOriginX, int canvasOriginY, int screenOriginX, int screenOriginY) {
		this.ruleName = ruleName;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.area = area;
		this.centroidX = centroidX;
		this.centroidY = centroidY;
		this.canvasOriginX = canvasOriginX;
		this.canvasOriginY = canvasOriginY;
		this.screenOriginX = screenOriginX;
		this.screenOriginY = screenOriginY;
	}

	public int getCanvasX() {
		return this.x + this.canvasOriginX;
	}

	public int getCanvasY() {
		return this.y + this.canvasOriginY;
	}

	public int getScreenX() {
		return this.x + this.canvasOriginX + this.screenOriginX;
	}

	public int getScreenY() {
		return this.y + this.canvasOriginY + this.screenOriginY;
	}

	/** Screen position of the region's centre of mass. */
	public int getScreenCentroidX() {
		return this.centroidX + this.canvasOriginX + this.screenOriginX;
	}

	public int getScreenCentroidY() {
		return this.centroidY + this.canvasOriginY + this.screenOriginY;
	}

	/** How solidly the region fills its box, 0-1. Low means a ragged or hollow shape. */
	public double getFillRatio() {
		int var1 = this.width * this.height;
		return var1 <= 0 ? 0.0D : (double) this.area / (double) var1;
	}

	public String toString() {
		return this.ruleName + " " + this.width + "x" + this.height + " @ viewport " + this.x + "," + this.y
			+ " screen " + this.getScreenX() + "," + this.getScreenY() + " area " + this.area;
	}
}
