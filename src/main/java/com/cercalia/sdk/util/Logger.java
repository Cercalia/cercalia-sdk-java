package com.cercalia.sdk.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for Cercalia SDK.
 * Provides debug, info, warn, and error logging levels.
 */
public final class Logger {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static volatile boolean debugEnabled = false;
    private static final Logger INSTANCE = new Logger();
    
    private Logger() {
        // Private constructor for singleton
    }
    
    /**
     * Returns the singleton {@link Logger} instance.
     *
     * @return the {@link Logger} instance.
     */
    public static Logger getInstance() {
        return INSTANCE;
    }
    
    /**
     * Enables or disables debug logging.
     *
     * @param enabled {@code true} to enable debug logging.
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    /**
     * Checks if debug logging is enabled.
     *
     * @return {@code true} if debug logging is enabled.
     */
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    private String formatMessage(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return String.format("%s [%s]: %s", timestamp, level, message);
    }
    
    /**
     * Logs an info message.
     *
     * @param message the message to log.
     */
    public void info(String message) {
        System.out.println(formatMessage("INFO", message));
    }
    
    /**
     * Logs an info message with arguments.
     *
     * @param format the message format.
     * @param args   the arguments.
     */
    public void info(String format, Object... args) {
        System.out.println(formatMessage("INFO", String.format(format, args)));
    }
    
    /**
     * Logs an error message.
     *
     * @param message the message to log.
     */
    public void error(String message) {
        System.err.println(formatMessage("ERROR", message));
    }
    
    /**
     * Logs an error message with arguments.
     *
     * @param format the message format.
     * @param args   the arguments.
     */
    public void error(String format, Object... args) {
        System.err.println(formatMessage("ERROR", String.format(format, args)));
    }
    
    /**
     * Logs an error message with a throwable.
     *
     * @param message   the message to log.
     * @param throwable the throwable to log.
     */
    public void error(String message, Throwable throwable) {
        System.err.println(formatMessage("ERROR", message + ": " + throwable.getMessage()));
        if (debugEnabled) {
            throwable.printStackTrace(System.err);
        }
    }
    
    /**
     * Logs a warning message.
     *
     * @param message the message to log.
     */
    public void warn(String message) {
        System.out.println(formatMessage("WARN", message));
    }
    
    /**
     * Logs a warning message with arguments.
     *
     * @param format the message format.
     * @param args   the arguments.
     */
    public void warn(String format, Object... args) {
        System.out.println(formatMessage("WARN", String.format(format, args)));
    }
    
    /**
     * Logs a debug message (only if debug is enabled).
     *
     * @param message the message to log.
     */
    public void debug(String message) {
        if (debugEnabled) {
            System.out.println(formatMessage("DEBUG", message));
        }
    }
    
    /**
     * Logs a debug message with arguments (only if debug is enabled).
     *
     * @param format the message format.
     * @param args   the arguments.
     */
    public void debug(String format, Object... args) {
        if (debugEnabled) {
            System.out.println(formatMessage("DEBUG", String.format(format, args)));
        }
    }
}
