package jagex2.config;

import deob.ObfuscatedName;
import jagex2.datastruct.JString;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.graphics.Pix32;
import jagex2.graphics.PixFont;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("hc")
public class Component {

	@ObfuscatedName("hc.a")
	public static int field957;

	@ObfuscatedName("hc.b")
	public static boolean field958;

	@ObfuscatedName("hc.c")
	public static Component[] instances;

	@ObfuscatedName("hc.d")
	public int[] invSlotObjId;

	@ObfuscatedName("hc.e")
	public int[] invSlotObjCount;

	@ObfuscatedName("hc.f")
	public int seqFrame;

	@ObfuscatedName("hc.g")
	public int seqCycle;

	@ObfuscatedName("hc.h")
	public int id;

	@ObfuscatedName("hc.i")
	public int layer;

	@ObfuscatedName("hc.j")
	public int type;

	@ObfuscatedName("hc.k")
	public int buttonType;

	@ObfuscatedName("hc.l")
	public int clientCode;

	@ObfuscatedName("hc.m")
	public int width;

	@ObfuscatedName("hc.n")
	public int height;

	@ObfuscatedName("hc.o")
	public int x;

	@ObfuscatedName("hc.p")
	public int y;

	@ObfuscatedName("hc.q")
	public int[][] scripts;

	@ObfuscatedName("hc.r")
	public int[] scriptComparator;

	@ObfuscatedName("hc.s")
	public int[] scriptOperand;

	@ObfuscatedName("hc.t")
	public int overLayer;

	@ObfuscatedName("hc.u")
	public int scroll;

	@ObfuscatedName("hc.v")
	public int scrollPosition;

	@ObfuscatedName("hc.w")
	public boolean hide;

	@ObfuscatedName("hc.x")
	public int[] childId;

	@ObfuscatedName("hc.y")
	public int[] childX;

	@ObfuscatedName("hc.z")
	public int[] childY;

	@ObfuscatedName("hc.ab")
	public int zoom;

	@ObfuscatedName("hc.bb")
	public int xan;

	@ObfuscatedName("hc.cb")
	public int yan;

	@ObfuscatedName("hc.db")
	public String actionVerb;

	@ObfuscatedName("hc.eb")
	public String action;

	@ObfuscatedName("hc.fb")
	public int actionTarget;

	@ObfuscatedName("hc.gb")
	public String option;

	@ObfuscatedName("hc.hb")
	public static LruCache imageCache;

	@ObfuscatedName("hc.ib")
	public static LruCache modelCache;

	@ObfuscatedName("hc.R")
	public int colour;

	@ObfuscatedName("hc.S")
	public int activeColour;

	@ObfuscatedName("hc.T")
	public int overColour;

	@ObfuscatedName("hc.Y")
	public int anim;

	@ObfuscatedName("hc.Z")
	public int activeAnim;

	@ObfuscatedName("hc.A")
	public int unusedShort1;

	@ObfuscatedName("hc.F")
	public int marginX;

	@ObfuscatedName("hc.G")
	public int marginY;

	@ObfuscatedName("hc.W")
	public Model model;

	@ObfuscatedName("hc.X")
	public Model activeModel;

	@ObfuscatedName("hc.U")
	public Pix32 graphic;

	@ObfuscatedName("hc.V")
	public Pix32 activeGraphic;

	@ObfuscatedName("hc.O")
	public PixFont font;

	@ObfuscatedName("hc.P")
	public String text;

	@ObfuscatedName("hc.Q")
	public String activeText;

	@ObfuscatedName("hc.B")
	public boolean unusedBoolean1;

	@ObfuscatedName("hc.C")
	public boolean draggable;

	@ObfuscatedName("hc.D")
	public boolean interactable;

	@ObfuscatedName("hc.E")
	public boolean usable;

	@ObfuscatedName("hc.L")
	public boolean fill;

	@ObfuscatedName("hc.M")
	public boolean center;

	@ObfuscatedName("hc.N")
	public boolean shadowed;

	@ObfuscatedName("hc.I")
	public int[] invSlotOffsetX;

	@ObfuscatedName("hc.J")
	public int[] invSlotOffsetY;

	@ObfuscatedName("hc.H")
	public Pix32[] invSlotSprite;

	@ObfuscatedName("hc.K")
	public String[] iops;

