package bwana.ui;

import bwana.plan.Plan;
import bwana.plan.PlanRunner;
import bwana.plan.PlanState;
import bwana.script.Landmark;
import bwana.target.TargetCriteria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

/**
 * Describes what should happen. Nothing here decides how.
 * <p>
 * Every control writes one field of a {@link Plan} and that is the entire extent of
 * this class's involvement — no target searching, no pathing, no interaction, no
 * inventory logic. Those live in the systems that already do them, and the plan is
 * simply handed over. If a new activity ever needs a new panel, something has gone
 * wrong with the abstraction rather than with the panel.
 */
public final class PlanPanel extends JPanel {

	private static final String[] FULL_CHOICES = new String[] {
		"Keep going", "Bank it", "Drop everything", "Stop"
	};

	private static final String[] STOP_CHOICES = new String[] {
		"Manually", "After N actions", "After N minutes", "After N bank trips"
	};

	private final PlanRunner runner = new PlanRunner();

	private final JTextField target = new JTextField("Tree");
	private final JCheckBox wantLocs = new JCheckBox("Objects", true);
	private final JCheckBox wantNpcs = new JCheckBox("NPCs", false);
	private final JTextField action = new JTextField("Chop");
	private final JSpinner range = new JSpinner(new SpinnerNumberModel(15, 1, 25, 1));
	private final JSpinner hpThreshold = new JSpinner(new SpinnerNumberModel(0, 0, 99, 5));
	private final JTextField foodName = new JTextField("Lobster");
	private final JSpinner foodWithdraw = new JSpinner(new SpinnerNumberModel(10, 1, 28, 1));
	private final JLabel health = new JLabel();
	private final JTextField lootNames = new JTextField("");
	private final JSpinner lootRange = new JSpinner(new SpinnerNumberModel(6, 1, 20, 1));
	private final JTextField itemName = new JTextField("");
	private final JTextField itemAction = new JTextField("Bury");
	private final JComboBox whenFull = new JComboBox(FULL_CHOICES);
	private final JTextField bank = new JTextField("Bank");
	private final JTextField bankAction = new JTextField("Use-quickly");
	private final JCheckBox returnAfter = new JCheckBox("Return and resume afterwards", true);
	private final JComboBox stopType = new JComboBox(STOP_CHOICES);
	private final JSpinner stopValue = new JSpinner(new SpinnerNumberModel(100, 1, 100000, 1));
	private final JButton run = new JButton("Start plan");
	private final JLabel state = new JLabel();
	private final JLabel summary = new JLabel();
	private final JTextArea logView = new JTextArea();

	private int lastLogSize = -1;

	public PlanPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.state.setFont(this.state.getFont().deriveFont(Font.BOLD, 13.0F));
		this.summary.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.health.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.health.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		this.logView.setFont(new Font("Monospaced", Font.PLAIN, 10));
		this.logView.setEditable(false);
		this.logView.setLineWrap(true);

		JPanel var1 = new JPanel();
		var1.setLayout(new BoxLayout(var1, BoxLayout.Y_AXIS));
		var1.add(section("Goal"));
		var1.add(labelled("Target name contains", this.target));
		JPanel var2 = new JPanel(new GridLayout(1, 2));
		var2.add(this.wantLocs);
		var2.add(this.wantNpcs);
		var1.add(var2);
		var1.add(labelled("Action name contains", this.action));
		var1.add(labelled("Search range (tiles)", this.range));

		var1.add(section("Health"));
		var1.add(labelled("Eat at or below HP % (0 = never)", this.hpThreshold));
		var1.add(labelled("Food name contains", this.foodName));
		var1.add(labelled("Withdraw this many when restocking", this.foodWithdraw));
		var1.add(this.health);

		var1.add(section("Loot"));
		var1.add(labelled("Pick up (comma separated, blank = none)", this.lootNames));
		var1.add(labelled("Loot range (tiles)", this.lootRange));

		var1.add(section("Carried items"));
		var1.add(labelled("Item name contains (blank = ignore)", this.itemName));
		var1.add(labelled("Do this to it", this.itemAction));

		var1.add(section("When the inventory is full"));
		var1.add(this.whenFull);
		var1.add(labelled("Bank location (name or x,y)", this.bank));
		var1.add(labelled("Bank action", this.bankAction));
		var1.add(this.returnAfter);

		var1.add(section("Stop condition"));
		var1.add(this.stopType);
		var1.add(labelled("Value", this.stopValue));
		var1.add(this.run);
		var1.add(this.state);
		var1.add(this.summary);

