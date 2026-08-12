package bwana.inspect;

/**
 * A box the client draws over its own viewport, used to show what the toolkit
 * believes it has selected.
 * <p>
 * This is the point of the whole exercise: if the highlight lands on the tree,
 * then world position, projection and viewport coordinates all agree. If it lands
 * beside it, one of them is wrong and you can see which way.
 * <p>
 * Deliberately a plain static holder rather than a queue of drawing commands. The
 * client's render loop reads it once per frame and needs no knowledge of the
 * toolkit beyond "is there a box, and where".
 */
public final class Highlight {

	private static volatile boolean active;
	private static volatile int x;
	private static volatile int y;
	private static volatile int width;
	private static volatile int height;
	private static volatile int rgb = 0x00FF00;
	private static volatile String label;
	/** Clears itself so a stale box cannot sit on screen after you walk away. */
	private static volatile long expiresAt;

	private Highlight() {
	}

	/**
	 * @param millis how long to show it; the scene moves under a fixed box, so it
	 *               should not outlive its usefulness
	 */
	public static void show(int x, int y, int width, int height, int rgb, String label, long millis) {
		Highlight.x = x;
		Highlight.y = y;
		Highlight.width = width;
		Highlight.height = height;
		Highlight.rgb = rgb;
		Highlight.label = label;
		Highlight.expiresAt = System.currentTimeMillis() + millis;
		active = true;
	}

	/** Second box, drawn in a different colour so two anchors can be compared. */
	private static volatile boolean hasSecond;
	private static volatile int x2;
	private static volatile int y2;
	private static volatile int w2;
	private static volatile int h2;
	private static volatile int rgb2 = 0xFF3399;

	/** Point the mouse is aimed at, drawn as a crosshair. */
	private static volatile boolean hasCross;
	private static volatile int crossX;
	private static volatile int crossY;

	public static void setSecondBox(int x, int y, int width, int height, int rgb) {
		x2 = x;
		y2 = y;
		w2 = width;
		h2 = height;
		rgb2 = rgb;
		hasSecond = true;
	}

	public static void setCrosshair(int x, int y) {
		crossX = x;
		crossY = y;
		hasCross = true;
	}

	public static boolean hasSecondBox() {
		return hasSecond;
	}

	public static int getX2() {
		return x2;
	}

	public static int getY2() {
		return y2;
	}

	public static int getWidth2() {
		return w2;
	}

	public static int getHeight2() {
		return h2;
	}

	public static int getRgb2() {
		return rgb2;
	}

	public static boolean hasCrosshair() {
		return hasCross;
	}

	public static int getCrossX() {
		return crossX;
	}

	public static int getCrossY() {
		return crossY;
	}

	public static void clear() {
		active = false;
		hasSecond = false;
		hasCross = false;
		label = null;
	}

	public static boolean isActive() {
		return active && System.currentTimeMillis() < expiresAt;
	}

	public static int getX() {
		return x;
	}

	public static int getY() {
		return y;
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static int getRgb() {
		return rgb;
	}

	public static String getLabel() {
		return label;
	}
}
