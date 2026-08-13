package jagex2.io;

import deob.ObfuscatedName;

@ObfuscatedName("rb")
public class BZip2 {

	@ObfuscatedName("rb.a")
	public static BZip2State state = new BZip2State();

	@ObfuscatedName("rb.a([BI[BII)I")
	public static int read(byte[] arg0, int arg1, byte[] arg2, int arg3, int arg4) {
		BZip2State var6 = state;
		synchronized (state) {
			state.stream = arg2;
			state.next_in = arg4;
			state.decompressed = arg0;
			state.next_out = 0;
			state.avail_in = arg3;
			state.avail_out = arg1;
			state.bsLive = 0;
			state.bsBuff = 0;
			state.total_in_lo32 = 0;
			state.total_in_hi32 = 0;
			state.total_out_lo32 = 0;
			state.total_out_hi32 = 0;
			state.currBlockNo = 0;
			decompress(state);
			return arg1 - state.avail_out;
		}
	}

	@ObfuscatedName("rb.a(Lsb;)V")
	public static void finish(BZip2State arg0) {
		byte var2 = arg0.state_out_ch;
		int var3 = arg0.state_out_len;
		int var4 = arg0.c_nblock_used;
		int var5 = arg0.k0;
		int[] var6 = BZip2State.tt;
		int var7 = arg0.tPos;
		byte[] var8 = arg0.decompressed;
		int var9 = arg0.next_out;
		int var10 = arg0.avail_out;
		int var11 = var10;
		int var12 = arg0.save_nblock + 1;
		label67: while (true) {
			if (var3 > 0) {
				while (true) {
					if (var10 == 0) {
						break label67;
					}
					if (var3 == 1) {
						if (var10 == 0) {
							var3 = 1;
							break label67;
						}
						var8[var9] = var2;
						var9++;
						var10--;
						break;
					}
					var8[var9] = var2;
					var3--;
					var9++;
					var10--;
				}
			}
			boolean var14 = true;
			byte var1;
			while (var14) {
				var14 = false;
				if (var4 == var12) {
					var3 = 0;
					break label67;
				}
				var2 = (byte) var5;
				var7 = var6[var7];
				var1 = (byte) (var7 & 0xFF);
				var7 >>= 0x8;
				var4++;
				if (var1 != var5) {
					var5 = var1;
					if (var10 == 0) {
						var3 = 1;
						break label67;
					}
					var8[var9] = var2;
					var9++;
					var10--;
					var14 = true;
				} else if (var4 == var12) {
					if (var10 == 0) {
						var3 = 1;
						break label67;
					}
					var8[var9] = var2;
					var9++;
					var10--;
					var14 = true;
				}
			}
			var3 = 2;
			var7 = var6[var7];
			var1 = (byte) (var7 & 0xFF);
			var7 >>= 0x8;
			var4++;
			if (var4 != var12) {
				if (var1 == var5) {
					var3 = 3;
					var7 = var6[var7];
					var1 = (byte) (var7 & 0xFF);
					var7 >>= 0x8;
					var4++;
					if (var4 != var12) {
						if (var1 == var5) {
							var7 = var6[var7];
							var1 = (byte) (var7 & 0xFF);
							var7 >>= 0x8;
							var4++;
							var3 = (var1 & 0xFF) + 4;
							var7 = var6[var7];
							var5 = (byte) (var7 & 0xFF);
							var7 >>= 0x8;
							var4++;
						} else {
							var5 = var1;
						}
					}
				} else {
					var5 = var1;
				}
			}
		}
		int var13 = arg0.total_out_lo32;
		arg0.total_out_lo32 += var11 - var10;
		if (arg0.total_out_lo32 < var13) {
			arg0.total_out_hi32++;
		}
		arg0.state_out_ch = var2;
		arg0.state_out_len = var3;
		arg0.c_nblock_used = var4;
		arg0.k0 = var5;
		BZip2State.tt = var6;
		arg0.tPos = var7;
		arg0.decompressed = var8;
		arg0.next_out = var9;
		arg0.avail_out = var10;
	}

