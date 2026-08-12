package bwana.ui;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.inspect.DebugOverlay;
import bwana.inspect.EntityInspector;
import bwana.inspect.Highlight;
import bwana.inspect.ProjectionDebug;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Finds the nearest object of a given name, points the mouse at it and boxes it
 * in the game view.
 * <p>
 * This is a test rig, not a tool. It exists to prove one chain end to end: the
 * game's world coordinates, the projection into viewport space, and the desktop
 * position derived from that. If the box lands on the tree and the cursor lands
 * in the box, all three agree. If the box is right but the cursor is not, the
 * error is in the canvas-to-desktop offset; if the box itself is wrong, it is in
 * the projection.
 * <p>
 * The search runs on the game thread, since it walks the scene. Moving the mouse
 * happens afterwards on the Swing thread.
 */
public final class InteractTestPanel extends JPanel {

	/** Where the viewport sits inside the canvas. */
	private static final int VIEWPORT_X = 8;
	private static final int VIEWPORT_Y = 11;

	private static final int HIGHLIGHT_MILLIS = 6000;
	private static final int HIGHLIGHT_RGB = 0x00FF66;

	private final JTextField filter = new JTextField("Tree");
	private final JCheckBox moveMouse = new JCheckBox("Move the mouse to it", true);
	private final JCheckBox repeat = new JCheckBox("Keep re-testing every second");
	private final JCheckBox useModelCentre = new JCheckBox("Aim at model centre instead of ground");
	private final JCheckBox overlay = new JCheckBox("Crosshair every matching object (live)");
	private final JLabel result = new JLabel("<html><i>Log in and press Find.</i></html>");

	private volatile boolean requested;
	private volatile String requestedName = "Tree";
	private volatile boolean overlayDue;

	public InteractTestPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.result.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.result.setVerticalAlignment(JLabel.TOP);

