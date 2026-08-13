package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;

@ObfuscatedName("hb")
public class Pix32 extends Pix2D {

	@ObfuscatedName("hb.v")
	public boolean field572 = false;

	@ObfuscatedName("hb.w")
	public int field573;

	@ObfuscatedName("hb.x")
	public boolean field574 = false;

	@ObfuscatedName("hb.y")
	public int field575 = 15223;

	@ObfuscatedName("hb.z")
	public byte field576 = 5;

	@ObfuscatedName("hb.A")
	public int[] pixels;

	@ObfuscatedName("hb.F")
	public int cropW;

	@ObfuscatedName("hb.B")
	public int width;

	@ObfuscatedName("hb.G")
	public int cropH;

	@ObfuscatedName("hb.C")
	public int height;

	@ObfuscatedName("hb.E")
	public int cropY;

	@ObfuscatedName("hb.D")
	public int cropX;

	public Pix32(int arg0, int arg1) {
		this.pixels = new int[arg0 * arg1];
		this.width = this.cropW = arg0;
		this.height = this.cropH = arg1;
		this.cropX = this.cropY = 0;
	}

	public Pix32(byte[] arg0, Component arg1) {
		try {
			Image var3 = Toolkit.getDefaultToolkit().createImage(arg0);
			MediaTracker var4 = new MediaTracker(arg1);
			var4.addImage(var3, 0);
			var4.waitForAll();
			this.width = var3.getWidth(arg1);
			this.height = var3.getHeight(arg1);
			this.cropW = this.width;
			this.cropH = this.height;
			this.cropX = 0;
			this.cropY = 0;
			this.pixels = new int[this.width * this.height];
			PixelGrabber var5 = new PixelGrabber(var3, 0, 0, this.width, this.height, this.pixels, 0, this.width);
			var5.grabPixels();
		} catch (Exception var6) {
			System.out.println("Error converting jpg");
		}
	}

	public Pix32(Jagfile arg0, String arg1, int arg2) {
		Packet var4 = new Packet(363, arg0.read(arg1 + ".dat", null, (byte) 2));
		Packet var5 = new Packet(363, arg0.read("index.dat", null, (byte) 2));
		var5.pos = var4.g2();
		this.cropW = var5.g2();
		this.cropH = var5.g2();
		int var6 = var5.g1();
		int[] var7 = new int[var6];
		for (int var8 = 0; var8 < var6 - 1; var8++) {
			var7[var8 + 1] = var5.g3();
			if (var7[var8 + 1] == 0) {
				var7[var8 + 1] = 1;
			}
		}
		for (int var9 = 0; var9 < arg2; var9++) {
			var5.pos += 2;
			var4.pos += var5.g2() * var5.g2();
			var5.pos++;
		}
		this.cropX = var5.g1();
		this.cropY = var5.g1();
		this.width = var5.g2();
		this.height = var5.g2();
		int var10 = var5.g1();
		int var11 = this.width * this.height;
		this.pixels = new int[var11];
		int var12;
		if (var10 == 0) {
			for (var12 = 0; var12 < var11; var12++) {
				this.pixels[var12] = var7[var4.g1()];
			}
		} else if (var10 == 1) {
			for (var12 = 0; var12 < this.width; var12++) {
				for (int var13 = 0; var13 < this.height; var13++) {
					this.pixels[var12 + var13 * this.width] = var7[var4.g1()];
				}
			}
		}
	}

	@ObfuscatedName("hb.a(B)V")
	public void bind(byte arg0) {
		if (arg0 != 62) {
			for (int var2 = 1; var2 > 0; var2++) {
			}
		}
		Pix2D.bind(this.width, this.pixels, -657, this.height);
	}

