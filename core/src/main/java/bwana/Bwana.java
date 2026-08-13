package bwana;

import bwana.model.CameraInfo;
import bwana.model.GroundItemInfo;
import bwana.model.ItemInfo;
import bwana.model.NpcInfo;
import bwana.model.PathInfo;
import bwana.model.PlayerInfo;
import bwana.vision.FrameSource;
import bwana.vision.Vision;

/**
 * Toolkit entry point. The client calls {@link #start} once, from main.
 * <p>
 * Everything the toolkit does hangs off this class, so the client's knowledge of
 * the toolkit is a single import and a single call.
 */
public final class Bwana {

	/**
	 * System property that turns on diagnostic output: {@code -Dbwana.debug=true}.
	 * <p>
	 * Off by default, so a normal session leaves the console clean. Note that
	 * {@code gradlew run} passes {@code -D} flags to Gradle's own JVM, not to the
	 * game, so to enable this run the built jar directly:
	 * <pre>
	 * java -Dbwana.debug=true -jar build/libs/rs2client.jar 10 2000 highmem members
	 * </pre>
	 */
	public static final String DEBUG_PROPERTY = "bwana.debug";

	private static final boolean DEBUG = Boolean.getBoolean(DEBUG_PROPERTY);

	private static boolean started;

	/** Whether diagnostic output is enabled. Read once at class load. */
	public static boolean isDebug() {
		return DEBUG;
	}

	private Bwana() {
	}