	@ObfuscatedName("hc.a(Lub;[Ljb;ILub;)V")
	public static void unpack(Jagfile arg0, PixFont[] arg1, int arg2, Jagfile arg3) {
		int var15 = 17 / arg2;
		imageCache = new LruCache((byte) 0, 50000);
		modelCache = new LruCache((byte) 0, 50000);
		Packet var4 = new Packet(363, arg3.read("data", null, (byte) 2));
		int var5 = -1;
		int var6 = var4.g2();
		instances = new Component[var6];
		while (true) {
			Component var8;
			do {
				if (var4.pos >= var4.data.length) {
					imageCache = null;
					modelCache = null;
					return;
				}
				int var7 = var4.g2();
				if (var7 == 65535) {
					var5 = var4.g2();
					var7 = var4.g2();
				}
				var8 = instances[var7] = new Component();
				var8.id = var7;
				var8.layer = var5;
				var8.type = var4.g1();
				var8.buttonType = var4.g1();
				var8.clientCode = var4.g2();
				var8.width = var4.g2();
				var8.height = var4.g2();
				var8.overLayer = var4.g1();
				if (var8.overLayer == 0) {
					var8.overLayer = -1;
				} else {
					var8.overLayer = (var8.overLayer - 1 << 8) + var4.g1();
				}
				int var9 = var4.g1();
				int var10;
				if (var9 > 0) {
					var8.scriptComparator = new int[var9];
					var8.scriptOperand = new int[var9];
					for (var10 = 0; var10 < var9; var10++) {
						var8.scriptComparator[var10] = var4.g1();
						var8.scriptOperand[var10] = var4.g2();
					}
				}
				var10 = var4.g1();
				int var11;
				int var12;
				if (var10 > 0) {
					var8.scripts = new int[var10][];
					for (var11 = 0; var11 < var10; var11++) {
						var12 = var4.g2();
						var8.scripts[var11] = new int[var12];
						for (int var13 = 0; var13 < var12; var13++) {
							var8.scripts[var11][var13] = var4.g2();
						}
					}
				}
				if (var8.type == 0) {
					var8.scroll = var4.g2();
					var8.hide = var4.g1() == 1;
					var11 = var4.g1();
					var8.childId = new int[var11];
					var8.childX = new int[var11];
					var8.childY = new int[var11];
					for (var12 = 0; var12 < var11; var12++) {
						var8.childId[var12] = var4.g2();
						var8.childX[var12] = var4.g2b();
						var8.childY[var12] = var4.g2b();
					}
				}
				if (var8.type == 1) {
					var8.unusedShort1 = var4.g2();
					var8.unusedBoolean1 = var4.g1() == 1;
				}
				if (var8.type == 2) {
					var8.invSlotObjId = new int[var8.width * var8.height];
					var8.invSlotObjCount = new int[var8.width * var8.height];
					var8.draggable = var4.g1() == 1;
					var8.interactable = var4.g1() == 1;
					var8.usable = var4.g1() == 1;
					var8.marginX = var4.g1();
					var8.marginY = var4.g1();
					var8.invSlotOffsetX = new int[20];
					var8.invSlotOffsetY = new int[20];
					var8.invSlotSprite = new Pix32[20];
					for (var11 = 0; var11 < 20; var11++) {
						var12 = var4.g1();
						if (var12 == 1) {
							var8.invSlotOffsetX[var11] = var4.g2b();
							var8.invSlotOffsetY[var11] = var4.g2b();
							String var17 = var4.gjstr();
							if (arg0 != null && var17.length() > 0) {
								int var14 = var17.lastIndexOf(",");
								var8.invSlotSprite[var11] = getImage(arg0, Integer.parseInt(var17.substring(var14 + 1)), var17.substring(0, var14), -36068);
							}
						}
					}
					var8.iops = new String[5];
					for (var12 = 0; var12 < 5; var12++) {
						var8.iops[var12] = var4.gjstr();
						if (var8.iops[var12].length() == 0) {
							var8.iops[var12] = null;
						}
					}
				}
				if (var8.type == 3) {
					var8.fill = var4.g1() == 1;
				}
				if (var8.type == 4 || var8.type == 1) {
					var8.center = var4.g1() == 1;
					var11 = var4.g1();
					if (arg1 != null) {
						var8.font = arg1[var11];
					}
					var8.shadowed = var4.g1() == 1;
				}
				if (var8.type == 4) {
					var8.text = var4.gjstr();
					var8.activeText = var4.gjstr();
				}
				if (var8.type == 1 || var8.type == 3 || var8.type == 4) {
					var8.colour = var4.g4();
				}
				if (var8.type == 3 || var8.type == 4) {
					var8.activeColour = var4.g4();
					var8.overColour = var4.g4();
				}
				if (var8.type == 5) {
					String var16 = var4.gjstr();
					if (arg0 != null && var16.length() > 0) {
						var12 = var16.lastIndexOf(",");
						var8.graphic = getImage(arg0, Integer.parseInt(var16.substring(var12 + 1)), var16.substring(0, var12), -36068);
					}
					var16 = var4.gjstr();
					if (arg0 != null && var16.length() > 0) {
						var12 = var16.lastIndexOf(",");
						var8.activeGraphic = getImage(arg0, Integer.parseInt(var16.substring(var12 + 1)), var16.substring(0, var12), -36068);
					}
				}
				if (var8.type == 6) {
					var7 = var4.g1();
					if (var7 != 0) {
						var8.model = getModel(4, (var7 - 1 << 8) + var4.g1());
					}
					var7 = var4.g1();
					if (var7 != 0) {
						var8.activeModel = getModel(4, (var7 - 1 << 8) + var4.g1());
					}
					var7 = var4.g1();
					if (var7 == 0) {
						var8.anim = -1;
					} else {
						var8.anim = (var7 - 1 << 8) + var4.g1();
					}
					var7 = var4.g1();
					if (var7 == 0) {
						var8.activeAnim = -1;
					} else {
						var8.activeAnim = (var7 - 1 << 8) + var4.g1();
					}
					var8.zoom = var4.g2();
					var8.xan = var4.g2();
					var8.yan = var4.g2();
				}
				if (var8.type == 7) {
					var8.invSlotObjId = new int[var8.width * var8.height];
					var8.invSlotObjCount = new int[var8.width * var8.height];
					var8.center = var4.g1() == 1;
					var11 = var4.g1();
					if (arg1 != null) {
						var8.font = arg1[var11];
					}
					var8.shadowed = var4.g1() == 1;
					var8.colour = var4.g4();
					var8.marginX = var4.g2b();
					var8.marginY = var4.g2b();
					var8.interactable = var4.g1() == 1;
					var8.iops = new String[5];
					for (var12 = 0; var12 < 5; var12++) {
						var8.iops[var12] = var4.gjstr();
						if (var8.iops[var12].length() == 0) {
							var8.iops[var12] = null;
						}
					}
				}
				if (var8.buttonType == 2 || var8.type == 2) {
					var8.actionVerb = var4.gjstr();
					var8.action = var4.gjstr();
					var8.actionTarget = var4.g2();
				}
			} while (var8.buttonType != 1 && var8.buttonType != 4 && var8.buttonType != 5 && var8.buttonType != 6);
			var8.option = var4.gjstr();
			if (var8.option.length() == 0) {
				if (var8.buttonType == 1) {
					var8.option = "Ok";
				}
				if (var8.buttonType == 4) {
					var8.option = "Select";
				}
				if (var8.buttonType == 5) {
					var8.option = "Select";
				}
				if (var8.buttonType == 6) {
					var8.option = "Continue";
				}
			}
		}
	}

