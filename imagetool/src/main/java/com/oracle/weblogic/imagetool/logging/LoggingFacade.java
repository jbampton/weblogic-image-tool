// Copyright (c) 2019, 2021, Oracle and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package com.oracle.weblogic.imagetool.logging;

import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.weblogic.imagetool.util.XPathUtil;
import org.w3c.dom.Document;

/** Centralized logging for the operator. */
public class LoggingFacade {

    private static final String CLASS = LoggingFacade.class.getName();
    private final Logger logger;

    /**
     * Create an Image Tool facade for the java.util.logging.Logger class.
     * This facade is used to override the logging methods in order to infer caller details.
     * @param logger the named logger to wrap.
     */
    public LoggingFacade(Logger logger) {
        this.logger = logger;

        final Logger parentLogger = Logger.getAnonymousLogger().getParent();
        final Handler[] handlers = parentLogger.getHandlers();
        for (final Handler handler : handlers) {
            if (handler instanceof FileHandler) {
                handler.setFormatter(new FileFormatter());
            }
            if (handler instanceof ConsoleHandler) {
                handler.setFormatter(new ConsoleFormatter());
            }
        }
    }

    /**
     * Logs a message at the CONFIG level.
     *
     * @param msg message to log
     */
    public void config(String msg) {
        if (isConfigEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.CONFIG, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the CONFIG level.
     *
     * @param msg message to log
     * @param params vararg list of parameters to use when logging the message
     */
    public void config(String msg, Object... params) {
        if (isConfigEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.CONFIG, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the CONFIG level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void config(String msg, Throwable thrown) {
        if (isConfigEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.CONFIG, details.clazz, details.method, msg, thrown);
        }
    }

    /** Logs a method entry. The calling class and method names will be inferred. */
    public void entering() {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.entering(details.clazz, details.method);
        }
    }

    /**
     * Logs a method entry, with a list of arguments of interest. The calling class and method names
     * will be inferred. Warning: Depending on the nature of the arguments, it may be required to cast
     * those of type String to Object, to ensure that this variant is called as expected, instead of
     * one of those referenced below.
     *
     * @param params varargs list of objects to include in the log message
     */
    public void entering(Object... params) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.entering(details.clazz, details.method, params);
        }
    }

