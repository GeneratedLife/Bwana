package bwana;

/**
 * Reports what the client is actually doing when it appears to freeze.
 * <p>
 * Exists because "it disconnects" and "it stalls" look identical from the outside
 * and have nothing in common underneath. A disconnect drops the login flag and
 * rebuilds the scene; a stall is the game loop not running for a while, with the
 * connection perfectly healthy. Guessing between them wasted several rounds of
 * optimisation on a cause that turned out not to be the problem, so this measures
 * both and says which happened.
 * <p>
 * Deliberately cheap: two long comparisons per tick and nothing allocated unless
 * something is worth reporting.
 */
public final class ConnectionMonitor extends GameEventsAdapter {

	/** A gap longer than this means the loop stopped; a tick is nominally 20ms. */
	private static final long STALL_MILLIS = 300L;

	/** Summarise every so often, so a quiet run says so rather than saying nothing. */
	private static final long SUMMARY_MILLIS = 30000L;

	private long lastTick;
	private long lastSummary;
	private long sessionStart;

	private int stalls;
	private long worstStall;
	private long totalStalled;
	private int logins;
	private int logouts;

	/**
	 * Reconnects, counted where they happen rather than inferred from the login flag.
	 * <p>
	 * {@code tryReconnect} clears {@code ingame} and logs straight back in within a
	 * single call, so anything polling the flag once a tick never sees it drop. That
	 * is precisely how a run showing "0 disconnects" was in fact reconnecting every
	 * few seconds, and it sent this investigation after garbage collection and scan
	 * cost for several rounds. Counted at the source, it cannot be missed.
	 */
	private static volatile int reconnects;

	/** Called from the client the moment it decides to re-establish the connection. */
	public static void noteReconnect() {
		reconnects++;
		System.out.println("bwana/conn RECONNECT #" + reconnects
			+ "  -- connection was dropped; the blocking re-login is the freeze");
	}

	public static int getReconnects() {
		return reconnects;
	}

	public void onLogin() {
		this.logins++;
		long var1 = System.currentTimeMillis();
		System.out.println("bwana/conn LOGIN #" + this.logins
			+ (this.logouts > 0 ? "  (reconnected after " + (var1 - this.sessionStart) + "ms off)"
				: ""));
		this.sessionStart = var1;
		this.lastTick = 0L;
	}

	public void onLogout() {
		this.logouts++;
		this.sessionStart = System.currentTimeMillis();
		System.out.println("bwana/conn LOGOUT #" + this.logouts
			+ "  -- the login flag actually dropped, so this is a real disconnect,"
			+ " not a stall");
	}

	public void onTick() {
		long var1 = System.currentTimeMillis();
		if (this.lastTick == 0L) {
			this.lastTick = var1;
			this.lastSummary = var1;
			return;
		}
		long var3 = var1 - this.lastTick;
		this.lastTick = var1;

		if (var3 >= STALL_MILLIS) {
			this.stalls++;
			this.totalStalled += var3;
			if (var3 > this.worstStall) {
				this.worstStall = var3;
			}
			// No logout alongside this means the connection is fine and the client
			// simply stopped running -- a very different problem to chase.
			System.out.println("bwana/conn STALL " + var3 + "ms"
				+ "  (loop stopped; logins " + this.logins + ", logouts " + this.logouts + ")");
		}

		if (var1 - this.lastSummary >= SUMMARY_MILLIS) {
			this.lastSummary = var1;
			System.out.println("bwana/conn 30s summary: " + this.stalls + " stalls, "
				+ this.totalStalled + "ms lost, worst " + this.worstStall + "ms, "
				+ reconnects + " reconnects, " + this.logouts + " full logouts");
			this.stalls = 0;
			this.totalStalled = 0L;
			this.worstStall = 0L;
		}
	}
}
