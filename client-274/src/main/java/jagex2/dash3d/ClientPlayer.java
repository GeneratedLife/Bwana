package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.client.Client;
import jagex2.config.*;
import jagex2.datastruct.JString;
import jagex2.datastruct.LruCache;
import jagex2.io.Packet;

@ObfuscatedName("ab")
public final class ClientPlayer extends ClientEntity {

	@ObfuscatedName("ab.tb")
	public String name;

	@ObfuscatedName("ab.ub")
	public boolean ready = false;

	@ObfuscatedName("ab.vb")
	public int gender;

	@ObfuscatedName("ab.wb")
	public int headicons;

	@ObfuscatedName("ab.xb")
	public final int[] appearance = new int[12];

	@ObfuscatedName("ab.yb")
	public final int[] colour = new int[5];

	@ObfuscatedName("ab.zb")
	public int combatLevel;

	@ObfuscatedName("ab.Qb")
	public static LruCache modelCache = new LruCache(260);

	@ObfuscatedName("ab.Nb")
	public boolean lowMem = false;

	@ObfuscatedName("ab.Ob")
	public long modelCacheKey = -1L;

	@ObfuscatedName("ab.Ab")
	public int skillLevel;

	@ObfuscatedName("ab.Cb")
	public int y;

	@ObfuscatedName("ab.Db")
	public int locStartCycle;

	@ObfuscatedName("ab.Eb")
	public int locStopCycle;

	@ObfuscatedName("ab.Fb")
	public int locOffsetX;

	@ObfuscatedName("ab.Gb")
	public int locOffsetY;

	@ObfuscatedName("ab.Hb")
	public int locOffsetZ;

	@ObfuscatedName("ab.Jb")
	public int minTileX;

	@ObfuscatedName("ab.Kb")
	public int minTileZ;

	@ObfuscatedName("ab.Lb")
	public int maxTileX;

	@ObfuscatedName("ab.Mb")
	public int maxTileZ;

	@ObfuscatedName("ab.Bb")
	public long baseId;

	@ObfuscatedName("ab.Ib")
	public Model locModel;

	@ObfuscatedName("ab.Pb")
	public NpcType transmog;

	@ObfuscatedName("ab.a(Llb;I)V")
	public void setAppearance(Packet arg0) {
		arg0.data = 0;
		gender = arg0.g1();
		headicons = arg0.g1();
		transmog = null;
		for (int var3 = 0; var3 < 12; var3++) {
			int var4 = arg0.g1();
			if (var4 == 0) {
				appearance[var3] = 0;
			} else {
				int var5 = arg0.g1();
				appearance[var3] = (var4 << 8) + var5;
				if (var3 == 0 && appearance[0] == 65535) {
					transmog = NpcType.list(arg0.g2());
					break;
				}
			}
		}
		for (int var6 = 0; var6 < 5; var6++) {
			int var7 = arg0.g1();
			if (var7 < 0 || var7 >= Client.recol1d[var6].length) {
				var7 = 0;
			}
			colour[var6] = var7;
		}
		super.readyanim = arg0.g2();
		if (super.readyanim == 65535) {
			super.readyanim = -1;
		}
		super.turnanim = arg0.g2();
		if (super.turnanim == 65535) {
			super.turnanim = -1;
		}
		super.walkanim = arg0.g2();
		if (super.walkanim == 65535) {
			super.walkanim = -1;
		}
		super.walkanim_b = arg0.g2();
		if (super.walkanim_b == 65535) {
			super.walkanim_b = -1;
		}
		super.walkanim_r = arg0.g2();
		if (super.walkanim_r == 65535) {
			super.walkanim_r = -1;
		}
		super.walkanim_l = arg0.g2();
		if (super.walkanim_l == 65535) {
			super.walkanim_l = -1;
		}
		super.runanim = arg0.g2();
		if (super.runanim == 65535) {
			super.runanim = -1;
		}
		name = JString.toScreenName(JString.toRawUsername(arg0.g8()));
		combatLevel = arg0.g1();
		skillLevel = arg0.g2();
		ready = true;
		baseId = 0L;
		for (int var8 = 0; var8 < 12; var8++) {
			baseId <<= 0x4;
			if (appearance[var8] >= 256) {
				baseId += appearance[var8] - 256;
			}
		}
		if (appearance[0] >= 256) {
			baseId += appearance[0] - 256 >> 4;
		}
		if (appearance[1] >= 256) {
			baseId += appearance[1] - 256 >> 8;
		}
		for (int var9 = 0; var9 < 5; var9++) {
			baseId <<= 0x3;
			baseId += colour[var9];
		}
		baseId <<= 0x1;
		baseId += gender;
	}

