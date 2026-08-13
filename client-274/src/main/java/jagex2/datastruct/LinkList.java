package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("ob")
public final class LinkList {

	@ObfuscatedName("ob.d")
	public final Linkable sentinel = new Linkable();

	@ObfuscatedName("ob.e")
	public Linkable cursor;

	public LinkList() {
		sentinel.next = sentinel;
		sentinel.prev = sentinel;
	}

	@ObfuscatedName("ob.a(Lu;)V")
	public void push(Linkable arg0) {
		if (arg0.prev != null) {
			arg0.unlink();
		}
		arg0.prev = sentinel.prev;
		arg0.next = sentinel;
		arg0.prev.next = arg0;
		arg0.next.prev = arg0;
	}

	@ObfuscatedName("ob.a(ZLu;)V")
	public void pushFront(Linkable arg1) {
		if (arg1.prev != null) {
			arg1.unlink();
		}
		arg1.prev = sentinel;
		arg1.next = sentinel.next;
		arg1.prev.next = arg1;
		arg1.next.prev = arg1;
	}

	@ObfuscatedName("ob.a()Lu;")
	public Linkable popFront() {
		Linkable var1 = sentinel.next;
		if (var1 == sentinel) {
			return null;
		} else {
			var1.unlink();
			return var1;
		}
	}

	@ObfuscatedName("ob.b()Lu;")
	public Linkable head() {
		Linkable var1 = sentinel.next;
		if (var1 == sentinel) {
			cursor = null;
			return null;
		} else {
			cursor = var1.next;
			return var1;
		}
	}

	@ObfuscatedName("ob.a(Z)Lu;")
	public Linkable tail() {
		Linkable var2 = sentinel.prev;
		if (var2 == sentinel) {
			cursor = null;
			return null;
		} else {
			cursor = var2.prev;
			return var2;
		}
	}

	@ObfuscatedName("ob.a(B)Lu;")
	public Linkable next() {
		Linkable var2 = cursor;
		if (var2 == sentinel) {
			cursor = null;
			return null;
		}
		cursor = var2.next;
		return var2;
	}

	@ObfuscatedName("ob.b(Z)Lu;")
	public Linkable prev() {
		Linkable var2 = cursor;
		if (var2 == sentinel) {
			cursor = null;
			return null;
		} else {
			cursor = var2.prev;
			return var2;
		}
	}

	@ObfuscatedName("ob.c()V")
	public void clear() {
		if (sentinel.next == sentinel) {
			return;
		}
		while (true) {
			Linkable var1 = sentinel.next;
			if (var1 == sentinel) {
				return;
			}
			var1.unlink();
		}
	}
}
