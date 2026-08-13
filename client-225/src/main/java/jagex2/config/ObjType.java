package jagex2.config;

import deob.ObfuscatedName;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.graphics.Pix2D;
import jagex2.graphics.Pix32;
import jagex2.graphics.Pix3D;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("cc")
public class ObjType {

	@ObfuscatedName("cc.a")
	public static int field871;

	@ObfuscatedName("cc.b")
	public boolean field872 = false;

	@ObfuscatedName("cc.c")
	public static int field873 = -192;

	@ObfuscatedName("cc.d")
	public int field874 = -22246;

	@ObfuscatedName("cc.e")
	public static int count;

	@ObfuscatedName("cc.f")
	public static int[] offsets;

	@ObfuscatedName("cc.g")
	public static Packet dat;

	@ObfuscatedName("cc.h")
	public static ObjType[] cache;

	@ObfuscatedName("cc.i")
	public static int cachePos;

	@ObfuscatedName("cc.j")
	public static boolean membersWorld = true;

	@ObfuscatedName("cc.k")
	public int index = -1;

	@ObfuscatedName("cc.l")
	public int model;

	@ObfuscatedName("cc.m")
	public String name;

	@ObfuscatedName("cc.n")
	public byte[] desc;

	@ObfuscatedName("cc.o")
	public int[] recol_s;

	@ObfuscatedName("cc.p")
	public int[] recol_d;

	@ObfuscatedName("cc.q")
	public int zoom2d;

	@ObfuscatedName("cc.r")
	public int xan2d;

	@ObfuscatedName("cc.s")
	public int yan2d;

	@ObfuscatedName("cc.t")
	public int zan2d;

	@ObfuscatedName("cc.u")
	public int xof2d;

	@ObfuscatedName("cc.v")
	public int yof2d;

	@ObfuscatedName("cc.w")
	public boolean code9;

	@ObfuscatedName("cc.x")
	public int code10;

	@ObfuscatedName("cc.y")
	public boolean stackable;

	@ObfuscatedName("cc.z")
	public int cost;

	@ObfuscatedName("cc.T")
	public static LruCache modelCache = new LruCache((byte) 0, 50);

	@ObfuscatedName("cc.U")
	public static LruCache iconCache = new LruCache((byte) 0, 200);

	@ObfuscatedName("cc.F")
	public byte manwearOffsetY;

	@ObfuscatedName("cc.I")
	public byte womanwearOffsetY;

	@ObfuscatedName("cc.D")
	public int manwear;

	@ObfuscatedName("cc.E")
	public int manwear2;

	@ObfuscatedName("cc.G")
	public int womanwear;

	@ObfuscatedName("cc.H")
	public int womanwear2;

	@ObfuscatedName("cc.J")
	public int manwear3;

	@ObfuscatedName("cc.K")
	public int womanwear3;

	@ObfuscatedName("cc.L")
	public int manhead;

	@ObfuscatedName("cc.M")
	public int manhead2;

	@ObfuscatedName("cc.N")
	public int womanhead;

	@ObfuscatedName("cc.O")
	public int womanhead2;

	@ObfuscatedName("cc.R")
	public int certlink;

	@ObfuscatedName("cc.S")
	public int certtemplate;

	@ObfuscatedName("cc.A")
	public boolean members;

	@ObfuscatedName("cc.P")
	public int[] countobj;

	@ObfuscatedName("cc.Q")
	public int[] countco;

	@ObfuscatedName("cc.B")
	public String[] op;

	@ObfuscatedName("cc.C")
	public String[] iop;

	@ObfuscatedName("cc.a(Lub;)V")
	public static final void unpack(Jagfile arg0) {
		dat = new Packet(363, arg0.read("obj.dat", null, (byte) 2));
		Packet var1 = new Packet(363, arg0.read("obj.idx", null, (byte) 2));
		count = var1.g2();
		offsets = new int[count];
		int var2 = 2;
		for (int var3 = 0; var3 < count; var3++) {
			offsets[var3] = var2;
			var2 += var1.g2();
		}
		cache = new ObjType[10];
		for (int var4 = 0; var4 < 10; var4++) {
			cache[var4] = new ObjType();
		}
	}

	@ObfuscatedName("cc.a(Z)V")
	public static final void unload(boolean arg0) {
		modelCache = null;
		iconCache = null;
		offsets = null;
		cache = null;
		dat = null;
		if (!arg0) {
			field871 = -296;
		}
	}

