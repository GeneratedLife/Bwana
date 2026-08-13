package jagex2.dash3d;

import deob.ObfuscatedName;
import jagex2.client.Client;
import jagex2.config.LocType;
import jagex2.config.SeqType;

@ObfuscatedName("bb")
public final class ClientLocAnim extends ModelSource {

	@ObfuscatedName("bb.o")
	public final int id;

	@ObfuscatedName("bb.p")
	public final int shape;

	@ObfuscatedName("bb.q")
	public final int angle;

	@ObfuscatedName("bb.r")
	public final int heightSW;

	@ObfuscatedName("bb.s")
	public final int heightSE;

	@ObfuscatedName("bb.t")
	public final int heightNE;

	@ObfuscatedName("bb.u")
	public final int heightNW;

	@ObfuscatedName("bb.v")
	public SeqType seq;

	@ObfuscatedName("bb.w")
	public int seqFrame;

	@ObfuscatedName("bb.x")
	public int seqCycle;

	public ClientLocAnim(int arg0, int arg1, int arg2, int arg3, int arg4, boolean arg6, int arg7, int arg8, int arg9) {
		id = arg3;
		shape = arg0;
		angle = arg1;
		heightSW = arg9;
		heightSE = arg7;
		heightNE = arg4;
		heightNW = arg8;
		seq = SeqType.list[arg2];
		seqFrame = 0;
		seqCycle = Client.loopCycle;
		if (arg6 && seq.loops != -1) {
			seqFrame = (int) (Math.random() * (double) seq.numFrames);
			seqCycle -= (int) (Math.random() * (double) seq.getDelay(seqFrame));
		}
	}

	@ObfuscatedName("bb.a(Z)Leb;")
	@Override
	public Model getTempModel() {
		if (seq != null) {
			int var2 = Client.loopCycle - seqCycle;
			if (var2 > 100 && seq.loops > 0) {
				var2 = 100;
			}
			label42: {
				do {
					do {
						if (var2 <= seq.getDelay(seqFrame)) {
							break label42;
						}
						var2 -= seq.getDelay(seqFrame);
						seqFrame++;
					} while (seqFrame < seq.numFrames);
					seqFrame -= seq.loops;
				} while (seqFrame >= 0 && seqFrame < seq.numFrames);
				seq = null;
			}
			seqCycle = Client.loopCycle - var2;
		}
		int var3 = -1;
		if (seq != null) {
			var3 = seq.frames[seqFrame];
		}
		LocType var4 = LocType.list(id);
		return var4.getModel(shape, angle, heightSW, heightSE, heightNE, heightNW, var3);
	}
}
