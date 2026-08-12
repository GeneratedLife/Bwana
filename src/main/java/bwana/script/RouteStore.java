package bwana.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads routes: {@code ~/.bwana/routes.txt}.
 * <p>
 * One route per line, plain text, so a route can be corrected in an editor when a
 * waypoint turns out to be a tile inside a wall. Routes are gathered by walking
 * them, which makes them tedious to rebuild — worth keeping in a format that
 * survives without this class.
 */
public final class RouteStore {

	private RouteStore() {
	}

	private static File directory() {
		return new File(System.getProperty("user.home"), ".bwana");
	}

	private static File file() {
		return new File(directory(), "routes.txt");
	}

	/** Every stored route, in file order. Never null; empty when there is no file. */
	public static List<Route> load() {
		List<Route> var0 = new ArrayList<Route>();
		File var1 = file();
		if (!var1.isFile()) {
			return var0;
		}
		BufferedReader var2 = null;
		try {
			var2 = new BufferedReader(new FileReader(var1));
			String var3;
			while ((var3 = var2.readLine()) != null) {
				String var4 = var3.trim();
				if (var4.length() == 0 || var4.charAt(0) == '#') {
					continue;
				}
				Route var5 = Route.parse(var4);
				if (var5 != null) {
					var0.add(var5);
				}
			}
		} catch (IOException var6) {
			System.err.println("bwana: could not read routes: " + var6);
		} finally {
			close(var2);
		}
		return var0;
	}

	/** Replace the file with these routes. */
	public static void save(List<Route> routes) {
		File var1 = directory();
		if (!var1.isDirectory() && !var1.mkdirs()) {
			System.err.println("bwana: could not create " + var1);
			return;
		}
		PrintWriter var2 = null;
		try {
			var2 = new PrintWriter(file());
			var2.println("# Bwana navigation routes");
			var2.println("# name|leg;leg;leg   -- a leg is \"x,y\" or an object name");
			for (int var3 = 0; var3 < routes.size(); var3++) {
				var2.println(routes.get(var3).toStorageLine());
			}
		} catch (IOException var4) {
			System.err.println("bwana: could not write routes: " + var4);
		} finally {
			if (var2 != null) {
				var2.close();
			}
		}
	}

	/**
	 * Add a route, replacing any existing one with the same name.
	 * <p>
	 * Replacing rather than appending because saving under a name you already used
	 * is how you correct a route, and silently keeping both would leave the old one
	 * shadowing the fix.
	 */
	public static List<Route> put(Route route) {
		List<Route> var1 = load();
		for (int var2 = 0; var2 < var1.size(); var2++) {
			if (var1.get(var2).name.equalsIgnoreCase(route.name)) {
				var1.set(var2, route);
				save(var1);
				return var1;
			}
		}
		var1.add(route);
		save(var1);
		return var1;
	}

	public static List<Route> remove(String name) {
		List<Route> var1 = load();
		for (int var2 = var1.size() - 1; var2 >= 0; var2--) {
			if (var1.get(var2).name.equalsIgnoreCase(name)) {
				var1.remove(var2);
			}
		}
		save(var1);
		return var1;
	}

	private static void close(BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException var2) {
				// nothing useful to do while closing a file we have finished with
			}
		}
	}
}
