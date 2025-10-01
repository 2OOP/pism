package org.toop;

import org.toop.app.App;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

public final class Main {
	static void main(String[] args) {
		initSystems();
		App.run(args);
	}

	private static void initSystems() throws NetworkingInitializationException {
		new NetworkingClientManager();
	}
}