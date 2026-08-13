package bwana.plan;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.GameState;
import bwana.action.ActionRunner;
import bwana.action.ActionState;
import bwana.action.EntityAction;
import bwana.action.ItemAction;
import bwana.WorldQuery;
import bwana.inspect.EntityInfo;
import bwana.inspect.NameFilter;
import bwana.model.ItemInfo;
import bwana.model.PlayerInfo;
import bwana.nav.Traveller;
import bwana.script.ScriptLog;
import bwana.target.Selection;
import bwana.target.TargetCriteria;
import bwana.target.TargetTracker;

/**
 * Carries out a {@link Plan}, whatever it says.
 * <p>
 * The only place in the toolkit that knows the <i>shape</i> of an activity — find,
 * act, verify, handle a full inventory, come back, repeat — and it knows nothing
 * about any activity in particular. Every decision it makes reads a field of the
 * plan or a value from game state; none is written into the code.
 * <p>
 * It performs none of the work itself. Targets come from {@link TargetTracker},
 * interactions and their verification from {@link ActionRunner}, movement from
 * {@link Traveller}, and inventory from {@link GameState}. This class is only the
 * order those things happen in, which is exactly what a planner should be.
 * <p>
 * Banking and carried-item handling run through one mechanism, not two. Burying a
 * bone and depositing it differ only in which verb the client offers on the slot —
 * the item's own definition supplies "Bury" everywhere, and the bank interface adds
 * "Deposit-All" while it happens to be open. So nothing here knows what a bank is
 * beyond which word to look for.
 */
public final class PlanRunner extends GameEventsAdapter {

	private static final int TPS = 50;

	/** Give up looking for a target after this long with nothing found. */
	private static final int FIND_TIMEOUT = 20 * TPS;

	/** Not animating and no result for this long: the interaction did not take. */
	private static final int IDLE_TIMEOUT = 6 * TPS;

	/** Re-clicks on an unresponsive target before moving on to another. */
	private static final int MAX_RETRIES = 2;

	private static final int RESELECT_COOLDOWN = TPS / 2;

	/** An object offering no matching action never will; skip its whole type. */
	private static final long TYPE_EXCLUSION = 300000L;

	private static final long TRANSIENT_EXCLUSION = 45000L;

	private static final int IN_RANGE = 2;

	/** Ticks to wait for an item action to take effect before trying again. */
	private static final int ITEM_WAIT = TPS;

	/** Ticks between attempts to open the bank. */
	private static final int BANK_WAIT = 3 * TPS;

	private static final int BANK_TIMEOUT = 30 * TPS;

	/** Gap between deposit clicks, so the server can apply one before the next. */
	private static final int DEPOSIT_WAIT = TPS / 5;

	/** Gap between pickups, long enough to walk a tile or two to the stack. */
	private static final int LOOT_WAIT = 2 * TPS;

	private static final int LOOT_TIMEOUT = 15 * TPS;

	/** Gap after a bite, long enough for the heal to register. */
	private static final int EAT_WAIT = 3 * TPS;

	private static final int WITHDRAW_TIMEOUT = 20 * TPS;

	/** The overlay only has to keep up with a human reading it. */
	private static final int HUD_INTERVAL = 10;

	private final TargetTracker tracker = new TargetTracker();
	private final ActionRunner actions = new ActionRunner();
	private final Traveller traveller = new Traveller();
	private final ScriptLog log = new ScriptLog(true);

	private volatile Plan plan = new Plan();
	private volatile boolean running;
	private volatile int state = PlanState.IDLE;
	private volatile String note = "";

	// ---- progress ----
	private volatile int actionsDone;
	private volatile int trips;
	private volatile long startedAt;

	/** Where the activity was happening, so the plan can come back to it. */
	private volatile int anchorX = Integer.MIN_VALUE;
	private volatile int anchorZ;

	private int stateTicks;
	private int idleTicks;
	private int retries;
	private boolean counted;
	private boolean refusedForSpace;
	private boolean issued;
	private bwana.inspect.EntityHandle target;
	private boolean waitingForLogin;
	private int itemBusy;
	private int bankBusy;

	/** Cached deposit container for this bank visit; -1 when not yet located. */
	private int depositContainer = -1;

	/** Items acted on, for the readout. */
	private volatile int itemsHandled;

	/** Ground items picked up. */
	private volatile int lootTaken;

	/** The state just left, for the on-screen readout. */
	private volatile String lastAction = "";

	/** Food eaten, for the readout. */
	private volatile int mealsEaten;

	/** Set when a bank trip exists to restock food rather than to unload. */
	private boolean needFood;

	private int hudTicks;
	private int eatBusy;
	private int hpAtEat;
	private int foodAtEat;

	public PlanRunner() {
		// Order matters: the tracker publishes the selection, the action layer reads
		// it, and this reads both, so both must tick first.
		GameEventBus.addListener(this.tracker);
		GameEventBus.addListener(this.actions);
		GameEventBus.addListener(this);
	}

	// ---- read by the UI ----

	public boolean isRunning() {
		return this.running;
	}

	public int getState() {
		return this.state;
	}

	public String getStateName() {
		return PlanState.name(this.state);
	}

	public String getNote() {
		return this.note;
	}

	public int getActionsDone() {
		return this.actionsDone;
	}

	public int getTrips() {
		return this.trips;
	}

	public ScriptLog getLog() {
		return this.log;
	}

	public Plan getPlan() {
		return this.plan;
	}

	// ---- control ----

	public synchronized void start(Plan wanted) {
		if (this.running) {
			return;
		}
		this.plan = wanted.copy();
		this.actionsDone = 0;
		this.trips = 0;
		this.retries = 0;
		this.idleTicks = 0;
		this.counted = false;
		this.refusedForSpace = false;
		this.issued = false;
		this.target = null;
		this.anchorX = Integer.MIN_VALUE;
		this.waitingForLogin = false;
		this.itemBusy = 0;
		this.bankBusy = 0;
		this.depositContainer = -1;
		this.itemsHandled = 0;
		this.lootTaken = 0;
		this.mealsEaten = 0;
		this.needFood = false;
		this.eatBusy = 0;
		this.startedAt = System.currentTimeMillis();
		this.log.clear();
		this.log.add("PLAN " + this.plan.name);
		String[] var2 = this.plan.describe();
		for (int var3 = 0; var3 < var2.length; var3++) {
			this.log.add("  " + var2[var3]);
		}

		TargetCriteria var4 = this.tracker.getCriteria();
		var4.names = NameFilter.parse(this.plan.targetName);
		var4.kinds = this.plan.targetKinds;
		var4.maxDistance = this.plan.searchRange;
		// A target behind the camera is still a target; requiring it on screen would
		// reject perfectly good ones for a reason that only matters to a human.
		var4.requireOnScreen = false;
		var4.excluded.clear();

		// Watch for the bank from now on, so walking past one records it. Without
		// this the memory only filled from a lookup that had already succeeded.
		if (!this.plan.bank.isFixed()) {
			bwana.nav.LandmarkMemory.watch(this.plan.bank.name);
		}

		this.running = true;
		this.transition(PlanState.FIND_TARGET, "plan started");
	}

