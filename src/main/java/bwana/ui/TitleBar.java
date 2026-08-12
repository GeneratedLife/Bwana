package bwana.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A title bar the theme can actually colour.
 * <p>
 * The window's own bar is drawn by the operating system, so no Swing colour
 * reaches it — it stays whatever the desktop says regardless of the look and feel.
 * The only way to have a black bar with an orange edge is to turn the decorations
 * off and draw one, which also means providing the parts that came with them:
 * dragging the window, minimising, and closing.
 * <p>
 * Closing goes through the frame's own window-closing event rather than exiting
 * directly, because the client hangs its shutdown off that — bypassing it would
 * skip the session and map writes that happen on the way out.
 */
public final class TitleBar extends JPanel {

	private static final int HEIGHT = 26;

	private Point dragOffset;

	public TitleBar(final JFrame frame, String title) {
		super(new BorderLayout());
		this.setBackground(Theme.BACKGROUND);
		// Transparent: the rounded pane underneath supplies the background, curve
		// and all, so painting a rectangle here would square the corners back off.
		this.setOpaque(false);
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.ORANGE));
		this.setPreferredSize(new Dimension(100, HEIGHT));

		JLabel var3 = new JLabel(" " + title, new CrownIcon(HEIGHT - 6),
			javax.swing.SwingConstants.LEFT);
		var3.setForeground(Theme.ORANGE);
		var3.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 13));
		var3.setIconTextGap(5);
		var3.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));
		this.add(var3, BorderLayout.WEST);

		JPanel var4 = new JPanel();
		var4.setOpaque(false);
		var4.setLayout(new BoxLayout(var4, BoxLayout.X_AXIS));
		var4.add(button("–", new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.setExtendedState(Frame.ICONIFIED);
			}
		}));
		var4.add(Box.createHorizontalStrut(4));
		var4.add(button("❐", new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Toggle rather than always maximise, so the button restores too.
				if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
					frame.setExtendedState(Frame.NORMAL);
				} else {
					frame.setExtendedState(Frame.MAXIMIZED_BOTH);
				}
			}
		}));
		var4.add(Box.createHorizontalStrut(4));
		var4.add(button("✕", new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// The client shuts itself down from this event; calling exit here
				// would skip the session and navigation-map writes.
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		}));
		var4.add(Box.createHorizontalStrut(4));
		this.add(var4, BorderLayout.EAST);

		// Undecorated frames cannot be moved, so the bar has to do it.
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				if (event.getClickCount() == 2) {
					if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
						frame.setExtendedState(Frame.NORMAL);
					} else {
						frame.setExtendedState(Frame.MAXIMIZED_BOTH);
					}
					return;
				}
				TitleBar.this.dragOffset = event.getPoint();
			}

			public void mouseReleased(MouseEvent event) {
				TitleBar.this.dragOffset = null;
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				Point var2 = TitleBar.this.dragOffset;
				if (var2 != null && (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
					Point var3x = event.getLocationOnScreen();
					frame.setLocation(var3x.x - var2.x, var3x.y - var2.y);
				}
			}
		});
	}

	private static JButton button(String text, ActionListener action) {
		JButton var2 = new JButton(text);
		var2.setFont(new Font("Dialog", Font.BOLD, 12));
		var2.setForeground(Color.WHITE);
		var2.setFocusPainted(false);
		var2.setMargin(new java.awt.Insets(0, 6, 0, 6));
		var2.setPreferredSize(new Dimension(30, HEIGHT - 6));
		var2.setMaximumSize(new Dimension(30, HEIGHT - 6));
		var2.addActionListener(action);
		return var2;
	}
}
