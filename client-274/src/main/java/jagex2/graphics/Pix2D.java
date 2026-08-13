package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.datastruct.Linkable2;

@ObfuscatedName("gb")
public class Pix2D extends Linkable2 {

	@ObfuscatedName("gb.l")
	public static int[] pixels;

	@ObfuscatedName("gb.m")
	public static int width;

	@ObfuscatedName("gb.n")
	public static int height;

	@ObfuscatedName("gb.o")
	public static int clipMinY;

	@ObfuscatedName("gb.p")
	public static int clipMaxY;

	@ObfuscatedName("gb.q")
	public static int clipMinX;

	@ObfuscatedName("gb.r")
	public static int clipMaxX;

	@ObfuscatedName("gb.s")
	public static int sizeX;

	@ObfuscatedName("gb.t")
	public static int maxX;

	@ObfuscatedName("gb.u")
	public static int maxY;

	@ObfuscatedName("gb.a(II[II)V")
	public static void setPixels(int arg0, int arg1, int[] arg2) {
		pixels = arg2;
		width = arg0;
		height = arg1;
		setClipping(5, arg1, arg0, 0, 0);
	}

	@ObfuscatedName("gb.a(Z)V")
	public static void resetClipping() {
		clipMinX = 0;
		clipMinY = 0;
		clipMaxX = width;
		clipMaxY = height;
		sizeX = clipMaxX - 1;
		maxX = clipMaxX / 2;
	}

