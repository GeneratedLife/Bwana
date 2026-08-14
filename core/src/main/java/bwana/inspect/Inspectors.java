package bwana.inspect;

import bwana.target.Candidate;
import bwana.target.TargetCriteria;

/**
 * The parts of {@link EntityInspector} that are the same on every revision.
 * <p>
 * <b>Why these are not in the adapter.</b> An adapter's job is to answer things
 * only its client knows: where a model was picked, what a tile projects to, which
 * array an entity lives in. Filtering the eligible candidates out of a list, or
 * ordering entities by what a person probably meant, is neither — it is policy,
 * and it is identical whichever client supplied the list.
 * <p>
 * They were nonetheless written inside rev 225's client, because that is where the
 * interface happened to be implemented. Left there, every future revision would
 * have reimplemented them, and the copies would have drifted. That is exactly the
 * failure {@link EntityHandle} was extracted to prevent, and the revision-coupling
 * audit's first recommendation; this is the same move at a larger scale.
 * <p>
 * An adapter implements {@link EntityInspector#findCandidates}, which genuinely
 * needs the client, and delegates the rest here.
 */
public final class Inspectors {

	/**
	 * How far out, in tiles, a search looks by default.
	 * <p>
	 * Policy rather than a client fact: it is a judgement about how far away a
	 * player still means something, not a limit any revision imposes. Lives here so
	 * one number serves every adapter.
	 */
	public static final int PICK_RADIUS = 26;

	private Inspectors() {
	}

	/**
	 * The eligible entities from a candidate scan, best first, at most {@code max}.
	 * <p>
	 * Over-fetches by 16 before filtering, because {@link Candidate} carries
	 * rejected entries too — asking for exactly {@code max} candidates would return
	 * fewer than {@code max} eligible ones whenever anything was rejected.
	 */
	public static EntityInfo[] findTargets(EntityInspector inspector, TargetCriteria criteria, int max) {
		Candidate[] candidates = inspector.findCandidates(criteria, max + 16);
		int eligible = 0;
		for (int i = 0; i < candidates.length && eligible < max; i++) {
			if (candidates[i].isEligible()) {
				eligible++;
			}
		}
		EntityInfo[] found = new EntityInfo[eligible];
		int kept = 0;
		for (int i = 0; i < candidates.length && kept < eligible; i++) {
			if (candidates[i].isEligible()) {
				found[kept++] = candidates[i].entity;
			}
		}
		return found;
	}

	/**
	 * Everything whose name matches, on screen or not.
	 * <p>
	 * The loose search: scenery and creatures alike, within {@link #PICK_RADIUS},
	 * with no requirement that it be visible.
	 */
	public static EntityInfo[] findObjects(EntityInspector inspector, String nameContains, int max) {
		TargetCriteria criteria = new TargetCriteria();
		criteria.names = NameFilter.parse(nameContains);
		criteria.requireOnScreen = false;
		criteria.maxDistance = PICK_RADIUS;
		return findTargets(inspector, criteria, max);
	}

	/**
	 * Sorts in place, most likely to have been meant first.
	 * <p>
	 * Kind decides the band and distance from the camera orders within it, so a
	 * creature always outranks a rock and the nearer of two rocks wins. Distance is
	 * capped so it can never push an entity into the next band: the ordering is
	 * "creatures, then loot, then named scenery, then unnamed, then bare tiles",
	 * and no amount of distance may reorder those.
	 * <p>
	 * A selection sort, because these lists are tens of entries and a stable, plain
	 * one is easier to reason about than a faster one.
	 *
	 * @param cameraX scene-local camera position, as {@code CameraInfo} reports it
	 */
	public static void rankByRelevance(EntityInfo[] entities, int cameraX, int cameraZ) {
		long[] rank = new long[entities.length];
		for (int i = 0; i < entities.length; i++) {
			EntityInfo entity = entities[i];
			int band;
			if (entity.kind == EntityInfo.KIND_NPC || entity.kind == EntityInfo.KIND_PLAYER) {
				band = 0;
			} else if (entity.kind == EntityInfo.KIND_GROUND_ITEM) {
				band = 1;
			} else if (entity.kind == EntityInfo.KIND_TILE) {
				band = 4;
			} else {
				band = isNamed(entity.name) ? 2 : 3;
			}
			int dx = entity.tileX * 128 + 64 - cameraX;
			int dz = entity.tileZ * 128 + 64 - cameraZ;
			long distance = (long) dx * (long) dx + (long) dz * (long) dz;
			if (distance > 16777215L) {
				distance = 16777215L;
			}
			rank[i] = (long) band * 16777216L + distance;
		}
		for (int i = 0; i < entities.length; i++) {
			int best = i;
			for (int j = i + 1; j < entities.length; j++) {
				if (rank[j] < rank[best]) {
					best = j;
				}
			}
			long swapRank = rank[i];
			rank[i] = rank[best];
			rank[best] = swapRank;
			EntityInfo swap = entities[i];
			entities[i] = entities[best];
			entities[best] = swap;
		}
	}

	/** Scenery with no name of its own is rendered as "(unnamed)" and ranks below named. */
	private static boolean isNamed(String name) {
		return name != null && name.length() > 0 && name.charAt(0) != '(';
	}
}
