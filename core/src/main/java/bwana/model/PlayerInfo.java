package bwana.model;

/**
 * A snapshot of the local player, taken on the game thread.
 * <p>
 * Immutable on purpose: the client's {@code PlayerEntity} mutates every tick, so
 * handing a reference to the UI would let it read {@code x} from one tick and
 * {@code z} from the next.
 */
public final class PlayerInfo {

	public final String name;

	/** Absolute world tile, not scene-local. */
	public final int worldX;
	public final int worldY;
	public final int plane;

	/** Facing direction, 0-2047 clockwise. Not degrees. */
	public final int yaw;

	/**
	 * The hit bar's current value, <b>not</b> hitpoints. It is a 0-30ish ratio the
	 * client uses to draw the green/red bar above the head. For your actual
	 * hitpoints use {@code getSkillLevel(Skill.HITPOINTS)}.
	 */
	public final int hitbarCurrent;
	public final int hitbarMax;

	/** Currently playing animation, or -1 when idle. */
	public final int animationId;

	public final int combatLevel;

	public PlayerInfo(String name, int worldX, int worldY, int plane, int yaw,
			int hitbarCurrent, int hitbarMax, int animationId, int combatLevel) {
		this.name = name;
		this.worldX = worldX;
		this.worldY = worldY;
		this.plane = plane;
		this.yaw = yaw;
		this.hitbarCurrent = hitbarCurrent;
		this.hitbarMax = hitbarMax;
		this.animationId = animationId;
		this.combatLevel = combatLevel;
	}

	public boolean isAnimating() {
		return this.animationId != -1;
	}
}
