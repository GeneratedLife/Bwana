package bwana.vision;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups matching pixels into connected regions, so a scatter of pixels becomes a
 * shape with a position and a size.
 * <p>
 * Two-pass: build a match mask, then flood fill it. Separating them means the
 * colour test runs exactly once per pixel instead of once per neighbour visit.
 * <p>
 * The fill is <b>iterative with an explicit stack</b>. A recursive flood fill over
 * a 512x334 viewport can recurse 170,000 deep on a large uniform area and will
 * reliably blow the JVM stack — a crash that only shows up on the frame where
 * something big and monochrome fills the screen.
 * <p>
 * Buffers are retained between calls, so steady-state detection allocates almost
 * nothing. Not thread-safe; give each worker its own instance.
 */
public final class ConnectedRegions {

	private static final byte UNMATCHED = 0;
	private static final byte MATCHED = 1;
	private static final byte VISITED = 2;

	/** Stop collecting past this many regions; a badly tuned rule can match everything. */
	public static final int MAX_REGIONS = 400;

	private byte[] mask = new byte[0];
	private int[] stack = new int[8192];
	/** Pixels of the region being filled; only populated when labels are wanted. */
	private int[] members = new int[8192];
	/** Working buffer for the separable morphology passes. */
	private byte[] scratch;
	private final float[] hsvScratch = new float[3];

	public List<Detection> find(Frame frame, ColorRule rule, boolean eightWay,
			int screenOriginX, int screenOriginY) {
		return this.find(frame, rule, eightWay, screenOriginX, screenOriginY, null, null);
	}

	/**
	 * As above, optionally emitting the intermediate stages for the debug views.
	 *
	 * @param maskOut  if non-null, receives 1 for every pixel the colour test
	 *                 passed — <b>before</b> the min-area filter, so you can see
	 *                 the noise the filter is removing
	 * @param labelOut if non-null, receives a 1-based region ordinal per pixel for
	 *                 regions that survived filtering, 0 elsewhere
	 */
	public List<Detection> find(Frame frame, ColorRule rule, boolean eightWay,
			int screenOriginX, int screenOriginY, byte[] maskOut, int[] labelOut) {
		List<Detection> var8 = new ArrayList<Detection>();
		if (frame == null || rule == null || !rule.enabled) {
			return var8;
		}
		return this.run(frame, rule, eightWay, screenOriginX, screenOriginY, maskOut, labelOut, var8);
	}

