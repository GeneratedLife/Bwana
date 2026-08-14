package bwana.adapter.rev274;

import java.util.ArrayList;

import bwana.GameState;
import bwana.WorldQuery;
import bwana.vision.Frame;
import bwana.vision.FrameSource;
import bwana.widget.WidgetSource;
import bwana.model.CameraInfo;
import bwana.model.GroundItemInfo;
import bwana.model.ItemInfo;
import bwana.model.NpcInfo;
import bwana.model.PathInfo;
import bwana.model.PlayerInfo;
import jagex2.client.Client;
import jagex2.config.ObjType;
import jagex2.config.NpcType;
import jagex2.config.IfType;
import jagex2.dash3d.ClientNpc;
import jagex2.dash3d.ClientObj;
import jagex2.dash3d.ClientPlayer;
import jagex2.graphics.PixMap;
import jagex2.datastruct.LinkList;
import jagex2.datastruct.Linkable;

/**
 * Reads rev 274's client for the toolkit.
 * <p>
 * <b>Outside the client, on purpose.</b> 225 does this by having {@code client}
 * implement the interfaces itself, which buries 2,659 lines of toolkit code in a
 * file the fork does not own. Nothing forces that: the client carries no access
 * control at all — 67 public classes, 2,155 public members, not one {@code private}
 * — so an adapter can sit here and read {@link Client} from outside. See
 * {@code bwana-adapter-extraction.md}. 274 is written the way 225 should be.
 * <p>
 * <b>Game thread only</b>, like everything else that reads the client: the fields
 * below are rewritten as packets arrive.
 * <p>
 * <b>Partial.</b> Twelve of the eighteen methods are implemented; the rest throw
 * rather than answer. Every mapping here was traced in 274's own source, never
 * carried over from 225 on the assumption that a name meant the same thing — see
 * {@code ../README.md} for what is left and why those six need more than a rename.
 */
public final class State274 implements GameState, WorldQuery, FrameSource, WidgetSource {

	private static final int[] NO_SLOTS = new int[0];

	private static final GroundItemInfo[] NO_GROUND_ITEMS = new GroundItemInfo[0];

	private static final NpcInfo[] NO_NPCS = new NpcInfo[0];

	private static final ItemInfo[] NO_ITEMS = new ItemInfo[0];

	/**
	 * Where the game view sits on the canvas. <b>Not 225's (8, 11)</b> — 274 draws
	 * with {@code areaViewport.draw(4, graphics, 4)}, and {@code PixMap.draw} takes
	 * x, graphics, y in that order, so it is (4, 4). The canvas differs too: 274
	 * opens 765x503 where 225 opens 532x789.
	 */
	private static final int VIEWPORT_X = 4;

	private static final int VIEWPORT_Y = 4;

	/**
	 * Sidebar tabs holding the inventory and the worn equipment.
	 * <p>
	 * <b>The one mapping here resting on correspondence rather than a statement in
	 * the source.</b> 274 names the array sideOverlayId where 225 says
	 * tabInterfaceId, and neither client labels a tab. What lines them up: both are
	 * {@code int[15]} filled with -1, both are read at the same set of literal
	 * indices, and both pair index i with {@code sideicons[i]} in the same redraw
	 * and the same click test — only the pixel coordinates differ, which follows
	 * from the different canvas. 225 and 274 are six months apart with no tab added
	 * between them.
	 * <p>
	 * If an inventory read ever comes back as somebody else's tab, this is the first
	 * thing to doubt.
	 */
	private static final int INVENTORY_TAB = 3;

	private static final int EQUIPMENT_TAB = 4;

	private final Client client;

	public State274(Client client) {
		this.client = client;
	}

	// --- what is true right now ------------------------------------------------

	public boolean isLoggedIn() {
		return this.client.ingame;
	}

	/** 274 calls 225's skillLevel statEffectiveLevel: the boosted value. */
	public int getSkillLevel(int skill) {
		int[] levels = this.client.statEffectiveLevel;
		return skill >= 0 && skill < levels.length ? levels[skill] : 0;
	}

	public int getSkillBaseLevel(int skill) {
		int[] levels = this.client.statBaseLevel;
		return skill >= 0 && skill < levels.length ? levels[skill] : 0;
	}

	public int getSkillExperience(int skill) {
		int[] xp = this.client.statXP;
		return skill >= 0 && skill < xp.length ? xp[skill] : 0;
	}

