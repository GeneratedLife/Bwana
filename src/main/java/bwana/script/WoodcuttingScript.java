package bwana.script;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;
import bwana.Skill;
import bwana.action.ActionRunner;
import bwana.action.ActionState;
import bwana.action.EntityAction;
import bwana.inspect.EntityHandle;
import bwana.inspect.EntityInfo;
import bwana.inspect.NameFilter;
import bwana.model.PathInfo;
import bwana.model.PlayerInfo;
import bwana.target.Selection;
import bwana.target.TargetCriteria;
import bwana.target.TargetTracker;

/**
 * The smallest complete Woodcutting loop, built only from Bwana's own abstractions.
 * <p>
 * This is an architecture test before it is a script. It touches no client type and
 * knows nothing about revision 225: entities come from the targeting layer,
 * interactions from the action layer, and every judgement about whether something
 * worked comes from {@link GameState}. If those abstractions are sound, the same
 * class runs against a different adapter unchanged; if it needed to reach past them
 * even once, they are not finished.
 * <p>
 * <b>On walking.</b> Bwana exposes {@link PathInfo} to <i>observe</i> movement but
 * has no command to walk somewhere, so nothing here issues one. It does not need
 * to: requesting an interaction on a distant object makes the client path to it, so
 * the chop is what commands the walk and {@link #WALK_TO_TREE} is the phase spent
 * watching that happen. Worth naming as a gap rather than hiding — a task that
 * needs to walk without interacting could not currently be written.
 * <p>
 * <b>On counting.</b> A successful chop is a rise in Woodcutting experience read
 * back out of {@code GameState}, polled rather than taken from the experience
 * event. The event's first delivery per skill is a login baseline and is
 * indistinguishable from a real gain by value alone; comparing against a snapshot
 * taken when the run started sidesteps that ambiguity entirely.
 * <p>
 * <b>Threading.</b> Everything runs on the game thread inside {@code onTick}. The
 * UI reads the volatile fields and calls {@link #start}/{@link #stop}.
 */
public final class WoodcuttingScript extends GameEventsAdapter {

	/** onTick is 50 Hz, so every duration here is in fiftieths of a second. */
	private static final int TPS = 50;

	/** No tree within range for this long and the run has nothing to do. */
	private static final int FIND_TIMEOUT = 15 * TPS;

	/** Long enough to cross the search range on foot, short enough to notice a wall. */
	private static final int WALK_TIMEOUT = 25 * TPS;

	/**
	 * No animation and no experience for this long means the click did not take.
	 * <p>
	 * Generous on purpose: the gap between felling one log and starting the next is
	 * a second or two, and a timeout tighter than that would re-click a tree that
	 * was working perfectly well.
	 */
	private static final int IDLE_TIMEOUT = 6 * TPS;

	/** Re-clicks allowed on a tree that has gone quiet before abandoning it. */
	private static final int MAX_RETRIES = 2;

	/** Pause before scanning again, so a bad area cannot spin at tick rate. */
	private static final int RESELECT_COOLDOWN = TPS / 2;

	/** An object that offers no such action never will; skip it for a good while. */
	private static final long TYPE_EXCLUSION = 300000L;

	/** Unreachable or unresponsive may not last; come back to it. */
	private static final long TRANSIENT_EXCLUSION = 45000L;

	/** Close enough that failing to interact means something other than distance. */
	private static final int IN_RANGE = 2;

	private final TargetTracker tracker = new TargetTracker();
	private final ActionRunner runner = new ActionRunner();
	private final ScriptLog log = new ScriptLog(true);

	private volatile boolean running;
	private volatile int state = ScriptState.IDLE;
	private volatile String note = "";

	/** What to look for and what to do to it. Set before starting. */
	private volatile String treeName = "Tree";
	private volatile String actionName = "Chop";
	private volatile int goal = 10;
	private volatile int searchRange = 15;

	// ---- run statistics ----
	private volatile int chops;
	private volatile int xpGained;
	private volatile int failedInteractions;
	private volatile int reselects;
	private volatile int treesUsed;
	private volatile long startedAt;
	private volatile long finishedAt;

	private int startXp = -1;
	private int lastXp = -1;
	private int stateTicks;
	private EntityHandle target;
	private boolean issued;
	private boolean waitingForLogin;
	private int lastProbe;
	private int idleTicks;
	private int retries;
	private int chopsAtIssue;

