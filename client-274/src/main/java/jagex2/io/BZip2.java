package jagex2.io;

import deob.ObfuscatedName;

@ObfuscatedName("rb")
public final class BZip2 {

	@ObfuscatedName("rb.a")
	public static final BZip2State state = new BZip2State();

	@ObfuscatedName("rb.a([BI[BII)I")
	public static int decompress(byte[] arg0, int arg1, byte[] arg2, int arg3, int arg4) {
		BZip2State var5 = state;
		synchronized (state) {
			state.field765 = arg2;
			state.field766 = arg4;
			state.field770 = arg0;
			state.field771 = 0;
			state.field767 = arg3;
			state.field772 = arg1;
			state.field779 = 0;
			state.field778 = 0;
			state.field768 = 0;
			state.field769 = 0;
			state.field773 = 0;
			state.field774 = 0;
			state.field781 = 0;
			decompress(state);
			return arg1 - state.field772;
		}
	}

	@ObfuscatedName("rb.a(Lsb;)V")
	public static void finish(BZip2State arg0) {
		byte var1 = arg0.field775;
		int var2 = arg0.field776;
		int var3 = arg0.field786;
		int var4 = arg0.field784;
		int[] var5 = BZip2State.tt;
		int var6 = arg0.field783;
		byte[] var7 = arg0.field770;
		int var8 = arg0.field771;
		int var9 = arg0.field772;
		int var10 = var9;
		int var11 = arg0.field803 + 1;
		label67: while (true) {
			if (var2 > 0) {
				while (true) {
					if (var9 == 0) {
						break label67;
					}
					if (var2 == 1) {
						if (var9 == 0) {
							var2 = 1;
							break label67;
						}
						var7[var8] = var1;
						var8++;
						var9--;
						break;
					}
					var7[var8] = var1;
					var2--;
					var8++;
					var9--;
				}
			}
			boolean var12 = true;
			while (var12) {
				var12 = false;
				if (var3 == var11) {
					var2 = 0;
					break label67;
				}
				var1 = (byte) var4;
				int var13 = var5[var6];
				byte var14 = (byte) (var13 & 0xFF);
				var6 = var13 >> 8;
				var3++;
				if (var14 != var4) {
					var4 = var14;
					if (var9 == 0) {
						var2 = 1;
						break label67;
					}
					var7[var8] = var1;
					var8++;
					var9--;
					var12 = true;
				} else if (var3 == var11) {
					if (var9 == 0) {
						var2 = 1;
						break label67;
					}
					var7[var8] = var1;
					var8++;
					var9--;
					var12 = true;
				}
			}
			var2 = 2;
			int var15 = var5[var6];
			byte var16 = (byte) (var15 & 0xFF);
			var6 = var15 >> 8;
			var3++;
			if (var3 != var11) {
				if (var16 == var4) {
					var2 = 3;
					int var17 = var5[var6];
					byte var18 = (byte) (var17 & 0xFF);
					var6 = var17 >> 8;
					var3++;
					if (var3 != var11) {
						if (var18 == var4) {
							int var19 = var5[var6];
							byte var20 = (byte) (var19 & 0xFF);
							int var21 = var19 >> 8;
							var3++;
							var2 = (var20 & 0xFF) + 4;
							int var22 = var5[var21];
							var4 = (byte) (var22 & 0xFF);
							var6 = var22 >> 8;
							var3++;
						} else {
							var4 = var18;
						}
					}
				} else {
					var4 = var16;
				}
			}
		}
		int var23 = arg0.field773;
		arg0.field773 += var10 - var9;
		if (arg0.field773 < var23) {
			arg0.field774++;
		}
		arg0.field775 = var1;
		arg0.field776 = var2;
		arg0.field786 = var3;
		arg0.field784 = var4;
		BZip2State.tt = var5;
		arg0.field783 = var6;
		arg0.field770 = var7;
		arg0.field771 = var8;
		arg0.field772 = var9;
	}

