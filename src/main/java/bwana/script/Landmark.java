package bwana.script;

/**
 * A place the navigation test knows how to find again.
 * <p>
 * Two ways to say where somewhere is, and they check different things. A fixed
 * <b>coordinate</b> tests only movement: the target cannot be wrong, so any
 * disagreement is the player's position or the path. A named <b>object</b> tests
 * the world model as well, because the coordinate has to be recovered from the
 * scene each time — and a landmark that reports a different position after you
 * have walked half a region is exactly the failure worth catching.
 */
public final class Landmark {

	/** Object name to resolve against the scene, or null when fixed. */
	public final String name;

	/** Fixed world tile, or -1 when the landmark is resolved by name. */
	public final int fixedX;
	public final int fixedY;

	private Landmark(String name, int fixedX, int fixedY) {
		this.name = name;
		this.fixedX = fixedX;
		this.fixedY = fixedY;
	}

	public static Landmark named(String name) {
		return new Landmark(name, -1, -1);
	}

	public static Landmark at(int worldX, int worldY) {
		return new Landmark(null, worldX, worldY);
	}

	/**
	 * Parse a landmark from one field: "3222,3218" for a coordinate, anything else
	 * as an object name. Returns null if there is nothing usable.
	 */
	public static Landmark parse(String text) {
		if (text == null) {
			return null;
		}
		String var1 = text.trim();
		if (var1.length() == 0) {
			return null;
		}
		int var2 = var1.indexOf(',');
		if (var2 > 0) {
			try {
				int var3 = Integer.parseInt(var1.substring(0, var2).trim());
				int var4 = Integer.parseInt(var1.substring(var2 + 1).trim());
				return at(var3, var4);
			} catch (NumberFormatException var5) {
				// not a coordinate pair, so treat the whole thing as a name
			}
		}
		return named(var1);
	}

	public boolean isFixed() {
		return this.name == null;
	}

	public String describe() {
		return this.isFixed() ? this.fixedX + ", " + this.fixedY : "\"" + this.name + "\"";
	}
}
