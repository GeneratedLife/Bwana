package jagex2.dash3d.type;

import deob.ObfuscatedName;
import jagex2.datastruct.Linkable;

@ObfuscatedName("cb")
public class Ground extends Linkable {

	@ObfuscatedName("cb.e")
	public int level;

	@ObfuscatedName("cb.f")
	public int x;

	@ObfuscatedName("cb.g")
	public int z;

	@ObfuscatedName("cb.h")
	public int occludeLevel;

	@ObfuscatedName("cb.i")
	public TileUnderlay underlay;

	@ObfuscatedName("cb.j")
	public TileOverlay overlay;

	@ObfuscatedName("cb.k")
	public Wall wall;

	@ObfuscatedName("cb.l")
	public Decor decor;

	@ObfuscatedName("cb.m")
	public GroundDecor groundDecor;

	@ObfuscatedName("cb.n")
	public GroundObject groundObj;

	@ObfuscatedName("cb.o")
	public int locCount;

	@ObfuscatedName("cb.p")
	public Location[] locs = new Location[5];

	@ObfuscatedName("cb.q")
	public int[] locSpan = new int[5];

	@ObfuscatedName("cb.r")
	public int locSpans;

	@ObfuscatedName("cb.s")
	public int drawLevel;

	@ObfuscatedName("cb.t")
	public boolean visible;

	@ObfuscatedName("cb.u")
	public boolean update;

	@ObfuscatedName("cb.v")
	public boolean containsLocs;

	@ObfuscatedName("cb.w")
	public int checkLocSpans;

	@ObfuscatedName("cb.x")
	public int blockLocSpans;

	@ObfuscatedName("cb.y")
	public int inverseBlockLocSpans;

	@ObfuscatedName("cb.z")
	public int backWallTypes;

	@ObfuscatedName("cb.A")
	public Ground bridge;

	public Ground(int arg0, int arg1, int arg2) {
		this.occludeLevel = this.level = arg0;
		this.x = arg1;
		this.z = arg2;
	}
}
