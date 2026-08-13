package bwana.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Icon;

/**
 * The crown beside the window title.
 * <p>
 * Drawn rather than shipped, so there is no asset to lose and it scales to
 * whatever size the bar asks for. If {@code ~/.bwana/icon.png} exists it is used
 * instead — dropping a real image in that path overrides this without a rebuild,
 * which is easier than replacing vector maths with more vector maths.
 * <p>
 * Painted with no background at all: the title bar shows through, so the icon sits
 * on black rather than in a box.
 */
public final class CrownIcon implements Icon {

	/** Outline of a five-pointed crown, as fractions of the icon box. */
	private static final float[] SHAPE = new float[] {
		0.02F, 0.92F,
		0.02F, 0.26F,
		0.20F, 0.60F,
		0.33F, 0.08F,
		0.50F, 0.50F,
		0.67F, 0.02F,
		0.80F, 0.58F,
		0.98F, 0.24F,
		0.98F, 0.92F,
		0.50F, 1.00F
	};

	private final int size;
	private Image loaded;
	private boolean lookedForFile;

	public CrownIcon(int size) {
		this.size = size;
	}

	public int getIconWidth() {
		return this.size;
	}

	public int getIconHeight() {
		return this.size;
	}

	public void paintIcon(Component component, Graphics g, int x, int y) {
		Image var5 = this.image();
		Graphics2D var6 = (Graphics2D) g.create();
		var6.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		if (var5 != null) {
			// Centred, because the source is not square and stretching it to a square
			// box would flatten the crown.
			int var11 = var5.getWidth(null);
			int var12 = var5.getHeight(null);
			var6.drawImage(var5, x + (this.size - var11) / 2,
				y + (this.size - var12) / 2, null);
			var6.dispose();
			return;
		}
		Path2D.Float var7 = new Path2D.Float();
		for (int var8 = 0; var8 < SHAPE.length; var8 += 2) {
			float var9 = x + SHAPE[var8] * this.size;
			float var10 = y + SHAPE[var8 + 1] * this.size;
			if (var8 == 0) {
				var7.moveTo(var9, var10);
			} else {
				var7.lineTo(var9, var10);
			}
		}
		var7.closePath();
		// Lit from above, like the rest of the theme's gradients.
		var6.setPaint(new GradientPaint(0.0F, (float) y, new java.awt.Color(0xFFA347),
			0.0F, (float) (y + this.size), new java.awt.Color(0xD86A00)));
		var6.fill(var7);
		var6.dispose();
	}

	/** The override image, loaded once, or null when there is none. */
	private Image image() {
		if (!this.lookedForFile) {
			this.lookedForFile = true;
			File var1 = new File(new File(System.getProperty("user.home"), ".bwana"),
				"icon.png");
			if (var1.isFile()) {
				try {
					java.awt.image.BufferedImage var3 = ImageIO.read(var1);
					if (var3 != null) {
						// Scaled once, on load, with a smooth filter. The source is a
						// render several hundred pixels across and drawing it straight
						// into a sixteen-pixel box leaves it ragged; doing it here also
						// means the work happens once rather than every repaint.
						int var4 = var3.getWidth();
						int var5 = var3.getHeight();
						int var6 = var4 > var5 ? this.size : this.size * var4 / var5;
						int var7 = var4 > var5 ? this.size * var5 / var4 : this.size;
						this.loaded = var3.getScaledInstance(var6 < 1 ? 1 : var6,
							var7 < 1 ? 1 : var7, Image.SCALE_SMOOTH);
					}
				} catch (Exception var2) {
					System.err.println("bwana: could not read " + var1 + ": " + var2);
				}
			}
		}
		return this.loaded;
	}
}
