/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Measures execution times of CloudSim's methods.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public final class ExecutionTimeMeasurer {

    /**
     * A map of execution start times, where each key
     * represents the name of the method/process and each key is the
     * time the method/process started (in milliseconds).
     * Usually, this name is the method/process name, making
     * it easy to identify the execution start times into the map.
     */
    private static final Map<String, Long> EXECUTION_START_TIMES = new HashMap<>();

    /**
     * A private constructor to avoid class instantiation.
     */
    private ExecutionTimeMeasurer(){/**/}

    /**
     * Starts measuring the execution time of a method/process.
     * Usually this method has to be called at the first line of the method
     * that has to be its execution time measured.
     *
     * @param name the name of the method/process being measured.
     * @see #getExecutionStartTimes()
     */
    public static void start(final String name) {
        EXECUTION_START_TIMES.put(name, System.currentTimeMillis());
    }

    /**
     * Finalizes measuring the execution time of a method/process.
     *
     * @param name the name of the method/process being measured.
     * @return the time the method/process spent in execution (in seconds)
     * @see #getExecutionStartTimes()
     */
    public static double end(final String name) {
        return (System.currentTimeMillis() - EXECUTION_START_TIMES.remove(name)) / 1000.0;
    }

    /**
     * @return the map of execution times.
     * @see #EXECUTION_START_TIMES
     */
    static Map<String, Long> getExecutionStartTimes() {
        return EXECUTION_START_TIMES;
    }

    /**
     * {@return the execution start time for the given method/process}
     *
     * @param name the name of the method/process to get the execution start time
     * @see #EXECUTION_START_TIMES
     */
    static Long getExecutionStartTime(final String name){
        return EXECUTION_START_TIMES.get(name);
    }
}
