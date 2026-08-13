package bwana.ui;

import bwana.Notepad;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A scratch pad that survives restarts.
 * <p>
 * Saves itself {@value #IDLE_SAVE_MS}ms after you stop typing rather than on
 * every keystroke, and again on shutdown. Writing per keystroke would put disk
 * I/O on the event dispatch thread several times a second.
 */
public final class NotesPanel extends JPanel {

	/** Quiet period after the last edit before an autosave fires. */
	private static final int IDLE_SAVE_MS = 1500;

	private final JTextArea area = new JTextArea();
	private final JLabel status = new JLabel(" ");
	private final Timer idleTimer;

	/**
	 * Latest text, kept outside Swing so the shutdown hook can read it.
	 * <p>
	 * A shutdown hook cannot safely ask the EDT for anything — by then the EDT may
	 * already be gone — so the text is mirrored here on every edit.
	 */
	private volatile String pending = "";

	private volatile boolean dirty;

	public NotesPanel() {
		super(new BorderLayout(0, 6));
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.area.setLineWrap(true);
		this.area.setWrapStyleWord(true);
		this.area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.area.setText(Notepad.load());
		this.area.setCaretPosition(0);
		this.pending = this.area.getText();

		this.status.setFont(this.status.getFont().deriveFont(Font.PLAIN, 11.0F));
		this.status.setText(this.pending.length() == 0 ? "Empty" : "Loaded");

		this.idleTimer = new Timer(IDLE_SAVE_MS, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				NotesPanel.this.saveNow();
			}
		});
		this.idleTimer.setRepeats(false);

		this.area.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent event) {
				NotesPanel.this.touched();
			}

			public void removeUpdate(DocumentEvent event) {
				NotesPanel.this.touched();
			}

			public void changedUpdate(DocumentEvent event) {
				NotesPanel.this.touched();
			}
		});

		this.add(new JScrollPane(this.area), BorderLayout.CENTER);
		this.add(this.status, BorderLayout.SOUTH);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				NotesPanel.this.flush();
			}
		}, "bwana-notes-flush"));
	}

	/** EDT: an edit happened. Mirror the text and restart the idle countdown. */
	private void touched() {
		this.pending = this.area.getText();
		this.dirty = true;
		this.status.setText("Unsaved changes");
		this.idleTimer.restart();
	}

	/** EDT: hand the current text to a worker thread to write. */
	private void saveNow() {
		if (!this.dirty) {
			return;
		}
		final String var1 = this.pending;
		Thread var2 = new Thread(new Runnable() {
			public void run() {
				String var1x;
				try {
					Notepad.save(var1);
					NotesPanel.this.dirty = false;
					var1x = "Saved " + new SimpleDateFormat("HH:mm:ss").format(new Date());
				} catch (Throwable var3x) {
					var1x = "Save failed: " + var3x.getMessage();
				}
				final String var2x = var1x;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						NotesPanel.this.status.setText(var2x);
					}
				});
			}
		}, "bwana-notes-save");
		var2.setDaemon(true);
		var2.start();
	}

	/** Shutdown hook: write synchronously, without touching Swing. */
	private void flush() {
		if (!this.dirty) {
			return;
		}
		try {
			Notepad.save(this.pending);
		} catch (Throwable var2) {
			System.err.println("bwana: could not flush notes: " + var2.getMessage());
		}
	}
}
