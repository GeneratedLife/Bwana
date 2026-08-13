package bwana.action;

/**
 * Where an action is in its life cycle.
 * <p>
 * The split that matters is {@link #EXECUTING} versus {@link #WAITING}. Executing
 * means the interaction has been handed to the client; waiting means the packet is
 * gone and we are watching the game for evidence that anything came of it. Nothing
 * reaches {@link #SUCCESS} on the strength of having sent something — sending is
 * the easy half, and a request the server ignores looks identical at that point.
 */
public final class ActionState {

	/** Nothing selected to act on. */
	public static final int NONE = 0;

	/** An action is selected and could be executed now. */
	public static final int READY = 1;

	/** Handing the interaction to the client this tick. */
	public static final int EXECUTING = 2;

	/** Sent; watching game state for confirmation. */
	public static final int WAITING = 3;

	/** The game changed in a way consistent with the action happening. */
	public static final int SUCCESS = 4;

	/** Rejected before sending, refused by the game, or nothing happened in time. */
	public static final int FAILED = 5;

	private ActionState() {
	}

	public static String name(int state) {
		if (state == READY) {
			return "READY";
		}
		if (state == EXECUTING) {
			return "EXECUTING";
		}
		if (state == WAITING) {
			return "WAITING";
		}
		if (state == SUCCESS) {
			return "SUCCESS";
		}
		if (state == FAILED) {
			return "FAILED";
		}
		return "NONE";
	}

	public static boolean isTerminal(int state) {
		return state == SUCCESS || state == FAILED;
	}
}
