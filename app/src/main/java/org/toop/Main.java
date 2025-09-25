package org.toop;

import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;


public class Main {
	public static void main(String[] args) {
		initSystems();
        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);
	}

	private static void initSystems() throws NetworkingInitializationException {
		new NetworkingClientManager();
	}

}