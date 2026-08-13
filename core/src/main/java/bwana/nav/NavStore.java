package bwana.nav;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Persists the navigation graph to {@code ~/.bwana/navmap.dat}.
 * <p>
 * The map is expensive to gather — every region in it had to be walked through — so
 * losing it on exit would mean re-exploring the world every session, which is the
 * one thing a persistent graph exists to avoid.
 * <p>
 * Binary, because it is thousands of tiles per region and nobody reads it by hand.
 * A version byte leads, so a later format change can decline to load an old file
 * rather than misinterpret it.
 */
public final class NavStore {

	private static final int MAGIC = 0x424E4156;
	private static final int VERSION = 1;

	private NavStore() {
	}

	private static File file() {
		return new File(new File(System.getProperty("user.home"), ".bwana"), "navmap.dat");
	}

	public static boolean exists() {
		return file().isFile();
	}

	/** Read into the grid, adding to whatever it already holds. */
	public static boolean load(NavGrid grid) {
		File var1 = file();
		if (!var1.isFile()) {
			return false;
		}
		DataInputStream var2 = null;
		try {
			var2 = new DataInputStream(new BufferedInputStream(new FileInputStream(var1)));
			if (var2.readInt() != MAGIC || var2.readInt() != VERSION) {
				System.err.println("bwana/nav: navmap.dat is not a format we know; ignoring");
				return false;
			}
			int var3 = var2.readInt();
			for (int var4 = 0; var4 < var3; var4++) {
				int var5 = var2.readInt();
				int[] var6 = new int[NavGrid.REGION * NavGrid.REGION];
				for (int var7 = 0; var7 < var6.length; var7++) {
					var6[var7] = var2.readInt();
				}
				grid.putRegion(Integer.valueOf(var5), var6);
			}
			return true;
		} catch (IOException var8) {
			System.err.println("bwana/nav: could not read navmap: " + var8);
			return false;
		} finally {
			closeQuietly(var2);
		}
	}

	/** Write the whole grid, replacing any previous file. */
	public static boolean save(NavGrid grid) {
		File var1 = file();
		File var2 = var1.getParentFile();
		if (!var2.isDirectory() && !var2.mkdirs()) {
			System.err.println("bwana/nav: could not create " + var2);
			return false;
		}
		DataOutputStream var3 = null;
		try {
			var3 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(var1)));
			var3.writeInt(MAGIC);
			var3.writeInt(VERSION);
			Integer[] var4 = grid.keys();
			var3.writeInt(var4.length);
			for (int var5 = 0; var5 < var4.length; var5++) {
				int[] var6 = grid.region(var4[var5]);
				if (var6 == null) {
					continue;
				}
				var3.writeInt(var4[var5].intValue());
				for (int var7 = 0; var7 < var6.length; var7++) {
					var3.writeInt(var6[var7]);
				}
			}
			return true;
		} catch (IOException var8) {
			System.err.println("bwana/nav: could not write navmap: " + var8);
			return false;
		} finally {
			if (var3 != null) {
				try {
					var3.close();
				} catch (IOException var9) {
					// the data is already flushed or already lost
				}
			}
		}
	}

	private static void closeQuietly(DataInputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException var2) {
				// nothing useful to do while closing a file we have finished with
			}
		}
	}
}
