package bwana.script;

/**
 * A short, timestamped record of what a script did and why.
 * <p>
 * Every line is written at a decision point, with the reason attached. When a run
 * goes wrong the question is always "why did it leave that state", and a log that
 * only records where it went cannot answer it.
 * <p>
 * A fixed ring rather than a growing list: this is written from the game thread at
 * up to 50 Hz, and an unbounded buffer behind a UI that reads it four times a
 * second is a leak with extra steps.
 * <p>
 * Written on the game thread, read from Swing — hence the volatile snapshot.
 */
public final class ScriptLog {

	private static final int CAPACITY = 200;

	private final String[] lines = new String[CAPACITY];
	private int next;
	private volatile int count;
	private final long started = System.currentTimeMillis();

	/** Mirror to stdout as well, so a run can be read after the window is closed. */
	private final boolean echo;

	public ScriptLog(boolean echo) {
		this.echo = echo;
	}

	public synchronized void add(String text) {
		String var2 = stamp(System.currentTimeMillis() - this.started) + " " + text;
		this.lines[this.next] = var2;
		this.next = (this.next + 1) % CAPACITY;
		if (this.count < CAPACITY) {
			this.count++;
		}
		if (this.echo) {
			System.out.println("bwana/script " + var2);
		}
	}

	/** Oldest first. A fresh array, safe to hand to the UI. */
	public synchronized String[] snapshot() {
		int var1 = this.count;
		String[] var2 = new String[var1];
		int var3 = this.count < CAPACITY ? 0 : this.next;
		for (int var4 = 0; var4 < var1; var4++) {
			var2[var4] = this.lines[(var3 + var4) % CAPACITY];
		}
		return var2;
	}

	public synchronized void clear() {
		this.count = 0;
		this.next = 0;
	}

	public int size() {
		return this.count;
	}

	private static String stamp(long millis) {
		long var2 = millis / 1000L;
		long var4 = var2 / 60L;
		long var6 = var2 % 60L;
		return "[" + (var4 < 10L ? "0" : "") + var4 + ":" + (var6 < 10L ? "0" : "") + var6 + "]";
	}
}
