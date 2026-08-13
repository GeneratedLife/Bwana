package jagex2.wordenc;

import deob.ObfuscatedName;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("mc")
public class WordFilter {

	@ObfuscatedName("mc.a")
	public static boolean field1072;

	@ObfuscatedName("mc.b")
	public static int field1073 = 24882;

	@ObfuscatedName("mc.c")
	public static int field1074 = -178;

	@ObfuscatedName("mc.d")
	public static int field1075;

	@ObfuscatedName("mc.e")
	public static int field1076 = 16180;

	@ObfuscatedName("mc.f")
	public static int field1077 = 383;

	@ObfuscatedName("mc.g")
	public static byte field1078 = 6;

	@ObfuscatedName("mc.h")
	public static int field1079 = -81;

	@ObfuscatedName("mc.i")
	public static boolean field1080;

	@ObfuscatedName("mc.j")
	public static int[] fragments;

	@ObfuscatedName("mc.k")
	public static char[][] badWords;

	@ObfuscatedName("mc.l")
	public static byte[][][] badCombinations;

	@ObfuscatedName("mc.m")
	public static char[][] domains;

	@ObfuscatedName("mc.n")
	public static char[][] tlds;

	@ObfuscatedName("mc.o")
	public static int[] tldType;

	@ObfuscatedName("mc.p")
	public static final String[] ALLOWLIST = new String[] { "cook", "cook's", "cooks", "seeks", "sheet" };

	@ObfuscatedName("mc.q")
	public static int field1088;

	@ObfuscatedName("mc.a(Lub;)V")
	public static final void unpack(Jagfile arg0) {
		Packet var1 = new Packet(363, arg0.read("fragmentsenc.txt", null, (byte) 2));
		Packet var2 = new Packet(363, arg0.read("badenc.txt", null, (byte) 2));
		Packet var3 = new Packet(363, arg0.read("domainenc.txt", null, (byte) 2));
		Packet var4 = new Packet(363, arg0.read("tldlist.txt", null, (byte) 2));
		read(var1, var2, var3, var4);
	}

	@ObfuscatedName("mc.a(Lkb;Lkb;Lkb;Lkb;)V")
	public static final void read(Packet arg0, Packet arg1, Packet arg2, Packet arg3) {
		readBadWords(-33152, arg1);
		readDomains(arg2, -717);
		readFragments(24882, arg0);
		readTld(true, arg3);
	}

	@ObfuscatedName("mc.a(ZLkb;)V")
	public static final void readTld(boolean arg0, Packet arg1) {
		int var2 = arg1.g4();
		tlds = new char[var2][];
		tldType = new int[var2];
		if (!arg0) {
			return;
		}
		for (int var3 = 0; var3 < var2; var3++) {
			tldType[var3] = arg1.g1();
			char[] var4 = new char[arg1.g1()];
			for (int var5 = 0; var5 < var4.length; var5++) {
				var4[var5] = (char) arg1.g1();
			}
			tlds[var3] = var4;
		}
	}

	@ObfuscatedName("mc.a(ILkb;)V")
	public static final void readBadWords(int arg0, Packet arg1) {
		int var2 = arg1.g4();
		if (arg0 != -33152) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		badWords = new char[var2][];
		badCombinations = new byte[var2][][];
		readBadCombinations(badCombinations, badWords, arg1, (byte) 1);
	}

	@ObfuscatedName("mc.a(Lkb;I)V")
	public static final void readDomains(Packet arg0, int arg1) {
		int var2 = arg0.g4();
		if (arg1 < 0) {
			domains = new char[var2][];
			readDomain(-178, arg0, domains);
		}
	}

	@ObfuscatedName("mc.b(ILkb;)V")
	public static final void readFragments(int arg0, Packet arg1) {
		fragments = new int[arg1.g4()];
		for (int var2 = 0; var2 < fragments.length; var2++) {
			fragments[var2] = arg1.g2();
		}
		if (arg0 != field1073) {
			field1072 = !field1072;
		}
	}

	@ObfuscatedName("mc.a([[[B[[CLkb;B)V")
	public static final void readBadCombinations(byte[][][] arg0, char[][] arg1, Packet arg2, byte arg3) {
		if (arg3 != 1) {
			return;
		}
		boolean var9 = false;
		for (int var4 = 0; var4 < arg1.length; var4++) {
			char[] var5 = new char[arg2.g1()];
			for (int var6 = 0; var6 < var5.length; var6++) {
				var5[var6] = (char) arg2.g1();
			}
			arg1[var4] = var5;
			byte[][] var7 = new byte[arg2.g1()][2];
			for (int var8 = 0; var8 < var7.length; var8++) {
				var7[var8][0] = (byte) arg2.g1();
				var7[var8][1] = (byte) arg2.g1();
			}
			if (var7.length > 0) {
				arg0[var4] = var7;
			}
		}
	}

