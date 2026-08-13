package bwana.ui;

import bwana.GameEventBus;
import bwana.GameState;
import bwana.script.NavState;
import bwana.script.NavigationTest;
import bwana.script.Route;
import bwana.script.RouteStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Runs the navigation validation test, and builds the routes it follows.
 * <p>
 * Routes exist because the pathfinder only sees the loaded scene and cannot find
 * its way out of a pocket. The practical way to make one is to walk it: stand where
 * the route should turn and press <b>Add my position</b>. That is why the button is
 * here rather than in a route editor somewhere else — a waypoint is only worth
 * having if you know the player can stand on it.
 */
public final class NavigationPanel extends JPanel {

	private static final String NO_ROUTE = "(fields below)";

	private final NavigationTest test = new NavigationTest();

	private final JComboBox routes = new JComboBox();
	private final JTextField start = new JTextField("3222,3218");
	private final JTextField via = new JTextField();
	private final JTextField destination = new JTextField("3274,3431");
	private final JButton addHere = new JButton("Add my position");
	private final JButton saveRoute = new JButton("Save as route...");
	private final JButton deleteRoute = new JButton("Delete");
	private final JButton run = new JButton("Run test");
	private final JLabel state = new JLabel();
	private final JTextArea logView = new JTextArea();

	private int lastLogSize = -1;

	/** Guards the route selector while it is being repopulated. */
	private boolean syncing;

	public NavigationPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.state.setFont(this.state.getFont().deriveFont(Font.BOLD, 13.0F));
		this.logView.setFont(new Font("Monospaced", Font.PLAIN, 10));
		this.logView.setEditable(false);
		this.logView.setLineWrap(true);

		JPanel var1 = new JPanel();
		var1.setLayout(new BoxLayout(var1, BoxLayout.Y_AXIS));
		var1.add(labelled("Route", this.routes));
		var1.add(hint("<html>A leg is a coordinate (<i>3222,3218</i>) or an object "
			+ "name (<i>Bank booth</i>).<br>Names are only found within the loaded "
			+ "scene, so use coordinates for anything distant.</html>"));
		var1.add(labelled("Start", this.start));
		var1.add(labelled("Via (waypoints, ; separated)", this.via));
		var1.add(labelled("Destination", this.destination));

		JPanel var2 = new JPanel(new GridLayout(1, 2, 6, 0));
		var2.add(this.addHere);
		var2.add(this.saveRoute);
		var1.add(var2);

		JPanel var3 = new JPanel(new GridLayout(1, 2, 6, 0));
		var3.add(this.deleteRoute);
		var3.add(this.run);
		var1.add(var3);
		var1.add(this.state);

		this.add(var1, BorderLayout.NORTH);
		this.add(new JScrollPane(this.logView), BorderLayout.CENTER);

