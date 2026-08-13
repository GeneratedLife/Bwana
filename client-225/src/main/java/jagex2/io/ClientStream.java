package jagex2.io;

import deob.ObfuscatedName;
import jagex2.client.GameShell;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@ObfuscatedName("d")
public class ClientStream implements Runnable {

	@ObfuscatedName("d.a")
	public boolean field61 = false;

	@ObfuscatedName("d.b")
	public InputStream in;

	@ObfuscatedName("d.c")
	public OutputStream out;

	@ObfuscatedName("d.d")
	public Socket socket;

	@ObfuscatedName("d.e")
	public boolean closed = false;

	@ObfuscatedName("d.f")
	public GameShell shell;

	@ObfuscatedName("d.g")
	public byte[] buf;

	@ObfuscatedName("d.h")
	public int bufLen;

	@ObfuscatedName("d.i")
	public int bufPos;

	@ObfuscatedName("d.j")
	public boolean writer = false;

	@ObfuscatedName("d.k")
	public boolean ioerror = false;

	public ClientStream(GameShell arg0, byte arg1, Socket arg2) throws IOException {
		if (arg1 == 2) {
			boolean var4 = false;
		} else {
			this.field61 = !this.field61;
		}
		this.shell = arg0;
		this.socket = arg2;
		this.socket.setSoTimeout(30000);
		this.socket.setTcpNoDelay(true);
		this.in = this.socket.getInputStream();
		this.out = this.socket.getOutputStream();
	}

	@ObfuscatedName("d.a()V")
	public void close() {
		this.closed = true;
		try {
			if (this.in != null) {
				this.in.close();
			}
			if (this.out != null) {
				this.out.close();
			}
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException var3) {
			System.out.println("Error closing stream");
		}
		this.writer = false;
		synchronized (this) {
			this.notify();
		}
		this.buf = null;
	}

	@ObfuscatedName("d.b()I")
	public int read() throws IOException {
		return this.closed ? 0 : this.in.read();
	}

	@ObfuscatedName("d.c()I")
	public int available() throws IOException {
		return this.closed ? 0 : this.in.available();
	}

	@ObfuscatedName("d.a([BII)V")
	public void read(byte[] arg0, int arg1, int arg2) throws IOException {
		if (this.closed) {
			return;
		}
		while (arg2 > 0) {
			int var4 = this.in.read(arg0, arg1, arg2);
			if (var4 <= 0) {
				throw new IOException("EOF");
			}
			arg1 += var4;
			arg2 -= var4;
		}
	}

	@ObfuscatedName("d.a([BIZI)V")
	public void write(byte[] arg0, int arg1, boolean arg2, int arg3) throws IOException {
		if (!arg2 || this.closed) {
			return;
		}
		if (this.ioerror) {
			this.ioerror = false;
			throw new IOException("Error in writer thread");
		}
		if (this.buf == null) {
			this.buf = new byte[5000];
		}
		synchronized (this) {
			for (int var7 = 0; var7 < arg1; var7++) {
				this.buf[this.bufPos] = arg0[var7 + arg3];
				this.bufPos = (this.bufPos + 1) % 5000;
				if (this.bufPos == (this.bufLen + 4900) % 5000) {
					throw new IOException("buffer overflow");
				}
			}
			if (!this.writer) {
				this.writer = true;
				this.shell.startThread(this, 2);
			}
			this.notify();
		}
	}

	public void run() {
		while (this.writer) {
			int var1;
			int var2;
			label54: {
				synchronized (this) {
					if (this.bufPos == this.bufLen) {
						try {
							this.wait();
						} catch (InterruptedException var7) {
						}
					}
					if (this.writer) {
						var2 = this.bufLen;
						if (this.bufPos >= this.bufLen) {
							var1 = this.bufPos - this.bufLen;
						} else {
							var1 = 5000 - this.bufLen;
						}
						break label54;
					}
				}
				return;
			}
			if (var1 > 0) {
				try {
					this.out.write(this.buf, var2, var1);
				} catch (IOException var6) {
					this.ioerror = true;
				}
				this.bufLen = (this.bufLen + var1) % 5000;
				try {
					if (this.bufPos == this.bufLen) {
						this.out.flush();
					}
				} catch (IOException var5) {
					this.ioerror = true;
				}
			}
		}
	}
}
