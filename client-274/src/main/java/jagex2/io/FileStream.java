package jagex2.io;

import deob.ObfuscatedName;

import java.io.IOException;
import java.io.RandomAccessFile;

@ObfuscatedName("vb")
public final class FileStream {

	@ObfuscatedName("vb.d")
	public static final byte[] temp = new byte[520];

	@ObfuscatedName("vb.e")
	public final RandomAccessFile dat;

	@ObfuscatedName("vb.f")
	public final RandomAccessFile idx;

	@ObfuscatedName("vb.g")
	public final int archive;

	@ObfuscatedName("vb.h")
	public int maxFileSize = 65000;

	public FileStream(RandomAccessFile arg0, int arg1, RandomAccessFile arg2, int arg3, int arg4) {
		archive = arg3;
		dat = arg0;
		idx = arg2;
		maxFileSize = arg4;
	}

	@ObfuscatedName("vb.a(BI)[B")
	public synchronized byte[] readFromFile(int arg1) {
		try {
			seek(arg1 * 6, idx);
			int var4;
			for (int var3 = 0; var3 < 6; var3 += var4) {
				var4 = idx.read(temp, var3, 6 - var3);
				if (var4 == -1) {
					return null;
				}
			}
			int var5 = ((temp[0] & 0xFF) << 16) + ((temp[1] & 0xFF) << 8) + (temp[2] & 0xFF);
			int var6 = ((temp[3] & 0xFF) << 16) + ((temp[4] & 0xFF) << 8) + (temp[5] & 0xFF);
			if (var5 < 0 || var5 > maxFileSize) {
				return null;
			} else if (var6 > 0 && (long) var6 <= dat.length() / 520L) {
				byte[] var7 = new byte[var5];
				int var8 = 0;
				int var9 = 0;
				while (var8 < var5) {
					if (var6 == 0) {
						return null;
					}
					seek(var6 * 520, dat);
					int var10 = 0;
					int var11 = var5 - var8;
					if (var11 > 512) {
						var11 = 512;
					}
					while (var10 < var11 + 8) {
						int var12 = dat.read(temp, var10, var11 + 8 - var10);
						if (var12 == -1) {
							return null;
						}
						var10 += var12;
					}
					int var13 = ((temp[0] & 0xFF) << 8) + (temp[1] & 0xFF);
					int var14 = ((temp[2] & 0xFF) << 8) + (temp[3] & 0xFF);
					int var15 = ((temp[4] & 0xFF) << 16) + ((temp[5] & 0xFF) << 8) + (temp[6] & 0xFF);
					int var16 = temp[7] & 0xFF;
					if (var13 == arg1 && var14 == var9 && var16 == archive) {
						if (var15 >= 0 && (long) var15 <= dat.length() / 520L) {
							for (int var17 = 0; var17 < var11; var17++) {
								var7[var8++] = temp[var17 + 8];
							}
							var6 = var15;
							var9++;
							continue;
						}
						return null;
					}
					return null;
				}
				return var7;
			} else {
				return null;
			}
		} catch (IOException var18) {
			return null;
		}
	}

	@ObfuscatedName("vb.a(II[BB)Z")
	public synchronized boolean writeToFile(int arg0, int arg1, byte[] arg2) {
		boolean var5 = writeToFile(true, arg2, arg1, arg0);
		if (!var5) {
			var5 = writeToFile(false, arg2, arg1, arg0);
		}
		return var5;
	}

	@ObfuscatedName("vb.a(Z[BIIB)Z")
	public synchronized boolean writeToFile(boolean arg0, byte[] arg1, int arg2, int arg3) {
		try {
			int var8;
			if (arg0) {
				seek(arg2 * 6, idx);
				int var7;
				for (int var6 = 0; var6 < 6; var6 += var7) {
					var7 = idx.read(temp, var6, 6 - var6);
					if (var7 == -1) {
						return false;
					}
				}
				var8 = ((temp[3] & 0xFF) << 16) + ((temp[4] & 0xFF) << 8) + (temp[5] & 0xFF);
				if (var8 <= 0 || (long) var8 > dat.length() / 520L) {
					return false;
				}
			} else {
				var8 = (int) ((dat.length() + 519L) / 520L);
				if (var8 == 0) {
					var8 = 1;
				}
			}
			temp[0] = (byte) (arg3 >> 16);
			temp[1] = (byte) (arg3 >> 8);
			temp[2] = (byte) arg3;
			temp[3] = (byte) (var8 >> 16);
			temp[4] = (byte) (var8 >> 8);
			temp[5] = (byte) var8;
			seek(arg2 * 6, idx);
			idx.write(temp, 0, 6);
			int var9 = 0;
			int var10 = 0;
			while (var9 < arg3) {
				int var11 = 0;
				if (arg0) {
					seek(var8 * 520, dat);
					int var12;
					int var13;
					for (var12 = 0; var12 < 8; var12 += var13) {
						var13 = dat.read(temp, var12, 8 - var12);
						if (var13 == -1) {
							break;
						}
					}
					if (var12 == 8) {
						label110: {
							int var14 = ((temp[0] & 0xFF) << 8) + (temp[1] & 0xFF);
							int var15 = ((temp[2] & 0xFF) << 8) + (temp[3] & 0xFF);
							var11 = ((temp[4] & 0xFF) << 16) + ((temp[5] & 0xFF) << 8) + (temp[6] & 0xFF);
							int var16 = temp[7] & 0xFF;
							if (var14 == arg2 && var15 == var10 && var16 == archive) {
								if (var11 >= 0 && (long) var11 <= dat.length() / 520L) {
									break label110;
								}
								return false;
							}
							return false;
						}
					}
				}
				if (var11 == 0) {
					arg0 = false;
					var11 = (int) ((dat.length() + 519L) / 520L);
					if (var11 == 0) {
						var11++;
					}
					if (var11 == var8) {
						var11++;
					}
				}
				if (arg3 - var9 <= 512) {
					var11 = 0;
				}
				temp[0] = (byte) (arg2 >> 8);
				temp[1] = (byte) arg2;
				temp[2] = (byte) (var10 >> 8);
				temp[3] = (byte) var10;
				temp[4] = (byte) (var11 >> 16);
				temp[5] = (byte) (var11 >> 8);
				temp[6] = (byte) var11;
				temp[7] = (byte) archive;
				seek(var8 * 520, dat);
				dat.write(temp, 0, 8);
				int var17 = arg3 - var9;
				if (var17 > 512) {
					var17 = 512;
				}
				dat.write(arg1, var9, var17);
				var9 += var17;
				var8 = var11;
				var10++;
			}
			return true;
		} catch (IOException var18) {
			return false;
		}
	}

	@ObfuscatedName("vb.a(IZLjava/io/RandomAccessFile;)V")
	public synchronized void seek(int arg0, RandomAccessFile arg2) throws IOException {
		if (arg0 < 0 || arg0 > 62914560) {
			System.out.println("Badseek - pos:" + arg0 + " len:" + arg2.length());
			arg0 = 62914560;
			try {
				Thread.sleep(1000L);
			} catch (Exception var4) {
			}
		}
		arg2.seek((long) arg0);
	}
}
