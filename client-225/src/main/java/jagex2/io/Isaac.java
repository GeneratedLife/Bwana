package jagex2.io;

import deob.ObfuscatedName;

@ObfuscatedName("tb")
public class Isaac {

	@ObfuscatedName("tb.a")
	public int count;

	@ObfuscatedName("tb.b")
	public int[] rsl = new int[256];

	@ObfuscatedName("tb.c")
	public int[] mem = new int[256];

	@ObfuscatedName("tb.d")
	public int a;

	@ObfuscatedName("tb.e")
	public int b;

	@ObfuscatedName("tb.f")
	public int c;

	public Isaac(byte arg0, int[] arg1) {
		for (int var3 = 0; var3 < arg1.length; var3++) {
			this.rsl[var3] = arg1[var3];
		}
		if (arg0 != 1) {
			throw new NullPointerException();
		}
		boolean var4 = false;
		this.init();
	}

	@ObfuscatedName("tb.a()I")
	public final int takeNextValue() {
		if (this.count-- == 0) {
			this.generate();
			this.count = 255;
		}
		return this.rsl[this.count];
	}

	@ObfuscatedName("tb.b()V")
	public final void generate() {
		this.b += ++this.c;
		for (int var1 = 0; var1 < 256; var1++) {
			int var2 = this.mem[var1];
			switch(var1 & 0x3) {
				case 0:
					this.a ^= this.a << 13;
					break;
				case 1:
					this.a ^= this.a >>> 6;
					break;
				case 2:
					this.a ^= this.a << 2;
					break;
				case 3:
					this.a ^= this.a >>> 16;
			}
			this.a += this.mem[var1 + 128 & 0xFF];
			int var3;
			this.mem[var1] = var3 = this.mem[var2 >> 2 & 0xFF] + this.a + this.b;
			this.rsl[var1] = this.b = this.mem[var3 >> 8 >> 2 & 0xFF] + var2;
		}
	}

	@ObfuscatedName("tb.c()V")
	public final void init() {
		int var9 = -1640531527;
		int var8 = -1640531527;
		int var7 = -1640531527;
		int var6 = -1640531527;
		int var5 = -1640531527;
		int var4 = -1640531527;
		int var3 = -1640531527;
		int var2 = -1640531527;
		int var1;
		for (var1 = 0; var1 < 4; var1++) {
			var2 ^= var3 << 11;
			var5 += var2;
			var3 += var4;
			var3 ^= var4 >>> 2;
			var6 += var3;
			var4 += var5;
			var4 ^= var5 << 8;
			var7 += var4;
			var5 += var6;
			var5 ^= var6 >>> 16;
			var8 += var5;
			var6 += var7;
			var6 ^= var7 << 10;
			var9 += var6;
			var7 += var8;
			var7 ^= var8 >>> 4;
			var2 += var7;
			var8 += var9;
			var8 ^= var9 << 8;
			var3 += var8;
			var9 += var2;
			var9 ^= var2 >>> 9;
			var4 += var9;
			var2 += var3;
		}
		for (var1 = 0; var1 < 256; var1 += 8) {
			var2 += this.rsl[var1];
			var3 += this.rsl[var1 + 1];
			var4 += this.rsl[var1 + 2];
			var5 += this.rsl[var1 + 3];
			var6 += this.rsl[var1 + 4];
			var7 += this.rsl[var1 + 5];
			var8 += this.rsl[var1 + 6];
			var9 += this.rsl[var1 + 7];
			var2 ^= var3 << 11;
			var5 += var2;
			var3 += var4;
			var3 ^= var4 >>> 2;
			var6 += var3;
			var4 += var5;
			var4 ^= var5 << 8;
			var7 += var4;
			var5 += var6;
			var5 ^= var6 >>> 16;
			var8 += var5;
			var6 += var7;
			var6 ^= var7 << 10;
			var9 += var6;
			var7 += var8;
			var7 ^= var8 >>> 4;
			var2 += var7;
			var8 += var9;
			var8 ^= var9 << 8;
			var3 += var8;
			var9 += var2;
			var9 ^= var2 >>> 9;
			var4 += var9;
			var2 += var3;
			this.mem[var1] = var2;
			this.mem[var1 + 1] = var3;
			this.mem[var1 + 2] = var4;
			this.mem[var1 + 3] = var5;
			this.mem[var1 + 4] = var6;
			this.mem[var1 + 5] = var7;
			this.mem[var1 + 6] = var8;
			this.mem[var1 + 7] = var9;
		}
		for (var1 = 0; var1 < 256; var1 += 8) {
			var2 += this.mem[var1];
			var3 += this.mem[var1 + 1];
			var4 += this.mem[var1 + 2];
			var5 += this.mem[var1 + 3];
			var6 += this.mem[var1 + 4];
			var7 += this.mem[var1 + 5];
			var8 += this.mem[var1 + 6];
			var9 += this.mem[var1 + 7];
			var2 ^= var3 << 11;
			var5 += var2;
			var3 += var4;
			var3 ^= var4 >>> 2;
			var6 += var3;
			var4 += var5;
			var4 ^= var5 << 8;
			var7 += var4;
			var5 += var6;
			var5 ^= var6 >>> 16;
			var8 += var5;
			var6 += var7;
			var6 ^= var7 << 10;
			var9 += var6;
			var7 += var8;
			var7 ^= var8 >>> 4;
			var2 += var7;
			var8 += var9;
			var8 ^= var9 << 8;
			var3 += var8;
			var9 += var2;
			var9 ^= var2 >>> 9;
			var4 += var9;
			var2 += var3;
			this.mem[var1] = var2;
			this.mem[var1 + 1] = var3;
			this.mem[var1 + 2] = var4;
			this.mem[var1 + 3] = var5;
			this.mem[var1 + 4] = var6;
			this.mem[var1 + 5] = var7;
			this.mem[var1 + 6] = var8;
			this.mem[var1 + 7] = var9;
		}
		this.generate();
		this.count = 256;
	}
}
