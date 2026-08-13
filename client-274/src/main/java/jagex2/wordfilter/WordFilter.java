package jagex2.wordfilter;

import deob.ObfuscatedName;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("sc")
public final class WordFilter {

	@ObfuscatedName("sc.n")
	public static int[] fragments;

	@ObfuscatedName("sc.o")
	public static char[][] badWords;

	@ObfuscatedName("sc.p")
	public static byte[][][] badCombinations;

	@ObfuscatedName("sc.q")
	public static char[][] domains;

	@ObfuscatedName("sc.r")
	public static char[][] tld;

	@ObfuscatedName("sc.s")
	public static int[] tldType;

	@ObfuscatedName("sc.t")
	public static final String[] whitelist = new String[] { "cook", "cook's", "cooks", "seeks", "sheet", "woop", "woops", "faq", "noob", "noobs" };

	@ObfuscatedName("sc.a(Lxb;)V")
	public static void unpack(JagFile arg0) {
		Packet var1 = new Packet(arg0.read("fragmentsenc.txt", null));
		Packet var2 = new Packet(arg0.read("badenc.txt", null));
		Packet var3 = new Packet(arg0.read("domainenc.txt", null));
		Packet var4 = new Packet(arg0.read("tldlist.txt", null));
		read(var1, var2, var3, var4);
	}

	@ObfuscatedName("sc.a(Llb;Llb;Llb;Llb;)V")
	public static void read(Packet arg0, Packet arg1, Packet arg2, Packet arg3) {
		readBadWords(arg1);
		readDomains(arg2);
		readFragments(arg0);
		readTld(arg3);
	}

	@ObfuscatedName("sc.a(Llb;B)V")
	public static void readTld(Packet arg0) {
		int var2 = arg0.g4();
		tld = new char[var2][];
		tldType = new int[var2];
		for (int var3 = 0; var3 < var2; var3++) {
			tldType[var3] = arg0.g1();
			char[] var4 = new char[arg0.g1()];
			for (int var5 = 0; var5 < var4.length; var5++) {
				var4[var5] = (char) arg0.g1();
			}
			tld[var3] = var4;
		}
	}

	@ObfuscatedName("sc.a(ILlb;)V")
	public static void readBadWords(Packet arg0) {
		int var1 = arg0.g4();
		badWords = new char[var1][];
		badCombinations = new byte[var1][][];
		readBad(arg0, badCombinations, badWords);
	}

	@ObfuscatedName("sc.a(Llb;Z)V")
	public static void readDomains(Packet arg0) {
		int var2 = arg0.g4();
		domains = new char[var2][];
		readDomain(domains, arg0);
	}

	@ObfuscatedName("sc.b(ILlb;)V")
	public static void readFragments(Packet arg1) {
		fragments = new int[arg1.g4()];
		for (int var3 = 0; var3 < fragments.length; var3++) {
			fragments[var3] = arg1.g2();
		}
	}

	@ObfuscatedName("sc.a(Llb;[[[B[[CZ)V")
	public static void readBad(Packet arg0, byte[][][] arg1, char[][] arg2) {
		for (int var4 = 0; var4 < arg2.length; var4++) {
			char[] var5 = new char[arg0.g1()];
			for (int var6 = 0; var6 < var5.length; var6++) {
				var5[var6] = (char) arg0.g1();
			}
			arg2[var4] = var5;
			byte[][] var7 = new byte[arg0.g1()][2];
			for (int var8 = 0; var8 < var7.length; var8++) {
				var7[var8][0] = (byte) arg0.g1();
				var7[var8][1] = (byte) arg0.g1();
			}
			if (var7.length > 0) {
				arg1[var4] = var7;
			}
		}
	}

	@ObfuscatedName("sc.a(Z[[CLlb;)V")
	public static void readDomain(char[][] arg1, Packet arg2) {
		for (int var3 = 0; var3 < arg1.length; var3++) {
			char[] var4 = new char[arg2.g1()];
			for (int var5 = 0; var5 < var4.length; var5++) {
				var4[var5] = (char) arg2.g1();
			}
			arg1[var3] = var4;
		}
	}

