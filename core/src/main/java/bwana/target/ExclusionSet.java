package bwana.target;

import bwana.inspect.EntityHandle;
import java.util.ArrayList;
import java.util.List;

/**
 * Targets to skip, and for how long.
 * <p>
 * Exists because "pick the nearest match" and "pick a different one" are not the
 * same request, and without this they are indistinguishable: a caller that rejects
 * the nearest candidate and asks again is handed the same one back, forever. The
 * first Woodcutting run did exactly that — four thousand iterations against one
 * tree stump, at full tick rate.
 * <p>
 * <b>Entries expire.</b> A permanent blacklist would be wrong for most of the
 * reasons a target gets rejected: unreachable right now is not unreachable
 * forever, and a tree someone else felled a moment before you got there will grow
 * back. The caller says how long its reason is good for.
 * <p>
 * Bounded, because this is fed from a loop. When full the oldest entry goes,
 * which is also the one closest to expiring anyway.
 */
public final class ExclusionSet {

	/** Enough to skip a clearing's worth of bad targets; small enough to scan. */
	private static final int CAPACITY = 64;

	private static final class Entry {
		/** Null for a whole-type entry. */
		final EntityHandle handle;
		final int kind;
		final int typeId;
		final long until;

		Entry(EntityHandle handle, int kind, int typeId, long until) {
			this.handle = handle;
			this.kind = kind;
			this.typeId = typeId;
			this.until = until;
		}

		boolean covers(EntityHandle other) {
			if (this.handle != null) {
				return this.handle.matches(other);
			}
			return other.getKind() == this.kind && other.getTypeId() == this.typeId;
		}

		boolean sameAs(Entry other) {
			if (this.handle != null || other.handle != null) {
				return this.handle != null && other.handle != null
					&& this.handle.matches(other.handle);
			}
			return this.kind == other.kind && this.typeId == other.typeId;
		}
	}

	private final List<Entry> entries = new ArrayList<Entry>();

	/**
	 * Skip this entity for a while.
	 *
	 * @param millis how long the reason for skipping it stays true
	 */
	public synchronized void add(EntityHandle handle, long millis) {
		if (handle != null) {
			this.put(new Entry(handle, -1, -1, System.currentTimeMillis() + millis));
		}
	}

	/**
	 * Skip <i>everything</i> of this type for a while.
	 * <p>
	 * For reasons that belong to the definition rather than to the instance. A tree
	 * stump offering no "Chop" is a fact about tree stumps, so learning it once
	 * beats rediscovering it at every stump in the forest — and there are as many
	 * stumps as there have been trees.
	 */
	public synchronized void addType(int kind, int typeId, long millis) {
		if (typeId >= 0) {
			this.put(new Entry(null, kind, typeId, System.currentTimeMillis() + millis));
		}
	}

	private void put(Entry entry) {
		for (int var2 = 0; var2 < this.entries.size(); var2++) {
			// re-excluding something already listed extends it rather than duplicating
			if (this.entries.get(var2).sameAs(entry)) {
				this.entries.set(var2, entry);
				return;
			}
		}
		if (this.entries.size() >= CAPACITY) {
			this.entries.remove(0);
		}
		this.entries.add(entry);
	}

	/** True while this entity should be skipped. Expired entries are dropped here. */
	public synchronized boolean contains(EntityHandle handle) {
		if (handle == null || this.entries.isEmpty()) {
			return false;
		}
		long var2 = System.currentTimeMillis();
		boolean var4 = false;
		for (int var5 = this.entries.size() - 1; var5 >= 0; var5--) {
			Entry var6 = this.entries.get(var5);
			if (var6.until <= var2) {
				this.entries.remove(var5);
			} else if (var6.covers(handle)) {
				var4 = true;
			}
		}
		return var4;
	}

	public synchronized void clear() {
		this.entries.clear();
	}

	/** Live count, for the debug readout. Does not include expired entries. */
	public synchronized int size() {
		long var1 = System.currentTimeMillis();
		int var3 = 0;
		for (int var4 = 0; var4 < this.entries.size(); var4++) {
			if (this.entries.get(var4).until > var1) {
				var3++;
			}
		}
		return var3;
	}
}
