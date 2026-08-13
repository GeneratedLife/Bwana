package jagex2.client;

import deob.ObfuscatedName;
import jagex2.io.Packet;

@ObfuscatedName("e")
public class InputTracking {

	@ObfuscatedName("e.a")
	public static byte field72 = 65;

	@ObfuscatedName("e.b")
	public static int field73 = 78;

	@ObfuscatedName("e.c")
	public static boolean field74;

	@ObfuscatedName("e.d")
	public static boolean field75;

	@ObfuscatedName("e.e")
	public static boolean enabled;

	@ObfuscatedName("e.f")
	public static Packet outBuffer = null;

	@ObfuscatedName("e.g")
	public static Packet oldBuffer = null;

	@ObfuscatedName("e.h")
	public static long lastTime;

	@ObfuscatedName("e.i")
	public static int trackedCount;

	@ObfuscatedName("e.j")
	public static long lastMoveTime;

	@ObfuscatedName("e.k")
	public static int lastX;

	@ObfuscatedName("e.l")
	public static int lastY;

	@ObfuscatedName("e.a(I)V")
	public static final synchronized void setEnabled(int arg0) {
		outBuffer = Packet.alloc(1, -737);
		oldBuffer = null;
		lastTime = System.currentTimeMillis();
		if (arg0 != -31717) {
			field75 = !field75;
		}
		enabled = true;
	}

	@ObfuscatedName("e.a(B)V")
	public static final synchronized void setDisabled(byte arg0) {
		enabled = false;
		outBuffer = null;
		if (arg0 == field72) {
			oldBuffer = null;
		}
	}

	@ObfuscatedName("e.b(I)Lkb;")
	public static final synchronized Packet flush(int arg0) {
		Packet var1 = null;
		if (oldBuffer != null && enabled) {
			var1 = oldBuffer;
		}
		oldBuffer = null;
		if (arg0 >= 0) {
			field74 = !field74;
		}
		return var1;
	}

	@ObfuscatedName("e.c(I)Lkb;")
	public static final synchronized Packet stop(int arg0) {
		if (arg0 <= 0) {
			field75 = !field75;
		}
		Packet var1 = null;
		if (outBuffer != null && outBuffer.pos > 0 && enabled) {
			var1 = outBuffer;
		}
		setDisabled((byte) 65);
		return var1;
	}

	@ObfuscatedName("e.a(II)V")
	public static final synchronized void ensureCapacity(int arg0, int arg1) {
		if (arg0 <= 0) {
			field74 = !field74;
		}
		if (outBuffer.pos + arg1 >= 500) {
			Packet var2 = outBuffer;
			outBuffer = Packet.alloc(1, -737);
			oldBuffer = var2;
		}
	}

	@ObfuscatedName("e.a(IIIB)V")
	public static final synchronized void mousePressed(int arg0, int arg1, int arg2, byte arg3) {
		if (!enabled || (arg0 < 0 || arg0 >= 789 || arg2 < 0 || arg2 >= 532)) {
			return;
		}
		trackedCount++;
		long var4 = System.currentTimeMillis();
		long var6 = (var4 - lastTime) / 10L;
		if (var6 > 250L) {
			var6 = 250L;
		}
		lastTime = var4;
		ensureCapacity(field73, 5);
		if (arg3 != 4) {
			return;
		}
		if (arg1 == 1) {
			outBuffer.p1(1);
		} else {
			outBuffer.p1(2);
		}
		outBuffer.p1((int) var6);
		outBuffer.p3(arg0 + (arg2 << 10));
	}

