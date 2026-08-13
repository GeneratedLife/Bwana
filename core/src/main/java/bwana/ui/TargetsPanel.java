package bwana.ui;

import bwana.vision.TargetReport;
import bwana.vision.TargetWatcher;
import bwana.vision.Vision;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Continuous readout of which saved targets are on screen and where.
 * <p>
 * Separate from the Vision tab on purpose: that one is for defining targets, this
 * one is for using them. Once a target is tuned you want a stable answer to "is it
 * visible and where", not a wall of tuning controls.
 */
public final class TargetsPanel extends JPanel {

	private final JLabel body = new JLabel();
	private final TargetWatcher watcher = Vision.getInstance().getTargetWatcher();

	public TargetsPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.body.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.body.setVerticalAlignment(JLabel.TOP);

		final JCheckBox var1 = new JCheckBox("Log appearances to targets.log", this.watcher.isLogging());
		var1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TargetsPanel.this.watcher.setLogging(var1.isSelected());
			}
		});

		this.add(var1, BorderLayout.NORTH);
		this.add(new JScrollPane(this.body), BorderLayout.CENTER);

		// the watcher fires on the vision worker thread; poll from the EDT instead
		// so this panel never touches Swing from the wrong thread
		Timer var2 = new Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TargetsPanel.this.refresh();
			}
		});
		var2.start();

		this.refresh();
	}

	private void refresh() {
		if (!Vision.getInstance().isEnabled()) {
			this.body.setText("<html><i>Vision is off.<br>Enable it on the Vision tab.</i></html>");
			return;
		}
		List<TargetReport> var1 = this.watcher.getReports();
		if (var1.isEmpty()) {
			this.body.setText("<html><i>No enabled targets.</i></html>");
			return;
		}
		StringBuilder var2 = new StringBuilder("<html>");
		for (int var3 = 0; var3 < var1.size(); var3++) {
			TargetReport var4 = var1.get(var3);
			if (var4.visible && var4.largest != null) {
				var2.append("<b>").append(escape(var4.name)).append("</b>")
					.append(" &mdash; <font color='#0A7D2C'>visible</font><br>")
					.append("&nbsp;&nbsp;").append(var4.regionCount).append(" region(s)<br>")
					.append("&nbsp;&nbsp;largest ").append(var4.largest.width).append('x')
					.append(var4.largest.height).append(" area ").append(var4.largest.area).append("<br>")
					.append("&nbsp;&nbsp;screen <b>").append(var4.getScreenX()).append(", ")
					.append(var4.getScreenY()).append("</b><br>")
					.append("&nbsp;&nbsp;viewport ").append(var4.largest.x).append(", ")
					.append(var4.largest.y).append("<br><br>");
			} else {
				var2.append("<b>").append(escape(var4.name)).append("</b>")
					.append(" &mdash; <font color='#999999'>not visible</font><br><br>");
			}
		}
		var2.append("</html>");
		this.body.setText(var2.toString());
	}

	private static String escape(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
