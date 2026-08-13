package bwana.target;

import bwana.GameEventBus;
import bwana.GameEventsAdapter;
import bwana.inspect.DebugOverlay;
import bwana.inspect.EntityInfo;
import bwana.inspect.EntityInspector;

/**
 * Keeps one target selected: finds it, holds it, notices when it is gone.
 * <p>
 * The distinction that shapes this is between <b>acquiring</b> and <b>holding</b>.
 * Searching every tick would make the selection flicker between equidistant
 * candidates and would re-scan the scene needlessly. So once a target is found it
 * is re-resolved by identity instead — cheap, and it keeps hold of the same
 * goblin even when a nearer one wanders past.
 * <p>
 * Runs on the game thread. This milestone only identifies and tracks; nothing
 * here touches input or the game.
 */
public final class TargetTracker extends GameEventsAdapter {

	/**
	 * Ticks between searches while nothing is selected.
	 * <p>
	 * A scan sweeps a few thousand tiles, so this is a budget, not a preference.
	 * Five a second was enough to stall the game loop in a city — and a stalled loop
	 * stops reading packets, which is how a client ends up disconnected for standing
	 * in Varrock. Twice a second still finds a target well within the time it takes
	 * to walk to one.
	 */
	private static final int SEARCH_INTERVAL = 25;

	/** Ticks between candidate-list refreshes for the on-screen readout, when it is shown. */
	private static final int HUD_INTERVAL = 25;

	/** Ticks a lost target is reported before searching resumes, so it is readable. */
	private static final int LOST_LINGER = 25;

	/**
	 * How many name-matching candidates to pull back before filtering them.
	 * <p>
	 * This used to be 12, which is a display-sized number that had quietly become a
	 * <i>search</i>-sized one. The candidate list is nearest-first and truncated
	 * before eligibility is considered, so twelve unusable things standing closer
	 * than the nearest usable one hide it completely. Felling trees is the case that
	 * exposed it: every chopped tree leaves a "Tree stump" behind, the name filter
	 * matches it, and after a dozen the search reported "12 matched by name, none
	 * eligible" in a forest.
	 * <p>
	 * Selection wants a generous number; the readout wants a short one. Those are
	 * different needs and now have different limits — see {@link TargetHud}.
	 */
	private static final int CANDIDATE_LIMIT = 64;

	private final TargetCriteria criteria = new TargetCriteria();
	private volatile boolean enabled;
	private volatile Candidate[] candidates = new Candidate[0];
	/** Ticks between "why did nothing match" reports. onTick is 50 Hz. */
	private static final int FAILURE_REPORT_INTERVAL = 150;

	private int ticks;
	private int lostFor;
	private int hudTicks;
	private int quietTicks = FAILURE_REPORT_INTERVAL;

	public TargetCriteria getCriteria() {
		return this.criteria;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean on) {
		this.enabled = on;
		if (!on) {
			Selection.idle();
		} else {
			Selection.searching("enabled");
		}
	}

	/** Drop the current target and look again — used when the criteria change. */
	public void reset() {
		this.ticks = SEARCH_INTERVAL;
		this.lostFor = 0;
		if (this.enabled) {
			Selection.searching("criteria changed");
		}
	}

	public void onLogout() {
		Selection.idle();
	}