	@ObfuscatedName("rb.b(Lsb;)V")
	public static void decompress(BZip2State arg0) {
		int var1 = 0;
		int[] var2 = null;
		int[] var3 = null;
		int[] var4 = null;
		arg0.blockSize100k = 1;
		if (BZip2State.tt == null) {
			BZip2State.tt = new int[arg0.blockSize100k * 100000];
		}
		boolean var5 = true;
		while (true) {
			while (var5) {
				byte var6 = method259(arg0);
				if (var6 == 23) {
					return;
				}
				byte var7 = method259(arg0);
				byte var8 = method259(arg0);
				byte var9 = method259(arg0);
				byte var10 = method259(arg0);
				byte var11 = method259(arg0);
				arg0.field781++;
				byte var12 = method259(arg0);
				byte var13 = method259(arg0);
				byte var14 = method259(arg0);
				byte var15 = method259(arg0);
				byte var16 = method260(arg0);
				if (var16 == 0) {
					arg0.field777 = false;
				} else {
					arg0.field777 = true;
				}
				if (arg0.field777) {
					System.out.println("PANIC! RANDOMISED BLOCK!");
				}
				arg0.field782 = 0;
				byte var17 = method259(arg0);
				arg0.field782 = arg0.field782 << 8 | var17 & 0xFF;
				byte var18 = method259(arg0);
				arg0.field782 = arg0.field782 << 8 | var18 & 0xFF;
				byte var19 = method259(arg0);
				arg0.field782 = arg0.field782 << 8 | var19 & 0xFF;
				for (int var20 = 0; var20 < 16; var20++) {
					byte var21 = method260(arg0);
					if (var21 == 1) {
						arg0.field792[var20] = true;
					} else {
						arg0.field792[var20] = false;
					}
				}
				for (int var22 = 0; var22 < 256; var22++) {
					arg0.field791[var22] = false;
				}
				for (int var23 = 0; var23 < 16; var23++) {
					if (arg0.field792[var23]) {
						for (int var24 = 0; var24 < 16; var24++) {
							byte var25 = method260(arg0);
							if (var25 == 1) {
								arg0.field791[var23 * 16 + var24] = true;
							}
						}
					}
				}
				method262(arg0);
				int var26 = arg0.field790 + 2;
				int var27 = method261(3, arg0);
				int var28 = method261(15, arg0);
				for (int var29 = 0; var29 < var28; var29++) {
					int var30 = 0;
					while (true) {
						byte var31 = method260(arg0);
						if (var31 == 0) {
							arg0.field797[var29] = (byte) var30;
							break;
						}
						var30++;
					}
				}
				byte[] var32 = new byte[6];
				byte var33 = 0;
				while (var33 < var27) {
					var32[var33] = var33++;
				}
				for (int var34 = 0; var34 < var28; var34++) {
					byte var35 = arg0.field797[var34];
					byte var36 = var32[var35];
					while (var35 > 0) {
						var32[var35] = var32[var35 - 1];
						var35--;
					}
					var32[0] = var36;
					arg0.field796[var34] = var36;
				}
				for (int var37 = 0; var37 < var27; var37++) {
					int var38 = method261(5, arg0);
					for (int var39 = 0; var39 < var26; var39++) {
						while (true) {
							byte var40 = method260(arg0);
							if (var40 == 0) {
								arg0.field798[var37][var39] = (byte) var38;
								break;
							}
							byte var41 = method260(arg0);
							if (var41 == 0) {
								var38++;
							} else {
								var38--;
							}
						}
					}
				}
				for (int var42 = 0; var42 < var27; var42++) {
					byte var43 = 32;
					byte var44 = 0;
					for (int var45 = 0; var45 < var26; var45++) {
						if (arg0.field798[var42][var45] > var44) {
							var44 = arg0.field798[var42][var45];
						}
						if (arg0.field798[var42][var45] < var43) {
							var43 = arg0.field798[var42][var45];
						}
					}
					method263(arg0.field799[var42], arg0.field800[var42], arg0.field801[var42], arg0.field798[var42], var43, var44, var26);
					arg0.field802[var42] = var43;
				}
				int var46 = arg0.field790 + 1;
				int var47 = arg0.blockSize100k * 100000;
				int var48 = -1;
				byte var49 = 0;
				for (int var50 = 0; var50 <= 255; var50++) {
					arg0.field785[var50] = 0;
				}
				int var51 = 4095;
				for (int var52 = 15; var52 >= 0; var52--) {
					for (int var53 = 15; var53 >= 0; var53--) {
						arg0.field794[var51] = (byte) (var52 * 16 + var53);
						var51--;
					}
					arg0.field795[var52] = var51 + 1;
				}
				int var54 = 0;
				if (var49 == 0) {
					var48++;
					var49 = 50;
					byte var55 = arg0.field796[var48];
					var1 = arg0.field802[var55];
					var2 = arg0.field799[var55];
					var4 = arg0.field801[var55];
					var3 = arg0.field800[var55];
				}
				int var85 = var49 - 1;
				int var56 = var1;
				int var57;
				byte var58;
				for (var57 = method261(var1, arg0); var57 > var2[var56]; var57 = var57 << 1 | var58) {
					var56++;
					var58 = method260(arg0);
				}
				int var59 = var4[var57 - var3[var56]];
				while (true) {
					while (var59 != var46) {
						if (var59 == 0 || var59 == 1) {
							int var60 = -1;
							int var61 = 1;
							do {
								if (var59 == 0) {
									var60 += var61;
								} else if (var59 == 1) {
									var60 += var61 * 2;
								}
								var61 *= 2;
								if (var85 == 0) {
									var48++;
									var85 = 50;
									byte var62 = arg0.field796[var48];
									var1 = arg0.field802[var62];
									var2 = arg0.field799[var62];
									var4 = arg0.field801[var62];
									var3 = arg0.field800[var62];
								}
								var85--;
								int var63 = var1;
								int var64;
								byte var65;
								for (var64 = method261(var1, arg0); var64 > var2[var63]; var64 = var64 << 1 | var65) {
									var63++;
									var65 = method260(arg0);
								}
								var59 = var4[var64 - var3[var63]];
							} while (var59 == 0 || var59 == 1);
							var60++;
							byte var66 = arg0.field793[arg0.field794[arg0.field795[0]] & 0xFF];
							arg0.field785[var66 & 0xFF] += var60;
							while (var60 > 0) {
								BZip2State.tt[var54] = var66 & 0xFF;
								var54++;
								var60--;
							}
						} else {
							int var67 = var59 - 1;
							byte var69;
							if (var67 < 16) {
								int var68 = arg0.field795[0];
								var69 = arg0.field794[var68 + var67];
								while (var67 > 3) {
									int var70 = var68 + var67;
									arg0.field794[var70] = arg0.field794[var70 - 1];
									arg0.field794[var70 - 1] = arg0.field794[var70 - 2];
									arg0.field794[var70 - 2] = arg0.field794[var70 - 3];
									arg0.field794[var70 - 3] = arg0.field794[var70 - 4];
									var67 -= 4;
								}
								while (var67 > 0) {
									arg0.field794[var68 + var67] = arg0.field794[var68 + var67 - 1];
									var67--;
								}
								arg0.field794[var68] = var69;
							} else {
								int var71 = var67 / 16;
								int var72 = var67 % 16;
								int var73 = arg0.field795[var71] + var72;
								var69 = arg0.field794[var73];
								while (var73 > arg0.field795[var71]) {
									arg0.field794[var73] = arg0.field794[var73 - 1];
									var73--;
								}
								int var10002 = arg0.field795[var71]++;
								while (var71 > 0) {
									var10002 = arg0.field795[var71]--;
									arg0.field794[arg0.field795[var71]] = arg0.field794[arg0.field795[var71 - 1] + 16 - 1];
									var71--;
								}
								var10002 = arg0.field795[0]--;
								arg0.field794[arg0.field795[0]] = var69;
								if (arg0.field795[0] == 0) {
									int var74 = 4095;
									for (int var75 = 15; var75 >= 0; var75--) {
										for (int var76 = 15; var76 >= 0; var76--) {
											arg0.field794[var74] = arg0.field794[arg0.field795[var75] + var76];
											var74--;
										}
										arg0.field795[var75] = var74 + 1;
									}
								}
							}
							arg0.field785[arg0.field793[var69 & 0xFF] & 0xFF]++;
							BZip2State.tt[var54] = arg0.field793[var69 & 0xFF] & 0xFF;
							var54++;
							if (var85 == 0) {
								var48++;
								var85 = 50;
								byte var77 = arg0.field796[var48];
								var1 = arg0.field802[var77];
								var2 = arg0.field799[var77];
								var4 = arg0.field801[var77];
								var3 = arg0.field800[var77];
							}
							var85--;
							int var78 = var1;
							int var79;
							byte var80;
							for (var79 = method261(var1, arg0); var79 > var2[var78]; var79 = var79 << 1 | var80) {
								var78++;
								var80 = method260(arg0);
							}
							var59 = var4[var79 - var3[var78]];
						}
					}
					arg0.field776 = 0;
					arg0.field775 = 0;
					arg0.field787[0] = 0;
					for (int var81 = 1; var81 <= 256; var81++) {
						arg0.field787[var81] = arg0.field785[var81 - 1];
					}
					for (int var82 = 1; var82 <= 256; var82++) {
						arg0.field787[var82] += arg0.field787[var82 - 1];
					}
					for (int var83 = 0; var83 < var54; var83++) {
						byte var84 = (byte) (BZip2State.tt[var83] & 0xFF);
						BZip2State.tt[arg0.field787[var84 & 0xFF]] |= var83 << 8;
						arg0.field787[var84 & 0xFF]++;
					}
					arg0.field783 = BZip2State.tt[arg0.field782] >> 8;
					arg0.field786 = 0;
					arg0.field783 = BZip2State.tt[arg0.field783];
					arg0.field784 = (byte) (arg0.field783 & 0xFF);
					arg0.field783 >>= 0x8;
					arg0.field786++;
					arg0.field803 = var54;
					finish(arg0);
					if (arg0.field786 == arg0.field803 + 1 && arg0.field776 == 0) {
						var5 = true;
						break;
					}
					var5 = false;
					break;
				}
			}
			return;
		}
	}

