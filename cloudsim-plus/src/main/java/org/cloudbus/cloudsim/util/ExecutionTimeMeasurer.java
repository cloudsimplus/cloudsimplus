/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Measurement of execution times of CloudSim's methods.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public final class ExecutionTimeMeasurer {

    /**
     * A map of execution start times where each key
     * represents the name of the method/process and each key is the
     * time the method/process started (in milliseconds).
     * Usually, this name is the method/process name, making
     * it easy to identify the execution start times into the map.
     */
    private static final Map<String, Long> executionStartTimes = new HashMap<>();

    /**
     * A private constructor to avoid class instantiation.
     */
    private ExecutionTimeMeasurer(){}

    /**
     * Starts measuring the execution time of a method/process.
     * Usually this method has to be called at the first line of the method
     * that has to be its execution time measured.
     *
     * @param name the name of the method/process being measured.
     * @see #getExecutionStartTimes()
     */
    public static void start(String name) {
        getExecutionStartTimes().put(name, System.currentTimeMillis());
    }

    /**
     * Finalizes measuring the execution time of a method/process.
     *
     * @param name the name of the method/process being measured.
     * @return the time the method/process spent in execution (in seconds)
     * @see #getExecutionStartTimes()
     */
    public static double end(String name) {
        final double executionTime = (System.currentTimeMillis() - getExecutionStartTimes().get(name)) / 1000.0;
        getExecutionStartTimes().remove(name);
        return executionTime;
    }

    /**
     * Gets map the execution times.
     *
     * @return the execution times map
     * @see #executionStartTimes
     */
    public static Map<String, Long> getExecutionStartTimes() {
        return executionStartTimes;
    }

}
