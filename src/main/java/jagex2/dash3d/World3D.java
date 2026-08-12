package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.dash3d.entity.Entity;
import jagex2.dash3d.type.Decor;
import jagex2.dash3d.type.Ground;
import jagex2.dash3d.type.GroundDecor;
import jagex2.dash3d.type.GroundObject;
import jagex2.dash3d.type.Location;
import jagex2.dash3d.type.TileOverlay;
import jagex2.dash3d.type.TileUnderlay;
import jagex2.dash3d.type.Wall;
import jagex2.datastruct.LinkList;
import jagex2.graphics.Model;
import jagex2.graphics.Pix2D;
import jagex2.graphics.Pix3D;
import jagex2.graphics.VertexNormal;

@ObfuscatedName("r")
public class World3D {

	@ObfuscatedName("r.a")
	public boolean field212 = false;

	@ObfuscatedName("r.b")
	public byte field213 = 6;

	@ObfuscatedName("r.c")
	public int field214;

	@ObfuscatedName("r.d")
	public byte field215 = 1;

	@ObfuscatedName("r.e")
	public static int field216;

	@ObfuscatedName("r.f")
	public static int field217 = -546;

	@ObfuscatedName("r.g")
	public static boolean field218 = true;

	@ObfuscatedName("r.h")
	public static boolean lowMemory = true;

	@ObfuscatedName("r.i")
	public int maxLevel;

	@ObfuscatedName("r.j")
	public int maxTileX;

	@ObfuscatedName("r.k")
	public int maxTileZ;

	@ObfuscatedName("r.l")
	public int[][][] levelHeightmaps;

	@ObfuscatedName("r.m")
	public Ground[][][] levelTiles;

	@ObfuscatedName("r.n")
	public int minLevel;

	@ObfuscatedName("r.o")
	public int temporaryLocCount;

	@ObfuscatedName("r.p")
	public Location[] temporaryLocs = new Location[5000];

	@ObfuscatedName("r.q")
	public int[][][] levelTileOcclusionCycles;

	@ObfuscatedName("r.r")
	public static int tilesRemaining;

	@ObfuscatedName("r.s")
	public static int topLevel;

	@ObfuscatedName("r.t")
	public static int cycle;

	@ObfuscatedName("r.u")
	public static int minDrawTileX;

	@ObfuscatedName("r.v")
	public static int maxDrawTileX;

	@ObfuscatedName("r.w")
	public static int minDrawTileZ;

	@ObfuscatedName("r.x")
	public static int maxDrawTileZ;

	@ObfuscatedName("r.y")
	public static int eyeTileX;

	@ObfuscatedName("r.z")
	public static int eyeTileZ;

	@ObfuscatedName("r.ab")
	public static final int[] WALL_CORNER_TYPE_16_BLOCK_LOC_SPANS = new int[] { 0, 0, 2, 0, 0, 2, 1, 1, 0 };

	@ObfuscatedName("r.bb")
	public static final int[] WALL_CORNER_TYPE_32_BLOCK_LOC_SPANS = new int[] { 2, 0, 0, 2, 0, 0, 0, 4, 4 };

	@ObfuscatedName("r.cb")
	public static final int[] WALL_CORNER_TYPE_64_BLOCK_LOC_SPANS = new int[] { 0, 4, 4, 8, 0, 0, 8, 0, 0 };

	@ObfuscatedName("r.db")
	public static final int[] WALL_CORNER_TYPE_128_BLOCK_LOC_SPANS = new int[] { 1, 1, 0, 0, 0, 8, 0, 0, 8 };

	@ObfuscatedName("r.eb")
	public static final int[] TEXTURE_HSL = new int[] { 41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 43086, 41, 41, 41, 41, 41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 3131, 41, 41, 41 };

	@ObfuscatedName("r.fb")
	public int[] mergeIndexA = new int[10000];

	@ObfuscatedName("r.gb")
	public int[] mergeIndexB = new int[10000];

	@ObfuscatedName("r.hb")
	public int tmpMergeIndex;

	@ObfuscatedName("r.ib")
	public int[][] MINIMAP_OVERLAY_SHAPE = new int[][] { new int[16], { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1 }, { 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1 } };

