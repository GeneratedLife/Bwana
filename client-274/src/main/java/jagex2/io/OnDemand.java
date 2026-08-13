package jagex2.io;

import deob.ObfuscatedName;
import jagex2.client.Client;
import jagex2.datastruct.LinkList;
import jagex2.datastruct.LinkList2;
import sign.signlink;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

@ObfuscatedName("ub")
public final class OnDemand extends OnDemandProvider implements Runnable {

	@ObfuscatedName("ub.e")
	public final int[][] versions = new int[4][];

	@ObfuscatedName("ub.f")
	public final int[][] crcs = new int[4][];

	@ObfuscatedName("ub.g")
	public final byte[][] priorities = new byte[4][];

	@ObfuscatedName("ub.h")
	public int topPriority;

	@ObfuscatedName("ub.i")
	public byte[] modelUse;

	@ObfuscatedName("ub.j")
	public int[] mapIndex;

	@ObfuscatedName("ub.k")
	public int[] mapLand;

	@ObfuscatedName("ub.l")
	public int[] mapLoc;

	@ObfuscatedName("ub.m")
	public int[] mapFree;

	@ObfuscatedName("ub.n")
	public int[] animFrameIndex;

	@ObfuscatedName("ub.o")
	public int[] midiJingle;

	@ObfuscatedName("ub.p")
	public boolean running = true;

	@ObfuscatedName("ub.q")
	public Client app;

	@ObfuscatedName("ub.r")
	public final CRC32 crc32 = new CRC32();

	@ObfuscatedName("ub.s")
	public boolean active = false;

	@ObfuscatedName("ub.t")
	public int urgentCount;

	@ObfuscatedName("ub.u")
	public int requestCount;

	@ObfuscatedName("ub.v")
	public final LinkList2 requests = new LinkList2();

	@ObfuscatedName("ub.w")
	public final LinkList queue = new LinkList();

	@ObfuscatedName("ub.x")
	public final LinkList missing = new LinkList();

	@ObfuscatedName("ub.y")
	public final LinkList pending = new LinkList();

	@ObfuscatedName("ub.z")
	public final LinkList completed = new LinkList();

	@ObfuscatedName("ub.A")
	public final LinkList prefetches = new LinkList();

	@ObfuscatedName("ub.B")
	public String message = "";

	@ObfuscatedName("ub.L")
	public final byte[] buf = new byte[500];

	@ObfuscatedName("ub.M")
	public final byte[] data = new byte[65000];

	@ObfuscatedName("ub.C")
	public int loadedPrefetchFiles;

	@ObfuscatedName("ub.D")
	public int totalPrefetchFiles;

	@ObfuscatedName("ub.H")
	public int failCount;

	@ObfuscatedName("ub.J")
	public int partOffset;

	@ObfuscatedName("ub.K")
	public int partAvailable;

	@ObfuscatedName("ub.N")
	public int packetCycle;

	@ObfuscatedName("ub.O")
	public int noTimeoutCycle;

	@ObfuscatedName("ub.Q")
	public int cycle;

	@ObfuscatedName("ub.P")
	public long socketOpenTime;

	@ObfuscatedName("ub.I")
	public OnDemandRequest current;

	@ObfuscatedName("ub.F")
	public InputStream in;

	@ObfuscatedName("ub.G")
	public OutputStream out;

	@ObfuscatedName("ub.E")
	public Socket socket;

