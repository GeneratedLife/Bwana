package bwana.vision;

/**
 * An immutable copy of the game viewport's pixels.
 * <p>
 * Taken straight from the client's {@code areaViewport} raster rather than from a
 * screen grab. That matters: these are the exact values the software renderer
 * produced, so detection is unaffected by windows covering the game, by the
 * window being minimised, or by display scaling and colour management.
 * <p>
 * Pixels are packed {@code 0xRRGGBB} in row-major order, top-left origin.
 */
public final class Frame {

	/** Where the viewport sits inside the game canvas. */
	public final int canvasOriginX;
	public final int canvasOriginY;

	public final int width;
	public final int height;

	/** Packed 0xRRGGBB, length width * height. */
	public final int[] pixels;

	public final long time;

	public Frame(int[] pixels, int width, int height, int canvasOriginX, int canvasOriginY, long time) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.canvasOriginX = canvasOriginX;
		this.canvasOriginY = canvasOriginY;
		this.time = time;
	}

	public int getRgb(int x, int y) {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
			return 0;
		}
		return this.pixels[y * this.width + x] & 0xFFFFFF;
	}
}
