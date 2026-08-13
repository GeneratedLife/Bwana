package bwana.action;

import bwana.inspect.EntityInfo;

/**
 * Asks the client what can be done to an entity, and does it.
 * <p>
 * The counterpart to {@link bwana.inspect.EntityInspector}: that one answers "what
 * is there", this one answers "what can I do to it" and then does it. Kept apart
 * from {@link bwana.GameState} because these are the only two methods in the
 * toolkit that <b>change</b> anything — everything else observes. Having exactly
 * one interface that can act makes the acting side easy to find and easy to
 * disable.
 * <p>
 * <b>Game thread only.</b> {@link #execute} writes to the outbound packet stream,
 * whose ISAAC cipher is stateful, so calling it from Swing would corrupt the
 * connection rather than merely race.
 */
public interface ActionExecutor {

	/**
	 * The interactions the client would offer on this entity, in definition order.
	 * <p>
	 * Read from the same {@code op[]} tables the right-click menu is built from, so
	 * an entity exposes exactly what the game says it exposes and nothing invented.
	 *
	 * @return possibly empty, never null
	 */
	EntityAction[] getActions(EntityInfo entity);

	/**
	 * Perform the interaction through the client's own menu handler.
	 * <p>
	 * Returning null means the client accepted it — which is <b>not</b> a claim that
	 * it worked. It means a request was made. Whether anything came of it has to be
	 * read from the game afterwards.
	 *
	 * @return null if handed off, otherwise why it could not be
	 */
	String execute(EntityAction action);

	/**
	 * Walk toward a world tile.
	 * <p>
	 * The one movement command in the toolkit. Until now the only way to make the
	 * player go anywhere was to interact with something at the far end, which is
	 * fine for gameplay but useless for testing movement itself — you could not tell
	 * a pathing failure from an interaction failure.
	 * <p>
	 * <b>Best effort by design.</b> A destination outside the loaded scene is
	 * clamped to as far along the way as the client can currently path, so calling
	 * this repeatedly makes progress across a long journey as the scene reloads
	 * beneath you. Callers therefore need no notion of scene size or route length,
	 * both of which are the adapter's business.
	 *
	 * @return null if a move was commanded, otherwise why it could not be
	 */
	String walkTo(int worldX, int worldY);

	/**
	 * The interactions available on an item in a container.
	 * <p>
	 * Read from the same tables the client's own menu uses, so an item offers what
	 * the game says it offers — including verbs that only exist while a particular
	 * interface is open, such as depositing.
	 *
	 * @param componentId container to look in; {@code -1} for the inventory
	 * @param slot        position within it
	 * @return possibly empty, never null
	 */
	ItemAction[] getItemActions(int componentId, int slot);

	/**
	 * Perform an item interaction through the client's own menu handler.
	 * <p>
	 * Re-checks that the slot still holds the item the action was built for before
	 * sending. Containers reshuffle constantly, and acting on a stale slot is how a
	 * script eats its logs.
	 *
	 * @return null if handed off, otherwise why it was not
	 */
	String executeItem(ItemAction action);
}
