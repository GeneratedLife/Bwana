package jagex2.dash3d;

import deob.ObfuscatedName;

@ObfuscatedName("i")
public final class Ground {

	@ObfuscatedName("i.b")
	public final int[] vertexX;

	@ObfuscatedName("i.c")
	public final int[] vertexY;

	@ObfuscatedName("i.d")
	public final int[] vertexZ;

	@ObfuscatedName("i.e")
	public final int[] faceColourA;

	@ObfuscatedName("i.f")
	public final int[] faceColourB;

	@ObfuscatedName("i.g")
	public final int[] faceColourC;

	@ObfuscatedName("i.h")
	public final int[] faceVertexA;

	@ObfuscatedName("i.i")
	public final int[] faceVertexB;

	@ObfuscatedName("i.j")
	public final int[] faceVertexC;

	@ObfuscatedName("i.k")
	public int[] faceTexture;

	@ObfuscatedName("i.l")
	public boolean flat = true;

	@ObfuscatedName("i.m")
	public final int overlayShape;

	@ObfuscatedName("i.n")
	public final int overlayRotation;

	@ObfuscatedName("i.o")
	public final int minimapOverlay;

	@ObfuscatedName("i.p")
	public final int minimapUnderlay;

	@ObfuscatedName("i.q")
	public static final int[] drawVertexX = new int[6];

	@ObfuscatedName("i.r")
	public static final int[] drawVertexY = new int[6];

	@ObfuscatedName("i.s")
	public static final int[] drawTextureVertexX = new int[6];

	@ObfuscatedName("i.t")
	public static final int[] drawTextureVertexY = new int[6];

	@ObfuscatedName("i.u")
	public static final int[] drawTextureVertexZ = new int[6];

	@ObfuscatedName("i.v")
	public static final int[] shape0P1 = new int[] { 1, 0 };

	@ObfuscatedName("i.w")
	public static final int[] shape0P2 = new int[] { 2, 1 };

	@ObfuscatedName("i.x")
	public static final int[] shape0P3 = new int[] { 3, 3 };

	@ObfuscatedName("i.y")
	public static final int[][] defShapeP = new int[][] { { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 2, 6 }, { 1, 3, 5, 7, 2, 8 }, { 1, 3, 5, 7, 2, 8 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 13, 14 } };

	@ObfuscatedName("i.z")
	public static final int[][] defShapeF = new int[][] { { 0, 1, 2, 3, 0, 0, 1, 3 }, { 1, 1, 2, 3, 1, 0, 1, 3 }, { 0, 1, 2, 3, 1, 0, 1, 3 }, { 0, 0, 1, 2, 0, 0, 2, 4, 1, 0, 4, 3 }, { 0, 0, 1, 4, 0, 0, 4, 3, 1, 1, 2, 4 }, { 0, 0, 4, 3, 1, 0, 1, 2, 1, 0, 2, 4 }, { 0, 1, 2, 4, 1, 0, 1, 4, 1, 0, 4, 3 }, { 0, 4, 1, 2, 0, 4, 2, 5, 1, 0, 4, 5, 1, 0, 5, 3 }, { 0, 4, 1, 2, 0, 4, 2, 3, 0, 4, 3, 5, 1, 0, 4, 5 }, { 0, 0, 4, 5, 1, 4, 1, 2, 1, 4, 2, 3, 1, 4, 3, 5 }, { 0, 0, 1, 5, 0, 1, 4, 5, 0, 1, 2, 4, 1, 0, 5, 3, 1, 5, 4, 3, 1, 4, 2, 3 }, { 1, 0, 1, 5, 1, 1, 4, 5, 1, 1, 2, 4, 0, 0, 5, 3, 0, 5, 4, 3, 0, 4, 2, 3 }, { 1, 0, 5, 4, 1, 0, 1, 5, 0, 0, 4, 3, 0, 4, 5, 3, 0, 5, 2, 3, 0, 1, 2, 5 } };

