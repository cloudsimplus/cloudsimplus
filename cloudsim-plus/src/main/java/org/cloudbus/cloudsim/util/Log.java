/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.util;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * Performs logging of the simulation process. It provides the
 * ability to substitute the output stream by any OutputStream subclass.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public final class Log {
    /**
     * Checks if application is running in debug mode.
     * "jdwp" is the acronym for "Java Debug Wire Protocol" that
     * may exists as an application parameter to define
     * the application is running in debug mode.
     *
     * @see #isDebug()
     */
    private static final boolean debug =
            ManagementFactory.getRuntimeMXBean().getInputArguments()
                    .toString().indexOf("jdwp") > 0;

    /**
     * A private constructor to avoid class instantiation.
     */
    private Log(){}

    /**
     * Checks if the simulation is running in the Java debugger.
     * @return
     */
    public static boolean isDebug() {
        return debug;
    }

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
