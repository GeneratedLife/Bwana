package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.Model;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Pix2D;
import jagex2.graphics.Pix32;
import jagex2.graphics.Pix3D;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("hc")
public final class ObjType {

	@ObfuscatedName("hc.e")
	public static int numDefinitions;

	@ObfuscatedName("hc.f")
	public static int[] idx;

	@ObfuscatedName("hc.g")
	public static Packet dat;

	@ObfuscatedName("hc.h")
	public static ObjType[] recent;

	@ObfuscatedName("hc.i")
	public static int recentPos;

	@ObfuscatedName("hc.j")
	public static boolean memServer = true;

	@ObfuscatedName("hc.k")
	public int id = -1;

	@ObfuscatedName("hc.l")
	public int model;

	@ObfuscatedName("hc.m")
	public String name;

	@ObfuscatedName("hc.n")
	public byte[] desc;

	@ObfuscatedName("hc.o")
	public int[] recol_s;

	@ObfuscatedName("hc.p")
	public int[] recol_d;

	@ObfuscatedName("hc.q")
	public int zoom2d;

	@ObfuscatedName("hc.r")
	public int xan2d;

	@ObfuscatedName("hc.s")
	public int yan2d;

	@ObfuscatedName("hc.t")
	public int zan2d;

	@ObfuscatedName("hc.u")
	public int xof2d;

	@ObfuscatedName("hc.v")
	public int yof2d;

	@ObfuscatedName("hc.w")
	public int field1052;

	@ObfuscatedName("hc.x")
	public boolean stackable;

	@ObfuscatedName("hc.y")
	public int cost;

	@ObfuscatedName("hc.z")
	public boolean members;

	@ObfuscatedName("hc.X")
	public static LruCache modelcache = new LruCache(50);

	@ObfuscatedName("hc.Y")
	public static LruCache spriteCache = new LruCache(100);

	@ObfuscatedName("hc.E")
	public byte manwearOffset;

	@ObfuscatedName("hc.H")
	public byte womanwearOffset;

	@ObfuscatedName("hc.C")
	public int manwear;

	@ObfuscatedName("hc.D")
	public int manwear2;

	@ObfuscatedName("hc.F")
	public int womanwear;

	@ObfuscatedName("hc.G")
	public int womanwear2;

	@ObfuscatedName("hc.I")
	public int manwear3;

	@ObfuscatedName("hc.J")
	public int womanwear3;

	@ObfuscatedName("hc.K")
	public int manhead;

	@ObfuscatedName("hc.L")
	public int manhead2;

	@ObfuscatedName("hc.M")
	public int womanhead;

	@ObfuscatedName("hc.N")
	public int womanhead2;

	@ObfuscatedName("hc.Q")
	public int certlink;

	@ObfuscatedName("hc.R")
	public int certtemplate;

	@ObfuscatedName("hc.S")
	public int resizex;

	@ObfuscatedName("hc.T")
	public int resizey;

	@ObfuscatedName("hc.U")
	public int resizez;

	@ObfuscatedName("hc.V")
	public int ambient;

	@ObfuscatedName("hc.W")
	public int contrast;

	@ObfuscatedName("hc.O")
	public int[] countobj;

	@ObfuscatedName("hc.P")
	public int[] countco;

	@ObfuscatedName("hc.A")
	public String[] op;

	@ObfuscatedName("hc.B")
	public String[] iop;

