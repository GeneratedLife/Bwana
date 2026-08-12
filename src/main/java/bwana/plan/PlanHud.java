package bwana.plan;

/**
 * The plan's on-screen readout.
 * <p>
 * Lines are composed on the game thread and the render hook only draws them, which
 * is the same arrangement the target overlay uses: the wording can change without
 * touching the client, and the drawing code stays a loop over strings.
 * <p>
 * Shows what it is doing <i>now</i>, what it did <i>last</i>, and what it is
 * working toward. The last action matters more than it looks — most of the time a
 * plan is waiting, and without it the overlay reads as though nothing is happening.
 * <p>
 * Strictly a view. Nothing here influences the plan.
 */
public final class PlanHud {

	public static final int COLOUR_TITLE = 0xFF8A1E;
	public static final int COLOUR_TEXT = 0xFFB870;
	public static final int COLOUR_DIM = 0xC08040;
	public static final int COLOUR_GOOD = 0x8AFF6A;
	public static final int COLOUR_BAD = 0xFF6A6A;

	private static final String[] NO_LINES = new String[0];
	private static final int[] NO_COLOURS = new int[0];

	private static volatile boolean enabled = true;
	private static volatile String[] lines = NO_LINES;
	private static volatile int[] colours = NO_COLOURS;

	private PlanHud() {
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean on) {
		enabled = on;
		if (!on) {
			lines = NO_LINES;
			colours = NO_COLOURS;
		}
	}

	public static String[] getLines() {
		return lines;
	}

	public static int[] getColours() {
		return colours;
	}

	public static void clear() {
		lines = NO_LINES;
		colours = NO_COLOURS;
	}

	/** Rebuild the readout. Called from the runner, on the game thread. */
	public static void update(Plan plan, String state, String note, String last,
			String[] health, int actions, int loot, int trips) {
		if (!enabled) {
			return;
		}
		String[] var8 = new String[24];
		int[] var9 = new int[24];
		int var10 = 0;

		var8[var10] = "BWANA -- " + plan.name;
		var9[var10++] = COLOUR_TITLE;
		var8[var10] = "Now:  " + state;
		var9[var10++] = COLOUR_TEXT;
		if (note != null && note.length() > 0) {
			var8[var10] = "      " + note;
			var9[var10++] = COLOUR_DIM;
		}
		var8[var10] = "Last: " + (last == null || last.length() == 0 ? "-" : last);
		var9[var10++] = COLOUR_DIM;

		var8[var10] = "";
		var9[var10++] = COLOUR_DIM;
		var8[var10] = "PRIORITIES";
		var9[var10++] = COLOUR_TITLE;
		// In the order the runner actually considers them, so the overlay explains
		// the behaviour rather than just listing the settings.
		if (plan.hpThreshold > 0) {
			var8[var10] = "1. eat " + plan.foodName + " at " + plan.hpThreshold + "%";
			var9[var10++] = COLOUR_TEXT;
		}
		if (plan.lootNames.length() > 0) {
			var8[var10] = "2. loot " + plan.lootNames;
			var9[var10++] = COLOUR_TEXT;
		}
		if (plan.itemName.length() > 0) {
			var8[var10] = "3. " + plan.itemAction + " " + plan.itemName;
			var9[var10++] = COLOUR_TEXT;
		}
		var8[var10] = "4. when full: " + Plan.describeFull(plan.whenFull);
		var9[var10++] = COLOUR_TEXT;
		var8[var10] = "Target: " + plan.actionName + " " + plan.targetName;
		var9[var10++] = COLOUR_TITLE;

		if (health != null) {
			var8[var10] = "";
			var9[var10++] = COLOUR_DIM;
			for (int var11 = 0; var11 < health.length && var10 < 22; var11++) {
				var8[var10] = health[var11];
				var9[var10++] = COLOUR_TEXT;
			}
		}

		var8[var10] = "";
		var9[var10++] = COLOUR_DIM;
		var8[var10] = actions + " actions, " + loot + " looted, " + trips + " trips";
		var9[var10++] = COLOUR_GOOD;

		String[] var12 = new String[var10];
		int[] var13 = new int[var10];
		System.arraycopy(var8, 0, var12, 0, var10);
		System.arraycopy(var9, 0, var13, 0, var10);
		lines = var12;
		colours = var13;
	}
}
