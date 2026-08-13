package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("vb")
public class JString {

	@ObfuscatedName("vb.a")
	public static boolean field744;

	@ObfuscatedName("vb.b")
	public static byte field745 = 25;

	@ObfuscatedName("vb.c")
	public static int field746 = 3;

	@ObfuscatedName("vb.d")
	public static boolean field747 = true;

	@ObfuscatedName("vb.e")
	public static int field748 = 629;

	@ObfuscatedName("vb.f")
	public static char[] builder = new char[12];

	@ObfuscatedName("vb.g")
	public static char[] BASE37_LOOKUP = new char[] { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	@ObfuscatedName("vb.a(Ljava/lang/String;)J")
	public static long toBase37(String arg0) {
		long var1 = 0L;
		for (int var3 = 0; var3 < arg0.length() && var3 < 12; var3++) {
			char var4 = arg0.charAt(var3);
			var1 *= 37L;
			if (var4 >= 'A' && var4 <= 'Z') {
				var1 += var4 + 1 - 65;
			} else if (var4 >= 'a' && var4 <= 'z') {
				var1 += var4 + 1 - 97;
			} else if (var4 >= '0' && var4 <= '9') {
				var1 += var4 + 27 - 48;
			}
		}
		while (var1 % 37L == 0L && var1 != 0L) {
			var1 /= 37L;
		}
		return var1;
	}

	@ObfuscatedName("vb.a(JZ)Ljava/lang/String;")
	public static String fromBase37(long arg0, boolean arg1) {
		if (arg0 <= 0L || arg0 >= 6582952005840035281L) {
			return "invalid_name";
		} else if (arg0 % 37L == 0L) {
			return "invalid_name";
		} else {
			int var3 = 0;
			if (arg1) {
				field748 = -363;
			}
			while (arg0 != 0L) {
				long var4 = arg0;
				arg0 /= 37L;
				builder[11 - var3++] = BASE37_LOOKUP[(int) (var4 - arg0 * 37L)];
			}
			return new String(builder, 12 - var3, var3);
		}
	}

	@ObfuscatedName("vb.a(ILjava/lang/String;)J")
	public static long hashCode(int arg0, String arg1) {
		if (arg0 != 0) {
			throw new NullPointerException();
		}
		String var5 = arg1.toUpperCase();
		long var2 = 0L;
		for (int var4 = 0; var4 < var5.length(); var4++) {
			var2 = var2 * 61L + (long) var5.charAt(var4) - 32L;
			var2 = var2 + (var2 >> 56) & 0xFFFFFFFFFFFFFFL;
		}
		return var2;
	}

	@ObfuscatedName("vb.a(II)Ljava/lang/String;")
	public static String formatIPv4(int arg0, int arg1) {
		if (arg0 >= 0) {
			field744 = !field744;
		}
		return (arg1 >> 24 & 0xFF) + "." + (arg1 >> 16 & 0xFF) + "." + (arg1 >> 8 & 0xFF) + "." + (arg1 & 0xFF);
	}

	@ObfuscatedName("vb.b(ILjava/lang/String;)Ljava/lang/String;")
	public static String formatName(int arg0, String arg1) {
		if (arg0 != 0) {
			throw new NullPointerException();
		} else if (arg1.length() > 0) {
			char[] var2 = arg1.toCharArray();
			for (int var3 = 0; var3 < var2.length; var3++) {
				if (var2[var3] == '_') {
					var2[var3] = ' ';
					if (var3 + 1 < var2.length && var2[var3 + 1] >= 'a' && var2[var3 + 1] <= 'z') {
						var2[var3 + 1] = (char) (var2[var3 + 1] + 'A' - 97);
					}
				}
			}
			if (var2[0] >= 'a' && var2[0] <= 'z') {
				var2[0] = (char) (var2[0] + 'A' - 97);
			}
			return new String(var2);
		} else {
			return arg1;
		}
	}

	@ObfuscatedName("vb.a(Ljava/lang/String;I)Ljava/lang/String;")
	public static String toSentenceCase(String arg0, int arg1) {
		String var7 = arg0.toLowerCase();
		if (arg1 != 0) {
			field746 = -18;
		}
		char[] var2 = var7.toCharArray();
		int var3 = var2.length;
		boolean var4 = true;
		for (int var5 = 0; var5 < var3; var5++) {
			char var6 = var2[var5];
			if (var4 && var6 >= 'a' && var6 <= 'z') {
				var2[var5] = (char) (var2[var5] + -32);
				var4 = false;
			}
			if (var6 == '.' || var6 == '!') {
				var4 = true;
			}
		}
		return new String(var2);
	}

	@ObfuscatedName("vb.c(ILjava/lang/String;)Ljava/lang/String;")
	public static String toAsterisks(int arg0, String arg1) {
		String var2 = "";
		if (arg0 != 7) {
			throw new NullPointerException();
		}
		for (int var3 = 0; var3 < arg1.length(); var3++) {
			var2 = var2 + "*";
		}
		return var2;
	}
}