	public synchronized void stop() {
		if (this.running) {
			this.running = false;
			this.finish(PlanState.DONE, "stopped by hand");
		}
	}

	// ---- game thread ----

	public void onLogout() {
		if (this.running) {
			this.note = "waiting for login";
			this.waitingForLogin = true;
		}
	}

	public void onTick() {
		if (!this.running) {
			return;
		}
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			if (!this.waitingForLogin) {
				this.waitingForLogin = true;
				this.log.add("PAUSED  not logged in");
			}
			return;
		}
		if (this.waitingForLogin) {
			this.waitingForLogin = false;
			this.log.add("RESUMED logged in");
			this.stateTicks = 0;
		}
		this.stateTicks++;

		if (this.anchorX == Integer.MIN_VALUE) {
			// Wherever the plan began is the activity's home. Recorded rather than
			// configured: the user should not have to type in where they are standing.
			this.anchorX = var1.getWorldX();
			this.anchorZ = var1.getWorldY();
			this.log.add("ANCHOR activity at " + this.anchorX + ", " + this.anchorZ);
		}
		if (++this.hudTicks >= HUD_INTERVAL) {
			this.hudTicks = 0;
			PlanHud.update(this.plan, PlanState.name(this.state), this.note,
				this.lastAction, this.getHealthLines(), this.actionsDone,
				this.lootTaken, this.trips);
		}
		if (this.checkStopCondition()) {
			return;
		}

