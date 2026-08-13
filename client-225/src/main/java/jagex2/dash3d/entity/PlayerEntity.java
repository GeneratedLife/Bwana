package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import deob.client;
import jagex2.config.IdkType;
import jagex2.config.ObjType;
import jagex2.config.SeqType;
import jagex2.config.SpotAnimType;
import jagex2.datastruct.JString;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Packet;

@ObfuscatedName("z")
public class PlayerEntity extends PathingEntity {

	@ObfuscatedName("z.gb")
	public int field355;

	@ObfuscatedName("z.hb")
	public boolean field356 = false;

	@ObfuscatedName("z.ib")
	public String name;

	@ObfuscatedName("z.jb")
	public boolean visible = false;

	@ObfuscatedName("z.kb")
	public int gender;

	@ObfuscatedName("z.lb")
	public int headicons;

	@ObfuscatedName("z.mb")
	public int[] appearances = new int[12];

	@ObfuscatedName("z.nb")
	public int[] colors = new int[5];

	@ObfuscatedName("z.ob")
	public int combatLevel;

	@ObfuscatedName("z.pb")
	public long appearanceHashcode;

	@ObfuscatedName("z.qb")
	public int y;

	@ObfuscatedName("z.rb")
	public int locStartCycle;

	@ObfuscatedName("z.sb")
	public int locStopCycle;

	@ObfuscatedName("z.tb")
	public int locOffsetX;

	@ObfuscatedName("z.ub")
	public int locOffsetY;

	@ObfuscatedName("z.vb")
	public int locOffsetZ;

	@ObfuscatedName("z.wb")
	public Model locModel;

	@ObfuscatedName("z.xb")
	public int minTileX;

	@ObfuscatedName("z.yb")
	public int minTileZ;

	@ObfuscatedName("z.zb")
	public int maxTileX;

	@ObfuscatedName("z.Bb")
	public boolean lowMemory = false;

	@ObfuscatedName("z.Cb")
	public static LruCache modelCache = new LruCache((byte) 0, 200);

	@ObfuscatedName("z.Ab")
	public int maxTileZ;

	@ObfuscatedName("z.a(ZLkb;)V")
	public final void read(boolean arg0, Packet arg1) {
		arg1.pos = 0;
		this.gender = arg1.g1();
		this.headicons = arg1.g1();
		int var4;
		int var5;
		for (int var3 = 0; var3 < 12; var3++) {
			var4 = arg1.g1();
			if (var4 == 0) {
				this.appearances[var3] = 0;
			} else {
				var5 = arg1.g1();
				this.appearances[var3] = (var4 << 8) + var5;
			}
		}
		for (var4 = 0; var4 < 5; var4++) {
			var5 = arg1.g1();
			if (var5 < 0 || var5 >= client.field1307[var4].length) {
				var5 = 0;
			}
			this.colors[var4] = var5;
		}
		if (arg0) {
			return;
		}
		super.seqStandId = arg1.g2();
		if (super.seqStandId == 65535) {
			super.seqStandId = -1;
		}
		super.seqTurnId = arg1.g2();
		if (super.seqTurnId == 65535) {
			super.seqTurnId = -1;
		}
		super.seqWalkId = arg1.g2();
		if (super.seqWalkId == 65535) {
			super.seqWalkId = -1;
		}
		super.seqTurnAroundId = arg1.g2();
		if (super.seqTurnAroundId == 65535) {
			super.seqTurnAroundId = -1;
		}
		super.seqTurnLeftId = arg1.g2();
		if (super.seqTurnLeftId == 65535) {
			super.seqTurnLeftId = -1;
		}
		super.seqTurnRightId = arg1.g2();
		if (super.seqTurnRightId == 65535) {
			super.seqTurnRightId = -1;
		}
		super.seqRunId = arg1.g2();
		if (super.seqRunId == 65535) {
			super.seqRunId = -1;
		}
		this.name = JString.formatName(0, JString.fromBase37(arg1.g8(603), false));
		this.combatLevel = arg1.g1();
		this.visible = true;
		this.appearanceHashcode = 0L;
		for (var5 = 0; var5 < 12; var5++) {
			this.appearanceHashcode <<= 0x4;
			if (this.appearances[var5] >= 256) {
				this.appearanceHashcode += this.appearances[var5] - 256;
			}
		}
		if (this.appearances[0] >= 256) {
			this.appearanceHashcode += this.appearances[0] - 256 >> 4;
		}
		if (this.appearances[1] >= 256) {
			this.appearanceHashcode += this.appearances[1] - 256 >> 8;
		}
		for (int var6 = 0; var6 < 5; var6++) {
			this.appearanceHashcode <<= 0x3;
			this.appearanceHashcode += this.colors[var6];
		}
		this.appearanceHashcode <<= 0x1;
		this.appearanceHashcode += this.gender;
	}

