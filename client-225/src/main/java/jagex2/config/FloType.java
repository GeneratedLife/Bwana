package jagex2.config;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("fc")
public class FloType {

	@ObfuscatedName("fc.a")
	public static int field931 = 473;

	@ObfuscatedName("fc.b")
	public static int field932 = -546;

	@ObfuscatedName("fc.c")
	public static int count;

	@ObfuscatedName("fc.d")
	public static FloType[] instances;

	@ObfuscatedName("fc.e")
	public int rgb;

	@ObfuscatedName("fc.f")
	public int texture = -1;

	@ObfuscatedName("fc.g")
	public boolean overlay = false;

	@ObfuscatedName("fc.h")
	public boolean occlude = true;

	@ObfuscatedName("fc.i")
	public String name;

	@ObfuscatedName("fc.j")
	public int hue;

	@ObfuscatedName("fc.k")
	public int saturation;

	@ObfuscatedName("fc.l")
	public int lightness;

	@ObfuscatedName("fc.m")
	public int chroma;

	@ObfuscatedName("fc.n")
	public int luminance;

	@ObfuscatedName("fc.o")
	public int hsl;

	@ObfuscatedName("fc.a(Lub;I)V")
	public static void unpack(Jagfile arg0, int arg1) {
		Packet var2 = new Packet(363, arg0.read("flo.dat", null, (byte) 2));
		count = var2.g2();
		int var4 = 35 / arg1;
		if (instances == null) {
			instances = new FloType[count];
		}
		for (int var3 = 0; var3 < count; var3++) {
			if (instances[var3] == null) {
				instances[var3] = new FloType();
			}
			instances[var3].decode(false, var2);
		}
	}

	@ObfuscatedName("fc.a(ZLkb;)V")
	public void decode(boolean arg0, Packet arg1) {
		if (arg0) {
			throw new NullPointerException();
		}
		while (true) {
			int var3 = arg1.g1();
			if (var3 == 0) {
				return;
			}
			if (var3 == 1) {
				this.rgb = arg1.g3();
				this.setColor(field932, this.rgb);
			} else if (var3 == 2) {
				this.texture = arg1.g1();
			} else if (var3 == 3) {
				this.overlay = true;
			} else if (var3 == 5) {
				this.occlude = false;
			} else if (var3 == 6) {
				this.name = arg1.gjstr();
			} else {
				System.out.println("Error unrecognised config code: " + var3);
			}
		}
	}

	@ObfuscatedName("fc.a(II)V")
	public void setColor(int arg0, int arg1) {
		double var3 = (double) (arg1 >> 16 & 0xFF) / 256.0D;
		if (arg0 >= 0) {
			for (int var5 = 1; var5 > 0; var5++) {
			}
		}
		double var22 = (double) (arg1 >> 8 & 0xFF) / 256.0D;
		double var7 = (double) (arg1 & 0xFF) / 256.0D;
		double var9 = var3;
		if (var22 < var3) {
			var9 = var22;
		}
		if (var7 < var9) {
			var9 = var7;
		}
		double var11 = var3;
		if (var22 > var3) {
			var11 = var22;
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
				var13 = (var22 - var7) / (var11 - var9);
			} else if (var22 == var11) {
				var13 = (var7 - var3) / (var11 - var9) + 2.0D;
			} else if (var7 == var11) {
				var13 = (var3 - var22) / (var11 - var9) + 4.0D;
			}
		}
		var13 /= 6.0D;
		this.hue = (int) (var13 * 256.0D);
		this.saturation = (int) (var15 * 256.0D);
		this.lightness = (int) (var17 * 256.0D);
		if (this.saturation < 0) {
			this.saturation = 0;
		} else if (this.saturation > 255) {
			this.saturation = 255;
		}
		if (this.lightness < 0) {
			this.lightness = 0;
		} else if (this.lightness > 255) {
			this.lightness = 255;
		}
		if (var17 > 0.5D) {
			this.luminance = (int) ((1.0D - var17) * var15 * 512.0D);
		} else {
			this.luminance = (int) (var17 * var15 * 512.0D);
		}
		if (this.luminance < 1) {
			this.luminance = 1;
		}
		this.chroma = (int) (var13 * (double) this.luminance);
		int var19 = this.hue + (int) (Math.random() * 16.0D) - 8;
		if (var19 < 0) {
			var19 = 0;
		} else if (var19 > 255) {
			var19 = 255;
		}
		int var20 = this.saturation + (int) (Math.random() * 48.0D) - 24;
		if (var20 < 0) {
			var20 = 0;
		} else if (var20 > 255) {
			var20 = 255;
		}
		int var21 = this.lightness + (int) (Math.random() * 48.0D) - 24;
		if (var21 < 0) {
			var21 = 0;
		} else if (var21 > 255) {
			var21 = 255;
		}
		this.hsl = this.hsl24to16(var19, var20, var21);
	}

	@ObfuscatedName("fc.a(III)I")
	public final int hsl24to16(int arg0, int arg1, int arg2) {
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