	@ObfuscatedName("sc.a([CI)V")
	public static void filterCharacters(char[] arg0) {
		int var2 = 0;
		for (int var3 = 0; var3 < arg0.length; var3++) {
			if (allowCharacter(arg0[var3])) {
				arg0[var2] = arg0[var3];
			} else {
				arg0[var2] = ' ';
			}
			if (var2 == 0 || arg0[var2] != ' ' || arg0[var2 - 1] != ' ') {
				var2++;
			}
		}
		for (int var4 = var2; var4 < arg0.length; var4++) {
			arg0[var4] = ' ';
		}
	}

	@ObfuscatedName("sc.a(CI)Z")
	public static boolean allowCharacter(char arg0) {
		return arg0 >= ' ' && arg0 <= '\u007F' || arg0 == ' ' || arg0 == '\n' || arg0 == '\t' || arg0 == '£' || arg0 == '€';
	}

	@ObfuscatedName("sc.a(BLjava/lang/String;)Ljava/lang/String;")
	public static String filter(String arg1) {
		long var2 = System.currentTimeMillis();
		char[] var4 = arg1.toCharArray();
		filterCharacters(var4);
		String var5 = (new String(var4)).trim();
		char[] var6 = var5.toLowerCase().toCharArray();
		String var7 = var5.toLowerCase();
		filterTld(var6);
		filterBad(var6, (byte) 6);
		filterDomains(var6);
		filterFragments(var6);
		for (int var8 = 0; var8 < whitelist.length; var8++) {
			int var9 = -1;
			while ((var9 = var7.indexOf(whitelist[var8], var9 + 1)) != -1) {
				char[] var10 = whitelist[var8].toCharArray();
				for (int var11 = 0; var11 < var10.length; var11++) {
					var6[var11 + var9] = var10[var11];
				}
			}
		}
		replaceUpperCases(var6, var5.toCharArray(), -51);
		formatUpperCases((byte) 7, var6);
		long var12 = System.currentTimeMillis();
		return (new String(var6)).trim();
	}

	@ObfuscatedName("sc.a([C[CI)V")
	public static void replaceUpperCases(char[] arg0, char[] arg1, int arg2) {
		if (arg2 >= 0) {
			return;
		}
		for (int var3 = 0; var3 < arg1.length; var3++) {
			if (arg0[var3] != '*' && isUpperCase(arg1[var3])) {
				arg0[var3] = arg1[var3];
			}
		}
	}

	@ObfuscatedName("sc.a(B[C)V")
	public static void formatUpperCases(byte arg0, char[] arg1) {
		boolean var2 = true;
		if (arg0 != 7) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		for (int var4 = 0; var4 < arg1.length; var4++) {
			char var5 = arg1[var4];
			if (!isAlpha(var5)) {
				var2 = true;
			} else if (var2) {
				if (isLowerCase(var5)) {
					var2 = false;
				}
			} else if (isUpperCase(var5)) {
				arg1[var4] = (char) (var5 + 'a' - 65);
			}
		}
	}

	@ObfuscatedName("sc.a([CB)V")
	public static void filterBad(char[] arg0, byte arg1) {
		for (int var2 = 0; var2 < 2; var2++) {
			for (int var3 = badWords.length - 1; var3 >= 0; var3--) {
				filter(arg0, badWords[var3], badCombinations[var3]);
			}
		}
		if (arg1 != 6) {
			for (int var4 = 1; var4 > 0; var4++) {
			}
		}
	}

	@ObfuscatedName("sc.b([CB)V")
	public static void filterDomains(char[] arg0) {
		char[] var2 = (char[]) arg0.clone();
		char[] var3 = new char[] { '(', 'a', ')' };
		filter(var2, var3, null);
		char[] var4 = (char[]) arg0.clone();
		char[] var5 = new char[] { 'd', 'o', 't' };
		filter(var4, var5, null);
		for (int var6 = domains.length - 1; var6 >= 0; var6--) {
			filterDomain(var2, domains[var6], arg0, var4);
		}
	}

