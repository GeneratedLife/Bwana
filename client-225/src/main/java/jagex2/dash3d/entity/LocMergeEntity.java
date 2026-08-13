package jagex2.dash3d.entity;

import deob.ObfuscatedName;
import jagex2.datastruct.Linkable;

@ObfuscatedName("mb")
public class LocMergeEntity extends Linkable {

	@ObfuscatedName("mb.e")
	public int field644 = 27808;

	@ObfuscatedName("mb.f")
	public int plane;

	@ObfuscatedName("mb.g")
	public int layer;

	@ObfuscatedName("mb.h")
	public int x;

	@ObfuscatedName("mb.i")
	public int z;

	@ObfuscatedName("mb.j")
	public int locIndex;

	@ObfuscatedName("mb.k")
	public int angle;

	@ObfuscatedName("mb.l")
	public int shape;

	@ObfuscatedName("mb.m")
	public int lastCycle;

	public LocMergeEntity(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		if (arg4 != 2) {
			this.field644 = -420;
		}
		this.plane = arg0;
		this.layer = arg8;
		this.x = arg7;
		this.z = arg2;
		this.locIndex = arg6;
		this.angle = arg1;
		this.shape = arg5;
		this.lastCycle = arg3;
	}
}
