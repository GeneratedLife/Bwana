package bwana.plan;

import bwana.script.Landmark;

/**
 * What the user wants done. Not how.
 * <p>
 * Deliberately inert — fields and a name, no logic. Everything about carrying it
 * out lives in {@link PlanRunner}, and everything about finding, walking to and
 * interacting with things lives in the systems that already do those jobs. The
 * split is the whole point: a plan is a description, so a new activity is new
 * <i>data</i> rather than a new script.
 * <p>
 * Woodcutting is simply the first plan anyone happens to write. Nothing here
 * mentions trees, logs or axes, and if it ever needs to, the abstraction has
 * failed.
 * <p>
 * The vocabulary is small on purpose. Each field answers one question the user was
 * actually asked:
 * <ul>
 * <li><b>What</b> to act on — {@link #targetName}, {@link #targetKinds}</li>
 * <li><b>What to do</b> to it — {@link #actionName}</li>
 * <li><b>What happens when the inventory fills</b> — {@link #whenFull}</li>
 * <li><b>Where to go</b> for that — {@link #bank}</li>
 * <li><b>Whether to come back</b> — {@link #returnAfter}</li>
 * <li><b>When to stop</b> — {@link #stopType}, {@link #stopValue}</li>
 * </ul>
 */
public final class Plan {

	// ---- what to do when the inventory fills ----
	public static final int FULL_IGNORE = 0;
	public static final int FULL_BANK = 1;
	public static final int FULL_DROP = 2;
	public static final int FULL_STOP = 3;

	// ---- when to stop ----
	public static final int STOP_MANUAL = 0;
	public static final int STOP_ACTIONS = 1;
	public static final int STOP_MINUTES = 2;
	public static final int STOP_TRIPS = 3;

	public String name = "unnamed";

	/** Matched against entity names the same way every other search matches them. */
	public String targetName = "Tree";

	/** Bitmask of {@link bwana.target.TargetCriteria} kinds. */
	public int targetKinds = bwana.target.TargetCriteria.KIND_LOC;

	/** Matched against the interactions the client itself offers on the target. */
	public String actionName = "Chop";

	/** How far from the activity anchor targets may be chosen. */
	public int searchRange = 15;

	/**
	 * An item to deal with whenever it appears, and what to do to it.
	 * <p>
	 * Generic on purpose. "Bury Bones" is the same shape as "Eat Trout" or "Drop
	 * Iron ore": find a matching item, take a verb the client offers on it, do that.
	 * Nothing here knows what bones are, and a separate burying mode would have been
	 * the first crack in the abstraction.
	 * <p>
	 * Blank means the plan ignores its inventory contents entirely.
	 */
	/**
	 * Ground items worth picking up, comma separated. Blank means loot nothing.
	 * <p>
	 * A scope rather than a switch: "loot everything" fills an inventory with rubbish
	 * and "loot nothing" wastes the kill, so what matters is naming the few things
	 * actually wanted. Matched as substrings like every other name in a plan, so
	 * "Bones, Feather" covers both without needing exact spellings.
	 */
	/**
	 * Eat when hitpoints fall to this percentage or below. 0 disables the check.
	 * <p>
	 * A percentage rather than a number of hitpoints, so the same plan stays sensible
	 * as the character levels up.
	 */
	public int hpThreshold;

	/** Food to eat and to withdraw. Matched as a substring like every other name. */
	public String foodName = "Lobster";

	/** How many to take out of the bank on a restock trip. */
	public int foodWithdraw = 10;

	public String lootNames = "";

	/** How far from the player to look for loot. Kills drop close by. */
	public int lootRange = 6;

	public String itemName = "";

	public String itemAction = "Bury";

	public int whenFull = FULL_BANK;

	/**
	 * Where banking happens. Only consulted when {@link #whenFull} is FULL_BANK.
	 * <p>
	 * Just "Bank" by default rather than "Bank booth": names vary between towns —
	 * booth, chest, stall — and a substring matches all of them, where the longer
	 * name silently matches none.
	 */
	public Landmark bank = Landmark.named("Bank");

	/**
	 * Which verb to use on the bank.
	 * <p>
	 * A field rather than a constant because the right one is not obvious and varies:
	 * a booth's default left-click op is often not the one that opens the bank, so
	 * naming it here beats hard-coding a guess. Same matching as every other action.
	 */
	public String bankAction = "Use-quickly";

	/**
	 * Whether to walk back and carry on after dealing with a full inventory.
	 * <p>
	 * The anchor is wherever the activity was happening, recorded when the plan
	 * starts rather than configured — the user should not have to type in the place
	 * they are standing.
	 */
	public boolean returnAfter = true;

	public int stopType = STOP_MANUAL;

	/** Meaning depends on {@link #stopType}; ignored for STOP_MANUAL. */
	public int stopValue = 100;

	public static String describeFull(int whenFull) {
		if (whenFull == FULL_BANK) {
			return "bank it";
		}
		if (whenFull == FULL_DROP) {
			return "drop everything";
		}
		if (whenFull == FULL_STOP) {
			return "stop";
		}
		return "keep going";
	}

	public static String describeStop(int stopType, int value) {
		if (stopType == STOP_ACTIONS) {
			return "after " + value + " successful actions";
		}
		if (stopType == STOP_MINUTES) {
			return "after " + value + " minutes";
		}
		if (stopType == STOP_TRIPS) {
			return "after " + value + " bank trips";
		}
		return "manually";
	}

	/** The plan in a few lines, for the debug readout. */
	public String[] describe() {
		return new String[] {
			"GOAL      " + this.actionName + " " + this.targetName
				+ "  (within " + this.searchRange + " tiles)",
			"HEALTH    " + (this.hpThreshold <= 0 ? "(not watched)"
				: "eat " + this.foodName + " at or below " + this.hpThreshold
					+ "%, withdraw " + this.foodWithdraw),
			"LOOT      " + (this.lootNames.length() == 0 ? "(nothing)"
				: this.lootNames + " within " + this.lootRange + " tiles"),
			"ON ITEM   " + (this.itemName.length() == 0 ? "(nothing)"
				: this.itemAction + " any " + this.itemName + " on sight"),
			"WHEN FULL " + describeFull(this.whenFull)
				+ (this.whenFull == FULL_BANK ? " at " + this.bank.describe() : ""),
			"AFTER     " + (this.returnAfter ? "return and resume" : "stay where you are"),
			"STOP      " + describeStop(this.stopType, this.stopValue)
		};
	}

	public Plan copy() {
		Plan var1 = new Plan();
		var1.name = this.name;
		var1.targetName = this.targetName;
		var1.targetKinds = this.targetKinds;
		var1.actionName = this.actionName;
		var1.searchRange = this.searchRange;
		var1.hpThreshold = this.hpThreshold;
		var1.foodName = this.foodName;
		var1.foodWithdraw = this.foodWithdraw;
		var1.lootNames = this.lootNames;
		var1.lootRange = this.lootRange;
		var1.itemName = this.itemName;
		var1.itemAction = this.itemAction;
		var1.whenFull = this.whenFull;
		var1.bank = this.bank;
		var1.bankAction = this.bankAction;
		var1.returnAfter = this.returnAfter;
		var1.stopType = this.stopType;
		var1.stopValue = this.stopValue;
		return var1;
	}
}
