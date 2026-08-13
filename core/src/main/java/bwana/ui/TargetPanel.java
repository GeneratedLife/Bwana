package bwana.ui;

import bwana.GameEventBus;
import bwana.inspect.EntityInfo;
import bwana.inspect.NameFilter;
import bwana.target.Selection;
import bwana.target.TargetCriteria;
import bwana.target.TargetHud;
import bwana.target.TargetTracker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Drives the target selector and shows what it is doing.
 * <p>
 * Milestone one: identification and tracking only. Nothing here acts on the
 * target, which is deliberate — the selection logic has to be shown correct
 * before anything is built on top of it.
 */
public final class TargetPanel extends JPanel {

	private final TargetTracker tracker = new TargetTracker();

	private final JCheckBox enabled = new JCheckBox("Track a target");
	private final JTextField names = new JTextField("Tree");
	private final JCheckBox wantLocs = new JCheckBox("Objects", true);
	private final JCheckBox wantNpcs = new JCheckBox("NPCs", true);
	private final JCheckBox wantPlayers = new JCheckBox("Players", false);
	private final JCheckBox onScreen = new JCheckBox("Must be on screen", true);
	private final JCheckBox hud = new JCheckBox("Show debug overlay in game", true);
	private final JSlider distance = new JSlider(1, 25, 15);
	private final JLabel distanceLabel = new JLabel();
	private final JLabel state = new JLabel();
	private final JLabel detail = new JLabel();

	public TargetPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.state.setFont(this.state.getFont().deriveFont(Font.BOLD, 14.0F));
		this.detail.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.detail.setVerticalAlignment(JLabel.TOP);

		JPanel var1 = new JPanel();
		var1.setLayout(new BoxLayout(var1, BoxLayout.Y_AXIS));
		var1.add(this.enabled);
		var1.add(new JLabel("<html>Names, comma separated<br><i>blank matches anything</i></html>"));
		var1.add(this.names);

		JPanel var2 = new JPanel(new GridLayout(1, 3));
		var2.add(this.wantLocs);
		var2.add(this.wantNpcs);
		var2.add(this.wantPlayers);
		var1.add(var2);
		var1.add(this.onScreen);
		var1.add(this.hud);
		var1.add(this.distanceLabel);
		var1.add(this.distance);
		var1.add(this.state);

		this.add(var1, BorderLayout.NORTH);
		this.add(new JScrollPane(this.detail), BorderLayout.CENTER);

		ActionListener var3 = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TargetPanel.this.apply();
			}
		};
		this.enabled.addActionListener(var3);
		this.wantLocs.addActionListener(var3);
		this.wantNpcs.addActionListener(var3);
		this.wantPlayers.addActionListener(var3);
		this.onScreen.addActionListener(var3);
		this.hud.addActionListener(var3);
		this.names.addActionListener(var3);
		this.distance.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				TargetPanel.this.apply();
			}
		});

		GameEventBus.addListener(this.tracker);
		this.apply();

		Timer var4 = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TargetPanel.this.refresh();
			}
		});
		var4.start();
	}

	/** Push the controls into the criteria. Changing them restarts the search. */
	private void apply() {
		TargetCriteria var1 = this.tracker.getCriteria();
		var1.names = NameFilter.parse(this.names.getText());
		var1.maxDistance = this.distance.getValue();
		var1.requireOnScreen = this.onScreen.isSelected();
		int var2 = 0;
		if (this.wantLocs.isSelected()) {
			var2 |= TargetCriteria.KIND_LOC;
		}
		if (this.wantNpcs.isSelected()) {
			var2 |= TargetCriteria.KIND_NPC;
		}
		if (this.wantPlayers.isSelected()) {
			var2 |= TargetCriteria.KIND_PLAYER;
		}
		var1.kinds = var2;
		this.distanceLabel.setText("Within " + var1.maxDistance + " tiles");
		TargetHud.setEnabled(this.hud.isSelected() && this.enabled.isSelected());
		this.tracker.setEnabled(this.enabled.isSelected());
		this.tracker.reset();
	}

	private void refresh() {
		int var1 = Selection.getState();
		this.state.setText(Selection.getStateName() + "  (" + Selection.getStateMillis() / 1000 + "s)");
		this.state.setForeground(var1 == Selection.STATE_FOUND ? new Color(0x0A7D2C)
			: (var1 == Selection.STATE_LOST ? new Color(0xB00020) : Color.DARK_GRAY));

		EntityInfo var2 = Selection.getInfo();
		if (var1 != Selection.STATE_FOUND || var2 == null) {
			String var3 = Selection.getNote();
			this.detail.setText("<html><i>" + (var3.length() == 0 ? "&nbsp;" : escape(var3))
				+ "</i></html>");
			return;
		}

		StringBuilder var4 = new StringBuilder("<html>");
		var4.append("<b>").append(escape(var2.name)).append("</b><br>");
		var4.append("&nbsp;").append(var2.getKindName());
		if (var2.id >= 0) {
			var4.append("  id ").append(var2.id);
		}
		var4.append("<br>");
		if (var2.definition != null) {
			var4.append("&nbsp;").append(escape(var2.definition)).append("<br>");
		}
		var4.append("<br><b>Identity</b><br>");
		bwana.inspect.EntityHandle var5 = Selection.getHandle();
		if (var5 != null) {
			var4.append("&nbsp;").append(escape(var5.describe())).append("<br>");
			var4.append("&nbsp;keyed on ").append(escape(var5.describeRule())).append("<br>");
		}
		if (var2.getSlot() >= 0) {
			var4.append("&nbsp;client slot ").append(var2.getSlot()).append("<br>");
		}
		var4.append("&nbsp;re-resolved ").append(Selection.getResolveCount())
			.append("x since lock<br>");
		// The number that proves the point: every slot change here is a case that
		// would have dropped the target under the old index-as-identity rule.
		int var6 = Selection.getSlotChangeCount();
		if (var6 > 0) {
			var4.append("&nbsp;<b>survived ").append(var6)
				.append(" slot change").append(var6 == 1 ? "" : "s").append("</b><br>");
		}
		if (Selection.getReacquireCount() > 0) {
			var4.append("&nbsp;<b>reacquired ").append(Selection.getReacquireCount())
				.append("x after loss</b><br>");
		}
		var4.append("<br><b>Position</b><br>");
		var4.append("&nbsp;world ").append(var2.worldX).append(", ").append(var2.worldY)
			.append("  plane ").append(var2.plane).append("<br>");
		var4.append("&nbsp;tile ").append(var2.tileX).append(", ").append(var2.tileZ).append("<br>");
		var4.append("<br><b>Screen</b><br>");
		var4.append("&nbsp;viewport ").append(var2.screenX).append(", ").append(var2.screenY)
			.append("<br>");
		var4.append("&nbsp;box ").append(var2.boxWidth).append('x').append(var2.boxHeight)
			.append(" at ").append(var2.boxX).append(", ").append(var2.boxY).append("<br>");
		if (var2.animationId >= 0 || var2.interaction != null) {
			var4.append("<br><b>State</b><br>");
			if (var2.animationId >= 0) {
				var4.append("&nbsp;anim ").append(var2.animationId).append("<br>");
			}
			if (var2.interaction != null) {
				var4.append("&nbsp;interacting with ").append(escape(var2.interaction)).append("<br>");
			}
		}
		var4.append("</html>");
		this.detail.setText(var4.toString());
	}

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
