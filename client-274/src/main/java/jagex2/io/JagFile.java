package jagex2.io;

import deob.ObfuscatedName;

@ObfuscatedName("xb")
public final class JagFile {

	@ObfuscatedName("xb.d")
	public byte[] data;

	@ObfuscatedName("xb.e")
	public int fileCount;

	@ObfuscatedName("xb.f")
	public int[] fileHash;

	@ObfuscatedName("xb.g")
	public int[] fileUnpackedSize;

	@ObfuscatedName("xb.h")
	public int[] filePackedSize;

	@ObfuscatedName("xb.i")
	public int[] fileOffset;

	@ObfuscatedName("xb.j")
	public boolean unpacked;

	public JagFile(byte[] arg1) {
		unpack(arg1);
	}

	@ObfuscatedName("xb.a(I[B)V")
	public void unpack(byte[] arg1) {
		Packet var3 = new Packet(arg1);
		int var4 = var3.g3();
		int var5 = var3.g3();
		if (var5 == var4) {
			data = arg1;
			unpacked = false;
		} else {
			byte[] var6 = new byte[var4];
			BZip2.decompress(var6, var4, arg1, var5, 6);
			data = var6;
			var3 = new Packet(data);
			unpacked = true;
		}
		fileCount = var3.g2();
		fileHash = new int[fileCount];
		fileUnpackedSize = new int[fileCount];
		filePackedSize = new int[fileCount];
		fileOffset = new int[fileCount];
		int var7 = var3.data + fileCount * 10;
		for (int var8 = 0; var8 < fileCount; var8++) {
			fileHash[var8] = var3.g4();
			fileUnpackedSize[var8] = var3.g3();
			filePackedSize[var8] = var3.g3();
			fileOffset[var8] = var7;
			var7 += filePackedSize[var8];
		}
	}

	@ObfuscatedName("xb.a(Ljava/lang/String;[B)[B")
	public byte[] read(String arg0, byte[] arg1) {
		int var3 = 0;
		String var4 = arg0.toUpperCase();
		for (int var5 = 0; var5 < var4.length(); var5++) {
			var3 = var3 * 61 + var4.charAt(var5) - 32;
		}
		for (int var6 = 0; var6 < fileCount; var6++) {
			if (fileHash[var6] == var3) {
				if (arg1 == null) {
					arg1 = new byte[fileUnpackedSize[var6]];
				}
				if (unpacked) {
					for (int var7 = 0; var7 < fileUnpackedSize[var6]; var7++) {
						arg1[var7] = data[fileOffset[var6] + var7];
					}
				} else {
					BZip2.decompress(arg1, fileUnpackedSize[var6], data, filePackedSize[var6], fileOffset[var6]);
				}
				return arg1;
			}
		}
		return null;
	}
}
