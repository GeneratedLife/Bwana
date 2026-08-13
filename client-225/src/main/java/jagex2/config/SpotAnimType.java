package jagex2.config;

import deob.ObfuscatedName;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("kc")
public class SpotAnimType {

	@ObfuscatedName("kc.a")
	public static int field1041 = 473;

	@ObfuscatedName("kc.b")
	public static int count;

	@ObfuscatedName("kc.c")
	public static SpotAnimType[] instances;

	@ObfuscatedName("kc.d")
	public int index;

	@ObfuscatedName("kc.e")
	public int model;

	@ObfuscatedName("kc.f")
	public int anim = -1;

	@ObfuscatedName("kc.g")
	public SeqType seq;

	@ObfuscatedName("kc.h")
	public boolean animHasAlpha = false;

	@ObfuscatedName("kc.i")
	public int[] recol_s = new int[6];

	@ObfuscatedName("kc.j")
	public int[] recol_d = new int[6];

	@ObfuscatedName("kc.k")
	public int resizeh = 128;

	@ObfuscatedName("kc.l")
	public int resizev = 128;

	@ObfuscatedName("kc.m")
	public int orientation;

	@ObfuscatedName("kc.n")
	public int ambient;

	@ObfuscatedName("kc.o")
	public int contrast;

	@ObfuscatedName("kc.p")
	public static LruCache modelCache = new LruCache((byte) 0, 30);

	@ObfuscatedName("kc.a(Lub;I)V")
	public static void unpack(Jagfile arg0, int arg1) {
		int var4 = 91 / arg1;
		Packet var2 = new Packet(363, arg0.read("spotanim.dat", null, (byte) 2));
		count = var2.g2();
		if (instances == null) {
			instances = new SpotAnimType[count];
		}
		for (int var3 = 0; var3 < count; var3++) {
			if (instances[var3] == null) {
				instances[var3] = new SpotAnimType();
			}
			instances[var3].index = var3;
			instances[var3].decode(false, var2);
		}
	}

	@ObfuscatedName("kc.a(ZLkb;)V")
	public void decode(boolean arg0, Packet arg1) {
		int var3;
		if (arg0) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
		while (true) {
			while (true) {
				var3 = arg1.g1();
				if (var3 == 0) {
					return;
				}
				if (var3 == 1) {
					this.model = arg1.g2();
				} else if (var3 == 2) {
					this.anim = arg1.g2();
					if (SeqType.instances != null) {
						this.seq = SeqType.instances[this.anim];
					}
				} else if (var3 == 3) {
					this.animHasAlpha = true;
				} else if (var3 == 4) {
					this.resizeh = arg1.g2();
				} else if (var3 == 5) {
					this.resizev = arg1.g2();
				} else if (var3 == 6) {
					this.orientation = arg1.g2();
				} else if (var3 == 7) {
					this.ambient = arg1.g1();
				} else if (var3 == 8) {
					this.contrast = arg1.g1();
				} else if (var3 >= 40 && var3 < 50) {
					this.recol_s[var3 - 40] = arg1.g2();
				} else if (var3 >= 50 && var3 < 60) {
					this.recol_d[var3 - 50] = arg1.g2();
				} else {
					System.out.println("Error unrecognised spotanim config code: " + var3);
				}
			}
		}
	}

	@ObfuscatedName("kc.a()Leb;")
	public Model getModel() {
		Model var1 = (Model) modelCache.get((long) this.index);
		if (var1 != null) {
			return var1;
		}
		var1 = new Model(false, this.model);
		for (int var2 = 0; var2 < 6; var2++) {
			if (this.recol_s[0] != 0) {
				var1.recolor(this.recol_s[var2], this.recol_d[var2]);
			}
		}
		modelCache.put(6, (long) this.index, var1);
		return var1;
	}
}
