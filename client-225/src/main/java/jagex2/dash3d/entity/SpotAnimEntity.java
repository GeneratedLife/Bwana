package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;

@ObfuscatedName("bb")
public class SpotAnimEntity extends Entity {

	@ObfuscatedName("bb.e")
	public int field404;

	@ObfuscatedName("bb.f")
	public int field405;

	@ObfuscatedName("bb.g")
	public SpotAnimType type;

	@ObfuscatedName("bb.h")
	public int startCycle;

	@ObfuscatedName("bb.i")
	public int level;

	@ObfuscatedName("bb.j")
	public int x;

	@ObfuscatedName("bb.k")
	public int z;

	@ObfuscatedName("bb.l")
	public int y;

	@ObfuscatedName("bb.m")
	public int seqFrame;

	@ObfuscatedName("bb.n")
	public int seqCycle;

	@ObfuscatedName("bb.o")
	public boolean seqComplete = false;

	public SpotAnimEntity(int arg0, int arg1, boolean arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		this.type = SpotAnimType.instances[arg1];
		this.level = arg6;
		if (arg2) {
			this.field405 = -147;
		}
		this.x = arg0;
		this.z = arg3;
		this.y = arg5;
		this.startCycle = arg7 + arg4;
		this.seqComplete = false;
	}

	@ObfuscatedName("bb.a(II)V")
	public final void update(int arg0, int arg1) {
		if (arg1 != 0) {
			this.field405 = -255;
		}
		this.seqCycle += arg0;
		while (true) {
			do {
				do {
					if (this.seqCycle <= this.type.seq.delay[this.seqFrame]) {
						return;
					}
					this.seqCycle -= this.type.seq.delay[this.seqFrame] + 1;
					this.seqFrame++;
				} while (this.seqFrame < this.type.seq.frameCount);
			} while (this.seqFrame >= 0 && this.seqFrame < this.type.seq.frameCount);
			this.seqFrame = 0;
			this.seqComplete = true;
		}
	}

	@ObfuscatedName("bb.a(Z)Leb;")
	public final Model draw(boolean arg0) {
		Model var2 = this.type.getModel();
		Model var3 = new Model(var2, true, !this.type.animHasAlpha, this.field404, false);
		if (!arg0) {
			throw new NullPointerException();
		}
		if (!this.seqComplete) {
			var3.createLabelReferences(4);
			var3.applyTransform(-16599, this.type.seq.frames[this.seqFrame]);
			var3.labelFaces = null;
			var3.labelVertices = null;
		}
		if (this.type.resizeh != 128 || this.type.resizev != 128) {
			var3.scale(this.type.resizeh, 2, this.type.resizev, this.type.resizeh);
		}
		if (this.type.orientation != 0) {
			if (this.type.orientation == 90) {
				var3.rotateY90(0);
			}
			if (this.type.orientation == 180) {
				var3.rotateY90(0);
				var3.rotateY90(0);
			}
			if (this.type.orientation == 270) {
				var3.rotateY90(0);
				var3.rotateY90(0);
				var3.rotateY90(0);
			}
		}
		var3.calculateNormals(this.type.ambient + 64, this.type.contrast + 850, -30, -50, -30, true);
		return var3;
	}
}