		// The controls outgrew the panel once health, loot and carried items were
		// added, so they scroll. Wrapped in a BorderLayout first: a BoxLayout column
		// dropped straight into a scroll pane stretches its children to fill the
		// viewport instead of keeping their preferred heights.
		JPanel var9 = new JPanel(new BorderLayout());
		var9.add(var1, BorderLayout.NORTH);
		JScrollPane var10 = new JScrollPane(var9,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		var10.setBorder(null);
		// The default of one pixel per click makes a form this tall unusable.
		var10.getVerticalScrollBar().setUnitIncrement(16);

		// A split rather than a fixed share, so the log can be opened up when
		// something goes wrong and collapsed when it does not.
		javax.swing.JSplitPane var11 = new javax.swing.JSplitPane(
			javax.swing.JSplitPane.VERTICAL_SPLIT, var10,
			new JScrollPane(this.logView));
		var11.setResizeWeight(0.65D);
		var11.setBorder(null);
		var11.setDividerSize(6);
		this.add(var11, BorderLayout.CENTER);

		this.run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PlanPanel.this.toggle();
			}
		});

		Timer var3 = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PlanPanel.this.refresh();
			}
		});
		var3.start();
		this.refresh();
	}

	private static JLabel section(String text) {
		JLabel var1 = new JLabel(text);
		var1.setFont(var1.getFont().deriveFont(Font.BOLD));
		var1.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));
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

	/** Read the controls into a plan. The whole of this panel's responsibility. */
	private Plan buildPlan() {
		Plan var1 = new Plan();
		var1.name = this.action.getText().trim() + " " + this.target.getText().trim();
		var1.targetName = this.target.getText().trim();
		var1.actionName = this.action.getText().trim();
		var1.searchRange = ((Integer) this.range.getValue()).intValue();
		int var2 = 0;
		if (this.wantLocs.isSelected()) {
			var2 |= TargetCriteria.KIND_LOC;
		}
		if (this.wantNpcs.isSelected()) {
			var2 |= TargetCriteria.KIND_NPC;
		}
		var1.targetKinds = var2 == 0 ? TargetCriteria.KIND_LOC : var2;
		var1.hpThreshold = ((Integer) this.hpThreshold.getValue()).intValue();
		var1.foodName = this.foodName.getText().trim();
		var1.foodWithdraw = ((Integer) this.foodWithdraw.getValue()).intValue();
		var1.lootNames = this.lootNames.getText().trim();
		var1.lootRange = ((Integer) this.lootRange.getValue()).intValue();
		var1.itemName = this.itemName.getText().trim();
		var1.itemAction = this.itemAction.getText().trim();
		var1.whenFull = this.whenFull.getSelectedIndex();
		Landmark var3 = Landmark.parse(this.bank.getText());
		if (var3 != null) {
			var1.bank = var3;
		}
		var1.bankAction = this.bankAction.getText().trim();
		var1.returnAfter = this.returnAfter.isSelected();
		var1.stopType = this.stopType.getSelectedIndex();
		var1.stopValue = ((Integer) this.stopValue.getValue()).intValue();
		return var1;
	}

	private void toggle() {
		if (this.runner.isRunning()) {
			this.runner.stop();
			return;
		}
		Plan var1 = this.buildPlan();
		if (var1.targetName.length() == 0 || var1.actionName.length() == 0) {
			this.state.setText("a target and an action are both required");
			this.state.setForeground(new Color(0xB00020));
			return;
		}
		this.runner.start(var1);
	}

	private void refresh() {
		boolean var1 = this.runner.isRunning();
		this.run.setText(var1 ? "Stop plan" : "Start plan");
		this.target.setEnabled(!var1);
		this.action.setEnabled(!var1);
		this.range.setEnabled(!var1);
		this.hpThreshold.setEnabled(!var1);
		this.foodName.setEnabled(!var1);
		this.foodWithdraw.setEnabled(!var1);
		this.lootNames.setEnabled(!var1);
		this.lootRange.setEnabled(!var1);
		this.itemName.setEnabled(!var1);
		this.itemAction.setEnabled(!var1);
		this.whenFull.setEnabled(!var1);
		this.bank.setEnabled(!var1);
		this.bankAction.setEnabled(!var1);
		this.returnAfter.setEnabled(!var1);
		this.stopType.setEnabled(!var1);
		this.stopValue.setEnabled(!var1);
		this.wantLocs.setEnabled(!var1);
		this.wantNpcs.setEnabled(!var1);

		String[] var10 = this.runner.getHealthLines();
		StringBuilder var11 = new StringBuilder("<html>");
		for (int var12 = 0; var12 < var10.length; var12++) {
			var11.append(escape(var10[var12])).append("<br>");
		}
		this.health.setText(var11.append("</html>").toString());

		int var2 = this.runner.getState();
		this.state.setText("<html>" + this.runner.getStateName()
			+ "<br><span style='font-weight:normal'><i>"
			+ escape(this.runner.getNote()) + "</i></span></html>");
		this.state.setForeground(var2 == PlanState.DONE ? new Color(0x0A7D2C)
			: (var2 == PlanState.FAILED ? new Color(0xB00020)
				: (var2 == PlanState.IDLE ? Color.DARK_GRAY : new Color(0xB06A00))));
		this.summary.setText(this.runner.getActionsDone() + " actions, "
			+ this.runner.getLootTaken() + " looted, "
			+ this.runner.getItemsHandled() + " items, "
			+ this.runner.getTrips() + " bank trips");

		int var3 = this.runner.getLog().size();
		if (var3 != this.lastLogSize) {
			this.lastLogSize = var3;
			String[] var4 = this.runner.getLog().snapshot();
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
