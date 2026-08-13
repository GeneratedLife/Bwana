package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.config.NpcType;
import jagex2.config.SeqType;
import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;

@ObfuscatedName("y")
public class NpcEntity extends PathingEntity {

	@ObfuscatedName("y.gb")
	public int field352;

	@ObfuscatedName("y.hb")
	public boolean field353 = false;

	@ObfuscatedName("y.ib")
	public NpcType type;

	@ObfuscatedName("y.a(Z)Leb;")
	public final Model draw(boolean arg0) {
		if (this.type == null) {
			return null;
		} else if (super.spotanimId == -1 || super.spotanimFrame == -1) {
			return this.getSequencedModel(false);
		} else {
			Model var2 = this.getSequencedModel(false);
			SpotAnimType var3 = SpotAnimType.instances[super.spotanimId];
			Model var4 = new Model(var3.getModel(), true, !var3.animHasAlpha, this.field352, false);
			var4.translate(-super.spotanimOffset, 0, -122, 0);
			var4.createLabelReferences(4);
			var4.applyTransform(-16599, var3.seq.frames[super.spotanimFrame]);
			var4.labelFaces = null;
			var4.labelVertices = null;
			if (!arg0) {
				throw new NullPointerException();
			}
			if (var3.resizeh != 128 || var3.resizev != 128) {
				var4.scale(var3.resizeh, 2, var3.resizev, var3.resizeh);
			}
			var4.calculateNormals(var3.ambient + 64, var3.contrast + 850, -30, -50, -30, true);
			Model[] var5 = new Model[] { var2, var4 };
			Model var6 = new Model(var5, (byte) -31, 2, true);
			if (this.type.size == 1) {
				var6.pickable = true;
			}
			return var6;
		}
	}

	@ObfuscatedName("y.c(Z)Leb;")
	public final Model getSequencedModel(boolean arg0) {
		int var2;
		if (super.primarySeqId >= 0 && super.primarySeqDelay == 0) {
			var2 = SeqType.instances[super.primarySeqId].frames[super.primarySeqFrame];
			int var4 = -1;
			if (super.secondarySeqId >= 0 && super.secondarySeqId != super.seqStandId) {
				var4 = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
			}
			return this.type.getSequencedModel(var2, var4, SeqType.instances[super.primarySeqId].walkmerge);
		}
		var2 = -1;
		if (arg0) {
			throw new NullPointerException();
		}
		if (super.secondarySeqId >= 0) {
			var2 = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
		}
		Model var3 = this.type.getSequencedModel(var2, -1, null);
		super.height = var3.maxY;
		return var3;
	}

	@ObfuscatedName("y.b(Z)Z")
	public final boolean isVisible(boolean arg0) {
		if (arg0) {
			this.field353 = !this.field353;
		}
		return this.type != null;
	}
}
