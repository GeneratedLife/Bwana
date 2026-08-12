package bwana.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * The window's inside: black with a rounded top and an orange outline.
 * <p>
 * Replaces a plain content pane with a line border, which could not work. That
 * combination fills the <em>whole rectangle</em> black and draws a square outline,
 * so rounding anything sitting on top of it — the title bar, say — achieved
 * nothing: the corners were painted straight back in from underneath. Shaping the
 * window clips the very outside, but everything inside the clip stayed square.
 * <p>
 * Painting the background and the outline together, on one rounded path, is what
 * makes the curve actually appear.
 */
public final class RoundedPane extends JPanel {

	/** Matches the shape applied to the window itself. */
	private static final int CORNER = 10;

	/** Border thickness, and therefore how far children are inset. */
	private static final int EDGE = 3;

	public RoundedPane() {
		super(new BorderLayout());
		this.setOpaque(false);
		this.setBackground(Theme.BACKGROUND);
		// Keeps children off the outline, and leaves a strip thick enough to grab
		// for resizing.
		this.setBorder(BorderFactory.createEmptyBorder(EDGE, EDGE, EDGE, EDGE));
	}

	protected void paintComponent(Graphics g) {
		Graphics2D var2 = (Graphics2D) g.create();
		var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		int var3 = this.getWidth();
		int var4 = this.getHeight();

		// Top corners only: the bottom sits against the desktop edge often enough
		// that rounding it reads as a mistake.
		var2.setColor(Theme.BACKGROUND);
		var2.fillRoundRect(0, 0, var3, var4 + CORNER, CORNER * 2, CORNER * 2);

		var2.setColor(Theme.ORANGE);
		var2.setStroke(new BasicStroke((float) EDGE));
		int var5 = EDGE / 2;
		var2.drawRoundRect(var5, var5, var3 - EDGE, var4 + CORNER,
			CORNER * 2, CORNER * 2);
		var2.dispose();
		super.paintComponent(g);
	}
}
