package bwana;

/**
 * Chat message types, as the rev-225 client uses them.
 * <p>
 * These are not documented anywhere; they were read off the client's own chat
 * renderer and the packet handlers that call {@code addMessage}. Each constant
 * notes where it comes from so the mapping can be re-checked against the source.
 */
public final class ChatType {

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
		return "type" + type;
	}
}
