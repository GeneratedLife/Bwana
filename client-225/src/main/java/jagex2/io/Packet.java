package jagex2.io;

import deob.ObfuscatedName;
import jagex2.datastruct.DoublyLinkable;
import jagex2.datastruct.LinkList;
import java.math.BigInteger;

@ObfuscatedName("kb")
public class Packet extends DoublyLinkable {

	@ObfuscatedName("kb.h")
	public static int field612 = 40946;

	@ObfuscatedName("kb.i")
	public byte field613 = -34;

	@ObfuscatedName("kb.j")
	public byte field614 = -106;

	@ObfuscatedName("kb.k")
	public int field615 = 3;

	@ObfuscatedName("kb.l")
	public int field616 = -506;

	@ObfuscatedName("kb.m")
	public boolean field617 = true;

	@ObfuscatedName("kb.n")
	public int field618 = 4277;

	@ObfuscatedName("kb.o")
	public static boolean field619 = true;

	@ObfuscatedName("kb.p")
	public int field620 = -178;

	@ObfuscatedName("kb.q")
	public byte[] data;

	@ObfuscatedName("kb.r")
	public int pos;

	@ObfuscatedName("kb.s")
	public int bitPos;

	@ObfuscatedName("kb.t")
	public static int[] crctable = new int[256];

	@ObfuscatedName("kb.u")
	public static final int[] BITMASK;

	@ObfuscatedName("kb.v")
	public Isaac random;

	@ObfuscatedName("kb.w")
	public static int cacheMinCount;

	@ObfuscatedName("kb.x")
	public static int cacheMidCount;

	@ObfuscatedName("kb.y")
	public static int cacheMaxCount;

	@ObfuscatedName("kb.z")
	public static LinkList cacheMin;

	@ObfuscatedName("kb.A")
	public static LinkList cacheMid;

	@ObfuscatedName("kb.B")
	public static LinkList cacheMax;

	@ObfuscatedName("kb.C")
	public static boolean field633;

	@ObfuscatedName("kb.a(II)Lkb;")
	public static Packet alloc(int arg0, int arg1) {
		LinkList var3 = cacheMid;
		Packet var2;
		synchronized (cacheMid) {
			Packet var5 = null;
			if (arg0 == 0 && cacheMinCount > 0) {
				cacheMinCount--;
				var5 = (Packet) cacheMin.removeHead();
			} else if (arg0 == 1 && cacheMidCount > 0) {
				cacheMidCount--;
				var5 = (Packet) cacheMid.removeHead();
			} else if (arg0 == 2 && cacheMaxCount > 0) {
				cacheMaxCount--;
				var5 = (Packet) cacheMax.removeHead();
			}
			if (var5 != null) {
				var5.pos = 0;
				var2 = var5;
				return var2;
			}
		}
		if (arg1 >= 0) {
			field619 = !field619;
		}
		var2 = new Packet(field612);
		var2.pos = 0;
		if (arg0 == 0) {
			var2.data = new byte[100];
		} else if (arg0 == 1) {
			var2.data = new byte[5000];
		} else {
			var2.data = new byte[30000];
		}
		return var2;
	}

	@ObfuscatedName("kb.a(B)V")
	public void release(byte arg0) {
		LinkList var2 = cacheMid;
		synchronized (cacheMid) {
			this.pos = 0;
			if (this.data.length == 100 && cacheMinCount < 1000) {
				cacheMin.addTail(this);
				cacheMinCount++;
				return;
			}
			if (this.data.length == 5000 && cacheMidCount < 250) {
				cacheMid.addTail(this);
				cacheMidCount++;
				return;
			}
			if (this.data.length == 30000 && cacheMaxCount < 50) {
				cacheMax.addTail(this);
				cacheMaxCount++;
				return;
			}
		}
		if (arg0 != 8) {
			field612 = -173;
		}
	}

	public Packet(int arg0) {
		if (arg0 != 40946) {
			field619 = !field619;
		}
	}

	public Packet(int arg0, byte[] arg1) {
		this.data = arg1;
		this.pos = 0;
		int var3 = 15 / arg0;
	}

	@ObfuscatedName("kb.a(BI)V")
	public void p1isaac(byte arg0, int arg1) {
		this.data[this.pos++] = (byte) (arg1 + this.random.takeNextValue());
		if (arg0 != this.field613) {
			field619 = !field619;
		}
	}

