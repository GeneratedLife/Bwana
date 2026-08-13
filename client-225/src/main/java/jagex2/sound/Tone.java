package jagex2.sound;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("zb")
public class Tone {

	@ObfuscatedName("zb.a")
	public static int field773 = 8;

	@ObfuscatedName("zb.b")
	public int field774 = -15143;

	@ObfuscatedName("zb.c")
	public Envelope frequencyBase;

	@ObfuscatedName("zb.d")
	public Envelope amplitudeBase;

	@ObfuscatedName("zb.e")
	public Envelope frequencyModRate;

	@ObfuscatedName("zb.f")
	public Envelope frequencyModRange;

	@ObfuscatedName("zb.g")
	public Envelope amplitudeModRate;

	@ObfuscatedName("zb.h")
	public Envelope amplitudeModRange;

	@ObfuscatedName("zb.i")
	public Envelope release;

	@ObfuscatedName("zb.j")
	public Envelope attack;

	@ObfuscatedName("zb.k")
	public int[] harmonicVolume = new int[5];

	@ObfuscatedName("zb.l")
	public int[] harmonicSemitone = new int[5];

	@ObfuscatedName("zb.m")
	public int[] harmonicDelay = new int[5];

	@ObfuscatedName("zb.n")
	public int reverbDelay;

	@ObfuscatedName("zb.o")
	public int reverbVolume = 100;

	@ObfuscatedName("zb.p")
	public int length = 500;

	@ObfuscatedName("zb.q")
	public int start;

	@ObfuscatedName("zb.r")
	public static int[] buffer;

	@ObfuscatedName("zb.s")
	public static int[] noise;

	@ObfuscatedName("zb.t")
	public static int[] sin;

	@ObfuscatedName("zb.u")
	public static int[] tmpPhases = new int[5];

	@ObfuscatedName("zb.v")
	public static int[] tmpDelays = new int[5];

	@ObfuscatedName("zb.w")
	public static int[] tmpVolumes = new int[5];

	@ObfuscatedName("zb.x")
	public static int[] tmpSemitones = new int[5];

	@ObfuscatedName("zb.y")
	public static int[] tmpStarts = new int[5];

	@ObfuscatedName("zb.z")
	public static boolean field798;

	@ObfuscatedName("zb.a()V")
	public static final void init() {
		noise = new int[32768];
		for (int var0 = 0; var0 < 32768; var0++) {
			if (Math.random() > 0.5D) {
				noise[var0] = 1;
			} else {
				noise[var0] = -1;
			}
		}
		sin = new int[32768];
		for (int var1 = 0; var1 < 32768; var1++) {
			sin[var1] = (int) (Math.sin((double) var1 / 5215.1903D) * 16384.0D);
		}
		buffer = new int[220500];
	}

