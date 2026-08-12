package bwana.action;

import bwana.inspect.EntityHandle;
import bwana.inspect.EntityInfo;

/**
 * One interaction the client itself offers on one entity — "Chop down", "Attack",
 * "Examine".
 * <p>
 * These are not invented. Every field is copied from the entry the client would
 * have put in its own right-click menu, read out of the same {@code op[]} tables
 * the menu builder reads: the opcode, the three parameters, the wording. That is
 * deliberate. A hand-written table of "trees can be chopped" would be a second
 * source of truth that drifts the moment a cache is updated, and it could not
 * answer the question for an entity nobody wrote an entry for.
 * <p>
 * Immutable, and free of any reference to a live client object, so it can be
 * handed to Swing and read there safely.
 */
public final class EntityAction {

	/** Wording as the menu would show it, tags stripped: "Chop down". */
	public final String name;

	/**
	 * Slot in the type's {@code op} array, 0-4, or -1 for options the client adds
	 * itself rather than reading from the definition — Examine, Follow, Trade.
	 */
	public final int opIndex;

	/** The client's own menu opcode. Passed straight back to {@code useMenuOption}. */
	public final int menuAction;

	/** Menu parameters, in the client's order: identity, then tile. */
	public final int paramA;
	public final int paramB;
	public final int paramC;

	/**
	 * Which entity this came from, so an action cannot be run against the wrong thing.
	 * <p>
	 * The handle rather than the kind/id/slot it decomposes into: this record used
	 * to re-derive "is that still the same entity" out of {@link #paramA}, which
	 * meant the action layer knew that paramA is sometimes a slot and sometimes a
	 * scene bitset. It no longer needs to know.
	 */
	public final EntityHandle entityHandle;

	public final int entityKind;
	public final int entityId;
	public final String entityName;

	/**
	 * True when the client answers the interaction by itself and sends nothing.
	 * <p>
	 * Examine is the case: the description is in the cache, so the client prints it
	 * and no packet leaves. Worth marking, because confirmation has to look for a
	 * chat line rather than for a change in the world.
	 */
	public final boolean local;

	public EntityAction(String name, int opIndex, int menuAction, int paramA, int paramB,
			int paramC, EntityHandle entityHandle, String entityName, boolean local) {
		this.name = name;
		this.opIndex = opIndex;
		this.menuAction = menuAction;
		this.paramA = paramA;
		this.paramB = paramB;
		this.paramC = paramC;
		this.entityHandle = entityHandle;
		this.entityKind = entityHandle.getKind();
		this.entityId = entityHandle.getTypeId();
		this.entityName = entityName;
		this.local = local;
	}

	/** True if this action was built for {@code entity} and can still be run against it. */
	public boolean appliesTo(EntityInfo entity) {
		return entity != null && this.entityHandle.matches(entity.handle);
	}

	public String describe() {
		return this.name + " " + this.entityName;
	}

	public String toString() {
		return this.describe();
	}
}
