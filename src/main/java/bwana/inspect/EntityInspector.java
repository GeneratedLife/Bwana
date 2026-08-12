package bwana.inspect;

/**
 * Identifies what the game drew at a point in the viewport.
 * <p>
 * Kept out of {@code GameState} for the same reason {@code WorldQuery} is: this
 * walks entity lists and projects each one, so it is a query rather than a field
 * read. Kept out of {@code bwana.vision} because it is the opposite approach —
 * vision guesses from pixels, this asks the game.
 * <p>
 * <b>Game thread only.</b> It reads live entity arrays and the scene's hover
 * state, both of which the renderer rewrites every frame.
 */
public interface EntityInspector {

	/**
	 * Everything at a point, nearest first.
	 * <p>
	 * Several things can occupy one pixel — an NPC standing on a tile with an item
	 * on it, in front of a tree — so this returns all of them rather than guessing
	 * which you meant.
	 *
	 * @param canvasX position within the game canvas, not the viewport
	 * @return possibly empty, never null
	 */
	EntityInfo[] inspectAt(int canvasX, int canvasY);

	/**
	 * The same, at wherever the client currently thinks the mouse is.
	 * <p>
	 * Preferred over passing coordinates in: the client already tracks the cursor
	 * on its own canvas, so this needs no polling of the desktop pointer and no
	 * conversion between coordinate spaces, and it agrees exactly with what the
	 * scene used when it recorded the hovered tile.
	 */
	EntityInfo[] inspectAtCursor();

	/**
	 * Every scene object whose name contains {@code nameContains}, on the player's
	 * current plane, nearest first.
	 * <p>
	 * Unlike {@link #inspectAtCursor} this does not depend on the cursor: it scans
	 * the scene, so it can answer "where is the closest tree" rather than "what am
	 * I pointing at". Case-insensitive; an empty or null filter returns everything
	 * named.
	 *
	 * @param max stop after this many, since the caller almost always wants one
	 */
	EntityInfo[] findObjects(String nameContains, int max);

	/**
	 * Full projection breakdown for the nearest object matching the name.
	 * <p>
	 * Reads the loc's real model to get its bounds, so the anchor being used can be
	 * compared against where the object is actually drawn.
	 *
	 * @return null if nothing matched
	 */
	ProjectionDebug debugNearest(String nameContains);

	/**
	 * Rebuild the live crosshair overlay's target list.
	 * <p>
	 * Only the list is rebuilt here; the markers themselves are projected every
	 * frame by the render hook, which is what keeps them locked to their objects
	 * while the camera moves. Call this occasionally — as you walk — not per frame.
	 *
	 * @param nameContains filter, or null/empty for every named object
	 * @param max          cap, since a town is hundreds of objects
	 */
	void refreshDebugOverlay(String nameContains, int max);

	/**
	 * Entities matching the criteria, nearest the player first.
	 * <p>
	 * The generic form of {@link #findObjects}, which is now a thin case of it.
	 * Scenery, NPCs and players come back in one ranked list because they are all
	 * {@link EntityInfo} — the caller filters by kind through the criteria rather
	 * than by calling a different method.
	 */
	EntityInfo[] findTargets(bwana.target.TargetCriteria criteria, int max);

	/**
	 * Re-find a previously selected entity, or null if it is gone.
	 * <p>
	 * Separate from searching because re-acquiring a known target and choosing a
	 * new one are different questions. A creature is matched on index <i>and</i>
	 * type so a reused index cannot silently substitute a different one; scenery is
	 * matched on tile and id.
	 */
	EntityInfo resolveTarget(EntityInfo previous);

	/** The overlay marker for an entity, so the selection can be drawn per frame. */
	bwana.inspect.DebugOverlay.Target markerFor(EntityInfo entity);

	/**
	 * Entities matching the identity criteria — kind, name, id — each annotated with
	 * why it was rejected if it was.
	 * <p>
	 * Distance and visibility are reported rather than applied, so the caller can
	 * show what was considered instead of only what survived.
	 * <p>
	 * <b>Ordering:</b> eligible entries first, nearest first within each group, then
	 * rejected ones on the same basis. Eligibility outranks distance <i>only</i>
	 * because of {@code max}: a plain nearest-first cut turns the limit into a
	 * filter, and a cluster of rejected entries standing closer than the nearest
	 * usable one would hide it completely. Entry 0 is therefore the entity
	 * {@link #findTargets} would return, whenever there is one at all.
	 *
	 * @param max cap on entries returned; rejected ones never displace eligible ones
	 */
	bwana.target.Candidate[] findCandidates(bwana.target.TargetCriteria criteria, int max);
}
