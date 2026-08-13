package jagex2.config;

import deob.ObfuscatedName;
import jagex2.client.Client;
import jagex2.dash3d.AnimFrame;
import jagex2.dash3d.Model;
import jagex2.datastruct.JString;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Pix32;
import jagex2.graphics.PixFont;
import jagex2.io.JagFile;
import jagex2.io.Packet;

@ObfuscatedName("d")
public final class IfType {

	@ObfuscatedName("d.c")
	public static IfType[] list;

	@ObfuscatedName("d.d")
	public int[] linkObjType;

	@ObfuscatedName("d.e")
	public int[] linkObjNumber;

	@ObfuscatedName("d.f")
	public int animFrame;

	@ObfuscatedName("d.g")
	public int animCycle;

	@ObfuscatedName("d.h")
	public int id;

	@ObfuscatedName("d.i")
	public int layerId;

	@ObfuscatedName("d.j")
	public int type;

	@ObfuscatedName("d.k")
	public int buttonType;

	@ObfuscatedName("d.l")
	public int clientCode;

	@ObfuscatedName("d.m")
	public int width;

	@ObfuscatedName("d.n")
	public int height;

	@ObfuscatedName("d.o")
	public byte trans;

	@ObfuscatedName("d.p")
	public int x;

	@ObfuscatedName("d.q")
	public int y;

	@ObfuscatedName("d.r")
	public int[][] scripts;

	@ObfuscatedName("d.s")
	public int[] scriptComparator;

	@ObfuscatedName("d.t")
	public int[] scriptOperand;

	@ObfuscatedName("d.u")
	public int overLayerId;

	@ObfuscatedName("d.v")
	public int scrollHeight;

	@ObfuscatedName("d.w")
	public int scrollPos;

	@ObfuscatedName("d.x")
	public boolean hide;

	@ObfuscatedName("d.y")
	public int[] children;

	@ObfuscatedName("d.z")
	public int[] childX;

	@ObfuscatedName("d.ab")
	public int model1Id;

	@ObfuscatedName("d.bb")
	public int model2Type;

	@ObfuscatedName("d.cb")
	public int model2Id;

	@ObfuscatedName("d.db")
	public int modelAnim;

	@ObfuscatedName("d.eb")
	public int modelAnim2;

	@ObfuscatedName("d.fb")
	public int modelZoom;

	@ObfuscatedName("d.gb")
	public int modelXAn;

	@ObfuscatedName("d.hb")
	public int modelYAn;

	@ObfuscatedName("d.ib")
	public String targetVerb;

	@ObfuscatedName("d.jb")
	public String targetBase;

	@ObfuscatedName("d.kb")
	public int targetMask;

	@ObfuscatedName("d.lb")
	public String buttonText;

	@ObfuscatedName("d.mb")
	public static final LruCache modelCache = new LruCache(30);

	@ObfuscatedName("d.nb")
	public static LruCache spriteCache;

	@ObfuscatedName("d.I")
	public int marginY;

	@ObfuscatedName("d.T")
	public int colour;

	@ObfuscatedName("d.U")
	public int colour2;

	@ObfuscatedName("d.V")
	public int colourOver;

	@ObfuscatedName("d.W")
	public int colour2Over;

	@ObfuscatedName("d.Z")
	public int model1Type;

	@ObfuscatedName("d.B")
	public int field93;

	@ObfuscatedName("d.H")
	public int marginX;

	@ObfuscatedName("d.X")
	public Pix32 graphic;

	@ObfuscatedName("d.Y")
	public Pix32 graphic2;

	@ObfuscatedName("d.Q")
	public PixFont font;

	@ObfuscatedName("d.R")
	public String text;

	@ObfuscatedName("d.S")
	public String text2;

	@ObfuscatedName("d.N")
	public boolean fill;

	@ObfuscatedName("d.O")
	public boolean centre;

	@ObfuscatedName("d.P")
	public boolean shadow;

	@ObfuscatedName("d.C")
	public boolean field94;

	@ObfuscatedName("d.D")
	public boolean objSwap;

	@ObfuscatedName("d.E")
	public boolean objOps;

	@ObfuscatedName("d.F")
	public boolean objUse;

	@ObfuscatedName("d.G")
	public boolean objReplace;

	@ObfuscatedName("d.K")
	public int[] invBackgroundX;

	@ObfuscatedName("d.L")
	public int[] invBackgroundY;

	@ObfuscatedName("d.A")
	public int[] childY;

	@ObfuscatedName("d.J")
	public Pix32[] invBackground;

	@ObfuscatedName("d.M")
	public String[] iop;

