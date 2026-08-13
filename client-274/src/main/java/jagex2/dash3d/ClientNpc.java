package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.NpcType;
import jagex2.config.SeqType;
import jagex2.config.SpotType;

@ObfuscatedName("z")
public final class ClientNpc extends ClientEntity {

	@ObfuscatedName("z.rb")
	public NpcType type;

	@ObfuscatedName("z.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		if (type == null) {
			return null;
		}
		Model var3 = getTempModel2();
		if (var3 == null) {
			return null;
		}
		super.height = var3.minY;
		if (super.spotanimId != -1 && super.spotanimFrame != -1) {
			SpotType var4 = SpotType.list[super.spotanimId];
			Model var5 = var4.getTempModel2();
			if (var5 != null) {
				int var6 = var4.seq.frames[super.spotanimFrame];
				Model var7 = new Model(true, AnimFrame.animateTransparencies(var6), var5, false);
				var7.translate(0, -super.spotanimHeight, 0);
				var7.prepareAnim();
				var7.animate(var6);
				var7.labelFaces = null;
				var7.labelVertices = null;
				if (var4.resizeh != 128 || var4.resizev != 128) {
					var7.resize(var4.resizeh, var4.resizeh, var4.resizev);
				}
				var7.calculateNormals(var4.ambient + 64, var4.contrast + 850, -30, -50, -30, true);
				Model[] var8 = new Model[] { var3, var7 };
				var3 = new Model(true, var8, 2);
			}
		}
		if (type.size == 1) {
			var3.useAABBMouseCheck = true;
		}
		return var3;
	}

	@ObfuscatedName("z.c(Z)Leb;")
	public Model getTempModel2() {
		if (super.primaryAnim >= 0 && super.primaryAnimDelay == 0) {
			int var2 = SeqType.list[super.primaryAnim].frames[super.primaryAnimFrame];
			int var3 = -1;
			if (super.secondaryAnim >= 0 && super.secondaryAnim != super.readyanim) {
				var3 = SeqType.list[super.secondaryAnim].frames[super.secondaryAnimFrame];
			}
			return type.getTempModel(var2, var3, SeqType.list[super.primaryAnim].walkmerge);
		}
		int var4 = -1;
		if (super.secondaryAnim >= 0) {
			var4 = SeqType.list[super.secondaryAnim].frames[super.secondaryAnimFrame];
		}
		return type.getTempModel(var4, -1, null);
	}

	@ObfuscatedName("z.a(I)Z")
	@Override
	public boolean isReady() {
		return type != null;
	}
}
