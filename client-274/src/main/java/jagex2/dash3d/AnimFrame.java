package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("g")
public final class AnimFrame {

	@ObfuscatedName("g.a")
	public static AnimFrame[] list;

	@ObfuscatedName("g.b")
	public int delay;

	@ObfuscatedName("g.c")
	public AnimBase base;

	@ObfuscatedName("g.d")
	public int size;

	@ObfuscatedName("g.e")
	public int[] ti;

	@ObfuscatedName("g.f")
	public int[] tx;

	@ObfuscatedName("g.g")
	public int[] ty;

	@ObfuscatedName("g.h")
	public int[] tz;

	@ObfuscatedName("g.i")
	public static boolean[] opaque;

	@ObfuscatedName("g.a(I)V")
	public static void init(int arg0) {
		list = new AnimFrame[arg0 + 1];
		opaque = new boolean[arg0 + 1];
		for (int var1 = 0; var1 < arg0 + 1; var1++) {
			opaque[var1] = true;
		}
	}

	@ObfuscatedName("g.a(I[B)V")
	public static void unpack(byte[] arg1) {
		Packet var2 = new Packet(arg1);
		var2.data = arg1.length - 8;
		int var3 = var2.g2();
		int var4 = var2.g2();
		int var5 = var2.g2();
		int var6 = var2.g2();
		byte var7 = 0;
		Packet var8 = new Packet(arg1);
		var8.data = var7;
		int var9 = var7 + var3 + 2;
		Packet var10 = new Packet(arg1);
		var10.data = var9;
		int var11 = var9 + var4;
		Packet var12 = new Packet(arg1);
		var12.data = var11;
		int var13 = var11 + var5;
		Packet var14 = new Packet(arg1);
		var14.data = var13;
		int var15 = var13 + var6;
		Packet var16 = new Packet(arg1);
		var16.data = var15;
		AnimBase var17 = new AnimBase(var16);
		int var18 = var8.g2();
		int[] var19 = new int[500];
		int[] var20 = new int[500];
		int[] var21 = new int[500];
		int[] var22 = new int[500];
		for (int var23 = 0; var23 < var18; var23++) {
			int var24 = var8.g2();
			AnimFrame var25 = list[var24] = new AnimFrame();
			var25.delay = var14.g1();
			var25.base = var17;
			int var26 = var8.g1();
			int var27 = -1;
			int var28 = 0;
			for (int var29 = 0; var29 < var26; var29++) {
				int var30 = var10.g1();
				if (var30 > 0) {
					if (var17.type[var29] != 0) {
						for (int var31 = var29 - 1; var31 > var27; var31--) {
							if (var17.type[var31] == 0) {
								var19[var28] = var31;
								var20[var28] = 0;
								var21[var28] = 0;
								var22[var28] = 0;
								var28++;
								break;
							}
						}
					}
					var19[var28] = var29;
					short var32 = 0;
					if (var17.type[var29] == 3) {
						var32 = 128;
					}
					if ((var30 & 0x1) == 0) {
						var20[var28] = var32;
					} else {
						var20[var28] = var12.gsmarts();
					}
					if ((var30 & 0x2) == 0) {
						var21[var28] = var32;
					} else {
						var21[var28] = var12.gsmarts();
					}
					if ((var30 & 0x4) == 0) {
						var22[var28] = var32;
					} else {
						var22[var28] = var12.gsmarts();
					}
					var27 = var29;
					var28++;
					if (var17.type[var29] == 5) {
						opaque[var24] = false;
					}
				}
			}
			var25.size = var28;
			var25.ti = new int[var28];
			var25.tx = new int[var28];
			var25.ty = new int[var28];
			var25.tz = new int[var28];
			for (int var33 = 0; var33 < var28; var33++) {
				var25.ti[var33] = var19[var33];
				var25.tx[var33] = var20[var33];
				var25.ty[var33] = var21[var33];
				var25.tz[var33] = var22[var33];
			}
		}
	}

	@ObfuscatedName("g.a(B)V")
	public static void unload() {
		list = null;
	}

	@ObfuscatedName("g.a(II)Lg;")
	public static AnimFrame get(int arg0) {
		return list == null ? null : list[arg0];
	}

	@ObfuscatedName("g.b(II)Z")
	public static boolean animateTransparencies(int arg0) {
		return arg0 == -1;
	}
}
