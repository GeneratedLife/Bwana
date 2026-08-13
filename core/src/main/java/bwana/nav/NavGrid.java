package bwana.nav;

import java.util.HashMap;
import java.util.Map;

/**
 * Everything the toolkit has ever seen about where the player can walk.
 * <p>
 * The problem this solves: the client only knows the 104 tiles around you, so when
 * the way to somewhere leads first <i>away</i> from it — around a river, out through
 * a gate — there is no reachable tile closer to the goal and greedy walking stops
 * dead. Routing needs to see more of the world than the client holds, and the only
 * way to have seen more is to remember.
 * <p>
 * Stored by region, the same 64x64 blocks the game divides the world into, and only
 * for regions actually visited. Empty regions cost nothing, so the map is as small
 * as the exploring done so far.
 * <p>
 * <b>Merging is additive.</b> A revisited region overwrites its old flags rather
 * than being skipped: doors open, walls are built, and stale collision is worse than
 * none because it routes confidently into something that is no longer passable.
 * <p>
 * Thread-safe by synchronisation. The mapper writes from the game thread while the
 * pathfinder and the UI read.
 */
public final class NavGrid {

	/** Region side, in tiles. Matches how the game partitions the world. */
	public static final int REGION = 64;

	/** Returned for a tile in a region that has never been seen. */
	public static final int UNKNOWN = -1;

	private final Map<Integer, int[]> regions = new HashMap<Integer, int[]>();

	/** Key packs plane and region coordinates; regions run to about 60 in each axis. */
	private static int key(int regionX, int regionZ, int plane) {
		return (plane << 24) | (regionX << 12) | regionZ;
	}

	/** Fold a scene snapshot into the map. */
	public synchronized void merge(SceneCollision scene) {
		if (scene == null) {
			return;
		}
		for (int var2 = 0; var2 < SceneCollision.SIZE; var2++) {
			int var3 = scene.baseZ + var2;
			for (int var4 = 0; var4 < SceneCollision.SIZE; var4++) {
				int var5 = scene.baseX + var4;
				int var6 = scene.flags[var2 * SceneCollision.SIZE + var4];
				// The scene's outermost ring is filled with "blocked everything" as a
				// sentinel, not as terrain. Storing it would wall off the seam between
				// every pair of regions.
				if (var6 == 0xFFFFFF) {
					continue;
				}
				this.set(var5, var3, scene.plane, var6);
			}
		}
	}

	private void set(int worldX, int worldZ, int plane, int flags) {
		int var5 = key(worldX / REGION, worldZ / REGION, plane);
		int[] var6 = this.regions.get(Integer.valueOf(var5));
		if (var6 == null) {
			var6 = new int[REGION * REGION];
			for (int var7 = 0; var7 < var6.length; var7++) {
				var6[var7] = UNKNOWN;
			}
			this.regions.put(Integer.valueOf(var5), var6);
		}
		var6[(worldZ % REGION) * REGION + (worldX % REGION)] = flags;
	}

	/** Movement flags for a world tile, or {@link #UNKNOWN} if never seen. */
	public synchronized int flagsAt(int worldX, int worldZ, int plane) {
		if (worldX < 0 || worldZ < 0) {
			return UNKNOWN;
		}
		int[] var4 = this.regions.get(
			Integer.valueOf(key(worldX / REGION, worldZ / REGION, plane)));
		if (var4 == null) {
			return UNKNOWN;
		}
		return var4[(worldZ % REGION) * REGION + (worldX % REGION)];
	}

	public synchronized boolean isKnown(int worldX, int worldZ, int plane) {
		return this.flagsAt(worldX, worldZ, plane) != UNKNOWN;
	}

	public synchronized int getRegionCount() {
		return this.regions.size();
	}

	/** Tiles recorded, for the coverage readout. */
	public synchronized int getKnownTileCount() {
		int var1 = 0;
		for (int[] var3 : this.regions.values()) {
			for (int var4 = 0; var4 < var3.length; var4++) {
				if (var3[var4] != UNKNOWN) {
					var1++;
				}
			}
		}
		return var1;
	}

	public synchronized void clear() {
		this.regions.clear();
	}

	// ---- persistence support; the store owns the file format ----

	public synchronized Integer[] keys() {
		return this.regions.keySet().toArray(new Integer[this.regions.size()]);
	}

	public synchronized int[] region(Integer key) {
		return this.regions.get(key);
	}

	public synchronized void putRegion(Integer key, int[] flags) {
		if (flags != null && flags.length == REGION * REGION) {
			this.regions.put(key, flags);
		}
	}
}
