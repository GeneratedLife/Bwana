package bwana.target;

import bwana.inspect.EntityInfo;

/**
 * The on-screen target readout.
 * <p>
 * Lines are composed here, on the game thread, and the render hook only draws
 * them. Keeping the formatting out of the client means the drawing code stays a
 * loop over strings, and the wording can change without touching the game.
 * <p>
 * Strictly a view. Nothing here influences what gets selected.
 */
public final class TargetHud {

	public static final int COLOUR_LABEL = 0xFFFFFF;
	public static final int COLOUR_GOOD = 0x66FF66;
	public static final int COLOUR_BAD = 0xFF6666;
	public static final int COLOUR_DIM = 0xAAAAAA;
	public static final int COLOUR_PICK = 0xFFCC00;

	/**
	 * Candidates to list on screen.
	 * <p>
	 * Independent of how many the search considers, which is much larger. Conflating
	 * the two is what once limited the search to a readable dozen and let a screenful
	 * of tree stumps hide every actual tree.
	 */
	private static final int MAX_LISTED = 10;

	private static final String[] NO_LINES = new String[0];
	private static final int[] NO_COLOURS = new int[0];

	private static volatile boolean enabled;
	private static volatile String[] lines = NO_LINES;
	private static volatile int[] colours = NO_COLOURS;

	private TargetHud() {
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

	/** Rebuild the readout. Called from the tracker, on the game thread. */
	public static void update(TargetCriteria criteria, Candidate[] candidates) {
		if (!enabled) {
			return;
		}
		// Headroom over the worst case: 8 target lines, 5 identity, reason, range,
		// the header, MAX_LISTED candidates and the "more considered" tail.
		String[] var2 = new String[48];
		int[] var3 = new int[48];
		int var4 = 0;

		EntityInfo var5 = Selection.getInfo();
		boolean var6 = Selection.getState() == Selection.STATE_FOUND && var5 != null;

		if (var6) {
			var2[var4] = "TARGET: " + var5.name + (var5.id >= 0 ? " #" + var5.id : "");
			var3[var4++] = COLOUR_PICK;
			var2[var4] = "Type: " + var5.getKindName();
			var3[var4++] = COLOUR_LABEL;
			var2[var4] = "World: " + var5.worldX + ", " + var5.worldY + "  plane " + var5.plane;
			var3[var4++] = COLOUR_LABEL;
			var2[var4] = "Tile: " + var5.tileX + ", " + var5.tileZ;
			var3[var4++] = COLOUR_LABEL;
			var2[var4] = "Distance: " + distanceOf(candidates, var5) + " tiles";
			var3[var4++] = COLOUR_LABEL;
			boolean var7 = var5.visible && var5.screenX >= 0;
			var2[var4] = "Visible: " + (var7 ? "YES" : "NO");
			var3[var4++] = var7 ? COLOUR_GOOD : COLOUR_BAD;
			var2[var4] = "Locked: YES";
			var3[var4++] = COLOUR_GOOD;
			var2[var4] = "Valid: YES";
			var3[var4++] = COLOUR_GOOD;

			// Identity, so the lock is falsifiable from the game window alone. A
			// target silently swapped for another of the same name looks identical
			// to a target held correctly unless you can see what it is keyed on.
			bwana.inspect.EntityHandle var13 = Selection.getHandle();
			if (var13 != null) {
				var2[var4] = "ID: " + var13.describe();
				var3[var4++] = COLOUR_LABEL;
				var2[var4] = "Keyed on: " + var13.describeRule();
				var3[var4++] = COLOUR_DIM;
			}
			var2[var4] = "Held: " + Selection.getResolveCount() + " resolves";
			var3[var4++] = COLOUR_DIM;
			int var14 = Selection.getSlotChangeCount();
			if (var14 > 0) {
				var2[var4] = "Survived " + var14 + " slot change" + (var14 == 1 ? "" : "s");
				var3[var4++] = COLOUR_GOOD;
			}
			if (Selection.getReacquireCount() > 0) {
				var2[var4] = "Reacquired " + Selection.getReacquireCount() + "x after loss";
				var3[var4++] = COLOUR_GOOD;
			}
		} else {
			var2[var4] = "TARGET: none";
			var3[var4++] = COLOUR_BAD;
			var2[var4] = "State: " + Selection.getStateName();
			var3[var4++] = Selection.getState() == Selection.STATE_LOST ? COLOUR_BAD : COLOUR_DIM;
			var2[var4] = "Locked: NO";
			var3[var4++] = COLOUR_DIM;
		}

		String var8 = Selection.getNote();
		if (var8 != null && var8.length() > 0) {
			var2[var4] = "Reason: " + var8;
			var3[var4++] = COLOUR_BAD;
		}
		var2[var4] = "Search range: " + criteria.maxDistance + " tiles";
		var3[var4++] = COLOUR_DIM;

		var2[var4] = "CANDIDATES:";
		var3[var4++] = COLOUR_LABEL;
		if (candidates == null || candidates.length == 0) {
			var2[var4] = "  (none matched the name)";
			var3[var4++] = COLOUR_DIM;
		}
		int var14 = candidates == null ? 0 : candidates.length;
		for (int var9 = 0; var9 < var14 && var9 < MAX_LISTED && var4 < 40; var9++) {
			Candidate var10 = candidates[var9];
			StringBuilder var11 = new StringBuilder();
			var11.append(var10.selected ? "> " : "  ");
			var11.append(var10.entity.name);
			if (var10.entity.id >= 0) {
				var11.append(" #").append(var10.entity.id);
			}
			var11.append(" - ").append(var10.distance).append(" tiles");
			if (var10.selected) {
				var11.append(" - SELECTED");
			} else if (var10.reason != null) {
				var11.append(" - ").append(var10.reason);
			}
			var2[var4] = var11.toString();
			var3[var4++] = var10.selected ? COLOUR_PICK
				: (var10.reason == null ? COLOUR_LABEL : COLOUR_DIM);
		}
		// Say how many were considered but not shown, so a short list is never
		// mistaken for a short search.
		if (var14 > MAX_LISTED) {
			var2[var4] = "  ...and " + (var14 - MAX_LISTED) + " more considered";
			var3[var4++] = COLOUR_DIM;
		}

		String[] var12 = new String[var4];
		int[] var13 = new int[var4];
		System.arraycopy(var2, 0, var12, 0, var4);
		System.arraycopy(var3, 0, var13, 0, var4);
		lines = var12;
		colours = var13;
	}

	/** The candidate list already measured distance, so reuse it. */
	private static int distanceOf(Candidate[] candidates, EntityInfo target) {
		if (candidates != null) {
			for (int var2 = 0; var2 < candidates.length; var2++) {
				if (candidates[var2].selected) {
					return candidates[var2].distance;
				}
			}
		}
		return -1;
	}
}
