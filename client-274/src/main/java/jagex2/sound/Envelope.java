package jagex2.sound;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("ac")
public final class Envelope {

	@ObfuscatedName("ac.c")
	public int length;

	@ObfuscatedName("ac.d")
	public int[] shapeDelta;

	@ObfuscatedName("ac.e")
	public int[] shapePeak;

	@ObfuscatedName("ac.f")
	public int start;

	@ObfuscatedName("ac.g")
	public int end;

	@ObfuscatedName("ac.h")
	public int form;

	@ObfuscatedName("ac.i")
	public int threshold;

	@ObfuscatedName("ac.j")
	public int position;

	@ObfuscatedName("ac.k")
	public int delta;

	@ObfuscatedName("ac.l")
	public int amplitude;

	@ObfuscatedName("ac.m")
	public int ticks;

	@ObfuscatedName("ac.a(BLlb;)V")
	public void load(Packet arg1) {
		form = arg1.g1();
		start = arg1.g4();
		end = arg1.g4();
		loadPoints(arg1);
	}

	@ObfuscatedName("ac.a(Llb;I)V")
	public void loadPoints(Packet arg0) {
		length = arg0.g1();
		shapeDelta = new int[length];
		shapePeak = new int[length];
		for (int var3 = 0; var3 < length; var3++) {
			shapeDelta[var3] = arg0.g2();
			shapePeak[var3] = arg0.g2();
		}
	}

	@ObfuscatedName("ac.a(I)V")
	public void genInit() {
		threshold = 0;
		position = 0;
		delta = 0;
		amplitude = 0;
		ticks = 0;
	}

	@ObfuscatedName("ac.a(BI)I")
	public int genNext(int arg1) {
		if (ticks >= threshold) {
			amplitude = shapePeak[position++] << 15;
			if (position >= length) {
				position = length - 1;
			}
			threshold = (int) ((double) shapeDelta[position] / 65536.0D * (double) arg1);
			if (threshold > ticks) {
				delta = ((shapePeak[position] << 15) - amplitude) / (threshold - ticks);
			}
		}
		amplitude += delta;
		ticks++;
		return amplitude - delta >> 15;
	}
}