	@ObfuscatedName("ub.a(Lxb;Lclient;)V")
	public void init(JagFile arg0, Client arg1) {
		String[] var3 = new String[] { "model_version", "anim_version", "midi_version", "map_version" };
		for (int var4 = 0; var4 < 4; var4++) {
			byte[] var5 = arg0.read(var3[var4], null);
			int var6 = var5.length / 2;
			Packet var7 = new Packet(var5);
			versions[var4] = new int[var6];
			priorities[var4] = new byte[var6];
			for (int var8 = 0; var8 < var6; var8++) {
				versions[var4][var8] = var7.g2();
			}
		}
		String[] var9 = new String[] { "model_crc", "anim_crc", "midi_crc", "map_crc" };
		for (int var10 = 0; var10 < 4; var10++) {
			byte[] var11 = arg0.read(var9[var10], null);
			int var12 = var11.length / 4;
			Packet var13 = new Packet(var11);
			crcs[var10] = new int[var12];
			for (int var14 = 0; var14 < var12; var14++) {
				crcs[var10][var14] = var13.g4();
			}
		}
		byte[] var15 = arg0.read("model_index", null);
		int var16 = versions[0].length;
		modelUse = new byte[var16];
		for (int var17 = 0; var17 < var16; var17++) {
			if (var17 < var15.length) {
				modelUse[var17] = var15[var17];
			} else {
				modelUse[var17] = 0;
			}
		}
		byte[] var18 = arg0.read("map_index", null);
		Packet var19 = new Packet(var18);
		int var20 = var18.length / 7;
		mapIndex = new int[var20];
		mapLand = new int[var20];
		mapLoc = new int[var20];
		mapFree = new int[var20];
		for (int var21 = 0; var21 < var20; var21++) {
			mapIndex[var21] = var19.g2();
			mapLand[var21] = var19.g2();
			mapLoc[var21] = var19.g2();
			mapFree[var21] = var19.g1();
		}
		byte[] var22 = arg0.read("anim_index", null);
		Packet var23 = new Packet(var22);
		int var24 = var22.length / 2;
		animFrameIndex = new int[var24];
		for (int var25 = 0; var25 < var24; var25++) {
			animFrameIndex[var25] = var23.g2();
		}
		byte[] var26 = arg0.read("midi_index", null);
		Packet var27 = new Packet(var26);
		int var28 = var26.length;
		midiJingle = new int[var28];
		for (int var29 = 0; var29 < var28; var29++) {
			midiJingle[var29] = var27.g1();
		}
		app = arg1;
		running = true;
		app.startThread(this, 2);
	}

	@ObfuscatedName("ub.a()V")
	public void stop() {
		running = false;
	}

	@ObfuscatedName("ub.a(II)I")
	public int getFileCount(int arg1) {
		return versions[arg1].length;
	}

	@ObfuscatedName("ub.b(I)I")
	public int getAnimFrameCount() {
		return animFrameIndex.length;
	}

	@ObfuscatedName("ub.a(IIZI)I")
	public int getMapFile(int arg0, int arg1, int arg3) {
		int var5 = (arg1 << 8) + arg3;
		for (int var6 = 0; var6 < mapIndex.length; var6++) {
			if (mapIndex[var6] == var5) {
				if (arg0 == 0) {
					return mapLand[var6];
				}
				return mapLoc[var6];
			}
		}
		return -1;
	}

	@ObfuscatedName("ub.a(ZI)V")
	public void prefetchMaps(boolean arg0) {
		int var3 = mapIndex.length;
		for (int var4 = 0; var4 < var3; var4++) {
			if (arg0 || mapFree[var4] != 0) {
				prefetchPriority(mapLoc[var4], 3, (byte) 2);
				prefetchPriority(mapLand[var4], 3, (byte) 2);
			}
		}
	}

	@ObfuscatedName("ub.b(II)Z")
	public boolean hasMapLocFile(int arg0) {
		for (int var4 = 0; var4 < mapIndex.length; var4++) {
			if (mapLoc[var4] == arg0) {
				return true;
			}
		}
		return false;
	}

	@ObfuscatedName("ub.a(IB)I")
	public int getModelUse(int arg0) {
		return modelUse[arg0] & 0xFF;
	}

	@ObfuscatedName("ub.a(IZ)Z")
	public boolean isMidiJingle(int arg0) {
		return midiJingle[arg0] == 1;
	}

	@ObfuscatedName("ub.a(I)V")
	@Override
	public void requestModel(int arg0) {
		request(0, arg0);
	}

	@ObfuscatedName("ub.c(II)V")
	public void request(int arg0, int arg1) {
		if (arg0 < 0 || arg0 > versions.length || arg1 < 0 || arg1 > versions[arg0].length || versions[arg0][arg1] == 0) {
			return;
		}
		LinkList2 var3 = requests;
		synchronized (requests) {
			for (OnDemandRequest var4 = (OnDemandRequest) requests.head(); var4 != null; var4 = (OnDemandRequest) requests.next()) {
				if (var4.archive == arg0 && var4.file == arg1) {
					return;
				}
			}
			OnDemandRequest var5 = new OnDemandRequest();
			var5.archive = arg0;
			var5.file = arg1;
			var5.urgent = true;
			LinkList var6 = queue;
			synchronized (queue) {
				queue.push(var5);
			}
			requests.push(var5);
		}
	}

