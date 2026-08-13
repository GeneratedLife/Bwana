package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;
import java.util.Random;

@ObfuscatedName("jb")
public class PixFont extends Pix2D {

	@ObfuscatedName("jb.v")
	public boolean field596 = true;

	@ObfuscatedName("jb.w")
	public byte field597 = 8;

	@ObfuscatedName("jb.x")
	public byte field598 = 6;

	@ObfuscatedName("jb.y")
	public byte field599 = 2;

	@ObfuscatedName("jb.z")
	public int field600 = -708;

	@ObfuscatedName("jb.A")
	public int field601 = 997;

	@ObfuscatedName("jb.B")
	public byte[][] charMask = new byte[94][];

	@ObfuscatedName("jb.C")
	public int[] charMaskWidth = new int[94];

	@ObfuscatedName("jb.D")
	public int[] charMaskHeight = new int[94];

	@ObfuscatedName("jb.E")
	public int[] charOffsetX = new int[94];

	@ObfuscatedName("jb.F")
	public int[] charOffsetY = new int[94];

	@ObfuscatedName("jb.G")
	public int[] charAdvance = new int[95];

	@ObfuscatedName("jb.H")
	public int[] drawWidth = new int[256];

	@ObfuscatedName("jb.J")
	public Random random = new Random();

	@ObfuscatedName("jb.I")
	public int height;

	@ObfuscatedName("jb.K")
	public static int[] CHAR_LOOKUP = new int[256];

	public PixFont(Jagfile arg0, String arg1, int arg2) {
		Packet var4 = new Packet(363, arg0.read(arg1 + ".dat", null, (byte) 2));
		Packet var5 = new Packet(363, arg0.read("index.dat", null, (byte) 2));
		var5.pos = var4.g2() + 4;
		int var6 = var5.g1();
		if (var6 > 0) {
			var5.pos += (var6 - 1) * 3;
		}
		int var8;
		for (int var7 = 0; var7 < 94; var7++) {
			this.charOffsetX[var7] = var5.g1();
			this.charOffsetY[var7] = var5.g1();
			var8 = this.charMaskWidth[var7] = var5.g2();
			int var9 = this.charMaskHeight[var7] = var5.g2();
			int var10 = var5.g1();
			int var11 = var8 * var9;
			this.charMask[var7] = new byte[var11];
			int var12;
			int var13;
			if (var10 == 0) {
				for (var12 = 0; var12 < var11; var12++) {
					this.charMask[var7][var12] = var4.g1b();
				}
			} else if (var10 == 1) {
				for (var12 = 0; var12 < var8; var12++) {
					for (var13 = 0; var13 < var9; var13++) {
						this.charMask[var7][var12 + var13 * var8] = var4.g1b();
					}
				}
			}
			if (var9 > this.height) {
				this.height = var9;
			}
			this.charOffsetX[var7] = 1;
			this.charAdvance[var7] = var8 + 2;
			var12 = 0;
			for (var13 = var9 / 7; var13 < var9; var13++) {
				var12 += this.charMask[var7][var13 * var8];
			}
			int var10002;
			if (var12 <= var9 / 7) {
				var10002 = this.charAdvance[var7]--;
				this.charOffsetX[var7] = 0;
			}
			var12 = 0;
			for (int var14 = var9 / 7; var14 < var9; var14++) {
				var12 += this.charMask[var7][var8 - 1 + var14 * var8];
			}
			if (var12 <= var9 / 7) {
				var10002 = this.charAdvance[var7]--;
			}
		}
		int var15 = 9 / arg2;
		this.charAdvance[94] = this.charAdvance[8];
		for (var8 = 0; var8 < 256; var8++) {
			this.drawWidth[var8] = this.charAdvance[CHAR_LOOKUP[var8]];
		}
	}

	@ObfuscatedName("jb.a(IBILjava/lang/String;I)V")
	public void drawStringCenter(int arg0, byte arg1, int arg2, String arg3, int arg4) {
		if (arg1 != 6) {
			this.field601 = 140;
		}
		this.drawString(arg4 - this.stringWidth(false, arg3) / 2, arg0, false, arg2, arg3);
	}

