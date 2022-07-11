package com.kh.jugglefest.data;

public class Juggler {
	public final int id;
	public final int handToEye;
	public final int endurance;
	public final int pizzazz;
	private final int[] circuitPreferences;
	private int next_preference = 0;
	private static final int STOP_MATCHING_PREFERENCES = -1;

	public Juggler(int id, int handToEye, int endurance, int pizzazz, int[] circuitPreferences) {
		this.id = id;
		this.handToEye = handToEye;
		this.endurance = endurance;
		this.pizzazz = pizzazz;
		this.circuitPreferences = circuitPreferences;
	}

	public int[] getPreferences() {
		return circuitPreferences;
	}

	public boolean hasUncheckedPreference() {
		return next_preference != STOP_MATCHING_PREFERENCES;
	}

	public int getNextPreference() {

		if (next_preference == STOP_MATCHING_PREFERENCES)
			throw new IllegalStateException();

		int cid = circuitPreferences[next_preference++];

		if (next_preference == circuitPreferences.length) {
			next_preference = STOP_MATCHING_PREFERENCES;
		}

		return cid;
	}

}
