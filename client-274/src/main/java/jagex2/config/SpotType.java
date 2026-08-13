package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.Model;
import jagex2.datastruct.LruCache;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("pc")
public final class SpotType {

	@ObfuscatedName("pc.b")
	public static int numDefinitions;

	@ObfuscatedName("pc.c")
	public static SpotType[] list;

	@ObfuscatedName("pc.d")
	public int id;

	@ObfuscatedName("pc.e")
	public int model;

	@ObfuscatedName("pc.f")
	public int anim = -1;

	@ObfuscatedName("pc.g")
	public SeqType seq;

	@ObfuscatedName("pc.h")
	public final int[] recol_s = new int[6];

	@ObfuscatedName("pc.i")
	public final int[] recol_d = new int[6];

	@ObfuscatedName("pc.j")
	public int resizeh = 128;

	@ObfuscatedName("pc.k")
	public int resizev = 128;

	@ObfuscatedName("pc.l")
	public int angle;

	@ObfuscatedName("pc.m")
	public int ambient;

	@ObfuscatedName("pc.n")
	public int contrast;

	@ObfuscatedName("pc.o")
	public static LruCache modelCache = new LruCache(30);

	@ObfuscatedName("pc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("spotanim.dat", null));
		numDefinitions = var2.g2();
		if (list == null) {
			list = new SpotType[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new SpotType();
			}
			list[var3].id = var3;
			list[var3].decode(var2);
		}
	}

	@ObfuscatedName("pc.a(BLlb;)V")
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
					anim = arg1.g2();
					if (SeqType.list != null) {
						seq = SeqType.list[anim];
					}
				} else if (var3 == 4) {
					resizeh = arg1.g2();
				} else if (var3 == 5) {
					resizev = arg1.g2();
				} else if (var3 == 6) {
					angle = arg1.g2();
				} else if (var3 == 7) {
					ambient = arg1.g1();
				} else if (var3 == 8) {
					contrast = arg1.g1();
				} else if (var3 >= 40 && var3 < 50) {
					recol_s[var3 - 40] = arg1.g2();
				} else if (var3 >= 50 && var3 < 60) {
					recol_d[var3 - 50] = arg1.g2();
				} else {
					System.out.println("Error unrecognised spotanim config code: " + var3);
				}
			}
		}
	}

	@ObfuscatedName("pc.a()Leb;")
	public Model getTempModel2() {
		Model var1 = (Model) modelCache.find((long) id);
		if (var1 != null) {
			return var1;
		}
		Model var2 = Model.load(model);
		if (var2 == null) {
			return null;
		}
		for (int var3 = 0; var3 < 6; var3++) {
			if (recol_s[0] != 0) {
				var2.recolour(recol_s[var3], recol_d[var3]);
			}
		}
		modelCache.put((long) id, var2);
		return var2;
	}
}