	@ObfuscatedName("jb.a(IIZILjava/lang/String;I)V")
	public void drawStringTaggableCenter(int arg0, int arg1, boolean arg2, int arg3, String arg4, int arg5) {
		this.drawStringTaggable(arg0 - this.stringWidth(false, arg4) / 2, 6, arg3, arg4, arg2, arg1);
		if (arg5 != 0) {
			this.field596 = !this.field596;
		}
	}

	@ObfuscatedName("jb.a(ZLjava/lang/String;)I")
	public int stringWidth(boolean arg0, String arg1) {
		if (arg1 == null) {
			return 0;
		}
		int var3 = 0;
		if (arg0) {
			return this.field600;
		}
		for (int var4 = 0; var4 < arg1.length(); var4++) {
			if (arg1.charAt(var4) == '@' && var4 + 4 < arg1.length() && arg1.charAt(var4 + 4) == '@') {
				var4 += 4;
			} else {
				var3 += this.drawWidth[arg1.charAt(var4)];
			}
		}
		return var3;
	}

	@ObfuscatedName("jb.a(IIZILjava/lang/String;)V")
	public void drawString(int arg0, int arg1, boolean arg2, int arg3, String arg4) {
		if (arg4 == null) {
			return;
		}
		int var8 = arg1 - this.height;
		if (arg2) {
			this.field596 = !this.field596;
		}
		for (int var6 = 0; var6 < arg4.length(); var6++) {
			int var7 = CHAR_LOOKUP[arg4.charAt(var6)];
			if (var7 != 94) {
				this.drawChar(this.charMask[var7], arg0 + this.charOffsetX[var7], var8 + this.charOffsetY[var7], this.charMaskWidth[var7], this.charMaskHeight[var7], arg3);
			}
			arg0 += this.charAdvance[var7];
		}
	}

	@ObfuscatedName("jb.a(IBIIILjava/lang/String;)V")
	public void drawCenteredWave(int arg0, byte arg1, int arg2, int arg3, int arg4, String arg5) {
		if (arg5 == null) {
			return;
		}
		arg2 -= this.stringWidth(false, arg5) / 2;
		int var9 = arg3 - this.height;
		int var7;
		if (arg1 != this.field597) {
			for (var7 = 1; var7 > 0; var7++) {
			}
		}
		for (var7 = 0; var7 < arg5.length(); var7++) {
			int var8 = CHAR_LOOKUP[arg5.charAt(var7)];
			if (var8 != 94) {
				this.drawChar(this.charMask[var8], arg2 + this.charOffsetX[var8], var9 + this.charOffsetY[var8] + (int) (Math.sin((double) var7 / 2.0D + (double) arg0 / 5.0D) * 5.0D), this.charMaskWidth[var8], this.charMaskHeight[var8], arg4);
			}
			arg2 += this.charAdvance[var8];
		}
	}

	@ObfuscatedName("jb.a(IIILjava/lang/String;ZI)V")
	public void drawStringTaggable(int arg0, int arg1, int arg2, String arg3, boolean arg4, int arg5) {
		if (arg3 == null) {
			return;
		}
		int var9 = arg2 - this.height;
		for (int var7 = 0; var7 < arg3.length(); var7++) {
			if (arg3.charAt(var7) == '@' && var7 + 4 < arg3.length() && arg3.charAt(var7 + 4) == '@') {
				arg5 = this.evaluateTag(0, arg3.substring(var7 + 1, var7 + 4));
				var7 += 4;
			} else {
				int var8 = CHAR_LOOKUP[arg3.charAt(var7)];
				if (var8 != 94) {
					if (arg4) {
						this.drawChar(this.charMask[var8], arg0 + this.charOffsetX[var8] + 1, var9 + this.charOffsetY[var8] + 1, this.charMaskWidth[var8], this.charMaskHeight[var8], 0);
					}
					this.drawChar(this.charMask[var8], arg0 + this.charOffsetX[var8], var9 + this.charOffsetY[var8], this.charMaskWidth[var8], this.charMaskHeight[var8], arg5);
				}
				arg0 += this.charAdvance[var8];
			}
		}
		if (arg1 == 6) {
			;
		}
	}

