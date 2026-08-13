package jagex2.graphics;

import deob.ObfuscatedName;
import jagex2.datastruct.DoublyLinkable;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

@ObfuscatedName("eb")
public class Model extends DoublyLinkable {

	@ObfuscatedName("eb.h")
	public static boolean field441;

	@ObfuscatedName("eb.i")
	public int field442 = 45861;

	@ObfuscatedName("eb.j")
	public byte field443 = 47;

	@ObfuscatedName("eb.k")
	public byte field444 = 47;

	@ObfuscatedName("eb.l")
	public int field445 = 5;

	@ObfuscatedName("eb.m")
	public boolean field446 = false;

	@ObfuscatedName("eb.n")
	public int vertexCount;

	@ObfuscatedName("eb.o")
	public int[] vertexX;

	@ObfuscatedName("eb.p")
	public int[] vertexY;

	@ObfuscatedName("eb.q")
	public int[] vertexZ;

	@ObfuscatedName("eb.r")
	public int faceCount;

	@ObfuscatedName("eb.s")
	public int[] faceVertexA;

	@ObfuscatedName("eb.t")
	public int[] faceVertexB;

	@ObfuscatedName("eb.u")
	public int[] faceVertexC;

	@ObfuscatedName("eb.v")
	public int[] faceColorA;

	@ObfuscatedName("eb.w")
	public int[] faceColorB;

	@ObfuscatedName("eb.x")
	public int[] faceColorC;

	@ObfuscatedName("eb.y")
	public int[] faceInfo;

	@ObfuscatedName("eb.z")
	public int[] facePriority;

	@ObfuscatedName("eb.ab")
	public static Packet face1;

	@ObfuscatedName("eb.bb")
	public static Packet face2;

	@ObfuscatedName("eb.cb")
	public static Packet face3;

	@ObfuscatedName("eb.db")
	public static Packet face4;

	@ObfuscatedName("eb.eb")
	public static Packet face5;

	@ObfuscatedName("eb.fb")
	public static Packet point1;

	@ObfuscatedName("eb.gb")
	public static Packet point2;

	@ObfuscatedName("eb.hb")
	public static Packet point3;

	@ObfuscatedName("eb.ib")
	public static Packet point4;

	@ObfuscatedName("eb.jb")
	public static Packet point5;

	@ObfuscatedName("eb.kb")
	public static Packet vertex1;

	@ObfuscatedName("eb.lb")
	public static Packet vertex2;

	@ObfuscatedName("eb.mb")
	public static Packet axis;

	@ObfuscatedName("eb.nb")
	public static boolean[] faceClippedX = new boolean[4096];

	@ObfuscatedName("eb.ob")
	public static boolean[] faceNearClipped = new boolean[4096];

	@ObfuscatedName("eb.pb")
	public static int[] vertexScreenX = new int[4096];

	@ObfuscatedName("eb.qb")
	public static int[] vertexScreenY = new int[4096];

	@ObfuscatedName("eb.rb")
	public static int[] vertexScreenZ = new int[4096];

	@ObfuscatedName("eb.sb")
	public static int[] vertexViewSpaceX = new int[4096];

	@ObfuscatedName("eb.tb")
	public static int[] vertexViewSpaceY = new int[4096];

	@ObfuscatedName("eb.ub")
	public static int[] vertexViewSpaceZ = new int[4096];

	@ObfuscatedName("eb.vb")
	public static int[] tmpDepthFaceCount = new int[1500];

	@ObfuscatedName("eb.wb")
	public static int[][] tmpDepthFaces = new int[1500][512];

	@ObfuscatedName("eb.xb")
	public static int[] tmpPriorityFaceCount = new int[12];

	@ObfuscatedName("eb.yb")
	public static int[][] tmpPriorityFaces = new int[12][2000];

	@ObfuscatedName("eb.zb")
	public static int[] tmpPriority10FaceDepth = new int[2000];

	@ObfuscatedName("eb.V")
	public boolean pickable = false;

	@ObfuscatedName("eb.D")
	public int texturedFaceCount;

	@ObfuscatedName("eb.E")
	public int[] texturedVertexA;

	@ObfuscatedName("eb.F")
	public int[] texturedVertexB;

	@ObfuscatedName("eb.G")
	public int[] texturedVertexC;

	@ObfuscatedName("eb.R")
	public int[] vertexLabel;

	@ObfuscatedName("eb.C")
	public int priority;

	@ObfuscatedName("eb.A")
	public int[] faceAlpha;

	@ObfuscatedName("eb.S")
	public int[] faceLabel;

	@ObfuscatedName("eb.B")
	public int[] faceColor;

	@ObfuscatedName("eb.W")
	public VertexNormal[] vertexNormal;

	@ObfuscatedName("eb.X")
	public VertexNormal[] vertexNormalOriginal;

	@ObfuscatedName("eb.M")
	public int maxY;

	@ObfuscatedName("eb.N")
	public int minY;

	@ObfuscatedName("eb.L")
	public int radius;

	@ObfuscatedName("eb.P")
	public int minDepth;

	@ObfuscatedName("eb.O")
	public int maxDepth;

	@ObfuscatedName("eb.H")
	public int minX;

	@ObfuscatedName("eb.J")
	public int maxZ;

	@ObfuscatedName("eb.K")
	public int minZ;

	@ObfuscatedName("eb.I")
	public int maxX;

	@ObfuscatedName("eb.U")
	public int[][] labelFaces;

	@ObfuscatedName("eb.T")
	public int[][] labelVertices;

	@ObfuscatedName("eb.Ab")
	public static int[] tmpPriority11FaceDepth = new int[2000];

	@ObfuscatedName("eb.Bb")
	public static int[] tmpPriorityDepthSum = new int[12];

	@ObfuscatedName("eb.Cb")
	public static int[] clippedX = new int[10];

	@ObfuscatedName("eb.Db")
	public static int[] clippedY = new int[10];

	@ObfuscatedName("eb.Eb")
	public static int[] clippedColor = new int[10];

	@ObfuscatedName("eb.Mb")
	public static int[] pickedBitsets = new int[1000];

	@ObfuscatedName("eb.Nb")
	public static int[] sin = Pix3D.sinTable;

	@ObfuscatedName("eb.Ob")
	public static int[] cos = Pix3D.cosTable;

	@ObfuscatedName("eb.Pb")
	public static int[] palette = Pix3D.colourTable;

	@ObfuscatedName("eb.Qb")
	public static int[] reciprical16 = Pix3D.divTable2;

	@ObfuscatedName("eb.Q")
	public int objRaise;

	@ObfuscatedName("eb.Fb")
	public static int baseX;

	@ObfuscatedName("eb.Gb")
	public static int baseY;

	@ObfuscatedName("eb.Hb")
	public static int baseZ;

	@ObfuscatedName("eb.Jb")
	public static int mouseX;

	@ObfuscatedName("eb.Kb")
	public static int mouseZ;

	@ObfuscatedName("eb.Lb")
	public static int pickedCount;

	@ObfuscatedName("eb.Z")
	public static Packet head;

	@ObfuscatedName("eb.Ib")
	public static boolean checkHover;

	@ObfuscatedName("eb.Rb")
	public static boolean field529;

	@ObfuscatedName("eb.Y")
	public static Metadata[] metadata;

	@ObfuscatedName("eb.a(Z)V")
	public static void unload(boolean arg0) {
		metadata = null;
		head = null;
		face1 = null;
		face2 = null;
		face3 = null;
		face4 = null;
		face5 = null;
		point1 = null;
		point2 = null;
		point3 = null;
		point4 = null;
		point5 = null;
		vertex1 = null;
		vertex2 = null;
		axis = null;
		faceClippedX = null;
		if (!arg0) {
			field441 = !field441;
		}
		faceNearClipped = null;
		vertexScreenX = null;
		vertexScreenY = null;
		vertexScreenZ = null;
		vertexViewSpaceX = null;
		vertexViewSpaceY = null;
		vertexViewSpaceZ = null;
		tmpDepthFaceCount = null;
		tmpDepthFaces = null;
		tmpPriorityFaceCount = null;
		tmpPriorityFaces = null;
		tmpPriority10FaceDepth = null;
		tmpPriority11FaceDepth = null;
		tmpPriorityDepthSum = null;
		sin = null;
		cos = null;
		palette = null;
		reciprical16 = null;
	}

