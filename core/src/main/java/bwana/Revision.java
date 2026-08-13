package bwana;

/**
 * The facts about a game revision that the toolkit cannot work out for itself.
 * <p>
 * Everything else in {@code core} is written to be true of any client in this
 * family. These are the things that are not: numbers and names the server picked,
 * which differ between revisions and which nothing in the toolkit can derive.
 * <p>
 * <b>Why this exists.</b> The skill names and chat type ids used to be static
 * tables in {@link Skill} and {@link ChatType}, filled in with what rev 225 uses.
 * That is invisible until it is wrong: 225 leaves skill slots 18 and 19 empty, 317
 * fills them with Slayer and Farming, so {@code Skill.name(18)} answered
 * {@code "Stat18"} on a client where the player is looking at Slayer. No error, no
 * warning, just a wrong label in a tracker whose entire job is to be right about
 * which skill gained.
 * <p>
 * An adapter supplies these instead, so getting them wrong is a property of one
 * small class per revision rather than of the toolkit.
 *
 * @see Bwana#start(Revision, GameState, WorldQuery)
 */
public interface Revision {

	/**
	 * How this revision is known — {@code "225"}, {@code "274"}. Shown in the UI
	 * title and written into logs, so a saved session can be read back knowing
	 * what it came from.
	 */
	String name();

	/**
	 * Highest skill id this revision uses, exclusive. Never more than
	 * {@link Skill#CAPACITY}.
	 */
	int skillCount();

	/**
	 * Display name for a skill id, as players know it.
	 *
	 * @param skill 0 to {@link #skillCount()} - 1
	 */
	String skillName(int skill);

	/**
	 * Whether this revision uses the slot at all.
	 * <p>
	 * Numbering has holes: a revision keeps later skills at their familiar ids and
	 * leaves the gap empty rather than renumbering, so a disabled slot is a real
	 * state and not an error.
	 */
	boolean isSkillEnabled(int skill);

	/**
	 * Translates a chat type id as this client emits it into one of
	 * {@link ChatType}'s kinds.
	 * <p>
	 * Called once, at the edge, by
	 * {@link GameEventBus#fireChatMessage(int, String, String)}. Everything
	 * downstream compares against {@code ChatType} constants and is therefore
	 * correct on any revision without knowing this mapping exists.
	 *
	 * @return a {@code ChatType} constant, or {@link ChatType#UNKNOWN}
	 */
	int chatKind(int rawType);
}
