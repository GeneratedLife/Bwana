package bwana;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Persistence for the notes tab: {@code ~/.bwana/notes.txt}.
 * <p>
 * Unlike the session log and the chat log, this file is <b>rewritten in full</b>
 * on every save rather than appended to, which makes a torn write catastrophic
 * instead of merely lossy — an interrupted save could leave you with a truncated
 * or empty notes file. So {@link #save} never writes to the real file directly:
 * it writes a temp file, rotates the previous version to {@code notes.txt.bak},
 * and only then moves the temp into place. If the move fails the backup is put
 * back. Worst case you lose the newest edit, never the whole file.
 */
public final class Notepad {

	private static final Object LOCK = new Object();

	private Notepad() {
	}

	public static File file() {
		return new File(SessionStore.directory(), "notes.txt");
	}

	public static File backupFile() {
		return new File(SessionStore.directory(), "notes.txt.bak");
	}

	/** Returns the saved notes, or an empty string if there are none. */
	public static String load() {
		synchronized (LOCK) {
			File var0 = file();
			if (!var0.exists()) {
				// fall back to the backup if a previous save was interrupted
				var0 = backupFile();
				if (!var0.exists()) {
					return "";
				}
			}
			InputStream var1 = null;
			try {
				var1 = new FileInputStream(var0);
				ByteArrayOutputStream var2 = new ByteArrayOutputStream();
				byte[] var3 = new byte[4096];
				int var4;
				while ((var4 = var1.read(var3)) != -1) {
					var2.write(var3, 0, var4);
				}
				return new String(var2.toByteArray(), "UTF-8");
			} catch (IOException var9) {
				System.err.println("bwana: could not read notes: " + var9.getMessage());
				return "";
			} finally {
				if (var1 != null) {
					try {
						var1.close();
					} catch (IOException var8) {
					}
				}
			}
		}
	}

	/**
	 * Write the notes out. Call off the event dispatch thread.
	 *
	 * @throws IOException if the notes could not be written
	 */
	public static void save(String text) throws IOException {
		if (text == null) {
			text = "";
		}
		synchronized (LOCK) {
			File var1 = SessionStore.directory();
			if (!var1.exists() && !var1.mkdirs()) {
				throw new IOException("could not create " + var1);
			}
			File var2 = file();
			File var3 = backupFile();
			File var4 = new File(var1, "notes.txt.tmp");

			Writer var5 = null;
			try {
				var5 = new OutputStreamWriter(new FileOutputStream(var4), "UTF-8");
				var5.write(text);
				var5.flush();
			} finally {
				if (var5 != null) {
					try {
						var5.close();
					} catch (IOException var11) {
					}
				}
			}

			// rotate the current file aside rather than deleting it outright
			if (var2.exists()) {
				if (var3.exists() && !var3.delete()) {
					throw new IOException("could not clear " + var3);
				}
				if (!var2.renameTo(var3)) {
					throw new IOException("could not rotate " + var2);
				}
			}
			if (!var4.renameTo(var2)) {
				// put the old one back so we are never left with nothing
				var3.renameTo(var2);
				throw new IOException("could not move new notes into place");
			}
		}
	}
}
