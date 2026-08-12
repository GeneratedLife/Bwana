package bwana.target;

import bwana.inspect.EntityInfo;

/**
 * One entity the selector considered, and what became of it.
 * <p>
 * The point of keeping rejected candidates rather than filtering them away is
 * that "why did it pick that one" is otherwise unanswerable. A search that
 * silently drops everything failing a condition looks identical whether it
 * rejected four goblins for being out of range or never saw them at all.
 */
public final class Candidate {

	public final EntityInfo entity;
	/** Chebyshev tiles from the player. */
	public final int distance;
	/** Why it was rejected, or null if it was acceptable. */
	public final String reason;
	/** True for the one actually chosen. */
	public boolean selected;

	public Candidate(EntityInfo entity, int distance, String reason) {
		this.entity = entity;
		this.distance = distance;
		this.reason = reason;
	}

	public boolean isEligible() {
		return this.reason == null;
	}
}
