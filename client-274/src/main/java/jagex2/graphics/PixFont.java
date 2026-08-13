package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

import java.util.Random;

@ObfuscatedName("kb")
public final class PixFont extends Pix2D {

	@ObfuscatedName("kb.B")
	public final byte[][] charMask = new byte[256][];

	@ObfuscatedName("kb.C")
	public final int[] charMaskWidth = new int[256];

	@ObfuscatedName("kb.D")
	public final int[] charMaskHeight = new int[256];

	@ObfuscatedName("kb.E")
	public final int[] charOffsetX = new int[256];

	@ObfuscatedName("kb.F")
	public final int[] charOffsetY = new int[256];

	@ObfuscatedName("kb.G")
	public final int[] charAdvance = new int[256];

	@ObfuscatedName("kb.I")
	public final Random rand = new Random();

	@ObfuscatedName("kb.J")
	public boolean strikeout = false;

	@ObfuscatedName("kb.H")
	public int height;

	public PixFont(JagFile arg0, boolean arg1, String arg2, byte arg3) {
		Packet var5 = new Packet(arg0.read(arg2 + ".dat", null));
		Packet var6 = new Packet(arg0.read("index.dat", null));
		var6.data = var5.g2() + 4;
		int var7 = var6.g1();
		if (var7 > 0) {
			var6.data += (var7 - 1) * 3;
		}
		for (int var8 = 0; var8 < 256; var8++) {
			charOffsetX[var8] = var6.g1();
			charOffsetY[var8] = var6.g1();
			int var9 = charMaskWidth[var8] = var6.g2();
			int var10 = charMaskHeight[var8] = var6.g2();
			int var11 = var6.g1();
			int var12 = var9 * var10;
			charMask[var8] = new byte[var12];
			if (var11 == 0) {
				for (int var13 = 0; var13 < var12; var13++) {
					charMask[var8][var13] = var5.g1b();
				}
			} else if (var11 == 1) {
				for (int var14 = 0; var14 < var9; var14++) {
					for (int var15 = 0; var15 < var10; var15++) {
						charMask[var8][var14 + var15 * var9] = var5.g1b();
					}
				}
			}
			if (var10 > height && var8 < 128) {
				height = var10;
			}
			charOffsetX[var8] = 1;
			charAdvance[var8] = var9 + 2;
			int var16 = 0;
			for (int var17 = var10 / 7; var17 < var10; var17++) {
				var16 += charMask[var8][var17 * var9];
			}
			int var10002;
			if (var16 <= var10 / 7) {
				var10002 = charAdvance[var8]--;
				charOffsetX[var8] = 0;
			}
			int var18 = 0;
			for (int var19 = var10 / 7; var19 < var10; var19++) {
				var18 += charMask[var8][var9 + var19 * var9 - 1];
			}
			if (var18 <= var10 / 7) {
				var10002 = charAdvance[var8]--;
			}
		}
		if (arg1) {
			charAdvance[32] = charAdvance[73];
		} else {
			charAdvance[32] = charAdvance[105];
		}
	}

	@ObfuscatedName("kb.a(IIILjava/lang/String;Z)V")
	public void centreString(int arg0, int arg1, int arg2, String arg3) {
		drawString(arg0 - stringWid(arg3) / 2, arg3, arg2, arg1);
	}

	@ObfuscatedName("kb.a(IIILjava/lang/String;IZ)V")
	public void centreStringTag(int arg1, int arg2, String arg3, int arg4, boolean arg5) {
		drawStringTag(arg3, arg5, arg2, arg1 - stringWid(arg3) / 2, arg4);
	}

	@ObfuscatedName("kb.a(ILjava/lang/String;)I")
	public int stringWid(String arg1) {
		if (arg1 == null) {
			return 0;
		}
		int var3 = 0;
		for (int var4 = 0; var4 < arg1.length(); var4++) {
			if (arg1.charAt(var4) == '@' && var4 + 4 < arg1.length() && arg1.charAt(var4 + 4) == '@') {
				var4 += 4;
			} else {
				var3 += charAdvance[arg1.charAt(var4)];
			}
		}
		return var3;
	}

	@ObfuscatedName("kb.a(ILjava/lang/String;IIZ)V")
	public void drawString(int arg0, String arg1, int arg2, int arg3) {
		if (arg1 == null) {
			return;
		}
		int var6 = arg3 - height;
		for (int var7 = 0; var7 < arg1.length(); var7++) {
			char var8 = arg1.charAt(var7);
			if (var8 != ' ') {
				plotLetter(charMask[var8], arg0 + charOffsetX[var8], var6 + charOffsetY[var8], charMaskWidth[var8], charMaskHeight[var8], arg2);
			}
			arg0 += charAdvance[var8];
		}
	}

