package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.ObjType;

@ObfuscatedName("cb")
public final class ClientObj extends ModelSource {

	@ObfuscatedName("cb.n")
	public int id;

	@ObfuscatedName("cb.o")
	public int count;

	@ObfuscatedName("cb.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		ObjType var2 = ObjType.list(id);
		return var2.getModelLit(count);
	}
}