	@ObfuscatedName("gb.a(IIIII)V")
	public static void setClipping(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg3 < 0) {
			arg3 = 0;
		}
		if (arg4 < 0) {
			arg4 = 0;
		}
		if (arg2 > width) {
			arg2 = width;
		}
		if (arg1 > height) {
			arg1 = height;
		}
		clipMinX = arg3;
		clipMinY = arg4;
		clipMaxX = arg2;
		clipMaxY = arg1;
		sizeX = clipMaxX - 1;
		maxX = clipMaxX / 2;
		maxY = clipMaxY / 2;
	}

	@ObfuscatedName("gb.a(I)V")
	public static void cls() {
		int var1 = width * height;
		for (int var2 = 0; var2 < var1; var2++) {
			pixels[var2] = 0;
		}
	}

	@ObfuscatedName("gb.a(IIIIIII)V")
	public static void fillRectTrans(int arg0, int arg1, int arg2, int arg3, int arg4, int arg6) {
		if (arg4 < clipMinX) {
			arg0 -= clipMinX - arg4;
			arg4 = clipMinX;
		}
		if (arg2 < clipMinY) {
			arg1 -= clipMinY - arg2;
			arg2 = clipMinY;
		}
		if (arg4 + arg0 > clipMaxX) {
			arg0 = clipMaxX - arg4;
		}
		if (arg2 + arg1 > clipMaxY) {
			arg1 = clipMaxY - arg2;
		}
		int var7 = 256 - arg6;
		int var8 = (arg3 >> 16 & 0xFF) * arg6;
		int var9 = (arg3 >> 8 & 0xFF) * arg6;
		int var10 = (arg3 & 0xFF) * arg6;
		int var11 = width - arg0;
		int var12 = arg4 + arg2 * width;
		for (int var13 = 0; var13 < arg1; var13++) {
			for (int var14 = -arg0; var14 < 0; var14++) {
				int var15 = (pixels[var12] >> 16 & 0xFF) * var7;
				int var16 = (pixels[var12] >> 8 & 0xFF) * var7;
				int var17 = (pixels[var12] & 0xFF) * var7;
				int var18 = (var8 + var15 >> 8 << 16) + (var9 + var16 >> 8 << 8) + (var10 + var17 >> 8);
				pixels[var12++] = var18;
			}
			var12 += var11;
		}
	}

	@ObfuscatedName("gb.a(IIIIII)V")
	public static void fillRect(int arg0, int arg1, int arg2, int arg3, int arg5) {
		if (arg3 < clipMinX) {
			arg2 -= clipMinX - arg3;
			arg3 = clipMinX;
		}
		if (arg5 < clipMinY) {
			arg0 -= clipMinY - arg5;
			arg5 = clipMinY;
		}
		if (arg3 + arg2 > clipMaxX) {
			arg2 = clipMaxX - arg3;
		}
		if (arg5 + arg0 > clipMaxY) {
			arg0 = clipMaxY - arg5;
		}
		int var7 = width - arg2;
		int var8 = arg3 + arg5 * width;
		for (int var9 = -arg0; var9 < 0; var9++) {
			for (int var10 = -arg2; var10 < 0; var10++) {
				pixels[var8++] = arg1;
			}
			var8 += var7;
		}
	}

	@ObfuscatedName("gb.b(IIIIII)V")
	public static void drawRect(int arg0, int arg1, int arg2, int arg3, int arg4) {
		hline(arg0, arg1, arg3, arg4);
		hline(arg0, arg1, arg3, arg4 + arg2 - 1);
		vline(arg1, arg2, arg3, arg4);
		vline(arg1, arg2, arg3 + arg0 - 1, arg4);
	}

	@ObfuscatedName("gb.a(IZIIIII)V")
	public static void drawRectTrans(int arg0, int arg2, int arg3, int arg4, int arg5, int arg6) {
		hlineTrans(arg0, arg2, arg6, arg3, arg4);
		hlineTrans(arg0, arg2 + arg5 - 1, arg6, arg3, arg4);
		if (arg5 >= 3) {
			vlineTrans(arg3, arg5 - 2, arg2 + 1, arg4, arg6);
			vlineTrans(arg3 + arg0 - 1, arg5 - 2, arg2 + 1, arg4, arg6);
		}
	}

	@ObfuscatedName("gb.a(IIIBI)V")
	public static void hline(int arg0, int arg1, int arg2, int arg4) {
		if (arg4 < clipMinY || arg4 >= clipMaxY) {
			return;
		}
		if (arg2 < clipMinX) {
			arg0 -= clipMinX - arg2;
			arg2 = clipMinX;
		}
		if (arg2 + arg0 > clipMaxX) {
			arg0 = clipMaxX - arg2;
		}
		int var5 = arg2 + arg4 * width;
		for (int var6 = 0; var6 < arg0; var6++) {
			pixels[var5 + var6] = arg1;
		}
	}

	@ObfuscatedName("gb.c(IIIIII)V")
	public static void hlineTrans(int arg0, int arg1, int arg2, int arg3, int arg5) {
		if (arg1 < clipMinY || arg1 >= clipMaxY) {
			return;
		}
		if (arg3 < clipMinX) {
			arg0 -= clipMinX - arg3;
			arg3 = clipMinX;
		}
		if (arg3 + arg0 > clipMaxX) {
			arg0 = clipMaxX - arg3;
		}
		int var6 = 256 - arg2;
		int var7 = (arg5 >> 16 & 0xFF) * arg2;
		int var8 = (arg5 >> 8 & 0xFF) * arg2;
		int var9 = (arg5 & 0xFF) * arg2;
		int var10 = arg3 + arg1 * width;
		for (int var11 = 0; var11 < arg0; var11++) {
			int var12 = (pixels[var10] >> 16 & 0xFF) * var6;
			int var13 = (pixels[var10] >> 8 & 0xFF) * var6;
			int var14 = (pixels[var10] & 0xFF) * var6;
			int var15 = (var7 + var12 >> 8 << 16) + (var8 + var13 >> 8 << 8) + (var9 + var14 >> 8);
			pixels[var10++] = var15;
		}
	}

	@ObfuscatedName("gb.b(IIIII)V")
	public static void vline(int arg0, int arg1, int arg2, int arg3) {
		if (arg2 < clipMinX || arg2 >= clipMaxX) {
			return;
		}
		if (arg3 < clipMinY) {
			arg1 -= clipMinY - arg3;
			arg3 = clipMinY;
		}
		if (arg3 + arg1 > clipMaxY) {
			arg1 = clipMaxY - arg3;
		}
		int var4 = arg2 + arg3 * width;
		for (int var5 = 0; var5 < arg1; var5++) {
			pixels[var4 + var5 * width] = arg0;
		}
	}

	@ObfuscatedName("gb.a(IIIZII)V")
	public static void vlineTrans(int arg0, int arg1, int arg2, int arg4, int arg5) {
		if (arg0 < clipMinX || arg0 >= clipMaxX) {
			return;
		}
		if (arg2 < clipMinY) {
			arg1 -= clipMinY - arg2;
			arg2 = clipMinY;
		}
		if (arg2 + arg1 > clipMaxY) {
			arg1 = clipMaxY - arg2;
		}
		int var6 = 256 - arg5;
		int var7 = (arg4 >> 16 & 0xFF) * arg5;
		int var8 = (arg4 >> 8 & 0xFF) * arg5;
		int var9 = (arg4 & 0xFF) * arg5;
		int var10 = arg0 + arg2 * width;
		for (int var11 = 0; var11 < arg1; var11++) {
			int var12 = (pixels[var10] >> 16 & 0xFF) * var6;
			int var13 = (pixels[var10] >> 8 & 0xFF) * var6;
			int var14 = (pixels[var10] & 0xFF) * var6;
			int var15 = (var7 + var12 >> 8 << 16) + (var8 + var13 >> 8 << 8) + (var9 + var14 >> 8);
			pixels[var10] = var15;
			var10 += width;
		}
	}
}
