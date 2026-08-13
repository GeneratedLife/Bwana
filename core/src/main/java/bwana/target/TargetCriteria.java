package bwana.target;

import bwana.inspect.EntityInfo;
import bwana.inspect.NameFilter;

/**
 * What counts as an acceptable target.
 * <p>
 * One description covering scenery, NPCs and players rather than a search per
 * kind. Everything the client can point at is already an {@link EntityInfo}, so
 * the only thing that varies between kinds is which fields are populated — and a
 * criteria object can simply ignore the ones that do not apply.
 * <p>
 * All conditions must hold. An unset condition does not constrain: the default
 * accepts any kind, any name, any id, within a generous distance, on screen.
 */
public final class TargetCriteria {

	/** Bit per kind so several can be accepted at once. */
	public static final int KIND_LOC = 1;
	public static final int KIND_NPC = 2;
	public static final int KIND_PLAYER = 4;
	public static final int KIND_ANY = KIND_LOC | KIND_NPC | KIND_PLAYER;

	public int kinds = KIND_ANY;

	/** Comma-separated names, matched as "any of these". Never null. */
	public NameFilter names = NameFilter.EMPTY;

	/** Config id, or -1 for any. */
	public int id = -1;

	/** Chebyshev tiles from the player. */
	public int maxDistance = 20;

	/**
	 * Require the target to project into the viewport.
	 * <p>
	 * On by default: a target you cannot see is one you cannot point at, and for a
	 * selection layer that is meant to feed interaction later, silently selecting
	 * something behind the camera would be a poor foundation.
	 */
	public boolean requireOnScreen = true;

	/**
	 * Targets to skip for now, and why they expire.
	 * <p>
	 * Without this, rejecting the nearest match and asking again returns the same
	 * one — so a caller that finds a target unusable has no way to say "not that
	 * one" and loops on it forever.
	 */
	public final ExclusionSet excluded = new ExclusionSet();

	public boolean accepts(EntityInfo entity, int distance) {
		return this.rejectionReason(entity, distance) == null;
	}

	/**
	 * Why this entity is unacceptable, or null if it is fine.
	 * <p>
	 * The reason is the whole value here: a selector that only answers yes or no
	 * cannot explain itself, and "why was that one not chosen" is the question you
	 * actually ask when the answer surprises you.
	 */
	public String rejectionReason(EntityInfo entity, int distance) {
		if (entity == null) {
			return "no entity";
		}
		if (!this.acceptsKind(entity.kind)) {
			return "WRONG KIND";
		}
		if (this.id >= 0 && entity.id != this.id) {
			return "WRONG ID";
		}
		if (!this.names.matches(entity.name)) {
			return "NAME MISMATCH";
		}
		if (this.excluded.contains(entity.handle)) {
			return "EXCLUDED";
		}
		if (distance > this.maxDistance) {
			return "OUT OF RANGE";
		}
		if (this.requireOnScreen && (!entity.visible || entity.screenX < 0)) {
			return "OFF SCREEN";
		}
		return null;
	}

	public boolean acceptsKind(int entityKind) {
		if (entityKind == EntityInfo.KIND_LOC) {
			return (this.kinds & KIND_LOC) != 0;
		}
		if (entityKind == EntityInfo.KIND_NPC) {
			return (this.kinds & KIND_NPC) != 0;
		}
		if (entityKind == EntityInfo.KIND_PLAYER) {
			return (this.kinds & KIND_PLAYER) != 0;
		}
		return false;
	}

	public TargetCriteria copy() {
		TargetCriteria var1 = new TargetCriteria();
		var1.kinds = this.kinds;
		var1.names = this.names;
		var1.id = this.id;
		var1.maxDistance = this.maxDistance;
		var1.requireOnScreen = this.requireOnScreen;
		return var1;
	}

	public String describe() {
		StringBuilder var1 = new StringBuilder();
		var1.append(this.names.isEmpty() ? "any name" : this.names.toString());
		// Kinds belong in here. Without them "no match for Tree" reads as a name
		// problem even when the real reason is that scenery was not being scanned
		// at all, which is a checkbox away and looks identical from outside.
		var1.append(" [");
		if (this.kinds == 0) {
			var1.append("NO KINDS SELECTED");
		} else {
			var1.append((this.kinds & KIND_LOC) != 0 ? "objects " : "")
				.append((this.kinds & KIND_NPC) != 0 ? "npcs " : "")
				.append((this.kinds & KIND_PLAYER) != 0 ? "players" : "");
		}
		var1.append("]");
		if (this.id >= 0) {
			var1.append(", id ").append(this.id);
		}
		var1.append(", within ").append(this.maxDistance).append(" tiles");
		if (!this.requireOnScreen) {
			var1.append(", off screen allowed");
		}
		return var1.toString();
	}
}
