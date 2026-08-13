package bwana.ui;

import bwana.GameEventBus;
import bwana.action.ActionRunner;
import bwana.action.ActionState;
import bwana.action.EntityAction;
import bwana.inspect.EntityInfo;
import bwana.target.Selection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * TARGET, ACTION, RESULT — the three questions, kept visibly apart.
 * <p>
 * The target half of this panel is read-only. Selection is owned by the Target tab
 * and nothing here can change it, which is what makes the two verifiable
 * independently: if the wrong thing gets chopped, the fault is in one tab or the
 * other and never in the seam between them.
 * <p>
 * The result half deliberately reports its evidence rather than a verdict alone.
 * "SUCCESS" on its own is a claim; "SUCCESS — Woodcutting +25 xp" is a claim you
 * can check.
 */
public final class ActionPanel extends JPanel {

	private final ActionRunner runner = new ActionRunner();

	private final JLabel target = new JLabel();
	private final DefaultListModel actionModel = new DefaultListModel();
	private final JList actions = new JList(this.actionModel);
	private final JButton execute = new JButton("Execute Action");
	private final JLabel state = new JLabel();
	private final JLabel detail = new JLabel();

	/** The array the list was last built from, so it is rebuilt only when it changes. */
	private EntityAction[] shown = new EntityAction[0];

	/** Guards the listener while the list is being resynced from the runner. */
	private boolean syncing;

	public ActionPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.target.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.state.setFont(this.state.getFont().deriveFont(Font.BOLD, 14.0F));
		this.detail.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.detail.setVerticalAlignment(JLabel.TOP);
		this.actions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.actions.setVisibleRowCount(6);

		JPanel var1 = new JPanel();
		var1.setLayout(new BoxLayout(var1, BoxLayout.Y_AXIS));
		var1.add(section("Current target"));
		var1.add(this.target);
		var1.add(section("Available actions"));

		JPanel var2 = new JPanel(new BorderLayout(0, 6));
		var2.add(new JScrollPane(this.actions), BorderLayout.CENTER);

		JPanel var3 = new JPanel();
		var3.setLayout(new BoxLayout(var3, BoxLayout.Y_AXIS));
		var3.add(this.execute);
		var3.add(section("Action state"));
		var3.add(this.state);

		this.add(var1, BorderLayout.NORTH);
		this.add(var2, BorderLayout.CENTER);

		JPanel var4 = new JPanel(new BorderLayout(0, 4));
		var4.add(var3, BorderLayout.NORTH);
		var4.add(this.detail, BorderLayout.CENTER);
		this.add(var4, BorderLayout.SOUTH);

		this.actions.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting() && !ActionPanel.this.syncing) {
					ActionPanel.this.runner.select(ActionPanel.this.actions.getSelectedIndex());
				}
			}
		});
		this.execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ActionPanel.this.runner.requestExecute();
			}
		});

		GameEventBus.addListener(this.runner);

		Timer var5 = new Timer(150, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ActionPanel.this.refresh();
			}
		});
		var5.start();
		this.refresh();
	}

	private static JLabel section(String text) {
		JLabel var1 = new JLabel(text);
		var1.setFont(var1.getFont().deriveFont(Font.BOLD));
		var1.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));
		return var1;
	}

	private void refresh() {
		EntityInfo var1 = Selection.getState() == Selection.STATE_FOUND ? Selection.getInfo() : null;
		if (var1 == null) {
			this.target.setText("<html><i>none &mdash; select one on the Target tab</i></html>");
		} else {
			this.target.setText("<html>" + escape(var1.name)
				+ (var1.id >= 0 ? " #" + var1.id : "")
				+ "<br>" + var1.getKindName()
				+ " at " + var1.worldX + ", " + var1.worldY + "</html>");
		}

		EntityAction[] var2 = this.runner.getAvailable();
		if (var2 != this.shown) {
			this.shown = var2;
			this.syncing = true;
			this.actionModel.clear();
			for (int var3 = 0; var3 < var2.length; var3++) {
				this.actionModel.addElement(var2[var3].name
					+ (var2[var3].local ? "  (client-side)" : ""));
			}
			this.syncing = false;
		}
		int var4 = this.runner.getSelectedIndex();
		if (var4 != this.actions.getSelectedIndex() && var4 < this.actionModel.getSize()) {
			this.syncing = true;
			this.actions.setSelectedIndex(var4);
			this.syncing = false;
		}

		int var5 = this.runner.getState();
		EntityAction var6 = this.runner.getState() == ActionState.READY
			|| var5 == ActionState.NONE ? this.runner.getSelected() : this.runner.getRunning();
		String var7 = ActionState.name(var5);
		if (var5 == ActionState.WAITING) {
			var7 = var7 + "  (" + this.runner.getSecondsLeft() + "s)";
		}
		this.state.setText("<html>" + var7
			+ (var6 == null ? "" : "<br><span style='font-weight:normal'>"
				+ escape(var6.describe()) + "</span>") + "</html>");
		this.state.setForeground(colourFor(var5));

		this.execute.setEnabled(this.runner.getSelected() != null
			&& var5 != ActionState.EXECUTING && var5 != ActionState.WAITING);
		this.execute.setText(ActionState.isTerminal(var5) ? "Execute Again" : "Execute Action");

		StringBuilder var8 = new StringBuilder("<html>");
		String var9 = this.runner.getNote();
		if (var9 != null && var9.length() > 0) {
			var8.append("<i>").append(escape(var9)).append("</i><br>");
		}
		String[] var10 = this.runner.getEvidence();
		for (int var11 = 0; var11 < var10.length; var11++) {
			var8.append("&bull; ").append(escape(var10[var11])).append("<br>");
		}
		var8.append("</html>");
		this.detail.setText(var8.toString());
	}

	private static Color colourFor(int state) {
		if (state == ActionState.SUCCESS) {
			return new Color(0x0A7D2C);
		}
		if (state == ActionState.FAILED) {
			return new Color(0xB00020);
		}
		if (state == ActionState.WAITING || state == ActionState.EXECUTING) {
			return new Color(0xB06A00);
		}
		return Color.DARK_GRAY;
	}

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
