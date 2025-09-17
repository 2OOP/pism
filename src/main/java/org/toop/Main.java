package org.toop;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.ServerManager;
import org.toop.server.frontend.ConnectionManager;
import org.toop.server.backend.TcpServer;

import org.toop.game.*;
import org.toop.game.tictactoe.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        initSystems();
        disableLogs();

		ConsoleGui console = new ConsoleGui();

		do {
			console.print();
		} while (console.next());

		console.print();
    }

    public static void initSystems() {
        new ServerManager();
        new ConnectionManager();
    }

    public static void disableLogs() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(EventRegistry.class.getName());
        loggerConfig.setLevel(Level.OFF);
        ctx.updateLoggers(); // apply changes immediately
    }

    public static void enableLogs(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(EventRegistry.class.getName());
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

}
