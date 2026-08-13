package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("ib")
public class Pix8 extends Pix2D {

	@ObfuscatedName("ib.v")
	public boolean field584 = false;

	@ObfuscatedName("ib.w")
	public int field585 = 45861;

	@ObfuscatedName("ib.x")
	public byte field586 = 5;

	@ObfuscatedName("ib.y")
	public boolean field587 = true;

	@ObfuscatedName("ib.z")
	public byte[] pixels;

	@ObfuscatedName("ib.F")
	public int cropW;

	@ObfuscatedName("ib.G")
	public int cropH;

	@ObfuscatedName("ib.A")
	public int[] palette;

	@ObfuscatedName("ib.D")
	public int cropX;

	@ObfuscatedName("ib.E")
	public int cropY;

	@ObfuscatedName("ib.B")
	public int width;

	@ObfuscatedName("ib.C")
	public int height;

	public Pix8(Jagfile arg0, String arg1, int arg2) {
		Packet var4 = new Packet(363, arg0.read(arg1 + ".dat", null, (byte) 2));
		Packet var5 = new Packet(363, arg0.read("index.dat", null, (byte) 2));
		var5.pos = var4.g2();
		this.cropW = var5.g2();
		this.cropH = var5.g2();
		int var6 = var5.g1();
		this.palette = new int[var6];
		for (int var7 = 0; var7 < var6 - 1; var7++) {
			this.palette[var7 + 1] = var5.g3();
		}
		for (int var8 = 0; var8 < arg2; var8++) {
			var5.pos += 2;
			var4.pos += var5.g2() * var5.g2();
			var5.pos++;
		}
		this.cropX = var5.g1();
		this.cropY = var5.g1();
		this.width = var5.g2();
		this.height = var5.g2();
		int var9 = var5.g1();
		int var10 = this.width * this.height;
		this.pixels = new byte[var10];
		int var11;
		if (var9 == 0) {
			for (var11 = 0; var11 < var10; var11++) {
				this.pixels[var11] = var4.g1b();
			}
		} else if (var9 == 1) {
			for (var11 = 0; var11 < this.width; var11++) {
				for (int var12 = 0; var12 < this.height; var12++) {
					this.pixels[var11 + var12 * this.width] = var4.g1b();
				}
			}
		}
	}

	@ObfuscatedName("ib.a(Z)V")
	public void shrink(boolean arg0) {
		this.cropW /= 2;
		this.cropH /= 2;
		byte[] var2 = new byte[this.cropW * this.cropH];
		int var3 = 0;
		for (int var4 = 0; var4 < this.height; var4++) {
			for (int var5 = 0; var5 < this.width; var5++) {
				var2[(var5 + this.cropX >> 1) + (var4 + this.cropY >> 1) * this.cropW] = this.pixels[var3++];
			}
		}
		if (arg0) {
			this.field585 = 197;
		}
		this.pixels = var2;
		this.width = this.cropW;
		this.height = this.cropH;
		this.cropX = 0;
		this.cropY = 0;
	}

	@ObfuscatedName("ib.c(I)V")
	public void crop(int arg0) {
		if (this.width == this.cropW && this.height == this.cropH) {
			return;
		}
		byte[] var2 = new byte[this.cropW * this.cropH];
		int var3 = 0;
		if (arg0 != 0) {
			this.field584 = !this.field584;
		}
		for (int var4 = 0; var4 < this.height; var4++) {
			for (int var5 = 0; var5 < this.width; var5++) {
				var2[var5 + this.cropX + (var4 + this.cropY) * this.cropW] = this.pixels[var3++];
			}
		}
		this.pixels = var2;
		this.width = this.cropW;
		this.height = this.cropH;
		this.cropX = 0;
		this.cropY = 0;
	}

	@ObfuscatedName("ib.d(I)V")
	public void flipHorizontally(int arg0) {
		byte[] var2 = new byte[this.width * this.height];
		int var3 = 0;
		for (int var4 = 0; var4 < this.height; var4++) {
			for (int var5 = this.width - 1; var5 >= 0; var5--) {
				var2[var3++] = this.pixels[var5 + var4 * this.width];
			}
		}
		this.pixels = var2;
		this.cropX = this.cropW - this.width - this.cropX;
		if (arg0 >= 0) {
			;
		}
	}

	@ObfuscatedName("ib.a(B)V")
	public void flipVertically(byte arg0) {
		byte[] var2 = new byte[this.width * this.height];
		int var3;
		if (arg0 != -74) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
		var3 = 0;
		for (int var4 = this.height - 1; var4 >= 0; var4--) {
			for (int var5 = 0; var5 < this.width; var5++) {
				var2[var3++] = this.pixels[var5 + var4 * this.width];
			}
		}
		this.pixels = var2;
		this.cropY = this.cropH - this.height - this.cropY;
	}

	@ObfuscatedName("ib.a(IIIZ)V")
	public void translate(int arg0, int arg1, int arg2, boolean arg3) {
		int var6;
		for (int var5 = 0; var5 < this.palette.length; var5++) {
			var6 = this.palette[var5] >> 16 & 0xFF;
			var6 += arg0;
			if (var6 < 0) {
				var6 = 0;
			} else if (var6 > 255) {
				var6 = 255;
			}
			int var7 = this.palette[var5] >> 8 & 0xFF;
			var7 += arg1;
			if (var7 < 0) {
				var7 = 0;
			} else if (var7 > 255) {
				var7 = 255;
			}
			int var8 = this.palette[var5] & 0xFF;
			var8 += arg2;
			if (var8 < 0) {
				var8 = 0;
			} else if (var8 > 255) {
				var8 = 255;
			}
			this.palette[var5] = (var6 << 16) + (var7 << 8) + var8;
		}
		if (!arg3) {
			for (var6 = 1; var6 > 0; var6++) {
			}
		}
	}

	@ObfuscatedName("ib.a(IIZ)V")
	public void draw(int arg0, int arg1, boolean arg2) {
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
			this.copyPixels(Pix2D.data, var5, var9, this.pixels, var6, 0, var7, var4, var8, this.palette);
			if (!arg2) {
				;
			}
		}
	}

	@ObfuscatedName("ib.a([III[BIIIII[I)V")
	public void copyPixels(int[] arg0, int arg1, int arg2, byte[] arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int[] arg9) {
		int var11 = -(arg6 >> 2);
		int var16 = -(arg6 & 0x3);
		if (arg5 != 0) {
			return;
		}
		for (int var12 = -arg4; var12 < 0; var12++) {
			for (int var13 = var11; var13 < 0; var13++) {
				byte var14 = arg3[arg1++];
				if (var14 == 0) {
					arg7++;
				} else {
					arg0[arg7++] = arg9[var14 & 0xFF];
				}
				var14 = arg3[arg1++];
				if (var14 == 0) {
					arg7++;
				} else {
					arg0[arg7++] = arg9[var14 & 0xFF];
				}
				var14 = arg3[arg1++];
				if (var14 == 0) {
					arg7++;
				} else {
					arg0[arg7++] = arg9[var14 & 0xFF];
				}
				var14 = arg3[arg1++];
				if (var14 == 0) {
					arg7++;
				} else {
					arg0[arg7++] = arg9[var14 & 0xFF];
				}
			}
			for (int var17 = var16; var17 < 0; var17++) {
				byte var15 = arg3[arg1++];
				if (var15 == 0) {
					arg7++;
				} else {
					arg0[arg7++] = arg9[var15 & 0xFF];
				}
			}
			arg7 += arg8;
			arg1 += arg2;
		}
	}
}
