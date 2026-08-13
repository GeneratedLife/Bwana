package jagex2.dash3d.type;

import deob.ObfuscatedName;

@ObfuscatedName("o")
public class TileUnderlay {

	@ObfuscatedName("o.a")
	public int southwestColor;

	@ObfuscatedName("o.b")
	public int southeastColor;

	@ObfuscatedName("o.c")
	public int northeastColor;

	@ObfuscatedName("o.d")
	public int northwestColor;

	@ObfuscatedName("o.e")
	public int textureId;

	@ObfuscatedName("o.f")
	public boolean flat = true;

	@ObfuscatedName("o.g")
	public int rgb;

	public TileUnderlay(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, boolean arg6) {
		this.southwestColor = arg0;
		this.southeastColor = arg1;
		this.northeastColor = arg2;
		this.northwestColor = arg3;
		this.textureId = arg4;
		this.rgb = arg5;
		this.flat = arg6;
	}
}
