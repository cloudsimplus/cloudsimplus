/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.util;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to enable changing logging
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
     * by using, for example, {@link Level#WARN} value.
     * To completely disable the given logger, use {@link Level#OFF}.
     * @param level the logging level to set
     */
    public static void setLevel(final Logger logger, final Level level) {
        if (logger instanceof ch.qos.logback.classic.Logger logback)
            logback.setLevel(level);
        else {
            final var msg = "The logger must be and instance of " + ch.qos.logback.classic.Logger.class.getName();
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Sets the logging {@link Level} for <b>all logger instances</b>.
     * You can enable just a specific type of log messages
     * by using, for example, {@link Level#WARN} value.
     * To completely disable logging, use {@link Level#OFF}.
     * @param level the logging level to set
     */
    public static void setLevel(final Level level){
        final var root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        setLevel(root, level);
    }
}