	@ObfuscatedName("cc.a(I)Lcc;")
	public static final ObjType get(int arg0) {
		for (int var1 = 0; var1 < 10; var1++) {
			if (cache[var1].index == arg0) {
				return cache[var1];
			}
		}
		cachePos = (cachePos + 1) % 10;
		ObjType var2 = cache[cachePos];
		dat.pos = offsets[arg0];
		var2.index = arg0;
		var2.reset();
		var2.decode(false, dat);
		if (var2.certtemplate != -1) {
			var2.toCertificate(-856);
		}
		if (!membersWorld && var2.members) {
			var2.name = "Members Object";
			var2.desc = "Login to a members' server to use this object.".getBytes();
			var2.op = null;
			var2.iop = null;
		}
		return var2;
	}

	@ObfuscatedName("cc.a()V")
	public final void reset() {
		this.model = 0;
		this.name = null;
		this.desc = null;
		this.recol_s = null;
		this.recol_d = null;
		this.zoom2d = 2000;
		this.xan2d = 0;
		this.yan2d = 0;
		this.zan2d = 0;
		this.xof2d = 0;
		this.yof2d = 0;
		this.code9 = false;
		this.code10 = -1;
		this.stackable = false;
		this.cost = 1;
		this.members = false;
		this.op = null;
		this.iop = null;
		this.manwear = -1;
		this.manwear2 = -1;
		this.manwearOffsetY = 0;
		this.womanwear = -1;
		this.womanwear2 = -1;
		this.womanwearOffsetY = 0;
		this.manwear3 = -1;
		this.womanwear3 = -1;
		this.manhead = -1;
		this.manhead2 = -1;
		this.womanhead = -1;
		this.womanhead2 = -1;
		this.countobj = null;
		this.countco = null;
		this.certlink = -1;
		this.certtemplate = -1;
	}

