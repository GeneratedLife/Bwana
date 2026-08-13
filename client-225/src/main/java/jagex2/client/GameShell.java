package jagex2.client;

import deob.ObfuscatedName;
import jagex2.graphics.Pix32;
import jagex2.graphics.PixMap;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@ObfuscatedName("a")
public class GameShell implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener {

	@ObfuscatedName("a.a")
	public boolean field1 = false;

	@ObfuscatedName("a.b")
	public boolean field2 = false;

	@ObfuscatedName("a.c")
	public int field3;

	@ObfuscatedName("a.d")
	public boolean field4 = false;

	@ObfuscatedName("a.e")
	public byte field5 = 3;

	@ObfuscatedName("a.f")
	public int field6 = 27808;

	@ObfuscatedName("a.g")
	public int state;

	@ObfuscatedName("a.h")
	public int deltime = 20;

	@ObfuscatedName("a.i")
	public int mindel = 1;

	@ObfuscatedName("a.j")
	public long[] otim = new long[10];

	@ObfuscatedName("a.k")
	public int fps;

	@ObfuscatedName("a.l")
	public int screenWidth;

	@ObfuscatedName("a.m")
	public int screenHeight;

	@ObfuscatedName("a.n")
	public Graphics graphics;

	@ObfuscatedName("a.o")
	public PixMap drawArea;

	@ObfuscatedName("a.p")
	public Pix32[] temp = new Pix32[6];

	@ObfuscatedName("a.q")
	public ViewBox frame;

	@ObfuscatedName("a.r")
	public boolean refresh = true;

	@ObfuscatedName("a.s")
	public int idleCycles;

	@ObfuscatedName("a.t")
	public int mouseButton;

	@ObfuscatedName("a.u")
	public int mouseX;

	@ObfuscatedName("a.v")
	public int mouseY;

	@ObfuscatedName("a.w")
	public int mouseClickButton;

	@ObfuscatedName("a.x")
	public int mouseClickX;

	@ObfuscatedName("a.y")
	public int mouseClickY;

	@ObfuscatedName("a.z")
	public int[] actionKey = new int[128];

	@ObfuscatedName("a.A")
	public int[] keyQueue = new int[128];

	@ObfuscatedName("a.B")
	public int keyQueueReadPos;

	@ObfuscatedName("a.C")
	public int keyQueueWritePos;

	@ObfuscatedName("a.D")
	public static int field30;

	@ObfuscatedName("a.a(III)V")
	public final void initApplication(int arg0, int arg1, int arg2) {
		if (arg2 != 0) {
			this.field3 = 411;
		}
		this.screenWidth = arg1;
		this.screenHeight = arg0;
		this.frame = new ViewBox(this.screenHeight, 35731, this, this.screenWidth);
		this.graphics = this.getBaseComponent(this.field5).getGraphics();
		this.drawArea = new PixMap(this.getBaseComponent(this.field5), this.screenWidth, 299, this.screenHeight);
		this.startThread(this, 1);
	}

	@ObfuscatedName("a.a(IZI)V")
	public final void initApplet(int arg0, boolean arg1, int arg2) {
		this.screenWidth = arg2;
		this.screenHeight = arg0;
		this.graphics = this.getBaseComponent(this.field5).getGraphics();
		this.drawArea = new PixMap(this.getBaseComponent(this.field5), this.screenWidth, 299, this.screenHeight);
		if (arg1) {
			this.field3 = -370;
		}
		this.startThread(this, 1);
	}