	public void onTick() {
		if (!this.enabled) {
			return;
		}
		EntityInspector var1 = GameEventBus.getInspector();
		if (var1 == null) {
			return;
		}

		EntityInfo var2 = Selection.getInfo();
		if (Selection.getState() == Selection.STATE_FOUND && var2 != null) {
			// hold: re-resolve the same entity rather than searching again
			EntityInfo var3 = var1.resolveTarget(var2);
			if (var3 == null) {
				Selection.lost(describeLoss(var2));
				this.lostFor = 0;
				return;
			}
			// still there but no longer acceptable -- walked out of range, or
			// behind us now -- which is a loss of a different kind
			int var4 = distance(var3);
			if (!this.criteria.accepts(var3, var4)) {
				Selection.lost(var3.name + " no longer matches (" + var4 + " tiles)");
				this.lostFor = 0;
				return;
			}
			Selection.refresh(var3);

			// Refresh the readout while holding, so distance and the candidate list
			// stay live as you walk. This scan is for display only -- the selection
			// still comes from resolveTarget above, so watching cannot change what
			// is held.
			if (TargetHud.isEnabled() && ++this.hudTicks >= HUD_INTERVAL) {
				this.hudTicks = 0;
				Candidate[] var9 = var1.findCandidates(this.criteria, CANDIDATE_LIMIT);
				markHeld(var9, var3);
				this.candidates = var9;
				TargetHud.update(this.criteria, var9);
			}
			return;
		}

		if (Selection.getState() == Selection.STATE_LOST && ++this.lostFor < LOST_LINGER) {
			return;
		}

		if (++this.ticks < SEARCH_INTERVAL) {
			if (Selection.getState() != Selection.STATE_SEARCHING) {
				Selection.searching("looking for " + this.criteria.describe());
			}
			return;
		}
		this.ticks = 0;

		// the candidate list is what the readout shows, so selecting from it
		// guarantees the report and the choice cannot disagree
		Candidate[] var5 = var1.findCandidates(this.criteria, CANDIDATE_LIMIT);
		this.candidates = var5;

		Candidate var7 = null;
		for (int var8 = 0; var8 < var5.length; var8++) {
			if (var5[var8].isEligible()) {
				var7 = var5[var8];
				break;
			}
		}
		if (var7 == null) {
			Selection.searching(var5.length == 0
				? "no match for " + this.criteria.describe()
				: var5.length + " matched by name, none eligible");
			this.reportFailure(var5);
			TargetHud.update(this.criteria, var5);
			return;
		}
		DebugOverlay.Target var6 = var1.markerFor(var7.entity);
		if (var6 == null) {
			Selection.searching("found " + var7.entity.name + " but could not mark it");
			TargetHud.update(this.criteria, var5);
			return;
		}
		Selection.found(var7.entity, var6);
		TargetHud.update(this.criteria, var5);
	}

	public Candidate[] getCandidates() {
		return this.candidates;
	}

	/**
	 * Print why a search came back empty-handed.
	 * <p>
	 * "None eligible" names the symptom but not the cause, and the causes look
	 * nothing alike: out of range, off screen and excluded need different fixes.
	 * Rate-limited to once every few seconds — a search that fails usually keeps
	 * failing, and the second line tells you nothing the first did not.
	 */
	private void reportFailure(Candidate[] found) {
		if (++this.quietTicks < FAILURE_REPORT_INTERVAL) {
			return;
		}
		this.quietTicks = 0;
		StringBuilder var2 = new StringBuilder("bwana/target no target: ");
		var2.append(this.criteria.describe());
		var2.append("  |  ").append(found.length).append(" candidate(s)");
		for (int var3 = 0; var3 < found.length && var3 < 5; var3++) {
			var2.append("\n    ").append(found[var3].entity.name)
				.append(" #").append(found[var3].entity.id)
				.append("  ").append(found[var3].distance).append(" tiles  ")
				.append(found[var3].reason == null ? "ELIGIBLE" : found[var3].reason);
		}
		if (found.length > 5) {
			var2.append("\n    ...").append(found.length - 5).append(" more");
		}
		System.out.println(var2.toString());
	}

	/**
	 * Mark the held target as the selected candidate rather than the nearest one.
	 * <p>
	 * A fresh scan marks whichever is nearest, but while a target is locked that
	 * may not be the one being held — the whole point of holding is that a nearer
	 * candidate does not steal the selection. Showing the nearest as SELECTED
	 * would misreport exactly the behaviour this readout exists to verify.
	 */
	private static void markHeld(Candidate[] candidates, EntityInfo held) {
		for (int var2 = 0; var2 < candidates.length; var2++) {
			candidates[var2].selected = false;
		}
		for (int var3 = 0; var3 < candidates.length; var3++) {
			if (candidates[var3].entity.isSameEntity(held)) {
				candidates[var3].selected = true;
				return;
			}
		}
	}

	/** Distance in tiles from the player, matching how the search ranks candidates. */
	private static int distance(EntityInfo entity) {
		bwana.GameState var1 = GameEventBus.getGameState();
		bwana.model.PlayerInfo var2 = var1 == null ? null : var1.getPlayer();
		if (var2 == null) {
			return 0;
		}
		return Math.max(Math.abs(entity.worldX - var2.worldX), Math.abs(entity.worldY - var2.worldY));
	}

	private static String describeLoss(EntityInfo entity) {
		if (entity.kind == EntityInfo.KIND_LOC) {
			return entity.name + " is no longer at that tile";
		}
		return entity.name + " despawned or left the scene";
	}
}