	@ObfuscatedName("rb.b(Lsb;)V")
	public static void decompress(BZip2State arg0) {
		boolean var4 = false;
		boolean var5 = false;
		boolean var6 = false;
		boolean var7 = false;
		boolean var8 = false;
		boolean var9 = false;
		boolean var10 = false;
		boolean var11 = false;
		boolean var12 = false;
		boolean var13 = false;
		boolean var14 = false;
		boolean var15 = false;
		boolean var16 = false;
		boolean var17 = false;
		boolean var18 = false;
		boolean var19 = false;
		boolean var20 = false;
		boolean var21 = false;
		boolean var22 = false;
		int var23 = 0;
		int[] var24 = null;
		int[] var25 = null;
		int[] var26 = null;
		arg0.blockSize100k = 1;
		if (BZip2State.tt == null) {
			BZip2State.tt = new int[arg0.blockSize100k * 100000];
		}
		boolean var27 = true;
		while (true) {
			while (var27) {
				byte var1 = getUnsignedChar(arg0);
				if (var1 == 23) {
					return;
				}
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				arg0.currBlockNo++;
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getUnsignedChar(arg0);
				var1 = getBit(arg0);
				if (var1 == 0) {
					arg0.blockRandomized = false;
				} else {
					arg0.blockRandomized = true;
				}
				if (arg0.blockRandomized) {
					System.out.println("PANIC! RANDOMISED BLOCK!");
				}
				arg0.origPtr = 0;
				var1 = getUnsignedChar(arg0);
				arg0.origPtr = arg0.origPtr << 8 | var1 & 0xFF;
				var1 = getUnsignedChar(arg0);
				arg0.origPtr = arg0.origPtr << 8 | var1 & 0xFF;
				var1 = getUnsignedChar(arg0);
				arg0.origPtr = arg0.origPtr << 8 | var1 & 0xFF;
				int var42;
				for (var42 = 0; var42 < 16; var42++) {
					var1 = getBit(arg0);
					if (var1 == 1) {
						arg0.inUse16[var42] = true;
					} else {
						arg0.inUse16[var42] = false;
					}
				}
				for (var42 = 0; var42 < 256; var42++) {
					arg0.inUse[var42] = false;
				}
				int var43;
				for (var42 = 0; var42 < 16; var42++) {
					if (arg0.inUse16[var42]) {
						for (var43 = 0; var43 < 16; var43++) {
							var1 = getBit(arg0);
							if (var1 == 1) {
								arg0.inUse[var42 * 16 + var43] = true;
							}
						}
					}
				}
				makeMaps(arg0);
				int var45 = arg0.nInUse + 2;
				int var46 = getBits(3, arg0);
				int var47 = getBits(15, arg0);
				for (var42 = 0; var42 < var47; var42++) {
					var43 = 0;
					while (true) {
						var1 = getBit(arg0);
						if (var1 == 0) {
							arg0.selectorMtf[var42] = (byte) var43;
							break;
						}
						var43++;
					}
				}
				byte[] var28 = new byte[6];
				byte var30 = 0;
				while (var30 < var46) {
					var28[var30] = var30++;
				}
				for (var42 = 0; var42 < var47; var42++) {
					var30 = arg0.selectorMtf[var42];
					byte var29 = var28[var30];
					while (var30 > 0) {
						var28[var30] = var28[var30 - 1];
						var30--;
					}
					var28[0] = var29;
					arg0.selector[var42] = var29;
				}
				int var44;
				for (var44 = 0; var44 < var46; var44++) {
					int var57 = getBits(5, arg0);
					for (var42 = 0; var42 < var45; var42++) {
						while (true) {
							var1 = getBit(arg0);
							if (var1 == 0) {
								arg0.len[var44][var42] = (byte) var57;
								break;
							}
							var1 = getBit(arg0);
							if (var1 == 0) {
								var57++;
							} else {
								var57--;
							}
						}
					}
				}
				for (var44 = 0; var44 < var46; var44++) {
					byte var2 = 32;
					byte var3 = 0;
					for (var42 = 0; var42 < var45; var42++) {
						if (arg0.len[var44][var42] > var3) {
							var3 = arg0.len[var44][var42];
						}
						if (arg0.len[var44][var42] < var2) {
							var2 = arg0.len[var44][var42];
						}
					}
					createDecodeTables(arg0.limit[var44], arg0.base[var44], arg0.perm[var44], arg0.len[var44], var2, var3, var45);
					arg0.minLens[var44] = var2;
				}
				int var48 = arg0.nInUse + 1;
				int var53 = arg0.blockSize100k * 100000;
				int var49 = -1;
				byte var50 = 0;
				for (var42 = 0; var42 <= 255; var42++) {
					arg0.unzftab[var42] = 0;
				}
				int var33 = 4095;
				for (int var31 = 15; var31 >= 0; var31--) {
					for (int var32 = 15; var32 >= 0; var32--) {
						arg0.mtfa[var33] = (byte) (var31 * 16 + var32);
						var33--;
					}
					arg0.mtfbase[var31] = var33 + 1;
				}
				int var54 = 0;
				byte var61;
				if (var50 == 0) {
					var49++;
					var50 = 50;
					var61 = arg0.selector[var49];
					var23 = arg0.minLens[var61];
					var24 = arg0.limit[var61];
					var26 = arg0.perm[var61];
					var25 = arg0.base[var61];
				}
				int var51 = var50 - 1;
				int var58 = var23;
				int var59;
				byte var60;
				for (var59 = getBits(var23, arg0); var59 > var24[var58]; var59 = var59 << 1 | var60) {
					var58++;
					var60 = getBit(arg0);
				}
				int var52 = var26[var59 - var25[var58]];
				while (true) {
					while (var52 != var48) {
						if (var52 == 0 || var52 == 1) {
							int var55 = -1;
							int var56 = 1;
							do {
								if (var52 == 0) {
									var55 += var56;
								} else if (var52 == 1) {
									var55 += var56 * 2;
								}
								var56 *= 2;
								if (var51 == 0) {
									var49++;
									var51 = 50;
									var61 = arg0.selector[var49];
									var23 = arg0.minLens[var61];
									var24 = arg0.limit[var61];
									var26 = arg0.perm[var61];
									var25 = arg0.base[var61];
								}
								var51--;
								var58 = var23;
								for (var59 = getBits(var23, arg0); var59 > var24[var58]; var59 = var59 << 1 | var60) {
									var58++;
									var60 = getBit(arg0);
								}
								var52 = var26[var59 - var25[var58]];
							} while (var52 == 0 || var52 == 1);
							var55++;
							var1 = arg0.seqToUnseq[arg0.mtfa[arg0.mtfbase[0]] & 0xFF];
							arg0.unzftab[var1 & 0xFF] += var55;
							while (var55 > 0) {
								BZip2State.tt[var54] = var1 & 0xFF;
								var54++;
								var55--;
							}
						} else {
							int var40 = var52 - 1;
							int var37;
							if (var40 < 16) {
								var37 = arg0.mtfbase[0];
								var1 = arg0.mtfa[var37 + var40];
								while (var40 > 3) {
									int var41 = var37 + var40;
									arg0.mtfa[var41] = arg0.mtfa[var41 - 1];
									arg0.mtfa[var41 - 1] = arg0.mtfa[var41 - 2];
									arg0.mtfa[var41 - 2] = arg0.mtfa[var41 - 3];
									arg0.mtfa[var41 - 3] = arg0.mtfa[var41 - 4];
									var40 -= 4;
								}
								while (var40 > 0) {
									arg0.mtfa[var37 + var40] = arg0.mtfa[var37 + var40 - 1];
									var40--;
								}
								arg0.mtfa[var37] = var1;
							} else {
								int var38 = var40 / 16;
								int var39 = var40 % 16;
								var37 = arg0.mtfbase[var38] + var39;
								var1 = arg0.mtfa[var37];
								while (var37 > arg0.mtfbase[var38]) {
									arg0.mtfa[var37] = arg0.mtfa[var37 - 1];
									var37--;
								}
								int var10002 = arg0.mtfbase[var38]++;
								while (var38 > 0) {
									var10002 = arg0.mtfbase[var38]--;
									arg0.mtfa[arg0.mtfbase[var38]] = arg0.mtfa[arg0.mtfbase[var38 - 1] + 16 - 1];
									var38--;
								}
								var10002 = arg0.mtfbase[0]--;
								arg0.mtfa[arg0.mtfbase[0]] = var1;
								if (arg0.mtfbase[0] == 0) {
									int var36 = 4095;
									for (int var34 = 15; var34 >= 0; var34--) {
										for (int var35 = 15; var35 >= 0; var35--) {
											arg0.mtfa[var36] = arg0.mtfa[arg0.mtfbase[var34] + var35];
											var36--;
										}
										arg0.mtfbase[var34] = var36 + 1;
									}
								}
							}
							arg0.unzftab[arg0.seqToUnseq[var1 & 0xFF] & 0xFF]++;
							BZip2State.tt[var54] = arg0.seqToUnseq[var1 & 0xFF] & 0xFF;
							var54++;
							if (var51 == 0) {
								var49++;
								var51 = 50;
								var61 = arg0.selector[var49];
								var23 = arg0.minLens[var61];
								var24 = arg0.limit[var61];
								var26 = arg0.perm[var61];
								var25 = arg0.base[var61];
							}
							var51--;
							var58 = var23;
							for (var59 = getBits(var23, arg0); var59 > var24[var58]; var59 = var59 << 1 | var60) {
								var58++;
								var60 = getBit(arg0);
							}
							var52 = var26[var59 - var25[var58]];
						}
					}
					arg0.state_out_len = 0;
					arg0.state_out_ch = 0;
					arg0.cftab[0] = 0;
					for (var42 = 1; var42 <= 256; var42++) {
						arg0.cftab[var42] = arg0.unzftab[var42 - 1];
					}
					for (var42 = 1; var42 <= 256; var42++) {
						arg0.cftab[var42] += arg0.cftab[var42 - 1];
					}
					for (var42 = 0; var42 < var54; var42++) {
						var1 = (byte) (BZip2State.tt[var42] & 0xFF);
						BZip2State.tt[arg0.cftab[var1 & 0xFF]] |= var42 << 8;
						arg0.cftab[var1 & 0xFF]++;
					}
					arg0.tPos = BZip2State.tt[arg0.origPtr] >> 8;
					arg0.c_nblock_used = 0;
					arg0.tPos = BZip2State.tt[arg0.tPos];
					arg0.k0 = (byte) (arg0.tPos & 0xFF);
					arg0.tPos >>= 0x8;
					arg0.c_nblock_used++;
					arg0.save_nblock = var54;
					finish(arg0);
					if (arg0.c_nblock_used == arg0.save_nblock + 1 && arg0.state_out_len == 0) {
						var27 = true;
						break;
					}
					var27 = false;
					break;
				}
			}
			return;
		}
	}

