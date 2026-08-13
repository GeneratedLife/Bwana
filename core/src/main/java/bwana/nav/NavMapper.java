package bwana.nav;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;

/**
 * Fills the navigation graph in as you travel.
 * <p>
 * Mapping is a side effect of playing rather than a task: whatever the player walks
 * through gets recorded, so the graph grows to cover the routes actually used. That
 * matters because unknown ground is impassable to the pathfinder — the map has to be
 * built by the greedy walking it eventually replaces.
 * <p>
 * Sampling is driven by the scene origin, not a timer. The client only rebuilds its
 * collision map when the scene reloads, so between reloads there is by definition
 * nothing new; capturing on a timer would copy the same 10,816 tiles over and over
 * for nothing.
 */
public final class NavMapper extends GameEventsAdapter {

	/** Re-capture this often even without a reload, to pick up opened doors. */
	private static final int REFRESH_TICKS = 30 * 50;

	/** Save at most this often; writing the whole map is not free. */
	private static final long SAVE_INTERVAL = 60000L;

	private static final NavGrid GRID = new NavGrid();

	private static volatile boolean loaded;
	private static volatile int captures;

	private int lastBaseX = Integer.MIN_VALUE;
	private int lastBaseZ = Integer.MIN_VALUE;
	private int lastPlane = -1;
	private int ticks;
	private long lastSave;
	private boolean dirty;

	public static NavGrid getGrid() {
		return GRID;
	}

	public static int getCaptureCount() {
		return captures;
	}

	/** Read the stored map once, at startup. */
	public static synchronized void loadOnce() {
		if (!loaded) {
			loaded = true;
			if (NavStore.load(GRID)) {
				System.out.println("bwana/nav: loaded " + GRID.getRegionCount()
					+ " regions, " + GRID.getKnownTileCount() + " tiles");
			}
		}
	}

	/**
	 * Note where any watched landmark stands in the newly loaded scene.
	 * <p>
	 * Only on a scene change, and only for names something actually asked about, so
	 * this costs one scan per region crossing rather than a survey of the world.
	 */
	private void recordWatched(GameState state) {
		String[] var2 = LandmarkMemory.getWatched();
		if (var2.length == 0) {
			return;
		}
		bwana.inspect.EntityInspector var3 = GameEventBus.getInspector();
		if (var3 == null) {
			return;
		}
		for (int var4 = 0; var4 < var2.length; var4++) {
			bwana.target.TargetCriteria var5 = new bwana.target.TargetCriteria();
			var5.kinds = bwana.target.TargetCriteria.KIND_LOC;
			var5.names = bwana.inspect.NameFilter.parse(var2[var4]);
			var5.maxDistance = 25;
			var5.requireOnScreen = false;
			bwana.target.Candidate[] var6 = var3.findCandidates(var5, 8);
			for (int var7 = 0; var7 < var6.length; var7++) {
				bwana.inspect.EntityInfo var8 = var6[var7].entity;
				if (var8.name != null) {
					LandmarkMemory.remember(var8.name, var8.worldX, var8.worldY, var8.plane);
				}
			}
		}
	}

	public static void saveNow() {
		if (NavStore.save(GRID)) {
			System.out.println("bwana/nav: saved " + GRID.getRegionCount() + " regions");
		}
	}

	public void onLogin() {
		this.lastBaseX = Integer.MIN_VALUE;
		this.lastBaseZ = Integer.MIN_VALUE;
	}

	public void onTick() {
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			return;
		}
		CollisionSource var2 = GameEventBus.getCollisionSource();
		if (var2 == null) {
			return;
		}
		boolean var3 = ++this.ticks >= REFRESH_TICKS;
		SceneCollision var4 = var2.captureCollision();
		if (var4 == null) {
			return;
		}
		// Only the scene origin changing means genuinely new ground.
		boolean var5 = var4.baseX != this.lastBaseX || var4.baseZ != this.lastBaseZ
			|| var4.plane != this.lastPlane;
		if (!var5 && !var3) {
			return;
		}
		this.ticks = 0;
		this.lastBaseX = var4.baseX;
		this.lastBaseZ = var4.baseZ;
		this.lastPlane = var4.plane;
		GRID.merge(var4);
		captures++;
		this.dirty = true;
		if (var5) {
			this.recordWatched(var1);
			System.out.println("bwana/nav: mapped scene at " + var4.baseX + ", " + var4.baseZ
				+ " plane " + var4.plane + "  (" + GRID.getRegionCount() + " regions known)");
		}

		long var6 = System.currentTimeMillis();
		if (this.dirty && var6 - this.lastSave >= SAVE_INTERVAL) {
			this.lastSave = var6;
			this.dirty = false;
			saveNow();
		}
	}
}
