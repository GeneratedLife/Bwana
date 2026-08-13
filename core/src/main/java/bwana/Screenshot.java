package bwana;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * Saves a PNG of the game canvas to {@code ~/.bwana/screenshots/}.
 * <p>
 * <b>Why this uses {@link Robot} rather than reading the game's own pixels.</b>
 * The client has no composed framebuffer to copy. It blits roughly fifteen
 * separate {@code PixMap} regions — viewport, sidebar, chatback and a dozen
 * background strips — directly onto the AWT {@code Graphics} each frame, so the
 * finished image exists only on screen. Compositing those regions here would mean
 * hardcoding the 2004 layout offsets and re-doing it for any other revision.
 * <p>
 * The cost of that decision is real and worth knowing: <b>Robot photographs the
 * screen</b>, so anything overlapping the canvas ends up in the shot, and a
 * minimised window cannot be captured at all. {@link #capture} raises the window
 * first to make this unlikely, but it cannot make it impossible.
 */
public final class Screenshot {

	private Screenshot() {
	}

	public static File directory() {
		return new File(SessionStore.directory(), "screenshots");
	}

	/**
	 * Grab the given screen rectangle and write it out.
	 * <p>
	 * Call this off the event dispatch thread — it does disk I/O and a short
	 * sleep. The caller is responsible for having read {@code bounds} on the EDT.
	 *
	 * @return the file written
	 */
	public static File capture(Rectangle bounds) throws AWTException, IOException {
		if (bounds == null || bounds.width <= 0 || bounds.height <= 0) {
			throw new IOException("game canvas is not on screen");
		}
		Robot var1 = new Robot();
		BufferedImage var2 = var1.createScreenCapture(bounds);

		File var3 = directory();
		if (!var3.exists() && !var3.mkdirs()) {
			throw new IOException("could not create " + var3);
		}
		File var4 = new File(var3, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".png");
		// a second shot inside the same second would otherwise overwrite the first
		int var5 = 2;
		while (var4.exists()) {
			var4 = new File(var3, new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "_" + var5++ + ".png");
		}
		if (!ImageIO.write(var2, "png", var4)) {
			throw new IOException("no PNG writer available");
		}
		return var4;
	}
}