	@ObfuscatedName("kb.a(I)V")
	public void p1(int arg0) {
		this.data[this.pos++] = (byte) arg0;
	}

	@ObfuscatedName("kb.b(I)V")
	public void p2(int arg0) {
		this.data[this.pos++] = (byte) (arg0 >> 8);
		this.data[this.pos++] = (byte) arg0;
	}

	@ObfuscatedName("kb.a(ZI)V")
	public void ip2(boolean arg0, int arg1) {
		if (!arg0) {
			field619 = !field619;
		}
		this.data[this.pos++] = (byte) arg1;
		this.data[this.pos++] = (byte) (arg1 >> 8);
	}

	@ObfuscatedName("kb.c(I)V")
	public void p3(int arg0) {
		this.data[this.pos++] = (byte) (arg0 >> 16);
		this.data[this.pos++] = (byte) (arg0 >> 8);
		this.data[this.pos++] = (byte) arg0;
	}

	@ObfuscatedName("kb.d(I)V")
	public void p4(int arg0) {
		this.data[this.pos++] = (byte) (arg0 >> 24);
		this.data[this.pos++] = (byte) (arg0 >> 16);
		this.data[this.pos++] = (byte) (arg0 >> 8);
		this.data[this.pos++] = (byte) arg0;
	}

	@ObfuscatedName("kb.b(ZI)V")
	public void ip4(boolean arg0, int arg1) {
		this.data[this.pos++] = (byte) arg1;
		this.data[this.pos++] = (byte) (arg1 >> 8);
		this.data[this.pos++] = (byte) (arg1 >> 16);
		if (!arg0) {
			this.data[this.pos++] = (byte) (arg1 >> 24);
		}
	}

	@ObfuscatedName("kb.a(ZJ)V")
	public void p8(boolean arg0, long arg1) {
		this.data[this.pos++] = (byte) (arg1 >> 56);
		this.data[this.pos++] = (byte) (arg1 >> 48);
		this.data[this.pos++] = (byte) (arg1 >> 40);
		this.data[this.pos++] = (byte) (arg1 >> 32);
		this.data[this.pos++] = (byte) (arg1 >> 24);
		this.data[this.pos++] = (byte) (arg1 >> 16);
		this.data[this.pos++] = (byte) (arg1 >> 8);
		if (arg0) {
			this.data[this.pos++] = (byte) arg1;
		}
	}

	@ObfuscatedName("kb.a(Ljava/lang/String;)V")
	public void pjstr(String arg0) {
		arg0.getBytes(0, arg0.length(), this.data, this.pos);
		this.pos += arg0.length();
		this.data[this.pos++] = 10;
	}

	@ObfuscatedName("kb.a([BIIB)V")
	public void pdata(byte[] arg0, int arg1, int arg2, byte arg3) {
		int var5;
		if (arg3 != this.field614) {
			for (var5 = 1; var5 > 0; var5++) {
			}
		}
		for (var5 = arg2; var5 < arg2 + arg1; var5++) {
			this.data[this.pos++] = arg0[var5];
		}
	}

	@ObfuscatedName("kb.b(II)V")
	public void psize1(int arg0, int arg1) {
		if (arg0 != 0) {
			field619 = !field619;
		}
		this.data[this.pos - arg1 - 1] = (byte) arg1;
	}

	@ObfuscatedName("kb.c()I")
	public int g1() {
		return this.data[this.pos++] & 0xFF;
	}

	@ObfuscatedName("kb.d()B")
	public byte g1b() {
		return this.data[this.pos++];
	}

