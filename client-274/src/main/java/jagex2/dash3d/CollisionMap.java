package jagex2.dash3d;

import deob.ObfuscatedName;

@ObfuscatedName("jc")
public final class CollisionMap {

	@ObfuscatedName("jc.h")
	public final int startX;

	@ObfuscatedName("jc.i")
	public final int startZ;

	@ObfuscatedName("jc.j")
	public final int sizeX;

	@ObfuscatedName("jc.k")
	public final int sizeZ;

	@ObfuscatedName("jc.l")
	public final int[][] flags;

	public CollisionMap(int arg0, int arg1) {
		startX = 0;
		startZ = 0;
		sizeX = arg1;
		sizeZ = arg0;
		flags = new int[sizeX][sizeZ];
		reset();
	}

	@ObfuscatedName("jc.a(B)V")
	public void reset() {
		for (int var2 = 0; var2 < sizeX; var2++) {
			for (int var3 = 0; var3 < sizeZ; var3++) {
				if (var2 == 0 || var3 == 0 || var2 == sizeX - 1 || var3 == sizeZ - 1) {
					flags[var2][var3] = 16777215;
				} else {
					flags[var2][var3] = 0;
				}
			}
		}
	}

	@ObfuscatedName("jc.a(IIZIBI)V")
	public void addWall(int arg0, int arg1, boolean arg2, int arg3, int arg5) {
		int var7 = arg5 - startX;
		int var8 = arg3 - startZ;
		if (arg1 == 0) {
			if (arg0 == 0) {
				addCMap(var7, var8, 128);
				addCMap(var7 - 1, var8, 8);
			}
			if (arg0 == 1) {
				addCMap(var7, var8, 2);
				addCMap(var7, var8 + 1, 32);
			}
			if (arg0 == 2) {
				addCMap(var7, var8, 8);
				addCMap(var7 + 1, var8, 128);
			}
			if (arg0 == 3) {
				addCMap(var7, var8, 32);
				addCMap(var7, var8 - 1, 2);
			}
		}
		if (arg1 == 1 || arg1 == 3) {
			if (arg0 == 0) {
				addCMap(var7, var8, 1);
				addCMap(var7 - 1, var8 + 1, 16);
			}
			if (arg0 == 1) {
				addCMap(var7, var8, 4);
				addCMap(var7 + 1, var8 + 1, 64);
			}
			if (arg0 == 2) {
				addCMap(var7, var8, 16);
				addCMap(var7 + 1, var8 - 1, 1);
			}
			if (arg0 == 3) {
				addCMap(var7, var8, 64);
				addCMap(var7 - 1, var8 - 1, 4);
			}
		}
		if (arg1 == 2) {
			if (arg0 == 0) {
				addCMap(var7, var8, 130);
				addCMap(var7 - 1, var8, 8);
				addCMap(var7, var8 + 1, 32);
			}
			if (arg0 == 1) {
				addCMap(var7, var8, 10);
				addCMap(var7, var8 + 1, 32);
				addCMap(var7 + 1, var8, 128);
			}
			if (arg0 == 2) {
				addCMap(var7, var8, 40);
				addCMap(var7 + 1, var8, 128);
				addCMap(var7, var8 - 1, 2);
			}
			if (arg0 == 3) {
				addCMap(var7, var8, 160);
				addCMap(var7, var8 - 1, 2);
				addCMap(var7 - 1, var8, 8);
			}
		}
		if (!arg2) {
			return;
		}
		if (arg1 == 0) {
			if (arg0 == 0) {
				addCMap(var7, var8, 65536);
				addCMap(var7 - 1, var8, 4096);
			}
			if (arg0 == 1) {
				addCMap(var7, var8, 1024);
				addCMap(var7, var8 + 1, 16384);
			}
			if (arg0 == 2) {
				addCMap(var7, var8, 4096);
				addCMap(var7 + 1, var8, 65536);
			}
			if (arg0 == 3) {
				addCMap(var7, var8, 16384);
				addCMap(var7, var8 - 1, 1024);
			}
		}
		if (arg1 == 1 || arg1 == 3) {
			if (arg0 == 0) {
				addCMap(var7, var8, 512);
				addCMap(var7 - 1, var8 + 1, 8192);
			}
			if (arg0 == 1) {
				addCMap(var7, var8, 2048);
				addCMap(var7 + 1, var8 + 1, 32768);
			}
			if (arg0 == 2) {
				addCMap(var7, var8, 8192);
				addCMap(var7 + 1, var8 - 1, 512);
			}
			if (arg0 == 3) {
				addCMap(var7, var8, 32768);
				addCMap(var7 - 1, var8 - 1, 2048);
			}
		}
		if (arg1 != 2) {
			return;
		}
		if (arg0 == 0) {
			addCMap(var7, var8, 66560);
			addCMap(var7 - 1, var8, 4096);
			addCMap(var7, var8 + 1, 16384);
		}
		if (arg0 == 1) {
			addCMap(var7, var8, 5120);
			addCMap(var7, var8 + 1, 16384);
			addCMap(var7 + 1, var8, 65536);
		}
		if (arg0 == 2) {
			addCMap(var7, var8, 20480);
			addCMap(var7 + 1, var8, 65536);
			addCMap(var7, var8 - 1, 1024);
		}
		if (arg0 == 3) {
			addCMap(var7, var8, 81920);
			addCMap(var7, var8 - 1, 1024);
			addCMap(var7 - 1, var8, 4096);
			return;
		}
	}