	@ObfuscatedName("cc.a(ZLkb;)V")
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
				if (var3 == 1) {
					this.model = arg1.g2();
				} else if (var3 == 2) {
					this.name = arg1.gjstr();
				} else if (var3 == 3) {
					this.desc = arg1.gstrbyte((byte) 31);
				} else if (var3 == 4) {
					this.zoom2d = arg1.g2();
				} else if (var3 == 5) {
					this.xan2d = arg1.g2();
				} else if (var3 == 6) {
					this.yan2d = arg1.g2();
				} else if (var3 == 7) {
					this.xof2d = arg1.g2();
					if (this.xof2d > 32767) {
						this.xof2d -= 65536;
					}
				} else if (var3 == 8) {
					this.yof2d = arg1.g2();
					if (this.yof2d > 32767) {
						this.yof2d -= 65536;
					}
				} else if (var3 == 9) {
					this.code9 = true;
				} else if (var3 == 10) {
					this.code10 = arg1.g2();
				} else if (var3 == 11) {
					this.stackable = true;
				} else if (var3 == 12) {
					this.cost = arg1.g4();
				} else if (var3 == 16) {
					this.members = true;
				} else if (var3 == 23) {
					this.manwear = arg1.g2();
					this.manwearOffsetY = arg1.g1b();
				} else if (var3 == 24) {
					this.manwear2 = arg1.g2();
				} else if (var3 == 25) {
					this.womanwear = arg1.g2();
					this.womanwearOffsetY = arg1.g1b();
				} else if (var3 == 26) {
					this.womanwear2 = arg1.g2();
				} else if (var3 >= 30 && var3 < 35) {
					if (this.op == null) {
						this.op = new String[5];
					}
					this.op[var3 - 30] = arg1.gjstr();
					if (this.op[var3 - 30].equalsIgnoreCase("hidden")) {
						this.op[var3 - 30] = null;
					}
				} else if (var3 >= 35 && var3 < 40) {
					if (this.iop == null) {
						this.iop = new String[5];
					}
					this.iop[var3 - 35] = arg1.gjstr();
				} else if (var3 == 40) {
					int var4 = arg1.g1();
					this.recol_s = new int[var4];
					this.recol_d = new int[var4];
					for (int var5 = 0; var5 < var4; var5++) {
						this.recol_s[var5] = arg1.g2();
						this.recol_d[var5] = arg1.g2();
					}
				} else if (var3 == 78) {
					this.manwear3 = arg1.g2();
				} else if (var3 == 79) {
					this.womanwear3 = arg1.g2();
				} else if (var3 == 90) {
					this.manhead = arg1.g2();
				} else if (var3 == 91) {
					this.womanhead = arg1.g2();
				} else if (var3 == 92) {
					this.manhead2 = arg1.g2();
				} else if (var3 == 93) {
					this.womanhead2 = arg1.g2();
				} else if (var3 == 95) {
					this.zan2d = arg1.g2();
				} else if (var3 == 97) {
					this.certlink = arg1.g2();
				} else if (var3 == 98) {
					this.certtemplate = arg1.g2();
				} else if (var3 >= 100 && var3 < 110) {
					if (this.countobj == null) {
						this.countobj = new int[10];
						this.countco = new int[10];
					}
					this.countobj[var3 - 100] = arg1.g2();
					this.countco[var3 - 100] = arg1.g2();
				}
			}
		}
	}

	@ObfuscatedName("cc.b(I)V")
	public void toCertificate(int arg0) {
		ObjType var2 = get(this.certtemplate);
		this.model = var2.model;
		this.zoom2d = var2.zoom2d;
		this.xan2d = var2.xan2d;
		while (arg0 >= 0) {
			this.field872 = !this.field872;
		}
		this.yan2d = var2.yan2d;
		this.zan2d = var2.zan2d;
		this.xof2d = var2.xof2d;
		this.yof2d = var2.yof2d;
		this.recol_s = var2.recol_s;
		this.recol_d = var2.recol_d;
		ObjType var3 = get(this.certlink);
		this.name = var3.name;
		this.members = var3.members;
		this.cost = var3.cost;
		String var4 = "a";
		char var5 = var3.name.charAt(0);
		if (var5 == 'A' || var5 == 'E' || var5 == 'I' || var5 == 'O' || var5 == 'U') {
			var4 = "an";
		}
		this.desc = ("Swap this note at any bank for " + var4 + " " + var3.name + ".").getBytes();
		this.stackable = true;
	}

	@ObfuscatedName("cc.c(I)Leb;")
	public final Model getInterfaceModel(int arg0) {
		int var3;
		if (this.countobj != null && arg0 > 1) {
			int var2 = -1;
			for (var3 = 0; var3 < 10; var3++) {
				if (arg0 >= this.countco[var3] && this.countco[var3] != 0) {
					var2 = this.countobj[var3];
				}
			}
			if (var2 != -1) {
				return get(var2).getInterfaceModel(1);
			}
		}
		Model var4 = (Model) modelCache.get((long) this.index);
		if (var4 != null) {
			return var4;
		}
		var4 = new Model(false, this.model);
		if (this.recol_s != null) {
			for (var3 = 0; var3 < this.recol_s.length; var3++) {
				var4.recolor(this.recol_s[var3], this.recol_d[var3]);
			}
		}
		var4.calculateNormals(64, 768, -50, -10, -50, true);
		var4.pickable = true;
		modelCache.put(6, (long) this.index, var4);
		return var4;
	}

	@ObfuscatedName("cc.a(III)Lhb;")
	public static final Pix32 getIcon(int arg0, int arg1, int arg2) {
		Pix32 var3 = (Pix32) iconCache.get((long) arg0);
		if (var3 != null && var3.cropH != arg2 && var3.cropH != -1) {
			var3.unlink();
			var3 = null;
		}
		if (var3 != null) {
			return var3;
		}
		ObjType var4 = get(arg0);
		if (var4.countobj == null) {
			arg2 = -1;
		}
		int var5;
		int var6;
		if (arg2 > 1) {
			var5 = -1;
			for (var6 = 0; var6 < 10; var6++) {
				if (arg2 >= var4.countco[var6] && var4.countco[var6] != 0) {
					var5 = var4.countobj[var6];
				}
			}
			if (var5 != -1) {
				var4 = get(var5);
			}
		}
		var3 = new Pix32(32, 32);
		var5 = Pix3D.centerW3D;
		var6 = Pix3D.centerH3D;
		int[] var7 = Pix3D.lineOffset;
		int[] var8 = Pix2D.data;
		int var9 = Pix2D.width2d;
		int var10 = Pix2D.height2d;
		int var11 = Pix2D.boundLeft;
		int var12 = Pix2D.boundRight;
		int var13 = Pix2D.boundTop;
		int var14 = Pix2D.boundBottom;
		Pix3D.jagged = false;
		Pix2D.bind(32, var3.pixels, -657, 32);
		Pix2D.fillRect(0, 0, 0, (byte) 93, 32, 32);
		Pix3D.init2D(field873);
		Model var15 = var4.getInterfaceModel(1);
		int var16 = Pix3D.sinTable[var4.xan2d] * var4.zoom2d >> 16;
		int var17 = Pix3D.cosTable[var4.xan2d] * var4.zoom2d >> 16;
		var15.drawSimple(0, var4.yan2d, var4.zan2d, var4.xan2d, var4.xof2d, var16 + var15.maxY / 2 + var4.yof2d, var17 + var4.yof2d);
		if (arg1 != 24638) {
			throw new NullPointerException();
		}
		for (int var18 = 31; var18 >= 0; var18--) {
			for (var17 = 31; var17 >= 0; var17--) {
				if (var3.pixels[var18 + var17 * 32] == 0) {
					if (var18 > 0 && var3.pixels[var18 - 1 + var17 * 32] > 1) {
						var3.pixels[var18 + var17 * 32] = 1;
					} else if (var17 > 0 && var3.pixels[var18 + (var17 - 1) * 32] > 1) {
						var3.pixels[var18 + var17 * 32] = 1;
					} else if (var18 < 31 && var3.pixels[var18 + 1 + var17 * 32] > 1) {
						var3.pixels[var18 + var17 * 32] = 1;
					} else if (var17 < 31 && var3.pixels[var18 + (var17 + 1) * 32] > 1) {
						var3.pixels[var18 + var17 * 32] = 1;
					}
				}
			}
		}
		for (int var19 = 31; var19 >= 0; var19--) {
			for (var17 = 31; var17 >= 0; var17--) {
				if (var3.pixels[var19 + var17 * 32] == 0 && var19 > 0 && var17 > 0 && var3.pixels[var19 - 1 + (var17 - 1) * 32] > 0) {
					var3.pixels[var19 + var17 * 32] = 3153952;
				}
			}
		}
		if (var4.certtemplate != -1) {
			Pix32 var20 = getIcon(var4.certlink, 24638, 10);
			int var21 = var20.cropW;
			int var22 = var20.cropH;
			var20.cropW = 32;
			var20.cropH = 32;
			var20.crop(22, 5, 22, 17713, 5);
			var20.cropW = var21;
			var20.cropH = var22;
		}
		iconCache.put(6, (long) arg0, var3);
		Pix2D.bind(var9, var8, -657, var10);
		Pix2D.setClipping(var14, var13, var12, 789, var11);
		Pix3D.centerW3D = var5;
		Pix3D.centerH3D = var6;
		Pix3D.lineOffset = var7;
		Pix3D.jagged = true;
		if (var4.stackable) {
			var3.cropW = 33;
		} else {
			var3.cropW = 32;
		}
		var3.cropH = arg2;
		return var3;
	}

	@ObfuscatedName("cc.a(BI)Leb;")
	public final Model getWornModel(byte arg0, int arg1) {
		int var3 = this.manwear;
		if (arg0 != 6) {
			throw new NullPointerException();
		}
		if (arg1 == 1) {
			var3 = this.womanwear;
		}
		if (var3 == -1) {
			return null;
		}
		int var4 = this.manwear2;
		int var5 = this.manwear3;
		if (arg1 == 1) {
			var4 = this.womanwear2;
			var5 = this.womanwear3;
		}
		Model var6 = new Model(false, var3);
		if (var4 != -1) {
			Model var7;
			if (var5 == -1) {
				var7 = new Model(false, var4);
				Model[] var11 = new Model[] { var6, var7 };
				var6 = new Model(0, var11, 2);
			} else {
				var7 = new Model(false, var4);
				Model var8 = new Model(false, var5);
				Model[] var9 = new Model[] { var6, var7, var8 };
				var6 = new Model(0, var9, 3);
			}
		}
		if (arg1 == 0 && this.manwearOffsetY != 0) {
			var6.translate(this.manwearOffsetY, 0, -122, 0);
		}
		if (arg1 == 1 && this.womanwearOffsetY != 0) {
			var6.translate(this.womanwearOffsetY, 0, -122, 0);
		}
		if (this.recol_s != null) {
			for (int var10 = 0; var10 < this.recol_s.length; var10++) {
				var6.recolor(this.recol_s[var10], this.recol_d[var10]);
			}
		}
		return var6;
	}

	@ObfuscatedName("cc.a(II)Leb;")
	public final Model getHeadModel(int arg0, int arg1) {
		int var3 = this.manhead;
		if (arg0 != this.field874) {
			field873 = 205;
		}
		if (arg1 == 1) {
			var3 = this.womanhead;
		}
		if (var3 == -1) {
			return null;
		}
		int var4 = this.manhead2;
		if (arg1 == 1) {
			var4 = this.womanhead2;
		}
		Model var5 = new Model(false, var3);
		if (var4 != -1) {
			Model var6 = new Model(false, var4);
			Model[] var7 = new Model[] { var5, var6 };
			var5 = new Model(0, var7, 2);
		}
		if (this.recol_s != null) {
			for (int var8 = 0; var8 < this.recol_s.length; var8++) {
				var5.recolor(this.recol_s[var8], this.recol_d[var8]);
			}
		}
		return var5;
	}
}
