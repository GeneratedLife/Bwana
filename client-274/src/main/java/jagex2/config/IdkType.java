package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.Model;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("lc")
public final class IdkType {

	@ObfuscatedName("lc.c")
	public static int numDefinitions;

	@ObfuscatedName("lc.d")
	public static IdkType[] list;

	@ObfuscatedName("lc.e")
	public int part = -1;

	@ObfuscatedName("lc.f")
	public int[] model;

	@ObfuscatedName("lc.g")
	public final int[] recol_s = new int[6];

	@ObfuscatedName("lc.h")
	public final int[] recol_d = new int[6];

	@ObfuscatedName("lc.i")
	public final int[] head = new int[] { -1, -1, -1, -1, -1 };

	@ObfuscatedName("lc.j")
	public boolean disable = false;

	@ObfuscatedName("lc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("idk.dat", null));
		numDefinitions = var2.g2();
		if (list == null) {
			list = new IdkType[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new IdkType();
			}
			list[var3].decode(var2);
		}
	}

	@ObfuscatedName("lc.a(BLlb;)V")
	public void decode(Packet arg1) {
		while (true) {
			while (true) {
				int var3 = arg1.g1();
				if (var3 == 0) {
					return;
				}
				if (var3 == 1) {
					part = arg1.g1();
				} else if (var3 == 2) {
					int var4 = arg1.g1();
					model = new int[var4];
					for (int var5 = 0; var5 < var4; var5++) {
						model[var5] = arg1.g2();
					}
				} else if (var3 == 3) {
					disable = true;
				} else if (var3 >= 40 && var3 < 50) {
					recol_s[var3 - 40] = arg1.g2();
				} else if (var3 >= 50 && var3 < 60) {
					recol_d[var3 - 50] = arg1.g2();
				} else if (var3 >= 60 && var3 < 70) {
					head[var3 - 60] = arg1.g2();
				} else {
					System.out.println("Error unrecognised config code: " + var3);
				}
			}
		}
	}

	@ObfuscatedName("lc.a(I)Z")
	public boolean checkModel() {
		if (model == null) {
			return true;
		}
		boolean var2 = true;
		for (int var3 = 0; var3 < model.length; var3++) {
			if (!Model.requestDownload(model[var3])) {
				var2 = false;
			}
		}
		return var2;
	}

	@ObfuscatedName("lc.a(B)Leb;")
	public Model getModelNoCheck() {
		if (model == null) {
			return null;
		}
		Model[] var2 = new Model[model.length];
		for (int var3 = 0; var3 < model.length; var3++) {
			var2[var3] = Model.load(model[var3]);
		}
		Model var4;
		if (var2.length == 1) {
			var4 = var2[0];
		} else {
			var4 = new Model(var2.length, var2);
		}
		for (int var5 = 0; var5 < 6 && recol_s[var5] != 0; var5++) {
			var4.recolour(recol_s[var5], recol_d[var5]);
		}
		return var4;
	}

	@ObfuscatedName("lc.b(I)Z")
	public boolean checkHead() {
		boolean var2 = true;
		for (int var3 = 0; var3 < 5; var3++) {
			if (head[var3] != -1 && !Model.requestDownload(head[var3])) {
				var2 = false;
			}
		}
		return var2;
	}

	@ObfuscatedName("lc.b(B)Leb;")
	public Model getHeadNoCheck() {
		Model[] var2 = new Model[5];
		int var3 = 0;
		for (int var4 = 0; var4 < 5; var4++) {
			if (head[var4] != -1) {
				var2[var3++] = Model.load(head[var4]);
			}
		}
		Model var5 = new Model(var3, var2);
		for (int var6 = 0; var6 < 6 && recol_s[var6] != 0; var6++) {
			var5.recolour(recol_s[var6], recol_d[var6]);
		}
		return var5;
	}
}