	@ObfuscatedName("kb.a(Ljava/lang/String;IIIII)V")
	public void centreStringWave(String arg0, int arg1, int arg2, int arg3, int arg5) {
		if (arg0 == null) {
			return;
		}
		int var7 = arg2 - stringWid(arg0) / 2;
		int var8 = arg1 - height;
		for (int var9 = 0; var9 < arg0.length(); var9++) {
			char var10 = arg0.charAt(var9);
			if (var10 != ' ') {
				plotLetter(charMask[var10], var7 + charOffsetX[var10], var8 + charOffsetY[var10] + (int) (Math.sin((double) var9 / 2.0D + (double) arg5 / 5.0D) * 5.0D), charMaskWidth[var10], charMaskHeight[var10], arg3);
			}
			var7 += charAdvance[var10];
		}
	}

	@ObfuscatedName("kb.a(Ljava/lang/String;IZIII)V")
	public void drawStringTag(String arg0, boolean arg2, int arg3, int arg4, int arg5) {
		strikeout = false;
		int var7 = arg4;
		if (arg0 == null) {
			return;
		}
		int var8 = arg3 - height;
		for (int var9 = 0; var9 < arg0.length(); var9++) {
			if (arg0.charAt(var9) == '@' && var9 + 4 < arg0.length() && arg0.charAt(var9 + 4) == '@') {
				int var10 = updateState(arg0.substring(var9 + 1, var9 + 4));
				if (var10 != -1) {
					arg5 = var10;
				}
				var9 += 4;
			} else {
				char var11 = arg0.charAt(var9);
				if (var11 != ' ') {
					if (arg2) {
						plotLetter(charMask[var11], arg4 + charOffsetX[var11] + 1, var8 + charOffsetY[var11] + 1, charMaskWidth[var11], charMaskHeight[var11], 0);
					}
					plotLetter(charMask[var11], arg4 + charOffsetX[var11], var8 + charOffsetY[var11], charMaskWidth[var11], charMaskHeight[var11], arg5);
				}
				arg4 += charAdvance[var11];
			}
		}
		if (strikeout) {
			Pix2D.hline(arg4 - var7, 8388608, var7, var8 + (int) ((double) height * 0.7D));
		}
	}

	@ObfuscatedName("kb.a(IILjava/lang/String;IIIZ)V")
	public void drawStringAntiMacro(int arg1, String arg2, int arg3, int arg4, int arg5, boolean arg6) {
		if (arg2 == null) {
			return;
		}
		rand.setSeed((long) arg5);
		int var8 = (rand.nextInt() & 0x1F) + 192;
		int var9 = arg3 - height;
		for (int var10 = 0; var10 < arg2.length(); var10++) {
			if (arg2.charAt(var10) == '@' && var10 + 4 < arg2.length() && arg2.charAt(var10 + 4) == '@') {
				int var11 = updateState(arg2.substring(var10 + 1, var10 + 4));
				if (var11 != -1) {
					arg4 = var11;
				}
				var10 += 4;
			} else {
				char var12 = arg2.charAt(var10);
				if (var12 != ' ') {
					if (arg6) {
						plotLetterTrans(var9 + charOffsetY[var12] + 1, charMask[var12], arg1 + charOffsetX[var12] + 1, 0, charMaskHeight[var12], 192, charMaskWidth[var12]);
					}
					plotLetterTrans(var9 + charOffsetY[var12], charMask[var12], arg1 + charOffsetX[var12], arg4, charMaskHeight[var12], var8, charMaskWidth[var12]);
				}
				arg1 += charAdvance[var12];
				if ((rand.nextInt() & 0x3) == 0) {
					arg1++;
				}
			}
		}
	}

	@ObfuscatedName("kb.a(Ljava/lang/String;I)I")
	public int updateState(String arg0) {
		if (arg0.equals("red")) {
			return 16711680;
		} else if (arg0.equals("gre")) {
			return 65280;
		} else if (arg0.equals("blu")) {
			return 255;
		} else if (arg0.equals("yel")) {
			return 16776960;
		} else if (arg0.equals("cya")) {
			return 65535;
		} else if (arg0.equals("mag")) {
			return 16711935;
		} else if (arg0.equals("whi")) {
			return 16777215;
		} else if (arg0.equals("bla")) {
			return 0;
		} else if (arg0.equals("lre")) {
			return 16748608;
		} else if (arg0.equals("dre")) {
			return 8388608;
		} else if (arg0.equals("dbl")) {
			return 128;
		} else if (arg0.equals("or1")) {
			return 16756736;
		} else if (arg0.equals("or2")) {
			return 16740352;
		} else if (arg0.equals("or3")) {
			return 16723968;
		} else if (arg0.equals("gr1")) {
			return 12648192;
		} else if (arg0.equals("gr2")) {
			return 8453888;
		} else if (arg0.equals("gr3")) {
			return 4259584;
		} else {
			if (arg0.equals("str")) {
				strikeout = true;
			}
			return -1;
		}
	}

