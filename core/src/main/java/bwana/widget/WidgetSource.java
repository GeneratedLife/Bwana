package bwana.widget;

/**
 * Reads the game's interfaces.
 * <p>
 * The gap that stopped the planner: inventory, bank, shops and dialogues all live
 * in the widget tree, so without this a plan could chop for an hour and then be
 * unable to put anything away. It is also the last item outstanding from the
 * revision-coupling audit.
 * <p>
 * Deliberately narrow. Widget trees are large and mostly presentational, and an
 * accessor for every property would be a second client rather than a seam. What is
 * here is what behaviour needs: <em>is something open</em>, <em>what does it
 * hold</em>, and <em>what does it say</em>.
 * <p>
 * <b>Game thread only.</b> Interface state is rewritten as packets arrive.
 */
public interface WidgetSource {

	/** Sentinel for "no interface". */
	int NONE = -1;

	/**
	 * The interface filling the game view — a bank, a shop, a level-up screen — or
	 * {@link #NONE}.
	 * <p>
	 * The reliable way to know a bank is open. Waiting on a timer after clicking a
	 * booth guesses; this observes.
	 */
	int getViewportInterfaceId();

	/** The interface occupying the chat area, or {@link #NONE}. Dialogues live here. */
	int getChatInterfaceId();

	/** True when either interface is showing anything at all. */
	boolean isAnyInterfaceOpen();

	/**
	 * Item ids held by a container component, one per slot, 0 for empty.
	 * <p>
	 * Works for any container, so the bank reads the same way the inventory does.
	 *
	 * @return empty when the component is not a container or does not exist
	 */
	int[] getContainerIds(int componentId);

	/** Stack counts alongside {@link #getContainerIds}. */
	int[] getContainerCounts(int componentId);

	/**
	 * Component ids of every container belonging to an interface.
	 * <p>
	 * An open bank shows its <i>own</i> copy of the inventory, a different component
	 * from the inventory tab and carrying different verbs — "Deposit-All" lives
	 * there, not on the tab. Looking only at the tab is how a plan can open a bank,
	 * find nothing to deposit, and walk away still full.
	 *
	 * @return empty when the interface has no containers
	 */
	int[] getContainersUnder(int interfaceId);

	/**
	 * Containers offering an option, wherever they live in the widget tree.
	 * <p>
	 * Asking by capability instead of by location, because location is not reliable:
	 * an open bank shows its storage and your inventory as <em>separate</em>
	 * interfaces, so walking down from the one on screen finds the 240-slot bank
	 * side offering "Withdraw" and never reaches the 28-slot side offering
	 * "Deposit". Hard-coding the second interface's number would work and would be
	 * exactly the revision-specific knowledge this layer exists to keep out.
	 *
	 * @param option substring matched against each container's own options
	 */
	int[] getContainersWithOption(String option);

	/** A component's text, or null. Dialogue and level-up messages live here. */
	String getWidgetText(int componentId);

	/** Whether a component is currently hidden. */
	boolean isWidgetHidden(int componentId);
}
