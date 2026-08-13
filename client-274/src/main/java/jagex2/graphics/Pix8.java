package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("jb")
public final class Pix8 extends Pix2D {

	@ObfuscatedName("jb.J")
	public int owi;

	@ObfuscatedName("jb.K")
	public int ohi;

	@ObfuscatedName("jb.E")
	public final int[] bpal;

	@ObfuscatedName("jb.H")
	public int xof;

	@ObfuscatedName("jb.I")
	public int yof;

	@ObfuscatedName("jb.F")
	public int wi;

	@ObfuscatedName("jb.G")
	public int hi;

	@ObfuscatedName("jb.D")
	public byte[] data;

	public Pix8(JagFile arg0, String arg1, int arg2) {
		Packet var4 = new Packet(arg0.read(arg1 + ".dat", null));
		Packet var5 = new Packet(arg0.read("index.dat", null));
		var5.data = var4.g2();
		owi = var5.g2();
		ohi = var5.g2();
		int var6 = var5.g1();
		bpal = new int[var6];
		for (int var7 = 0; var7 < var6 - 1; var7++) {
			bpal[var7 + 1] = var5.g3();
		}
		for (int var8 = 0; var8 < arg2; var8++) {
			var5.data += 2;
			var4.data += var5.g2() * var5.g2();
			var5.data++;
		}
		xof = var5.g1();
		yof = var5.g1();
		wi = var5.g2();
		hi = var5.g2();
		int var9 = var5.g1();
		int var10 = wi * hi;
		data = new byte[var10];
		if (var9 == 0) {
			for (int var11 = 0; var11 < var10; var11++) {
				data[var11] = var4.g1b();
			}
		} else if (var9 == 1) {
			for (int var12 = 0; var12 < wi; var12++) {
				for (int var13 = 0; var13 < hi; var13++) {
					data[var12 + var13 * wi] = var4.g1b();
				}
			}
		}
	}

	@ObfuscatedName("jb.a(B)V")
	public void halveSize() {
		owi /= 2;
		ohi /= 2;
		byte[] var2 = new byte[owi * ohi];
		int var3 = 0;
		for (int var4 = 0; var4 < hi; var4++) {
			for (int var5 = 0; var5 < wi; var5++) {
				var2[(var5 + xof >> 1) + (var4 + yof >> 1) * owi] = data[var3++];
			}
		}
		data = var2;
		wi = owi;
		hi = ohi;
		xof = 0;
		yof = 0;
	}

	@ObfuscatedName("jb.b(I)V")
	public void trim() {
		if (wi == owi && hi == ohi) {
			return;
		}
		byte[] var2 = new byte[owi * ohi];
		int var3 = 0;
		for (int var4 = 0; var4 < hi; var4++) {
			for (int var5 = 0; var5 < wi; var5++) {
				var2[var5 + xof + (var4 + yof) * owi] = data[var3++];
			}
		}
		data = var2;
		wi = owi;
		hi = ohi;
		xof = 0;
		yof = 0;
	}

	@ObfuscatedName("jb.c(I)V")
	public void hflip() {
		byte[] var2 = new byte[wi * hi];
		int var3 = 0;
		for (int var4 = 0; var4 < hi; var4++) {
			for (int var5 = wi - 1; var5 >= 0; var5--) {
				var2[var3++] = data[var5 + var4 * wi];
			}
		}
		data = var2;
		xof = owi - wi - xof;
	}

	@ObfuscatedName("jb.b(B)V")
	public void vflip() {
		byte[] var2 = new byte[wi * hi];
		int var3 = 0;
		for (int var4 = hi - 1; var4 >= 0; var4--) {
			for (int var5 = 0; var5 < wi; var5++) {
				var2[var3++] = data[var5 + var4 * wi];
			}
		}
		data = var2;
		yof = ohi - hi - yof;
	}

	@ObfuscatedName("jb.a(IIII)V")
	public void rgbAdjust(int arg0, int arg2, int arg3) {
		for (int var5 = 0; var5 < bpal.length; var5++) {
			int var6 = bpal[var5] >> 16 & 0xFF;
			int var7 = var6 + arg2;
			if (var7 < 0) {
				var7 = 0;
			} else if (var7 > 255) {
				var7 = 255;
			}
			int var8 = bpal[var5] >> 8 & 0xFF;
			int var9 = var8 + arg3;
			if (var9 < 0) {
				var9 = 0;
			} else if (var9 > 255) {
				var9 = 255;
			}
			int var10 = bpal[var5] & 0xFF;
			int var11 = var10 + arg0;
			if (var11 < 0) {
				var11 = 0;
			} else if (var11 > 255) {
				var11 = 255;
			}
			bpal[var5] = (var7 << 16) + (var9 << 8) + var11;
		}
	}

	@ObfuscatedName("jb.a(BII)V")
	public void plotSprite(int arg1, int arg2) {
		int var4 = arg1 + xof;
		int var5 = arg2 + yof;
		int var6 = var4 + var5 * Pix2D.width;
		int var7 = 0;
		int var8 = hi;
		int var9 = wi;
		int var10 = Pix2D.width - var9;
		int var11 = 0;
		if (var5 < Pix2D.clipMinY) {
			int var12 = Pix2D.clipMinY - var5;
			var8 -= var12;
			var5 = Pix2D.clipMinY;
			var7 += var12 * var9;
			var6 += var12 * Pix2D.width;
		}
		if (var5 + var8 > Pix2D.clipMaxY) {
			var8 -= var5 + var8 - Pix2D.clipMaxY;
		}
		if (var4 < Pix2D.clipMinX) {
			int var13 = Pix2D.clipMinX - var4;
			var9 -= var13;
			var4 = Pix2D.clipMinX;
			var7 += var13;
			var6 += var13;
			var11 += var13;
			var10 += var13;
		}
		if (var4 + var9 > Pix2D.clipMaxX) {
			int var14 = var4 + var9 - Pix2D.clipMaxX;
			var9 -= var14;
			var11 += var14;
			var10 += var14;
		}
		if (var9 > 0 && var8 > 0) {
			plotSprite(var11, var10, var7, var8, var9, Pix2D.pixels, data, var6, bpal);
		}
	}

	@ObfuscatedName("jb.a(IBIIII[I[BI[I)V")
	public void plotSprite(int arg0, int arg2, int arg3, int arg4, int arg5, int[] arg6, byte[] arg7, int arg8, int[] arg9) {
		int var11 = -(arg5 >> 2);
		int var12 = -(arg5 & 0x3);
		for (int var13 = -arg4; var13 < 0; var13++) {
			for (int var14 = var11; var14 < 0; var14++) {
				byte var15 = arg7[arg3++];
				if (var15 == 0) {
					arg8++;
				} else {
					arg6[arg8++] = arg9[var15 & 0xFF];
				}
				byte var16 = arg7[arg3++];
				if (var16 == 0) {
					arg8++;
				} else {
					arg6[arg8++] = arg9[var16 & 0xFF];
				}
				byte var17 = arg7[arg3++];
				if (var17 == 0) {
					arg8++;
				} else {
					arg6[arg8++] = arg9[var17 & 0xFF];
				}
				byte var18 = arg7[arg3++];
				if (var18 == 0) {
					arg8++;
				} else {
					arg6[arg8++] = arg9[var18 & 0xFF];
				}
			}
			for (int var19 = var12; var19 < 0; var19++) {
				byte var20 = arg7[arg3++];
				if (var20 == 0) {
					arg8++;
				} else {
					arg6[arg8++] = arg9[var20 & 0xFF];
				}
			}
			arg8 += arg2;
			arg3 += arg0;
		}
	}
}
