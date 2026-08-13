package jagex2.io;

import deob.ObfuscatedName;

@ObfuscatedName("ub")
public class Jagfile {

	@ObfuscatedName("ub.a")
	public boolean field733 = false;

	@ObfuscatedName("ub.b")
	public byte field734 = 2;

	@ObfuscatedName("ub.c")
	public int field735 = 40267;

	@ObfuscatedName("ub.d")
	public boolean field736 = false;

	@ObfuscatedName("ub.e")
	public byte[] buffer;

	@ObfuscatedName("ub.f")
	public int fileCount;

	@ObfuscatedName("ub.g")
	public int[] fileHash;

	@ObfuscatedName("ub.h")
	public int[] fileUnpackedSize;

	@ObfuscatedName("ub.i")
	public int[] filePackedSize;

	@ObfuscatedName("ub.j")
	public int[] fileOffset;

	@ObfuscatedName("ub.k")
	public boolean unpacked;

	public Jagfile(byte[] arg0, boolean arg1) {
		if (arg1) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		this.load(true, arg0);
	}

	@ObfuscatedName("ub.a(Z[B)V")
	public void load(boolean arg0, byte[] arg1) {
		Packet var3 = new Packet(363, arg1);
		int var4 = var3.g3();
		int var5 = var3.g3();
		if (var5 == var4) {
			this.buffer = arg1;
			this.unpacked = false;
		} else {
			byte[] var6 = new byte[var4];
			BZip2.read(var6, var4, arg1, var5, 6);
			this.buffer = var6;
			var3 = new Packet(363, this.buffer);
			this.unpacked = true;
		}
		this.fileCount = var3.g2();
		this.fileHash = new int[this.fileCount];
		this.fileUnpackedSize = new int[this.fileCount];
		this.filePackedSize = new int[this.fileCount];
		this.fileOffset = new int[this.fileCount];
		if (!arg0) {
			return;
		}
		int var8 = var3.pos + this.fileCount * 10;
		for (int var7 = 0; var7 < this.fileCount; var7++) {
			this.fileHash[var7] = var3.g4();
			this.fileUnpackedSize[var7] = var3.g3();
			this.filePackedSize[var7] = var3.g3();
			this.fileOffset[var7] = var8;
			var8 += this.filePackedSize[var7];
		}
	}

	@ObfuscatedName("ub.a(Ljava/lang/String;[BB)[B")
	public byte[] read(String arg0, byte[] arg1, byte arg2) {
		int var4 = 0;
		String var8 = arg0.toUpperCase();
		for (int var5 = 0; var5 < var8.length(); var5++) {
			var4 = var4 * 61 + var8.charAt(var5) - 32;
		}
		for (int var6 = 0; var6 < this.fileCount; var6++) {
			if (this.fileHash[var6] == var4) {
				if (arg1 == null) {
					arg1 = new byte[this.fileUnpackedSize[var6]];
				}
				if (this.unpacked) {
					for (int var7 = 0; var7 < this.fileUnpackedSize[var6]; var7++) {
						arg1[var7] = this.buffer[this.fileOffset[var6] + var7];
					}
				} else {
					BZip2.read(arg1, this.fileUnpackedSize[var6], this.buffer, this.filePackedSize[var6], this.fileOffset[var6]);
				}
				return arg1;
			}
		}
		if (arg2 != this.field734) {
			throw new NullPointerException();
		}
		return null;
	}
}
