package bwana.model;

/**
 * Where the camera is and which way it points.
 * <p>
 * Units are the client's, not anything friendlier: positions are in 1/128ths of a
 * tile, and angles run 0-2047 rather than 0-359. Shift right by 7 for a tile
 * coordinate; multiply by 360/2048 for degrees.
 */
public final class CameraInfo {

	/** Scene-local, in 1/128 tile units. Use {@link #worldX} to compare with entities. */
	public final int x;
	public final int y;
	public final int z;

	/**
	 * Absolute world tile, matching {@code PlayerInfo.worldX} and the rest of the
	 * API.
	 * <p>
	 * The raw {@link #x}/{@link #z} above are scene-local — the client stores the
	 * camera relative to the loaded 104x104 scene, not the world — so comparing
	 * them against an entity position silently comes out ~3000 tiles wrong. These
	 * two fields are the ones you want.
	 */
	public final int worldX;
	public final int worldY;

	/** 0-2047. Higher pitch looks further down. */
	public final int pitch;
	public final int yaw;

	public CameraInfo(int x, int y, int z, int worldX, int worldY, int pitch, int yaw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldX = worldX;
		this.worldY = worldY;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	/** Scene-local tile. See {@link #worldX} for the absolute one. */
	public int getSceneTileX() {
		return this.x >> 7;
	}

	public int getSceneTileZ() {
		return this.z >> 7;
	}

	public double getYawDegrees() {
		return this.yaw * 360.0D / 2048.0D;
	}

	public double getPitchDegrees() {
		return this.pitch * 360.0D / 2048.0D;
	}
}
