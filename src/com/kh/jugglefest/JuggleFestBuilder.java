package com.kh.jugglefest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.kh.jugglefest.data.Circuit;
import com.kh.jugglefest.data.Data;
import com.kh.jugglefest.data.Juggler;

public class JuggleFestBuilder {

	/*
	 * Ordered listing of Circuits by Circuit Id
	 */
	private final List<Circuit> circuits;

	/*
	 * Ordered listing of Jugglers by Juggler Id
	 */
	private final List<Juggler> jugglers;

	/*
	 * Jugglers awaiting to be assigned to a Circuit by their preference, Dot
	 * Product and circuit availability
	 */
	private final Queue<Integer> q1;

	/*
	 * Jugglers with no remaining preferences to test awaiting to be assigned to a
	 * Circuit by Dot Product and circuit availability
	 */
	private final Queue<Integer> q2;

	/*
	 * Indicates whether a juggler (id=key) is free or taken
	 */
	private final boolean[] free;

	/*
	 * Count of members added to a circuit
	 */
	private final int[] membership;

	/*
	 * Maximum number of Jugglers that can be assigned to a Circuit
	 */
	private final int max_membership;

	/*
	 * Resulting match between Circuit and Jugglers
	 */
	private final int[][] matches;

	public JuggleFestBuilder(Data data) {
		this.circuits = data.getCircuits();
		this.jugglers = data.getJugglers();

		this.free = new boolean[jugglers.size()];
		Arrays.fill(free, true);

		this.max_membership = jugglers.size() / circuits.size();
		this.membership = new int[circuits.size()];

		this.q1 = new LinkedList<>();
		this.q2 = new LinkedList<>();
		for (int i = 0; i < jugglers.size(); i++) {
			q1.add(i); // jugglers with preferences
		}

		// Resulting matches/pairs
		this.matches = new int[circuits.size()][max_membership];
		for (int i = 0; i < matches.length; i++) {
			Arrays.fill(matches[i], -1); // indicate unsigned
		}
	}

	public boolean hasVacancy(int cid) {
		return !isCircuitFull(cid);
	}

	public boolean isCircuitFull(int cid) {
		return membership[cid] == max_membership;
	}

	public int[][] getMatches() {
		return matches;
	}

	public int[] getMemberships() {
		return membership;
	}

	public void addMember(int cid, int jid) {
		matches[cid][membership[cid]] = jid;
		membership[cid]++; // increase count/ next slot available
		free[jid] = false;
	}

	public void replaceMember(int cid, int old_jid, int new_jid) {
		int i = indexOfMember(old_jid, cid);
		matches[cid][i] = new_jid;
		free[new_jid] = false;
		free[old_jid] = true;
	}

	public JuggleFest build() {

		// Attempt to match all by their preferences
		boolean run1 = true;
		while (!q1.isEmpty()) {

			if (run1) {
				System.out.println("Match all by their preferences count=" + q1.size());
				run1 = false;
			}

			int jid = q1.remove();
			Juggler juggler = jugglers.get(jid);

			while (free[jid] && juggler.hasUncheckedPreference()) {
				int cid = juggler.getNextPreference();
				if (hasVacancy(cid)) {
					addMember(cid, jid);
				} else {
					int min = getWeakestMember(cid);
					if (compareDotProduct(cid, min, jid) < 0) {
						replaceMember(cid, min, jid);
						q1.add(min);
					}
				}
			}

			if (free[jid] && !juggler.hasUncheckedPreference()) {
				q2.add(jid);
			}

		}

		boolean run2 = true;
		int chead = 0;
		while (!q2.isEmpty()) {

			if (run2) {
				System.out.println("Match all by highest Dot Product count=" + q2.size());
				run2 = false;
			}

			int jid = q2.remove();
			while (free[jid]) {

				// find next available circuit
				for (; chead < circuits.size() && !hasVacancy(chead); chead++)
					;

				// find best juggler for circuit
				// starting from the position of the current juggler
				int max = -1;

				for (int x = jid; x < jugglers.size(); x++) {

					if (free[x]) {
						if (max == -1) {
							max = x;
							continue;
						}

						// determine best match
						if (compareDotProduct(chead, max, x) < 0) {
							max = x;
						}
					}

				}

				addMember(chead, max);
			}
		}

		// Build Pairing
		Map<Circuit, Juggler[]> ret = new LinkedHashMap<>(); // maintain insertion order
		for (int i = 0; i < matches.length; i++) {
			Juggler[] aJuggler = new Juggler[matches[i].length];
			for (int j = 0; j < aJuggler.length; j++) {
				aJuggler[j] = jugglers.get(matches[i][j]);
			}
			ret.put(circuits.get(i), aJuggler);
		}

		return new JuggleFest(ret);
	}

	public int indexOfMember(int jid, int cid) {
		int i = -1;
		for (int j = 0; j < matches[cid].length; j++) {
			if (jid == matches[cid][j]) {
				i = j;
				break;
			}
		}
		return i;
	}

	public int getWeakestMember(int cid) {

		int min = -1; // juggler id
		for (int jid : matches[cid]) {

			if (jid == -1) {
				continue;
			}

			if (min == -1) {
				min = jid;
				continue;
			}

			if (compareDotProduct(cid, jid, min) < 0) {
				min = jid;
			}

		}

		return min;
	}

	public int weakestMatch(int cid) {
		Circuit c = circuits.get(cid);
		int min = -1;
		for (int jid : matches[cid]) {
			Juggler j = jugglers.get(jid);
			int dp = getDotProduct(j, c);
			if (dp < min) {
				dp = min;
			}
		}
		return min;
	}

	public int compareDotProduct(int cid, int jid_1, int jid_2) {
		return compare(circuits.get(cid), jugglers.get(jid_1), jugglers.get(jid_2));
	}

	public static int compare(Circuit c, Juggler j1, Juggler j2) {
		int dp1 = getDotProduct(j1, c);
		int dp2 = getDotProduct(j2, c);
		int result = Integer.compare(dp1, dp2);
		return result;
	}

	public static Integer getDotProduct(Juggler j, Circuit c) {
		int dotProduct = 0;
		dotProduct += j.handToEye * c.handToEye;
		dotProduct += j.endurance * c.endurance;
		dotProduct += j.pizzazz * c.pizzazz;
		return dotProduct;
	}
}
