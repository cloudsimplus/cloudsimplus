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
package org.cloudsimplus.vms;

import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Resource;

import java.util.function.Function;

/**
 * A base class for computing statistics about {@link Resource} utilization
 * for a given machine ({@link Vm} or {@link Host}). Such a resource can be, for instance, CPU, RAM or BW.
 *
 * @param <T> The kind of machine to collect resource utilization statistics, such as {@link Vm} or {@link Host}
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class ResourceStats<T extends Machine> {
    private final Function<T, Double> resourceUtilizationFunction;
    private final T machine;
    private final SummaryStatistics stats;
    private double previousTime;
    private double previousUtilization;

    /**
     * Creates a ResourceStats to collect resource utilization statistics.
     * @param machine the machine where the statistics will be collected from (which can be a Vm or Host)
     * @param resourceUtilizationFunction a {@link Function} that receives a Machine
     *                                    and returns the current resource utilization for that machine
     */
    protected ResourceStats(@NonNull final T machine, @NonNull final Function<T, Double> resourceUtilizationFunction){
        this.resourceUtilizationFunction = resourceUtilizationFunction;
        this.machine = machine;
        this.stats = new SummaryStatistics();
    }

    /**
     * Collects the current resource utilization percentage (in scale from 0 to 1)
     * for the given time to the statistics.
     * @param time current simulation time (in seconds)
     * @return true if data was collected, false otherwise (meaning it's not time to collect data).
     */
    public boolean add(final double time) {
        try {
            if (isNotTimeToAddHistory(time)) {
                return false;
            }

            final double utilization = resourceUtilizationFunction.apply(machine);

            /* If (i) the previous utilization is not zero, and the current utilization is zero
             * and (ii) those values don't change, it means the machine has finished
             * and this utilization must not be collected.
             * If that happens, it may reduce the accuracy of the utilization mean.
             * For instance, if a machine uses 100% of a resource all the time,
             * when it finishes, the utilization will be zero.
             * If that utilization is collected, the average won't be 100% anymore. */
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
     * @return the minimum resource utilization percentage (from 0 to 1).
     */
    public double getMin(){
        return stats.getMin();
    }

    /**
     * @return the maximum resource utilization percentage (from 0 to 1).
     */
    public double getMax(){
        return stats.getMax();
    }

    /**
     * @return the average resource utilization percentage (from 0 to 1).
     */
    public double getMean(){
        return stats.getMean();
    }

    /**
     * @return the Standard Deviation of resource utilization percentage (from 0 to 1).
     */
    public double getStandardDeviation(){
        return stats.getStandardDeviation();
    }

    /**
     * @return the (sample) variance of resource utilization percentage (from 0 to 1).
     */
    public double getVariance(){
        return stats.getVariance();
    }

    /**
     * @return the number of collected resource utilization samples.
     */
    public double count(){
        return stats.getN();
    }

    /**
     * @return true if no resource utilization sample was collected, false otherwise.
     */
    public boolean isEmpty(){ return count() == 0; }

    /// {@return true if it's time to add utilization history, false otherwise}
    /// The utilization history is not updated if any one of the following conditions is met:
    ///
    /// - The simulation clock was not changed yet.
    /// - The time passed is smaller than one second, and the machine is not idle.
    /// - The floor time is equal to the previous time, and the machine is not idle.
    ///
    /// If the time is smaller than one second and the machine became idle,
    /// the history will be added so that we know what the resource
    /// utilization was when the VM became idle.
    /// This way, we can see clearly in the history when the machine was busy
    /// and when it became idle.
    ///
    /// If the floor time is equal to the previous time and the machine is not idle,
    /// that means not even a second has passed. Therefore, that utilization value will not be stored.
    ///
    /// @param time the current simulation time
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
     * @return the previous time that resource statistics were computed.
     */
    protected double getPreviousTime() {
        return previousTime;
    }
}