		JButton var1 = new JButton("Find nearest and point at it");
		var1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				InteractTestPanel.this.request();
			}
		});

		JPanel var2 = new JPanel();
		var2.setLayout(new BoxLayout(var2, BoxLayout.Y_AXIS));
		var2.add(new JLabel("<html>Names, comma separated<br>"
			+ "<i>e.g. Man, Goblin, Tree</i></html>"));
		var2.add(this.filter);
		var2.add(var1);
		var2.add(this.moveMouse);
		var2.add(this.useModelCentre);
		var2.add(this.overlay);
		var2.add(this.repeat);

		this.overlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DebugOverlay.setEnabled(InteractTestPanel.this.overlay.isSelected());
				InteractTestPanel.this.overlayDue = true;
			}
		});

		this.add(var2, BorderLayout.NORTH);
		this.add(new JScrollPane(this.result), BorderLayout.CENTER);

		Timer var3 = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (InteractTestPanel.this.repeat.isSelected()) {
					InteractTestPanel.this.request();
				}
			}
		});
		var3.start();

		GameEventBus.addListener(new Runner());
	}

	/** EDT: ask the game thread to do the search on its next tick. */
	private void request() {
		this.requestedName = this.filter.getText();
		this.requested = true;
		this.overlayDue = true;
	}

	/** Game thread: walk the scene, publish the result, arm the highlight. */
	private final class Runner extends GameEventsAdapter {

		/** Rebuild the overlay list about twice a second as the player walks. */
		private static final int TICKS_PER_OVERLAY_REFRESH = 25;

		private int ticks;

		public void onTick() {
			EntityInspector var1x = GameEventBus.getInspector();
			if (var1x != null && DebugOverlay.isEnabled()
					&& (InteractTestPanel.this.overlayDue || ++this.ticks >= TICKS_PER_OVERLAY_REFRESH)) {
				// only the list is rebuilt here; the markers themselves are
				// projected every frame by the render hook
				this.ticks = 0;
				InteractTestPanel.this.overlayDue = false;
				var1x.refreshDebugOverlay(InteractTestPanel.this.requestedName, 64);
			}

			if (!InteractTestPanel.this.requested) {
				return;
			}
			InteractTestPanel.this.requested = false;

			EntityInspector var1 = GameEventBus.getInspector();
			if (var1 == null) {
				return;
			}
			ProjectionDebug var2 = var1.debugNearest(InteractTestPanel.this.requestedName);
			if (var2 == null) {
				Highlight.clear();
				InteractTestPanel.this.publish("<html><b>No match</b> for \""
					+ escape(InteractTestPanel.this.requestedName) + "\" within range.</html>", -1, -1);
				return;
			}

			// Which anchor to aim at is the question under test, so it is a choice
			// rather than a constant.
			int var3;
			int var4;
			String var5;
			if (InteractTestPanel.this.useModelCentre.isSelected() && var2.centreScreenX >= 0) {
				var3 = var2.centreScreenX;
				var4 = var2.centreScreenY;
				var5 = "model centre (anchor at half of model height)";
			} else {
				var3 = var2.targetScreenX;
				var4 = var2.targetScreenY;
				var5 = var2.targetDescription;
			}

			Highlight.show(var2.footprintBoxX, var2.footprintBoxY,
				var2.footprintBoxW, var2.footprintBoxH,
				HIGHLIGHT_RGB, var2.name + " (" + var2.distance + " tiles)", HIGHLIGHT_MILLIS);
			if (var2.modelBoxValid) {
				Highlight.setSecondBox(var2.modelBoxX, var2.modelBoxY,
					var2.modelBoxW, var2.modelBoxH, 0xFF3399);
			}
			Highlight.setCrosshair(var3, var4);

			InteractTestPanel.this.publish(describe(var2, var3, var4, var5), var3, var4);
		}
	}

	/** Every number in the chain, grouped so a mismatch can be attributed. */
	private static String describe(ProjectionDebug d, int targetX, int targetY, String targetWhat) {
		StringBuilder var4 = new StringBuilder("<html>");
		var4.append("<b>").append(escape(d.name)).append("</b>  id ").append(d.id).append("<br>");
		var4.append("&nbsp;footprint ").append(d.width).append('x').append(d.length)
			.append("  shape ").append(d.shape).append("  rot ").append(d.rotation).append("<br>");
		var4.append("&nbsp;distance ").append(d.distance).append(" tiles<br><br>");

		var4.append("<b>Object position</b><br>");
		var4.append("&nbsp;world ").append(d.worldX).append(", ").append(d.worldY)
			.append("  plane ").append(d.plane).append("<br>");
		var4.append("&nbsp;scene tile ").append(d.tileX).append(", ").append(d.tileZ).append("<br>");
		var4.append("&nbsp;anchor local ").append(d.anchorX).append(", ").append(d.anchorZ)
			.append("<br>");
		var4.append("&nbsp;terrain y ").append(d.anchorGroundY).append("<br><br>");

		var4.append("<b>Model</b><br>");
		if (d.modelAvailable) {
			var4.append("&nbsp;height ").append(d.modelMaxY)
				.append("  radius ").append(d.modelRadius).append("<br>");
			var4.append("&nbsp;minY ").append(d.modelMinY)
				.append("  x ").append(d.modelMinX).append(" to ").append(d.modelMaxX).append("<br>");
			if (d.modelBoxValid) {
				var4.append("&nbsp;screen box ").append(d.modelBoxW).append('x').append(d.modelBoxH)
					.append(" at ").append(d.modelBoxX).append(", ").append(d.modelBoxY).append("<br>");
			}
		} else {
			var4.append("&nbsp;<i>not resolvable</i><br>");
		}
		var4.append("<br>");

		var4.append("<b>Anchor candidates (viewport)</b><br>");
		var4.append("&nbsp;tile origin&nbsp;&nbsp; ").append(d.originScreenX).append(", ")
			.append(d.originScreenY).append("<br>");
		var4.append("&nbsp;ground centre ").append(d.groundScreenX).append(", ")
			.append(d.groundScreenY).append("<br>");
		var4.append("&nbsp;model centre&nbsp; ").append(d.centreScreenX).append(", ")
			.append(d.centreScreenY).append("<br>");
		var4.append("&nbsp;model top&nbsp;&nbsp;&nbsp;&nbsp; ").append(d.topScreenX).append(", ")
			.append(d.topScreenY).append("<br>");
		var4.append("&nbsp;footprint box ").append(d.footprintBoxW).append('x')
			.append(d.footprintBoxH).append(" at ").append(d.footprintBoxX).append(", ")
			.append(d.footprintBoxY).append("<br><br>");

		var4.append("<b>Aiming at</b> ").append(targetX).append(", ").append(targetY).append("<br>");
		var4.append("&nbsp;").append(escape(targetWhat)).append("<br><br>");

		var4.append("<b>Observer</b><br>");
		var4.append("&nbsp;player world ").append(d.playerWorldX).append(", ")
			.append(d.playerWorldY).append("<br>");
		var4.append("&nbsp;player tile&nbsp; ").append(d.playerTileX).append(", ")
			.append(d.playerTileZ).append("<br>");
		var4.append("&nbsp;camera ").append(d.cameraX).append(", ").append(d.cameraY)
			.append(", ").append(d.cameraZ).append("<br>");
		var4.append("&nbsp;pitch ").append(d.cameraPitch).append(" (")
			.append(d.getCameraPitchDegrees()).append("deg)  yaw ").append(d.cameraYaw)
			.append(" (").append(d.getCameraYawDegrees()).append("deg)<br><br>");

		var4.append("<i>green box = ground footprint<br>");
		var4.append("pink box = model bounds<br>");
		var4.append("yellow cross = aim point</i>");
		return var4.append("</html>").toString();
	}

	/**
	 * Hop to Swing to show the result and, if asked, move the pointer.
	 *
	 * @param viewportX centre of the highlight, or -1 to leave the pointer alone
	 */
	private void publish(final String html, final int viewportX, final int viewportY) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InteractTestPanel.this.result.setText(html);
				if (viewportX < 0 || !InteractTestPanel.this.moveMouse.isSelected()) {
					return;
				}
				Canvas var1 = findCanvas(SwingUtilities.getWindowAncestor(InteractTestPanel.this));
				if (var1 == null || !var1.isShowing()) {
					return;
				}
				try {
					Point var2 = var1.getLocationOnScreen();
					// viewport -> canvas -> desktop, the last hop being the one the
					// projection cannot know about
					new Robot().mouseMove(var2.x + VIEWPORT_X + viewportX,
						var2.y + VIEWPORT_Y + viewportY);
				} catch (Exception var3) {
					InteractTestPanel.this.result.setText(
						"<html>Could not move the mouse: " + escape(var3.getMessage()) + "</html>");
				}
			}
		});
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

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
