package bwana.script;

/**
 * The states a task script moves through.
 * <p>
 * Named after what the script is <i>waiting for</i>, not what it is doing, because
 * a state machine driven by a game is mostly waiting. {@link #CHOP} is not "swing
 * an axe" — it is "the interaction has been requested and we are within range of
 * it working".
 */
public final class ScriptState {

	/** Stopped. Nothing is enabled and no target is held. */
	public static final int IDLE = 0;

	/** Asking the targeting layer for a tree and waiting for it to lock one. */
	public static final int FIND_TREE = 1;

	/** Target locked, out of range, closing the distance. */
	public static final int WALK_TO_TREE = 2;

	/** In range; the interaction has been requested. */
	public static final int CHOP = 3;

	/** Interaction sent; the action layer is watching game state for a result. */
	public static final int WAIT_FOR_RESULT = 4;

	/** A result arrived; deciding what it means. */
	public static final int VERIFY = 5;

	/** The target is gone, depleted or unreachable; drop it and find another. */
	public static final int RESELECT = 6;

	/** Ran to completion. */
	public static final int DONE = 7;

	private ScriptState() {
	}

	public static String name(int state) {
		if (state == FIND_TREE) {
			return "FIND_TREE";
		}
		if (state == WALK_TO_TREE) {
			return "WALK_TO_TREE";
		}
		if (state == CHOP) {
			return "CHOP";
		}
		if (state == WAIT_FOR_RESULT) {
			return "WAIT_FOR_RESULT";
		}
		if (state == VERIFY) {
			return "VERIFY";
		}
		if (state == RESELECT) {
			return "RESELECT";
		}
		if (state == DONE) {
			return "DONE";
		}
		return "IDLE";
	}
}
