package jagex2.dash3d;

import deob.ObfuscatedName;

@ObfuscatedName("ec")
public class CollisionMap {

	@ObfuscatedName("ec.a")
	public boolean field921 = false;

	@ObfuscatedName("ec.b")
	public int field922;

	@ObfuscatedName("ec.c")
	public byte field923 = 12;

	@ObfuscatedName("ec.d")
	public int field924 = 27808;

	@ObfuscatedName("ec.e")
	public boolean field925 = true;

	@ObfuscatedName("ec.f")
	public int offsetX;

	@ObfuscatedName("ec.g")
	public int offsetZ;

	@ObfuscatedName("ec.h")
	public int sizeX;

	@ObfuscatedName("ec.i")
	public int sizeZ;

	@ObfuscatedName("ec.j")
	public int[][] flags;

	public CollisionMap(int arg0, int arg1, int arg2) {
		while (arg1 >= 0) {
			this.field925 = !this.field925;
		}
		this.offsetX = 0;
		this.offsetZ = 0;
		this.sizeX = arg0;
		this.sizeZ = arg2;
		this.flags = new int[this.sizeX][this.sizeZ];
		this.reset((byte) 74);
	}

	@ObfuscatedName("ec.a(B)V")
	public void reset(byte arg0) {
		int var3;
		for (int var2 = 0; var2 < this.sizeX; var2++) {
			for (var3 = 0; var3 < this.sizeZ; var3++) {
				if (var2 == 0 || var3 == 0 || var2 == this.sizeX - 1 || var3 == this.sizeZ - 1) {
					this.flags[var2][var3] = 16777215;
				} else {
					this.flags[var2][var3] = 0;
				}
			}
		}
		if (arg0 != 74) {
			for (var3 = 1; var3 > 0; var3++) {
			}
		}
	}

	@ObfuscatedName("ec.a(ZIIIZI)V")
	public void addWall(boolean arg0, int arg1, int arg2, int arg3, boolean arg4, int arg5) {
		int var8 = arg3 - this.offsetX;
		if (!arg0) {
			this.field921 = !this.field921;
		}
		int var7 = arg2 - this.offsetZ;
		if (arg5 == 0) {
			if (arg1 == 0) {
				this.addCMap(var8, var7, 128);
				this.addCMap(var8 - 1, var7, 8);
			}
			if (arg1 == 1) {
				this.addCMap(var8, var7, 2);
				this.addCMap(var8, var7 + 1, 32);
			}
			if (arg1 == 2) {
				this.addCMap(var8, var7, 8);
				this.addCMap(var8 + 1, var7, 128);
			}
			if (arg1 == 3) {
				this.addCMap(var8, var7, 32);
				this.addCMap(var8, var7 - 1, 2);
			}
		}
		if (arg5 == 1 || arg5 == 3) {
			if (arg1 == 0) {
				this.addCMap(var8, var7, 1);
				this.addCMap(var8 - 1, var7 + 1, 16);
			}
			if (arg1 == 1) {
				this.addCMap(var8, var7, 4);
				this.addCMap(var8 + 1, var7 + 1, 64);
			}
			if (arg1 == 2) {
				this.addCMap(var8, var7, 16);
				this.addCMap(var8 + 1, var7 - 1, 1);
			}
			if (arg1 == 3) {
				this.addCMap(var8, var7, 64);
				this.addCMap(var8 - 1, var7 - 1, 4);
			}
		}
		if (arg5 == 2) {
			if (arg1 == 0) {
				this.addCMap(var8, var7, 130);
				this.addCMap(var8 - 1, var7, 8);
				this.addCMap(var8, var7 + 1, 32);
			}
			if (arg1 == 1) {
				this.addCMap(var8, var7, 10);
				this.addCMap(var8, var7 + 1, 32);
				this.addCMap(var8 + 1, var7, 128);
			}
			if (arg1 == 2) {
				this.addCMap(var8, var7, 40);
				this.addCMap(var8 + 1, var7, 128);
				this.addCMap(var8, var7 - 1, 2);
			}
			if (arg1 == 3) {
				this.addCMap(var8, var7, 160);
				this.addCMap(var8, var7 - 1, 2);
				this.addCMap(var8 - 1, var7, 8);
			}
		}
		if (!arg4) {
			return;
		}
		if (arg5 == 0) {
			if (arg1 == 0) {
				this.addCMap(var8, var7, 65536);
				this.addCMap(var8 - 1, var7, 4096);
			}
			if (arg1 == 1) {
				this.addCMap(var8, var7, 1024);
				this.addCMap(var8, var7 + 1, 16384);
			}
			if (arg1 == 2) {
				this.addCMap(var8, var7, 4096);
				this.addCMap(var8 + 1, var7, 65536);
			}
			if (arg1 == 3) {
				this.addCMap(var8, var7, 16384);
				this.addCMap(var8, var7 - 1, 1024);
			}
		}
		if (arg5 == 1 || arg5 == 3) {
			if (arg1 == 0) {
				this.addCMap(var8, var7, 512);
				this.addCMap(var8 - 1, var7 + 1, 8192);
			}
			if (arg1 == 1) {
				this.addCMap(var8, var7, 2048);
				this.addCMap(var8 + 1, var7 + 1, 32768);
			}
			if (arg1 == 2) {
				this.addCMap(var8, var7, 8192);
				this.addCMap(var8 + 1, var7 - 1, 512);
			}
			if (arg1 == 3) {
				this.addCMap(var8, var7, 32768);
				this.addCMap(var8 - 1, var7 - 1, 2048);
			}
		}
		if (arg5 != 2) {
			return;
		}
		if (arg1 == 0) {
			this.addCMap(var8, var7, 66560);
			this.addCMap(var8 - 1, var7, 4096);
			this.addCMap(var8, var7 + 1, 16384);
		}
		if (arg1 == 1) {
			this.addCMap(var8, var7, 5120);
			this.addCMap(var8, var7 + 1, 16384);
			this.addCMap(var8 + 1, var7, 65536);
		}
		if (arg1 == 2) {
			this.addCMap(var8, var7, 20480);
			this.addCMap(var8 + 1, var7, 65536);
			this.addCMap(var8, var7 - 1, 1024);
		}
		if (arg1 == 3) {
			this.addCMap(var8, var7, 81920);
			this.addCMap(var8, var7 - 1, 1024);
			this.addCMap(var8 - 1, var7, 4096);
			return;
		}
	}

