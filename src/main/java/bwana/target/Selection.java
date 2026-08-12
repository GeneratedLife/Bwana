package bwana.target;

import bwana.inspect.DebugOverlay;
import bwana.inspect.EntityHandle;
import bwana.inspect.EntityInfo;

/**
 * The currently selected target and the state of the search for it.
 * <p>
 * Holds two representations on purpose. {@link #getInfo} is a snapshot for the
 * UI to read and print. {@link #getMarker} is a {@link DebugOverlay.Target},
 * which is what the render hook already knows how to resolve every frame — so the
 * selection crosshair follows a walking NPC through exactly the same path as the
 * live overlay markers, rather than a second implementation that would drift out
 * of agreement with the first.
 */
public final class Selection {

	public static final int STATE_IDLE = 0;
	public static final int STATE_SEARCHING = 1;
	public static final int STATE_FOUND = 2;
	public static final int STATE_LOST = 3;

	private static volatile int state = STATE_IDLE;
	private static volatile EntityInfo info;
	private static volatile DebugOverlay.Target marker;
	private static volatile long stateSince = System.currentTimeMillis();
	private static volatile String note = "";

	// ---- identity continuity ----
	// Evidence that the thing being held is still the same logical thing. Without
	// this the lock is unfalsifiable: a target silently swapped for another of the
	// same name looks exactly like a target held correctly.

	/** Identity locked onto, kept for the life of the lock. */
	private static volatile EntityHandle handle;

	/** Successful re-resolutions of that identity since the lock was taken. */
	private static volatile int resolves;

	/**
	 * Times the client moved this entity to a different slot without the lock
	 * breaking.
	 * <p>
	 * The headline number. Under the old index-as-identity rule any of these would
	 * have dropped the target; every one of them is a case the abstraction now
	 * survives.
	 */
	private static volatile int slotChanges;

	private static volatile int lastSlot = -1;

	/** Identity of the last target lost, so a return can be recognised as a return. */
	private static volatile EntityHandle lostHandle;

	/** Times a lost target was found again and proved to be the same entity. */
	private static volatile int reacquires;

	private Selection() {
	}

	public static EntityHandle getHandle() {
		return handle;
	}

	public static int getResolveCount() {
		return resolves;
	}

	public static int getSlotChangeCount() {
		return slotChanges;
	}

	public static int getLastSlot() {
		return lastSlot;
	}

	public static int getReacquireCount() {
		return reacquires;
	}

	/** True while the last thing lost is the thing now held — a genuine reacquisition. */
	public static boolean isReacquired() {
		return reacquires > 0;
	}

	public static int getState() {
		return state;
	}

	public static String getStateName() {
		if (state == STATE_SEARCHING) {
			return "SEARCHING";
		}
		if (state == STATE_FOUND) {
			return "TARGET FOUND";
		}
		if (state == STATE_LOST) {
			return "TARGET LOST";
		}
		return "IDLE";
	}

	public static EntityInfo getInfo() {
		return info;
	}

	public static DebugOverlay.Target getMarker() {
		return marker;
	}

	public static long getStateMillis() {
		return System.currentTimeMillis() - stateSince;
	}

	public static String getNote() {
		return note;
	}

	/** True while there is something to draw a selection crosshair on. */
	public static boolean hasTarget() {
		return state == STATE_FOUND && marker != null;
	}

	public static synchronized void found(EntityInfo entity, DebugOverlay.Target target) {
		info = entity;
		marker = target;
		handle = entity.handle;
		// A lock taken on the same identity we just lost is a reacquisition, not a
		// new target -- worth counting separately, because "it came back and I knew
		// it was the same one" is the hard case.
		if (lostHandle != null && lostHandle.matches(entity.handle)) {
			reacquires++;
		} else {
			reacquires = 0;
		}
		lostHandle = null;
		resolves = 0;
		slotChanges = 0;
		lastSlot = entity.getSlot();
		setState(STATE_FOUND, null);
	}

	/** Same target, refreshed values — does not reset the state clock. */
	public static synchronized void refresh(EntityInfo entity) {
		if (state != STATE_FOUND) {
			return;
		}
		info = entity;
		resolves++;
		int var1 = entity.getSlot();
		if (var1 != lastSlot) {
			// Only meaningful for kinds whose identity ignores the slot; for the rest
			// a slot change means a different entity and we would never get here.
			if (lastSlot >= 0 && var1 >= 0) {
				slotChanges++;
			}
			lastSlot = var1;
		}
		// keep the handle's slot hint current so the adapter's next lookup is cheap
		handle = entity.handle;
	}

	public static synchronized void lost(String why) {
		marker = null;
		lostHandle = handle;
		setState(STATE_LOST, why);
	}

	public static synchronized void searching(String why) {
		marker = null;
		info = null;
		handle = null;
		setState(STATE_SEARCHING, why);
	}

	public static synchronized void idle() {
		marker = null;
		info = null;
		handle = null;
		lostHandle = null;
		resolves = 0;
		slotChanges = 0;
		reacquires = 0;
		lastSlot = -1;
		setState(STATE_IDLE, null);
	}

	private static void setState(int next, String why) {
		if (state != next) {
			state = next;
			stateSince = System.currentTimeMillis();
		}
		note = why == null ? "" : why;
	}
}