	/**
	 * Scene-local position plus the scene origin, exactly as 225 does it. 274 calls
	 * the origin mapBuildBaseX rather than sceneBaseTileX; the client itself uses
	 * the same expression at Client.java:2349.
	 */
	public int getWorldX() {
		return Client.localPlayer == null ? -1 : (Client.localPlayer.x >> 7) + this.client.mapBuildBaseX;
	}

	public int getWorldY() {
		return Client.localPlayer == null ? -1 : (Client.localPlayer.z >> 7) + this.client.mapBuildBaseZ;
	}

	/**
	 * 274's name for this is minusedlevel, which reads like something else entirely.
	 * It is the plane: both clients fill it from a 2-bit field of the same packet
	 * (225 {@code currentLevel = gBit(9, 2)}, 274 {@code minusedlevel = gBit(2)}),
	 * and 274 indexes {@code collision[]} and the heightmap with it.
	 */
	public int getPlane() {
		return this.client.minusedlevel;
	}

	public String getLocalPlayerName() {
		return Client.localPlayer == null ? null : Client.localPlayer.name;
	}

	public CameraInfo getCamera() {
		// Scene-local, converted so it lines up with entity positions.
		return new CameraInfo(this.client.camX, this.client.camY, this.client.camZ,
			(this.client.camX >> 7) + this.client.mapBuildBaseX,
			(this.client.camZ >> 7) + this.client.mapBuildBaseZ,
			this.client.camPitch, this.client.camYaw);
	}

	/**
	 * 225's pathLength/pathTileX/pathTileZ/pathRunning are routeLength/routeX/
	 * routeZ/routeRun here, on the shared entity base class.
	 * <p>
	 * The destination convention carries over, and was checked rather than assumed:
	 * 274 zeroes minimapFlagX once the player stands on it (Client.java:7576), the
	 * same way 225 zeroes flagSceneTileX, so 0 means "no destination" in both.
	 */
	public PathInfo getPath() {
		if (Client.localPlayer == null) {
			return new PathInfo(NO_SLOTS, NO_SLOTS, new boolean[0], -1, -1);
		}
		int steps = Client.localPlayer.routeLength;
		if (steps < 0) {
			steps = 0;
		}
		if (steps > Client.localPlayer.routeX.length) {
			steps = Client.localPlayer.routeX.length;
		}
		int[] queuedX = new int[steps];
		int[] queuedZ = new int[steps];
		boolean[] running = new boolean[steps];
		for (int step = 0; step < steps; step++) {
			queuedX[step] = Client.localPlayer.routeX[step] + this.client.mapBuildBaseX;
			queuedZ[step] = Client.localPlayer.routeZ[step] + this.client.mapBuildBaseZ;
			running[step] = Client.localPlayer.routeRun[step];
		}
		int destX = this.client.minimapFlagX == 0 ? -1 : this.client.minimapFlagX + this.client.mapBuildBaseX;
		int destY = this.client.minimapFlagX == 0 ? -1 : this.client.minimapFlagZ + this.client.mapBuildBaseZ;
		return new PathInfo(queuedX, queuedZ, running, destX, destY);
	}

	// --- what is out there ------------------------------------------------------

	/**
	 * 225 walks levelObjStacks; 274 calls the same structure groundObj, and its
	 * LinkList iterates with head()/next() rather than head()/next(1).
	 * <p>
	 * The cursor lives on the list, which is why this may only run on the game
	 * thread — two readers would walk each other's iteration.
	 */
	public GroundItemInfo[] getGroundItems() {
		if (!this.client.ingame || this.client.groundObj == null) {
			return NO_GROUND_ITEMS;
		}
		int plane = this.client.minusedlevel;
		LinkList[][] stacks = this.client.groundObj[plane];
		ArrayList<GroundItemInfo> found = new ArrayList<GroundItemInfo>();
		for (int x = 0; x < stacks.length; x++) {
			for (int z = 0; z < stacks[x].length; z++) {
				LinkList stack = stacks[x][z];
				if (stack == null) {
					continue;
				}
				for (Linkable link = stack.head(); link != null; link = stack.next()) {
					if (!(link instanceof ClientObj)) {
						continue;
					}
					ClientObj obj = (ClientObj) link;
					found.add(new GroundItemInfo(x + this.client.mapBuildBaseX,
						z + this.client.mapBuildBaseZ, plane, obj.id, obj.count));
				}
			}
		}
		return found.toArray(new GroundItemInfo[found.size()]);
	}

	/** 274's ObjType accessor is list(int) where 225's is get(int). */
	public String getItemName(int id) {
		if (id < 0) {
			return null;
		}
		ObjType type = ObjType.list(id);
		return type == null ? null : type.name;
	}

	// --- what is out there, continued -------------------------------------------