	@ObfuscatedName("kb.e()I")
	public int g2() {
		this.pos += 2;
		return ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

	@ObfuscatedName("kb.f()I")
	public int g2b() {
		this.pos += 2;
		int var1 = ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
		if (var1 > 32767) {
			var1 -= 65536;
		}
		return var1;
	}

	@ObfuscatedName("kb.g()I")
	public int g3() {
		this.pos += 3;
		return ((this.data[this.pos - 3] & 0xFF) << 16) + ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

	@ObfuscatedName("kb.h()I")
	public int g4() {
		this.pos += 4;
		return ((this.data[this.pos - 4] & 0xFF) << 24) + ((this.data[this.pos - 3] & 0xFF) << 16) + ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

	@ObfuscatedName("kb.e(I)J")
	public long g8(int arg0) {
		long var2 = (long) this.g4() & 0xFFFFFFFFL;
		int var6 = 87 / arg0;
		long var4 = (long) this.g4() & 0xFFFFFFFFL;
		return (var2 << 32) + var4;
	}

	@ObfuscatedName("kb.i()Ljava/lang/String;")
	public String gjstr() {
		int var1 = this.pos;
		while (this.data[this.pos++] != 10) {
		}
		return new String(this.data, var1, this.pos - var1 - 1);
	}

	@ObfuscatedName("kb.b(B)[B")
	public byte[] gstrbyte(byte arg0) {
		int var2 = this.pos;
		if (arg0 != 31) {
			throw new NullPointerException();
		}
		while (this.data[this.pos++] != 10) {
		}
		byte[] var3 = new byte[this.pos - var2 - 1];
		for (int var4 = var2; var4 < this.pos - 1; var4++) {
			var3[var4 - var2] = this.data[var4];
		}
		return var3;
	}

	@ObfuscatedName("kb.a(III[B)V")
	public void gdata(int arg0, int arg1, int arg2, byte[] arg3) {
		if (arg1 >= 0) {
			this.field616 = -432;
		}
		for (int var5 = arg2; var5 < arg2 + arg0; var5++) {
			arg3[var5] = this.data[this.pos++];
		}
	}

	@ObfuscatedName("kb.f(I)V")
	public void accessBits(int arg0) {
		this.bitPos = this.pos * 8;
		if (arg0 <= 0) {
			this.field617 = !this.field617;
		}
	}

	@ObfuscatedName("kb.c(II)I")
	public int gBit(int arg0, int arg1) {
		if (arg0 < 9 || arg0 > 9) {
			return this.field615;
		}
		int var3 = this.bitPos >> 3;
		int var4 = 8 - (this.bitPos & 0x7);
		int var5 = 0;
		this.bitPos += arg1;
		while (arg1 > var4) {
			var5 += (this.data[var3++] & BITMASK[var4]) << arg1 - var4;
			arg1 -= var4;
			var4 = 8;
		}
		if (arg1 == var4) {
			var5 += this.data[var3] & BITMASK[var4];
		} else {
			var5 += this.data[var3] >> var4 - arg1 & BITMASK[arg1];
		}
		return var5;
	}

	@ObfuscatedName("kb.g(I)V")
	public void accessBytes(int arg0) {
		if (arg0 != this.field618) {
			for (int var2 = 1; var2 > 0; var2++) {
			}
		}
		this.pos = (this.bitPos + 7) / 8;
	}

	@ObfuscatedName("kb.j()I")
	public int gsmart() {
		int var1 = this.data[this.pos] & 0xFF;
		return var1 < 128 ? this.g1() - 64 : this.g2() - 49152;
	}

	@ObfuscatedName("kb.k()I")
	public int gsmarts() {
		int var1 = this.data[this.pos] & 0xFF;
		return var1 < 128 ? this.g1() : this.g2() - 32768;
	}

	@ObfuscatedName("kb.a(Ljava/math/BigInteger;Ljava/math/BigInteger;I)V")
	public void rsaenc(BigInteger arg0, BigInteger arg1, int arg2) {
		int var4 = this.pos;
		this.pos = 0;
		byte[] var5 = new byte[var4];
		this.gdata(var4, -110, 0, var5);
		BigInteger var6 = new BigInteger(var5);
		BigInteger var7 = var6.modPow(arg1, arg0);
		byte[] var8 = var7.toByteArray();
		this.pos = 0;
		if (arg2 == 24676) {
			this.p1(var8.length);
			this.pdata(var8, var8.length, 0, (byte) -106);
		}
	}

	static {
		for (int var1 = 0; var1 < 256; var1++) {
			int var0 = var1;
			for (int var2 = 0; var2 < 8; var2++) {
				if ((var0 & 0x1) == 1) {
					var0 = var0 >>> 1 ^ 0xEDB88320;
				} else {
					var0 >>>= 0x1;
				}
			}
			crctable[var1] = var0;
		}
		BITMASK = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1 };
		cacheMin = new LinkList(0);
		cacheMid = new LinkList(0);
		cacheMax = new LinkList(0);
	}
}
