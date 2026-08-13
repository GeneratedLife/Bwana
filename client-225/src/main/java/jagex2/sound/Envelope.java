package jagex2.sound;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("xb")
public class Envelope {

	@ObfuscatedName("xb.a")
	public int length;

	@ObfuscatedName("xb.b")
	public int[] shapeDelta;

	@ObfuscatedName("xb.c")
	public int[] shapePeak;

	@ObfuscatedName("xb.d")
	public int start;

	@ObfuscatedName("xb.e")
	public int end;

	@ObfuscatedName("xb.f")
	public int form;

	@ObfuscatedName("xb.g")
	public int threshold;

	@ObfuscatedName("xb.h")
	public int position;

	@ObfuscatedName("xb.i")
	public int delta;

	@ObfuscatedName("xb.j")
	public int amplitude;

	@ObfuscatedName("xb.k")
	public int ticks;

	@ObfuscatedName("xb.a(ZLkb;)V")
	public final void read(boolean arg0, Packet arg1) {
		this.form = arg1.g1();
		this.start = arg1.g4();
		this.end = arg1.g4();
		this.length = arg1.g1();
		this.shapeDelta = new int[this.length];
		this.shapePeak = new int[this.length];
		int var3;
		if (arg0) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
		for (var3 = 0; var3 < this.length; var3++) {
			this.shapeDelta[var3] = arg1.g2();
			this.shapePeak[var3] = arg1.g2();
		}
	}

	@ObfuscatedName("xb.a(I)V")
	public final void reset(int arg0) {
		this.threshold = 0;
		this.position = 0;
		this.delta = 0;
		this.amplitude = 0;
		if (arg0 >= 8 && arg0 <= 8) {
			this.ticks = 0;
		}
	}

	@ObfuscatedName("xb.a(ZI)I")
	public final int evaluate(boolean arg0, int arg1) {
		if (!arg0) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		if (this.ticks >= this.threshold) {
			this.amplitude = this.shapePeak[this.position++] << 15;
			if (this.position >= this.length) {
				this.position = this.length - 1;
			}
			this.threshold = (int) ((double) this.shapeDelta[this.position] / 65536.0D * (double) arg1);
			if (this.threshold > this.ticks) {
				this.delta = ((this.shapePeak[this.position] << 15) - this.amplitude) / (this.threshold - this.ticks);
			}
		}
		this.amplitude += this.delta;
		this.ticks++;
		return this.amplitude - this.delta >> 15;
	}
}
