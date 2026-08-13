package bwana.model;

/**
 * An item stack lying on the ground within the loaded scene.
 */
public final class GroundItemInfo {

	public final int worldX;
	public final int worldY;
	public final int plane;
	public final int id;
	public final int count;

	public GroundItemInfo(int worldX, int worldY, int plane, int id, int count) {
		this.worldX = worldX;
		this.worldY = worldY;
		this.plane = plane;
		this.id = id;
		this.count = count;
	}
}
