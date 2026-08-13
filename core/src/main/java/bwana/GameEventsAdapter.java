package bwana;

/**
 * No-op implementation of {@link GameEvents}. Extend this and override only the
 * callbacks you care about.
 */
public class GameEventsAdapter implements GameEvents {

	public void onLogin() {
	}

	public void onLogout() {
	}

	public void onTick() {
	}

	public void onExperienceGained(int skill, int oldXp, int newXp) {
	}

	public void onChatMessage(int type, String sender, String text) {
	}
}
