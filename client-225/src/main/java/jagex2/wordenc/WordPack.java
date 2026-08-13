package jagex2.wordenc;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("wb")
public class WordPack {

	@ObfuscatedName("wb.a")
	public static char[] charBuffer = new char[100];

	@ObfuscatedName("wb.b")
	public static char[] TABLE = new char[] { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '£', '$', '%', '"', '[', ']' };

	@ObfuscatedName("wb.a(Lkb;II)Ljava/lang/String;")
	public static String unpack(Packet arg0, int arg1, int arg2) {
		int var3 = 0;
		int var4 = -1;
		int var9 = 43 / arg1;
		int var7;
		for (int var5 = 0; var5 < arg2; var5++) {
			int var6 = arg0.g1();
			var7 = var6 >> 4 & 0xF;
			if (var4 != -1) {
				charBuffer[var3++] = TABLE[(var4 << 4) + var7 - 195];
				var4 = -1;
			} else if (var7 < 13) {
				charBuffer[var3++] = TABLE[var7];
			} else {
				var4 = var7;
			}
			var7 = var6 & 0xF;
			if (var4 != -1) {
				charBuffer[var3++] = TABLE[(var4 << 4) + var7 - 195];
				var4 = -1;
			} else if (var7 < 13) {
				charBuffer[var3++] = TABLE[var7];
			} else {
				var4 = var7;
			}
		}
		boolean var10 = true;
		for (var7 = 0; var7 < var3; var7++) {
			char var8 = charBuffer[var7];
			if (var10 && var8 >= 'a' && var8 <= 'z') {
				charBuffer[var7] = (char) (charBuffer[var7] + -32);
				var10 = false;
			}
			if (var8 == '.' || var8 == '!') {
				var10 = true;
			}
		}
		return new String(charBuffer, 0, var3);
	}

	@ObfuscatedName("wb.a(Lkb;ZLjava/lang/String;)V")
	public static void pack(Packet arg0, boolean arg1, String arg2) {
		if (arg2.length() > 80) {
			arg2 = arg2.substring(0, 80);
		}
		arg2 = arg2.toLowerCase();
		int var3 = -1;
		for (int var4 = 0; var4 < arg2.length(); var4++) {
			char var5 = arg2.charAt(var4);
			int var6 = 0;
			for (int var7 = 0; var7 < TABLE.length; var7++) {
				if (var5 == TABLE[var7]) {
					var6 = var7;
					break;
				}
			}
			if (var6 > 12) {
				var6 += 195;
			}
			if (var3 == -1) {
				if (var6 < 13) {
					var3 = var6;
				} else {
					arg0.p1(var6);
				}
			} else if (var6 < 13) {
				arg0.p1((var3 << 4) + var6);
				var3 = -1;
			} else {
				arg0.p1((var3 << 4) + (var6 >> 4));
				var3 = var6 & 0xF;
			}
		}
		if (arg1 && var3 != -1) {
			arg0.p1(var3 << 4);
		}
	}
}