	public WoodcuttingScript() {
		// Order matters: the tracker publishes the selection and the runner reads it,
		// so both must have ticked before this class looks at either.
		GameEventBus.addListener(this.tracker);
		GameEventBus.addListener(this.runner);
		GameEventBus.addListener(this);

		TargetCriteria var1 = this.tracker.getCriteria();
		var1.kinds = TargetCriteria.KIND_LOC;
		var1.names = NameFilter.parse(this.treeName);
		var1.maxDistance = this.searchRange;
		// A tree behind the camera is still a tree. Requiring it on screen would
		// reject perfectly choppable targets for a reason that only matters to a
		// human clicking one.
		var1.requireOnScreen = false;
	}

	// ---- configuration ----

	public void setTreeName(String name) {
		this.treeName = name == null || name.trim().length() == 0 ? "Tree" : name.trim();
		this.tracker.getCriteria().names = NameFilter.parse(this.treeName);
	}

	public void setActionName(String name) {
		this.actionName = name == null || name.trim().length() == 0 ? "Chop" : name.trim();
	}

	public void setGoal(int count) {
		this.goal = count < 1 ? 1 : count;
	}

	public void setSearchRange(int tiles) {
		this.searchRange = tiles;
		this.tracker.getCriteria().maxDistance = tiles;
	}

	// ---- read by the UI ----

	public boolean isRunning() {
		return this.running;
	}

	public int getState() {
		return this.state;
	}

	public String getStateName() {
		return ScriptState.name(this.state);
	}

	public String getNote() {
		return this.note;
	}

	public int getChops() {
		return this.chops;
	}

	public int getGoal() {
		return this.goal;
	}

	public ScriptLog getLog() {
		return this.log;
	}

	// ---- control, called from Swing ----

	public synchronized void start() {
		if (this.running) {
			return;
		}
		this.chops = 0;
		this.xpGained = 0;
		this.failedInteractions = 0;
		this.reselects = 0;
		this.treesUsed = 0;
		this.startXp = -1;
		this.lastXp = -1;
		this.target = null;
		this.issued = false;
		this.waitingForLogin = false;
		this.lastProbe = 0;
		this.idleTicks = 0;
		this.retries = 0;
		this.chopsAtIssue = 0;
		this.tracker.getCriteria().excluded.clear();
		this.startedAt = System.currentTimeMillis();
		this.finishedAt = 0L;
		this.log.clear();
		this.log.add("START  looking for \"" + this.treeName + "\", action \"" + this.actionName
			+ "\", within " + this.searchRange + " tiles, goal " + this.goal + " chops");
		this.running = true;
		this.transition(ScriptState.FIND_TREE, "run started");
	}

	public synchronized void stop() {
		if (!this.running) {
			return;
		}
		this.running = false;
		this.finish("stopped by hand");
	}

	// ---- game thread ----

	public void onLogout() {
		if (this.running) {
			this.running = false;
			this.finish("logged out");
		}
	}

	public void onTick() {
		if (!this.running) {
			return;
		}
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			// Say so rather than sitting silently. Started from the login screen this
			// would otherwise show a running script that never leaves FIND_TREE and
			// never times out, which looks identical to a dead state machine.
			if (!this.waitingForLogin) {
				this.waitingForLogin = true;
				this.note = "waiting for login";
				this.log.add("PAUSED  not logged in; will start when you are");
			}
			return;
		}
		if (this.waitingForLogin) {
			this.waitingForLogin = false;
			this.log.add("RESUMED logged in");
			this.stateTicks = 0;
		}
		this.stateTicks++;
		this.pollExperience(var1);

