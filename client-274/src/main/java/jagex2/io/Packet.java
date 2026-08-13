package jagex2.io;

import deob.ObfuscatedName;
import jagex2.datastruct.LinkList;
import jagex2.datastruct.Linkable2;

import java.math.BigInteger;

@ObfuscatedName("lb")
public final class Packet extends Linkable2 {

	@ObfuscatedName("lb.t")
	public byte[] pos;

	@ObfuscatedName("lb.u")
	public int data;

	@ObfuscatedName("lb.v")
	public int bitPos;

	@ObfuscatedName("lb.w")
	public static final int[] crctable = new int[256];

	@ObfuscatedName("lb.x")
	public static final int[] bitmask;

	@ObfuscatedName("lb.y")
	public Isaac random;

	@ObfuscatedName("lb.z")
	public static int cacheMinCount;

	@ObfuscatedName("lb.C")
	public static final LinkList cacheMin;

	@ObfuscatedName("lb.D")
	public static final LinkList cacheMid;

	@ObfuscatedName("lb.E")
	public static final LinkList cacheMax;

	@ObfuscatedName("lb.F")
	public final char[] field720 = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	@ObfuscatedName("lb.A")
	public static int cacheMidCount;

	@ObfuscatedName("lb.B")
	public static int cacheMaxCount;

