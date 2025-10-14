package org.toop.app;

public record GameInformation(
        String[] playerName,
        boolean[] isPlayerHuman,
        int[] computerDifficulty,
        int[] computerThinkTime,
        boolean isConnectionLocal,
        String serverIP,
        String serverPort) {}
