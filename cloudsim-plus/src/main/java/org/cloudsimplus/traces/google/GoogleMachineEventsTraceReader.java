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

import org.cloudbus.cloudsim.core.CloudInformationService;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.util.TraceReaderAbstract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Process "machine events" trace files from
 * <a href="https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md">Google Cluster Data</a>.
 * When a trace file is {@link #process() processed}, it creates a list of available {@link Host}s for every line with a zero timestamp
 * and the {@link #getEventType() event type} equals to {@link MachineEventType#ADD}, meaning
 * that such Hosts will be immediately available at the simulation start.
 * Hosts addition events with timestamp greater than zero will be scheduled to be added
 * just at the specified type. In the same way, Hosts removal are accordingly scheduled.
 *
 * <p>Such trace files are the ones inside the machine_events sub-directory of downloaded Google traces.
 * The instructions to download the traces are provided in the link above.
 * A spreadsheet that makes it easier to understand the structure of trace files is provided
 * in docs/google-cluster-data-samples.xlsx</p>
 *
 * <p>The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above.
 * It's strongly recommended to read such a documentation before trying to use this class.</p>
 *
 * <p>Check important details at {@link TraceReaderAbstract}.</p>
 *
 * @see #getInstance(String, Function)
 * @see #process()
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class GoogleMachineEventsTraceReader extends GoogleTraceReaderAbstract<Host> {

    /**
     * The index of each field in the trace file.
     */
    public enum FieldIndex implements TraceField<GoogleMachineEventsTraceReader> {
        /**
         * 0: The index of the field containing the time the event happened (in microsecond).
         */
        TIMESTAMP{
            /**
             * Gets the timestamp converted to milliseconds.
             * @param reader the reader for the trace file
             * @return
             */
            @Override
            public Double getValue(final GoogleMachineEventsTraceReader reader) {
                return TimeUtil.microToSeconds(reader.getFieldDoubleValue(this));
            }
        },

        /**
         * 1: The index of the field containing the machine ID.
         */
        MACHINE_ID{
            @Override
            public Long getValue(final GoogleMachineEventsTraceReader reader) {
                return reader.getFieldLongValue(this);
            }
        },

        /**
         * 2: The index of the field containing the type of event.
         * The possible values for this field are the ordinal values of the enum {@link MachineEventType}.
         */
        EVENT_TYPE{
            @Override
            public Integer getValue(final GoogleMachineEventsTraceReader reader) {
                return reader.getFieldIntValue(this);
            }
        },

        /**
         * 3: The platform ID is an opaque string representing the micro-architecture and chipset version of the machine.
         */
        PLATFORM_ID{
            @Override
            public Integer getValue(final GoogleMachineEventsTraceReader reader) {
                return reader.getFieldIntValue(this);
            }
        },

        /**
         * 4: The index of the CPU capacity field in the trace,
         * that represents a percentage (between 0 and 1)
         * of the {@link #getMaxCpuCores()}.
         */
        CPU_CAPACITY{
            /**
             * Gets the actual number of {@link Pe}s (CPU cores) to be assigned to a Host,
             * according the {@link GoogleMachineEventsTraceReader#getMaxCpuCores()}.
             */
            @Override
            public Integer getValue(final GoogleMachineEventsTraceReader reader) {
                final double fieldValue = reader.getFieldDoubleValue(this);
                return (int) Math.round(fieldValue * reader.getMaxCpuCores());
            }
        },

        /**
         * 5: The index of the RAM capacity field in the trace,
         * that represents a percentage (between 0 and 1)
         * of the {@link #getMaxRamCapacity()} ()}.
         */
        RAM_CAPACITY{
            /**
             * Gets the actual RAM capacity to be assigned to a Host,
             * according the {@link GoogleMachineEventsTraceReader#getMaxRamCapacity()}.
             */
            @Override
            public Long getValue(final GoogleMachineEventsTraceReader reader) {
                final double fieldValue = reader.getFieldDoubleValue(this);
                return Math.round(fieldValue * reader.getMaxCpuCores());
            }
        }
    }

    /**
     * @see #getMaxRamCapacity()
     */
    private long maxRamCapacity;

    /**
     * @see #getMaxCpuCores()
     */
    private int maxCpuCores;

    /**
     * @see #getDatacenterForLaterHosts()
     */
    private Datacenter datacenterForLaterHosts;

    /**
     * @see #setHostCreationFunction(Function)
     */
    private Function<MachineEvent, Host> hostCreationFunction;

    /**
     * The list of Hosts that will be available just after a given timestamp (i.e. timestamp > 0).
     */
    private final List<Host> laterAvailableHosts;

    /**
     * List of Hosts to be removed from the Datacenter.
     */
    private final List<Host> hostsForRemoval;

    /**
     * Gets a {@link GoogleMachineEventsTraceReader} instance to read a "machine events" trace file
     * inside the <b>application's resource directory</b>.
     * Created Hosts will have 16GB of maximum RAM and the maximum of 8 {@link Pe}s.
     * Use the available constructors if you want to load a file outside the resource directory.
     *
     * @param filePath           the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param hostCreationFunction A {@link Function} that will be called for every {@link Host} to be created
     *                           from a line inside the trace file.
     *                           The {@link Function} will receive a {@link MachineEvent} object containing
     *                           the Host data read from the trace and must return the created Host
     *                           according to such data.
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #setMaxRamCapacity(long)
     * @see #setMaxCpuCores(int)
     * @see #process()
     */
    public static GoogleMachineEventsTraceReader getInstance(
        final String filePath,
        final Function<MachineEvent, Host> hostCreationFunction)
    {
        final InputStream reader = ResourceLoader.newInputStream(filePath, GoogleMachineEventsTraceReader.class);
        return new GoogleMachineEventsTraceReader(filePath, reader, hostCreationFunction);
    }

    /**
     * Instantiates a GoogleMachineEventsTraceReader to read a "machine events" trace file.
     * Created Hosts will have 16GB of maximum RAM and the maximum of 8 {@link Pe}s.
     *
     * @param filePath           the path to the trace file
     * @param hostCreationFunction A {@link Function} that will be called for every {@link Host} to be created
     *                           from a line inside the trace file.
     *                           The {@link Function} will receive a {@link MachineEvent} object containing
     *                           the Host data read from the trace and must return the created Host
     *                           according to such data.
     * @throws FileNotFoundException    when the trace file is not found
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @see #setMaxRamCapacity(long)
     * @see #setMaxCpuCores(int)
     * @see #process()
     */
    public GoogleMachineEventsTraceReader(
        final String filePath,
        final Function<MachineEvent, Host> hostCreationFunction) throws IOException
    {
        this(filePath, Files.newInputStream(Paths.get(filePath)), hostCreationFunction);
    }

    /**
     * Instantiates a GoogleMachineEventsTraceReader to read a "machine events" from a given InputStream.
     * Created Hosts will have 16GB of maximum RAM and the maximum of 8 {@link Pe}s.
     *
     * @param filePath           the path to the trace file
     * @param reader             a {@link InputStream} object to read the file
     * @param hostCreationFunction A {@link Function} that will be called for every {@link Host} to be created
     *                           from a line inside the trace file.
     *                           The {@link Function} will receive a {@link MachineEvent} object containing
     *                           the Host data read from the trace and must return the created Host
     *                           according to such data.
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @see #setMaxRamCapacity(long)
     * @see #setMaxCpuCores(int)
     * @see #process()
     */
    private GoogleMachineEventsTraceReader(
        final String filePath,
        final InputStream reader,
        final Function<MachineEvent, Host> hostCreationFunction)
    {
        super(filePath, reader);
        this.setHostCreationFunction(hostCreationFunction);
        this.setMaxRamCapacity((long) Conversion.gigaToMega(16));
        this.setMaxCpuCores(8);
        this.laterAvailableHosts = new ArrayList<>();
        this.hostsForRemoval = new ArrayList<>();
    }

    /**
     * Process the {@link #getFilePath() trace file} creating a Set of {@link Host}s
     * described in the file.
     *
     * <p>It returns the Set of {@link Host}s that were available at timestamp 0 inside the trace file.
     * Hosts available just after this initial timestamp (that represents the beginning of the simulation)
     * will be dynamically requested to be created by sending a message to the given Datacenter.
     * </p>
     *
     * <p>
     * The Set of returned Hosts is not added to any Datacenter. The developer creating the simulation
     * must add such Hosts to any Datacenter desired.</p>
     *
     * @return the Set of {@link Host}s that were available at timestamp 0 inside the trace file.
     */
    @Override
    public Set<Host> process() {
        return super.process();
    }

    @Override
    protected void preProcess() {
        if (this.datacenterForLaterHosts == null) {
            throw new IllegalStateException("The Datacenter where the Hosts with timestamp greater than 0 will be created must be set.");
        }
    }

    /**
     * Process hosts events occurring for a timestamp greater than zero.
     */
    @Override
    protected void postProcess() {
        if (datacenterForLaterHosts.getSimulation().isRunning())
            sendLaterHostsAdditionAndRemovalRequests();
        else {
            /* Since sending events just works after the simulation has started,
             * if it hasn't yet, a listener is used to send the Host creation events just after
             * the simulation starts. */
            datacenterForLaterHosts.getSimulation().addOnSimulationStartListener(info -> sendLaterHostsAdditionAndRemovalRequests());
        }
    }

    /**
     * Process addition and removal of Hosts occurring for a timestamp greater than zero.
     */
    private void sendLaterHostsAdditionAndRemovalRequests() {
        final CloudInformationService cis = datacenterForLaterHosts.getSimulation().getCloudInfoService();
        laterAvailableHosts.forEach(host -> cis.schedule(datacenterForLaterHosts, host.getStartTime(), CloudSimTags.HOST_ADD, host));

        //Sends a request to every Datacenter to try remove the Hosts (since we don't have how to know which Datacenter each Host is)
        cis.getDatacenterList().forEach(this::sendHostsRemovalRequests);
    }

    /**
     * Send requests to remove Hosts from a given Datacenter.
     *
     * @param dc the first Datacenter to look for the Host to remove
     *           since we don't have how to know from the trace in which Datacenter the Host is
     */
    private void sendHostsRemovalRequests(final Datacenter dc) {
        final CloudInformationService cis = dc.getSimulation().getCloudInfoService();

        /* The shutdown time is increased by a small fraction
         * to ensure that for each Datacenter, a request to find and remove
         * a Host will be sent in different times.
         * This way, if a DC finds a Host and removes it,
         * it cancel the subsequent messages to the next Datacenters.
         * Since the Host was already found, the simulator doesn't need
         * to keep looking for the Host inside the other datacenters.*/
        hostsForRemoval.forEach(host -> cis.schedule(dc, host.getShutdownTime() + dc.getId() * 0.00001, CloudSimTags.HOST_REMOVE, host.getId()));
    }

    @Override
    protected boolean processParsedLineInternal() {
        return getEventType().process(this);
    }

    /**
     * Gets the number of Hosts that are going to be created
     * later, according to the timestamp in the trace file.
     *
     * @return
     */
    public int getNumberOfLaterAvailableHosts() {
        return laterAvailableHosts.size();
    }

    /**
     * Gets the number of Hosts to be removed from some Datacenter.
     *
     * @return
     */
    public int getNumberOfHostsForRemoval() {
        return hostsForRemoval.size();
    }

    /**
     * Gets the enum value that represents the event type of the current trace line.
     *
     * @return the {@link MachineEventType} value
     */
    private MachineEventType getEventType() {
        return MachineEventType.getValue(FieldIndex.EVENT_TYPE.getValue(this));
    }

    /**
     * Creates a Host instance from the {@link #getLastParsedLineArray() last parsed line},
     * using the given {@link #setHostCreationFunction(Function) host create function}.
     *
     * @return the Host instance
     */
    protected Host createHostFromTraceLine() {
        final MachineEvent event = new MachineEvent();
        event.setCpuCores(FieldIndex.CPU_CAPACITY.getValue(this))
             .setRam(FieldIndex.RAM_CAPACITY.getValue(this))
             .setTimestamp(FieldIndex.TIMESTAMP.getValue(this))
             .setMachineId(FieldIndex.MACHINE_ID.getValue(this));
        final Host host = hostCreationFunction.apply(event);
        host.setId(FieldIndex.MACHINE_ID.getValue(this));
        return host;
    }

    /**
     * Gets the Datacenter where the Hosts with timestamp greater than 0 will be created.
     *
     * @return
     */
    public Datacenter getDatacenterForLaterHosts() {
        return datacenterForLaterHosts;
    }

    /**
     * Adds a Host to the List of Hosts to be removed from the Datacenter.
     *
     * @param host
     */
    protected boolean addHostToRemovalList(final Host host) {
        return hostsForRemoval.add(host);
    }

    /**
     * Adds a Host that will become available for the Datacenter just
     * at the time specified by the timestamp in the trace line,
     * which is set as the host {@link Host#getStartTime() startup time}.
     *
     * @param host the Host to be added
     * @return
     */
    protected boolean addLaterAvailableHost(final Host host) {
        return laterAvailableHosts.add(host);
    }

    public void setDatacenterForLaterHosts(final Datacenter datacenterForLaterHosts) {
        this.datacenterForLaterHosts = requireNonNull(datacenterForLaterHosts);
    }

    /**
     * Gets the maximum RAM capacity (in MB) for created Hosts.
     *
     * @return
     */
    public long getMaxRamCapacity() {
        return maxRamCapacity;
    }

    /**
     * Sets the maximum RAM capacity (in MB) for created Hosts.
     *
     * @param maxRamCapacity the maximum RAM capacity (in MB) to set
     */
    public void setMaxRamCapacity(final long maxRamCapacity) {
        if (maxRamCapacity <= 0) {
            throw new IllegalArgumentException("RAM capacity must be greater than 0.");
        }
        this.maxRamCapacity = maxRamCapacity;
    }

    /**
     * Gets the maximum number of {@link Pe}s (CPU cores) for created Hosts.
     *
     * @return
     */
    public int getMaxCpuCores() {
        return maxCpuCores;
    }

    /**
     * Sets the maximum number of {@link Pe}s (CPU cores) for created Hosts.
     *
     * @param maxCpuCores the maximum number of {@link Pe}s (CPU cores) to set
     */
    public void setMaxCpuCores(final int maxCpuCores) {
        if (maxCpuCores <= 0) {
            throw new IllegalArgumentException("Number of CPU cores must be greater than 0.");
        }
        this.maxCpuCores = maxCpuCores;
    }

    /**
     * Sets a {@link BiFunction} that will be called for every {@link Host} to be created
     * from a line inside the trace file.
     * The {@link BiFunction} will receive the number of {@link Pe}s (CPU cores)
     * and RAM capacity for the Host to be created, returning the created Host.
     * The provided function must instantiate the Host and defines Host's CPU cores and RAM
     * capacity according the the received parameters.
     * For other Hosts configurations (such as storage capacity), the provided
     * function must define the value as desired, since the trace file
     * doesn't have any other information for such resources.
     *
     * @param hostCreationFunction the Host creation {@link BiFunction} to set
     */
    public void setHostCreationFunction(final Function<MachineEvent, Host> hostCreationFunction) {
        this.hostCreationFunction = requireNonNull(hostCreationFunction);
    }
}
