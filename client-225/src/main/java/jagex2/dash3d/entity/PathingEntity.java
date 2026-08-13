package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.config.SeqType;

@ObfuscatedName("x")
public class PathingEntity extends Entity {

	@ObfuscatedName("x.e")
	public int field298 = 332;

	@ObfuscatedName("x.f")
	public boolean field299 = false;

	@ObfuscatedName("x.g")
	public int x;

	@ObfuscatedName("x.h")
	public int z;

	@ObfuscatedName("x.i")
	public int yaw;

	@ObfuscatedName("x.j")
	public boolean seqStretches = false;

	@ObfuscatedName("x.k")
	public int size = 1;

	@ObfuscatedName("x.l")
	public int seqStandId = -1;

	@ObfuscatedName("x.m")
	public int seqTurnId = -1;

	@ObfuscatedName("x.n")
	public int seqWalkId = -1;

	@ObfuscatedName("x.o")
	public int seqTurnAroundId = -1;

	@ObfuscatedName("x.p")
	public int seqTurnLeftId = -1;

	@ObfuscatedName("x.q")
	public int seqTurnRightId = -1;

	@ObfuscatedName("x.r")
	public int seqRunId = -1;

	@ObfuscatedName("x.s")
	public String chat;

	@ObfuscatedName("x.t")
	public int chatTimer = 100;

	@ObfuscatedName("x.u")
	public int chatColor;

	@ObfuscatedName("x.v")
	public int chatStyle;

	@ObfuscatedName("x.w")
	public int damage;

	@ObfuscatedName("x.x")
	public int damageType;

	@ObfuscatedName("x.y")
	public int combatCycle = -1000;

	@ObfuscatedName("x.z")
	public int health;

	@ObfuscatedName("x.ab")
	public int dstYaw;

	@ObfuscatedName("x.bb")
	public int pathLength;

	@ObfuscatedName("x.cb")
	public int[] pathTileX = new int[10];

	@ObfuscatedName("x.db")
	public int[] pathTileZ = new int[10];

	@ObfuscatedName("x.eb")
	public boolean[] pathRunning = new boolean[10];

	@ObfuscatedName("x.fb")
	public int seqTrigger;

	@ObfuscatedName("x.B")
	public int targetId = -1;

	@ObfuscatedName("x.E")
	public int secondarySeqId = -1;

	@ObfuscatedName("x.H")
	public int primarySeqId = -1;

	@ObfuscatedName("x.M")
	public int spotanimId = -1;

	@ObfuscatedName("x.A")
	public int totalHealth;

	@ObfuscatedName("x.C")
	public int targetTileX;

	@ObfuscatedName("x.D")
	public int targetTileZ;

	@ObfuscatedName("x.F")
	public int secondarySeqFrame;

	@ObfuscatedName("x.G")
	public int secondarySeqCycle;

	@ObfuscatedName("x.I")
	public int primarySeqFrame;

	@ObfuscatedName("x.J")
	public int primarySeqCycle;

	@ObfuscatedName("x.K")
	public int primarySeqDelay;

	@ObfuscatedName("x.L")
	public int primarySeqLoop;

	@ObfuscatedName("x.N")
	public int spotanimFrame;

	@ObfuscatedName("x.O")
	public int spotanimCycle;

	@ObfuscatedName("x.P")
	public int spotanimLastCycle;

	@ObfuscatedName("x.Q")
	public int spotanimOffset;

	@ObfuscatedName("x.R")
	public int forceMoveStartSceneTileX;

	@ObfuscatedName("x.S")
	public int forceMoveEndSceneTileX;

	@ObfuscatedName("x.T")
	public int forceMoveStartSceneTileZ;

	@ObfuscatedName("x.U")
	public int forceMoveEndSceneTileZ;

	@ObfuscatedName("x.V")
	public int forceMoveEndCycle;

	@ObfuscatedName("x.W")
	public int forceMoveStartCycle;

	@ObfuscatedName("x.X")
	public int forceMoveFaceDirection;

	@ObfuscatedName("x.Y")
	public int cycle;

	@ObfuscatedName("x.Z")
	public int height;

	@ObfuscatedName("x.a(ZZII)V")
	public final void teleport(boolean arg0, boolean arg1, int arg2, int arg3) {
		if (this.primarySeqId != -1 && SeqType.instances[this.primarySeqId].priority <= 1) {
			this.primarySeqId = -1;
		}
		if (!arg1) {
			int var5 = arg2 - this.pathTileX[0];
			int var6 = arg3 - this.pathTileZ[0];
			if (var5 >= -8 && var5 <= 8 && var6 >= -8 && var6 <= 8) {
				if (this.pathLength < 9) {
					this.pathLength++;
				}
				for (int var7 = this.pathLength; var7 > 0; var7--) {
					this.pathTileX[var7] = this.pathTileX[var7 - 1];
					this.pathTileZ[var7] = this.pathTileZ[var7 - 1];
					this.pathRunning[var7] = this.pathRunning[var7 - 1];
				}
				this.pathTileX[0] = arg2;
				this.pathTileZ[0] = arg3;
				this.pathRunning[0] = false;
				return;
			}
		}
		this.pathLength = 0;
		this.seqTrigger = 0;
		this.pathTileX[0] = arg2;
		if (!arg0) {
			this.pathTileZ[0] = arg3;
			this.x = this.pathTileX[0] * 128 + this.size * 64;
			this.z = this.pathTileZ[0] * 128 + this.size * 64;
		}
	}

	@ObfuscatedName("x.a(ZIB)V")
	public final void moveAlongRoute(boolean arg0, int arg1, byte arg2) {
		int var4 = this.pathTileX[0];
		int var5 = this.pathTileZ[0];
		if (arg2 == 6) {
			boolean var7 = false;
		} else {
			this.field298 = 243;
		}
		if (arg1 == 0) {
			var4--;
			var5++;
		}
		if (arg1 == 1) {
			var5++;
		}
		if (arg1 == 2) {
			var4++;
			var5++;
		}
		if (arg1 == 3) {
			var4--;
		}
		if (arg1 == 4) {
			var4++;
		}
		if (arg1 == 5) {
			var4--;
			var5--;
		}
		if (arg1 == 6) {
			var5--;
		}
		if (arg1 == 7) {
			var4++;
			var5--;
		}
		if (this.primarySeqId != -1 && SeqType.instances[this.primarySeqId].priority <= 1) {
			this.primarySeqId = -1;
		}
		if (this.pathLength < 9) {
			this.pathLength++;
		}
		for (int var6 = this.pathLength; var6 > 0; var6--) {
			this.pathTileX[var6] = this.pathTileX[var6 - 1];
			this.pathTileZ[var6] = this.pathTileZ[var6 - 1];
			this.pathRunning[var6] = this.pathRunning[var6 - 1];
		}
		this.pathTileX[0] = var4;
		this.pathTileZ[0] = var5;
		this.pathRunning[0] = arg0;
	}

	@ObfuscatedName("x.b(Z)Z")
	public boolean isVisible(boolean arg0) {
		if (arg0) {
			throw new NullPointerException();
		}
		return false;
	}
}
