package bwana.plan;

/**
 * The behaviour flow, independent of any particular activity.
 * <p>
 * These names describe a <i>shape</i> — find something, act on it, check what
 * happened, deal with a full inventory, come back — and nothing in the shape is
 * specific to chopping trees. Mining, fishing and thieving are the same flow with
 * different plan data, which is why the states are worth naming carefully once.
 */
public final class PlanState {

	public static final int IDLE = 0;

	/** Asking the targeting layer for something matching the plan. */
	public static final int FIND_TARGET = 1;

	/** Target chosen but out of reach; navigation is closing the distance. */
	public static final int TRAVEL_TO_TARGET = 2;

	/** Requesting the interaction. */
	public static final int PERFORM_ACTION = 3;

	/** Watching game state for evidence the interaction did something. */
	public static final int AWAIT_RESULT = 4;

	/** Deciding what happens next: carry on, bank, drop, or stop. */
	public static final int CHECK_CONDITIONS = 5;

	/** Eating, because hitpoints fell to the plan's threshold. */
	public static final int EAT = 13;

	/** At the bank, taking out more food. */
	public static final int WITHDRAW_FOOD = 14;

	/** Picking up what the last action left on the ground. */
	public static final int LOOT = 12;

	/** Acting on something in the inventory: burying, eating, dropping. */
	public static final int USE_ITEM = 11;

	public static final int TRAVEL_TO_BANK = 6;

	/** At the bank, doing the banking. */
	public static final int BANKING = 7;

	/** Walking back to where the activity was happening. */
	public static final int RETURN_TO_ACTIVITY = 8;

	public static final int DONE = 9;
	public static final int FAILED = 10;

	private PlanState() {
	}

	public static String name(int state) {
		if (state == FIND_TARGET) {
			return "FIND_TARGET";
		}
		if (state == TRAVEL_TO_TARGET) {
			return "TRAVEL_TO_TARGET";
		}
		if (state == PERFORM_ACTION) {
			return "PERFORM_ACTION";
		}
		if (state == AWAIT_RESULT) {
			return "AWAIT_RESULT";
		}
		if (state == CHECK_CONDITIONS) {
			return "CHECK_CONDITIONS";
		}
		if (state == EAT) {
			return "EAT";
		}
		if (state == WITHDRAW_FOOD) {
			return "WITHDRAW_FOOD";
		}
		if (state == LOOT) {
			return "LOOT";
		}
		if (state == USE_ITEM) {
			return "USE_ITEM";
		}
		if (state == TRAVEL_TO_BANK) {
			return "TRAVEL_TO_BANK";
		}
		if (state == BANKING) {
			return "BANKING";
		}
		if (state == RETURN_TO_ACTIVITY) {
			return "RETURN_TO_ACTIVITY";
		}
		if (state == DONE) {
			return "DONE";
		}
		if (state == FAILED) {
			return "FAILED";
		}
		return "IDLE";
	}

	public static boolean isFinished(int state) {
		return state == DONE || state == FAILED;
	}
}
