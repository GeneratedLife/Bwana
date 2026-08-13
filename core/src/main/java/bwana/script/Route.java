package bwana.script;

/**
 * A named sequence of landmarks to travel through.
 * <p>
 * Exists because greedy stepping cannot leave a pocket. The pathfinder only sees
 * the loaded scene, so when the way out of somewhere runs <i>away</i> from the
 * destination — around a river, or through a gate in a wall — there is no reachable
 * tile closer to the target and the walk stops dead. A route says which way to go
 * without needing a world-scale router to work it out.
 * <p>
 * {@link #legs} is the whole journey: the first is where the run starts, the last
 * is the destination, and anything between is a waypoint. Two legs is an ordinary
 * point-to-point trip, which is why routes and the plain start/destination case are
 * the same shape rather than two mechanisms.
 */
public final class Route {

	/** Separates legs when stored or typed. */
	public static final char LEG_SEPARATOR = ';';

	/** Separates the route name from its legs on disk. */
	private static final char NAME_SEPARATOR = '|';

	public final String name;

	/** Start first, destination last, waypoints between. Always at least two. */
	public final Landmark[] legs;

	public Route(String name, Landmark[] legs) {
		this.name = name;
		this.legs = legs;
	}

	public Landmark getStart() {
		return this.legs[0];
	}

	public Landmark getDestination() {
		return this.legs[this.legs.length - 1];
	}

	/** Waypoint count, i.e. everything that is neither start nor destination. */
	public int getWaypointCount() {
		return this.legs.length - 2;
	}

	/**
	 * Build a route from three fields, skipping blanks.
	 *
	 * @param via semicolon-separated waypoints, or null/empty for a direct trip
	 * @return null if start or destination is missing
	 */
	public static Route of(String name, String start, String via, String destination) {
		Landmark var4 = Landmark.parse(start);
		Landmark var5 = Landmark.parse(destination);
		if (var4 == null || var5 == null) {
			return null;
		}
		java.util.List<Landmark> var6 = new java.util.ArrayList<Landmark>();
		var6.add(var4);
		if (via != null) {
			String[] var7 = via.split(String.valueOf(LEG_SEPARATOR));
			for (int var8 = 0; var8 < var7.length; var8++) {
				Landmark var9 = Landmark.parse(var7[var8]);
				if (var9 != null) {
					var6.add(var9);
				}
			}
		}
		var6.add(var5);
		return new Route(name, var6.toArray(new Landmark[var6.size()]));
	}

	/** The waypoints only, as they would be typed back into the Via field. */
	public String describeWaypoints() {
		StringBuilder var1 = new StringBuilder();
		for (int var2 = 1; var2 < this.legs.length - 1; var2++) {
			if (var2 > 1) {
				var1.append(LEG_SEPARATOR).append(' ');
			}
			var1.append(describeLeg(this.legs[var2]));
		}
		return var1.toString();
	}

	/** One line, parseable by {@link #parse}. */
	public String toStorageLine() {
		StringBuilder var1 = new StringBuilder();
		var1.append(sanitise(this.name)).append(NAME_SEPARATOR);
		for (int var2 = 0; var2 < this.legs.length; var2++) {
			if (var2 > 0) {
				var1.append(LEG_SEPARATOR);
			}
			var1.append(describeLeg(this.legs[var2]));
		}
		return var1.toString();
	}

	/** Reverse of {@link #toStorageLine}; null if the line is unusable. */
	public static Route parse(String line) {
		if (line == null) {
			return null;
		}
		int var1 = line.indexOf(NAME_SEPARATOR);
		if (var1 <= 0) {
			return null;
		}
		String var2 = line.substring(0, var1).trim();
		String[] var3 = line.substring(var1 + 1).split(String.valueOf(LEG_SEPARATOR));
		java.util.List<Landmark> var4 = new java.util.ArrayList<Landmark>();
		for (int var5 = 0; var5 < var3.length; var5++) {
			Landmark var6 = Landmark.parse(var3[var5]);
			if (var6 != null) {
				var4.add(var6);
			}
		}
		if (var2.length() == 0 || var4.size() < 2) {
			return null;
		}
		return new Route(var2, var4.toArray(new Landmark[var4.size()]));
	}

	/** A leg as text: coordinates stay coordinates, names stay names. */
	private static String describeLeg(Landmark landmark) {
		return landmark.isFixed()
			? landmark.fixedX + "," + landmark.fixedY
			: sanitise(landmark.name);
	}

	/** Strip the characters the storage format uses as structure. */
	private static String sanitise(String text) {
		return text == null ? "" : text.replace(NAME_SEPARATOR, ' ').replace(LEG_SEPARATOR, ' ').trim();
	}

	public String toString() {
		return this.name + " (" + this.legs.length + " legs)";
	}
}
