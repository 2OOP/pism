package org.toop;

import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;


public class Main {
	public static void main(String[] args) {
		initSystems();
	}

	private static void initSystems() throws NetworkingInitializationException {
		new NetworkingClientManager();
	}

}