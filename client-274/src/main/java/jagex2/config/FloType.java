package jagex2.config;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("kc")
public final class FloType {

	@ObfuscatedName("kc.c")
	public static int numDefinitions;

	@ObfuscatedName("kc.d")
	public static FloType[] list;

	@ObfuscatedName("kc.e")
	public int colour;

	@ObfuscatedName("kc.f")
	public int texture = -1;

	@ObfuscatedName("kc.g")
	public boolean overlay = false;

	@ObfuscatedName("kc.h")
	public boolean occlude = true;

	@ObfuscatedName("kc.i")
	public String debugname;

	@ObfuscatedName("kc.j")
	public int hue;

	@ObfuscatedName("kc.k")
	public int saturation;

	@ObfuscatedName("kc.l")
	public int lightness;

	@ObfuscatedName("kc.m")
	public int underlayHue;

	@ObfuscatedName("kc.n")
	public int chroma;

	@ObfuscatedName("kc.o")
	public int overlayHsl;

	@ObfuscatedName("kc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("flo.dat", null));
		numDefinitions = var2.g2();
		if (list == null) {
			list = new FloType[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new FloType();
			}
			list[var3].decode(var2);
		}
	}

	@ObfuscatedName("kc.a(BLlb;)V")
	public void decode(Packet arg1) {
		while (true) {
			int var3 = arg1.g1();
			if (var3 == 0) {
				return;
			}
			if (var3 == 1) {
				colour = arg1.g3();
				getHsl(colour);
			} else if (var3 == 2) {
				texture = arg1.g1();
			} else if (var3 == 3) {
				overlay = true;
			} else if (var3 == 5) {
				occlude = false;
			} else if (var3 == 6) {
				debugname = arg1.gjstr();
			} else {
				System.out.println("Error unrecognised config code: " + var3);
			}
		}
	}

	@ObfuscatedName("kc.a(II)V")
	public void getHsl(int arg0) {
		double var3 = (double) (arg0 >> 16 & 0xFF) / 256.0D;
		double var5 = (double) (arg0 >> 8 & 0xFF) / 256.0D;
		double var7 = (double) (arg0 & 0xFF) / 256.0D;
		double var9 = var3;
		if (var5 < var3) {
			var9 = var5;
		}
		if (var7 < var9) {
			var9 = var7;
		}
		double var11 = var3;
		if (var5 > var3) {
			var11 = var5;
		}
		if (var7 > var11) {
			var11 = var7;
		}
		double var13 = 0.0D;
		double var15 = 0.0D;
		double var17 = (var9 + var11) / 2.0D;
		if (var9 != var11) {
			if (var17 < 0.5D) {
				var15 = (var11 - var9) / (var11 + var9);
			}
			if (var17 >= 0.5D) {
				var15 = (var11 - var9) / (2.0D - var11 - var9);
			}
			if (var3 == var11) {
				var13 = (var5 - var7) / (var11 - var9);
			} else if (var5 == var11) {
				var13 = (var7 - var3) / (var11 - var9) + 2.0D;
			} else if (var7 == var11) {
				var13 = (var3 - var5) / (var11 - var9) + 4.0D;
			}
		}
		double var19 = var13 / 6.0D;
		hue = (int) (var19 * 256.0D);
		saturation = (int) (var15 * 256.0D);
		lightness = (int) (var17 * 256.0D);
		if (saturation < 0) {
			saturation = 0;
		} else if (saturation > 255) {
			saturation = 255;
		}
		if (lightness < 0) {
			lightness = 0;
		} else if (lightness > 255) {
			lightness = 255;
		}
		if (var17 > 0.5D) {
			chroma = (int) ((1.0D - var17) * var15 * 512.0D);
		} else {
			chroma = (int) (var17 * var15 * 512.0D);
		}
		if (chroma < 1) {
			chroma = 1;
		}
		underlayHue = (int) (var19 * (double) chroma);
		int var21 = hue + (int) (Math.random() * 16.0D) - 8;
		if (var21 < 0) {
			var21 = 0;
		} else if (var21 > 255) {
			var21 = 255;
		}
		int var22 = saturation + (int) (Math.random() * 48.0D) - 24;
		if (var22 < 0) {
			var22 = 0;
		} else if (var22 > 255) {
			var22 = 255;
		}
		int var23 = lightness + (int) (Math.random() * 48.0D) - 24;
		if (var23 < 0) {
			var23 = 0;
		} else if (var23 > 255) {
			var23 = 255;
		}
		overlayHsl = getTable(var21, var22, var23);
	}

	@ObfuscatedName("kc.a(III)I")
	public int getTable(int arg0, int arg1, int arg2) {
		if (arg2 > 179) {
			arg1 /= 2;
		}
		if (arg2 > 192) {
			arg1 /= 2;
		}
		if (arg2 > 217) {
			arg1 /= 2;
		}
		if (arg2 > 243) {
			arg1 /= 2;
		}
		return (arg0 / 4 << 10) + (arg1 / 32 << 7) + arg2 / 2;
	}
}
