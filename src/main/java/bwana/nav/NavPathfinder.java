package bwana.nav;

import java.util.HashMap;
import java.util.Map;

/**
 * A* over everything the map has seen.
 * <p>
 * The counterpart to the client's own search rather than a replacement for it. The
 * client pathfinds beautifully within 104 tiles and cannot see past that; this
 * searches the remembered world and returns a handful of waypoints, which the
 * client then walks between using its own pathfinder. Neither does the other's job.
 * <p>
 * Unknown tiles are impassable. That is the conservative reading and the right one:
 * routing through terrain never observed would produce confident paths into
 * unexplored space, and the failure would look exactly like a map bug. A route only
 * exists once the ground under it has actually been walked.
 */
public final class NavPathfinder {

	/** Cap on tiles examined, so an impossible request cannot hang the caller. */
	private static final int MAX_EXPANSIONS = 400000;

	private static final int[] STEP_X = new int[] { 0, 1, 0, -1 };
	private static final int[] STEP_Z = new int[] { 1, 0, -1, 0 };

	private final NavGrid grid;
	private final CollisionSource collision;

	public NavPathfinder(NavGrid grid, CollisionSource collision) {
		this.grid = grid;
		this.collision = collision;
	}

	/**
	 * A route from one world tile to another, as packed {@code x | z << 16} tiles.
	 * <p>
	 * Returned in travel order, start first. Null when no path exists through known
	 * ground — which usually means the map has a hole, not that the world does.
	 *
	 * @param tolerance how near the destination counts as arriving; a tile occupied
	 *                  by scenery cannot be stood on, so demanding it exactly would
	 *                  fail on most landmarks
	 */
	public int[] find(int startX, int startZ, int destX, int destZ, int plane, int tolerance) {
		if (this.collision == null) {
			return null;
		}
		Map<Integer, Integer> var7 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> var8 = new HashMap<Integer, Integer>();
		NavQueue var9 = new NavQueue();

		int var10 = pack(startX, startZ);
		var8.put(Integer.valueOf(var10), Integer.valueOf(0));
		var9.push(var10, heuristic(startX, startZ, destX, destZ));

		int var11 = 0;
		while (!var9.isEmpty() && var11++ < MAX_EXPANSIONS) {
			int var12 = var9.pop();
			int var13 = unpackX(var12);
			int var14 = unpackZ(var12);
			if (Math.abs(var13 - destX) <= tolerance && Math.abs(var14 - destZ) <= tolerance) {
				return rebuild(var7, var12);
			}
			int var15 = var8.get(Integer.valueOf(var12)).intValue();
			for (int var16 = 0; var16 < 4; var16++) {
				int var17 = var13 + STEP_X[var16];
				int var18 = var14 + STEP_Z[var16];
				int var19 = this.grid.flagsAt(var17, var18, plane);
				if (var19 == NavGrid.UNKNOWN || !this.collision.canEnter(var19, var16)) {
					continue;
				}
				int var20 = pack(var17, var18);
				int var21 = var15 + 1;
				Integer var22 = var8.get(Integer.valueOf(var20));
				if (var22 != null && var22.intValue() <= var21) {
					continue;
				}
				var8.put(Integer.valueOf(var20), Integer.valueOf(var21));
				var7.put(Integer.valueOf(var20), Integer.valueOf(var12));
				var9.push(var20, var21 + heuristic(var17, var18, destX, destZ));
			}
		}
		return null;
	}

	/**
	 * Reduce a tile-by-tile path to its corners.
	 * <p>
	 * The client walks between waypoints perfectly well on its own, so handing it
	 * every tile would be busywork — and each waypoint costs a walk command. Only
	 * the points where the path changes direction carry information.
	 */
	public static int[] simplify(int[] path) {
		if (path == null || path.length <= 2) {
			return path;
		}
		int[] var1 = new int[path.length];
		int var2 = 0;
		var1[var2++] = path[0];
		for (int var3 = 1; var3 < path.length - 1; var3++) {
			int var4 = unpackX(path[var3]) - unpackX(path[var3 - 1]);
			int var5 = unpackZ(path[var3]) - unpackZ(path[var3 - 1]);
			int var6 = unpackX(path[var3 + 1]) - unpackX(path[var3]);
			int var7 = unpackZ(path[var3 + 1]) - unpackZ(path[var3]);
			if (var4 != var6 || var5 != var7) {
				var1[var2++] = path[var3];
			}
		}
		var1[var2++] = path[path.length - 1];
		int[] var8 = new int[var2];
		System.arraycopy(var1, 0, var8, 0, var2);
		return var8;
	}

	private static int[] rebuild(Map<Integer, Integer> cameFrom, int end) {
		int var2 = 0;
		Integer var3 = Integer.valueOf(end);
		while (var3 != null) {
			var2++;
			var3 = cameFrom.get(var3);
		}
		int[] var4 = new int[var2];
		var3 = Integer.valueOf(end);
		while (var3 != null) {
			var4[--var2] = var3.intValue();
			var3 = cameFrom.get(var3);
		}
		return var4;
	}

	/** Manhattan, because movement here is four-directional and unit cost. */
	private static int heuristic(int x1, int z1, int x2, int z2) {
		return Math.abs(x1 - x2) + Math.abs(z1 - z2);
	}

	public static int pack(int x, int z) {
		return (x & 0xFFFF) | (z & 0xFFFF) << 16;
	}

	public static int unpackX(int packed) {
		return packed & 0xFFFF;
	}

	public static int unpackZ(int packed) {
		return packed >>> 16 & 0xFFFF;
	}

	/** Binary heap keyed on estimated total cost. */
	private static final class NavQueue {

		private int[] items = new int[1024];
		private int[] costs = new int[1024];
		private int size;

		boolean isEmpty() {
			return this.size == 0;
		}

		void push(int item, int cost) {
			if (this.size == this.items.length) {
				int[] var3 = new int[this.size * 2];
				int[] var4 = new int[this.size * 2];
				System.arraycopy(this.items, 0, var3, 0, this.size);
				System.arraycopy(this.costs, 0, var4, 0, this.size);
				this.items = var3;
				this.costs = var4;
			}
			int var5 = this.size++;
			this.items[var5] = item;
			this.costs[var5] = cost;
			while (var5 > 0) {
				int var6 = (var5 - 1) / 2;
				if (this.costs[var6] <= this.costs[var5]) {
					break;
				}
				swap(this.items, var5, var6);
				swap(this.costs, var5, var6);
				var5 = var6;
			}
		}

		int pop() {
			int var1 = this.items[0];
			this.items[0] = this.items[--this.size];
			this.costs[0] = this.costs[this.size];
			int var2 = 0;
			while (true) {
				int var3 = var2 * 2 + 1;
				if (var3 >= this.size) {
					break;
				}
				if (var3 + 1 < this.size && this.costs[var3 + 1] < this.costs[var3]) {
					var3++;
				}
				if (this.costs[var2] <= this.costs[var3]) {
					break;
				}
				swap(this.items, var2, var3);
				swap(this.costs, var2, var3);
				var2 = var3;
			}
			return var1;
		}

		private static void swap(int[] array, int a, int b) {
			int var3 = array[a];
			array[a] = array[b];
			array[b] = var3;
		}
	}
}