	@ObfuscatedName("sc.a([CB[C[C[C)V")
	public static void filterDomain(char[] arg0, char[] arg2, char[] arg3, char[] arg4) {
		if (arg2.length > arg3.length) {
			return;
		}
		int var8;
		for (int var5 = 0; var5 <= arg3.length - arg2.length; var5 += var8) {
			int var6 = var5;
			int var7 = 0;
			var8 = 1;
			label59: while (true) {
				while (true) {
					if (var6 >= arg3.length) {
						break label59;
					}
					char var9 = arg3[var6];
					char var10 = 0;
					if (var6 + 1 < arg3.length) {
						var10 = arg3[var6 + 1];
					}
					int var11;
					if (var7 < arg2.length && (var11 = getEmulatedDomainCharSize(arg2[var7], var10, var9)) > 0) {
						var6 += var11;
						var7++;
					} else {
						if (var7 == 0) {
							break label59;
						}
						int var12;
						if ((var12 = getEmulatedDomainCharSize(arg2[var7 - 1], var10, var9)) > 0) {
							var6 += var12;
							if (var7 == 1) {
								var8++;
							}
						} else {
							if (var7 >= arg2.length || !isSymbol(var9)) {
								break label59;
							}
							var6++;
						}
					}
				}
			}
			if (var7 >= arg2.length) {
				boolean var13 = false;
				int var14 = getDomainAtFilterStatus(arg3, (byte) -8, var5, arg0);
				int var15 = getDomainDotFilterStatus(var6 - 1, arg3, arg4);
				if (var14 > 2 || var15 > 2) {
					var13 = true;
				}
				if (var13) {
					for (int var16 = var5; var16 < var6; var16++) {
						arg3[var16] = '*';
					}
				}
			}
		}
	}

