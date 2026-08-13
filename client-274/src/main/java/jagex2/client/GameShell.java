package jagex2.client;

import deob.ObfuscatedName;
import jagex2.graphics.Pix32;
import jagex2.graphics.PixMap;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

@ObfuscatedName("a")
public class GameShell extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener {

	@ObfuscatedName("a.f")
	public int state;

	@ObfuscatedName("a.g")
	public int deltime = 20;

	@ObfuscatedName("a.h")
	public int mindel = 1;

	@ObfuscatedName("a.i")
	public final long[] otim = new long[10];

	@ObfuscatedName("a.j")
	public int fps;

	@ObfuscatedName("a.k")
	public boolean debug = false;

	@ObfuscatedName("a.l")
	public int sHei;

	@ObfuscatedName("a.m")
	public int sWid;

	@ObfuscatedName("a.n")
	public Graphics graphics;

	@ObfuscatedName("a.o")
	public PixMap drawArea;

	@ObfuscatedName("a.p")
	public final Pix32[] drawAreas = new Pix32[6];

	@ObfuscatedName("a.q")
	public ViewBox frame;

	@ObfuscatedName("a.r")
	public boolean fullredraw = true;

	@ObfuscatedName("a.s")
	public boolean focus = true;

	@ObfuscatedName("a.t")
	public int idleTimer;

	@ObfuscatedName("a.u")
	public int mouseButton;

	@ObfuscatedName("a.v")
	public int mouseX;

	@ObfuscatedName("a.w")
	public int mouseY;

	@ObfuscatedName("a.x")
	public int nextMouseClickButton;

	@ObfuscatedName("a.y")
	public int nextMouseClickX;

	@ObfuscatedName("a.z")
	public int nextMouseClickY;

	@ObfuscatedName("a.F")
	public final int[] keyHeld = new int[128];

	@ObfuscatedName("a.G")
	public final int[] keyQueue = new int[128];

	@ObfuscatedName("a.B")
	public int mouseClickButton;

	@ObfuscatedName("a.C")
	public int mouseClickX;

	@ObfuscatedName("a.D")
	public int mouseClickY;

	@ObfuscatedName("a.H")
	public int keyQueueReadPos;

	@ObfuscatedName("a.I")
	public int keyQueueWritePos;

	@ObfuscatedName("a.A")
	public long nextMouseClickTime;

	@ObfuscatedName("a.E")
	public long mouseClickTime;

	@ObfuscatedName("a.a(ZII)V")
	public final void initApplication(int arg1, int arg2) {
		this.setPreferredSize(new Dimension(arg1, arg2));
		sHei = arg1;
		sWid = arg2;
		frame = new ViewBox(this, sHei, sWid);
		graphics = getBaseComponent().getGraphics();
		drawArea = new PixMap(sWid, sHei, getBaseComponent());
		startThread(this, 1);
	}

	@ObfuscatedName("a.a(IIB)V")
	public final void initApplet(int arg0, int arg1) {
		sHei = arg1;
		sWid = arg0;
		graphics = getBaseComponent().getGraphics();
		drawArea = new PixMap(sWid, sHei, getBaseComponent());
		startThread(this, 1);
	}

