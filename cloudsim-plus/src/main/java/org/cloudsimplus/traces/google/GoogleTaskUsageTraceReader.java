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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.util.TraceReaderAbstract;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.listeners.EventInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Process "task usage" trace files from
 * <a href="https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md">Google Cluster Data</a>
 * to change the resource utilization of {@link Cloudlet}s.
 * The trace files are the ones inside the task_usage sub-directory of downloaded Google traces.
 * The instructions to download the traces are provided in the link above.
 *
 * <p>A spreadsheet that makes it easier to understand the structure of trace files is provided
 * in docs/google-cluster-data-samples.xlsx</p>
 *
 * <p>The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above.
 * It's strongly recommended to read such a documentation before trying to use this class.</p>
 *
 * <p>Check important details at {@link TraceReaderAbstract}.</p>
 *
 * @see #process()
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class GoogleTaskUsageTraceReader extends GoogleTraceReaderAbstract<Cloudlet> {
    private final List<CloudSimEvent> cloudletUsageChangeEvents;
    private final List<DatacenterBroker> brokers;

    /**
     * The index of each field in the trace file.
     */
    public enum FieldIndex implements TraceField<GoogleTaskUsageTraceReader> {
        /**
         * 0: The index of the field containing the start time​ of the measurement period (stored in microsecond
         * but converted to seconds when read from the file).
         */
        START_TIME{
            /**
             * Gets the start time converted to seconds.
             * @param reader the reader for the trace file
             * @return
             */
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return TimeUtil.microToSeconds(reader.getFieldDoubleValue(this));
            }
        },

        /**
         * 1: The index of the field containing the end time​ of the measurement period (stored in microsecond
         * but converted to seconds when read from the file).
         */
        END_TIME{
            /**
             * Gets the end time converted to seconds.
             * @param reader the reader for the trace file
             * @return
             */
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return TimeUtil.microToSeconds(reader.getFieldDoubleValue(this));
            }
        },

        /**
         * 2: The index of the field containing the id of the job this task belongs to.
         */
        JOB_ID{
            @Override
            public Long getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldLongValue(this);
            }
        },

        /**
         * 3: The index of the field containing the task index within the job.
         */
        TASK_INDEX{
            @Override
            public Long getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldLongValue(this);
            }
        },

        /**
         * 4: The index of the field containing the machineID.
         * If the field is present, indicates the machine onto which the task was scheduled,
         * otherwise, the reader will return -1 as default value.
         */
        MACHINE_ID{
            @Override
            public Long getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldLongValue(this, -1);
            }
        },

        /**
         * 5: The index of the field containing the mean CPU usage rate (in percentage from 0 to 1).
         */
        MEAN_CPU_USAGE_RATE{
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 6: The index of the field containing the canonical memory usage,
         * i.e., the number of user accessible pages,
         * including page cache but excluding some pages marked as stale.
         */
        CANONICAL_MEMORY_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 7: The index of the field containing the assigned memory usage,
         * i.e., memory usage based on the memory actually assigned (but not necessarily used)
         * to the container where the task was running inside the
         * Google Cluster.
         */
        ASSIGNED_MEMORY_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 8: The index of the field containing the unmapped page cache memory usage,
         * i.e., Linux page cache (file-backed memory) not mapped into any userspace process.
         */
        UNMAPPED_PAGE_CACHE_MEMORY_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 9: The index of the field containing the total page cache memory usage,
         * i.e., the total Linux page cache (file-backed memory).
         */
        TOTAL_PAGE_CACHE_MEMORY_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 10: The index of the field containing the maximum memory usage,
         * i.e., the maximum value of the canonical memory usage
         * measurement observed over the measurement interval.
         * This value is not available for some tasks.
         */
        MAXIMUM_MEMORY_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, -1);
            }
        },

        /**
         * 11: The index of the field containing the mean disk I/O time.
         */
        MEAN_DISK_IO_TIME {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 12: The index of the field containing the mean local disk space used.
         * Represents runtime local disk capacity usage.
         * Disk usage required for binaries and other read-only, pre-staged runtime files is ​not​included.
         * Additionally, most disk space used by distributed, persistent storage (e.g. GFS, Colossus)
         * is not accounted for in this trace.
         */
        MEAN_LOCAL_DISK_SPACE_USED {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, 0);
            }
        },

        /**
         * 13: The index of the field containing the maximum CPU usage
         * observed over the measurement interval.
         */
        MAXIMUM_CPU_USAGE {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, -1);
            }
        },

        /**
         * 14: The index of the field containing the maximum disk IO time
         * observed over the measurement interval.
         */
        MAXIMUM_DISK_IO_TIME {
            @Override
            public Double getValue(final GoogleTaskUsageTraceReader reader) {
                return reader.getFieldDoubleValue(this, -1);
            }
        }
    }

    private final Simulation simulation;

    /**
     * Gets a {@link GoogleTaskUsageTraceReader} instance to read a "task usage" trace file
     * inside the <b>application's resource directory</b>.
     *
     * @param brokers a list of {@link DatacenterBroker}s that own running Cloudlets for which
     *                resource usage will be read from the trace.
     * @param filePath the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    public static GoogleTaskUsageTraceReader getInstance(
        final List<DatacenterBroker> brokers,
        final String filePath)
    {
        final InputStream reader = ResourceLoader.newInputStream(filePath, GoogleTaskUsageTraceReader.class);
        return new GoogleTaskUsageTraceReader(brokers, filePath, reader);
    }

    /**
     * Instantiates a {@link GoogleTaskUsageTraceReader} to read a "task usage" trace file.
     *
     * @param brokers a list of {@link DatacenterBroker}s that own running Cloudlets for which
     *                resource usage will be read from the trace.
     * @param filePath               the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    public GoogleTaskUsageTraceReader(
        final List<DatacenterBroker> brokers,
        final String filePath) throws IOException
    {
        this(brokers, filePath, Files.newInputStream(Paths.get(filePath)));
    }

    /**
     * Instantiates a {@link GoogleTaskUsageTraceReader} to read a "task usage" from a given InputStream.
     *
     * @param brokers a list of {@link DatacenterBroker}s that own running Cloudlets for which
     *                resource usage will be read from the trace.
     * @param filePath   the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param reader     a {@link InputStream} object to read the file
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    private GoogleTaskUsageTraceReader(
        final List<DatacenterBroker> brokers,
        final String filePath,
        final InputStream reader)
    {
        super(filePath, reader);
        this.brokers = requireNonNull(brokers);
        if(brokers.isEmpty()){
            throw new IllegalArgumentException("The broker list cannot be empty");
        }
        this.simulation = brokers.get(0).getSimulation();
        cloudletUsageChangeEvents = new ArrayList<>();
    }

    /**
     * Process the {@link #getFilePath() trace file} request to change the resource usage of {@link Cloudlet}s
     * as described in the file. It returns the List of all processed {@link Cloudlet}s.
     *
     * <p>If the Cloudlets created by a {@link GoogleTaskEventsTraceReader}
     * use a {@link UtilizationModelFull} to define that the CPUs required
     * by the Cloudlets will be used 100%,
     * when the "task usage" file is read, a different CPU usage can be set.
     * In regular simulations, if this value is smaller,
     * a Cloudlet will spend more time to finish.
     * However, since the "task events" file defines the exact time to finish
     * each Cloudlet, using less than 100% won't make the Cloudlet to finish
     * earlier (as in simulations not using the Google Cluster Data).
     * Each Cloudlet will just have a smaller length at the end of the simulation.</p>
     *
     * <p>These trace files don't define the length of the Cloudlet (task).
     * This way, the Cloudlets are created with an indefinite length
     * (see {@link Cloudlet#setLength(long)}) and the length is increased
     * as the Cloudlet is executed. Therefore, if the Cloudlet is using
     * a higher percentage of the CPU capacity, it will execute
     * more instructions in a given time interval.
     * If it's using a lower percentage of the CPU capacity, it will execute
     * less instructions in that interval.</p>
     *
     * <p>In conclusion, the exec and finish time of Cloudlets created
     * from Google Cluster trace files won't change according
     * to the percentage of CPU the Cloudlets are using.</p>
     *
     * @return the Set of all {@link Cloudlet}s processed according to a line in the trace file
     */
    @Override
    public Set<Cloudlet> process() {
        return super.process();
    }

    /** There is not pre-process for this implementation. */
    @Override
    protected void preProcess(){/**/}

    @Override
    protected void postProcess(){
        simulation.addOnSimulationStartListener(this::onSimulationStart);
    }

    /**
     * Adds an event listener that is notified when the simulation starts,
     * so that the messages to change Cloudlet resource usage are sent.
     *
     * @param info the simulation start event information
     */
    private void onSimulationStart(final EventInfo info) {
        cloudletUsageChangeEvents.forEach(evt -> evt.getSource().schedule(evt));
    }

    @Override
    protected boolean processParsedLineInternal() {
        final TaskUsage taskUsage = createTaskUsageFromTraceLine();
        return brokers.stream()
               .flatMap(broker -> broker.getCloudletSubmittedList().stream())
               .filter(cloudlet -> cloudlet.getId() == taskUsage.getUniqueTaskId())
               .findFirst()
               .map(cloudlet -> requestCloudletUsageChange(cloudlet, taskUsage)).isPresent();
    }

    private TaskUsage createTaskUsageFromTraceLine() {
        final TaskUsage usage = new TaskUsage();
        usage
            .setStartTime(FieldIndex.START_TIME.getValue(this))
            .setEndTime(FieldIndex.END_TIME.getValue(this))
            .setMeanCpuUsageRate(FieldIndex.MEAN_CPU_USAGE_RATE.getValue(this))
            .setCanonicalMemoryUsage(FieldIndex.CANONICAL_MEMORY_USAGE.getValue(this))
            .setAssignedMemoryUsage(FieldIndex.ASSIGNED_MEMORY_USAGE.getValue(this))
            .setMaximumMemoryUsage(FieldIndex.MAXIMUM_MEMORY_USAGE.getValue(this))
            .setMeanDiskIoTime(FieldIndex.MEAN_DISK_IO_TIME.getValue(this))
            .setMeanLocalDiskSpaceUsed(FieldIndex.MEAN_LOCAL_DISK_SPACE_USED.getValue(this))
            .setMaximumCpuUsage(FieldIndex.MAXIMUM_CPU_USAGE.getValue(this))
            .setMaximumDiskIoTime(FieldIndex.MAXIMUM_DISK_IO_TIME.getValue(this))
            .setJobId(FieldIndex.JOB_ID.getValue(this))
            .setTaskIndex(FieldIndex.TASK_INDEX.getValue(this))
            .setMachineId(FieldIndex.MACHINE_ID.getValue(this));
        return usage;
    }

    /**
     * Send a message to the broker to request change in a Cloudlet resource usage.
     * @return true if the request was created, false otherwise
     */
    private boolean requestCloudletUsageChange(final Cloudlet cloudlet, final TaskUsage taskUsage)
    {
        final Runnable resourceUsageUpdateRunnable = () -> {
            final StringBuilder builder = new StringBuilder();
            if (cloudlet.getUtilizationOfCpu() != taskUsage.getMeanCpuUsageRate()) {
                builder.append("CPU Utilization: ")
                    .append(formatPercentValue(cloudlet.getUtilizationOfCpu())).append(VAL_SEPARATOR)
                    .append(formatPercentValue(taskUsage.getMeanCpuUsageRate())).append('%').append(COL_SEPARATOR);

                cloudlet.setUtilizationModelCpu(createUtilizationModel(cloudlet.getUtilizationModelCpu(), taskUsage.getMeanCpuUsageRate()));
            }

            if (cloudlet.getUtilizationOfRam() != taskUsage.getCanonicalMemoryUsage()) {
                builder.append("RAM Utilization: ")
                    .append(formatPercentValue(cloudlet.getUtilizationOfRam())).append(VAL_SEPARATOR)
                    .append(formatPercentValue(taskUsage.getCanonicalMemoryUsage()))
                    .append('%')
                    .append(COL_SEPARATOR);
                cloudlet.setUtilizationModelRam(createUtilizationModel(cloudlet.getUtilizationModelRam(), taskUsage.getCanonicalMemoryUsage()));
            }

            /* We don't need to check if some resource was changed because
             * if this Runnable is executed is because something was.
             * An event to execute such Runnable is just sent in such a condition.*/
            final DatacenterBroker broker = cloudlet.getBroker();
            broker.LOGGER.trace("{}: {}: {} resource usage changed: {}", simulation.clockStr(), broker.getName(), cloudlet, builder);
            cloudlet.getVm().getHost().updateProcessing(simulation.clock());
        };


        if(hasCloudletResourceUsageChanged(cloudlet, taskUsage)){
            addAvailableObject(cloudlet);
            final CloudSimEvent evt =
                new CloudSimEvent(
                    taskUsage.getStartTime(), cloudlet.getBroker(),
                    CloudSimTags.CLOUDLET_UPDATE_ATTRIBUTES, resourceUsageUpdateRunnable);
            return cloudletUsageChangeEvents.add(evt);
        }

        return false;
    }

    /**
     * Creates a {@link UtilizationModel} based on another one.
     * If the given instance is a {@link UtilizationModelDynamic},
     * otherwise a UtilizationModelDynamic is created without cloning
     * another instance (that means it won't have the configurations
     * defined by another model).
     * Then, the initial utilization of the created {@link UtilizationModelDynamic}
     * is set as the given parameter.
     *
     * @param source the utilization model that will be used as based to
     * @param initialUtilization a percentage value (in scale from 0 to 1)
     *                           to define the current utilization for the created {@link UtilizationModelDynamic}
     * @return an {@link UtilizationModelDynamic} instance with the current utilization equals
     *         to the given parameter
     */
    private UtilizationModel createUtilizationModel(final UtilizationModel source, final double initialUtilization){
        if(source instanceof UtilizationModelDynamic){
            return new UtilizationModelDynamic((UtilizationModelDynamic)source, initialUtilization);
        }

        return new UtilizationModelDynamic(initialUtilization);
    }

    private boolean hasCloudletResourceUsageChanged(final Cloudlet cloudlet, final TaskUsage taskUsage){
        return cloudlet.getUtilizationOfCpu() != taskUsage.getMeanCpuUsageRate() ||
               cloudlet.getUtilizationOfRam() != taskUsage.getCanonicalMemoryUsage();
    }

}