	@ObfuscatedName("mc.a(ILkb;[[C)V")
	public static final void readDomain(int arg0, Packet arg1, char[][] arg2) {
		label32: while (true) {
			int var3;
			if (arg0 >= 0) {
				var3 = 1;
				while (true) {
					if (var3 <= 0) {
						continue label32;
					}
					var3++;
				}
			}
			for (var3 = 0; var3 < arg2.length; var3++) {
				char[] var4 = new char[arg1.g1()];
				for (int var5 = 0; var5 < var4.length; var5++) {
					var4[var5] = (char) arg1.g1();
				}
				arg2[var3] = var4;
			}
			return;
		}
	}

	@ObfuscatedName("mc.a([CI)V")
	public static final void filterCharacters(char[] arg0, int arg1) {
		int var2 = 0;
		for (int var3 = 0; var3 < arg0.length; var3++) {
			if (allowCharacter(16180, arg0[var3])) {
				arg0[var2] = arg0[var3];
			} else {
				arg0[var2] = ' ';
			}
			if (var2 == 0 || arg0[var2] != ' ' || arg0[var2 - 1] != ' ') {
				var2++;
			}
		}
		if (arg1 != 0) {
			field1072 = !field1072;
		}
		for (int var4 = var2; var4 < arg0.length; var4++) {
			arg0[var4] = ' ';
		}
	}

	@ObfuscatedName("mc.a(IC)Z")
	public static final boolean allowCharacter(int arg0, char arg1) {
		if (arg0 != field1076) {
			throw new NullPointerException();
		}
		return arg1 >= ' ' && arg1 <= 127 || arg1 == ' ' || arg1 == '\n' || arg1 == '\t' || arg1 == 163 || arg1 == 8364;
	}

	@ObfuscatedName("mc.a(Ljava/lang/String;I)Ljava/lang/String;")
	public static final String filter(String arg0, int arg1) {
		long var2 = System.currentTimeMillis();
		char[] var4 = arg0.toCharArray();
		filterCharacters(var4, 0);
		String var5 = (new String(var4)).trim();
		char[] var11 = var5.toLowerCase().toCharArray();
		String var6 = var5.toLowerCase();
		filterTld(var11, 0);
		filterBad(true, var11);
		filterDomains((byte) 120, var11);
		filterFragments(8, var11);
		int var8;
		for (int var7 = 0; var7 < ALLOWLIST.length; var7++) {
			var8 = -1;
			while ((var8 = var6.indexOf(ALLOWLIST[var7], var8 + 1)) != -1) {
				char[] var9 = ALLOWLIST[var7].toCharArray();
				for (int var10 = 0; var10 < var9.length; var10++) {
					var11[var10 + var8] = var9[var10];
				}
			}
		}
		if (arg1 != 0) {
			for (var8 = 1; var8 > 0; var8++) {
			}
		}
		replaceUpperCases(var11, 135, var5.toCharArray());
		formatUpperCases((byte) 6, var11);
		long var12 = System.currentTimeMillis();
		return (new String(var11)).trim();
	}

	@ObfuscatedName("mc.a([CI[C)V")
	public static final void replaceUpperCases(char[] arg0, int arg1, char[] arg2) {
		for (int var3 = 0; var3 < arg2.length; var3++) {
			if (arg0[var3] != '*' && isUpperCase(0, arg2[var3])) {
				arg0[var3] = arg2[var3];
			}
		}
		int var4 = 76 / arg1;
	}

	@ObfuscatedName("mc.a(B[C)V")
	public static final void formatUpperCases(byte arg0, char[] arg1) {
		boolean var2 = true;
		if (arg0 != 6) {
			return;
		}
		boolean var5 = false;
		for (int var3 = 0; var3 < arg1.length; var3++) {
			char var4 = arg1[var3];
			if (!isAlpha(-175, var4)) {
				var2 = true;
			} else if (var2) {
				if (isLowerCase((byte) 0, var4)) {
					var2 = false;
				}
			} else if (isUpperCase(0, var4)) {
				arg1[var3] = (char) (var4 + 'a' - 65);
			}
		}
	}

	@ObfuscatedName("mc.a(Z[C)V")
	public static final void filterBad(boolean arg0, char[] arg1) {
		for (int var2 = 0; var2 < 2; var2++) {
			for (int var3 = badWords.length - 1; var3 >= 0; var3--) {
				filter((byte) -102, badCombinations[var3], arg1, badWords[var3]);
			}
		}
		if (arg0) {
			;
		}
	}

