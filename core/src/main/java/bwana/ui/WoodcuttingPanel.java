package bwana.ui;

import bwana.script.ScriptState;
import bwana.script.WoodcuttingScript;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

/**
 * Drives the Woodcutting script and shows what it is thinking.
 * <p>
 * The log is the point of this panel. A task script that only reports its current
 * state tells you nothing about the run that just went wrong, and the transitions
 * are where the interesting decisions are.
 */
public final class WoodcuttingPanel extends JPanel {

	private final WoodcuttingScript script = new WoodcuttingScript();

	private final JTextField tree = new JTextField("Tree");
	private final JTextField action = new JTextField("Chop");
	private final JSpinner goal = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
	private final JSpinner range = new JSpinner(new SpinnerNumberModel(15, 1, 25, 1));
	private final JButton run = new JButton("Start");
	private final JLabel state = new JLabel();
	private final JLabel progress = new JLabel();
	private final JTextArea logView = new JTextArea();

	private int lastLogSize = -1;

	public WoodcuttingPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.state.setFont(this.state.getFont().deriveFont(Font.BOLD, 14.0F));
		this.progress.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.logView.setFont(new Font("Monospaced", Font.PLAIN, 10));
		this.logView.setEditable(false);
		this.logView.setLineWrap(true);

		JPanel var1 = new JPanel();
		var1.setLayout(new BoxLayout(var1, BoxLayout.Y_AXIS));
		var1.add(labelled("Object name contains", this.tree));
		var1.add(labelled("Action name contains", this.action));

		JPanel var2 = new JPanel(new GridLayout(1, 2, 6, 0));
		var2.add(labelled("Stop after", this.goal));
		var2.add(labelled("Range (tiles)", this.range));
		var1.add(var2);
		var1.add(this.run);
		var1.add(this.state);
		var1.add(this.progress);
		// The targeting layer publishes one selection globally, so two things driving
		// it at once would fight. Worth saying out loud rather than debugging twice.
		JLabel var3 = new JLabel("<html><i>Turn off &quot;Track a target&quot; on the "
			+ "Target tab first &mdash; both drive the same selection.</i></html>");
		var3.setForeground(Color.GRAY);
		var1.add(var3);

		this.add(var1, BorderLayout.NORTH);
		this.add(new JScrollPane(this.logView), BorderLayout.CENTER);

		this.run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				WoodcuttingPanel.this.toggle();
			}
		});

		Timer var4 = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				WoodcuttingPanel.this.refresh();
			}
		});
		var4.start();
		this.refresh();
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

	private void toggle() {
		if (this.script.isRunning()) {
			this.script.stop();
			return;
		}
		this.script.setTreeName(this.tree.getText());
		this.script.setActionName(this.action.getText());
		this.script.setGoal(((Integer) this.goal.getValue()).intValue());
		this.script.setSearchRange(((Integer) this.range.getValue()).intValue());
		this.script.start();
	}

	private void refresh() {
		boolean var1 = this.script.isRunning();
		this.run.setText(var1 ? "Stop" : "Start");
		this.tree.setEnabled(!var1);
		this.action.setEnabled(!var1);
		this.goal.setEnabled(!var1);
		this.range.setEnabled(!var1);

		int var2 = this.script.getState();
		this.state.setText(this.script.getStateName());
		this.state.setForeground(var2 == ScriptState.DONE ? new Color(0x0A7D2C)
			: (var2 == ScriptState.IDLE ? Color.DARK_GRAY : new Color(0xB06A00)));
		this.progress.setText("<html>" + this.script.getChops() + " / " + this.script.getGoal()
			+ " chops<br><i>" + escape(this.script.getNote()) + "</i></html>");

		// Only rebuild when there is something new; this runs four times a second.
		int var3 = this.script.getLog().size();
		if (var3 != this.lastLogSize) {
			this.lastLogSize = var3;
			String[] var4 = this.script.getLog().snapshot();
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
