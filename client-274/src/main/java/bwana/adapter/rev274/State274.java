package bwana.adapter.rev274;

import java.util.ArrayList;

import bwana.GameState;
import bwana.WorldQuery;
import bwana.model.CameraInfo;
import bwana.model.GroundItemInfo;
import bwana.model.ItemInfo;
import bwana.model.NpcInfo;
import bwana.model.PathInfo;
import bwana.model.PlayerInfo;
import jagex2.client.Client;
import jagex2.config.ObjType;
import jagex2.dash3d.ClientObj;
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
public final class State274 implements GameState, WorldQuery {

	private static final int[] NO_SLOTS = new int[0];

	private static final GroundItemInfo[] NO_GROUND_ITEMS = new GroundItemInfo[0];

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

	// --- not yet ----------------------------------------------------------------

	// These need more than a field rename, so they refuse rather than answer.
	// Returning an empty array would be a plausible lie -- "no npcs here", "empty
	// inventory" -- and a plausible lie is the failure this codebase keeps being
	// rewritten to avoid. Nothing calls Bwana.start in this module yet, so nothing
	// reaches them at runtime.

	public int[] getInventoryIds() {
		throw new UnsupportedOperationException("274: inventory needs the IfType container mapping");
	}

	public int[] getInventoryCounts() {
		throw new UnsupportedOperationException("274: inventory needs the IfType container mapping");
	}

	public ItemInfo[] getInventory() {
		throw new UnsupportedOperationException("274: inventory needs the IfType container mapping");
	}

	public ItemInfo[] getEquipment() {
		throw new UnsupportedOperationException("274: equipment needs the IfType container mapping");
	}

	public NpcInfo[] getNpcs() {
		throw new UnsupportedOperationException("274: npcs need the ClientNpc and NpcType field mapping");
	}

	public PlayerInfo getPlayer() {
		throw new UnsupportedOperationException("274: player info needs the ClientPlayer field mapping");
	}
}
