package jagex2.wordfilter;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("zb")
public final class WordPack {

	@ObfuscatedName("zb.a")
	public static final char[] builder = new char[100];

	@ObfuscatedName("zb.b")
	public static final char[] TABLE = new char[] { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '£', '$', '%', '"', '[', ']' };

	@ObfuscatedName("zb.a(BILlb;)Ljava/lang/String;")
	public static String unpack(int arg1, Packet arg2) {
		int var3 = 0;
		int var4 = -1;
		for (int var5 = 0; var5 < arg1; var5++) {
			int var6 = arg2.g1();
			int var7 = var6 >> 4 & 0xF;
			if (var4 != -1) {
				builder[var3++] = TABLE[(var4 << 4) + var7 - 195];
				var4 = -1;
			} else if (var7 < 13) {
				builder[var3++] = TABLE[var7];
			} else {
				var4 = var7;
			}
			int var8 = var6 & 0xF;
			if (var4 != -1) {
				builder[var3++] = TABLE[(var4 << 4) + var8 - 195];
				var4 = -1;
			} else if (var8 < 13) {
				builder[var3++] = TABLE[var8];
			} else {
				var4 = var8;
			}
		}
		boolean var9 = true;
		for (int var10 = 0; var10 < var3; var10++) {
			char var11 = builder[var10];
			if (var9 && var11 >= 'a' && var11 <= 'z') {
				builder[var10] = (char) (builder[var10] - 32);
				var9 = false;
			}
			if (var11 == '.' || var11 == '!') {
				var9 = true;
			}
		}
		return new String(builder, 0, var3);
	}

	@ObfuscatedName("zb.a(Ljava/lang/String;BLlb;)V")
	public static void pack(String arg0, Packet arg2) {
		if (arg0.length() > 80) {
			arg0 = arg0.substring(0, 80);
		}
		String var3 = arg0.toLowerCase();
		int var5 = -1;
		for (int var6 = 0; var6 < var3.length(); var6++) {
			char var7 = var3.charAt(var6);
			int var8 = 0;
			for (int var9 = 0; var9 < TABLE.length; var9++) {
				if (var7 == TABLE[var9]) {
					var8 = var9;
					break;
				}
			}
			if (var8 > 12) {
				var8 += 195;
			}
			if (var5 == -1) {
				if (var8 < 13) {
					var5 = var8;
				} else {
					arg2.p1(var8);
				}
			} else if (var8 < 13) {
				arg2.p1((var5 << 4) + var8);
				var5 = -1;
			} else {
				arg2.p1((var5 << 4) + (var8 >> 4));
				var5 = var8 & 0xF;
			}
		}
		if (var5 != -1) {
			arg2.p1(var5 << 4);
		}
	}
}
