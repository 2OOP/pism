package org.toop;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Utility class for configuring logging levels dynamically at runtime using Log4j 2.
 * <p>
 * Provides methods to enable or disable logs globally or per class, with support for
 * specifying log levels either via {@link Level} enums or string names.
 * </p>
 */
public final class Logging {

    /**
     * Disables all logging globally by setting the root logger level to {@link Level#OFF}.
     */
    public static void disableAllLogs() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.setLevel(Level.OFF);
        ctx.updateLoggers();
    }

    /**
     * Enables all logging globally by setting the root logger level to {@link Level#ALL}.
     */
    public static void enableAllLogs() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.setLevel(Level.ALL);
        ctx.updateLoggers();
    }

    /**
     * Enables global logging at a specific level by setting the root logger.
     *
     * @param level the logging level to enable for all logs
     */
    public static void enableAllLogs(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    /**
     * Verifies whether the provided string corresponds to a valid class name.
     *
     * @param className fully-qualified class name to check
     * @return true if the class exists, false otherwise
     */
    private static boolean verifyStringIsActualClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Internal helper to disable logs for a specific class by name.
     *
     * @param className fully-qualified class name
     */
    private static void disableLogsForClassInternal(String className) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        config.removeLogger(className);
        LoggerConfig specificConfig = new LoggerConfig(className, Level.OFF, false);
        config.addLogger(className, specificConfig);
        ctx.updateLoggers();
    }

    /**
     * Disables logs for a specific class.
     *
     * @param class_ the class for which logs should be disabled
     * @param <T>    type of the class
     */
    public static <T> void disableLogsForClass(Class<T> class_) {
        disableLogsForClassInternal(class_.getName());
    }

    /**
     * Disables logs for a class specified by fully-qualified name, if the class exists.
     *
     * @param className fully-qualified class name
     */
    public static void disableLogsForClass(String className) {
        if (verifyStringIsActualClass(className)) {
            disableLogsForClassInternal(className);
        }
    }

    /**
     * Internal helper to enable logs for a specific class at a specific level.
     *
     * @param className fully-qualified class name
     * @param level     logging level to set
     */
    private static void enableLogsForClassInternal(String className, Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggers().get(className);
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig(className, level, false);
            config.addLogger(className, loggerConfig);
        } else {
            loggerConfig.setLevel(level);
        }
        ctx.updateLoggers();
    }

    /**
     * Enables logging for a class at a specific level.
     *
     * @param class_      class to configure
     * @param levelToLog  the logging level to set
     * @param <T>         type of the class
     */
    public static <T> void enableLogsForClass(Class<T> class_, Level levelToLog) {
        enableLogsForClassInternal(class_.getName(), levelToLog);
    }

    /**
     * Enables logging for a class specified by name at a specific level, if the class exists.
     *
     * @param className   fully-qualified class name
     * @param levelToLog  the logging level to set
     */
    public static void enableLogsForClass(String className, Level levelToLog) {
        if (verifyStringIsActualClass(className)) {
            enableLogsForClassInternal(className, levelToLog);
        }
    }

    /**
     * Enables logging for a class specified by name at a specific level using a string.
     *
     * @param className   fully-qualified class name
     * @param levelToLog  name of the logging level (e.g., "DEBUG", "INFO")
     */
    public static void enableLogsForClass(String className, String levelToLog) {
        Level level = Level.valueOf(levelToLog.trim().toUpperCase());
        if (level != null && verifyStringIsActualClass(className)) {
            enableLogsForClassInternal(className, level);
        }
    }

    /** Convenience methods for enabling logs at specific levels for classes. */
    public static <T> void enableAllLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.ALL);
    }

    public static void enableAllLogsForClass(String className) {
        enableLogsForClass(className, Level.ALL);
    }

    public static <T> void enableDebugLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.DEBUG);
    }

    public static void enableDebugLogsForClass(String className) {
        enableLogsForClass(className, Level.DEBUG);
    }

    public static <T> void enableErrorLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.ERROR);
    }

    public static void enableErrorLogsForClass(String className) {
        enableLogsForClass(className, Level.ERROR);
    }

    public static <T> void enableFatalLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.FATAL);
    }

    public static void enableFatalLogsForClass(String className) {
        enableLogsForClass(className, Level.FATAL);
    }

    public static <T> void enableInfoLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.INFO);
    }

    public static void enableInfoLogsForClass(String className) {
        enableLogsForClass(className, Level.INFO);
    }

    public static <T> void enableTraceLogsForClass(Class<T> class_) {
        enableLogsForClass(class_, Level.TRACE);
    }

    public static void enableTraceLogsForClass(String className) {
        enableLogsForClass(className, Level.TRACE);
    }
}
