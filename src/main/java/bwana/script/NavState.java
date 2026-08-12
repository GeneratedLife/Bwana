package bwana.script;

/** Stages of the navigation validation run. */
public final class NavState {

	public static final int IDLE = 0;

	/** Reading the player's own position and checking it is self-consistent. */
	public static final int CONFIRM_POSITION = 1;

	public static final int RESOLVE_START = 2;
	public static final int RESOLVE_DEST = 3;

	/** Getting to the start landmark, so the measured leg begins where it should. */
	public static final int TRAVEL_TO_START = 4;

	/** The measured leg. */
	public static final int TRAVEL_TO_DEST = 5;

	/** Arrived; comparing what was predicted against what happened. */
	public static final int VERIFY = 6;

	public static final int PASSED = 7;
	public static final int FAILED = 8;

	private NavState() {
	}

	public static String name(int state) {
		if (state == CONFIRM_POSITION) {
			return "CONFIRM_POSITION";
		}
		if (state == RESOLVE_START) {
			return "RESOLVE_START";
		}
		if (state == RESOLVE_DEST) {
			return "RESOLVE_DEST";
		}
		if (state == TRAVEL_TO_START) {
			return "TRAVEL_TO_START";
		}
		if (state == TRAVEL_TO_DEST) {
			return "TRAVEL_TO_DEST";
		}
		if (state == VERIFY) {
			return "VERIFY";
		}
		if (state == PASSED) {
			return "PASSED";
		}
		if (state == FAILED) {
			return "FAILED";
		}
		return "IDLE";
	}

	public static boolean isFinished(int state) {
		return state == PASSED || state == FAILED;
	}
}
