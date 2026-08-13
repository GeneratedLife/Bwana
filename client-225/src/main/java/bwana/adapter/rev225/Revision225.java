package bwana.adapter.rev225;

import bwana.ChatType;
import bwana.Revision;

/**
 * What rev 225's numbers mean.
 * <p>
 * Lives in the client module rather than in {@code core}, which is the point: the
 * toolkit compiles without knowing any of this, and a second revision is a second
 * class like this one rather than an edit to a shared table.
 * <p>
 * Nothing here is derivable. The skill list mirrors {@code PlayerStat} in
 * Engine-TS; the chat ids were read off the client's chat renderer and the packet
 * handlers calling {@code addMessage}, because no revision documents them.
 */
public final class Revision225 implements Revision {

	/**
	 * Slots 18 and 19 are placeholders the server marks disabled. They exist so
	 * the numbering reaches Runecraft at 20 without renumbering everything below
	 * it, and no packet should ever arrive for them. 317 fills them with Slayer
	 * and Farming, which is exactly the difference this class exists to hold.
	 */
	private static final String[] NAMES = new String[] {
		"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic",
		"Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting",
		"Smithing", "Mining", "Herblore", "Agility", "Thieving", null, null,
		"Runecraft"
	};

	public String name() {
		return "225";
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

	/** Null in the table is how a disabled slot is written, so the two cannot disagree. */
	public boolean isSkillEnabled(int skill) {
		return skill >= 0 && skill < NAMES.length && NAMES[skill] != null;
	}

	/**
	 * 225 happens to number chat types the same way {@link ChatType} does, so this
	 * is close to an identity mapping. It is written out rather than returning the
	 * argument: the equality is a coincidence, and a mapping that is spelled out
	 * cannot quietly become wrong when {@code ChatType} gains a kind.
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
