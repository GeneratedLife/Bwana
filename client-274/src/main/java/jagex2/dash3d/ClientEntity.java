package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.SeqType;

@ObfuscatedName("y")
public class ClientEntity extends ModelSource {

	@ObfuscatedName("y.n")
	public int x;

	@ObfuscatedName("y.o")
	public int z;

	@ObfuscatedName("y.p")
	public int yaw;

	@ObfuscatedName("y.q")
	public boolean reachforward = false;

	@ObfuscatedName("y.r")
	public int size = 1;

	@ObfuscatedName("y.s")
	public int readyanim = -1;

	@ObfuscatedName("y.t")
	public int turnanim = -1;

	@ObfuscatedName("y.u")
	public int walkanim = -1;

	@ObfuscatedName("y.v")
	public int walkanim_b = -1;

	@ObfuscatedName("y.w")
	public int walkanim_r = -1;

	@ObfuscatedName("y.x")
	public int walkanim_l = -1;

	@ObfuscatedName("y.y")
	public int runanim = -1;

	@ObfuscatedName("y.z")
	public String chatMessage;

	@ObfuscatedName("y.ab")
	public int exactEndX;

	@ObfuscatedName("y.bb")
	public int exactStartZ;

	@ObfuscatedName("y.cb")
	public int exactEndZ;

	@ObfuscatedName("y.db")
	public int exactMoveStart;

	@ObfuscatedName("y.eb")
	public int exactMoveEnd;

	@ObfuscatedName("y.fb")
	public int exactMoveFacing;

	@ObfuscatedName("y.gb")
	public int cycle;

	@ObfuscatedName("y.hb")
	public int height = 200;

	@ObfuscatedName("y.ib")
	public int dstYaw;

	@ObfuscatedName("y.jb")
	public int turnspeed = 32;

	@ObfuscatedName("y.kb")
	public int routeLength;

	@ObfuscatedName("y.lb")
	public final int[] routeX = new int[10];

	@ObfuscatedName("y.mb")
	public final int[] routeZ = new int[10];

	@ObfuscatedName("y.nb")
	public final boolean[] routeRun = new boolean[10];

	@ObfuscatedName("y.ob")
	public int animDelayMove;

	@ObfuscatedName("y.pb")
	public int preanimRouteLength;

	@ObfuscatedName("y.A")
	public int chatTimer = 100;

	@ObfuscatedName("y.D")
	public final int[] damageValues = new int[4];

	@ObfuscatedName("y.E")
	public final int[] damageTypes = new int[4];

	@ObfuscatedName("y.F")
	public final int[] damageCycles = new int[4];

	@ObfuscatedName("y.G")
	public int combatCycle = -1000;

	@ObfuscatedName("y.J")
	public int faceEntity = -1;

	@ObfuscatedName("y.M")
	public int secondaryAnim = -1;

	@ObfuscatedName("y.P")
	public int primaryAnim = -1;

	@ObfuscatedName("y.U")
	public int spotanimId = -1;

	@ObfuscatedName("y.B")
	public int chatColour;

	@ObfuscatedName("y.C")
	public int chatEffect;

	@ObfuscatedName("y.H")
	public int health;

	@ObfuscatedName("y.I")
	public int totalHealth;

	@ObfuscatedName("y.K")
	public int faceSquareX;

	@ObfuscatedName("y.L")
	public int faceSquareZ;

	@ObfuscatedName("y.N")
	public int secondaryAnimFrame;

	@ObfuscatedName("y.O")
	public int secondaryAnimCycle;

	@ObfuscatedName("y.Q")
	public int primaryAnimFrame;

	@ObfuscatedName("y.R")
	public int primaryAnimCycle;

	@ObfuscatedName("y.S")
	public int primaryAnimDelay;

	@ObfuscatedName("y.T")
	public int primaryAnimLoop;

	@ObfuscatedName("y.V")
	public int spotanimFrame;

	@ObfuscatedName("y.W")
	public int spotanimCycle;

	@ObfuscatedName("y.X")
	public int spotanimLastCycle;

	@ObfuscatedName("y.Y")
	public int spotanimHeight;

	@ObfuscatedName("y.Z")
	public int exactStartX;

	@ObfuscatedName("y.a(IZZI)V")
	public final void teleport(int arg0, boolean arg1, int arg3) {
		if (primaryAnim != -1 && SeqType.list[primaryAnim].postanim_move == 1) {
			primaryAnim = -1;
		}
		if (!arg1) {
			int var5 = arg0 - routeX[0];
			int var6 = arg3 - routeZ[0];
			if (var5 >= -8 && var5 <= 8 && var6 >= -8 && var6 <= 8) {
				if (routeLength < 9) {
					routeLength++;
				}
				for (int var7 = routeLength; var7 > 0; var7--) {
					routeX[var7] = routeX[var7 - 1];
					routeZ[var7] = routeZ[var7 - 1];
					routeRun[var7] = routeRun[var7 - 1];
				}
				routeX[0] = arg0;
				routeZ[0] = arg3;
				routeRun[0] = false;
				return;
			}
		}
		routeLength = 0;
		preanimRouteLength = 0;
		animDelayMove = 0;
		routeX[0] = arg0;
		routeZ[0] = arg3;
		x = routeX[0] * 128 + size * 64;
		z = routeZ[0] * 128 + size * 64;
	}

	@ObfuscatedName("y.a(ZIZ)V")
	public final void moveCode(int arg1, boolean arg2) {
		int var4 = routeX[0];
		int var5 = routeZ[0];
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
		if (primaryAnim != -1 && SeqType.list[primaryAnim].postanim_move == 1) {
			primaryAnim = -1;
		}
		if (routeLength < 9) {
			routeLength++;
		}
		for (int var6 = routeLength; var6 > 0; var6--) {
			routeX[var6] = routeX[var6 - 1];
			routeZ[var6] = routeZ[var6 - 1];
			routeRun[var6] = routeRun[var6 - 1];
		}
		routeX[0] = var4;
		routeZ[0] = var5;
		routeRun[0] = arg2;
	}

	@ObfuscatedName("y.b(Z)V")
	public final void abortRoute() {
		routeLength = 0;
		preanimRouteLength = 0;
	}

	@ObfuscatedName("y.a(I)Z")
	public boolean isReady() {
		return false;
	}

	@ObfuscatedName("y.a(IBII)V")
	public final void addHitmark(int arg0, int arg2, int arg3) {
		for (int var5 = 0; var5 < 4; var5++) {
			if (damageCycles[var5] <= arg0) {
				damageValues[var5] = arg2;
				damageTypes[var5] = arg3;
				damageCycles[var5] = arg0 + 70;
				return;
			}
		}
	}
}
