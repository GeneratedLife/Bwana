package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.AnimFrame;
import jagex2.dash3d.Model;
import jagex2.datastruct.LruCache;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("gc")
public final class NpcType {

	@ObfuscatedName("gc.c")
	public static int numDefinitions;

	@ObfuscatedName("gc.d")
	public static int[] idx;

	@ObfuscatedName("gc.e")
	public static Packet dat;

	@ObfuscatedName("gc.f")
	public static NpcType[] recent;

	@ObfuscatedName("gc.g")
	public static int recentPos;

	@ObfuscatedName("gc.h")
	public long id = -1L;

	@ObfuscatedName("gc.i")
	public String name;

	@ObfuscatedName("gc.j")
	public byte[] desc;

	@ObfuscatedName("gc.k")
	public byte size = 1;

	@ObfuscatedName("gc.l")
	public int[] model;

	@ObfuscatedName("gc.m")
	public int[] head;

	@ObfuscatedName("gc.n")
	public int readyanim = -1;

	@ObfuscatedName("gc.o")
	public int walkanim = -1;

	@ObfuscatedName("gc.p")
	public int walkanim_b = -1;

	@ObfuscatedName("gc.q")
	public int walkanim_r = -1;

	@ObfuscatedName("gc.r")
	public int walkanim_l = -1;

	@ObfuscatedName("gc.s")
	public int[] recol_s;

	@ObfuscatedName("gc.t")
	public int[] recol_d;

	@ObfuscatedName("gc.u")
	public String[] op;

	@ObfuscatedName("gc.v")
	public int field1017 = -1;

	@ObfuscatedName("gc.w")
	public int field1018 = -1;

	@ObfuscatedName("gc.x")
	public int field1019 = -1;

	@ObfuscatedName("gc.y")
	public boolean minimap = true;

	@ObfuscatedName("gc.z")
	public int vislevel = -1;

	@ObfuscatedName("gc.H")
	public static LruCache modelCache = new LruCache(30);

	@ObfuscatedName("gc.A")
	public int resizeh = 128;

	@ObfuscatedName("gc.B")
	public int resizev = 128;

	@ObfuscatedName("gc.C")
	public boolean alwaysontop = false;

	@ObfuscatedName("gc.F")
	public int headicon = -1;

	@ObfuscatedName("gc.G")
	public int turnspeed = 32;

	@ObfuscatedName("gc.D")
	public int ambient;

	@ObfuscatedName("gc.E")
	public int contrast;

