package jagex2.config;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("lc")
public class VarpType {

	@ObfuscatedName("lc.a")
	public static int field1057 = 473;

	@ObfuscatedName("lc.b")
	public static int field1058 = 13703;

	@ObfuscatedName("lc.c")
	public static int count;

	@ObfuscatedName("lc.d")
	public static VarpType[] instances;

	@ObfuscatedName("lc.e")
	public static int code3Count;

	@ObfuscatedName("lc.f")
	public static int[] code3;

	@ObfuscatedName("lc.g")
	public String code10;

	@ObfuscatedName("lc.h")
	public int code1;

	@ObfuscatedName("lc.i")
	public int code2;

	@ObfuscatedName("lc.j")
	public boolean hasCode3 = false;

	@ObfuscatedName("lc.k")
	public boolean code4 = true;

	@ObfuscatedName("lc.l")
	public int clientcode;

	@ObfuscatedName("lc.m")
	public boolean code6 = false;

	@ObfuscatedName("lc.n")
	public int code7;

	@ObfuscatedName("lc.o")
	public boolean code8 = false;

	@ObfuscatedName("lc.a(Lub;I)V")
	public static void unpack(Jagfile arg0, int arg1) {
		Packet var2 = new Packet(363, arg0.read("varp.dat", null, (byte) 2));
		code3Count = 0;
		int var4 = 28 / arg1;
		count = var2.g2();
		if (instances == null) {
			instances = new VarpType[count];
		}
		if (code3 == null) {
			code3 = new int[count];
		}
		for (int var3 = 0; var3 < count; var3++) {
			if (instances[var3] == null) {
				instances[var3] = new VarpType();
			}
			instances[var3].decode(field1058, var3, var2);
		}
	}

	@ObfuscatedName("lc.a(IILkb;)V")
	public void decode(int arg0, int arg1, Packet arg2) {
		if (arg0 != 13703) {
			return;
		}
		while (true) {
			int var4 = arg2.g1();
			if (var4 == 0) {
				return;
			}
			if (var4 == 1) {
				this.code1 = arg2.g1();
			} else if (var4 == 2) {
				this.code2 = arg2.g1();
			} else if (var4 == 3) {
				this.hasCode3 = true;
				code3[code3Count++] = arg1;
			} else if (var4 == 4) {
				this.code4 = false;
			} else if (var4 == 5) {
				this.clientcode = arg2.g2();
			} else if (var4 == 6) {
				this.code6 = true;
			} else if (var4 == 7) {
				this.code7 = arg2.g4();
			} else if (var4 == 8) {
				this.code8 = true;
			} else if (var4 == 10) {
				this.code10 = arg2.gjstr();
			} else {
				System.out.println("Error unrecognised config code: " + var4);
			}
		}
	}
}
