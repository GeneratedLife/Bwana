package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.FloType;
import jagex2.config.LocType;
import jagex2.config.SeqType;
import jagex2.dash3d.entity.Entity;
import jagex2.dash3d.entity.LocEntity;
import jagex2.datastruct.LinkList;
import jagex2.graphics.Model;
import jagex2.graphics.Pix3D;
import jagex2.io.Packet;

@ObfuscatedName("c")
public class World {

	@ObfuscatedName("c.a")
	public static boolean field33 = true;

	@ObfuscatedName("c.b")
	public boolean field34 = true;

	@ObfuscatedName("c.c")
	public int field35 = 8;

	@ObfuscatedName("c.d")
	public static boolean lowMemory = true;

	@ObfuscatedName("c.e")
	public static int levelBuilt;

	@ObfuscatedName("c.f")
	public static boolean fullbright;

	@ObfuscatedName("c.g")
	public int maxTileX;

	@ObfuscatedName("c.h")
	public int maxTileZ;

	@ObfuscatedName("c.i")
	public int[][][] levelHeightmap;

	@ObfuscatedName("c.j")
	public byte[][][] levelTileFlags;

	@ObfuscatedName("c.k")
	public byte[][][] levelTileUnderlayIds;

	@ObfuscatedName("c.l")
	public byte[][][] levelTileOverlayIds;

	@ObfuscatedName("c.m")
	public byte[][][] levelTileOverlayShape;

	@ObfuscatedName("c.n")
	public byte[][][] levelTileOverlayRotation;

	@ObfuscatedName("c.o")
	public byte[][][] levelShademap;

	@ObfuscatedName("c.p")
	public int[][] levelLightmap;

	@ObfuscatedName("c.q")
	public int[] blendChroma;

	@ObfuscatedName("c.r")
	public int[] blendSaturation;

	@ObfuscatedName("c.s")
	public int[] blendLightness;

	@ObfuscatedName("c.t")
	public int[] blendLuminance;

	@ObfuscatedName("c.u")
	public int[] blendMagnitude;

	@ObfuscatedName("c.v")
	public int[][][] levelOccludemap;

	@ObfuscatedName("c.w")
	public static final int[] ROTATION_WALL_TYPE = new int[] { 1, 2, 4, 8 };

	@ObfuscatedName("c.x")
	public static final int[] ROTATION_WALL_CORNER_TYPE = new int[] { 16, 32, 64, 128 };

	@ObfuscatedName("c.y")
	public static final int[] WALL_DECORATION_ROTATION_FORWARD_X = new int[] { 1, 0, -1, 0 };

	@ObfuscatedName("c.z")
	public static final int[] WALL_DECORATION_ROTATION_FORWARD_Z = new int[] { 0, -1, 0, 1 };

	@ObfuscatedName("c.A")
	public static int randomHueOffset = (int) (Math.random() * 17.0D) - 8;

	@ObfuscatedName("c.B")
	public static int randomLightnessOffset = (int) (Math.random() * 33.0D) - 16;

	public World(int arg0, byte[][][] arg1, int arg2, int[][][] arg3, int arg4) {
		this.maxTileX = arg2;
		this.maxTileZ = arg0;
		this.levelHeightmap = arg3;
		this.levelTileFlags = arg1;
		this.levelTileUnderlayIds = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayIds = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayShape = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayRotation = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelOccludemap = new int[4][this.maxTileX + 1][this.maxTileZ + 1];
		this.levelShademap = new byte[4][this.maxTileX + 1][this.maxTileZ + 1];
		this.levelLightmap = new int[this.maxTileX + 1][this.maxTileZ + 1];
		this.blendChroma = new int[this.maxTileZ];
		this.blendSaturation = new int[this.maxTileZ];
		if (arg4 != -35388) {
			this.field35 = 28;
		}
		this.blendLightness = new int[this.maxTileZ];
		this.blendLuminance = new int[this.maxTileZ];
		this.blendMagnitude = new int[this.maxTileZ];
	}

