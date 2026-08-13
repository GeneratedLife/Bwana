package jagex2.config;

import deob.ObfuscatedName;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("ac")
public class LocType {

	@ObfuscatedName("ac.a")
	public static int field799;

	@ObfuscatedName("ac.b")
	public static int field800;

	@ObfuscatedName("ac.c")
	public static boolean reset;

	@ObfuscatedName("ac.d")
	public static int count;

	@ObfuscatedName("ac.e")
	public static int[] offsets;

	@ObfuscatedName("ac.f")
	public static Packet dat;

	@ObfuscatedName("ac.g")
	public static LocType[] cache;

	@ObfuscatedName("ac.h")
	public static int cachePos;

	@ObfuscatedName("ac.i")
	public int index = -1;

	@ObfuscatedName("ac.j")
	public int[] models;

	@ObfuscatedName("ac.k")
	public int[] shapes;

	@ObfuscatedName("ac.l")
	public String name;

	@ObfuscatedName("ac.m")
	public byte[] desc;

	@ObfuscatedName("ac.n")
	public int[] recol_s;

	@ObfuscatedName("ac.o")
	public int[] recol_d;

	@ObfuscatedName("ac.p")
	public int width;

	@ObfuscatedName("ac.q")
	public int length;

	@ObfuscatedName("ac.r")
	public boolean blockwalk;

	@ObfuscatedName("ac.s")
	public boolean blockrange;

	@ObfuscatedName("ac.t")
	public boolean active;

	@ObfuscatedName("ac.u")
	public boolean hillskew;

	@ObfuscatedName("ac.v")
	public boolean sharelight;

	@ObfuscatedName("ac.w")
	public boolean occlude;

	@ObfuscatedName("ac.x")
	public int anim;

	@ObfuscatedName("ac.y")
	public int wallwidth;

	@ObfuscatedName("ac.z")
	public byte ambient;

	@ObfuscatedName("ac.P")
	public static LruCache modelCacheStatic = new LruCache((byte) 0, 500);

	@ObfuscatedName("ac.Q")
	public static LruCache modelCacheDynamic = new LruCache((byte) 0, 30);

	@ObfuscatedName("ac.A")
	public byte contrast;

	@ObfuscatedName("ac.D")
	public int mapfunction;

	@ObfuscatedName("ac.E")
	public int mapscene;

	@ObfuscatedName("ac.H")
	public int resizex;

	@ObfuscatedName("ac.I")
	public int resizey;

	@ObfuscatedName("ac.J")
	public int resizez;

	@ObfuscatedName("ac.K")
	public int offsetx;

	@ObfuscatedName("ac.L")
	public int offsety;

	@ObfuscatedName("ac.M")
	public int offsetz;

	@ObfuscatedName("ac.N")
	public int forceapproach;

	@ObfuscatedName("ac.C")
	public boolean animHasAlpha;

	@ObfuscatedName("ac.F")
	public boolean mirror;

	@ObfuscatedName("ac.G")
	public boolean shadow;

	@ObfuscatedName("ac.O")
	public boolean forcedecor;

	@ObfuscatedName("ac.B")
	public String[] op;

	@ObfuscatedName("ac.a(Lub;)V")
	public static final void unpack(Jagfile arg0) {
		dat = new Packet(363, arg0.read("loc.dat", null, (byte) 2));
		Packet var1 = new Packet(363, arg0.read("loc.idx", null, (byte) 2));
		count = var1.g2();
		offsets = new int[count];
		int var2 = 2;
		for (int var3 = 0; var3 < count; var3++) {
			offsets[var3] = var2;
			var2 += var1.g2();
		}
		cache = new LocType[10];
		for (int var4 = 0; var4 < 10; var4++) {
			cache[var4] = new LocType();
		}
	}

	@ObfuscatedName("ac.a(Z)V")
	public static final void unload(boolean arg0) {
		modelCacheStatic = null;
		modelCacheDynamic = null;
		if (arg0) {
			offsets = null;
			cache = null;
			dat = null;
		}
	}

	@ObfuscatedName("ac.a(I)Lac;")
	public static final LocType get(int arg0) {
		for (int var1 = 0; var1 < 10; var1++) {
			if (cache[var1].index == arg0) {
				return cache[var1];
			}
		}
		cachePos = (cachePos + 1) % 10;
		LocType var2 = cache[cachePos];
		dat.pos = offsets[arg0];
		var2.index = arg0;
		var2.reset();
		var2.decode(false, dat);
		return var2;
	}

