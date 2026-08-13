package bwana;

/**
 * Things that happen, as opposed to {@link GameState}, which is things that are.
 * <p>
 * <b>Every callback runs on the game thread</b>, inside the client's packet loop.
 * Two consequences you cannot design around:
 * <ul>
 * <li>Do not block. A slow listener stalls the game loop and the network read.</li>
 * <li>Do not touch Swing. Hand work to the UI with
 * {@code SwingUtilities.invokeLater}.</li>
 * </ul>
 * Prefer extending {@link GameEventsAdapter} so adding a method here later does
 * not break your implementations — this codebase targets Java 1.6, so interfaces
 * cannot carry default methods.
 */
public interface GameEvents {

	void onLogin();

	void onLogout();

	/**
	 * Fired once per client tick — 50 times a second — whether logged in or not.
	 * <p>
	 * This is the only correct place to read {@link GameState} for a UI, because
	 * it runs on the game thread. Snapshot the few values you need into fields and
	 * hand those to Swing; do not do work here proportional to anything.
	 * <p>
	 * At 50 Hz, throttle before touching the UI. Pushing an
	 * {@code invokeLater} every tick will flood the event queue.
	 */
	void onTick();

	/**
	 * Fired when the server reports new experience for a skill.
	 * <p>
	 * <b>You get one of these per skill at login</b>, reporting your existing
	 * total rather than a gain. Track "seen since logout" per skill and treat the
	 * first event for each as a baseline, or you will count a lifetime of
	 * experience as session gain.
	 * <p>
	 * <b>Do not detect that baseline by testing {@code oldXp == 0}.</b> The value
	 * is ambiguous — it also means "first experience ever in this skill", so on a
	 * new account the first gain in each skill would be swallowed. Keep an
	 * explicit flag array and clear it in {@link #onLogout}, not
	 * {@link #onLogin}: the baseline burst arrives before onLogin fires.
	 * <p>
	 * Note the server also sends baselines for the two disabled skill slots, 18
	 * and 19 — see {@link Skill#isEnabled}.
	 *
	 * @param oldXp the value held before this packet; 0 both at login and for a
	 *              skill you have never trained
	 */
	void onExperienceGained(int skill, int oldXp, int newXp);

	/**
	 * Fired for every message added to the chat area, including game messages
	 * with an empty sender.
	 */
	void onChatMessage(int type, String sender, String text);
}