	@Override
	public void run() {
		getBaseComponent().addMouseListener(this);
		getBaseComponent().addMouseMotionListener(this);
		getBaseComponent().addKeyListener(this);
		getBaseComponent().addFocusListener(this);
		if (frame != null) {
			frame.addWindowListener(this);
		}
		messageBox("Loading...", 0);
		maininit();
		int var1 = 0;
		int var2 = 256;
		int var3 = 1;
		int var4 = 0;
		int var5 = 0;
		for (int var6 = 0; var6 < 10; var6++) {
			otim[var6] = System.currentTimeMillis();
		}
		long var7 = System.currentTimeMillis();
		while (true) {
			long var11;
			do {
				if (state < 0) {
					if (state == -1) {
						shutdown();
					}
					return;
				}
				if (state > 0) {
					state--;
					if (state == 0) {
						shutdown();
						return;
					}
				}
				int var9 = var2;
				int var10 = var3;
				var2 = 300;
				var3 = 1;
				var11 = System.currentTimeMillis();
				if (otim[var1] == 0L) {
					var2 = var9;
					var3 = var10;
				} else if (var11 > otim[var1]) {
					var2 = (int) ((long) (deltime * 2560) / (var11 - otim[var1]));
				}
				if (var2 < 25) {
					var2 = 25;
				}
				if (var2 > 256) {
					var2 = 256;
					var3 = (int) ((long) deltime - (var11 - otim[var1]) / 10L);
				}
				if (var3 > deltime) {
					var3 = deltime;
				}
				otim[var1] = var11;
				var1 = (var1 + 1) % 10;
				if (var3 > 1) {
					for (int var13 = 0; var13 < 10; var13++) {
						if (otim[var13] != 0L) {
							otim[var13] += var3;
						}
					}
				}
				if (var3 < mindel) {
					var3 = mindel;
				}
				try {
					Thread.sleep((long) var3);
				} catch (InterruptedException var16) {
					var5++;
				}
				while (var4 < 256) {
					mouseClickButton = nextMouseClickButton;
					mouseClickX = nextMouseClickX;
					mouseClickY = nextMouseClickY;
					mouseClickTime = nextMouseClickTime;
					nextMouseClickButton = 0;
					mainloop();
					keyQueueReadPos = keyQueueWritePos;
					var4 += var2;
				}
				var4 &= 0xFF;
				if (deltime > 0) {
					fps = var2 * 1000 / (deltime * 256);
				}
				maindraw();
			} while (!debug);
			System.out.println("ntime:" + var11);
			for (int var14 = 0; var14 < 10; var14++) {
				int var15 = (var1 + 20 - var14 - 1) % 10;
				System.out.println("otim" + var15 + ":" + otim[var15]);
			}
			System.out.println("fps:" + fps + " ratio:" + var2 + " count:" + var4);
			System.out.println("del:" + var3 + " deltime:" + deltime + " mindel:" + mindel);
			System.out.println("intex:" + var5 + " opos:" + var1);
			debug = false;
			var5 = 0;
		}
	}

	@ObfuscatedName("a.a(I)V")
	public void shutdown() {
		state = -2;
		unload();
		if (frame == null) {
			return;
		}
		try {
			Thread.sleep(1000L);
		} catch (Exception var4) {
		}
		try {
			System.exit(0);
		} catch (Throwable var3) {
		}
	}

	@ObfuscatedName("a.a(II)V")
	public final void setFramerate(int arg1) {
		deltime = 1000 / arg1;
	}

	@Override
	public final void start() {
		if (state >= 0) {
			state = 0;
		}
	}

	@Override
	public final void stop() {
		if (state >= 0) {
			state = 4000 / deltime;
		}
	}

	@Override
	public final void destroy() {
		state = -1;
		try {
			Thread.sleep(5000L);
		} catch (Exception var1) {
		}
		if (state == -1) {
			shutdown();
		}
	}

	@Override
	public final void update(Graphics arg0) {
		if (graphics == null) {
			graphics = arg0;
		}
		fullredraw = true;
		refresh();
	}

	@Override
	public final void paint(Graphics arg0) {
		if (graphics == null) {
			graphics = arg0;
		}
		fullredraw = true;
		refresh();
	}

	@Override
	public final void mousePressed(MouseEvent arg0) {
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		// if (frame != null) {
		// 	var2 -= 4;
		// 	var3 -= 22;
		// }
		idleTimer = 0;
		nextMouseClickX = var2;
		nextMouseClickY = var3;
		nextMouseClickTime = System.currentTimeMillis();
		// if (arg0.isMetaDown()) {
		if (arg0.getButton() == MouseEvent.BUTTON3) {
			nextMouseClickButton = 2;
			mouseButton = 2;
		} else {
			nextMouseClickButton = 1;
			mouseButton = 1;
		}
	}

	@Override
	public final void mouseReleased(MouseEvent arg0) {
		idleTimer = 0;
		mouseButton = 0;
	}

	@Override
	public final void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public final void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public final void mouseExited(MouseEvent arg0) {
		idleTimer = 0;
		mouseX = -1;
		mouseY = -1;
	}

	@Override
	public final void mouseDragged(MouseEvent arg0) {
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		// if (frame != null) {
		// 	var2 -= 4;
		// 	var3 -= 22;
		// }
		idleTimer = 0;
		mouseX = var2;
		mouseY = var3;
	}

	@Override
	public final void mouseMoved(MouseEvent arg0) {
		int var2 = arg0.getX();
		int var3 = arg0.getY();
		// if (frame != null) {
		// 	var2 -= 4;
		// 	var3 -= 22;
		// }
		idleTimer = 0;
		mouseX = var2;
		mouseY = var3;
	}

