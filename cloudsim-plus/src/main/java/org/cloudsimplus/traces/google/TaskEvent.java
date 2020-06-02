/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.traces.google;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;

import java.util.function.Function;

/**
 * A data class to store the attributes to create a {@link Cloudlet},
 * according to the data read from a line inside a "task events" trace file.
 * Instance of this class are created by the {@link GoogleTaskEventsTraceReader}
 * and provided to the user's simulation.
 *
 * <p>
 * In order to create such Cloudlets, the {@link GoogleTaskEventsTraceReader} requires
 * the developer to provide a {@link Function}
 * that creates Cloudlets according to the developer needs.
 * </p>
 *
 * <p>The {@link GoogleTaskEventsTraceReader} cannot create the Cloudlets itself
 * by hardcoding some simulation specific parameters such as the {@link UtilizationModel}
 * or cloudlet length. This way, it request a {@link Function} implemented
 * by the developer using the {@link GoogleTaskEventsTraceReader} class
 * that has the custom logic to create Cloudlets.
 * However, this developer's {@link Function} needs to receive
 * the task parameters read from the trace file such as
 * CPU, RAM and disk requirements and priority.
 * To avoid passing so many parameters to the developer's
 * Function, an instance of this class that wraps all these
 * parameters is used instead.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class TaskEvent extends TaskData {
    private int priority;
    private int schedulingClass;
    private double resourceRequestForCpuCores;
    private double resourceRequestForRam;
    private double resourceRequestForLocalDiskSpace;
    private String userName;
    private double timestamp;

    public int getPriority() {
        return priority;
    }

    protected TaskEvent setPriority(final int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Gets the maximum number of CPU cores
     * the task is permitted to use (in percentage from 0 to 1).
     * This percentage value can be used to compute the number of {@link Pe}s
     * the Cloudlet will require, based on the number of PEs of the Vm where the Cloudlet will be executed.
     *
     * <p>The actual value to be assigned to a Cloudlet created from this trace field
     * must be defined by the researcher, inside the
     * {@link GoogleTaskEventsTraceReader#getCloudletCreationFunction() cloudlet creation function}
     * given to the trace reader.
     * </p>
     *
     * <p>
     * Since there are "task usage" trace files, they can used used to
     * define the CPU utilization along the time.
     * The value of this attribute is not the same as the max resource usage
     * of the CPU {@link UtilizationModel}.
     * It just represents the maximum number of CPUs the Cloudlet will use.
     * The percentage that such CPUs will be used for a given time is defined
     * by the CPU {@link UtilizationModel#getUtilization()}.
     * Such a value is defined by a "task usage" trace.
     * </p>
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#RESOURCE_REQUEST_FOR_CPU_CORES
     * @see GoogleTaskUsageTraceReader
     */
    public double getResourceRequestForCpuCores() {
        return resourceRequestForCpuCores;
    }

    /**
     * Computes the actual number of CPU cores (PEs) to be assigned to
     * a Cloudlet, according to the {@link #getResourceRequestForCpuCores() percentage of CPUs to be used}
     * and a given maximum number of existing CPUs.
     * @param maxCpuCores the maximum number of existing CPUs the Cloudlet can use (that can be defined as the number of VM's CPUs)
     * @return the actual number of CPU cores the Cloudlet will require
     */
    public long actualCpuCores(final long maxCpuCores){
        return (long)(resourceRequestForCpuCores*maxCpuCores);
    }

    /* default */ TaskEvent setResourceRequestForCpuCores(final double resourceRequestForCpuCores) {
        this.resourceRequestForCpuCores = resourceRequestForCpuCores;
        return this;
    }

    /**
     * Gets the maximum amount of RAM
     * the task is permitted to use (in percentage from 0 to 1).
     *
     * <p>The actual value to be assigned to a Cloudlet created from this trace field
     * must be defined by the researcher, inside the
     * {@link GoogleTaskEventsTraceReader#getCloudletCreationFunction() cloudlet creation function}
     * given to the trace reader.
     * </p>
     *
     * <p>This field can be used to define the max resource utilization percentage for a
     * UtilizationModel when creating the Cloudlet.
     * Since there are "task usage" trace files, they can used used to
     * define the RAM utilization along the time.
     * In this case, a {@link UtilizationModelDynamic} is required for the Cloudlet's
     * RAM UtilizationModel. Using a different class will raise an runtime exception
     * when trying to create the Cloudlets.
     * </p>
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#RESOURCE_REQUEST_FOR_RAM
     * @see GoogleTaskUsageTraceReader
     */
    public double getResourceRequestForRam() {
        return resourceRequestForRam;
    }

    /* default */ TaskEvent setResourceRequestForRam(final double resourceRequestForRam) {
        this.resourceRequestForRam = resourceRequestForRam;
        return this;
    }

    /**
     * Gets the maximum amount of local disk space
     * the task is permitted to use (in percentage from 0 to 1).
     *
     * <p>The actual value to be assigned to a Cloudlet created from this trace field
     * must be defined by the researcher, inside the
     * {@link GoogleTaskEventsTraceReader#getCloudletCreationFunction() cloudlet creation function}
     * given to the trace reader.
     * </p>
     *
     * <p>This field can be used to define the initial Cloudlet file size and/or output size
     * when creating the Cloudlet, according to the researcher needs.
     * </p>
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE
     */
    public double getResourceRequestForLocalDiskSpace() {
        return resourceRequestForLocalDiskSpace;
    }

    /* default */ TaskEvent setResourceRequestForLocalDiskSpace(final double resourceRequestForLocalDiskSpace) {
        this.resourceRequestForLocalDiskSpace = resourceRequestForLocalDiskSpace;
        return this;
    }

    /**
     * Gets the hashed username provided as an opaque base64-encoded string that can be tested for equality.
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#USERNAME
     */
    public String getUserName() {
        return userName;
    }

    /* default */ TaskEvent setUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Gets the time the event happened (converted to seconds).
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#TIMESTAMP
     */
    public double getTimestamp() {
        return timestamp;
    }

    protected TaskEvent setTimestamp(final double timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Gets the scheduling class ​that roughly represents how latency-sensitive the task is.
     * The scheduling class is represented by a single number,
     * with 3 representing a more latency-sensitive task (e.g., serving revenue-generating user requests)
     * and 0 representing a non-production task (e.g., development, non-business-critical analyses, etc.).
     * @return
     * @see GoogleTaskEventsTraceReader.FieldIndex#SCHEDULING_CLASS
     */
    public int getSchedulingClass() {
        return schedulingClass;
    }

    /* default */ TaskEvent setSchedulingClass(final int schedulingClass) {
        this.schedulingClass = schedulingClass;
        return this;
    }
}
