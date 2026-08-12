package bwana.vision;

import bwana.SessionStore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads target definitions: {@code ~/.bwana/vision-rules.txt}.
 * <p>
 * One rule per line as {@code key=value} pairs separated by {@code |}. Not JSON,
 * deliberately — a tuned rule is a handful of numbers you will want to read,
 * diff and hand-edit, and this format survives being opened in Notepad. It also
 * needs no parser beyond {@code split}, which matters when the alternative is
 * hand-rolling JSON parsing in a codebase with no dependencies.
 * <p>
 * Unknown keys are ignored and missing keys keep their defaults, so a file
 * written by a later version still loads.
 */
public final class RuleStore {

	private RuleStore() {
	}

	public static File file() {
		return new File(SessionStore.directory(), "vision-rules.txt");
	}

	public static void save(List<ColorRule> rules) throws IOException {
		File var1 = SessionStore.directory();
		if (!var1.exists() && !var1.mkdirs()) {
			throw new IOException("could not create " + var1);
		}
		Writer var2 = null;
		try {
			var2 = new OutputStreamWriter(new FileOutputStream(file()), "UTF-8");
			var2.write("# Bwana visual targets. One rule per line.\n");
			var2.write("# mode is RGB or HSV. target is hex RRGGBB.\n");
			for (int var3 = 0; var3 < rules.size(); var3++) {
				ColorRule var4 = rules.get(var3);
				var2.write("name=" + clean(var4.name));
				var2.write("|mode=" + (var4.mode == ColorRule.MODE_HSV ? "HSV" : "RGB"));
				var2.write("|target=" + hex(var4.targetRgb));
				var2.write("|rgbTol=" + var4.rgbTolerance);
				var2.write("|hue=" + var4.hueTolerance);
				var2.write("|sat=" + var4.satTolerance);
				var2.write("|val=" + var4.valTolerance);
				var2.write("|minArea=" + var4.minArea);
				var2.write("|gapFill=" + var4.gapFill);
				var2.write("|enabled=" + var4.enabled);
				var2.write("|overlay=" + hex(var4.overlayRgb));
				var2.write("\n");
			}
		} finally {
			if (var2 != null) {
				try {
					var2.close();
				} catch (IOException var8) {
				}
			}
		}
	}

	/** Returns an empty list if there is no saved file. */
	public static List<ColorRule> load() {
		List<ColorRule> var0 = new ArrayList<ColorRule>();
		File var1 = file();
		if (!var1.exists()) {
			return var0;
		}
		BufferedReader var2 = null;
		try {
			var2 = new BufferedReader(new InputStreamReader(new FileInputStream(var1), "UTF-8"));
			String var3;
			while ((var3 = var2.readLine()) != null) {
				var3 = var3.trim();
				if (var3.length() == 0 || var3.charAt(0) == '#') {
					continue;
				}
				ColorRule var4 = parse(var3);
				if (var4 != null) {
					var0.add(var4);
				}
			}
		} catch (IOException var9) {
			System.err.println("bwana: could not read vision rules: " + var9.getMessage());
		} finally {
			if (var2 != null) {
				try {
					var2.close();
				} catch (IOException var8) {
				}
			}
		}
		return var0;
	}

	private static ColorRule parse(String line) {
		String[] var1 = line.split("\\|");
		ColorRule var2 = new ColorRule("Unnamed", 0xFF00FF);
		for (int var3 = 0; var3 < var1.length; var3++) {
			int var4 = var1[var3].indexOf('=');
			if (var4 <= 0) {
				continue;
			}
			String var5 = var1[var3].substring(0, var4).trim();
			String var6 = var1[var3].substring(var4 + 1).trim();
			try {
				if ("name".equals(var5)) {
					var2.name = var6;
				} else if ("mode".equals(var5)) {
					var2.mode = "HSV".equalsIgnoreCase(var6) ? ColorRule.MODE_HSV : ColorRule.MODE_RGB;
				} else if ("target".equals(var5)) {
					var2.setTarget((int) Long.parseLong(var6, 16));
				} else if ("rgbTol".equals(var5)) {
					var2.rgbTolerance = Integer.parseInt(var6);
				} else if ("hue".equals(var5)) {
					var2.hueTolerance = Integer.parseInt(var6);
				} else if ("sat".equals(var5)) {
					var2.satTolerance = Integer.parseInt(var6);
				} else if ("val".equals(var5)) {
					var2.valTolerance = Integer.parseInt(var6);
				} else if ("minArea".equals(var5)) {
					var2.minArea = Integer.parseInt(var6);
				} else if ("gapFill".equals(var5)) {
					var2.gapFill = Integer.parseInt(var6);
				} else if ("enabled".equals(var5)) {
					var2.enabled = Boolean.valueOf(var6).booleanValue();
				} else if ("overlay".equals(var5)) {
					var2.overlayRgb = (int) Long.parseLong(var6, 16);
				}
			} catch (NumberFormatException var8) {
				// a bad field should not lose the whole rule
			}
		}
		return var2;
	}

	private static String clean(String name) {
		return name == null ? "Unnamed" : name.replace('|', ' ').replace('=', ' ').trim();
	}

	private static String hex(int rgb) {
		String var1 = Integer.toHexString(rgb & 0xFFFFFF);
		while (var1.length() < 6) {
			var1 = "0" + var1;
		}
		return var1.toUpperCase();
	}
}