		this.routes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!NavigationPanel.this.syncing) {
					NavigationPanel.this.loadSelectedRoute();
				}
			}
		});
		this.addHere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NavigationPanel.this.addCurrentPosition();
			}
		});
		this.saveRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NavigationPanel.this.saveCurrentAsRoute();
			}
		});
		this.deleteRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NavigationPanel.this.deleteSelectedRoute();
			}
		});
		this.run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NavigationPanel.this.toggle();
			}
		});

		GameEventBus.addListener(this.test);
		this.reloadRoutes(null);

		Timer var4 = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NavigationPanel.this.refresh();
			}
		});
		var4.start();
		this.refresh();
	}

	private static JLabel hint(String html) {
		JLabel var1 = new JLabel(html);
		var1.setFont(var1.getFont().deriveFont(Font.PLAIN, 11.0F));
		var1.setForeground(Color.DARK_GRAY);
		var1.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		return var1;
	}

	private static JPanel labelled(String text, java.awt.Component field) {
		JPanel var2 = new JPanel(new BorderLayout(4, 0));
		JLabel var3 = new JLabel(text);
		var3.setFont(var3.getFont().deriveFont(Font.PLAIN, 11.0F));
		var2.add(var3, BorderLayout.NORTH);
		var2.add(field, BorderLayout.CENTER);
		var2.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		return var2;
	}

	// ---- routes ----

	private void reloadRoutes(String select) {
		this.syncing = true;
		this.routes.removeAllItems();
		this.routes.addItem(NO_ROUTE);
		List<Route> var2 = RouteStore.load();
		for (int var3 = 0; var3 < var2.size(); var3++) {
			this.routes.addItem(var2.get(var3).name);
		}
		if (select != null) {
			this.routes.setSelectedItem(select);
		}
		this.syncing = false;
	}

	private void loadSelectedRoute() {
		Object var1 = this.routes.getSelectedItem();
		if (var1 == null || NO_ROUTE.equals(var1)) {
			return;
		}
		List<Route> var2 = RouteStore.load();
		for (int var3 = 0; var3 < var2.size(); var3++) {
			Route var4 = var2.get(var3);
			if (var4.name.equals(var1)) {
				this.start.setText(legText(var4, 0));
				this.via.setText(var4.describeWaypoints());
				this.destination.setText(legText(var4, var4.legs.length - 1));
				return;
			}
		}
	}

	private static String legText(Route route, int index) {
		return route.legs[index].isFixed()
			? route.legs[index].fixedX + "," + route.legs[index].fixedY
			: route.legs[index].name;
	}

	/**
	 * Append the player's current tile to the waypoint list.
	 * <p>
	 * The whole point of route building: walk to the corner, press the button. A
	 * waypoint typed from a map is a guess about walkability; one captured where the
	 * player is standing is a fact.
	 */
	private void addCurrentPosition() {
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			this.state.setText("not logged in");
			this.state.setForeground(new Color(0xB00020));
			return;
		}
		String var2 = var1.getWorldX() + "," + var1.getWorldY();
		String var3 = this.via.getText().trim();
		this.via.setText(var3.length() == 0 ? var2 : var3 + "; " + var2);
	}

	private void saveCurrentAsRoute() {
		Object var1 = this.routes.getSelectedItem();
		String var2 = var1 == null || NO_ROUTE.equals(var1) ? "" : var1.toString();
		String var3 = JOptionPane.showInputDialog(this, "Route name", var2);
		if (var3 == null || var3.trim().length() == 0) {
			return;
		}
		Route var4 = Route.of(var3.trim(), this.start.getText(), this.via.getText(),
			this.destination.getText());
		if (var4 == null) {
			JOptionPane.showMessageDialog(this,
				"A route needs both a start and a destination.");
			return;
		}
		RouteStore.put(var4);
		this.reloadRoutes(var4.name);
	}

	private void deleteSelectedRoute() {
		Object var1 = this.routes.getSelectedItem();
		if (var1 == null || NO_ROUTE.equals(var1)) {
			return;
		}
		RouteStore.remove(var1.toString());
		this.reloadRoutes(null);
	}

	// ---- running ----

	private void toggle() {
		if (this.test.isRunning()) {
			this.test.stop();
			return;
		}
		Route var1 = Route.of("run", this.start.getText(), this.via.getText(),
			this.destination.getText());
		if (var1 == null) {
			this.state.setText("start and destination are both required");
			this.state.setForeground(new Color(0xB00020));
			return;
		}
		this.test.setLegs(var1.legs);
		this.test.start();
	}

	private void refresh() {
		boolean var1 = this.test.isRunning();
		this.run.setText(var1 ? "Stop" : "Run test");
		this.start.setEnabled(!var1);
		this.via.setEnabled(!var1);
		this.destination.setEnabled(!var1);
		this.routes.setEnabled(!var1);
		this.saveRoute.setEnabled(!var1);
		this.deleteRoute.setEnabled(!var1);

		int var2 = this.test.getState();
		this.state.setText("<html>" + this.test.getStateName()
			+ "<br><span style='font-weight:normal'><i>"
			+ escape(this.test.getNote()) + "</i></span></html>");
		this.state.setForeground(var2 == NavState.PASSED ? new Color(0x0A7D2C)
			: (var2 == NavState.FAILED ? new Color(0xB00020)
				: (var2 == NavState.IDLE ? Color.DARK_GRAY : new Color(0xB06A00))));

		int var3 = this.test.getLog().size();
		if (var3 != this.lastLogSize) {
			this.lastLogSize = var3;
			String[] var4 = this.test.getLog().snapshot();
			StringBuilder var5 = new StringBuilder();
			for (int var6 = 0; var6 < var4.length; var6++) {
				var5.append(var4[var6]).append('\n');
			}
			this.logView.setText(var5.toString());
			this.logView.setCaretPosition(this.logView.getDocument().getLength());
		}
	}

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