	@ObfuscatedName("ab.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		if (!ready) {
			return null;
		}
		Model var2 = getTempModel2();
		if (var2 == null) {
			return null;
		}
		super.height = var2.minY;
		var2.useAABBMouseCheck = true;
		if (lowMem) {
			return var2;
		}
		if (super.spotanimId != -1 && super.spotanimFrame != -1) {
			SpotType var3 = SpotType.list[super.spotanimId];
			Model var4 = var3.getTempModel2();
			if (var4 != null) {
				Model var5 = new Model(true, AnimFrame.animateTransparencies(super.spotanimFrame), var4, false);
				var5.translate(0, -super.spotanimHeight, 0);
				var5.prepareAnim();
				var5.animate(var3.seq.frames[super.spotanimFrame]);
				var5.labelFaces = null;
				var5.labelVertices = null;
				if (var3.resizeh != 128 || var3.resizev != 128) {
					var5.resize(var3.resizeh, var3.resizeh, var3.resizev);
				}
				var5.calculateNormals(var3.ambient + 64, var3.contrast + 850, -30, -50, -30, true);
				Model[] var6 = new Model[] { var2, var5 };
				var2 = new Model(true, var6, 2);
			}
		}
		if (locModel != null) {
			if (Client.loopCycle >= locStopCycle) {
				locModel = null;
			}
			if (Client.loopCycle >= locStartCycle && Client.loopCycle < locStopCycle) {
				Model var7 = locModel;
				var7.translate(locOffsetX - super.x, locOffsetY - y, locOffsetZ - super.z);
				if (super.dstYaw == 512) {
					var7.rotate90();
					var7.rotate90();
					var7.rotate90();
				} else if (super.dstYaw == 1024) {
					var7.rotate90();
					var7.rotate90();
				} else if (super.dstYaw == 1536) {
					var7.rotate90();
				}
				Model[] var8 = new Model[] { var2, var7 };
				var2 = new Model(true, var8, 2);
				if (super.dstYaw == 512) {
					var7.rotate90();
				} else if (super.dstYaw == 1024) {
					var7.rotate90();
					var7.rotate90();
				} else if (super.dstYaw == 1536) {
					var7.rotate90();
					var7.rotate90();
					var7.rotate90();
				}
				var7.translate(super.x - locOffsetX, y - locOffsetY, super.z - locOffsetZ);
			}
		}
		var2.useAABBMouseCheck = true;
		return var2;
	}

	@ObfuscatedName("ab.c(Z)Leb;")
	public Model getTempModel2() {
		if (transmog != null) {
			int var3 = -1;
			if (super.primaryAnim >= 0 && super.primaryAnimDelay == 0) {
				var3 = SeqType.list[super.primaryAnim].frames[super.primaryAnimFrame];
			} else if (super.secondaryAnim >= 0) {
				var3 = SeqType.list[super.secondaryAnim].frames[super.secondaryAnimFrame];
			}
			return transmog.getTempModel(var3, -1, null);
		}
		long var5 = baseId;
		int var7 = -1;
		int var8 = -1;
		int var9 = -1;
		int var10 = -1;
		if (super.primaryAnim >= 0 && super.primaryAnimDelay == 0) {
			SeqType var11 = SeqType.list[super.primaryAnim];
			var7 = var11.frames[super.primaryAnimFrame];
			if (super.secondaryAnim >= 0 && super.secondaryAnim != super.readyanim) {
				var8 = SeqType.list[super.secondaryAnim].frames[super.secondaryAnimFrame];
			}
			if (var11.replaceheldleft >= 0) {
				var9 = var11.replaceheldleft;
				var5 += var9 - appearance[5] << 8;
			}
			if (var11.replaceheldright >= 0) {
				var10 = var11.replaceheldright;
				var5 += var10 - appearance[3] << 16;
			}
		} else if (super.secondaryAnim >= 0) {
			var7 = SeqType.list[super.secondaryAnim].frames[super.secondaryAnimFrame];
		}
		Model var12 = (Model) modelCache.find(var5);
		if (var12 == null) {
			boolean var13 = false;
			for (int var14 = 0; var14 < 12; var14++) {
				int var15 = appearance[var14];
				if (var10 >= 0 && var14 == 3) {
					var15 = var10;
				}
				if (var9 >= 0 && var14 == 5) {
					var15 = var9;
				}
				if (var15 >= 256 && var15 < 512 && !IdkType.list[var15 - 256].checkModel()) {
					var13 = true;
				}
				if (var15 >= 512 && !ObjType.list(var15 - 512).checkWearModel(gender)) {
					var13 = true;
				}
			}
			if (var13) {
				if (modelCacheKey != -1L) {
					var12 = (Model) modelCache.find(modelCacheKey);
				}
				if (var12 == null) {
					return null;
				}
			}
		}
		if (var12 == null) {
			Model[] var16 = new Model[12];
			int var17 = 0;
			for (int var18 = 0; var18 < 12; var18++) {
				int var19 = appearance[var18];
				if (var10 >= 0 && var18 == 3) {
					var19 = var10;
				}
				if (var9 >= 0 && var18 == 5) {
					var19 = var9;
				}
				if (var19 >= 256 && var19 < 512) {
					Model var20 = IdkType.list[var19 - 256].getModelNoCheck();
					if (var20 != null) {
						var16[var17++] = var20;
					}
				}
				if (var19 >= 512) {
					Model var21 = ObjType.list(var19 - 512).getWearModelNoCheck(gender);
					if (var21 != null) {
						var16[var17++] = var21;
					}
				}
			}
			var12 = new Model(var17, var16);
			for (int var22 = 0; var22 < 5; var22++) {
				if (colour[var22] != 0) {
					var12.recolour(Client.recol1d[var22][0], Client.recol1d[var22][colour[var22]]);
					if (var22 == 1) {
						var12.recolour(Client.recol2d[0], Client.recol2d[colour[var22]]);
					}
				}
			}
			var12.prepareAnim();
			var12.calculateNormals(64, 850, -30, -50, -30, true);
			modelCache.put(var5, var12);
			modelCacheKey = var5;
		}
		if (lowMem) {
			return var12;
		}
		Model var23 = Model.tempModel;
		var23.set(var12, AnimFrame.animateTransparencies(var7) & AnimFrame.animateTransparencies(var8));
		if (var7 != -1 && var8 != -1) {
			var23.maskAnimate(SeqType.list[super.primaryAnim].walkmerge, var7, var8);
		} else if (var7 != -1) {
			var23.animate(var7);
		}
		var23.calcBoundingCylinder();
		var23.labelFaces = null;
		var23.labelVertices = null;
		return var23;
	}

	@ObfuscatedName("ab.d(Z)Leb;")
	public Model getHeadModel() {
		if (!ready) {
			return null;
		}
		boolean var2 = false;
		for (int var3 = 0; var3 < 12; var3++) {
			int var4 = appearance[var3];
			if (var4 >= 256 && var4 < 512 && !IdkType.list[var4 - 256].checkHead()) {
				var2 = true;
			}
			if (var4 >= 512 && !ObjType.list(var4 - 512).checkHeadModel(gender)) {
				var2 = true;
			}
		}
		if (var2) {
			return null;
		}
		Model[] var5 = new Model[12];
		int var6 = 0;
		for (int var7 = 0; var7 < 12; var7++) {
			int var8 = appearance[var7];
			if (var8 >= 256 && var8 < 512) {
				Model var9 = IdkType.list[var8 - 256].getHeadNoCheck();
				if (var9 != null) {
					var5[var6++] = var9;
				}
			}
			if (var8 >= 512) {
				Model var10 = ObjType.list(var8 - 512).getHeadModelNoCheck(gender);
				if (var10 != null) {
					var5[var6++] = var10;
				}
			}
		}
		Model var11 = new Model(var6, var5);
		for (int var12 = 0; var12 < 5; var12++) {
			if (colour[var12] != 0) {
				var11.recolour(Client.recol1d[var12][0], Client.recol1d[var12][colour[var12]]);
				if (var12 == 1) {
					var11.recolour(Client.recol2d[0], Client.recol2d[colour[var12]]);
				}
			}
		}
		return var11;
	}

	@ObfuscatedName("ab.a(I)Z")
	@Override
	public boolean isReady() {
		return ready;
	}
}