	@ObfuscatedName("c.a(IIIII)V")
	public final void clearLandscape(int arg0, int arg1, int arg2, int arg3, int arg4) {
		byte var6 = 0;
		if (arg2 != 3) {
			this.field35 = 123;
		}
		for (int var7 = 0; var7 < FloType.count; var7++) {
			if (FloType.instances[var7].name.equalsIgnoreCase("water")) {
				var6 = (byte) (var7 + 1);
				break;
			}
		}
		for (int var8 = arg1; var8 < arg1 + arg4; var8++) {
			for (int var9 = arg0; var9 < arg0 + arg3; var9++) {
				if (var9 >= 0 && var9 < this.maxTileX && var8 >= 0 && var8 < this.maxTileZ) {
					this.levelTileOverlayIds[0][var9][var8] = var6;
					for (int var10 = 0; var10 < 4; var10++) {
						this.levelHeightmap[var10][var9][var8] = 0;
						this.levelTileFlags[var10][var9][var8] = 0;
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a([BIIIII)V")
	public final void loadGround(byte[] arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		Packet var7 = new Packet(363, arg0);
		if (arg2 != 1) {
			this.field34 = !this.field34;
		}
		for (int var8 = 0; var8 < 4; var8++) {
			for (int var9 = 0; var9 < 64; var9++) {
				for (int var10 = 0; var10 < 64; var10++) {
					int var11 = var9 + arg4;
					int var12 = var10 + arg3;
					int var13;
					if (var11 >= 0 && var11 < 104 && var12 >= 0 && var12 < 104) {
						this.levelTileFlags[var8][var11][var12] = 0;
						while (true) {
							var13 = var7.g1();
							if (var13 == 0) {
								if (var8 == 0) {
									this.levelHeightmap[0][var11][var12] = -perlinNoise(var11 + 932731 + arg1, var12 + 556238 + arg5) * 8;
								} else {
									this.levelHeightmap[var8][var11][var12] = this.levelHeightmap[var8 - 1][var11][var12] - 240;
								}
								break;
							}
							if (var13 == 1) {
								int var14 = var7.g1();
								if (var14 == 1) {
									var14 = 0;
								}
								if (var8 == 0) {
									this.levelHeightmap[0][var11][var12] = -var14 * 8;
								} else {
									this.levelHeightmap[var8][var11][var12] = this.levelHeightmap[var8 - 1][var11][var12] - var14 * 8;
								}
								break;
							}
							if (var13 <= 49) {
								this.levelTileOverlayIds[var8][var11][var12] = var7.g1b();
								this.levelTileOverlayShape[var8][var11][var12] = (byte) ((var13 - 2) / 4);
								this.levelTileOverlayRotation[var8][var11][var12] = (byte) (var13 - 2 & 0x3);
							} else if (var13 <= 81) {
								this.levelTileFlags[var8][var11][var12] = (byte) (var13 - 49);
							} else {
								this.levelTileUnderlayIds[var8][var11][var12] = (byte) (var13 - 81);
							}
						}
					} else {
						while (true) {
							var13 = var7.g1();
							if (var13 == 0) {
								break;
							}
							if (var13 == 1) {
								var7.g1();
								break;
							}
							if (var13 <= 49) {
								var7.g1();
							}
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a([BLr;[Lec;Lob;ZII)V")
	public final void loadLocations(byte[] arg0, World3D arg1, CollisionMap[] arg2, LinkList arg3, boolean arg4, int arg5, int arg6) {
		Packet var8 = new Packet(363, arg0);
		if (!arg4) {
			this.field34 = !this.field34;
		}
		int var9 = -1;
		while (true) {
			int var10 = var8.gsmarts();
			if (var10 == 0) {
				return;
			}
			var9 += var10;
			int var11 = 0;
			while (true) {
				int var12 = var8.gsmarts();
				if (var12 == 0) {
					break;
				}
				var11 += var12 - 1;
				int var13 = var11 & 0x3F;
				int var14 = var11 >> 6 & 0x3F;
				int var15 = var11 >> 12;
				int var16 = var8.g1();
				int var17 = var16 >> 2;
				int var18 = var16 & 0x3;
				int var19 = var14 + arg6;
				int var20 = var13 + arg5;
				if (var19 > 0 && var20 > 0 && var19 < 103 && var20 < 103) {
					int var21 = var15;
					if ((this.levelTileFlags[1][var19][var20] & 0x2) == 2) {
						var21 = var15 - 1;
					}
					CollisionMap var22 = null;
					if (var21 >= 0) {
						var22 = arg2[var21];
					}
					this.addLoc(var22, true, var15, var20, var18, var17, arg1, arg3, var9, var19);
				}
			}
		}
	}

	@ObfuscatedName("c.a(Lec;ZIIIILr;Lob;II)V")
	public final void addLoc(CollisionMap arg0, boolean arg1, int arg2, int arg3, int arg4, int arg5, World3D arg6, LinkList arg7, int arg8, int arg9) {
		if (!arg1) {
			return;
		}
		if (lowMemory) {
			if ((this.levelTileFlags[arg2][arg9][arg3] & 0x10) != 0) {
				return;
			}
			if (this.getDrawLevel(arg2, (byte) 10, arg9, arg3) != levelBuilt) {
				return;
			}
		}
		int var11 = this.levelHeightmap[arg2][arg9][arg3];
		int var12 = this.levelHeightmap[arg2][arg9 + 1][arg3];
		int var13 = this.levelHeightmap[arg2][arg9 + 1][arg3 + 1];
		int var14 = this.levelHeightmap[arg2][arg9][arg3 + 1];
		int var15 = var11 + var12 + var13 + var14 >> 2;
		LocType var16 = LocType.get(arg8);
		int var17 = arg9 + (arg3 << 7) + (arg8 << 14) + 1073741824;
		if (!var16.active) {
			var17 += Integer.MIN_VALUE;
		}
		byte var18 = (byte) ((arg4 << 6) + arg5);
		Model var19;
		if (arg5 != 22) {
			int var20;
			if (arg5 == 10 || arg5 == 11) {
				var19 = var16.getModel(10, arg4, var11, var12, var13, var14, -1);
				if (var19 != null) {
					int var22 = 0;
					if (arg5 == 11) {
						var22 += 256;
					}
					int var21;
					if (arg4 == 1 || arg4 == 3) {
						var20 = var16.length;
						var21 = var16.width;
					} else {
						var20 = var16.width;
						var21 = var16.length;
					}
					if (arg6.addLoc(var15, 775, arg2, null, var17, arg3, arg9, var20, var18, var19, var22, var21) && var16.shadow) {
						for (int var23 = 0; var23 <= var20; var23++) {
							for (int var24 = 0; var24 <= var21; var24++) {
								int var25 = var19.radius / 4;
								if (var25 > 30) {
									var25 = 30;
								}
								if (var25 > this.levelShademap[arg2][arg9 + var23][arg3 + var24]) {
									this.levelShademap[arg2][arg9 + var23][arg3 + var24] = (byte) var25;
								}
							}
						}
					}
				}
				if (var16.blockwalk && arg0 != null) {
					arg0.addLoc(arg4, var16.length, var16.width, arg9, 9, arg3, var16.blockrange);
				}
				if (var16.anim != -1) {
					arg7.addTail(new LocEntity(true, arg8, arg2, 0, 2, SeqType.instances[var16.anim], arg3, arg9));
				}
			} else if (arg5 >= 12) {
				var19 = var16.getModel(arg5, arg4, var11, var12, var13, var14, -1);
				arg6.addLoc(var15, 775, arg2, null, var17, arg3, arg9, 1, var18, var19, 0, 1);
				if (arg5 >= 12 && arg5 <= 17 && arg5 != 13 && arg2 > 0) {
					this.levelOccludemap[arg2][arg9][arg3] |= 0x924;
				}
				if (var16.blockwalk && arg0 != null) {
					arg0.addLoc(arg4, var16.length, var16.width, arg9, 9, arg3, var16.blockrange);
				}
				if (var16.anim != -1) {
					arg7.addTail(new LocEntity(true, arg8, arg2, 0, 2, SeqType.instances[var16.anim], arg3, arg9));
				}
			} else if (arg5 == 0) {
				var19 = var16.getModel(0, arg4, var11, var12, var13, var14, -1);
				arg6.addWall(0, var15, arg2, ROTATION_WALL_TYPE[arg4], 8, var19, null, arg9, var17, arg3, var18);
				if (arg4 == 0) {
					if (var16.shadow) {
						this.levelShademap[arg2][arg9][arg3] = 50;
						this.levelShademap[arg2][arg9][arg3 + 1] = 50;
					}
					if (var16.occlude) {
						this.levelOccludemap[arg2][arg9][arg3] |= 0x249;
					}
				} else if (arg4 == 1) {
					if (var16.shadow) {
						this.levelShademap[arg2][arg9][arg3 + 1] = 50;
						this.levelShademap[arg2][arg9 + 1][arg3 + 1] = 50;
					}
					if (var16.occlude) {
						this.levelOccludemap[arg2][arg9][arg3 + 1] |= 0x492;
					}
				} else if (arg4 == 2) {
					if (var16.shadow) {
						this.levelShademap[arg2][arg9 + 1][arg3] = 50;
						this.levelShademap[arg2][arg9 + 1][arg3 + 1] = 50;
					}
					if (var16.occlude) {
						this.levelOccludemap[arg2][arg9 + 1][arg3] |= 0x249;
					}
				} else if (arg4 == 3) {
					if (var16.shadow) {
						this.levelShademap[arg2][arg9][arg3] = 50;
						this.levelShademap[arg2][arg9 + 1][arg3] = 50;
					}
					if (var16.occlude) {
						this.levelOccludemap[arg2][arg9][arg3] |= 0x492;
					}
				}
				if (var16.blockwalk && arg0 != null) {
					arg0.addWall(true, arg4, arg3, arg9, var16.blockrange, arg5);
				}
				if (var16.anim != -1) {
					arg7.addTail(new LocEntity(true, arg8, arg2, 0, 0, SeqType.instances[var16.anim], arg3, arg9));
				}
				if (var16.wallwidth != 16) {
					arg6.setWallDecorationOffset(arg2, arg3, arg9, var16.wallwidth, (byte) 6);
				}
			} else if (arg5 == 1) {
				var19 = var16.getModel(1, arg4, var11, var12, var13, var14, -1);
				arg6.addWall(0, var15, arg2, ROTATION_WALL_CORNER_TYPE[arg4], 8, var19, null, arg9, var17, arg3, var18);
				if (var16.shadow) {
					if (arg4 == 0) {
						this.levelShademap[arg2][arg9][arg3 + 1] = 50;
					} else if (arg4 == 1) {
						this.levelShademap[arg2][arg9 + 1][arg3 + 1] = 50;
					} else if (arg4 == 2) {
						this.levelShademap[arg2][arg9 + 1][arg3] = 50;
					} else if (arg4 == 3) {
						this.levelShademap[arg2][arg9][arg3] = 50;
					}
				}
				if (var16.blockwalk && arg0 != null) {
					arg0.addWall(true, arg4, arg3, arg9, var16.blockrange, arg5);
				}
				if (var16.anim != -1) {
					arg7.addTail(new LocEntity(true, arg8, arg2, 0, 0, SeqType.instances[var16.anim], arg3, arg9));
				}
			} else {
				int var26;
				Model var28;
				if (arg5 == 2) {
					var26 = arg4 + 1 & 0x3;
					Model var27 = var16.getModel(2, arg4 + 4, var11, var12, var13, var14, -1);
					var28 = var16.getModel(2, var26, var11, var12, var13, var14, -1);
					arg6.addWall(ROTATION_WALL_TYPE[var26], var15, arg2, ROTATION_WALL_TYPE[arg4], 8, var27, var28, arg9, var17, arg3, var18);
					if (var16.occlude) {
						if (arg4 == 0) {
							this.levelOccludemap[arg2][arg9][arg3] |= 0x249;
							this.levelOccludemap[arg2][arg9][arg3 + 1] |= 0x492;
						} else if (arg4 == 1) {
							this.levelOccludemap[arg2][arg9][arg3 + 1] |= 0x492;
							this.levelOccludemap[arg2][arg9 + 1][arg3] |= 0x249;
						} else if (arg4 == 2) {
							this.levelOccludemap[arg2][arg9 + 1][arg3] |= 0x249;
							this.levelOccludemap[arg2][arg9][arg3] |= 0x492;
						} else if (arg4 == 3) {
							this.levelOccludemap[arg2][arg9][arg3] |= 0x492;
							this.levelOccludemap[arg2][arg9][arg3] |= 0x249;
						}
					}
					if (var16.blockwalk && arg0 != null) {
						arg0.addWall(true, arg4, arg3, arg9, var16.blockrange, arg5);
					}
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 0, SeqType.instances[var16.anim], arg3, arg9));
					}
					if (var16.wallwidth != 16) {
						arg6.setWallDecorationOffset(arg2, arg3, arg9, var16.wallwidth, (byte) 6);
					}
				} else if (arg5 == 3) {
					var19 = var16.getModel(3, arg4, var11, var12, var13, var14, -1);
					arg6.addWall(0, var15, arg2, ROTATION_WALL_CORNER_TYPE[arg4], 8, var19, null, arg9, var17, arg3, var18);
					if (var16.shadow) {
						if (arg4 == 0) {
							this.levelShademap[arg2][arg9][arg3 + 1] = 50;
						} else if (arg4 == 1) {
							this.levelShademap[arg2][arg9 + 1][arg3 + 1] = 50;
						} else if (arg4 == 2) {
							this.levelShademap[arg2][arg9 + 1][arg3] = 50;
						} else if (arg4 == 3) {
							this.levelShademap[arg2][arg9][arg3] = 50;
						}
					}
					if (var16.blockwalk && arg0 != null) {
						arg0.addWall(true, arg4, arg3, arg9, var16.blockrange, arg5);
					}
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 0, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 9) {
					var19 = var16.getModel(arg5, arg4, var11, var12, var13, var14, -1);
					arg6.addLoc(var15, 775, arg2, null, var17, arg3, arg9, 1, var18, var19, 0, 1);
					if (var16.blockwalk && arg0 != null) {
						arg0.addLoc(arg4, var16.length, var16.width, arg9, 9, arg3, var16.blockrange);
					}
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 2, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 4) {
					var19 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
					arg6.setWallDecoration(var15, arg3, 0, var17, arg4 * 512, ROTATION_WALL_TYPE[arg4], 465, 0, arg9, var19, var18, arg2);
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 1, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 5) {
					var26 = 16;
					var20 = arg6.getWallBitset(arg2, arg9, arg3);
					if (var20 > 0) {
						var26 = LocType.get(var20 >> 14 & 0x7FFF).wallwidth;
					}
					var28 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
					arg6.setWallDecoration(var15, arg3, WALL_DECORATION_ROTATION_FORWARD_Z[arg4] * var26, var17, arg4 * 512, ROTATION_WALL_TYPE[arg4], 465, WALL_DECORATION_ROTATION_FORWARD_X[arg4] * var26, arg9, var28, var18, arg2);
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 1, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 6) {
					var19 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
					arg6.setWallDecoration(var15, arg3, 0, var17, arg4, 256, 465, 0, arg9, var19, var18, arg2);
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 1, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 7) {
					var19 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
					arg6.setWallDecoration(var15, arg3, 0, var17, arg4, 512, 465, 0, arg9, var19, var18, arg2);
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 1, SeqType.instances[var16.anim], arg3, arg9));
					}
				} else if (arg5 == 8) {
					var19 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
					arg6.setWallDecoration(var15, arg3, 0, var17, arg4, 768, 465, 0, arg9, var19, var18, arg2);
					if (var16.anim != -1) {
						arg7.addTail(new LocEntity(true, arg8, arg2, 0, 1, SeqType.instances[var16.anim], arg3, arg9));
					}
				}
			}
		} else if (!lowMemory || var16.active || var16.forcedecor) {
			var19 = var16.getModel(22, arg4, var11, var12, var13, var14, -1);
			arg6.addGroundDecoration(var19, (byte) 6, arg9, var17, arg3, arg2, var18, var15);
			if (var16.blockwalk && var16.active && arg0 != null) {
				arg0.setBlocked((byte) 12, arg3, arg9);
			}
			if (var16.anim != -1) {
				arg7.addTail(new LocEntity(true, arg8, arg2, 0, 3, SeqType.instances[var16.anim], arg3, arg9));
			}
		}
	}

	@ObfuscatedName("c.a(Lr;I[Lec;)V")
	public final void build(World3D arg0, int arg1, CollisionMap[] arg2) {
		int var5;
		int var6;
		int var7;
		for (int var4 = 0; var4 < 4; var4++) {
			for (var5 = 0; var5 < 104; var5++) {
				for (var6 = 0; var6 < 104; var6++) {
					if ((this.levelTileFlags[var4][var5][var6] & 0x1) == 1) {
						var7 = var4;
						if ((this.levelTileFlags[1][var5][var6] & 0x2) == 2) {
							var7 = var4 - 1;
						}
						if (var7 >= 0) {
							arg2[var7].setBlocked((byte) 12, var6, var5);
						}
					}
				}
			}
		}
		randomHueOffset += (int) (Math.random() * 5.0D) - 2;
		if (randomHueOffset < -8) {
			randomHueOffset = -8;
		}
		if (randomHueOffset > 8) {
			randomHueOffset = 8;
		}
		randomLightnessOffset += (int) (Math.random() * 5.0D) - 2;
		if (randomLightnessOffset < -16) {
			randomLightnessOffset = -16;
		}
		if (randomLightnessOffset > 16) {
			randomLightnessOffset = 16;
		}
		int var12;
		int var13;
		int var14;
		int var15;
		int var16;
		int var17;
		int var18;
		int var19;
		int var20;
		int var21;
		int var22;
		int var23;
		for (var5 = 0; var5 < 4; var5++) {
			byte[][] var45 = this.levelShademap[var5];
			byte var46 = 96;
			short var8 = 768;
			byte var9 = -50;
			byte var10 = -10;
			byte var11 = -50;
			var12 = (int) Math.sqrt((double) (var9 * var9 + var10 * var10 + var11 * var11));
			var13 = var8 * var12 >> 8;
			for (var14 = 1; var14 < this.maxTileZ - 1; var14++) {
				for (var15 = 1; var15 < this.maxTileX - 1; var15++) {
					var16 = this.levelHeightmap[var5][var15 + 1][var14] - this.levelHeightmap[var5][var15 - 1][var14];
					var17 = this.levelHeightmap[var5][var15][var14 + 1] - this.levelHeightmap[var5][var15][var14 - 1];
					var18 = (int) Math.sqrt((double) (var16 * var16 + 65536 + var17 * var17));
					var19 = (var16 << 8) / var18;
					var20 = 65536 / var18;
					var21 = (var17 << 8) / var18;
					var22 = var46 + (var9 * var19 + var10 * var20 + var11 * var21) / var13;
					var23 = (var45[var15 - 1][var14] >> 2) + (var45[var15 + 1][var14] >> 3) + (var45[var15][var14 - 1] >> 2) + (var45[var15][var14 + 1] >> 3) + (var45[var15][var14] >> 1);
					this.levelLightmap[var15][var14] = var22 - var23;
				}
			}
			for (var15 = 0; var15 < this.maxTileZ; var15++) {
				this.blendChroma[var15] = 0;
				this.blendSaturation[var15] = 0;
				this.blendLightness[var15] = 0;
				this.blendLuminance[var15] = 0;
				this.blendMagnitude[var15] = 0;
			}
			for (var16 = -5; var16 < this.maxTileX + 5; var16++) {
				for (var17 = 0; var17 < this.maxTileZ; var17++) {
					var18 = var16 + 5;
					int var10002;
					if (var18 >= 0 && var18 < this.maxTileX) {
						var19 = this.levelTileUnderlayIds[var5][var18][var17] & 0xFF;
						if (var19 > 0) {
							FloType var51 = FloType.instances[var19 - 1];
							this.blendChroma[var17] += var51.chroma;
							this.blendSaturation[var17] += var51.saturation;
							this.blendLightness[var17] += var51.lightness;
							this.blendLuminance[var17] += var51.luminance;
							var10002 = this.blendMagnitude[var17]++;
						}
					}
					var19 = var16 - 5;
					if (var19 >= 0 && var19 < this.maxTileX) {
						var20 = this.levelTileUnderlayIds[var5][var19][var17] & 0xFF;
						if (var20 > 0) {
							FloType var52 = FloType.instances[var20 - 1];
							this.blendChroma[var17] -= var52.chroma;
							this.blendSaturation[var17] -= var52.saturation;
							this.blendLightness[var17] -= var52.lightness;
							this.blendLuminance[var17] -= var52.luminance;
							var10002 = this.blendMagnitude[var17]--;
						}
					}
				}
				if (var16 >= 1 && var16 < this.maxTileX - 1) {
					var18 = 0;
					var19 = 0;
					var20 = 0;
					var21 = 0;
					var22 = 0;
					for (var23 = -5; var23 < this.maxTileZ + 5; var23++) {
						int var24 = var23 + 5;
						if (var24 >= 0 && var24 < this.maxTileZ) {
							var18 += this.blendChroma[var24];
							var19 += this.blendSaturation[var24];
							var20 += this.blendLightness[var24];
							var21 += this.blendLuminance[var24];
							var22 += this.blendMagnitude[var24];
						}
						int var25 = var23 - 5;
						if (var25 >= 0 && var25 < this.maxTileZ) {
							var18 -= this.blendChroma[var25];
							var19 -= this.blendSaturation[var25];
							var20 -= this.blendLightness[var25];
							var21 -= this.blendLuminance[var25];
							var22 -= this.blendMagnitude[var25];
						}
						if (var23 >= 1 && var23 < this.maxTileZ - 1 && (!lowMemory || (this.levelTileFlags[var5][var16][var23] & 0x10) == 0 && this.getDrawLevel(var5, (byte) 10, var16, var23) == levelBuilt)) {
							int var26 = this.levelTileUnderlayIds[var5][var16][var23] & 0xFF;
							int var27 = this.levelTileOverlayIds[var5][var16][var23] & 0xFF;
							if (var26 > 0 || var27 > 0) {
								int var28 = this.levelHeightmap[var5][var16][var23];
								int var29 = this.levelHeightmap[var5][var16 + 1][var23];
								int var30 = this.levelHeightmap[var5][var16 + 1][var23 + 1];
								int var31 = this.levelHeightmap[var5][var16][var23 + 1];
								int var32 = this.levelLightmap[var16][var23];
								int var33 = this.levelLightmap[var16 + 1][var23];
								int var34 = this.levelLightmap[var16 + 1][var23 + 1];
								int var35 = this.levelLightmap[var16][var23 + 1];
								int var36 = -1;
								int var37 = -1;
								int var38;
								int var39;
								if (var26 > 0) {
									var38 = var18 * 256 / var21;
									var39 = var19 / var22;
									int var40 = var20 / var22;
									var36 = this.hsl24to16(var38, var39, var40);
									int var54 = var38 + randomHueOffset & 0xFF;
									var40 += randomLightnessOffset;
									if (var40 < 0) {
										var40 = 0;
									} else if (var40 > 255) {
										var40 = 255;
									}
									var37 = this.hsl24to16(var54, var39, var40);
								}
								if (var5 > 0) {
									boolean var55 = true;
									if (var26 == 0 && this.levelTileOverlayShape[var5][var16][var23] != 0) {
										var55 = false;
									}
									if (var27 > 0 && !FloType.instances[var27 - 1].occlude) {
										var55 = false;
									}
									if (var55 && var28 == var29 && var28 == var30 && var28 == var31) {
										this.levelOccludemap[var5][var16][var23] |= 0x924;
									}
								}
								var38 = 0;
								if (var36 != -1) {
									var38 = Pix3D.colourTable[mulHSL(var37, 96)];
								}
								if (var27 == 0) {
									arg0.setTile(var5, var16, var23, 0, 0, -1, var28, var29, var30, var31, mulHSL(var36, var32), mulHSL(var36, var33), mulHSL(var36, var34), mulHSL(var36, var35), 0, 0, 0, 0, var38, 0);
								} else {
									var39 = this.levelTileOverlayShape[var5][var16][var23] + 1;
									byte var56 = this.levelTileOverlayRotation[var5][var16][var23];
									FloType var41 = FloType.instances[var27 - 1];
									int var42 = var41.texture;
									int var43;
									int var44;
									if (var42 >= 0) {
										var44 = Pix3D.getAverageTextureRGB(787, var42);
										var43 = -1;
									} else if (var41.rgb == 16711935) {
										var44 = 0;
										var43 = -2;
										var42 = -1;
									} else {
										var43 = this.hsl24to16(var41.hue, var41.saturation, var41.lightness);
										var44 = Pix3D.colourTable[this.adjustLightness(var41.hsl, 96)];
									}
									arg0.setTile(var5, var16, var23, var39, var56, var42, var28, var29, var30, var31, mulHSL(var36, var32), mulHSL(var36, var33), mulHSL(var36, var34), mulHSL(var36, var35), this.adjustLightness(var43, var32), this.adjustLightness(var43, var33), this.adjustLightness(var43, var34), this.adjustLightness(var43, var35), var38, var44);
								}
							}
						}
					}
				}
			}
			for (var17 = 1; var17 < this.maxTileZ - 1; var17++) {
				for (var18 = 1; var18 < this.maxTileX - 1; var18++) {
					arg0.setDrawLevel(var5, var18, var17, this.getDrawLevel(var5, (byte) 10, var18, var17));
				}
			}
		}
		while (arg1 >= 0) {
			for (var6 = 1; var6 > 0; var6++) {
			}
		}
		if (!fullbright) {
			arg0.buildModels(-10, 64, -50, 768, -50, false);
		}
		for (var6 = 0; var6 < this.maxTileX; var6++) {
			for (var7 = 0; var7 < this.maxTileZ; var7++) {
				if ((this.levelTileFlags[1][var6][var7] & 0x2) == 2) {
					arg0.setBridge(var7, var6, (byte) -41);
				}
			}
		}
		if (fullbright) {
			return;
		}
		var7 = 1;
		int var47 = 2;
		int var48 = 4;
		for (int var49 = 0; var49 < 4; var49++) {
			if (var49 > 0) {
				var7 <<= 0x3;
				var47 <<= 0x3;
				var48 <<= 0x3;
			}
			for (int var50 = 0; var50 <= var49; var50++) {
				for (var12 = 0; var12 <= this.maxTileZ; var12++) {
					for (var13 = 0; var13 <= this.maxTileX; var13++) {
						short var53;
						if ((this.levelOccludemap[var50][var13][var12] & var7) != 0) {
							var14 = var12;
							var15 = var12;
							var16 = var50;
							var17 = var50;
							while (var14 > 0 && (this.levelOccludemap[var50][var13][var14 - 1] & var7) != 0) {
								var14--;
							}
							while (var15 < this.maxTileZ && (this.levelOccludemap[var50][var13][var15 + 1] & var7) != 0) {
								var15++;
							}
							label334: while (var16 > 0) {
								for (var18 = var14; var18 <= var15; var18++) {
									if ((this.levelOccludemap[var16 - 1][var13][var18] & var7) == 0) {
										break label334;
									}
								}
								var16--;
							}
							label323: while (var17 < var49) {
								for (var18 = var14; var18 <= var15; var18++) {
									if ((this.levelOccludemap[var17 + 1][var13][var18] & var7) == 0) {
										break label323;
									}
								}
								var17++;
							}
							var18 = (var17 + 1 - var16) * (var15 - var14 + 1);
							if (var18 >= 8) {
								var53 = 240;
								var20 = this.levelHeightmap[var17][var13][var14] - var53;
								var21 = this.levelHeightmap[var16][var13][var14];
								World3D.addOccluder(var15 * 128 + 128, var13 * 128, -802, var21, 1, var13 * 128, var49, var20, var14 * 128);
								for (var22 = var16; var22 <= var17; var22++) {
									for (var23 = var14; var23 <= var15; var23++) {
										this.levelOccludemap[var22][var13][var23] &= ~var7;
									}
								}
							}
						}
						if ((this.levelOccludemap[var50][var13][var12] & var47) != 0) {
							var14 = var13;
							var15 = var13;
							var16 = var50;
							var17 = var50;
							while (var14 > 0 && (this.levelOccludemap[var50][var14 - 1][var12] & var47) != 0) {
								var14--;
							}
							while (var15 < this.maxTileX && (this.levelOccludemap[var50][var15 + 1][var12] & var47) != 0) {
								var15++;
							}
							label387: while (var16 > 0) {
								for (var18 = var14; var18 <= var15; var18++) {
									if ((this.levelOccludemap[var16 - 1][var18][var12] & var47) == 0) {
										break label387;
									}
								}
								var16--;
							}
							label376: while (var17 < var49) {
								for (var18 = var14; var18 <= var15; var18++) {
									if ((this.levelOccludemap[var17 + 1][var18][var12] & var47) == 0) {
										break label376;
									}
								}
								var17++;
							}
							var18 = (var17 + 1 - var16) * (var15 - var14 + 1);
							if (var18 >= 8) {
								var53 = 240;
								var20 = this.levelHeightmap[var17][var14][var12] - var53;
								var21 = this.levelHeightmap[var16][var14][var12];
								World3D.addOccluder(var12 * 128, var14 * 128, -802, var21, 2, var15 * 128 + 128, var49, var20, var12 * 128);
								for (var22 = var16; var22 <= var17; var22++) {
									for (var23 = var14; var23 <= var15; var23++) {
										this.levelOccludemap[var22][var23][var12] &= ~var47;
									}
								}
							}
						}
						if ((this.levelOccludemap[var50][var13][var12] & var48) != 0) {
							var14 = var13;
							var15 = var13;
							var16 = var12;
							var17 = var12;
							while (var16 > 0 && (this.levelOccludemap[var50][var13][var16 - 1] & var48) != 0) {
								var16--;
							}
							while (var17 < this.maxTileZ && (this.levelOccludemap[var50][var13][var17 + 1] & var48) != 0) {
								var17++;
							}
							label440: while (var14 > 0) {
								for (var18 = var16; var18 <= var17; var18++) {
									if ((this.levelOccludemap[var50][var14 - 1][var18] & var48) == 0) {
										break label440;
									}
								}
								var14--;
							}
							label429: while (var15 < this.maxTileX) {
								for (var18 = var16; var18 <= var17; var18++) {
									if ((this.levelOccludemap[var50][var15 + 1][var18] & var48) == 0) {
										break label429;
									}
								}
								var15++;
							}
							if ((var15 - var14 + 1) * (var17 - var16 + 1) >= 4) {
								var18 = this.levelHeightmap[var50][var14][var16];
								World3D.addOccluder(var17 * 128 + 128, var14 * 128, -802, var18, 4, var15 * 128 + 128, var49, var18, var16 * 128);
								for (var19 = var14; var19 <= var15; var19++) {
									for (var20 = var16; var20 <= var17; var20++) {
										this.levelOccludemap[var50][var19][var20] &= ~var48;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a(IBII)I")
	public int getDrawLevel(int arg0, byte arg1, int arg2, int arg3) {
		if (arg1 != 10) {
			this.field35 = 29;
		}
		if ((this.levelTileFlags[arg0][arg2][arg3] & 0x8) == 0) {
			return arg0 <= 0 || (this.levelTileFlags[1][arg2][arg3] & 0x2) == 0 ? arg0 : arg0 - 1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("c.a(II)I")
	public static final int perlinNoise(int arg0, int arg1) {
		int var2 = interpolatedNoise(arg0 + 45365, arg1 + 91923, 4) - 128 + (interpolatedNoise(arg0 + 10294, arg1 + 37821, 2) - 128 >> 1) + (interpolatedNoise(arg0, arg1, 1) - 128 >> 2);
		var2 = (int) ((double) var2 * 0.3D) + 35;
		if (var2 < 10) {
			var2 = 10;
		} else if (var2 > 60) {
			var2 = 60;
		}
		return var2;
	}

	@ObfuscatedName("c.a(III)I")
	public static final int interpolatedNoise(int arg0, int arg1, int arg2) {
		int var3 = arg0 / arg2;
		int var4 = arg0 & arg2 - 1;
		int var5 = arg1 / arg2;
		int var6 = arg1 & arg2 - 1;
		int var7 = smoothNoise(var3, var5);
		int var8 = smoothNoise(var3 + 1, var5);
		int var9 = smoothNoise(var3, var5 + 1);
		int var10 = smoothNoise(var3 + 1, var5 + 1);
		int var11 = interpolate(var7, var8, var4, arg2);
		int var12 = interpolate(var9, var10, var4, arg2);
		return interpolate(var11, var12, var6, arg2);
	}

	@ObfuscatedName("c.a(IIII)I")
	public static final int interpolate(int arg0, int arg1, int arg2, int arg3) {
		int var4 = 65536 - Pix3D.cosTable[arg2 * 1024 / arg3] >> 1;
		return (arg0 * (65536 - var4) >> 16) + (arg1 * var4 >> 16);
	}

	@ObfuscatedName("c.b(II)I")
	public static final int smoothNoise(int arg0, int arg1) {
		int var2 = noise(arg0 - 1, arg1 - 1) + noise(arg0 + 1, arg1 - 1) + noise(arg0 - 1, arg1 + 1) + noise(arg0 + 1, arg1 + 1);
		int var3 = noise(arg0 - 1, arg1) + noise(arg0 + 1, arg1) + noise(arg0, arg1 - 1) + noise(arg0, arg1 + 1);
		int var4 = noise(arg0, arg1);
		return var2 / 16 + var3 / 8 + var4 / 4;
	}

	@ObfuscatedName("c.c(II)I")
	public static final int noise(int arg0, int arg1) {
		int var2 = arg0 + arg1 * 57;
		int var4 = var2 << 13 ^ var2;
		int var3 = var4 * (var4 * var4 * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE;
		return var3 >> 19 & 0xFF;
	}

	@ObfuscatedName("c.d(II)I")
	public static final int mulHSL(int arg0, int arg1) {
		if (arg0 == -1) {
			return 12345678;
		}
		arg1 = arg1 * (arg0 & 0x7F) / 128;
		if (arg1 < 2) {
			arg1 = 2;
		} else if (arg1 > 126) {
			arg1 = 126;
		}
		return (arg0 & 0xFF80) + arg1;
	}

	@ObfuscatedName("c.e(II)I")
	public final int adjustLightness(int arg0, int arg1) {
		if (arg0 == -2) {
			return 12345678;
		} else if (arg0 == -1) {
			if (arg1 < 0) {
				arg1 = 0;
			} else if (arg1 > 127) {
				arg1 = 127;
			}
			return 127 - arg1;
		} else {
			arg1 = arg1 * (arg0 & 0x7F) / 128;
			if (arg1 < 2) {
				arg1 = 2;
			} else if (arg1 > 126) {
				arg1 = 126;
			}
			return (arg0 & 0xFF80) + arg1;
		}
	}

	@ObfuscatedName("c.b(III)I")
	public final int hsl24to16(int arg0, int arg1, int arg2) {
		if (arg2 > 179) {
			arg1 /= 2;
		}
		if (arg2 > 192) {
			arg1 /= 2;
		}
		if (arg2 > 217) {
			arg1 /= 2;
		}
		if (arg2 > 243) {
			arg1 /= 2;
		}
		return (arg0 / 4 << 10) + (arg1 / 32 << 7) + arg2 / 2;
	}

	@ObfuscatedName("c.a(ILob;Lec;II[[[IIIIILr;I)V")
	public static final void addLoc(int arg0, LinkList arg1, CollisionMap arg2, int arg3, int arg4, int[][][] arg5, int arg6, int arg7, int arg8, int arg9, World3D arg10, int arg11) {
		if (arg6 != 0) {
			field33 = !field33;
		}
		int var12 = arg5[arg11][arg0][arg3];
		int var13 = arg5[arg11][arg0 + 1][arg3];
		int var14 = arg5[arg11][arg0 + 1][arg3 + 1];
		int var15 = arg5[arg11][arg0][arg3 + 1];
		int var16 = var12 + var13 + var14 + var15 >> 2;
		LocType var17 = LocType.get(arg8);
		int var18 = arg0 + (arg3 << 7) + (arg8 << 14) + 1073741824;
		if (!var17.active) {
			var18 += Integer.MIN_VALUE;
		}
		byte var19 = (byte) ((arg4 << 6) + arg9);
		Model var20;
		if (arg9 == 22) {
			var20 = var17.getModel(22, arg4, var12, var13, var14, var15, -1);
			arg10.addGroundDecoration(var20, (byte) 6, arg0, var18, arg3, arg7, var19, var16);
			if (var17.blockwalk && var17.active) {
				arg2.setBlocked((byte) 12, arg3, arg0);
			}
			if (var17.anim != -1) {
				arg1.addTail(new LocEntity(true, arg8, arg7, 0, 3, SeqType.instances[var17.anim], arg3, arg0));
			}
			return;
		}
		int var21;
		if (arg9 == 10 || arg9 == 11) {
			var20 = var17.getModel(10, arg4, var12, var13, var14, var15, -1);
			if (var20 != null) {
				int var23 = 0;
				if (arg9 == 11) {
					var23 += 256;
				}
				int var22;
				if (arg4 == 1 || arg4 == 3) {
					var21 = var17.length;
					var22 = var17.width;
				} else {
					var21 = var17.width;
					var22 = var17.length;
				}
				arg10.addLoc(var16, 775, arg7, null, var18, arg3, arg0, var21, var19, var20, var23, var22);
			}
			if (var17.blockwalk) {
				arg2.addLoc(arg4, var17.length, var17.width, arg0, 9, arg3, var17.blockrange);
			}
			if (var17.anim != -1) {
				arg1.addTail(new LocEntity(true, arg8, arg7, 0, 2, SeqType.instances[var17.anim], arg3, arg0));
			}
		} else if (arg9 >= 12) {
			var20 = var17.getModel(arg9, arg4, var12, var13, var14, var15, -1);
			arg10.addLoc(var16, 775, arg7, null, var18, arg3, arg0, 1, var19, var20, 0, 1);
			if (var17.blockwalk) {
				arg2.addLoc(arg4, var17.length, var17.width, arg0, 9, arg3, var17.blockrange);
			}
			if (var17.anim != -1) {
				arg1.addTail(new LocEntity(true, arg8, arg7, 0, 2, SeqType.instances[var17.anim], arg3, arg0));
			}
		} else if (arg9 == 0) {
			var20 = var17.getModel(0, arg4, var12, var13, var14, var15, -1);
			arg10.addWall(0, var16, arg7, ROTATION_WALL_TYPE[arg4], 8, var20, null, arg0, var18, arg3, var19);
			if (var17.blockwalk) {
				arg2.addWall(true, arg4, arg3, arg0, var17.blockrange, arg9);
			}
			if (var17.anim != -1) {
				arg1.addTail(new LocEntity(true, arg8, arg7, 0, 0, SeqType.instances[var17.anim], arg3, arg0));
			}
		} else if (arg9 == 1) {
			var20 = var17.getModel(1, arg4, var12, var13, var14, var15, -1);
			arg10.addWall(0, var16, arg7, ROTATION_WALL_CORNER_TYPE[arg4], 8, var20, null, arg0, var18, arg3, var19);
			if (var17.blockwalk) {
				arg2.addWall(true, arg4, arg3, arg0, var17.blockrange, arg9);
			}
			if (var17.anim != -1) {
				arg1.addTail(new LocEntity(true, arg8, arg7, 0, 0, SeqType.instances[var17.anim], arg3, arg0));
			}
		} else {
			int var24;
			Model var26;
			if (arg9 == 2) {
				var24 = arg4 + 1 & 0x3;
				Model var25 = var17.getModel(2, arg4 + 4, var12, var13, var14, var15, -1);
				var26 = var17.getModel(2, var24, var12, var13, var14, var15, -1);
				arg10.addWall(ROTATION_WALL_TYPE[var24], var16, arg7, ROTATION_WALL_TYPE[arg4], 8, var25, var26, arg0, var18, arg3, var19);
				if (var17.blockwalk) {
					arg2.addWall(true, arg4, arg3, arg0, var17.blockrange, arg9);
				}
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 0, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 3) {
				var20 = var17.getModel(3, arg4, var12, var13, var14, var15, -1);
				arg10.addWall(0, var16, arg7, ROTATION_WALL_CORNER_TYPE[arg4], 8, var20, null, arg0, var18, arg3, var19);
				if (var17.blockwalk) {
					arg2.addWall(true, arg4, arg3, arg0, var17.blockrange, arg9);
				}
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 0, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 9) {
				var20 = var17.getModel(arg9, arg4, var12, var13, var14, var15, -1);
				arg10.addLoc(var16, 775, arg7, null, var18, arg3, arg0, 1, var19, var20, 0, 1);
				if (var17.blockwalk) {
					arg2.addLoc(arg4, var17.length, var17.width, arg0, 9, arg3, var17.blockrange);
				}
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 2, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 4) {
				var20 = var17.getModel(4, 0, var12, var13, var14, var15, -1);
				arg10.setWallDecoration(var16, arg3, 0, var18, arg4 * 512, ROTATION_WALL_TYPE[arg4], 465, 0, arg0, var20, var19, arg7);
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 1, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 5) {
				var24 = 16;
				var21 = arg10.getWallBitset(arg7, arg0, arg3);
				if (var21 > 0) {
					var24 = LocType.get(var21 >> 14 & 0x7FFF).wallwidth;
				}
				var26 = var17.getModel(4, 0, var12, var13, var14, var15, -1);
				arg10.setWallDecoration(var16, arg3, WALL_DECORATION_ROTATION_FORWARD_Z[arg4] * var24, var18, arg4 * 512, ROTATION_WALL_TYPE[arg4], 465, WALL_DECORATION_ROTATION_FORWARD_X[arg4] * var24, arg0, var26, var19, arg7);
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 1, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 6) {
				var20 = var17.getModel(4, 0, var12, var13, var14, var15, -1);
				arg10.setWallDecoration(var16, arg3, 0, var18, arg4, 256, 465, 0, arg0, var20, var19, arg7);
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 1, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 7) {
				var20 = var17.getModel(4, 0, var12, var13, var14, var15, -1);
				arg10.setWallDecoration(var16, arg3, 0, var18, arg4, 512, 465, 0, arg0, var20, var19, arg7);
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 1, SeqType.instances[var17.anim], arg3, arg0));
				}
			} else if (arg9 == 8) {
				var20 = var17.getModel(4, 0, var12, var13, var14, var15, -1);
				arg10.setWallDecoration(var16, arg3, 0, var18, arg4, 768, 465, 0, arg0, var20, var19, arg7);
				if (var17.anim != -1) {
					arg1.addTail(new LocEntity(true, arg8, arg7, 0, 1, SeqType.instances[var17.anim], arg3, arg0));
				}
			}
		}
	}
}