	@ObfuscatedName("z.a(Z)Leb;")
	public final Model draw(boolean arg0) {
		if (!this.visible) {
			return null;
		}
		Model var2 = this.getSequencedModel(false);
		super.height = var2.maxY;
		var2.pickable = true;
		if (this.lowMemory) {
			return var2;
		}
		if (super.spotanimId != -1 && super.spotanimFrame != -1) {
			SpotAnimType var3 = SpotAnimType.instances[super.spotanimId];
			Model var4 = new Model(var3.getModel(), true, !var3.animHasAlpha, this.field355, false);
			var4.translate(-super.spotanimOffset, 0, -122, 0);
			var4.createLabelReferences(4);
			var4.applyTransform(-16599, var3.seq.frames[super.spotanimFrame]);
			var4.labelFaces = null;
			var4.labelVertices = null;
			if (var3.resizeh != 128 || var3.resizev != 128) {
				var4.scale(var3.resizeh, 2, var3.resizev, var3.resizeh);
			}
			var4.calculateNormals(var3.ambient + 64, var3.contrast + 850, -30, -50, -30, true);
			Model[] var5 = new Model[] { var2, var4 };
			var2 = new Model(var5, (byte) -31, 2, true);
		}
		if (this.locModel != null) {
			if (client.loopCycle >= this.locStopCycle) {
				this.locModel = null;
			}
			if (client.loopCycle >= this.locStartCycle && client.loopCycle < this.locStopCycle) {
				Model var6 = this.locModel;
				var6.translate(this.locOffsetY - this.y, this.locOffsetX - super.x, -122, this.locOffsetZ - super.z);
				if (super.dstYaw == 512) {
					var6.rotateY90(0);
					var6.rotateY90(0);
					var6.rotateY90(0);
				} else if (super.dstYaw == 1024) {
					var6.rotateY90(0);
					var6.rotateY90(0);
				} else if (super.dstYaw == 1536) {
					var6.rotateY90(0);
				}
				Model[] var8 = new Model[] { var2, var6 };
				var2 = new Model(var8, (byte) -31, 2, true);
				if (super.dstYaw == 512) {
					var6.rotateY90(0);
				} else if (super.dstYaw == 1024) {
					var6.rotateY90(0);
					var6.rotateY90(0);
				} else if (super.dstYaw == 1536) {
					var6.rotateY90(0);
					var6.rotateY90(0);
					var6.rotateY90(0);
				}
				var6.translate(this.y - this.locOffsetY, super.x - this.locOffsetX, -122, super.z - this.locOffsetZ);
			}
		}
		var2.pickable = true;
		if (!arg0) {
			for (int var7 = 1; var7 > 0; var7++) {
			}
		}
		return var2;
	}