	@ObfuscatedName("eb.a(ILub;)V")
	public static void unpack(int arg0, Jagfile arg1) {
		if (arg0 < 3 || arg0 > 3) {
			return;
		}
		try {
			head = new Packet(363, arg1.read("ob_head.dat", null, (byte) 2));
			face1 = new Packet(363, arg1.read("ob_face1.dat", null, (byte) 2));
			face2 = new Packet(363, arg1.read("ob_face2.dat", null, (byte) 2));
			face3 = new Packet(363, arg1.read("ob_face3.dat", null, (byte) 2));
			face4 = new Packet(363, arg1.read("ob_face4.dat", null, (byte) 2));
			face5 = new Packet(363, arg1.read("ob_face5.dat", null, (byte) 2));
			point1 = new Packet(363, arg1.read("ob_point1.dat", null, (byte) 2));
			point2 = new Packet(363, arg1.read("ob_point2.dat", null, (byte) 2));
			point3 = new Packet(363, arg1.read("ob_point3.dat", null, (byte) 2));
			point4 = new Packet(363, arg1.read("ob_point4.dat", null, (byte) 2));
			point5 = new Packet(363, arg1.read("ob_point5.dat", null, (byte) 2));
			vertex1 = new Packet(363, arg1.read("ob_vertex1.dat", null, (byte) 2));
			vertex2 = new Packet(363, arg1.read("ob_vertex2.dat", null, (byte) 2));
			axis = new Packet(363, arg1.read("ob_axis.dat", null, (byte) 2));
			head.pos = 0;
			point1.pos = 0;
			point2.pos = 0;
			point3.pos = 0;
			point4.pos = 0;
			vertex1.pos = 0;
			vertex2.pos = 0;
			int var2 = head.g2();
			metadata = new Metadata[var2 + 100];
			int var3 = 0;
			int var4 = 0;
			int var5 = 0;
			int var6 = 0;
			int var7 = 0;
			int var8 = 0;
			int var9 = 0;
			for (int var10 = 0; var10 < var2; var10++) {
				int var11 = head.g2();
				Metadata var12 = metadata[var11] = new Metadata();
				var12.vertexCount = head.g2();
				var12.faceCount = head.g2();
				var12.texturedFaceCount = head.g1();
				var12.vertexFlagsOffset = point1.pos;
				var12.vertexXOffset = point2.pos;
				var12.vertexYOffset = point3.pos;
				var12.vertexZOffset = point4.pos;
				var12.faceVerticesOffset = vertex1.pos;
				var12.faceOrientationsOffset = vertex2.pos;
				int var13 = head.g1();
				int var14 = head.g1();
				int var15 = head.g1();
				int var16 = head.g1();
				int var17 = head.g1();
				int var19;
				for (int var18 = 0; var18 < var12.vertexCount; var18++) {
					var19 = point1.g1();
					if ((var19 & 0x1) != 0) {
						point2.gsmart();
					}
					if ((var19 & 0x2) != 0) {
						point3.gsmart();
					}
					if ((var19 & 0x4) != 0) {
						point4.gsmart();
					}
				}
				for (var19 = 0; var19 < var12.faceCount; var19++) {
					int var20 = vertex2.g1();
					if (var20 == 1) {
						vertex1.gsmart();
						vertex1.gsmart();
					}
					vertex1.gsmart();
				}
				var12.faceColorsOffset = var5;
				var5 += var12.faceCount * 2;
				if (var13 == 1) {
					var12.faceInfosOffset = var6;
					var6 += var12.faceCount;
				} else {
					var12.faceInfosOffset = -1;
				}
				if (var14 == 255) {
					var12.facePrioritiesOffset = var7;
					var7 += var12.faceCount;
				} else {
					var12.facePrioritiesOffset = -var14 - 1;
				}
				if (var15 == 1) {
					var12.faceAlphasOffset = var8;
					var8 += var12.faceCount;
				} else {
					var12.faceAlphasOffset = -1;
				}
				if (var16 == 1) {
					var12.faceLabelsOffset = var9;
					var9 += var12.faceCount;
				} else {
					var12.faceLabelsOffset = -1;
				}
				if (var17 == 1) {
					var12.vertexLabelsOffset = var4;
					var4 += var12.vertexCount;
				} else {
					var12.vertexLabelsOffset = -1;
				}
				var12.faceTextureAxisOffset = var3;
				var3 += var12.texturedFaceCount;
			}
		} catch (Exception var21) {
			System.out.println("Error loading model index");
			var21.printStackTrace();
		}
	}

	public Model(boolean arg0, int arg1) {
		if (metadata != null) {
			Metadata var3 = metadata[arg1];
			if (var3 == null) {
				System.out.println("Error model:" + arg1 + " not found!");
			} else {
				this.vertexCount = var3.vertexCount;
				this.faceCount = var3.faceCount;
				this.texturedFaceCount = var3.texturedFaceCount;
				this.vertexX = new int[this.vertexCount];
				this.vertexY = new int[this.vertexCount];
				this.vertexZ = new int[this.vertexCount];
				this.faceVertexA = new int[this.faceCount];
				this.faceVertexB = new int[this.faceCount];
				this.faceVertexC = new int[this.faceCount];
				this.texturedVertexA = new int[this.texturedFaceCount];
				this.texturedVertexB = new int[this.texturedFaceCount];
				this.texturedVertexC = new int[this.texturedFaceCount];
				if (var3.vertexLabelsOffset >= 0) {
					this.vertexLabel = new int[this.vertexCount];
				}
				if (var3.faceInfosOffset >= 0) {
					this.faceInfo = new int[this.faceCount];
				}
				if (var3.facePrioritiesOffset >= 0) {
					this.facePriority = new int[this.faceCount];
				} else {
					this.priority = -var3.facePrioritiesOffset - 1;
				}
				if (var3.faceAlphasOffset >= 0) {
					this.faceAlpha = new int[this.faceCount];
				}
				if (var3.faceLabelsOffset >= 0) {
					this.faceLabel = new int[this.faceCount];
				}
				this.faceColor = new int[this.faceCount];
				point1.pos = var3.vertexFlagsOffset;
				point2.pos = var3.vertexXOffset;
				point3.pos = var3.vertexYOffset;
				point4.pos = var3.vertexZOffset;
				point5.pos = var3.vertexLabelsOffset;
				int var4 = 0;
				int var5 = 0;
				int var6 = 0;
				int var8;
				int var9;
				int var10;
				int var11;
				for (int var7 = 0; var7 < this.vertexCount; var7++) {
					var8 = point1.g1();
					var9 = 0;
					if ((var8 & 0x1) != 0) {
						var9 = point2.gsmart();
					}
					var10 = 0;
					if ((var8 & 0x2) != 0) {
						var10 = point3.gsmart();
					}
					var11 = 0;
					if ((var8 & 0x4) != 0) {
						var11 = point4.gsmart();
					}
					this.vertexX[var7] = var4 + var9;
					this.vertexY[var7] = var5 + var10;
					this.vertexZ[var7] = var6 + var11;
					var4 = this.vertexX[var7];
					var5 = this.vertexY[var7];
					var6 = this.vertexZ[var7];
					if (this.vertexLabel != null) {
						this.vertexLabel[var7] = point5.g1();
					}
				}
				face1.pos = var3.faceColorsOffset;
				face2.pos = var3.faceInfosOffset;
				face3.pos = var3.facePrioritiesOffset;
				face4.pos = var3.faceAlphasOffset;
				face5.pos = var3.faceLabelsOffset;
				for (var8 = 0; var8 < this.faceCount; var8++) {
					this.faceColor[var8] = face1.g2();
					if (this.faceInfo != null) {
						this.faceInfo[var8] = face2.g1();
					}
					if (this.facePriority != null) {
						this.facePriority[var8] = face3.g1();
					}
					if (this.faceAlpha != null) {
						this.faceAlpha[var8] = face4.g1();
					}
					if (this.faceLabel != null) {
						this.faceLabel[var8] = face5.g1();
					}
				}
				vertex1.pos = var3.faceVerticesOffset;
				vertex2.pos = var3.faceOrientationsOffset;
				var9 = 0;
				var10 = 0;
				var11 = 0;
				int var12 = 0;
				int var14;
				for (int var13 = 0; var13 < this.faceCount; var13++) {
					var14 = vertex2.g1();
					if (var14 == 1) {
						var9 = vertex1.gsmart() + var12;
						var10 = vertex1.gsmart() + var9;
						var11 = vertex1.gsmart() + var10;
						var12 = var11;
						this.faceVertexA[var13] = var9;
						this.faceVertexB[var13] = var10;
						this.faceVertexC[var13] = var11;
					}
					if (var14 == 2) {
						var9 = var9;
						var10 = var11;
						var11 = vertex1.gsmart() + var12;
						var12 = var11;
						this.faceVertexA[var13] = var9;
						this.faceVertexB[var13] = var10;
						this.faceVertexC[var13] = var11;
					}
					if (var14 == 3) {
						var9 = var11;
						var10 = var10;
						var11 = vertex1.gsmart() + var12;
						var12 = var11;
						this.faceVertexA[var13] = var9;
						this.faceVertexB[var13] = var10;
						this.faceVertexC[var13] = var11;
					}
					if (var14 == 4) {
						int var15 = var9;
						var9 = var10;
						var10 = var15;
						var11 = vertex1.gsmart() + var12;
						var12 = var11;
						this.faceVertexA[var13] = var9;
						this.faceVertexB[var13] = var15;
						this.faceVertexC[var13] = var11;
					}
				}
				if (arg0) {
					throw new NullPointerException();
				}
				axis.pos = var3.faceTextureAxisOffset * 6;
				for (var14 = 0; var14 < this.texturedFaceCount; var14++) {
					this.texturedVertexA[var14] = axis.g2();
					this.texturedVertexB[var14] = axis.g2();
					this.texturedVertexC[var14] = axis.g2();
				}
			}
		}
	}