	public void run() {
		this.getBaseComponent(this.field5).addMouseListener(this);
		this.getBaseComponent(this.field5).addMouseMotionListener(this);
		this.getBaseComponent(this.field5).addKeyListener(this);
		this.getBaseComponent(this.field5).addFocusListener(this);
		if (this.frame != null) {
			this.frame.addWindowListener(this);
		}
		this.drawProgress(true, "Loading...", 0);
		this.load();
		int var3 = 0;
		int var4 = 256;
		int var5 = 1;
		int var6 = 0;
		for (int var7 = 0; var7 < 10; var7++) {
			this.otim[var7] = System.currentTimeMillis();
		}
		long var1 = System.currentTimeMillis();
		while (this.state >= 0) {
			if (this.state > 0) {
				this.state--;
				if (this.state == 0) {
					this.shutdown(-652);
					return;
				}
			}
			int var8 = var4;
			int var9 = var5;
			var4 = 300;
			var5 = 1;
			var1 = System.currentTimeMillis();
			if (this.otim[var3] == 0L) {
				var4 = var8;
				var5 = var9;
			} else if (var1 > this.otim[var3]) {
				var4 = (int) ((long) (this.deltime * 2560) / (var1 - this.otim[var3]));
			}
			if (var4 < 25) {
				var4 = 25;
			}
			if (var4 > 256) {
				var4 = 256;
				var5 = (int) ((long) this.deltime - (var1 - this.otim[var3]) / 10L);
			}
			this.otim[var3] = var1;
			var3 = (var3 + 1) % 10;
			if (var5 > 1) {
				for (int var10 = 0; var10 < 10; var10++) {
					if (this.otim[var10] != 0L) {
						this.otim[var10] += var5;
					}
				}
			}
			if (var5 < this.mindel) {
				var5 = this.mindel;
			}
			try {
				Thread.sleep((long) var5);
			} catch (InterruptedException var11) {
			}
			while (var6 < 256) {
				this.update(437);
				this.mouseClickButton = 0;
				this.keyQueueReadPos = this.keyQueueWritePos;
				var6 += var4;
			}
			var6 &= 0xFF;
			if (this.deltime > 0) {
				this.fps = var4 * 1000 / (this.deltime * 256);
			}
			this.draw(false);
		}
		if (this.state == -1) {
			this.shutdown(-652);
		}
	}

	@ObfuscatedName("a.a(I)V")
	public final void shutdown(int arg0) {
		while (arg0 >= 0) {
			this.field4 = !this.field4;
		}
		this.state = -2;
		this.unload((byte) -28);
		try {
			Thread.sleep(1000L);
		} catch (Exception var3) {
		}
		try {
			System.exit(0);
		} catch (Throwable var2) {
		}
	}

	@ObfuscatedName("a.a(II)V")
	public final void setFramerate(int arg0, int arg1) {
		this.deltime = 1000 / arg1;
		if (arg0 <= 0) {
			this.field1 = !this.field1;
		}
	}

	public final void start() {
		if (this.state >= 0) {
			this.state = 0;
		}
	}

	public final void stop() {
		if (this.state >= 0) {
			this.state = 4000 / this.deltime;
		}
	}

	public final void destroy() {
		this.state = -1;
		try {
			Thread.sleep(5000L);
		} catch (Exception var1) {
		}
		if (this.state == -1) {
			this.shutdown(-652);
		}
	}

	public final void update(Graphics arg0) {
		if (this.graphics == null) {
			this.graphics = arg0;
		}
		this.refresh = true;
		this.refresh(3);
	}

	public final void paint(Graphics arg0) {
		if (this.graphics == null) {
			this.graphics = arg0;
		}
		this.refresh = true;
		this.refresh(3);
	}

	public final void mousePressed(MouseEvent arg0) {
		// events arrive on the canvas, so they are already canvas-relative
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		this.idleCycles = 0;
		this.mouseClickX = var2;
		this.mouseClickY = var3;
		if (arg0.isMetaDown()) {
			this.mouseClickButton = 2;
			this.mouseButton = 2;
		} else {
			this.mouseClickButton = 1;
			this.mouseButton = 1;
		}
		if (InputTracking.enabled) {
			InputTracking.mousePressed(var2, arg0.isMetaDown() ? 1 : 0, var3, (byte) 4);
		}
	}

	public final void mouseReleased(MouseEvent arg0) {
		this.idleCycles = 0;
		this.mouseButton = 0;
		if (InputTracking.enabled) {
			InputTracking.mouseReleased(arg0.isMetaDown() ? 1 : 0, 0);
		}
	}

