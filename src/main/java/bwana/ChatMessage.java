package bwana;

/**
 * One chat line, captured at the moment the client displayed it. Immutable, so it
 * can cross from the game thread to the writer thread and to Swing without
 * copying or locking.
 */
public final class ChatMessage {

	public final long time;
	public final int type;
	public final String sender;
	public final String text;

	public ChatMessage(long time, int type, String sender, String text) {
		this.time = time;
		this.type = type;
		this.sender = sender == null ? "" : sender;
		this.text = text == null ? "" : text;
	}

	public boolean hasSender() {
		return this.sender.length() > 0;
	}

	/** "Nick: hello" or, for senderless game messages, just the text. */
	public String display() {
		if (!this.hasSender()) {
			return this.text;
		}
		if (this.type == ChatType.PRIVATE_IN || this.type == ChatType.PRIVATE_IN_STAFF) {
			return "From " + this.sender + ": " + this.text;
		}
		if (this.type == ChatType.PRIVATE_OUT) {
			return "To " + this.sender + ": " + this.text;
		}
		if (this.type == ChatType.TRADE_REQUEST || this.type == ChatType.DUEL_REQUEST) {
			return this.sender + " " + this.text;
		}
		return this.sender + ": " + this.text;
	}
}
