package org.toop;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.Logging;

public class LoggingTest {

    private LoggerContext ctx;

    @BeforeEach
    void setUp() {
        ctx = (LoggerContext) LogManager.getContext(false);
        Logging.enableAllLogs(Level.DEBUG); // reset root logger before each test
    }

    @AfterEach
    void tearDown() {
        Logging.enableAllLogs(Level.DEBUG); // restore root logger after each test
    }

    @Test
    void testDisableAllLogs_setsRootLevelOff() {
        Logging.disableAllLogs();

        LoggerConfig rootConfig = ctx.getConfiguration().getRootLogger();
        assertEquals(Level.OFF, rootConfig.getLevel());
    }

    @Test
    void testEnableAllLogs_setsRootLevelAll() {
        Logging.enableAllLogs();

        LoggerConfig rootConfig = ctx.getConfiguration().getRootLogger();
        assertEquals(Level.ALL, rootConfig.getLevel());
    }

    @Test
    void testEnableAllLogs_LevelParam_setsRootLevel() {
        Logging.enableAllLogs(Level.WARN);

        LoggerConfig rootConfig = ctx.getConfiguration().getRootLogger();
        assertEquals(Level.WARN, rootConfig.getLevel());
    }

    @Test
    void testDisableLogsForClass_addsLoggerWithOff() {
        Logging.disableLogsForClass(LoggingTest.class);

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get(LoggingTest.class.getName());
        assertNotNull(loggerConfig);
        assertEquals(Level.OFF, loggerConfig.getLevel());
    }

    @Test
    void testEnableLogsForClass_addsLoggerWithLevel() {
        Logging.enableLogsForClass(LoggingTest.class, Level.ERROR);

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get(LoggingTest.class.getName());
        assertNotNull(loggerConfig);
        assertEquals(Level.ERROR, loggerConfig.getLevel());
    }

    @Test
    void testEnableLogsForClass_withStringLevel() {
        Logging.enableLogsForClass(LoggingTest.class.getName(), "INFO");

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get(LoggingTest.class.getName());
        assertNotNull(loggerConfig);
        assertEquals(Level.INFO, loggerConfig.getLevel());
    }

    @Test
    void testEnableDebugLogsForClass_setsDebug() {
        Logging.enableDebugLogsForClass(LoggingTest.class);

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get(LoggingTest.class.getName());
        assertNotNull(loggerConfig);
        assertEquals(Level.DEBUG, loggerConfig.getLevel());
    }

    @Test
    void testEnableInfoLogsForClass_setsInfo() {
        Logging.enableInfoLogsForClass(LoggingTest.class);

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get(LoggingTest.class.getName());
        assertNotNull(loggerConfig);
        assertEquals(Level.INFO, loggerConfig.getLevel());
    }

    @Test
    void testDisableLogsForNonexistentClass_doesNothing() {
        Logging.disableLogsForClass("org.toop.DoesNotExist");

        LoggerConfig loggerConfig =
                ctx.getConfiguration().getLoggers().get("org.toop.DoesNotExist");
        assertNull(loggerConfig); // class doesn't exist, so no logger added
    }
}
