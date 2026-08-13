package bwana;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Appends finished sessions to {@code ~/.bwana/sessions.jsonl}.
 * <p>
 * One JSON object per line rather than one big JSON array. Appending to an array
 * means reading, parsing, and rewriting the whole file, and a crash mid-write
 * leaves you with a truncated document that no parser will accept. Appending a
 * line is a single atomic-ish write, and a torn final line costs you one session
 * instead of all of them.
 * <p>
 * Hand-rolled because the client targets Java 1.6 with no dependencies, and this
 * needs perhaps thirty lines of writer rather than a JSON library.
 */
public final class SessionStore {

	private static final Object LOCK = new Object();

	private SessionStore() {
	}

	public static File directory() {
		return new File(System.getProperty("user.home"), ".bwana");
	}

	public static File file() {
		return new File(directory(), "sessions.jsonl");
	}

	/**
	 * @param gained experience gained per skill, indexed by skill id
	 */
	public static void append(long startMillis, long endMillis, int[] gained) {
		StringBuilder var4 = new StringBuilder(256);
		var4.append("{\"start\":").append(startMillis);
		var4.append(",\"end\":").append(endMillis);
		var4.append(",\"skills\":{");

		boolean var5 = false;
		int var6 = 0;
		for (int var7 = 0; var7 < gained.length; var7++) {
			if (gained[var7] <= 0) {
				continue;
			}
			if (var5) {
				var4.append(',');
			}
			var5 = true;
			var4.append('"').append(escape(Skill.name(var7))).append("\":").append(gained[var7]);
			var6 += gained[var7];
		}
		var4.append("},\"total\":").append(var6).append('}');

		if (!var5) {
			return; // nothing was gained; not worth a line
		}

		synchronized (LOCK) {
			Writer var8 = null;
			try {
				File var9 = directory();
				if (!var9.exists() && !var9.mkdirs()) {
					return;
				}
				var8 = new OutputStreamWriter(new FileOutputStream(file(), true), "UTF-8");
				var8.write(var4.toString());
				var8.write('\n');
			} catch (IOException var14) {
				// persistence failing must never take the game down with it
				System.err.println("bwana: could not write session log: " + var14.getMessage());
			} finally {
				if (var8 != null) {
					try {
						var8.close();
					} catch (IOException var13) {
					}
				}
			}
		}
	}

	private static String escape(String text) {
		StringBuilder var1 = new StringBuilder(text.length() + 8);
		for (int var2 = 0; var2 < text.length(); var2++) {
			char var3 = text.charAt(var2);
			if (var3 == '"' || var3 == '\\') {
				var1.append('\\').append(var3);
			} else if (var3 < 32) {
				var1.append(' ');
			} else {
				var1.append(var3);
			}
		}
		return var1.toString();
	}
}
