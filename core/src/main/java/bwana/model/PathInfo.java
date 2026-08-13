package bwana.model;

/**
 * Movement state for an entity.
 * <p>
 * <b>Read this before using it — the name promises more than the client can
 * deliver.</b> {@link #queuedX} is not a route to your destination. It is the
 * client's interpolation queue: the server streams movement a step or two at a
 * time, and the client pushes each onto a 10-entry queue to animate between
 * tiles. The client never knows the full path, so {@code queuedX.length} is
 * almost always 1 or 2 even on a long walk.
 * <p>
 * If you want "where am I heading", that is {@link #destX} — the yellow click
 * flag — which is set from the click and cleared on arrival.
 */
public final class PathInfo {

	/** Queued steps in absolute world tiles. Index 0 is the most recent target. */
	public final int[] queuedX;
	public final int[] queuedZ;

	/** Whether each queued step is being run rather than walked. */
	public final boolean[] running;

	/** Destination flag in world tiles, or -1 when there is no flag. */
	public final int destX;
	public final int destY;

	public PathInfo(int[] queuedX, int[] queuedZ, boolean[] running, int destX, int destY) {
		this.queuedX = queuedX;
		this.queuedZ = queuedZ;
		this.running = running;
		this.destX = destX;
		this.destY = destY;
	}

	public boolean hasDestination() {
		return this.destX != -1;
	}

	/** True while there are steps left to interpolate through. */
	public boolean isMoving() {
		return this.queuedX.length > 0;
	}
}
