package bwana.ui;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.inspect.EntityInfo;
import bwana.inspect.EntityInspector;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Reads out whatever the cursor is over in the game, from the game's own data.
 * <p>
 * The lookup runs on the game thread inside {@code onTick}, because it walks live
 * entity arrays and reads the scene's hover state — both rewritten every frame.
 * Only the finished text crosses to Swing.
 * <p>
 * Throttled to a few times a second: the answer cannot change faster than the
 * cursor moves, and formatting HTML sixty times a second would be waste.
 */
public final class InspectPanel extends JPanel {

	private static final int TICKS_PER_UPDATE = 10;

	/** Where the viewport sits inside the canvas. */
	private static final int VIEWPORT_X = 8;
	private static final int VIEWPORT_Y = 11;

	private final JLabel body = new JLabel();
	private final JCheckBox freeze = new JCheckBox("Freeze (stop following the cursor)");
	private final JLabel stale = new JLabel(" ");
	private final JCheckBox showAll = new JCheckBox("Show everything under the cursor");

	private volatile int screenOriginX;
	private volatile int screenOriginY;
	private volatile int canvasWidth;
	private volatile int canvasHeight;
	private volatile boolean pointerInside;

	public InspectPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.body.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.body.setVerticalAlignment(JLabel.TOP);
		this.body.setText("<html><i>Hover the game view.</i></html>");

		this.stale.setFont(this.stale.getFont().deriveFont(Font.ITALIC, 11.0F));
		this.stale.setForeground(new Color(0x88, 0x66, 0x00));

		JPanel var2 = new JPanel(new java.awt.GridLayout(0, 1));
		var2.add(this.freeze);
		var2.add(this.showAll);
		var2.add(this.stale);
		this.add(var2, BorderLayout.NORTH);
		this.add(new JScrollPane(this.body), BorderLayout.CENTER);