	@ObfuscatedName("hc.a(Lxb;)V")
	public static void init(JagFile arg0) {
		dat = new Packet(arg0.read("obj.dat", null));
		Packet var1 = new Packet(arg0.read("obj.idx", null));
		numDefinitions = var1.g2();
		idx = new int[numDefinitions];
		int var2 = 2;
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			idx[var3] = var2;
			var2 += var1.g2();
		}
		recent = new ObjType[10];
		for (int var4 = 0; var4 < 10; var4++) {
			recent[var4] = new ObjType();
		}
	}

	@ObfuscatedName("hc.a(B)V")
	public static void unload() {
		modelcache = null;
		spriteCache = null;
		idx = null;
		recent = null;
		dat = null;
	}

	@ObfuscatedName("hc.a(I)Lhc;")
	public static ObjType list(int arg0) {
		for (int var1 = 0; var1 < 10; var1++) {
			if (recent[var1].id == arg0) {
				return recent[var1];
			}
		}
		recentPos = (recentPos + 1) % 10;
		ObjType var2 = recent[recentPos];
		dat.data = idx[arg0];
		var2.id = arg0;
		var2.reset();
		var2.decode(dat);
		if (var2.certtemplate != -1) {
			var2.genCert();
		}
		if (!memServer && var2.members) {
			var2.name = "Members Object";
			var2.desc = "Login to a members' server to use this object.".getBytes();
			var2.op = null;
			var2.iop = null;
		}
		return var2;
	}

	@ObfuscatedName("hc.a(IIII)Lib;")
	public static Pix32 getSprite(int arg0, int arg1, int arg3) {
		if (arg1 == 0) {
			Pix32 var4 = (Pix32) spriteCache.find((long) arg0);
			if (var4 != null && var4.ohi != arg3 && var4.ohi != -1) {
				var4.unlink();
				var4 = null;
			}
			if (var4 != null) {
				return var4;
			}
		}
		ObjType var5 = list(arg0);
		if (var5.countobj == null) {
			arg3 = -1;
		}
		if (arg3 > 1) {
			int var6 = -1;
			for (int var7 = 0; var7 < 10; var7++) {
				if (arg3 >= var5.countco[var7] && var5.countco[var7] != 0) {
					var6 = var5.countobj[var7];
				}
			}
			if (var6 != -1) {
				var5 = list(var6);
			}
		}
		Model var8 = var5.getModelLit(1);
		if (var8 == null) {
			return null;
		}
		Pix32 var9 = null;
		if (var5.certtemplate != -1) {
			var9 = getSprite(var5.certlink, -1, 10);
			if (var9 == null) {
				return null;
			}
		}
		Pix32 var10 = new Pix32(32, 32);
		int var11 = Pix3D.originX;
		int var12 = Pix3D.originY;
		int[] var13 = Pix3D.scanline;
		int[] var14 = Pix2D.pixels;
		int var15 = Pix2D.width;
		int var16 = Pix2D.height;
		int var17 = Pix2D.clipMinX;
		int var18 = Pix2D.clipMaxX;
		int var19 = Pix2D.clipMinY;
		int var20 = Pix2D.clipMaxY;
		Pix3D.lowDetail = false;
		Pix2D.setPixels(32, 32, var10.data);
		Pix2D.fillRect(32, 0, 32, 0, 0);
		Pix3D.setRenderClipping();
		int var21 = var5.zoom2d;
		if (arg1 == -1) {
			var21 = (int) ((double) var21 * 1.5D);
		}
		if (arg1 > 0) {
			var21 = (int) ((double) var21 * 1.04D);
		}
		int var22 = Pix3D.sinTable[var5.xan2d] * var21 >> 16;
		int var23 = Pix3D.cosTable[var5.xan2d] * var21 >> 16;
		var8.objRender(0, var5.yan2d, var5.zan2d, var5.xan2d, var5.xof2d, var22 + var8.minY / 2 + var5.yof2d, var23 + var5.yof2d);
		for (int var24 = 31; var24 >= 0; var24--) {
			for (int var25 = 31; var25 >= 0; var25--) {
				if (var10.data[var24 + var25 * 32] == 0) {
					if (var24 > 0 && var10.data[var24 + var25 * 32 - 1] > 1) {
						var10.data[var24 + var25 * 32] = 1;
					} else if (var25 > 0 && var10.data[var24 + (var25 - 1) * 32] > 1) {
						var10.data[var24 + var25 * 32] = 1;
					} else if (var24 < 31 && var10.data[var24 + var25 * 32 + 1] > 1) {
						var10.data[var24 + var25 * 32] = 1;
					} else if (var25 < 31 && var10.data[var24 + (var25 + 1) * 32] > 1) {
						var10.data[var24 + var25 * 32] = 1;
					}
				}
			}
		}
		if (arg1 > 0) {
			for (int var26 = 31; var26 >= 0; var26--) {
				for (int var27 = 31; var27 >= 0; var27--) {
					if (var10.data[var26 + var27 * 32] == 0) {
						if (var26 > 0 && var10.data[var26 + var27 * 32 - 1] == 1) {
							var10.data[var26 + var27 * 32] = arg1;
						} else if (var27 > 0 && var10.data[var26 + (var27 - 1) * 32] == 1) {
							var10.data[var26 + var27 * 32] = arg1;
						} else if (var26 < 31 && var10.data[var26 + var27 * 32 + 1] == 1) {
							var10.data[var26 + var27 * 32] = arg1;
						} else if (var27 < 31 && var10.data[var26 + (var27 + 1) * 32] == 1) {
							var10.data[var26 + var27 * 32] = arg1;
						}
					}
				}
			}
		} else if (arg1 == 0) {
			for (int var28 = 31; var28 >= 0; var28--) {
				for (int var29 = 31; var29 >= 0; var29--) {
					if (var10.data[var28 + var29 * 32] == 0 && var28 > 0 && var29 > 0 && var10.data[var28 + (var29 - 1) * 32 - 1] > 0) {
						var10.data[var28 + var29 * 32] = 3153952;
					}
				}
			}
		}
		if (var5.certtemplate != -1) {
			int var30 = var9.owi;
			int var31 = var9.ohi;
			var9.owi = 32;
			var9.ohi = 32;
			var9.plotSprite(0, 0);
			var9.owi = var30;
			var9.ohi = var31;
		}
		if (arg1 == 0) {
			spriteCache.put((long) arg0, var10);
		}
		Pix2D.setPixels(var15, var16, var14);
		Pix2D.setClipping(5, var20, var18, var17, var19);
		Pix3D.originX = var11;
		Pix3D.originY = var12;
		Pix3D.scanline = var13;
		Pix3D.lowDetail = true;
		if (var5.stackable) {
			var10.owi = 33;
		} else {
			var10.owi = 32;
		}
		var10.ohi = arg3;
		return var10;
	}

	@ObfuscatedName("hc.a()V")
	public void reset() {
		model = 0;
		name = null;
		desc = null;
		recol_s = null;
		recol_d = null;
		zoom2d = 2000;
		xan2d = 0;
		yan2d = 0;
		zan2d = 0;
		xof2d = 0;
		yof2d = 0;
		field1052 = -1;
		stackable = false;
		cost = 1;
		members = false;
		op = null;
		iop = null;
		manwear = -1;
		manwear2 = -1;
		manwearOffset = 0;
		womanwear = -1;
		womanwear2 = -1;
		womanwearOffset = 0;
		manwear3 = -1;
		womanwear3 = -1;
		manhead = -1;
		manhead2 = -1;
		womanhead = -1;
		womanhead2 = -1;
		countobj = null;
		countco = null;
		certlink = -1;
		certtemplate = -1;
		resizex = 128;
		resizey = 128;
		resizez = 128;
		ambient = 0;
		contrast = 0;
	}

	@ObfuscatedName("hc.a(BLlb;)V")
	public void decode(Packet arg1) {
		while (true) {
			while (true) {
				int var3 = arg1.g1();
				if (var3 == 0) {
					return;
				}
				if (var3 == 1) {
					model = arg1.g2();
				} else if (var3 == 2) {
					name = arg1.gjstr();
				} else if (var3 == 3) {
					desc = arg1.gjstrraw();
				} else if (var3 == 4) {
					zoom2d = arg1.g2();
				} else if (var3 == 5) {
					xan2d = arg1.g2();
				} else if (var3 == 6) {
					yan2d = arg1.g2();
				} else if (var3 == 7) {
					xof2d = arg1.g2();
					if (xof2d > 32767) {
						xof2d -= 65536;
					}
				} else if (var3 == 8) {
					yof2d = arg1.g2();
					if (yof2d > 32767) {
						yof2d -= 65536;
					}
				} else if (var3 == 10) {
					field1052 = arg1.g2();
				} else if (var3 == 11) {
					stackable = true;
				} else if (var3 == 12) {
					cost = arg1.g4();
				} else if (var3 == 16) {
					members = true;
				} else if (var3 == 23) {
					manwear = arg1.g2();
					manwearOffset = arg1.g1b();
				} else if (var3 == 24) {
					manwear2 = arg1.g2();
				} else if (var3 == 25) {
					womanwear = arg1.g2();
					womanwearOffset = arg1.g1b();
				} else if (var3 == 26) {
					womanwear2 = arg1.g2();
				} else if (var3 >= 30 && var3 < 35) {
					if (op == null) {
						op = new String[5];
					}
					op[var3 - 30] = arg1.gjstr();
					if (op[var3 - 30].equalsIgnoreCase("hidden")) {
						op[var3 - 30] = null;
					}
				} else if (var3 >= 35 && var3 < 40) {
					if (iop == null) {
						iop = new String[5];
					}
					iop[var3 - 35] = arg1.gjstr();
				} else if (var3 == 40) {
					int var4 = arg1.g1();
					recol_s = new int[var4];
					recol_d = new int[var4];
					for (int var5 = 0; var5 < var4; var5++) {
						recol_s[var5] = arg1.g2();
						recol_d[var5] = arg1.g2();
					}
				} else if (var3 == 78) {
					manwear3 = arg1.g2();
				} else if (var3 == 79) {
					womanwear3 = arg1.g2();
				} else if (var3 == 90) {
					manhead = arg1.g2();
				} else if (var3 == 91) {
					womanhead = arg1.g2();
				} else if (var3 == 92) {
					manhead2 = arg1.g2();
				} else if (var3 == 93) {
					womanhead2 = arg1.g2();
				} else if (var3 == 95) {
					zan2d = arg1.g2();
				} else if (var3 == 97) {
					certlink = arg1.g2();
				} else if (var3 == 98) {
					certtemplate = arg1.g2();
				} else if (var3 >= 100 && var3 < 110) {
					if (countobj == null) {
						countobj = new int[10];
						countco = new int[10];
					}
					countobj[var3 - 100] = arg1.g2();
					countco[var3 - 100] = arg1.g2();
				} else if (var3 == 110) {
					resizex = arg1.g2();
				} else if (var3 == 111) {
					resizey = arg1.g2();
				} else if (var3 == 112) {
					resizez = arg1.g2();
				} else if (var3 == 113) {
					ambient = arg1.g1b();
				} else if (var3 == 114) {
					contrast = arg1.g1b() * 5;
				}
			}
		}
	}

	@ObfuscatedName("hc.a(Z)V")
	public void genCert() {
		ObjType var2 = list(certtemplate);
		model = var2.model;
		zoom2d = var2.zoom2d;
		xan2d = var2.xan2d;
		yan2d = var2.yan2d;
		zan2d = var2.zan2d;
		xof2d = var2.xof2d;
		yof2d = var2.yof2d;
		recol_s = var2.recol_s;
		recol_d = var2.recol_d;
		ObjType var3 = list(certlink);
		name = var3.name;
		members = var3.members;
		cost = var3.cost;
		String var4 = "a";
		char var5 = var3.name.charAt(0);
		if (var5 == 'A' || var5 == 'E' || var5 == 'I' || var5 == 'O' || var5 == 'U') {
			var4 = "an";
		}
		desc = ("Swap this note at any bank for " + var4 + " " + var3.name + ".").getBytes();
		stackable = true;
	}

	@ObfuscatedName("hc.b(I)Leb;")
	public Model getModelLit(int arg0) {
		if (countobj != null && arg0 > 1) {
			int var2 = -1;
			for (int var3 = 0; var3 < 10; var3++) {
				if (arg0 >= countco[var3] && countco[var3] != 0) {
					var2 = countobj[var3];
				}
			}
			if (var2 != -1) {
				return list(var2).getModelLit(1);
			}
		}
		Model var4 = (Model) modelcache.find((long) id);
		if (var4 != null) {
			return var4;
		}
		Model var5 = Model.load(model);
		if (var5 == null) {
			return null;
		}
		if (resizex != 128 || resizey != 128 || resizez != 128) {
			var5.resize(resizez, resizex, resizey);
		}
		if (recol_s != null) {
			for (int var6 = 0; var6 < recol_s.length; var6++) {
				var5.recolour(recol_s[var6], recol_d[var6]);
			}
		}
		var5.calculateNormals(ambient + 64, contrast + 768, -50, -10, -50, true);
		var5.useAABBMouseCheck = true;
		modelcache.put((long) id, var5);
		return var5;
	}

	@ObfuscatedName("hc.a(BI)Leb;")
	public Model getModelUnlit(int arg1) {
		if (countobj != null && arg1 > 1) {
			int var3 = -1;
			for (int var4 = 0; var4 < 10; var4++) {
				if (arg1 >= countco[var4] && countco[var4] != 0) {
					var3 = countobj[var4];
				}
			}
			if (var3 != -1) {
				return list(var3).getModelUnlit(1);
			}
		}
		Model var5 = Model.load(model);
		if (var5 == null) {
			return null;
		} else {
			if (recol_s != null) {
				for (int var6 = 0; var6 < recol_s.length; var6++) {
					var5.recolour(recol_s[var6], recol_d[var6]);
				}
			}
			return var5;
		}
	}

	@ObfuscatedName("hc.a(II)Z")
	public boolean checkWearModel(int arg0) {
		int var3 = manwear;
		int var4 = manwear2;
		int var5 = manwear3;
		if (arg0 == 1) {
			var3 = womanwear;
			var4 = womanwear2;
			var5 = womanwear3;
		}
		if (var3 == -1) {
			return true;
		}
		boolean var6 = true;
		if (!Model.requestDownload(var3)) {
			var6 = false;
		}
		if (var4 != -1 && !Model.requestDownload(var4)) {
			var6 = false;
		}
		if (var5 != -1 && !Model.requestDownload(var5)) {
			var6 = false;
		}
		return var6;
	}

	@ObfuscatedName("hc.b(II)Leb;")
	public Model getWearModelNoCheck(int arg1) {
		int var3 = manwear;
		int var4 = manwear2;
		int var5 = manwear3;
		if (arg1 == 1) {
			var3 = womanwear;
			var4 = womanwear2;
			var5 = womanwear3;
		}
		if (var3 == -1) {
			return null;
		}
		Model var6 = Model.load(var3);
		if (var4 != -1) {
			if (var5 == -1) {
				Model var10 = Model.load(var4);
				Model[] var11 = new Model[] { var6, var10 };
				var6 = new Model(2, var11);
			} else {
				Model var7 = Model.load(var4);
				Model var8 = Model.load(var5);
				Model[] var9 = new Model[] { var6, var7, var8 };
				var6 = new Model(3, var9);
			}
		}
		if (arg1 == 0 && manwearOffset != 0) {
			var6.translate(0, manwearOffset, 0);
		}
		if (arg1 == 1 && womanwearOffset != 0) {
			var6.translate(0, womanwearOffset, 0);
		}
		if (recol_s != null) {
			for (int var12 = 0; var12 < recol_s.length; var12++) {
				var6.recolour(recol_s[var12], recol_d[var12]);
			}
		}
		return var6;
	}

	@ObfuscatedName("hc.a(IB)Z")
	public boolean checkHeadModel(int arg0) {
		int var3 = manhead;
		int var4 = manhead2;
		if (arg0 == 1) {
			var3 = womanhead;
			var4 = womanhead2;
		}
		if (var3 == -1) {
			return true;
		}
		boolean var5 = true;
		if (!Model.requestDownload(var3)) {
			var5 = false;
		}
		if (var4 != -1 && !Model.requestDownload(var4)) {
			var5 = false;
		}
		return var5;
	}

	@ObfuscatedName("hc.a(ZI)Leb;")
	public Model getHeadModelNoCheck(int arg1) {
		int var3 = manhead;
		int var4 = manhead2;
		if (arg1 == 1) {
			var3 = womanhead;
			var4 = womanhead2;
		}
		if (var3 == -1) {
			return null;
		}
		Model var5 = Model.load(var3);
		if (var4 != -1) {
			Model var6 = Model.load(var4);
			Model[] var7 = new Model[] { var5, var6 };
			var5 = new Model(2, var7);
		}
		if (recol_s != null) {
			for (int var8 = 0; var8 < recol_s.length; var8++) {
				var5.recolour(recol_s[var8], recol_d[var8]);
			}
		}
		return var5;
	}
}
