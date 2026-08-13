package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;

@ObfuscatedName("ab")
public class ProjectileEntity extends Entity {

	@ObfuscatedName("ab.e")
	public int field378;

	@ObfuscatedName("ab.f")
	public int field379 = -159;

	@ObfuscatedName("ab.g")
	public SpotAnimType spotanim;

	@ObfuscatedName("ab.h")
	public int level;

	@ObfuscatedName("ab.i")
	public int srcX;

	@ObfuscatedName("ab.j")
	public int srcZ;

	@ObfuscatedName("ab.k")
	public int srcY;

	@ObfuscatedName("ab.l")
	public int offsetY;

	@ObfuscatedName("ab.m")
	public int startCycle;

	@ObfuscatedName("ab.n")
	public int lastCycle;

	@ObfuscatedName("ab.o")
	public int peakPitch;

	@ObfuscatedName("ab.p")
	public int arc;

	@ObfuscatedName("ab.q")
	public int target;

	@ObfuscatedName("ab.r")
	public boolean mobile = false;

	@ObfuscatedName("ab.s")
	public double x;

	@ObfuscatedName("ab.t")
	public double z;

	@ObfuscatedName("ab.u")
	public double y;

	@ObfuscatedName("ab.v")
	public double velocityX;

	@ObfuscatedName("ab.w")
	public double velocityZ;

	@ObfuscatedName("ab.x")
	public double velocity;

	@ObfuscatedName("ab.y")
	public double velocityY;

	@ObfuscatedName("ab.z")
	public double accelerationY;

	@ObfuscatedName("ab.A")
	public int yaw;

	@ObfuscatedName("ab.B")
	public int pitch;

	@ObfuscatedName("ab.C")
	public int seqFrame;

	@ObfuscatedName("ab.D")
	public int seqCycle;

	public ProjectileEntity(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8, int arg9, int arg10, int arg11) {
		this.spotanim = SpotAnimType.instances[arg10];
		if (arg8 != 0) {
			throw new NullPointerException();
		}
		this.level = arg4;
		this.srcX = arg11;
		this.srcZ = arg2;
		this.srcY = arg9;
		this.startCycle = arg6;
		this.lastCycle = arg3;
		this.peakPitch = arg1;
		this.arc = arg7;
		this.target = arg5;
		this.offsetY = arg0;
		this.mobile = false;
	}

	@ObfuscatedName("ab.a(IIIII)V")
	public final void updateVelocity(int arg0, int arg1, int arg2, int arg3, int arg4) {
		double var6;
		if (!this.mobile) {
			var6 = arg2 - this.srcX;
			double var8 = (double) (arg1 - this.srcZ);
			double var10 = Math.sqrt(var6 * var6 + var8 * var8);
			this.x = (double) this.srcX + var6 * (double) this.arc / var10;
			this.z = (double) this.srcZ + var8 * (double) this.arc / var10;
			this.y = this.srcY;
		}
		var6 = this.lastCycle + 1 - arg4;
		this.velocityX = ((double) arg2 - this.x) / var6;
		this.velocityZ = ((double) arg1 - this.z) / var6;
		this.velocity = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		if (!this.mobile) {
			this.velocityY = -this.velocity * Math.tan((double) this.peakPitch * 0.02454369D);
		}
		this.accelerationY = ((double) arg0 - this.y - this.velocityY * var6) * 2.0D / (var6 * var6);
		if (arg3 < 0) {
			;
		}
	}

	@ObfuscatedName("ab.a(BI)V")
	public final void update(byte arg0, int arg1) {
		if (arg0 != -30) {
			this.field379 = -454;
		}
		this.mobile = true;
		this.x += this.velocityX * (double) arg1;
		this.z += this.velocityZ * (double) arg1;
		this.y += this.velocityY * (double) arg1 + this.accelerationY * 0.5D * (double) arg1 * (double) arg1;
		this.velocityY += this.accelerationY * (double) arg1;
		this.yaw = (int) (Math.atan2(this.velocityX, this.velocityZ) * 325.949D) + 1024 & 0x7FF;
		this.pitch = (int) (Math.atan2(this.velocityY, this.velocity) * 325.949D) & 0x7FF;
		if (this.spotanim.seq == null) {
			return;
		}
		this.seqCycle += arg1;
		while (this.seqCycle > this.spotanim.seq.delay[this.seqFrame]) {
			this.seqCycle -= this.spotanim.seq.delay[this.seqFrame] + 1;
			this.seqFrame++;
			if (this.seqFrame >= this.spotanim.seq.frameCount) {
				this.seqFrame = 0;
			}
		}
	}

	@ObfuscatedName("ab.a(Z)Leb;")
	public final Model draw(boolean arg0) {
		Model var2 = this.spotanim.getModel();
		Model var3 = new Model(var2, true, !this.spotanim.animHasAlpha, this.field378, false);
		if (!arg0) {
			for (int var4 = 1; var4 > 0; var4++) {
			}
		}
		if (this.spotanim.seq != null) {
			var3.createLabelReferences(4);
			var3.applyTransform(-16599, this.spotanim.seq.frames[this.seqFrame]);
			var3.labelFaces = null;
			var3.labelVertices = null;
		}
		if (this.spotanim.resizeh != 128 || this.spotanim.resizev != 128) {
			var3.scale(this.spotanim.resizeh, 2, this.spotanim.resizev, this.spotanim.resizeh);
		}
		var3.rotateX((byte) 7, this.pitch);
		var3.calculateNormals(this.spotanim.ambient + 64, this.spotanim.contrast + 850, -30, -50, -30, true);
		return var3;
	}
}
