package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.datastruct.DoublyLinkable;

@ObfuscatedName("fb")
public class Pix2D extends DoublyLinkable {

	@ObfuscatedName("fb.h")
	public static int field530;

	@ObfuscatedName("fb.i")
	public static int field531;

	@ObfuscatedName("fb.j")
	public static boolean field532 = true;

	@ObfuscatedName("fb.k")
	public static int[] data;

	@ObfuscatedName("fb.l")
	public static int width2d;

	@ObfuscatedName("fb.m")
	public static int height2d;

	@ObfuscatedName("fb.n")
	public static int boundTop;

	@ObfuscatedName("fb.o")
	public static int boundBottom;

	@ObfuscatedName("fb.p")
	public static int boundLeft;

	@ObfuscatedName("fb.q")
	public static int boundRight;

	@ObfuscatedName("fb.r")
	public static int safeWidth;

	@ObfuscatedName("fb.s")
	public static int centerW2D;

	@ObfuscatedName("fb.t")
	public static int centerH2D;

	@ObfuscatedName("fb.u")
	public static int field543;

	@ObfuscatedName("fb.a(I[III)V")
	public static void bind(int arg0, int[] arg1, int arg2, int arg3) {
		data = arg1;
		width2d = arg0;
		while (arg2 >= 0) {
			field530 = -151;
		}
		height2d = arg3;
		setClipping(arg3, 0, arg0, 789, 0);
	}

	@ObfuscatedName("fb.a(I)V")
	public static void resetClipping(int arg0) {
		boundLeft = 0;
		boundTop = 0;
		boundRight = width2d;
		if (arg0 != 0) {
			for (int var1 = 1; var1 > 0; var1++) {
			}
		}
		boundBottom = height2d;
		safeWidth = boundRight - 1;
		centerW2D = boundRight / 2;
	}

	@ObfuscatedName("fb.a(IIIII)V")
	public static void setClipping(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg4 < 0) {
			arg4 = 0;
		}
		if (arg1 < 0) {
			arg1 = 0;
		}
		if (arg2 > width2d) {
			arg2 = width2d;
		}
		if (arg0 > height2d) {
			arg0 = height2d;
		}
		boundLeft = arg4;
		boundTop = arg1;
		boundRight = arg2;
		boundBottom = arg0;
		safeWidth = boundRight - 1;
		centerW2D = boundRight / 2;
		if (arg3 <= 0) {
			for (int var5 = 1; var5 > 0; var5++) {
			}
		}
		centerH2D = boundBottom / 2;
	}

	@ObfuscatedName("fb.b(I)V")
	public static void clear(int arg0) {
		int var3 = 87 / arg0;
		int var1 = width2d * height2d;
		for (int var2 = 0; var2 < var1; var2++) {
			data[var2] = 0;
		}
	}

	@ObfuscatedName("fb.a(IIIBII)V")
	public static void fillRect(int arg0, int arg1, int arg2, byte arg3, int arg4, int arg5) {
		if (arg3 != 93) {
			field530 = 289;
		}
		if (arg1 < boundLeft) {
			arg4 -= boundLeft - arg1;
			arg1 = boundLeft;
		}
		if (arg0 < boundTop) {
			arg5 -= boundTop - arg0;
			arg0 = boundTop;
		}
		if (arg1 + arg4 > boundRight) {
			arg4 = boundRight - arg1;
		}
		if (arg0 + arg5 > boundBottom) {
			arg5 = boundBottom - arg0;
		}
		int var6 = width2d - arg4;
		int var7 = arg1 + arg0 * width2d;
		for (int var8 = -arg5; var8 < 0; var8++) {
			for (int var9 = -arg4; var9 < 0; var9++) {
				data[var7++] = arg2;
			}
			var7 += var6;
		}
	}

	@ObfuscatedName("fb.a(IIIIII)V")
	public static void drawRect(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		if (arg0 >= 3 && arg0 <= 3) {
			hline(arg2, 0, arg4, arg5, arg1);
			hline(arg2, 0, arg4 + arg3 - 1, arg5, arg1);
			vline(arg2, field531, arg4, arg3, arg1);
			vline(arg2, field531, arg4, arg3, arg1 + arg5 - 1);
		}
	}

	@ObfuscatedName("fb.b(IIIII)V")
	public static void hline(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg2 < boundTop || arg2 >= boundBottom) {
			return;
		}
		if (arg4 < boundLeft) {
			arg3 -= boundLeft - arg4;
			arg4 = boundLeft;
		}
		if (arg4 + arg3 > boundRight) {
			arg3 = boundRight - arg4;
		}
		int var5 = arg4 + arg2 * width2d;
		if (arg1 == 0) {
			for (int var6 = 0; var6 < arg3; var6++) {
				data[var5 + var6] = arg0;
			}
		}
	}

	@ObfuscatedName("fb.c(IIIII)V")
	public static void vline(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg4 < boundLeft || arg4 >= boundRight) {
			return;
		}
		if (arg2 < boundTop) {
			arg3 -= boundTop - arg2;
			arg2 = boundTop;
		}
		if (arg2 + arg3 > boundBottom) {
			arg3 = boundBottom - arg2;
		}
		int var5 = arg4 + arg2 * width2d;
		if (arg1 != 0) {
			field531 = 71;
		}
		for (int var6 = 0; var6 < arg3; var6++) {
			data[var5 + var6 * width2d] = arg0;
		}
	}
}
