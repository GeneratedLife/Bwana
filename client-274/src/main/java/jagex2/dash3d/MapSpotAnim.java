package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.config.SpotType;

@ObfuscatedName("fb")
public final class MapSpotAnim extends ModelSource {

	@ObfuscatedName("fb.m")
	public final SpotType type;

	@ObfuscatedName("fb.n")
	public final int startCycle;

	@ObfuscatedName("fb.o")
	public final int level;

	@ObfuscatedName("fb.p")
	public final int x;

	@ObfuscatedName("fb.q")
	public final int z;

	@ObfuscatedName("fb.r")
	public final int y;

	@ObfuscatedName("fb.s")
	public int animFrame;

	@ObfuscatedName("fb.t")
	public int animCycle;

	@ObfuscatedName("fb.u")
	public boolean animComplete = false;

	public MapSpotAnim(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		type = SpotType.list[arg6];
		level = arg3;
		x = arg2;
		z = arg1;
		y = arg5;
		startCycle = arg7 + arg0;
		animComplete = false;
	}

	@ObfuscatedName("fb.a(II)V")
	public void update(int arg1) {
		animCycle += arg1;
		while (true) {
			do {
				do {
					if (animCycle <= type.seq.getDelay(animFrame)) {
						return;
					}
					animCycle -= type.seq.getDelay(animFrame) + 1;
					animFrame++;
				} while (animFrame < type.seq.numFrames);
			} while (animFrame >= 0 && animFrame < type.seq.numFrames);
			animFrame = 0;
			animComplete = true;
		}
	}

	@ObfuscatedName("fb.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		Model var2 = type.getTempModel2();
		if (var2 == null) {
			return null;
		} else {
			int var3 = type.seq.frames[animFrame];
			Model var4 = new Model(true, AnimFrame.animateTransparencies(var3), var2, false);
			if (!animComplete) {
				var4.prepareAnim();
				var4.animate(var3);
				var4.labelFaces = null;
				var4.labelVertices = null;
			}
			if (type.resizeh != 128 || type.resizev != 128) {
				var4.resize(type.resizeh, type.resizeh, type.resizev);
			}
			if (type.angle != 0) {
				if (type.angle == 90) {
					var4.rotate90();
				}
				if (type.angle == 180) {
					var4.rotate90();
					var4.rotate90();
				}
				if (type.angle == 270) {
					var4.rotate90();
					var4.rotate90();
					var4.rotate90();
				}
			}
			var4.calculateNormals(type.ambient + 64, type.contrast + 850, -30, -50, -30, true);
			return var4;
		}
	}
}
