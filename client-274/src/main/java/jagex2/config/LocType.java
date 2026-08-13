package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.AnimFrame;
import jagex2.dash3d.Model;
import jagex2.datastruct.LruCache;
import jagex2.io.JagFile;
import jagex2.io.OnDemand;
import jagex2.io.Packet;

@ObfuscatedName("ec")
public final class LocType {

	@ObfuscatedName("ec.f")
	public static int numDefinitions;

	@ObfuscatedName("ec.g")
	public static int[] idx;

	@ObfuscatedName("ec.h")
	public static Packet dat;

	@ObfuscatedName("ec.i")
	public static LocType[] recent;

	@ObfuscatedName("ec.j")
	public static int recentPos;

	@ObfuscatedName("ec.k")
	public static final Model[] temp = new Model[4];

	@ObfuscatedName("ec.l")
	public int id = -1;

	@ObfuscatedName("ec.m")
	public int[] model;

	@ObfuscatedName("ec.n")
	public int[] shape;

	@ObfuscatedName("ec.o")
	public String name;

	@ObfuscatedName("ec.p")
	public byte[] desc;

	@ObfuscatedName("ec.q")
	public int[] recol_s;

	@ObfuscatedName("ec.r")
	public int[] recol_d;

	@ObfuscatedName("ec.s")
	public int width;

	@ObfuscatedName("ec.t")
	public int length;

	@ObfuscatedName("ec.u")
	public boolean blockwalk;

	@ObfuscatedName("ec.v")
	public boolean blockrange;

	@ObfuscatedName("ec.w")
	public boolean active;

	@ObfuscatedName("ec.x")
	public boolean hillskew;

	@ObfuscatedName("ec.y")
	public boolean sharelight;

	@ObfuscatedName("ec.z")
	public boolean occlude;

	@ObfuscatedName("ec.T")
	public static LruCache mc1 = new LruCache(500);

	@ObfuscatedName("ec.U")
	public static LruCache mc2 = new LruCache(30);

	@ObfuscatedName("ec.C")
	public byte ambient;

	@ObfuscatedName("ec.D")
	public byte contrast;

	@ObfuscatedName("ec.A")
	public int anim;

	@ObfuscatedName("ec.B")
	public int wallwidth;

	@ObfuscatedName("ec.F")
	public int mapfunction;

	@ObfuscatedName("ec.G")
	public int mapscene;

	@ObfuscatedName("ec.J")
	public int resizex;

	@ObfuscatedName("ec.K")
	public int resizey;

	@ObfuscatedName("ec.L")
	public int resizez;

	@ObfuscatedName("ec.M")
	public int offsetx;

	@ObfuscatedName("ec.N")
	public int offsety;

	@ObfuscatedName("ec.O")
	public int offsetz;

	@ObfuscatedName("ec.P")
	public int forceapproach;

	@ObfuscatedName("ec.S")
	public int raiseobject;

	@ObfuscatedName("ec.H")
	public boolean mirror;

	@ObfuscatedName("ec.I")
	public boolean shadow;

	@ObfuscatedName("ec.Q")
	public boolean forcedecor;

	@ObfuscatedName("ec.R")
	public boolean breakroutefinding;

	@ObfuscatedName("ec.E")
	public String[] op;