	@ObfuscatedName("rb.c(Lsb;)B")
	public static byte getUnsignedChar(BZip2State arg0) {
		return (byte) getBits(8, arg0);
	}

	@ObfuscatedName("rb.d(Lsb;)B")
	public static byte getBit(BZip2State arg0) {
		return (byte) getBits(1, arg0);
	}

	@ObfuscatedName("rb.a(ILsb;)I")
	public static int getBits(int arg0, BZip2State arg1) {
		while (arg1.bsLive < arg0) {
			arg1.bsBuff = arg1.bsBuff << 8 | arg1.stream[arg1.next_in] & 0xFF;
			arg1.bsLive += 8;
			arg1.next_in++;
			arg1.avail_in--;
			arg1.total_in_lo32++;
			if (arg1.total_in_lo32 == 0) {
				arg1.total_in_hi32++;
			}
		}
		int var3 = arg1.bsBuff >> arg1.bsLive - arg0 & (0x1 << arg0) - 1;
		arg1.bsLive -= arg0;
		return var3;
	}

	@ObfuscatedName("rb.e(Lsb;)V")
	public static void makeMaps(BZip2State arg0) {
		arg0.nInUse = 0;
		for (int var1 = 0; var1 < 256; var1++) {
			if (arg0.inUse[var1]) {
				arg0.seqToUnseq[arg0.nInUse] = (byte) var1;
				arg0.nInUse++;
			}
		}
	}

