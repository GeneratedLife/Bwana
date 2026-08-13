package jagex2.client;

import deob.ObfuscatedName;
import jagex2.config.FloType;
import jagex2.config.LocType;
import jagex2.dash3d.*;
import jagex2.graphics.Pix3D;
import jagex2.io.OnDemand;
import jagex2.io.Packet;

@ObfuscatedName("c")
public final class ClientBuild {

	@ObfuscatedName("c.e")
	public static boolean lowMem = true;

	@ObfuscatedName("c.f")
	public static int minusedlevel;

	@ObfuscatedName("c.g")
	public final int maxTileX;

	@ObfuscatedName("c.h")
	public final int maxTileZ;

	@ObfuscatedName("c.i")
	public final int[][][] groundh;

	@ObfuscatedName("c.j")
	public final byte[][][] mapl;

	@ObfuscatedName("c.k")
	public final byte[][][] floort1;

	@ObfuscatedName("c.l")
	public final byte[][][] floort2;

	@ObfuscatedName("c.m")
	public final byte[][][] floors;

	@ObfuscatedName("c.n")
	public final byte[][][] floorr;

	@ObfuscatedName("c.o")
	public final byte[][][] shadow;

	@ObfuscatedName("c.p")
	public final int[][] lightmap;

	@ObfuscatedName("c.q")
	public final int[] huetot;

	@ObfuscatedName("c.r")
	public final int[] sattot;

	@ObfuscatedName("c.s")
	public final int[] ligtot;

	@ObfuscatedName("c.t")
	public final int[] comtot;

	@ObfuscatedName("c.u")
	public final int[] tot;

	@ObfuscatedName("c.v")
	public final int[][][] mapo;

	@ObfuscatedName("c.w")
	public static final int[] WSHAPE0 = new int[] { 1, 2, 4, 8 };

	@ObfuscatedName("c.x")
	public static final int[] WSHAPE1 = new int[] { 16, 32, 64, 128 };

	@ObfuscatedName("c.y")
	public static final int[] DECORXOF = new int[] { 1, 0, -1, 0 };

	@ObfuscatedName("c.z")
	public static final int[] DECORZOF = new int[] { 0, -1, 0, 1 };

	@ObfuscatedName("c.A")
	public static int hueOff = (int) (Math.random() * 17.0D) - 8;

	@ObfuscatedName("c.B")
	public static int ligOff = (int) (Math.random() * 33.0D) - 16;

	@ObfuscatedName("c.a(B[BII)Z")
	public static boolean checkLocations(byte[] arg1, int arg2, int arg3) {
		boolean var4 = true;
		Packet var5 = new Packet(arg1);
		int var6 = -1;
		label52: while (true) {
			int var7 = var5.gsmart();
			if (var7 == 0) {
				return var4;
			}
			var6 += var7;
			int var8 = 0;
			boolean var9 = false;
			while (true) {
				while (!var9) {
					int var11 = var5.gsmart();
					if (var11 == 0) {
						continue label52;
					}
					var8 += var11 - 1;
					int var12 = var8 & 0x3F;
					int var13 = var8 >> 6 & 0x3F;
					int var14 = var5.g1() >> 2;
					int var15 = var13 + arg3;
					int var16 = var12 + arg2;
					if (var15 > 0 && var16 > 0 && var15 < 103 && var16 < 103) {
						LocType var17 = LocType.list(var6);
						if (var14 != 22 || !lowMem || var17.active || var17.forcedecor) {
							var4 &= var17.checkModelAll();
							var9 = true;
						}
					}
				}
				int var10 = var5.gsmart();
				if (var10 == 0) {
					break;
				}
				var5.g1();
			}
		}
	}

	@ObfuscatedName("c.a(BLlb;Lub;)V")
	public static void prefetchLocations(Packet arg1, OnDemand arg2) {
		int var4 = -1;
		while (true) {
			int var5 = arg1.gsmart();
			if (var5 == 0) {
				return;
			}
			var4 += var5;
			LocType var6 = LocType.list(var4);
			var6.prefetchModelAll(arg2);
			while (true) {
				int var7 = arg1.gsmart();
				if (var7 == 0) {
					break;
				}
				arg1.g1();
			}
		}
	}

	@ObfuscatedName("c.a(II)I")
	public static int perlinNoise(int arg0, int arg1) {
		int var2 = interpolatedNoise(arg0 + 45365, arg1 + 91923, 4) + (interpolatedNoise(arg0 + 10294, arg1 + 37821, 2) - 128 >> 1) + (interpolatedNoise(arg0, arg1, 1) - 128 >> 2) - 128;
		int var3 = (int) ((double) var2 * 0.3D) + 35;
		if (var3 < 10) {
			var3 = 10;
		} else if (var3 > 60) {
			var3 = 60;
		}
		return var3;
	}

