package bwana.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;
import bwana.XpTracker;

/**
 * Builds the toolkit chrome that surrounds the game canvas.
 * <p>
 * {@code ViewBox} calls into here rather than containing this code, so the fork's
 * changes to upstream files stay to a handful of lines.
 * <p>
 * <b>Threading.</b> Everything here is constructed and mutated on the Swing event
 * dispatch thread. The only data crossing from the game thread is a snapshot
 * taken in {@link StatusPump#onTick}, which is handed over via
 * {@code invokeLater}. Nothing on this side ever reads a client field.
 */
public final class BwanaUi {

	public static final int SIDEBAR_WIDTH = 250;

	/** Update the status bar 4x a second rather than 50. */
	private static final int TICKS_PER_STATUS_UPDATE = 12;

	private static JLabel statusState;
	private static JLabel statusPosition;

	private BwanaUi() {
	}

	/**
	 * Popups and tooltips must be heavyweight, or they render <i>behind</i> the
	 * game canvas. The canvas is a native AWT component, so a lightweight popup
	 * drawn into the Swing layer has no way to appear above it.
	 */
	/** Apply the colour theme. Called before the window is built. */
	public static void applyTheme() {
		Theme.apply();
	}

	public static void configureHeavyweightPopups() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
	}

	/** The docked tool panel. */
	public static Component createSidebar() {
		JTabbedPane var0 = new JTabbedPane();
		var0.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 100));
		// Wrap into as many rows as it takes. Scrolling kept the tabs to one row but
		// hid most of them behind a two-arrow strip, which is worse than losing the
		// few pixels of panel height: you cannot pick a tab you cannot see.
		var0.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

		XpTrackerPanel var1 = new XpTrackerPanel();
		final XpTracker var2 = var1.getTracker();
		GameEventBus.addListener(var2);
		// closing the window calls System.exit, which runs shutdown hooks, so an
		// unfinished session still reaches disk
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				var2.persistNow();
			}
		}, "bwana-persist"));
		var0.addTab("XP", var1);

		ChatLogPanel var3 = new ChatLogPanel();
		GameEventBus.addListener(var3.getLog());
		var0.addTab("Chat", var3);

		var0.addTab("Inspect", new InspectPanel());
		var0.addTab("Target", new TargetPanel());
		var0.addTab("Action", new ActionPanel());
		var0.addTab("Chop", new WoodcuttingPanel());
		var0.addTab("Navigate", new NavigationPanel());
		var0.addTab("Plan", new PlanPanel());
		var0.addTab("Interact", new InteractTestPanel());
		var0.addTab("Vision", new VisionPanel());
		var0.addTab("Targets", new TargetsPanel());
		var0.addTab("Notes", new NotesPanel());
		var0.addTab("Tools", new ToolsPanel());

		return var0;
	}

	/** The status strip along the bottom. */
	public static Component createStatusBar() {
		JPanel var0 = new JPanel(new BorderLayout(12, 0));
		var0.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
			BorderFactory.createEmptyBorder(4, 8, 4, 8)));

		statusState = new JLabel("not logged in");
		statusPosition = new JLabel(" ");

		Font var1 = statusState.getFont().deriveFont(Font.PLAIN, 11.0F);
		statusState.setFont(var1);
		statusPosition.setFont(var1);

		var0.add(statusState, BorderLayout.WEST);
		var0.add(statusPosition, BorderLayout.EAST);

		GameEventBus.addListener(new StatusPump());
		return var0;
	}

	/**
	 * Runs on the game thread, snapshots a few values, and pushes them to the EDT.
	 * <p>
	 * This is the pattern every future tool should copy: read {@link GameState}
	 * here, never from Swing.
	 */
	private static final class StatusPump extends GameEventsAdapter {

		private int ticks;

		public void onTick() {
			if (++this.ticks < TICKS_PER_STATUS_UPDATE) {
				return;
			}
			this.ticks = 0;

			GameState var1 = GameEventBus.getGameState();
			if (var1 == null) {
				return;
			}

			// snapshot on the game thread; these locals are all the EDT ever sees
			final boolean var2 = var1.isLoggedIn();
			final String var3 = var1.getLocalPlayerName();
			final int var4 = var1.getWorldX();
			final int var5 = var1.getWorldY();
			final int var6 = var1.getPlane();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (statusState == null) {
						return;
					}
					if (var2) {
						statusState.setText(var3 == null ? "logged in" : var3);
						statusPosition.setText(var4 + ", " + var5 + (var6 > 0 ? "  (level " + var6 + ")" : ""));
					} else {
						statusState.setText("not logged in");
						statusPosition.setText(" ");
					}
				}
			});
		}
	}
}