	@ObfuscatedName("mc.b(B[C)V")
	public static final void filterDomains(byte arg0, char[] arg1) {
		char[] var2 = (char[]) arg1.clone();
		char[] var3 = new char[] { '(', 'a', ')' };
		filter((byte) -102, null, var2, var3);
		char[] var4 = (char[]) arg1.clone();
		char[] var5 = new char[] { 'd', 'o', 't' };
		filter((byte) -102, null, var4, var5);
		if (arg0 == 120) {
			for (int var6 = domains.length - 1; var6 >= 0; var6--) {
				filterDomain(var4, -706, var2, domains[var6], arg1);
			}
		}
	}

	@ObfuscatedName("mc.a([CI[C[C[C)V")
	public static final void filterDomain(char[] arg0, int arg1, char[] arg2, char[] arg3, char[] arg4) {
		if (arg3.length > arg4.length) {
			return;
		}
		boolean var5 = true;
		if (arg1 >= 0) {
			field1076 = -499;
		}
		int var13;
		for (int var6 = 0; var6 <= arg4.length - arg3.length; var6 += var13) {
			int var7 = var6;
			int var8 = 0;
			var13 = 1;
			boolean var9;
			label59: while (true) {
				while (true) {
					if (var7 >= arg4.length) {
						break label59;
					}
					var9 = false;
					char var10 = arg4[var7];
					char var11 = 0;
					if (var7 + 1 < arg4.length) {
						var11 = arg4[var7 + 1];
					}
					int var14;
					if (var8 < arg3.length && (var14 = getEmulatedDomainCharSize(-81, var11, arg3[var8], var10)) > 0) {
						var7 += var14;
						var8++;
					} else {
						if (var8 == 0) {
							break label59;
						}
						int var15;
						if ((var15 = getEmulatedDomainCharSize(-81, var11, arg3[var8 - 1], var10)) > 0) {
							var7 += var15;
							if (var8 == 1) {
								var13++;
							}
						} else {
							if (var8 >= arg3.length || !isSymbol(var10, 2)) {
								break label59;
							}
							var7++;
						}
					}
				}
			}
			if (var8 >= arg3.length) {
				var9 = false;
				int var16 = getDomainAtFilterStatus(var6, arg4, (byte) 6, arg2);
				int var17 = getDomainDotFilterStatus(arg0, arg4, var7 - 1, -808);
				if (var16 > 2 || var17 > 2) {
					var9 = true;
				}
				if (var9) {
					for (int var12 = var6; var12 < var7; var12++) {
						arg4[var12] = '*';
					}
				}
			}
		}
	}

