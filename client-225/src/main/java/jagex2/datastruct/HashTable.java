package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("t")
public class HashTable {

	@ObfuscatedName("t.a")
	public int field288 = 4277;

	@ObfuscatedName("t.b")
	public boolean field289 = false;

	@ObfuscatedName("t.c")
	public int bucketCount;

	@ObfuscatedName("t.d")
	public Linkable[] buckets;

	public HashTable(int arg0, int arg1) {
		this.bucketCount = arg1;
		this.buckets = new Linkable[arg1];
		if (arg0 < 9 || arg0 > 9) {
			this.field289 = !this.field289;
		}
		for (int var3 = 0; var3 < arg1; var3++) {
			Linkable var4 = this.buckets[var3] = new Linkable();
			var4.next = var4;
			var4.prev = var4;
		}
	}

	@ObfuscatedName("t.a(J)Lu;")
	public Linkable get(long arg0) {
		Linkable var3 = this.buckets[(int) (arg0 & (long) (this.bucketCount - 1))];
		for (Linkable var4 = var3.next; var4 != var3; var4 = var4.next) {
			if (var4.key == arg0) {
				return var4;
			}
		}
		return null;
	}

	@ObfuscatedName("t.a(JILu;)V")
	public void put(long arg0, int arg1, Linkable arg2) {
		if (arg2.prev != null) {
			arg2.unlink();
		}
		Linkable var5 = this.buckets[(int) (arg0 & (long) (this.bucketCount - 1))];
		arg2.prev = var5.prev;
		if (arg1 < 0) {
			arg2.next = var5;
			arg2.prev.next = arg2;
			arg2.next.prev = arg2;
			arg2.key = arg0;
		}
	}
}