	public final void mouseClicked(MouseEvent arg0) {
	}

	public final void mouseEntered(MouseEvent arg0) {
		if (InputTracking.enabled) {
			InputTracking.mouseEntered(-657);
		}
	}

	public final void mouseExited(MouseEvent arg0) {
		if (InputTracking.enabled) {
			InputTracking.mouseExited(false);
		}
	}

	public final void mouseDragged(MouseEvent arg0) {
		// events arrive on the canvas, so they are already canvas-relative
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		this.idleCycles = 0;
		this.mouseX = var2;
		this.mouseY = var3;
		if (InputTracking.enabled) {
			InputTracking.mouseMoved(var3, true, var2);
		}
	}

	public final void mouseMoved(MouseEvent arg0) {
		// events arrive on the canvas, so they are already canvas-relative
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		this.idleCycles = 0;
		this.mouseX = var2;
		this.mouseY = var3;
		if (InputTracking.enabled) {
			InputTracking.mouseMoved(var3, true, var2);
		}
	}

	public final void keyPressed(KeyEvent arg0) {
		this.idleCycles = 0;
		int var2 = arg0.getKeyCode();
		int var3 = arg0.getKeyChar();
		if (var3 < 30) {
			var3 = 0;
		}
		if (var2 == 37) {
			var3 = 1;
		}
		if (var2 == 39) {
			var3 = 2;
		}
		if (var2 == 38) {
			var3 = 3;
		}
		if (var2 == 40) {
			var3 = 4;
		}
		if (var2 == 17) {
			var3 = 5;
		}
		if (var2 == 8) {
			var3 = 8;
		}
		if (var2 == 127) {
			var3 = 8;
		}
		if (var2 == 9) {
			var3 = 9;
		}
		if (var2 == 10) {
			var3 = 10;
		}
		if (var2 >= 112 && var2 <= 123) {
			var3 = var2 + 1008 - 112;
		}
		if (var2 == 36) {
			var3 = 1000;
		}
		if (var2 == 35) {
			var3 = 1001;
		}
		if (var2 == 33) {
			var3 = 1002;
		}
		if (var2 == 34) {
			var3 = 1003;
		}
		if (var3 > 0 && var3 < 128) {
			this.actionKey[var3] = 1;
		}
		if (var3 > 4) {
			this.keyQueue[this.keyQueueWritePos] = var3;
			this.keyQueueWritePos = this.keyQueueWritePos + 1 & 0x7F;
		}
		if (InputTracking.enabled) {
			InputTracking.keyPressed(var3, true);
		}
	}

	public final void keyReleased(KeyEvent arg0) {
		this.idleCycles = 0;
		int var2 = arg0.getKeyCode();
		char var3 = arg0.getKeyChar();
		if (var3 < 30) {
			var3 = 0;
		}
		if (var2 == 37) {
			var3 = 1;
		}
		if (var2 == 39) {
			var3 = 2;
		}
		if (var2 == 38) {
			var3 = 3;
		}
		if (var2 == 40) {
			var3 = 4;
		}
		if (var2 == 17) {
			var3 = 5;
		}
		if (var2 == 8) {
			var3 = '\b';
		}
		if (var2 == 127) {
			var3 = '\b';
		}
		if (var2 == 9) {
			var3 = '\t';
		}
		if (var2 == 10) {
			var3 = '\n';
		}
		if (var3 > 0 && var3 < 128) {
			this.actionKey[var3] = 0;
		}
		if (InputTracking.enabled) {
			InputTracking.keyReleased(var3, 1);
		}
	}

	public final void keyTyped(KeyEvent arg0) {
	}

	public final void focusGained(FocusEvent arg0) {
		this.refresh = true;
		this.refresh(3);
		if (InputTracking.enabled) {
			InputTracking.focusGained(-936);
		}
	}

