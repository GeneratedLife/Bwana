package bwana.ui;

import bwana.vision.ColorRule;
import bwana.vision.Histogram;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Draws the hue, saturation and value distributions as three stacked charts.
 * <p>
 * Each chart shows the whole scene in grey and the pixels your rule matched in
 * colour, with the rule's accepted band shaded behind. Reading it:
 * <ul>
 * <li>colour filling the shaded band, grey outside it — the rule is taking what
 * you aimed at</li>
 * <li>grey humps inside the band that are not coloured — impossible; the band is
 * what the rule accepts on that axis, so a grey hump inside it means another axis
 * is rejecting those pixels, which is the axis actually doing the work</li>
 * <li>two grey humps with your band covering both — that is the cut you want,
 * and it tells you exactly where to put the boundary</li>
 * </ul>
 * Bars use a square-root scale: a linear one is dominated by whichever bin holds
 * the sky or the ground, and flattens everything else to nothing.
 */
public final class HistogramPanel extends JPanel {

	private static final int CHART_HEIGHT = 54;
	private static final int LABEL_WIDTH = 26;
	private static final int GAP = 16;

	private Histogram histogram;
	private ColorRule rule;

	public HistogramPanel() {
		// minimum as well as preferred: a layout that runs short on space must
		// shrink something else, not flatten the charts into a stripe
		this.setMinimumSize(new Dimension(200, (CHART_HEIGHT + GAP) * 3 + 8));
		this.setPreferredSize(new Dimension(300, (CHART_HEIGHT + GAP) * 3 + 8));
		this.setBackground(new Color(0x1E1E1E));
		this.setOpaque(true);
	}

	public void update(Histogram histogram, ColorRule rule) {
		this.histogram = histogram;
		this.rule = rule;
		this.repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.histogram == null) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("No frame yet", 8, 20);
			return;
		}
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 10.0F));

		float[] var2 = new float[3];
		int var3 = -1;
		int var4 = -1;
		int var5 = -1;
		int var6 = -1;
		int var7 = -1;
		int var8 = -1;
		int var9 = -1;
		if (this.rule != null) {
			ColorRule.toHsv(this.rule.targetRgb, var2);
			if (this.rule.mode == ColorRule.MODE_HSV) {
				var3 = (int) var2[0];
				var4 = this.rule.hueTolerance;
				var5 = (int) var2[1];
				var6 = this.rule.satTolerance;
				var7 = (int) var2[2];
				var8 = this.rule.valTolerance;
			} else {
				// in RGB mode there is no band on these axes; still show where the
				// target sits so the sample can be judged
				var9 = 1;
				var3 = (int) var2[0];
				var5 = (int) var2[1];
				var7 = (int) var2[2];
			}
		}

		int var10 = 4;
		this.chart(g, var10, "H", this.histogram.hueAll, this.histogram.hueMatched,
			360, var3, var9 == 1 ? -1 : var4, true);
		var10 += CHART_HEIGHT + GAP;
		this.chart(g, var10, "S", this.histogram.satAll, this.histogram.satMatched,
			100, var5, var9 == 1 ? -1 : var6, false);
		var10 += CHART_HEIGHT + GAP;
		this.chart(g, var10, "V", this.histogram.valAll, this.histogram.valMatched,
			100, var7, var9 == 1 ? -1 : var8, false);
	}

	/**
	 * @param target    target position in axis units, or -1
	 * @param tolerance half-width of the accepted band, or -1 for none
	 * @param wraps     true for hue, where the band can straddle 0
	 */
	private void chart(Graphics g, int top, String label, int[] all, int[] matched,
			int axisMax, int target, int tolerance, boolean wraps) {
		int var9 = this.getWidth() - LABEL_WIDTH - 8;
		if (var9 <= 0) {
			return;
		}
		int var10 = all.length;
		double var11 = (double) var9 / (double) var10;

		g.setColor(Color.GRAY);
		g.drawString(label, 6, top + CHART_HEIGHT / 2);

		// accepted band behind the bars
		if (target >= 0 && tolerance >= 0) {
			g.setColor(new Color(0x33, 0x55, 0x33));
			int var13 = target - tolerance;
			int var14 = target + tolerance;
			if (wraps) {
				if (var13 < 0) {
					this.band(g, top, LABEL_WIDTH, var11, var10, var13 + axisMax, axisMax - 1, axisMax);
					this.band(g, top, LABEL_WIDTH, var11, var10, 0, var14, axisMax);
				} else if (var14 >= axisMax) {
					this.band(g, top, LABEL_WIDTH, var11, var10, var13, axisMax - 1, axisMax);
					this.band(g, top, LABEL_WIDTH, var11, var10, 0, var14 - axisMax, axisMax);
				} else {
					this.band(g, top, LABEL_WIDTH, var11, var10, var13, var14, axisMax);
				}
			} else {
				this.band(g, top, LABEL_WIDTH, var11, var10,
					var13 < 0 ? 0 : var13, var14 > axisMax ? axisMax : var14, axisMax);
			}
		}

		int var15 = Histogram.max(all);
		if (var15 <= 0) {
			return;
		}
		// sqrt scale: one huge bin (sky, ground) would flatten everything else
		double var16 = Math.sqrt((double) var15);

		for (int var18 = 0; var18 < var10; var18++) {
			int var19 = LABEL_WIDTH + (int) ((double) var18 * var11);
			int var20 = (int) var11 + 1;

			int var21 = (int) (Math.sqrt((double) all[var18]) / var16 * (double) CHART_HEIGHT);
			g.setColor(new Color(0x55, 0x55, 0x55));
			g.fillRect(var19, top + CHART_HEIGHT - var21, var20, var21);

			int var22 = (int) (Math.sqrt((double) matched[var18]) / var16 * (double) CHART_HEIGHT);
			if (var22 > 0) {
				g.setColor(colourFor(label, var18, var10));
				g.fillRect(var19, top + CHART_HEIGHT - var22, var20, var22);
			}
		}

		// target marker
		if (target >= 0) {
			int var23 = LABEL_WIDTH + (int) ((double) target / (double) axisMax * (double) var9);
			g.setColor(Color.WHITE);
			g.drawLine(var23, top, var23, top + CHART_HEIGHT);
		}

		g.setColor(new Color(0x77, 0x77, 0x77));
		g.drawLine(LABEL_WIDTH, top + CHART_HEIGHT, LABEL_WIDTH + var9, top + CHART_HEIGHT);
		g.setColor(Color.GRAY);
		g.drawString("0", LABEL_WIDTH, top + CHART_HEIGHT + 11);
		String var24 = Integer.toString(axisMax);
		g.drawString(var24, LABEL_WIDTH + var9 - 14, top + CHART_HEIGHT + 11);
	}

	private void band(Graphics g, int top, int left, double scale, int bins, int lo, int hi, int axisMax) {
		int var9 = (int) ((double) lo / (double) axisMax * (double) bins * scale);
		int var10 = (int) ((double) hi / (double) axisMax * (double) bins * scale);
		g.fillRect(left + var9, top, Math.max(1, var10 - var9), CHART_HEIGHT);
	}

	/** Hue bars are drawn in their own hue; saturation and value stay neutral. */
	private static Color colourFor(String axis, int bin, int bins) {
		if ("H".equals(axis)) {
			return new Color(Color.HSBtoRGB((float) bin / (float) bins, 0.9F, 1.0F));
		}
		return new Color(0x66, 0xCC, 0x66);
	}
}
