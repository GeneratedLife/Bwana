package bwana.ui;

import bwana.Screenshot;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Odds and ends that do not deserve a tab of their own yet.
 */
public final class ToolsPanel extends JPanel {

	private final JLabel status = new JLabel(" ");
	private final JButton shoot = new JButton("Screenshot");

	public ToolsPanel() {
		super(new BorderLayout(0, 8));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.status.setFont(this.status.getFont().deriveFont(Font.PLAIN, 11.0F));
		this.status.setVerticalAlignment(JLabel.TOP);

		this.shoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ToolsPanel.this.takeScreenshot();
			}
		});

		JButton var1 = new JButton("Open folder");
		var1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ToolsPanel.this.openFolder();
			}
		});

		JPanel var2 = new JPanel(new GridLayout(0, 1, 0, 6));
		var2.add(this.shoot);
		var2.add(var1);

		this.add(var2, BorderLayout.NORTH);
		this.add(this.status, BorderLayout.CENTER);
	}

	private void takeScreenshot() {
		Window var1 = SwingUtilities.getWindowAncestor(this);
		final Canvas var2 = findCanvas(var1);
		if (var2 == null) {
			this.status.setText("<html>Could not find the game canvas.</html>");
			return;
		}

		// Robot photographs the screen, so raise the window first. This is a
		// mitigation, not a guarantee — an always-on-top window still wins.
		if (var1 != null) {
			var1.toFront();
		}

		final Rectangle var3 = boundsOnScreen(var2);
		this.shoot.setEnabled(false);
		this.status.setText("Capturing...");

		// disk I/O and the settle delay must not run on the EDT
		Thread var4 = new Thread(new Runnable() {
			public void run() {
				String var1x;
				try {
					// let the raise actually repaint before photographing it
					Thread.sleep(250L);
					File var2x = Screenshot.capture(var3);
					var1x = "Saved " + var2x.getName();
				} catch (Throwable var3x) {
					var1x = "Failed: " + var3x.getMessage();
				}
				final String var4x = var1x;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ToolsPanel.this.status.setText("<html>" + var4x + "</html>");
						ToolsPanel.this.shoot.setEnabled(true);
					}
				});
			}
		}, "bwana-screenshot");
		var4.setDaemon(true);
		var4.start();
	}

	/**
	 * Screen rectangle of the canvas, or null if it is not showing.
	 * <p>
	 * Must be called on the EDT — {@code getLocationOnScreen} throws if the
	 * component is not displayable, which is the case while minimised.
	 */
	private static Rectangle boundsOnScreen(Component component) {
		if (component == null || !component.isShowing()) {
			return null;
		}
		try {
			Point var1 = component.getLocationOnScreen();
			return new Rectangle(var1.x, var1.y, component.getWidth(), component.getHeight());
		} catch (Exception var2) {
			return null;
		}
	}

	/**
	 * Finds the game canvas by walking the window's component tree.
	 * <p>
	 * Deliberately not passed in from ViewBox: discovering it here means the
	 * upstream file needs no extra constructor argument, and the fork's diff stays
	 * where it is.
	 */
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

	private void openFolder() {
		try {
			File var1 = Screenshot.directory();
			if (!var1.exists() && !var1.mkdirs()) {
				return;
			}
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(var1);
			}
		} catch (Exception var2) {
			this.status.setText("<html>Could not open folder: " + var2.getMessage() + "</html>");
		}
	}
}