		if (this.state == ScriptState.FIND_TREE) {
			this.tickFind();
		} else if (this.state == ScriptState.WALK_TO_TREE) {
			this.tickWalk(var1);
		} else if (this.state == ScriptState.CHOP) {
			this.tickChop();
		} else if (this.state == ScriptState.WAIT_FOR_RESULT) {
			this.tickWait(var1);
		} else if (this.state == ScriptState.VERIFY) {
			this.tickVerify();
		} else if (this.state == ScriptState.RESELECT) {
			this.tickReselect();
		}
	}

	/**
	 * Count chops from the experience total rather than the experience event.
	 * <p>
	 * See the class note: the event cannot distinguish its own login baseline from a
	 * real gain, and a snapshot taken at start has no such ambiguity.
	 */
	private void pollExperience(GameState state) {
		int var2 = state.getSkillExperience(Skill.WOODCUTTING);
		if (this.startXp < 0) {
			this.startXp = var2;
			this.lastXp = var2;
			return;
		}
		if (var2 <= this.lastXp) {
			return;
		}
		int var3 = var2 - this.lastXp;
		this.lastXp = var2;
		this.xpGained = var2 - this.startXp;
		this.chops++;
		this.log.add("CHOP #" + this.chops + "  +" + var3 + " Woodcutting xp"
			+ (this.target == null ? "" : "  from " + this.target.describe()));
	}

	private void tickFind() {
		if (!this.tracker.isEnabled()) {
			this.tracker.setEnabled(true);
		}
		EntityInfo var1 = heldTarget();
		if (var1 != null) {
			EntityHandle var2 = var1.handle;
			if (this.target == null || !this.target.matches(var2)) {
				this.treesUsed++;
			}
			this.target = var2;
			this.issued = false;
			this.transition(ScriptState.WALK_TO_TREE,
				var1.name + " at " + var1.worldX + "," + var1.worldY
					+ " (" + distanceTo(var1) + " tiles)");
			return;
		}
		// Report what the targeting layer is saying while we wait. Without this a
		// failed search is indistinguishable from a stalled script: both look like
		// FIND_TREE forever. The tracker already explains itself; surface it.
		if (this.stateTicks - this.lastProbe >= 2 * TPS) {
			this.lastProbe = this.stateTicks;
			this.log.add("  ...targeting says " + Selection.getStateName()
				+ (Selection.getNote().length() == 0 ? "" : ": " + Selection.getNote()));
		}
		if (this.stateTicks > FIND_TIMEOUT) {
			this.running = false;
			this.finish("no \"" + this.treeName + "\" within " + this.searchRange
				+ " tiles (targeting: " + Selection.getNote() + ")");
		}
	}

	private void tickWalk(GameState state) {
		EntityInfo var2 = this.heldSameTarget();
		if (var2 == null) {
			this.toReselect("target lost while approaching");
			return;
		}
		// Requesting the interaction is what commands the walk; there is no separate
		// walk API to call, and issuing it once here covers both.
		if (!this.issued) {
			if (!this.requestAction()) {
				this.excludeTargetType("offers no \"" + this.actionName + "\" action",
					TYPE_EXCLUSION);
				this.toReselect("no \"" + this.actionName + "\" action on " + var2.name);
				return;
			}
			// The click that commanded the walk is also the click that starts the
			// chop, so once in range there is nothing further to send.
			this.transition(ScriptState.WAIT_FOR_RESULT, "walking, already clicked");
			return;
		}
		int var3 = distanceTo(var2);
		if (var3 <= IN_RANGE) {
			this.transition(ScriptState.WAIT_FOR_RESULT, "in range at " + var3 + " tiles");
			return;
		}
		if (this.stateTicks > WALK_TIMEOUT) {
			PathInfo var4 = state.getPath();
			this.excludeTarget("unreachable", TRANSIENT_EXCLUSION);
			this.toReselect("unreachable: still " + var3 + " tiles away after "
				+ (this.stateTicks / TPS) + "s"
				+ (var4 != null && var4.isMoving() ? ", still moving" : ", not moving"));
		}
	}

	/**
	 * Request the interaction exactly once, then get out of the way.
	 * <p>
	 * One click is the whole job. The game keeps swinging on its own until the tree
	 * falls, and clicking again restarts that — the first version re-issued after
	 * every confirmed chop, which spam-clicked the tree and interrupted the swing it
	 * was supposedly waiting for.
	 */
	private void tickChop() {
		EntityInfo var1 = this.heldSameTarget();
		if (var1 == null) {
			this.toReselect("tree gone (felled or depleted)");
			return;
		}
		if (!this.issued && !this.requestAction()) {
			this.excludeTargetType("offers no \"" + this.actionName + "\" action", TYPE_EXCLUSION);
			this.toReselect("no \"" + this.actionName + "\" action on " + var1.name);
			return;
		}
		this.transition(ScriptState.WAIT_FOR_RESULT, "clicked once, now watching");
	}

	/**
	 * Watch the game work, and do nothing while it is working.
	 * <p>
	 * "Still chopping" is read from the player's animation and from experience
	 * arriving, both out of {@link GameState}. Neither is an assumption about
	 * timing: while either is true the request is demonstrably still being acted
	 * on, and the only correct thing to do is wait.
	 */
	private void tickWait(GameState state) {
		if (this.heldSameTarget() == null) {
			// The expected ending: the tree fell out of the scene.
			this.transition(ScriptState.VERIFY, "tree gone");
			return;
		}
		// Three independent signs that the click was acted on, all read from
		// GameState. Walking is the one that matters most here: crossing twelve
		// tiles takes longer than the idle timeout, so without it the script gave up
		// on every tree it had to walk to -- which is every tree worth having.
		PlayerInfo var2 = state.getPlayer();
		PathInfo var3 = state.getPath();
		boolean var4 = var2 != null && var2.isAnimating();
		boolean var5 = var3 != null && (var3.isMoving() || var3.hasDestination());
		if (var4 || var5 || this.chops > this.chopsAtIssue) {
			this.idleTicks = 0;
			this.chopsAtIssue = this.chops;
			return;
		}
		if (this.runner.getState() == ActionState.FAILED) {
			this.transition(ScriptState.VERIFY, "action failed: " + this.runner.getNote());
			return;
		}
		if (++this.idleTicks > IDLE_TIMEOUT) {
			this.transition(ScriptState.VERIFY, "nothing happening for "
				+ (IDLE_TIMEOUT / TPS) + "s");
		}
	}

	private void tickVerify() {
		if (this.chops >= this.goal) {
			this.running = false;
			this.finish("reached " + this.goal + " chops");
			return;
		}
		EntityInfo var1 = this.heldSameTarget();
		if (var1 == null) {
			// Not a failure: this is what success looks like from the outside.
			this.log.add("VERIFY tree felled after " + this.chops + " chops so far");
			this.toReselect("tree gone, finding another");
			return;
		}
		// The tree is still standing but nothing is happening. One retry on the same
		// tree, then give up on it -- a tree that will not respond twice is a tree
		// worth walking away from.
		if (this.retries < MAX_RETRIES) {
			this.retries++;
			this.runner.clear();
			this.issued = false;
			this.log.add("VERIFY retry " + this.retries + "/" + MAX_RETRIES + " on " + var1.name);
			this.transition(ScriptState.CHOP, "retrying the same tree");
			return;
		}
		this.failedInteractions++;
		this.excludeTarget("would not respond after " + MAX_RETRIES + " tries", TRANSIENT_EXCLUSION);
		this.toReselect("giving up on this tree");
	}

	private void tickReselect() {
		// Settle before scanning again. Without this the reselect loop runs at tick
		// rate, which is how one bad target produced four thousand log lines.
		if (this.stateTicks < RESELECT_COOLDOWN) {
			return;
		}
		this.reselects++;
		this.runner.clear();
		this.tracker.reset();
		this.target = null;
		this.issued = false;
		this.retries = 0;
		this.idleTicks = 0;
		this.transition(ScriptState.FIND_TREE, "looking for another tree");
	}

	/** Tell the targeting layer to stop offering this one, and say so in the log. */
	private void excludeTarget(String why, long millis) {
		if (this.target == null) {
			return;
		}
		this.tracker.getCriteria().excluded.add(this.target, millis);
		this.log.add("  EXCLUDE " + this.target.describe() + " for "
			+ (millis / 1000L) + "s: " + why);
	}

	/**
	 * Skip everything of the target's type.
	 * <p>
	 * Used when the reason is a property of the definition rather than of this one
	 * object. Felling a tree leaves a stump named "Tree", so a name filter keeps
	 * matching it; learning once that stumps cannot be chopped stops the search
	 * silting up with them as the run goes on.
	 */
	private void excludeTargetType(String why, long millis) {
		if (this.target == null) {
			return;
		}
		this.tracker.getCriteria().excluded.addType(this.target.getKind(),
			this.target.getTypeId(), millis);
		this.log.add("  EXCLUDE all of type " + this.target.getTypeId()
			+ " (" + this.target.getName() + ") for " + (millis / 1000L) + "s: " + why);
	}

	// ---- helpers, all through the public abstractions ----

	/** The locked target, or null if the targeting layer is not holding one. */
	private static EntityInfo heldTarget() {
		return Selection.getState() == Selection.STATE_FOUND ? Selection.getInfo() : null;
	}

	/**
	 * The locked target, but only if it is still the one this run is working on.
	 * <p>
	 * The identity check is the point: the tracker may well have locked another tree
	 * of the same name by now, and continuing to chop as though nothing happened
	 * would quietly attribute a new tree's logs to the old one.
	 */
	private EntityInfo heldSameTarget() {
		EntityInfo var1 = heldTarget();
		if (var1 == null || this.target == null || !this.target.matches(var1.handle)) {
			return null;
		}
		return var1;
	}

	private static int distanceTo(EntityInfo entity) {
		GameState var1 = GameEventBus.getGameState();
		PlayerInfo var2 = var1 == null ? null : var1.getPlayer();
		if (var2 == null) {
			return -1;
		}
		return Math.max(Math.abs(entity.worldX - var2.worldX),
			Math.abs(entity.worldY - var2.worldY));
	}

	/**
	 * Ask the action layer to perform the configured interaction.
	 * <p>
	 * The available list comes from the client's own definitions, so this matches by
	 * wording rather than assuming an op slot — "Chop down" is where the game put it,
	 * not where a hard-coded index says it should be.
	 */
	private boolean requestAction() {
		EntityAction[] var1 = this.runner.getAvailable();
		String var2 = this.actionName.toLowerCase();
		for (int var3 = 0; var3 < var1.length; var3++) {
			if (var1[var3].local || var1[var3].name == null) {
				continue;
			}
			if (var1[var3].name.toLowerCase().indexOf(var2) >= 0) {
				this.runner.select(var3);
				this.runner.requestExecute();
				this.issued = true;
				this.log.add("ACTION \"" + var1[var3].name + "\" -> " + var1[var3].entityName);
				return true;
			}
		}
		// Name it and list what was on offer. "no Chop action" is a dead end; "no
		// Chop action, the tree offers Cut-down and Examine" is a fix.
		StringBuilder var4 = new StringBuilder();
		for (int var5 = 0; var5 < var1.length; var5++) {
			var4.append(var5 == 0 ? "" : ", ").append(var1[var5].name);
		}
		this.log.add("  no action matching \"" + this.actionName + "\"; available: "
			+ (var1.length == 0 ? "(none)" : var4.toString()));
		return false;
	}

	private void toReselect(String why) {
		this.transition(ScriptState.RESELECT, why);
	}

	private void transition(int next, String why) {
		int var3 = this.state;
		this.state = next;
		this.note = why == null ? "" : why;
		this.stateTicks = 0;
		// Reset the patience counters too. Leaving idleTicks above the timeout made
		// every retry fire on the tick it was requested: three "nothing happening for
		// 6s" inside one second, and a tree abandoned before the player had moved.
		this.idleTicks = 0;
		this.chopsAtIssue = this.chops;
		this.log.add(ScriptState.name(var3) + " -> " + ScriptState.name(next)
			+ (why == null || why.length() == 0 ? "" : "   " + why));
	}

	private void finish(String why) {
		this.finishedAt = System.currentTimeMillis();
		this.tracker.setEnabled(false);
		this.runner.clear();
		this.transition(ScriptState.DONE, why);
		String[] var2 = this.summary();
		for (int var3 = 0; var3 < var2.length; var3++) {
			this.log.add(var2[var3]);
		}
	}

	/** The run in a handful of lines. Also written to the log when a run ends. */
	public String[] summary() {
		long var1 = (this.finishedAt == 0L ? System.currentTimeMillis() : this.finishedAt)
			- this.startedAt;
		long var3 = var1 / 1000L;
		String var5 = (var3 / 60L) + "m " + (var3 % 60L) + "s";
		String var6 = var3 > 0L ? String.valueOf(this.xpGained * 3600L / var3) : "n/a";
		return new String[] {
			"SUMMARY  " + this.chops + "/" + this.goal + " chops in " + var5,
			"  woodcutting xp gained   " + this.xpGained + "  (" + var6 + "/hr)",
			"  trees targeted          " + this.treesUsed,
			"  reselects               " + this.reselects,
			"  failed interactions     " + this.failedInteractions,
			"  ended                   " + this.note
		};
	}
}
