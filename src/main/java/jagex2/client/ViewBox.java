package jagex2.client;

import deob.ObfuscatedName;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import bwana.ui.BwanaUi;

@ObfuscatedName("b")
public class ViewBox extends JFrame {

	@ObfuscatedName("b.a")
	public int field31 = 8;

	@ObfuscatedName("b.b")
	public GameShell shell;

	/**
	 * The game's drawing surface. Everything the client renders goes here.
	 * <p>
	 * This is a heavyweight AWT Canvas rather than a Swing component on purpose:
	 * PixMap hands its ImageProducer to {@link java.awt.Component#createImage} and
	 * blits through a raw Graphics, which needs a native peer.
	 */
	public Canvas canvas;

	public ViewBox(final int arg0, int arg1, GameShell arg2, final int arg3) {
		if (arg1 != 35731) {
			this.field31 = -475;
		}
		this.shell = arg2;
		// Swing wants its components built on the event dispatch thread, and this
		// runs on the main thread. invokeAndWait keeps it synchronous, because
		// GameShell calls getCanvas().getGraphics() the moment we return.
		if (SwingUtilities.isEventDispatchThread()) {
			this.build(arg3, arg0);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						ViewBox.this.build(arg3, arg0);
					}
				});
			} catch (Exception var6) {
				throw new RuntimeException("could not build the game window", var6);
			}
		}
	}

	private void build(int arg0, int arg1) {
		BwanaUi.applyTheme(); // Bwana
		BwanaUi.configureHeavyweightPopups();

		this.canvas = new GameCanvas();
		this.canvas.setPreferredSize(new Dimension(arg0, arg1));
		this.canvas.setBackground(Color.black);
		// let the game see Tab instead of it moving focus
		this.canvas.setFocusTraversalKeysEnabled(false);

		// GridBagLayout with no constraints centres a child at its preferred size,
		// so the canvas keeps its exact dimensions if the window is ever resized.
		JPanel var3 = new JPanel(new GridBagLayout());
		var3.setBackground(Color.black);
		var3.add(this.canvas);

		this.setTitle("Bwana"); // Bwana
		// The window's own bar is drawn by the desktop and no Swing colour reaches
		// it, so it is turned off and replaced with one the theme owns. Must happen
		// before the frame is displayable. // Bwana
		this.setUndecorated(true); // Bwana
		// GameShell.windowClosing drives the shutdown, so don't let Swing do it
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// A rounded pane rather than a background colour plus a line border: the
		// latter fills the whole rectangle and squares off every corner underneath
		// whatever sits on it. // Bwana
		this.setContentPane(new bwana.ui.RoundedPane()); // Bwana
		this.getContentPane().add(new bwana.ui.TitleBar(this, "Bwana"),
			BorderLayout.NORTH); // Bwana
		this.getContentPane().add(var3, BorderLayout.CENTER);

		Component var4 = BwanaUi.createSidebar();
		if (var4 != null) {
			this.getContentPane().add(var4, BorderLayout.EAST);
		}
		Component var5 = BwanaUi.createStatusBar();
		if (var5 != null) {
			this.getContentPane().add(var5, BorderLayout.SOUTH);
		}

		this.pack();
		this.setMinimumSize(this.getSize());
		bwana.ui.FrameDecor.installRoundedCorners(this); // Bwana
		bwana.ui.FrameDecor.installResizeHandles(this); // Bwana
		this.setLocationRelativeTo(null);
		// must be showing before GameShell calls createImage on the canvas
		this.setVisible(true);
		this.toFront();
		this.canvas.requestFocus();
	}

	@ObfuscatedName("b.c()Ljava/awt/Canvas;")
	public Canvas getCanvas() {
		return this.canvas;
	}

	private final class GameCanvas extends Canvas {

		public final void update(Graphics arg0) {
			ViewBox.this.shell.update(arg0);
		}

		public final void paint(Graphics arg0) {
			ViewBox.this.shell.paint(arg0);
		}
	}
}