	@ObfuscatedName("zb.a(II)[I")
	public final int[] generate(int arg0, int arg1) {
		for (int var3 = 0; var3 < arg0; var3++) {
			buffer[var3] = 0;
		}
		if (arg1 < 10) {
			return buffer;
		}
		double var4 = (double) arg0 / ((double) arg1 + 0.0D);
		this.frequencyBase.reset(field773);
		this.amplitudeBase.reset(field773);
		int var6 = 0;
		int var7 = 0;
		int var8 = 0;
		if (this.frequencyModRate != null) {
			this.frequencyModRate.reset(field773);
			this.frequencyModRange.reset(field773);
			var6 = (int) ((double) (this.frequencyModRate.end - this.frequencyModRate.start) * 32.768D / var4);
			var7 = (int) ((double) this.frequencyModRate.start * 32.768D / var4);
		}
		int var9 = 0;
		int var10 = 0;
		int var11 = 0;
		if (this.amplitudeModRate != null) {
			this.amplitudeModRate.reset(field773);
			this.amplitudeModRange.reset(field773);
			var9 = (int) ((double) (this.amplitudeModRate.end - this.amplitudeModRate.start) * 32.768D / var4);
			var10 = (int) ((double) this.amplitudeModRate.start * 32.768D / var4);
		}
		for (int var12 = 0; var12 < 5; var12++) {
			if (this.harmonicVolume[var12] != 0) {
				tmpPhases[var12] = 0;
				tmpDelays[var12] = (int) ((double) this.harmonicDelay[var12] * var4);
				tmpVolumes[var12] = (this.harmonicVolume[var12] << 14) / 100;
				tmpSemitones[var12] = (int) ((double) (this.frequencyBase.end - this.frequencyBase.start) * 32.768D * Math.pow(1.0057929410678534D, (double) this.harmonicSemitone[var12]) / var4);
				tmpStarts[var12] = (int) ((double) this.frequencyBase.start * 32.768D / var4);
			}
		}
		int var14;
		int var15;
		int var17;
		for (int var13 = 0; var13 < arg0; var13++) {
			var14 = this.frequencyBase.evaluate(true, arg0);
			var15 = this.amplitudeBase.evaluate(true, arg0);
			int var16;
			if (this.frequencyModRate != null) {
				var16 = this.frequencyModRate.evaluate(true, arg0);
				var17 = this.frequencyModRange.evaluate(true, arg0);
				var14 += this.generate(-15143, var17, var8, this.frequencyModRate.form) >> 1;
				var8 += (var16 * var6 >> 16) + var7;
			}
			if (this.amplitudeModRate != null) {
				var16 = this.amplitudeModRate.evaluate(true, arg0);
				var17 = this.amplitudeModRange.evaluate(true, arg0);
				var15 = var15 * ((this.generate(-15143, var17, var11, this.amplitudeModRate.form) >> 1) + 32768) >> 15;
				var11 += (var16 * var9 >> 16) + var10;
			}
			for (var16 = 0; var16 < 5; var16++) {
				if (this.harmonicVolume[var16] != 0) {
					var17 = var13 + tmpDelays[var16];
					if (var17 < arg0) {
						buffer[var17] += this.generate(-15143, var15 * tmpVolumes[var16] >> 15, tmpPhases[var16], this.frequencyBase.form);
						tmpPhases[var16] += (var14 * tmpSemitones[var16] >> 16) + tmpStarts[var16];
					}
				}
			}
		}
		if (this.release != null) {
			this.release.reset(field773);
			this.attack.reset(field773);
			var14 = 0;
			boolean var20 = false;
			boolean var21 = true;
			for (var17 = 0; var17 < arg0; var17++) {
				int var18 = this.release.evaluate(true, arg0);
				int var19 = this.attack.evaluate(true, arg0);
				if (var21) {
					var15 = this.release.start + ((this.release.end - this.release.start) * var18 >> 8);
				} else {
					var15 = this.release.start + ((this.release.end - this.release.start) * var19 >> 8);
				}
				var14 += 256;
				if (var14 >= var15) {
					var14 = 0;
					var21 = !var21;
				}
				if (var21) {
					buffer[var17] = 0;
				}
			}
		}
		if (this.reverbDelay > 0 && this.reverbVolume > 0) {
			var14 = (int) ((double) this.reverbDelay * var4);
			for (var15 = var14; var15 < arg0; var15++) {
				buffer[var15] += buffer[var15 - var14] * this.reverbVolume / 100;
			}
		}
		for (var14 = 0; var14 < arg0; var14++) {
			if (buffer[var14] < -32768) {
				buffer[var14] = -32768;
			}
			if (buffer[var14] > 32767) {
				buffer[var14] = 32767;
			}
		}
		return buffer;
	}

	@ObfuscatedName("zb.a(IIII)I")
	public final int generate(int arg0, int arg1, int arg2, int arg3) {
		if (arg0 != this.field774) {
			return 2;
		} else if (arg3 == 1) {
			return (arg2 & 0x7FFF) < 16384 ? arg1 : -arg1;
		} else if (arg3 == 2) {
			return sin[arg2 & 0x7FFF] * arg1 >> 14;
		} else if (arg3 == 3) {
			return ((arg2 & 0x7FFF) * arg1 >> 14) - arg1;
		} else if (arg3 == 4) {
			return noise[arg2 / 2607 & 0x7FFF] * arg1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("zb.a(ZLkb;)V")
	public final void read(boolean arg0, Packet arg1) {
		this.frequencyBase = new Envelope();
		this.frequencyBase.read(false, arg1);
		this.amplitudeBase = new Envelope();
		this.amplitudeBase.read(false, arg1);
		int var3 = arg1.g1();
		if (arg0) {
			throw new NullPointerException();
		}
		if (var3 != 0) {
			arg1.pos--;
			this.frequencyModRate = new Envelope();
			this.frequencyModRate.read(false, arg1);
			this.frequencyModRange = new Envelope();
			this.frequencyModRange.read(false, arg1);
		}
		var3 = arg1.g1();
		if (var3 != 0) {
			arg1.pos--;
			this.amplitudeModRate = new Envelope();
			this.amplitudeModRate.read(false, arg1);
			this.amplitudeModRange = new Envelope();
			this.amplitudeModRange.read(false, arg1);
		}
		var3 = arg1.g1();
		if (var3 != 0) {
			arg1.pos--;
			this.release = new Envelope();
			this.release.read(false, arg1);
			this.attack = new Envelope();
			this.attack.read(false, arg1);
		}
		for (int var4 = 0; var4 < 10; var4++) {
			int var5 = arg1.gsmarts();
			if (var5 == 0) {
				break;
			}
			this.harmonicVolume[var4] = var5;
			this.harmonicSemitone[var4] = arg1.gsmart();
			this.harmonicDelay[var4] = arg1.gsmarts();
		}
		this.reverbDelay = arg1.gsmarts();
		this.reverbVolume = arg1.gsmarts();
		this.length = arg1.g2();
		this.start = arg1.g2();
	}
}
