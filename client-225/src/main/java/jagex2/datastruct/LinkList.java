package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("ob")
public class LinkList {

	@ObfuscatedName("ob.a")
	public boolean field661 = true;

	@ObfuscatedName("ob.b")
	public byte field662 = 2;

	@ObfuscatedName("ob.c")
	public int field663 = -546;

	@ObfuscatedName("ob.d")
	public int field664 = -676;

	@ObfuscatedName("ob.e")
	public Linkable sentinel = new Linkable();

	@ObfuscatedName("ob.f")
	public Linkable cursor;

	public LinkList(int arg0) {
		if (arg0 != 0) {
			this.field661 = !this.field661;
		}
		this.sentinel.next = this.sentinel;
		this.sentinel.prev = this.sentinel;
	}

	@ObfuscatedName("ob.a(Lu;)V")
	public void addTail(Linkable arg0) {
		if (arg0.prev != null) {
			arg0.unlink();
		}
		arg0.prev = this.sentinel.prev;
		arg0.next = this.sentinel;
		arg0.prev.next = arg0;
		arg0.next.prev = arg0;
	}

	@ObfuscatedName("ob.a(Lu;I)V")
	public void addHead(Linkable arg0, int arg1) {
		if (arg0.prev != null) {
			arg0.unlink();
		}
		arg0.prev = this.sentinel;
		if (arg1 == -26173) {
			arg0.next = this.sentinel.next;
			arg0.prev.next = arg0;
			arg0.next.prev = arg0;
		}
	}

	@ObfuscatedName("ob.a()Lu;")
	public Linkable removeHead() {
		Linkable var1 = this.sentinel.next;
		if (var1 == this.sentinel) {
			return null;
		} else {
			var1.unlink();
			return var1;
		}
	}

	@ObfuscatedName("ob.b()Lu;")
	public Linkable head() {
		Linkable var1 = this.sentinel.next;
		if (var1 == this.sentinel) {
			this.cursor = null;
			return null;
		} else {
			this.cursor = var1.next;
			return var1;
		}
	}

	@ObfuscatedName("ob.a(B)Lu;")
	public Linkable tail(byte arg0) {
		Linkable var2 = this.sentinel.prev;
		if (var2 == this.sentinel) {
			this.cursor = null;
			return null;
		}
		this.cursor = var2.prev;
		if (arg0 != this.field662) {
			this.field664 = 112;
		}
		return var2;
	}

	@ObfuscatedName("ob.a(I)Lu;")
	public Linkable next(int arg0) {
		if (arg0 <= 0) {
			throw new NullPointerException();
		}
		Linkable var2 = this.cursor;
		if (var2 == this.sentinel) {
			this.cursor = null;
			return null;
		} else {
			this.cursor = var2.next;
			return var2;
		}
	}

	@ObfuscatedName("ob.a(Z)Lu;")
	public Linkable prev(boolean arg0) {
		Linkable var2 = this.cursor;
		if (arg0) {
			for (int var3 = 1; var3 > 0; var3++) {
			}
		}
		if (var2 == this.sentinel) {
			this.cursor = null;
			return null;
		} else {
			this.cursor = var2.prev;
			return var2;
		}
	}

	@ObfuscatedName("ob.c()V")
	public void clear() {
		while (true) {
			Linkable var1 = this.sentinel.next;
			if (var1 == this.sentinel) {
				return;
			}
			var1.unlink();
		}
	}
}