	@ObfuscatedName("d.a(Lxb;Lxb;[Lkb;I)V")
	public static void init(JagFile arg0, JagFile arg1, PixFont[] arg2) {
		spriteCache = new LruCache(50000);
		Packet var4 = new Packet(arg0.read("data", null));
		int var5 = -1;
		int var6 = var4.g2();
		list = new IfType[var6];
		while (true) {
			IfType var8;
			do {
				if (var4.data >= var4.pos.length) {
					spriteCache = null;
					return;
				}
				int var7 = var4.g2();
				if (var7 == 65535) {
					var5 = var4.g2();
					var7 = var4.g2();
				}
				var8 = list[var7] = new IfType();
				var8.id = var7;
				var8.layerId = var5;
				var8.type = var4.g1();
				var8.buttonType = var4.g1();
				var8.clientCode = var4.g2();
				var8.width = var4.g2();
				var8.height = var4.g2();
				var8.trans = (byte) var4.g1();
				var8.overLayerId = var4.g1();
				if (var8.overLayerId == 0) {
					var8.overLayerId = -1;
				} else {
					var8.overLayerId = (var8.overLayerId - 1 << 8) + var4.g1();
				}
				int var9 = var4.g1();
				if (var9 > 0) {
					var8.scriptComparator = new int[var9];
					var8.scriptOperand = new int[var9];
					for (int var10 = 0; var10 < var9; var10++) {
						var8.scriptComparator[var10] = var4.g1();
						var8.scriptOperand[var10] = var4.g2();
					}
				}
				int var11 = var4.g1();
				if (var11 > 0) {
					var8.scripts = new int[var11][];
					for (int var12 = 0; var12 < var11; var12++) {
						int var13 = var4.g2();
						var8.scripts[var12] = new int[var13];
						for (int var14 = 0; var14 < var13; var14++) {
							var8.scripts[var12][var14] = var4.g2();
						}
					}
				}
				if (var8.type == 0) {
					var8.scrollHeight = var4.g2();
					var8.hide = var4.g1() == 1;
					int var15 = var4.g2();
					var8.children = new int[var15];
					var8.childX = new int[var15];
					var8.childY = new int[var15];
					for (int var16 = 0; var16 < var15; var16++) {
						var8.children[var16] = var4.g2();
						var8.childX[var16] = var4.g2b();
						var8.childY[var16] = var4.g2b();
					}
				}
				if (var8.type == 1) {
					var8.field93 = var4.g2();
					var8.field94 = var4.g1() == 1;
				}
				if (var8.type == 2) {
					var8.linkObjType = new int[var8.width * var8.height];
					var8.linkObjNumber = new int[var8.width * var8.height];
					var8.objSwap = var4.g1() == 1;
					var8.objOps = var4.g1() == 1;
					var8.objUse = var4.g1() == 1;
					var8.objReplace = var4.g1() == 1;
					var8.marginX = var4.g1();
					var8.marginY = var4.g1();
					var8.invBackgroundX = new int[20];
					var8.invBackgroundY = new int[20];
					var8.invBackground = new Pix32[20];
					for (int var17 = 0; var17 < 20; var17++) {
						int var18 = var4.g1();
						if (var18 == 1) {
							var8.invBackgroundX[var17] = var4.g2b();
							var8.invBackgroundY[var17] = var4.g2b();
							String var19 = var4.gjstr();
							if (arg1 != null && var19.length() > 0) {
								int var20 = var19.lastIndexOf(",");
								var8.invBackground[var17] = getSprite(Integer.parseInt(var19.substring(var20 + 1)), var19.substring(0, var20), arg1);
							}
						}
					}
					var8.iop = new String[5];
					for (int var21 = 0; var21 < 5; var21++) {
						var8.iop[var21] = var4.gjstr();
						if (var8.iop[var21].length() == 0) {
							var8.iop[var21] = null;
						}
					}
				}
				if (var8.type == 3) {
					var8.fill = var4.g1() == 1;
				}
				if (var8.type == 4 || var8.type == 1) {
					var8.centre = var4.g1() == 1;
					int var22 = var4.g1();
					if (arg2 != null) {
						var8.font = arg2[var22];
					}
					var8.shadow = var4.g1() == 1;
				}
				if (var8.type == 4) {
					var8.text = var4.gjstr();
					var8.text2 = var4.gjstr();
				}
				if (var8.type == 1 || var8.type == 3 || var8.type == 4) {
					var8.colour = var4.g4();
				}
				if (var8.type == 3 || var8.type == 4) {
					var8.colour2 = var4.g4();
					var8.colourOver = var4.g4();
					var8.colour2Over = var4.g4();
				}
				if (var8.type == 5) {
					String var23 = var4.gjstr();
					if (arg1 != null && var23.length() > 0) {
						int var24 = var23.lastIndexOf(",");
						var8.graphic = getSprite(Integer.parseInt(var23.substring(var24 + 1)), var23.substring(0, var24), arg1);
					}
					String var25 = var4.gjstr();
					if (arg1 != null && var25.length() > 0) {
						int var26 = var25.lastIndexOf(",");
						var8.graphic2 = getSprite(Integer.parseInt(var25.substring(var26 + 1)), var25.substring(0, var26), arg1);
					}
				}
				if (var8.type == 6) {
					int var27 = var4.g1();
					if (var27 != 0) {
						var8.model1Type = 1;
						var8.model1Id = (var27 - 1 << 8) + var4.g1();
					}
					int var28 = var4.g1();
					if (var28 != 0) {
						var8.model2Type = 1;
						var8.model2Id = (var28 - 1 << 8) + var4.g1();
					}
					int var29 = var4.g1();
					if (var29 == 0) {
						var8.modelAnim = -1;
					} else {
						var8.modelAnim = (var29 - 1 << 8) + var4.g1();
					}
					int var30 = var4.g1();
					if (var30 == 0) {
						var8.modelAnim2 = -1;
					} else {
						var8.modelAnim2 = (var30 - 1 << 8) + var4.g1();
					}
					var8.modelZoom = var4.g2();
					var8.modelXAn = var4.g2();
					var8.modelYAn = var4.g2();
				}
				if (var8.type == 7) {
					var8.linkObjType = new int[var8.width * var8.height];
					var8.linkObjNumber = new int[var8.width * var8.height];
					var8.centre = var4.g1() == 1;
					int var31 = var4.g1();
					if (arg2 != null) {
						var8.font = arg2[var31];
					}
					var8.shadow = var4.g1() == 1;
					var8.colour = var4.g4();
					var8.marginX = var4.g2b();
					var8.marginY = var4.g2b();
					var8.objOps = var4.g1() == 1;
					var8.iop = new String[5];
					for (int var32 = 0; var32 < 5; var32++) {
						var8.iop[var32] = var4.gjstr();
						if (var8.iop[var32].length() == 0) {
							var8.iop[var32] = null;
						}
					}
				}
				if (var8.buttonType == 2 || var8.type == 2) {
					var8.targetVerb = var4.gjstr();
					var8.targetBase = var4.gjstr();
					var8.targetMask = var4.g2();
				}
			} while (var8.buttonType != 1 && var8.buttonType != 4 && var8.buttonType != 5 && var8.buttonType != 6);
			var8.buttonText = var4.gjstr();
			if (var8.buttonText.length() == 0) {
				if (var8.buttonType == 1) {
					var8.buttonText = "Ok";
				}
				if (var8.buttonType == 4) {
					var8.buttonText = "Select";
				}
				if (var8.buttonType == 5) {
					var8.buttonText = "Select";
				}
				if (var8.buttonType == 6) {
					var8.buttonText = "Continue";
				}
			}
		}
	}

