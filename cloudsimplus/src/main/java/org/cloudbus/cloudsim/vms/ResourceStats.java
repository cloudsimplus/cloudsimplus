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
package org.cloudbus.cloudsim.vms;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.core.AbstractMachine;
import org.cloudbus.cloudsim.resources.Resource;

import java.util.Objects;
import java.util.function.Function;

/**
 * A base class for computing statistics about {@link Resource} utilization
 * for a given machine (VM or Host). Such a resource can be, for instance, CPU, RAM or BW.
 *
 * @param <T> The kind of machine to collect resource utilization statistics
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class ResourceStats<T extends AbstractMachine> {
    private final Function<T, Double> resourceUtilizationFunction;
    private final T machine;
    private final SummaryStatistics stats;
    private double previousTime;
    private double previousUtilization;

    /**
     * Creates a ResourceStats to collect resource utilization statistics.
     * @param machine the machine where the statistics will be collected (which can be a Vm or Host)
     * @param resourceUtilizationFunction a {@link Function} that receives a Machine
     *                                    and returns the current resource utilization for that machine
     */
    protected ResourceStats(final T machine, final Function<T, Double> resourceUtilizationFunction){
        this.resourceUtilizationFunction = Objects.requireNonNull(resourceUtilizationFunction);
        this.machine = Objects.requireNonNull(machine);
        this.stats = new SummaryStatistics();
    }

    /**
     * Collects the current resource utilization percentage (in scale from 0 to 1)
     * for the given time to the statistics.
     * @param time current simulation time
     * @return true if data was collected, false otherwise (meaning it's not time to collect data).
     */
    public boolean add(final double time) {
        try {
            if (isNotTimeToAddHistory(time)) {
                return false;
            }

            final double utilization = resourceUtilizationFunction.apply(machine);
            /*If (i) the previous utilization is not zero and the current utilization is zero
            * and (ii) those values don't change, it means the machine has finished
            * and this utilization must not be collected.
            * If that happens, it may reduce accuracy of the utilization mean.
            * For instance, if a machine uses 100% of a resource all the time,
            * when it finishes, the utilization will be zero.
            * If that utilization is collected, the mean won't be 100% anymore.*/
            if((previousUtilization != 0 && utilization == 0) || (machine.isIdle() && previousUtilization > 0)) {
                this.previousUtilization = utilization;
                return false;
            }

            this.stats.addValue(utilization);
            this.previousUtilization = utilization;
            return true;
        } finally {
            this.previousTime = machine.isIdle() ? time : (int)time;
        }
    }

    /**
     * Gets the minimum resource utilization percentage (from 0 to 1).
     * @return
     */
    public double getMin(){
        return stats.getMin();
    }

    /**
     * Gets the maximum resource utilization percentage (from 0 to 1).
     * @return
     */
    public double getMax(){
        return stats.getMax();
    }

    /**
     * Gets the average resource utilization percentage (from 0 to 1).
     * @return
     */
    public double getMean(){
        return stats.getMean();
    }

    /**
     * Gets the Standard Deviation of resource utilization percentage (from 0 to 1).
     * @return
     */
    public double getStandardDeviation(){
        return stats.getStandardDeviation();
    }

    /**
     * Gets the (sample) variance of resource utilization percentage (from 0 to 1).
     * @return
     */
    public double getVariance(){
        return stats.getVariance();
    }

    /**
     * Gets the number of collected resource utilization samples.
     * @return
     */
    public double count(){
        return stats.getN();
    }

    /**
     * Indicates if no resource utilization sample was collected.
     * @return
     */
    public boolean isEmpty(){ return count() == 0; }

    /**
     * Checks if it isn't time to add a value to the utilization history.
     * The utilization history is not updated in any one of the following conditions is met:
     * <ul>
     * <li>the simulation clock was not changed yet;</li>
     * <li>the time passed is smaller than one second and the VM is not idle;</li>
     * <li>the floor time is equal to the previous time and VM is not idle.</li>
     * </ul>
     *
     * <p>If the time is smaller than one second and the VM became idle,
     * the history will be added so that we know what is the resource
     * utilization when the VM became idle.
     * This way, we can see clearly in the history when the VM was busy
     * and when it became idle.</p>
     *
     * <p>If the floor time is equal to the previous time and VM is not idle,
     * that means not even a second has passed. This way,
     * that utilization will not be stored.</p>
     *
     * @param time the current simulation time
     * @return true if it's time to add utilization history, false otherwise
     */
    protected final boolean isNotTimeToAddHistory(final double time) {
        return time <= 0 ||
               isElapsedTimeSmall(time) ||
               isNotEntireSecondElapsed(time);
    }

    protected final boolean isElapsedTimeSmall(final double time) {
        return time - previousTime < 1 && !machine.isIdle();
    }

    protected final boolean isNotEntireSecondElapsed(final double time) {
        return Math.floor(time) == previousTime && !machine.isIdle();
    }

    protected T getMachine(){
        return machine;
    }

    /**
     * Gets the previous time that resource statistics were computed.
     * @return
     */
    protected double getPreviousTime() {
        return previousTime;
    }
}