	@ObfuscatedName("jb.a(IZBIILjava/lang/String;I)V")
	public void drawStringTooltip(int arg0, boolean arg1, byte arg2, int arg3, int arg4, String arg5, int arg6) {
		if (arg5 == null) {
			return;
		}
		this.random.setSeed((long) arg0);
		int var8 = (this.random.nextInt() & 0x1F) + 192;
		int var11 = arg3 - this.height;
		if (arg2 != -121) {
			this.field601 = 341;
		}
		for (int var9 = 0; var9 < arg5.length(); var9++) {
			if (arg5.charAt(var9) == '@' && var9 + 4 < arg5.length() && arg5.charAt(var9 + 4) == '@') {
				arg4 = this.evaluateTag(0, arg5.substring(var9 + 1, var9 + 4));
				var9 += 4;
			} else {
				int var10 = CHAR_LOOKUP[arg5.charAt(var9)];
				if (var10 != 94) {
					if (arg1) {
						this.drawCharAlpha(this.charMask[var10], (byte) 6, arg6 + this.charOffsetX[var10] + 1, this.charMaskHeight[var10], 0, var11 + this.charOffsetY[var10] + 1, 192, this.charMaskWidth[var10]);
					}
					this.drawCharAlpha(this.charMask[var10], (byte) 6, arg6 + this.charOffsetX[var10], this.charMaskHeight[var10], arg4, var11 + this.charOffsetY[var10], var8, this.charMaskWidth[var10]);
				}
				arg6 += this.charAdvance[var10];
				if ((this.random.nextInt() & 0x3) == 0) {
					arg6++;
				}
			}
		}
	}