		// Only the Swing thread may ask where the canvas sits on the desktop, but
		// the readout is built on the game thread, so the position is mirrored here
		// for it to read. Same arrangement the vision layer uses.
		Timer var1 = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				InspectPanel.this.refreshOrigin();
			}
		});
		var1.start();

		GameEventBus.addListener(new Poller());
	}

	private void refreshOrigin() {
		Canvas var1 = findCanvas(SwingUtilities.getWindowAncestor(this));
		if (var1 == null || !var1.isShowing()) {
			return;
		}
		try {
			Point var2 = var1.getLocationOnScreen();
			this.screenOriginX = var2.x;
			this.screenOriginY = var2.y;
			this.canvasWidth = var1.getWidth();
			this.canvasHeight = var1.getHeight();
		} catch (Exception var3) {
			// minimised; keep the last known position
		}
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

	/** Game thread: do the lookup, hand finished text to Swing. */
	private final class Poller extends GameEventsAdapter {

		private int ticks;

		public void onTick() {
			if (++this.ticks < TICKS_PER_UPDATE || InspectPanel.this.freeze.isSelected()) {
				return;
			}
			this.ticks = 0;
			EntityInspector var1 = GameEventBus.getInspector();
			if (var1 == null) {
				return;
			}

			// The client's own mouseX/mouseY only change while the pointer is over
			// the canvas -- once it leaves, they keep their last value forever, so
			// reading them reports whatever was under the cursor when it crossed
			// the edge. That is why an entity kept appearing with the mouse
			// elsewhere. Ask the desktop instead, and hold the last good reading
			// rather than overwriting it with a stale position.
			PointerInfo var2 = MouseInfo.getPointerInfo();
			if (var2 == null) {
				return;
			}
			Point var3 = var2.getLocation();
			int var4 = var3.x - InspectPanel.this.screenOriginX;
			int var5 = var3.y - InspectPanel.this.screenOriginY;
			int var6 = InspectPanel.this.canvasWidth;
			int var7 = InspectPanel.this.canvasHeight;
			boolean var8 = var6 > 0 && var7 > 0
				&& var4 >= 0 && var5 >= 0 && var4 < var6 && var5 < var7;

			if (!var8) {
				if (InspectPanel.this.pointerInside) {
					InspectPanel.this.pointerInside = false;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							InspectPanel.this.stale.setText(
								"Pointer left the game view - showing last reading");
						}
					});
				}
				return;
			}
			InspectPanel.this.pointerInside = true;

			// inspectAtCursor uses the renderer's own picking, which is exact;
			// the pointer check above only decides whether that result is current
			EntityInfo[] var9 = var1.inspectAtCursor();
			final String var10 = InspectPanel.this.format(var9, var4, var5);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					InspectPanel.this.body.setText(var10);
					InspectPanel.this.stale.setText(" ");
				}
			});
		}
	}

	/**
	 * @param canvasX cursor position within the game canvas
	 */
	private String format(EntityInfo[] entities, int canvasX, int canvasY) {
		// Three spaces, because each is the right one somewhere: viewport matches
		// what the renderer and the vision layer use, canvas is what the client's
		// own mouse handling sees, and screen is what a click would need.
		String var4 = "<html><b>Mouse</b><br>"
			+ "&nbsp;viewport " + (canvasX - VIEWPORT_X) + ", " + (canvasY - VIEWPORT_Y) + "<br>"
			+ "&nbsp;canvas " + canvasX + ", " + canvasY + "<br>"
			+ "&nbsp;screen " + (canvasX + this.screenOriginX) + ", "
			+ (canvasY + this.screenOriginY) + "<br><br>";

		if (entities == null || entities.length == 0) {
			return var4 + "<i>Nothing under the cursor.</i></html>";
		}
		// One answer by default. The loc column test is generous by necessity, so
		// several things genuinely contain the cursor and listing them all buries
		// the one you were pointing at. They are ranked, so the first is it.
		int var2x = this.showAll.isSelected() ? entities.length : 1;

		StringBuilder var1 = new StringBuilder(var4);
		for (int var2 = 0; var2 < var2x; var2++) {
			EntityInfo var3 = entities[var2];
			var1.append("<b>").append(var3.getKindName()).append("</b>");
			if (var3.name != null) {
				var1.append(" &mdash; ").append(escape(var3.name));
			}
			var1.append("<br>");
			if (var3.id >= 0) {
				var1.append("&nbsp;id ").append(var3.id);
				if (var3.definition != null) {
					var1.append(" (").append(escape(var3.definition)).append(')');
				}
				var1.append("<br>");
			} else if (var3.definition != null) {
				var1.append("&nbsp;").append(escape(var3.definition)).append("<br>");
			}

			var1.append("&nbsp;world ").append(var3.worldX).append(", ").append(var3.worldY)
				.append("  plane ").append(var3.plane).append("<br>");
			var1.append("&nbsp;tile ").append(var3.tileX).append(", ").append(var3.tileZ);
			if (var3.localX >= 0) {
				var1.append("  local ").append(var3.localX).append(", ").append(var3.localZ);
			}
			var1.append("<br>");

			if (var3.screenX >= 0) {
				var1.append("&nbsp;viewport ").append(var3.screenX).append(", ").append(var3.screenY)
					.append("<br>");
				// absolute desktop position: what a click would actually need
				var1.append("&nbsp;<b>screen ")
					.append(var3.screenX + VIEWPORT_X + this.screenOriginX).append(", ")
					.append(var3.screenY + VIEWPORT_Y + this.screenOriginY).append("</b><br>");
				if (var3.boxWidth > 0) {
					var1.append("&nbsp;box ").append(var3.boxWidth).append('x').append(var3.boxHeight)
						.append(" at ").append(var3.boxX).append(", ").append(var3.boxY).append("<br>");
				}
			} else {
				var1.append("&nbsp;not projected (off screen)<br>");
			}

			if (var3.animationId >= 0 || var3.orientation >= 0 || var3.interaction != null) {
				var1.append("&nbsp;");
				if (var3.animationId >= 0) {
					var1.append("anim ").append(var3.animationId).append("  ");
				}
				if (var3.orientation >= 0) {
					var1.append("facing ").append(var3.getOrientationDegrees()).append("deg  ");
				}
				var1.append("<br>");
				if (var3.interaction != null) {
					var1.append("&nbsp;interacting with ").append(escape(var3.interaction)).append("<br>");
				}
			}
			var1.append("<br>");
		}
		if (!this.showAll.isSelected() && entities.length > 1) {
			var1.append("<i>").append(entities.length - 1)
				.append(" more here (tick 'show everything')</i>");
		}
		var1.append("</html>");
		return var1.toString();
	}

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
