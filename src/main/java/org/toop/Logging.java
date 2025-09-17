package org.toop;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.toop.eventbus.EventRegistry;

public final class Logging {
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
