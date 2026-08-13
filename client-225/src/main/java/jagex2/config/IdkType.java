package jagex2.config;

import deob.ObfuscatedName;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("gc")
public class IdkType {

	@ObfuscatedName("gc.a")
	public static int field946;

	@ObfuscatedName("gc.b")
	public static int field947 = 473;

	@ObfuscatedName("gc.c")
	public boolean field948 = false;

	@ObfuscatedName("gc.d")
	public static int count;

	@ObfuscatedName("gc.e")
	public static IdkType[] instances;

	@ObfuscatedName("gc.f")
	public int type = -1;

	@ObfuscatedName("gc.g")
	public int[] models;

	@ObfuscatedName("gc.h")
	public int[] recol_s = new int[6];

	@ObfuscatedName("gc.i")
	public int[] recol_d = new int[6];

	@ObfuscatedName("gc.j")
	public int[] heads = new int[] { -1, -1, -1, -1, -1 };

	@ObfuscatedName("gc.k")
	public boolean disable = false;

	@ObfuscatedName("gc.a(Lub;I)V")
	public static void unpack(Jagfile arg0, int arg1) {
		Packet var2 = new Packet(363, arg0.read("idk.dat", null, (byte) 2));
		count = var2.g2();
		if (instances == null) {
			instances = new IdkType[count];
		}
		for (int var3 = 0; var3 < count; var3++) {
			if (instances[var3] == null) {
				instances[var3] = new IdkType();
			}
			instances[var3].decode(false, var2);
		}
		int var4 = 87 / arg1;
	}

	@ObfuscatedName("gc.a(ZLkb;)V")
	public void decode(boolean arg0, Packet arg1) {
		if (arg0) {
			field946 = 65;
		}
		while (true) {
			while (true) {
				int var3 = arg1.g1();
				if (var3 == 0) {
					return;
				}
				if (var3 == 1) {
					this.type = arg1.g1();
				} else if (var3 == 2) {
					int var4 = arg1.g1();
					this.models = new int[var4];
					for (int var5 = 0; var5 < var4; var5++) {
						this.models[var5] = arg1.g2();
					}
				} else if (var3 == 3) {
					this.disable = true;
				} else if (var3 >= 40 && var3 < 50) {
					this.recol_s[var3 - 40] = arg1.g2();
				} else if (var3 >= 50 && var3 < 60) {
					this.recol_d[var3 - 50] = arg1.g2();
				} else if (var3 >= 60 && var3 < 70) {
					this.heads[var3 - 60] = arg1.g2();
				} else {
					System.out.println("Error unrecognised config code: " + var3);
				}
			}
		}
	}

	@ObfuscatedName("gc.a()Leb;")
	public Model getModel() {
		if (this.models == null) {
			return null;
		}
		Model[] var1 = new Model[this.models.length];
		for (int var2 = 0; var2 < this.models.length; var2++) {
			var1[var2] = new Model(false, this.models[var2]);
		}
		Model var3;
		if (var1.length == 1) {
			var3 = var1[0];
		} else {
			var3 = new Model(0, var1, var1.length);
		}
		for (int var4 = 0; var4 < 6 && this.recol_s[var4] != 0; var4++) {
			var3.recolor(this.recol_s[var4], this.recol_d[var4]);
		}
		return var3;
	}

	@ObfuscatedName("gc.a(Z)Leb;")
	public Model getHeadModel(boolean arg0) {
		Model[] var2 = new Model[5];
		int var3 = 0;
		for (int var4 = 0; var4 < 5; var4++) {
			if (this.heads[var4] != -1) {
				var2[var3++] = new Model(false, this.heads[var4]);
			}
		}
		Model var5 = new Model(0, var2, var3);
		for (int var6 = 0; var6 < 6 && this.recol_s[var6] != 0; var6++) {
			var5.recolor(this.recol_s[var6], this.recol_d[var6]);
		}
		if (arg0) {
			for (int var7 = 1; var7 > 0; var7++) {
			}
		}
		return var5;
	}
}