	@ObfuscatedName("ub.b()I")
	public int remaining() {
		LinkList2 var1 = requests;
		synchronized (requests) {
			return requests.size();
		}
	}

	@ObfuscatedName("ub.c()Lmb;")
	public OnDemandRequest loop() {
		LinkList var1 = completed;
		OnDemandRequest var2;
		synchronized (completed) {
			var2 = (OnDemandRequest) completed.popFront();
		}
		if (var2 == null) {
			return null;
		}
		LinkList2 var3 = requests;
		synchronized (requests) {
			var2.unlink2();
		}
		if (var2.data == null) {
			return var2;
		}
		int var4 = 0;
		try {
			GZIPInputStream var5 = new GZIPInputStream(new ByteArrayInputStream(var2.data));
			while (true) {
				if (var4 == data.length) {
					throw new RuntimeException("buffer overflow!");
				}
				int var6 = var5.read(data, var4, data.length - var4);
				if (var6 == -1) {
					break;
				}
				var4 += var6;
			}
		} catch (IOException var10) {
			throw new RuntimeException("error unzipping");
		}
		var2.data = new byte[var4];
		for (int var7 = 0; var7 < var4; var7++) {
			var2.data[var7] = data[var7];
		}
		return var2;
	}

	@ObfuscatedName("ub.a(BIIB)V")
	public void prefetchPriority(int arg1, int arg2, byte arg3) {
		if (app.fileStreams[0] == null || versions[arg2][arg1] == 0) {
			return;
		}
		byte[] var6 = app.fileStreams[arg2 + 1].readFromFile(arg1);
		if (validate(crcs[arg2][arg1], versions[arg2][arg1], var6)) {
			return;
		}
		priorities[arg2][arg1] = arg3;
		if (arg3 > topPriority) {
			topPriority = arg3;
		}
		totalPrefetchFiles++;
	}

	@ObfuscatedName("ub.a(B)V")
	public void clearPrefetches() {
		LinkList var2 = prefetches;
		synchronized (prefetches) {
			prefetches.clear();
		}
	}

	@ObfuscatedName("ub.a(III)V")
	public void prefetch(int arg1, int arg2) {
		if (app.fileStreams[0] == null || (versions[arg1][arg2] == 0 || (priorities[arg1][arg2] == 0 || topPriority == 0))) {
			return;
		}
		OnDemandRequest var4 = new OnDemandRequest();
		var4.archive = arg1;
		var4.file = arg2;
		var4.urgent = false;
		LinkList var5 = prefetches;
		synchronized (prefetches) {
			prefetches.push(var4);
		}
	}

	@Override
	public void run() {
		try {
			while (running) {
				cycle++;
				byte var1 = 20;
				if (topPriority == 0 && app.fileStreams[0] != null) {
					var1 = 50;
				}
				try {
					Thread.sleep((long) var1);
				} catch (Exception var9) {
				}
				active = true;
				for (int var2 = 0; var2 < 100 && active; var2++) {
					active = false;
					handleQueue();
					handlePending();
					if (urgentCount == 0 && var2 >= 5) {
						break;
					}
					handleExtra();
					if (in != null) {
						read();
					}
				}
				boolean var3 = false;
				for (OnDemandRequest var4 = (OnDemandRequest) pending.head(); var4 != null; var4 = (OnDemandRequest) pending.next()) {
					if (var4.urgent) {
						var3 = true;
						var4.cycle++;
						if (var4.cycle > 50) {
							var4.cycle = 0;
							send(var4);
						}
					}
				}
				if (!var3) {
					for (OnDemandRequest var5 = (OnDemandRequest) pending.head(); var5 != null; var5 = (OnDemandRequest) pending.next()) {
						var3 = true;
						var5.cycle++;
						if (var5.cycle > 50) {
							var5.cycle = 0;
							send(var5);
						}
					}
				}
				if (var3) {
					packetCycle++;
					if (packetCycle > 750) {
						try {
							socket.close();
						} catch (Exception var8) {
						}
						socket = null;
						in = null;
						out = null;
						partAvailable = 0;
					}
				} else {
					packetCycle = 0;
					message = "";
				}
				if (app.ingame && socket != null && out != null && (topPriority > 0 || app.fileStreams[0] == null)) {
					noTimeoutCycle++;
					if (noTimeoutCycle > 500) {
						noTimeoutCycle = 0;
						buf[0] = 0;
						buf[1] = 0;
						buf[2] = 0;
						buf[3] = 10;
						try {
							out.write(buf, 0, 4);
						} catch (IOException var7) {
							packetCycle = 5000;
						}
					}
				}
			}
		} catch (Exception var10) {
			signlink.reporterror("od_ex " + var10.getMessage());
		}
	}

