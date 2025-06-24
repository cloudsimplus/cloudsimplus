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

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.traces.google.GoogleTaskUsageTraceReader.FieldIndex;

/**
 * A data class to store the attributes representing the resource usage of a {@link Cloudlet},
 * according to the data read from a line inside a "task usage" trace file.
 * Instances of this class are created by the {@link GoogleTaskUsageTraceReader}
 * and provided to the user's simulation.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
@Getter
public final class TaskUsage extends TaskData {
    /**
     * The start time of the measurement period (converted to seconds).
     * @see FieldIndex#START_TIME
     */
    private double startTime;

    /**
     * The end time of the measurement period (converted to seconds).
     * @see FieldIndex#END_TIME
     */
    private double endTime;

    /**
     * The mean CPU usage rate (in percentage from 0 to 1).
     * @see FieldIndex#MEAN_CPU_USAGE_RATE
     */
    private double meanCpuUsageRate;

    /**
     * The canonical memory usage,
     * i.e., the number of user-accessible pages,
     * including page cache but excluding some pages marked as stale.
     * @see FieldIndex#CANONICAL_MEMORY_USAGE
     */
    private double canonicalMemoryUsage;

    /**
     * The assigned memory usage,
     * i.e., memory usage based on the memory actually assigned (but not necessarily used)
     * to the container where the task was running inside the
     * Google Cluster.
     * @see FieldIndex#ASSIGNED_MEMORY_USAGE
     */
    private double assignedMemoryUsage;

    /**
     * The maximum memory usage,
     * i.e., the maximum value of the canonical memory usage
     * measurement observed over the measurement interval.
     * This value is not available for some tasks.
     * @see FieldIndex#MAXIMUM_MEMORY_USAGE
     */
    private double maximumMemoryUsage;

    /**
     * The mean disk I/O time.
     * @see FieldIndex#MEAN_DISK_IO_TIME
     */
    private double meanDiskIoTime;

    /**
     * The mean local disk space used.
     * Represents runtime local disk capacity usage.
     * Disk usage required for binaries and other read-only pre-staged runtime files is not included.
     * Additionally, most disk space used by distributed, persistent storage (e.g. GFS, Colossus)
     * is not accounted for in this trace.
     * @see FieldIndex#MEAN_LOCAL_DISK_SPACE_USED
     */
    private double meanLocalDiskSpaceUsed;

    /**
     * The maximum CPU usage observed over the measurement interval.
     * @see FieldIndex#MAXIMUM_CPU_USAGE
     */
    private double maximumCpuUsage;

    /**
     * The maximum disk IO time observed over the measurement interval.
     * @see FieldIndex#MAXIMUM_DISK_IO_TIME
     */
    private double maximumDiskIoTime;

    public TaskUsage(@NonNull final GoogleTaskUsageTraceReader reader) {
        this.startTime = FieldIndex.START_TIME.getValue(reader);
        this.endTime = FieldIndex.END_TIME.getValue(reader);
        this.meanCpuUsageRate = FieldIndex.MEAN_CPU_USAGE_RATE.getValue(reader);
        this.canonicalMemoryUsage = FieldIndex.CANONICAL_MEMORY_USAGE.getValue(reader);
        this.assignedMemoryUsage = FieldIndex.ASSIGNED_MEMORY_USAGE.getValue(reader);
        this.maximumMemoryUsage = FieldIndex.MAXIMUM_MEMORY_USAGE.getValue(reader);
        this.meanDiskIoTime = FieldIndex.MEAN_DISK_IO_TIME.getValue(reader);
        this.meanLocalDiskSpaceUsed = FieldIndex.MEAN_LOCAL_DISK_SPACE_USED.getValue(reader);
        this.maximumCpuUsage = FieldIndex.MAXIMUM_CPU_USAGE.getValue(reader);
        this.maximumDiskIoTime = FieldIndex.MAXIMUM_DISK_IO_TIME.getValue(reader);
        setJobId(FieldIndex.JOB_ID.getValue(reader));
        setTaskIndex(FieldIndex.TASK_INDEX.getValue(reader));
        setMachineId(FieldIndex.MACHINE_ID.getValue(reader));
    }
}
