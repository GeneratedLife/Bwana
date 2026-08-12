package bwana;

import java.util.ArrayList;
import java.util.List;

/**
 * Session experience tracking.
 * <p>
 * All mutable state is owned by the game thread. The UI never reads it directly —
 * {@link #onTick} builds an {@link XpSnapshot} and pushes it to a
 * {@link Listener}, which is expected to hop to the event dispatch thread itself.
 * <p>
 * The one thing that crosses the other way is the reset button, and it crosses as
 * a single volatile boolean that the game thread consumes on its next tick. That
 * avoids locking the tracker just to service a button.
 */
public final class XpTracker extends GameEventsAdapter {

	public interface Listener {
		/** Called on the game thread with a fresh immutable snapshot. */
		void onSnapshot(XpSnapshot snapshot);
	}

	/** Push a snapshot 4x a second rather than 50. */
	private static final int TICKS_PER_PUSH = 12;

	/**
	 * Whether a stat packet has been seen for this skill since the last logout.
	 * <p>
	 * The server sends a baseline for every skill at login, reporting your
	 * existing total rather than a gain. This flag is how we tell the two apart —
	 * <b>not</b> {@code oldXp == 0}, which is also true the first time you ever
	 * train a skill and would silently swallow that first gain.
	 */
	private final boolean[] seen = new boolean[Skill.COUNT];

	private final int[] gained = new int[Skill.COUNT];
	private final int[] currentXp = new int[Skill.COUNT];
	/** When this skill first gained experience this session. */
	private final long[] firstGainAt = new long[Skill.COUNT];

	private long sessionStart;
	private int ticks;

	private volatile boolean resetRequested;

	private final Listener listener;

	public XpTracker(Listener listener) {
		this.listener = listener;
	}

	/** Safe to call from any thread; honoured on the next game tick. */
	public void requestReset() {
		this.resetRequested = true;
	}

	public void onLogout() {
		// A logout ends the session: write it out and start clean on next login.
		// If you would rather keep counting across a world hop, drop the persist
		// call here and let only the reset button and shutdown hook close a session.
		this.persist();
		for (int var1 = 0; var1 < Skill.COUNT; var1++) {
			this.seen[var1] = false;
		}
	}

	/**
	 * Flush the current session to disk. Safe to call from a shutdown hook — it
	 * races with the game thread only over ints that are already written.
	 */
	public void persistNow() {
		this.persist();
	}

	public void onExperienceGained(int skill, int oldXp, int newXp) {
		if (skill < 0 || skill >= Skill.COUNT) {
			return;
		}
		this.currentXp[skill] = newXp;
		if (!this.seen[skill]) {
			// login baseline: record where we started, count nothing
			this.seen[skill] = true;
			return;
		}
		int var4 = newXp - oldXp;
		if (var4 <= 0) {
			return;
		}
		long var5 = System.currentTimeMillis();
		if (this.gained[skill] == 0) {
			this.firstGainAt[skill] = var5;
		}
		if (this.sessionStart == 0L) {
			this.sessionStart = var5;
		}
		this.gained[skill] += var4;
	}

	public void onTick() {
		if (this.resetRequested) {
			this.resetRequested = false;
			this.reset();
		}
		if (++this.ticks < TICKS_PER_PUSH) {
			return;
		}
		this.ticks = 0;
		if (this.listener != null) {
			this.listener.onSnapshot(this.snapshot());
		}
	}

	private void reset() {
		this.persist();
	}

	/**
	 * Writes the session out and clears it, so calling this twice cannot log the
	 * same experience twice — which matters because logout and the shutdown hook
	 * can both fire when you close the client while logged in.
	 */
	private synchronized void persist() {
		if (this.sessionStart == 0L) {
			return;
		}
		SessionStore.append(this.sessionStart, System.currentTimeMillis(), this.gained);
		for (int var1 = 0; var1 < Skill.COUNT; var1++) {
			this.gained[var1] = 0;
			this.firstGainAt[var1] = 0L;
		}
		this.sessionStart = 0L;
	}

	/** Built on the game thread, immutable once returned. */
	private XpSnapshot snapshot() {
		long var1 = System.currentTimeMillis();
		List<XpSnapshot.Row> var3 = new ArrayList<XpSnapshot.Row>();
		int var4 = 0;

		for (int var5 = 0; var5 < Skill.COUNT; var5++) {
			if (this.gained[var5] <= 0) {
				continue;
			}
			var4 += this.gained[var5];

			// rate is measured from this skill's first gain, not from login —
			// otherwise idling before you start training drags the average down
			long var6 = var1 - this.firstGainAt[var5];
			long var8 = var6 > 0L ? (long) this.gained[var5] * 3600000L / var6 : 0L;

			int var10 = this.currentXp[var5];
			int var11 = Levels.levelForExperience(var10);
			int var12 = Levels.experienceToNextLevel(var10);
			long var13 = var8 > 0L && var12 > 0 ? (long) var12 * 3600000L / var8 : -1L;

			var3.add(new XpSnapshot.Row(var5, this.gained[var5], var10, var11, var12, var8, var13));
		}

		long var15 = this.sessionStart == 0L ? 0L : var1 - this.sessionStart;
		XpSnapshot.Row[] var17 = var3.toArray(new XpSnapshot.Row[var3.size()]);
		return new XpSnapshot(var17, var4, var15, this.sessionStart != 0L);
	}
}
