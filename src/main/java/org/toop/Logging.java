package org.toop;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/** Options for logging. */
public final class Logging {
    public static void disableAllLogs() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.setLevel(Level.OFF);
        ctx.updateLoggers();
    }

    public static void enableAllLogs(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static <T> void disableLogsForClass(Class<T> class_) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig specificConfig = new LoggerConfig(class_.getName(), Level.OFF, true);
        config.addLogger(class_.getName(), specificConfig);
        ctx.updateLoggers();
    }

    public static <T> void enableLogsForClass(Class<T> class_, Level levelToLog) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggers().get(class_.getName());
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig(class_.getName(), levelToLog, true);
            config.addLogger(class_.getName(), loggerConfig);
        } else {
            loggerConfig.setLevel(levelToLog);
        }
        ctx.updateLoggers();
    }

    public static <T> void enableAllLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.ALL);
    }

    public static <T> void enableDebugLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.DEBUG);
    }

    public static <T> void enableErrorLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.ERROR);
    }

    public static <T> void enableFatalLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.FATAL);
    }

    public static <T> void enableInfoLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.INFO);
    }

    public static <T> void enableTraceLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.TRACE);
    }
}
