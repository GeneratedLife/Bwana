package jagex2.config;

import deob.ObfuscatedName;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("bc")
public class NpcType {

	@ObfuscatedName("bc.a")
	public boolean field842 = false;

	@ObfuscatedName("bc.b")
	public static int count;

	@ObfuscatedName("bc.c")
	public static int[] offsets;

	@ObfuscatedName("bc.d")
	public static Packet dat;

	@ObfuscatedName("bc.e")
	public static NpcType[] cache;

	@ObfuscatedName("bc.f")
	public static int cachePos;

	@ObfuscatedName("bc.g")
	public long index = -1L;

	@ObfuscatedName("bc.h")
	public String name;

	@ObfuscatedName("bc.i")
	public byte[] desc;

	@ObfuscatedName("bc.j")
	public byte size = 1;

	@ObfuscatedName("bc.k")
	public int[] models;

	@ObfuscatedName("bc.l")
	public int[] heads;

	@ObfuscatedName("bc.m")
	public int readyanim = -1;

	@ObfuscatedName("bc.n")
	public int walkanim = -1;

	@ObfuscatedName("bc.o")
	public int walkanim_b = -1;

	@ObfuscatedName("bc.p")
	public int walkanim_r = -1;

	@ObfuscatedName("bc.q")
	public int walkanim_l = -1;

	@ObfuscatedName("bc.r")
	public boolean animHasAlpha = false;

	@ObfuscatedName("bc.s")
	public int[] recol_s;

	@ObfuscatedName("bc.t")
	public int[] recol_d;

	@ObfuscatedName("bc.u")
	public String[] op;

	@ObfuscatedName("bc.v")
	public int resizex = -1;

	@ObfuscatedName("bc.w")
	public int resizey = -1;

	@ObfuscatedName("bc.x")
	public int resizez = -1;

	@ObfuscatedName("bc.y")
	public boolean minimap = true;

	@ObfuscatedName("bc.z")
	public int vislevel = -1;

	@ObfuscatedName("bc.A")
	public int resizeh = 128;

	@ObfuscatedName("bc.B")
	public int resizev = 128;

	@ObfuscatedName("bc.C")
	public static LruCache modelCache = new LruCache((byte) 0, 30);

	@ObfuscatedName("bc.a(Lub;)V")
	public static final void unpack(Jagfile arg0) {
		dat = new Packet(363, arg0.read("npc.dat", null, (byte) 2));
		Packet var1 = new Packet(363, arg0.read("npc.idx", null, (byte) 2));
		count = var1.g2();
		offsets = new int[count];
		int var2 = 2;
		for (int var3 = 0; var3 < count; var3++) {
			offsets[var3] = var2;
			var2 += var1.g2();
		}
		cache = new NpcType[20];
		for (int var4 = 0; var4 < 20; var4++) {
			cache[var4] = new NpcType();
		}
	}

	@ObfuscatedName("bc.a(Z)V")
	public static final void unload(boolean arg0) {
		modelCache = null;
		offsets = null;
		cache = null;
		if (!arg0) {
			for (int var1 = 1; var1 > 0; var1++) {
			}
		}
		dat = null;
	}

	@ObfuscatedName("bc.a(I)Lbc;")
	public static final NpcType get(int arg0) {
		for (int var1 = 0; var1 < 20; var1++) {
			if (cache[var1].index == (long) arg0) {
				return cache[var1];
			}
		}
		cachePos = (cachePos + 1) % 20;
		NpcType var2 = cache[cachePos] = new NpcType();
		dat.pos = offsets[arg0];
		var2.index = arg0;
		var2.decode(false, dat);
		return var2;
	}

