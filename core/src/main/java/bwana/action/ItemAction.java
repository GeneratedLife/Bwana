package bwana.action;

/**
 * One interaction the client offers on an item in a container.
 * <p>
 * The inventory counterpart to {@link EntityAction}, and separate from it for an
 * honest reason: an item's identity is a slot in a container, not a place in the
 * world, so it cannot carry an {@link bwana.inspect.EntityHandle} and pretending
 * otherwise would put a hole in the identity rules.
 * <p>
 * Two sources feed these, and the difference matters. <b>Item ops</b> come from the
 * item's own definition — Bury, Eat, Drop — and are available wherever it is.
 * <b>Interface ops</b> come from the component showing it, which is how the same
 * bones offer "Bury" in the inventory and "Deposit-1" once a bank is open. Same
 * item, different container, different verbs.
 */
public final class ItemAction {

	/** Wording as the menu would show it: "Bury", "Deposit-All". */
	public final String name;

	/** The client's own menu opcode. */
	public final int menuAction;

	public final int itemId;

	/** Slot within the container. */
	public final int slot;

	/** Component the item is displayed in; part of what the interaction addresses. */
	public final int componentId;

	/** True when the verb came from the interface rather than the item definition. */
	public final boolean fromInterface;

	public ItemAction(String name, int menuAction, int itemId, int slot, int componentId,
			boolean fromInterface) {
		this.name = name;
		this.menuAction = menuAction;
		this.itemId = itemId;
		this.slot = slot;
		this.componentId = componentId;
		this.fromInterface = fromInterface;
	}

	/**
	 * True if this action still addresses the same item in the same place.
	 * <p>
	 * Slots shuffle whenever anything is added or removed, so acting on a remembered
	 * slot without checking is how a script buries the wrong thing.
	 */
	public boolean stillValid(int[] containerIds) {
		return containerIds != null && this.slot >= 0 && this.slot < containerIds.length
			&& containerIds[this.slot] == this.itemId;
	}

	public String describe() {
		return this.name + " item " + this.itemId + " (slot " + this.slot + ")";
	}

	public String toString() {
		return this.describe();
	}
}
