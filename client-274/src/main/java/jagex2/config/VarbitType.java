package jagex2.config;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("qc")
public final class VarbitType {

	@ObfuscatedName("qc.c")
	public static int numDefinitions;

	@ObfuscatedName("qc.d")
	public static VarbitType[] list;

	@ObfuscatedName("qc.e")
	public String debugname;

	@ObfuscatedName("qc.f")
	public int basevar;

	@ObfuscatedName("qc.g")
	public int startbit;

	@ObfuscatedName("qc.h")
	public int endbit;

	@ObfuscatedName("qc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("varbit.dat", null));
		numDefinitions = var2.g2();
		if (list == null) {
			list = new VarbitType[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new VarbitType();
			}
			list[var3].decode(var2, var3);
		}
		if (var2.data != var2.pos.length) {
			System.out.println("varbit load mismatch");
		}
	}

	@ObfuscatedName("qc.a(Llb;BI)V")
	public void decode(Packet arg0, int arg2) {
		while (true) {
			int var5 = arg0.g1();
			if (var5 == 0) {
				return;
			}
			if (var5 == 1) {
				basevar = arg0.g2();
				startbit = arg0.g1();
				endbit = arg0.g1();
			} else if (var5 == 10) {
				debugname = arg0.gjstr();
			} else {
				System.out.println("Error unrecognised config code: " + var5);
			}
		}
	}
}
