package bwana.inspect;

/**
 * A stable logical identity for one thing in the world.
 * <p>
 * <b>The point of this class is that a slot is not an identity.</b> The client
 * stores creatures in fixed-size arrays and reuses a slot the moment its occupant
 * despawns, so "the goblin at index 41" silently becomes a different goblin — or a
 * chicken — with no event to notice. Before this existed, the rule for coping with
 * that (pair the slot with the type id, and for players use the name instead) was
 * written out separately in the tracker, in the action runner and in the action
 * record. Three copies of a rule is three chances for one of them to be subtly
 * wrong, and the interesting failures here are all silent.
 * <p>
 * So the rule lives here once, in {@link #matches}, and callers ask rather than
 * reimplement.
 * <p>
 * Identity is defined per kind, because the honest answer differs:
 * <ul>
 * <li><b>Scenery</b> — fixed in the world. Identity is its type plus the world
 * tile it stands on. Deliberately <i>world</i> and not scene-local: the client
 * rebuilds its scene when you cross a region boundary and shifts the origin
 * underneath you, at which point a scene-local tile refers to somewhere else
 * entirely.</li>
 * <li><b>NPCs</b> — type plus slot. The slot is not sufficient alone, and the type
 * is not sufficient alone; together they are enough to catch a reused slot.</li>
 * <li><b>Players</b> — the name, and only the name. Player slots are reassigned
 * freely as people come and go, so a slot-based identity would be wrong far more
 * often than for NPCs.</li>
 * <li><b>Ground items and tiles</b> — like scenery, type plus world tile.</li>
 * </ul>
 * <p>
 * <b>What this cannot do.</b> It cannot survive a genuine despawn. If an NPC
 * leaves the scene and comes back, the client offers nothing to connect the two,
 * so the returning creature is a new logical entity and this will say so. That is
 * a limitation of the data, not of the abstraction, and it is better stated than
 * papered over.
 * <p>
 * Immutable, and free of any client type, so a future adapter for another revision
 * constructs these from whatever <i>it</i> uses for identity without the toolkit
 * needing to know what that is.
 */
public final class EntityHandle {

	/** Matches {@link EntityInfo}'s kind constants. */
	private final int kind;

	/** Config id, or -1 where the kind has none (players, bare tiles). */
	private final int typeId;

	/**
	 * Where the client currently keeps this entity, or -1 for things that live in
	 * the scene rather than an array.
	 * <p>
	 * <b>A lookup hint, not an identity.</b> It exists so an adapter can find the
	 * entity in one array read instead of a scan, and every use of it must be
	 * followed by a {@link #matches} check against what was found. Nothing outside
	 * an adapter should read it for any purpose but display.
	 */
	private final int slot;

	/** Absolute world tile, for entities fixed in the world. -1 otherwise. */
	private final int worldX;
	private final int worldY;
	private final int plane;

	/** Display name; the actual identity for players. */
	private final String name;

	private EntityHandle(int kind, int typeId, int slot, int worldX, int worldY, int plane,
			String name) {
		this.kind = kind;
		this.typeId = typeId;
		this.slot = slot;
		this.worldX = worldX;
		this.worldY = worldY;
		this.plane = plane;
		this.name = name;
	}

	/** Scenery, fixed at a world tile. */
	public static EntityHandle forLoc(int typeId, int worldX, int worldY, int plane, String name) {
		return new EntityHandle(EntityInfo.KIND_LOC, typeId, -1, worldX, worldY, plane, name);
	}

	/** An NPC, identified by type and current slot together. */
	public static EntityHandle forNpc(int typeId, int slot, String name) {
		return new EntityHandle(EntityInfo.KIND_NPC, typeId, slot, -1, -1, -1, name);
	}

	/** A player, identified by name; the slot is carried only as a lookup hint. */
	public static EntityHandle forPlayer(String name, int slot) {
		return new EntityHandle(EntityInfo.KIND_PLAYER, -1, slot, -1, -1, -1, name);
	}

	/** A ground item stack, fixed at a world tile. */
	public static EntityHandle forGroundItem(int typeId, int worldX, int worldY, int plane,
			String name) {
		return new EntityHandle(EntityInfo.KIND_GROUND_ITEM, typeId, -1, worldX, worldY, plane,
			name);
	}