	@ObfuscatedName("rb.c(Lsb;)B")
	public static byte method259(BZip2State arg0) {
		return (byte) method261(8, arg0);
	}

	@ObfuscatedName("rb.d(Lsb;)B")
	public static byte method260(BZip2State arg0) {
		return (byte) method261(1, arg0);
	}

	@ObfuscatedName("rb.a(ILsb;)I")
	public static int method261(int arg0, BZip2State arg1) {
		while (arg1.field779 < arg0) {
			arg1.field778 = arg1.field778 << 8 | arg1.field765[arg1.field766] & 0xFF;
			arg1.field779 += 8;
			arg1.field766++;
			arg1.field767--;
			arg1.field768++;
			if (arg1.field768 == 0) {
				arg1.field769++;
			}
		}
		int var2 = arg1.field778 >> arg1.field779 - arg0 & (0x1 << arg0) - 1;
		arg1.field779 -= arg0;
		return var2;
	}

	@ObfuscatedName("rb.e(Lsb;)V")
	public static void method262(BZip2State arg0) {
		arg0.field790 = 0;
		for (int var1 = 0; var1 < 256; var1++) {
			if (arg0.field791[var1]) {
				arg0.field793[arg0.field790] = (byte) var1;
				arg0.field790++;
			}
		}
	}