	@ObfuscatedName("r.jb")
	public int[][] MINIMAP_OVERLAY_ROTATION = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, { 12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3 }, { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 }, { 3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12 } };

	@ObfuscatedName("r.kb")
	public static boolean[][][][] visibilityMatrix = new boolean[8][32][51][51];

	@ObfuscatedName("r.lb")
	public static boolean[][] visibilityMap;

	@ObfuscatedName("r.mb")
	public static int viewportCenterX;

	@ObfuscatedName("r.nb")
	public static int viewportCenterY;

	@ObfuscatedName("r.ob")
	public static int viewportLeft;

	@ObfuscatedName("r.pb")
	public static int viewportTop;

	@ObfuscatedName("r.qb")
	public static int viewportRight;

	@ObfuscatedName("r.rb")
	public static int viewportBottom;

	@ObfuscatedName("r.H")
	public static Location[] locBuffer = new Location[100];

	@ObfuscatedName("r.I")
	public static final int[] WALL_DECORATION_INSET_X = new int[] { 53, -53, -53, 53 };

	@ObfuscatedName("r.J")
	public static final int[] WALL_DECORATION_INSET_Z = new int[] { -53, -53, 53, 53 };

	@ObfuscatedName("r.K")
	public static final int[] WALL_DECORATION_OUTSET_X = new int[] { -45, 45, 45, -45 };

	@ObfuscatedName("r.L")
	public static final int[] WALL_DECORATION_OUTSET_Z = new int[] { 45, 45, -45, -45 };

	@ObfuscatedName("r.P")
	public static int clickTileX = -1;

	@ObfuscatedName("r.Q")
	public static int clickTileZ = -1;

	@ObfuscatedName("r.R")
	public static int LEVEL_COUNT = 4;

	@ObfuscatedName("r.S")
	public static int[] levelOccluderCount = new int[LEVEL_COUNT];

	@ObfuscatedName("r.T")
	public static Occlude[][] levelOccluders = new Occlude[LEVEL_COUNT][500];

	@ObfuscatedName("r.V")
	public static Occlude[] activeOccluders = new Occlude[500];

	@ObfuscatedName("r.W")
	public static LinkList drawTileQueue = new LinkList(0);

	@ObfuscatedName("r.X")
	public static final int[] FRONT_WALL_TYPES = new int[] { 19, 55, 38, 155, 255, 110, 137, 205, 76 };

	@ObfuscatedName("r.Y")
	public static final int[] DIRECTION_ALLOW_WALL_CORNER_TYPE = new int[] { 160, 192, 80, 96, 0, 144, 80, 48, 160 };

	@ObfuscatedName("r.Z")
	public static final int[] BACK_WALL_TYPES = new int[] { 76, 8, 137, 4, 0, 1, 38, 2, 19 };

	@ObfuscatedName("r.A")
	public static int eyeX;

	@ObfuscatedName("r.B")
	public static int eyeY;

	@ObfuscatedName("r.C")
	public static int eyeZ;

	@ObfuscatedName("r.D")
	public static int sinEyePitch;

	@ObfuscatedName("r.E")
	public static int cosEyePitch;

	@ObfuscatedName("r.F")
	public static int sinEyeYaw;

	@ObfuscatedName("r.G")
	public static int cosEyeYaw;

	@ObfuscatedName("r.N")
	public static int mouseX;

	@ObfuscatedName("r.O")
	public static int mouseY;

	@ObfuscatedName("r.U")
	public static int activeOccluderCount;

	@ObfuscatedName("r.M")
	public static boolean takingInput;

	public World3D(int arg0, int[][][] arg1, int arg2, int arg3, int arg4) {
		this.maxLevel = arg3;
		this.maxTileX = arg4;
		this.maxTileZ = arg2;
		this.levelTiles = new Ground[arg3][arg4][arg2];
		this.levelTileOcclusionCycles = new int[arg3][arg4 + 1][arg2 + 1];
		int var6 = 27 / arg0;
		this.levelHeightmaps = arg1;
		this.reset(742);
	}

	@ObfuscatedName("r.a(Z)V")
	public static void unload(boolean arg0) {
		locBuffer = null;
		levelOccluderCount = null;
		if (!arg0) {
			field218 = !field218;
		}
		levelOccluders = null;
		drawTileQueue = null;
		visibilityMatrix = null;
		visibilityMap = null;
	}

	@ObfuscatedName("r.a(I)V")
	public void reset(int arg0) {
		int var3;
		int var4;
		for (int var2 = 0; var2 < this.maxLevel; var2++) {
			for (var3 = 0; var3 < this.maxTileX; var3++) {
				for (var4 = 0; var4 < this.maxTileZ; var4++) {
					this.levelTiles[var2][var3][var4] = null;
				}
			}
		}
		int var6 = 38 / arg0;
		for (var3 = 0; var3 < LEVEL_COUNT; var3++) {
			for (var4 = 0; var4 < levelOccluderCount[var3]; var4++) {
				levelOccluders[var3][var4] = null;
			}
			levelOccluderCount[var3] = 0;
		}
		for (var4 = 0; var4 < this.temporaryLocCount; var4++) {
			this.temporaryLocs[var4] = null;
		}
		this.temporaryLocCount = 0;
		for (int var5 = 0; var5 < locBuffer.length; var5++) {
			locBuffer[var5] = null;
		}
	}

	@ObfuscatedName("r.a(II)V")
	public void setMinLevel(int arg0, int arg1) {
		this.minLevel = arg1;
		int var4;
		for (int var3 = 0; var3 < this.maxTileX; var3++) {
			for (var4 = 0; var4 < this.maxTileZ; var4++) {
				this.levelTiles[arg1][var3][var4] = new Ground(arg1, var3, var4);
			}
		}
		if (arg0 != 0) {
			for (var4 = 1; var4 > 0; var4++) {
			}
		}
	}

	@ObfuscatedName("r.a(IIB)V")
	public void setBridge(int arg0, int arg1, byte arg2) {
		Ground var4 = this.levelTiles[0][arg1][arg0];
		for (int var5 = 0; var5 < 3; var5++) {
			this.levelTiles[var5][arg1][arg0] = this.levelTiles[var5 + 1][arg1][arg0];
			if (this.levelTiles[var5][arg1][arg0] != null) {
				this.levelTiles[var5][arg1][arg0].level--;
			}
		}
		if (this.levelTiles[0][arg1][arg0] == null) {
			this.levelTiles[0][arg1][arg0] = new Ground(0, arg1, arg0);
		}
		this.levelTiles[0][arg1][arg0].bridge = var4;
		this.levelTiles[3][arg1][arg0] = null;
		if (arg2 != -41) {
			for (int var6 = 1; var6 > 0; var6++) {
			}
		}
	}

	@ObfuscatedName("r.a(IIIIIIIII)V")
	public static void addOccluder(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		Occlude var9 = new Occlude();
		var9.minTileX = arg1 / 128;
		var9.maxTileX = arg5 / 128;
		var9.minTileZ = arg8 / 128;
		var9.maxTileZ = arg0 / 128;
		var9.type = arg4;
		var9.minX = arg1;
		var9.maxX = arg5;
		var9.minZ = arg8;
		while (arg2 >= 0) {
			field217 = 127;
		}
		var9.maxZ = arg0;
		var9.minY = arg7;
		var9.maxY = arg3;
		levelOccluders[arg6][levelOccluderCount[arg6]++] = var9;
	}

	@ObfuscatedName("r.a(IIII)V")
	public void setDrawLevel(int arg0, int arg1, int arg2, int arg3) {
		Ground var5 = this.levelTiles[arg0][arg1][arg2];
		if (var5 != null) {
			this.levelTiles[arg0][arg1][arg2].drawLevel = arg3;
		}
	}

	@ObfuscatedName("r.a(IIIIIIIIIIIIIIIIIIII)V")
	public void setTile(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9, int arg10, int arg11, int arg12, int arg13, int arg14, int arg15, int arg16, int arg17, int arg18, int arg19) {
		TileUnderlay var21;
		int var22;
		if (arg3 == 0) {
			var21 = new TileUnderlay(arg10, arg11, arg12, arg13, -1, arg18, false);
			for (var22 = arg0; var22 >= 0; var22--) {
				if (this.levelTiles[var22][arg1][arg2] == null) {
					this.levelTiles[var22][arg1][arg2] = new Ground(var22, arg1, arg2);
				}
			}
			this.levelTiles[arg0][arg1][arg2].underlay = var21;
		} else if (arg3 == 1) {
			var21 = new TileUnderlay(arg14, arg15, arg16, arg17, arg5, arg19, arg6 == arg7 && arg6 == arg8 && arg6 == arg9);
			for (var22 = arg0; var22 >= 0; var22--) {
				if (this.levelTiles[var22][arg1][arg2] == null) {
					this.levelTiles[var22][arg1][arg2] = new Ground(var22, arg1, arg2);
				}
			}
			this.levelTiles[arg0][arg1][arg2].underlay = var21;
		} else {
			TileOverlay var23 = new TileOverlay(arg1, arg3, arg15, arg7, arg12, arg4, arg10, arg9, arg19, arg14, arg5, arg17, arg18, arg8, arg16, arg13, 10659, arg6, arg2, arg11);
			for (var22 = arg0; var22 >= 0; var22--) {
				if (this.levelTiles[var22][arg1][arg2] == null) {
					this.levelTiles[var22][arg1][arg2] = new Ground(var22, arg1, arg2);
				}
			}
			this.levelTiles[arg0][arg1][arg2].overlay = var23;
		}
	}

	@ObfuscatedName("r.a(Leb;BIIIIBI)V")
	public void addGroundDecoration(Model arg0, byte arg1, int arg2, int arg3, int arg4, int arg5, byte arg6, int arg7) {
		GroundDecor var9 = new GroundDecor();
		var9.model = arg0;
		var9.x = arg2 * 128 + 64;
		var9.z = arg4 * 128 + 64;
		var9.y = arg7;
		var9.bitset = arg3;
		var9.info = arg6;
		if (this.levelTiles[arg5][arg2][arg4] == null) {
			this.levelTiles[arg5][arg2][arg4] = new Ground(arg5, arg2, arg4);
		}
		this.levelTiles[arg5][arg2][arg4].groundDecor = var9;
		if (arg1 == this.field213) {
			boolean var10 = false;
		} else {
			field218 = !field218;
		}
	}

	@ObfuscatedName("r.a(Leb;Leb;IIIIILeb;I)V")
	public void addObjStack(Model arg0, Model arg1, int arg2, int arg3, int arg4, int arg5, int arg6, Model arg7, int arg8) {
		GroundObject var10 = new GroundObject();
		var10.topObj = arg0;
		int var15 = 90 / arg8;
		var10.x = arg6 * 128 + 64;
		var10.z = arg5 * 128 + 64;
		var10.y = arg2;
		var10.bitset = arg4;
		var10.bottomObj = arg1;
		var10.middleObj = arg7;
		int var11 = 0;
		Ground var12 = this.levelTiles[arg3][arg6][arg5];
		if (var12 != null) {
			for (int var13 = 0; var13 < var12.locCount; var13++) {
				int var14 = var12.locs[var13].model.objRaise;
				if (var14 > var11) {
					var11 = var14;
				}
			}
		}
		var10.offset = var11;
		if (this.levelTiles[arg3][arg6][arg5] == null) {
			this.levelTiles[arg3][arg6][arg5] = new Ground(arg3, arg6, arg5);
		}
		this.levelTiles[arg3][arg6][arg5].groundObj = var10;
	}

	@ObfuscatedName("r.a(IIIIILeb;Leb;IIIB)V")
	public void addWall(int arg0, int arg1, int arg2, int arg3, int arg4, Model arg5, Model arg6, int arg7, int arg8, int arg9, byte arg10) {
		if (arg5 == null && arg6 == null) {
			return;
		}
		Wall var12 = new Wall();
		var12.bitset = arg8;
		var12.info = arg10;
		var12.x = arg7 * 128 + 64;
		var12.z = arg9 * 128 + 64;
		var12.y = arg1;
		var12.modelA = arg5;
		if (arg4 != 8) {
			field218 = !field218;
		}
		var12.modelB = arg6;
		var12.typeA = arg3;
		var12.typeB = arg0;
		for (int var13 = arg2; var13 >= 0; var13--) {
			if (this.levelTiles[var13][arg7][arg9] == null) {
				this.levelTiles[var13][arg7][arg9] = new Ground(var13, arg7, arg9);
			}
		}
		this.levelTiles[arg2][arg7][arg9].wall = var12;
	}

	@ObfuscatedName("r.a(IIIIIIIIILeb;BI)V")
	public void setWallDecoration(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, Model arg9, byte arg10, int arg11) {
		int var15 = 66 / arg6;
		if (arg9 == null) {
			return;
		}
		Decor var13 = new Decor();
		var13.bitset = arg3;
		var13.info = arg10;
		var13.x = arg8 * 128 + 64 + arg7;
		var13.z = arg1 * 128 + 64 + arg2;
		var13.y = arg0;
		var13.model = arg9;
		var13.type = arg5;
		var13.angle = arg4;
		for (int var14 = arg11; var14 >= 0; var14--) {
			if (this.levelTiles[var14][arg8][arg1] == null) {
				this.levelTiles[var14][arg8][arg1] = new Ground(var14, arg8, arg1);
			}
		}
		this.levelTiles[arg11][arg8][arg1].decor = var13;
	}

	@ObfuscatedName("r.a(IIILw;IIIIBLeb;II)Z")
	public boolean addLoc(int arg0, int arg1, int arg2, Entity arg3, int arg4, int arg5, int arg6, int arg7, byte arg8, Model arg9, int arg10, int arg11) {
		int var15 = 79 / arg1;
		if (arg9 == null && arg3 == null) {
			return true;
		} else {
			int var13 = arg6 * 128 + arg7 * 64;
			int var14 = arg5 * 128 + arg11 * 64;
			return this.addLoc(arg2, arg6, arg5, arg7, arg11, var13, var14, arg0, arg9, arg3, arg10, false, arg4, arg8);
		}
	}

	@ObfuscatedName("r.a(IIIIIIZLeb;Lw;II)Z")
	public boolean addTemporary(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6, Model arg7, Entity arg8, int arg9, int arg10) {
		if (arg7 == null && arg8 == null) {
			return true;
		}
		int var12 = arg4 - arg2;
		int var13 = arg1 - arg2;
		int var14 = arg4 + arg2;
		int var15 = arg1 + arg2;
		if (arg6) {
			if (arg3 > 640 && arg3 < 1408) {
				var15 += 128;
			}
			if (arg3 > 1152 && arg3 < 1920) {
				var14 += 128;
			}
			if (arg3 > 1664 || arg3 < 384) {
				var13 -= 128;
			}
			if (arg3 > 128 && arg3 < 896) {
				var12 -= 128;
			}
		}
		var12 /= 128;
		if (arg0 != -44713) {
			field218 = !field218;
		}
		var13 /= 128;
		var14 /= 128;
		var15 /= 128;
		return this.addLoc(arg10, var12, var13, var14 - var12 + 1, var15 - var13 + 1, arg4, arg1, arg9, arg7, arg8, arg3, true, arg5, (byte) 0);
	}

	@ObfuscatedName("r.a(IILeb;IIIIIILw;ZIII)Z")
	public boolean addTemporary(int arg0, int arg1, Model arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, Entity arg9, boolean arg10, int arg11, int arg12, int arg13) {
		if (arg10) {
			for (int var15 = 1; var15 > 0; var15++) {
			}
		}
		return arg2 == null && arg9 == null ? true : this.addLoc(arg11, arg8, arg7, arg0 - arg8 + 1, arg12 - arg7 + 1, arg13, arg3, arg4, arg2, arg9, arg6, true, arg5, (byte) 0);
	}

	@ObfuscatedName("r.a(IIIIIIIILeb;Lw;IZIB)Z")
	public boolean addLoc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, Model arg8, Entity arg9, int arg10, boolean arg11, int arg12, byte arg13) {
		if (arg8 == null && arg9 == null) {
			return false;
		}
		for (int var15 = arg1; var15 < arg1 + arg3; var15++) {
			for (int var16 = arg2; var16 < arg2 + arg4; var16++) {
				if (var15 < 0 || var16 < 0 || var15 >= this.maxTileX || var16 >= this.maxTileZ) {
					return false;
				}
				Ground var17 = this.levelTiles[arg0][var15][var16];
				if (var17 != null && var17.locCount >= 5) {
					return false;
				}
			}
		}
		Location var22 = new Location();
		var22.bitset = arg12;
		var22.info = arg13;
		var22.level = arg0;
		var22.x = arg5;
		var22.z = arg6;
		var22.y = arg7;
		var22.model = arg8;
		var22.entity = arg9;
		var22.yaw = arg10;
		var22.minSceneTileX = arg1;
		var22.minSceneTileZ = arg2;
		var22.maxSceneTileX = arg1 + arg3 - 1;
		var22.maxSceneTileZ = arg2 + arg4 - 1;
		for (int var23 = arg1; var23 < arg1 + arg3; var23++) {
			for (int var18 = arg2; var18 < arg2 + arg4; var18++) {
				int var19 = 0;
				if (var23 > arg1) {
					var19++;
				}
				if (var23 < arg1 + arg3 - 1) {
					var19 += 4;
				}
				if (var18 > arg2) {
					var19 += 8;
				}
				if (var18 < arg2 + arg4 - 1) {
					var19 += 2;
				}
				for (int var20 = arg0; var20 >= 0; var20--) {
					if (this.levelTiles[var20][var23][var18] == null) {
						this.levelTiles[var20][var23][var18] = new Ground(var20, var23, var18);
					}
				}
				Ground var21 = this.levelTiles[arg0][var23][var18];
				var21.locs[var21.locCount] = var22;
				var21.locSpan[var21.locCount] = var19;
				var21.locSpans |= var19;
				var21.locCount++;
			}
		}
		if (arg11) {
			this.temporaryLocs[this.temporaryLocCount++] = var22;
		}
		return true;
	}

	@ObfuscatedName("r.b(I)V")
	public void clearTemporaryLocs(int arg0) {
		for (int var2 = 0; var2 < this.temporaryLocCount; var2++) {
			Location var3 = this.temporaryLocs[var2];
			this.removeLoc(var3, (byte) 1);
			this.temporaryLocs[var2] = null;
		}
		if (arg0 != 0) {
			this.field212 = !this.field212;
		}
		this.temporaryLocCount = 0;
	}

	@ObfuscatedName("r.a(Lp;B)V")
	public void removeLoc(Location arg0, byte arg1) {
		for (int var3 = arg0.minSceneTileX; var3 <= arg0.maxSceneTileX; var3++) {
			for (int var4 = arg0.minSceneTileZ; var4 <= arg0.maxSceneTileZ; var4++) {
				Ground var5 = this.levelTiles[arg0.level][var3][var4];
				if (var5 != null) {
					int var7;
					for (int var6 = 0; var6 < var5.locCount; var6++) {
						if (var5.locs[var6] == arg0) {
							var5.locCount--;
							for (var7 = var6; var7 < var5.locCount; var7++) {
								var5.locs[var7] = var5.locs[var7 + 1];
								var5.locSpan[var7] = var5.locSpan[var7 + 1];
							}
							var5.locs[var5.locCount] = null;
							break;
						}
					}
					var5.locSpans = 0;
					for (var7 = 0; var7 < var5.locCount; var7++) {
						var5.locSpans |= var5.locSpan[var7];
					}
				}
			}
		}
		if (arg1 == this.field215) {
			boolean var8 = false;
		}
	}

	@ObfuscatedName("r.a(ILeb;III)V")
	public void setLocModel(int arg0, Model arg1, int arg2, int arg3, int arg4) {
		if (arg1 == null) {
			return;
		}
		Ground var6 = this.levelTiles[arg3][arg0][arg4];
		int var7;
		if (arg2 < 1 || arg2 > 1) {
			for (var7 = 1; var7 > 0; var7++) {
			}
		}
		if (var6 == null) {
			return;
		}
		for (var7 = 0; var7 < var6.locCount; var7++) {
			Location var8 = var6.locs[var7];
			if ((var8.bitset >> 29 & 0x3) == 2) {
				var8.model = arg1;
				return;
			}
		}
	}

	@ObfuscatedName("r.a(IIIIB)V")
	public void setWallDecorationOffset(int arg0, int arg1, int arg2, int arg3, byte arg4) {
		Ground var6 = this.levelTiles[arg0][arg2][arg1];
		if (arg4 != 6) {
			for (int var7 = 1; var7 > 0; var7++) {
			}
		}
		if (var6 == null) {
			return;
		}
		Decor var10 = var6.decor;
		if (var10 != null) {
			int var8 = arg2 * 128 + 64;
			int var9 = arg1 * 128 + 64;
			var10.x = var8 + (var10.x - var8) * arg3 / 16;
			var10.z = var9 + (var10.z - var9) * arg3 / 16;
		}
	}

	@ObfuscatedName("r.a(IIILeb;I)V")
	public void setWallDecorationModel(int arg0, int arg1, int arg2, Model arg3, int arg4) {
		int var8 = 34 / arg0;
		if (arg3 == null) {
			return;
		}
		Ground var6 = this.levelTiles[arg4][arg2][arg1];
		if (var6 != null) {
			Decor var7 = var6.decor;
			if (var7 != null) {
				var7.model = arg3;
			}
		}
	}

	@ObfuscatedName("r.a(Leb;IIII)V")
	public void setGroundDecorationModel(Model arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg2 != -48639 || arg0 == null) {
			return;
		}
		Ground var6 = this.levelTiles[arg4][arg3][arg1];
		if (var6 != null) {
			GroundDecor var7 = var6.groundDecor;
			if (var7 != null) {
				var7.model = arg0;
			}
		}
	}

	@ObfuscatedName("r.b(ILeb;III)V")
	public void setWallModel(int arg0, Model arg1, int arg2, int arg3, int arg4) {
		if (arg0 != 35568) {
			for (int var6 = 1; var6 > 0; var6++) {
			}
		}
		if (arg1 == null) {
			return;
		}
		Ground var8 = this.levelTiles[arg4][arg3][arg2];
		if (var8 != null) {
			Wall var7 = var8.wall;
			if (var7 != null) {
				var7.modelA = arg1;
			}
		}
	}

	@ObfuscatedName("r.a(Leb;Leb;IZII)V")
	public void setWallModels(Model arg0, Model arg1, int arg2, boolean arg3, int arg4, int arg5) {
		if (arg0 == null) {
			return;
		}
		Ground var7 = this.levelTiles[arg5][arg4][arg2];
		if (var7 == null) {
			return;
		}
		Wall var8 = var7.wall;
		if (var8 == null) {
			return;
		}
		var8.modelA = arg0;
		var8.modelB = arg1;
		if (arg3) {
			this.field212 = !this.field212;
		}
	}

	@ObfuscatedName("r.b(IIII)V")
	public void removeWall(int arg0, int arg1, int arg2, int arg3) {
		Ground var5 = this.levelTiles[arg1][arg0][arg2];
		if (arg3 == 1 && var5 != null) {
			var5.wall = null;
		}
	}

	@ObfuscatedName("r.c(IIII)V")
	public void removeWallDecoration(int arg0, int arg1, int arg2, int arg3) {
		Ground var5 = this.levelTiles[arg0][arg3][arg1];
		if (arg2 < 0 || arg2 > 0) {
			this.field212 = !this.field212;
		}
		if (var5 != null) {
			var5.decor = null;
		}
	}

	@ObfuscatedName("r.d(IIII)V")
	public void removeLoc(int arg0, int arg1, int arg2, int arg3) {
		Ground var5 = this.levelTiles[arg3][arg0][arg1];
		if (var5 == null) {
			return;
		}
		for (int var6 = 0; var6 < var5.locCount; var6++) {
			Location var7 = var5.locs[var6];
			if ((var7.bitset >> 29 & 0x3) == 2 && var7.minSceneTileX == arg0 && var7.minSceneTileZ == arg1) {
				this.removeLoc(var7, (byte) 1);
				return;
			}
		}
		if (arg2 >= 0) {
			for (int var8 = 1; var8 > 0; var8++) {
			}
		}
	}

	@ObfuscatedName("r.e(IIII)V")
	public void removeGroundDecoration(int arg0, int arg1, int arg2, int arg3) {
		if (arg1 != 0) {
			field218 = !field218;
		}
		Ground var5 = this.levelTiles[arg0][arg2][arg3];
		if (var5 != null) {
			var5.groundDecor = null;
		}
	}

	@ObfuscatedName("r.a(III)V")
	public void removeObjStack(int arg0, int arg1, int arg2) {
		Ground var4 = this.levelTiles[arg0][arg1][arg2];
		if (var4 != null) {
			var4.groundObj = null;
		}
	}

	@ObfuscatedName("r.b(III)I")
	public int getWallBitset(int arg0, int arg1, int arg2) {
		Ground var4 = this.levelTiles[arg0][arg1][arg2];
		return var4 == null || var4.wall == null ? 0 : var4.wall.bitset;
	}

	@ObfuscatedName("r.f(IIII)I")
	public int getWallDecorationBitset(int arg0, int arg1, int arg2, int arg3) {
		if (arg2 != 3) {
			this.field212 = !this.field212;
		}
		Ground var5 = this.levelTiles[arg0][arg3][arg1];
		return var5 == null || var5.decor == null ? 0 : var5.decor.bitset;
	}

	@ObfuscatedName("r.c(III)I")
	public int getLocBitset(int arg0, int arg1, int arg2) {
		Ground var4 = this.levelTiles[arg0][arg1][arg2];
		if (var4 == null) {
			return 0;
		}
		for (int var5 = 0; var5 < var4.locCount; var5++) {
			Location var6 = var4.locs[var5];
			if ((var6.bitset >> 29 & 0x3) == 2 && var6.minSceneTileX == arg1 && var6.minSceneTileZ == arg2) {
				return var6.bitset;
			}
		}
		return 0;
	}

	@ObfuscatedName("r.d(III)I")
	public int getGroundDecorationBitset(int arg0, int arg1, int arg2) {
		Ground var4 = this.levelTiles[arg0][arg1][arg2];
		return var4 == null || var4.groundDecor == null ? 0 : var4.groundDecor.bitset;
	}

	/**
	 * Every loc bitset on one tile, in a single lookup. // Bwana
	 * <p>
	 * The four accessors above each walk {@code levelTiles} independently, so asking
	 * for all of them costs four triple-array dereferences and four null checks per
	 * tile. A scan sweeping a few thousand tiles several times a second does that
	 * tens of thousands of times, on the game thread — enough to stall the loop, stop
	 * the packet reader and get the client dropped in a dense city.
	 * <p>
	 * Fills {@code arg4} with wall, decoration, loc and ground decoration, in that
	 * order, and reports whether the tile had anything at all so the caller can skip
	 * it outright.
	 *
	 * @param arg4 scratch of length 4, reused across the sweep
	 */
	public boolean getLocBitsets(int arg0, int arg1, int arg2, int[] arg4) {
		Ground var6 = this.levelTiles[arg0][arg1][arg2];
		if (var6 == null) {
			return false;
		}
		arg4[0] = var6.wall == null ? 0 : var6.wall.bitset;
		arg4[1] = var6.decor == null ? 0 : var6.decor.bitset;
		arg4[2] = 0;
		for (int var7 = 0; var7 < var6.locCount; var7++) {
			Location var8 = var6.locs[var7];
			if ((var8.bitset >> 29 & 0x3) == 2 && var8.minSceneTileX == arg1
					&& var8.minSceneTileZ == arg2) {
				arg4[2] = var8.bitset;
				break;
			}
		}
		arg4[3] = var6.groundDecor == null ? 0 : var6.groundDecor.bitset;
		return (arg4[0] | arg4[1] | arg4[2] | arg4[3]) != 0;
	}

	@ObfuscatedName("r.g(IIII)I")
	public int getInfo(int arg0, int arg1, int arg2, int arg3) {
		Ground var5 = this.levelTiles[arg0][arg1][arg2];
		if (var5 == null) {
			return -1;
		} else if (var5.wall != null && var5.wall.bitset == arg3) {
			return var5.wall.info & 0xFF;
		} else if (var5.decor != null && var5.decor.bitset == arg3) {
			return var5.decor.info & 0xFF;
		} else if (var5.groundDecor != null && var5.groundDecor.bitset == arg3) {
			return var5.groundDecor.info & 0xFF;
		} else {
			for (int var6 = 0; var6 < var5.locCount; var6++) {
				if (var5.locs[var6].bitset == arg3) {
					return var5.locs[var6].info & 0xFF;
				}
			}
			return -1;
		}
	}

	@ObfuscatedName("r.a(IIIIIZ)V")
	public void buildModels(int arg0, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
		int var7 = (int) Math.sqrt((double) (arg2 * arg2 + arg0 * arg0 + arg4 * arg4));
		if (arg5) {
			field216 = -77;
		}
		int var8 = arg3 * var7 >> 8;
		for (int var9 = 0; var9 < this.maxLevel; var9++) {
			for (int var10 = 0; var10 < this.maxTileX; var10++) {
				for (int var11 = 0; var11 < this.maxTileZ; var11++) {
					Ground var12 = this.levelTiles[var9][var10][var11];
					if (var12 != null) {
						Wall var13 = var12.wall;
						if (var13 != null && var13.modelA != null && var13.modelA.vertexNormal != null) {
							this.mergeLocNormals(var10, 1, 1, var9, 872, var13.modelA, var11);
							if (var13.modelB != null && var13.modelB.vertexNormal != null) {
								this.mergeLocNormals(var10, 1, 1, var9, 872, var13.modelB, var11);
								this.mergeNormals(var13.modelA, var13.modelB, 0, 0, 0, false);
								var13.modelB.applyLighting(arg1, var8, arg2, arg0, arg4);
							}
							var13.modelA.applyLighting(arg1, var8, arg2, arg0, arg4);
						}
						for (int var14 = 0; var14 < var12.locCount; var14++) {
							Location var15 = var12.locs[var14];
							if (var15 != null && var15.model != null && var15.model.vertexNormal != null) {
								this.mergeLocNormals(var10, var15.maxSceneTileX - var15.minSceneTileX + 1, var15.maxSceneTileZ - var15.minSceneTileZ + 1, var9, 872, var15.model, var11);
								var15.model.applyLighting(arg1, var8, arg2, arg0, arg4);
							}
						}
						GroundDecor var16 = var12.groundDecor;
						if (var16 != null && var16.model.vertexNormal != null) {
							this.mergeGroundDecorationNormals((byte) -70, var9, var11, var16.model, var10);
							var16.model.applyLighting(arg1, var8, arg2, arg0, arg4);
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("r.a(BIILeb;I)V")
	public void mergeGroundDecorationNormals(byte arg0, int arg1, int arg2, Model arg3, int arg4) {
		if (arg0 != -70) {
			field216 = -417;
		}
		Ground var6;
		if (arg4 < this.maxTileX) {
			var6 = this.levelTiles[arg1][arg4 + 1][arg2];
			if (var6 != null && var6.groundDecor != null && var6.groundDecor.model.vertexNormal != null) {
				this.mergeNormals(arg3, var6.groundDecor.model, 128, 0, 0, true);
			}
		}
		if (arg2 < this.maxTileX) {
			var6 = this.levelTiles[arg1][arg4][arg2 + 1];
			if (var6 != null && var6.groundDecor != null && var6.groundDecor.model.vertexNormal != null) {
				this.mergeNormals(arg3, var6.groundDecor.model, 0, 0, 128, true);
			}
		}
		if (arg4 < this.maxTileX && arg2 < this.maxTileZ) {
			var6 = this.levelTiles[arg1][arg4 + 1][arg2 + 1];
			if (var6 != null && var6.groundDecor != null && var6.groundDecor.model.vertexNormal != null) {
				this.mergeNormals(arg3, var6.groundDecor.model, 128, 0, 128, true);
			}
		}
		if (arg4 >= this.maxTileX || arg2 <= 0) {
			return;
		}
		var6 = this.levelTiles[arg1][arg4 + 1][arg2 - 1];
		if (var6 != null && var6.groundDecor != null && var6.groundDecor.model.vertexNormal != null) {
			this.mergeNormals(arg3, var6.groundDecor.model, 128, 0, -128, true);
			return;
		}
	}

	@ObfuscatedName("r.a(IIIIILeb;I)V")
	public void mergeLocNormals(int arg0, int arg1, int arg2, int arg3, int arg4, Model arg5, int arg6) {
		int var23 = 21 / arg4;
		boolean var8 = true;
		int var9 = arg0;
		int var10 = arg0 + arg1;
		int var11 = arg6 - 1;
		int var12 = arg6 + arg2;
		for (int var13 = arg3; var13 <= arg3 + 1; var13++) {
			if (var13 != this.maxLevel) {
				for (int var14 = var9; var14 <= var10; var14++) {
					if (var14 >= 0 && var14 < this.maxTileX) {
						for (int var15 = var11; var15 <= var12; var15++) {
							if (var15 >= 0 && var15 < this.maxTileZ && (!var8 || var14 >= var10 || var15 >= var12 || var15 < arg6 && var14 != arg0)) {
								Ground var16 = this.levelTiles[var13][var14][var15];
								if (var16 != null) {
									int var17 = (this.levelHeightmaps[var13][var14][var15] + this.levelHeightmaps[var13][var14 + 1][var15] + this.levelHeightmaps[var13][var14][var15 + 1] + this.levelHeightmaps[var13][var14 + 1][var15 + 1]) / 4 - (this.levelHeightmaps[arg3][arg0][arg6] + this.levelHeightmaps[arg3][arg0 + 1][arg6] + this.levelHeightmaps[arg3][arg0][arg6 + 1] + this.levelHeightmaps[arg3][arg0 + 1][arg6 + 1]) / 4;
									Wall var18 = var16.wall;
									if (var18 != null && var18.modelA != null && var18.modelA.vertexNormal != null) {
										this.mergeNormals(arg5, var18.modelA, (var14 - arg0) * 128 + (1 - arg1) * 64, var17, (var15 - arg6) * 128 + (1 - arg2) * 64, var8);
									}
									if (var18 != null && var18.modelB != null && var18.modelB.vertexNormal != null) {
										this.mergeNormals(arg5, var18.modelB, (var14 - arg0) * 128 + (1 - arg1) * 64, var17, (var15 - arg6) * 128 + (1 - arg2) * 64, var8);
									}
									for (int var19 = 0; var19 < var16.locCount; var19++) {
										Location var20 = var16.locs[var19];
										if (var20 != null && var20.model != null && var20.model.vertexNormal != null) {
											int var21 = var20.maxSceneTileX - var20.minSceneTileX + 1;
											int var22 = var20.maxSceneTileZ - var20.minSceneTileZ + 1;
											this.mergeNormals(arg5, var20.model, (var20.minSceneTileX - arg0) * 128 + (var21 - arg1) * 64, var17, (var20.minSceneTileZ - arg6) * 128 + (var22 - arg2) * 64, var8);
										}
									}
								}
							}
						}
					}
				}
				var9--;
				var8 = false;
			}
		}
	}

	@ObfuscatedName("r.a(Leb;Leb;IIIZ)V")
	public void mergeNormals(Model arg0, Model arg1, int arg2, int arg3, int arg4, boolean arg5) {
		this.tmpMergeIndex++;
		int var7 = 0;
		int[] var8 = arg1.vertexX;
		int var9 = arg1.vertexCount;
		for (int var10 = 0; var10 < arg0.vertexCount; var10++) {
			VertexNormal var11 = arg0.vertexNormal[var10];
			VertexNormal var12 = arg0.vertexNormalOriginal[var10];
			if (var12.w != 0) {
				int var13 = arg0.vertexY[var10] - arg3;
				if (var13 <= arg1.minY) {
					int var14 = arg0.vertexX[var10] - arg2;
					if (var14 >= arg1.minX && var14 <= arg1.maxX) {
						int var15 = arg0.vertexZ[var10] - arg4;
						if (var15 >= arg1.minZ && var15 <= arg1.maxZ) {
							for (int var16 = 0; var16 < var9; var16++) {
								VertexNormal var17 = arg1.vertexNormal[var16];
								VertexNormal var18 = arg1.vertexNormalOriginal[var16];
								if (var14 == var8[var16] && var15 == arg1.vertexZ[var16] && var13 == arg1.vertexY[var16] && var18.w != 0) {
									var11.x += var18.x;
									var11.y += var18.y;
									var11.z += var18.z;
									var11.w += var18.w;
									var17.x += var12.x;
									var17.y += var12.y;
									var17.z += var12.z;
									var17.w += var12.w;
									var7++;
									this.mergeIndexA[var10] = this.tmpMergeIndex;
									this.mergeIndexB[var16] = this.tmpMergeIndex;
								}
							}
						}
					}
				}
			}
		}
		if (var7 < 3 || !arg5) {
			return;
		}
		for (int var19 = 0; var19 < arg0.faceCount; var19++) {
			if (this.mergeIndexA[arg0.faceVertexA[var19]] == this.tmpMergeIndex && this.mergeIndexA[arg0.faceVertexB[var19]] == this.tmpMergeIndex && this.mergeIndexA[arg0.faceVertexC[var19]] == this.tmpMergeIndex) {
				arg0.faceInfo[var19] = -1;
			}
		}
		for (int var20 = 0; var20 < arg1.faceCount; var20++) {
			if (this.mergeIndexB[arg1.faceVertexA[var20]] == this.tmpMergeIndex && this.mergeIndexB[arg1.faceVertexB[var20]] == this.tmpMergeIndex && this.mergeIndexB[arg1.faceVertexC[var20]] == this.tmpMergeIndex) {
				arg1.faceInfo[var20] = -1;
			}
		}
	}

	@ObfuscatedName("r.a([IIIIII)V")
	public void drawMinimapTile(int[] arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		Ground var7 = this.levelTiles[arg3][arg4][arg5];
		if (var7 == null) {
			return;
		}
		TileUnderlay var8 = var7.underlay;
		int var10;
		if (var8 != null) {
			int var9 = var8.rgb;
			if (var9 != 0) {
				for (var10 = 0; var10 < 4; var10++) {
					arg0[arg1] = var9;
					arg0[arg1 + 1] = var9;
					arg0[arg1 + 2] = var9;
					arg0[arg1 + 3] = var9;
					arg1 += arg2;
				}
			}
			return;
		}
		TileOverlay var18 = var7.overlay;
		if (var18 == null) {
			return;
		}
		var10 = var18.shape;
		int var11 = var18.rotation;
		int var12 = var18.backgroundRgb;
		int var13 = var18.foregroundRgb;
		int[] var14 = this.MINIMAP_OVERLAY_SHAPE[var10];
		int[] var15 = this.MINIMAP_OVERLAY_ROTATION[var11];
		int var16 = 0;
		int var17;
		if (var12 != 0) {
			for (var17 = 0; var17 < 4; var17++) {
				arg0[arg1] = var14[var15[var16++]] == 0 ? var12 : var13;
				arg0[arg1 + 1] = var14[var15[var16++]] == 0 ? var12 : var13;
				arg0[arg1 + 2] = var14[var15[var16++]] == 0 ? var12 : var13;
				arg0[arg1 + 3] = var14[var15[var16++]] == 0 ? var12 : var13;
				arg1 += arg2;
			}
			return;
		}
		for (var17 = 0; var17 < 4; var17++) {
			if (var14[var15[var16++]] != 0) {
				arg0[arg1] = var13;
			}
			if (var14[var15[var16++]] != 0) {
				arg0[arg1 + 1] = var13;
			}
			if (var14[var15[var16++]] != 0) {
				arg0[arg1 + 2] = var13;
			}
			if (var14[var15[var16++]] != 0) {
				arg0[arg1 + 3] = var13;
			}
			arg1 += arg2;
		}
	}

	@ObfuscatedName("r.a([IIIBII)V")
	public static void init(int[] arg0, int arg1, int arg2, byte arg3, int arg4, int arg5) {
		viewportLeft = 0;
		viewportTop = 0;
		viewportRight = arg2;
		viewportBottom = arg4;
		viewportCenterX = arg2 / 2;
		if (arg3 != 15) {
			field216 = 29;
		}
		viewportCenterY = arg4 / 2;
		boolean[][][][] var6 = new boolean[9][32][53][53];
		int var8;
		int var9;
		int var10;
		int var11;
		int var13;
		int var14;
		for (int var7 = 128; var7 <= 384; var7 += 32) {
			for (var8 = 0; var8 < 2048; var8 += 64) {
				sinEyePitch = Model.sin[var7];
				cosEyePitch = Model.cos[var7];
				sinEyeYaw = Model.sin[var8];
				cosEyeYaw = Model.cos[var8];
				var9 = (var7 - 128) / 32;
				var10 = var8 / 64;
				for (var11 = -26; var11 <= 26; var11++) {
					for (int var12 = -26; var12 <= 26; var12++) {
						var13 = var11 * 128;
						var14 = var12 * 128;
						boolean var15 = false;
						for (int var16 = -arg5; var16 <= arg1; var16 += 128) {
							if (testPoint(var13, var14, arg0[var9] + var16, -268)) {
								var15 = true;
								break;
							}
						}
						var6[var9][var10][var11 + 25 + 1][var12 + 25 + 1] = var15;
					}
				}
			}
		}
		for (var8 = 0; var8 < 8; var8++) {
			for (var9 = 0; var9 < 32; var9++) {
				for (var10 = -25; var10 < 25; var10++) {
					for (var11 = -25; var11 < 25; var11++) {
						boolean var17 = false;
						label80: for (var13 = -1; var13 <= 1; var13++) {
							for (var14 = -1; var14 <= 1; var14++) {
								if (var6[var8][var9][var10 + var13 + 25 + 1][var11 + var14 + 25 + 1]) {
									var17 = true;
									break label80;
								}
								if (var6[var8][(var9 + 1) % 31][var10 + var13 + 25 + 1][var11 + var14 + 25 + 1]) {
									var17 = true;
									break label80;
								}
								if (var6[var8 + 1][var9][var10 + var13 + 25 + 1][var11 + var14 + 25 + 1]) {
									var17 = true;
									break label80;
								}
								if (var6[var8 + 1][(var9 + 1) % 31][var10 + var13 + 25 + 1][var11 + var14 + 25 + 1]) {
									var17 = true;
									break label80;
								}
							}
						}
						visibilityMatrix[var8][var9][var10 + 25][var11 + 25] = var17;
					}
				}
			}
		}
	}

	@ObfuscatedName("r.h(IIII)Z")
	public static boolean testPoint(int arg0, int arg1, int arg2, int arg3) {
		int var4 = arg1 * sinEyeYaw + arg0 * cosEyeYaw >> 16;
		int var5 = arg1 * cosEyeYaw - arg0 * sinEyeYaw >> 16;
		int var6 = arg2 * sinEyePitch + var5 * cosEyePitch >> 16;
		int var7 = arg2 * cosEyePitch - var5 * sinEyePitch >> 16;
		int var8;
		while (arg3 >= 0) {
			for (var8 = 1; var8 > 0; var8++) {
			}
		}
		if (var6 < 50 || var6 > 3500) {
			return false;
		}
		var8 = viewportCenterX + (var4 << 9) / var6;
		int var9 = viewportCenterY + (var7 << 9) / var6;
		if (var8 >= viewportLeft && var8 <= viewportRight && var9 >= viewportTop && var9 <= viewportBottom) {
			return true;
		} else {
			return false;
		}
	}

	@ObfuscatedName("r.e(III)V")
	public void click(int arg0, int arg1, int arg2) {
		takingInput = true;
		mouseX = arg2;
		if (arg0 >= 4 && arg0 <= 4) {
			mouseY = arg1;
			clickTileX = -1;
			clickTileZ = -1;
		}
	}

	@ObfuscatedName("r.a(IIIIIII)V")
	public void draw(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		if (arg1 < 0) {
			arg1 = 0;
		} else if (arg1 >= this.maxTileX * 128) {
			arg1 = this.maxTileX * 128 - 1;
		}
		if (arg5 < 0) {
			arg5 = 0;
		} else if (arg5 >= this.maxTileZ * 128) {
			arg5 = this.maxTileZ * 128 - 1;
		}
		cycle++;
		sinEyePitch = Model.sin[arg3];
		cosEyePitch = Model.cos[arg3];
		sinEyeYaw = Model.sin[arg0];
		cosEyeYaw = Model.cos[arg0];
		visibilityMap = visibilityMatrix[(arg3 - 128) / 32][arg0 / 64];
		eyeX = arg1;
		eyeY = arg4;
		eyeZ = arg5;
		eyeTileX = arg1 / 128;
		eyeTileZ = arg5 / 128;
		topLevel = arg2;
		minDrawTileX = eyeTileX - 25;
		if (minDrawTileX < 0) {
			minDrawTileX = 0;
		}
		minDrawTileZ = eyeTileZ - 25;
		if (minDrawTileZ < 0) {
			minDrawTileZ = 0;
		}
		maxDrawTileX = eyeTileX + 25;
		if (arg6 != 0) {
			this.field214 = -462;
		}
		if (maxDrawTileX > this.maxTileX) {
			maxDrawTileX = this.maxTileX;
		}
		maxDrawTileZ = eyeTileZ + 25;
		if (maxDrawTileZ > this.maxTileZ) {
			maxDrawTileZ = this.maxTileZ;
		}
		this.updateActiveOccluders(false);
		tilesRemaining = 0;
		int var10;
		int var11;
		for (int var8 = this.minLevel; var8 < this.maxLevel; var8++) {
			Ground[][] var9 = this.levelTiles[var8];
			for (var10 = minDrawTileX; var10 < maxDrawTileX; var10++) {
				for (var11 = minDrawTileZ; var11 < maxDrawTileZ; var11++) {
					Ground var12 = var9[var10][var11];
					if (var12 != null) {
						if (var12.drawLevel <= arg2 && (visibilityMap[var10 - eyeTileX + 25][var11 - eyeTileZ + 25] || this.levelHeightmaps[var8][var10][var11] - arg4 >= 2000)) {
							var12.visible = true;
							var12.update = true;
							if (var12.locCount > 0) {
								var12.containsLocs = true;
							} else {
								var12.containsLocs = false;
							}
							tilesRemaining++;
						} else {
							var12.visible = false;
							var12.update = false;
							var12.checkLocSpans = 0;
						}
					}
				}
			}
		}
		int var13;
		int var14;
		int var15;
		int var16;
		int var22;
		for (int var19 = this.minLevel; var19 < this.maxLevel; var19++) {
			Ground[][] var20 = this.levelTiles[var19];
			for (var11 = -25; var11 <= 0; var11++) {
				var22 = eyeTileX + var11;
				var13 = eyeTileX - var11;
				if (var22 >= minDrawTileX || var13 < maxDrawTileX) {
					for (var14 = -25; var14 <= 0; var14++) {
						var15 = eyeTileZ + var14;
						var16 = eyeTileZ - var14;
						Ground var17;
						if (var22 >= minDrawTileX) {
							if (var15 >= minDrawTileZ) {
								var17 = var20[var22][var15];
								if (var17 != null && var17.visible) {
									this.drawTile(var17, true);
								}
							}
							if (var16 < maxDrawTileZ) {
								var17 = var20[var22][var16];
								if (var17 != null && var17.visible) {
									this.drawTile(var17, true);
								}
							}
						}
						if (var13 < maxDrawTileX) {
							if (var15 >= minDrawTileZ) {
								var17 = var20[var13][var15];
								if (var17 != null && var17.visible) {
									this.drawTile(var17, true);
								}
							}
							if (var16 < maxDrawTileZ) {
								var17 = var20[var13][var16];
								if (var17 != null && var17.visible) {
									this.drawTile(var17, true);
								}
							}
						}
						if (tilesRemaining == 0) {
							takingInput = false;
							return;
						}
					}
				}
			}
		}
		for (var10 = this.minLevel; var10 < this.maxLevel; var10++) {
			Ground[][] var21 = this.levelTiles[var10];
			for (var22 = -25; var22 <= 0; var22++) {
				var13 = eyeTileX + var22;
				var14 = eyeTileX - var22;
				if (var13 >= minDrawTileX || var14 < maxDrawTileX) {
					for (var15 = -25; var15 <= 0; var15++) {
						var16 = eyeTileZ + var15;
						int var23 = eyeTileZ - var15;
						Ground var18;
						if (var13 >= minDrawTileX) {
							if (var16 >= minDrawTileZ) {
								var18 = var21[var13][var16];
								if (var18 != null && var18.visible) {
									this.drawTile(var18, false);
								}
							}
							if (var23 < maxDrawTileZ) {
								var18 = var21[var13][var23];
								if (var18 != null && var18.visible) {
									this.drawTile(var18, false);
								}
							}
						}
						if (var14 < maxDrawTileX) {
							if (var16 >= minDrawTileZ) {
								var18 = var21[var14][var16];
								if (var18 != null && var18.visible) {
									this.drawTile(var18, false);
								}
							}
							if (var23 < maxDrawTileZ) {
								var18 = var21[var14][var23];
								if (var18 != null && var18.visible) {
									this.drawTile(var18, false);
								}
							}
						}
						if (tilesRemaining == 0) {
							takingInput = false;
							return;
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("r.a(Lcb;Z)V")
	public void drawTile(Ground arg0, boolean arg1) {
		drawTileQueue.addTail(arg0);
		while (true) {
			Ground var3;
			int var4;
			int var5;
			int var6;
			int var7;
			Ground[][] var8;
			Ground var9;
			int var11;
			int var14;
			int var15;
			int var16;
			int var17;
			int var18;
			Wall var25;
			int var26;
			int var29;
			do {
				do {
					do {
						do {
							do {
								do {
									while (true) {
										Location var12;
										int var22;
										boolean var23;
										Ground var35;
										while (true) {
											do {
												var3 = (Ground) drawTileQueue.removeHead();
												if (var3 == null) {
													return;
												}
											} while (!var3.update);
											var4 = var3.x;
											var5 = var3.z;
											var6 = var3.level;
											var7 = var3.occludeLevel;
											var8 = this.levelTiles[var6];
											if (!var3.visible) {
												break;
											}
											if (arg1) {
												if (var6 > 0) {
													var9 = this.levelTiles[var6 - 1][var4][var5];
													if (var9 != null && var9.update) {
														continue;
													}
												}
												if (var4 <= eyeTileX && var4 > minDrawTileX) {
													var9 = var8[var4 - 1][var5];
													if (var9 != null && var9.update && (var9.visible || (var3.locSpans & 0x1) == 0)) {
														continue;
													}
												}
												if (var4 >= eyeTileX && var4 < maxDrawTileX - 1) {
													var9 = var8[var4 + 1][var5];
													if (var9 != null && var9.update && (var9.visible || (var3.locSpans & 0x4) == 0)) {
														continue;
													}
												}
												if (var5 <= eyeTileZ && var5 > minDrawTileZ) {
													var9 = var8[var4][var5 - 1];
													if (var9 != null && var9.update && (var9.visible || (var3.locSpans & 0x8) == 0)) {
														continue;
													}
												}
												if (var5 >= eyeTileZ && var5 < maxDrawTileZ - 1) {
													var9 = var8[var4][var5 + 1];
													if (var9 != null && var9.update && (var9.visible || (var3.locSpans & 0x2) == 0)) {
														continue;
													}
												}
											} else {
												arg1 = true;
											}
											var3.visible = false;
											if (var3.bridge != null) {
												var9 = var3.bridge;
												if (var9.underlay == null) {
													if (var9.overlay != null && !this.tileVisible(0, var4, var5)) {
														this.drawTileOverlay(sinEyeYaw, var5, var9.overlay, var4, cosEyePitch, sinEyePitch, cosEyeYaw, true);
													}
												} else if (!this.tileVisible(0, var4, var5)) {
													this.drawTileUnderlay(var9.underlay, 0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var4, var5);
												}
												Wall var10 = var9.wall;
												if (var10 != null) {
													var10.modelA.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var10.x - eyeX, var10.y - eyeY, var10.z - eyeZ, var10.bitset);
												}
												for (var11 = 0; var11 < var9.locCount; var11++) {
													var12 = var9.locs[var11];
													if (var12 != null) {
														Model var13 = var12.model;
														if (var13 == null) {
															var13 = var12.entity.draw(true);
														}
														var13.draw(var12.yaw, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var12.x - eyeX, var12.y - eyeY, var12.z - eyeZ, var12.bitset);
													}
												}
											}
											var23 = false;
											if (var3.underlay == null) {
												if (var3.overlay != null && !this.tileVisible(var7, var4, var5)) {
													var23 = true;
													this.drawTileOverlay(sinEyeYaw, var5, var3.overlay, var4, cosEyePitch, sinEyePitch, cosEyeYaw, true);
												}
											} else if (!this.tileVisible(var7, var4, var5)) {
												var23 = true;
												this.drawTileUnderlay(var3.underlay, var7, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var4, var5);
											}
											var22 = 0;
											var11 = 0;
											Wall var24 = var3.wall;
											Decor var28 = var3.decor;
											if (var24 != null || var28 != null) {
												if (eyeTileX == var4) {
													var22++;
												} else if (eyeTileX < var4) {
													var22 += 2;
												}
												if (eyeTileZ == var5) {
													var22 += 3;
												} else if (eyeTileZ > var5) {
													var22 += 6;
												}
												var11 = FRONT_WALL_TYPES[var22];
												var3.backWallTypes = BACK_WALL_TYPES[var22];
											}
											if (var24 != null) {
												if ((var24.typeA & DIRECTION_ALLOW_WALL_CORNER_TYPE[var22]) == 0) {
													var3.checkLocSpans = 0;
												} else if (var24.typeA == 16) {
													var3.checkLocSpans = 3;
													var3.blockLocSpans = WALL_CORNER_TYPE_16_BLOCK_LOC_SPANS[var22];
													var3.inverseBlockLocSpans = 3 - var3.blockLocSpans;
												} else if (var24.typeA == 32) {
													var3.checkLocSpans = 6;
													var3.blockLocSpans = WALL_CORNER_TYPE_32_BLOCK_LOC_SPANS[var22];
													var3.inverseBlockLocSpans = 6 - var3.blockLocSpans;
												} else if (var24.typeA == 64) {
													var3.checkLocSpans = 12;
													var3.blockLocSpans = WALL_CORNER_TYPE_64_BLOCK_LOC_SPANS[var22];
													var3.inverseBlockLocSpans = 12 - var3.blockLocSpans;
												} else {
													var3.checkLocSpans = 9;
													var3.blockLocSpans = WALL_CORNER_TYPE_128_BLOCK_LOC_SPANS[var22];
													var3.inverseBlockLocSpans = 9 - var3.blockLocSpans;
												}
												if ((var24.typeA & var11) != 0 && !this.wallVisible(var7, var4, var5, var24.typeA)) {
													var24.modelA.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var24.x - eyeX, var24.y - eyeY, var24.z - eyeZ, var24.bitset);
												}
												if ((var24.typeB & var11) != 0 && !this.wallVisible(var7, var4, var5, var24.typeB)) {
													var24.modelB.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var24.x - eyeX, var24.y - eyeY, var24.z - eyeZ, var24.bitset);
												}
											}
											if (var28 != null && !this.visible(var7, var4, var5, var28.model.maxY)) {
												if ((var28.type & var11) != 0) {
													var28.model.draw(var28.angle, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var28.x - eyeX, var28.y - eyeY, var28.z - eyeZ, var28.bitset);
												} else if ((var28.type & 0x300) != 0) {
													var14 = var28.x - eyeX;
													var15 = var28.y - eyeY;
													var16 = var28.z - eyeZ;
													var17 = var28.angle;
													if (var17 == 1 || var17 == 2) {
														var18 = -var14;
													} else {
														var18 = var14;
													}
													int var19;
													if (var17 == 2 || var17 == 3) {
														var19 = -var16;
													} else {
														var19 = var16;
													}
													int var20;
													int var21;
													if ((var28.type & 0x100) != 0 && var19 < var18) {
														var20 = var14 + WALL_DECORATION_INSET_X[var17];
														var21 = var16 + WALL_DECORATION_INSET_Z[var17];
														var28.model.draw(var17 * 512 + 256, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var20, var15, var21, var28.bitset);
													}
													if ((var28.type & 0x200) != 0 && var19 > var18) {
														var20 = var14 + WALL_DECORATION_OUTSET_X[var17];
														var21 = var16 + WALL_DECORATION_OUTSET_Z[var17];
														var28.model.draw(var17 * 512 + 1280 & 0x7FF, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var20, var15, var21, var28.bitset);
													}
												}
											}
											if (var23) {
												GroundDecor var30 = var3.groundDecor;
												if (var30 != null) {
													var30.model.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var30.x - eyeX, var30.y - eyeY, var30.z - eyeZ, var30.bitset);
												}
												GroundObject var34 = var3.groundObj;
												if (var34 != null && var34.offset == 0) {
													if (var34.bottomObj != null) {
														var34.bottomObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var34.x - eyeX, var34.y - eyeY, var34.z - eyeZ, var34.bitset);
													}
													if (var34.middleObj != null) {
														var34.middleObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var34.x - eyeX, var34.y - eyeY, var34.z - eyeZ, var34.bitset);
													}
													if (var34.topObj != null) {
														var34.topObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var34.x - eyeX, var34.y - eyeY, var34.z - eyeZ, var34.bitset);
													}
												}
											}
											var14 = var3.locSpans;
											if (var14 != 0) {
												if (var4 < eyeTileX && (var14 & 0x4) != 0) {
													var35 = var8[var4 + 1][var5];
													if (var35 != null && var35.update) {
														drawTileQueue.addTail(var35);
													}
												}
												if (var5 < eyeTileZ && (var14 & 0x2) != 0) {
													var35 = var8[var4][var5 + 1];
													if (var35 != null && var35.update) {
														drawTileQueue.addTail(var35);
													}
												}
												if (var4 > eyeTileX && (var14 & 0x1) != 0) {
													var35 = var8[var4 - 1][var5];
													if (var35 != null && var35.update) {
														drawTileQueue.addTail(var35);
													}
												}
												if (var5 > eyeTileZ && (var14 & 0x8) != 0) {
													var35 = var8[var4][var5 - 1];
													if (var35 != null && var35.update) {
														drawTileQueue.addTail(var35);
													}
												}
											}
											break;
										}
										if (var3.checkLocSpans != 0) {
											var23 = true;
											for (var22 = 0; var22 < var3.locCount; var22++) {
												if (var3.locs[var22].cycle != cycle && (var3.locSpan[var22] & var3.checkLocSpans) == var3.blockLocSpans) {
													var23 = false;
													break;
												}
											}
											if (var23) {
												var25 = var3.wall;
												if (!this.wallVisible(var7, var4, var5, var25.typeA)) {
													var25.modelA.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var25.x - eyeX, var25.y - eyeY, var25.z - eyeZ, var25.bitset);
												}
												var3.checkLocSpans = 0;
											}
										}
										if (!var3.containsLocs) {
											break;
										}
										int var27 = var3.locCount;
										var3.containsLocs = false;
										var22 = 0;
										label559: for (var11 = 0; var11 < var27; var11++) {
											var12 = var3.locs[var11];
											if (var12.cycle != cycle) {
												for (var29 = var12.minSceneTileX; var29 <= var12.maxSceneTileX; var29++) {
													for (var14 = var12.minSceneTileZ; var14 <= var12.maxSceneTileZ; var14++) {
														var35 = var8[var29][var14];
														if (var35.visible) {
															var3.containsLocs = true;
															continue label559;
														}
														if (var35.checkLocSpans != 0) {
															var16 = 0;
															if (var29 > var12.minSceneTileX) {
																var16++;
															}
															if (var29 < var12.maxSceneTileX) {
																var16 += 4;
															}
															if (var14 > var12.minSceneTileZ) {
																var16 += 8;
															}
															if (var14 < var12.maxSceneTileZ) {
																var16 += 2;
															}
															if ((var16 & var35.checkLocSpans) == var3.inverseBlockLocSpans) {
																var3.containsLocs = true;
																continue label559;
															}
														}
													}
												}
												locBuffer[var22++] = var12;
												var14 = eyeTileX - var12.minSceneTileX;
												var15 = var12.maxSceneTileX - eyeTileX;
												if (var15 > var14) {
													var14 = var15;
												}
												var16 = eyeTileZ - var12.minSceneTileZ;
												var17 = var12.maxSceneTileZ - eyeTileZ;
												if (var17 > var16) {
													var12.distance = var14 + var17;
												} else {
													var12.distance = var14 + var16;
												}
											}
										}
										while (var22 > 0) {
											var26 = -50;
											var29 = -1;
											Location var36;
											for (var14 = 0; var14 < var22; var14++) {
												var36 = locBuffer[var14];
												if (var36.distance > var26 && var36.cycle != cycle) {
													var26 = var36.distance;
													var29 = var14;
												}
											}
											if (var29 == -1) {
												break;
											}
											var36 = locBuffer[var29];
											var36.cycle = cycle;
											Model var37 = var36.model;
											if (var37 == null) {
												var37 = var36.entity.draw(true);
											}
											if (!this.locVisible(var7, var36.minSceneTileX, var36.maxSceneTileX, var36.minSceneTileZ, var36.maxSceneTileZ, var37.maxY)) {
												var37.draw(var36.yaw, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var36.x - eyeX, var36.y - eyeY, var36.z - eyeZ, var36.bitset);
											}
											for (var17 = var36.minSceneTileX; var17 <= var36.maxSceneTileX; var17++) {
												for (var18 = var36.minSceneTileZ; var18 <= var36.maxSceneTileZ; var18++) {
													Ground var38 = var8[var17][var18];
													if (var38.checkLocSpans != 0) {
														drawTileQueue.addTail(var38);
													} else if ((var17 != var4 || var18 != var5) && var38.update) {
														drawTileQueue.addTail(var38);
													}
												}
											}
										}
										if (!var3.containsLocs) {
											break;
										}
									}
								} while (!var3.update);
							} while (var3.checkLocSpans != 0);
							if (var4 > eyeTileX || var4 <= minDrawTileX) {
								break;
							}
							var9 = var8[var4 - 1][var5];
						} while (var9 != null && var9.update);
						if (var4 < eyeTileX || var4 >= maxDrawTileX - 1) {
							break;
						}
						var9 = var8[var4 + 1][var5];
					} while (var9 != null && var9.update);
					if (var5 > eyeTileZ || var5 <= minDrawTileZ) {
						break;
					}
					var9 = var8[var4][var5 - 1];
				} while (var9 != null && var9.update);
				if (var5 < eyeTileZ || var5 >= maxDrawTileZ - 1) {
					break;
				}
				var9 = var8[var4][var5 + 1];
			} while (var9 != null && var9.update);
			var3.update = false;
			tilesRemaining--;
			GroundObject var32 = var3.groundObj;
			if (var32 != null && var32.offset != 0) {
				if (var32.bottomObj != null) {
					var32.bottomObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var32.x - eyeX, var32.y - eyeY - var32.offset, var32.z - eyeZ, var32.bitset);
				}
				if (var32.middleObj != null) {
					var32.middleObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var32.x - eyeX, var32.y - eyeY - var32.offset, var32.z - eyeZ, var32.bitset);
				}
				if (var32.topObj != null) {
					var32.topObj.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var32.x - eyeX, var32.y - eyeY - var32.offset, var32.z - eyeZ, var32.bitset);
				}
			}
			if (var3.backWallTypes != 0) {
				Decor var31 = var3.decor;
				if (var31 != null && !this.visible(var7, var4, var5, var31.model.maxY)) {
					if ((var31.type & var3.backWallTypes) != 0) {
						var31.model.draw(var31.angle, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var31.x - eyeX, var31.y - eyeY, var31.z - eyeZ, var31.bitset);
					} else if ((var31.type & 0x300) != 0) {
						var11 = var31.x - eyeX;
						var26 = var31.y - eyeY;
						var29 = var31.z - eyeZ;
						var14 = var31.angle;
						if (var14 == 1 || var14 == 2) {
							var15 = -var11;
						} else {
							var15 = var11;
						}
						if (var14 == 2 || var14 == 3) {
							var16 = -var29;
						} else {
							var16 = var29;
						}
						if ((var31.type & 0x100) != 0 && var16 >= var15) {
							var17 = var11 + WALL_DECORATION_INSET_X[var14];
							var18 = var29 + WALL_DECORATION_INSET_Z[var14];
							var31.model.draw(var14 * 512 + 256, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var17, var26, var18, var31.bitset);
						}
						if ((var31.type & 0x200) != 0 && var16 <= var15) {
							var17 = var11 + WALL_DECORATION_OUTSET_X[var14];
							var18 = var29 + WALL_DECORATION_OUTSET_Z[var14];
							var31.model.draw(var14 * 512 + 1280 & 0x7FF, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var17, var26, var18, var31.bitset);
						}
					}
				}
				var25 = var3.wall;
				if (var25 != null) {
					if ((var25.typeB & var3.backWallTypes) != 0 && !this.wallVisible(var7, var4, var5, var25.typeB)) {
						var25.modelB.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var25.x - eyeX, var25.y - eyeY, var25.z - eyeZ, var25.bitset);
					}
					if ((var25.typeA & var3.backWallTypes) != 0 && !this.wallVisible(var7, var4, var5, var25.typeA)) {
						var25.modelA.draw(0, sinEyePitch, cosEyePitch, sinEyeYaw, cosEyeYaw, var25.x - eyeX, var25.y - eyeY, var25.z - eyeZ, var25.bitset);
					}
				}
			}
			Ground var33;
			if (var6 < this.maxLevel - 1) {
				var33 = this.levelTiles[var6 + 1][var4][var5];
				if (var33 != null && var33.update) {
					drawTileQueue.addTail(var33);
				}
			}
			if (var4 < eyeTileX) {
				var33 = var8[var4 + 1][var5];
				if (var33 != null && var33.update) {
					drawTileQueue.addTail(var33);
				}
			}
			if (var5 < eyeTileZ) {
				var33 = var8[var4][var5 + 1];
				if (var33 != null && var33.update) {
					drawTileQueue.addTail(var33);
				}
			}
			if (var4 > eyeTileX) {
				var33 = var8[var4 - 1][var5];
				if (var33 != null && var33.update) {
					drawTileQueue.addTail(var33);
				}
			}
			if (var5 > eyeTileZ) {
				var33 = var8[var4][var5 - 1];
				if (var33 != null && var33.update) {
					drawTileQueue.addTail(var33);
				}
			}
		}
	}

	@ObfuscatedName("r.a(Lo;IIIIIII)V")
	public void drawTileUnderlay(TileUnderlay arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		int var9;
		int var10 = var9 = (arg6 << 7) - eyeX;
		int var11;
		int var12 = var11 = (arg7 << 7) - eyeZ;
		int var13;
		int var14 = var13 = var10 + 128;
		int var15;
		int var16 = var15 = var12 + 128;
		int var17 = this.levelHeightmaps[arg1][arg6][arg7] - eyeY;
		int var18 = this.levelHeightmaps[arg1][arg6 + 1][arg7] - eyeY;
		int var19 = this.levelHeightmaps[arg1][arg6 + 1][arg7 + 1] - eyeY;
		int var20 = this.levelHeightmaps[arg1][arg6][arg7 + 1] - eyeY;
		int var21 = var12 * arg4 + var10 * arg5 >> 16;
		int var35 = var12 * arg5 - var10 * arg4 >> 16;
		int var32 = var21;
		int var41 = var17 * arg3 - var35 * arg2 >> 16;
		int var36 = var17 * arg2 + var35 * arg3 >> 16;
		int var40 = var41;
		if (var36 < 50) {
			return;
		}
		var21 = var11 * arg4 + var14 * arg5 >> 16;
		int var33 = var11 * arg5 - var14 * arg4 >> 16;
		var14 = var21;
		var21 = var18 * arg3 - var33 * arg2 >> 16;
		int var34 = var18 * arg2 + var33 * arg3 >> 16;
		var18 = var21;
		if (var34 < 50) {
			return;
		}
		var21 = var16 * arg4 + var13 * arg5 >> 16;
		var16 = var16 * arg5 - var13 * arg4 >> 16;
		int var37 = var21;
		var21 = var19 * arg3 - var16 * arg2 >> 16;
		var16 = var19 * arg2 + var16 * arg3 >> 16;
		var19 = var21;
		if (var16 < 50) {
			return;
		}
		var21 = var15 * arg4 + var9 * arg5 >> 16;
		int var38 = var15 * arg5 - var9 * arg4 >> 16;
		int var31 = var21;
		var21 = var20 * arg3 - var38 * arg2 >> 16;
		int var39 = var20 * arg2 + var38 * arg3 >> 16;
		if (var39 < 50) {
			return;
		}
		int var22 = Pix3D.centerW3D + (var32 << 9) / var36;
		int var23 = Pix3D.centerH3D + (var40 << 9) / var36;
		int var24 = Pix3D.centerW3D + (var14 << 9) / var34;
		int var25 = Pix3D.centerH3D + (var18 << 9) / var34;
		int var26 = Pix3D.centerW3D + (var37 << 9) / var16;
		int var27 = Pix3D.centerH3D + (var19 << 9) / var16;
		int var28 = Pix3D.centerW3D + (var31 << 9) / var39;
		int var29 = Pix3D.centerH3D + (var21 << 9) / var39;
		Pix3D.trans = 0;
		int var30;
		if ((var26 - var28) * (var25 - var29) - (var27 - var29) * (var24 - var28) > 0) {
			Pix3D.hclip = false;
			if (var26 < 0 || var28 < 0 || var24 < 0 || var26 > Pix2D.safeWidth || var28 > Pix2D.safeWidth || var24 > Pix2D.safeWidth) {
				Pix3D.hclip = true;
			}
			if (takingInput && this.pointInsideTriangle(mouseX, mouseY, var27, var29, var25, var26, var28, var24)) {
				clickTileX = arg6;
				clickTileZ = arg7;
			}
			if (arg0.textureId == -1) {
				if (arg0.northeastColor != 12345678) {
					Pix3D.gouraudTriangle(var27, var29, var25, var26, var28, var24, arg0.northeastColor, arg0.northwestColor, arg0.southeastColor);
				}
			} else if (lowMemory) {
				var30 = TEXTURE_HSL[arg0.textureId];
				Pix3D.gouraudTriangle(var27, var29, var25, var26, var28, var24, this.mulLightness(arg0.northeastColor, var30, 9), this.mulLightness(arg0.northwestColor, var30, 9), this.mulLightness(arg0.southeastColor, var30, 9));
			} else if (arg0.flat) {
				Pix3D.textureTriangle(var27, var29, var25, var26, var28, var24, arg0.northeastColor, arg0.northwestColor, arg0.southeastColor, var32, var14, var31, var40, var18, var21, var36, var34, var39, arg0.textureId);
			} else {
				Pix3D.textureTriangle(var27, var29, var25, var26, var28, var24, arg0.northeastColor, arg0.northwestColor, arg0.southeastColor, var37, var31, var14, var19, var21, var18, var16, var39, var34, arg0.textureId);
			}
		}
		if ((var22 - var24) * (var29 - var25) - (var23 - var25) * (var28 - var24) <= 0) {
			return;
		}
		Pix3D.hclip = false;
		if (var22 < 0 || var24 < 0 || var28 < 0 || var22 > Pix2D.safeWidth || var24 > Pix2D.safeWidth || var28 > Pix2D.safeWidth) {
			Pix3D.hclip = true;
		}
		if (takingInput && this.pointInsideTriangle(mouseX, mouseY, var23, var25, var29, var22, var24, var28)) {
			clickTileX = arg6;
			clickTileZ = arg7;
		}
		if (arg0.textureId != -1) {
			if (!lowMemory) {
				Pix3D.textureTriangle(var23, var25, var29, var22, var24, var28, arg0.southwestColor, arg0.southeastColor, arg0.northwestColor, var32, var14, var31, var40, var18, var21, var36, var34, var39, arg0.textureId);
				return;
			}
			var30 = TEXTURE_HSL[arg0.textureId];
			Pix3D.gouraudTriangle(var23, var25, var29, var22, var24, var28, this.mulLightness(arg0.southwestColor, var30, 9), this.mulLightness(arg0.southeastColor, var30, 9), this.mulLightness(arg0.northwestColor, var30, 9));
		} else if (arg0.southwestColor != 12345678) {
			Pix3D.gouraudTriangle(var23, var25, var29, var22, var24, var28, arg0.southwestColor, arg0.southeastColor, arg0.northwestColor);
			return;
		}
	}

	@ObfuscatedName("r.a(IILi;IIIIZ)V")
	public void drawTileOverlay(int arg0, int arg1, TileOverlay arg2, int arg3, int arg4, int arg5, int arg6, boolean arg7) {
		int var9 = arg2.vertexX.length;
		int var11;
		int var12;
		int var13;
		int var14;
		for (int var10 = 0; var10 < var9; var10++) {
			var11 = arg2.vertexX[var10] - eyeX;
			var12 = arg2.vertexY[var10] - eyeY;
			var13 = arg2.vertexZ[var10] - eyeZ;
			var14 = var13 * arg0 + var11 * arg6 >> 16;
			int var23 = var13 * arg6 - var11 * arg0 >> 16;
			int var25 = var12 * arg4 - var23 * arg5 >> 16;
			int var24 = var12 * arg5 + var23 * arg4 >> 16;
			if (var24 < 50) {
				return;
			}
			if (arg2.triangleTextureIds != null) {
				TileOverlay.tmpViewspaceX[var10] = var14;
				TileOverlay.tmpViewspaceY[var10] = var25;
				TileOverlay.tmpViewspaceZ[var10] = var24;
			}
			TileOverlay.tmpScreenX[var10] = Pix3D.centerW3D + (var14 << 9) / var24;
			TileOverlay.tmpScreenY[var10] = Pix3D.centerH3D + (var25 << 9) / var24;
		}
		Pix3D.trans = 0;
		var9 = arg2.triangleVertexA.length;
		if (!arg7) {
			return;
		}
		for (var11 = 0; var11 < var9; var11++) {
			var12 = arg2.triangleVertexA[var11];
			var13 = arg2.triangleVertexB[var11];
			var14 = arg2.triangleVertexC[var11];
			int var15 = TileOverlay.tmpScreenX[var12];
			int var16 = TileOverlay.tmpScreenX[var13];
			int var17 = TileOverlay.tmpScreenX[var14];
			int var18 = TileOverlay.tmpScreenY[var12];
			int var19 = TileOverlay.tmpScreenY[var13];
			int var20 = TileOverlay.tmpScreenY[var14];
			if ((var15 - var16) * (var20 - var19) - (var18 - var19) * (var17 - var16) > 0) {
				Pix3D.hclip = false;
				if (var15 < 0 || var16 < 0 || var17 < 0 || var15 > Pix2D.safeWidth || var16 > Pix2D.safeWidth || var17 > Pix2D.safeWidth) {
					Pix3D.hclip = true;
				}
				if (takingInput && this.pointInsideTriangle(mouseX, mouseY, var18, var19, var20, var15, var16, var17)) {
					clickTileX = arg3;
					clickTileZ = arg1;
				}
				if (arg2.triangleTextureIds == null || arg2.triangleTextureIds[var11] == -1) {
					if (arg2.triangleColorA[var11] != 12345678) {
						Pix3D.gouraudTriangle(var18, var19, var20, var15, var16, var17, arg2.triangleColorA[var11], arg2.triangleColorB[var11], arg2.triangleColorC[var11]);
					}
				} else if (lowMemory) {
					int var21 = TEXTURE_HSL[arg2.triangleTextureIds[var11]];
					Pix3D.gouraudTriangle(var18, var19, var20, var15, var16, var17, this.mulLightness(arg2.triangleColorA[var11], var21, 9), this.mulLightness(arg2.triangleColorB[var11], var21, 9), this.mulLightness(arg2.triangleColorC[var11], var21, 9));
				} else if (arg2.flat) {
					Pix3D.textureTriangle(var18, var19, var20, var15, var16, var17, arg2.triangleColorA[var11], arg2.triangleColorB[var11], arg2.triangleColorC[var11], TileOverlay.tmpViewspaceX[0], TileOverlay.tmpViewspaceX[1], TileOverlay.tmpViewspaceX[3], TileOverlay.tmpViewspaceY[0], TileOverlay.tmpViewspaceY[1], TileOverlay.tmpViewspaceY[3], TileOverlay.tmpViewspaceZ[0], TileOverlay.tmpViewspaceZ[1], TileOverlay.tmpViewspaceZ[3], arg2.triangleTextureIds[var11]);
				} else {
					Pix3D.textureTriangle(var18, var19, var20, var15, var16, var17, arg2.triangleColorA[var11], arg2.triangleColorB[var11], arg2.triangleColorC[var11], TileOverlay.tmpViewspaceX[var12], TileOverlay.tmpViewspaceX[var13], TileOverlay.tmpViewspaceX[var14], TileOverlay.tmpViewspaceY[var12], TileOverlay.tmpViewspaceY[var13], TileOverlay.tmpViewspaceY[var14], TileOverlay.tmpViewspaceZ[var12], TileOverlay.tmpViewspaceZ[var13], TileOverlay.tmpViewspaceZ[var14], arg2.triangleTextureIds[var11]);
				}
			}
		}
	}

	@ObfuscatedName("r.f(III)I")
	public int mulLightness(int arg0, int arg1, int arg2) {
		int var4 = 127 - arg0;
		if (arg2 < 9 || arg2 > 9) {
			return 4;
		}
		arg0 = var4 * (arg1 & 0x7F) / 160;
		if (arg0 < 2) {
			arg0 = 2;
		} else if (arg0 > 126) {
			arg0 = 126;
		}
		return (arg1 & 0xFF80) + arg0;
	}

	@ObfuscatedName("r.a(IIIIIIII)Z")
	public boolean pointInsideTriangle(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		if (arg1 < arg2 && arg1 < arg3 && arg1 < arg4) {
			return false;
		} else if (arg1 > arg2 && arg1 > arg3 && arg1 > arg4) {
			return false;
		} else if (arg0 < arg5 && arg0 < arg6 && arg0 < arg7) {
			return false;
		} else if (arg0 > arg5 && arg0 > arg6 && arg0 > arg7) {
			return false;
		} else {
			int var9 = (arg1 - arg2) * (arg6 - arg5) - (arg0 - arg5) * (arg3 - arg2);
			int var10 = (arg1 - arg4) * (arg5 - arg7) - (arg0 - arg7) * (arg2 - arg4);
			int var11 = (arg1 - arg3) * (arg7 - arg6) - (arg0 - arg6) * (arg4 - arg3);
			return var9 * var11 > 0 && var11 * var10 > 0;
		}
	}

	@ObfuscatedName("r.b(Z)V")
	public void updateActiveOccluders(boolean arg0) {
		int var2 = levelOccluderCount[topLevel];
		Occlude[] var3 = levelOccluders[topLevel];
		activeOccluderCount = 0;
		for (int var4 = 0; var4 < var2; var4++) {
			Occlude var5 = var3[var4];
			int var6;
			int var7;
			int var8;
			int var10;
			boolean var14;
			if (var5.type == 1) {
				var6 = var5.minTileX - eyeTileX + 25;
				if (var6 >= 0 && var6 <= 50) {
					var7 = var5.minTileZ - eyeTileZ + 25;
					if (var7 < 0) {
						var7 = 0;
					}
					var8 = var5.maxTileZ - eyeTileZ + 25;
					if (var8 > 50) {
						var8 = 50;
					}
					var14 = false;
					while (var7 <= var8) {
						if (visibilityMap[var6][var7++]) {
							var14 = true;
							break;
						}
					}
					if (var14) {
						var10 = eyeX - var5.minX;
						if (var10 > 32) {
							var5.mode = 1;
						} else {
							if (var10 >= -32) {
								continue;
							}
							var5.mode = 2;
							var10 = -var10;
						}
						var5.minDeltaZ = (var5.minZ - eyeZ << 8) / var10;
						var5.maxDeltaZ = (var5.maxZ - eyeZ << 8) / var10;
						var5.minDeltaY = (var5.minY - eyeY << 8) / var10;
						var5.maxDeltaY = (var5.maxY - eyeY << 8) / var10;
						activeOccluders[activeOccluderCount++] = var5;
					}
				}
			} else if (var5.type == 2) {
				var6 = var5.minTileZ - eyeTileZ + 25;
				if (var6 >= 0 && var6 <= 50) {
					var7 = var5.minTileX - eyeTileX + 25;
					if (var7 < 0) {
						var7 = 0;
					}
					var8 = var5.maxTileX - eyeTileX + 25;
					if (var8 > 50) {
						var8 = 50;
					}
					var14 = false;
					while (var7 <= var8) {
						if (visibilityMap[var7++][var6]) {
							var14 = true;
							break;
						}
					}
					if (var14) {
						var10 = eyeZ - var5.minZ;
						if (var10 > 32) {
							var5.mode = 3;
						} else {
							if (var10 >= -32) {
								continue;
							}
							var5.mode = 4;
							var10 = -var10;
						}
						var5.minDeltaX = (var5.minX - eyeX << 8) / var10;
						var5.maxDeltaX = (var5.maxX - eyeX << 8) / var10;
						var5.minDeltaY = (var5.minY - eyeY << 8) / var10;
						var5.maxDeltaY = (var5.maxY - eyeY << 8) / var10;
						activeOccluders[activeOccluderCount++] = var5;
					}
				}
			} else if (var5.type == 4) {
				var6 = var5.minY - eyeY;
				if (var6 > 128) {
					var7 = var5.minTileZ - eyeTileZ + 25;
					if (var7 < 0) {
						var7 = 0;
					}
					var8 = var5.maxTileZ - eyeTileZ + 25;
					if (var8 > 50) {
						var8 = 50;
					}
					if (var7 <= var8) {
						int var9 = var5.minTileX - eyeTileX + 25;
						if (var9 < 0) {
							var9 = 0;
						}
						var10 = var5.maxTileX - eyeTileX + 25;
						if (var10 > 50) {
							var10 = 50;
						}
						boolean var11 = false;
						label146: for (int var12 = var9; var12 <= var10; var12++) {
							for (int var13 = var7; var13 <= var8; var13++) {
								if (visibilityMap[var12][var13]) {
									var11 = true;
									break label146;
								}
							}
						}
						if (var11) {
							var5.mode = 5;
							var5.minDeltaX = (var5.minX - eyeX << 8) / var6;
							var5.maxDeltaX = (var5.maxX - eyeX << 8) / var6;
							var5.minDeltaZ = (var5.minZ - eyeZ << 8) / var6;
							var5.maxDeltaZ = (var5.maxZ - eyeZ << 8) / var6;
							activeOccluders[activeOccluderCount++] = var5;
						}
					}
				}
			}
		}
		if (!arg0) {
			;
		}
	}

	@ObfuscatedName("r.g(III)Z")
	public boolean tileVisible(int arg0, int arg1, int arg2) {
		int var4 = this.levelTileOcclusionCycles[arg0][arg1][arg2];
		if (var4 == -cycle) {
			return false;
		} else if (var4 == cycle) {
			return true;
		} else {
			int var5 = arg1 << 7;
			int var6 = arg2 << 7;
			if (this.occluded(var5 + 1, this.levelHeightmaps[arg0][arg1][arg2], var6 + 1) && this.occluded(var5 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg2], var6 + 1) && this.occluded(var5 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg2 + 1], var6 + 128 - 1) && this.occluded(var5 + 1, this.levelHeightmaps[arg0][arg1][arg2 + 1], var6 + 128 - 1)) {
				this.levelTileOcclusionCycles[arg0][arg1][arg2] = cycle;
				return true;
			} else {
				this.levelTileOcclusionCycles[arg0][arg1][arg2] = -cycle;
				return false;
			}
		}
	}

	@ObfuscatedName("r.i(IIII)Z")
	public boolean wallVisible(int arg0, int arg1, int arg2, int arg3) {
		if (!this.tileVisible(arg0, arg1, arg2)) {
			return false;
		}
		int var5 = arg1 << 7;
		int var6 = arg2 << 7;
		int var7 = this.levelHeightmaps[arg0][arg1][arg2] - 1;
		int var8 = var7 - 120;
		int var9 = var7 - 230;
		int var10 = var7 - 238;
		if (arg3 < 16) {
			if (arg3 == 1) {
				if (var5 > eyeX) {
					if (!this.occluded(var5, var7, var6)) {
						return false;
					}
					if (!this.occluded(var5, var7, var6 + 128)) {
						return false;
					}
				}
				if (arg0 > 0) {
					if (!this.occluded(var5, var8, var6)) {
						return false;
					}
					if (!this.occluded(var5, var8, var6 + 128)) {
						return false;
					}
				}
				if (!this.occluded(var5, var9, var6)) {
					return false;
				}
				if (!this.occluded(var5, var9, var6 + 128)) {
					return false;
				}
				return true;
			}
			if (arg3 == 2) {
				if (var6 < eyeZ) {
					if (!this.occluded(var5, var7, var6 + 128)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var7, var6 + 128)) {
						return false;
					}
				}
				if (arg0 > 0) {
					if (!this.occluded(var5, var8, var6 + 128)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var8, var6 + 128)) {
						return false;
					}
				}
				if (!this.occluded(var5, var9, var6 + 128)) {
					return false;
				}
				if (!this.occluded(var5 + 128, var9, var6 + 128)) {
					return false;
				}
				return true;
			}
			if (arg3 == 4) {
				if (var5 < eyeX) {
					if (!this.occluded(var5 + 128, var7, var6)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var7, var6 + 128)) {
						return false;
					}
				}
				if (arg0 > 0) {
					if (!this.occluded(var5 + 128, var8, var6)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var8, var6 + 128)) {
						return false;
					}
				}
				if (!this.occluded(var5 + 128, var9, var6)) {
					return false;
				}
				if (!this.occluded(var5 + 128, var9, var6 + 128)) {
					return false;
				}
				return true;
			}
			if (arg3 == 8) {
				if (var6 > eyeZ) {
					if (!this.occluded(var5, var7, var6)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var7, var6)) {
						return false;
					}
				}
				if (arg0 > 0) {
					if (!this.occluded(var5, var8, var6)) {
						return false;
					}
					if (!this.occluded(var5 + 128, var8, var6)) {
						return false;
					}
				}
				if (!this.occluded(var5, var9, var6)) {
					return false;
				}
				if (!this.occluded(var5 + 128, var9, var6)) {
					return false;
				}
				return true;
			}
		}
		if (!this.occluded(var5 + 64, var10, var6 + 64)) {
			return false;
		} else if (arg3 == 16) {
			return this.occluded(var5, var9, var6 + 128);
		} else if (arg3 == 32) {
			return this.occluded(var5 + 128, var9, var6 + 128);
		} else if (arg3 == 64) {
			return this.occluded(var5 + 128, var9, var6);
		} else if (arg3 == 128) {
			return this.occluded(var5, var9, var6);
		} else {
			System.out.println("Warning unsupported wall type");
			return true;
		}
	}

	@ObfuscatedName("r.j(IIII)Z")
	public boolean visible(int arg0, int arg1, int arg2, int arg3) {
		if (this.tileVisible(arg0, arg1, arg2)) {
			int var5 = arg1 << 7;
			int var6 = arg2 << 7;
			return this.occluded(var5 + 1, this.levelHeightmaps[arg0][arg1][arg2] - arg3, var6 + 1) && this.occluded(var5 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg2] - arg3, var6 + 1) && this.occluded(var5 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg2 + 1] - arg3, var6 + 128 - 1) && this.occluded(var5 + 1, this.levelHeightmaps[arg0][arg1][arg2 + 1] - arg3, var6 + 128 - 1);
		} else {
			return false;
		}
	}

	@ObfuscatedName("r.a(IIIIII)Z")
	public boolean locVisible(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		int var7;
		int var8;
		if (arg1 != arg2 || arg3 != arg4) {
			for (var7 = arg1; var7 <= arg2; var7++) {
				for (var8 = arg3; var8 <= arg4; var8++) {
					if (this.levelTileOcclusionCycles[arg0][var7][var8] == -cycle) {
						return false;
					}
				}
			}
			var8 = (arg1 << 7) + 1;
			int var9 = (arg3 << 7) + 2;
			int var10 = this.levelHeightmaps[arg0][arg1][arg3] - arg5;
			if (!this.occluded(var8, var10, var9)) {
				return false;
			}
			int var11 = (arg2 << 7) - 1;
			if (!this.occluded(var11, var10, var9)) {
				return false;
			}
			int var12 = (arg4 << 7) - 1;
			if (!this.occluded(var8, var10, var12)) {
				return false;
			} else if (this.occluded(var11, var10, var12)) {
				return true;
			} else {
				return false;
			}
		} else if (this.tileVisible(arg0, arg1, arg3)) {
			var7 = arg1 << 7;
			var8 = arg3 << 7;
			return this.occluded(var7 + 1, this.levelHeightmaps[arg0][arg1][arg3] - arg5, var8 + 1) && this.occluded(var7 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg3] - arg5, var8 + 1) && this.occluded(var7 + 128 - 1, this.levelHeightmaps[arg0][arg1 + 1][arg3 + 1] - arg5, var8 + 128 - 1) && this.occluded(var7 + 1, this.levelHeightmaps[arg0][arg1][arg3 + 1] - arg5, var8 + 128 - 1);
		} else {
			return false;
		}
	}

	@ObfuscatedName("r.h(III)Z")
	public boolean occluded(int arg0, int arg1, int arg2) {
		for (int var4 = 0; var4 < activeOccluderCount; var4++) {
			Occlude var5 = activeOccluders[var4];
			int var6;
			int var7;
			int var8;
			int var9;
			int var10;
			if (var5.mode == 1) {
				var6 = var5.minX - arg0;
				if (var6 > 0) {
					var7 = var5.minZ + (var5.minDeltaZ * var6 >> 8);
					var8 = var5.maxZ + (var5.maxDeltaZ * var6 >> 8);
					var9 = var5.minY + (var5.minDeltaY * var6 >> 8);
					var10 = var5.maxY + (var5.maxDeltaY * var6 >> 8);
					if (arg2 >= var7 && arg2 <= var8 && arg1 >= var9 && arg1 <= var10) {
						return true;
					}
				}
			} else if (var5.mode == 2) {
				var6 = arg0 - var5.minX;
				if (var6 > 0) {
					var7 = var5.minZ + (var5.minDeltaZ * var6 >> 8);
					var8 = var5.maxZ + (var5.maxDeltaZ * var6 >> 8);
					var9 = var5.minY + (var5.minDeltaY * var6 >> 8);
					var10 = var5.maxY + (var5.maxDeltaY * var6 >> 8);
					if (arg2 >= var7 && arg2 <= var8 && arg1 >= var9 && arg1 <= var10) {
						return true;
					}
				}
			} else if (var5.mode == 3) {
				var6 = var5.minZ - arg2;
				if (var6 > 0) {
					var7 = var5.minX + (var5.minDeltaX * var6 >> 8);
					var8 = var5.maxX + (var5.maxDeltaX * var6 >> 8);
					var9 = var5.minY + (var5.minDeltaY * var6 >> 8);
					var10 = var5.maxY + (var5.maxDeltaY * var6 >> 8);
					if (arg0 >= var7 && arg0 <= var8 && arg1 >= var9 && arg1 <= var10) {
						return true;
					}
				}
			} else if (var5.mode == 4) {
				var6 = arg2 - var5.minZ;
				if (var6 > 0) {
					var7 = var5.minX + (var5.minDeltaX * var6 >> 8);
					var8 = var5.maxX + (var5.maxDeltaX * var6 >> 8);
					var9 = var5.minY + (var5.minDeltaY * var6 >> 8);
					var10 = var5.maxY + (var5.maxDeltaY * var6 >> 8);
					if (arg0 >= var7 && arg0 <= var8 && arg1 >= var9 && arg1 <= var10) {
						return true;
					}
				}
			} else if (var5.mode == 5) {
				var6 = arg1 - var5.minY;
				if (var6 > 0) {
					var7 = var5.minX + (var5.minDeltaX * var6 >> 8);
					var8 = var5.maxX + (var5.maxDeltaX * var6 >> 8);
					var9 = var5.minZ + (var5.minDeltaZ * var6 >> 8);
					var10 = var5.maxZ + (var5.maxDeltaZ * var6 >> 8);
					if (arg0 >= var7 && arg0 <= var8 && arg2 >= var9 && arg2 <= var10) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
