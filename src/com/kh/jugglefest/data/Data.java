package com.kh.jugglefest.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Data {

	private final File file;
	private final List<Circuit> circuits;
	private final List<Juggler> jugglers;

	public static Data loadData() {
		Data data = new Data(new File("data//input.txt"));
		data.load();
		return data;
	}

	public static Data loadTestData() {
		Data data = new Data(new File("data//input.test.txt"));
		data.load();
		return data;
	}

	public Data(File file) {
		this.file = file;
		this.circuits = new ArrayList<>();
		this.jugglers = new ArrayList<>();
	}

	public Juggler getJuggler(int id) {
		return jugglers.get(id);
	}

	public Circuit getCircuit(int id) {
		return circuits.get(id);
	}

	public void load() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;

				String[] data = line.split(" ");

				if (data[0].equals("C")) {
					circuits.add(newCircuit(data));
				} else {
					jugglers.add(newJuggler(data));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<Circuit> getCircuits() {
		return circuits;
	}

	public List<Juggler> getJugglers() {
		return jugglers;
	}

	private static Circuit newCircuit(String[] data) {
		int id = parseInt(data[1], 1);
		int handToEye = parseInt(data[2], 2);
		int endurance = parseInt(data[3], 2);
		int pizzazz = parseInt(data[4], 2);

		return new Circuit(id, handToEye, endurance, pizzazz);
	}

	private static Juggler newJuggler(String[] data) {
		int id = parseInt(data[1], 1);
		int handToEye = parseInt(data[2], 2);
		int endurance = parseInt(data[3], 2);
		int pizzazz = parseInt(data[4], 2);

		String[] strCircuitPreferences = data[5].split(",");
		int[] circuitPreferences = new int[strCircuitPreferences.length];
		for (int i = 0; i < circuitPreferences.length; i++) {
			circuitPreferences[i] = parseInt(strCircuitPreferences[i], 1);
		}

		return new Juggler(id, handToEye, endurance, pizzazz, circuitPreferences);
	}

	private static int parseInt(String str, int start) {
		return Integer.valueOf(str.substring(start));
	}
}