    /** Logs a method exit. The calling class and method names will be inferred. */
    public void exiting() {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.exiting(details.clazz, details.method);
        }
    }

    /**
     * Logs a method exit, with a result object. The calling class and method names will be inferred.
     *
     * @param result object to log which is the result of the method call
     */
    public void exiting(Object result) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.exiting(details.clazz, details.method, result);
        }
    }

    /**
     * Logs a message at the FINE level.
     *
     * @param msg the message to log
     */
    public void fine(String msg) {
        if (isFineEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINE, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the FINE level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void fine(String msg, Object... params) {
        if (isFineEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINE, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the FINE level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void fine(String msg, Throwable thrown) {
        if (isFineEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINE, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Logs a message at the FINER level.
     *
     * @param msg the message to log
     */
    public void finer(String msg) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINER, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the FINER level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void finer(String msg, Object... params) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINER, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the FINER level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void finer(String msg, Throwable thrown) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINER, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Logs a message at the FINER level.
     *
     * @param map the message to log
     */
    public void finer(Map<Object,Object> map) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            StringBuilder builder = new StringBuilder();
            map.forEach((k, v) -> builder.append(k).append("=").append(v).append(";"));
            logger.logp(Level.FINER, details.clazz, details.method, builder.toString());
        }
    }

    /**
     * Logs a message at the FINEST level.
     *
     * @param msg the message to log
     */
    public void finest(Document msg) {
        if (isFinestEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINEST, details.clazz, details.method, XPathUtil.prettyPrint(msg));
        }
    }

    /**
     * Logs a message at the FINEST level.
     *
     * @param msg the message to log
     */
    public void finest(String msg) {
        if (isFinestEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINEST, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the FINEST level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void finest(String msg, Object... params) {
        if (isFinestEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINEST, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the FINEST level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void finest(String msg, Throwable thrown) {
        if (isFinestEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.FINEST, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Returns the level at which the underlying logger operates.
     *
     * @return a Level object at which logger is operating
     */
    public Level getLevel() {
        return logger.getLevel();
    }

    /**
     * Sets the level at which the underlying Logger operates. This should not be called in the
     * general case; levels should be set via OOB configuration (a configuration file exposed by the
     * logging implementation, management API, etc).
     *
     * @param newLevel Level to set
     */
    public void setLevel(Level newLevel) {
        logger.setLevel(newLevel);
    }

    /**
     * Returns the name of the underlying logger.
     *
     * @return a String with the name of the logger
     */
    public String getName() {
        return logger.getName();
    }

    /**
     * Returns the underlying logger. This should only be used when component code calls others' code,
     * and that code requires that we provide it with a Logger.
     *
     * @return the underlying Logger object
     */
    public Logger getUnderlyingLogger() {
        return logger;
    }

    /**
     * Logs a message at the INFO level.
     *
     * @param msg the message to log
     */
    public void info(String msg) {
        if (isInfoEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.INFO, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the INFO level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void info(String msg, Object... params) {
        if (isInfoEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.INFO, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the INFO level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void info(String msg, Throwable thrown) {
        if (isInfoEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.INFO, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Checks if a message at CONFIG level would actually be logged.
     *
     * @return <code>true</code> if logging at the CONFIG level
     */
    public boolean isConfigEnabled() {
        return logger.isLoggable(Level.CONFIG);
    }

    /**
     * Checks if a message at FINE level would actually be logged.
     *
     * @return <code>true</code> if logging at the FINE level
     */
    public boolean isFineEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    /**
     * Checks if a message at FINER level would actually be logged.
     *
     * @return <code>true</code> if logging at the FINER level
     */
    public boolean isFinerEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    /**
     * Checks if a message at FINEST level would actually be logged.
     *
     * @return <code>true</code> if logging at the FINEST level
     */
    public boolean isFinestEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    /**
     * Checks if a message at INFO level would actually be logged.
     *
     * @return <code>true</code> if logging at the INFO level
     */
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    /**
     * Checks if a message at the provided level would actually be logged.
     *
     * @param level a Level object to check against
     * @return <code>true</code> if logging at the level specified
     */
    public boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }

    /**
     * Checks if a message at SEVERE level would actually be logged.
     *
     * @return <code>true</code> if logging at the SEVERE level
     */
    public boolean isSevereEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    /**
     * Checks if a message at WARNING level would actually be logged.
     *
     * @return <code>true</code> if logging at the WARNING level
     */
    public boolean isWarningEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    /**
     * Logs a message at the requested level. Normally, one of the level-specific methods should be
     * used instead.
     *
     * @param level Level at which log log the message
     * @param msg the message to log
     */
    public void log(Level level, String msg) {
        if (isLoggable(level)) {
            CallerDetails details = inferCaller();
            logger.logp(level, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters. This replaces the Logger equivalents taking a single
     * param or an Object array, and is backward-compatible with them. Calling the per-Level methods
     * is preferred, but this is present for completeness.
     *
     * @param level Level at which log log the message
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     * @see Logger#log(java.util.logging.Level, String, Object[])
     */
    public void log(Level level, String msg, Object... params) {
        if (isLoggable(level)) {
            CallerDetails details = inferCaller();
            logger.logp(level, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable. Calling equivalent per-Level method is preferred,
     * but this is present for completeness.
     *
     * @param level Level at which log log the message
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void log(Level level, String msg, Throwable thrown) {
        if (isLoggable(level)) {
            CallerDetails details = inferCaller();
            logger.logp(level, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Logs a message at the SEVERE level.
     *
     * @param msg the message to log
     */
    public void severe(String msg) {
        if (isSevereEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.SEVERE, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the SEVERE level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void severe(String msg, Object... params) {
        if (isSevereEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.SEVERE, details.clazz, details.method, msg, params);
        }
    }

    /**
     * Logs a message which accompanies a Throwable at the SEVERE level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void severe(String msg, Throwable thrown) {
        if (isSevereEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.SEVERE, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Logs that an exception will be thrown. The calling class and method names will be inferred.
     *
     * @param pending an Exception to include in the logged message
     */
    public <T extends Throwable> T throwing(T pending) {
        if (isFinerEnabled()) {
            CallerDetails details = inferCaller();
            logger.throwing(details.clazz, details.method, pending);
        }
        return pending;
    }

    /**
     * Logs a message at the WARNING level.
     *
     * @param msg the message to log
     */
    public void warning(String msg) {
        if (isWarningEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.WARNING, details.clazz, details.method, msg);
        }
    }

    /**
     * Logs a message which requires parameters at the WARNING level.
     *
     * @param msg the message to log
     * @param params varargs list of objects to include in the log message
     */
    public void warning(String msg, Object... params) {
        if (isWarningEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.WARNING, details.clazz, details.method, msg, params);
        }
    }


    /**
     * Logs a message which accompanies a Throwable at the WARNING level.
     *
     * @param msg the message to log
     * @param thrown an Exception to include in the logged message
     */
    public void warning(String msg, Throwable thrown) {
        if (isWarningEnabled()) {
            CallerDetails details = inferCaller();
            logger.logp(Level.WARNING, details.clazz, details.method, msg, thrown);
        }
    }

    /**
     * Obtains caller details, class name and method, to be provided to the actual Logger. This code
     * is adapted from ODLLogRecord, which should yield consistency in reporting using PlatformLogger
     * versus a raw (ODL) Logger. JDK Logger does something similar but utilizes native methods
     * directly.
     */
    CallerDetails inferCaller() {
        CallerDetails details = new CallerDetails();
        Throwable t = new Throwable();
        StackTraceElement[] stack = t.getStackTrace();

        // Walk the stack until we hit a frame outside this class
        int i = 0;
        while (i < stack.length) {
            StackTraceElement frame = stack[i];
            String cname = frame.getClassName();
            if (!cname.equals(CLASS)) {
                details.clazz = cname;
                details.method = frame.getMethodName();
                break;
            }
            i++;
        }

        return details;
    }

    /** Holds caller details obtained by inference. */
    class CallerDetails {
        String clazz;
        String method;
    }
}