	@ObfuscatedName("jc.a(IIIIIZI)V")
	public void addLoc(int arg0, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
		int var8 = 256;
		if (arg5) {
			var8 += 131072;
		}
		int var9 = arg1 - startX;
		int var10 = arg3 - startZ;
		if (arg4 == 1 || arg4 == 3) {
			int var11 = arg0;
			arg0 = arg2;
			arg2 = var11;
		}
		for (int var12 = var9; var12 < var9 + arg0; var12++) {
			if (var12 >= 0 && var12 < sizeX) {
				for (int var13 = var10; var13 < var10 + arg2; var13++) {
					if (var13 >= 0 && var13 < sizeZ) {
						addCMap(var12, var13, var8);
					}
				}
			}
		}
	}

	@ObfuscatedName("jc.a(ZII)V")
	public void blockGround(int arg1, int arg2) {
		int var4 = arg1 - startX;
		int var5 = arg2 - startZ;
		flags[var4][var5] |= 0x200000;
	}

	@ObfuscatedName("jc.a(III)V")
	public void addCMap(int arg0, int arg1, int arg2) {
		flags[arg0][arg1] |= arg2;
	}

	@ObfuscatedName("jc.a(BIZIII)V")
	public void delWall(int arg1, boolean arg2, int arg3, int arg4, int arg5) {
		int var7 = arg5 - startX;
		int var8 = arg4 - startZ;
		if (arg1 == 0) {
			if (arg3 == 0) {
				remCMap(var8, 128, var7);
				remCMap(var8, 8, var7 - 1);
			}
			if (arg3 == 1) {
				remCMap(var8, 2, var7);
				remCMap(var8 + 1, 32, var7);
			}
			if (arg3 == 2) {
				remCMap(var8, 8, var7);
				remCMap(var8, 128, var7 + 1);
			}
			if (arg3 == 3) {
				remCMap(var8, 32, var7);
				remCMap(var8 - 1, 2, var7);
			}
		}
		if (arg1 == 1 || arg1 == 3) {
			if (arg3 == 0) {
				remCMap(var8, 1, var7);
				remCMap(var8 + 1, 16, var7 - 1);
			}
			if (arg3 == 1) {
				remCMap(var8, 4, var7);
				remCMap(var8 + 1, 64, var7 + 1);
			}
			if (arg3 == 2) {
				remCMap(var8, 16, var7);
				remCMap(var8 - 1, 1, var7 + 1);
			}
			if (arg3 == 3) {
				remCMap(var8, 64, var7);
				remCMap(var8 - 1, 4, var7 - 1);
			}
		}
		if (arg1 == 2) {
			if (arg3 == 0) {
				remCMap(var8, 130, var7);
				remCMap(var8, 8, var7 - 1);
				remCMap(var8 + 1, 32, var7);
			}
			if (arg3 == 1) {
				remCMap(var8, 10, var7);
				remCMap(var8 + 1, 32, var7);
				remCMap(var8, 128, var7 + 1);
			}
			if (arg3 == 2) {
				remCMap(var8, 40, var7);
				remCMap(var8, 128, var7 + 1);
				remCMap(var8 - 1, 2, var7);
			}
			if (arg3 == 3) {
				remCMap(var8, 160, var7);
				remCMap(var8 - 1, 2, var7);
				remCMap(var8, 8, var7 - 1);
			}
		}
		if (!arg2) {
			return;
		}
		if (arg1 == 0) {
			if (arg3 == 0) {
				remCMap(var8, 65536, var7);
				remCMap(var8, 4096, var7 - 1);
			}
			if (arg3 == 1) {
				remCMap(var8, 1024, var7);
				remCMap(var8 + 1, 16384, var7);
			}
			if (arg3 == 2) {
				remCMap(var8, 4096, var7);
				remCMap(var8, 65536, var7 + 1);
			}
			if (arg3 == 3) {
				remCMap(var8, 16384, var7);
				remCMap(var8 - 1, 1024, var7);
			}
		}
		if (arg1 == 1 || arg1 == 3) {
			if (arg3 == 0) {
				remCMap(var8, 512, var7);
				remCMap(var8 + 1, 8192, var7 - 1);
			}
			if (arg3 == 1) {
				remCMap(var8, 2048, var7);
				remCMap(var8 + 1, 32768, var7 + 1);
			}
			if (arg3 == 2) {
				remCMap(var8, 8192, var7);
				remCMap(var8 - 1, 512, var7 + 1);
			}
			if (arg3 == 3) {
				remCMap(var8, 32768, var7);
				remCMap(var8 - 1, 2048, var7 - 1);
			}
		}
		if (arg1 != 2) {
			return;
		}
		if (arg3 == 0) {
			remCMap(var8, 66560, var7);
			remCMap(var8, 4096, var7 - 1);
			remCMap(var8 + 1, 16384, var7);
		}
		if (arg3 == 1) {
			remCMap(var8, 5120, var7);
			remCMap(var8 + 1, 16384, var7);
			remCMap(var8, 65536, var7 + 1);
		}
		if (arg3 == 2) {
			remCMap(var8, 20480, var7);
			remCMap(var8, 65536, var7 + 1);
			remCMap(var8 - 1, 1024, var7);
		}
		if (arg3 == 3) {
			remCMap(var8, 81920, var7);
			remCMap(var8 - 1, 1024, var7);
			remCMap(var8, 4096, var7 - 1);
			return;
		}
	}

