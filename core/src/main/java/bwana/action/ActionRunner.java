package bwana.action;

import bwana.ChatType;
import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;
import bwana.Skill;
import bwana.inspect.EntityInfo;
import bwana.model.PlayerInfo;
import bwana.target.Selection;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs one action at a time against whatever the target system has selected, and
 * decides afterwards whether it worked.
 * <p>
 * The point of this class is the second half. Sending an interaction is trivial —
 * write five numbers and call the client's menu handler. The hard and useful part
 * is refusing to call that a success. A request the server drops, a tree someone
 * else fells first, a hatchet you are not carrying: all of them look exactly like
 * a working chop at the moment the packet leaves. So execution only ever moves the
 * state to {@link ActionState#WAITING}, and the state advances to SUCCESS solely
 * on evidence read back out of the game — experience, inventory, animation, or the
 * game's own chat line.
 * <p>
 * Only observes and acts through {@link bwana.target.Selection} and
 * {@link ActionExecutor}; it never searches for entities itself, so the targeting
 * subsystem stays the sole authority on what is selected.
 * <p>
 * Runs on the game thread. Swing touches it only through the volatile fields and
 * {@link #requestExecute}.
 */
public final class ActionRunner extends GameEventsAdapter {

	/** onTick is 50 Hz, so these are fiftieths of a second, not game ticks. */
	private static final int TICKS_PER_SECOND = 50;

	/** How long to wait for the world to react. Long enough to walk across the search range. */
	private static final int REMOTE_TIMEOUT = 12 * TICKS_PER_SECOND;

	/** Client-side actions answer immediately or not at all. */
	private static final int LOCAL_TIMEOUT = 2 * TICKS_PER_SECOND;

	/** Snapshots cost a couple of small arrays, so compare a few times a second, not fifty. */
	private static final int CHECK_INTERVAL = 5;

	/** Lines the game prints when it is telling you no. */
	private static final String[] REFUSALS = new String[] {
		"you need", "you don't have", "you do not have", "you can't", "you cannot",
		"i can't", "i cannot", "nothing interesting happens", "unable to", "you must",
		"that is too far away", "you are already"
	};

	private volatile EntityAction[] available = new EntityAction[0];
	private volatile int selectedIndex = -1;
	private volatile int state = ActionState.NONE;
	private volatile String note = "";
	private volatile EntityAction running;
	private volatile String[] evidence = new String[0];

	/** Set from Swing, consumed on the game thread. */
	private volatile boolean executeRequested;

	/** Set from the chat callback while waiting; consumed by the confirmation check. */
	private volatile String chatSeen;

	private EntityInfo lastTarget;
	private Snapshot baseline;
	private int waited;
	private int sinceCheck;
	private boolean movedNoted;

	// ---- read by the UI ----

	public EntityAction[] getAvailable() {
		return this.available;
	}

	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	public EntityAction getSelected() {
		EntityAction[] var1 = this.available;
		int var2 = this.selectedIndex;
		return var2 >= 0 && var2 < var1.length ? var1[var2] : null;
	}

	public int getState() {
		return this.state;
	}

	public String getStateName() {
		return ActionState.name(this.state);
	}

	public String getNote() {
		return this.note;
	}

	public EntityAction getRunning() {
		return this.running;
	}

	public String[] getEvidence() {
		return this.evidence;
	}

	/** Seconds left before a pending action is given up on, or -1 when not waiting. */
	public int getSecondsLeft() {
		if (this.state != ActionState.WAITING) {
			return -1;
		}
		int var1 = this.running != null && this.running.local ? LOCAL_TIMEOUT : REMOTE_TIMEOUT;
		return (var1 - this.waited + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
	}

	// ---- driven by the UI ----

	public void select(int index) {
		this.selectedIndex = index;
		if (!ActionState.isTerminal(this.state) || index >= 0) {
			this.state = index >= 0 ? ActionState.READY : ActionState.NONE;
			this.note = "";
			this.evidence = new String[0];
			this.running = null;
		}
	}

	/** Ask for the selected action to run. Picked up on the next game tick. */
	public void requestExecute() {
		this.executeRequested = true;
	}

	public void clear() {
		this.executeRequested = false;
		this.running = null;
		this.baseline = null;
		this.evidence = new String[0];
		this.note = "";
		this.state = this.selectedIndex >= 0 ? ActionState.READY : ActionState.NONE;
	}

	// ---- game thread ----

	public void onLogout() {
		this.available = new EntityAction[0];
		this.selectedIndex = -1;
		this.lastTarget = null;
		this.clear();
		this.state = ActionState.NONE;
	}

	public void onChatMessage(int type, String sender, String text) {
		// Only the game's own voice counts. Another player saying "you need a hatchet"
		// in public chat is not the server answering our request.
		if (this.state == ActionState.WAITING && type == ChatType.GAME && text != null) {
			this.chatSeen = text;
		}
	}

	public void onTick() {
		ActionExecutor var1 = GameEventBus.getActionExecutor();
		if (var1 == null) {
			return;
		}
		this.refreshAvailable(var1);

		if (this.executeRequested) {
			this.executeRequested = false;
			this.beginExecute(var1);
			return;
		}
		if (this.state == ActionState.WAITING) {
			this.watch();
		}
	}

	/**
	 * Keep the action list in step with the selection.
	 * <p>
	 * Re-queried only when the target actually changes, not every tick: the ops come
	 * from cache definitions that cannot change under us, so re-reading them fifty
	 * times a second would buy nothing. It also means the chosen action survives the
	 * target being re-resolved each tick, which is what lets you pick "Chop down"
	 * and then press execute a second later.
	 */
	private void refreshAvailable(ActionExecutor executor) {
		EntityInfo var2 = Selection.getState() == Selection.STATE_FOUND ? Selection.getInfo() : null;
		if (var2 == null) {
			if (this.lastTarget != null) {
				this.lastTarget = null;
				this.available = new EntityAction[0];
				this.selectedIndex = -1;
				if (this.state == ActionState.READY) {
					this.state = ActionState.NONE;
				}
			}
			return;
		}
		if (sameEntity(this.lastTarget, var2)) {
			return;
		}
		this.lastTarget = var2;

		EntityAction[] var3 = executor.getActions(var2);
		this.available = var3 == null ? new EntityAction[0] : var3;

		// Carry the choice across a target change when the new target offers the same
		// option -- switching from one tree to the next should not silently deselect
		// "Chop down" and leave the execute button doing nothing.
		EntityAction var4 = this.getSelected();
		int var5 = -1;
		for (int var6 = 0; var4 != null && var6 < this.available.length; var6++) {
			if (this.available[var6].name.equalsIgnoreCase(var4.name)) {
				var5 = var6;
				break;
			}
		}
		if (var5 == -1 && this.available.length > 0) {
			var5 = 0;
		}
		this.selectedIndex = var5;
		if (!ActionState.isTerminal(this.state)) {
			this.state = var5 >= 0 ? ActionState.READY : ActionState.NONE;
		}
	}

	private void beginExecute(ActionExecutor executor) {
		EntityAction var2 = this.getSelected();
		if (var2 == null) {
			this.fail("no action selected");
			return;
		}
		EntityInfo var3 = Selection.getState() == Selection.STATE_FOUND ? Selection.getInfo() : null;
		if (var3 == null) {
			this.fail("no target selected");
			return;
		}
		if (!var2.appliesTo(var3)) {
			// The target moved on between choosing and pressing. Refusing beats sending
			// an interaction aimed at whatever now happens to hold that index.
			this.fail("target changed since the action was chosen");
			return;
		}

		this.running = var2;
		this.state = ActionState.EXECUTING;
		this.note = "";
		this.evidence = new String[0];
		this.chatSeen = null;
		this.movedNoted = false;
		this.waited = 0;
		this.sinceCheck = 0;
		this.baseline = Snapshot.take();

		String var4 = executor.execute(var2);
		if (var4 != null) {
			this.fail(var4);
			return;
		}
		if (this.baseline == null) {
			this.fail("sent, but not logged in to confirm against");
			return;
		}
		this.state = ActionState.WAITING;
		this.note = "sent " + var2.describe() + ", watching for a change";
	}

	/**
	 * Look for evidence that the action landed.
	 * <p>
	 * Ordered by how directly each signal answers the question. A refusal in the
	 * game's own words is the clearest thing there is, so it is tested first and
	 * beats every positive signal — otherwise a player animation left over from
	 * something else would report success over the top of "You need an axe".
	 */
	private void watch() {
		this.waited++;
		int var1 = this.running != null && this.running.local ? LOCAL_TIMEOUT : REMOTE_TIMEOUT;

		String var2 = this.chatSeen;
		if (var2 != null) {
			this.chatSeen = null;
			if (looksLikeRefusal(var2)) {
				this.finish(ActionState.FAILED, "the game refused", "game said: \"" + var2 + "\"");
				return;
			}
			this.finish(ActionState.SUCCESS, "the game responded", "game said: \"" + var2 + "\"");
			return;
		}

		if (++this.sinceCheck < CHECK_INTERVAL) {
			if (this.waited >= var1) {
				this.timeout();
			}
			return;
		}
		this.sinceCheck = 0;

		Snapshot var3 = Snapshot.take();
		if (var3 == null || this.baseline == null) {
			if (this.waited >= var1) {
				this.timeout();
			}
			return;
		}

		int var4 = this.baseline.xpSkillChanged(var3);
		if (var4 >= 0) {
			this.finish(ActionState.SUCCESS, "experience gained",
				Skill.name(var4) + " +" + (var3.xp[var4] - this.baseline.xp[var4]) + " xp");
			return;
		}
		String var5 = this.baseline.inventoryChange(var3);
		if (var5 != null) {
			this.finish(ActionState.SUCCESS, "inventory changed", var5);
			return;
		}
		if (this.baseline.animation < 0 && var3.animation >= 0) {
			this.finish(ActionState.SUCCESS, "player started animating",
				"animation " + var3.animation);
			return;
		}
		if (!this.movedNoted && (var3.worldX != this.baseline.worldX
				|| var3.worldY != this.baseline.worldY)) {
			// Movement alone is not success -- walking towards a tree is not chopping it
			// -- but it does show the client acted on the request, so it is worth saying.
			this.movedNoted = true;
			this.note = "walking towards the target";
		}
		if (this.waited >= var1) {
			this.timeout();
		}
	}

	private void timeout() {
		this.finish(ActionState.FAILED, "nothing observable happened",
			"no experience, inventory, animation or message within "
				+ (this.waited / TICKS_PER_SECOND) + "s");
	}

	private void finish(int newState, String why, String proof) {
		List<String> var4 = new ArrayList<String>();
		if (this.movedNoted) {
			var4.add("player moved after the request");
		}
		var4.add(proof);
		this.evidence = var4.toArray(new String[var4.size()]);
		this.note = why;
		this.state = newState;
		this.baseline = null;
	}

	private void fail(String why) {
		this.note = why;
		this.evidence = new String[0];
		this.state = ActionState.FAILED;
		this.baseline = null;
	}

	private static boolean looksLikeRefusal(String text) {
		String var1 = text.toLowerCase();
		for (int var2 = 0; var2 < REFUSALS.length; var2++) {
			if (var1.indexOf(REFUSALS[var2]) >= 0) {
				return true;
			}
		}
		return false;
	}

	private static boolean sameEntity(EntityInfo a, EntityInfo b) {
		if (a == null || b == null) {
			return a == b;
		}
		return a.isSameEntity(b);
	}

	/**
	 * The handful of values that tell you whether anything actually happened.
	 * <p>
	 * Taken before the request and again while waiting; a difference is the only
	 * thing this class will accept as proof.
	 */
	private static final class Snapshot {

		final int[] xp;
		final int[] invIds;
		final int[] invCounts;
		final int animation;
		final int worldX;
		final int worldY;

		private Snapshot(int[] xp, int[] invIds, int[] invCounts, int animation,
				int worldX, int worldY) {
			this.xp = xp;
			this.invIds = invIds;
			this.invCounts = invCounts;
			this.animation = animation;
			this.worldX = worldX;
			this.worldY = worldY;
		}

		/** Null when not logged in, which means there is nothing to compare against. */
		static Snapshot take() {
			GameState var0 = GameEventBus.getGameState();
			if (var0 == null || !var0.isLoggedIn()) {
				return null;
			}
			int[] var1 = new int[Skill.CAPACITY];
			for (int var2 = 0; var2 < Skill.CAPACITY; var2++) {
				var1[var2] = var0.getSkillExperience(var2);
			}
			PlayerInfo var3 = var0.getPlayer();
			return new Snapshot(var1, var0.getInventoryIds(), var0.getInventoryCounts(),
				var3 == null ? -1 : var3.animationId,
				var0.getWorldX(), var0.getWorldY());
		}

		/** The first skill whose experience went up, or -1. */
		int xpSkillChanged(Snapshot now) {
			for (int var2 = 0; var2 < this.xp.length && var2 < now.xp.length; var2++) {
				if (now.xp[var2] > this.xp[var2]) {
					return var2;
				}
			}
			return -1;
		}

		/** A description of the first inventory difference, or null if there is none. */
		String inventoryChange(Snapshot now) {
			if (this.invIds.length != now.invIds.length) {
				return "inventory size changed";
			}
			for (int var2 = 0; var2 < this.invIds.length; var2++) {
				if (this.invIds[var2] != now.invIds[var2]) {
					return "slot " + var2 + ": item " + this.invIds[var2]
						+ " -> " + now.invIds[var2];
				}
				if (this.invCounts[var2] != now.invCounts[var2]) {
					return "slot " + var2 + ": count " + this.invCounts[var2]
						+ " -> " + now.invCounts[var2];
				}
			}
			return null;
		}
	}
}
