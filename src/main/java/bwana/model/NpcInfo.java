package bwana.model;

/**
 * A snapshot of one NPC in the loaded scene.
 * <p>
 * The name is free here — unlike items, an NPC entity already holds a resolved
 * {@code NpcType}, so no cache lookup is needed.
 */
public final class NpcInfo {

	/** Config id, or -1 if the type has not loaded yet. */
	public final int id;

	/** May be null before the type loads. */
	public final String name;

	public final int worldX;
	public final int worldY;

	/** Tiles occupied per side; 1 for most, larger for bosses. */
	public final int size;

	/** Facing direction, 0-2047. */
	public final int yaw;

	/** Hit bar ratio, not hitpoints. See {@link PlayerInfo#hitbarCurrent}. */
	public final int hitbarCurrent;
	public final int hitbarMax;

	/** Currently playing animation, or -1. */
	public final int animationId;

	/**
	 * Who this NPC is interacting with, or -1. Values below 32768 index NPCs;
	 * above that, subtract 32768 for a player index.
	 */
	public final int targetId;

	public NpcInfo(int id, String name, int worldX, int worldY, int size, int yaw,
			int hitbarCurrent, int hitbarMax, int animationId, int targetId) {
		this.id = id;
		this.name = name;
		this.worldX = worldX;
		this.worldY = worldY;
		this.size = size;
		this.yaw = yaw;
		this.hitbarCurrent = hitbarCurrent;
		this.hitbarMax = hitbarMax;
		this.animationId = animationId;
		this.targetId = targetId;
	}

	public boolean isInteracting() {
		return this.targetId != -1;
	}
}