	@ObfuscatedName("bc.a(ZLkb;)V")
	public final void decode(boolean arg0, Packet arg1) {
		if (arg0) {
			throw new NullPointerException();
		}
		while (true) {
			while (true) {
				int var3 = arg1.g1();
				if (var3 == 0) {
					return;
				}
				int var4;
				int var5;
				if (var3 == 1) {
					var4 = arg1.g1();
					this.models = new int[var4];
					for (var5 = 0; var5 < var4; var5++) {
						this.models[var5] = arg1.g2();
					}
				} else if (var3 == 2) {
					this.name = arg1.gjstr();
				} else if (var3 == 3) {
					this.desc = arg1.gstrbyte((byte) 31);
				} else if (var3 == 12) {
					this.size = arg1.g1b();
				} else if (var3 == 13) {
					this.readyanim = arg1.g2();
				} else if (var3 == 14) {
					this.walkanim = arg1.g2();
				} else if (var3 == 16) {
					this.animHasAlpha = true;
				} else if (var3 == 17) {
					this.walkanim = arg1.g2();
					this.walkanim_b = arg1.g2();
					this.walkanim_r = arg1.g2();
					this.walkanim_l = arg1.g2();
				} else if (var3 >= 30 && var3 < 40) {
					if (this.op == null) {
						this.op = new String[5];
					}
					this.op[var3 - 30] = arg1.gjstr();
					if (this.op[var3 - 30].equalsIgnoreCase("hidden")) {
						this.op[var3 - 30] = null;
					}
				} else if (var3 == 40) {
					var4 = arg1.g1();
					this.recol_s = new int[var4];
					this.recol_d = new int[var4];
					for (var5 = 0; var5 < var4; var5++) {
						this.recol_s[var5] = arg1.g2();
						this.recol_d[var5] = arg1.g2();
					}
				} else if (var3 == 60) {
					var4 = arg1.g1();
					this.heads = new int[var4];
					for (var5 = 0; var5 < var4; var5++) {
						this.heads[var5] = arg1.g2();
					}
				} else if (var3 == 90) {
					this.resizex = arg1.g2();
				} else if (var3 == 91) {
					this.resizey = arg1.g2();
				} else if (var3 == 92) {
					this.resizez = arg1.g2();
				} else if (var3 == 93) {
					this.minimap = false;
				} else if (var3 == 95) {
					this.vislevel = arg1.g2();
				} else if (var3 == 97) {
					this.resizeh = arg1.g2();
				} else if (var3 == 98) {
					this.resizev = arg1.g2();
				}
			}
		}
	}

	@ObfuscatedName("bc.a(II[I)Leb;")
	public final Model getSequencedModel(int arg0, int arg1, int[] arg2) {
		Model var4 = null;
		Model var5 = (Model) modelCache.get(this.index);
		if (var5 == null) {
			Model[] var6 = new Model[this.models.length];
			for (int var7 = 0; var7 < this.models.length; var7++) {
				var6[var7] = new Model(false, this.models[var7]);
			}
			if (var6.length == 1) {
				var5 = var6[0];
			} else {
				var5 = new Model(0, var6, var6.length);
			}
			if (this.recol_s != null) {
				for (int var8 = 0; var8 < this.recol_s.length; var8++) {
					var5.recolor(this.recol_s[var8], this.recol_d[var8]);
				}
			}
			var5.createLabelReferences(4);
			var5.calculateNormals(64, 850, -30, -50, -30, true);
			modelCache.put(6, this.index, var5);
		}
		var4 = new Model(0, var5, !this.animHasAlpha);
		if (arg0 != -1 && arg1 != -1) {
			var4.applyTransforms(arg1, 3, arg0, arg2);
		} else if (arg0 != -1) {
			var4.applyTransform(-16599, arg0);
		}
		if (this.resizeh != 128 || this.resizev != 128) {
			var4.scale(this.resizeh, 2, this.resizev, this.resizeh);
		}
		var4.calculateBoundsCylinder(2992);
		var4.labelFaces = null;
		var4.labelVertices = null;
		if (this.size == 1) {
			var4.pickable = true;
		}
		return var4;
	}

	@ObfuscatedName("bc.b(Z)Leb;")
	public final Model getHeadModel(boolean arg0) {
		if (arg0) {
			throw new NullPointerException();
		} else if (this.heads == null) {
			return null;
		} else {
			Model[] var2 = new Model[this.heads.length];
			for (int var3 = 0; var3 < this.heads.length; var3++) {
				var2[var3] = new Model(false, this.heads[var3]);
			}
			Model var4;
			if (var2.length == 1) {
				var4 = var2[0];
			} else {
				var4 = new Model(0, var2, var2.length);
			}
			if (this.recol_s != null) {
				for (int var5 = 0; var5 < this.recol_s.length; var5++) {
					var4.recolor(this.recol_s[var5], this.recol_d[var5]);
				}
			}
			return var4;
		}
	}
}
