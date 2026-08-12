package bwana.vision;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The vision layer's front door.
 * <p>
 * Runs as a {@code GameEvents} listener: on a throttled tick it copies the
 * viewport on the game thread, then hands that immutable {@link Frame} to a
 * worker for the expensive part. The game loop therefore pays for one array copy
 * and nothing else — colour testing 171,000 pixels across several rules never
 * happens on the thread that also reads the network.
 * <p>
 * <b>Query API for other tools:</b> {@link #getDetections()} and
 * {@link #getDetections(String)} return the most recent results and never block.
 * They are safe to call from any thread, including Swing.
 *
 * <pre>
 * Vision v = Vision.getInstance();
 * for (Detection d : v.getDetections("Ore")) {
 *     click(d.getScreenCentroidX(), d.getScreenCentroidY());
 * }
 * </pre>
 */
public final class Vision extends GameEventsAdapter {

	/** Detect roughly twice a second; the scene does not change faster than you can act. */
	private static final int TICKS_PER_SCAN = 25;

	private static Vision instance;

	public interface Listener {
		/** Called on the worker thread once a scan completes. */
		void onDetections(Frame frame, List<Detection> detections);
	}

	private final List<ColorRule> rules = new CopyOnWriteArrayList<ColorRule>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	private final ConnectedRegions regions = new ConnectedRegions();
	private final TargetWatcher watcher = new TargetWatcher();

	private volatile List<Detection> latest = Collections.emptyList();
	private volatile Frame latestFrame;
	private volatile VisionDebug debug;
	private volatile String debugRule;
	private byte[] debugMask;
	private int[] debugLabels;
	private volatile boolean enabled;
	private volatile boolean eightWay = true;
	private volatile boolean scanning;
	private volatile int screenOriginX;
	private volatile int screenOriginY;
	private volatile long lastScanMillis;

	private FrameSource source;
	private int ticks;

	private Vision() {
		// the watcher is just another listener; it turns raw regions into
		// per-target visibility, which is what tools actually want
		this.listeners.add(this.watcher);
	}

	/** Per-target visibility and screen coordinates. */
	public TargetWatcher getTargetWatcher() {
		return this.watcher;
	}

	public static synchronized Vision getInstance() {
		if (instance == null) {
			instance = new Vision();
		}
		return instance;
	}

	public void setSource(FrameSource source) {
		this.source = source;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			this.latest = Collections.emptyList();
		}
	}

	public boolean isEightWay() {
		return this.eightWay;
	}

	/** 8-way joins diagonal neighbours; 4-way splits shapes that touch only at corners. */
	public void setEightWay(boolean eightWay) {
		this.eightWay = eightWay;
	}

	/** Milliseconds the last scan took, for the debug view. */
	public long getLastScanMillis() {
		return this.lastScanMillis;
	}

	/**
	 * Where the game canvas sits on the desktop. Reported by the UI, which is the
	 * only place that can ask Swing safely.
	 */
	public void setScreenOrigin(int x, int y) {
		this.screenOriginX = x;
		this.screenOriginY = y;
	}

	/** Desktop x of the game canvas' top-left, as last reported by the UI. */
	public int getScreenOriginX() {
		return this.screenOriginX;
	}

	public int getScreenOriginY() {
		return this.screenOriginY;
	}

	// ---- rules ----

	public List<ColorRule> getRules() {
		return this.rules;
	}

	public void addRule(ColorRule rule) {
		this.rules.add(rule);
	}

	public void removeRule(ColorRule rule) {
		this.rules.remove(rule);
	}

	// ---- results ----

	/** Most recent detections across all rules. Never null. */
	public List<Detection> getDetections() {
		return this.latest;
	}

	/** Most recent detections for one rule. Never null. */
	public List<Detection> getDetections(String ruleName) {
		List<Detection> var2 = new ArrayList<Detection>();
		List<Detection> var3 = this.latest;
		for (int var4 = 0; var4 < var3.size(); var4++) {
			Detection var5 = var3.get(var4);
			if (var5.ruleName.equals(ruleName)) {
				var2.add(var5);
			}
		}
		return var2;
	}

	/** The largest detection for a rule, or null. Usually "the one you meant". */
	public Detection getLargest(String ruleName) {
		Detection var2 = null;
		List<Detection> var3 = this.latest;
		for (int var4 = 0; var4 < var3.size(); var4++) {
			Detection var5 = var3.get(var4);
			if (var5.ruleName.equals(ruleName) && (var2 == null || var5.area > var2.area)) {
				var2 = var5;
			}
		}
		return var2;
	}

	/** The frame the current detections came from, or null. */
	public Frame getLatestFrame() {
		return this.latestFrame;
	}

	/**
	 * Ask the next scan to keep the mask and region labels for one rule.
	 * <p>
	 * Null turns it off. Only the debug window sets this; leaving it off means
	 * normal detection allocates and copies nothing extra.
	 */
	public void setDebugRule(String ruleName) {
		this.debugRule = ruleName;
		if (ruleName == null) {
			this.debug = null;
		}
	}

	public String getDebugRule() {
		return this.debugRule;
	}

	/** Intermediate stages of the last scan of the debug rule, or null. */
	public VisionDebug getDebug() {
		return this.debug;
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	/** Capture and scan once, regardless of the throttle. Call on the game thread. */
	public void scanNow() {
		this.capture();
	}

	// ---- driven by the game loop ----

	public void onTick() {
		if (!this.enabled) {
			return;
		}
		if (++this.ticks < TICKS_PER_SCAN) {
			return;
		}
		this.ticks = 0;
		this.capture();
	}

	private void capture() {
		FrameSource var1 = this.source;
		if (var1 == null || this.scanning) {
			// still working on the previous frame; skip rather than queue up
			return;
		}
		final Frame var2 = var1.captureViewport();
		if (var2 == null) {
			return;
		}
		this.scanning = true;
		Thread var3 = new Thread(new Runnable() {
			public void run() {
				try {
					Vision.this.scan(var2);
				} finally {
					Vision.this.scanning = false;
				}
			}
		}, "bwana-vision");
		var3.setDaemon(true);
		var3.setPriority(Thread.MIN_PRIORITY);
		var3.start();
	}

	/** Worker thread: the expensive part. */
	private void scan(Frame frame) {
		long var2 = System.currentTimeMillis();
		List<Detection> var4 = new ArrayList<Detection>();
		String var5 = this.debugRule;
		for (ColorRule var6 : this.rules) {
			if (!var6.enabled) {
				continue;
			}
			if (var5 != null && var5.equals(var6.name)) {
				var4.addAll(this.scanWithDebug(frame, var6));
			} else {
				var4.addAll(this.regions.find(frame, var6, this.eightWay, this.screenOriginX, this.screenOriginY));
			}
		}
		this.latest = Collections.unmodifiableList(var4);
		this.latestFrame = frame;
		this.lastScanMillis = System.currentTimeMillis() - var2;

		for (Listener var8 : this.listeners) {
			try {
				var8.onDetections(frame, this.latest);
			} catch (Throwable var9) {
				System.err.println("bwana: vision listener threw");
				var9.printStackTrace();
			}
		}
	}

	/** Worker thread: same scan, but keeping the intermediate stages. */
	private List<Detection> scanWithDebug(Frame frame, ColorRule rule) {
		int var3 = frame.width * frame.height;
		if (this.debugMask == null || this.debugMask.length < var3) {
			this.debugMask = new byte[var3];
			this.debugLabels = new int[var3];
		}
		List<Detection> var4 = this.regions.find(frame, rule, this.eightWay,
			this.screenOriginX, this.screenOriginY, this.debugMask, this.debugLabels);

		int var5 = 0;
		for (int var6 = 0; var6 < var3; var6++) {
			if (this.debugMask[var6] != 0) {
				var5++;
			}
		}
		// copied, because the next scan reuses the working arrays
		byte[] var7 = new byte[var3];
		int[] var8 = new int[var3];
		System.arraycopy(this.debugMask, 0, var7, 0, var3);
		System.arraycopy(this.debugLabels, 0, var8, 0, var3);
		this.debug = new VisionDebug(rule.name, frame.width, frame.height, var7, var8, var5, var4.size());
		return var4;
	}

	/** Register with the event bus and wire up the pixel source. */
	public static void install(FrameSource source) {
		Vision var1 = getInstance();
		var1.setSource(source);
		GameEventBus.addListener(var1);
	}
}
