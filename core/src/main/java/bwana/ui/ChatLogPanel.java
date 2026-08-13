package bwana.ui;

import bwana.ChatLog;
import bwana.ChatMessage;
import bwana.ChatType;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Live chat view with a category filter.
 * <p>
 * Keeps the last {@link #MAX_MESSAGES} lines in memory so the filter can be
 * re-applied without re-reading the log file. The file on disk is the permanent
 * record; this is just the window onto it.
 */
public final class ChatLogPanel extends JPanel implements ChatLog.Listener {

	private static final int MAX_MESSAGES = 1000;

	private final ChatLog log = new ChatLog(this);
	private final List<ChatMessage> messages = new ArrayList<ChatMessage>();
	private final JTextArea area = new JTextArea();
	private final JComboBox filter = new JComboBox(ChatType.CATEGORIES);
	private final SimpleDateFormat clock = new SimpleDateFormat("HH:mm:ss");

	public ChatLogPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.area.setEditable(false);
		this.area.setLineWrap(true);
		this.area.setWrapStyleWord(true);
		this.area.setFont(new Font("Monospaced", Font.PLAIN, 11));

		this.filter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ChatLogPanel.this.rebuild();
			}
		});

		JPanel var1 = new JPanel(new BorderLayout(6, 0));
		var1.add(this.filter, BorderLayout.CENTER);
		this.add(var1, BorderLayout.NORTH);
		this.add(new JScrollPane(this.area), BorderLayout.CENTER);

		JButton var2 = new JButton("Open folder");
		var2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ChatLogPanel.this.openFolder();
			}
		});
		JPanel var3 = new JPanel(new BorderLayout());
		var3.add(var2, BorderLayout.EAST);
		this.add(var3, BorderLayout.SOUTH);

		this.log.start();
	}

	/** Register this with the event bus to start receiving chat. */
	public ChatLog getLog() {
		return this.log;
	}

	/** Called on the game thread. Hop to the EDT before touching Swing. */
	public void onChatLogged(final ChatMessage message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ChatLogPanel.this.add(message);
			}
		});
	}

	private void add(ChatMessage message) {
		this.messages.add(message);
		while (this.messages.size() > MAX_MESSAGES) {
			this.messages.remove(0);
		}
		if (this.matches(message)) {
			this.append(message);
		}
	}

	private boolean matches(ChatMessage message) {
		Object var2 = this.filter.getSelectedItem();
		if (var2 == null || ChatType.CATEGORY_ALL.equals(var2)) {
			return true;
		}
		return var2.equals(ChatType.category(message.type));
	}

	private void append(ChatMessage message) {
		this.area.append("[" + this.clock.format(new Date(message.time)) + "] " + message.display() + "\n");
		// pin to the bottom so the newest line is always visible
		this.area.setCaretPosition(this.area.getDocument().getLength());
	}

	private void rebuild() {
		this.area.setText("");
		for (int var1 = 0; var1 < this.messages.size(); var1++) {
			ChatMessage var2 = this.messages.get(var1);
			if (this.matches(var2)) {
				this.append(var2);
			}
		}
	}

	private void openFolder() {
		try {
			File var1 = ChatLog.directory();
			if (!var1.exists() && !var1.mkdirs()) {
				return;
			}
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(var1);
			}
		} catch (Exception var2) {
			System.err.println("bwana: could not open chat folder: " + var2.getMessage());
		}
	}
}
