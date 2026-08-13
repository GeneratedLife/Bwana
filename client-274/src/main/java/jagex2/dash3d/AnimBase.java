package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("f")
public final class AnimBase {

	@ObfuscatedName("f.a")
	public final int size;

	@ObfuscatedName("f.b")
	public final int[] type;

	@ObfuscatedName("f.c")
	public final int[][] labels;

	public AnimBase(Packet arg1) {
		size = arg1.g1();
		type = new int[size];
		labels = new int[size][];
		for (int var4 = 0; var4 < size; var4++) {
			type[var4] = arg1.g1();
		}
		for (int var5 = 0; var5 < size; var5++) {
			int var6 = arg1.g1();
			labels[var5] = new int[var6];
			for (int var7 = 0; var7 < var6; var7++) {
				labels[var5][var7] = arg1.g1();
			}
		}
	}
}