	@ObfuscatedName("hc.a(IIZ)Leb;")
	public Model getModel(int arg0, int arg1, boolean arg2) {
		Model var4 = this.model;
		if (arg2) {
			var4 = this.activeModel;
		}
		if (var4 == null) {
			return null;
		} else if (arg0 == -1 && arg1 == -1 && var4.faceColor == null) {
			return var4;
		} else {
			Model var5 = new Model(var4, true, true, field957, false);
			if (arg0 != -1 || arg1 != -1) {
				var5.createLabelReferences(4);
			}
			if (arg0 != -1) {
				var5.applyTransform(-16599, arg0);
			}
			if (arg1 != -1) {
				var5.applyTransform(-16599, arg1);
			}
			var5.calculateNormals(64, 768, -50, -10, -50, true);
			return var5;
		}
	}

	@ObfuscatedName("hc.a(Lub;ILjava/lang/String;I)Lhb;")
	public static Pix32 getImage(Jagfile arg0, int arg1, String arg2, int arg3) {
		long var4 = (JString.hashCode(0, arg2) << 8) + (long) arg1;
		Pix32 var6 = (Pix32) imageCache.get(var4);
		if (arg3 != -36068) {
			throw new NullPointerException();
		} else if (var6 == null) {
			try {
				var6 = new Pix32(arg0, arg2, arg1);
				imageCache.put(6, var4, var6);
				return var6;
			} catch (Exception var7) {
				return null;
			}
		} else {
			return var6;
		}
	}

	@ObfuscatedName("hc.a(II)Leb;")
	public static Model getModel(int arg0, int arg1) {
		Model var2 = (Model) modelCache.get((long) arg1);
		if (arg0 != 4) {
			field958 = !field958;
		}
		if (var2 == null) {
			var2 = new Model(false, arg1);
			modelCache.put(6, (long) arg1, var2);
			return var2;
		} else {
			return var2;
		}
	}
}
