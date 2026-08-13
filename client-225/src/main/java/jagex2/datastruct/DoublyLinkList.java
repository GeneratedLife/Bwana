package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("pb")
public class DoublyLinkList {

	@ObfuscatedName("pb.a")
	public int field667 = 679;

	@ObfuscatedName("pb.b")
	public byte field668 = 2;

	@ObfuscatedName("pb.c")
	public boolean field669 = true;

	@ObfuscatedName("pb.d")
	public DoublyLinkable head = new DoublyLinkable();

	public DoublyLinkList(int arg0) {
		this.head.next2 = this.head;
		if (arg0 < 5 || arg0 > 5) {
			this.field667 = -426;
		}
		this.head.prev2 = this.head;
	}

	@ObfuscatedName("pb.a(Ldb;)V")
	public void push(DoublyLinkable arg0) {
		if (arg0.prev2 != null) {
			arg0.uncache();
		}
		arg0.prev2 = this.head.prev2;
		arg0.next2 = this.head;
		arg0.prev2.next2 = arg0;
		arg0.next2.prev2 = arg0;
	}

	@ObfuscatedName("pb.a()Ldb;")
	public DoublyLinkable pop() {
		DoublyLinkable var1 = this.head.next2;
		if (var1 == this.head) {
			return null;
		} else {
			var1.uncache();
			return var1;
		}
	}
}
