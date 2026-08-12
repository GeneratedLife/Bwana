package bwana.nav;

/**
 * A copy of the loaded scene's movement flags.
 * <p>
 * A copy, not a view: the client rewrites its collision map whenever the scene
 * reloads, and the mapper reads this on a different schedule.
 */
public final class SceneCollision {

	/** Scene side length in tiles. The client's scene is always square. */
	public static final int SIZE = 104;

	/** World tile of scene tile (0, 0). */
	public final int baseX;
	public final int baseZ;
	public final int plane;

	/** Movement flags, indexed {@code z * SIZE + x}. */
	public final int[] flags;

	public SceneCollision(int baseX, int baseZ, int plane, int[] flags) {
		this.baseX = baseX;
		this.baseZ = baseZ;
		this.plane = plane;
		this.flags = flags;
	}
}