	@ObfuscatedName("mc.a(I[CB[C)I")
	public static final int getDomainAtFilterStatus(int arg0, char[] arg1, byte arg2, char[] arg3) {
		if (arg0 == 0) {
			return 2;
		}
		for (int var4 = arg0 - 1; var4 >= 0 && isSymbol(arg1[var4], 2); var4--) {
			if (arg1[var4] == '@') {
				return 3;
			}
		}
		if (arg2 != field1078) {
			return field1074;
		}
		boolean var7 = false;
		int var5 = 0;
		for (int var6 = arg0 - 1; var6 >= 0 && isSymbol(arg3[var6], 2); var6--) {
			if (arg3[var6] == '*') {
				var5++;
			}
		}
		if (var5 >= 3) {
			return 4;
		} else if (isSymbol(arg1[arg0 - 1], 2)) {
			return 1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("mc.a([C[CII)I")
	public static final int getDomainDotFilterStatus(char[] arg0, char[] arg1, int arg2, int arg3) {
		if (arg3 >= 0) {
			return field1075;
		} else if (arg2 + 1 == arg1.length) {
			return 2;
		} else {
			int var4 = arg2 + 1;
			while (true) {
				if (var4 < arg1.length && isSymbol(arg1[var4], 2)) {
					if (arg1[var4] != '.' && arg1[var4] != ',') {
						var4++;
						continue;
					}
					return 3;
				}
				int var5 = 0;
				for (int var6 = arg2 + 1; var6 < arg1.length && isSymbol(arg0[var6], 2); var6++) {
					if (arg0[var6] == '*') {
						var5++;
					}
				}
				if (var5 >= 3) {
					return 4;
				}
				if (isSymbol(arg1[arg2 + 1], 2)) {
					return 1;
				}
				return 0;
			}
		}
	}

	@ObfuscatedName("mc.b([CI)V")
	public static final void filterTld(char[] arg0, int arg1) {
		char[] var2 = (char[]) arg0.clone();
		char[] var3 = new char[] { 'd', 'o', 't' };
		filter((byte) -102, null, var2, var3);
		char[] var4 = (char[]) arg0.clone();
		char[] var5 = new char[] { 's', 'l', 'a', 's', 'h' };
		if (arg1 != 0) {
			field1072 = !field1072;
		}
		filter((byte) -102, null, var4, var5);
		for (int var6 = 0; var6 < tlds.length; var6++) {
			filterTld(var4, tldType[var6], true, arg0, tlds[var6], var2);
		}
	}

	@ObfuscatedName("mc.a([CIZ[C[C[C)V")
	public static final void filterTld(char[] arg0, int arg1, boolean arg2, char[] arg3, char[] arg4, char[] arg5) {
		int var6;
		if (!arg2) {
			for (var6 = 1; var6 > 0; var6++) {
			}
		}
		if (arg4.length > arg3.length) {
			return;
		}
		boolean var17 = true;
		for (int var7 = 0; var7 <= arg3.length - arg4.length; var7 += var6) {
			int var8 = var7;
			int var9 = 0;
			var6 = 1;
			boolean var10;
			label121: while (true) {
				while (true) {
					if (var8 >= arg3.length) {
						break label121;
					}
					var10 = false;
					char var11 = arg3[var8];
					char var12 = 0;
					if (var8 + 1 < arg3.length) {
						var12 = arg3[var8 + 1];
					}
					int var18;
					if (var9 < arg4.length && (var18 = getEmulatedDomainCharSize(-81, var12, arg4[var9], var11)) > 0) {
						var8 += var18;
						var9++;
					} else {
						if (var9 == 0) {
							break label121;
						}
						int var19;
						if ((var19 = getEmulatedDomainCharSize(-81, var12, arg4[var9 - 1], var11)) > 0) {
							var8 += var19;
							if (var9 == 1) {
								var6++;
							}
						} else {
							if (var9 >= arg4.length || !isSymbol(var11, 2)) {
								break label121;
							}
							var8++;
						}
					}
				}
			}
			if (var9 >= arg4.length) {
				var10 = false;
				int var20 = getTldDotFilterStatus(arg3, false, arg5, var7);
				int var21 = getTldSlashFilterStatus(arg0, -678, var8 - 1, arg3);
				if (arg1 == 1 && var20 > 0 && var21 > 0) {
					var10 = true;
				}
				if (arg1 == 2 && (var20 > 2 && var21 > 0 || var20 > 0 && var21 > 2)) {
					var10 = true;
				}
				if (arg1 == 3 && var20 > 0 && var21 > 2) {
					var10 = true;
				}
				boolean var10000;
				if (arg1 == 3 && var20 > 2 && var21 > 0) {
					var10000 = true;
				} else {
					var10000 = false;
				}
				if (var10) {
					int var13 = var7;
					int var14 = var8 - 1;
					boolean var15;
					int var16;
					if (var20 > 2) {
						if (var20 == 4) {
							var15 = false;
							for (var16 = var7 - 1; var16 >= 0; var16--) {
								if (var15) {
									if (arg5[var16] != '*') {
										break;
									}
									var13 = var16;
								} else if (arg5[var16] == '*') {
									var13 = var16;
									var15 = true;
								}
							}
						}
						var15 = false;
						for (var16 = var13 - 1; var16 >= 0; var16--) {
							if (var15) {
								if (isSymbol(arg3[var16], 2)) {
									break;
								}
								var13 = var16;
							} else if (!isSymbol(arg3[var16], 2)) {
								var15 = true;
								var13 = var16;
							}
						}
					}
					if (var21 > 2) {
						if (var21 == 4) {
							var15 = false;
							for (var16 = var14 + 1; var16 < arg3.length; var16++) {
								if (var15) {
									if (arg0[var16] != '*') {
										break;
									}
									var14 = var16;
								} else if (arg0[var16] == '*') {
									var14 = var16;
									var15 = true;
								}
							}
						}
						var15 = false;
						for (var16 = var14 + 1; var16 < arg3.length; var16++) {
							if (var15) {
								if (isSymbol(arg3[var16], 2)) {
									break;
								}
								var14 = var16;
							} else if (!isSymbol(arg3[var16], 2)) {
								var15 = true;
								var14 = var16;
							}
						}
					}
					for (int var22 = var13; var22 <= var14; var22++) {
						arg3[var22] = '*';
					}
				}
			}
		}
	}

	@ObfuscatedName("mc.a([CZ[CI)I")
	public static final int getTldDotFilterStatus(char[] arg0, boolean arg1, char[] arg2, int arg3) {
		if (arg3 == 0) {
			return 2;
		}
		int var4 = arg3 - 1;
		while (true) {
			if (var4 >= 0 && isSymbol(arg0[var4], 2)) {
				if (arg0[var4] != ',' && arg0[var4] != '.') {
					var4--;
					continue;
				}
				return 3;
			}
			int var5 = 0;
			int var6;
			if (arg1) {
				for (var6 = 1; var6 > 0; var6++) {
				}
			}
			for (var6 = arg3 - 1; var6 >= 0 && isSymbol(arg2[var6], 2); var6--) {
				if (arg2[var6] == '*') {
					var5++;
				}
			}
			if (var5 >= 3) {
				return 4;
			}
			if (isSymbol(arg0[arg3 - 1], 2)) {
				return 1;
			}
			return 0;
		}
	}

	@ObfuscatedName("mc.a([CII[C)I")
	public static final int getTldSlashFilterStatus(char[] arg0, int arg1, int arg2, char[] arg3) {
		if (arg2 + 1 == arg3.length) {
			return 2;
		}
		int var4 = arg2 + 1;
		while (true) {
			if (var4 < arg3.length && isSymbol(arg3[var4], 2)) {
				if (arg3[var4] != '\\' && arg3[var4] != '/') {
					var4++;
					continue;
				}
				return 3;
			}
			int var5 = 0;
			for (int var6 = arg2 + 1; var6 < arg3.length && isSymbol(arg0[var6], 2); var6++) {
				if (arg0[var6] == '*') {
					var5++;
				}
			}
			if (arg1 >= 0) {
				return 3;
			}
			if (var5 >= 5) {
				return 4;
			}
			if (isSymbol(arg3[arg2 + 1], 2)) {
				return 1;
			}
			return 0;
		}
	}

	@ObfuscatedName("mc.a(B[[B[C[C)V")
	public static final void filter(byte arg0, byte[][] arg1, char[] arg2, char[] arg3) {
		if (arg3.length > arg2.length) {
			return;
		}
		boolean var4 = true;
		if (arg0 != -102) {
			field1077 = -198;
		}
		int var20;
		for (int var5 = 0; var5 <= arg2.length - arg3.length; var5 += var20) {
			int var6 = var5;
			int var7 = 0;
			int var8 = 0;
			var20 = 1;
			boolean var9 = false;
			boolean var10 = false;
			boolean var11 = false;
			boolean var12;
			char var13;
			char var14;
			label160: while (true) {
				while (true) {
					if (var6 >= arg2.length || var10 && var11) {
						break label160;
					}
					var12 = false;
					var13 = arg2[var6];
					var14 = 0;
					if (var6 + 1 < arg2.length) {
						var14 = arg2[var6 + 1];
					}
					int var21;
					if (var7 < arg3.length && (var21 = getEmulatedSize(var14, arg3[var7], var13, 7326)) > 0) {
						if (var21 == 1 && isNumber(var13, 10361)) {
							var10 = true;
						}
						if (var21 == 2 && (isNumber(var13, 10361) || isNumber(var14, 10361))) {
							var10 = true;
						}
						var6 += var21;
						var7++;
					} else {
						if (var7 == 0) {
							break label160;
						}
						int var22;
						if ((var22 = getEmulatedSize(var14, arg3[var7 - 1], var13, 7326)) > 0) {
							var6 += var22;
							if (var7 == 1) {
								var20++;
							}
						} else {
							if (var7 >= arg3.length || !isLowerCaseAlpha(var13, (byte) 13)) {
								break label160;
							}
							if (isSymbol(var13, 2) && var13 != '\'') {
								var9 = true;
							}
							if (isNumber(var13, 10361)) {
								var11 = true;
							}
							var6++;
							var8++;
							if (var8 * 100 / (var6 - var5) > 90) {
								break label160;
							}
						}
					}
				}
			}
			if (var7 >= arg3.length && (!var10 || !var11)) {
				var12 = true;
				int var28;
				if (var9) {
					boolean var23 = false;
					boolean var27 = false;
					if (var5 - 1 < 0 || isSymbol(arg2[var5 - 1], 2) && arg2[var5 - 1] != '\'') {
						var23 = true;
					}
					if (var6 >= arg2.length || isSymbol(arg2[var6], 2) && arg2[var6] != '\'') {
						var27 = true;
					}
					if (!var23 || !var27) {
						boolean var24 = false;
						var28 = var5 - 2;
						if (var23) {
							var28 = var5;
						}
						while (!var24 && var28 < var6) {
							if (var28 >= 0 && (!isSymbol(arg2[var28], 2) || arg2[var28] == '\'')) {
								char[] var17 = new char[3];
								int var18;
								for (var18 = 0; var18 < 3 && var28 + var18 < arg2.length && (!isSymbol(arg2[var28 + var18], 2) || arg2[var28 + var18] == '\''); var18++) {
									var17[var18] = arg2[var28 + var18];
								}
								boolean var19 = true;
								if (var18 == 0) {
									var19 = false;
								}
								if (var18 < 3 && var28 - 1 >= 0 && (!isSymbol(arg2[var28 - 1], 2) || arg2[var28 - 1] == '\'')) {
									var19 = false;
								}
								if (var19 && !isBadFragment(var17, 6)) {
									var24 = true;
								}
							}
							var28++;
						}
						if (!var24) {
							var12 = false;
						}
					}
				} else {
					var13 = ' ';
					if (var5 - 1 >= 0) {
						var13 = arg2[var5 - 1];
					}
					var14 = ' ';
					if (var6 < arg2.length) {
						var14 = arg2[var6];
					}
					byte var15 = getIndex(0, var13);
					byte var16 = getIndex(0, var14);
					if (arg1 != null && comboMatches(841, var15, arg1, var16)) {
						var12 = false;
					}
				}
				if (var12) {
					int var25 = 0;
					int var29 = 0;
					for (int var26 = var5; var26 < var6; var26++) {
						if (isNumber(arg2[var26], 10361)) {
							var25++;
						} else if (isAlpha(-175, arg2[var26])) {
							var29++;
						}
					}
					if (var25 <= var29) {
						for (var28 = var5; var28 < var6; var28++) {
							arg2[var28] = '*';
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("mc.a(IB[[BB)Z")
	public static final boolean comboMatches(int arg0, byte arg1, byte[][] arg2, byte arg3) {
		if (arg0 <= 0) {
			field1080 = !field1080;
		}
		int var4 = 0;
		if (arg2[var4][0] == arg1 && arg2[var4][1] == arg3) {
			return true;
		}
		int var5 = arg2.length - 1;
		if (arg2[var5][0] == arg1 && arg2[var5][1] == arg3) {
			return true;
		}
		do {
			int var6 = (var4 + var5) / 2;
			if (arg2[var6][0] == arg1 && arg2[var6][1] == arg3) {
				return true;
			}
			if (arg1 < arg2[var6][0] || arg1 == arg2[var6][0] && arg3 < arg2[var6][1]) {
				var5 = var6;
			} else {
				var4 = var6;
			}
		} while (var4 != var5 && var4 + 1 != var5);
		return false;
	}

	@ObfuscatedName("mc.a(ICCC)I")
	public static final int getEmulatedDomainCharSize(int arg0, char arg1, char arg2, char arg3) {
		while (arg0 >= 0) {
			for (int var4 = 1; var4 > 0; var4++) {
			}
		}
		if (arg2 == arg3) {
			return 1;
		} else if (arg2 == 'o' && arg3 == '0') {
			return 1;
		} else if (arg2 == 'o' && arg3 == '(' && arg1 == ')') {
			return 2;
		} else if (arg2 == 'c' && (arg3 == '(' || arg3 == '<' || arg3 == '[')) {
			return 1;
		} else if (arg2 == 'e' && arg3 == 8364) {
			return 1;
		} else if (arg2 == 's' && arg3 == '$') {
			return 1;
		} else if (arg2 == 'l' && arg3 == 'i') {
			return 1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("mc.a(CCCI)I")
	public static final int getEmulatedSize(char arg0, char arg1, char arg2, int arg3) {
		if (arg3 != 7326) {
			for (int var4 = 1; var4 > 0; var4++) {
			}
		}
		if (arg1 == arg2) {
			return 1;
		}
		if (arg1 >= 'a' && arg1 <= 'm') {
			if (arg1 == 'a') {
				if (arg2 != '4' && arg2 != '@' && arg2 != '^') {
					if (arg2 == '/' && arg0 == '\\') {
						return 2;
					}
					return 0;
				}
				return 1;
			}
			if (arg1 == 'b') {
				if (arg2 != '6' && arg2 != '8') {
					if (arg2 == '1' && arg0 == '3') {
						return 2;
					}
					return 0;
				}
				return 1;
			}
			if (arg1 == 'c') {
				if (arg2 != '(' && arg2 != '<' && arg2 != '{' && arg2 != '[') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'd') {
				if (arg2 == '[' && arg0 == ')') {
					return 2;
				}
				return 0;
			}
			if (arg1 == 'e') {
				if (arg2 != '3' && arg2 != 8364) {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'f') {
				if (arg2 == 'p' && arg0 == 'h') {
					return 2;
				}
				if (arg2 == 163) {
					return 1;
				}
				return 0;
			}
			if (arg1 == 'g') {
				if (arg2 != '9' && arg2 != '6') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'h') {
				if (arg2 == '#') {
					return 1;
				}
				return 0;
			}
			if (arg1 == 'i') {
				if (arg2 != 'y' && arg2 != 'l' && arg2 != 'j' && arg2 != '1' && arg2 != '!' && arg2 != ':' && arg2 != ';' && arg2 != '|') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'j') {
				return 0;
			}
			if (arg1 == 'k') {
				return 0;
			}
			if (arg1 == 'l') {
				if (arg2 != '1' && arg2 != '|' && arg2 != 'i') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'm') {
				return 0;
			}
		}
		if (arg1 >= 'n' && arg1 <= 'z') {
			if (arg1 == 'n') {
				return 0;
			}
			if (arg1 == 'o') {
				if (arg2 != '0' && arg2 != '*') {
					if ((arg2 != '(' || arg0 != ')') && (arg2 != '[' || arg0 != ']') && (arg2 != '{' || arg0 != '}') && (arg2 != '<' || arg0 != '>')) {
						return 0;
					}
					return 2;
				}
				return 1;
			}
			if (arg1 == 'p') {
				return 0;
			}
			if (arg1 == 'q') {
				return 0;
			}
			if (arg1 == 'r') {
				return 0;
			}
			if (arg1 == 's') {
				if (arg2 != '5' && arg2 != 'z' && arg2 != '$' && arg2 != '2') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 't') {
				if (arg2 != '7' && arg2 != '+') {
					return 0;
				}
				return 1;
			}
			if (arg1 == 'u') {
				if (arg2 == 'v') {
					return 1;
				}
				if (arg2 == '\\' && arg0 == '/' || arg2 == '\\' && arg0 == '|' || arg2 == '|' && arg0 == '/') {
					return 2;
				}
				return 0;
			}
			if (arg1 == 'v') {
				if ((arg2 != '\\' || arg0 != '/') && (arg2 != '\\' || arg0 != '|') && (arg2 != '|' || arg0 != '/')) {
					return 0;
				}
				return 2;
			}
			if (arg1 == 'w') {
				if (arg2 == 'v' && arg0 == 'v') {
					return 2;
				}
				return 0;
			}
			if (arg1 == 'x') {
				if ((arg2 != ')' || arg0 != '(') && (arg2 != '}' || arg0 != '{') && (arg2 != ']' || arg0 != '[') && (arg2 != '>' || arg0 != '<')) {
					return 0;
				}
				return 2;
			}
			if (arg1 == 'y') {
				return 0;
			}
			if (arg1 == 'z') {
				return 0;
			}
		}
		if (arg1 >= '0' && arg1 <= '9') {
			if (arg1 == '0') {
				if (arg2 == 'o' || arg2 == 'O') {
					return 1;
				} else if ((arg2 != '(' || arg0 != ')') && (arg2 != '{' || arg0 != '}') && (arg2 != '[' || arg0 != ']')) {
					return 0;
				} else {
					return 2;
				}
			} else if (arg1 == '1') {
				return arg2 == 'l' ? 1 : 0;
			} else {
				return 0;
			}
		} else if (arg1 == ',') {
			return arg2 == '.' ? 1 : 0;
		} else if (arg1 == '.') {
			return arg2 == ',' ? 1 : 0;
		} else if (arg1 == '!') {
			return arg2 == 'i' ? 1 : 0;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("mc.b(IC)B")
	public static final byte getIndex(int arg0, char arg1) {
		if (arg0 != 0) {
			throw new NullPointerException();
		} else if (arg1 >= 'a' && arg1 <= 'z') {
			return (byte) (arg1 - 'a' + 1);
		} else if (arg1 == '\'') {
			return 28;
		} else if (arg1 >= '0' && arg1 <= '9') {
			return (byte) (arg1 - '0' + 29);
		} else {
			return 27;
		}
	}

	@ObfuscatedName("mc.a(I[C)V")
	public static final void filterFragments(int arg0, char[] arg1) {
		boolean var2 = false;
		int var3 = 0;
		if (arg0 != 8) {
			return;
		}
		int var4 = 0;
		int var5 = 0;
		while (true) {
			do {
				int var11;
				if ((var11 = indexOfNumber(149, arg1, var3)) == -1) {
					return;
				}
				boolean var6 = false;
				for (int var7 = var3; var7 >= 0 && var7 < var11 && !var6; var7++) {
					if (!isSymbol(arg1[var7], 2) && !isLowerCaseAlpha(arg1[var7], (byte) 13)) {
						var6 = true;
					}
				}
				if (var6) {
					var4 = 0;
				}
				if (var4 == 0) {
					var5 = var11;
				}
				var3 = indexOfNonNumber(6, var11, arg1);
				int var8 = 0;
				for (int var9 = var11; var9 < var3; var9++) {
					var8 = var8 * 10 + arg1[var9] - 48;
				}
				if (var8 <= 255 && var3 - var11 <= 8) {
					var4++;
				} else {
					var4 = 0;
				}
			} while (var4 != 4);
			for (int var10 = var5; var10 < var3; var10++) {
				arg1[var10] = '*';
			}
			var4 = 0;
		}
	}

	@ObfuscatedName("mc.a(I[CI)I")
	public static final int indexOfNumber(int arg0, char[] arg1, int arg2) {
		int var4 = 66 / arg0;
		for (int var3 = arg2; var3 < arg1.length && var3 >= 0; var3++) {
			if (arg1[var3] >= '0' && arg1[var3] <= '9') {
				return var3;
			}
		}
		return -1;
	}

	@ObfuscatedName("mc.a(II[C)I")
	public static final int indexOfNonNumber(int arg0, int arg1, char[] arg2) {
		if (arg0 != 6) {
			return 4;
		}
		int var3 = arg1;
		while (true) {
			if (var3 < arg2.length && var3 >= 0) {
				if (arg2[var3] >= '0' && arg2[var3] <= '9') {
					var3++;
					continue;
				}
				return var3;
			}
			return arg2.length;
		}
	}

	@ObfuscatedName("mc.a(CI)Z")
	public static final boolean isSymbol(char arg0, int arg1) {
		if (arg1 != 2) {
			throw new NullPointerException();
		}
		return !isAlpha(-175, arg0) && !isNumber(arg0, 10361);
	}

	@ObfuscatedName("mc.a(CB)Z")
	public static final boolean isLowerCaseAlpha(char arg0, byte arg1) {
		if (arg1 != 13) {
			throw new NullPointerException();
		} else if (arg0 >= 'a' && arg0 <= 'z') {
			return arg0 == 'v' || arg0 == 'x' || arg0 == 'j' || arg0 == 'q' || arg0 == 'z';
		} else {
			return true;
		}
	}

	@ObfuscatedName("mc.c(IC)Z")
	public static final boolean isAlpha(int arg0, char arg1) {
		if (arg0 >= 0) {
			throw new NullPointerException();
		}
		return arg1 >= 'a' && arg1 <= 'z' || arg1 >= 'A' && arg1 <= 'Z';
	}

	@ObfuscatedName("mc.b(CI)Z")
	public static final boolean isNumber(char arg0, int arg1) {
		if (arg1 != 10361) {
			field1073 = -124;
		}
		return arg0 >= '0' && arg0 <= '9';
	}

	@ObfuscatedName("mc.a(BC)Z")
	public static final boolean isLowerCase(byte arg0, char arg1) {
		if (arg0 != 0) {
			field1074 = -254;
		}
		return arg1 >= 'a' && arg1 <= 'z';
	}

	@ObfuscatedName("mc.d(IC)Z")
	public static final boolean isUpperCase(int arg0, char arg1) {
		if (arg0 < 0 || arg0 > 0) {
			field1072 = !field1072;
		}
		return arg1 >= 'A' && arg1 <= 'Z';
	}

	@ObfuscatedName("mc.c([CI)Z")
	public static final boolean isBadFragment(char[] arg0, int arg1) {
		boolean var2 = true;
		for (int var3 = 0; var3 < arg0.length; var3++) {
			if (!isNumber(arg0[var3], 10361) && arg0[var3] != 0) {
				var2 = false;
			}
		}
		if (var2) {
			return true;
		}
		int var4 = firstFragmentId(5, arg0);
		int var5 = 0;
		int var6;
		if (arg1 != 6) {
			for (var6 = 1; var6 > 0; var6++) {
			}
		}
		var6 = fragments.length - 1;
		if (var4 == fragments[var5] || var4 == fragments[var6]) {
			return true;
		}
		do {
			int var7 = (var5 + var6) / 2;
			if (var4 == fragments[var7]) {
				return true;
			}
			if (var4 < fragments[var7]) {
				var6 = var7;
			} else {
				var5 = var7;
			}
		} while (var5 != var6 && var5 + 1 != var6);
		return false;
	}

	@ObfuscatedName("mc.b(I[C)I")
	public static final int firstFragmentId(int arg0, char[] arg1) {
		if (arg1.length > 6) {
			return 0;
		}
		int var2 = 0;
		for (int var3 = 0; var3 < arg1.length; var3++) {
			char var4 = arg1[arg1.length - var3 - 1];
			if (var4 >= 'a' && var4 <= 'z') {
				var2 = var2 * 38 + var4 - 'a' + 1;
			} else if (var4 == '\'') {
				var2 = var2 * 38 + 27;
			} else if (var4 >= '0' && var4 <= '9') {
				var2 = var2 * 38 + var4 - '0' + 28;
			} else if (var4 != 0) {
				return 0;
			}
		}
		if (arg0 < 5 || arg0 > 5) {
			for (int var5 = 1; var5 > 0; var5++) {
			}
		}
		return var2;
	}
}
