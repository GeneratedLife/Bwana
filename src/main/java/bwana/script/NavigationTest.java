package bwana.script;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;
import bwana.action.ActionExecutor;
import bwana.inspect.EntityInfo;
import bwana.inspect.EntityInspector;
import bwana.inspect.NameFilter;
import bwana.model.PathInfo;
import bwana.nav.NavGrid;
import bwana.nav.NavMapper;
import bwana.nav.NavPathfinder;
import bwana.model.PlayerInfo;
import bwana.target.Candidate;
import bwana.target.TargetCriteria;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether Bwana actually knows where things are.
 * <p>
 * Not a gameplay script. It performs the simplest possible journey and spends all
 * its effort on measuring, because the question is not "can the player walk there"
 * but "does the toolkit's account of the walk match what happened". Everything it
 * reports is a comparison between something predicted before moving and something
 * observed after.
 * <p>
 * Four claims get tested:
 * <ul>
 * <li><b>Position.</b> The player's world coordinate is read every tick. Walking
 * moves it smoothly; a jump of several tiles between ticks means the coordinate is
 * being derived wrongly, most likely across a scene reload.</li>
 * <li><b>Landmarks.</b> A named landmark is resolved to a world tile at the start
 * and re-resolved whenever it is in range. That number must not move. Scene-local
 * coordinates converted through a stale origin are the classic way for it to, and
 * the drift is invisible unless something is watching for it.</li>
 * <li><b>Distance.</b> The straight-line prediction is compared against the path
 * actually walked. They will differ — routes bend around obstacles — but the ratio
 * says whether they differ plausibly.</li>
 * <li><b>Arrival.</b> Reaching the destination is decided by measured distance, not
 * by the walk command having been sent.</li>
 * </ul>
 * <p>
 * Uses only the shared abstractions, and deliberately does <i>not</i> drive
 * {@link bwana.target.Selection}: a validation run should not perturb the state it
 * shares with the tab you might be watching it from. Landmarks are resolved through
 * {@link EntityInspector#findCandidates} with a {@link TargetCriteria}, which is the
 * targeting model without the global singleton.
 */
public final class NavigationTest extends GameEventsAdapter {

	private static final int TPS = 50;

	/** Close enough to call it arrival. One tile is unreachable when a loc occupies it. */
	private static final int ARRIVAL_TOLERANCE = 2;

	/**
	 * Minimum gap between walk commands.
	 * <p>
	 * A backstop, not a heartbeat: a walk is normally only re-sent when the client
	 * is no longer heading where we want it to. Routes are capped in length and the
	 * scene reloads under a long journey, so some re-issuing is unavoidable.
	 */
	private static final int WALK_REPEAT = 4 * TPS;

	/** Standing still this long means the walk finished short, or never started. */
	private static final int STILL_BEFORE_REISSUE = TPS;

	/**
	 * How often to re-resolve a landmark to check it has not moved.
	 * <p>
	 * Each check is a full scene scan, so this is deliberately slow. Drift, if it
	 * happens at all, happens when the scene origin shifts — an event measured in
	 * region crossings, not seconds.
	 */
	private static final int STABILITY_INTERVAL = 6 * TPS;

	/**
	 * Fastest the player can legitimately travel, in tiles per second.
	 * <p>
	 * Running is two tiles per 600ms game tick, so 3.33/s; this allows a wide margin
	 * on top. Movement is judged against <b>elapsed time</b> rather than against
	 * ticks, which is the whole point: the first version flagged "jumped 6 tiles in
	 * one tick" whenever the game loop stalled for a second, which is a client
	 * problem being reported as a coordinate problem. Only motion faster than
	 * physically possible is evidence the world model is wrong.
	 */
	private static final int MAX_TILES_PER_SECOND = 6;

	/** A gap longer than this between ticks means the game loop stopped, not the player. */
	private static final long STALL_MILLIS = 400L;

	/**
	 * Smallest move worth judging for speed.
	 * <p>
	 * Below this, "impossibly fast" only ever means the client was catching up after
	 * a pause. Reporting those buried the run's real findings under dozens of
	 * one-tile complaints.
	 */
	private static final int MIN_JUMP_TILES = 3;

	/** No movement at all for this long, and we are not going to arrive. */
	private static final int STUCK_TIMEOUT = 12 * TPS;

	/**
	 * Travel budget, scaled to the distance rather than fixed.
	 * <p>
	 * A flat limit is either too tight for a long journey or useless on a short one.
	 * Walking covers about a tile and a half a second, so two seconds per tile plus
	 * a minute of slack leaves room for detours without letting a genuinely stuck
	 * run sit there indefinitely.
	 */
	private int travelTimeout() {
		int var1 = this.expectedDistance < 0 ? 0 : this.expectedDistance;
		return (60 + var1 * 2) * TPS;
	}

	/** How often to write a position line while travelling. */
	private static final int TRACE_INTERVAL = 2 * TPS;

	/** Named landmarks are found by scanning the loaded scene, which has a radius. */
	private static final int RESOLVE_RANGE = 25;

	private final ScriptLog log = new ScriptLog(true);

	private volatile boolean running;
	private volatile int state = NavState.IDLE;
	private volatile String note = "";

	/**
	 * The whole journey: start first, destination last, waypoints between.
	 * <p>
	 * A direct trip is simply a two-leg route, so there is one travel mechanism
	 * rather than a special case for each.
	 */
	private volatile Landmark[] legs = new Landmark[] {
		Landmark.named("Bank booth"), Landmark.named("Tree")
	};

	/** Resolved world position of each leg, filled as each is reached. */
	private volatile int[] legX = new int[0];
	private volatile int[] legY = new int[0];

	/** Which leg is currently being travelled to. */
	private volatile int legIndex;

	/** Waypoints actually reached, for the report. */
	private volatile int waypointsReached;

	/** Current graph-planned hop, packed; 0 when travelling by direct aim. */
	private int navHop;

	/** A* is not free, so a run that keeps failing to plan stops trying. */
	private static final int MAX_PLANS = 200;

	private volatile int planAttempts;
	private volatile int plansMade;

	/** Waypoints as text, for the log and the report. */
	private String describeWaypoints() {
		StringBuilder var1 = new StringBuilder();
		for (int var2 = 1; var2 < this.legs.length - 1; var2++) {
			if (var2 > 1) {
				var1.append("; ");
			}
			var1.append(this.legs[var2].describe());
		}
		return var1.toString();
	}

	// ---- measurements ----
	private volatile int playerStartX;
	private volatile int playerStartY;
	private volatile int startX = -1;
	private volatile int startY = -1;
	private volatile int destX = -1;
	private volatile int destY = -1;
	private volatile int expectedDistance = -1;
	private volatile int pathLength;
	private volatile int steps;
	private volatile int finalX = -1;
	private volatile int finalY = -1;
	private volatile int finalDistance = -1;
	private volatile int maxJump;
	private volatile int destDrift;
	private final List<String> discrepancies = new ArrayList<String>();

	private int stateTicks;
	private int walkTicks;
	private int stillTicks;
	private int traceTicks;
	private int stabilityTicks;

	/** Walk commands sent, so the report shows how chatty the run was. */
	private volatile int walksIssued;
	private int lastX = Integer.MIN_VALUE;
	private int lastY = Integer.MIN_VALUE;
	private long startedAt;
	private long lastSampleAt;

	/** Client stalls, tracked apart from the world model so one cannot indict the other. */
	private volatile int stalls;
	private volatile long stalledMillis;
	private volatile long worstStall;
	private volatile int disconnects;

	// ---- configuration ----

	/** Set the whole journey at once. Needs at least a start and a destination. */
	public void setLegs(Landmark[] route) {
		if (route != null && route.length >= 2) {
			this.legs = route;
		}
	}

	private Landmark startMark() {
		return this.legs[0];
	}

	private Landmark destMark() {
		return this.legs[this.legs.length - 1];
	}

	// ---- read by the UI ----

	public boolean isRunning() {
		return this.running;
	}

	public int getState() {
		return this.state;
	}

	public String getStateName() {
		return NavState.name(this.state);
	}

	public String getNote() {
		return this.note;
	}

	public ScriptLog getLog() {
		return this.log;
	}

	// ---- control ----

	public synchronized void start() {
		if (this.running) {
			return;
		}
		this.startX = this.startY = this.destX = this.destY = -1;
		this.legIndex = 1;
		this.waypointsReached = 0;
		this.navHop = 0;
		this.planAttempts = 0;
		this.plansMade = 0;
		this.legX = new int[this.legs.length];
		this.legY = new int[this.legs.length];
		for (int var1 = 0; var1 < this.legs.length; var1++) {
			this.legX[var1] = this.legY[var1] = Integer.MIN_VALUE;
		}
		this.finalX = this.finalY = this.finalDistance = -1;
		this.expectedDistance = -1;
		this.pathLength = 0;
		this.steps = 0;
		this.maxJump = 0;
		this.destDrift = 0;
		this.discrepancies.clear();
		this.lastX = this.lastY = Integer.MIN_VALUE;
		this.walkTicks = this.stillTicks = this.traceTicks = this.stabilityTicks = 0;
		this.walksIssued = 0;
		this.stalls = 0;
		this.stalledMillis = 0L;
		this.worstStall = 0L;
		this.disconnects = 0;
		this.lastSampleAt = System.currentTimeMillis();
		this.startedAt = System.currentTimeMillis();
		this.log.clear();
		this.log.add("START  from " + this.startMark().describe()
			+ " to " + this.destMark().describe());
		this.running = true;
		this.transition(NavState.CONFIRM_POSITION, "reading player position");
	}

	public synchronized void stop() {
		if (this.running) {
			this.running = false;
			this.fail("stopped by hand");
		}
	}

	// ---- game thread ----

	/**
	 * A disconnect pauses the run rather than ending it.
	 * <p>
	 * Failing here would make the test a connection-stability check, which it is
	 * not. The measurements survive: they are all comparisons against landmarks and
	 * distances, none of which care that the link dropped. The interruption is
	 * counted and reported instead, so a run that limped through three reconnects
	 * still says whether the world model held up.
	 */
	public void onLogout() {
		if (this.running) {
			this.disconnects++;
			this.log.add("  ! disconnected mid-run; pausing (" + this.disconnects + " so far)");
		}
	}

	public void onLogin() {
		if (this.running) {
			this.log.add("  reconnected; resuming");
			// The scene has been rebuilt, so the previous sample is meaningless as a
			// baseline for movement. Start fresh rather than reporting the gap as a leap.
			this.lastX = this.lastY = Integer.MIN_VALUE;
			this.lastSampleAt = System.currentTimeMillis();
			this.walkTicks = WALK_REPEAT;
		}
	}

	public void onTick() {
		if (!this.running) {
			return;
		}
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			// Paused, not failed. Hold the clock so a long outage cannot look like a
			// travel timeout.
			this.lastSampleAt = System.currentTimeMillis();
			return;
		}
		this.stateTicks++;
		this.trackPosition(var1);

		if (this.state == NavState.CONFIRM_POSITION) {
			this.tickConfirm(var1);
		} else if (this.state == NavState.RESOLVE_START) {
			this.tickResolveStart(var1);
		} else if (this.state == NavState.RESOLVE_DEST) {
			this.tickResolveDest(var1);
		} else if (this.state == NavState.TRAVEL_TO_START) {
			this.tickTravel(var1, this.startX, this.startY, true);
		} else if (this.state == NavState.TRAVEL_TO_DEST) {
			// A named waypoint cannot be found until we are near it, so legs resolve
			// as their turn comes rather than all at once.
			if (!this.resolveLeg(this.legIndex)) {
				this.fail("could not resolve waypoint " + this.legIndex + " ("
					+ this.legs[this.legIndex].describe() + ") from here");
				return;
			}
			this.tickTravel(var1, this.legX[this.legIndex], this.legY[this.legIndex], false);
		} else if (this.state == NavState.VERIFY) {
			this.tickVerify(var1);
		}
	}

	/**
	 * Follow the player tile by tile.
	 * <p>
	 * Path length is accumulated from every change rather than sampled on a timer,
	 * so it is the real number of tiles walked and not an estimate that happens to
	 * agree. The jump check is the position half of the world-model test: a
	 * coordinate that leaps is a coordinate being computed against the wrong origin.
	 */
	private void trackPosition(GameState state) {
		long var2 = System.currentTimeMillis();
		int var4 = state.getWorldX();
		int var5 = state.getWorldY();
		if (this.lastX == Integer.MIN_VALUE) {
			this.lastX = var4;
			this.lastY = var5;
			this.lastSampleAt = var2;
			return;
		}
		long var6 = var2 - this.lastSampleAt;
		this.lastSampleAt = var2;

		// A gap here is the game loop having stopped, not the player having moved.
		// Counting it separately keeps client stalls out of the world-model verdict,
		// while still reporting them -- they are worth knowing about on their own.
		if (var6 >= STALL_MILLIS) {
			this.stalls++;
			this.stalledMillis += var6;
			if (var6 > this.worstStall) {
				this.worstStall = var6;
			}
		}

		if (var4 == this.lastX && var5 == this.lastY) {
			this.stillTicks++;
			return;
		}
		int var8 = chebyshev(this.lastX, this.lastY, var4, var5);
		this.stillTicks = 0;
		this.steps++;
		if (this.state == NavState.TRAVEL_TO_DEST) {
			this.pathLength += var8;
		}
		if (var8 > this.maxJump) {
			this.maxJump = var8;
		}
		// Judge against how much time actually passed, and only for real jumps.
		//
		// A single tile is never evidence of anything: after the loop stalls, the
		// client catches up by stepping through the movement it missed, one tile per
		// tick, which is physically impossible speed and completely correct
		// behaviour. Only a multi-tile leap says the coordinate itself is wrong.
		long var9 = var6 < 1L ? 1L : var6;
		if (var8 >= MIN_JUMP_TILES
				&& (long) var8 * 1000L > (long) MAX_TILES_PER_SECOND * var9) {
			this.addDiscrepancy("moved " + var8 + " tiles in " + var6 + "ms, faster than"
				+ " possible: " + this.lastX + "," + this.lastY + " -> " + var4 + "," + var5);
		}
		this.lastX = var4;
		this.lastY = var5;
	}

	private void tickConfirm(GameState state) {
		PlayerInfo var2 = state.getPlayer();
		if (var2 == null) {
			if (this.stateTicks > 5 * TPS) {
				this.fail("no player snapshot available");
			}
			return;
		}
		this.playerStartX = state.getWorldX();
		this.playerStartY = state.getWorldY();
		// Two independent readings of the same fact. They come from different places
		// in the adapter, so disagreement means one of them is wrong.
		if (var2.worldX != this.playerStartX || var2.worldY != this.playerStartY) {
			this.addDiscrepancy("GameState says " + this.playerStartX + ","
				+ this.playerStartY + " but PlayerInfo says " + var2.worldX + "," + var2.worldY);
		}
		this.log.add("POSITION player at " + this.playerStartX + ", " + this.playerStartY
			+ "  plane " + state.getPlane());
		this.transition(NavState.RESOLVE_START, "resolving start landmark");
	}

	private void tickResolveStart(GameState state) {
		int[] var2 = this.resolve(this.startMark());
		if (var2 == null) {
			this.fail("could not resolve start landmark " + this.startMark().describe()
				+ (this.startMark().isFixed() ? "" : " within " + RESOLVE_RANGE + " tiles"));
			return;
		}
		this.startX = var2[0];
		this.startY = var2[1];
		this.legX[0] = var2[0];
		this.legY[0] = var2[1];
		this.log.add("START LANDMARK " + this.startMark().describe() + " at "
			+ this.startX + ", " + this.startY
			+ "  (" + chebyshev(this.playerStartX, this.playerStartY, this.startX, this.startY)
			+ " tiles from player)");
		this.transition(NavState.RESOLVE_DEST, "resolving destination landmark");
	}

	private void tickResolveDest(GameState state) {
		int[] var2 = this.resolve(this.destMark());
		if (var2 == null) {
			this.fail("could not resolve destination " + this.destMark().describe()
				+ (this.destMark().isFixed() ? ""
					: " within " + RESOLVE_RANGE + " tiles -- name lookup only sees the loaded"
						+ " scene, so use coordinates for longer trips"));
			return;
		}
		this.destX = var2[0];
		this.destY = var2[1];
		int var3 = this.legs.length - 1;
		this.legX[var3] = var2[0];
		this.legY[var3] = var2[1];

		// Fix every waypoint given as a coordinate now, so the expected distance is
		// the sum of the legs rather than a straight line through whatever the route
		// exists to avoid. Named waypoints resolve later, when their leg begins.
		this.expectedDistance = -1;
		for (int var4 = 1; var4 <= var3; var4++) {
			if (this.legs[var4].isFixed()) {
				this.legX[var4] = this.legs[var4].fixedX;
				this.legY[var4] = this.legs[var4].fixedY;
			}
			if (this.legX[var4] != Integer.MIN_VALUE && this.legX[var4 - 1] != Integer.MIN_VALUE) {
				this.expectedDistance = (this.expectedDistance < 0 ? 0 : this.expectedDistance)
					+ chebyshev(this.legX[var4 - 1], this.legY[var4 - 1],
						this.legX[var4], this.legY[var4]);
			}
		}
		this.log.add("DESTINATION " + this.destMark().describe() + " at "
			+ this.destX + ", " + this.destY);
		if (var3 > 1) {
			this.log.add("VIA " + (var3 - 1) + " waypoint(s): " + this.describeWaypoints());
		}
		this.log.add("EXPECTED distance " + this.expectedDistance + " tiles"
			+ (var3 > 1 ? " (sum of legs)" : " (straight line)"));
		if (this.expectedDistance == 0) {
			this.fail("start and destination resolved to the same tile; nothing to measure");
			return;
		}
		this.transition(NavState.TRAVEL_TO_START, "walking to the start landmark");
	}

	/**
	 * Walk toward a tile until we are there, or until it is clear we will not be.
	 *
	 * @param toStart the approach leg, which is not measured — only the destination
	 *                leg counts toward path length
	 */
	private void tickTravel(GameState state, int targetX, int targetY, boolean toStart) {
		int var5 = state.getWorldX();
		int var6 = state.getWorldY();
		int var7 = chebyshev(var5, var6, targetX, targetY);

		if (var7 <= ARRIVAL_TOLERANCE) {
			if (toStart) {
				this.log.add("AT START  " + var5 + ", " + var6 + "  (" + var7 + " tiles off)");
				this.pathLength = 0;
				this.steps = 0;
				this.walkTicks = WALK_REPEAT;
				this.legIndex = 1;
				this.transition(NavState.TRAVEL_TO_DEST,
					"measured leg begins, " + (this.legs.length - 1) + " leg(s) to walk");
			} else if (this.legIndex < this.legs.length - 1) {
				// A waypoint, not the destination: note it and carry on. Path length
				// keeps accumulating across legs, since the whole journey is the thing
				// being measured.
				this.waypointsReached++;
				this.log.add("WAYPOINT " + this.legIndex + "/" + (this.legs.length - 2)
					+ " reached at " + var5 + ", " + var6 + "  (path so far " + this.pathLength + ")");
				this.legIndex++;
				this.walkTicks = WALK_REPEAT;
				this.transition(NavState.TRAVEL_TO_DEST,
					"leg " + this.legIndex + " of " + (this.legs.length - 1));
			} else {
				this.transition(NavState.VERIFY, "arrived within " + var7 + " tiles");
			}
			return;
		}

		// Ask the navigation graph for a route before falling back to aiming at the
		// destination directly. The graph sees everything mapped so far, so it can
		// route out of a pocket the client cannot see out of; when it has no answer
		// the greedy aim still makes progress, and that progress is what maps the
		// ground the graph was missing.
		if (this.navHop == 0 || chebyshev(var5, var6,
				NavPathfinder.unpackX(this.navHop), NavPathfinder.unpackZ(this.navHop))
					<= ARRIVAL_TOLERANCE) {
			this.navHop = this.nextGraphHop(state, targetX, targetY);
		}
		int var20 = this.navHop == 0 ? targetX : NavPathfinder.unpackX(this.navHop);
		int var21 = this.navHop == 0 ? targetY : NavPathfinder.unpackZ(this.navHop);

		// Re-issue only when the walk in progress is not already going where we want.
		//
		// The first version re-sent every two seconds regardless, which is roughly
		// every three or four tiles -- each one recomputing a route and restarting
		// the move that was already under way. A human clicks once and waits; so
		// should this. The destination flag is the client's own record of where it
		// is heading, so it can simply be asked.
		PathInfo var8 = state.getPath();
		boolean var9 = var8 != null && var8.hasDestination()
			&& chebyshev(var8.destX, var8.destY, var20, var21) <= ARRIVAL_TOLERANCE;
		boolean var10 = this.stillTicks > STILL_BEFORE_REISSUE;
		if (++this.walkTicks >= WALK_REPEAT && (!var9 || var10)) {
			this.walkTicks = 0;
			ActionExecutor var11 = GameEventBus.getActionExecutor();
			if (var11 == null) {
				this.fail("no action executor");
				return;
			}
			String var12 = var11.walkTo(var20, var21);
			this.walksIssued++;
			if (var12 != null) {
				this.log.add("  walk refused: " + var12);
				// The graph promised a hop the client will not walk; drop it and
				// re-plan rather than asking again for the same refusal.
				this.navHop = 0;
			}
		}

		if (++this.traceTicks >= TRACE_INTERVAL) {
			this.traceTicks = 0;
			this.log.add("  at " + var5 + ", " + var6 + "  " + var7 + " tiles to go"
				+ "  path=" + this.pathLength
				+ (var8 != null && var8.isMoving() ? "  moving" : "  idle")
				+ (var8 != null && var8.hasDestination()
					? "  flag " + var8.destX + "," + var8.destY : ""));
		}
		// Kept off the trace interval: writing a log line is free, re-scanning the
		// scene is not, and only one of the two needs doing every couple of seconds.
		if (++this.stabilityTicks >= STABILITY_INTERVAL) {
			this.stabilityTicks = 0;
			this.checkLandmarkStable(toStart);
		}

		if (this.stillTicks > STUCK_TIMEOUT) {
			this.addDiscrepancy("stopped moving " + var7 + " tiles short of "
				+ targetX + "," + targetY);
			this.fail("stuck at " + var5 + ", " + var6 + " with " + var7 + " tiles to go");
			return;
		}
		if (this.stateTicks > this.travelTimeout()) {
			this.fail("travel timed out with " + var7 + " tiles to go after "
				+ (this.stateTicks / TPS) + "s");
		}
	}

	/**
	 * Re-resolve the landmark and check it has not moved.
	 * <p>
	 * The sharpest test in here. A landmark's world coordinate is derived from a
	 * scene-local tile plus the scene's origin, and the origin shifts when you cross
	 * a region boundary — precisely what a long walk does. If the conversion is
	 * wrong the landmark appears to slide, and nothing else in the run would notice.
	 */
	private void checkLandmarkStable(boolean toStart) {
		Landmark var2 = toStart ? this.startMark() : this.destMark();
		if (var2.isFixed()) {
			return;
		}
		int[] var3 = this.resolve(var2);
		if (var3 == null) {
			return;
		}
		int var4 = toStart ? this.startX : this.destX;
		int var5 = toStart ? this.startY : this.destY;
		int var6 = chebyshev(var3[0], var3[1], var4, var5);
		if (var6 == 0) {
			return;
		}
		// Another instance of the same name can legitimately be nearer now; only a
		// small shift is evidence of a coordinate problem rather than a different
		// object being picked.
		if (var6 > this.destDrift) {
			this.destDrift = var6;
		}
		if (var6 <= 3) {
			this.addDiscrepancy(var2.describe() + " re-resolved " + var6
				+ " tiles away from its recorded position: " + var4 + "," + var5
				+ " -> " + var3[0] + "," + var3[1]);
		}
	}

	private void tickVerify(GameState state) {
		this.finalX = state.getWorldX();
		this.finalY = state.getWorldY();
		this.finalDistance = chebyshev(this.finalX, this.finalY, this.destX, this.destY);

		// Re-resolve the destination now we are standing next to it. If the landmark
		// has moved in the toolkit's account of the world, this is where it shows.
		if (!this.destMark().isFixed()) {
			int[] var2 = this.resolve(this.destMark());
			if (var2 == null) {
				this.addDiscrepancy("destination landmark could not be re-resolved on arrival");
			} else {
				int var3 = chebyshev(var2[0], var2[1], this.destX, this.destY);
				if (var3 > 0) {
					this.log.add("NOTE destination re-resolved at " + var2[0] + "," + var2[1]
						+ " (" + var3 + " tiles from the recorded position)");
					if (var3 > this.destDrift) {
						this.destDrift = var3;
					}
				}
			}
		}

		if (this.pathLength < this.expectedDistance) {
			this.addDiscrepancy("walked " + this.pathLength + " tiles but the straight line"
				+ " was " + this.expectedDistance + "; a path cannot be shorter than the"
				+ " distance it covers");
		}
		this.running = false;
		if (this.finalDistance > ARRIVAL_TOLERANCE) {
			this.fail("final position is " + this.finalDistance + " tiles from the destination");
			return;
		}
		this.finish(NavState.PASSED, "arrived and verified");
	}

	// ---- helpers ----

	/**
	 * The next waypoint on a graph-planned route, or 0 if the graph has no answer.
	 * <p>
	 * Re-planned each time a hop is reached rather than once at the start: the map
	 * grows while you travel, so a route that did not exist at the outset often does
	 * by the time you are halfway. Returning 0 is not a failure — it means the
	 * caller should keep aiming at the destination directly, which both makes
	 * progress and maps the ground that was missing.
	 */
	private int nextGraphHop(GameState state, int targetX, int targetY) {
		if (++this.planAttempts > MAX_PLANS) {
			return 0;
		}
		NavGrid var4 = NavMapper.getGrid();
		if (var4.getRegionCount() == 0) {
			return 0;
		}
		int var5 = state.getWorldX();
		int var6 = state.getWorldY();
		int[] var7 = new NavPathfinder(var4, GameEventBus.getCollisionSource())
			.find(var5, var6, targetX, targetY, state.getPlane(), ARRIVAL_TOLERANCE);
		if (var7 == null || var7.length < 2) {
			return 0;
		}
		int[] var8 = NavPathfinder.simplify(var7);
		// The first corner far enough away to be worth a walk command; nearer ones
		// the client's own pathfinder would cover on the way past anyway.
		for (int var9 = 1; var9 < var8.length; var9++) {
			int var10 = chebyshev(var5, var6,
				NavPathfinder.unpackX(var8[var9]), NavPathfinder.unpackZ(var8[var9]));
			if (var10 > ARRIVAL_TOLERANCE) {
				this.plansMade++;
				this.log.add("  graph route: " + var7.length + " tiles, " + var8.length
					+ " corners, next hop " + NavPathfinder.unpackX(var8[var9]) + ","
					+ NavPathfinder.unpackZ(var8[var9]) + " (" + var10 + " tiles)");
				return var8[var9];
			}
		}
		return 0;
	}

	/**
	 * Fix a leg's world position, once.
	 * <p>
	 * Coordinates are known immediately; names need the scene, so they resolve when
	 * their leg begins. Once fixed, a leg is never re-resolved — the target must not
	 * wander mid-approach just because a nearer object of the same name came into
	 * view.
	 */
	private boolean resolveLeg(int index) {
		if (this.legX[index] != Integer.MIN_VALUE) {
			return true;
		}
		int[] var2 = this.resolve(this.legs[index]);
		if (var2 == null) {
			return false;
		}
		this.legX[index] = var2[0];
		this.legY[index] = var2[1];
		if (index > 0 && this.legX[index - 1] != Integer.MIN_VALUE) {
			// Expected distance is the sum of the legs, not the straight line from
			// start to finish: a route exists precisely because the straight line is
			// not walkable, so comparing against it would be comparing against a
			// journey nobody asked for.
			int var3 = chebyshev(this.legX[index - 1], this.legY[index - 1],
				var2[0], var2[1]);
			this.expectedDistance = (this.expectedDistance < 0 ? 0 : this.expectedDistance)
				+ var3;
		}
		return true;
	}

	/**
	 * A landmark's world tile, or null if it cannot be found right now.
	 * <p>
	 * Named landmarks go through the ordinary candidate search, so this test sees
	 * exactly what the targeting layer sees — including its range limit, which is
	 * why a distant landmark has to be given as a coordinate.
	 */
	private int[] resolve(Landmark landmark) {
		if (landmark.isFixed()) {
			return new int[] { landmark.fixedX, landmark.fixedY };
		}
		EntityInspector var2 = GameEventBus.getInspector();
		if (var2 == null) {
			return null;
		}
		TargetCriteria var3 = new TargetCriteria();
		var3.kinds = TargetCriteria.KIND_LOC;
		var3.names = NameFilter.parse(landmark.name);
		var3.maxDistance = RESOLVE_RANGE;
		var3.requireOnScreen = false;
		Candidate[] var4 = var2.findCandidates(var3, 16);
		for (int var5 = 0; var5 < var4.length; var5++) {
			if (var4[var5].isEligible()) {
				EntityInfo var6 = var4[var5].entity;
				// Anything resolved is worth remembering; the memory fills in through
				// ordinary use rather than needing a survey.
				bwana.nav.LandmarkMemory.remember(var6.name, var6.worldX, var6.worldY,
					var6.plane);
				return new int[] { var6.worldX, var6.worldY };
			}
		}
		return null;
	}

	private void addDiscrepancy(String text) {
		synchronized (this.discrepancies) {
			if (this.discrepancies.size() < 40) {
				this.discrepancies.add(text);
			}
		}
		this.log.add("  ! " + text);
	}

	private static int chebyshev(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
	}

	private void transition(int next, String why) {
		int var3 = this.state;
		this.state = next;
		this.note = why == null ? "" : why;
		this.stateTicks = 0;
		this.traceTicks = 0;
		this.log.add(NavState.name(var3) + " -> " + NavState.name(next)
			+ (why == null || why.length() == 0 ? "" : "   " + why));
	}

	private void fail(String why) {
		this.running = false;
		this.finish(NavState.FAILED, why);
	}

	private void finish(int result, String why) {
		this.transition(result, why);
		String[] var3 = this.report();
		for (int var4 = 0; var4 < var3.length; var4++) {
			this.log.add(var3[var4]);
		}
	}

	/** The diagnostic report. Also written to the log when a run ends. */
	public String[] report() {
		List<String> var1 = new ArrayList<String>();
		long var2 = (System.currentTimeMillis() - this.startedAt) / 1000L;
		var1.add("---- NAVIGATION REPORT ----");
		var1.add("  result              " + NavState.name(this.state)
			+ "  (" + var2 + "s)");
		var1.add("  start landmark      " + this.startMark().describe()
			+ (this.startX < 0 ? "  UNRESOLVED" : "  -> " + this.startX + ", " + this.startY));
		var1.add("  destination         " + this.destMark().describe()
			+ (this.destX < 0 ? "  UNRESOLVED" : "  -> " + this.destX + ", " + this.destY));
		if (this.legs.length > 2) {
			var1.add("  waypoints           " + this.waypointsReached + "/"
				+ (this.legs.length - 2) + " reached  [" + this.describeWaypoints() + "]");
		}
		var1.add("  expected distance   " + describeNumber(this.expectedDistance) + " tiles"
			+ (this.legs.length > 2 ? " (sum of legs)" : ""));
		var1.add("  actual path length  " + this.pathLength + " tiles over "
			+ this.steps + " position updates");
		var1.add("  final coordinates   " + (this.finalX < 0
			? "not reached" : this.finalX + ", " + this.finalY));
		var1.add("  distance from dest  " + describeNumber(this.finalDistance) + " tiles");
		if (this.expectedDistance > 0 && this.pathLength > 0) {
			// Routes bend; a ratio near 1 means the straight line was walkable, and a
			// large one means a long detour rather than a measurement problem.
			var1.add("  path / straight     "
				+ (this.pathLength * 100 / this.expectedDistance) + "%");
		}
		var1.add("  largest step        " + this.maxJump + " tiles between samples");
		// Reported, but never counted against the verdict: a stalling client is a
		// performance problem, and a world model that is right during a stall is
		// still right. Conflating them is how a stutter reads as a coordinate bug.
		var1.add("  walk commands       " + this.walksIssued);
		var1.add("  graph hops planned  " + this.plansMade + " of " + this.planAttempts
			+ " attempts  (map: " + NavMapper.getGrid().getRegionCount() + " regions)");
		var1.add("  client stalls       " + this.stalls
			+ (this.stalls == 0 ? "" : "  (" + this.stalledMillis + "ms total, worst "
				+ this.worstStall + "ms)"));
		var1.add("  disconnects         " + this.disconnects
			+ (this.disconnects == 0 ? "" : "  (run continued through them)"));
		if (!this.destMark().isFixed()) {
			var1.add("  landmark drift      " + this.destDrift + " tiles");
		}
		synchronized (this.discrepancies) {
			if (this.discrepancies.isEmpty()) {
				var1.add("  discrepancies       none");
			} else {
				var1.add("  discrepancies       " + this.discrepancies.size());
				for (int var4 = 0; var4 < this.discrepancies.size(); var4++) {
					var1.add("    - " + this.discrepancies.get(var4));
				}
			}
		}
		var1.add("  VERDICT             "
			+ (this.state == NavState.PASSED ? "PASS" : "FAIL - " + this.note));
		return var1.toArray(new String[var1.size()]);
	}

	private static String describeNumber(int value) {
		return value < 0 ? "n/a" : String.valueOf(value);
	}
}
