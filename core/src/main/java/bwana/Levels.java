package bwana;

/**
 * The experience curve, and level maths built on it.
 * <p>
 * This is a deliberate duplicate of the table the client builds in its static
 * initialiser. Bwana could read the client's copy, but then it would depend on
 * {@code deob.client}, which is exactly the coupling the toolkit exists to avoid.
 * The formula is fixed 2004-era game data, so a copy cannot drift.
 */
public final class Levels {

	public static final int MAX_LEVEL = 99;

	/** XP_AT[i] is the experience required to reach level i + 2. */
	private static final int[] XP_AT = new int[99];

	static {
		int var0 = 0;
		for (int var1 = 0; var1 < 99; var1++) {
			int var2 = var1 + 1;
			var0 += (int) ((double) var2 + Math.pow(2.0D, (double) var2 / 7.0D) * 300.0D);
			XP_AT[var1] = var0 / 4;
		}
	}

	private Levels() {
	}

	/** Experience needed to reach the given level. Level 1 is 0. */
	public static int experienceForLevel(int level) {
		if (level <= 1) {
			return 0;
		}
		if (level > MAX_LEVEL) {
			level = MAX_LEVEL;
		}
		return XP_AT[level - 2];
	}

	/** The level a given experience total corresponds to. */
	public static int levelForExperience(int xp) {
		int var1 = 1;
		for (int var2 = 0; var2 < XP_AT.length; var2++) {
			if (xp >= XP_AT[var2]) {
				var1 = var2 + 2;
			}
		}
		return var1 > MAX_LEVEL ? MAX_LEVEL : var1;
	}

	/** Experience still needed to reach the next level, or 0 at 99. */
	public static int experienceToNextLevel(int xp) {
		int var1 = levelForExperience(xp);
		if (var1 >= MAX_LEVEL) {
			return 0;
		}
		return experienceForLevel(var1 + 1) - xp;
	}
}