	/**
	 * npc[] is a sparse 16384 array and npcIds[] holds the live indices, exactly as
	 * 225 keeps npcs[] and npcIds[]. NpcType.id is declared long in both; ids fit
	 * an int comfortably.
	 * <p>
	 * 225's targetId is faceEntity here — same meaning, same -1 for "nobody", and
	 * the animation it calls primarySeqId is primaryAnim.
	 */
	public NpcInfo[] getNpcs() {
		if (!this.client.ingame) {
			return NO_NPCS;
		}
		NpcInfo[] found = new NpcInfo[this.client.npcCount];
		int kept = 0;
		for (int i = 0; i < this.client.npcCount; i++) {
			ClientNpc npc = this.client.npc[this.client.npcIds[i]];
			if (npc == null) {
				continue;
			}
			NpcType type = npc.type;
			found[kept++] = new NpcInfo(type == null ? -1 : (int) type.id,
				type == null ? null : type.name,
				(npc.x >> 7) + this.client.mapBuildBaseX,
				(npc.z >> 7) + this.client.mapBuildBaseZ,
				npc.size, npc.yaw,
				npc.health, npc.totalHealth,
				npc.primaryAnim, npc.faceEntity);
		}
		if (kept == found.length) {
			return found;
		}
		NpcInfo[] exact = new NpcInfo[kept];
		System.arraycopy(found, 0, exact, 0, kept);
		return exact;
	}

	public PlayerInfo getPlayer() {
		ClientPlayer player = Client.localPlayer;
		if (player == null) {
			return null;
		}
		return new PlayerInfo(player.name,
			(player.x >> 7) + this.client.mapBuildBaseX,
			(player.z >> 7) + this.client.mapBuildBaseZ,
			this.client.minusedlevel, player.yaw,
			player.health, player.totalHealth,
			player.primaryAnim, player.combatLevel);
	}

	// --- containers --------------------------------------------------------------

	/**
	 * The interface backing a sidebar tab, or null.
	 * <p>
	 * 274 calls 225's Component IfType and its instances list; the slot arrays are
	 * linkObjType and linkObjNumber where 225 has invSlotObjId and invSlotObjCount,
	 * and layer is layerId. The search is the same: the tab names an interface id,
	 * and the container is whichever component sits on that layer and actually
	 * carries slots.
	 */
	private IfType container(int tab) {
		if (!this.client.ingame || IfType.list == null) {
			return null;
		}
		int layer = this.client.sideOverlayId[tab];
		if (layer == -1) {
			return null;
		}
		for (int i = 0; i < IfType.list.length; i++) {
			IfType candidate = IfType.list[i];
			if (candidate != null && candidate.layerId == layer && candidate.linkObjType != null) {
				return candidate;
			}
		}
		return null;
	}

	/** Slot ids, one per slot, -1 for empty. */
	public int[] getInventoryIds() {
		IfType inventory = this.container(INVENTORY_TAB);
		if (inventory == null) {
			return NO_SLOTS;
		}
		int[] ids = new int[inventory.linkObjType.length];
		for (int slot = 0; slot < ids.length; slot++) {
			// Stored as id+1 so that 0 can mean empty -- the same encoding 225 uses,
			// and confirmed here by the client's own ObjType.list(linkObjType - 1).
			ids[slot] = inventory.linkObjType[slot] - 1;
		}
		return ids;
	}

	public int[] getInventoryCounts() {
		IfType inventory = this.container(INVENTORY_TAB);
		if (inventory == null || inventory.linkObjNumber == null) {
			return NO_SLOTS;
		}
		int[] counts = new int[inventory.linkObjNumber.length];
		System.arraycopy(inventory.linkObjNumber, 0, counts, 0, counts.length);
		return counts;
	}

	public ItemInfo[] getInventory() {
		return this.readContainer(INVENTORY_TAB);
	}

	public ItemInfo[] getEquipment() {
		return this.readContainer(EQUIPMENT_TAB);
	}

	/** Occupied slots only, so an empty slot is absent rather than an id of -1. */
	private ItemInfo[] readContainer(int tab) {
		IfType from = this.container(tab);
		if (from == null || from.linkObjNumber == null) {
			return NO_ITEMS;
		}
		int occupied = 0;
		for (int slot = 0; slot < from.linkObjType.length; slot++) {
			if (from.linkObjType[slot] > 0) {
				occupied++;
			}
		}
		ItemInfo[] items = new ItemInfo[occupied];
		int kept = 0;
		for (int slot = 0; slot < from.linkObjType.length; slot++) {
			if (from.linkObjType[slot] > 0) {
				items[kept++] = new ItemInfo(slot, from.linkObjType[slot] - 1, from.linkObjNumber[slot]);
			}
		}
		return items;
	}

