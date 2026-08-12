package bwana.inspect;

/**
 * Live model-centre markers for visible world objects.
 * <p>
 * <b>Targets are stored in world terms, never as screen positions.</b> That is
 * the whole design: a screen position captured on a timer is stale the moment the
 * camera turns, and the markers would visibly lag and drift. Instead each target
 * keeps the anchor and height the client itself uses to place the model, and the
 * render hook projects them fresh every frame — so a marker stays welded to its
 * object however the camera moves, at no cost beyond one projection each.
 * <p>
 * The target list is rebuilt occasionally as you walk, which is a different and
 * much slower concern than following the camera.
 * <p>
 * The array is swapped wholesale rather than mutated, so the render thread always
 * sees a complete list.
 */
public final class DebugOverlay {

	public static final int KIND_LOC = 0;
	public static final int KIND_NPC = 1;
	public static final int KIND_PLAYER = 2;

	/**
	 * One marked thing.
	 * <p>
	 * Scenery and creatures are stored differently on purpose. A loc never moves,
	 * so its anchor is baked in at refresh time. A creature walks, so storing its
	 * position would pin the marker to wherever it stood when the list was last
	 * rebuilt — half a second of drift, and worse while it runs. Creatures are
	 * therefore held as an <b>index into the client's entity array</b> and their
	 * position is read live in the frame, exactly as their model is.
	 */
	public static final class Target {
		public final int kind;
		public final int id;
		public final String name;
		/**
		 * Identity of the marked creature, or null for scenery.
		 * <p>
		 * The marker used to hold a bare array index and follow whatever was in that
		 * slot each frame. That is wrong in a way you would rarely catch: when a
		 * creature despawns and the slot is reused, the crosshair keeps drawing —
		 * now on a different creature, still wearing the old name. Carrying the
		 * handle lets the frame check before it draws.
		 */
		public final EntityHandle handle;
		/** Footprint centre in 1/128 tile units — scenery only. */
		public final int anchorX;
		public final int anchorZ;
		/** Half the model height — scenery only; creatures report their own. */
		public final int centreHeight;

		/** Scenery, fixed in the world. */
		public Target(int id, String name, int anchorX, int anchorZ, int centreHeight) {
			this.kind = KIND_LOC;
			this.id = id;
			this.name = name;
			this.handle = null;
			this.anchorX = anchorX;
			this.anchorZ = anchorZ;
			this.centreHeight = centreHeight;
		}

		/** A creature, re-resolved from its handle every frame. */
		public Target(int kind, EntityHandle handle, int id, String name) {
			this.kind = kind;
			this.handle = handle;
			this.id = id;
			this.name = name;
			this.anchorX = 0;
			this.anchorZ = 0;
			this.centreHeight = 0;
		}

		/** Lookup hint for the adapter; -1 for scenery. Never an identity test. */
		public int getSlot() {
			return this.handle == null ? -1 : this.handle.getSlot();
		}
	}

	private static final Target[] NONE = new Target[0];

	private static volatile boolean enabled;
	private static volatile Target[] targets = NONE;

	/**
	 * Name filter, applied to creatures in the frame rather than at refresh time.
	 * <p>
	 * Scenery has to be gathered in advance because finding it means scanning the
	 * scene and resolving models. Creatures are already a short live list the
	 * client maintains, so they are filtered and drawn directly each frame — which
	 * means a creature that walks into view is marked immediately, and an index
	 * reused for a different creature can never be labelled with the old one's name.
	 */
	private static volatile NameFilter filter = NameFilter.EMPTY;

	private DebugOverlay() {
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean on) {
		enabled = on;
		if (!on) {
			targets = NONE;
			baseX = Integer.MIN_VALUE;
			baseZ = Integer.MIN_VALUE;
		}
	}

	public static Target[] getTargets() {
		return targets;
	}

	/**
	 * Scene origin the current targets were gathered against.
	 * <p>
	 * Scenery anchors are scene-local. When the player crosses a region boundary
	 * the client rebuilds the scene and shifts that origin, at which point every
	 * stored anchor silently refers to a different place in the world — markers
	 * detach from their objects and drift until the next refresh. Recording the
	 * origin lets the frame notice and skip them instead.
	 */
	private static volatile int baseX = Integer.MIN_VALUE;
	private static volatile int baseZ = Integer.MIN_VALUE;

	public static void setTargets(Target[] list, int sceneBaseX, int sceneBaseZ) {
		targets = list == null ? NONE : list;
		baseX = sceneBaseX;
		baseZ = sceneBaseZ;
	}

	/** True while the stored anchors still mean what they did when gathered. */
	public static boolean matchesScene(int sceneBaseX, int sceneBaseZ) {
		return baseX == sceneBaseX && baseZ == sceneBaseZ;
	}

	public static NameFilter getFilter() {
		return filter;
	}

	public static void setFilter(String text) {
		filter = NameFilter.parse(text);
	}

	/** True if a name passes the current filter. */
	public static boolean accepts(String name) {
		return filter.matches(name);
	}

	public static int count() {
		return targets.length;
	}
}
