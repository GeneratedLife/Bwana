package bwana.inspect;

/**
 * Every number involved in turning an object's world position into a point on
 * screen, so a mismatch can be attributed rather than guessed at.
 * <p>
 * The question this exists to answer is which anchor a coordinate represents.
 * The client places a loc's model at the <b>centre of its footprint</b>
 * ({@code tile * 128 + size * 64}) at ground height, and the model then extends
 * upward from there — so projecting that anchor gives a point at the base of a
 * tree, not on its canopy. This record exposes the ground anchor, the model
 * centre and the model top side by side so the difference is visible instead of
 * inferred.
 * <p>
 * All screen values are viewport-relative.
 */
public final class ProjectionDebug {

	// ---- object ----
	public String name;
	public int id = -1;
	public int worldX;
	public int worldY;
	public int plane;
	/** Scene tile the loc is registered on. */
	public int tileX;
	public int tileZ;
	/** Footprint in tiles, after rotation is applied. */
	public int width = 1;
	public int length = 1;
	public int shape = -1;
	public int rotation = -1;
	/** Distance from the player in tiles, Chebyshev. */
	public int distance = -1;

	// ---- the anchor the client itself uses ----
	/** Footprint centre in 1/128 tile units, which is where the model is placed. */
	public int anchorX;
	public int anchorZ;
	/** Terrain height at the anchor. */
	public int anchorGroundY;

	// ---- model ----
	public boolean modelAvailable;
	/** Height above the anchor, in the same units. 0 when unknown. */
	public int modelMaxY;
	public int modelMinY;
	/** Horizontal extent about the anchor. */
	public int modelRadius;
	public int modelMinX;
	public int modelMaxX;

	// ---- projections of the same object at different anchors ----
	public int groundScreenX = -1;
	public int groundScreenY = -1;
	public int centreScreenX = -1;
	public int centreScreenY = -1;
	public int topScreenX = -1;
	public int topScreenY = -1;
	/** Tile origin, the south-west corner rather than the centre. */
	public int originScreenX = -1;
	public int originScreenY = -1;

	/** Screen box of the footprint at ground level, which is what was being used. */
	public int footprintBoxX;
	public int footprintBoxY;
	public int footprintBoxW;
	public int footprintBoxH;

	/** Screen box of the model's actual bounding volume. */
	public boolean modelBoxValid;
	public int modelBoxX;
	public int modelBoxY;
	public int modelBoxW;
	public int modelBoxH;

	/** The point currently handed to the mouse. */
	public int targetScreenX = -1;
	public int targetScreenY = -1;
	public String targetDescription;

	// ---- observer ----
	public int playerWorldX;
	public int playerWorldY;
	public int playerTileX;
	public int playerTileZ;
	public int cameraX;
	public int cameraY;
	public int cameraZ;
	public int cameraPitch;
	public int cameraYaw;

	public int getCameraPitchDegrees() {
		return this.cameraPitch * 360 / 2048;
	}

	public int getCameraYawDegrees() {
		return this.cameraYaw * 360 / 2048;
	}
}