	@ObfuscatedName("e.b(II)V")
	public static final synchronized void mouseReleased(int arg0, int arg1) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		long var2 = System.currentTimeMillis();
		long var4 = (var2 - lastTime) / 10L;
		if (var4 > 250L) {
			var4 = 250L;
		}
		lastTime = var2;
		if (arg1 != 0) {
			return;
		}
		ensureCapacity(field73, 2);
		if (arg0 == 1) {
			outBuffer.p1(3);
		} else {
			outBuffer.p1(4);
		}
		outBuffer.p1((int) var4);
	}

	@ObfuscatedName("e.a(IZI)V")
	public static final synchronized void mouseMoved(int arg0, boolean arg1, int arg2) {
		if (!enabled || (arg2 < 0 || arg2 >= 789 || arg0 < 0 || arg0 >= 532)) {
			return;
		}
		long var3 = System.currentTimeMillis();
		if (!arg1) {
			field73 = 445;
		}
		if (var3 - lastMoveTime < 50L) {
			return;
		}
		lastMoveTime = var3;
		trackedCount++;
		long var5 = (var3 - lastTime) / 10L;
		if (var5 > 250L) {
			var5 = 250L;
		}
		lastTime = var3;
		if (arg2 - lastX < 8 && arg2 - lastX >= -8 && arg0 - lastY < 8 && arg0 - lastY >= -8) {
			ensureCapacity(field73, 3);
			outBuffer.p1(5);
			outBuffer.p1((int) var5);
			outBuffer.p1(arg2 - lastX + 8 + (arg0 - lastY + 8 << 4));
		} else if (arg2 - lastX < 128 && arg2 - lastX >= -128 && arg0 - lastY < 128 && arg0 - lastY >= -128) {
			ensureCapacity(field73, 4);
			outBuffer.p1(6);
			outBuffer.p1((int) var5);
			outBuffer.p1(arg2 - lastX + 128);
			outBuffer.p1(arg0 - lastY + 128);
		} else {
			ensureCapacity(field73, 5);
			outBuffer.p1(7);
			outBuffer.p1((int) var5);
			outBuffer.p3(arg2 + (arg0 << 10));
		}
		lastX = arg2;
		lastY = arg0;
	}

	@ObfuscatedName("e.a(IZ)V")
	public static final synchronized void keyPressed(int arg0, boolean arg1) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		long var2 = System.currentTimeMillis();
		long var4 = (var2 - lastTime) / 10L;
		if (var4 > 250L) {
			var4 = 250L;
		}
		lastTime = var2;
		if (arg0 == 1000) {
			arg0 = 11;
		}
		if (arg0 == 1001) {
			arg0 = 12;
		}
		if (arg0 == 1002) {
			arg0 = 14;
		}
		if (arg0 == 1003) {
			arg0 = 15;
		}
		if (arg0 >= 1008) {
			arg0 -= 992;
		}
		ensureCapacity(field73, 3);
		if (!arg1) {
			for (int var6 = 1; var6 > 0; var6++) {
			}
		}
		outBuffer.p1(8);
		outBuffer.p1((int) var4);
		outBuffer.p1(arg0);
	}

	@ObfuscatedName("e.c(II)V")
	public static final synchronized void keyReleased(int arg0, int arg1) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		long var2 = System.currentTimeMillis();
		long var4 = (var2 - lastTime) / 10L;
		if (var4 > 250L) {
			var4 = 250L;
		}
		lastTime = var2;
		if (arg0 == 1000) {
			arg0 = 11;
		}
		if (arg0 == 1001) {
			arg0 = 12;
		}
		if (arg0 == 1002) {
			arg0 = 14;
		}
		if (arg0 == 1003) {
			arg0 = 15;
		}
		if (arg0 >= 1008) {
			arg0 -= 992;
		}
		ensureCapacity(field73, 3);
		outBuffer.p1(9);
		outBuffer.p1((int) var4);
		outBuffer.p1(arg0);
		if (arg1 == 1) {
			;
		}
	}

	@ObfuscatedName("e.d(I)V")
	public static final synchronized void focusGained(int arg0) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		if (arg0 >= 0) {
			return;
		}
		long var1 = System.currentTimeMillis();
		long var3 = (var1 - lastTime) / 10L;
		if (var3 > 250L) {
			var3 = 250L;
		}
		lastTime = var1;
		ensureCapacity(field73, 2);
		outBuffer.p1(10);
		outBuffer.p1((int) var3);
	}

	@ObfuscatedName("e.e(I)V")
	public static final synchronized void focusLost(int arg0) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		long var1 = System.currentTimeMillis();
		long var3 = (var1 - lastTime) / 10L;
		if (var3 > 250L) {
			var3 = 250L;
		}
		lastTime = var1;
		ensureCapacity(field73, 2);
		if (arg0 != 0) {
			field75 = !field75;
		}
		outBuffer.p1(11);
		outBuffer.p1((int) var3);
	}

	@ObfuscatedName("e.f(I)V")
	public static final synchronized void mouseEntered(int arg0) {
		if (!enabled) {
			return;
		}
		trackedCount++;
		long var1 = System.currentTimeMillis();
		long var3 = (var1 - lastTime) / 10L;
		if (var3 > 250L) {
			var3 = 250L;
		}
		lastTime = var1;
		ensureCapacity(field73, 2);
		if (arg0 < 0) {
			outBuffer.p1(12);
			outBuffer.p1((int) var3);
		}
	}

	@ObfuscatedName("e.a(Z)V")
	public static final synchronized void mouseExited(boolean arg0) {
		if (arg0 || !enabled) {
			return;
		}
		trackedCount++;
		long var1 = System.currentTimeMillis();
		long var3 = (var1 - lastTime) / 10L;
		if (var3 > 250L) {
			var3 = 250L;
		}
		lastTime = var1;
		ensureCapacity(field73, 2);
		outBuffer.p1(13);
		outBuffer.p1((int) var3);
	}
}
