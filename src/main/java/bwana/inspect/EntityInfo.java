package bwana.inspect;

/**
 * Everything the client knows about one thing under the cursor.
 * <p>
 * This is the alternative to colour detection: rather than inferring "that green
 * blob is probably a tree", the game is asked what it drew there. The answer
 * carries an id, a name and a type, which no amount of pixel matching can
 * recover.
 */
public final class EntityInfo {

	public static final int KIND_PLAYER = 0;
	public static final int KIND_NPC = 1;
	public static final int KIND_LOC = 2;
	public static final int KIND_GROUND_ITEM = 3;
	public static final int KIND_TILE = 4;

	// ---- entity ----
	public final int kind;
	/** Config id, or -1 for players and bare tiles. */
	public final int id;
	public final String name;
	/** Short summary from the type definition, or null. */
	public final String definition;

	// ---- position ----
	public final int worldX;
	public final int worldY;
	public final int plane;
	/** Scene-local position in 1/128 tile units; -1 when only a tile is known. */
	public final int localX;
	public final int localZ;
	/** Scene tile. */
	public final int tileX;
	public final int tileZ;

	// ---- rendering ----
	/** Viewport coordinates, or -1 when off screen. */
	public final int screenX;
	public final int screenY;
	/** Bounding box in viewport coordinates; width/height 0 when unknown. */
	public final int boxX;
	public final int boxY;
	public final int boxWidth;
	public final int boxHeight;
	public final boolean visible;

	// ---- game state ----
	/** Currently playing animation, or -1. */
	public final int animationId;
	/** Facing direction 0-2047, or -1 for things that do not turn. */
	public final int orientation;
	/** What it is interacting with, or null. */
	public final String interaction;

	/**
	 * This entity's stable logical identity. Never null.
	 * <p>
	 * Use this — not position, not the client slot — whenever the question is
	 * "is that the same thing I was looking at". {@link EntityHandle} owns that
	 * rule; see its notes for why it is not simply an array index.
	 */
	public final EntityHandle handle;

	/**
	 * Builds the handle from the record's own fields.
	 * <p>
	 * Available so scenery, ground items and tiles — whose identity is entirely
	 * derivable from what is already here — do not each have to construct one.
	 * Creatures must pass a handle explicitly, because their slot is not a field
	 * of this record.
	 */
	public EntityInfo(int kind, int id, String name, String definition,
			int worldX, int worldY, int plane, int localX, int localZ, int tileX, int tileZ,
			int screenX, int screenY, int boxX, int boxY, int boxWidth, int boxHeight,
			boolean visible, int animationId, int orientation, String interaction) {
		this(kind, id, name, definition, worldX, worldY, plane, localX, localZ, tileX, tileZ,
			screenX, screenY, boxX, boxY, boxWidth, boxHeight, visible, animationId, orientation,
			interaction, defaultHandle(kind, id, name, worldX, worldY, plane));
	}

	public EntityInfo(int kind, int id, String name, String definition,
			int worldX, int worldY, int plane, int localX, int localZ, int tileX, int tileZ,
			int screenX, int screenY, int boxX, int boxY, int boxWidth, int boxHeight,
			boolean visible, int animationId, int orientation, String interaction,
			EntityHandle handle) {
		this.handle = handle == null
			? defaultHandle(kind, id, name, worldX, worldY, plane) : handle;
		this.kind = kind;
		this.id = id;
		this.name = name;
		this.definition = definition;
		this.worldX = worldX;
		this.worldY = worldY;
		this.plane = plane;
		this.localX = localX;
		this.localZ = localZ;
		this.tileX = tileX;
		this.tileZ = tileZ;
		this.screenX = screenX;
		this.screenY = screenY;
		this.boxX = boxX;
		this.boxY = boxY;
		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;
		this.visible = visible;
		this.animationId = animationId;
		this.orientation = orientation;
		this.interaction = interaction;
	}

	private static EntityHandle defaultHandle(int kind, int id, String name,
			int worldX, int worldY, int plane) {
		if (kind == KIND_GROUND_ITEM) {
			return EntityHandle.forGroundItem(id, worldX, worldY, plane, name);
		}
		if (kind == KIND_TILE) {
			return EntityHandle.forTile(worldX, worldY, plane);
		}
		// Creatures reaching this point have no slot to key on, so they would get a
		// world-tile identity that breaks the moment they walk. Callers that build
		// creatures pass a handle; this stays correct for everything fixed.
		return EntityHandle.forLoc(id, worldX, worldY, plane, name);
	}

	/**
	 * The client slot this entity was last seen at, or -1.
	 * <p>
	 * For display only. Comparing slots to decide whether two records are the same
	 * entity is exactly the mistake {@link EntityHandle} exists to prevent — use
	 * {@code handle.matches(other.handle)}.
	 */
	public int getSlot() {
		return this.handle.getSlot();
	}

	/** True if this record describes the same logical entity as another. */
	public boolean isSameEntity(EntityInfo other) {
		return other != null && this.handle.matches(other.handle);
	}

	public String getKindName() {
		if (this.kind == KIND_PLAYER) {
			return "Player";
		}
		if (this.kind == KIND_NPC) {
			return "NPC";
		}
		if (this.kind == KIND_LOC) {
			return "Object";
		}
		if (this.kind == KIND_GROUND_ITEM) {
			return "Ground item";
		}
		return "Tile";
	}

	/** Orientation in degrees, or -1. */
	public int getOrientationDegrees() {
		return this.orientation < 0 ? -1 : this.orientation * 360 / 2048;
	}
}
