package bwana.vision;

/**
 * Supplies raw viewport pixels to the vision layer.
 * <p>
 * Deliberately <b>not</b> part of {@code GameState}. Game state is about what the
 * game knows — skills, entities, position. This is about what the game
 * <i>drew</i>, which is a different concern with a different cost profile and a
 * different consumer. Keeping them apart means a tool that wants pixels does not
 * drag in the whole state interface, and vice versa.
 * <p>
 * <b>Game thread only.</b> The implementation copies out of a live render buffer;
 * reading it while the renderer is mid-frame would tear.
 */
public interface FrameSource {

	/**
	 * Copy the current viewport.
	 *
	 * @return a fresh immutable frame, or null if the viewport does not exist yet
	 *         (before login, or before the first scene has been drawn)
	 */
	Frame captureViewport();
}