	@ObfuscatedName("hb.a(IIIZ)V")
	public void translate(int arg0, int arg1, int arg2, boolean arg3) {
		for (int var5 = 0; var5 < this.pixels.length; var5++) {
			int var6 = this.pixels[var5];
			if (var6 != 0) {
				int var7 = var6 >> 16 & 0xFF;
				var7 += arg0;
				if (var7 < 1) {
					var7 = 1;
				} else if (var7 > 255) {
					var7 = 255;
				}
				int var8 = var6 >> 8 & 0xFF;
				var8 += arg1;
				if (var8 < 1) {
					var8 = 1;
				} else if (var8 > 255) {
					var8 = 255;
				}
				int var9 = var6 & 0xFF;
				var9 += arg2;
				if (var9 < 1) {
					var9 = 1;
				} else if (var9 > 255) {
					var9 = 255;
				}
				this.pixels[var5] = (var7 << 16) + (var8 << 8) + var9;
			}
		}
		if (arg3) {
			;
		}
	}

	@ObfuscatedName("hb.a(III)V")
	public void blitOpaque(int arg0, int arg1, int arg2) {
		arg1 += this.cropX;
		arg2 += this.cropY;
		int var4 = arg1 + arg2 * Pix2D.width2d;
		int var5 = 0;
		int var6 = this.height;
		int var7 = this.width;
		int var8 = Pix2D.width2d - var7;
		int var9 = 0;
		int var10;
		if (arg2 < Pix2D.boundTop) {
			var10 = Pix2D.boundTop - arg2;
			var6 -= var10;
			arg2 = Pix2D.boundTop;
			var5 += var10 * var7;
			var4 += var10 * Pix2D.width2d;
		}
		if (arg2 + var6 > Pix2D.boundBottom) {
			var6 -= arg2 + var6 - Pix2D.boundBottom;
		}
		if (arg1 < Pix2D.boundLeft) {
			var10 = Pix2D.boundLeft - arg1;
			var7 -= var10;
			arg1 = Pix2D.boundLeft;
			var5 += var10;
			var4 += var10;
			var9 += var10;
			var8 += var10;
		}
		if (arg1 + var7 > Pix2D.boundRight) {
			var10 = arg1 + var7 - Pix2D.boundRight;
			var7 -= var10;
			var9 += var10;
			var8 += var10;
		}
		if (var7 > 0 && var6 > 0) {
			this.copyPixels(15223, this.pixels, var8, var6, var5, var9, var4, var7, Pix2D.data);
			if (arg0 != 34676) {
				this.field573 = 117;
			}
		}
	}