	@ObfuscatedName("z.c(Z)Leb;")
	public final Model getSequencedModel(boolean arg0) {
		long var2 = this.appearanceHashcode;
		int var4 = -1;
		int var5 = -1;
		int var6 = -1;
		int var7 = -1;
		if (super.primarySeqId >= 0 && super.primarySeqDelay == 0) {
			SeqType var8 = SeqType.instances[super.primarySeqId];
			var4 = var8.frames[super.primarySeqFrame];
			if (super.secondarySeqId >= 0 && super.secondarySeqId != super.seqStandId) {
				var5 = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
			}
			if (var8.righthand >= 0) {
				var6 = var8.righthand;
				var2 += var6 - this.appearances[5] << 8;
			}
			if (var8.lefthand >= 0) {
				var7 = var8.lefthand;
				var2 += var7 - this.appearances[3] << 16;
			}
		} else if (super.secondarySeqId >= 0) {
			var4 = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
		}
		Model var15 = (Model) modelCache.get(var2);
		if (var15 == null) {
			Model[] var9 = new Model[12];
			int var10 = 0;
			int var12;
			for (int var11 = 0; var11 < 12; var11++) {
				var12 = this.appearances[var11];
				if (var7 >= 0 && var11 == 3) {
					var12 = var7;
				}
				if (var6 >= 0 && var11 == 5) {
					var12 = var6;
				}
				if (var12 >= 256 && var12 < 512) {
					var9[var10++] = IdkType.instances[var12 - 256].getModel();
				}
				if (var12 >= 512) {
					ObjType var13 = ObjType.get(var12 - 512);
					Model var14 = var13.getWornModel((byte) 6, this.gender);
					if (var14 != null) {
						var9[var10++] = var14;
					}
				}
			}
			var15 = new Model(0, var9, var10);
			for (var12 = 0; var12 < 5; var12++) {
				if (this.colors[var12] != 0) {
					var15.recolor(client.field1307[var12][0], client.field1307[var12][this.colors[var12]]);
					if (var12 == 1) {
						var15.recolor(client.field1438[0], client.field1438[this.colors[var12]]);
					}
				}
			}
			var15.createLabelReferences(4);
			var15.calculateNormals(64, 850, -30, -50, -30, true);
			modelCache.put(6, var2, var15);
		}
		if (this.lowMemory) {
			return var15;
		}
		Model var16 = new Model(0, var15, true);
		if (arg0) {
			this.field356 = !this.field356;
		}
		if (var4 != -1 && var5 != -1) {
			var16.applyTransforms(var5, 3, var4, SeqType.instances[super.primarySeqId].walkmerge);
		} else if (var4 != -1) {
			var16.applyTransform(-16599, var4);
		}
		var16.calculateBoundsCylinder(2992);
		var16.labelFaces = null;
		var16.labelVertices = null;
		return var16;
	}

	@ObfuscatedName("z.a(I)Leb;")
	public final Model getHeadModel(int arg0) {
		if (!this.visible) {
			return null;
		}
		Model[] var2 = new Model[12];
		int var3 = 0;
		for (int var4 = 0; var4 < 12; var4++) {
			int var5 = this.appearances[var4];
			if (var5 >= 256 && var5 < 512) {
				var2[var3++] = IdkType.instances[var5 - 256].getHeadModel(false);
			}
			if (var5 >= 512) {
				Model var6 = ObjType.get(var5 - 512).getHeadModel(-22246, this.gender);
				if (var6 != null) {
					var2[var3++] = var6;
				}
			}
		}
		Model var7 = new Model(0, var2, var3);
		for (int var8 = 0; var8 < 5; var8++) {
			if (this.colors[var8] != 0) {
				var7.recolor(client.field1307[var8][0], client.field1307[var8][this.colors[var8]]);
				if (var8 == 1) {
					var7.recolor(client.field1438[0], client.field1438[this.colors[var8]]);
				}
			}
		}
		if (arg0 >= 0) {
			throw new NullPointerException();
		}
		return var7;
	}

	@ObfuscatedName("z.b(Z)Z")
	public final boolean isVisible(boolean arg0) {
		if (arg0) {
			this.field356 = !this.field356;
		}
		return this.visible;
	}
}
