package jagex2.client;

import deob.ObfuscatedName;

@ObfuscatedName("fc")
public final class MouseTracking implements Runnable {

	@ObfuscatedName("fc.b")
	public final Client app;

	@ObfuscatedName("fc.c")
	public boolean active = true;

	@ObfuscatedName("fc.d")
	public final Object lock = new Object();

	@ObfuscatedName("fc.e")
	public int length;

	@ObfuscatedName("fc.f")
	public final int[] x = new int[500];

	@ObfuscatedName("fc.g")
	public final int[] y = new int[500];

	public MouseTracking(Client arg1) {
		app = arg1;
	}

	@Override
	public void run() {
		while (active) {
			Object var1 = lock;
			synchronized (lock) {
				if (length < 500) {
					x[length] = app.mouseX;
					y[length] = app.mouseY;
					length++;
				}
			}
			try {
				Thread.sleep(50L);
			} catch (Exception var2) {
			}
		}
	}
}