		if (this.state == PlanState.FIND_TARGET) {
			this.tickFind();
		} else if (this.state == PlanState.TRAVEL_TO_TARGET) {
			this.tickTravelToTarget(var1);
		} else if (this.state == PlanState.PERFORM_ACTION) {
			this.tickPerform();
		} else if (this.state == PlanState.AWAIT_RESULT) {
			this.tickAwait(var1);
		} else if (this.state == PlanState.CHECK_CONDITIONS) {
			this.tickCheck(var1);
		} else if (this.state == PlanState.EAT) {
			this.tickEat(var1);
		} else if (this.state == PlanState.WITHDRAW_FOOD) {
			this.tickWithdrawFood(var1);
		} else if (this.state == PlanState.LOOT) {
			this.tickLoot(var1);
		} else if (this.state == PlanState.USE_ITEM) {
			this.tickUseItem(var1);
		} else if (this.state == PlanState.TRAVEL_TO_BANK) {
			this.tickTravelToBank(var1);
		} else if (this.state == PlanState.BANKING) {
			this.tickBanking(var1);
		} else if (this.state == PlanState.RETURN_TO_ACTIVITY) {
			this.tickReturn(var1);
		}
	}

	// ---- the flow ----

	private void tickFind() {
		if (!this.tracker.isEnabled()) {
			this.tracker.setEnabled(true);
		}
		EntityInfo var1 = held();
		if (var1 != null) {
			this.target = var1.handle;
			this.issued = false;
			this.retries = 0;
			this.transition(PlanState.TRAVEL_TO_TARGET, var1.name + " at "
				+ var1.worldX + ", " + var1.worldY);
			return;
		}
		if (this.stateTicks > FIND_TIMEOUT) {
			this.running = false;
			this.finish(PlanState.FAILED, "no \"" + this.plan.targetName
				+ "\" within " + this.plan.searchRange + " tiles ("
				+ Selection.getNote() + ")");
		}
	}

	private void tickTravelToTarget(GameState state) {
		EntityInfo var2 = this.heldSame();
		if (var2 == null) {
			this.toFind("target lost while approaching");
			return;
		}
		// Requesting the interaction is also what commands the walk, so there is
		// nothing separate to send while approaching.
		this.transition(PlanState.PERFORM_ACTION, "in reach or walking");
	}

	private void tickPerform() {
		EntityInfo var1 = this.heldSame();
		if (var1 == null) {
			this.toFind("target gone before acting");
			return;
		}
		if (!this.issued && !this.requestAction()) {
			this.excludeType("offers no \"" + this.plan.actionName + "\" action");
			this.toFind("no matching action on " + var1.name);
			return;
		}
		this.counted = false;
		this.refusedForSpace = false;
		this.transition(PlanState.AWAIT_RESULT, "requested, now watching");
	}

	/**
	 * Wait while the game is visibly working.
	 * <p>
	 * Animation and rising action counts both come from game state, so this waits on
	 * evidence rather than on a timer. Clicking again while the player is mid-action
	 * restarts it, which is the classic way to make a script look busy and achieve
	 * nothing.
	 */
	private void tickAwait(GameState state) {
		EntityInfo var2 = this.heldSame();
		if (var2 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "target consumed");
			return;
		}
		// Once per execution. The old guard compared two counters and then made them
		// equal again, which re-opened it on the very next tick -- 782 "actions" in
		// sixty-nine seconds, all of them the same pick.
		if (!this.counted && this.actions.getState() == ActionState.SUCCESS) {
			this.counted = true;
			this.actionsDone++;
			this.log.add("ACTION #" + this.actionsDone + "  " + this.actions.getNote());
		}
		PlayerInfo var3 = state.getPlayer();
		if ((var3 != null && var3.isAnimating()) || this.counted) {
			this.idleTicks = 0;
			return;
		}
		if (this.actions.getState() == ActionState.FAILED) {
			this.refusedForSpace = mentionsSpace(this.actions.getEvidence());
			this.transition(PlanState.CHECK_CONDITIONS, "action failed: "
				+ this.actions.getNote());
			return;
		}
		if (++this.idleTicks > IDLE_TIMEOUT) {
			this.transition(PlanState.CHECK_CONDITIONS, "nothing happening");
		}
	}

	/** The branch point. Everything the plan can decide is decided here. */
	private void tickCheck(GameState state) {
		// Health outranks everything: the rest of the plan still makes sense in a
		// moment, and only if there is someone left to carry it out.
		if (this.checkHealth(state)) {
			return;
		}

		// Order by what makes room, not by what is urgent.
		//
		// The first version looted first on the grounds that drops expire. That is
		// true and still wrong: with a full inventory it kept trying to pick up a
		// raw chicken it had nowhere to put, forever. Space has to come before
		// acquisition, so anything disposable is dealt with first, and picking up
		// only happens when there is somewhere to put it.
		boolean var2 = this.isInventoryFull(state);

		// Full, but carrying something the plan knows how to get rid of: bury it.
		// Cheaper than a bank trip, and usually the entire point of carrying it.
		if (var2 && this.findItemAction(state) != null) {
			this.transition(PlanState.USE_ITEM, "full -- " + this.plan.itemAction
				+ " " + this.plan.itemName + " to make room");
			return;
		}
		// Loot only with room to spare. Drops do expire, but an unreachable pickup
		// costs the whole run rather than one item.
		if (!var2 && this.findLoot(state) != null) {
			this.transition(PlanState.LOOT, "something worth picking up");
			return;
		}
		// Not full: deal with carried items anyway, so the inventory never gets
		// there in the first place.
		if (this.findItemAction(state) != null) {
			this.transition(PlanState.USE_ITEM, this.plan.itemAction + " "
				+ this.plan.itemName);
			return;
		}
		if (var2) {
			if (this.plan.whenFull == Plan.FULL_STOP) {
				this.running = false;
				this.finish(PlanState.DONE, "inventory full, plan says stop");
				return;
			}
			if (this.plan.whenFull == Plan.FULL_BANK) {
				this.trips++;
				this.transition(PlanState.TRAVEL_TO_BANK, "inventory full, banking");
				return;
			}
			if (this.plan.whenFull == Plan.FULL_DROP) {
				this.running = false;
				this.finish(PlanState.FAILED, "plan says drop, but dropping needs the"
					+ " inventory interface and there is no widget layer yet");
				return;
			}
		}
		// Not full, or told to ignore it: carry on with the same target if it is
		// still there, otherwise find another.
		this.actions.clear();
		this.issued = false;
		if (this.heldSame() != null) {
			if (this.retries < MAX_RETRIES) {
				this.retries++;
				this.transition(PlanState.PERFORM_ACTION, "target still there");
				return;
			}
			this.excludeInstance("unresponsive after " + MAX_RETRIES + " tries");
		}
		this.toFind("looking for the next target");
	}

	private void tickTravelToBank(GameState state) {
		if (!this.traveller.hasDestination()) {
			int[] var2 = this.resolveBank(state);
			if (var2 == null) {
				this.running = false;
				this.finish(PlanState.FAILED, "cannot find the bank "
					+ this.plan.bank.describe() + " -- never seen one, so either walk past"
					+ " a bank once or give it as a coordinate");
				return;
			}
			this.traveller.setDestination(var2[0], var2[1], IN_RANGE + 1);
			this.log.add("BANK heading to " + var2[0] + ", " + var2[1]);
		}
		int var3 = this.traveller.tick(state);
		if (var3 == Traveller.ARRIVED) {
			this.traveller.clear();
			this.depositContainer = -1;
			this.itemBusy = 0;
			this.transition(PlanState.BANKING, "at the bank");
		} else if (var3 == Traveller.STUCK) {
			this.traveller.clear();
			this.running = false;
			this.finish(PlanState.FAILED, "could not reach the bank: "
				+ this.traveller.getNote());
		}
	}

	/**
	 * Pick up one thing, then look again.
	 * <p>
	 * Verified by the stack disappearing rather than by the click, same as every
	 * other interaction: an item out of reach would otherwise be clicked forever.
	 */
	private void tickLoot(GameState state) {
		if (this.isInventoryFull(state)) {
			// Checked here as well as before entering: an inventory can fill on the
			// pickup that got us here, and clicking at a full inventory is exactly
			// how the run got stuck on a raw chicken.
			this.transition(PlanState.CHECK_CONDITIONS, "inventory filled while looting");
			return;
		}
		EntityInfo var2 = this.findLoot(state);
		if (var2 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "nothing left to pick up");
			return;
		}
		if (this.itemBusy > 0) {
			this.itemBusy--;
			return;
		}
		if (this.stateTicks > LOOT_TIMEOUT) {
			this.transition(PlanState.CHECK_CONDITIONS, "gave up reaching the loot");
			return;
		}
		bwana.action.ActionExecutor var3 = GameEventBus.getActionExecutor();
		if (var3 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "no action executor");
			return;
		}
		EntityAction var4 = pickAction(var3.getActions(var2), "take");
		if (var4 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "no way to take " + var2.name);
			return;
		}
		String var5 = var3.execute(var4);
		if (var5 != null) {
			this.log.add("  loot refused: " + var5);
			this.transition(PlanState.CHECK_CONDITIONS, "could not take " + var2.name);
			return;
		}
		this.lootTaken++;
		this.log.add("LOOT #" + this.lootTaken + "  " + var4.name + " " + var2.name
			+ " at " + var2.worldX + ", " + var2.worldY);
		this.itemBusy = LOOT_WAIT;
	}

	/**
	 * The nearest ground item the plan wants, or null.
	 * <p>
	 * Ground items live in stacked lists the scene walks separately from scenery, so
	 * this goes through {@link bwana.WorldQuery} rather than the entity search.
	 */
	private EntityInfo findLoot(GameState state) {
		if (this.plan.lootNames == null || this.plan.lootNames.trim().length() == 0) {
			return null;
		}
		bwana.WorldQuery var2 = GameEventBus.getWorldQuery();
		if (var2 == null) {
			return null;
		}
		NameFilter var3 = NameFilter.parse(this.plan.lootNames);
		bwana.model.GroundItemInfo[] var4 = var2.getGroundItems();
		int var5 = Integer.MAX_VALUE;
		bwana.model.GroundItemInfo var6 = null;
		for (int var7 = 0; var7 < var4.length; var7++) {
			if (var4[var7].plane != state.getPlane()) {
				continue;
			}
			String var8 = var2.getItemName(var4[var7].id);
			if (var8 == null || !var3.matches(var8)) {
				continue;
			}
			int var9 = Math.max(Math.abs(var4[var7].worldX - state.getWorldX()),
				Math.abs(var4[var7].worldY - state.getWorldY()));
			if (var9 <= this.plan.lootRange && var9 < var5) {
				var5 = var9;
				var6 = var4[var7];
			}
		}
		if (var6 == null) {
			return null;
		}
		String var10 = var2.getItemName(var6.id);
		return new EntityInfo(EntityInfo.KIND_GROUND_ITEM, var6.id,
			var10 == null ? "item" : var10, "ground item",
			var6.worldX, var6.worldY, var6.plane, -1, -1,
			var6.worldX, var6.worldY, -1, -1, 0, 0, 0, 0, true, -1, -1, null);
	}

	public int getLootTaken() {
		return this.lootTaken;
	}

	/**
	 * Health comes before everything else the plan might be doing.
	 * <p>
	 * Not a priority judgement so much as an ordering one: looting, burying and
	 * banking all still make sense in a moment's time, and being alive is what makes
	 * them possible. Hitpoints are read from {@link GameState} every tick, never
	 * inferred from how long ago something hit you.
	 *
	 * @return true when the plan has been diverted and the caller should stop
	 */
	private boolean checkHealth(GameState state) {
		if (this.plan.hpThreshold <= 0) {
			return false;
		}
		int var2 = state.getSkillLevel(bwana.Skill.HITPOINTS);
		int var3 = state.getSkillBaseLevel(bwana.Skill.HITPOINTS);
		if (var3 <= 0 || var2 > var3 * this.plan.hpThreshold / 100) {
			return false;
		}
		if (this.countFood(state) > 0) {
			this.transition(PlanState.EAT, "hp " + var2 + "/" + var3 + " at or below "
				+ this.plan.hpThreshold + "%");
			return true;
		}
		// Out of food and hurt: the bank is the only place more comes from.
		this.needFood = true;
		this.transition(PlanState.TRAVEL_TO_BANK, "hp " + var2 + "/" + var3
			+ " and no " + this.plan.foodName + " left");
		return true;
	}

	/**
	 * Eat once, then re-check.
	 * <p>
	 * Verified by hitpoints rising or the food count falling — either proves the
	 * bite landed. Waiting on a timer would eat the whole inventory during the
	 * delay before the first heal registers.
	 */
	private void tickEat(GameState state) {
		int var2 = state.getSkillLevel(bwana.Skill.HITPOINTS);
		int var3 = this.countFood(state);
		if (this.eatBusy > 0) {
			this.eatBusy--;
			if (var2 > this.hpAtEat || var3 < this.foodAtEat) {
				this.eatBusy = 0;
				this.log.add("EAT ok -- hp " + this.hpAtEat + " -> " + var2
					+ ", " + this.plan.foodName + " " + this.foodAtEat + " -> " + var3);
				this.transition(PlanState.CHECK_CONDITIONS, "healed, resuming");
			}
			return;
		}
		if (var3 == 0) {
			this.needFood = true;
			this.transition(PlanState.TRAVEL_TO_BANK, "no " + this.plan.foodName + " left");
			return;
		}
		int var4 = state.getSkillBaseLevel(bwana.Skill.HITPOINTS);
		if (var4 > 0 && var2 > var4 * this.plan.hpThreshold / 100) {
			this.transition(PlanState.CHECK_CONDITIONS, "back above the threshold");
			return;
		}
		ItemAction var5 = this.findSlotAction(state, this.plan.foodName.toLowerCase(),
			"eat", false);
		if (var5 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "carrying " + this.plan.foodName
				+ " but nothing offers an Eat option");
			return;
		}
		bwana.action.ActionExecutor var6 = GameEventBus.getActionExecutor();
		if (var6 == null || var6.executeItem(var5) != null) {
			this.transition(PlanState.CHECK_CONDITIONS, "could not eat");
			return;
		}
		this.hpAtEat = var2;
		this.foodAtEat = var3;
		this.mealsEaten++;
		this.log.add("EAT #" + this.mealsEaten + "  " + var5.name + " at hp " + var2);
		this.eatBusy = EAT_WAIT;
	}

	/**
	 * Take food out of the bank, then leave.
	 * <p>
	 * Withdrawing is the same item-action mechanism as depositing, one interface
	 * away: the bank side offers "Withdraw" verbs where the inventory side offers
	 * "Deposit". Fixed amounts are combined rather than using Withdraw-X, which
	 * would need a number entry dialogue.
	 */
	private void tickWithdrawFood(GameState state) {
		int var2 = this.countFood(state);
		if (var2 >= this.plan.foodWithdraw) {
			this.needFood = false;
			this.log.add("FOOD have " + var2 + " " + this.plan.foodName);
			this.leaveBank("restocked");
			return;
		}
		if (this.itemBusy > 0) {
			this.itemBusy--;
			return;
		}
		if (this.stateTicks > WITHDRAW_TIMEOUT) {
			this.running = false;
			this.finish(PlanState.FAILED, "the bank has no " + this.plan.foodName
				+ " -- withdrew " + var2 + " of " + this.plan.foodWithdraw);
			return;
		}
		ItemAction var3 = this.findWithdrawAction(state, this.plan.foodWithdraw - var2);
		if (var3 == null) {
			this.running = false;
			this.finish(PlanState.FAILED, "no " + this.plan.foodName
				+ " in the bank, and none carried -- stopping rather than fighting on"
				+ " with nothing to eat");
			return;
		}
		bwana.action.ActionExecutor var4 = GameEventBus.getActionExecutor();
		if (var4 == null || var4.executeItem(var3) != null) {
			this.transition(PlanState.CHECK_CONDITIONS, "could not withdraw");
			return;
		}
		this.log.add("FOOD " + var3.name + " " + this.plan.foodName
			+ " (" + var2 + "/" + this.plan.foodWithdraw + ")");
		this.itemBusy = DEPOSIT_WAIT;
	}

	/** The largest withdraw verb that does not overshoot what is still wanted. */
	private ItemAction findWithdrawAction(GameState state, int wanted) {
		bwana.widget.WidgetSource var3 = GameEventBus.getWidgetSource();
		bwana.action.ActionExecutor var4 = GameEventBus.getActionExecutor();
		bwana.WorldQuery var5 = GameEventBus.getWorldQuery();
		if (var3 == null || var4 == null || var5 == null) {
			return null;
		}
		int[] var6 = var3.getContainersWithOption("withdraw");
		String var7 = this.plan.foodName.toLowerCase();
		for (int var8 = 0; var8 < var6.length; var8++) {
			int[] var9 = var3.getContainerIds(var6[var8]);
			for (int var10 = 0; var10 < var9.length; var10++) {
				if (var9[var10] < 0) {
					continue;
				}
				String var11 = var5.getItemName(var9[var10]);
				if (var11 == null || var11.toLowerCase().indexOf(var7) < 0) {
					continue;
				}
				ItemAction[] var12 = var4.getItemActions(var6[var8], var10);
				ItemAction var13 = null;
				int var14 = 0;
				for (int var15 = 0; var15 < var12.length; var15++) {
					if (var12[var15].name == null
							|| var12[var15].name.toLowerCase().indexOf("withdraw") < 0) {
						continue;
					}
					int var16 = amountOf(var12[var15].name);
					if (var16 > 0 && var16 <= wanted && var16 > var14) {
						var14 = var16;
						var13 = var12[var15];
					}
					if (var13 == null && var16 == 1) {
						var13 = var12[var15];
					}
				}
				if (var13 != null) {
					return var13;
				}
			}
		}
		return null;
	}

	/** The number in a verb like "Withdraw 10", or 0 when it carries none. */
	private static int amountOf(String verb) {
		int var1 = 0;
		boolean var2 = false;
		for (int var3 = 0; var3 < verb.length(); var3++) {
			char var4 = verb.charAt(var3);
			if (var4 >= '0' && var4 <= '9') {
				var1 = var1 * 10 + (var4 - '0');
				var2 = true;
			} else if (var2) {
				break;
			}
		}
		return var1;
	}

	/** How many of the plan's food are carried. */
	private int countFood(GameState state) {
		if (this.plan.foodName == null || this.plan.foodName.trim().length() == 0) {
			return 0;
		}
		bwana.WorldQuery var2 = GameEventBus.getWorldQuery();
		if (var2 == null) {
			return 0;
		}
		String var3 = this.plan.foodName.trim().toLowerCase();
		int[] var4 = state.getInventoryIds();
		int var5 = 0;
		for (int var6 = 0; var6 < var4.length; var6++) {
			if (var4[var6] < 0) {
				continue;
			}
			String var7 = var2.getItemName(var4[var6]);
			if (var7 != null && var7.toLowerCase().indexOf(var3) >= 0) {
				var5++;
			}
		}
		return var5;
	}

	/** Leave the bank the way the plan says to. */
	private void leaveBank(String why) {
		this.depositContainer = -1;
		if (this.plan.returnAfter) {
			this.transition(PlanState.RETURN_TO_ACTIVITY, why + ", heading back");
		} else {
			this.running = false;
			this.finish(PlanState.DONE, why + ", and the plan says stay");
		}
	}

	/**
	 * The health readout, as the debug panel shows it.
	 * <p>
	 * Built here rather than in the panel so the numbers are the same ones the
	 * decision was made from, rather than a second reading taken a moment later.
	 */
	public String[] getHealthLines() {
		GameState var1 = GameEventBus.getGameState();
		if (var1 == null || !var1.isLoggedIn()) {
			return new String[] { "HP: not logged in" };
		}
		int var2 = var1.getSkillLevel(bwana.Skill.HITPOINTS);
		int var3 = var1.getSkillBaseLevel(bwana.Skill.HITPOINTS);
		int var4 = var3 <= 0 ? 0 : var2 * 100 / var3;
		int var5 = this.countFood(var1);
		String var6;
		if (this.plan.hpThreshold <= 0) {
			var6 = "not watching hp";
		} else if (this.state == PlanState.EAT) {
			var6 = "Eat " + this.plan.foodName;
		} else if (this.state == PlanState.WITHDRAW_FOOD) {
			var6 = "Withdraw " + this.plan.foodName;
		} else if (var4 <= this.plan.hpThreshold) {
			var6 = var5 > 0 ? "eat next" : "no food -- bank next";
		} else {
			var6 = "No action";
		}
		return new String[] {
			"HP: " + var2 + "/" + var3 + " (" + var4 + "%)",
			"Food: " + this.plan.foodName + " x " + var5,
			"Threshold: " + this.plan.hpThreshold + "%",
			"Decision: " + var6,
			"Meals: " + this.mealsEaten
		};
	}

	/**
	 * Act on a carried item, then check whether there are more.
	 * <p>
	 * Verified by the inventory changing rather than by the click having happened —
	 * the same standard the action layer holds world interactions to. An item that
	 * refuses to go away would otherwise loop forever.
	 */
	private void tickUseItem(GameState state) {
		ItemAction var2 = this.findItemAction(state);
		if (var2 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "nothing left to "
				+ this.plan.itemAction.toLowerCase());
			return;
		}
		if (this.itemBusy > 0) {
			this.itemBusy--;
			// Wait for the slot to actually empty; acting again first would queue up
			// clicks the game silently discards.
			if (!var2.stillValid(state.getInventoryIds())) {
				this.itemsHandled++;
				this.itemBusy = 0;
				// A slot just freed up, so the "no room" refusal no longer holds.
				// Leaving it set would send the plan to the bank with space to spare.
				this.refusedForSpace = false;
				this.log.add("ITEM #" + this.itemsHandled + "  " + var2.describe() + " done");
			}
			return;
		}
		bwana.action.ActionExecutor var3 = GameEventBus.getActionExecutor();
		if (var3 == null) {
			this.transition(PlanState.CHECK_CONDITIONS, "no action executor");
			return;
		}
		String var4 = var3.executeItem(var2);
		if (var4 != null) {
			this.log.add("  item action refused: " + var4);
			this.transition(PlanState.CHECK_CONDITIONS, "could not " + var2.name);
			return;
		}
		this.itemBusy = ITEM_WAIT;
	}

	/**
	 * Bank: open it if it is shut, empty the inventory if it is open.
	 * <p>
	 * Depositing works through exactly the same item-action mechanism as burying.
	 * The verbs differ only because the client offers different ones while a bank is
	 * showing — "Deposit-All" is an option on the <i>interface</i>, not on the item —
	 * so nothing here has to know what a bank is beyond which words to look for.
	 */
	private void tickBanking(GameState state) {
		bwana.widget.WidgetSource var2 = GameEventBus.getWidgetSource();
		bwana.action.ActionExecutor var3 = GameEventBus.getActionExecutor();
		if (var2 == null || var3 == null) {
			this.running = false;
			this.finish(PlanState.FAILED, "no widget or action layer");
			return;
		}
		if (var2.getViewportInterfaceId() == bwana.widget.WidgetSource.NONE) {
			if (this.stateTicks > BANK_TIMEOUT) {
				this.running = false;
				this.finish(PlanState.FAILED, "the bank never opened");
				return;
			}
			// Open it through the ordinary entity-action path.
			if (this.bankBusy > 0) {
				this.bankBusy--;
				return;
			}
			this.bankBusy = BANK_WAIT;
			this.openBank(var3);
			return;
		}
		ItemAction var4 = this.findDepositAction(state);
		if (var4 == null) {
			// "No deposit verb" and "nothing left to deposit" are different things, and
			// treating them alike is how the plan reported a successful trip while
			// walking away with a full inventory.
			if (this.isInventoryFull(state)) {
				this.running = false;
				this.finish(PlanState.FAILED, "the bank is open but nothing offers a"
					+ " deposit option -- containers: "
					+ this.describeBankContainers());
				return;
			}
			this.trips++;
			this.refusedForSpace = false;
			this.depositContainer = -1;
			if (this.needFood || this.countFood(state) < this.plan.foodWithdraw
					&& this.plan.hpThreshold > 0) {
				this.transition(PlanState.WITHDRAW_FOOD, "restocking "
					+ this.plan.foodName);
				return;
			}
			this.log.add("BANK deposited everything (" + this.trips + " trips)");
			if (this.plan.returnAfter) {
				this.transition(PlanState.RETURN_TO_ACTIVITY, "heading back");
			} else {
				this.running = false;
				this.finish(PlanState.DONE, "banked, and the plan says stay");
			}
			return;
		}
		if (this.itemBusy > 0) {
			this.itemBusy--;
			return;
		}
		String var5 = var3.executeItem(var4);
		if (var5 != null) {
			this.log.add("  deposit refused: " + var5);
			this.depositContainer = -1;
			return;
		}
		this.log.add("  " + var4.name + " item " + var4.itemId);
		this.itemBusy = DEPOSIT_WAIT;
	}

	/** Find a bank and use it, through the same action path as any other object. */
	private void openBank(bwana.action.ActionExecutor executor) {
		bwana.inspect.EntityInspector var2 = GameEventBus.getInspector();
		if (var2 == null) {
			return;
		}
		TargetCriteria var3 = new TargetCriteria();
		var3.kinds = TargetCriteria.KIND_LOC;
		var3.names = NameFilter.parse(this.plan.bank.isFixed()
			? "bank" : this.plan.bank.name);
		var3.maxDistance = 12;
		var3.requireOnScreen = false;
		bwana.target.Candidate[] var4 = var2.findCandidates(var3, 8);
		for (int var5 = 0; var5 < var4.length; var5++) {
			if (!var4[var5].isEligible()) {
				continue;
			}
			EntityAction[] var6 = executor.getActions(var4[var5].entity);
			// The configured verb first. A booth's default left-click op is often not
			// the one that opens the bank, so matching "use" loosely picks the wrong
			// one -- which is exactly what broke the first attempt.
			EntityAction var7 = pickAction(var6, this.plan.bankAction);
			if (var7 == null) {
				var7 = pickAction(var6, "bank");
			}
			if (var7 == null) {
				this.log.add("  " + var4[var5].entity.name + " offers "
					+ describeActions(var6) + " -- none match \""
					+ this.plan.bankAction + "\"");
				continue;
			}
			executor.execute(var7);
			this.log.add("BANK opening " + var4[var5].entity.name + " via \""
				+ var7.name + "\"");
			return;
		}
	}

	/** First non-local action whose wording contains {@code verb}, or null. */
	private static EntityAction pickAction(EntityAction[] actions, String verb) {
		if (actions == null || verb == null || verb.trim().length() == 0) {
			return null;
		}
		String var2 = verb.trim().toLowerCase();
		for (int var3 = 0; var3 < actions.length; var3++) {
			if (!actions[var3].local && actions[var3].name != null
					&& actions[var3].name.toLowerCase().indexOf(var2) >= 0) {
				return actions[var3];
			}
		}
		return null;
	}

	private static String describeActions(EntityAction[] actions) {
		if (actions == null || actions.length == 0) {
			return "(nothing)";
		}
		StringBuilder var1 = new StringBuilder();
		for (int var2 = 0; var2 < actions.length; var2++) {
			var1.append(var2 == 0 ? "" : ", ").append(actions[var2].name);
		}
		return var1.toString();
	}

	private void tickReturn(GameState state) {
		if (!this.traveller.hasDestination()) {
			this.traveller.setDestination(this.anchorX, this.anchorZ, IN_RANGE + 1);
			this.log.add("RETURN heading to " + this.anchorX + ", " + this.anchorZ);
		}
		int var2 = this.traveller.tick(state);
		if (var2 == Traveller.ARRIVED) {
			this.traveller.clear();
			this.toFind("back at the activity");
		} else if (var2 == Traveller.STUCK) {
			this.traveller.clear();
			this.running = false;
			this.finish(PlanState.FAILED, "could not get back: " + this.traveller.getNote());
		}
	}

	// ---- conditions ----

	/**
	 * Whether there is no room for anything else.
	 * <p>
	 * Two signals, because neither alone is reliable. Counting free slots is the
	 * obvious one but depends on the slot array being exactly the inventory's size,
	 * which is the adapter's business and not something to bet on. The game itself
	 * saying "you don't have room" is unambiguous, comes straight from the server,
	 * and is what actually caught this: the plan kept picking flax into a full
	 * inventory because the count said there was space.
	 */
	private boolean isInventoryFull(GameState state) {
		if (this.refusedForSpace) {
			return true;
		}
		int[] var2 = state.getInventoryIds();
		if (var2.length == 0) {
			return false;
		}
		for (int var3 = 0; var3 < var2.length; var3++) {
			if (var2[var3] < 0) {
				return false;
			}
		}
		return true;
	}

	/** True if the game's own refusal was about carrying capacity. */
	private static boolean mentionsSpace(String[] evidence) {
		for (int var1 = 0; evidence != null && var1 < evidence.length; var1++) {
			if (evidence[var1] == null) {
				continue;
			}
			String var2 = evidence[var1].toLowerCase();
			if (var2.indexOf("room") >= 0 || var2.indexOf("space") >= 0
					|| var2.indexOf("carry") >= 0 || var2.indexOf("hands are full") >= 0) {
				return true;
			}
		}
		return false;
	}

	/** True when the plan has finished; sets the terminal state itself. */
	private boolean checkStopCondition() {
		if (this.plan.stopType == Plan.STOP_ACTIONS
				&& this.actionsDone >= this.plan.stopValue) {
			this.running = false;
			this.finish(PlanState.DONE, "reached " + this.plan.stopValue + " actions");
			return true;
		}
		if (this.plan.stopType == Plan.STOP_TRIPS && this.trips >= this.plan.stopValue) {
			this.running = false;
			this.finish(PlanState.DONE, "reached " + this.plan.stopValue + " bank trips");
			return true;
		}
		if (this.plan.stopType == Plan.STOP_MINUTES) {
			long var1 = (System.currentTimeMillis() - this.startedAt) / 60000L;
			if (var1 >= (long) this.plan.stopValue) {
				this.running = false;
				this.finish(PlanState.DONE, "ran for " + this.plan.stopValue + " minutes");
				return true;
			}
		}
		return false;
	}

	// ---- helpers, all through the shared systems ----

	private static EntityInfo held() {
		return Selection.getState() == Selection.STATE_FOUND ? Selection.getInfo() : null;
	}

	/** The selection, but only if it is still the entity this plan is working on. */
	private EntityInfo heldSame() {
		EntityInfo var1 = held();
		if (var1 == null || this.target == null || !this.target.matches(var1.handle)) {
			return null;
		}
		return var1;
	}

	private boolean requestAction() {
		EntityAction[] var1 = this.actions.getAvailable();
		String var2 = this.plan.actionName.toLowerCase();
		for (int var3 = 0; var3 < var1.length; var3++) {
			if (var1[var3].local || var1[var3].name == null) {
				continue;
			}
			if (var1[var3].name.toLowerCase().indexOf(var2) >= 0) {
				this.actions.select(var3);
				this.actions.requestExecute();
				this.issued = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Where to go to bank.
	 * <p>
	 * Three answers, in order of confidence: a coordinate the user typed, one in the
	 * loaded scene, or one remembered from a previous visit. The third is what makes
	 * banking work at all from a flax field — object searches only see the loaded
	 * scene, so without memory a bank fifty tiles away does not exist.
	 * <p>
	 * A scene hit is remembered on the way past, so using this is also what fills
	 * the memory in.
	 */
	private int[] resolveBank(GameState state) {
		if (this.plan.bank.isFixed()) {
			return new int[] { this.plan.bank.fixedX, this.plan.bank.fixedY };
		}
		bwana.inspect.EntityInspector var2 = GameEventBus.getInspector();
		if (var2 != null) {
			TargetCriteria var3 = new TargetCriteria();
			var3.kinds = TargetCriteria.KIND_LOC;
			var3.names = NameFilter.parse(this.plan.bank.name);
			var3.maxDistance = 25;
			var3.requireOnScreen = false;
			bwana.target.Candidate[] var4 = var2.findCandidates(var3, 8);
			for (int var5 = 0; var5 < var4.length; var5++) {
				if (var4[var5].isEligible()) {
					EntityInfo var6 = var4[var5].entity;
					bwana.nav.LandmarkMemory.remember(var6.name, var6.worldX, var6.worldY,
						var6.plane);
					return new int[] { var6.worldX, var6.worldY };
				}
			}
		}
		this.reportNearbyNames(var2);
		int[] var7 = bwana.nav.LandmarkMemory.nearest(this.plan.bank.name,
			state.getWorldX(), state.getWorldY(), state.getPlane());
		if (var7 != null) {
			this.log.add("BANK using remembered " + this.plan.bank.name + " at "
				+ var7[0] + ", " + var7[1]);
			return new int[] { var7[0], var7[1] };
		}
		return null;
	}

	/**
	 * A carried item matching the plan, and the verb the plan wants on it.
	 * <p>
	 * Names come from the cache through {@link WorldQuery}, and verbs from whatever
	 * the client offers on that slot — so this finds bones to bury without knowing
	 * bones exist. Returns null when the plan names no item, or none is carried.
	 */
	private ItemAction findItemAction(GameState state) {
		if (this.plan.itemName == null || this.plan.itemName.trim().length() == 0) {
			return null;
		}
		return this.findSlotAction(state, this.plan.itemName.toLowerCase(),
			this.plan.itemAction.toLowerCase(), false);
	}

	/**
	 * A deposit verb on anything still carried, searching the bank's own containers.
	 * <p>
	 * Not the inventory tab: an open bank shows its own copy of the inventory, a
	 * different component carrying different verbs. Reading the tab found no deposit
	 * option, which the caller then mistook for an empty inventory — so the plan
	 * opened the bank, deposited nothing, and walked back still full.
	 */
	private ItemAction findDepositAction(GameState state) {
		bwana.widget.WidgetSource var2 = GameEventBus.getWidgetSource();
		bwana.action.ActionExecutor var3 = GameEventBus.getActionExecutor();
		if (var2 == null || var3 == null) {
			return null;
		}
		// Find the deposit container once per visit, not once per tick. Locating it
		// means scanning every component in the interface tree and decoding an item
		// definition per occupied slot -- affordable occasionally, ruinous at 50 Hz,
		// and it was starving the game loop badly enough to make each deposit take
		// twenty seconds.
		if (this.depositContainer < 0) {
			int[] var4 = var2.getContainersWithOption("deposit");
			if (var4.length == 0) {
				return null;
			}
			this.depositContainer = var4[0];
		}
		int[] var5 = var2.getContainerIds(this.depositContainer);
		for (int var6 = 0; var6 < var5.length; var6++) {
			if (var5[var6] < 0) {
				continue;
			}
			ItemAction[] var7 = var3.getItemActions(this.depositContainer, var6);
			ItemAction var8 = pickDepositAll(var7);
			if (var8 != null) {
				return var8;
			}
		}
		return null;
	}

	/**
	 * Prefer the verb that empties the stack in one go.
	 * <p>
	 * Matched as "deposit" and "all" separately rather than as one string: the
	 * wording is "Deposit All" here but hyphenated in other clients, and looking for
	 * "deposit-all" quietly fell through to "Deposit 1" — which worked, one flax at
	 * a time.
	 */
	private static ItemAction pickDepositAll(ItemAction[] actions) {
		ItemAction var1 = null;
		for (int var2 = 0; actions != null && var2 < actions.length; var2++) {
			if (actions[var2].name == null) {
				continue;
			}
			String var3 = actions[var2].name.toLowerCase();
			if (var3.indexOf("deposit") < 0) {
				continue;
			}
			if (var3.indexOf("all") >= 0) {
				return actions[var2];
			}
			if (var1 == null) {
				var1 = actions[var2];
			}
		}
		return var1;
	}

	/**
	 * Scan the inventory for an item and a verb.
	 *
	 * @param itemMatch   substring the item name must contain, or null for any
	 * @param verbMatch   substring the action name must contain
	 * @param interfaceOp true to accept only verbs the open interface adds
	 */
	private ItemAction findSlotAction(GameState state, String itemMatch, String verbMatch,
			boolean interfaceOp) {
		bwana.action.ActionExecutor var5 = GameEventBus.getActionExecutor();
		WorldQuery var6 = GameEventBus.getWorldQuery();
		if (var5 == null) {
			return null;
		}
		int[] var7 = state.getInventoryIds();
		for (int var8 = 0; var8 < var7.length; var8++) {
			if (var7[var8] < 0) {
				continue;
			}
			if (itemMatch != null) {
				String var9 = var6 == null ? null : var6.getItemName(var7[var8]);
				if (var9 == null || var9.toLowerCase().indexOf(itemMatch) < 0) {
					continue;
				}
			}
			ItemAction[] var10 = var5.getItemActions(-1, var8);
			for (int var11 = 0; var11 < var10.length; var11++) {
				if (var10[var11].fromInterface != interfaceOp || var10[var11].name == null) {
					continue;
				}
				if (var10[var11].name.toLowerCase().indexOf(verbMatch) >= 0) {
					return var10[var11];
				}
			}
		}
		return null;
	}

	public int getItemsHandled() {
		return this.itemsHandled;
	}

	/**
	 * List the named scenery in range when a landmark lookup fails.
	 * <p>
	 * "Cannot find the bank" is a dead end; "cannot find it, but there is a Bank
	 * booth right there" names the fix. The scene already knows, so guessing at the
	 * spelling was never necessary.
	 */
	private void reportNearbyNames(bwana.inspect.EntityInspector inspector) {
		if (inspector == null) {
			return;
		}
		TargetCriteria var2 = new TargetCriteria();
		var2.kinds = TargetCriteria.KIND_LOC;
		var2.names = NameFilter.EMPTY;
		var2.maxDistance = 12;
		var2.requireOnScreen = false;
		bwana.target.Candidate[] var3 = inspector.findCandidates(var2, 40);
		StringBuilder var4 = new StringBuilder();
		int var5 = 0;
		for (int var6 = 0; var6 < var3.length && var5 < 12; var6++) {
			String var7 = var3[var6].entity.name;
			if (var7 == null || var4.indexOf(var7) >= 0) {
				continue;
			}
			var4.append(var5 == 0 ? "" : ", ").append(var7);
			var5++;
		}
		this.log.add("  nearby scenery: " + (var5 == 0 ? "(none named)" : var4.toString()));
	}

	/** What the open interface actually contains, for when depositing finds nothing. */
	private String describeBankContainers() {
		bwana.widget.WidgetSource var1 = GameEventBus.getWidgetSource();
		bwana.action.ActionExecutor var2 = GameEventBus.getActionExecutor();
		if (var1 == null || var2 == null) {
			return "(no widget layer)";
		}
		int[] var3 = var1.getContainersWithOption("deposit");
		if (var3.length == 0) {
			var3 = var1.getContainersUnder(var1.getViewportInterfaceId());
		}
		if (var3.length == 0) {
			return "(interface " + var1.getViewportInterfaceId() + " has no containers)";
		}
		StringBuilder var4 = new StringBuilder();
		for (int var5 = 0; var5 < var3.length && var5 < 4; var5++) {
			int[] var6 = var1.getContainerIds(var3[var5]);
			var4.append(var5 == 0 ? "" : "; ").append(var3[var5]).append(" holds ")
				.append(var6.length).append(" slots");
			for (int var7 = 0; var7 < var6.length; var7++) {
				if (var6[var7] >= 0) {
					ItemAction[] var8 = var2.getItemActions(var3[var5], var7);
					var4.append(" offering ");
					for (int var9 = 0; var9 < var8.length; var9++) {
						var4.append(var9 == 0 ? "" : "/").append(var8[var9].name);
					}
					break;
				}
			}
		}
		return var4.toString();
	}

	private void excludeType(String why) {
		if (this.target != null) {
			this.tracker.getCriteria().excluded.addType(this.target.getKind(),
				this.target.getTypeId(), TYPE_EXCLUSION);
			this.log.add("  EXCLUDE all of type " + this.target.getTypeId() + ": " + why);
		}
	}

	private void excludeInstance(String why) {
		if (this.target != null) {
			this.tracker.getCriteria().excluded.add(this.target, TRANSIENT_EXCLUSION);
			this.log.add("  EXCLUDE " + this.target.describe() + ": " + why);
		}
	}

	private void toFind(String why) {
		this.actions.clear();
		this.tracker.reset();
		this.target = null;
		this.issued = false;
		this.retries = 0;
		this.idleTicks = 0;
		this.transition(PlanState.FIND_TARGET, why);
	}

	private void transition(int next, String why) {
		// Remember where we came from before overwriting it: an overlay showing only
		// the current state reads as though nothing ever happens, because a plan
		// spends most of its time waiting.
		if (this.state != next) {
			this.lastAction = PlanState.name(this.state)
				+ (this.note == null || this.note.length() == 0 ? "" : " (" + this.note + ")");
		}
		int var3 = this.state;
		this.state = next;
		this.note = why == null ? "" : why;
		this.stateTicks = 0;
		this.idleTicks = 0;
		this.log.add(PlanState.name(var3) + " -> " + PlanState.name(next)
			+ (why == null || why.length() == 0 ? "" : "   " + why));
	}

	private void finish(int result, String why) {
		PlanHud.clear();
		this.tracker.setEnabled(false);
		this.actions.clear();
		this.traveller.clear();
		this.transition(result, why);
		long var3 = (System.currentTimeMillis() - this.startedAt) / 1000L;
		this.log.add("SUMMARY " + this.actionsDone + " actions, " + this.trips
			+ " bank trips, " + (var3 / 60L) + "m " + (var3 % 60L) + "s");
	}
}