	/** A bare tile, which is only ever itself. */
	public static EntityHandle forTile(int worldX, int worldY, int plane) {
		return new EntityHandle(EntityInfo.KIND_TILE, -1, -1, worldX, worldY, plane, "Tile");
	}

	public int getKind() {
		return this.kind;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public String getName() {
		return this.name;
	}

	public int getWorldX() {
		return this.worldX;
	}

	public int getWorldY() {
		return this.worldY;
	}

	public int getPlane() {
		return this.plane;
	}

	/**
	 * The client slot this handle was last seen at, or -1.
	 * <p>
	 * For adapters and debug readouts only — see the field's note. If you are
	 * comparing this to decide whether two records are the same entity, you want
	 * {@link #matches} instead, and the difference is not cosmetic.
	 */
	public int getSlot() {
		return this.slot;
	}

	/** True when finding this entity means looking in an array rather than the scene. */
	public boolean isSlotBased() {
		return this.kind == EntityInfo.KIND_NPC || this.kind == EntityInfo.KIND_PLAYER;
	}

	/**
	 * The one place that decides whether two records describe the same logical thing.
	 * <p>
	 * Note what is deliberately <i>not</i> compared: the slot for players, and
	 * position for creatures. A walking goblin is the same goblin, and a player who
	 * gets moved to a different array index is the same player. Comparing those
	 * would make a target unlockable the moment it did the one thing targets do.
	 */
	public boolean matches(EntityHandle other) {
		if (other == null || other.kind != this.kind) {
			return false;
		}
		if (this.kind == EntityInfo.KIND_PLAYER) {
			return this.name != null && this.name.equalsIgnoreCase(other.name);
		}
		if (this.kind == EntityInfo.KIND_NPC) {
			return this.typeId == other.typeId && this.slot == other.slot;
		}
		// scenery, ground items, tiles: fixed in the world
		return this.typeId == other.typeId && this.worldX == other.worldX
			&& this.worldY == other.worldY && this.plane == other.plane;
	}

	/**
	 * The same entity, now found at a different slot.
	 * <p>
	 * Only meaningful for players, whose identity survives being moved. Used by the
	 * debug readout to show that a slot change did not break the lock — which is
	 * the behaviour this whole class exists to guarantee, and therefore the thing
	 * most worth being able to see happen.
	 */
	public EntityHandle atSlot(int newSlot) {
		if (newSlot == this.slot) {
			return this;
		}
		return new EntityHandle(this.kind, this.typeId, newSlot, this.worldX, this.worldY,
			this.plane, this.name);
	}

	/** Short form for debug output: what identifies this, not everything about it. */
	public String describe() {
		if (this.kind == EntityInfo.KIND_PLAYER) {
			return "player \"" + this.name + "\"";
		}
		if (this.kind == EntityInfo.KIND_NPC) {
			return "npc type " + this.typeId + " @ slot " + this.slot;
		}
		if (this.kind == EntityInfo.KIND_TILE) {
			return "tile " + this.worldX + "," + this.worldY + " plane " + this.plane;
		}
		String var1 = this.kind == EntityInfo.KIND_LOC ? "loc" : "obj";
		return var1 + " type " + this.typeId + " @ " + this.worldX + "," + this.worldY
			+ " plane " + this.plane;
	}

	/** What identity is keyed on for this kind, in words. For the debug readout. */
	public String describeRule() {
		if (this.kind == EntityInfo.KIND_PLAYER) {
			return "name (slot ignored)";
		}
		if (this.kind == EntityInfo.KIND_NPC) {
			return "type + slot";
		}
		return "type + world tile";
	}

	public boolean equals(Object other) {
		return other instanceof EntityHandle && this.matches((EntityHandle) other);
	}

	public int hashCode() {
		if (this.kind == EntityInfo.KIND_PLAYER) {
			return this.name == null ? 0 : this.name.toLowerCase().hashCode();
		}
		if (this.kind == EntityInfo.KIND_NPC) {
			return this.kind * 31 + this.typeId * 17 + this.slot;
		}
		return this.kind * 31 + this.typeId * 17 + this.worldX * 7 + this.worldY * 3 + this.plane;
	}

	public String toString() {
		return this.describe();
	}
}