	@Override
	public final void keyPressed(KeyEvent arg0) {
		idleTimer = 0;
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
			keyHeld[var3] = 1;
		}
		if (var3 > 4) {
			keyQueue[keyQueueWritePos] = var3;
			keyQueueWritePos = keyQueueWritePos + 1 & 0x7F;
		}
	}

	@Override
	public final void keyReleased(KeyEvent arg0) {
		idleTimer = 0;
		int var2 = arg0.getKeyCode();
		char var3 = arg0.getKeyChar();
		if (var3 < '\u001E') {
			var3 = '\u0000';
		}
		if (var2 == 37) {
			var3 = '\u0001';
		}
		if (var2 == 39) {
			var3 = '\u0002';
		}
		if (var2 == 38) {
			var3 = '\u0003';
		}
		if (var2 == 40) {
			var3 = '\u0004';
		}
		if (var2 == 17) {
			var3 = '\u0005';
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
		if (var3 > '\u0000' && var3 < '\u0080') {
			keyHeld[var3] = 0;
		}
	}

	@Override
	public final void keyTyped(KeyEvent arg0) {
	}

	@ObfuscatedName("a.a(B)I")
	public final int pollKey() {
		int var2 = -1;
		if (keyQueueWritePos != keyQueueReadPos) {
			var2 = keyQueue[keyQueueReadPos];
			keyQueueReadPos = keyQueueReadPos + 1 & 0x7F;
		}
		return var2;
	}

	@Override
	public final void focusGained(FocusEvent arg0) {
		focus = true;
		fullredraw = true;
		refresh();
	}

	@Override
	public final void focusLost(FocusEvent arg0) {
		focus = false;
		for (int var2 = 0; var2 < 128; var2++) {
			keyHeld[var2] = 0;
		}
	}

	@Override
	public final void windowActivated(WindowEvent arg0) {
	}

	@Override
	public final void windowClosed(WindowEvent arg0) {
	}

	@Override
	public final void windowClosing(WindowEvent arg0) {
		destroy();
	}

	@Override
	public final void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public final void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public final void windowIconified(WindowEvent arg0) {
	}

	@Override
	public final void windowOpened(WindowEvent arg0) {
	}

	@ObfuscatedName("a.a()V")
	public void maininit() {
	}

	@ObfuscatedName("a.b(I)V")
	public void mainloop() {
	}

	@ObfuscatedName("a.c(I)V")
	public void unload() {
	}

	@ObfuscatedName("a.a(Z)V")
	public void maindraw() {
	}

	@ObfuscatedName("a.d(I)V")
	public void refresh() {
	}

	@ObfuscatedName("a.b(B)Ljava/awt/Component;")
	public Component getBaseComponent() {
		// return frame == null ? this : frame;
		return this;
	}

	@ObfuscatedName("a.a(Ljava/lang/Runnable;I)V")
	public void startThread(Runnable arg0, int arg1) {
		Thread var3 = new Thread(arg0);
		var3.start();
		var3.setPriority(arg1);
	}

	@ObfuscatedName("a.a(Ljava/lang/String;II)V")
	public void messageBox(String arg0, int arg1) {
		while (graphics == null) {
			graphics = getBaseComponent().getGraphics();
			try {
				getBaseComponent().repaint();
			} catch (Exception var11) {
			}
			try {
				Thread.sleep(1000L);
			} catch (Exception var10) {
			}
		}
		Font var4 = new Font("Helvetica", 1, 13);
		FontMetrics var6 = getBaseComponent().getFontMetrics(var4);
		Font var7 = new Font("Helvetica", 0, 13);
		getBaseComponent().getFontMetrics(var7);
		if (fullredraw) {
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, sHei, sWid);
			fullredraw = false;
		}
		Color var8 = new Color(140, 17, 17);
		int var9 = sWid / 2 - 18;
		graphics.setColor(var8);
		graphics.drawRect(sHei / 2 - 152, var9, 304, 34);
		graphics.fillRect(sHei / 2 - 150, var9 + 2, arg1 * 3, 30);
		graphics.setColor(Color.black);
		graphics.fillRect(sHei / 2 + arg1 * 3 - 150, var9 + 2, 300 - arg1 * 3, 30);
		graphics.setFont(var4);
		graphics.setColor(Color.white);
		graphics.drawString(arg0, (sHei - var6.stringWidth(arg0)) / 2, var9 + 22);
	}
}