	@ObfuscatedName("ub.b(B)V")
	public void handleQueue() {
		LinkList var2 = queue;
		OnDemandRequest var3;
		synchronized (queue) {
			var3 = (OnDemandRequest) queue.popFront();
		}
		while (var3 != null) {
			active = true;
			byte[] var4 = null;
			if (app.fileStreams[0] != null) {
				var4 = app.fileStreams[var3.archive + 1].readFromFile(var3.file);
			}
			if (!validate(crcs[var3.archive][var3.file], versions[var3.archive][var3.file], var4)) {
				var4 = null;
			}
			LinkList var5 = queue;
			synchronized (queue) {
				if (var4 == null) {
					missing.push(var3);
				} else {
					var3.data = var4;
					LinkList var6 = completed;
					synchronized (completed) {
						completed.push(var3);
					}
				}
				var3 = (OnDemandRequest) queue.popFront();
			}
		}
	}

	@ObfuscatedName("ub.a(Z)V")
	public void handlePending() {
		urgentCount = 0;
		requestCount = 0;
		OnDemandRequest var2 = (OnDemandRequest) pending.head();
		while (var2 != null) {
			if (var2.urgent) {
				urgentCount++;
			} else {
				requestCount++;
			}
			var2 = (OnDemandRequest) pending.next();
		}
		while (urgentCount < 10) {
			OnDemandRequest var4 = (OnDemandRequest) missing.popFront();
			if (var4 == null) {
				break;
			}
			if (priorities[var4.archive][var4.file] != 0) {
				loadedPrefetchFiles++;
			}
			priorities[var4.archive][var4.file] = 0;
			pending.push(var4);
			urgentCount++;
			send(var4);
			active = true;
		}
	}

	@ObfuscatedName("ub.c(I)V")
	public void handleExtra() {
		while (urgentCount == 0) {
			if (requestCount >= 10 || topPriority == 0) {
				return;
			}
			LinkList var2 = prefetches;
			OnDemandRequest var3;
			synchronized (prefetches) {
				var3 = (OnDemandRequest) prefetches.popFront();
			}
			while (var3 != null) {
				if (priorities[var3.archive][var3.file] != 0) {
					priorities[var3.archive][var3.file] = 0;
					pending.push(var3);
					send(var3);
					active = true;
					if (loadedPrefetchFiles < totalPrefetchFiles) {
						loadedPrefetchFiles++;
					}
					message = "Loading extra files - " + loadedPrefetchFiles * 100 / totalPrefetchFiles + "%";
					requestCount++;
					if (requestCount == 10) {
						return;
					}
				}
				LinkList var4 = prefetches;
				synchronized (prefetches) {
					var3 = (OnDemandRequest) prefetches.popFront();
				}
			}
			for (int var5 = 0; var5 < 4; var5++) {
				byte[] var6 = priorities[var5];
				int var7 = var6.length;
				for (int var8 = 0; var8 < var7; var8++) {
					if (var6[var8] == topPriority) {
						var6[var8] = 0;
						OnDemandRequest var9 = new OnDemandRequest();
						var9.archive = var5;
						var9.file = var8;
						var9.urgent = false;
						pending.push(var9);
						send(var9);
						active = true;
						if (loadedPrefetchFiles < totalPrefetchFiles) {
							loadedPrefetchFiles++;
						}
						message = "Loading extra files - " + loadedPrefetchFiles * 100 / totalPrefetchFiles + "%";
						requestCount++;
						if (requestCount == 10) {
							return;
						}
					}
				}
			}
			topPriority--;
		}
	}

