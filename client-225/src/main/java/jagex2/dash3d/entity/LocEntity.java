package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.config.SeqType;
import jagex2.datastruct.Linkable;

@ObfuscatedName("nb")
public class LocEntity extends Linkable {

	@ObfuscatedName("nb.e")
	public int level;

	@ObfuscatedName("nb.f")
	public int type;

	@ObfuscatedName("nb.g")
	public int x;

	@ObfuscatedName("nb.h")
	public int z;

	@ObfuscatedName("nb.i")
	public int index;

	@ObfuscatedName("nb.j")
	public SeqType seq;

	@ObfuscatedName("nb.k")
	public int seqFrame;

	@ObfuscatedName("nb.l")
	public int seqCycle;

	public LocEntity(boolean arg0, int arg1, int arg2, int arg3, int arg4, SeqType arg5, int arg6, int arg7) {
		this.level = arg2;
		this.type = arg4;
		this.x = arg7;
		this.z = arg6;
		if (arg3 != 0) {
			throw new NullPointerException();
		}
		this.index = arg1;
		this.seq = arg5;
		if (arg0 && arg5.replayoff != -1) {
			this.seqFrame = (int) (Math.random() * (double) this.seq.frameCount);
			this.seqCycle = (int) (Math.random() * (double) this.seq.delay[this.seqFrame]);
		} else {
			this.seqFrame = -1;
			this.seqCycle = 0;
		}
	}
}
