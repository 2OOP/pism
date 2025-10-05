package org.toop.app;

public record GameInformation(String[] playerName, boolean[] isPlayerHuman, int[] computerDifficulty,
                              boolean isConnectionLocal, String serverIP, String serverPort) {
}
