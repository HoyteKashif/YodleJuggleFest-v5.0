package com.kh.jugglefest;

import com.kh.jugglefest.data.Data;

public class Application {
	public static void main(String[] args) {

		Data data = Data.loadData();
		JuggleFestBuilder builder = new JuggleFestBuilder(data);
		JuggleFest jf = builder.build();
		jf.printPairs();

	}
}
