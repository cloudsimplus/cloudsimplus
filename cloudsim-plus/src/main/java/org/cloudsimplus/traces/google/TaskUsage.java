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

/**
 * A data class to store the attributes representing the resource usage of a {@link Cloudlet},
 * according to the data read from a line inside a "task usage" trace file.
 * Instance of this class are created by the {@link GoogleTaskUsageTraceReader}
 * and provided to the user's simulation.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class TaskUsage extends TaskData {
    private double startTime;
    private double endTime;
    private double meanCpuUsageRate;
    private double canonicalMemoryUsage;
    private double assignedMemoryUsage;
    private double maximumMemoryUsage;
    private double meanDiskIoTime;
    private double meanLocalDiskSpaceUsed;
    private double maximumCpuUsage;
    private double maximumDiskIoTime;

    /**
     * Gets the start time​ of the measurement period (converted to seconds).
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#START_TIME
     */
    public double getStartTime() {
        return startTime;
    }

    protected TaskUsage setStartTime(final double startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Gets the end time​ of the measurement period (converted to seconds).
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#END_TIME
     */
    public double getEndTime() {
        return endTime;
    }

    /* default */ TaskUsage setEndTime(final double endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * Gets the mean CPU usage rate (in percentage from 0 to 1).
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MEAN_CPU_USAGE_RATE
     */
    public double getMeanCpuUsageRate() {
        return meanCpuUsageRate;
    }

    /* default */ TaskUsage setMeanCpuUsageRate(final double meanCpuUsageRate) {
        this.meanCpuUsageRate = meanCpuUsageRate;
        return this;
    }

    /**
     * Gets the canonical memory usage,
     * i.e., the number of user accessible pages,
     * including page cache but excluding some pages marked as stale.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#CANONICAL_MEMORY_USAGE
     */
    public double getCanonicalMemoryUsage() {
        return canonicalMemoryUsage;
    }

    /* default */ TaskUsage setCanonicalMemoryUsage(final double canonicalMemoryUsage) {
        this.canonicalMemoryUsage = canonicalMemoryUsage;
        return this;
    }

    /**
     * Gets the assigned memory usage,
     * i.e., memory usage based on the memory actually assigned (but not necessarily used)
     * to the container where the task was running inside the
     * Google Cluster.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#ASSIGNED_MEMORY_USAGE
     */
    public double getAssignedMemoryUsage() {
        return assignedMemoryUsage;
    }

    /* default */ TaskUsage setAssignedMemoryUsage(final double assignedMemoryUsage) {
        this.assignedMemoryUsage = assignedMemoryUsage;
        return this;
    }

    /**
     * Gets the maximum memory usage,
     * i.e., the maximum value of the canonical memory usage
     * measurement observed over the measurement interval.
     * This value is not available for some tasks.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MAXIMUM_MEMORY_USAGE
     */
    public double getMaximumMemoryUsage() {
        return maximumMemoryUsage;
    }

    /* default */ TaskUsage setMaximumMemoryUsage(final double maximumMemoryUsage) {
        this.maximumMemoryUsage = maximumMemoryUsage;
        return this;
    }

    /**
     * Gets the mean disk I/O time.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MEAN_DISK_IO_TIME
     */
    public double getMeanDiskIoTime() {
        return meanDiskIoTime;
    }

    /* default */ TaskUsage setMeanDiskIoTime(final double meanDiskIoTime) {
        this.meanDiskIoTime = meanDiskIoTime;
        return this;
    }

    /**
     * Gets the mean local disk space used.
     * Represents runtime local disk capacity usage.
     * Disk usage required for binaries and other read-only, pre-staged runtime files is ​not​included.
     * Additionally, most disk space used by distributed, persistent storage (e.g. GFS, Colossus)
     * is not accounted for in this trace.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MEAN_LOCAL_DISK_SPACE_USED
     */
    public double getMeanLocalDiskSpaceUsed() {
        return meanLocalDiskSpaceUsed;
    }

    /* default */ TaskUsage setMeanLocalDiskSpaceUsed(final double meanLocalDiskSpaceUsed) {
        this.meanLocalDiskSpaceUsed = meanLocalDiskSpaceUsed;
        return this;
    }

    /**
     * Gets the maximum CPU usage observed over the measurement interval.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MAXIMUM_CPU_USAGE
     */
    public double getMaximumCpuUsage() {
        return maximumCpuUsage;
    }

    /* default */ TaskUsage setMaximumCpuUsage(final double maximumCpuUsage) {
        this.maximumCpuUsage = maximumCpuUsage;
        return this;
    }

    /**
     * Gets the maximum disk IO time observed over the measurement interval.
     * @return
     * @see GoogleTaskUsageTraceReader.FieldIndex#MAXIMUM_DISK_IO_TIME
     */
    public double getMaximumDiskIoTime() {
        return maximumDiskIoTime;
    }

    /* default */ TaskUsage setMaximumDiskIoTime(final double maximumDiskIoTime) {
        this.maximumDiskIoTime = maximumDiskIoTime;
        return this;
    }
}