	@ObfuscatedName("ac.a()V")
	public final void reset() {
		this.models = null;
		this.shapes = null;
		this.name = null;
		this.desc = null;
		this.recol_s = null;
		this.recol_d = null;
		this.width = 1;
		this.length = 1;
		this.blockwalk = true;
		this.blockrange = true;
		this.active = false;
		this.hillskew = false;
		this.sharelight = false;
		this.occlude = false;
		this.anim = -1;
		this.wallwidth = 16;
		this.ambient = 0;
		this.contrast = 0;
		this.op = null;
		this.animHasAlpha = false;
		this.mapfunction = -1;
		this.mapscene = -1;
		this.mirror = false;
		this.shadow = true;
		this.resizex = 128;
		this.resizey = 128;
		this.resizez = 128;
		this.forceapproach = 0;
		this.offsetx = 0;
		this.offsety = 0;
		this.offsetz = 0;
		this.forcedecor = false;
	}

	@ObfuscatedName("ac.a(ZLkb;)V")
	public final void decode(boolean arg0, Packet arg1) {
		int var3;
		if (arg0) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
		var3 = -1;
		while (true) {
			while (true) {
				int var4 = arg1.g1();
				if (var4 == 0) {
					if (this.shapes == null) {
						this.shapes = new int[0];
					}
					if (var3 == -1) {
						this.active = false;
						if (this.shapes.length > 0 && this.shapes[0] == 10) {
							this.active = true;
						}
						if (this.op != null) {
							this.active = true;
							return;
						}
					}
					return;
				}
				int var5;
				int var6;
				if (var4 == 1) {
					var5 = arg1.g1();
					this.shapes = new int[var5];
					this.models = new int[var5];
					for (var6 = 0; var6 < var5; var6++) {
						this.models[var6] = arg1.g2();
						this.shapes[var6] = arg1.g1();
					}
				} else if (var4 == 2) {
					this.name = arg1.gjstr();
				} else if (var4 == 3) {
					this.desc = arg1.gstrbyte((byte) 31);
				} else if (var4 == 14) {
					this.width = arg1.g1();
				} else if (var4 == 15) {
					this.length = arg1.g1();
				} else if (var4 == 17) {
					this.blockwalk = false;
				} else if (var4 == 18) {
					this.blockrange = false;
				} else if (var4 == 19) {
					var3 = arg1.g1();
					if (var3 == 1) {
						this.active = true;
					}
				} else if (var4 == 21) {
					this.hillskew = true;
				} else if (var4 == 22) {
					this.sharelight = true;
				} else if (var4 == 23) {
					this.occlude = true;
				} else if (var4 == 24) {
					this.anim = arg1.g2();
					if (this.anim == 65535) {
						this.anim = -1;
					}
				} else if (var4 == 25) {
					this.animHasAlpha = true;
				} else if (var4 == 28) {
					this.wallwidth = arg1.g1();
				} else if (var4 == 29) {
					this.ambient = arg1.g1b();
				} else if (var4 == 39) {
					this.contrast = arg1.g1b();
				} else if (var4 >= 30 && var4 < 39) {
					if (this.op == null) {
						this.op = new String[5];
					}
					this.op[var4 - 30] = arg1.gjstr();
					if (this.op[var4 - 30].equalsIgnoreCase("hidden")) {
						this.op[var4 - 30] = null;
					}
				} else if (var4 == 40) {
					var5 = arg1.g1();
					this.recol_s = new int[var5];
					this.recol_d = new int[var5];
					for (var6 = 0; var6 < var5; var6++) {
						this.recol_s[var6] = arg1.g2();
						this.recol_d[var6] = arg1.g2();
					}
				} else if (var4 == 60) {
					this.mapfunction = arg1.g2();
				} else if (var4 == 62) {
					this.mirror = true;
				} else if (var4 == 64) {
					this.shadow = false;
				} else if (var4 == 65) {
					this.resizex = arg1.g2();
				} else if (var4 == 66) {
					this.resizey = arg1.g2();
				} else if (var4 == 67) {
					this.resizez = arg1.g2();
				} else if (var4 == 68) {
					this.mapscene = arg1.g2();
				} else if (var4 == 69) {
					this.forceapproach = arg1.g1();
				} else if (var4 == 70) {
					this.offsetx = arg1.g2b();
				} else if (var4 == 71) {
					this.offsety = arg1.g2b();
				} else if (var4 == 72) {
					this.offsetz = arg1.g2b();
				} else if (var4 == 73) {
					this.forcedecor = true;
				}
			}
		}
	}

