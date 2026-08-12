package bwana;

/**
 * An immutable copy of the tracker's state, built on the game thread and handed
 * to Swing.
 * <p>
 * The point of copying is that the UI never reads live tracker state, so there is
 * no lock and no chance of rendering a row that changed halfway through painting.
 */
public final class XpSnapshot {

	/** One row per skill that has gained experience this session. */
	public static final class Row {

		public final int skill;
		public final int gained;
		public final int currentXp;
		public final int level;
		public final int xpToNextLevel;
		/** Experience per hour, or 0 before enough time has passed to mean anything. */
		public final long xpPerHour;
		/** Milliseconds to the next level at the current rate, or -1 if unknown. */
		public final long millisToNextLevel;

		Row(int skill, int gained, int currentXp, int level, int xpToNextLevel, long xpPerHour, long millisToNextLevel) {
			this.skill = skill;
			this.gained = gained;
			this.currentXp = currentXp;
			this.level = level;
			this.xpToNextLevel = xpToNextLevel;
			this.xpPerHour = xpPerHour;
			this.millisToNextLevel = millisToNextLevel;
		}
	}

	public final Row[] rows;
	public final int totalGained;
	public final long elapsedMillis;
	public final boolean tracking;

	XpSnapshot(Row[] rows, int totalGained, long elapsedMillis, boolean tracking) {
		this.rows = rows;
		this.totalGained = totalGained;
		this.elapsedMillis = elapsedMillis;
		this.tracking = tracking;
	}
}
