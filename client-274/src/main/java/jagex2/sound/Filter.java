package jagex2.sound;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("bc")
public final class Filter {

	@ObfuscatedName("bc.d")
	public final int[] pairs = new int[2];

	@ObfuscatedName("bc.e")
	public final int[][][] frequencies = new int[2][2][4];

	@ObfuscatedName("bc.f")
	public final int[][][] ranges = new int[2][2][4];

	@ObfuscatedName("bc.g")
	public final int[] unities = new int[2];

	@ObfuscatedName("bc.h")
	public static final float[][] coeff = new float[2][8];

	@ObfuscatedName("bc.i")
	public static final int[][] coeffInt = new int[2][8];

	@ObfuscatedName("bc.j")
	public static float reduceCoeff;

	@ObfuscatedName("bc.k")
	public static int reduceCoeffInt;

	@ObfuscatedName("bc.a(IIIF)F")
	public float radius(int arg0, int arg1, float arg2) {
		float var4 = (float) ranges[arg1][0][arg0] + arg2 * (float) (ranges[arg1][1][arg0] - ranges[arg1][0][arg0]);
		float var5 = var4 * 0.0015258789F;
		return 1.0F - (float) Math.pow(10.0D, (double) (-var5 / 20.0F));
	}

	@ObfuscatedName("bc.a(BF)F")
	public float frequency(float arg1) {
		float var3 = (float) Math.pow(2.0D, (double) arg1) * 32.703197F;
		return var3 * 3.1415927F / 11025.0F;
	}

	@ObfuscatedName("bc.a(BIIF)F")
	public float frequency(int arg1, int arg2, float arg3) {
		float var5 = (float) frequencies[arg1][0][arg2] + arg3 * (float) (frequencies[arg1][1][arg2] - frequencies[arg1][0][arg2]);
		float var6 = var5 * 1.2207031E-4F;
		return frequency(var6);
	}

	@ObfuscatedName("bc.a(IFB)I")
	public int calculateCoeffs(int arg0, float arg1) {
		if (arg0 == 0) {
			float var4 = (float) unities[0] + (float) (unities[1] - unities[0]) * arg1;
			float var5 = var4 * 0.0030517578F;
			reduceCoeff = (float) Math.pow(0.1D, (double) (var5 / 20.0F));
			reduceCoeffInt = (int) (reduceCoeff * 65536.0F);
		}
		if (pairs[arg0] == 0) {
			return 0;
		}
		float var6 = radius(0, arg0, arg1);
		coeff[arg0][0] = -2.0F * var6 * (float) Math.cos((double) frequency(arg0, 0, arg1));
		coeff[arg0][1] = var6 * var6;
		for (int var7 = 1; var7 < pairs[arg0]; var7++) {
			float var8 = radius(var7, arg0, arg1);
			float var9 = -2.0F * var8 * (float) Math.cos((double) frequency(arg0, var7, arg1));
			float var10 = var8 * var8;
			coeff[arg0][var7 * 2 + 1] = coeff[arg0][var7 * 2 - 1] * var10;
			coeff[arg0][var7 * 2] = coeff[arg0][var7 * 2 - 1] * var9 + coeff[arg0][var7 * 2 - 2] * var10;
			for (int var11 = var7 * 2 - 1; var11 >= 2; var11--) {
				coeff[arg0][var11] += coeff[arg0][var11 - 1] * var9 + coeff[arg0][var11 - 2] * var10;
			}
			coeff[arg0][1] += coeff[arg0][0] * var9 + var10;
			coeff[arg0][0] += var9;
		}
		if (arg0 == 0) {
			for (int var12 = 0; var12 < pairs[0] * 2; var12++) {
				coeff[0][var12] *= reduceCoeff;
			}
		}
		for (int var13 = 0; var13 < pairs[arg0] * 2; var13++) {
			coeffInt[arg0][var13] = (int) (coeff[arg0][var13] * 65536.0F);
		}
		return pairs[arg0] * 2;
	}

	@ObfuscatedName("bc.a(Llb;BLac;)V")
	public void load(Packet arg0, Envelope arg2) {
		int var4 = arg0.g1();
		pairs[0] = var4 >> 4;
		pairs[1] = var4 & 0xF;
		if (var4 == 0) {
			unities[0] = unities[1] = 0;
			return;
		}
		unities[0] = arg0.g2();
		unities[1] = arg0.g2();
		int var6 = arg0.g1();
		for (int var7 = 0; var7 < 2; var7++) {
			for (int var8 = 0; var8 < pairs[var7]; var8++) {
				frequencies[var7][0][var8] = arg0.g2();
				ranges[var7][0][var8] = arg0.g2();
			}
		}
		for (int var9 = 0; var9 < 2; var9++) {
			for (int var10 = 0; var10 < pairs[var9]; var10++) {
				if ((var6 & 0x1 << var9 * 4 << var10) == 0) {
					frequencies[var9][1][var10] = frequencies[var9][0][var10];
					ranges[var9][1][var10] = ranges[var9][0][var10];
				} else {
					frequencies[var9][1][var10] = arg0.g2();
					ranges[var9][1][var10] = arg0.g2();
				}
			}
		}
		if (var6 != 0 || unities[1] != unities[0]) {
			arg2.loadPoints(arg0);
		}
	}
}