	private List<Detection> run(Frame frame, ColorRule rule, boolean eightWay,
			int screenOriginX, int screenOriginY, byte[] maskOut, int[] labelOut, List<Detection> var6) {

		int var7 = frame.width;
		int var8 = frame.height;
		int var9 = var7 * var8;
		if (this.mask.length < var9) {
			this.mask = new byte[var9];
		}
		byte[] var10 = this.mask;
		int[] var11 = frame.pixels;

		// pass one: colour test, once per pixel
		for (int var12 = 0; var12 < var9; var12++) {
			var10[var12] = rule.matches(var11[var12] & 0xFFFFFF, this.hsvScratch) ? MATCHED : UNMATCHED;
		}

		// bridge gaps in the mask before labelling, so a canopy broken up by sky
		// showing through the leaves is one region rather than five
		if (rule.gapFill > 0) {
			this.close(var10, var7, var8, rule.gapFill);
		}

		// snapshot the mask before filtering, so the debug view can show what
		// the colour test alone produced versus what survived min-area
		if (maskOut != null && maskOut.length >= var9) {
			for (int var35 = 0; var35 < var9; var35++) {
				maskOut[var35] = var10[var35];
			}
		}
		if (labelOut != null && labelOut.length >= var9) {
			java.util.Arrays.fill(labelOut, 0, var9, 0);
		}
		int var36 = 0;

		// pass two: flood fill each unvisited match
		for (int var13 = 0; var13 < var9; var13++) {
			if (var10[var13] != MATCHED) {
				continue;
			}
			int var14 = 0;
			this.stack[var14++] = var13;
			var10[var13] = VISITED;

			int var15 = var13 % var7;
			int var16 = var13 / var7;
			int var17 = var15;
			int var18 = var15;
			int var19 = var16;
			int var20 = var16;
			int var21 = 0;
			long var22 = 0L;
			long var24 = 0L;
			int var37 = 0;

			while (var14 > 0) {
				int var26 = this.stack[--var14];
				int var27 = var26 % var7;
				int var28 = var26 / var7;

				if (labelOut != null) {
					// remember membership so labels can be written only for the
					// regions that survive the min-area filter below
					if (var37 == this.members.length) {
						int[] var38 = new int[this.members.length * 2];
						System.arraycopy(this.members, 0, var38, 0, this.members.length);
						this.members = var38;
					}
					this.members[var37++] = var26;
				}

				var21++;
				var22 += (long) var27;
				var24 += (long) var28;
				if (var27 < var17) {
					var17 = var27;
				}
				if (var27 > var18) {
					var18 = var27;
				}
				if (var28 < var19) {
					var19 = var28;
				}
				if (var28 > var20) {
					var20 = var28;
				}

				for (int var29 = -1; var29 <= 1; var29++) {
					for (int var30 = -1; var30 <= 1; var30++) {
						if (var29 == 0 && var30 == 0) {
							continue;
						}
						if (!eightWay && var29 != 0 && var30 != 0) {
							continue;
						}
						int var31 = var27 + var30;
						int var32 = var28 + var29;
						if (var31 < 0 || var32 < 0 || var31 >= var7 || var32 >= var8) {
							continue;
						}
						int var33 = var32 * var7 + var31;
						if (var10[var33] != MATCHED) {
							continue;
						}
						var10[var33] = VISITED;
						if (var14 == this.stack.length) {
							int[] var34 = new int[this.stack.length * 2];
							System.arraycopy(this.stack, 0, var34, 0, this.stack.length);
							this.stack = var34;
						}
						this.stack[var14++] = var33;
					}
				}
			}

			if (var21 < rule.minArea || var6.size() >= MAX_REGIONS) {
				continue;
			}
			if (labelOut != null && labelOut.length >= var9) {
				var36++;
				for (int var39 = 0; var39 < var37; var39++) {
					labelOut[this.members[var39]] = var36;
				}
			}
			var6.add(new Detection(rule.name, var17, var19,
				var18 - var17 + 1, var20 - var19 + 1, var21,
				(int) (var22 / (long) var21), (int) (var24 / (long) var21),
				frame.canvasOriginX, frame.canvasOriginY, screenOriginX, screenOriginY));
		}
		return var6;
	}

	/**
	 * Morphological closing: dilate by {@code radius}, then erode by the same.
	 * <p>
	 * Separable — a horizontal pass then a vertical one — making it O(n*radius)
	 * rather than O(n*radius^2). Over 171,000 pixels that difference is essentially
	 * the whole cost of the operation.
	 */
	private void close(byte[] mask, int width, int height, int radius) {
		if (this.scratch == null || this.scratch.length < mask.length) {
			this.scratch = new byte[mask.length];
		}
		this.morph(mask, width, height, radius, true);
		this.morph(mask, width, height, radius, false);
	}

	/** One separable pass of dilation (any neighbour set) or erosion (all set). */
	private void morph(byte[] mask, int width, int height, int radius, boolean dilate) {
		byte[] var5 = this.scratch;

		for (int var6 = 0; var6 < height; var6++) {
			int var7 = var6 * width;
			for (int var8 = 0; var8 < width; var8++) {
				boolean var9 = !dilate;
				int var10 = var8 - radius < 0 ? 0 : var8 - radius;
				int var11 = var8 + radius >= width ? width - 1 : var8 + radius;
				for (int var12 = var10; var12 <= var11; var12++) {
					boolean var13 = mask[var7 + var12] != UNMATCHED;
					if (dilate) {
						if (var13) {
							var9 = true;
							break;
						}
					} else if (!var13) {
						var9 = false;
						break;
					}
				}
				var5[var7 + var8] = (byte) (var9 ? MATCHED : UNMATCHED);
			}
		}

		for (int var14 = 0; var14 < width; var14++) {
			for (int var15 = 0; var15 < height; var15++) {
				boolean var16 = !dilate;
				int var17 = var15 - radius < 0 ? 0 : var15 - radius;
				int var18 = var15 + radius >= height ? height - 1 : var15 + radius;
				for (int var19 = var17; var19 <= var18; var19++) {
					boolean var20 = var5[var19 * width + var14] != UNMATCHED;
					if (dilate) {
						if (var20) {
							var16 = true;
							break;
						}
					} else if (!var20) {
						var16 = false;
						break;
					}
				}
				mask[var15 * width + var14] = (byte) (var16 ? MATCHED : UNMATCHED);
			}
		}
	}
}
