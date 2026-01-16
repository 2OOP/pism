package org.toop.framework.networking.server;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.util.concurrent.CompletableFuture;

public record GameResultFuture(OnlineGame<TurnBasedGame> game, CompletableFuture<Integer> result) {}
