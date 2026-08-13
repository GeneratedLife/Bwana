package jagex2.dash3d.type;

import deob.ObfuscatedName;

@ObfuscatedName("i")
public class TileOverlay {

	@ObfuscatedName("i.a")
	public int[] vertexX;

	@ObfuscatedName("i.b")
	public int[] vertexY;

	@ObfuscatedName("i.c")
	public int[] vertexZ;

	@ObfuscatedName("i.d")
	public int[] triangleColorA;

	@ObfuscatedName("i.e")
	public int[] triangleColorB;

	@ObfuscatedName("i.f")
	public int[] triangleColorC;

	@ObfuscatedName("i.g")
	public int[] triangleVertexA;

	@ObfuscatedName("i.h")
	public int[] triangleVertexB;

	@ObfuscatedName("i.i")
	public int[] triangleVertexC;

	@ObfuscatedName("i.j")
	public int[] triangleTextureIds;

	@ObfuscatedName("i.k")
	public boolean flat = true;

	@ObfuscatedName("i.l")
	public int shape;

	@ObfuscatedName("i.m")
	public int rotation;

	@ObfuscatedName("i.n")
	public int backgroundRgb;

	@ObfuscatedName("i.o")
	public int foregroundRgb;

	@ObfuscatedName("i.p")
	public static int[] tmpScreenX = new int[6];

	@ObfuscatedName("i.q")
	public static int[] tmpScreenY = new int[6];

	@ObfuscatedName("i.r")
	public static int[] tmpViewspaceX = new int[6];

	@ObfuscatedName("i.s")
	public static int[] tmpViewspaceY = new int[6];

	@ObfuscatedName("i.t")
	public static int[] tmpViewspaceZ = new int[6];

	@ObfuscatedName("i.u")
	public static int[] field124 = new int[] { 1, 0 };

	@ObfuscatedName("i.v")
	public static int[] field125 = new int[] { 2, 1 };

	@ObfuscatedName("i.w")
	public static int[] field126 = new int[] { 3, 3 };

	@ObfuscatedName("i.x")
	public static final int[][] SHAPE_POINTS = new int[][] { { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 6 }, { 1, 3, 5, 7, 2, 6 }, { 1, 3, 5, 7, 2, 8 }, { 1, 3, 5, 7, 2, 8 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 11, 12 }, { 1, 3, 5, 7, 13, 14 } };

	@ObfuscatedName("i.y")
	public static final int[][] SHAPE_PATHS = new int[][] { { 0, 1, 2, 3, 0, 0, 1, 3 }, { 1, 1, 2, 3, 1, 0, 1, 3 }, { 0, 1, 2, 3, 1, 0, 1, 3 }, { 0, 0, 1, 2, 0, 0, 2, 4, 1, 0, 4, 3 }, { 0, 0, 1, 4, 0, 0, 4, 3, 1, 1, 2, 4 }, { 0, 0, 4, 3, 1, 0, 1, 2, 1, 0, 2, 4 }, { 0, 1, 2, 4, 1, 0, 1, 4, 1, 0, 4, 3 }, { 0, 4, 1, 2, 0, 4, 2, 5, 1, 0, 4, 5, 1, 0, 5, 3 }, { 0, 4, 1, 2, 0, 4, 2, 3, 0, 4, 3, 5, 1, 0, 4, 5 }, { 0, 0, 4, 5, 1, 4, 1, 2, 1, 4, 2, 3, 1, 4, 3, 5 }, { 0, 0, 1, 5, 0, 1, 4, 5, 0, 1, 2, 4, 1, 0, 5, 3, 1, 5, 4, 3, 1, 4, 2, 3 }, { 1, 0, 1, 5, 1, 1, 4, 5, 1, 1, 2, 4, 0, 0, 5, 3, 0, 5, 4, 3, 0, 4, 2, 3 }, { 1, 0, 5, 4, 1, 0, 1, 5, 0, 0, 4, 3, 0, 4, 5, 3, 0, 5, 2, 3, 0, 1, 2, 5 } };