	public final void focusLost(FocusEvent arg0) {
		if (InputTracking.enabled) {
			InputTracking.focusLost(0);
		}
	}

	@ObfuscatedName("a.a(Z)I")
	public final int pollKey(boolean arg0) {
		int var2 = -1;
		if (arg0) {
			return 2;
		}
		if (this.keyQueueWritePos != this.keyQueueReadPos) {
			var2 = this.keyQueue[this.keyQueueReadPos];
			this.keyQueueReadPos = this.keyQueueReadPos + 1 & 0x7F;
		}
		return var2;
	}

	public final void windowActivated(WindowEvent arg0) {
	}

	public final void windowClosed(WindowEvent arg0) {
	}

	public final void windowClosing(WindowEvent arg0) {
		this.destroy();
	}

	public final void windowDeactivated(WindowEvent arg0) {
	}

	public final void windowDeiconified(WindowEvent arg0) {
	}

	public final void windowIconified(WindowEvent arg0) {
	}

	public final void windowOpened(WindowEvent arg0) {
	}

	@ObfuscatedName("a.a()V")
	public void load() {
	}

	@ObfuscatedName("a.b(I)V")
	public void update(int arg0) {
		if (arg0 <= 0) {
			this.field2 = !this.field2;
		}
	}

	@ObfuscatedName("a.a(B)V")
	public void unload(byte arg0) {
		if (arg0 != -28) {
			this.field6 = -407;
		}
	}

	@ObfuscatedName("a.b(Z)V")
	public void draw(boolean arg0) {
		if (arg0) {
			this.field4 = !this.field4;
		}
	}

	@ObfuscatedName("a.c(I)V")
	public void refresh(int arg0) {
		if (arg0 < 3 || arg0 > 3) {
			this.field2 = !this.field2;
		}
	}

	@ObfuscatedName("a.b(B)Ljava/awt/Component;")
	public Component getBaseComponent(byte arg0) {
		if (arg0 != 3) {
			throw new NullPointerException();
		}
		return this.frame == null ? null : this.frame.getCanvas();
	}

	@ObfuscatedName("a.a(Ljava/lang/Runnable;I)V")
	public void startThread(Runnable arg0, int arg1) {
		Thread var3 = new Thread(arg0);
		var3.start();
		var3.setPriority(arg1);
	}

	@ObfuscatedName("a.a(ZLjava/lang/String;I)V")
	public void drawProgress(boolean arg0, String arg1, int arg2) {
		while (this.graphics == null) {
			this.graphics = this.getBaseComponent(this.field5).getGraphics();
			try {
				this.getBaseComponent(this.field5).repaint();
			} catch (Exception var10) {
			}
			try {
				Thread.sleep(1000L);
			} catch (Exception var9) {
			}
		}
		Font var4 = new Font("Helvetica", 1, 13);
		FontMetrics var5 = this.getBaseComponent(this.field5).getFontMetrics(var4);
		Font var6 = new Font("Helvetica", 0, 13);
		this.getBaseComponent(this.field5).getFontMetrics(var6);
		if (this.refresh) {
			this.graphics.setColor(Color.black);
			this.graphics.fillRect(0, 0, this.screenWidth, this.screenHeight);
			this.refresh = false;
		}
		Color var7 = new Color(140, 17, 17);
		int var8 = this.screenHeight / 2 - 18;
		this.graphics.setColor(var7);
		this.graphics.drawRect(this.screenWidth / 2 - 152, var8, 304, 34);
		this.graphics.fillRect(this.screenWidth / 2 - 150, var8 + 2, arg2 * 3, 30);
		this.graphics.setColor(Color.black);
		this.graphics.fillRect(this.screenWidth / 2 - 150 + arg2 * 3, var8 + 2, 300 - arg2 * 3, 30);
		this.graphics.setFont(var4);
		this.graphics.setColor(Color.white);
		if (arg0) {
			this.graphics.drawString(arg1, (this.screenWidth - var5.stringWidth(arg1)) / 2, var8 + 22);
		}
	}
}