	@ObfuscatedName("jb.a(ILjava/lang/String;)I")
	public int evaluateTag(int arg0, String arg1) {
		if (arg0 != 0) {
			this.field600 = 450;
		}
		if (arg1.equals("red")) {
			return 16711680;
		} else if (arg1.equals("gre")) {
			return 65280;
		} else if (arg1.equals("blu")) {
			return 255;
		} else if (arg1.equals("yel")) {
			return 16776960;
		} else if (arg1.equals("cya")) {
			return 65535;
		} else if (arg1.equals("mag")) {
			return 16711935;
		} else if (arg1.equals("whi")) {
			return 16777215;
		} else if (arg1.equals("bla")) {
			return 0;
		} else if (arg1.equals("lre")) {
			return 16748608;
		} else if (arg1.equals("dre")) {
			return 8388608;
		} else if (arg1.equals("dbl")) {
			return 128;
		} else if (arg1.equals("or1")) {
			return 16756736;
		} else if (arg1.equals("or2")) {
			return 16740352;
		} else if (arg1.equals("or3")) {
			return 16723968;
		} else if (arg1.equals("gr1")) {
			return 12648192;
		} else if (arg1.equals("gr2")) {
			return 8453888;
		} else if (arg1.equals("gr3")) {
			return 4259584;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("jb.a([BIIIII)V")
	public void drawChar(byte[] arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		int var7 = arg1 + arg2 * Pix2D.width2d;
		int var8 = Pix2D.width2d - arg3;
		int var9 = 0;
		int var10 = 0;
		int var11;
		if (arg2 < Pix2D.boundTop) {
			var11 = Pix2D.boundTop - arg2;
			arg4 -= var11;
			arg2 = Pix2D.boundTop;
			var10 += var11 * arg3;
			var7 += var11 * Pix2D.width2d;
		}
		if (arg2 + arg4 >= Pix2D.boundBottom) {
			arg4 -= arg2 + arg4 - Pix2D.boundBottom + 1;
		}
		if (arg1 < Pix2D.boundLeft) {
			var11 = Pix2D.boundLeft - arg1;
			arg3 -= var11;
			arg1 = Pix2D.boundLeft;
			var10 += var11;
			var7 += var11;
			var9 += var11;
			var8 += var11;
		}
		if (arg1 + arg3 >= Pix2D.boundRight) {
			var11 = arg1 + arg3 - Pix2D.boundRight + 1;
			arg3 -= var11;
			var9 += var11;
			var8 += var11;
		}
		if (arg3 > 0 && arg4 > 0) {
			this.drawMask(Pix2D.data, arg0, arg5, var10, var7, arg3, arg4, var8, var9);
		}
	}

	@ObfuscatedName("jb.a([I[BIIIIIII)V")
	public void drawMask(int[] arg0, byte[] arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		int var10 = -(arg5 >> 2);
		int var14 = -(arg5 & 0x3);
		for (int var11 = -arg6; var11 < 0; var11++) {
			for (int var12 = var10; var12 < 0; var12++) {
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
			for (int var13 = var14; var13 < 0; var13++) {
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

	@ObfuscatedName("jb.a([BBIIIIII)V")
	public void drawCharAlpha(byte[] arg0, byte arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		if (arg1 != this.field598) {
			return;
		}
		int var9 = arg2 + arg5 * Pix2D.width2d;
		int var10 = Pix2D.width2d - arg7;
		int var11 = 0;
		int var12 = 0;
		int var13;
		if (arg5 < Pix2D.boundTop) {
			var13 = Pix2D.boundTop - arg5;
			arg3 -= var13;
			arg5 = Pix2D.boundTop;
			var12 += var13 * arg7;
			var9 += var13 * Pix2D.width2d;
		}
		if (arg5 + arg3 >= Pix2D.boundBottom) {
			arg3 -= arg5 + arg3 - Pix2D.boundBottom + 1;
		}
		if (arg2 < Pix2D.boundLeft) {
			var13 = Pix2D.boundLeft - arg2;
			arg7 -= var13;
			arg2 = Pix2D.boundLeft;
			var12 += var13;
			var9 += var13;
			var11 += var13;
			var10 += var13;
		}
		if (arg2 + arg7 >= Pix2D.boundRight) {
			var13 = arg2 + arg7 - Pix2D.boundRight + 1;
			arg7 -= var13;
			var11 += var13;
			var10 += var13;
		}
		if (arg7 > 0 && arg3 > 0) {
			this.drawMaskAlpha(arg3, var9, arg7, Pix2D.data, arg0, arg6, var12, var10, var11, (byte) 2, arg4);
		}
	}

	@ObfuscatedName("jb.a(III[I[BIIIIBI)V")
	public void drawMaskAlpha(int arg0, int arg1, int arg2, int[] arg3, byte[] arg4, int arg5, int arg6, int arg7, int arg8, byte arg9, int arg10) {
		int var17 = ((arg10 & 0xFF00FF) * arg5 & 0xFF00FF00) + ((arg10 & 0xFF00) * arg5 & 0xFF0000) >> 8;
		int var12;
		if (arg9 == this.field599) {
			boolean var16 = false;
		} else {
			for (var12 = 1; var12 > 0; var12++) {
			}
		}
		int var15 = 256 - arg5;
		for (var12 = -arg0; var12 < 0; var12++) {
			for (int var13 = -arg2; var13 < 0; var13++) {
				if (arg4[arg6++] == 0) {
					arg1++;
				} else {
					int var14 = arg3[arg1];
					arg3[arg1++] = (((var14 & 0xFF00FF) * var15 & 0xFF00FF00) + ((var14 & 0xFF00) * var15 & 0xFF0000) >> 8) + var17;
				}
			}
			arg1 += arg7;
			arg6 += arg8;
		}
	}

	static {
		String var0 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
		for (int var1 = 0; var1 < 256; var1++) {
			int var2 = var0.indexOf(var1);
			if (var2 == -1) {
				var2 = 74;
			}
			CHAR_LOOKUP[var1] = var2;
		}
	}
}
