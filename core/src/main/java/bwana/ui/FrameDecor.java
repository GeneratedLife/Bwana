package bwana.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;

/**
 * The parts of a window that come free with decorations, put back by hand.
 * <p>
 * Turning decorations off to get a themed title bar also removes rounded corners,
 * edge resizing and the maximise behaviour, because all of those belong to the
 * desktop's frame rather than to the window's contents. This adds them back.
 */
public final class FrameDecor {

	/** Corner radius, in the neighbourhood of what Windows itself uses. */
	private static final int RADIUS = 10;

	/** How close to an edge counts as grabbing it. */
	private static final int GRIP = 6;

	private FrameDecor() {
	}

	/**
	 * Round the top corners, and keep them rounded as the window changes size.
	 * <p>
	 * Only the top two, which is what the desktop does — the bottom corners sit
	 * against the taskbar edge often enough that rounding them reads as a mistake.
	 * Squared off entirely while maximised, again matching the desktop: a maximised
	 * window has no corners to round.
	 */
	public static void installRoundedCorners(final JFrame frame) {
		GraphicsDevice var1 = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice();
		if (!var1.isWindowTranslucencySupported(
				GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
			// Shaped windows are not available everywhere; square corners are a far
			// better outcome than a window that refuses to open.
			return;
		}
		reshape(frame);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				reshape(frame);
			}
		});
	}

	private static void reshape(JFrame frame) {
		int var1 = frame.getWidth();
		int var2 = frame.getHeight();
		if (var1 <= 0 || var2 <= 0) {
			return;
		}
		if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
			frame.setShape(null);
			return;
		}
		Area var3 = new Area(new RoundRectangle2D.Float(0.0F, 0.0F, (float) var1,
			(float) var2, (float) (RADIUS * 2), (float) (RADIUS * 2)));
		// Square the bottom back off by unioning a rectangle over everything below
		// the corner radius.
		var3.add(new Area(new Rectangle2D.Float(0.0F, (float) RADIUS, (float) var1,
			(float) (var2 - RADIUS))));
		frame.setShape(var3);
	}

	/**
	 * Drag any edge or corner to resize.
	 * <p>
	 * Listens on the content pane, whose border is the only part of the window not
	 * covered by a child — which is why that border needs to be a few pixels thick
	 * to be grabbable at all. The game canvas is a native component and swallows its
	 * own mouse events, but it sits in the middle and never reaches an edge.
	 */
	public static void installResizeHandles(final JFrame frame) {
		final Component var1 = frame.getContentPane();
		MouseAdapter var2 = new MouseAdapter() {

			private int edge;
			private Rectangle start;
			private Point origin;

			public void mouseMoved(MouseEvent event) {
				if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
					var1.setCursor(Cursor.getDefaultCursor());
					return;
				}
				var1.setCursor(cursorFor(edgeAt(event, var1)));
			}

			public void mouseExited(MouseEvent event) {
				var1.setCursor(Cursor.getDefaultCursor());
			}

			public void mousePressed(MouseEvent event) {
				this.edge = edgeAt(event, var1);
				this.start = frame.getBounds();
				this.origin = event.getLocationOnScreen();
			}

			public void mouseReleased(MouseEvent event) {
				this.edge = 0;
			}

			public void mouseDragged(MouseEvent event) {
				if (this.edge == 0 || this.start == null) {
					return;
				}
				Point var2x = event.getLocationOnScreen();
				int var3 = var2x.x - this.origin.x;
				int var4 = var2x.y - this.origin.y;
				Rectangle var5 = new Rectangle(this.start);
				Dimension var6 = frame.getMinimumSize();
				if ((this.edge & WEST) != 0) {
					int var7 = this.start.width - var3;
					if (var7 >= var6.width) {
						var5.x = this.start.x + var3;
						var5.width = var7;
					}
				}
				if ((this.edge & EAST) != 0) {
					var5.width = Math.max(var6.width, this.start.width + var3);
				}
				if ((this.edge & NORTH) != 0) {
					int var8 = this.start.height - var4;
					if (var8 >= var6.height) {
						var5.y = this.start.y + var4;
						var5.height = var8;
					}
				}
				if ((this.edge & SOUTH) != 0) {
					var5.height = Math.max(var6.height, this.start.height + var4);
				}
				frame.setBounds(var5);
				frame.validate();
			}
		};
		var1.addMouseListener(var2);
		var1.addMouseMotionListener(var2);
	}

	private static final int NORTH = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 4;
	private static final int EAST = 8;

	private static int edgeAt(MouseEvent event, Component component) {
		int var2 = 0;
		if (event.getY() < GRIP) {
			var2 |= NORTH;
		} else if (event.getY() >= component.getHeight() - GRIP) {
			var2 |= SOUTH;
		}
		if (event.getX() < GRIP) {
			var2 |= WEST;
		} else if (event.getX() >= component.getWidth() - GRIP) {
			var2 |= EAST;
		}
		return var2;
	}

	private static Cursor cursorFor(int edge) {
		if (edge == (NORTH | WEST)) {
			return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
		}
		if (edge == (NORTH | EAST)) {
			return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
		}
		if (edge == (SOUTH | WEST)) {
			return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
		}
		if (edge == (SOUTH | EAST)) {
			return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
		}
		if (edge == NORTH) {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		}
		if (edge == SOUTH) {
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		}
		if (edge == WEST) {
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		}
		if (edge == EAST) {
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		}
		return Cursor.getDefaultCursor();
	}
}