	/**
	 * Starts the toolkit against one client.
	 * <p>
	 * The revision comes first and is not optional, because the alternative was a
	 * default: the toolkit used to assume 225's skill names and chat ids, and on
	 * any other client that assumption produced confident wrong labels rather than
	 * an error. An adapter that will not compile without naming its revision
	 * cannot make that mistake.
	 *
	 * @param revision what the numbers this client sends actually mean
	 */
	public static synchronized void start(Revision revision, GameState state, WorldQuery query) {
		if (started) {
			return;
		}
		started = true;
		// Before anything else: chat translation and skill names both read it, and
		// a listener can fire as soon as the bus has one.
		GameEventBus.setRevision(revision);
		GameEventBus.setGameState(state);
		GameEventBus.setWorldQuery(query);
		if (state instanceof FrameSource) {
			Vision.install((FrameSource) state);
		}
		if (state instanceof bwana.inspect.EntityInspector) {
			GameEventBus.setInspector((bwana.inspect.EntityInspector) state);
		}
		if (state instanceof bwana.action.ActionExecutor) {
			GameEventBus.setActionExecutor((bwana.action.ActionExecutor) state);
		}
		if (state instanceof bwana.widget.WidgetSource) {
			GameEventBus.setWidgetSource((bwana.widget.WidgetSource) state);
		}
		if (state instanceof bwana.nav.CollisionSource) {
			GameEventBus.setCollisionSource((bwana.nav.CollisionSource) state);
			// The map is gathered by walking, so mapping has to be running long
			// before anything asks it for a route.
			bwana.nav.NavMapper.loadOnce();
			bwana.nav.LandmarkMemory.loadOnce();
			GameEventBus.addListener(new bwana.nav.NavMapper());
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					bwana.nav.NavMapper.saveNow();
					bwana.nav.LandmarkMemory.save();
				}
			}, "bwana-navmap"));
		}
		// Always on: two long comparisons a tick, and it answers the one question
		// that is impossible to answer from the outside -- whether a freeze was a
		// disconnect or the loop simply not running.
		GameEventBus.addListener(new ConnectionMonitor());
		// registered only when debugging, so it costs nothing in a normal session
		if (DEBUG) {
			GameEventBus.addListener(new EventLogger());
		}
		// the tracker itself is registered by the UI that owns it, in BwanaUi
	}

	/**
	 * Prints the event stream so you can watch the seam working.
	 * <p>
	 * Registered only when {@link #DEBUG_PROPERTY} is set. The XP tracker and chat
	 * panels are the real consumers now; this exists for diagnosing the plumbing.
	 */
	private static final class EventLogger extends GameEventsAdapter {

		/**
		 * Whether we have seen a stat packet for this skill since the last logout.
		 * <p>
		 * Do <b>not</b> infer this from {@code oldXp == 0}. That value is
		 * ambiguous: it means either "login baseline" or "first experience ever in
		 * this skill", and on a fresh account the two are indistinguishable — your
		 * first tree looks exactly like a baseline and gets dropped.
		 * <p>
		 * Reset on logout rather than login, because the server's baseline burst
		 * arrives <i>before</i> onLogin fires (onLogin waits for the player's name,
		 * which comes later, in the appearance block).
		 */
		private final boolean[] seen = new boolean[Skill.CAPACITY];

		/**
		 * Ticks to wait after login before the sanity dump.
		 * <p>
		 * onLogin fires as soon as the player's name arrives, which is well before
		 * the inventory components are filled and before the first scene draw sets
		 * the camera. Dumping immediately reports zeroes that look like bugs but
		 * are just an unfinished login.
		 */
		private static final int DUMP_DELAY_TICKS = 250;

		private int dumpIn = -1;

		public void onLogin() {
			GameState var1 = GameEventBus.getGameState();
			System.out.println("[bwana] login: " + (var1 == null ? "?" : var1.getLocalPlayerName()));
			this.dumpIn = DUMP_DELAY_TICKS;
		}

		public void onTick() {
			if (this.dumpIn < 0) {
				return;
			}
			if (--this.dumpIn == 0) {
				GameState var1 = GameEventBus.getGameState();
				if (var1 != null) {
					dumpWorld(var1);
				}
			}
		}

		/**
		 * One-shot sanity dump of every accessor, on the game thread at login.
		 * <p>
		 * Includes the WorldQuery scans deliberately — once, at a moment where a
		 * 10,800-cell walk costs nothing, is exactly the right place for them.
		 */
		private void dumpWorld(GameState state) {
			PlayerInfo var2 = state.getPlayer();
			if (var2 != null) {
				System.out.println("[bwana]   player " + var2.name + " @ " + var2.worldX + "," + var2.worldY
					+ " plane " + var2.plane + " yaw " + var2.yaw + " combat " + var2.combatLevel);
			}
			CameraInfo var3 = state.getCamera();
			System.out.println("[bwana]   camera world " + var3.worldX + "," + var3.worldY
				+ " (scene " + var3.getSceneTileX() + "," + var3.getSceneTileZ() + ")"
				+ " yaw " + Math.round(var3.getYawDegrees()) % 360 + " deg");
			PathInfo var4 = state.getPath();
			System.out.println("[bwana]   path queued " + var4.queuedX.length
				+ ", destination " + (var4.hasDestination() ? var4.destX + "," + var4.destY : "none"));
			NpcInfo[] var5 = state.getNpcs();
			System.out.println("[bwana]   npcs " + var5.length + (var5.length > 0
				? " (nearest-ish: " + var5[0].name + " @ " + var5[0].worldX + "," + var5[0].worldY + ")" : ""));
			ItemInfo[] var6 = state.getInventory();
			ItemInfo[] var7 = state.getEquipment();
			System.out.println("[bwana]   inventory " + var6.length + " slots used, equipment " + var7.length + " worn");

			WorldQuery var8 = GameEventBus.getWorldQuery();
			if (var8 != null) {
				GroundItemInfo[] var9 = var8.getGroundItems();
				System.out.println("[bwana]   ground items in scene: " + var9.length);
				if (var6.length > 0) {
					System.out.println("[bwana]   first inventory item: id " + var6[0].id
						+ " x" + var6[0].count + " = " + var8.getItemName(var6[0].id));
				}
			}
		}

		public void onLogout() {
			for (int var1 = 0; var1 < this.seen.length; var1++) {
				this.seen[var1] = false;
			}
			System.out.println("[bwana] logout");
		}

		public void onExperienceGained(int skill, int oldXp, int newXp) {
			if (skill < 0 || skill >= this.seen.length) {
				return;
			}
			if (!this.seen[skill]) {
				this.seen[skill] = true;
				System.out.println("[bwana] baseline " + Skill.name(skill) + " = " + newXp);
			} else {
				System.out.println("[bwana] +" + (newXp - oldXp) + " " + Skill.name(skill) + " (now " + newXp + ")");
			}
		}
	}
}
