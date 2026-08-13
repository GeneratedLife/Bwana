package bwana;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Static dispatch point between the client and the toolkit.
 * <p>
 * Deliberately static: it lets the hooks inside {@code deob.client} be a single
 * line each, with no constructor plumbing threaded through an 11,000-line class.
 * That keeps the fork's diff reviewable, which is the whole point of Phase 7.
 * <p>
 * Listeners are held in a {@link CopyOnWriteArrayList} because they are added
 * from the Swing thread and fired from the game thread. Callbacks are wrapped in
 * try/catch so a broken listener cannot take the game loop down with it.
 */
public final class GameEventBus {

	private static final List<GameEvents> LISTENERS = new CopyOnWriteArrayList<GameEvents>();

	/** Last login state we reported, so we only fire on transitions. */
	private static boolean loggedIn = false;

	/** Set once by the client at startup. */
	/**
	 * Set once at startup and read from the game thread on every chat message, so
	 * it is not synchronised. Nothing reassigns it after {@link Bwana#start}.
	 */
	private static Revision REVISION;

	private static GameState state;

	/** Set once by the client at startup. */
	private static WorldQuery query;

	/** Set once by the client at startup. */
	private static bwana.inspect.EntityInspector inspector;

	/** Set once by the client at startup. */
	private static bwana.action.ActionExecutor executor;

	private GameEventBus() {
	}

	public static void addListener(GameEvents listener) {
		if (listener != null) {
			LISTENERS.add(listener);
		}
	}

	public static void removeListener(GameEvents listener) {
		LISTENERS.remove(listener);
	}

	/**
	 * Called by the adapter before anything else, since chat translation and every
	 * skill name depend on it.
	 */
	public static void setRevision(Revision revision) {
		REVISION = revision;
	}

	/**
	 * Which revision is being talked to, or null if the toolkit was started
	 * without one.
	 */
	public static Revision getRevision() {
		return REVISION;
	}

	/** Called by the client once it has constructed itself. */
	public static void setGameState(GameState gameState) {
		state = gameState;
	}

	/**
	 * The live game state, or null before the client has started.
	 * <p>
	 * Read it from inside a {@link GameEvents} callback — see the threading note
	 * on {@link GameState}.
	 */
	public static GameState getGameState() {
		return state;
	}

	/** Called by the client once it has constructed itself. */
	public static void setWorldQuery(WorldQuery worldQuery) {
		query = worldQuery;
	}

	/**
	 * The expensive-read interface, or null before the client has started.
	 * <p>
	 * Call these on the game thread and sparingly — see {@link WorldQuery}.
	 */
	public static WorldQuery getWorldQuery() {
		return query;
	}

	public static void setInspector(bwana.inspect.EntityInspector entityInspector) {
		inspector = entityInspector;
	}

	/**
	 * Identifies what the game drew under the cursor, or null before startup.
	 * <p>
	 * Game thread only — see {@link bwana.inspect.EntityInspector}.
	 */
	public static bwana.inspect.EntityInspector getInspector() {
		return inspector;
	}

	/** Set once by the client at startup. */
	private static bwana.widget.WidgetSource widgets;

	public static void setWidgetSource(bwana.widget.WidgetSource source) {
		widgets = source;
	}

	/**
	 * Reads the game's interfaces, or null before startup.
	 * <p>
	 * Game thread only — see {@link bwana.widget.WidgetSource}.
	 */
	public static bwana.widget.WidgetSource getWidgetSource() {
		return widgets;
	}

	/** Set once by the client at startup. */
	private static bwana.nav.CollisionSource collision;

	public static void setCollisionSource(bwana.nav.CollisionSource source) {
		collision = source;
	}

	/**
	 * Movement flags for the loaded scene, or null before startup.
	 * <p>
	 * Game thread only — see {@link bwana.nav.CollisionSource}.
	 */
	public static bwana.nav.CollisionSource getCollisionSource() {
		return collision;
	}

	public static void setActionExecutor(bwana.action.ActionExecutor actionExecutor) {
		executor = actionExecutor;
	}

	/**
	 * The one interface in the toolkit that can change the game, or null before
	 * startup.
	 * <p>
	 * Game thread only — see {@link bwana.action.ActionExecutor}.
	 */
	public static bwana.action.ActionExecutor getActionExecutor() {
		return executor;
	}

	// ---- fired from deob.client, always on the game thread ----

	/**
	 * The client's per-tick pump. Call once from the game loop with the current
	 * login flag.
	 * <p>
	 * Fires onLogin/onLogout on transitions, then onTick unconditionally. Polling
	 * the flag centrally beats hooking each of the four places the client assigns
	 * it, and it cannot miss a path.
	 */
	public static void tick(boolean nowLoggedIn) {
		if (nowLoggedIn != loggedIn) {
			loggedIn = nowLoggedIn;
			for (GameEvents listener : LISTENERS) {
				try {
					if (nowLoggedIn) {
						listener.onLogin();
					} else {
						listener.onLogout();
					}
				} catch (Throwable var4) {
					report(var4);
				}
			}
		}
		for (GameEvents listener : LISTENERS) {
			try {
				listener.onTick();
			} catch (Throwable var3) {
				report(var3);
			}
		}
	}

	public static void fireExperienceGained(int skill, int oldXp, int newXp) {
		for (GameEvents listener : LISTENERS) {
			try {
				listener.onExperienceGained(skill, oldXp, newXp);
			} catch (Throwable var5) {
				report(var5);
			}
		}
	}

	/**
	 * @param type the raw type this client emits, translated here and delivered
	 *             to listeners as a {@link ChatType} kind
	 */
	public static void fireChatMessage(int type, String sender, String text) {
		// Translate once, at the edge. Doing it here rather than in each listener
		// is what lets ActionRunner compare against ChatType.GAME and be right on
		// every revision: by the time a listener sees it, the number means the
		// same thing everywhere.
		Revision revision = REVISION;
		int kind = revision == null ? type : revision.chatKind(type);
		for (GameEvents listener : LISTENERS) {
			try {
				listener.onChatMessage(kind, sender, text);
			} catch (Throwable var4) {
				report(var4);
			}
		}
	}

	private static void report(Throwable cause) {
		System.err.println("bwana: listener threw, continuing");
		cause.printStackTrace();
	}
}
