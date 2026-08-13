package bwana;

/**
 * What a chat message <i>is</i>, independent of how any client numbers it.
 * <p>
 * <b>These are the toolkit's own kinds, not a client's ids.</b> A revision picks
 * its own numbering for chat types, and nothing documents it — 225's were read off
 * its chat renderer and its {@code addMessage} callers. So {@link Revision#chatKind}
 * translates once, at the edge, in
 * {@link GameEventBus#fireChatMessage(int, String, String)}; every message that
 * reaches a listener already carries a kind from this class.
 * <p>
 * That the values below happen to equal 225's ids is <b>a coincidence, and is not
 * relied upon</b>. It is recorded here because an unstated coincidence is the kind
 * of thing that is silently wrong for one revision in five: 225's adapter maps
 * these one-to-one, and another revision's will not.
 */
public final class ChatType {

	/** A type this revision's adapter did not recognise. */
	public static final int UNKNOWN = -1;

	/** Server or client message with no sender: "You get some logs." */
	public static final int GAME = 0;

	/** Public chat from a player with staff rights; drawn with a white name. */
	public static final int PUBLIC_STAFF = 1;

	/** Ordinary public chat. */
	public static final int PUBLIC = 2;

	/** Private message received, drawn as "From X:". */
	public static final int PRIVATE_IN = 3;

	/** "X wishes to trade with you." */
	public static final int TRADE_REQUEST = 4;

	/** Friend list notice: "X has logged in." */
	public static final int PRIVATE_SYSTEM = 5;

	/** Private message you sent, drawn as "To X:". */
	public static final int PRIVATE_OUT = 6;

	/** Private message from staff; bypasses the privacy setting. */
	public static final int PRIVATE_IN_STAFF = 7;

	/** "X wishes to duel with you." */
	public static final int DUEL_REQUEST = 8;

	public static final String CATEGORY_ALL = "All";
	public static final String CATEGORY_GAME = "Game";
	public static final String CATEGORY_PUBLIC = "Public";
	public static final String CATEGORY_PRIVATE = "Private";
	public static final String CATEGORY_TRADE = "Trade";

	public static final String[] CATEGORIES = new String[] {
		CATEGORY_ALL, CATEGORY_PUBLIC, CATEGORY_PRIVATE, CATEGORY_GAME, CATEGORY_TRADE
	};

	private ChatType() {
	}

	public static String category(int type) {
		if (type == PUBLIC || type == PUBLIC_STAFF) {
			return CATEGORY_PUBLIC;
		}
		if (type == PRIVATE_IN || type == PRIVATE_OUT || type == PRIVATE_IN_STAFF || type == PRIVATE_SYSTEM) {
			return CATEGORY_PRIVATE;
		}
		if (type == TRADE_REQUEST || type == DUEL_REQUEST) {
			return CATEGORY_TRADE;
		}
		return CATEGORY_GAME;
	}

	/** Short tag used in the log file, so lines stay greppable. */
	public static String tag(int type) {
		if (type == PUBLIC) {
			return "pub";
		}
		if (type == PUBLIC_STAFF) {
			return "pub*";
		}
		if (type == PRIVATE_IN) {
			return "from";
		}
		if (type == PRIVATE_IN_STAFF) {
			return "from*";
		}
		if (type == PRIVATE_OUT) {
			return "to";
		}
		if (type == PRIVATE_SYSTEM) {
			return "friend";
		}
		if (type == TRADE_REQUEST) {
			return "trade";
		}
		if (type == DUEL_REQUEST) {
			return "duel";
		}
		if (type == GAME) {
			return "game";
		}
		if (type == UNKNOWN) {
			return "?";
		}
		return "type" + type;
	}
}
