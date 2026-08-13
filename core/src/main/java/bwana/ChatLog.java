package bwana;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Captures every chat line and appends it to a daily file under
 * {@code ~/.bwana/chat/}.
 * <p>
 * <b>The writer runs on its own thread, and that is the whole point.</b>
 * {@link #onChatMessage} is called from the client's packet loop; opening a file
 * or flushing a buffer there would stall the game loop and, because the network
 * read shares that thread, the connection with it. So the game thread only
 * enqueues — an unbounded, non-blocking hand-off — and a daemon thread does the
 * I/O.
 * <p>
 * Plain text rather than JSON: a chat log's job is to be greppable a year later.
 */
public final class ChatLog extends GameEventsAdapter {

	public interface Listener {
		/** Called on the game thread, immediately as the message arrives. */
		void onChatLogged(ChatMessage message);
	}

	private final BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<ChatMessage>();
	private final Listener listener;
	private Thread writer;

	public ChatLog(Listener listener) {
		this.listener = listener;
	}

	public static File directory() {
		return new File(SessionStore.directory(), "chat");
	}

	public static File fileFor(long time) {
		return new File(directory(), "chat-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)) + ".log");
	}

	public synchronized void start() {
		if (this.writer != null) {
			return;
		}
		this.writer = new Thread(new Runnable() {
			public void run() {
				ChatLog.this.drain();
			}
		}, "bwana-chat-writer");
		this.writer.setDaemon(true);
		this.writer.setPriority(Thread.MIN_PRIORITY);
		this.writer.start();
	}

	public void onChatMessage(int type, String sender, String text) {
		ChatMessage var4 = new ChatMessage(System.currentTimeMillis(), type, sender, text);
		this.queue.add(var4); // never blocks
		if (this.listener != null) {
			this.listener.onChatLogged(var4);
		}
	}

	/** Writer thread. Blocks on the queue, so it costs nothing while idle. */
	private void drain() {
		SimpleDateFormat var1 = new SimpleDateFormat("HH:mm:ss");
		String var2 = null;
		Writer var3 = null;
		try {
			while (true) {
				ChatMessage var4 = this.queue.take();
				try {
					File var5 = fileFor(var4.time);
					// reopen when the date rolls over mid-session
					if (var3 == null || !var5.getPath().equals(var2)) {
						if (var3 != null) {
							var3.close();
						}
						File var6 = var5.getParentFile();
						if (!var6.exists() && !var6.mkdirs()) {
							continue;
						}
						var3 = new OutputStreamWriter(new FileOutputStream(var5, true), "UTF-8");
						var2 = var5.getPath();
					}
					var3.write("[" + var1.format(new Date(var4.time)) + "] "
						+ pad(ChatType.tag(var4.type)) + " " + var4.display() + "\n");
					// flush only when caught up, so a burst costs one flush
					if (this.queue.isEmpty()) {
						var3.flush();
					}
				} catch (IOException var7) {
					System.err.println("bwana: chat log write failed: " + var7.getMessage());
					closeQuietly(var3);
					var3 = null;
					var2 = null;
				}
			}
		} catch (InterruptedException var8) {
			closeQuietly(var3);
		}
	}

	private static String pad(String tag) {
		StringBuilder var1 = new StringBuilder(8);
		var1.append(tag);
		while (var1.length() < 6) {
			var1.append(' ');
		}
		return var1.toString();
	}

	private static void closeQuietly(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException var2) {
			}
		}
	}
}