	// --- what is on screen ---------------------------------------------------------

	/**
	 * A copy of the game view's pixels, taken on the game thread.
	 * <p>
	 * Copied rather than handed over: the client keeps drawing into that array, and
	 * a vision pass reading it while the next frame lands would see half of each.
	 * <p>
	 * 274's PixMap calls the pixel array data where 225 calls it pixels, and makes
	 * it final along with width and height.
	 */
	public Frame captureViewport() {
		PixMap viewport = this.client.areaViewport;
		if (viewport == null || viewport.data == null) {
			return null;
		}
		int[] pixels = new int[viewport.data.length];
		System.arraycopy(viewport.data, 0, pixels, 0, pixels.length);
		return new Frame(pixels, viewport.width, viewport.height,
			VIEWPORT_X, VIEWPORT_Y, System.currentTimeMillis());
	}

	// --- interfaces ----------------------------------------------------------------

	/**
	 * 225's viewportInterfaceId is mainModalId here. Same thing, and not guessed:
	 * both clients assign it in the same breath as the report-abuse interface, from
	 * the layer of whichever component carries clientCode 600.
	 */
	public int getViewportInterfaceId() {
		return this.client.ingame && this.client.mainModalId > 0 ? this.client.mainModalId : NONE;
	}

	/** chatInterfaceId in 225. */
	public int getChatInterfaceId() {
		return this.client.ingame && this.client.chatComId > 0 ? this.client.chatComId : NONE;
	}

	public boolean isAnyInterfaceOpen() {
		return this.getViewportInterfaceId() != NONE || this.getChatInterfaceId() != NONE;
	}

	/** IfType.list is 225's Component.instances, indexed by component id. */
	private IfType componentAt(int componentId) {
		if (componentId < 0 || IfType.list == null || componentId >= IfType.list.length) {
			return null;
		}
		return IfType.list[componentId];
	}

	public int[] getContainerIds(int componentId) {
		IfType component = this.componentAt(componentId);
		if (component == null || component.linkObjType == null) {
			return NO_SLOTS;
		}
		int[] ids = new int[component.linkObjType.length];
		for (int slot = 0; slot < ids.length; slot++) {
			// stored as id+1 so that 0 can mean empty
			ids[slot] = component.linkObjType[slot] - 1;
		}
		return ids;
	}

	public int[] getContainerCounts(int componentId) {
		IfType component = this.componentAt(componentId);
		if (component == null || component.linkObjNumber == null) {
			return NO_SLOTS;
		}
		int[] counts = new int[component.linkObjNumber.length];
		System.arraycopy(component.linkObjNumber, 0, counts, 0, counts.length);
		return counts;
	}

	public int[] getContainersUnder(int interfaceId) {
		if (interfaceId < 0 || IfType.list == null) {
			return NO_SLOTS;
		}
		ArrayList<Integer> found = new ArrayList<Integer>();
		for (int i = 0; i < IfType.list.length; i++) {
			IfType candidate = IfType.list[i];
			if (candidate != null && candidate.layerId == interfaceId && candidate.linkObjType != null) {
				found.add(Integer.valueOf(candidate.id));
			}
		}
		return toIntArray(found);
	}

	/** 225 calls the item-option array iops; 274 has it in the singular. */
	public int[] getContainersWithOption(String option) {
		if (option == null || IfType.list == null) {
			return NO_SLOTS;
		}
		String wanted = option.toLowerCase();
		ArrayList<Integer> found = new ArrayList<Integer>();
		for (int i = 0; i < IfType.list.length; i++) {
			IfType candidate = IfType.list[i];
			if (candidate == null || candidate.linkObjType == null || candidate.iop == null) {
				continue;
			}
			for (int op = 0; op < candidate.iop.length; op++) {
				if (candidate.iop[op] != null && candidate.iop[op].toLowerCase().indexOf(wanted) >= 0) {
					found.add(Integer.valueOf(candidate.id));
					break;
				}
			}
		}
		return toIntArray(found);
	}

	public String getWidgetText(int componentId) {
		IfType component = this.componentAt(componentId);
		return component == null ? null : component.text;
	}

	/** Absent counts as hidden: a component that is not there is not on screen. */
	public boolean isWidgetHidden(int componentId) {
		IfType component = this.componentAt(componentId);
		return component == null || component.hide;
	}

	private static int[] toIntArray(ArrayList<Integer> from) {
		int[] out = new int[from.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = from.get(i).intValue();
		}
		return out;
	}
}