	@ObfuscatedName("sc.a([CBI[C)I")
	public static int getDomainAtFilterStatus(char[] arg0, byte arg1, int arg2, char[] arg3) {
		if (arg1 != -8) {
			return 4;
		} else if (arg2 == 0) {
			return 2;
		} else {
			for (int var4 = arg2 - 1; var4 >= 0 && isSymbol(arg0[var4]); var4--) {
				if (arg0[var4] == '@') {
					return 3;
				}
			}
			int var5 = 0;
			for (int var6 = arg2 - 1; var6 >= 0 && isSymbol(arg3[var6]); var6--) {
				if (arg3[var6] == '*') {
					var5++;
				}
			}
			if (var5 >= 3) {
				return 4;
			} else if (isSymbol(arg0[arg2 - 1])) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@ObfuscatedName("sc.a(IB[C[C)I")
	public static int getDomainDotFilterStatus(int arg0, char[] arg2, char[] arg3) {
		if (arg0 + 1 == arg2.length) {
			return 2;
		}
		int var4 = arg0 + 1;
		while (true) {
			if (var4 < arg2.length && isSymbol(arg2[var4])) {
				if (arg2[var4] != '.' && arg2[var4] != ',') {
					var4++;
					continue;
				}
				return 3;
			}
			int var5 = 0;
			for (int var6 = arg0 + 1; var6 < arg2.length && isSymbol(arg3[var6]); var6++) {
				if (arg3[var6] == '*') {
					var5++;
				}
			}
			if (var5 >= 3) {
				return 4;
			}
			if (isSymbol(arg2[arg0 + 1])) {
				return 1;
			}
			return 0;
		}
	}

	@ObfuscatedName("sc.a(Z[C)V")
	public static void filterTld(char[] arg1) {
		char[] var2 = (char[]) arg1.clone();
		char[] var3 = new char[] { 'd', 'o', 't' };
		filter(var2, var3, null);
		char[] var5 = (char[]) arg1.clone();
		char[] var6 = new char[] { 's', 'l', 'a', 's', 'h' };
		filter(var5, var6, null);
		for (int var7 = 0; var7 < tld.length; var7++) {
			filterTld(var2, tldType[var7], arg1, var5, tld[var7]);
		}
	}

	@ObfuscatedName("sc.a([CI[C[C[CI)V")
	public static void filterTld(char[] arg0, int arg1, char[] arg2, char[] arg3, char[] arg4) {
		if (arg4.length > arg2.length) {
			return;
		}
		int var9;
		for (int var6 = 0; var6 <= arg2.length - arg4.length; var6 += var9) {
			int var7 = var6;
			int var8 = 0;
			var9 = 1;
			label120: while (true) {
				while (true) {
					if (var7 >= arg2.length) {
						break label120;
					}
					char var10 = arg2[var7];
					char var11 = 0;
					if (var7 + 1 < arg2.length) {
						var11 = arg2[var7 + 1];
					}
					int var12;
					if (var8 < arg4.length && (var12 = getEmulatedDomainCharSize(arg4[var8], var11, var10)) > 0) {
						var7 += var12;
						var8++;
					} else {
						if (var8 == 0) {
							break label120;
						}
						int var13;
						if ((var13 = getEmulatedDomainCharSize(arg4[var8 - 1], var11, var10)) > 0) {
							var7 += var13;
							if (var8 == 1) {
								var9++;
							}
						} else {
							if (var8 >= arg4.length || !isSymbol(var10)) {
								break label120;
							}
							var7++;
						}
					}
				}
			}
			if (var8 >= arg4.length) {
				boolean var14 = false;
				int var15 = getTldDotFilterStatus(arg0, arg2, var6);
				int var16 = getTldSlashFilterStatus(arg3, var7 - 1, arg2);
				if (arg1 == 1 && var15 > 0 && var16 > 0) {
					var14 = true;
				}
				if (arg1 == 2 && (var15 > 2 && var16 > 0 || var15 > 0 && var16 > 2)) {
					var14 = true;
				}
				if (arg1 == 3 && var15 > 0 && var16 > 2) {
					var14 = true;
				}
				boolean var10000;
				if (arg1 == 3 && var15 > 2 && var16 > 0) {
					var10000 = true;
				} else {
					var10000 = false;
				}
				if (var14) {
					int var17 = var6;
					int var18 = var7 - 1;
					if (var15 > 2) {
						if (var15 == 4) {
							boolean var19 = false;
							for (int var20 = var6 - 1; var20 >= 0; var20--) {
								if (var19) {
									if (arg0[var20] != '*') {
										break;
									}
									var17 = var20;
								} else if (arg0[var20] == '*') {
									var17 = var20;
									var19 = true;
								}
							}
						}
						boolean var21 = false;
						for (int var22 = var17 - 1; var22 >= 0; var22--) {
							if (var21) {
								if (isSymbol(arg2[var22])) {
									break;
								}
								var17 = var22;
							} else if (!isSymbol(arg2[var22])) {
								var21 = true;
								var17 = var22;
							}
						}
					}
					if (var16 > 2) {
						if (var16 == 4) {
							boolean var23 = false;
							for (int var24 = var18 + 1; var24 < arg2.length; var24++) {
								if (var23) {
									if (arg3[var24] != '*') {
										break;
									}
									var18 = var24;
								} else if (arg3[var24] == '*') {
									var18 = var24;
									var23 = true;
								}
							}
						}
						boolean var25 = false;
						for (int var26 = var18 + 1; var26 < arg2.length; var26++) {
							if (var25) {
								if (isSymbol(arg2[var26])) {
									break;
								}
								var18 = var26;
							} else if (!isSymbol(arg2[var26])) {
								var25 = true;
								var18 = var26;
							}
						}
					}
					for (int var27 = var17; var27 <= var18; var27++) {
						arg2[var27] = '*';
					}
				}
			}
		}
	}

	@ObfuscatedName("sc.a([CZ[CI)I")
	public static int getTldDotFilterStatus(char[] arg0, char[] arg2, int arg3) {
		if (arg3 == 0) {
			return 2;
		}
		int var4 = arg3 - 1;
		while (true) {
			if (var4 >= 0 && isSymbol(arg2[var4])) {
				if (arg2[var4] != ',' && arg2[var4] != '.') {
					var4--;
					continue;
				}
				return 3;
			}
			int var5 = 0;
			for (int var6 = arg3 - 1; var6 >= 0 && isSymbol(arg0[var6]); var6--) {
				if (arg0[var6] == '*') {
					var5++;
				}
			}
			if (var5 >= 3) {
				return 4;
			}
			if (isSymbol(arg2[arg3 - 1])) {
				return 1;
			}
			return 0;
		}
	}

	@ObfuscatedName("sc.a([CII[C)I")
	public static int getTldSlashFilterStatus(char[] arg0, int arg1, char[] arg3) {
		if (arg1 + 1 == arg3.length) {
			return 2;
		}
		int var4 = arg1 + 1;
		while (true) {
			if (var4 < arg3.length && isSymbol(arg3[var4])) {
				if (arg3[var4] != '\\' && arg3[var4] != '/') {
					var4++;
					continue;
				}
				return 3;
			}
			int var5 = 0;
			for (int var6 = arg1 + 1; var6 < arg3.length && isSymbol(arg0[var6]); var6++) {
				if (arg0[var6] == '*') {
					var5++;
				}
			}
			if (var5 >= 5) {
				return 4;
			}
			if (isSymbol(arg3[arg1 + 1])) {
				return 1;
			}
			return 0;
		}
	}

	@ObfuscatedName("sc.a([C[CI[[B)V")
	public static void filter(char[] arg0, char[] arg1, byte[][] arg3) {
		if (arg1.length > arg0.length) {
			return;
		}
		int var8;
		for (int var4 = 0; var4 <= arg0.length - arg1.length; var4 += var8) {
			int var5 = var4;
			int var6 = 0;
			int var7 = 0;
			var8 = 1;
			boolean var9 = false;
			boolean var10 = false;
			boolean var11 = false;
			label163: while (true) {
				while (true) {
					if (var5 >= arg0.length || var10 && var11) {
						break label163;
					}
					char var12 = arg0[var5];
					char var13 = 0;
					if (var5 + 1 < arg0.length) {
						var13 = arg0[var5 + 1];
					}
					int var14;
					if (var6 < arg1.length && (var14 = getEmulatedSize(var12, var13, arg1[var6])) > 0) {
						if (var14 == 1 && isNumber(var12)) {
							var10 = true;
						}
						if (var14 == 2 && (isNumber(var12) || isNumber(var13))) {
							var10 = true;
						}
						var5 += var14;
						var6++;
					} else {
						if (var6 == 0) {
							break label163;
						}
						int var15;
						if ((var15 = getEmulatedSize(var12, var13, arg1[var6 - 1])) > 0) {
							var5 += var15;
							if (var6 == 1) {
								var8++;
							}
						} else {
							if (var6 >= arg1.length || !isLowerCaseAlpha(var12)) {
								break label163;
							}
							if (isSymbol(var12) && var12 != '\'') {
								var9 = true;
							}
							if (isNumber(var12)) {
								var11 = true;
							}
							var5++;
							var7++;
							if (var7 * 100 / (var5 - var4) > 90) {
								break label163;
							}
						}
					}
				}
			}
			if (var6 >= arg1.length && (!var10 || !var11)) {
				boolean var16 = true;
				if (var9) {
					boolean var21 = false;
					boolean var22 = false;
					if (var4 - 1 < 0 || isSymbol(arg0[var4 - 1]) && arg0[var4 - 1] != '\'') {
						var21 = true;
					}
					if (var5 >= arg0.length || isSymbol(arg0[var5]) && arg0[var5] != '\'') {
						var22 = true;
					}
					if (!var21 || !var22) {
						boolean var23 = false;
						int var24 = var4 - 2;
						if (var21) {
							var24 = var4;
						}
						while (!var23 && var24 < var5) {
							if (var24 >= 0 && (!isSymbol(arg0[var24]) || arg0[var24] == '\'')) {
								char[] var25 = new char[3];
								int var26;
								for (var26 = 0; var26 < 3 && var24 + var26 < arg0.length && (!isSymbol(arg0[var24 + var26]) || arg0[var24 + var26] == '\''); var26++) {
									var25[var26] = arg0[var24 + var26];
								}
								boolean var27 = true;
								if (var26 == 0) {
									var27 = false;
								}
								if (var26 < 3 && var24 - 1 >= 0 && (!isSymbol(arg0[var24 - 1]) || arg0[var24 - 1] == '\'')) {
									var27 = false;
								}
								if (var27 && !isBadFragment(var25)) {
									var23 = true;
								}
							}
							var24++;
						}
						if (!var23) {
							var16 = false;
						}
					}
				} else {
					char var17 = ' ';
					if (var4 - 1 >= 0) {
						var17 = arg0[var4 - 1];
					}
					char var18 = ' ';
					if (var5 < arg0.length) {
						var18 = arg0[var5];
					}
					byte var19 = getIndex(var17);
					byte var20 = getIndex(var18);
					if (arg3 != null && comboMatches(arg3, var19, var20)) {
						var16 = false;
					}
				}
				if (var16) {
					int var28 = 0;
					int var29 = 0;
					int var30 = -1;
					for (int var31 = var4; var31 < var5; var31++) {
						if (isNumber(arg0[var31])) {
							var28++;
						} else if (isAlpha(arg0[var31])) {
							var29++;
							var30 = var31;
						}
					}
					if (var30 > -1) {
						var28 -= var5 - var30 - 1;
					}
					if (var28 <= var29) {
						for (int var32 = var4; var32 < var5; var32++) {
							arg0[var32] = '*';
						}
					} else {
						var8 = 1;
					}
				}
			}
		}
	}

	@ObfuscatedName("sc.a(I[[BBB)Z")
	public static boolean comboMatches(byte[][] arg1, byte arg2, byte arg3) {
		int var4 = 0;
		if (arg1[var4][0] == arg2 && arg1[var4][1] == arg3) {
			return true;
		}
		int var5 = arg1.length - 1;
		if (arg1[var5][0] == arg2 && arg1[var5][1] == arg3) {
			return true;
		} else {
			do {
				int var6 = (var4 + var5) / 2;
				if (arg1[var6][0] == arg2 && arg1[var6][1] == arg3) {
					return true;
				}
				if (arg2 < arg1[var6][0] || arg2 == arg1[var6][0] && arg3 < arg1[var6][1]) {
					var5 = var6;
				} else {
					var4 = var6;
				}
			} while (var4 != var5 && var4 + 1 != var5);
			return false;
		}
	}

	@ObfuscatedName("sc.a(CZCC)I")
	public static int getEmulatedDomainCharSize(char arg0, char arg2, char arg3) {
		if (arg0 == arg3) {
			return 1;
		} else if (arg0 == 'o' && arg3 == '0') {
			return 1;
		} else if (arg0 == 'o' && arg3 == '(' && arg2 == ')') {
			return 2;
		} else if (arg0 == 'c' && (arg3 == '(' || arg3 == '<' || arg3 == '[')) {
			return 1;
		} else if (arg0 == 'e' && arg3 == '€') {
			return 1;
		} else if (arg0 == 's' && arg3 == '$') {
			return 1;
		} else if (arg0 == 'l' && arg3 == 'i') {
			return 1;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("sc.a(CCCI)I")
	public static int getEmulatedSize(char arg0, char arg1, char arg2) {
		if (arg2 == arg0) {
			return 1;
		}
		if (arg2 >= 'a' && arg2 <= 'm') {
			if (arg2 == 'a') {
				if (arg0 != '4' && arg0 != '@' && arg0 != '^') {
					if (arg0 == '/' && arg1 == '\\') {
						return 2;
					}
					return 0;
				}
				return 1;
			}
			if (arg2 == 'b') {
				if (arg0 != '6' && arg0 != '8') {
					if ((arg0 != '1' || arg1 != '3') && (arg0 != 'i' || arg1 != '3')) {
						return 0;
					}
					return 2;
				}
				return 1;
			}
			if (arg2 == 'c') {
				if (arg0 != '(' && arg0 != '<' && arg0 != '{' && arg0 != '[') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'd') {
				if ((arg0 != '[' || arg1 != ')') && (arg0 != 'i' || arg1 != ')')) {
					return 0;
				}
				return 2;
			}
			if (arg2 == 'e') {
				if (arg0 != '3' && arg0 != '€') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'f') {
				if (arg0 == 'p' && arg1 == 'h') {
					return 2;
				}
				if (arg0 == '£') {
					return 1;
				}
				return 0;
			}
			if (arg2 == 'g') {
				if (arg0 != '9' && arg0 != '6' && arg0 != 'q') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'h') {
				if (arg0 == '#') {
					return 1;
				}
				return 0;
			}
			if (arg2 == 'i') {
				if (arg0 != 'y' && arg0 != 'l' && arg0 != 'j' && arg0 != '1' && arg0 != '!' && arg0 != ':' && arg0 != ';' && arg0 != '|') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'j') {
				return 0;
			}
			if (arg2 == 'k') {
				return 0;
			}
			if (arg2 == 'l') {
				if (arg0 != '1' && arg0 != '|' && arg0 != 'i') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'm') {
				return 0;
			}
		}
		if (arg2 >= 'n' && arg2 <= 'z') {
			if (arg2 == 'n') {
				return 0;
			}
			if (arg2 == 'o') {
				if (arg0 != '0' && arg0 != '*') {
					if ((arg0 != '(' || arg1 != ')') && (arg0 != '[' || arg1 != ']') && (arg0 != '{' || arg1 != '}') && (arg0 != '<' || arg1 != '>')) {
						return 0;
					}
					return 2;
				}
				return 1;
			}
			if (arg2 == 'p') {
				return 0;
			}
			if (arg2 == 'q') {
				return 0;
			}
			if (arg2 == 'r') {
				return 0;
			}
			if (arg2 == 's') {
				if (arg0 != '5' && arg0 != 'z' && arg0 != '$' && arg0 != '2') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 't') {
				if (arg0 != '7' && arg0 != '+') {
					return 0;
				}
				return 1;
			}
			if (arg2 == 'u') {
				if (arg0 == 'v') {
					return 1;
				}
				if ((arg0 != '\\' || arg1 != '/') && (arg0 != '\\' || arg1 != '|') && (arg0 != '|' || arg1 != '/')) {
					return 0;
				}
				return 2;
			}
			if (arg2 == 'v') {
				if ((arg0 != '\\' || arg1 != '/') && (arg0 != '\\' || arg1 != '|') && (arg0 != '|' || arg1 != '/')) {
					return 0;
				}
				return 2;
			}
			if (arg2 == 'w') {
				if (arg0 == 'v' && arg1 == 'v') {
					return 2;
				}
				return 0;
			}
			if (arg2 == 'x') {
				if ((arg0 != ')' || arg1 != '(') && (arg0 != '}' || arg1 != '{') && (arg0 != ']' || arg1 != '[') && (arg0 != '>' || arg1 != '<')) {
					return 0;
				}
				return 2;
			}
			if (arg2 == 'y') {
				return 0;
			}
			if (arg2 == 'z') {
				return 0;
			}
		}
		if (arg2 >= '0' && arg2 <= '9') {
			if (arg2 == '0') {
				if (arg0 == 'o' || arg0 == 'O') {
					return 1;
				} else if (arg0 == '(' && arg1 == ')' || arg0 == '{' && arg1 == '}' || arg0 == '[' && arg1 == ']') {
					return 2;
				} else {
					return 0;
				}
			} else if (arg2 == '1') {
				return arg0 == 'l' ? 1 : 0;
			} else {
				return 0;
			}
		} else if (arg2 == ',') {
			return arg0 == '.' ? 1 : 0;
		} else if (arg2 == '.') {
			return arg0 == ',' ? 1 : 0;
		} else if (arg2 == '!') {
			return arg0 == 'i' ? 1 : 0;
		} else {
			return 0;
		}
	}

	@ObfuscatedName("sc.b(CI)B")
	public static byte getIndex(char arg0) {
		if (arg0 >= 'a' && arg0 <= 'z') {
			return (byte) (arg0 + 1 - 'a');
		} else if (arg0 == '\'') {
			return 28;
		} else if (arg0 >= '0' && arg0 <= '9') {
			return (byte) (arg0 + 29 - '0');
		} else {
			return 27;
		}
	}

	@ObfuscatedName("sc.b([CI)V")
	public static void filterFragments(char[] arg0) {
		int var2 = 0;
		int var3 = 0;
		int var4 = 0;
		while (true) {
			do {
				int var7;
				if ((var7 = indexOfNumber(arg0, var2)) == -1) {
					return;
				}
				boolean var5 = false;
				for (int var6 = var2; var6 >= 0 && var6 < var7 && !var5; var6++) {
					if (!isSymbol(arg0[var6]) && !isLowerCaseAlpha(arg0[var6])) {
						var5 = true;
					}
				}
				if (var5) {
					var3 = 0;
				}
				if (var3 == 0) {
					var4 = var7;
				}
				var2 = indexOfNonNumber(arg0, var7);
				int var8 = 0;
				for (int var9 = var7; var9 < var2; var9++) {
					var8 = var8 * 10 + arg0[var9] - 48;
				}
				if (var8 <= 255 && var2 - var7 <= 8) {
					var3++;
				} else {
					var3 = 0;
				}
			} while (var3 != 4);
			for (int var10 = var4; var10 < var2; var10++) {
				arg0[var10] = '*';
			}
			var3 = 0;
		}
	}

	@ObfuscatedName("sc.a([CII)I")
	public static int indexOfNumber(char[] arg0, int arg2) {
		for (int var3 = arg2; var3 < arg0.length && var3 >= 0; var3++) {
			if (arg0[var3] >= '0' && arg0[var3] <= '9') {
				return var3;
			}
		}
		return -1;
	}

	@ObfuscatedName("sc.a(I[CI)I")
	public static int indexOfNonNumber(char[] arg1, int arg2) {
		int var3 = arg2;
		while (true) {
			if (var3 < arg1.length && var3 >= 0) {
				if (arg1[var3] >= '0' && arg1[var3] <= '9') {
					var3++;
					continue;
				}
				return var3;
			}
			return arg1.length;
		}
	}

	@ObfuscatedName("sc.a(IC)Z")
	public static boolean isSymbol(char arg1) {
		return !isAlpha(arg1) && !isNumber(arg1);
	}

	@ObfuscatedName("sc.c(CI)Z")
	public static boolean isLowerCaseAlpha(char arg0) {
		if (arg0 >= 'a' && arg0 <= 'z') {
			return arg0 == 'v' || arg0 == 'x' || arg0 == 'j' || arg0 == 'q' || arg0 == 'z';
		} else {
			return true;
		}
	}

	@ObfuscatedName("sc.a(BC)Z")
	public static boolean isAlpha(char arg1) {
		return arg1 >= 'a' && arg1 <= 'z' || arg1 >= 'A' && arg1 <= 'Z';
	}

	@ObfuscatedName("sc.b(IC)Z")
	public static boolean isNumber(char arg0) {
		return arg0 >= '0' && arg0 <= '9';
	}

	@ObfuscatedName("sc.c(IC)Z")
	public static boolean isLowerCase(char arg1) {
		return arg1 >= 'a' && arg1 <= 'z';
	}

	@ObfuscatedName("sc.d(IC)Z")
	public static boolean isUpperCase(char arg1) {
		return arg1 >= 'A' && arg1 <= 'Z';
	}

	@ObfuscatedName("sc.c([CI)Z")
	public static boolean isBadFragment(char[] arg0) {
		boolean var2 = true;
		for (int var3 = 0; var3 < arg0.length; var3++) {
			if (!isNumber(arg0[var3]) && arg0[var3] != '\u0000') {
				var2 = false;
			}
		}
		if (var2) {
			return true;
		}
		int var4 = firstFragmentId(arg0);
		int var5 = 0;
		int var6 = fragments.length - 1;
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

	@ObfuscatedName("sc.b(B[C)I")
	public static int firstFragmentId(char[] arg1) {
		if (arg1.length > 6) {
			return 0;
		}
		int var2 = 0;
		for (int var3 = 0; var3 < arg1.length; var3++) {
			char var4 = arg1[arg1.length - var3 - 1];
			if (var4 >= 'a' && var4 <= 'z') {
				var2 = var2 * 38 + var4 + 1 - 'a';
			} else if (var4 == '\'') {
				var2 = var2 * 38 + 27;
			} else if (var4 >= '0' && var4 <= '9') {
				var2 = var2 * 38 + var4 + 28 - '0';
			} else if (var4 != '\u0000') {
				return 0;
			}
		}
		return var2;
	}
}