	@ObfuscatedName("d.a(IILeb;I)V")
	public static void cacheModel(int arg1, Model arg2, int arg3) {
		modelCache.clear();
		if (arg2 != null && arg3 != 4) {
			modelCache.put((long) ((arg3 << 16) + arg1), arg2);
		}
	}

	@ObfuscatedName("d.a(IZLjava/lang/String;Lxb;)Lib;")
	public static Pix32 getSprite(int arg0, String arg2, JagFile arg3) {
		long var4 = (JString.hashCode(arg2) << 8) + (long) arg0;
		Pix32 var6 = (Pix32) spriteCache.find(var4);
		if (var6 != null) {
			return var6;
		}
		try {
			Pix32 var7 = new Pix32(arg3, arg2, arg0);
			spriteCache.put(var4, var7);
			return var7;
		} catch (Exception var8) {
			return null;
		}
	}

	@ObfuscatedName("d.a(III)V")
	public void swapSlots(int arg0, int arg1) {
		int var3 = linkObjType[arg0];
		linkObjType[arg0] = linkObjType[arg1];
		linkObjType[arg1] = var3;
		int var4 = linkObjNumber[arg0];
		linkObjNumber[arg0] = linkObjNumber[arg1];
		linkObjNumber[arg1] = var4;
	}

	@ObfuscatedName("d.a(IIIZ)Leb;")
	public Model getTempModel(int arg0, int arg1, boolean arg3) {
		Model var5;
		if (arg3) {
			var5 = getModel(model2Type, model2Id);
		} else {
			var5 = getModel(model1Type, model1Id);
		}
		if (var5 == null) {
			return null;
		} else if (arg0 == -1 && arg1 == -1 && var5.faceColour == null) {
			return var5;
		} else {
			Model var6 = new Model(true, AnimFrame.animateTransparencies(arg0) & AnimFrame.animateTransparencies(arg1), var5, false);
			if (arg0 != -1 || arg1 != -1) {
				var6.prepareAnim();
			}
			if (arg0 != -1) {
				var6.animate(arg0);
			}
			if (arg1 != -1) {
				var6.animate(arg1);
			}
			var6.calculateNormals(64, 768, -50, -10, -50, true);
			return var6;
		}
	}

	@ObfuscatedName("d.a(II)Leb;")
	public Model getModel(int arg0, int arg1) {
		Model var3 = (Model) modelCache.find((long) ((arg0 << 16) + arg1));
		if (var3 != null) {
			return var3;
		}
		if (arg0 == 1) {
			var3 = Model.load(arg1);
		}
		if (arg0 == 2) {
			var3 = NpcType.list(arg1).getHead();
		}
		if (arg0 == 3) {
			var3 = Client.localPlayer.getHeadModel();
		}
		if (arg0 == 4) {
			var3 = ObjType.list(arg1).getModelUnlit(50);
		}
		if (arg0 == 5) {
			var3 = null;
		}
		if (var3 != null) {
			modelCache.put((long) ((arg0 << 16) + arg1), var3);
		}
		return var3;
	}
}