	public Model(int arg0, Model[] arg1, int arg2) {
		boolean var4 = false;
		boolean var5 = false;
		boolean var6 = false;
		boolean var7 = false;
		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		this.priority = -1;
		for (int var8 = 0; var8 < arg2; var8++) {
			Model var9 = arg1[var8];
			if (var9 != null) {
				this.vertexCount += var9.vertexCount;
				this.faceCount += var9.faceCount;
				this.texturedFaceCount += var9.texturedFaceCount;
				var4 |= var9.faceInfo != null;
				if (var9.facePriority == null) {
					if (this.priority == -1) {
						this.priority = var9.priority;
					}
					if (this.priority != var9.priority) {
						var5 = true;
					}
				} else {
					var5 = true;
				}
				var6 |= var9.faceAlpha != null;
				var7 |= var9.faceLabel != null;
			}
		}
		if (arg0 != 0) {
			throw new NullPointerException();
		}
		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];
		this.vertexLabel = new int[this.vertexCount];
		this.faceVertexA = new int[this.faceCount];
		this.faceVertexB = new int[this.faceCount];
		this.faceVertexC = new int[this.faceCount];
		this.texturedVertexA = new int[this.texturedFaceCount];
		this.texturedVertexB = new int[this.texturedFaceCount];
		this.texturedVertexC = new int[this.texturedFaceCount];
		if (var4) {
			this.faceInfo = new int[this.faceCount];
		}
		if (var5) {
			this.facePriority = new int[this.faceCount];
		}
		if (var6) {
			this.faceAlpha = new int[this.faceCount];
		}
		if (var7) {
			this.faceLabel = new int[this.faceCount];
		}
		this.faceColor = new int[this.faceCount];
		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		for (int var13 = 0; var13 < arg2; var13++) {
			Model var10 = arg1[var13];
			if (var10 != null) {
				for (int var11 = 0; var11 < var10.faceCount; var11++) {
					if (var4) {
						if (var10.faceInfo == null) {
							this.faceInfo[this.faceCount] = 0;
						} else {
							this.faceInfo[this.faceCount] = var10.faceInfo[var11];
						}
					}
					if (var5) {
						if (var10.facePriority == null) {
							this.facePriority[this.faceCount] = var10.priority;
						} else {
							this.facePriority[this.faceCount] = var10.facePriority[var11];
						}
					}
					if (var6) {
						if (var10.faceAlpha == null) {
							this.faceAlpha[this.faceCount] = 0;
						} else {
							this.faceAlpha[this.faceCount] = var10.faceAlpha[var11];
						}
					}
					if (var7 && var10.faceLabel != null) {
						this.faceLabel[this.faceCount] = var10.faceLabel[var11];
					}
					this.faceColor[this.faceCount] = var10.faceColor[var11];
					this.faceVertexA[this.faceCount] = this.addVertex(var10, var10.faceVertexA[var11]);
					this.faceVertexB[this.faceCount] = this.addVertex(var10, var10.faceVertexB[var11]);
					this.faceVertexC[this.faceCount] = this.addVertex(var10, var10.faceVertexC[var11]);
					this.faceCount++;
				}
				for (int var12 = 0; var12 < var10.texturedFaceCount; var12++) {
					this.texturedVertexA[this.texturedFaceCount] = this.addVertex(var10, var10.texturedVertexA[var12]);
					this.texturedVertexB[this.texturedFaceCount] = this.addVertex(var10, var10.texturedVertexB[var12]);
					this.texturedVertexC[this.texturedFaceCount] = this.addVertex(var10, var10.texturedVertexC[var12]);
					this.texturedFaceCount++;
				}
			}
		}
	}

	public Model(Model[] arg0, byte arg1, int arg2, boolean arg3) {
		boolean var5 = false;
		boolean var6 = false;
		boolean var7 = false;
		boolean var8 = false;
		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		this.priority = -1;
		for (int var9 = 0; var9 < arg2; var9++) {
			Model var10 = arg0[var9];
			if (var10 != null) {
				this.vertexCount += var10.vertexCount;
				this.faceCount += var10.faceCount;
				this.texturedFaceCount += var10.texturedFaceCount;
				var5 |= var10.faceInfo != null;
				if (var10.facePriority == null) {
					if (this.priority == -1) {
						this.priority = var10.priority;
					}
					if (this.priority != var10.priority) {
						var6 = true;
					}
				} else {
					var6 = true;
				}
				var7 |= var10.faceAlpha != null;
				var8 |= var10.faceColor != null;
			}
		}
		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];
		this.faceVertexA = new int[this.faceCount];
		this.faceVertexB = new int[this.faceCount];
		this.faceVertexC = new int[this.faceCount];
		this.faceColorA = new int[this.faceCount];
		this.faceColorB = new int[this.faceCount];
		this.faceColorC = new int[this.faceCount];
		this.texturedVertexA = new int[this.texturedFaceCount];
		this.texturedVertexB = new int[this.texturedFaceCount];
		this.texturedVertexC = new int[this.texturedFaceCount];
		if (var5) {
			this.faceInfo = new int[this.faceCount];
		}
		if (var6) {
			this.facePriority = new int[this.faceCount];
		}
		if (var7) {
			this.faceAlpha = new int[this.faceCount];
		}
		if (var8) {
			this.faceColor = new int[this.faceCount];
		}
		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		int var16;
		if (arg1 != -31) {
			for (var16 = 1; var16 > 0; var16++) {
			}
		}
		for (var16 = 0; var16 < arg2; var16++) {
			Model var11 = arg0[var16];
			if (var11 != null) {
				int var12 = this.vertexCount;
				for (int var13 = 0; var13 < var11.vertexCount; var13++) {
					this.vertexX[this.vertexCount] = var11.vertexX[var13];
					this.vertexY[this.vertexCount] = var11.vertexY[var13];
					this.vertexZ[this.vertexCount] = var11.vertexZ[var13];
					this.vertexCount++;
				}
				for (int var14 = 0; var14 < var11.faceCount; var14++) {
					this.faceVertexA[this.faceCount] = var11.faceVertexA[var14] + var12;
					this.faceVertexB[this.faceCount] = var11.faceVertexB[var14] + var12;
					this.faceVertexC[this.faceCount] = var11.faceVertexC[var14] + var12;
					this.faceColorA[this.faceCount] = var11.faceColorA[var14];
					this.faceColorB[this.faceCount] = var11.faceColorB[var14];
					this.faceColorC[this.faceCount] = var11.faceColorC[var14];
					if (var5) {
						if (var11.faceInfo == null) {
							this.faceInfo[this.faceCount] = 0;
						} else {
							this.faceInfo[this.faceCount] = var11.faceInfo[var14];
						}
					}
					if (var6) {
						if (var11.facePriority == null) {
							this.facePriority[this.faceCount] = var11.priority;
						} else {
							this.facePriority[this.faceCount] = var11.facePriority[var14];
						}
					}
					if (var7) {
						if (var11.faceAlpha == null) {
							this.faceAlpha[this.faceCount] = 0;
						} else {
							this.faceAlpha[this.faceCount] = var11.faceAlpha[var14];
						}
					}
					if (var8 && var11.faceColor != null) {
						this.faceColor[this.faceCount] = var11.faceColor[var14];
					}
					this.faceCount++;
				}
				for (int var15 = 0; var15 < var11.texturedFaceCount; var15++) {
					this.texturedVertexA[this.texturedFaceCount] = var11.texturedVertexA[var15] + var12;
					this.texturedVertexB[this.texturedFaceCount] = var11.texturedVertexB[var15] + var12;
					this.texturedVertexC[this.texturedFaceCount] = var11.texturedVertexC[var15] + var12;
					this.texturedFaceCount++;
				}
			}
		}
		this.calculateBoundsCylinder(2992);
	}

	public Model(Model arg0, boolean arg1, boolean arg2, int arg3, boolean arg4) {
		this.vertexCount = arg0.vertexCount;
		this.faceCount = arg0.faceCount;
		this.texturedFaceCount = arg0.texturedFaceCount;
		int var6;
		if (arg4) {
			this.vertexX = arg0.vertexX;
			this.vertexY = arg0.vertexY;
			this.vertexZ = arg0.vertexZ;
		} else {
			this.vertexX = new int[this.vertexCount];
			this.vertexY = new int[this.vertexCount];
			this.vertexZ = new int[this.vertexCount];
			for (var6 = 0; var6 < this.vertexCount; var6++) {
				this.vertexX[var6] = arg0.vertexX[var6];
				this.vertexY[var6] = arg0.vertexY[var6];
				this.vertexZ[var6] = arg0.vertexZ[var6];
			}
		}
		if (arg1) {
			this.faceColor = arg0.faceColor;
		} else {
			this.faceColor = new int[this.faceCount];
			for (var6 = 0; var6 < this.faceCount; var6++) {
				this.faceColor[var6] = arg0.faceColor[var6];
			}
		}
		if (arg2) {
			this.faceAlpha = arg0.faceAlpha;
		} else {
			this.faceAlpha = new int[this.faceCount];
			if (arg0.faceAlpha == null) {
				for (var6 = 0; var6 < this.faceCount; var6++) {
					this.faceAlpha[var6] = 0;
				}
			} else {
				for (var6 = 0; var6 < this.faceCount; var6++) {
					this.faceAlpha[var6] = arg0.faceAlpha[var6];
				}
			}
		}
		this.vertexLabel = arg0.vertexLabel;
		this.faceLabel = arg0.faceLabel;
		this.faceInfo = arg0.faceInfo;
		if (arg3 != 0) {
			field441 = !field441;
		}
		this.faceVertexA = arg0.faceVertexA;
		this.faceVertexB = arg0.faceVertexB;
		this.faceVertexC = arg0.faceVertexC;
		this.facePriority = arg0.facePriority;
		this.priority = arg0.priority;
		this.texturedVertexA = arg0.texturedVertexA;
		this.texturedVertexB = arg0.texturedVertexB;
		this.texturedVertexC = arg0.texturedVertexC;
	}

	public Model(Model arg0, byte arg1, boolean arg2, boolean arg3) {
		this.vertexCount = arg0.vertexCount;
		this.faceCount = arg0.faceCount;
		this.texturedFaceCount = arg0.texturedFaceCount;
		int var5;
		if (arg2) {
			this.vertexY = new int[this.vertexCount];
			for (var5 = 0; var5 < this.vertexCount; var5++) {
				this.vertexY[var5] = arg0.vertexY[var5];
			}
		} else {
			this.vertexY = arg0.vertexY;
		}
		if (arg3) {
			this.faceColorA = new int[this.faceCount];
			this.faceColorB = new int[this.faceCount];
			this.faceColorC = new int[this.faceCount];
			for (var5 = 0; var5 < this.faceCount; var5++) {
				this.faceColorA[var5] = arg0.faceColorA[var5];
				this.faceColorB[var5] = arg0.faceColorB[var5];
				this.faceColorC[var5] = arg0.faceColorC[var5];
			}
			this.faceInfo = new int[this.faceCount];
			int var6;
			if (arg0.faceInfo == null) {
				for (var6 = 0; var6 < this.faceCount; var6++) {
					this.faceInfo[var6] = 0;
				}
			} else {
				for (var6 = 0; var6 < this.faceCount; var6++) {
					this.faceInfo[var6] = arg0.faceInfo[var6];
				}
			}
			this.vertexNormal = new VertexNormal[this.vertexCount];
			for (var6 = 0; var6 < this.vertexCount; var6++) {
				VertexNormal var7 = this.vertexNormal[var6] = new VertexNormal();
				VertexNormal var8 = arg0.vertexNormal[var6];
				var7.x = var8.x;
				var7.y = var8.y;
				var7.z = var8.z;
				var7.w = var8.w;
			}
			this.vertexNormalOriginal = arg0.vertexNormalOriginal;
		} else {
			this.faceColorA = arg0.faceColorA;
			this.faceColorB = arg0.faceColorB;
			this.faceColorC = arg0.faceColorC;
			this.faceInfo = arg0.faceInfo;
		}
		this.vertexX = arg0.vertexX;
		this.vertexZ = arg0.vertexZ;
		this.faceColor = arg0.faceColor;
		this.faceAlpha = arg0.faceAlpha;
		this.facePriority = arg0.facePriority;
		this.priority = arg0.priority;
		this.faceVertexA = arg0.faceVertexA;
		this.faceVertexB = arg0.faceVertexB;
		this.faceVertexC = arg0.faceVertexC;
		this.texturedVertexA = arg0.texturedVertexA;
		this.texturedVertexB = arg0.texturedVertexB;
		this.texturedVertexC = arg0.texturedVertexC;
		if (arg1 != -31) {
			throw new NullPointerException();
		}
		this.maxY = arg0.maxY;
		this.minY = arg0.minY;
		this.radius = arg0.radius;
		this.minDepth = arg0.minDepth;
		this.maxDepth = arg0.maxDepth;
		this.minX = arg0.minX;
		this.maxZ = arg0.maxZ;
		this.minZ = arg0.minZ;
		this.maxX = arg0.maxX;
	}

	public Model(int arg0, Model arg1, boolean arg2) {
		this.vertexCount = arg1.vertexCount;
		this.faceCount = arg1.faceCount;
		this.texturedFaceCount = arg1.texturedFaceCount;
		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];
		for (int var4 = 0; var4 < this.vertexCount; var4++) {
			this.vertexX[var4] = arg1.vertexX[var4];
			this.vertexY[var4] = arg1.vertexY[var4];
			this.vertexZ[var4] = arg1.vertexZ[var4];
		}
		if (arg2) {
			this.faceAlpha = arg1.faceAlpha;
		} else {
			this.faceAlpha = new int[this.faceCount];
			int var5;
			if (arg1.faceAlpha == null) {
				for (var5 = 0; var5 < this.faceCount; var5++) {
					this.faceAlpha[var5] = 0;
				}
			} else {
				for (var5 = 0; var5 < this.faceCount; var5++) {
					this.faceAlpha[var5] = arg1.faceAlpha[var5];
				}
			}
		}
		this.faceInfo = arg1.faceInfo;
		this.faceColor = arg1.faceColor;
		this.facePriority = arg1.facePriority;
		if (arg0 != 0) {
			this.field445 = 213;
		}
		this.priority = arg1.priority;
		this.labelFaces = arg1.labelFaces;
		this.labelVertices = arg1.labelVertices;
		this.faceVertexA = arg1.faceVertexA;
		this.faceVertexB = arg1.faceVertexB;
		this.faceVertexC = arg1.faceVertexC;
		this.faceColorA = arg1.faceColorA;
		this.faceColorB = arg1.faceColorB;
		this.faceColorC = arg1.faceColorC;
		this.texturedVertexA = arg1.texturedVertexA;
		this.texturedVertexB = arg1.texturedVertexB;
		this.texturedVertexC = arg1.texturedVertexC;
	}

	@ObfuscatedName("eb.a(Leb;I)I")
	public final int addVertex(Model arg0, int arg1) {
		int var3 = -1;
		int var4 = arg0.vertexX[arg1];
		int var5 = arg0.vertexY[arg1];
		int var6 = arg0.vertexZ[arg1];
		for (int var7 = 0; var7 < this.vertexCount; var7++) {
			if (var4 == this.vertexX[var7] && var5 == this.vertexY[var7] && var6 == this.vertexZ[var7]) {
				var3 = var7;
				break;
			}
		}
		if (var3 == -1) {
			this.vertexX[this.vertexCount] = var4;
			this.vertexY[this.vertexCount] = var5;
			this.vertexZ[this.vertexCount] = var6;
			if (arg0.vertexLabel != null) {
				this.vertexLabel[this.vertexCount] = arg0.vertexLabel[arg1];
			}
			var3 = this.vertexCount++;
		}
		return var3;
	}

	@ObfuscatedName("eb.a(I)V")
	public void calculateBoundsCylinder(int arg0) {
		this.maxY = 0;
		if (arg0 != 2992) {
			return;
		}
		this.radius = 0;
		this.minY = 0;
		for (int var2 = 0; var2 < this.vertexCount; var2++) {
			int var3 = this.vertexX[var2];
			int var4 = this.vertexY[var2];
			int var5 = this.vertexZ[var2];
			if (-var4 > this.maxY) {
				this.maxY = -var4;
			}
			if (var4 > this.minY) {
				this.minY = var4;
			}
			int var6 = var3 * var3 + var5 * var5;
			if (var6 > this.radius) {
				this.radius = var6;
			}
		}
		this.radius = (int) (Math.sqrt((double) this.radius) + 0.99D);
		this.minDepth = (int) (Math.sqrt((double) (this.radius * this.radius + this.maxY * this.maxY)) + 0.99D);
		this.maxDepth = this.minDepth + (int) (Math.sqrt((double) (this.radius * this.radius + this.minY * this.minY)) + 0.99D);
	}

	@ObfuscatedName("eb.b(I)V")
	public void calculateBoundsY(int arg0) {
		this.maxY = 0;
		int var2;
		if (arg0 != 0) {
			for (var2 = 1; var2 > 0; var2++) {
			}
		}
		this.minY = 0;
		for (var2 = 0; var2 < this.vertexCount; var2++) {
			int var3 = this.vertexY[var2];
			if (-var3 > this.maxY) {
				this.maxY = -var3;
			}
			if (var3 > this.minY) {
				this.minY = var3;
			}
		}
		this.minDepth = (int) (Math.sqrt((double) (this.radius * this.radius + this.maxY * this.maxY)) + 0.99D);
		this.maxDepth = this.minDepth + (int) (Math.sqrt((double) (this.radius * this.radius + this.minY * this.minY)) + 0.99D);
	}

	@ObfuscatedName("eb.a(B)V")
	public void calculateBoundsAABB(byte arg0) {
		this.maxY = 0;
		if (arg0 != this.field444) {
			return;
		}
		this.radius = 0;
		this.minY = 0;
		this.minX = 999999;
		this.maxX = -999999;
		this.maxZ = -99999;
		this.minZ = 99999;
		for (int var2 = 0; var2 < this.vertexCount; var2++) {
			int var3 = this.vertexX[var2];
			int var4 = this.vertexY[var2];
			int var5 = this.vertexZ[var2];
			if (var3 < this.minX) {
				this.minX = var3;
			}
			if (var3 > this.maxX) {
				this.maxX = var3;
			}
			if (var5 < this.minZ) {
				this.minZ = var5;
			}
			if (var5 > this.maxZ) {
				this.maxZ = var5;
			}
			if (-var4 > this.maxY) {
				this.maxY = -var4;
			}
			if (var4 > this.minY) {
				this.minY = var4;
			}
			int var6 = var3 * var3 + var5 * var5;
			if (var6 > this.radius) {
				this.radius = var6;
			}
		}
		this.radius = (int) Math.sqrt((double) this.radius);
		this.minDepth = (int) Math.sqrt((double) (this.radius * this.radius + this.maxY * this.maxY));
		this.maxDepth = this.minDepth + (int) Math.sqrt((double) (this.radius * this.radius + this.minY * this.minY));
	}

	@ObfuscatedName("eb.c(I)V")
	public void createLabelReferences(int arg0) {
		if (arg0 != 4) {
			return;
		}
		int[] var2;
		int var10002;
		int var3;
		int var4;
		int var5;
		int var6;
		int var7;
		if (this.vertexLabel != null) {
			var2 = new int[256];
			var3 = 0;
			for (var4 = 0; var4 < this.vertexCount; var4++) {
				var5 = this.vertexLabel[var4];
				var10002 = var2[var5]++;
				if (var5 > var3) {
					var3 = var5;
				}
			}
			this.labelVertices = new int[var3 + 1][];
			for (var5 = 0; var5 <= var3; var5++) {
				this.labelVertices[var5] = new int[var2[var5]];
				var2[var5] = 0;
			}
			var6 = 0;
			while (var6 < this.vertexCount) {
				var7 = this.vertexLabel[var6];
				this.labelVertices[var7][var2[var7]++] = var6++;
			}
			this.vertexLabel = null;
		}
		if (this.faceLabel == null) {
			return;
		}
		var2 = new int[256];
		var3 = 0;
		for (var4 = 0; var4 < this.faceCount; var4++) {
			var5 = this.faceLabel[var4];
			var10002 = var2[var5]++;
			if (var5 > var3) {
				var3 = var5;
			}
		}
		this.labelFaces = new int[var3 + 1][];
		for (var5 = 0; var5 <= var3; var5++) {
			this.labelFaces[var5] = new int[var2[var5]];
			var2[var5] = 0;
		}
		var6 = 0;
		while (var6 < this.faceCount) {
			var7 = this.faceLabel[var6];
			this.labelFaces[var7][var2[var7]++] = var6++;
		}
		this.faceLabel = null;
	}

	@ObfuscatedName("eb.a(II)V")
	public void applyTransform(int arg0, int arg1) {
		if (this.labelVertices == null || arg1 == -1) {
			return;
		}
		AnimFrame var3 = AnimFrame.instances[arg1];
		AnimBase var4 = var3.base;
		baseX = 0;
		baseY = 0;
		baseZ = 0;
		for (int var5 = 0; var5 < var3.length; var5++) {
			int var6 = var3.groups[var5];
			this.applyTransform(var4.types[var6], var4.labels[var6], var3.x[var5], var3.y[var5], var3.z[var5]);
		}
		if (arg0 == -16599) {
			;
		}
	}

	@ObfuscatedName("eb.a(III[I)V")
	public void applyTransforms(int arg0, int arg1, int arg2, int[] arg3) {
		if (arg2 == -1) {
			return;
		}
		if (arg3 == null || arg0 == -1) {
			this.applyTransform(-16599, arg2);
			return;
		}
		AnimFrame var5 = AnimFrame.instances[arg2];
		if (arg1 < 3 || arg1 > 3) {
			this.field445 = -162;
		}
		AnimFrame var6 = AnimFrame.instances[arg0];
		AnimBase var7 = var5.base;
		baseX = 0;
		baseY = 0;
		baseZ = 0;
		byte var8 = 0;
		int var13 = var8 + 1;
		int var9 = arg3[var8];
		int var11;
		for (int var10 = 0; var10 < var5.length; var10++) {
			var11 = var5.groups[var10];
			while (var11 > var9) {
				var9 = arg3[var13++];
			}
			if (var11 != var9 || var7.types[var11] == 0) {
				this.applyTransform(var7.types[var11], var7.labels[var11], var5.x[var10], var5.y[var10], var5.z[var10]);
			}
		}
		baseX = 0;
		baseY = 0;
		baseZ = 0;
		var8 = 0;
		var13 = var8 + 1;
		var9 = arg3[var8];
		for (var11 = 0; var11 < var6.length; var11++) {
			int var12 = var6.groups[var11];
			while (var12 > var9) {
				var9 = arg3[var13++];
			}
			if (var12 == var9 || var7.types[var12] == 0) {
				this.applyTransform(var7.types[var12], var7.labels[var12], var6.x[var11], var6.y[var11], var6.z[var11]);
			}
		}
	}

	@ObfuscatedName("eb.a(I[IIII)V")
	public void applyTransform(int arg0, int[] arg1, int arg2, int arg3, int arg4) {
		int var6 = arg1.length;
		int var7;
		int var8;
		int var11;
		int var12;
		if (arg0 == 0) {
			var7 = 0;
			baseX = 0;
			baseY = 0;
			baseZ = 0;
			for (var8 = 0; var8 < var6; var8++) {
				int var18 = arg1[var8];
				if (var18 < this.labelVertices.length) {
					int[] var19 = this.labelVertices[var18];
					for (var11 = 0; var11 < var19.length; var11++) {
						var12 = var19[var11];
						baseX += this.vertexX[var12];
						baseY += this.vertexY[var12];
						baseZ += this.vertexZ[var12];
						var7++;
					}
				}
			}
			if (var7 > 0) {
				baseX = baseX / var7 + arg2;
				baseY = baseY / var7 + arg3;
				baseZ = baseZ / var7 + arg4;
			} else {
				baseX = arg2;
				baseY = arg3;
				baseZ = arg4;
			}
			return;
		}
		int[] var9;
		int var10;
		if (arg0 == 1) {
			for (var7 = 0; var7 < var6; var7++) {
				var8 = arg1[var7];
				if (var8 < this.labelVertices.length) {
					var9 = this.labelVertices[var8];
					for (var10 = 0; var10 < var9.length; var10++) {
						var11 = var9[var10];
						this.vertexX[var11] += arg2;
						this.vertexY[var11] += arg3;
						this.vertexZ[var11] += arg4;
					}
				}
			}
		} else if (arg0 == 2) {
			for (var7 = 0; var7 < var6; var7++) {
				var8 = arg1[var7];
				if (var8 < this.labelVertices.length) {
					var9 = this.labelVertices[var8];
					for (var10 = 0; var10 < var9.length; var10++) {
						var11 = var9[var10];
						this.vertexX[var11] -= baseX;
						this.vertexY[var11] -= baseY;
						this.vertexZ[var11] -= baseZ;
						var12 = (arg2 & 0xFF) * 8;
						int var13 = (arg3 & 0xFF) * 8;
						int var14 = (arg4 & 0xFF) * 8;
						int var15;
						int var16;
						int var17;
						if (var14 != 0) {
							var15 = sin[var14];
							var16 = cos[var14];
							var17 = this.vertexY[var11] * var15 + this.vertexX[var11] * var16 >> 16;
							this.vertexY[var11] = this.vertexY[var11] * var16 - this.vertexX[var11] * var15 >> 16;
							this.vertexX[var11] = var17;
						}
						if (var12 != 0) {
							var15 = sin[var12];
							var16 = cos[var12];
							var17 = this.vertexY[var11] * var16 - this.vertexZ[var11] * var15 >> 16;
							this.vertexZ[var11] = this.vertexY[var11] * var15 + this.vertexZ[var11] * var16 >> 16;
							this.vertexY[var11] = var17;
						}
						if (var13 != 0) {
							var15 = sin[var13];
							var16 = cos[var13];
							var17 = this.vertexZ[var11] * var15 + this.vertexX[var11] * var16 >> 16;
							this.vertexZ[var11] = this.vertexZ[var11] * var16 - this.vertexX[var11] * var15 >> 16;
							this.vertexX[var11] = var17;
						}
						this.vertexX[var11] += baseX;
						this.vertexY[var11] += baseY;
						this.vertexZ[var11] += baseZ;
					}
				}
			}
		} else if (arg0 == 3) {
			for (var7 = 0; var7 < var6; var7++) {
				var8 = arg1[var7];
				if (var8 < this.labelVertices.length) {
					var9 = this.labelVertices[var8];
					for (var10 = 0; var10 < var9.length; var10++) {
						var11 = var9[var10];
						this.vertexX[var11] -= baseX;
						this.vertexY[var11] -= baseY;
						this.vertexZ[var11] -= baseZ;
						this.vertexX[var11] = this.vertexX[var11] * arg2 / 128;
						this.vertexY[var11] = this.vertexY[var11] * arg3 / 128;
						this.vertexZ[var11] = this.vertexZ[var11] * arg4 / 128;
						this.vertexX[var11] += baseX;
						this.vertexY[var11] += baseY;
						this.vertexZ[var11] += baseZ;
					}
				}
			}
		} else if (arg0 == 5 && (this.labelFaces != null && this.faceAlpha != null)) {
			for (var7 = 0; var7 < var6; var7++) {
				var8 = arg1[var7];
				if (var8 < this.labelFaces.length) {
					var9 = this.labelFaces[var8];
					for (var10 = 0; var10 < var9.length; var10++) {
						var11 = var9[var10];
						this.faceAlpha[var11] += arg2 * 8;
						if (this.faceAlpha[var11] < 0) {
							this.faceAlpha[var11] = 0;
						}
						if (this.faceAlpha[var11] > 255) {
							this.faceAlpha[var11] = 255;
						}
					}
				}
			}
		}
	}

	@ObfuscatedName("eb.d(I)V")
	public void rotateY90(int arg0) {
		if (arg0 != 0) {
			this.field442 = 472;
		}
		for (int var2 = 0; var2 < this.vertexCount; var2++) {
			int var3 = this.vertexX[var2];
			this.vertexX[var2] = this.vertexZ[var2];
			this.vertexZ[var2] = -var3;
		}
	}

	@ObfuscatedName("eb.a(BI)V")
	public void rotateX(byte arg0, int arg1) {
		int var3 = sin[arg1];
		int var4 = cos[arg1];
		for (int var5 = 0; var5 < this.vertexCount; var5++) {
			int var6 = this.vertexY[var5] * var4 - this.vertexZ[var5] * var3 >> 16;
			this.vertexZ[var5] = this.vertexY[var5] * var3 + this.vertexZ[var5] * var4 >> 16;
			this.vertexY[var5] = var6;
		}
		if (arg0 == 7) {
			boolean var7 = false;
		}
	}

	@ObfuscatedName("eb.a(IIII)V")
	public void translate(int arg0, int arg1, int arg2, int arg3) {
		if (arg2 >= 0) {
			this.field446 = !this.field446;
		}
		for (int var5 = 0; var5 < this.vertexCount; var5++) {
			this.vertexX[var5] += arg1;
			this.vertexY[var5] += arg0;
			this.vertexZ[var5] += arg3;
		}
	}

	@ObfuscatedName("eb.b(II)V")
	public void recolor(int arg0, int arg1) {
		for (int var3 = 0; var3 < this.faceCount; var3++) {
			if (this.faceColor[var3] == arg0) {
				this.faceColor[var3] = arg1;
			}
		}
	}

	@ObfuscatedName("eb.e(I)V")
	public void rotateY180(int arg0) {
		for (int var2 = 0; var2 < this.vertexCount; var2++) {
			this.vertexZ[var2] = -this.vertexZ[var2];
		}
		int var4;
		for (int var3 = 0; var3 < this.faceCount; var3++) {
			var4 = this.faceVertexA[var3];
			this.faceVertexA[var3] = this.faceVertexC[var3];
			this.faceVertexC[var3] = var4;
		}
		if (arg0 >= 0) {
			for (var4 = 1; var4 > 0; var4++) {
			}
		}
	}

	@ObfuscatedName("eb.b(IIII)V")
	public void scale(int arg0, int arg1, int arg2, int arg3) {
		for (int var5 = 0; var5 < this.vertexCount; var5++) {
			this.vertexX[var5] = this.vertexX[var5] * arg3 / 128;
			this.vertexY[var5] = this.vertexY[var5] * arg2 / 128;
			this.vertexZ[var5] = this.vertexZ[var5] * arg0 / 128;
		}
		if (arg1 < 2 || arg1 > 2) {
			;
		}
	}

	@ObfuscatedName("eb.a(IIIIIZ)V")
	public final void calculateNormals(int arg0, int arg1, int arg2, int arg3, int arg4, boolean arg5) {
		int var7 = (int) Math.sqrt((double) (arg2 * arg2 + arg3 * arg3 + arg4 * arg4));
		int var8 = arg1 * var7 >> 8;
		if (this.faceColorA == null) {
			this.faceColorA = new int[this.faceCount];
			this.faceColorB = new int[this.faceCount];
			this.faceColorC = new int[this.faceCount];
		}
		int var9;
		if (this.vertexNormal == null) {
			this.vertexNormal = new VertexNormal[this.vertexCount];
			for (var9 = 0; var9 < this.vertexCount; var9++) {
				this.vertexNormal[var9] = new VertexNormal();
			}
		}
		int var10;
		for (var9 = 0; var9 < this.faceCount; var9++) {
			var10 = this.faceVertexA[var9];
			int var11 = this.faceVertexB[var9];
			int var12 = this.faceVertexC[var9];
			int var13 = this.vertexX[var11] - this.vertexX[var10];
			int var14 = this.vertexY[var11] - this.vertexY[var10];
			int var15 = this.vertexZ[var11] - this.vertexZ[var10];
			int var16 = this.vertexX[var12] - this.vertexX[var10];
			int var17 = this.vertexY[var12] - this.vertexY[var10];
			int var18 = this.vertexZ[var12] - this.vertexZ[var10];
			int var19 = var14 * var18 - var17 * var15;
			int var20 = var15 * var16 - var18 * var13;
			int var21;
			for (var21 = var13 * var17 - var16 * var14; var19 > 8192 || var20 > 8192 || var21 > 8192 || var19 < -8192 || var20 < -8192 || var21 < -8192; var21 >>= 0x1) {
				var19 >>= 0x1;
				var20 >>= 0x1;
			}
			int var22 = (int) Math.sqrt((double) (var19 * var19 + var20 * var20 + var21 * var21));
			if (var22 <= 0) {
				var22 = 1;
			}
			var19 = var19 * 256 / var22;
			var20 = var20 * 256 / var22;
			var21 = var21 * 256 / var22;
			if (this.faceInfo == null || (this.faceInfo[var9] & 0x1) == 0) {
				VertexNormal var23 = this.vertexNormal[var10];
				var23.x += var19;
				var23.y += var20;
				var23.z += var21;
				var23.w++;
				VertexNormal var26 = this.vertexNormal[var11];
				var26.x += var19;
				var26.y += var20;
				var26.z += var21;
				var26.w++;
				VertexNormal var27 = this.vertexNormal[var12];
				var27.x += var19;
				var27.y += var20;
				var27.z += var21;
				var27.w++;
			} else {
				int var28 = arg0 + (arg2 * var19 + arg3 * var20 + arg4 * var21) / (var8 + var8 / 2);
				this.faceColorA[var9] = mulColorLightness(this.faceColor[var9], var28, this.faceInfo[var9]);
			}
		}
		if (arg5) {
			this.applyLighting(arg0, var8, arg2, arg3, arg4);
		} else {
			this.vertexNormalOriginal = new VertexNormal[this.vertexCount];
			for (var10 = 0; var10 < this.vertexCount; var10++) {
				VertexNormal var24 = this.vertexNormal[var10];
				VertexNormal var25 = this.vertexNormalOriginal[var10] = new VertexNormal();
				var25.x = var24.x;
				var25.y = var24.y;
				var25.z = var24.z;
				var25.w = var24.w;
			}
		}
		if (arg5) {
			this.calculateBoundsCylinder(2992);
		} else {
			this.calculateBoundsAABB(this.field443);
		}
	}

	@ObfuscatedName("eb.a(IIIII)V")
	public final void applyLighting(int arg0, int arg1, int arg2, int arg3, int arg4) {
		int var7;
		for (int var6 = 0; var6 < this.faceCount; var6++) {
			var7 = this.faceVertexA[var6];
			int var8 = this.faceVertexB[var6];
			int var9 = this.faceVertexC[var6];
			VertexNormal var10;
			int var11;
			int var12;
			if (this.faceInfo == null) {
				var12 = this.faceColor[var6];
				var10 = this.vertexNormal[var7];
				var11 = arg0 + (arg2 * var10.x + arg3 * var10.y + arg4 * var10.z) / (arg1 * var10.w);
				this.faceColorA[var6] = mulColorLightness(var12, var11, 0);
				VertexNormal var14 = this.vertexNormal[var8];
				int var16 = arg0 + (arg2 * var14.x + arg3 * var14.y + arg4 * var14.z) / (arg1 * var14.w);
				this.faceColorB[var6] = mulColorLightness(var12, var16, 0);
				VertexNormal var15 = this.vertexNormal[var9];
				int var17 = arg0 + (arg2 * var15.x + arg3 * var15.y + arg4 * var15.z) / (arg1 * var15.w);
				this.faceColorC[var6] = mulColorLightness(var12, var17, 0);
			} else if ((this.faceInfo[var6] & 0x1) == 0) {
				var12 = this.faceColor[var6];
				int var13 = this.faceInfo[var6];
				var10 = this.vertexNormal[var7];
				var11 = arg0 + (arg2 * var10.x + arg3 * var10.y + arg4 * var10.z) / (arg1 * var10.w);
				this.faceColorA[var6] = mulColorLightness(var12, var11, var13);
				var10 = this.vertexNormal[var8];
				var11 = arg0 + (arg2 * var10.x + arg3 * var10.y + arg4 * var10.z) / (arg1 * var10.w);
				this.faceColorB[var6] = mulColorLightness(var12, var11, var13);
				var10 = this.vertexNormal[var9];
				var11 = arg0 + (arg2 * var10.x + arg3 * var10.y + arg4 * var10.z) / (arg1 * var10.w);
				this.faceColorC[var6] = mulColorLightness(var12, var11, var13);
			}
		}
		this.vertexNormal = null;
		this.vertexNormalOriginal = null;
		this.vertexLabel = null;
		this.faceLabel = null;
		if (this.faceInfo != null) {
			for (var7 = 0; var7 < this.faceCount; var7++) {
				if ((this.faceInfo[var7] & 0x2) == 2) {
					return;
				}
			}
		}
		this.faceColor = null;
	}

	@ObfuscatedName("eb.a(III)I")
	public static final int mulColorLightness(int arg0, int arg1, int arg2) {
		if ((arg2 & 0x2) == 2) {
			if (arg1 < 0) {
				arg1 = 0;
			} else if (arg1 > 127) {
				arg1 = 127;
			}
			return 127 - arg1;
		}
		arg1 = arg1 * (arg0 & 0x7F) >> 7;
		if (arg1 < 2) {
			arg1 = 2;
		} else if (arg1 > 126) {
			arg1 = 126;
		}
		return (arg0 & 0xFF80) + arg1;
	}

	@ObfuscatedName("eb.a(IIIIIII)V")
	public final void drawSimple(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
		int var8 = Pix3D.centerW3D;
		int var9 = Pix3D.centerH3D;
		int var10 = sin[arg0];
		int var11 = cos[arg0];
		int var12 = sin[arg1];
		int var13 = cos[arg1];
		int var14 = sin[arg2];
		int var15 = cos[arg2];
		int var16 = sin[arg3];
		int var17 = cos[arg3];
		int var18 = arg5 * var16 + arg6 * var17 >> 16;
		for (int var19 = 0; var19 < this.vertexCount; var19++) {
			int var20 = this.vertexX[var19];
			int var21 = this.vertexY[var19];
			int var22 = this.vertexZ[var19];
			int var23;
			if (arg2 != 0) {
				var23 = var21 * var14 + var20 * var15 >> 16;
				var21 = var21 * var15 - var20 * var14 >> 16;
				var20 = var23;
			}
			if (arg0 != 0) {
				var23 = var21 * var11 - var22 * var10 >> 16;
				var22 = var21 * var10 + var22 * var11 >> 16;
				var21 = var23;
			}
			if (arg1 != 0) {
				var23 = var22 * var12 + var20 * var13 >> 16;
				var22 = var22 * var13 - var20 * var12 >> 16;
				var20 = var23;
			}
			var20 += arg4;
			var21 += arg5;
			var22 += arg6;
			var23 = var21 * var17 - var22 * var16 >> 16;
			var22 = var21 * var16 + var22 * var17 >> 16;
			vertexScreenZ[var19] = var22 - var18;
			vertexScreenX[var19] = var8 + (var20 << 9) / var22;
			vertexScreenY[var19] = var9 + (var23 << 9) / var22;
			if (this.texturedFaceCount > 0) {
				vertexViewSpaceX[var19] = var20;
				vertexViewSpaceY[var19] = var23;
				vertexViewSpaceZ[var19] = var22;
			}
		}
		try {
			this.draw(false, false, 0);
		} catch (Exception var24) {
		}
	}

	@ObfuscatedName("eb.a(IIIIIIIII)V")
	public final void draw(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, int arg8) {
		int var10 = arg7 * arg4 - arg5 * arg3 >> 16;
		int var11 = arg6 * arg1 + var10 * arg2 >> 16;
		int var12 = this.radius * arg2 >> 16;
		int var13 = var11 + var12;
		if (var13 <= 50 || var11 >= 3500) {
			return;
		}
		int var14 = arg7 * arg3 + arg5 * arg4 >> 16;
		int var15 = var14 - this.radius << 9;
		if (var15 / var13 >= Pix2D.centerW2D) {
			return;
		}
		int var16 = var14 + this.radius << 9;
		if (var16 / var13 <= -Pix2D.centerW2D) {
			return;
		}
		int var17 = arg6 * arg2 - var10 * arg1 >> 16;
		int var18 = this.radius * arg1 >> 16;
		int var19 = var17 + var18 << 9;
		if (var19 / var13 <= -Pix2D.centerH2D) {
			return;
		}
		int var20 = var18 + (this.maxY * arg2 >> 16);
		int var21 = var17 - var20 << 9;
		if (var21 / var13 >= Pix2D.centerH2D) {
			return;
		}
		int var22 = var12 + (this.maxY * arg1 >> 16);
		boolean var23 = false;
		if (var11 - var22 <= 50) {
			var23 = true;
		}
		boolean var24 = false;
		int var25;
		int var26;
		int var27;
		if (arg8 > 0 && checkHover) {
			var25 = var11 - var12;
			if (var25 <= 50) {
				var25 = 50;
			}
			if (var14 > 0) {
				var15 /= var13;
				var16 /= var25;
			} else {
				var16 /= var13;
				var15 /= var25;
			}
			if (var17 > 0) {
				var21 /= var13;
				var19 /= var25;
			} else {
				var19 /= var13;
				var21 /= var25;
			}
			var26 = mouseX - Pix3D.centerW3D;
			var27 = mouseZ - Pix3D.centerH3D;
			if (var26 > var15 && var26 < var16 && var27 > var21 && var27 < var19) {
				if (this.pickable) {
					pickedBitsets[pickedCount++] = arg8;
				} else {
					var24 = true;
				}
			}
		}
		var25 = Pix3D.centerW3D;
		var26 = Pix3D.centerH3D;
		var27 = 0;
		int var28 = 0;
		if (arg0 != 0) {
			var27 = sin[arg0];
			var28 = cos[arg0];
		}
		for (int var29 = 0; var29 < this.vertexCount; var29++) {
			int var30 = this.vertexX[var29];
			int var31 = this.vertexY[var29];
			int var32 = this.vertexZ[var29];
			int var33;
			if (arg0 != 0) {
				var33 = var32 * var27 + var30 * var28 >> 16;
				var32 = var32 * var28 - var30 * var27 >> 16;
				var30 = var33;
			}
			var30 += arg5;
			var31 += arg6;
			var32 += arg7;
			var33 = var32 * arg3 + var30 * arg4 >> 16;
			var32 = var32 * arg4 - var30 * arg3 >> 16;
			var30 = var33;
			var33 = var31 * arg2 - var32 * arg1 >> 16;
			var32 = var31 * arg1 + var32 * arg2 >> 16;
			vertexScreenZ[var29] = var32 - var11;
			if (var32 >= 50) {
				vertexScreenX[var29] = var25 + (var30 << 9) / var32;
				vertexScreenY[var29] = var26 + (var33 << 9) / var32;
			} else {
				vertexScreenX[var29] = -5000;
				var23 = true;
			}
			if (var23 || this.texturedFaceCount > 0) {
				vertexViewSpaceX[var29] = var30;
				vertexViewSpaceY[var29] = var33;
				vertexViewSpaceZ[var29] = var32;
			}
		}
		try {
			this.draw(var23, var24, arg8);
		} catch (Exception var34) {
		}
	}

	@ObfuscatedName("eb.a(ZZI)V")
	public final void draw(boolean arg0, boolean arg1, int arg2) {
		for (int var4 = 0; var4 < this.maxDepth; var4++) {
			tmpDepthFaceCount[var4] = 0;
		}
		int var6;
		int var7;
		int var8;
		int var9;
		int var10;
		int var11;
		int var12;
		for (int var5 = 0; var5 < this.faceCount; var5++) {
			if (this.faceInfo == null || this.faceInfo[var5] != -1) {
				var6 = this.faceVertexA[var5];
				var7 = this.faceVertexB[var5];
				var8 = this.faceVertexC[var5];
				var9 = vertexScreenX[var6];
				var10 = vertexScreenX[var7];
				var11 = vertexScreenX[var8];
				if (arg0 && (var9 == -5000 || var10 == -5000 || var11 == -5000)) {
					faceNearClipped[var5] = true;
					var12 = (vertexScreenZ[var6] + vertexScreenZ[var7] + vertexScreenZ[var8]) / 3 + this.minDepth;
					tmpDepthFaces[var12][tmpDepthFaceCount[var12]++] = var5;
				} else {
					if (arg1 && this.pointWithinTriangle(mouseX, mouseZ, vertexScreenY[var6], vertexScreenY[var7], vertexScreenY[var8], var9, var10, var11)) {
						pickedBitsets[pickedCount++] = arg2;
						arg1 = false;
					}
					if ((var9 - var10) * (vertexScreenY[var8] - vertexScreenY[var7]) - (vertexScreenY[var6] - vertexScreenY[var7]) * (var11 - var10) > 0) {
						faceNearClipped[var5] = false;
						if (var9 >= 0 && var10 >= 0 && var11 >= 0 && var9 <= Pix2D.safeWidth && var10 <= Pix2D.safeWidth && var11 <= Pix2D.safeWidth) {
							faceClippedX[var5] = false;
						} else {
							faceClippedX[var5] = true;
						}
						var12 = (vertexScreenZ[var6] + vertexScreenZ[var7] + vertexScreenZ[var8]) / 3 + this.minDepth;
						tmpDepthFaces[var12][tmpDepthFaceCount[var12]++] = var5;
					}
				}
			}
		}
		if (this.facePriority == null) {
			for (var6 = this.maxDepth - 1; var6 >= 0; var6--) {
				var7 = tmpDepthFaceCount[var6];
				if (var7 > 0) {
					int[] var21 = tmpDepthFaces[var6];
					for (var9 = 0; var9 < var7; var9++) {
						this.drawFace(var21[var9]);
					}
				}
			}
			return;
		}
		for (var6 = 0; var6 < 12; var6++) {
			tmpPriorityFaceCount[var6] = 0;
			tmpPriorityDepthSum[var6] = 0;
		}
		int var13;
		for (var7 = this.maxDepth - 1; var7 >= 0; var7--) {
			var8 = tmpDepthFaceCount[var7];
			if (var8 > 0) {
				int[] var20 = tmpDepthFaces[var7];
				for (var10 = 0; var10 < var8; var10++) {
					var11 = var20[var10];
					var12 = this.facePriority[var11];
					var13 = tmpPriorityFaceCount[var12]++;
					tmpPriorityFaces[var12][var13] = var11;
					if (var12 < 10) {
						tmpPriorityDepthSum[var12] += var7;
					} else if (var12 == 10) {
						tmpPriority10FaceDepth[var13] = var7;
					} else {
						tmpPriority11FaceDepth[var13] = var7;
					}
				}
			}
		}
		var8 = 0;
		if (tmpPriorityFaceCount[1] > 0 || tmpPriorityFaceCount[2] > 0) {
			var8 = (tmpPriorityDepthSum[1] + tmpPriorityDepthSum[2]) / (tmpPriorityFaceCount[1] + tmpPriorityFaceCount[2]);
		}
		var9 = 0;
		if (tmpPriorityFaceCount[3] > 0 || tmpPriorityFaceCount[4] > 0) {
			var9 = (tmpPriorityDepthSum[3] + tmpPriorityDepthSum[4]) / (tmpPriorityFaceCount[3] + tmpPriorityFaceCount[4]);
		}
		var10 = 0;
		if (tmpPriorityFaceCount[6] > 0 || tmpPriorityFaceCount[8] > 0) {
			var10 = (tmpPriorityDepthSum[6] + tmpPriorityDepthSum[8]) / (tmpPriorityFaceCount[6] + tmpPriorityFaceCount[8]);
		}
		var12 = 0;
		var13 = tmpPriorityFaceCount[10];
		int[] var14 = tmpPriorityFaces[10];
		int[] var15 = tmpPriority10FaceDepth;
		if (var12 == var13) {
			var12 = 0;
			var13 = tmpPriorityFaceCount[11];
			var14 = tmpPriorityFaces[11];
			var15 = tmpPriority11FaceDepth;
		}
		if (var12 < var13) {
			var11 = var15[var12];
		} else {
			var11 = -1000;
		}
		for (int var16 = 0; var16 < 10; var16++) {
			while (var16 == 0 && var11 > var8) {
				this.drawFace(var14[var12++]);
				if (var12 == var13 && var14 != tmpPriorityFaces[11]) {
					var12 = 0;
					var13 = tmpPriorityFaceCount[11];
					var14 = tmpPriorityFaces[11];
					var15 = tmpPriority11FaceDepth;
				}
				if (var12 < var13) {
					var11 = var15[var12];
				} else {
					var11 = -1000;
				}
			}
			while (var16 == 3 && var11 > var9) {
				this.drawFace(var14[var12++]);
				if (var12 == var13 && var14 != tmpPriorityFaces[11]) {
					var12 = 0;
					var13 = tmpPriorityFaceCount[11];
					var14 = tmpPriorityFaces[11];
					var15 = tmpPriority11FaceDepth;
				}
				if (var12 < var13) {
					var11 = var15[var12];
				} else {
					var11 = -1000;
				}
			}
			while (var16 == 5 && var11 > var10) {
				this.drawFace(var14[var12++]);
				if (var12 == var13 && var14 != tmpPriorityFaces[11]) {
					var12 = 0;
					var13 = tmpPriorityFaceCount[11];
					var14 = tmpPriorityFaces[11];
					var15 = tmpPriority11FaceDepth;
				}
				if (var12 < var13) {
					var11 = var15[var12];
				} else {
					var11 = -1000;
				}
			}
			int var17 = tmpPriorityFaceCount[var16];
			int[] var18 = tmpPriorityFaces[var16];
			for (int var19 = 0; var19 < var17; var19++) {
				this.drawFace(var18[var19]);
			}
		}
		while (var11 != -1000) {
			this.drawFace(var14[var12++]);
			if (var12 == var13 && var14 != tmpPriorityFaces[11]) {
				var12 = 0;
				var14 = tmpPriorityFaces[11];
				var13 = tmpPriorityFaceCount[11];
				var15 = tmpPriority11FaceDepth;
			}
			if (var12 < var13) {
				var11 = var15[var12];
			} else {
				var11 = -1000;
			}
		}
	}

	@ObfuscatedName("eb.f(I)V")
	public final void drawFace(int arg0) {
		if (faceNearClipped[arg0]) {
			this.drawNearClippedFace(arg0);
			return;
		}
		int var2 = this.faceVertexA[arg0];
		int var3 = this.faceVertexB[arg0];
		int var4 = this.faceVertexC[arg0];
		Pix3D.hclip = faceClippedX[arg0];
		if (this.faceAlpha == null) {
			Pix3D.trans = 0;
		} else {
			Pix3D.trans = this.faceAlpha[arg0];
		}
		int var5;
		if (this.faceInfo == null) {
			var5 = 0;
		} else {
			var5 = this.faceInfo[arg0] & 0x3;
		}
		if (var5 == 0) {
			Pix3D.gouraudTriangle(vertexScreenY[var2], vertexScreenY[var3], vertexScreenY[var4], vertexScreenX[var2], vertexScreenX[var3], vertexScreenX[var4], this.faceColorA[arg0], this.faceColorB[arg0], this.faceColorC[arg0]);
		} else if (var5 == 1) {
			Pix3D.flatTriangle(vertexScreenY[var2], vertexScreenY[var3], vertexScreenY[var4], vertexScreenX[var2], vertexScreenX[var3], vertexScreenX[var4], palette[this.faceColorA[arg0]]);
		} else {
			int var6;
			int var7;
			int var8;
			int var9;
			if (var5 == 2) {
				var6 = this.faceInfo[arg0] >> 2;
				var7 = this.texturedVertexA[var6];
				var8 = this.texturedVertexB[var6];
				var9 = this.texturedVertexC[var6];
				Pix3D.textureTriangle(vertexScreenY[var2], vertexScreenY[var3], vertexScreenY[var4], vertexScreenX[var2], vertexScreenX[var3], vertexScreenX[var4], this.faceColorA[arg0], this.faceColorB[arg0], this.faceColorC[arg0], vertexViewSpaceX[var7], vertexViewSpaceX[var8], vertexViewSpaceX[var9], vertexViewSpaceY[var7], vertexViewSpaceY[var8], vertexViewSpaceY[var9], vertexViewSpaceZ[var7], vertexViewSpaceZ[var8], vertexViewSpaceZ[var9], this.faceColor[arg0]);
			} else if (var5 == 3) {
				var6 = this.faceInfo[arg0] >> 2;
				var7 = this.texturedVertexA[var6];
				var8 = this.texturedVertexB[var6];
				var9 = this.texturedVertexC[var6];
				Pix3D.textureTriangle(vertexScreenY[var2], vertexScreenY[var3], vertexScreenY[var4], vertexScreenX[var2], vertexScreenX[var3], vertexScreenX[var4], this.faceColorA[arg0], this.faceColorA[arg0], this.faceColorA[arg0], vertexViewSpaceX[var7], vertexViewSpaceX[var8], vertexViewSpaceX[var9], vertexViewSpaceY[var7], vertexViewSpaceY[var8], vertexViewSpaceY[var9], vertexViewSpaceZ[var7], vertexViewSpaceZ[var8], vertexViewSpaceZ[var9], this.faceColor[arg0]);
			}
		}
	}

	@ObfuscatedName("eb.g(I)V")
	public final void drawNearClippedFace(int arg0) {
		int var2 = Pix3D.centerW3D;
		int var3 = Pix3D.centerH3D;
		int var4 = 0;
		int var5 = this.faceVertexA[arg0];
		int var6 = this.faceVertexB[arg0];
		int var7 = this.faceVertexC[arg0];
		int var8 = vertexViewSpaceZ[var5];
		int var9 = vertexViewSpaceZ[var6];
		int var10 = vertexViewSpaceZ[var7];
		int var11;
		int var12;
		int var13;
		int var14;
		if (var8 >= 50) {
			clippedX[var4] = vertexScreenX[var5];
			clippedY[var4] = vertexScreenY[var5];
			clippedColor[var4++] = this.faceColorA[arg0];
		} else {
			var11 = vertexViewSpaceX[var5];
			var12 = vertexViewSpaceY[var5];
			var13 = this.faceColorA[arg0];
			if (var10 >= 50) {
				var14 = (50 - var8) * reciprical16[var10 - var8];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var7] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var7] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorC[arg0] - var13) * var14 >> 16);
			}
			if (var9 >= 50) {
				var14 = (50 - var8) * reciprical16[var9 - var8];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var6] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var6] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorB[arg0] - var13) * var14 >> 16);
			}
		}
		if (var9 >= 50) {
			clippedX[var4] = vertexScreenX[var6];
			clippedY[var4] = vertexScreenY[var6];
			clippedColor[var4++] = this.faceColorB[arg0];
		} else {
			var11 = vertexViewSpaceX[var6];
			var12 = vertexViewSpaceY[var6];
			var13 = this.faceColorB[arg0];
			if (var8 >= 50) {
				var14 = (50 - var9) * reciprical16[var8 - var9];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var5] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var5] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorA[arg0] - var13) * var14 >> 16);
			}
			if (var10 >= 50) {
				var14 = (50 - var9) * reciprical16[var10 - var9];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var7] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var7] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorC[arg0] - var13) * var14 >> 16);
			}
		}
		if (var10 >= 50) {
			clippedX[var4] = vertexScreenX[var7];
			clippedY[var4] = vertexScreenY[var7];
			clippedColor[var4++] = this.faceColorC[arg0];
		} else {
			var11 = vertexViewSpaceX[var7];
			var12 = vertexViewSpaceY[var7];
			var13 = this.faceColorC[arg0];
			if (var9 >= 50) {
				var14 = (50 - var10) * reciprical16[var9 - var10];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var6] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var6] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorB[arg0] - var13) * var14 >> 16);
			}
			if (var8 >= 50) {
				var14 = (50 - var10) * reciprical16[var8 - var10];
				clippedX[var4] = var2 + (var11 + ((vertexViewSpaceX[var5] - var11) * var14 >> 16) << 9) / 50;
				clippedY[var4] = var3 + (var12 + ((vertexViewSpaceY[var5] - var12) * var14 >> 16) << 9) / 50;
				clippedColor[var4++] = var13 + ((this.faceColorA[arg0] - var13) * var14 >> 16);
			}
		}
		var11 = clippedX[0];
		var12 = clippedX[1];
		var13 = clippedX[2];
		var14 = clippedY[0];
		int var15 = clippedY[1];
		int var16 = clippedY[2];
		if ((var11 - var12) * (var16 - var15) - (var14 - var15) * (var13 - var12) <= 0) {
			return;
		}
		Pix3D.hclip = false;
		int var17;
		int var18;
		int var19;
		int var20;
		int var21;
		if (var4 == 3) {
			if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Pix2D.safeWidth || var12 > Pix2D.safeWidth || var13 > Pix2D.safeWidth) {
				Pix3D.hclip = true;
			}
			if (this.faceInfo == null) {
				var17 = 0;
			} else {
				var17 = this.faceInfo[arg0] & 0x3;
			}
			if (var17 == 0) {
				Pix3D.gouraudTriangle(var14, var15, var16, var11, var12, var13, clippedColor[0], clippedColor[1], clippedColor[2]);
			} else if (var17 == 1) {
				Pix3D.flatTriangle(var14, var15, var16, var11, var12, var13, palette[this.faceColorA[arg0]]);
			} else if (var17 == 2) {
				var18 = this.faceInfo[arg0] >> 2;
				var19 = this.texturedVertexA[var18];
				var20 = this.texturedVertexB[var18];
				var21 = this.texturedVertexC[var18];
				Pix3D.textureTriangle(var14, var15, var16, var11, var12, var13, clippedColor[0], clippedColor[1], clippedColor[2], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
			} else if (var17 == 3) {
				var18 = this.faceInfo[arg0] >> 2;
				var19 = this.texturedVertexA[var18];
				var20 = this.texturedVertexB[var18];
				var21 = this.texturedVertexC[var18];
				Pix3D.textureTriangle(var14, var15, var16, var11, var12, var13, this.faceColorA[arg0], this.faceColorA[arg0], this.faceColorA[arg0], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
			}
		}
		if (var4 != 4) {
			return;
		}
		if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Pix2D.safeWidth || var12 > Pix2D.safeWidth || var13 > Pix2D.safeWidth || clippedX[3] < 0 || clippedX[3] > Pix2D.safeWidth) {
			Pix3D.hclip = true;
		}
		if (this.faceInfo == null) {
			var17 = 0;
		} else {
			var17 = this.faceInfo[arg0] & 0x3;
		}
		if (var17 == 0) {
			Pix3D.gouraudTriangle(var14, var15, var16, var11, var12, var13, clippedColor[0], clippedColor[1], clippedColor[2]);
			Pix3D.gouraudTriangle(var14, var16, clippedY[3], var11, var13, clippedX[3], clippedColor[0], clippedColor[2], clippedColor[3]);
			return;
		}
		if (var17 == 1) {
			var18 = palette[this.faceColorA[arg0]];
			Pix3D.flatTriangle(var14, var15, var16, var11, var12, var13, var18);
			Pix3D.flatTriangle(var14, var16, clippedY[3], var11, var13, clippedX[3], var18);
			return;
		}
		if (var17 == 2) {
			var18 = this.faceInfo[arg0] >> 2;
			var19 = this.texturedVertexA[var18];
			var20 = this.texturedVertexB[var18];
			var21 = this.texturedVertexC[var18];
			Pix3D.textureTriangle(var14, var15, var16, var11, var12, var13, clippedColor[0], clippedColor[1], clippedColor[2], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
			Pix3D.textureTriangle(var14, var16, clippedY[3], var11, var13, clippedX[3], clippedColor[0], clippedColor[2], clippedColor[3], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
			return;
		}
		if (var17 != 3) {
			return;
		}
		var18 = this.faceInfo[arg0] >> 2;
		var19 = this.texturedVertexA[var18];
		var20 = this.texturedVertexB[var18];
		var21 = this.texturedVertexC[var18];
		Pix3D.textureTriangle(var14, var15, var16, var11, var12, var13, this.faceColorA[arg0], this.faceColorA[arg0], this.faceColorA[arg0], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
		Pix3D.textureTriangle(var14, var16, clippedY[3], var11, var13, clippedX[3], this.faceColorA[arg0], this.faceColorA[arg0], this.faceColorA[arg0], vertexViewSpaceX[var19], vertexViewSpaceX[var20], vertexViewSpaceX[var21], vertexViewSpaceY[var19], vertexViewSpaceY[var20], vertexViewSpaceY[var21], vertexViewSpaceZ[var19], vertexViewSpaceZ[var20], vertexViewSpaceZ[var21], this.faceColor[arg0]);
	}

	@ObfuscatedName("eb.a(IIIIIIII)Z")
	public final boolean pointWithinTriangle(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
		if (arg1 < arg2 && arg1 < arg3 && arg1 < arg4) {
			return false;
		} else if (arg1 > arg2 && arg1 > arg3 && arg1 > arg4) {
			return false;
		} else if (arg0 < arg5 && arg0 < arg6 && arg0 < arg7) {
			return false;
		} else {
			return arg0 <= arg5 || arg0 <= arg6 || arg0 <= arg7;
		}
	}
}
