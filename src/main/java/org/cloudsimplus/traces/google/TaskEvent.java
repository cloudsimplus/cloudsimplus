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
package org.cloudsimplus.traces.google;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;

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
 * by hard-coding some simulation specific parameters such as the {@link UtilizationModel}
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
@Getter @Setter @Builder
public final class TaskEvent extends TaskData {
    private int priority;

    /**
     * The scheduling class that roughly represents how latency-sensitive the task is.
     * The scheduling class is represented by a single number,
     * with 3 representing a more latency-sensitive task (e.g., serving revenue-generating user requests)
     * and 0 representing a non-production task (e.g., development, non-business-critical analyses, etc.).
     * @see TaskEventField#SCHEDULING_CLASS
     */
    private int schedulingClass;

    /**
     * The event type.
     */
    @Setter(AccessLevel.NONE)
    private TaskEventType type;

    /**
     * The maximum number of CPU cores
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
     *
     * @see TaskEventField#RESOURCE_REQUEST_FOR_CPU_CORES
     * @see GoogleTaskUsageTraceReader
     */
    @Setter
    private double resourceRequestForCpuCores;

    /**
     * The maximum amount of RAM
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
     * Since there are "task usage" trace files, they can be used to
     * define the RAM utilization along the time.
     * In this case, a {@link UtilizationModelDynamic} is required for the Cloudlet's
     * RAM UtilizationModel. Using a different class will raise a runtime exception
     * when trying to create the Cloudlets.
     * </p>
     *
     * @see TaskEventField#RESOURCE_REQUEST_FOR_RAM
     * @see GoogleTaskUsageTraceReader
     */
    @Setter
    private double resourceRequestForRam;

    /**
     * The maximum amount of local disk space
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
     *
     * @see TaskEventField#RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE
     */
    @Setter
    private double resourceRequestForLocalDiskSpace;

    /**
     * The hashed username provided as an opaque base64-encoded string that can be tested for equality.
     * @see TaskEventField#USERNAME
     */
    private String userName;

    /**
     * The time the event happened (converted to seconds).
     * @see TaskEventField#TIMESTAMP
     */
    private double timestamp;

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

    /**
     * Sets the {@link TaskEventType} according to the enum ordinal.
     * @param type the int value of the task event type
     * @return
     */
    public TaskEvent setType(final int type) {
        this.type = TaskEventType.getValue(type);
        return this;
    }

    /**
     * Creates a TaskEvent from the current processed line from a Google Task Events trace file.
     * @param reader
     * @return
     */
    public static TaskEvent of(final GoogleTaskEventsTraceReader reader) {
        final var builder = new Builder();
        /*TODO The tasks with the same username must run inside the same user's VM,
         *     unless the machineID is different.
         *     The task (cloudlet) needs to be mapped to a specific Host (according to the machineID).
         *     The challenge here is because the task requirements are usually not known,
         *     for instance when the task is submitted. It's just know when it starts to execute.
         */
        builder
            .type(TaskEventField.EVENT_TYPE.getValue(reader))
            .timestamp(TaskEventField.TIMESTAMP.getValue(reader))
            .resourceRequestForCpuCores(TaskEventField.RESOURCE_REQUEST_FOR_CPU_CORES.getValue(reader))
            .resourceRequestForLocalDiskSpace(TaskEventField.RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE.getValue(reader))
            .resourceRequestForRam(TaskEventField.RESOURCE_REQUEST_FOR_RAM.getValue(reader))
            .priority(TaskEventField.PRIORITY.getValue(reader))
            .schedulingClass(TaskEventField.SCHEDULING_CLASS.getValue(reader))
            .userName(TaskEventField.USERNAME.getValue(reader));

        final TaskEvent evt = builder.build();
        evt.setJobId(TaskEventField.JOB_ID.getValue(reader));
        evt.setTaskIndex(TaskEventField.TASK_INDEX.getValue(reader));
        return evt;
    }
}