	@ObfuscatedName("rb.a([I[I[I[BIII)V")
	public static void method263(int[] arg0, int[] arg1, int[] arg2, byte[] arg3, int arg4, int arg5, int arg6) {
		int var7 = 0;
		for (int var8 = arg4; var8 <= arg5; var8++) {
			for (int var9 = 0; var9 < arg6; var9++) {
				if (arg3[var9] == var8) {
					arg2[var7] = var9;
					var7++;
				}
			}
		}
		for (int var10 = 0; var10 < 23; var10++) {
			arg1[var10] = 0;
		}
		for (int var11 = 0; var11 < arg6; var11++) {
			arg1[arg3[var11] + 1]++;
		}
		for (int var12 = 1; var12 < 23; var12++) {
			arg1[var12] += arg1[var12 - 1];
		}
		for (int var13 = 0; var13 < 23; var13++) {
			arg0[var13] = 0;
		}
		int var14 = 0;
		for (int var15 = arg4; var15 <= arg5; var15++) {
			int var16 = var14 + arg1[var15 + 1] - arg1[var15];
			arg0[var15] = var16 - 1;
			var14 = var16 << 1;
		}
		for (int var17 = arg4 + 1; var17 <= arg5; var17++) {
			arg1[var17] = (arg0[var17 - 1] + 1 << 1) - arg1[var17];
		}
	}
}
