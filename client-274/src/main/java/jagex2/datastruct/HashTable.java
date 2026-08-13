package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("t")
public final class HashTable {

	@ObfuscatedName("t.e")
	public final int bucketCount;

	@ObfuscatedName("t.f")
	public final Linkable[] buckets;

	public HashTable(int arg0) {
		bucketCount = arg0;
		buckets = new Linkable[arg0];
		for (int var3 = 0; var3 < arg0; var3++) {
			Linkable var4 = buckets[var3] = new Linkable();
			var4.next = var4;
			var4.prev = var4;
		}
	}

	@ObfuscatedName("t.a(J)Lu;")
	public Linkable find(long arg0) {
		Linkable var3 = buckets[(int) (arg0 & (long) (bucketCount - 1))];
		for (Linkable var4 = var3.next; var4 != var3; var4 = var4.next) {
			if (var4.key == arg0) {
				return var4;
			}
		}
		return null;
	}

	@ObfuscatedName("t.a(ZJLu;)V")
	public void put(long arg1, Linkable arg2) {
		if (arg2.prev != null) {
			arg2.unlink();
		}
		Linkable var5 = buckets[(int) (arg1 & (long) (bucketCount - 1))];
		arg2.prev = var5.prev;
		arg2.next = var5;
		arg2.prev.next = arg2;
		arg2.next.prev = arg2;
		arg2.key = arg1;
	}
}
