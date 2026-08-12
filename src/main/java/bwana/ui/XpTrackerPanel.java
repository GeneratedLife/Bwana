package bwana.ui;

import bwana.Skill;
import bwana.XpSnapshot;
import bwana.XpTracker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * The XP tracker's UI. Lives entirely on the event dispatch thread.
 * <p>
 * It receives immutable snapshots from {@link XpTracker} — which builds them on
 * the game thread — and never reads game state itself.
 */
public final class XpTrackerPanel extends JPanel implements XpTracker.Listener {

	private static final String[] COLUMNS = new String[] { "Skill", "Gained", "XP/hr", "To level" };

	private final Model model = new Model();
	private final JLabel summary = new JLabel(" ");
	private final XpTracker tracker;

	public XpTrackerPanel() {
		super(new BorderLayout(0, 6));
		this.tracker = new XpTracker(this);
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.summary.setFont(this.summary.getFont().deriveFont(Font.BOLD, 12.0F));
		this.add(this.summary, BorderLayout.NORTH);

		JTable var1 = new JTable(this.model);
		var1.setRowHeight(19);
		var1.getTableHeader().setReorderingAllowed(false);
		var1.setFont(var1.getFont().deriveFont(11.0F));
		var1.getTableHeader().setFont(var1.getFont().deriveFont(Font.BOLD, 11.0F));

		DefaultTableCellRenderer var2 = new DefaultTableCellRenderer();
		var2.setHorizontalAlignment(SwingConstants.RIGHT);
		for (int var3 = 1; var3 < COLUMNS.length; var3++) {
			var1.getColumnModel().getColumn(var3).setCellRenderer(var2);
		}
		// "Woodcutting" is the longest name that matters; 78px clipped it
		var1.getColumnModel().getColumn(0).setPreferredWidth(88);
		var1.getColumnModel().getColumn(1).setPreferredWidth(48);
		var1.getColumnModel().getColumn(2).setPreferredWidth(52);
		var1.getColumnModel().getColumn(3).setPreferredWidth(46);

		this.add(new JScrollPane(var1), BorderLayout.CENTER);

		JButton var4 = new JButton("Reset");
		var4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// hands a flag to the game thread; it resets on its next tick
				XpTrackerPanel.this.tracker.requestReset();
			}
		});
		JPanel var5 = new JPanel(new BorderLayout());
		var5.add(var4, BorderLayout.EAST);
		this.add(var5, BorderLayout.SOUTH);

		this.showEmpty();
	}

	/** Register this with the event bus to start receiving events. */
	public XpTracker getTracker() {
		return this.tracker;
	}

	/** Called on the game thread. Hop to the EDT before touching anything. */
	public void onSnapshot(final XpSnapshot snapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				XpTrackerPanel.this.apply(snapshot);
			}
		});
	}

	private void apply(XpSnapshot snapshot) {
		if (!snapshot.tracking || snapshot.rows.length == 0) {
			this.showEmpty();
			this.model.setRows(snapshot.rows);
			return;
		}
		this.summary.setText(format(snapshot.totalGained) + " xp in " + duration(snapshot.elapsedMillis));
		this.model.setRows(snapshot.rows);
	}

	private void showEmpty() {
		this.summary.setText("No experience yet this session");
	}

	// ---- formatting ----

	static String format(long value) {
		StringBuilder var2 = new StringBuilder(Long.toString(value));
		for (int var3 = var2.length() - 3; var3 > 0; var3 -= 3) {
			var2.insert(var3, ',');
		}
		return var2.toString();
	}

	static String duration(long millis) {
		if (millis < 0L) {
			return "-";
		}
		long var2 = millis / 1000L;
		long var4 = var2 / 3600L;
		long var6 = var2 % 3600L / 60L;
		long var8 = var2 % 60L;
		if (var4 > 0L) {
			return var4 + "h " + var6 + "m";
		}
		if (var6 > 0L) {
			return var6 + "m " + var8 + "s";
		}
		return var8 + "s";
	}

	private final class Model extends AbstractTableModel {

		private XpSnapshot.Row[] rows = new XpSnapshot.Row[0];

		void setRows(XpSnapshot.Row[] rows) {
			this.rows = rows;
			this.fireTableDataChanged();
		}

		public int getRowCount() {
			return this.rows.length;
		}

		public int getColumnCount() {
			return COLUMNS.length;
		}

		public String getColumnName(int column) {
			return COLUMNS[column];
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public Object getValueAt(int row, int column) {
			XpSnapshot.Row var3 = this.rows[row];
			if (column == 0) {
				return Skill.name(var3.skill);
			}
			if (column == 1) {
				return format(var3.gained);
			}
			if (column == 2) {
				return var3.xpPerHour > 0L ? format(var3.xpPerHour) : "-";
			}
			if (var3.xpToNextLevel == 0) {
				return "max";
			}
			return var3.millisToNextLevel >= 0L ? duration(var3.millisToNextLevel) : "-";
		}
	}

	/** Convenience for the sidebar. */
	public Component asComponent() {
		return this;
	}
}