	@ObfuscatedName("jc.b(IIIIIZI)V")
	public void delLoc(int arg1, int arg2, int arg3, int arg4, boolean arg5, int arg6) {
		int var8 = 256;
		if (arg5) {
			var8 += 131072;
		}
		int var9 = arg2 - startX;
		int var10 = arg3 - startZ;
		if (arg4 == 1 || arg4 == 3) {
			int var11 = arg1;
			arg1 = arg6;
			arg6 = var11;
		}
		for (int var12 = var9; var12 < var9 + arg1; var12++) {
			if (var12 >= 0 && var12 < sizeX) {
				for (int var13 = var10; var13 < var10 + arg6; var13++) {
					if (var13 >= 0 && var13 < sizeZ) {
						remCMap(var13, var8, var12);
					}
				}
			}
		}
	}

	@ObfuscatedName("jc.a(IIII)V")
	public void remCMap(int arg0, int arg2, int arg3) {
		flags[arg3][arg0] &= 16777215 - arg2;
	}

	@ObfuscatedName("jc.b(III)V")
	public void unblockGround(int arg0, int arg2) {
		int var4 = arg0 - startX;
		int var5 = arg2 - startZ;
		flags[var4][var5] &= 0xDFFFFF;
	}

	@ObfuscatedName("jc.a(ZIIIIII)Z")
	public boolean testWall(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		if (arg1 == arg3 && arg6 == arg4) {
			return true;
		}
		int var8 = arg1 - startX;
		int var9 = arg6 - startZ;
		int var10 = arg3 - startX;
		int var11 = arg4 - startZ;
		if (arg5 == 0) {
			if (arg2 == 0) {
				if (var8 == var10 - 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x280120) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg2 == 1) {
				if (var8 == var10 && var9 == var11 + 1) {
					return true;
				}
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x280108) == 0) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x280180) == 0) {
					return true;
				}
			} else if (arg2 == 2) {
				if (var8 == var10 + 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x280120) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg2 == 3) {
				if (var8 == var10 && var9 == var11 - 1) {
					return true;
				}
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x280108) == 0) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x280180) == 0) {
					return true;
				}
			}
		}
		if (arg5 == 2) {
			if (arg2 == 0) {
				if (var8 == var10 - 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x280180) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg2 == 1) {
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x280108) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg2 == 2) {
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x280108) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x280120) == 0) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1) {
					return true;
				}
			} else if (arg2 == 3) {
				if (var8 == var10 - 1 && var9 == var11) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x280120) == 0) {
					return true;
				}
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x280180) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1) {
					return true;
				}
			}
		}
		if (arg5 == 9) {
			if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x20) == 0) {
				return true;
			}
			if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x2) == 0) {
				return true;
			}
			if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x8) == 0) {
				return true;
			}
			if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x80) == 0) {
				return true;
			}
		}
		return false;
	}

	@ObfuscatedName("jc.a(IIIIIII)Z")
	public boolean testWDecor(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		if (arg2 == arg0 && arg5 == arg1) {
			return true;
		}
		int var8 = arg2 - startX;
		int var9 = arg5 - startZ;
		int var10 = arg0 - startX;
		int var11 = arg1 - startZ;
		if (arg3 == 6 || arg3 == 7) {
			if (arg3 == 7) {
				arg4 = arg4 + 2 & 0x3;
			}
			if (arg4 == 0) {
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x80) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x2) == 0) {
					return true;
				}
			} else if (arg4 == 1) {
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x8) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x2) == 0) {
					return true;
				}
			} else if (arg4 == 2) {
				if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x8) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x20) == 0) {
					return true;
				}
			} else if (arg4 == 3) {
				if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x80) == 0) {
					return true;
				}
				if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x20) == 0) {
					return true;
				}
			}
		}
		if (arg3 == 8) {
			if (var8 == var10 && var9 == var11 + 1 && (flags[var8][var9] & 0x20) == 0) {
				return true;
			}
			if (var8 == var10 && var9 == var11 - 1 && (flags[var8][var9] & 0x2) == 0) {
				return true;
			}
			if (var8 == var10 - 1 && var9 == var11 && (flags[var8][var9] & 0x8) == 0) {
				return true;
			}
			if (var8 == var10 + 1 && var9 == var11 && (flags[var8][var9] & 0x80) == 0) {
				return true;
			}
		}
		return false;
	}

	@ObfuscatedName("jc.a(IIIIIIII)Z")
	public boolean testLoc(int arg0, int arg1, int arg2, int arg4, int arg5, int arg6, int arg7) {
		int var10 = arg0 + arg1 - 1;
		int var11 = arg4 + arg7 - 1;
		if (arg6 >= arg0 && arg6 <= var10 && arg5 >= arg4 && arg5 <= var11) {
			return true;
		} else if (arg6 == arg0 - 1 && arg5 >= arg4 && arg5 <= var11 && (flags[arg6 - startX][arg5 - startZ] & 0x8) == 0 && (arg2 & 0x8) == 0) {
			return true;
		} else if (arg6 == var10 + 1 && arg5 >= arg4 && arg5 <= var11 && (flags[arg6 - startX][arg5 - startZ] & 0x80) == 0 && (arg2 & 0x2) == 0) {
			return true;
		} else if (arg5 == arg4 - 1 && arg6 >= arg0 && arg6 <= var10 && (flags[arg6 - startX][arg5 - startZ] & 0x2) == 0 && (arg2 & 0x4) == 0) {
			return true;
		} else {
			return arg5 == var11 + 1 && arg6 >= arg0 && arg6 <= var10 && (flags[arg6 - startX][arg5 - startZ] & 0x20) == 0 && (arg2 & 0x1) == 0;
		}
	}
}