	@ObfuscatedName("hb.a(I[IIIIIII[I)V")
	public void copyPixels(int arg0, int[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int[] arg8) {
		int var10 = -(arg7 >> 2);
		int var14 = -(arg7 & 0x3);
		int var12;
		for (int var11 = -arg3; var11 < 0; var11++) {
			for (var12 = var10; var12 < 0; var12++) {
				arg8[arg6++] = arg1[arg4++];
				arg8[arg6++] = arg1[arg4++];
				arg8[arg6++] = arg1[arg4++];
				arg8[arg6++] = arg1[arg4++];
			}
			for (int var13 = var14; var13 < 0; var13++) {
				arg8[arg6++] = arg1[arg4++];
			}
			arg6 += arg2;
			arg4 += arg5;
		}
		if (arg0 != this.field575) {
			for (var12 = 1; var12 > 0; var12++) {
			}
		}
	}

	@ObfuscatedName("hb.a(IIZ)V")
	public void draw(int arg0, int arg1, boolean arg2) {
		if (arg2) {
			this.field575 = 32;
		}
		arg1 += this.cropX;
		arg0 += this.cropY;
		int var4 = arg1 + arg0 * Pix2D.width2d;
		int var5 = 0;
		int var6 = this.height;
		int var7 = this.width;
		int var8 = Pix2D.width2d - var7;
		int var9 = 0;
		int var10;
		if (arg0 < Pix2D.boundTop) {
			var10 = Pix2D.boundTop - arg0;
			var6 -= var10;
			arg0 = Pix2D.boundTop;
			var5 += var10 * var7;
			var4 += var10 * Pix2D.width2d;
		}
		if (arg0 + var6 > Pix2D.boundBottom) {
			var6 -= arg0 + var6 - Pix2D.boundBottom;
		}
		if (arg1 < Pix2D.boundLeft) {
			var10 = Pix2D.boundLeft - arg1;
			var7 -= var10;
			arg1 = Pix2D.boundLeft;
			var5 += var10;
			var4 += var10;
			var9 += var10;
			var8 += var10;
		}
		if (arg1 + var7 > Pix2D.boundRight) {
			var10 = arg1 + var7 - Pix2D.boundRight;
			var7 -= var10;
			var9 += var10;
			var8 += var10;
		}
		if (var7 > 0 && var6 > 0) {
			this.copyPixels(Pix2D.data, this.pixels, 0, var5, var4, var7, var6, var8, var9);
		}
	}

	@ObfuscatedName("hb.a([I[IIIIIIII)V")
	public void copyPixels(int[] arg0, int[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		int var10 = -(arg5 >> 2);
		int var15 = -(arg5 & 0x3);
		for (int var11 = -arg6; var11 < 0; var11++) {
			int var14;
			for (int var12 = var10; var12 < 0; var12++) {
				var14 = arg1[arg3++];
				if (var14 == 0) {
					arg4++;
				} else {
					arg0[arg4++] = var14;
				}
				var14 = arg1[arg3++];
				if (var14 == 0) {
					arg4++;
				} else {
					arg0[arg4++] = var14;
				}
				var14 = arg1[arg3++];
				if (var14 == 0) {
					arg4++;
				} else {
					arg0[arg4++] = var14;
				}
				var14 = arg1[arg3++];
				if (var14 == 0) {
					arg4++;
				} else {
					arg0[arg4++] = var14;
				}
			}
			for (int var13 = var15; var13 < 0; var13++) {
				var14 = arg1[arg3++];
				if (var14 == 0) {
					arg4++;
				} else {
					arg0[arg4++] = var14;
				}
			}
			arg4 += arg7;
			arg3 += arg8;
		}
	}

	@ObfuscatedName("hb.d(IIIII)V")
	public void crop(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg3 != 17713) {
			return;
		}
		try {
			int var6 = this.width;
			int var7 = this.height;
			int var8 = 0;
			int var9 = 0;
			int var10 = (var6 << 16) / arg2;
			int var11 = (var7 << 16) / arg0;
			int var12 = this.cropW;
			int var13 = this.cropH;
			int var18 = (var12 << 16) / arg2;
			int var19 = (var13 << 16) / arg0;
			arg4 += (this.cropX * arg2 + var12 - 1) / var12;
			arg1 += (this.cropY * arg0 + var13 - 1) / var13;
			if (this.cropX * arg2 % var12 != 0) {
				var8 = (var12 - this.cropX * arg2 % var12 << 16) / arg2;
			}
			if (this.cropY * arg0 % var13 != 0) {
				var9 = (var13 - this.cropY * arg0 % var13 << 16) / arg0;
			}
			arg2 = arg2 * (this.width - (var8 >> 16)) / var12;
			arg0 = arg0 * (this.height - (var9 >> 16)) / var13;
			int var14 = arg4 + arg1 * Pix2D.width2d;
			int var15 = Pix2D.width2d - arg2;
			int var16;
			if (arg1 < Pix2D.boundTop) {
				var16 = Pix2D.boundTop - arg1;
				arg0 -= var16;
				arg1 = 0;
				var14 += var16 * Pix2D.width2d;
				var9 += var19 * var16;
			}
			if (arg1 + arg0 > Pix2D.boundBottom) {
				arg0 -= arg1 + arg0 - Pix2D.boundBottom;
			}
			if (arg4 < Pix2D.boundLeft) {
				var16 = Pix2D.boundLeft - arg4;
				arg2 -= var16;
				arg4 = 0;
				var14 += var16;
				var8 += var18 * var16;
				var15 += var16;
			}
			if (arg4 + arg2 > Pix2D.boundRight) {
				var16 = arg4 + arg2 - Pix2D.boundRight;
				arg2 -= var16;
				var15 += var16;
			}
			this.scale(var8, var18, Pix2D.data, 0, var19, var9, 0, this.pixels, var15, var14, arg0, var6, arg2);
		} catch (Exception var17) {
			System.out.println("error in sprite clipping routine");
		}
	}

	@ObfuscatedName("hb.a(II[IIIII[IIIIII)V")
	public void scale(int arg0, int arg1, int[] arg2, int arg3, int arg4, int arg5, int arg6, int[] arg7, int arg8, int arg9, int arg10, int arg11, int arg12) {
		int var14;
		if (arg6 != 0) {
			for (var14 = 1; var14 > 0; var14++) {
			}
		}
		try {
			var14 = arg0;
			for (int var15 = -arg10; var15 < 0; var15++) {
				int var16 = (arg5 >> 16) * arg11;
				for (int var17 = -arg12; var17 < 0; var17++) {
					int var19 = arg7[(arg0 >> 16) + var16];
					if (var19 == 0) {
						arg9++;
					} else {
						arg2[arg9++] = var19;
					}
					arg0 += arg1;
				}
				arg5 += arg4;
				arg0 = var14;
				arg9 += arg8;
			}
		} catch (Exception var18) {
			System.out.println("error in plot_scale");
		}
	}

	@ObfuscatedName("hb.a(IIIB)V")
	public void drawAlpha(int arg0, int arg1, int arg2, byte arg3) {
		int var5;
		if (arg3 != -26) {
			for (var5 = 1; var5 > 0; var5++) {
			}
		}
		arg1 += this.cropX;
		arg2 += this.cropY;
		var5 = arg1 + arg2 * Pix2D.width2d;
		int var6 = 0;
		int var7 = this.height;
		int var8 = this.width;
		int var9 = Pix2D.width2d - var8;
		int var10 = 0;
		int var11;
		if (arg2 < Pix2D.boundTop) {
			var11 = Pix2D.boundTop - arg2;
			var7 -= var11;
			arg2 = Pix2D.boundTop;
			var6 += var11 * var8;
			var5 += var11 * Pix2D.width2d;
		}
		if (arg2 + var7 > Pix2D.boundBottom) {
			var7 -= arg2 + var7 - Pix2D.boundBottom;
		}
		if (arg1 < Pix2D.boundLeft) {
			var11 = Pix2D.boundLeft - arg1;
			var8 -= var11;
			arg1 = Pix2D.boundLeft;
			var6 += var11;
			var5 += var11;
			var10 += var11;
			var9 += var11;
		}
		if (arg1 + var8 > Pix2D.boundRight) {
			var11 = arg1 + var8 - Pix2D.boundRight;
			var8 -= var11;
			var10 += var11;
			var9 += var11;
		}
		if (var8 > 0 && var7 > 0) {
			this.copyPixelsAlpha(var5, 0, this.pixels, arg0, var7, Pix2D.data, var6, (byte) 8, var8, var9, var10);
		}
	}

	@ObfuscatedName("hb.a(II[III[IIBIII)V")
	public void copyPixelsAlpha(int arg0, int arg1, int[] arg2, int arg3, int arg4, int[] arg5, int arg6, byte arg7, int arg8, int arg9, int arg10) {
		int var12 = 256 - arg3;
		if (arg7 != 8) {
			this.field572 = !this.field572;
		}
		for (int var13 = -arg4; var13 < 0; var13++) {
			for (int var14 = -arg8; var14 < 0; var14++) {
				int var16 = arg2[arg6++];
				if (var16 == 0) {
					arg0++;
				} else {
					int var15 = arg5[arg0];
					arg5[arg0++] = ((var16 & 0xFF00FF) * arg3 + (var15 & 0xFF00FF) * var12 & 0xFF00FF00) + ((var16 & 0xFF00) * arg3 + (var15 & 0xFF00) * var12 & 0xFF0000) >> 8;
				}
			}
			arg0 += arg9;
			arg6 += arg10;
		}
	}

	@ObfuscatedName("hb.a(II[IIIIIIIZ[I)V")
	public void drawRotatedMasked(int arg0, int arg1, int[] arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, boolean arg9, int[] arg10) {
		if (arg9) {
			this.field574 = !this.field574;
		}
		try {
			int var12 = -arg1 / 2;
			int var13 = -arg3 / 2;
			int var14 = (int) (Math.sin((double) arg0 / 326.11D) * 65536.0D);
			int var15 = (int) (Math.cos((double) arg0 / 326.11D) * 65536.0D);
			int var24 = var14 * arg5 >> 8;
			int var25 = var15 * arg5 >> 8;
			int var16 = (arg6 << 16) + var13 * var24 + var12 * var25;
			int var17 = (arg4 << 16) + (var13 * var25 - var12 * var24);
			int var18 = arg7 + arg8 * Pix2D.width2d;
			for (int var27 = 0; var27 < arg3; var27++) {
				int var19 = arg2[var27];
				int var20 = var18 + var19;
				int var21 = var16 + var25 * var19;
				int var22 = var17 - var24 * var19;
				for (int var26 = -arg10[var27]; var26 < 0; var26++) {
					Pix2D.data[var20++] = this.pixels[(var21 >> 16) + (var22 >> 16) * this.width];
					var21 += var25;
					var22 -= var24;
				}
				var16 += var24;
				var17 += var25;
				var18 += Pix2D.width2d;
			}
		} catch (Exception var23) {
		}
	}

	@ObfuscatedName("hb.a(Lib;IIB)V")
	public void drawMasked(Pix8 arg0, int arg1, int arg2, byte arg3) {
		arg2 += this.cropX;
		arg1 += this.cropY;
		int var5 = arg2 + arg1 * Pix2D.width2d;
		int var6 = 0;
		if (arg3 != -15) {
			return;
		}
		int var7 = this.height;
		int var8 = this.width;
		int var9 = Pix2D.width2d - var8;
		int var10 = 0;
		int var11;
		if (arg1 < Pix2D.boundTop) {
			var11 = Pix2D.boundTop - arg1;
			var7 -= var11;
			arg1 = Pix2D.boundTop;
			var6 += var11 * var8;
			var5 += var11 * Pix2D.width2d;
		}
		if (arg1 + var7 > Pix2D.boundBottom) {
			var7 -= arg1 + var7 - Pix2D.boundBottom;
		}
		if (arg2 < Pix2D.boundLeft) {
			var11 = Pix2D.boundLeft - arg2;
			var8 -= var11;
			arg2 = Pix2D.boundLeft;
			var6 += var11;
			var5 += var11;
			var10 += var11;
			var9 += var11;
		}
		if (arg2 + var8 > Pix2D.boundRight) {
			var11 = arg2 + var8 - Pix2D.boundRight;
			var8 -= var11;
			var10 += var11;
			var9 += var11;
		}
		if (var8 > 0 && var7 > 0) {
			this.copyPixelsMasked(var8, var10, 0, -478, var7, var6, Pix2D.data, this.pixels, var5, arg0.pixels, var9);
		}
	}

	@ObfuscatedName("hb.a(IIIIII[I[II[BI)V")
	public void copyPixelsMasked(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int[] arg6, int[] arg7, int arg8, byte[] arg9, int arg10) {
		if (arg3 >= 0) {
			return;
		}
		int var12 = -(arg0 >> 2);
		int var16 = -(arg0 & 0x3);
		for (int var13 = -arg4; var13 < 0; var13++) {
			int var17;
			for (int var14 = var12; var14 < 0; var14++) {
				var17 = arg7[arg5++];
				if (var17 != 0 && arg9[arg8] == 0) {
					arg6[arg8++] = var17;
				} else {
					arg8++;
				}
				var17 = arg7[arg5++];
				if (var17 != 0 && arg9[arg8] == 0) {
					arg6[arg8++] = var17;
				} else {
					arg8++;
				}
				var17 = arg7[arg5++];
				if (var17 != 0 && arg9[arg8] == 0) {
					arg6[arg8++] = var17;
				} else {
					arg8++;
				}
				var17 = arg7[arg5++];
				if (var17 != 0 && arg9[arg8] == 0) {
					arg6[arg8++] = var17;
				} else {
					arg8++;
				}
			}
			for (int var15 = var16; var15 < 0; var15++) {
				var17 = arg7[arg5++];
				if (var17 != 0 && arg9[arg8] == 0) {
					arg6[arg8++] = var17;
				} else {
					arg8++;
				}
			}
			arg8 += arg10;
			arg5 += arg1;
		}
	}
}