	@ObfuscatedName("ac.a(IIIIIII)Leb;")
	public final Model getModel(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		int var8 = -1;
		for (int var9 = 0; var9 < this.shapes.length; var9++) {
			if (this.shapes[var9] == arg0) {
				var8 = var9;
				break;
			}
		}
		if (var8 == -1) {
			return null;
		}
		long var10 = (long) ((this.index << 6) + (var8 << 3) + arg1) + ((long) (arg6 + 1) << 32);
		if (reset) {
			var10 = 0L;
		}
		Model var12 = (Model) modelCacheDynamic.get(var10);
		int var13;
		int var19;
		if (var12 == null) {
			if (var8 >= this.models.length) {
				return null;
			}
			var13 = this.models[var8];
			if (var13 == -1) {
				return null;
			}
			boolean var14 = this.mirror ^ arg1 > 3;
			if (var14) {
				var13 += 65536;
			}
			Model var15 = (Model) modelCacheStatic.get((long) var13);
			if (var15 == null) {
				var15 = new Model(false, var13 & 0xFFFF);
				if (var14) {
					var15.rotateY180(-725);
				}
				modelCacheStatic.put(6, (long) var13, var15);
			}
			boolean var16;
			if (this.resizex == 128 && this.resizey == 128 && this.resizez == 128) {
				var16 = false;
			} else {
				var16 = true;
			}
			boolean var17;
			if (this.offsetx == 0 && this.offsety == 0 && this.offsetz == 0) {
				var17 = false;
			} else {
				var17 = true;
			}
			Model var18 = new Model(var15, this.recol_s == null, !this.animHasAlpha, field799, arg1 == 0 && arg6 == -1 && !var16 && !var17);
			if (arg6 != -1) {
				var18.createLabelReferences(4);
				var18.applyTransform(-16599, arg6);
				var18.labelFaces = null;
				var18.labelVertices = null;
			}
			while (arg1-- > 0) {
				var18.rotateY90(0);
			}
			if (this.recol_s != null) {
				for (var19 = 0; var19 < this.recol_s.length; var19++) {
					var18.recolor(this.recol_s[var19], this.recol_d[var19]);
				}
			}
			if (var16) {
				var18.scale(this.resizez, 2, this.resizey, this.resizex);
			}
			if (var17) {
				var18.translate(this.offsety, this.offsetx, -122, this.offsetz);
			}
			var18.calculateNormals(this.ambient + 64, this.contrast * 5 + 768, -50, -10, -50, !this.sharelight);
			if (this.blockwalk) {
				var18.objRaise = var18.maxY;
			}
			modelCacheDynamic.put(6, var10, var18);
			if (this.hillskew || this.sharelight) {
				var18 = new Model(var18, (byte) -31, this.hillskew, this.sharelight);
			}
			if (this.hillskew) {
				var19 = (arg2 + arg3 + arg4 + arg5) / 4;
				for (int var20 = 0; var20 < var18.vertexCount; var20++) {
					int var21 = var18.vertexX[var20];
					int var22 = var18.vertexZ[var20];
					int var23 = arg2 + (arg3 - arg2) * (var21 + 64) / 128;
					int var24 = arg5 + (arg4 - arg5) * (var21 + 64) / 128;
					int var25 = var23 + (var24 - var23) * (var22 + 64) / 128;
					var18.vertexY[var20] += var25 - var19;
				}
				var18.calculateBoundsY(field800);
			}
			return var18;
		} else if (reset) {
			return var12;
		} else {
			if (this.hillskew || this.sharelight) {
				var12 = new Model(var12, (byte) -31, this.hillskew, this.sharelight);
			}
			if (this.hillskew) {
				var13 = (arg2 + arg3 + arg4 + arg5) / 4;
				for (int var26 = 0; var26 < var12.vertexCount; var26++) {
					int var27 = var12.vertexX[var26];
					int var28 = var12.vertexZ[var26];
					int var29 = arg2 + (arg3 - arg2) * (var27 + 64) / 128;
					int var30 = arg5 + (arg4 - arg5) * (var27 + 64) / 128;
					var19 = var29 + (var30 - var29) * (var28 + 64) / 128;
					var12.vertexY[var26] += var19 - var13;
				}
				var12.calculateBoundsY(field800);
			}
			return var12;
		}
	}
}
