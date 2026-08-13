package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("s")
public class LruCache {

	@ObfuscatedName("s.a")
	public boolean field282 = false;

	@ObfuscatedName("s.b")
	public static int field283 = 5;

	@ObfuscatedName("s.c")
	public int capacity;

	@ObfuscatedName("s.d")
	public int available;

	@ObfuscatedName("s.e")
	public HashTable hashtable = new HashTable(9, 1024);

	@ObfuscatedName("s.f")
	public DoublyLinkList history = new DoublyLinkList(field283);

	public LruCache(byte arg0, int arg1) {
		this.capacity = arg1;
		if (arg0 != 0) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		this.available = arg1;
	}

	@ObfuscatedName("s.a(J)Ldb;")
	public DoublyLinkable get(long arg0) {
		DoublyLinkable var3 = (DoublyLinkable) this.hashtable.get(arg0);
		if (var3 != null) {
			this.history.push(var3);
		}
		return var3;
	}

	@ObfuscatedName("s.a(IJLdb;)V")
	public void put(int arg0, long arg1, DoublyLinkable arg2) {
		if (this.available == 0) {
			DoublyLinkable var5 = this.history.pop();
			var5.unlink();
			var5.uncache();
		} else {
			this.available--;
		}
		this.hashtable.put(arg1, -566, arg2);
		if (arg0 < 6 || arg0 > 6) {
			this.field282 = !this.field282;
		}
		this.history.push(arg2);
	}

	@ObfuscatedName("s.a()V")
	public void clear() {
		while (true) {
			DoublyLinkable var1 = this.history.pop();
			if (var1 == null) {
				this.available = this.capacity;
				return;
			}
			var1.unlink();
			var1.uncache();
		}
	}
}
