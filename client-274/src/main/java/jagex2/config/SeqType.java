package jagex2.config;

import deob.ObfuscatedName;
import jagex2.dash3d.AnimFrame;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("nc")
public final class SeqType {

	@ObfuscatedName("nc.a")
	public static int numDefinitions;

	@ObfuscatedName("nc.b")
	public static SeqType[] list;

	@ObfuscatedName("nc.c")
	public int numFrames;

	@ObfuscatedName("nc.d")
	public int[] frames;

	@ObfuscatedName("nc.e")
	public int[] iframes;

	@ObfuscatedName("nc.f")
	public int[] delay;

	@ObfuscatedName("nc.g")
	public int loops = -1;

	@ObfuscatedName("nc.h")
	public int[] walkmerge;

	@ObfuscatedName("nc.i")
	public boolean reachforward = false;

	@ObfuscatedName("nc.j")
	public int priority = 5;

	@ObfuscatedName("nc.k")
	public int replaceheldleft = -1;

	@ObfuscatedName("nc.l")
	public int replaceheldright = -1;

	@ObfuscatedName("nc.m")
	public int maxloops = 99;

	@ObfuscatedName("nc.n")
	public int preanim_move = -1;

	@ObfuscatedName("nc.o")
	public int postanim_move = -1;

	@ObfuscatedName("nc.p")
	public int duplicatebehaviour;

	@ObfuscatedName("nc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("seq.dat", null));
		numDefinitions = var2.g2();
		if (list == null) {
			list = new SeqType[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new SeqType();
			}
			list[var3].decode(var2);
		}
	}

	@ObfuscatedName("nc.a(II)I")
	public int getDelay(int arg0) {
		int var3 = delay[arg0];
		if (var3 == 0) {
			AnimFrame var4 = AnimFrame.get(frames[arg0]);
			if (var4 != null) {
				var3 = delay[arg0] = var4.delay;
			}
		}
		if (var3 == 0) {
			var3 = 1;
		}
		return var3;
	}

	@ObfuscatedName("nc.a(BLlb;)V")
	public void decode(Packet arg1) {
		while (true) {
			while (true) {
				int var4 = arg1.g1();
				if (var4 == 0) {
					if (numFrames == 0) {
						numFrames = 1;
						frames = new int[1];
						frames[0] = -1;
						iframes = new int[1];
						iframes[0] = -1;
						delay = new int[1];
						delay[0] = -1;
					}
					if (preanim_move == -1) {
						if (walkmerge == null) {
							preanim_move = 0;
						} else {
							preanim_move = 2;
						}
					}
					if (postanim_move == -1) {
						if (walkmerge != null) {
							postanim_move = 2;
							return;
						}
						postanim_move = 0;
						return;
					}
					return;
				}
				if (var4 == 1) {
					numFrames = arg1.g1();
					frames = new int[numFrames];
					iframes = new int[numFrames];
					delay = new int[numFrames];
					for (int var5 = 0; var5 < numFrames; var5++) {
						frames[var5] = arg1.g2();
						iframes[var5] = arg1.g2();
						if (iframes[var5] == 65535) {
							iframes[var5] = -1;
						}
						delay[var5] = arg1.g2();
					}
				} else if (var4 == 2) {
					loops = arg1.g2();
				} else if (var4 == 3) {
					int var6 = arg1.g1();
					walkmerge = new int[var6 + 1];
					for (int var7 = 0; var7 < var6; var7++) {
						walkmerge[var7] = arg1.g1();
					}
					walkmerge[var6] = 9999999;
				} else if (var4 == 4) {
					reachforward = true;
				} else if (var4 == 5) {
					priority = arg1.g1();
				} else if (var4 == 6) {
					replaceheldleft = arg1.g2();
				} else if (var4 == 7) {
					replaceheldright = arg1.g2();
				} else if (var4 == 8) {
					maxloops = arg1.g1();
				} else if (var4 == 9) {
					preanim_move = arg1.g1();
				} else if (var4 == 10) {
					postanim_move = arg1.g1();
				} else if (var4 == 11) {
					duplicatebehaviour = arg1.g1();
				} else {
					System.out.println("Error unrecognised seq config code: " + var4);
				}
			}
		}
	}
}
