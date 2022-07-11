package com.kh.jugglefest;

import java.util.Collections;
import java.util.Map;

import com.kh.jugglefest.data.Circuit;
import com.kh.jugglefest.data.Juggler;

public class JuggleFest {

	private final Map<Circuit, Juggler[]> map;

	public JuggleFest(Map<Circuit, Juggler[]> map) {
		this.map = map;
	}

	public Map<Circuit, Juggler[]> getPairing() {
		return Collections.unmodifiableMap(map);
	}

	public void printPairs() {
		StringBuilder sb = new StringBuilder();
		for (Circuit c : map.keySet()) {
			sb.append("C").append(c.id).append(" ");
			Juggler[] a = map.get(c);
			sb.append("[");
			for (int i = 0; i < a.length; i++) {
				sb.append("J").append(a[i].id);
				if (i < a.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]");
			System.out.println(sb.toString());
			sb.delete(0, sb.length());
		}
	}

}