	@ObfuscatedName("gc.a(Lxb;)V")
	public static void init(JagFile arg0) {
		dat = new Packet(arg0.read("npc.dat", null));
		Packet var1 = new Packet(arg0.read("npc.idx", null));
		numDefinitions = var1.g2();
		idx = new int[numDefinitions];
		int var2 = 2;
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			idx[var3] = var2;
			var2 += var1.g2();
		}
		recent = new NpcType[20];
		for (int var4 = 0; var4 < 20; var4++) {
			recent[var4] = new NpcType();
		}
	}

	@ObfuscatedName("gc.a(B)V")
	public static void unload() {
		modelCache = null;
		idx = null;
		recent = null;
		dat = null;
	}

	@ObfuscatedName("gc.a(I)Lgc;")
	public static NpcType list(int arg0) {
		for (int var1 = 0; var1 < 20; var1++) {
			if (recent[var1].id == (long) arg0) {
				return recent[var1];
			}
		}
		recentPos = (recentPos + 1) % 20;
		NpcType var2 = recent[recentPos] = new NpcType();
		dat.data = idx[arg0];
		var2.id = arg0;
		var2.decode(dat);
		return var2;
	}

	@ObfuscatedName("gc.a(BLlb;)V")
	public void decode(Packet arg1) {
		while (true) {
			while (true) {
				int var4 = arg1.g1();
				if (var4 == 0) {
					return;
				}
				if (var4 == 1) {
					int var5 = arg1.g1();
					model = new int[var5];
					for (int var6 = 0; var6 < var5; var6++) {
						model[var6] = arg1.g2();
					}
				} else if (var4 == 2) {
					name = arg1.gjstr();
				} else if (var4 == 3) {
					desc = arg1.gjstrraw();
				} else if (var4 == 12) {
					size = arg1.g1b();
				} else if (var4 == 13) {
					readyanim = arg1.g2();
				} else if (var4 == 14) {
					walkanim = arg1.g2();
				} else if (var4 == 17) {
					walkanim = arg1.g2();
					walkanim_b = arg1.g2();
					walkanim_r = arg1.g2();
					walkanim_l = arg1.g2();
				} else if (var4 >= 30 && var4 < 40) {
					if (op == null) {
						op = new String[5];
					}
					op[var4 - 30] = arg1.gjstr();
					if (op[var4 - 30].equalsIgnoreCase("hidden")) {
						op[var4 - 30] = null;
					}
				} else if (var4 == 40) {
					int var7 = arg1.g1();
					recol_s = new int[var7];
					recol_d = new int[var7];
					for (int var8 = 0; var8 < var7; var8++) {
						recol_s[var8] = arg1.g2();
						recol_d[var8] = arg1.g2();
					}
				} else if (var4 == 60) {
					int var9 = arg1.g1();
					head = new int[var9];
					for (int var10 = 0; var10 < var9; var10++) {
						head[var10] = arg1.g2();
					}
				} else if (var4 == 90) {
					field1017 = arg1.g2();
				} else if (var4 == 91) {
					field1018 = arg1.g2();
				} else if (var4 == 92) {
					field1019 = arg1.g2();
				} else if (var4 == 93) {
					minimap = false;
				} else if (var4 == 95) {
					vislevel = arg1.g2();
				} else if (var4 == 97) {
					resizeh = arg1.g2();
				} else if (var4 == 98) {
					resizev = arg1.g2();
				} else if (var4 == 99) {
					alwaysontop = true;
				} else if (var4 == 100) {
					ambient = arg1.g1b();
				} else if (var4 == 101) {
					contrast = arg1.g1b() * 5;
				} else if (var4 == 102) {
					headicon = arg1.g2();
				} else if (var4 == 103) {
					turnspeed = arg1.g2();
				}
			}
		}
	}

	@ObfuscatedName("gc.a(III[I)Leb;")
	public Model getTempModel(int arg0, int arg1, int[] arg2) {
		Model var4 = (Model) modelCache.find(id);
		if (var4 == null) {
			boolean var5 = false;
			for (int var6 = 0; var6 < model.length; var6++) {
				if (!Model.requestDownload(model[var6])) {
					var5 = true;
				}
			}
			if (var5) {
				return null;
			}
			Model[] var7 = new Model[model.length];
			for (int var8 = 0; var8 < model.length; var8++) {
				var7[var8] = Model.load(model[var8]);
			}
			if (var7.length == 1) {
				var4 = var7[0];
			} else {
				var4 = new Model(var7.length, var7);
			}
			if (recol_s != null) {
				for (int var9 = 0; var9 < recol_s.length; var9++) {
					var4.recolour(recol_s[var9], recol_d[var9]);
				}
			}
			var4.prepareAnim();
			var4.calculateNormals(ambient + 64, contrast + 850, -30, -50, -30, true);
			modelCache.put(id, var4);
		}
		Model var10 = Model.tempModel;
		var10.set(var4, AnimFrame.animateTransparencies(arg0) & AnimFrame.animateTransparencies(arg1));
		if (arg0 != -1 && arg1 != -1) {
			var10.maskAnimate(arg2, arg0, arg1);
		} else if (arg0 != -1) {
			var10.animate(arg0);
		}
		if (resizeh != 128 || resizev != 128) {
			var10.resize(resizeh, resizeh, resizev);
		}
		var10.calcBoundingCylinder();
		var10.labelFaces = null;
		var10.labelVertices = null;
		if (size == 1) {
			var10.useAABBMouseCheck = true;
		}
		return var10;
	}

	@ObfuscatedName("gc.b(B)Leb;")
	public Model getHead() {
		if (head == null) {
			return null;
		}
		boolean var3 = false;
		for (int var4 = 0; var4 < head.length; var4++) {
			if (!Model.requestDownload(head[var4])) {
				var3 = true;
			}
		}
		if (var3) {
			return null;
		}
		Model[] var5 = new Model[head.length];
		for (int var6 = 0; var6 < head.length; var6++) {
			var5[var6] = Model.load(head[var6]);
		}
		Model var7;
		if (var5.length == 1) {
			var7 = var5[0];
		} else {
			var7 = new Model(var5.length, var5);
		}
		if (recol_s != null) {
			for (int var8 = 0; var8 < recol_s.length; var8++) {
				var7.recolour(recol_s[var8], recol_d[var8]);
			}
		}
		return var7;
	}
}