	public TileOverlay(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9, int arg10, int arg11, int arg12, int arg13, int arg14, int arg15, int arg16, int arg17, int arg18, int arg19) {
		if (arg17 != arg3 || arg17 != arg13 || arg17 != arg7) {
			this.flat = false;
		}
		this.shape = arg1;
		this.rotation = arg5;
		this.backgroundRgb = arg12;
		this.foregroundRgb = arg8;
		short var21 = 128;
		int var22 = var21 / 2;
		int var23 = var21 / 4;
		int var24 = var21 * 3 / 4;
		int[] var25 = SHAPE_POINTS[arg1];
		int var26 = var25.length;
		this.vertexX = new int[var26];
		this.vertexY = new int[var26];
		this.vertexZ = new int[var26];
		int[] var27 = new int[var26];
		int[] var28 = new int[var26];
		int var29 = arg0 * var21;
		int var30 = arg18 * var21;
		int var33;
		int var34;
		int var35;
		int var36;
		int var37;
		for (int var31 = 0; var31 < var26; var31++) {
			int var32 = var25[var31];
			if ((var32 & 0x1) == 0 && var32 <= 8) {
				var32 = (var32 - arg5 - arg5 - 1 & 0x7) + 1;
			}
			if (var32 > 8 && var32 <= 12) {
				var32 = (var32 - 9 - arg5 & 0x3) + 9;
			}
			if (var32 > 12 && var32 <= 16) {
				var32 = (var32 - 13 - arg5 & 0x3) + 13;
			}
			if (var32 == 1) {
				var33 = var29;
				var34 = var30;
				var35 = arg17;
				var36 = arg6;
				var37 = arg9;
			} else if (var32 == 2) {
				var33 = var29 + var22;
				var34 = var30;
				var35 = arg17 + arg3 >> 1;
				var36 = arg6 + arg19 >> 1;
				var37 = arg9 + arg2 >> 1;
			} else if (var32 == 3) {
				var33 = var29 + var21;
				var34 = var30;
				var35 = arg3;
				var36 = arg19;
				var37 = arg2;
			} else if (var32 == 4) {
				var33 = var29 + var21;
				var34 = var30 + var22;
				var35 = arg3 + arg13 >> 1;
				var36 = arg19 + arg4 >> 1;
				var37 = arg2 + arg14 >> 1;
			} else if (var32 == 5) {
				var33 = var29 + var21;
				var34 = var30 + var21;
				var35 = arg13;
				var36 = arg4;
				var37 = arg14;
			} else if (var32 == 6) {
				var33 = var29 + var22;
				var34 = var30 + var21;
				var35 = arg13 + arg7 >> 1;
				var36 = arg4 + arg15 >> 1;
				var37 = arg14 + arg11 >> 1;
			} else if (var32 == 7) {
				var33 = var29;
				var34 = var30 + var21;
				var35 = arg7;
				var36 = arg15;
				var37 = arg11;
			} else if (var32 == 8) {
				var33 = var29;
				var34 = var30 + var22;
				var35 = arg7 + arg17 >> 1;
				var36 = arg15 + arg6 >> 1;
				var37 = arg11 + arg9 >> 1;
			} else if (var32 == 9) {
				var33 = var29 + var22;
				var34 = var30 + var23;
				var35 = arg17 + arg3 >> 1;
				var36 = arg6 + arg19 >> 1;
				var37 = arg9 + arg2 >> 1;
			} else if (var32 == 10) {
				var33 = var29 + var24;
				var34 = var30 + var22;
				var35 = arg3 + arg13 >> 1;
				var36 = arg19 + arg4 >> 1;
				var37 = arg2 + arg14 >> 1;
			} else if (var32 == 11) {
				var33 = var29 + var22;
				var34 = var30 + var24;
				var35 = arg13 + arg7 >> 1;
				var36 = arg4 + arg15 >> 1;
				var37 = arg14 + arg11 >> 1;
			} else if (var32 == 12) {
				var33 = var29 + var23;
				var34 = var30 + var22;
				var35 = arg7 + arg17 >> 1;
				var36 = arg15 + arg6 >> 1;
				var37 = arg11 + arg9 >> 1;
			} else if (var32 == 13) {
				var33 = var29 + var23;
				var34 = var30 + var23;
				var35 = arg17;
				var36 = arg6;
				var37 = arg9;
			} else if (var32 == 14) {
				var33 = var29 + var24;
				var34 = var30 + var23;
				var35 = arg3;
				var36 = arg19;
				var37 = arg2;
			} else if (var32 == 15) {
				var33 = var29 + var24;
				var34 = var30 + var24;
				var35 = arg13;
				var36 = arg4;
				var37 = arg14;
			} else {
				var33 = var29 + var23;
				var34 = var30 + var24;
				var35 = arg7;
				var36 = arg15;
				var37 = arg11;
			}
			this.vertexX[var31] = var33;
			this.vertexY[var31] = var35;
			this.vertexZ[var31] = var34;
			var27[var31] = var36;
			var28[var31] = var37;
		}
		int[] var40 = SHAPE_PATHS[arg1];
		var33 = var40.length / 4;
		this.triangleVertexA = new int[var33];
		this.triangleVertexB = new int[var33];
		this.triangleVertexC = new int[var33];
		this.triangleColorA = new int[var33];
		this.triangleColorB = new int[var33];
		if (arg16 != 10659) {
			throw new NullPointerException();
		}
		this.triangleColorC = new int[var33];
		if (arg10 != -1) {
			this.triangleTextureIds = new int[var33];
		}
		var34 = 0;
		for (var35 = 0; var35 < var33; var35++) {
			var36 = var40[var34];
			var37 = var40[var34 + 1];
			int var38 = var40[var34 + 2];
			int var39 = var40[var34 + 3];
			var34 += 4;
			if (var37 < 4) {
				var37 = var37 - arg5 & 0x3;
			}
			if (var38 < 4) {
				var38 = var38 - arg5 & 0x3;
			}
			if (var39 < 4) {
				var39 = var39 - arg5 & 0x3;
			}
			this.triangleVertexA[var35] = var37;
			this.triangleVertexB[var35] = var38;
			this.triangleVertexC[var35] = var39;
			if (var36 == 0) {
				this.triangleColorA[var35] = var27[var37];
				this.triangleColorB[var35] = var27[var38];
				this.triangleColorC[var35] = var27[var39];
				if (this.triangleTextureIds != null) {
					this.triangleTextureIds[var35] = -1;
				}
			} else {
				this.triangleColorA[var35] = var28[var37];
				this.triangleColorB[var35] = var28[var38];
				this.triangleColorC[var35] = var28[var39];
				if (this.triangleTextureIds != null) {
					this.triangleTextureIds[var35] = arg10;
				}
			}
		}
		var36 = arg17;
		var37 = arg3;
		if (arg3 < arg17) {
			var36 = arg3;
		}
		if (arg3 > arg3) {
			var37 = arg3;
		}
		if (arg13 < var36) {
			var36 = arg13;
		}
		if (arg13 > var37) {
			var37 = arg13;
		}
		if (arg7 < var36) {
			var36 = arg7;
		}
		if (arg7 > var37) {
			var37 = arg7;
		}
		var36 /= 14;
		var37 /= 14;
	}
}
