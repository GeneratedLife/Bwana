package bwana.nav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Where things are, as opposed to where you can walk.
 * <p>
 * The other half of the world model, and its absence is what stopped a plan that
 * had just picked 121 flax: the navigation graph could have routed anywhere, but
 * nothing knew where a bank was. Object searches only see the loaded scene, so a
 * bank fifty tiles away may as well not exist.
 * <p>
 * Remembered by name, so "Bank booth" resolves from anywhere once one has been
 * seen. Several positions are kept per name — banks are not unique — and the
 * nearest is chosen, which is also what makes a remembered bank useful in a
 * <i>different</i> town from the one it was recorded in.
 * <p>
 * Recorded as a side effect of looking things up. Anything the toolkit successfully
 * resolves in the scene gets remembered, so the map fills in through ordinary use
 * rather than needing a survey.
 */
public final class LandmarkMemory {

	/** Positions kept per name. Enough for several towns, small enough to scan. */
	private static final int MAX_PER_NAME = 24;

	private static final Map<String, List<int[]>> PLACES = new HashMap<String, List<int[]>>();

	/**
	 * Names worth watching for while travelling.
	 * <p>
	 * Recording every named object would store a town per region; recording none
	 * meant the memory only filled from a lookup that had already succeeded, which
	 * is circular — a bank fifty tiles away could never be found, so it could never
	 * be remembered. A plan registers what it cares about, and walking past one is
	 * then enough.
	 */
	private static final List<String> WATCHED = new ArrayList<String>();

	public static synchronized void watch(String name) {
		if (name == null || name.trim().length() == 0) {
			return;
		}
		String var1 = name.trim().toLowerCase();
		if (!WATCHED.contains(var1)) {
			WATCHED.add(var1);
		}
	}

	public static synchronized String[] getWatched() {
		return WATCHED.toArray(new String[WATCHED.size()]);
	}

	private static volatile boolean loaded;
	private static volatile boolean dirty;

	private LandmarkMemory() {
	}

	private static File file() {
		return new File(new File(System.getProperty("user.home"), ".bwana"), "landmarks.txt");
	}

	/**
	 * Note that something with this name stands here.
	 * <p>
	 * Positions within a few tiles of one already known are treated as the same
	 * object: scenery searches return whichever part of a multi-tile object was
	 * nearest, so exact coordinates wobble between sightings.
	 */
	public static synchronized void remember(String name, int worldX, int worldZ, int plane) {
		if (name == null || name.trim().length() == 0) {
			return;
		}
		String var4 = name.trim().toLowerCase();
		List<int[]> var5 = PLACES.get(var4);
		if (var5 == null) {
			var5 = new ArrayList<int[]>();
			PLACES.put(var4, var5);
		}
		for (int var6 = 0; var6 < var5.size(); var6++) {
			int[] var7 = var5.get(var6);
			if (var7[2] == plane && Math.abs(var7[0] - worldX) <= 4
					&& Math.abs(var7[1] - worldZ) <= 4) {
				return;
			}
		}
		if (var5.size() >= MAX_PER_NAME) {
			var5.remove(0);
		}
		var5.add(new int[] { worldX, worldZ, plane });
		dirty = true;
	}

	/**
	 * The nearest remembered place whose name contains {@code nameContains}.
	 *
	 * @return {x, z, plane}, or null if nothing matching has ever been seen
	 */
	public static synchronized int[] nearest(String nameContains, int fromX, int fromZ,
			int plane) {
		if (nameContains == null || nameContains.trim().length() == 0) {
			return null;
		}
		String var5 = nameContains.trim().toLowerCase();
		int[] var6 = null;
		int var7 = Integer.MAX_VALUE;
		for (Map.Entry<String, List<int[]>> var9 : PLACES.entrySet()) {
			if (var9.getKey().indexOf(var5) < 0) {
				continue;
			}
			List<int[]> var10 = var9.getValue();
			for (int var11 = 0; var11 < var10.size(); var11++) {
				int[] var12 = var10.get(var11);
				if (var12[2] != plane) {
					continue;
				}
				int var13 = Math.abs(var12[0] - fromX) + Math.abs(var12[1] - fromZ);
				if (var13 < var7) {
					var7 = var13;
					var6 = var12;
				}
			}
		}
		return var6;
	}

	public static synchronized int getNameCount() {
		return PLACES.size();
	}

	public static synchronized int getPlaceCount() {
		int var0 = 0;
		for (List<int[]> var2 : PLACES.values()) {
			var0 += var2.size();
		}
		return var0;
	}

	public static synchronized void loadOnce() {
		if (loaded) {
			return;
		}
		loaded = true;
		File var0 = file();
		if (!var0.isFile()) {
			return;
		}
		BufferedReader var1 = null;
		try {
			var1 = new BufferedReader(new FileReader(var0));
			String var2;
			while ((var2 = var1.readLine()) != null) {
				String var3 = var2.trim();
				if (var3.length() == 0 || var3.charAt(0) == '#') {
					continue;
				}
				int var4 = var3.lastIndexOf('|');
				if (var4 <= 0) {
					continue;
				}
				String[] var5 = var3.substring(var4 + 1).split(",");
				if (var5.length != 3) {
					continue;
				}
				try {
					remember(var3.substring(0, var4), Integer.parseInt(var5[0].trim()),
						Integer.parseInt(var5[1].trim()), Integer.parseInt(var5[2].trim()));
				} catch (NumberFormatException var6) {
					// a corrupt line is worth skipping, not worth failing the load over
				}
			}
			System.out.println("bwana/nav: remembered " + getPlaceCount() + " places under "
				+ getNameCount() + " names");
		} catch (IOException var7) {
			System.err.println("bwana/nav: could not read landmarks: " + var7);
		} finally {
			if (var1 != null) {
				try {
					var1.close();
				} catch (IOException var8) {
					// nothing useful to do while closing a file we have finished with
				}
			}
		}
	}

	public static synchronized void save() {
		if (!dirty) {
			return;
		}
		File var0 = file();
		File var1 = var0.getParentFile();
		if (!var1.isDirectory() && !var1.mkdirs()) {
			return;
		}
		PrintWriter var2 = null;
		try {
			var2 = new PrintWriter(var0);
			var2.println("# Bwana remembered places -- name|x,z,plane");
			for (Map.Entry<String, List<int[]>> var4 : PLACES.entrySet()) {
				List<int[]> var5 = var4.getValue();
				for (int var6 = 0; var6 < var5.size(); var6++) {
					int[] var7 = var5.get(var6);
					var2.println(var4.getKey() + "|" + var7[0] + "," + var7[1] + "," + var7[2]);
				}
			}
			dirty = false;
		} catch (IOException var8) {
			System.err.println("bwana/nav: could not write landmarks: " + var8);
		} finally {
			if (var2 != null) {
				var2.close();
			}
		}
	}
}