	static {
		for (int var0 = 0; var0 < 256; var0++) {
			int var1 = var0;
			for (int var2 = 0; var2 < 8; var2++) {
				if ((var1 & 0x1) == 1) {
					var1 = var1 >>> 1 ^ 0xEDB88320;
				} else {
					var1 >>>= 0x1;
				}
			}
			crctable[var0] = var1;
		}
		bitmask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1 };
		cacheMin = new LinkList();
		cacheMid = new LinkList();
		cacheMax = new LinkList();
	}

	@ObfuscatedName("lb.a(II)Llb;")
	public static Packet alloc(int arg1) {
		LinkList var2 = cacheMid;
		synchronized (cacheMid) {
			Packet var3 = null;
			if (arg1 == 0 && cacheMinCount > 0) {
				cacheMinCount--;
				var3 = (Packet) cacheMin.popFront();
			} else if (arg1 == 1 && cacheMidCount > 0) {
				cacheMidCount--;
				var3 = (Packet) cacheMid.popFront();
			} else if (arg1 == 2 && cacheMaxCount > 0) {
				cacheMaxCount--;
				var3 = (Packet) cacheMax.popFront();
			}
			if (var3 != null) {
				var3.data = 0;
				return var3;
			}
		}
		Packet var4 = new Packet();
		var4.data = 0;
		if (arg1 == 0) {
			var4.pos = new byte[100];
		} else if (arg1 == 1) {
			var4.pos = new byte[5000];
		} else {
			var4.pos = new byte[30000];
		}
		return var4;
	}

	public Packet() {
	}

	public Packet(byte[] arg0) {
		pos = arg0;
		data = 0;
	}

	@ObfuscatedName("lb.a(BI)V")
	public void p1Enc(int arg1) {
		pos[data++] = (byte) (arg1 + random.takeNextValue());
	}

	@ObfuscatedName("lb.a(I)V")
	public void p1(int arg0) {
		pos[data++] = (byte) arg0;
	}

	@ObfuscatedName("lb.b(I)V")
	public void p2(int arg0) {
		pos[data++] = (byte) (arg0 >> 8);
		pos[data++] = (byte) arg0;
	}

	@ObfuscatedName("lb.a(ZI)V")
	public void ip2(int arg1) {
		pos[data++] = (byte) arg1;
		pos[data++] = (byte) (arg1 >> 8);
	}

	@ObfuscatedName("lb.c(I)V")
	public void p3(int arg0) {
		pos[data++] = (byte) (arg0 >> 16);
		pos[data++] = (byte) (arg0 >> 8);
		pos[data++] = (byte) arg0;
	}

	@ObfuscatedName("lb.d(I)V")
	public void p4(int arg0) {
		pos[data++] = (byte) (arg0 >> 24);
		pos[data++] = (byte) (arg0 >> 16);
		pos[data++] = (byte) (arg0 >> 8);
		pos[data++] = (byte) arg0;
	}

	@ObfuscatedName("lb.b(ZI)V")
	public void ip4(int arg1) {
		pos[data++] = (byte) arg1;
		pos[data++] = (byte) (arg1 >> 8);
		pos[data++] = (byte) (arg1 >> 16);
		pos[data++] = (byte) (arg1 >> 24);
	}

	@ObfuscatedName("lb.a(JI)V")
	public void p8(long arg0) {
		pos[data++] = (byte) (arg0 >> 56);
		pos[data++] = (byte) (arg0 >> 48);
		pos[data++] = (byte) (arg0 >> 40);
		pos[data++] = (byte) (arg0 >> 32);
		pos[data++] = (byte) (arg0 >> 24);
		pos[data++] = (byte) (arg0 >> 16);
		pos[data++] = (byte) (arg0 >> 8);
		pos[data++] = (byte) arg0;
	}

	@ObfuscatedName("lb.a(Ljava/lang/String;)V")
	public void pjstr(String arg0) {
		arg0.getBytes(0, arg0.length(), pos, data);
		data += arg0.length();
		pos[data++] = 10;
	}

	@ObfuscatedName("lb.a([BZII)V")
	public void pdata(byte[] arg0, int arg2, int arg3) {
		for (int var5 = arg2; var5 < arg2 + arg3; var5++) {
			pos[data++] = arg0[var5];
		}
	}

	@ObfuscatedName("lb.b(II)V")
	public void psize1(int arg0) {
		pos[data - arg0 - 1] = (byte) arg0;
	}

	@ObfuscatedName("lb.c()I")
	public int g1() {
		return pos[data++] & 0xFF;
	}

	@ObfuscatedName("lb.d()B")
	public byte g1b() {
		return pos[data++];
	}

	@ObfuscatedName("lb.e()I")
	public int g2() {
		data += 2;
		return ((pos[data - 2] & 0xFF) << 8) + (pos[data - 1] & 0xFF);
	}

	@ObfuscatedName("lb.f()I")
	public int g2b() {
		data += 2;
		int var1 = ((pos[data - 2] & 0xFF) << 8) + (pos[data - 1] & 0xFF);
		if (var1 > 32767) {
			var1 -= 65536;
		}
		return var1;
	}

	@ObfuscatedName("lb.g()I")
	public int g3() {
		data += 3;
		return ((pos[data - 3] & 0xFF) << 16) + ((pos[data - 2] & 0xFF) << 8) + (pos[data - 1] & 0xFF);
	}

	@ObfuscatedName("lb.h()I")
	public int g4() {
		data += 4;
		return ((pos[data - 4] & 0xFF) << 24) + ((pos[data - 3] & 0xFF) << 16) + ((pos[data - 2] & 0xFF) << 8) + (pos[data - 1] & 0xFF);
	}

	@ObfuscatedName("lb.e(I)J")
	public long g8() {
		long var2 = (long) g4() & 0xFFFFFFFFL;
		long var4 = (long) g4() & 0xFFFFFFFFL;
		return (var2 << 32) + var4;
	}

	@ObfuscatedName("lb.i()Ljava/lang/String;")
	public String gjstr() {
		int var1 = data;
		while (pos[data++] != 10) {
		}
		return new String(pos, var1, data - var1 - 1);
	}

	@ObfuscatedName("lb.f(I)[B")
	public byte[] gjstrraw() {
		int var2 = data;
		while (pos[data++] != 10) {
		}
		byte[] var3 = new byte[data - var2 - 1];
		for (int var4 = var2; var4 < data - 1; var4++) {
			var3[var4 - var2] = pos[var4];
		}
		return var3;
	}

	@ObfuscatedName("lb.a(I[BII)V")
	public void gdata(int arg0, byte[] arg1, int arg2) {
		for (int var6 = arg2; var6 < arg2 + arg0; var6++) {
			arg1[var6] = pos[data++];
		}
	}

	@ObfuscatedName("lb.a(B)V")
	public void gBitStart() {
		bitPos = data * 8;
	}

	@ObfuscatedName("lb.a(IB)I")
	public int gBit(int arg0) {
		int var3 = bitPos >> 3;
		int var4 = 8 - (bitPos & 0x7);
		int var5 = 0;
		bitPos += arg0;
		while (arg0 > var4) {
			var5 += (pos[var3++] & bitmask[var4]) << arg0 - var4;
			arg0 -= var4;
			var4 = 8;
		}
		int var6;
		if (arg0 == var4) {
			var6 = var5 + (pos[var3] & bitmask[var4]);
		} else {
			var6 = var5 + (pos[var3] >> var4 - arg0 & bitmask[arg0]);
		}
		return var6;
	}

	@ObfuscatedName("lb.g(I)V")
	public void gBitEnd() {
		data = (bitPos + 7) / 8;
	}

	@ObfuscatedName("lb.j()I")
	public int gsmarts() {
		int var1 = pos[data] & 0xFF;
		return var1 < 128 ? g1() - 64 : g2() - 49152;
	}

	@ObfuscatedName("lb.k()I")
	public int gsmart() {
		int var1 = pos[data] & 0xFF;
		return var1 < 128 ? g1() : g2() - 32768;
	}

	@ObfuscatedName("lb.a(Ljava/math/BigInteger;Ljava/math/BigInteger;I)V")
	public void rsaenc(BigInteger arg0, BigInteger arg1) {
		int var4 = data;
		data = 0;
		byte[] var5 = new byte[var4];
		gdata(var4, var5, 0);
		BigInteger var6 = new BigInteger(var5);
		BigInteger var7 = var6.modPow(arg1, arg0);
		byte[] var8 = var7.toByteArray();
		data = 0;
		p1(var8.length);
		pdata(var8, 0, var8.length);
	}
}
