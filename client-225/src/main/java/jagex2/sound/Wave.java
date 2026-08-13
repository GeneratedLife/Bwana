package jagex2.sound;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("yb")
public class Wave {

	@ObfuscatedName("yb.a")
	public boolean field764 = true;

	@ObfuscatedName("yb.b")
	public static int field765 = 473;

	@ObfuscatedName("yb.c")
	public static Wave[] tracks = new Wave[1000];

	@ObfuscatedName("yb.d")
	public static int[] delays = new int[1000];

	@ObfuscatedName("yb.e")
	public static byte[] waveBytes;

	@ObfuscatedName("yb.f")
	public static Packet waveBuffer;

	@ObfuscatedName("yb.g")
	public Tone[] tones = new Tone[10];

	@ObfuscatedName("yb.h")
	public int loopBegin;

	@ObfuscatedName("yb.i")
	public int loopEnd;

	@ObfuscatedName("yb.a(Lkb;I)V")
	public static final void unpack(Packet arg0, int arg1) {
		waveBytes = new byte[441000];
		waveBuffer = new Packet(363, waveBytes);
		int var3 = 87 / arg1;
		Tone.init();
		while (true) {
			int var2 = arg0.g2();
			if (var2 == 65535) {
				return;
			}
			tracks[var2] = new Wave();
			tracks[var2].read(false, arg0);
			delays[var2] = tracks[var2].trim((byte) 7);
		}
	}

	@ObfuscatedName("yb.a(BII)Lkb;")
	public static final Packet generate(byte arg0, int arg1, int arg2) {
		if (arg0 != -16) {
			field765 = -83;
		}
		if (tracks[arg2] == null) {
			return null;
		} else {
			Wave var3 = tracks[arg2];
			return var3.getWave(true, arg1);
		}
	}

	@ObfuscatedName("yb.a(ZLkb;)V")
	public final void read(boolean arg0, Packet arg1) {
		for (int var3 = 0; var3 < 10; var3++) {
			int var4 = arg1.g1();
			if (var4 != 0) {
				arg1.pos--;
				this.tones[var3] = new Tone();
				this.tones[var3].read(false, arg1);
			}
		}
		if (arg0) {
			field765 = -307;
		}
		this.loopBegin = arg1.g2();
		this.loopEnd = arg1.g2();
	}

	@ObfuscatedName("yb.a(B)I")
	public final int trim(byte arg0) {
		int var2 = 9999999;
		for (int var3 = 0; var3 < 10; var3++) {
			if (this.tones[var3] != null && this.tones[var3].start / 20 < var2) {
				var2 = this.tones[var3].start / 20;
			}
		}
		if (arg0 == 7) {
			boolean var5 = false;
		} else {
			field765 = -8;
		}
		if (this.loopBegin < this.loopEnd && this.loopBegin / 20 < var2) {
			var2 = this.loopBegin / 20;
		}
		if (var2 == 9999999 || var2 == 0) {
			return 0;
		}
		for (int var4 = 0; var4 < 10; var4++) {
			if (this.tones[var4] != null) {
				this.tones[var4].start -= var2 * 20;
			}
		}
		if (this.loopBegin < this.loopEnd) {
			this.loopBegin -= var2 * 20;
			this.loopEnd -= var2 * 20;
		}
		return var2;
	}

	@ObfuscatedName("yb.a(ZI)Lkb;")
	public final Packet getWave(boolean arg0, int arg1) {
		int var3 = this.generate(arg1);
		waveBuffer.pos = 0;
		waveBuffer.p4(1380533830);
		waveBuffer.ip4(false, var3 + 36);
		waveBuffer.p4(1463899717);
		waveBuffer.p4(1718449184);
		waveBuffer.ip4(false, 16);
		if (!arg0) {
			for (int var4 = 1; var4 > 0; var4++) {
			}
		}
		waveBuffer.ip2(this.field764, 1);
		waveBuffer.ip2(this.field764, 1);
		waveBuffer.ip4(false, 22050);
		waveBuffer.ip4(false, 22050);
		waveBuffer.ip2(this.field764, 1);
		waveBuffer.ip2(this.field764, 8);
		waveBuffer.p4(1684108385);
		waveBuffer.ip4(false, var3);
		waveBuffer.pos += var3;
		return waveBuffer;
	}

	@ObfuscatedName("yb.a(I)I")
	public final int generate(int arg0) {
		int var2 = 0;
		for (int var3 = 0; var3 < 10; var3++) {
			if (this.tones[var3] != null && this.tones[var3].length + this.tones[var3].start > var2) {
				var2 = this.tones[var3].length + this.tones[var3].start;
			}
		}
		if (var2 == 0) {
			return 0;
		}
		int var4 = var2 * 22050 / 1000;
		int var5 = this.loopBegin * 22050 / 1000;
		int var6 = this.loopEnd * 22050 / 1000;
		if (var5 < 0 || var5 > var4 || var6 < 0 || var6 > var4 || var5 >= var6) {
			arg0 = 0;
		}
		int var7 = var4 + (var6 - var5) * (arg0 - 1);
		for (int var8 = 44; var8 < var7 + 44; var8++) {
			waveBytes[var8] = -128;
		}
		int var10;
		int var11;
		int var13;
		for (int var9 = 0; var9 < 10; var9++) {
			if (this.tones[var9] != null) {
				var10 = this.tones[var9].length * 22050 / 1000;
				var11 = this.tones[var9].start * 22050 / 1000;
				int[] var12 = this.tones[var9].generate(var10, this.tones[var9].length);
				for (var13 = 0; var13 < var10; var13++) {
					waveBytes[var13 + var11 + 44] += (byte) (var12[var13] >> 8);
				}
			}
		}
		if (arg0 > 1) {
			var5 += 44;
			var6 += 44;
			var4 += 44;
			var7 += 44;
			var10 = var7 - var4;
			for (var11 = var4 - 1; var11 >= var6; var11--) {
				waveBytes[var11 + var10] = waveBytes[var11];
			}
			for (int var14 = 1; var14 < arg0; var14++) {
				var10 = (var6 - var5) * var14;
				for (var13 = var5; var13 < var6; var13++) {
					waveBytes[var13 + var10] = waveBytes[var13];
				}
			}
			var7 -= 44;
		}
		return var7;
	}
}