	@ObfuscatedName("ec.a(IIIIIIZ)V")
	public void addLoc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6) {
		int var8 = 256;
		if (arg6) {
			var8 += 131072;
		}
		int var11 = arg3 - this.offsetX;
		int var12 = arg5 - this.offsetZ;
		int var9;
		if (arg0 == 1 || arg0 == 3) {
			var9 = arg2;
			arg2 = arg1;
			arg1 = var9;
		}
		int var10;
		for (var9 = var11; var9 < var11 + arg2; var9++) {
			if (var9 >= 0 && var9 < this.sizeX) {
				for (var10 = var12; var10 < var12 + arg1; var10++) {
					if (var10 >= 0 && var10 < this.sizeZ) {
						this.addCMap(var9, var10, var8);
					}
				}
			}
		}
		if (arg4 != 9) {
			for (var10 = 1; var10 > 0; var10++) {
			}
		}
	}

	@ObfuscatedName("ec.a(BII)V")
	public void setBlocked(byte arg0, int arg1, int arg2) {
		if (arg0 == this.field923) {
			int var5 = arg2 - this.offsetX;
			int var4 = arg1 - this.offsetZ;
			this.flags[var5][var4] |= 0x200000;
		}
	}

	@ObfuscatedName("ec.a(III)V")
	public void addCMap(int arg0, int arg1, int arg2) {
		this.flags[arg0][arg1] |= arg2;
	}

	@ObfuscatedName("ec.a(ZIIIII)V")
	public void delWall(boolean arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		int var9 = 9 / arg4;
		int var7 = arg2 - this.offsetX;
		int var8 = arg3 - this.offsetZ;
		if (arg5 == 0) {
			if (arg1 == 0) {
				this.remCMap(var8, (byte) -9, var7, 128);
				this.remCMap(var8, (byte) -9, var7 - 1, 8);
			}
			if (arg1 == 1) {
				this.remCMap(var8, (byte) -9, var7, 2);
				this.remCMap(var8 + 1, (byte) -9, var7, 32);
			}
			if (arg1 == 2) {
				this.remCMap(var8, (byte) -9, var7, 8);
				this.remCMap(var8, (byte) -9, var7 + 1, 128);
			}
			if (arg1 == 3) {
				this.remCMap(var8, (byte) -9, var7, 32);
				this.remCMap(var8 - 1, (byte) -9, var7, 2);
			}
		}
		if (arg5 == 1 || arg5 == 3) {
			if (arg1 == 0) {
				this.remCMap(var8, (byte) -9, var7, 1);
				this.remCMap(var8 + 1, (byte) -9, var7 - 1, 16);
			}
			if (arg1 == 1) {
				this.remCMap(var8, (byte) -9, var7, 4);
				this.remCMap(var8 + 1, (byte) -9, var7 + 1, 64);
			}
			if (arg1 == 2) {
				this.remCMap(var8, (byte) -9, var7, 16);
				this.remCMap(var8 - 1, (byte) -9, var7 + 1, 1);
			}
			if (arg1 == 3) {
				this.remCMap(var8, (byte) -9, var7, 64);
				this.remCMap(var8 - 1, (byte) -9, var7 - 1, 4);
			}
		}
		if (arg5 == 2) {
			if (arg1 == 0) {
				this.remCMap(var8, (byte) -9, var7, 130);
				this.remCMap(var8, (byte) -9, var7 - 1, 8);
				this.remCMap(var8 + 1, (byte) -9, var7, 32);
			}
			if (arg1 == 1) {
				this.remCMap(var8, (byte) -9, var7, 10);
				this.remCMap(var8 + 1, (byte) -9, var7, 32);
				this.remCMap(var8, (byte) -9, var7 + 1, 128);
			}
			if (arg1 == 2) {
				this.remCMap(var8, (byte) -9, var7, 40);
				this.remCMap(var8, (byte) -9, var7 + 1, 128);
				this.remCMap(var8 - 1, (byte) -9, var7, 2);
			}
			if (arg1 == 3) {
				this.remCMap(var8, (byte) -9, var7, 160);
				this.remCMap(var8 - 1, (byte) -9, var7, 2);
				this.remCMap(var8, (byte) -9, var7 - 1, 8);
			}
		}
		if (!arg0) {
			return;
		}
		if (arg5 == 0) {
			if (arg1 == 0) {
				this.remCMap(var8, (byte) -9, var7, 65536);
				this.remCMap(var8, (byte) -9, var7 - 1, 4096);
			}
			if (arg1 == 1) {
				this.remCMap(var8, (byte) -9, var7, 1024);
				this.remCMap(var8 + 1, (byte) -9, var7, 16384);
			}
			if (arg1 == 2) {
				this.remCMap(var8, (byte) -9, var7, 4096);
				this.remCMap(var8, (byte) -9, var7 + 1, 65536);
			}
			if (arg1 == 3) {
				this.remCMap(var8, (byte) -9, var7, 16384);
				this.remCMap(var8 - 1, (byte) -9, var7, 1024);
			}
		}
		if (arg5 == 1 || arg5 == 3) {
			if (arg1 == 0) {
				this.remCMap(var8, (byte) -9, var7, 512);
				this.remCMap(var8 + 1, (byte) -9, var7 - 1, 8192);
			}
			if (arg1 == 1) {
				this.remCMap(var8, (byte) -9, var7, 2048);
				this.remCMap(var8 + 1, (byte) -9, var7 + 1, 32768);
			}
			if (arg1 == 2) {
				this.remCMap(var8, (byte) -9, var7, 8192);
				this.remCMap(var8 - 1, (byte) -9, var7 + 1, 512);
			}
			if (arg1 == 3) {
				this.remCMap(var8, (byte) -9, var7, 32768);
				this.remCMap(var8 - 1, (byte) -9, var7 - 1, 2048);
			}
		}
		if (arg5 != 2) {
			return;
		}
		if (arg1 == 0) {
			this.remCMap(var8, (byte) -9, var7, 66560);
			this.remCMap(var8, (byte) -9, var7 - 1, 4096);
			this.remCMap(var8 + 1, (byte) -9, var7, 16384);
		}
		if (arg1 == 1) {
			this.remCMap(var8, (byte) -9, var7, 5120);
			this.remCMap(var8 + 1, (byte) -9, var7, 16384);
			this.remCMap(var8, (byte) -9, var7 + 1, 65536);
		}
		if (arg1 == 2) {
			this.remCMap(var8, (byte) -9, var7, 20480);
			this.remCMap(var8, (byte) -9, var7 + 1, 65536);
			this.remCMap(var8 - 1, (byte) -9, var7, 1024);
		}
		if (arg1 == 3) {
			this.remCMap(var8, (byte) -9, var7, 81920);
			this.remCMap(var8 - 1, (byte) -9, var7, 1024);
			this.remCMap(var8, (byte) -9, var7 - 1, 4096);
			return;
		}
	}

	@ObfuscatedName("ec.a(IIIIZZI)V")
	public void delLoc(int arg0, int arg1, int arg2, int arg3, boolean arg4, boolean arg5, int arg6) {
		int var8 = 256;
		if (arg5) {
			var8 += 131072;
		}
		int var12 = arg1 - this.offsetX;
		int var11 = arg0 - this.offsetZ;
		if (!arg4) {
			this.field922 = 67;
		}
		int var9;
		if (arg2 == 1 || arg2 == 3) {
			var9 = arg3;
			arg3 = arg6;
			arg6 = var9;
		}
		for (var9 = var12; var9 < var12 + arg3; var9++) {
			if (var9 >= 0 && var9 < this.sizeX) {
				for (int var10 = var11; var10 < var11 + arg6; var10++) {
					if (var10 >= 0 && var10 < this.sizeZ) {
						this.remCMap(var10, (byte) -9, var9, var8);
					}
				}
			}
		}
	}

	@ObfuscatedName("ec.a(IBII)V")
	public void remCMap(int arg0, byte arg1, int arg2, int arg3) {
		this.flags[arg2][arg0] &= 16777215 - arg3;
		if (arg1 == -9) {
			;
		}
	}

	@ObfuscatedName("ec.b(III)V")
	public void removeBlocked(int arg0, int arg1, int arg2) {
		if (arg2 == 0) {
			int var5 = arg1 - this.offsetX;
			int var4 = arg0 - this.offsetZ;
			this.flags[var5][var4] &= 0xDFFFFF;
		}
	}

	@ObfuscatedName("ec.a(IIIIIII)Z")
	public boolean testWall(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		if (arg6 == arg5 && arg4 == arg2) {
			return true;
		}
		int var11 = arg6 - this.offsetX;
		int var9 = arg4 - this.offsetZ;
		if (arg0 != -7517) {
			throw new NullPointerException();
		}
		int var10 = arg5 - this.offsetX;
		int var8 = arg2 - this.offsetZ;
		if (arg3 == 0) {
			if (arg1 == 0) {
				if (var11 == var10 - 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1 && (this.flags[var11][var9] & 0x280120) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1 && (this.flags[var11][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg1 == 1) {
				if (var11 == var10 && var9 == var8 + 1) {
					return true;
				}
				if (var11 == var10 - 1 && var9 == var8 && (this.flags[var11][var9] & 0x280108) == 0) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8 && (this.flags[var11][var9] & 0x280180) == 0) {
					return true;
				}
			} else if (arg1 == 2) {
				if (var11 == var10 + 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1 && (this.flags[var11][var9] & 0x280120) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1 && (this.flags[var11][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg1 == 3) {
				if (var11 == var10 && var9 == var8 - 1) {
					return true;
				}
				if (var11 == var10 - 1 && var9 == var8 && (this.flags[var11][var9] & 0x280108) == 0) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8 && (this.flags[var11][var9] & 0x280180) == 0) {
					return true;
				}
			}
		}
		if (arg3 == 2) {
			if (arg1 == 0) {
				if (var11 == var10 - 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8 && (this.flags[var11][var9] & 0x280180) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1 && (this.flags[var11][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg1 == 1) {
				if (var11 == var10 - 1 && var9 == var8 && (this.flags[var11][var9] & 0x280108) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1 && (this.flags[var11][var9] & 0x280102) == 0) {
					return true;
				}
			} else if (arg1 == 2) {
				if (var11 == var10 - 1 && var9 == var8 && (this.flags[var11][var9] & 0x280108) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1 && (this.flags[var11][var9] & 0x280120) == 0) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1) {
					return true;
				}
			} else if (arg1 == 3) {
				if (var11 == var10 - 1 && var9 == var8) {
					return true;
				}
				if (var11 == var10 && var9 == var8 + 1 && (this.flags[var11][var9] & 0x280120) == 0) {
					return true;
				}
				if (var11 == var10 + 1 && var9 == var8 && (this.flags[var11][var9] & 0x280180) == 0) {
					return true;
				}
				if (var11 == var10 && var9 == var8 - 1) {
					return true;
				}
			}
		}
		if (arg3 == 9) {
			if (var11 == var10 && var9 == var8 + 1 && (this.flags[var11][var9] & 0x20) == 0) {
				return true;
			}
			if (var11 == var10 && var9 == var8 - 1 && (this.flags[var11][var9] & 0x2) == 0) {
				return true;
			}
			if (var11 == var10 - 1 && var9 == var8 && (this.flags[var11][var9] & 0x8) == 0) {
				return true;
			}
			if (var11 == var10 + 1 && var9 == var8 && (this.flags[var11][var9] & 0x80) == 0) {
				return true;
			}
		}
		return false;
	}

	@ObfuscatedName("ec.b(IIIIIII)Z")
	public boolean testWDecor(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		if (arg2 != this.field924) {
			this.field922 = -218;
		}
		if (arg3 == arg4 && arg5 == arg6) {
			return true;
		}
		int var8 = arg3 - this.offsetX;
		int var10 = arg5 - this.offsetZ;
		int var9 = arg4 - this.offsetX;
		int var11 = arg6 - this.offsetZ;
		if (arg1 == 6 || arg1 == 7) {
			if (arg1 == 7) {
				arg0 = arg0 + 2 & 0x3;
			}
			if (arg0 == 0) {
				if (var8 == var9 + 1 && var10 == var11 && (this.flags[var8][var10] & 0x80) == 0) {
					return true;
				}
				if (var8 == var9 && var10 == var11 - 1 && (this.flags[var8][var10] & 0x2) == 0) {
					return true;
				}
			} else if (arg0 == 1) {
				if (var8 == var9 - 1 && var10 == var11 && (this.flags[var8][var10] & 0x8) == 0) {
					return true;
				}
				if (var8 == var9 && var10 == var11 - 1 && (this.flags[var8][var10] & 0x2) == 0) {
					return true;
				}
			} else if (arg0 == 2) {
				if (var8 == var9 - 1 && var10 == var11 && (this.flags[var8][var10] & 0x8) == 0) {
					return true;
				}
				if (var8 == var9 && var10 == var11 + 1 && (this.flags[var8][var10] & 0x20) == 0) {
					return true;
				}
			} else if (arg0 == 3) {
				if (var8 == var9 + 1 && var10 == var11 && (this.flags[var8][var10] & 0x80) == 0) {
					return true;
				}
				if (var8 == var9 && var10 == var11 + 1 && (this.flags[var8][var10] & 0x20) == 0) {
					return true;
				}
			}
		}
		if (arg1 == 8) {
			if (var8 == var9 && var10 == var11 + 1 && (this.flags[var8][var10] & 0x20) == 0) {
				return true;
			}
			if (var8 == var9 && var10 == var11 - 1 && (this.flags[var8][var10] & 0x2) == 0) {
				return true;
			}
			if (var8 == var9 - 1 && var10 == var11 && (this.flags[var8][var10] & 0x8) == 0) {
				return true;
			}
			if (var8 == var9 + 1 && var10 == var11 && (this.flags[var8][var10] & 0x80) == 0) {
				return true;
			}
		}
		return false;
	}

	@ObfuscatedName("ec.a(IIIIIIII)Z")
	public boolean testLoc(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		int var9 = arg3 + arg6 - 1;
		int var10 = arg5 + arg1 - 1;
		int var11 = 17 / arg7;
		if (arg2 >= arg3 && arg2 <= var9 && arg0 >= arg5 && arg0 <= var10) {
			return true;
		} else if (arg2 == arg3 - 1 && arg0 >= arg5 && arg0 <= var10 && (this.flags[arg2 - this.offsetX][arg0 - this.offsetZ] & 0x8) == 0 && (arg4 & 0x8) == 0) {
			return true;
		} else if (arg2 == var9 + 1 && arg0 >= arg5 && arg0 <= var10 && (this.flags[arg2 - this.offsetX][arg0 - this.offsetZ] & 0x80) == 0 && (arg4 & 0x2) == 0) {
			return true;
		} else if (arg0 == arg5 - 1 && arg2 >= arg3 && arg2 <= var9 && (this.flags[arg2 - this.offsetX][arg0 - this.offsetZ] & 0x2) == 0 && (arg4 & 0x4) == 0) {
			return true;
		} else {
			return arg0 == var10 + 1 && arg2 >= arg3 && arg2 <= var9 && (this.flags[arg2 - this.offsetX][arg0 - this.offsetZ] & 0x20) == 0 && (arg4 & 0x1) == 0;
		}
	}
}