	@ObfuscatedName("ec.a(Lxb;)V")
	public static void init(JagFile arg0) {
		dat = new Packet(arg0.read("loc.dat", null));
		Packet var1 = new Packet(arg0.read("loc.idx", null));
		numDefinitions = var1.g2();
		idx = new int[numDefinitions];
		int var2 = 2;
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			idx[var3] = var2;
			var2 += var1.g2();
		}
		recent = new LocType[10];
		for (int var4 = 0; var4 < 10; var4++) {
			recent[var4] = new LocType();
		}
	}

	@ObfuscatedName("ec.a(B)V")
	public static void unload() {
		mc1 = null;
		mc2 = null;
		idx = null;
		recent = null;
		dat = null;
	}

	@ObfuscatedName("ec.a(I)Lec;")
	public static LocType list(int arg0) {
		for (int var1 = 0; var1 < 10; var1++) {
			if (recent[var1].id == arg0) {
				return recent[var1];
			}
		}
		recentPos = (recentPos + 1) % 10;
		LocType var2 = recent[recentPos];
		dat.data = idx[arg0];
		var2.id = arg0;
		var2.reset();
		var2.decode(dat);
		return var2;
	}

	@ObfuscatedName("ec.a()V")
	public void reset() {
		model = null;
		shape = null;
		name = null;
		desc = null;
		recol_s = null;
		recol_d = null;
		width = 1;
		length = 1;
		blockwalk = true;
		blockrange = true;
		active = false;
		hillskew = false;
		sharelight = false;
		occlude = false;
		anim = -1;
		wallwidth = 16;
		ambient = 0;
		contrast = 0;
		op = null;
		mapfunction = -1;
		mapscene = -1;
		mirror = false;
		shadow = true;
		resizex = 128;
		resizey = 128;
		resizez = 128;
		forceapproach = 0;
		offsetx = 0;
		offsety = 0;
		offsetz = 0;
		forcedecor = false;
		breakroutefinding = false;
		raiseobject = -1;
	}

	@ObfuscatedName("ec.a(BLlb;)V")
	public void decode(Packet arg1) {
		int var3 = -1;
		while (true) {
			int var5;
			do {
				while (true) {
					int var4 = arg1.g1();
					if (var4 == 0) {
						if (var3 == -1) {
							active = false;
							if (model != null && (shape == null || shape[0] == 10)) {
								active = true;
							}
							if (op != null) {
								active = true;
							}
						}
						if (breakroutefinding) {
							blockwalk = false;
							blockrange = false;
						}
						if (raiseobject == -1) {
							raiseobject = blockwalk ? 1 : 0;
							return;
						}
						return;
					}
					if (var4 == 1) {
						var5 = arg1.g1();
						break;
					}
					if (var4 == 2) {
						name = arg1.gjstr();
					} else if (var4 == 3) {
						desc = arg1.gjstrraw();
					} else if (var4 == 5) {
						int var7 = arg1.g1();
						if (var7 > 0) {
							shape = null;
							model = new int[var7];
							for (int var8 = 0; var8 < var7; var8++) {
								model[var8] = arg1.g2();
							}
						}
					} else if (var4 == 14) {
						width = arg1.g1();
					} else if (var4 == 15) {
						length = arg1.g1();
					} else if (var4 == 17) {
						blockwalk = false;
					} else if (var4 == 18) {
						blockrange = false;
					} else if (var4 == 19) {
						var3 = arg1.g1();
						if (var3 == 1) {
							active = true;
						}
					} else if (var4 == 21) {
						hillskew = true;
					} else if (var4 == 22) {
						sharelight = true;
					} else if (var4 == 23) {
						occlude = true;
					} else if (var4 == 24) {
						anim = arg1.g2();
						if (anim == 65535) {
							anim = -1;
						}
					} else if (var4 == 28) {
						wallwidth = arg1.g1();
					} else if (var4 == 29) {
						ambient = arg1.g1b();
					} else if (var4 == 39) {
						contrast = arg1.g1b();
					} else if (var4 >= 30 && var4 < 39) {
						if (op == null) {
							op = new String[5];
						}
						op[var4 - 30] = arg1.gjstr();
						if (op[var4 - 30].equalsIgnoreCase("hidden")) {
							op[var4 - 30] = null;
						}
					} else if (var4 == 40) {
						int var9 = arg1.g1();
						recol_s = new int[var9];
						recol_d = new int[var9];
						for (int var10 = 0; var10 < var9; var10++) {
							recol_s[var10] = arg1.g2();
							recol_d[var10] = arg1.g2();
						}
					} else if (var4 == 60) {
						mapfunction = arg1.g2();
					} else if (var4 == 62) {
						mirror = true;
					} else if (var4 == 64) {
						shadow = false;
					} else if (var4 == 65) {
						resizex = arg1.g2();
					} else if (var4 == 66) {
						resizey = arg1.g2();
					} else if (var4 == 67) {
						resizez = arg1.g2();
					} else if (var4 == 68) {
						mapscene = arg1.g2();
					} else if (var4 == 69) {
						forceapproach = arg1.g1();
					} else if (var4 == 70) {
						offsetx = arg1.g2b();
					} else if (var4 == 71) {
						offsety = arg1.g2b();
					} else if (var4 == 72) {
						offsetz = arg1.g2b();
					} else if (var4 == 73) {
						forcedecor = true;
					} else if (var4 == 74) {
						breakroutefinding = true;
					} else if (var4 == 75) {
						raiseobject = arg1.g1();
					}
				}
			} while (var5 <= 0);
			shape = new int[var5];
			model = new int[var5];
			for (int var6 = 0; var6 < var5; var6++) {
				model[var6] = arg1.g2();
				shape[var6] = arg1.g1();
			}
		}
	}

	@ObfuscatedName("ec.a(II)Z")
	public boolean checkModel(int arg1) {
		if (shape != null) {
			for (int var6 = 0; var6 < shape.length; var6++) {
				if (shape[var6] == arg1) {
					return Model.requestDownload(model[var6] & 0xFFFF);
				}
			}
			return true;
		} else if (model == null) {
			return true;
		} else if (arg1 == 10) {
			boolean var4 = true;
			for (int var5 = 0; var5 < model.length; var5++) {
				var4 &= Model.requestDownload(model[var5] & 0xFFFF);
			}
			return var4;
		} else {
			return true;
		}
	}

	@ObfuscatedName("ec.a(Z)Z")
	public boolean checkModelAll() {
		if (model == null) {
			return true;
		}
		boolean var2 = true;
		for (int var3 = 0; var3 < model.length; var3++) {
			var2 &= Model.requestDownload(model[var3] & 0xFFFF);
		}
		return var2;
	}

	@ObfuscatedName("ec.a(BLub;)V")
	public void prefetchModelAll(OnDemand arg1) {
		if (model == null) {
			return;
		}
		for (int var3 = 0; var3 < model.length; var3++) {
			arg1.prefetch(0, model[var3] & 0xFFFF);
		}
	}

	@ObfuscatedName("ec.a(IIIIIII)Leb;")
	public Model getModel(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		Model var8 = buildModel(arg1, arg6, arg0);
		if (var8 == null) {
			return null;
		}
		if (hillskew || sharelight) {
			var8 = new Model(sharelight, var8, hillskew);
		}
		if (hillskew) {
			int var9 = (arg2 + arg3 + arg4 + arg5) / 4;
			for (int var10 = 0; var10 < var8.numPoints; var10++) {
				int var11 = var8.pointX[var10];
				int var12 = var8.pointZ[var10];
				int var13 = arg2 + (arg3 - arg2) * (var11 + 64) / 128;
				int var14 = arg5 + (arg4 - arg5) * (var11 + 64) / 128;
				int var15 = var13 + (var14 - var13) * (var12 + 64) / 128;
				var8.pointY[var10] += var15 - var9;
			}
			var8.recalcBoundingCylinder();
		}
		return var8;
	}

	@ObfuscatedName("ec.a(IZII)Leb;")
	public Model buildModel(int arg0, int arg2, int arg3) {
		Model var5 = null;
		long var6;
		if (shape == null) {
			if (arg3 != 10) {
				return null;
			}
			var6 = (long) ((id << 6) + arg0) + ((long) (arg2 + 1) << 32);
			Model var8 = (Model) mc2.find(var6);
			if (var8 != null) {
				return var8;
			}
			if (model == null) {
				return null;
			}
			boolean var9 = mirror ^ arg0 > 3;
			int var10 = model.length;
			for (int var11 = 0; var11 < var10; var11++) {
				int var12 = model[var11];
				if (var9) {
					var12 += 65536;
				}
				var5 = (Model) mc1.find((long) var12);
				if (var5 == null) {
					var5 = Model.load(var12 & 0xFFFF);
					if (var5 == null) {
						return null;
					}
					if (var9) {
						var5.mirror();
					}
					mc1.put((long) var12, var5);
				}
				if (var10 > 1) {
					temp[var11] = var5;
				}
			}
			if (var10 > 1) {
				var5 = new Model(var10, temp);
			}
		} else {
			int var13 = -1;
			for (int var14 = 0; var14 < shape.length; var14++) {
				if (shape[var14] == arg3) {
					var13 = var14;
					break;
				}
			}
			if (var13 == -1) {
				return null;
			}
			var6 = (long) ((id << 6) + (var13 << 3) + arg0) + ((long) (arg2 + 1) << 32);
			Model var15 = (Model) mc2.find(var6);
			if (var15 != null) {
				return var15;
			}
			int var16 = model[var13];
			boolean var17 = mirror ^ arg0 > 3;
			if (var17) {
				var16 += 65536;
			}
			var5 = (Model) mc1.find((long) var16);
			if (var5 == null) {
				var5 = Model.load(var16 & 0xFFFF);
				if (var5 == null) {
					return null;
				}
				if (var17) {
					var5.mirror();
				}
				mc1.put((long) var16, var5);
			}
		}
		boolean var18;
		if (resizex == 128 && resizey == 128 && resizez == 128) {
			var18 = false;
		} else {
			var18 = true;
		}
		boolean var19;
		if (offsetx == 0 && offsety == 0 && offsetz == 0) {
			var19 = false;
		} else {
			var19 = true;
		}
		Model var20 = new Model(recol_s == null, AnimFrame.animateTransparencies(arg2), var5, arg0 == 0 && arg2 == -1 && !var18 && !var19);
		if (arg2 != -1) {
			var20.prepareAnim();
			var20.animate(arg2);
			var20.labelFaces = null;
			var20.labelVertices = null;
		}
		while (arg0-- > 0) {
			var20.rotate90();
		}
		if (recol_s != null) {
			for (int var21 = 0; var21 < recol_s.length; var21++) {
				var20.recolour(recol_s[var21], recol_d[var21]);
			}
		}
		if (var18) {
			var20.resize(resizez, resizex, resizey);
		}
		if (var19) {
			var20.translate(offsetx, offsety, offsetz);
		}
		var20.calculateNormals(ambient + 64, contrast * 5 + 768, -50, -10, -50, !sharelight);
		if (raiseobject == 1) {
			var20.objRaise = var20.minY;
		}
		mc2.put(var6, var20);
		return var20;
	}
}
