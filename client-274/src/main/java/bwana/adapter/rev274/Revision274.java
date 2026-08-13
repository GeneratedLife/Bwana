package bwana.adapter.rev274;

import bwana.ChatType;
import bwana.Revision;

/**
 * What rev 274's numbers mean.
 * <p>
 * Nothing here was inferred from 225. Both tables were read out of this client:
 * the skills from {@link jagex2.client.Skill}, which 274 ships and 225 does not,
 * and the chat ids from the arguments its own {@code addChat} call sites pass.
 */
public final class Revision274 implements Revision {

	/**
	 * Mirrors {@code jagex2.client.Skill.names}, which declares 25 slots.
	 * <p>
	 * Null where that table says {@code "-unused-"}, so a disabled slot is written
	 * once rather than in two places that can disagree.
	 * <p>
	 * Slot 18 is the interesting one. 274 <i>names</i> it slayer but marks it
	 * unused, because the skill was announced before it opened — so the honest
	 * answer for 274 is that 18 is not a skill a player has, even though the client
	 * knows what it would be called. 317 has it live. That distinction is invisible
	 * from a name table alone and is exactly why {@link #isSkillEnabled} follows the
	 * client's {@code used} array rather than the presence of a name.
	 */
	private static final String[] NAMES = new String[] {
		"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic",
		"Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
		"Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", null,
		"Runecraft", null, null, null, null
	};

	/** Mirrors {@code jagex2.client.Skill.used}. Slayer is named but not live. */
	private static final boolean[] USED = new boolean[] {
		true, true, true, true, true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, false, false, true, false, false,
		false, false
	};

	public String name() {
		return "274";
	}

	public int skillCount() {
		return NAMES.length;
	}

	public String skillName(int skill) {
		if (skill < 0 || skill >= NAMES.length) {
			return null;
		}
		return NAMES[skill];
	}

	public boolean isSkillEnabled(int skill) {
		return skill >= 0 && skill < USED.length && USED[skill];
	}

	/**
	 * 274 numbers chat exactly as 225 does. Checked rather than assumed: every
	 * {@code addChat} call site in this client was read, and each literal lines up
	 * with the same meaning — 4 carries "wishes to trade with you.", 8 carries
	 * "wishes to duel with you.", 1 and 7 prefix the sender with the {@code @cr2@}
	 * staff crown.
	 * <p>
	 * Written out rather than returning the argument, for the same reason 225's is:
	 * two revisions agreeing is a fact about them, not a rule, and a spelled-out
	 * mapping cannot quietly stop being true.
	 */
	public int chatKind(int rawType) {
		switch (rawType) {
			case 0:
				return ChatType.GAME;
			case 1:
				return ChatType.PUBLIC_STAFF;
			case 2:
				return ChatType.PUBLIC;
			case 3:
				return ChatType.PRIVATE_IN;
			case 4:
				return ChatType.TRADE_REQUEST;
			case 5:
				return ChatType.PRIVATE_SYSTEM;
			case 6:
				return ChatType.PRIVATE_OUT;
			case 7:
				return ChatType.PRIVATE_IN_STAFF;
			case 8:
				return ChatType.DUEL_REQUEST;
			default:
				return ChatType.UNKNOWN;
		}
	}
}
