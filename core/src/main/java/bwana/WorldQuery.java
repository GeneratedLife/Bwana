package bwana;

import bwana.model.GroundItemInfo;

/**
 * Reads that are too expensive to sit next to {@link GameState}.
 * <p>
 * The split exists so cost is visible in the type. Everything on {@code GameState}
 * is a field read or a short loop and is safe to call every tick. Everything here
 * scans the scene or decodes cache data, and should be called when the user asks
 * for it — behind a button, or on a timer measured in seconds.
 * <p>
 * <b>Game thread only, same as {@link GameState}</b>, and here it is stricter than
 * a data race: ground items live in {@code LinkList}s whose iteration uses a
 * cursor stored on the list itself. Walking one from another thread would corrupt
 * the client's own traversal, not merely read a stale value.
 */
public interface WorldQuery {

	/**
	 * Every item stack on the ground in the loaded scene.
	 * <p>
	 * Walks the whole 104x104 tile grid for the current plane — roughly 10,800
	 * cells — so treat it as a scan, not a getter.
	 */
	GroundItemInfo[] getGroundItems();

	/**
	 * Resolve an item's name from the cache.
	 * <p>
	 * Backed by a ten-entry, linearly scanned cache. Asking for many distinct ids
	 * in a row evicts and re-decodes each one, so cache what you get back rather
	 * than calling this per frame.
	 *
	 * @return the name, or null if the id is unknown
	 */
	String getItemName(int id);
}