	@ObfuscatedName("ub.d(I)V")
	public void read() {
		try {
			int var2 = in.available();
			if (partAvailable == 0 && var2 >= 6) {
				active = true;
				for (int var3 = 0; var3 < 6; var3 += in.read(buf, var3, 6 - var3)) {
				}
				int var4 = buf[0] & 0xFF;
				int var5 = ((buf[1] & 0xFF) << 8) + (buf[2] & 0xFF);
				int var6 = ((buf[3] & 0xFF) << 8) + (buf[4] & 0xFF);
				int var7 = buf[5] & 0xFF;
				current = null;
				for (OnDemandRequest var8 = (OnDemandRequest) pending.head(); var8 != null; var8 = (OnDemandRequest) pending.next()) {
					if (var8.archive == var4 && var8.file == var5) {
						current = var8;
					}
					if (current != null) {
						var8.cycle = 0;
					}
				}
				if (current != null) {
					packetCycle = 0;
					if (var6 == 0) {
						signlink.reporterror("Rej: " + var4 + "," + var5);
						current.data = null;
						if (current.urgent) {
							LinkList var9 = completed;
							synchronized (completed) {
								completed.push(current);
							}
						} else {
							current.unlink();
						}
						current = null;
					} else {
						if (current.data == null && var7 == 0) {
							current.data = new byte[var6];
						}
						if (current.data == null && var7 != 0) {
							throw new IOException("missing start of file");
						}
					}
				}
				partOffset = var7 * 500;
				partAvailable = 500;
				if (partAvailable > var6 - var7 * 500) {
					partAvailable = var6 - var7 * 500;
				}
			}
			if (partAvailable > 0 && var2 >= partAvailable) {
				active = true;
				byte[] var10 = buf;
				int var11 = 0;
				if (current != null) {
					var10 = current.data;
					var11 = partOffset;
				}
				for (int var12 = 0; var12 < partAvailable; var12 += in.read(var10, var12 + var11, partAvailable - var12)) {
				}
				if (partAvailable + partOffset >= var10.length && current != null) {
					if (app.fileStreams[0] != null) {
						app.fileStreams[current.archive + 1].writeToFile(var10.length, current.file, var10);
					}
					if (!current.urgent && current.archive == 3) {
						current.urgent = true;
						current.archive = 93;
					}
					if (current.urgent) {
						LinkList var13 = completed;
						synchronized (completed) {
							completed.push(current);
						}
					} else {
						current.unlink();
					}
				}
				partAvailable = 0;
			}
		} catch (IOException var18) {
			try {
				socket.close();
			} catch (Exception var15) {
			}
			socket = null;
			in = null;
			out = null;
			partAvailable = 0;
		}
	}

	@ObfuscatedName("ub.a(BII[B)Z")
	public boolean validate(int arg1, int arg2, byte[] arg3) {
		if (arg3 == null || arg3.length < 2) {
			return false;
		}
		int var5 = arg3.length - 2;
		int var6 = ((arg3[var5] & 0xFF) << 8) + (arg3[var5 + 1] & 0xFF);
		crc32.reset();
		crc32.update(arg3, 0, var5);
		int var8 = (int) crc32.getValue();
		if (var6 == arg2) {
			return var8 == arg1;
		} else {
			return false;
		}
	}

	@ObfuscatedName("ub.a(Lmb;I)V")
	public void send(OnDemandRequest arg0) {
		try {
			if (socket == null) {
				long var3 = System.currentTimeMillis();
				if (var3 - socketOpenTime < 4000L) {
					return;
				}
				socketOpenTime = var3;
				socket = app.openSocket(Client.portOff + 43594);
				in = socket.getInputStream();
				out = socket.getOutputStream();
				out.write(15);
				for (int var5 = 0; var5 < 8; var5++) {
					in.read();
				}
				packetCycle = 0;
			}
			buf[0] = (byte) arg0.archive;
			buf[1] = (byte) (arg0.file >> 8);
			buf[2] = (byte) arg0.file;
			if (arg0.urgent) {
				buf[3] = 2;
			} else if (app.ingame) {
				buf[3] = 0;
			} else {
				buf[3] = 1;
			}
			out.write(buf, 0, 4);
			noTimeoutCycle = 0;
			failCount = -10000;
		} catch (IOException var8) {
			try {
				socket.close();
			} catch (Exception var7) {
			}
			socket = null;
			in = null;
			out = null;
			partAvailable = 0;
			failCount++;
		}
	}
}