	public Ground(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9, int arg10, int arg11, int arg12, int arg13, int arg14, int arg15, int arg16, int arg18, int arg19) {
		if (arg1 != arg14 || arg1 != arg2 || arg1 != arg13) {
			flat = false;
		}
		overlayShape = arg6;
		overlayRotation = arg16;
		minimapOverlay = arg4;
		minimapUnderlay = arg7;
		short var21 = 128;
		int var22 = var21 / 2;
		int var23 = var21 / 4;
		int var24 = var21 * 3 / 4;
		int[] var25 = defShapeP[arg6];
		int var26 = var25.length;
		vertexX = new int[var26];
		vertexY = new int[var26];
		vertexZ = new int[var26];
		int[] var27 = new int[var26];
		int[] var28 = new int[var26];
		int var29 = arg15 * var21;
		int var30 = arg11 * var21;
		for (int var31 = 0; var31 < var26; var31++) {
			int var32 = var25[var31];
			if ((var32 & 0x1) == 0 && var32 <= 8) {
				var32 = (var32 - arg16 - arg16 - 1 & 0x7) + 1;
			}
			if (var32 > 8 && var32 <= 12) {
				var32 = (var32 - arg16 - 9 & 0x3) + 9;
			}
			if (var32 > 12 && var32 <= 16) {
				var32 = (var32 - arg16 - 13 & 0x3) + 13;
			}
			int var33;
			int var34;
			int var35;
			int var36;
			int var37;
			if (var32 == 1) {
				var33 = var29;
				var34 = var30;
				var35 = arg1;
				var36 = arg8;
				var37 = arg0;
			} else if (var32 == 2) {
				var33 = var29 + var22;
				var34 = var30;
				var35 = arg1 + arg14 >> 1;
				var36 = arg8 + arg9 >> 1;
				var37 = arg0 + arg18 >> 1;
			} else if (var32 == 3) {
				var33 = var29 + var21;
				var34 = var30;
				var35 = arg14;
				var36 = arg9;
				var37 = arg18;
			} else if (var32 == 4) {
				var33 = var29 + var21;
				var34 = var30 + var22;
				var35 = arg14 + arg2 >> 1;
				var36 = arg9 + arg10 >> 1;
				var37 = arg18 + arg19 >> 1;
			} else if (var32 == 5) {
				var33 = var29 + var21;
				var34 = var30 + var21;
				var35 = arg2;
				var36 = arg10;
				var37 = arg19;
			} else if (var32 == 6) {
				var33 = var29 + var22;
				var34 = var30 + var21;
				var35 = arg2 + arg13 >> 1;
				var36 = arg10 + arg12 >> 1;
				var37 = arg19 + arg3 >> 1;
			} else if (var32 == 7) {
				var33 = var29;
				var34 = var30 + var21;
				var35 = arg13;
				var36 = arg12;
				var37 = arg3;
			} else if (var32 == 8) {
				var33 = var29;
				var34 = var30 + var22;
				var35 = arg13 + arg1 >> 1;
				var36 = arg12 + arg8 >> 1;
				var37 = arg3 + arg0 >> 1;
			} else if (var32 == 9) {
				var33 = var29 + var22;
				var34 = var30 + var23;
				var35 = arg1 + arg14 >> 1;
				var36 = arg8 + arg9 >> 1;
				var37 = arg0 + arg18 >> 1;
			} else if (var32 == 10) {
				var33 = var29 + var24;
				var34 = var30 + var22;
				var35 = arg14 + arg2 >> 1;
				var36 = arg9 + arg10 >> 1;
				var37 = arg18 + arg19 >> 1;
			} else if (var32 == 11) {
				var33 = var29 + var22;
				var34 = var30 + var24;
				var35 = arg2 + arg13 >> 1;
				var36 = arg10 + arg12 >> 1;
				var37 = arg19 + arg3 >> 1;
			} else if (var32 == 12) {
				var33 = var29 + var23;
				var34 = var30 + var22;
				var35 = arg13 + arg1 >> 1;
				var36 = arg12 + arg8 >> 1;
				var37 = arg3 + arg0 >> 1;
			} else if (var32 == 13) {
				var33 = var29 + var23;
				var34 = var30 + var23;
				var35 = arg1;
				var36 = arg8;
				var37 = arg0;
			} else if (var32 == 14) {
				var33 = var29 + var24;
				var34 = var30 + var23;
				var35 = arg14;
				var36 = arg9;
				var37 = arg18;
			} else if (var32 == 15) {
				var33 = var29 + var24;
				var34 = var30 + var24;
				var35 = arg2;
				var36 = arg10;
				var37 = arg19;
			} else {
				var33 = var29 + var23;
				var34 = var30 + var24;
				var35 = arg13;
				var36 = arg12;
				var37 = arg3;
			}
			vertexX[var31] = var33;
			vertexY[var31] = var35;
			vertexZ[var31] = var34;
			var27[var31] = var36;
			var28[var31] = var37;
		}
		int[] var38 = defShapeF[arg6];
		int var39 = var38.length / 4;
		faceVertexA = new int[var39];
		faceVertexB = new int[var39];
		faceVertexC = new int[var39];
		faceColourA = new int[var39];
		faceColourB = new int[var39];
		faceColourC = new int[var39];
		if (arg5 != -1) {
			faceTexture = new int[var39];
		}
		int var40 = 0;
		for (int var41 = 0; var41 < var39; var41++) {
			int var42 = var38[var40];
			int var43 = var38[var40 + 1];
			int var44 = var38[var40 + 2];
			int var45 = var38[var40 + 3];
			var40 += 4;
			if (var43 < 4) {
				var43 = var43 - arg16 & 0x3;
			}
			if (var44 < 4) {
				var44 = var44 - arg16 & 0x3;
			}
			if (var45 < 4) {
				var45 = var45 - arg16 & 0x3;
			}
			faceVertexA[var41] = var43;
			faceVertexB[var41] = var44;
			faceVertexC[var41] = var45;
			if (var42 == 0) {
				faceColourA[var41] = var27[var43];
				faceColourB[var41] = var27[var44];
				faceColourC[var41] = var27[var45];
				if (faceTexture != null) {
					faceTexture[var41] = -1;
				}
			} else {
				faceColourA[var41] = var28[var43];
				faceColourB[var41] = var28[var44];
				faceColourC[var41] = var28[var45];
				if (faceTexture != null) {
					faceTexture[var41] = arg5;
				}
			}
		}
		int var46 = arg1;
		int var47 = arg14;
		if (arg14 < arg1) {
			var46 = arg14;
		}
		if (arg14 > arg14) {
			var47 = arg14;
		}
		if (arg2 < var46) {
			var46 = arg2;
		}
		if (arg2 > arg14) {
			var47 = arg2;
		}
		if (arg13 < var46) {
			;
		}
		if (arg13 > var47) {
			;
		}
	}
}
