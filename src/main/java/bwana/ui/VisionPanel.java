package bwana.ui;

import bwana.vision.ColorRule;
import bwana.vision.Detection;
import bwana.vision.RuleStore;
import bwana.vision.Vision;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Sidebar controls for the vision layer. The detail lives in
 * {@link VisionDebugWindow}; this is the switch and the summary.
 * <p>
 * Also responsible for telling {@link Vision} where the canvas sits on the
 * desktop, since only the Swing thread can ask that safely.
 */
public final class VisionPanel extends JPanel {

	private final Vision vision = Vision.getInstance();
	private final JLabel summary = new JLabel("Off");
	private VisionDebugWindow window;

	public VisionPanel() {
		super(new BorderLayout(0, 8));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		seedDefaultRules(this.vision);

		final JCheckBox var1 = new JCheckBox("Enable detection", this.vision.isEnabled());
		var1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionPanel.this.vision.setEnabled(var1.isSelected());
				if (!var1.isSelected()) {
					VisionPanel.this.summary.setText("Off");
				}
			}
		});

		JButton var2 = new JButton("Debug window");
		var2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionPanel.this.openWindow();
			}
		});

		JPanel var3 = new JPanel(new GridLayout(0, 1, 0, 6));
		var3.add(var1);
		var3.add(var2);

		this.summary.setFont(this.summary.getFont().deriveFont(Font.PLAIN, 11.0F));
		this.summary.setVerticalAlignment(JLabel.TOP);

		this.add(var3, BorderLayout.NORTH);
		this.add(this.summary, BorderLayout.CENTER);

		// keep the canvas' desktop position current so Detection can report
		// screen coordinates; getLocationOnScreen is only safe on the EDT
		Timer var4 = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionPanel.this.tick();
			}
		});
		var4.start();
	}

	private void tick() {
		Canvas var1 = findCanvas(SwingUtilities.getWindowAncestor(this));
		if (var1 != null && var1.isShowing()) {
			try {
				Point var2 = var1.getLocationOnScreen();
				this.vision.setScreenOrigin(var2.x, var2.y);
			} catch (Exception var3) {
				// window is minimised; keep the last known origin
			}
		}
		if (!this.vision.isEnabled()) {
			return;
		}
		List<Detection> var4 = this.vision.getDetections();
		StringBuilder var5 = new StringBuilder("<html>");
		var5.append(var4.size()).append(" regions<br>scan ")
			.append(this.vision.getLastScanMillis()).append(" ms<br><br>");
		List<ColorRule> var6 = this.vision.getRules();
		for (int var7 = 0; var7 < var6.size(); var7++) {
			ColorRule var8 = var6.get(var7);
			var5.append(var8.name).append(": ")
				.append(this.vision.getDetections(var8.name).size())
				.append(var8.enabled ? "" : " (off)").append("<br>");
		}
		var5.append("</html>");
		this.summary.setText(var5.toString());
	}

	private void openWindow() {
		if (this.window == null || !this.window.isDisplayable()) {
			this.window = new VisionDebugWindow();
		}
		this.window.setVisible(true);
		this.window.toFront();
	}

	/**
	 * Starter rules chosen so something is visible immediately on a 2004 scene.
	 * <p>
	 * These are starting points to be re-sampled by clicking the debug image, not
	 * tuned values — the renderer's palette shifts with lighting and level.
	 */
	private static void seedDefaultRules(Vision vision) {
		if (!vision.getRules().isEmpty()) {
			return;
		}
		// saved targets win over the built-in starters
		List<ColorRule> var0 = RuleStore.load();
		if (!var0.isEmpty()) {
			for (int var1x = 0; var1x < var0.size(); var1x++) {
				vision.addRule(var0.get(var1x));
			}
			return;
		}

		// Measured from the Lumbridge scene: canopy sits at H76 S68 V23 against
		// grass at H83 S84 V27. Hue and value barely differ, so saturation is the
		// axis that separates them and its tolerance has to stay tight.
		ColorRule var2x = new ColorRule("Tree", 0x313C13);
		var2x.mode = ColorRule.MODE_HSV;
		var2x.hueTolerance = 9;
		var2x.satTolerance = 8;
		var2x.valTolerance = 18;
		var2x.minArea = 80;
		var2x.overlayRgb = 0xFF8800;
		vision.addRule(var2x);

		ColorRule var1 = new ColorRule("Grass", 0x4A6B2A);
		var1.mode = ColorRule.MODE_HSV;
		var1.hueTolerance = 18;
		var1.satTolerance = 45;
		var1.valTolerance = 45;
		var1.minArea = 60;
		var1.overlayRgb = 0x00FF66;
		vision.addRule(var1);

		ColorRule var2 = new ColorRule("Water", 0x2B4C7E);
		var2.mode = ColorRule.MODE_HSV;
		var2.hueTolerance = 16;
		var2.satTolerance = 45;
		var2.valTolerance = 45;
		var2.minArea = 60;
		var2.overlayRgb = 0x33CCFF;
		vision.addRule(var2);

		ColorRule var3 = new ColorRule("Player dot", 0xFFFFFF);
		var3.mode = ColorRule.MODE_RGB;
		var3.rgbTolerance = 20;
		var3.minArea = 4;
		var3.enabled = false;
		var3.overlayRgb = 0xFFCC00;
		vision.addRule(var3);
	}

	private static Canvas findCanvas(Component root) {
		if (root instanceof Canvas) {
			return (Canvas) root;
		}
		if (root instanceof Container) {
			Component[] var1 = ((Container) root).getComponents();
			for (int var2 = 0; var2 < var1.length; var2++) {
				Canvas var3 = findCanvas(var1[var2]);
				if (var3 != null) {
					return var3;
				}
			}
		}
		return null;
	}
}
