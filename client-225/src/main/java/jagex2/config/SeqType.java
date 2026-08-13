package jagex2.config;

import deob.ObfuscatedName;
import jagex2.graphics.AnimFrame;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("jc")
public class SeqType {

	@ObfuscatedName("jc.a")
	public static boolean field1025 = true;

	@ObfuscatedName("jc.b")
	public static int field1026 = 473;

	@ObfuscatedName("jc.c")
	public static int count;

	@ObfuscatedName("jc.d")
	public static SeqType[] instances;

	@ObfuscatedName("jc.e")
	public int frameCount;

	@ObfuscatedName("jc.f")
	public int[] frames;

	@ObfuscatedName("jc.g")
	public int[] iframes;

	@ObfuscatedName("jc.h")
	public int[] delay;

	@ObfuscatedName("jc.i")
	public int replayoff = -1;

	@ObfuscatedName("jc.j")
	public int[] walkmerge;

	@ObfuscatedName("jc.k")
	public boolean stretches = false;

	@ObfuscatedName("jc.l")
	public int priority = 5;

	@ObfuscatedName("jc.m")
	public int righthand = -1;

	@ObfuscatedName("jc.n")
	public int lefthand = -1;

	@ObfuscatedName("jc.o")
	public int replaycount = 99;

	@ObfuscatedName("jc.p")
	public static boolean field1040;

	@ObfuscatedName("jc.a(Lub;I)V")
	public static void unpack(Jagfile arg0, int arg1) {
		Packet var2 = new Packet(363, arg0.read("seq.dat", null, (byte) 2));
		if (arg1 <= 0) {
			field1025 = !field1025;
		}
		count = var2.g2();
		if (instances == null) {
			instances = new SeqType[count];
		}
		for (int var3 = 0; var3 < count; var3++) {
			if (instances[var3] == null) {
				instances[var3] = new SeqType();
			}
			instances[var3].decode(false, var2);
		}
	}

	@ObfuscatedName("jc.a(ZLkb;)V")
	public void decode(boolean arg0, Packet arg1) {
		int var3;
		if (arg0) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
		while (true) {
			while (true) {
				var3 = arg1.g1();
				if (var3 == 0) {
					if (this.frameCount == 0) {
						this.frameCount = 1;
						this.frames = new int[1];
						this.frames[0] = -1;
						this.iframes = new int[1];
						this.iframes[0] = -1;
						this.delay = new int[1];
						this.delay[0] = -1;
						return;
					}
					return;
				}
				int var4;
				if (var3 == 1) {
					this.frameCount = arg1.g1();
					this.frames = new int[this.frameCount];
					this.iframes = new int[this.frameCount];
					this.delay = new int[this.frameCount];
					for (var4 = 0; var4 < this.frameCount; var4++) {
						this.frames[var4] = arg1.g2();
						this.iframes[var4] = arg1.g2();
						if (this.iframes[var4] == 65535) {
							this.iframes[var4] = -1;
						}
						this.delay[var4] = arg1.g2();
						if (this.delay[var4] == 0) {
							this.delay[var4] = AnimFrame.instances[this.frames[var4]].delay;
						}
						if (this.delay[var4] == 0) {
							this.delay[var4] = 1;
						}
					}
				} else if (var3 == 2) {
					this.replayoff = arg1.g2();
				} else if (var3 == 3) {
					var4 = arg1.g1();
					this.walkmerge = new int[var4 + 1];
					for (int var5 = 0; var5 < var4; var5++) {
						this.walkmerge[var5] = arg1.g1();
					}
					this.walkmerge[var4] = 9999999;
				} else if (var3 == 4) {
					this.stretches = true;
				} else if (var3 == 5) {
					this.priority = arg1.g1();
				} else if (var3 == 6) {
					this.righthand = arg1.g2();
				} else if (var3 == 7) {
					this.lefthand = arg1.g2();
				} else if (var3 == 8) {
					this.replaycount = arg1.g1();
				} else {
					System.out.println("Error unrecognised seq config code: " + var3);
				}
			}
		}
	}
}
