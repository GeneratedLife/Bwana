package jagex2.io;

import deob.ObfuscatedName;
import jagex2.client.GameShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@ObfuscatedName("e")
public final class ClientStream implements Runnable {

	@ObfuscatedName("e.d")
	public final InputStream in;

	@ObfuscatedName("e.e")
	public final OutputStream out;

	@ObfuscatedName("e.f")
	public final Socket socket;

	@ObfuscatedName("e.g")
	public boolean dummy = false;

	@ObfuscatedName("e.h")
	public final GameShell shell;

	@ObfuscatedName("e.i")
	public byte[] data;

	@ObfuscatedName("e.j")
	public int tcycl;

	@ObfuscatedName("e.k")
	public int tnum;

	@ObfuscatedName("e.l")
	public boolean writer = false;

	@ObfuscatedName("e.m")
	public boolean ioerror = false;

	public ClientStream(Socket arg1, GameShell arg2) throws IOException {
		shell = arg2;
		socket = arg1;
		socket.setSoTimeout(30000);
		socket.setTcpNoDelay(true);
		in = socket.getInputStream();
		out = socket.getOutputStream();
	}

	@ObfuscatedName("e.a()V")
	public void close() {
		dummy = true;
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException var3) {
			System.out.println("Error closing stream");
		}
		writer = false;
		synchronized (this) {
			notify();
		}
		data = null;
	}

	@ObfuscatedName("e.b()I")
	public int read() throws IOException {
		return dummy ? 0 : in.read();
	}

	@ObfuscatedName("e.c()I")
	public int available() throws IOException {
		return dummy ? 0 : in.available();
	}

	@ObfuscatedName("e.a([BII)V")
	public void read(byte[] arg0, int arg1, int arg2) throws IOException {
		if (dummy) {
			return;
		}
		while (arg2 > 0) {
			int var4 = in.read(arg0, arg1, arg2);
			if (var4 <= 0) {
				throw new IOException("EOF");
			}
			arg1 += var4;
			arg2 -= var4;
		}
	}

	@ObfuscatedName("e.a(II[BI)V")
	public void write(int arg0, byte[] arg2, int arg3) throws IOException {
		if (dummy) {
			return;
		}
		if (ioerror) {
			ioerror = false;
			throw new IOException("Error in writer thread");
		}
		if (data == null) {
			data = new byte[5000];
		}
		synchronized (this) {
			for (int var6 = 0; var6 < arg0; var6++) {
				data[tnum] = arg2[var6 + arg3];
				tnum = (tnum + 1) % 5000;
				if (tnum == (tcycl + 4900) % 5000) {
					throw new IOException("buffer overflow");
				}
			}
			if (!writer) {
				writer = true;
				shell.startThread(this, 3);
			}
			notify();
		}
	}

	@Override
	public void run() {
		while (writer) {
			int var2;
			int var3;
			synchronized (this) {
				if (tnum == tcycl) {
					try {
						wait();
					} catch (InterruptedException var6) {
					}
				}
				if (!writer) {
					return;
				}
				var2 = tcycl;
				if (tnum >= tcycl) {
					var3 = tnum - tcycl;
				} else {
					var3 = 5000 - tcycl;
				}
			}
			if (var3 > 0) {
				try {
					out.write(data, var2, var3);
				} catch (IOException var5) {
					ioerror = true;
				}
				tcycl = (tcycl + var3) % 5000;
				try {
					if (tnum == tcycl) {
						out.flush();
					}
				} catch (IOException var4) {
					ioerror = true;
				}
			}
		}
	}

	@ObfuscatedName("e.a(Z)V")
	public void debug() {
		System.out.println("dummy:" + dummy);
		System.out.println("tcycl:" + tcycl);
		System.out.println("tnum:" + tnum);
		System.out.println("writer:" + writer);
		System.out.println("ioerror:" + ioerror);
		try {
			System.out.println("available:" + available());
		} catch (IOException var2) {
		}
	}
}
