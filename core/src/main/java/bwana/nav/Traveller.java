package bwana.nav;

import bwana.GameEventBus;
import bwana.GameState;
import bwana.action.ActionExecutor;
import bwana.model.PathInfo;

/**
 * Gets the player to a world tile, and says when it has.
 * <p>
 * Extracted so that "go somewhere" is one piece of machinery rather than a habit
 * each behaviour picks up separately. Anything that travels — a validation run, a
 * plan heading for a bank — asks this, and improvements to routing arrive
 * everywhere at once instead of in whichever copy was edited last.
 * <p>
 * Combines the two pathfinders without either knowing about the other: the
 * navigation graph supplies a corner to aim at from everything mapped so far, and
 * the client's own search walks to it. When the graph has no route the aim falls
 * back to the destination itself, which still makes progress and maps ground the
 * graph was missing.
 * <p>
 * <b>Game thread only.</b> Call {@link #tick} once per tick while travelling.
 */
public final class Traveller {

	public static final int TRAVELLING = 0;
	public static final int ARRIVED = 1;

	/** Not moving and not arriving; the caller decides whether that is fatal. */
	public static final int STUCK = 2;

	private static final int TPS = 50;

	/** Backstop between walk commands; normally movement is re-sent only when needed. */
	private static final int WALK_REPEAT = 4 * TPS;

	private static final int STILL_BEFORE_REISSUE = TPS;
	private static final int STUCK_TICKS = 12 * TPS;

	/** A* every hop is affordable; every tick is not. */
	private static final int MAX_PLANS = 400;

	private int destX = Integer.MIN_VALUE;
	private int destZ;
	private int tolerance = 2;

	private int hop;
	private int walkTicks;
	private int stillTicks;
	private int lastX = Integer.MIN_VALUE;
	private int lastZ;
	private int pathLength;
	private int plans;
	private int walks;
	private String note = "";

	/** Aim somewhere new. Resets progress tracking; the previous trip is forgotten. */
	public void setDestination(int worldX, int worldZ, int arrivalTolerance) {
		this.destX = worldX;
		this.destZ = worldZ;
		this.tolerance = arrivalTolerance;
		this.hop = 0;
		this.walkTicks = WALK_REPEAT;
		this.stillTicks = 0;
		this.pathLength = 0;
		this.plans = 0;
		this.walks = 0;
		this.lastX = Integer.MIN_VALUE;
		this.note = "";
	}

	public boolean hasDestination() {
		return this.destX != Integer.MIN_VALUE;
	}

	public String getNote() {
		return this.note;
	}

	/** Tiles actually walked since the destination was set. */
	public int getPathLength() {
		return this.pathLength;
	}

	public int getWalkCount() {
		return this.walks;
	}

	/** Forget the destination, so a caller can stop cleanly. */
	public void clear() {
		this.destX = Integer.MIN_VALUE;
		this.hop = 0;
		this.note = "";
	}

	public int tick(GameState state) {
		if (state == null || !state.isLoggedIn() || !this.hasDestination()) {
			return TRAVELLING;
		}
		int var2 = state.getWorldX();
		int var3 = state.getWorldY();
		this.track(var2, var3);

		int var4 = chebyshev(var2, var3, this.destX, this.destZ);
		if (var4 <= this.tolerance) {
			this.note = "arrived, " + var4 + " tiles off";
			return ARRIVED;
		}

		// Take the next corner from the graph, re-planning as the map grows.
		if (this.hop == 0 || chebyshev(var2, var3, NavPathfinder.unpackX(this.hop),
				NavPathfinder.unpackZ(this.hop)) <= this.tolerance) {
			this.hop = this.planHop(state, var2, var3);
		}
		int var5 = this.hop == 0 ? this.destX : NavPathfinder.unpackX(this.hop);
		int var6 = this.hop == 0 ? this.destZ : NavPathfinder.unpackZ(this.hop);

		// Only re-send when the client is no longer heading where we want, or has
		// stopped short. Re-sending on a timer restarts the move already under way.
		PathInfo var7 = state.getPath();
		boolean var8 = var7 != null && var7.hasDestination()
			&& chebyshev(var7.destX, var7.destY, var5, var6) <= this.tolerance;
		if (++this.walkTicks >= WALK_REPEAT
				&& (!var8 || this.stillTicks > STILL_BEFORE_REISSUE)) {
			this.walkTicks = 0;
			ActionExecutor var9 = GameEventBus.getActionExecutor();
			if (var9 != null) {
				String var10 = var9.walkTo(var5, var6);
				this.walks++;
				if (var10 != null) {
					this.note = var10;
					// The graph offered a hop the client will not walk; drop it and
					// re-plan rather than asking again for the same refusal.
					this.hop = 0;
				}
			}
		}

		if (this.stillTicks > STUCK_TICKS) {
			this.note = "not moving, " + var4 + " tiles short";
			return STUCK;
		}
		this.note = var4 + " tiles to go";
		return TRAVELLING;
	}

	private void track(int worldX, int worldZ) {
		if (this.lastX == Integer.MIN_VALUE) {
			this.lastX = worldX;
			this.lastZ = worldZ;
			return;
		}
		if (worldX == this.lastX && worldZ == this.lastZ) {
			this.stillTicks++;
			return;
		}
		this.pathLength += chebyshev(this.lastX, this.lastZ, worldX, worldZ);
		this.stillTicks = 0;
		this.lastX = worldX;
		this.lastZ = worldZ;
	}

	private int planHop(GameState state, int worldX, int worldZ) {
		if (++this.plans > MAX_PLANS) {
			return 0;
		}
		NavGrid var4 = NavMapper.getGrid();
		if (var4.getRegionCount() == 0) {
			return 0;
		}
		int[] var5 = new NavPathfinder(var4, GameEventBus.getCollisionSource())
			.find(worldX, worldZ, this.destX, this.destZ, state.getPlane(), this.tolerance);
		if (var5 == null || var5.length < 2) {
			return 0;
		}
		int[] var6 = NavPathfinder.simplify(var5);
		for (int var7 = 1; var7 < var6.length; var7++) {
			if (chebyshev(worldX, worldZ, NavPathfinder.unpackX(var6[var7]),
					NavPathfinder.unpackZ(var6[var7])) > this.tolerance) {
				return var6[var7];
			}
		}
		return 0;
	}

	private static int chebyshev(int x1, int z1, int x2, int z2) {
		int var4 = Math.abs(x1 - x2);
		int var5 = Math.abs(z1 - z2);
		return var4 > var5 ? var4 : var5;
	}
}