	@ObfuscatedName("rb.a([I[I[I[BIII)V")
	public static void createDecodeTables(int[] arg0, int[] arg1, int[] arg2, byte[] arg3, int arg4, int arg5, int arg6) {
		int var7 = 0;
		int var8;
		for (var8 = arg4; var8 <= arg5; var8++) {
			for (int var9 = 0; var9 < arg6; var9++) {
				if (arg3[var9] == var8) {
					arg2[var7] = var9;
					var7++;
				}
			}
		}
		for (var8 = 0; var8 < 23; var8++) {
			arg1[var8] = 0;
		}
		for (var8 = 0; var8 < arg6; var8++) {
			arg1[arg3[var8] + 1]++;
		}
		for (var8 = 1; var8 < 23; var8++) {
			arg1[var8] += arg1[var8 - 1];
		}
		for (var8 = 0; var8 < 23; var8++) {
			arg0[var8] = 0;
		}
		int var10 = 0;
		for (var8 = arg4; var8 <= arg5; var8++) {
			var10 += arg1[var8 + 1] - arg1[var8];
			arg0[var8] = var10 - 1;
			var10 <<= 0x1;
		}
		for (var8 = arg4 + 1; var8 <= arg5; var8++) {
			arg1[var8] = (arg0[var8 - 1] + 1 << 1) - arg1[var8];
		}
	}
}
