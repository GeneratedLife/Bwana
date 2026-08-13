package bwana;

/**
 * Skill ids, and the names to show for them.
 * <p>
 * The ids below are stable across this client family and are safe to compile
 * against. The <i>names</i> are not: which slots are used, and what they are
 * called, is a fact about the revision, so it comes from {@link Revision} rather
 * than from a table here. 225 leaves 18 and 19 empty where 317 has Slayer and
 * Farming.
 * <p>
 * Ids 0-17 and 20 have held their meaning everywhere this has been checked, which
 * is why {@link #WOODCUTTING} is a constant a script can name. A revision that
 * renumbered them would need more than a new table, and would be obvious rather
 * than silent.
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

	/**
	 * Slots to size an array for.
	 * <p>
	 * A ceiling, not a count — deliberately a compile-time constant so the xp
	 * arrays can be fields rather than built after the adapter registers. Ask
	 * {@link #count()} for how many this revision actually uses, and
	 * {@link #isEnabled} before showing one.
	 */
	public static final int CAPACITY = 24;

	private Skill() {
	}

	/** How many slots this revision uses. {@link #CAPACITY} if none is registered. */
	public static int count() {
		Revision var0 = GameEventBus.getRevision();
		return var0 == null ? CAPACITY : Math.min(var0.skillCount(), CAPACITY);
	}

	/**
	 * Display name for a skill id.
	 * <p>
	 * Falls back to {@code "Skill8"} rather than guessing when no revision is
	 * registered. An unhelpful label is easier to notice than a plausible wrong
	 * one, which is the failure this class was changed to prevent.
	 */
	public static String name(int skill) {
		Revision var1 = GameEventBus.getRevision();
		if (var1 == null || skill < 0 || skill >= var1.skillCount()) {
			return "Skill" + skill;
		}
		String var2 = var1.skillName(skill);
		return var2 == null ? "Skill" + skill : var2;
	}

	/** False for slots this revision does not use. True for all when none is registered. */
	public static boolean isEnabled(int skill) {
		if (skill < 0 || skill >= CAPACITY) {
			return false;
		}
		Revision var1 = GameEventBus.getRevision();
		return var1 == null || (skill < var1.skillCount() && var1.isSkillEnabled(skill));
	}
}
