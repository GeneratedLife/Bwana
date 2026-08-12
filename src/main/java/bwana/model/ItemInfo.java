package bwana.model;

/**
 * One occupied slot in a container.
 * <p>
 * Carries no name. Resolving one costs an {@code ObjType} lookup, and that cache
 * holds ten entries and is scanned linearly — asking for 28 names would evict and
 * re-decode most of them every call. Use {@code WorldQuery.getItemName} when you
 * actually need text, and cache the result yourself.
 */
public final class ItemInfo {

	/** Container slot, 0-based. */
	public final int slot;

	/** Item config id. */
	public final int id;

	public final int count;

	public ItemInfo(int slot, int id, int count) {
		this.slot = slot;
		this.id = id;
		this.count = count;
	}
}
