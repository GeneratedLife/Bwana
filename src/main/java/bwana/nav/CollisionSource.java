package bwana.nav;

/**
 * Supplies the loaded scene's movement flags.
 * <p>
 * The one thing the navigation graph needs from the client, and deliberately the
 * only thing: everything else — how the world is divided into regions, how a path
 * is searched, how any of it is stored — is arithmetic on world coordinates and
 * belongs on this side of the seam.
 * <p>
 * The flags themselves are the client's own, in its own encoding, because they are
 * what its pathfinder consults. Reinterpreting them into some tidier notion of
 * "walkable" would mean the graph and the client could disagree about whether a
 * step is legal, and the graph would lose every argument.
 * <p>
 * <b>Game thread only.</b>
 */
public interface CollisionSource {

	/**
	 * Copy the current scene's movement flags.
	 *
	 * @return null before a scene exists
	 */
	SceneCollision captureCollision();

	/**
	 * Whether a step onto a tile is legal, given that tile's flags and a heading.
	 * <p>
	 * Asked of the client rather than decided here so the masks live in one place —
	 * next to the pathfinder that already uses them.
	 *
	 * @param direction 0 north, 1 east, 2 south, 3 west
	 */
	boolean canEnter(int flags, int direction);
}
