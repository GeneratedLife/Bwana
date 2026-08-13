package bwana.vision;

import bwana.SessionStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Watches named targets and reports when each becomes visible or is lost, with
 * the screen position of its largest region.
 * <p>
 * The raw detection list changes every scan, which is too noisy to act on: a
 * target flickering in and out over a couple of frames would produce a stream of
 * contradictory events. So visibility is <b>debounced</b> — a target must be
 * absent for {@link #MISS_GRACE} consecutive scans before it counts as lost.
 * Appearance is reported immediately, because reacting late to something showing
 * up is worse than reacting late to it leaving.
 * <p>
 * Reports are pushed to listeners and, optionally, appended to
 * {@code ~/.bwana/targets.log}.
 */
public final class TargetWatcher implements Vision.Listener {

	/** Consecutive empty scans before a target is declared gone. */
	public static final int MISS_GRACE = 3;

	public interface Listener {
		/** Called on the vision worker thread whenever the report set is refreshed. */
		void onTargets(List<TargetReport> reports);
	}

	private final Map<String, TargetReport> current = new HashMap<String, TargetReport>();
	private final Map<String, Integer> misses = new HashMap<String, Integer>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

	private volatile List<TargetReport> latest = Collections.emptyList();
	private volatile boolean logging;

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	/** Most recent report per target. Safe to call from any thread. */
	public List<TargetReport> getReports() {
		return this.latest;
	}

	public boolean isLogging() {
		return this.logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public static File logFile() {
		return new File(SessionStore.directory(), "targets.log");
	}

	public void onDetections(Frame frame, List<Detection> detections) {
		Vision var3 = Vision.getInstance();
		List<ColorRule> var4 = var3.getRules();
		List<TargetReport> var5 = new ArrayList<TargetReport>();

		for (int var6 = 0; var6 < var4.size(); var6++) {
			ColorRule var7 = var4.get(var6);
			if (!var7.enabled) {
				this.current.remove(var7.name);
				this.misses.remove(var7.name);
				continue;
			}

			Detection var8 = null;
			int var9 = 0;
			for (int var10 = 0; var10 < detections.size(); var10++) {
				Detection var11 = detections.get(var10);
				if (!var11.ruleName.equals(var7.name)) {
					continue;
				}
				var9++;
				if (var8 == null || var11.area > var8.area) {
					var8 = var11;
				}
			}

			TargetReport var12 = this.current.get(var7.name);
			boolean var13 = var12 != null && var12.visible;

			if (var8 != null) {
				this.misses.put(var7.name, Integer.valueOf(0));
				TargetReport var14 = new TargetReport(var7.name, true, var9, var8, frame.time);
				this.current.put(var7.name, var14);
				var5.add(var14);
				if (!var13) {
					this.log("VISIBLE  " + var7.name + "  " + var9 + " region(s)  largest "
						+ var8.width + "x" + var8.height + " screen "
						+ var8.getScreenCentroidX() + "," + var8.getScreenCentroidY());
				}
			} else {
				Integer var15 = this.misses.get(var7.name);
				int var16 = (var15 == null ? 0 : var15.intValue()) + 1;
				this.misses.put(var7.name, Integer.valueOf(var16));
				if (var13 && var16 < MISS_GRACE) {
					// within the grace window: keep the last known position rather
					// than reporting a loss that a single noisy frame caused
					var5.add(var12);
				} else {
					TargetReport var17 = new TargetReport(var7.name, false, 0, null, frame.time);
					this.current.put(var7.name, var17);
					var5.add(var17);
					if (var13) {
						this.log("LOST     " + var7.name);
					}
				}
			}
		}

		this.latest = Collections.unmodifiableList(var5);
		for (Listener var18 : this.listeners) {
			try {
				var18.onTargets(this.latest);
			} catch (Throwable var19) {
				System.err.println("bwana: target listener threw");
				var19.printStackTrace();
			}
		}
	}

	private void log(String message) {
		if (!this.logging) {
			return;
		}
		Writer var2 = null;
		try {
			File var3 = SessionStore.directory();
			if (!var3.exists() && !var3.mkdirs()) {
				return;
			}
			var2 = new OutputStreamWriter(new FileOutputStream(logFile(), true), "UTF-8");
			var2.write("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message + "\n");
		} catch (IOException var8) {
			System.err.println("bwana: could not write target log: " + var8.getMessage());
		} finally {
			if (var2 != null) {
				try {
					var2.close();
				} catch (IOException var7) {
				}
			}
		}
	}
}
