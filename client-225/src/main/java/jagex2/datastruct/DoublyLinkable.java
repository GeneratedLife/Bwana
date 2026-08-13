package jagex2.datastruct;

import deob.ObfuscatedName;

@ObfuscatedName("db")
public class DoublyLinkable extends Linkable {

	@ObfuscatedName("db.e")
	public DoublyLinkable next2;

	@ObfuscatedName("db.f")
	public DoublyLinkable prev2;

	@ObfuscatedName("db.g")
	public static int field440;

	@ObfuscatedName("db.b()V")
	public void uncache() {
		if (this.prev2 != null) {
			this.prev2.next2 = this.next2;
			this.next2.prev2 = this.prev2;
			this.next2 = null;
			this.prev2 = null;
		}
	}
}
