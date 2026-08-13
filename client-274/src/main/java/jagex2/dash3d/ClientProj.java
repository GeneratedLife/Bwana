package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.SpotType;

@ObfuscatedName("db")
public final class ClientProj extends ModelSource {

	@ObfuscatedName("db.o")
	public final SpotType spotanim;

	@ObfuscatedName("db.p")
	public final int level;

	@ObfuscatedName("db.q")
	public final int srcX;

	@ObfuscatedName("db.r")
	public final int srcZ;

	@ObfuscatedName("db.s")
	public final int h1;

	@ObfuscatedName("db.t")
	public final int h2;

	@ObfuscatedName("db.u")
	public final int t1;

	@ObfuscatedName("db.v")
	public final int t2;

	@ObfuscatedName("db.w")
	public final int angle;

	@ObfuscatedName("db.x")
	public final int startpos;

	@ObfuscatedName("db.y")
	public final int target;

	@ObfuscatedName("db.z")
	public boolean mobile = false;

	@ObfuscatedName("db.A")
	public double x;

	@ObfuscatedName("db.B")
	public double z;

	@ObfuscatedName("db.C")
	public double y;

	@ObfuscatedName("db.D")
	public double velocityX;

	@ObfuscatedName("db.E")
	public double velocityZ;

	@ObfuscatedName("db.F")
	public double velocity;

	@ObfuscatedName("db.G")
	public double velocityY;

	@ObfuscatedName("db.H")
	public double accelerationY;

	@ObfuscatedName("db.I")
	public int aw;

	@ObfuscatedName("db.J")
	public int pitch;

	@ObfuscatedName("db.K")
	public int animFrame;

	@ObfuscatedName("db.L")
	public int animCycle;

	public ClientProj(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg9, int arg10, int arg11) {
		spotanim = SpotType.list[arg7];
		level = arg6;
		srcX = arg0;
		srcZ = arg11;
		h1 = arg9;
		t1 = arg5;
		t2 = arg3;
		angle = arg4;
		startpos = arg1;
		target = arg10;
		h2 = arg2;
		mobile = false;
	}

	@ObfuscatedName("db.a(IIIBI)V")
	public void setTarget(int arg0, int arg1, int arg2, int arg4) {
		if (!mobile) {
			double var6 = (double) (arg2 - srcX);
			double var8 = (double) (arg4 - srcZ);
			double var10 = Math.sqrt(var6 * var6 + var8 * var8);
			x = (double) srcX + var6 * (double) startpos / var10;
			z = (double) srcZ + var8 * (double) startpos / var10;
			y = h1;
		}
		double var12 = (double) (t2 + 1 - arg0);
		velocityX = ((double) arg2 - x) / var12;
		velocityZ = ((double) arg4 - z) / var12;
		velocity = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
		if (!mobile) {
			velocityY = -velocity * Math.tan((double) angle * 0.02454369D);
		}
		accelerationY = ((double) arg1 - y - velocityY * var12) * 2.0D / (var12 * var12);
	}

	@ObfuscatedName("db.a(IZ)V")
	public void move(int arg0) {
		mobile = true;
		x += velocityX * (double) arg0;
		z += velocityZ * (double) arg0;
		y += velocityY * (double) arg0 + accelerationY * 0.5D * (double) arg0 * (double) arg0;
		velocityY += accelerationY * (double) arg0;
		aw = (int) (Math.atan2(velocityX, velocityZ) * 325.949D) + 1024 & 0x7FF;
		pitch = (int) (Math.atan2(velocityY, velocity) * 325.949D) & 0x7FF;
		if (spotanim.seq == null) {
			return;
		}
		animCycle += arg0;
		while (animCycle > spotanim.seq.getDelay(animFrame)) {
			animCycle -= spotanim.seq.getDelay(animFrame) + 1;
			animFrame++;
			if (animFrame >= spotanim.seq.numFrames) {
				animFrame = 0;
			}
		}
	}

	@ObfuscatedName("db.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		Model var2 = spotanim.getTempModel2();
		if (var2 == null) {
			return null;
		}
		int var3 = -1;
		if (spotanim.seq != null) {
			var3 = spotanim.seq.frames[animFrame];
		}
		Model var4 = new Model(true, AnimFrame.animateTransparencies(var3), var2, false);
		if (var3 != -1) {
			var4.prepareAnim();
			var4.animate(var3);
			var4.labelFaces = null;
			var4.labelVertices = null;
		}
		if (spotanim.resizeh != 128 || spotanim.resizev != 128) {
			var4.resize(spotanim.resizeh, spotanim.resizeh, spotanim.resizev);
		}
		var4.rotateXAxis(pitch);
		var4.calculateNormals(spotanim.ambient + 64, spotanim.contrast + 850, -30, -50, -30, true);
		return var4;
	}
}