	@ObfuscatedName("kb.a([BIIIII)V")
	public void plotLetter(byte[] arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		int var7 = arg1 + arg2 * Pix2D.width;
		int var8 = Pix2D.width - arg3;
		int var9 = 0;
		int var10 = 0;
		if (arg2 < Pix2D.clipMinY) {
			int var11 = Pix2D.clipMinY - arg2;
			arg4 -= var11;
			arg2 = Pix2D.clipMinY;
			var10 += var11 * arg3;
			var7 += var11 * Pix2D.width;
		}
		if (arg2 + arg4 >= Pix2D.clipMaxY) {
			arg4 -= arg2 + arg4 + 1 - Pix2D.clipMaxY;
		}
		if (arg1 < Pix2D.clipMinX) {
			int var12 = Pix2D.clipMinX - arg1;
			arg3 -= var12;
			arg1 = Pix2D.clipMinX;
			var10 += var12;
			var7 += var12;
			var9 += var12;
			var8 += var12;
		}
		if (arg1 + arg3 >= Pix2D.clipMaxX) {
			int var13 = arg1 + arg3 + 1 - Pix2D.clipMaxX;
			arg3 -= var13;
			var9 += var13;
			var8 += var13;
		}
		if (arg3 > 0 && arg4 > 0) {
			plot(Pix2D.pixels, arg0, arg5, var10, var7, arg3, arg4, var8, var9);
		}
	}

	@ObfuscatedName("kb.a([I[BIIIIIII)V")
	public void plot(int[] arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		int var10 = -(arg5 >> 2);
		int var11 = -(arg5 & 0x3);
		for (int var12 = -arg6; var12 < 0; var12++) {
			for (int var13 = var10; var13 < 0; var13++) {
				if (arg1[arg3++] == 0) {
					arg4++;
				} else {
					arg0[arg4++] = arg2;
				}
				if (arg1[arg3++] == 0) {
					arg4++;
				} else {
					arg0[arg4++] = arg2;
				}
				if (arg1[arg3++] == 0) {
					arg4++;
				} else {
					arg0[arg4++] = arg2;
				}
				if (arg1[arg3++] == 0) {
					arg4++;
				} else {
					arg0[arg4++] = arg2;
				}
			}
			for (int var14 = var11; var14 < 0; var14++) {
				if (arg1[arg3++] == 0) {
					arg4++;
				} else {
					arg0[arg4++] = arg2;
				}
			}
			arg4 += arg7;
			arg3 += arg8;
		}
	}

	@ObfuscatedName("kb.a(I[BIIIZII)V")
	public void plotLetterTrans(int arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg6, int arg7) {
		int var9 = arg2 + arg0 * Pix2D.width;
		int var10 = Pix2D.width - arg7;
		int var11 = 0;
		int var12 = 0;
		if (arg0 < Pix2D.clipMinY) {
			int var13 = Pix2D.clipMinY - arg0;
			arg4 -= var13;
			arg0 = Pix2D.clipMinY;
			var12 += var13 * arg7;
			var9 += var13 * Pix2D.width;
		}
		if (arg0 + arg4 >= Pix2D.clipMaxY) {
			arg4 -= arg0 + arg4 + 1 - Pix2D.clipMaxY;
		}
		if (arg2 < Pix2D.clipMinX) {
			int var14 = Pix2D.clipMinX - arg2;
			arg7 -= var14;
			arg2 = Pix2D.clipMinX;
			var12 += var14;
			var9 += var14;
			var11 += var14;
			var10 += var14;
		}
		if (arg2 + arg7 >= Pix2D.clipMaxX) {
			int var15 = arg2 + arg7 + 1 - Pix2D.clipMaxX;
			arg7 -= var15;
			var11 += var15;
			var10 += var15;
		}
		if (arg7 > 0 && arg4 > 0) {
			plotTrans(arg4, arg1, arg3, arg7, var11, arg6, var9, Pix2D.pixels, var12, var10);
		}
	}

	@ObfuscatedName("kb.a(I[BIIIII[IIII)V")
	public void plotTrans(int arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int[] arg7, int arg9, int arg10) {
		int var12 = ((arg2 & 0xFF00FF) * arg5 & 0xFF00FF00) + ((arg2 & 0xFF00) * arg5 & 0xFF0000) >> 8;
		int var13 = 256 - arg5;
		for (int var14 = -arg0; var14 < 0; var14++) {
			for (int var15 = -arg3; var15 < 0; var15++) {
				if (arg1[arg9++] == 0) {
					arg6++;
				} else {
					int var16 = arg7[arg6];
					arg7[arg6++] = (((var16 & 0xFF00FF) * var13 & 0xFF00FF00) + ((var16 & 0xFF00) * var13 & 0xFF0000) >> 8) + var12;
				}
			}
			arg6 += arg10;
			arg9 += arg4;
		}
	}
}
