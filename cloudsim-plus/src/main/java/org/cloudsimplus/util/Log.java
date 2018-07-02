package org.cloudsimplus.util;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An utility class to enable changing logging
 * configuration such as the logging level.
 *
 * @since CloudSim Plus 3.0.0
 * @author Manoel Campos da Silva Filho
 */
public final class Log {

    /**
     * A private constructor to avoid class instantiation.
     */
    private Log(){}

    /**
     * Sets the logging {@link Level} for a given logger instance.
     * You can enable just a specific type of log messages
     * by using, for instance, {@link Level#WARN} value.
     * To completely disable the given logger, use {@link Level#OFF}.
     * @param level the logging level to set
     */
    public static void setLevel(final Logger logger, final Level level) {
        ((ch.qos.logback.classic.Logger) logger).setLevel(level);
    }

    /**
     * Sets the logging {@link Level} for <b>all logger instances</b>.
     * You can enable just a specific type of log messages
     * by using, for instance, {@link Level#WARN} value.
     * To completely disable logging, use {@link Level#OFF}.
     * @param level the logging level to set
     */
    public static void setLevel(final Level level){
        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        setLevel(root, level);
    }
}
