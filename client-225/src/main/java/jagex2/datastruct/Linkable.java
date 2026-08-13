package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("u")
public class Linkable {

	@ObfuscatedName("u.a")
	public long key;

	@ObfuscatedName("u.b")
	public Linkable next;

	@ObfuscatedName("u.c")
	public Linkable prev;

	@ObfuscatedName("u.d")
	public static int field295;

	@ObfuscatedName("u.a()V")
	public void unlink() {
		if (this.prev != null) {
			this.prev.next = this.next;
			this.next.prev = this.prev;
			this.next = null;
			this.prev = null;
		}
	}
}
