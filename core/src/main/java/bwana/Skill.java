package bwana;

/**
 * Skill ids as the rev-225 server defines them.
 * <p>
 * Mirrors {@code PlayerStat} in Engine-TS. Indices 18 and 19 are placeholders the
 * server marks disabled — they exist so the numbering lines up with Runecraft at
 * 20, and no packet should ever arrive for them.
 */
public final class Skill {

	public static final int ATTACK = 0;
	public static final int DEFENCE = 1;
	public static final int STRENGTH = 2;
	public static final int HITPOINTS = 3;
	public static final int RANGED = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int RUNECRAFT = 20;

	/** Number of skill slots the protocol can address. */
	public static final int COUNT = 21;

	private static final String[] NAMES = new String[] {
		"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic",
		"Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
		"Smithing", "Mining", "Herblore", "Agility", "Thieving", "Stat18", "Stat19",
		"Runecraft"
	};

	private Skill() {
	}

	public static String name(int skill) {
		if (skill < 0 || skill >= NAMES.length) {
			return "Skill" + skill;
		}
		return NAMES[skill];
	}

	/** False for the two placeholder slots the server never uses. */
	public static boolean isEnabled(int skill) {
		return skill >= 0 && skill < COUNT && skill != 18 && skill != 19;
	}
}
