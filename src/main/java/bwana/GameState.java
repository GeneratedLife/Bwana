package bwana;

import bwana.model.CameraInfo;
import bwana.model.ItemInfo;
import bwana.model.NpcInfo;
import bwana.model.PathInfo;
import bwana.model.PlayerInfo;

/**
 * A narrow, read-only view of the running game.
 * <p>
 * <b>Everything here is cheap</b> — a field read or a loop bounded by the number
 * of entities on screen — so it is safe to call from every tick. Reads that scan
 * the scene or decode cache data live on {@link WorldQuery} instead, so that the
 * expensive ones cannot be mistaken for these.
 * <p>
 * This is the seam between the client and the toolkit. Toolkit code depends on
 * this interface and never on {@code deob.client}, so the fork's diff against
 * upstream stays small enough to rebase.
 * <p>
 * <b>Threading.</b> Implementations read live client fields with no locking, so
 * every getter must be called from the game thread — in practice, from inside a
 * {@link GameEvents} callback. Calling these from the Swing event dispatch thread
 * will usually work and will occasionally return a value torn between two game
 * ticks. Snapshot what you need during the callback and hand that to the UI.
 */
public interface GameState {

	boolean isLoggedIn();

	/** Current level, including any boost or drain. */
	int getSkillLevel(int skill);

	/** Real level, derived client-side from experience. */
	int getSkillBaseLevel(int skill);

	/**
	 * Experience in whole points. The server keeps one more decimal place than
	 * this and divides it away before transmitting, so this value is quantized.
	 */
	int getSkillExperience(int skill);

	/** Item ids by inventory slot, 0 for empty. Never null; a fresh array each call. */
	int[] getInventoryIds();

	/** Stack counts by inventory slot. Never null; a fresh array each call. */
	int[] getInventoryCounts();

	/** Absolute world tile X, not scene-local. */
	int getWorldX();

	/** Absolute world tile Z. Called Y here to match the plan's naming. */
	int getWorldY();

	/** Height level, 0-3. */
	int getPlane();

	/** Logged-in player's display name, or null before login. */
	String getLocalPlayerName();

	// ---- entity and view snapshots ----

	/** The local player, or null before login. */
	PlayerInfo getPlayer();

	/** Camera position and orientation. Never null. */
	CameraInfo getCamera();

	/**
	 * The local player's movement queue and destination flag. Never null.
	 * <p>
	 * Read {@link PathInfo}'s notes before trusting the name — this is not a
	 * route.
	 */
	PathInfo getPath();

	/**
	 * Every NPC in the loaded scene. Never null.
	 * <p>
	 * Bounded by what is on screen, typically well under a hundred, so this stays
	 * on the cheap side of the split.
	 */
	NpcInfo[] getNpcs();

	/** Occupied inventory slots only. Never null. */
	ItemInfo[] getInventory();

	/** Occupied worn-equipment slots only. Never null. */
	ItemInfo[] getEquipment();
}