	@ObfuscatedName("c.a(III)I")
	public static int interpolatedNoise(int arg0, int arg1, int arg2) {
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

	@ObfuscatedName("c.b(IIII)I")
	public static int interpolate(int arg0, int arg1, int arg2, int arg3) {
		int var4 = 65536 - Pix3D.cosTable[arg2 * 1024 / arg3] >> 1;
		return (arg0 * (65536 - var4) >> 16) + (arg1 * var4 >> 16);
	}

	@ObfuscatedName("c.b(II)I")
	public static int smoothNoise(int arg0, int arg1) {
		int var2 = noise(arg0 - 1, arg1 - 1) + noise(arg0 + 1, arg1 - 1) + noise(arg0 - 1, arg1 + 1) + noise(arg0 + 1, arg1 + 1);
		int var3 = noise(arg0 - 1, arg1) + noise(arg0 + 1, arg1) + noise(arg0, arg1 - 1) + noise(arg0, arg1 + 1);
		int var4 = noise(arg0, arg1);
		return var2 / 16 + var3 / 8 + var4 / 4;
	}

	@ObfuscatedName("c.c(II)I")
	public static int noise(int arg0, int arg1) {
		int var2 = arg0 + arg1 * 57;
		int var3 = var2 << 13 ^ var2;
		int var4 = var3 * (var3 * var3 * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE;
		return var4 >> 19 & 0xFF;
	}

	@ObfuscatedName("c.d(II)I")
	public static int getUCol(int arg0, int arg1) {
		if (arg0 == -1) {
			return 12345678;
		}
		int var2 = arg1 * (arg0 & 0x7F) / 128;
		if (var2 < 2) {
			var2 = 2;
		} else if (var2 > 126) {
			var2 = 126;
		}
		return (arg0 & 0xFF80) + var2;
	}

	@ObfuscatedName("c.c(III)Z")
	public static boolean changeLocAvailable(int arg0, int arg1) {
		LocType var3 = LocType.list(arg0);
		if (arg1 == 11) {
			arg1 = 10;
		}
		if (arg1 >= 5 && arg1 <= 8) {
			arg1 = 4;
		}
		return var3.checkModel(arg1);
	}

	@ObfuscatedName("c.a(Lr;IIZIIILjc;[[[III)V")
	public static void changeLocUnchecked(World arg0, int arg1, int arg2, int arg4, int arg5, int arg6, CollisionMap arg7, int[][][] arg8, int arg9, int arg10) {
		int var11 = arg8[arg1][arg10][arg9];
		int var12 = arg8[arg1][arg10 + 1][arg9];
		int var13 = arg8[arg1][arg10 + 1][arg9 + 1];
		int var14 = arg8[arg1][arg10][arg9 + 1];
		int var15 = var11 + var12 + var13 + var14 >> 2;
		LocType var16 = LocType.list(arg4);
		int var17 = arg10 + (arg9 << 7) + (arg4 << 14) + 1073741824;
		if (!var16.active) {
			var17 += Integer.MIN_VALUE;
		}
		byte var18 = (byte) ((arg6 << 6) + arg5);
		if (arg5 == 22) {
			ModelSource var19;
			if (var16.anim == -1) {
				var19 = var16.getModel(22, arg6, var11, var12, var13, var14, -1);
			} else {
				var19 = new ClientLocAnim(22, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.setGroundDecor(var15, var19, arg10, arg2, var17, var18, arg9);
			if (var16.blockwalk && var16.active) {
				arg7.blockGround(arg10, arg9);
			}
		} else if (arg5 == 10 || arg5 == 11) {
			ModelSource var20;
			if (var16.anim == -1) {
				var20 = var16.getModel(10, arg6, var11, var12, var13, var14, -1);
			} else {
				var20 = new ClientLocAnim(10, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			if (var20 != null) {
				int var21 = 0;
				if (arg5 == 11) {
					var21 += 256;
				}
				int var22;
				int var23;
				if (arg6 == 1 || arg6 == 3) {
					var22 = var16.length;
					var23 = var16.width;
				} else {
					var22 = var16.width;
					var23 = var16.length;
				}
				arg0.addScenery(arg10, arg9, var23, arg2, var15, var20, var18, var22, var21, var17);
			}
			if (var16.blockwalk) {
				arg7.addLoc(var16.width, arg10, var16.length, arg9, arg6, var16.blockrange);
			}
		} else if (arg5 >= 12) {
			ModelSource var24;
			if (var16.anim == -1) {
				var24 = var16.getModel(arg5, arg6, var11, var12, var13, var14, -1);
			} else {
				var24 = new ClientLocAnim(arg5, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.addScenery(arg10, arg9, 1, arg2, var15, var24, var18, 1, 0, var17);
			if (var16.blockwalk) {
				arg7.addLoc(var16.width, arg10, var16.length, arg9, arg6, var16.blockrange);
			}
		} else if (arg5 == 0) {
			ModelSource var25;
			if (var16.anim == -1) {
				var25 = var16.getModel(0, arg6, var11, var12, var13, var14, -1);
			} else {
				var25 = new ClientLocAnim(0, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.setWall(WSHAPE0[arg6], null, var15, arg10, arg2, var18, 0, var17, var25, arg9);
			if (var16.blockwalk) {
				arg7.addWall(arg6, arg5, var16.blockrange, arg9, arg10);
			}
		} else if (arg5 == 1) {
			ModelSource var26;
			if (var16.anim == -1) {
				var26 = var16.getModel(1, arg6, var11, var12, var13, var14, -1);
			} else {
				var26 = new ClientLocAnim(1, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.setWall(WSHAPE1[arg6], null, var15, arg10, arg2, var18, 0, var17, var26, arg9);
			if (var16.blockwalk) {
				arg7.addWall(arg6, arg5, var16.blockrange, arg9, arg10);
			}
		} else if (arg5 == 2) {
			int var27 = arg6 + 1 & 0x3;
			ModelSource var28;
			ModelSource var29;
			if (var16.anim == -1) {
				var28 = var16.getModel(2, arg6 + 4, var11, var12, var13, var14, -1);
				var29 = var16.getModel(2, var27, var11, var12, var13, var14, -1);
			} else {
				var28 = new ClientLocAnim(2, arg6 + 4, var16.anim, arg4, var13, true, var12, var14, var11);
				var29 = new ClientLocAnim(2, var27, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.setWall(WSHAPE0[arg6], var29, var15, arg10, arg2, var18, WSHAPE0[var27], var17, var28, arg9);
			if (var16.blockwalk) {
				arg7.addWall(arg6, arg5, var16.blockrange, arg9, arg10);
			}
		} else if (arg5 == 3) {
			ModelSource var30;
			if (var16.anim == -1) {
				var30 = var16.getModel(3, arg6, var11, var12, var13, var14, -1);
			} else {
				var30 = new ClientLocAnim(3, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.setWall(WSHAPE1[arg6], null, var15, arg10, arg2, var18, 0, var17, var30, arg9);
			if (var16.blockwalk) {
				arg7.addWall(arg6, arg5, var16.blockrange, arg9, arg10);
			}
		} else if (arg5 == 9) {
			ModelSource var31;
			if (var16.anim == -1) {
				var31 = var16.getModel(arg5, arg6, var11, var12, var13, var14, -1);
			} else {
				var31 = new ClientLocAnim(arg5, arg6, var16.anim, arg4, var13, true, var12, var14, var11);
			}
			arg0.addScenery(arg10, arg9, 1, arg2, var15, var31, var18, 1, 0, var17);
			if (var16.blockwalk) {
				arg7.addLoc(var16.width, arg10, var16.length, arg9, arg6, var16.blockrange);
			}
		} else {
			if (var16.hillskew) {
				if (arg6 == 1) {
					int var32 = var14;
					var14 = var13;
					var13 = var12;
					var12 = var11;
					var11 = var32;
				} else if (arg6 == 2) {
					int var33 = var14;
					var14 = var12;
					var12 = var33;
					int var34 = var13;
					var13 = var11;
					var11 = var34;
				} else if (arg6 == 3) {
					int var35 = var14;
					var14 = var11;
					var11 = var12;
					var12 = var13;
					var13 = var35;
				}
			}
			if (arg5 == 4) {
				ModelSource var36;
				if (var16.anim == -1) {
					var36 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
				} else {
					var36 = new ClientLocAnim(4, 0, var16.anim, arg4, var13, true, var12, var14, var11);
				}
				arg0.setDecor(WSHAPE0[arg6], var36, var18, arg9, var15, 0, arg6 * 512, arg2, arg10, 0, var17);
			} else if (arg5 == 5) {
				int var37 = 16;
				int var38 = arg0.wallType(arg2, arg10, arg9);
				if (var38 > 0) {
					var37 = LocType.list(var38 >> 14 & 0x7FFF).wallwidth;
				}
				ModelSource var39;
				if (var16.anim == -1) {
					var39 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
				} else {
					var39 = new ClientLocAnim(4, 0, var16.anim, arg4, var13, true, var12, var14, var11);
				}
				arg0.setDecor(WSHAPE0[arg6], var39, var18, arg9, var15, DECORXOF[arg6] * var37, arg6 * 512, arg2, arg10, DECORZOF[arg6] * var37, var17);
			} else if (arg5 == 6) {
				ModelSource var40;
				if (var16.anim == -1) {
					var40 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
				} else {
					var40 = new ClientLocAnim(4, 0, var16.anim, arg4, var13, true, var12, var14, var11);
				}
				arg0.setDecor(256, var40, var18, arg9, var15, 0, arg6, arg2, arg10, 0, var17);
			} else if (arg5 == 7) {
				ModelSource var41;
				if (var16.anim == -1) {
					var41 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
				} else {
					var41 = new ClientLocAnim(4, 0, var16.anim, arg4, var13, true, var12, var14, var11);
				}
				arg0.setDecor(512, var41, var18, arg9, var15, 0, arg6, arg2, arg10, 0, var17);
			} else if (arg5 == 8) {
				ModelSource var42;
				if (var16.anim == -1) {
					var42 = var16.getModel(4, 0, var11, var12, var13, var14, -1);
				} else {
					var42 = new ClientLocAnim(4, 0, var16.anim, arg4, var13, true, var12, var14, var11);
				}
				arg0.setDecor(768, var42, var18, arg9, var15, 0, arg6, arg2, arg10, 0, var17);
			}
		}
	}

	public ClientBuild(int arg0, int[][][] arg1, int arg2, byte[][][] arg4) {
		maxTileX = arg0;
		maxTileZ = arg2;
		groundh = arg1;
		mapl = arg4;
		floort1 = new byte[4][maxTileX][maxTileZ];
		floort2 = new byte[4][maxTileX][maxTileZ];
		floors = new byte[4][maxTileX][maxTileZ];
		floorr = new byte[4][maxTileX][maxTileZ];
		mapo = new int[4][maxTileX + 1][maxTileZ + 1];
		shadow = new byte[4][maxTileX + 1][maxTileZ + 1];
		lightmap = new int[maxTileX + 1][maxTileZ + 1];
		huetot = new int[maxTileZ];
		sattot = new int[maxTileZ];
		ligtot = new int[maxTileZ];
		comtot = new int[maxTileZ];
		tot = new int[maxTileZ];
	}

	@ObfuscatedName("c.a(IIIII)V")
	public void fadeAdjacent(int arg0, int arg1, int arg3, int arg4) {
		for (int var7 = arg0; var7 <= arg0 + arg4; var7++) {
			for (int var8 = arg1; var8 <= arg1 + arg3; var8++) {
				if (var8 >= 0 && var8 < maxTileX && var7 >= 0 && var7 < maxTileZ) {
					shadow[0][var8][var7] = 127;
					if (var8 == arg1 && var8 > 0) {
						groundh[0][var8][var7] = groundh[0][var8 - 1][var7];
					}
					if (var8 == arg1 + arg3 && var8 < maxTileX - 1) {
						groundh[0][var8][var7] = groundh[0][var8 + 1][var7];
					}
					if (var7 == arg0 && var7 > 0) {
						groundh[0][var8][var7] = groundh[0][var8][var7 - 1];
					}
					if (var7 == arg0 + arg4 && var7 < maxTileZ - 1) {
						groundh[0][var8][var7] = groundh[0][var8][var7 + 1];
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a(ZI[BIII)V")
	public void loadGround(int arg1, byte[] arg2, int arg3, int arg4, int arg5) {
		Packet var7 = new Packet(arg2);
		for (int var8 = 0; var8 < 4; var8++) {
			for (int var9 = 0; var9 < 64; var9++) {
				for (int var10 = 0; var10 < 64; var10++) {
					int var11 = var9 + arg5;
					int var12 = var10 + arg3;
					if (var11 >= 0 && var11 < 104 && var12 >= 0 && var12 < 104) {
						mapl[var8][var11][var12] = 0;
						while (true) {
							int var13 = var7.g1();
							if (var13 == 0) {
								if (var8 == 0) {
									groundh[0][var11][var12] = -perlinNoise(var11 + arg1 + 932731, var12 + 556238 + arg4) * 8;
								} else {
									groundh[var8][var11][var12] = groundh[var8 - 1][var11][var12] - 240;
								}
								break;
							}
							if (var13 == 1) {
								int var14 = var7.g1();
								if (var14 == 1) {
									var14 = 0;
								}
								if (var8 == 0) {
									groundh[0][var11][var12] = -var14 * 8;
								} else {
									groundh[var8][var11][var12] = groundh[var8 - 1][var11][var12] - var14 * 8;
								}
								break;
							}
							if (var13 <= 49) {
								floort2[var8][var11][var12] = var7.g1b();
								floors[var8][var11][var12] = (byte) ((var13 - 2) / 4);
								floorr[var8][var11][var12] = (byte) (var13 - 2 & 0x3);
							} else if (var13 <= 81) {
								mapl[var8][var11][var12] = (byte) (var13 - 49);
							} else {
								floort1[var8][var11][var12] = (byte) (var13 - 81);
							}
						}
					} else {
						while (true) {
							int var15 = var7.g1();
							if (var15 == 0) {
								break;
							}
							if (var15 == 1) {
								var7.g1();
								break;
							}
							if (var15 <= 49) {
								var7.g1();
							}
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a(I[BZLr;I[Ljc;)V")
	public void loadLocations(int arg0, byte[] arg1, World arg3, int arg4, CollisionMap[] arg5) {
		Packet var7 = new Packet(arg1);
		int var8 = -1;
		while (true) {
			int var9 = var7.gsmart();
			if (var9 == 0) {
				return;
			}
			var8 += var9;
			int var10 = 0;
			while (true) {
				int var11 = var7.gsmart();
				if (var11 == 0) {
					break;
				}
				var10 += var11 - 1;
				int var12 = var10 & 0x3F;
				int var13 = var10 >> 6 & 0x3F;
				int var14 = var10 >> 12;
				int var15 = var7.g1();
				int var16 = var15 >> 2;
				int var17 = var15 & 0x3;
				int var18 = var13 + arg4;
				int var19 = var12 + arg0;
				if (var18 > 0 && var19 > 0 && var18 < 103 && var19 < 103) {
					int var20 = var14;
					if ((mapl[1][var18][var19] & 0x2) == 2) {
						var20 = var14 - 1;
					}
					CollisionMap var21 = null;
					if (var20 >= 0) {
						var21 = arg5[var20];
					}
					addLoc(var14, var21, var17, var16, var19, arg3, var8, var18);
				}
			}
		}
	}

	@ObfuscatedName("c.a(IILjc;IIILr;II)V")
	public void addLoc(int arg1, CollisionMap arg2, int arg3, int arg4, int arg5, World arg6, int arg7, int arg8) {
		if (lowMem) {
			if ((mapl[arg1][arg8][arg5] & 0x10) != 0) {
				return;
			}
			if (getVisBelowLevel(arg1, arg5, arg8) != minusedlevel) {
				return;
			}
		}
		int var10 = groundh[arg1][arg8][arg5];
		int var11 = groundh[arg1][arg8 + 1][arg5];
		int var12 = groundh[arg1][arg8 + 1][arg5 + 1];
		int var13 = groundh[arg1][arg8][arg5 + 1];
		int var14 = var10 + var11 + var12 + var13 >> 2;
		LocType var15 = LocType.list(arg7);
		int var16 = arg8 + (arg5 << 7) + (arg7 << 14) + 1073741824;
		if (!var15.active) {
			var16 += Integer.MIN_VALUE;
		}
		byte var17 = (byte) ((arg3 << 6) + arg4);
		if (arg4 == 22) {
			if (!lowMem || var15.active || var15.forcedecor) {
				ModelSource var18;
				if (var15.anim == -1) {
					var18 = var15.getModel(22, arg3, var10, var11, var12, var13, -1);
				} else {
					var18 = new ClientLocAnim(22, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setGroundDecor(var14, var18, arg8, arg1, var16, var17, arg5);
				if (var15.blockwalk && var15.active && arg2 != null) {
					arg2.blockGround(arg8, arg5);
				}
			}
		} else if (arg4 == 10 || arg4 == 11) {
			ModelSource var19;
			if (var15.anim == -1) {
				var19 = var15.getModel(10, arg3, var10, var11, var12, var13, -1);
			} else {
				var19 = new ClientLocAnim(10, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			if (var19 != null) {
				int var20 = 0;
				if (arg4 == 11) {
					var20 += 256;
				}
				int var21;
				int var22;
				if (arg3 == 1 || arg3 == 3) {
					var21 = var15.length;
					var22 = var15.width;
				} else {
					var21 = var15.width;
					var22 = var15.length;
				}
				if (arg6.addScenery(arg8, arg5, var22, arg1, var14, var19, var17, var21, var20, var16) && var15.shadow) {
					Model var23;
					if (var19 instanceof Model) {
						var23 = (Model) var19;
					} else {
						var23 = var15.getModel(10, arg3, var10, var11, var12, var13, -1);
					}
					if (var23 != null) {
						for (int var24 = 0; var24 <= var21; var24++) {
							for (int var25 = 0; var25 <= var22; var25++) {
								int var26 = var23.radius / 4;
								if (var26 > 30) {
									var26 = 30;
								}
								if (var26 > shadow[arg1][arg8 + var24][arg5 + var25]) {
									shadow[arg1][arg8 + var24][arg5 + var25] = (byte) var26;
								}
							}
						}
					}
				}
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addLoc(var15.width, arg8, var15.length, arg5, arg3, var15.blockrange);
			}
		} else if (arg4 >= 12) {
			ModelSource var27;
			if (var15.anim == -1) {
				var27 = var15.getModel(arg4, arg3, var10, var11, var12, var13, -1);
			} else {
				var27 = new ClientLocAnim(arg4, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.addScenery(arg8, arg5, 1, arg1, var14, var27, var17, 1, 0, var16);
			if (arg4 >= 12 && arg4 <= 17 && arg4 != 13 && arg1 > 0) {
				mapo[arg1][arg8][arg5] |= 0x924;
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addLoc(var15.width, arg8, var15.length, arg5, arg3, var15.blockrange);
			}
		} else if (arg4 == 0) {
			ModelSource var28;
			if (var15.anim == -1) {
				var28 = var15.getModel(0, arg3, var10, var11, var12, var13, -1);
			} else {
				var28 = new ClientLocAnim(0, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.setWall(WSHAPE0[arg3], null, var14, arg8, arg1, var17, 0, var16, var28, arg5);
			if (arg3 == 0) {
				if (var15.shadow) {
					shadow[arg1][arg8][arg5] = 50;
					shadow[arg1][arg8][arg5 + 1] = 50;
				}
				if (var15.occlude) {
					mapo[arg1][arg8][arg5] |= 0x249;
				}
			} else if (arg3 == 1) {
				if (var15.shadow) {
					shadow[arg1][arg8][arg5 + 1] = 50;
					shadow[arg1][arg8 + 1][arg5 + 1] = 50;
				}
				if (var15.occlude) {
					mapo[arg1][arg8][arg5 + 1] |= 0x492;
				}
			} else if (arg3 == 2) {
				if (var15.shadow) {
					shadow[arg1][arg8 + 1][arg5] = 50;
					shadow[arg1][arg8 + 1][arg5 + 1] = 50;
				}
				if (var15.occlude) {
					mapo[arg1][arg8 + 1][arg5] |= 0x249;
				}
			} else if (arg3 == 3) {
				if (var15.shadow) {
					shadow[arg1][arg8][arg5] = 50;
					shadow[arg1][arg8 + 1][arg5] = 50;
				}
				if (var15.occlude) {
					mapo[arg1][arg8][arg5] |= 0x492;
				}
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addWall(arg3, arg4, var15.blockrange, arg5, arg8);
			}
			if (var15.wallwidth != 16) {
				arg6.moveDecor(arg8, arg5, arg1, var15.wallwidth);
			}
		} else if (arg4 == 1) {
			ModelSource var29;
			if (var15.anim == -1) {
				var29 = var15.getModel(1, arg3, var10, var11, var12, var13, -1);
			} else {
				var29 = new ClientLocAnim(1, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.setWall(WSHAPE1[arg3], null, var14, arg8, arg1, var17, 0, var16, var29, arg5);
			if (var15.shadow) {
				if (arg3 == 0) {
					shadow[arg1][arg8][arg5 + 1] = 50;
				} else if (arg3 == 1) {
					shadow[arg1][arg8 + 1][arg5 + 1] = 50;
				} else if (arg3 == 2) {
					shadow[arg1][arg8 + 1][arg5] = 50;
				} else if (arg3 == 3) {
					shadow[arg1][arg8][arg5] = 50;
				}
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addWall(arg3, arg4, var15.blockrange, arg5, arg8);
			}
		} else if (arg4 == 2) {
			int var30 = arg3 + 1 & 0x3;
			ModelSource var31;
			ModelSource var32;
			if (var15.anim == -1) {
				var31 = var15.getModel(2, arg3 + 4, var10, var11, var12, var13, -1);
				var32 = var15.getModel(2, var30, var10, var11, var12, var13, -1);
			} else {
				var31 = new ClientLocAnim(2, arg3 + 4, var15.anim, arg7, var12, true, var11, var13, var10);
				var32 = new ClientLocAnim(2, var30, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.setWall(WSHAPE0[arg3], var32, var14, arg8, arg1, var17, WSHAPE0[var30], var16, var31, arg5);
			if (var15.occlude) {
				if (arg3 == 0) {
					mapo[arg1][arg8][arg5] |= 0x249;
					mapo[arg1][arg8][arg5 + 1] |= 0x492;
				} else if (arg3 == 1) {
					mapo[arg1][arg8][arg5 + 1] |= 0x492;
					mapo[arg1][arg8 + 1][arg5] |= 0x249;
				} else if (arg3 == 2) {
					mapo[arg1][arg8 + 1][arg5] |= 0x249;
					mapo[arg1][arg8][arg5] |= 0x492;
				} else if (arg3 == 3) {
					mapo[arg1][arg8][arg5] |= 0x492;
					mapo[arg1][arg8][arg5] |= 0x249;
				}
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addWall(arg3, arg4, var15.blockrange, arg5, arg8);
			}
			if (var15.wallwidth != 16) {
				arg6.moveDecor(arg8, arg5, arg1, var15.wallwidth);
			}
		} else if (arg4 == 3) {
			ModelSource var33;
			if (var15.anim == -1) {
				var33 = var15.getModel(3, arg3, var10, var11, var12, var13, -1);
			} else {
				var33 = new ClientLocAnim(3, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.setWall(WSHAPE1[arg3], null, var14, arg8, arg1, var17, 0, var16, var33, arg5);
			if (var15.shadow) {
				if (arg3 == 0) {
					shadow[arg1][arg8][arg5 + 1] = 50;
				} else if (arg3 == 1) {
					shadow[arg1][arg8 + 1][arg5 + 1] = 50;
				} else if (arg3 == 2) {
					shadow[arg1][arg8 + 1][arg5] = 50;
				} else if (arg3 == 3) {
					shadow[arg1][arg8][arg5] = 50;
				}
			}
			if (var15.blockwalk && arg2 != null) {
				arg2.addWall(arg3, arg4, var15.blockrange, arg5, arg8);
			}
		} else if (arg4 == 9) {
			ModelSource var34;
			if (var15.anim == -1) {
				var34 = var15.getModel(arg4, arg3, var10, var11, var12, var13, -1);
			} else {
				var34 = new ClientLocAnim(arg4, arg3, var15.anim, arg7, var12, true, var11, var13, var10);
			}
			arg6.addScenery(arg8, arg5, 1, arg1, var14, var34, var17, 1, 0, var16);
			if (var15.blockwalk && arg2 != null) {
				arg2.addLoc(var15.width, arg8, var15.length, arg5, arg3, var15.blockrange);
			}
		} else {
			if (var15.hillskew) {
				if (arg3 == 1) {
					int var35 = var13;
					var13 = var12;
					var12 = var11;
					var11 = var10;
					var10 = var35;
				} else if (arg3 == 2) {
					int var36 = var13;
					var13 = var11;
					var11 = var36;
					int var37 = var12;
					var12 = var10;
					var10 = var37;
				} else if (arg3 == 3) {
					int var38 = var13;
					var13 = var10;
					var10 = var11;
					var11 = var12;
					var12 = var38;
				}
			}
			if (arg4 == 4) {
				ModelSource var39;
				if (var15.anim == -1) {
					var39 = var15.getModel(4, 0, var10, var11, var12, var13, -1);
				} else {
					var39 = new ClientLocAnim(4, 0, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setDecor(WSHAPE0[arg3], var39, var17, arg5, var14, 0, arg3 * 512, arg1, arg8, 0, var16);
			} else if (arg4 == 5) {
				int var40 = 16;
				int var41 = arg6.wallType(arg1, arg8, arg5);
				if (var41 > 0) {
					var40 = LocType.list(var41 >> 14 & 0x7FFF).wallwidth;
				}
				ModelSource var42;
				if (var15.anim == -1) {
					var42 = var15.getModel(4, 0, var10, var11, var12, var13, -1);
				} else {
					var42 = new ClientLocAnim(4, 0, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setDecor(WSHAPE0[arg3], var42, var17, arg5, var14, DECORXOF[arg3] * var40, arg3 * 512, arg1, arg8, DECORZOF[arg3] * var40, var16);
			} else if (arg4 == 6) {
				ModelSource var43;
				if (var15.anim == -1) {
					var43 = var15.getModel(4, 0, var10, var11, var12, var13, -1);
				} else {
					var43 = new ClientLocAnim(4, 0, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setDecor(256, var43, var17, arg5, var14, 0, arg3, arg1, arg8, 0, var16);
			} else if (arg4 == 7) {
				ModelSource var44;
				if (var15.anim == -1) {
					var44 = var15.getModel(4, 0, var10, var11, var12, var13, -1);
				} else {
					var44 = new ClientLocAnim(4, 0, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setDecor(512, var44, var17, arg5, var14, 0, arg3, arg1, arg8, 0, var16);
			} else if (arg4 == 8) {
				ModelSource var45;
				if (var15.anim == -1) {
					var45 = var15.getModel(4, 0, var10, var11, var12, var13, -1);
				} else {
					var45 = new ClientLocAnim(4, 0, var15.anim, arg7, var12, true, var11, var13, var10);
				}
				arg6.setDecor(768, var45, var17, arg5, var14, 0, arg3, arg1, arg8, 0, var16);
			}
		}
	}

	@ObfuscatedName("c.a([Ljc;ZLr;)V")
	public void finishBuild(CollisionMap[] arg0, World arg2) {
		for (int var4 = 0; var4 < 4; var4++) {
			for (int var5 = 0; var5 < 104; var5++) {
				for (int var6 = 0; var6 < 104; var6++) {
					if ((mapl[var4][var5][var6] & 0x1) == 1) {
						int var7 = var4;
						if ((mapl[1][var5][var6] & 0x2) == 2) {
							var7 = var4 - 1;
						}
						if (var7 >= 0) {
							arg0[var7].blockGround(var5, var6);
						}
					}
				}
			}
		}
		hueOff += (int) (Math.random() * 5.0D) - 2;
		if (hueOff < -8) {
			hueOff = -8;
		}
		if (hueOff > 8) {
			hueOff = 8;
		}
		ligOff += (int) (Math.random() * 5.0D) - 2;
		if (ligOff < -16) {
			ligOff = -16;
		}
		if (ligOff > 16) {
			ligOff = 16;
		}
		for (int var8 = 0; var8 < 4; var8++) {
			byte[][] var9 = shadow[var8];
			byte var10 = 96;
			short var11 = 768;
			byte var12 = -50;
			byte var13 = -10;
			byte var14 = -50;
			int var15 = (int) Math.sqrt((double) (var12 * var12 + var13 * var13 + var14 * var14));
			int var16 = var11 * var15 >> 8;
			for (int var17 = 1; var17 < maxTileZ - 1; var17++) {
				for (int var18 = 1; var18 < maxTileX - 1; var18++) {
					int var19 = groundh[var8][var18 + 1][var17] - groundh[var8][var18 - 1][var17];
					int var20 = groundh[var8][var18][var17 + 1] - groundh[var8][var18][var17 - 1];
					int var21 = (int) Math.sqrt((double) (var19 * var19 + var20 * var20 + 65536));
					int var22 = (var19 << 8) / var21;
					int var23 = 65536 / var21;
					int var24 = (var20 << 8) / var21;
					int var25 = var10 + (var12 * var22 + var13 * var23 + var14 * var24) / var16;
					int var26 = (var9[var18 - 1][var17] >> 2) + (var9[var18 + 1][var17] >> 3) + (var9[var18][var17 - 1] >> 2) + (var9[var18][var17 + 1] >> 3) + (var9[var18][var17] >> 1);
					lightmap[var18][var17] = var25 - var26;
				}
			}
			for (int var27 = 0; var27 < maxTileZ; var27++) {
				huetot[var27] = 0;
				sattot[var27] = 0;
				ligtot[var27] = 0;
				comtot[var27] = 0;
				tot[var27] = 0;
			}
			for (int var28 = -5; var28 < maxTileX + 5; var28++) {
				for (int var29 = 0; var29 < maxTileZ; var29++) {
					int var30 = var28 + 5;
					int var10002;
					if (var30 >= 0 && var30 < maxTileX) {
						int var31 = floort1[var8][var30][var29] & 0xFF;
						if (var31 > 0) {
							FloType var32 = FloType.list[var31 - 1];
							huetot[var29] += var32.underlayHue;
							sattot[var29] += var32.saturation;
							ligtot[var29] += var32.lightness;
							comtot[var29] += var32.chroma;
							var10002 = tot[var29]++;
						}
					}
					int var33 = var28 - 5;
					if (var33 >= 0 && var33 < maxTileX) {
						int var34 = floort1[var8][var33][var29] & 0xFF;
						if (var34 > 0) {
							FloType var35 = FloType.list[var34 - 1];
							huetot[var29] -= var35.underlayHue;
							sattot[var29] -= var35.saturation;
							ligtot[var29] -= var35.lightness;
							comtot[var29] -= var35.chroma;
							var10002 = tot[var29]--;
						}
					}
				}
				if (var28 >= 1 && var28 < maxTileX - 1) {
					int var36 = 0;
					int var37 = 0;
					int var38 = 0;
					int var39 = 0;
					int var40 = 0;
					for (int var41 = -5; var41 < maxTileZ + 5; var41++) {
						int var42 = var41 + 5;
						if (var42 >= 0 && var42 < maxTileZ) {
							var36 += huetot[var42];
							var37 += sattot[var42];
							var38 += ligtot[var42];
							var39 += comtot[var42];
							var40 += tot[var42];
						}
						int var43 = var41 - 5;
						if (var43 >= 0 && var43 < maxTileZ) {
							var36 -= huetot[var43];
							var37 -= sattot[var43];
							var38 -= ligtot[var43];
							var39 -= comtot[var43];
							var40 -= tot[var43];
						}
						if (var41 >= 1 && var41 < maxTileZ - 1 && (!lowMem || (mapl[var8][var28][var41] & 0x10) == 0 && getVisBelowLevel(var8, var41, var28) == minusedlevel)) {
							int var44 = floort1[var8][var28][var41] & 0xFF;
							int var45 = floort2[var8][var28][var41] & 0xFF;
							if (var44 > 0 || var45 > 0) {
								int var46 = groundh[var8][var28][var41];
								int var47 = groundh[var8][var28 + 1][var41];
								int var48 = groundh[var8][var28 + 1][var41 + 1];
								int var49 = groundh[var8][var28][var41 + 1];
								int var50 = lightmap[var28][var41];
								int var51 = lightmap[var28 + 1][var41];
								int var52 = lightmap[var28 + 1][var41 + 1];
								int var53 = lightmap[var28][var41 + 1];
								int var54 = -1;
								int var55 = -1;
								if (var44 > 0) {
									int var56 = var36 * 256 / var39;
									int var57 = var37 / var40;
									int var58 = var38 / var40;
									var54 = getTable(var56, var57, var58);
									int var59 = var56 + hueOff & 0xFF;
									int var60 = var58 + ligOff;
									if (var60 < 0) {
										var60 = 0;
									} else if (var60 > 255) {
										var60 = 255;
									}
									var55 = getTable(var59, var57, var60);
								}
								if (var8 > 0) {
									boolean var61 = true;
									if (var44 == 0 && floors[var8][var28][var41] != 0) {
										var61 = false;
									}
									if (var45 > 0 && !FloType.list[var45 - 1].occlude) {
										var61 = false;
									}
									if (var61 && var46 == var47 && var46 == var48 && var46 == var49) {
										mapo[var8][var28][var41] |= 0x924;
									}
								}
								int var62 = 0;
								if (var54 != -1) {
									var62 = Pix3D.colourTable[getUCol(var55, 96)];
								}
								if (var45 == 0) {
									arg2.setGround(var8, var28, var41, 0, 0, -1, var46, var47, var48, var49, getUCol(var54, var50), getUCol(var54, var51), getUCol(var54, var52), getUCol(var54, var53), 0, 0, 0, 0, var62, 0);
								} else {
									int var63 = floors[var8][var28][var41] + 1;
									byte var64 = floorr[var8][var28][var41];
									FloType var65 = FloType.list[var45 - 1];
									int var66 = var65.texture;
									int var67;
									int var68;
									if (var66 >= 0) {
										var67 = Pix3D.getTextureAverage(var66);
										var68 = -1;
									} else if (var65.colour == 16711935) {
										var67 = 0;
										var68 = -2;
										var66 = -1;
									} else {
										var68 = getTable(var65.hue, var65.saturation, var65.lightness);
										var67 = Pix3D.colourTable[getOCol(var65.overlayHsl, 96)];
									}
									arg2.setGround(var8, var28, var41, var63, var64, var66, var46, var47, var48, var49, getUCol(var54, var50), getUCol(var54, var51), getUCol(var54, var52), getUCol(var54, var53), getOCol(var68, var50), getOCol(var68, var51), getOCol(var68, var52), getOCol(var68, var53), var62, var67);
								}
							}
						}
					}
				}
			}
			for (int var69 = 1; var69 < maxTileZ - 1; var69++) {
				for (int var70 = 1; var70 < maxTileX - 1; var70++) {
					arg2.setLayer(var8, var70, var69, getVisBelowLevel(var8, var69, var70));
				}
			}
		}
		arg2.shareLight(-50, 64, -10, -50, 768);
		for (int var71 = 0; var71 < maxTileX; var71++) {
			for (int var72 = 0; var72 < maxTileZ; var72++) {
				if ((mapl[1][var71][var72] & 0x2) == 2) {
					arg2.pushDown(var71, var72);
				}
			}
		}
		int var73 = 1;
		int var74 = 2;
		int var75 = 4;
		for (int var76 = 0; var76 < 4; var76++) {
			if (var76 > 0) {
				var73 <<= 0x3;
				var74 <<= 0x3;
				var75 <<= 0x3;
			}
			for (int var77 = 0; var77 <= var76; var77++) {
				for (int var78 = 0; var78 <= maxTileZ; var78++) {
					for (int var79 = 0; var79 <= maxTileX; var79++) {
						if ((mapo[var77][var79][var78] & var73) != 0) {
							int var80 = var78;
							int var81 = var78;
							int var82 = var77;
							int var83 = var77;
							while (var80 > 0 && (mapo[var77][var79][var80 - 1] & var73) != 0) {
								var80--;
							}
							while (var81 < maxTileZ && (mapo[var77][var79][var81 + 1] & var73) != 0) {
								var81++;
							}
							label325: while (var82 > 0) {
								for (int var84 = var80; var84 <= var81; var84++) {
									if ((mapo[var82 - 1][var79][var84] & var73) == 0) {
										break label325;
									}
								}
								var82--;
							}
							label314: while (var83 < var76) {
								for (int var85 = var80; var85 <= var81; var85++) {
									if ((mapo[var83 + 1][var79][var85] & var73) == 0) {
										break label314;
									}
								}
								var83++;
							}
							int var86 = (var83 + 1 - var82) * (var81 - var80 + 1);
							if (var86 >= 8) {
								short var87 = 240;
								int var88 = groundh[var83][var79][var80] - var87;
								int var89 = groundh[var82][var79][var80];
								World.setOcclude(var76, var88, var81 * 128 + 128, var79 * 128, var79 * 128, var80 * 128, var89, 1);
								for (int var90 = var82; var90 <= var83; var90++) {
									for (int var91 = var80; var91 <= var81; var91++) {
										mapo[var90][var79][var91] &= ~var73;
									}
								}
							}
						}
						if ((mapo[var77][var79][var78] & var74) != 0) {
							int var92 = var79;
							int var93 = var79;
							int var94 = var77;
							int var95 = var77;
							while (var92 > 0 && (mapo[var77][var92 - 1][var78] & var74) != 0) {
								var92--;
							}
							while (var93 < maxTileX && (mapo[var77][var93 + 1][var78] & var74) != 0) {
								var93++;
							}
							label378: while (var94 > 0) {
								for (int var96 = var92; var96 <= var93; var96++) {
									if ((mapo[var94 - 1][var96][var78] & var74) == 0) {
										break label378;
									}
								}
								var94--;
							}
							label367: while (var95 < var76) {
								for (int var97 = var92; var97 <= var93; var97++) {
									if ((mapo[var95 + 1][var97][var78] & var74) == 0) {
										break label367;
									}
								}
								var95++;
							}
							int var98 = (var95 + 1 - var94) * (var93 - var92 + 1);
							if (var98 >= 8) {
								short var99 = 240;
								int var100 = groundh[var95][var92][var78] - var99;
								int var101 = groundh[var94][var92][var78];
								World.setOcclude(var76, var100, var78 * 128, var92 * 128, var93 * 128 + 128, var78 * 128, var101, 2);
								for (int var102 = var94; var102 <= var95; var102++) {
									for (int var103 = var92; var103 <= var93; var103++) {
										mapo[var102][var103][var78] &= ~var74;
									}
								}
							}
						}
						if ((mapo[var77][var79][var78] & var75) != 0) {
							int var104 = var79;
							int var105 = var79;
							int var106 = var78;
							int var107 = var78;
							while (var106 > 0 && (mapo[var77][var79][var106 - 1] & var75) != 0) {
								var106--;
							}
							while (var107 < maxTileZ && (mapo[var77][var79][var107 + 1] & var75) != 0) {
								var107++;
							}
							label431: while (var104 > 0) {
								for (int var108 = var106; var108 <= var107; var108++) {
									if ((mapo[var77][var104 - 1][var108] & var75) == 0) {
										break label431;
									}
								}
								var104--;
							}
							label420: while (var105 < maxTileX) {
								for (int var109 = var106; var109 <= var107; var109++) {
									if ((mapo[var77][var105 + 1][var109] & var75) == 0) {
										break label420;
									}
								}
								var105++;
							}
							if ((var105 + 1 - var104) * (var107 - var106 + 1) >= 4) {
								int var110 = groundh[var77][var104][var106];
								World.setOcclude(var76, var110, var107 * 128 + 128, var104 * 128, var105 * 128 + 128, var106 * 128, var110, 4);
								for (int var111 = var104; var111 <= var105; var111++) {
									for (int var112 = var106; var112 <= var107; var112++) {
										mapo[var77][var111][var112] &= ~var75;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("c.a(IIII)I")
	public int getVisBelowLevel(int arg0, int arg1, int arg3) {
		if ((mapl[arg0][arg3][arg1] & 0x8) == 0) {
			return arg0 <= 0 || (mapl[1][arg3][arg1] & 0x2) == 0 ? arg0 : arg0 - 1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("c.e(II)I")
	public int getOCol(int arg0, int arg1) {
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
			int var4 = arg1 * (arg0 & 0x7F) / 128;
			if (var4 < 2) {
				var4 = 2;
			} else if (var4 > 126) {
				var4 = 126;
			}
			return (arg0 & 0xFF80) + var4;
		}
	}

	@ObfuscatedName("c.b(III)I")
	public int getTable(int arg0, int arg1, int arg2) {
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
}
