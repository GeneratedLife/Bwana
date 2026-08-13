package jagex2.config;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("rc")
public final class VarpType {

	@ObfuscatedName("rc.c")
	public static int numDefinitions;

	@ObfuscatedName("rc.d")
	public static VarpType[] list;

	@ObfuscatedName("rc.e")
	public static int field1179;

	@ObfuscatedName("rc.f")
	public static int[] field1180;

	@ObfuscatedName("rc.g")
	public String field1181;

	@ObfuscatedName("rc.h")
	public int field1182;

	@ObfuscatedName("rc.i")
	public int field1183;

	@ObfuscatedName("rc.j")
	public boolean field1184 = false;

	@ObfuscatedName("rc.k")
	public boolean field1185 = true;

	@ObfuscatedName("rc.l")
	public int clientcode;

	@ObfuscatedName("rc.m")
	public boolean field1187 = false;

	@ObfuscatedName("rc.n")
	public int field1188;

	@ObfuscatedName("rc.o")
	public int field1189;

	@ObfuscatedName("rc.p")
	public boolean field1190 = false;

	@ObfuscatedName("rc.q")
	public int field1191 = -1;

	@ObfuscatedName("rc.a(ZLxb;)V")
	public static void init(JagFile arg1) {
		Packet var2 = new Packet(arg1.read("varp.dat", null));
		field1179 = 0;
		numDefinitions = var2.g2();
		if (list == null) {
			list = new VarpType[numDefinitions];
		}
		if (field1180 == null) {
			field1180 = new int[numDefinitions];
		}
		for (int var3 = 0; var3 < numDefinitions; var3++) {
			if (list[var3] == null) {
				list[var3] = new VarpType();
			}
			list[var3].decode(var2, var3);
		}
		if (var2.data != var2.pos.length) {
			System.out.println("varptype load mismatch");
		}
	}

	@ObfuscatedName("rc.a(Llb;BI)V")
	public void decode(Packet arg0, int arg2) {
		while (true) {
			int var5 = arg0.g1();
			if (var5 == 0) {
				return;
			}
			if (var5 == 1) {
				field1182 = arg0.g1();
			} else if (var5 == 2) {
				field1183 = arg0.g1();
			} else if (var5 == 3) {
				field1184 = true;
				field1180[field1179++] = arg2;
			} else if (var5 == 4) {
				field1185 = false;
			} else if (var5 == 5) {
				clientcode = arg0.g2();
			} else if (var5 == 6) {
				field1187 = true;
			} else if (var5 == 7) {
				field1188 = arg0.g4();
			} else if (var5 == 8) {
				field1189 = 1;
				field1190 = true;
			} else if (var5 == 10) {
				field1181 = arg0.gjstr();
			} else if (var5 == 11) {
				field1190 = true;
			} else if (var5 == 12) {
				field1191 = arg0.g4();
			} else if (var5 == 13) {
				field1189 = 2;
			} else {
				System.out.println("Error unrecognised config code: " + var5);
			}
		}
	}
}
