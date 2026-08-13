package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("g")
public class AnimFrame {

	@ObfuscatedName("g.a")
	public static AnimFrame[] instances;

	@ObfuscatedName("g.b")
	public int delay;

	@ObfuscatedName("g.c")
	public AnimBase base;

	@ObfuscatedName("g.d")
	public int length;

	@ObfuscatedName("g.e")
	public int[] groups;

	@ObfuscatedName("g.f")
	public int[] x;

	@ObfuscatedName("g.g")
	public int[] y;

	@ObfuscatedName("g.h")
	public int[] z;

	@ObfuscatedName("g.a(ZLub;)V")
	public static void unpack(boolean arg0, Jagfile arg1) {
		if (arg0) {
			throw new NullPointerException();
		}
		Packet var2 = new Packet(363, arg1.read("frame_head.dat", null, (byte) 2));
		Packet var3 = new Packet(363, arg1.read("frame_tran1.dat", null, (byte) 2));
		Packet var4 = new Packet(363, arg1.read("frame_tran2.dat", null, (byte) 2));
		Packet var5 = new Packet(363, arg1.read("frame_del.dat", null, (byte) 2));
		int var6 = var2.g2();
		int var7 = var2.g2();
		instances = new AnimFrame[var7 + 1];
		int[] var8 = new int[500];
		int[] var9 = new int[500];
		int[] var10 = new int[500];
		int[] var11 = new int[500];
		for (int var12 = 0; var12 < var6; var12++) {
			int var13 = var2.g2();
			AnimFrame var14 = instances[var13] = new AnimFrame();
			var14.delay = var5.g1();
			int var15 = var2.g2();
			AnimBase var16 = AnimBase.instances[var15];
			var14.base = var16;
			int var17 = var2.g1();
			int var18 = -1;
			int var19 = 0;
			int var21;
			for (int var20 = 0; var20 < var17; var20++) {
				var21 = var3.g1();
				if (var21 > 0) {
					if (var16.types[var20] != 0) {
						for (int var22 = var20 - 1; var22 > var18; var22--) {
							if (var16.types[var22] == 0) {
								var8[var19] = var22;
								var9[var19] = 0;
								var10[var19] = 0;
								var11[var19] = 0;
								var19++;
								break;
							}
						}
					}
					var8[var19] = var20;
					short var23 = 0;
					if (var16.types[var8[var19]] == 3) {
						var23 = 128;
					}
					if ((var21 & 0x1) == 0) {
						var9[var19] = var23;
					} else {
						var9[var19] = var4.gsmart();
					}
					if ((var21 & 0x2) == 0) {
						var10[var19] = var23;
					} else {
						var10[var19] = var4.gsmart();
					}
					if ((var21 & 0x4) == 0) {
						var11[var19] = var23;
					} else {
						var11[var19] = var4.gsmart();
					}
					var18 = var20;
					var19++;
				}
			}
			var14.length = var19;
			var14.groups = new int[var19];
			var14.x = new int[var19];
			var14.y = new int[var19];
			var14.z = new int[var19];
			for (var21 = 0; var21 < var19; var21++) {
				var14.groups[var21] = var8[var21];
				var14.x[var21] = var9[var21];
				var14.y[var21] = var10[var21];
				var14.z[var21] = var11[var21];
			}
		}
	}
}
