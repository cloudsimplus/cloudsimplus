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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.core.CloudInformationService;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.traces.TraceReaderAbstract;
import org.cloudsimplus.util.BytesConversion;
import org.cloudsimplus.util.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/// Process "machine events" trace files from
/// [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md).
///
/// When a trace file is [processed][#process()], it creates a list of available [Host]s for every line with a zero timestamp
/// and the [event type][#getEventType()] equals to [MachineEventType#ADD], meaning
/// that such Hosts will be immediately available at the simulation start.
/// Hosts addition events with timestamp greater than zero will be scheduled to be added
/// just at the specified type. In the same way, Hosts removal is accordingly scheduled.
///
/// Such trace files are the ones inside the machine_events subdirectory of downloaded Google traces.
/// The instructions to download the traces are provided in the link above.
/// A spreadsheet that makes it easier to understand the structure of trace files is provided
/// in `docs/google-cluster-data-samples.xlsx`
///
/// The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above.
/// It's strongly recommended to read such documentation before trying to use this class.
///
/// Check important details at [TraceReaderAbstract].
///
/// @see #getInstance(String, Function)
/// @see #process()
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
@Getter
public final class GoogleMachineEventsTraceReader extends GoogleTraceReaderAbstract<Host> {

    /**
     * The maximum RAM capacity (in MB) for created Hosts.
     */
    private long maxRamCapacity;

    /**
     * The maximum number of {@link Pe}s (CPU cores) for created Hosts.
     */
    private int maxCpuCores;

    /**
     * The Datacenter where the Hosts with timestamp greater than 0 will be created.
     */
    @Setter @NonNull
    private Datacenter datacenterForLaterHosts;

    /**
     * A {@link BiFunction} that will be called for every {@link Host} to be created
     * from a line inside the trace file.
     * The {@link BiFunction} will receive the number of {@link Pe}s (CPU cores)
     * and RAM capacity for the Host to be created, returning the created Host.
     * The provided function must instantiate the Host and define Host's CPU cores and RAM
     * capacity, according to the received parameters.
     * For other Hosts' configurations (such as storage capacity), the provided
     * function must define the value as desired, since the trace file
     * doesn't have any other information for such resources.
     */
    @Getter(AccessLevel.NONE) @Setter
    private Function<MachineEvent, Host> hostCreationFunction;

    /**
     * The list of Hosts that will be available just after a given timestamp (i.e., `timestamp > 0`).
     */
    private final List<Host> laterAvailableHosts;

    /**
     * List of Hosts to be removed from the Datacenter.
     */
    private final List<Host> hostsForRemoval;

    /// Gets a [GoogleMachineEventsTraceReader] instance to read a "machine events" trace file
    /// inside the **application's resource directory**.
    /// Created Hosts will have 16GB of maximum RAM and the maximum of 8 [Pe]s.
    /// Use the available constructors if you want to load a file outside the resource directory.
    ///
    /// @param filePath           the workload trace **relative file name** in one of the following formats: _ASCII text, zip, gz._
    /// @param hostCreationFunction A [Function] that will be called for every [Host] to be created
    ///                           from a line inside the trace file.
    ///                           The [Function] will receive a [MachineEvent] object containing
    ///                           the Host data read from the trace and must return the created Host
    ///                           according to such data.
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
    /// @see #setMaxRamCapacity(long)
    /// @see #setMaxCpuCores(int)
    /// @see #process()
    public static GoogleMachineEventsTraceReader getInstance(
        final String filePath,
        final Function<MachineEvent, Host> hostCreationFunction)
    {
        final var is = ResourceLoader.newInputStream(filePath, GoogleMachineEventsTraceReader.class);
        return new GoogleMachineEventsTraceReader(filePath, is, hostCreationFunction);
    }

    /// Instantiates a GoogleMachineEventsTraceReader to read a "machine events" trace file.
    /// Created Hosts will have 16GB of maximum RAM and the maximum of 8 [Pe]s.
    ///
    /// @param filePath           the path to the trace file
    /// @param hostCreationFunction A [Function] that will be called for every [Host] to be created
    ///                           from a line inside the trace file.
    ///                           The [Function] will receive a [MachineEvent] object containing
    ///                           the Host data read from the trace and must return the created Host
    ///                           according to such data.
    /// @throws FileNotFoundException    when the trace file is not found
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @see #setMaxRamCapacity(long)
    /// @see #setMaxCpuCores(int)
    /// @see #process()
    public GoogleMachineEventsTraceReader(
        final String filePath,
        final Function<MachineEvent, Host> hostCreationFunction) throws IOException
    {
        this(filePath, Files.newInputStream(Paths.get(filePath)), hostCreationFunction);
    }

    /// Instantiates a GoogleMachineEventsTraceReader to read a "machine events" from a given InputStream.
    /// Created Hosts will have 16GB of maximum RAM and the maximum of 8 [Pe]s.
    ///
    /// @param filePath           the path to the trace file
    /// @param reader             a [InputStream] object to read the file
    /// @param hostCreationFunction A [Function] that will be called for every [Host] to be created
    ///                           from a line inside the trace file.
    ///                           The [Function] will receive a [MachineEvent] object containing
    ///                           the Host data read from the trace and must return the created Host
    ///                           according to such data.
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @see #setMaxRamCapacity(long)
    /// @see #setMaxCpuCores(int)
    /// @see #process()
    private GoogleMachineEventsTraceReader(
        final String filePath,
        final InputStream reader,
        final Function<MachineEvent, Host> hostCreationFunction)
    {
        super(filePath);
        this.setHostCreationFunction(hostCreationFunction);
        this.setMaxRamCapacity((long) BytesConversion.gigaToMega(16));
        this.setMaxCpuCores(8);
        this.laterAvailableHosts = new ArrayList<>();
        this.hostsForRemoval = new ArrayList<>();
    }

    /// Process the [trace file][#getFilePath()] creating a Set of [Host]s
    /// described in the file.
    ///
    /// It returns the Set of [Host]s that were available at timestamp 0 inside the trace file.
    /// Hosts available just after this initial timestamp (that represents the beginning of the simulation)
    /// will be dynamically requested to be created by sending a message to the given Datacenter.
    ///
    /// The Set of returned Hosts is not added to any Datacenter. The developer creating the simulation
    /// must add such Hosts to any Datacenter desired.
    ///
    /// @return the Set of [Host]s that were available at timestamp 0 inside the trace file.
    @Override
    public Collection<Host> process() {
        return super.process();
    }

    @Override
    protected void preProcess() {
        if (this.datacenterForLaterHosts == null) {
            throw new IllegalStateException("The Datacenter where the Hosts with timestamp greater than 0 will be created must be set.");
        }
    }

    /**
     * Process all hosts events occurring for a timestamp greater than zero.
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
     * Process the addition and removal of Hosts occurring for a timestamp greater than zero.
     */
    private void sendLaterHostsAdditionAndRemovalRequests() {
        final var cis = datacenterForLaterHosts.getSimulation().getCis();
        laterAvailableHosts.forEach(host -> cis.schedule(datacenterForLaterHosts, host.getStartTime(), CloudSimTag.HOST_ADD, host));

        //Sends a request to every Datacenter to try to remove the Hosts (since we don't have how to know which Datacenter each Host is)
        cis.getDatacenterList().forEach(this::sendHostsRemovalRequests);
    }

    /**
     * Send requests to remove Hosts from a given Datacenter.
     *
     * @param dc the first Datacenter to look for the Host to remove
     *           since we don't have how to know from the trace in which Datacenter the Host is
     */
    private void sendHostsRemovalRequests(final Datacenter dc) {
        final CloudInformationService cis = dc.getSimulation().getCis();

        /* The shutdown time is increased by a small fraction
         * to ensure that for each Datacenter, a request to find and remove
         * a Host will be sent in different times.
         * This way, if a DC finds a Host and removes it,
         * it cancels the subsequent messages to the next Datacenters.
         * Since the Host was already found, the simulator doesn't need
         * to keep looking for the Host inside the other datacenters.*/
        hostsForRemoval.forEach(host -> cis.schedule(dc, host.getFinishTime() + dc.getId() * 0.00001, CloudSimTag.HOST_REMOVE, host.getId()));
    }

    @Override
    protected boolean processParsedLineInternal() {
        return getEventType().process(this);
    }

    /**
     * @return the number of Hosts that are going to be created
     * later, according to the timestamp in the trace file.
     */
    public int getNumberOfLaterAvailableHosts() {
        return laterAvailableHosts.size();
    }

    /**
     * @return the number of Hosts to be removed from some Datacenter.
     */
    public int getNumberOfHostsForRemoval() {
        return hostsForRemoval.size();
    }

    /**
     * Gets the enum value that represents the event type of the current trace line.
     *
     * @return the {@link MachineEventType} value
     */
    public MachineEventType getEventType() {
        return MachineEventType.getValue(MachineEventField.EVENT_TYPE.getValue(this));
    }

    /// Creates a Host instance from the [last parsed line][#getLastParsedLineArray()],
    /// using the given [host create function][#setHostCreationFunction(Function)].
    ///
    /// @return the Host instance
    Host createHostFromTraceLine() {
        final var event =
            MachineEvent.builder()
                .cpuCores(MachineEventField.CPU_CAPACITY.getValue(this))
                .ram(MachineEventField.RAM_CAPACITY.getValue(this))
                .timestamp(MachineEventField.TIMESTAMP.getValue(this))
                .machineId(MachineEventField.MACHINE_ID.getValue(this))
                .build();
        final Host host = hostCreationFunction.apply(event);
        host.setId(MachineEventField.MACHINE_ID.getValue(this));
        return host;
    }

    /**
     * Adds a Host to the List of Hosts to be removed from the Datacenter.
     *
     * @param host the Host to add
     */
    boolean addHostToRemovalList(final Host host) {
        return hostsForRemoval.add(host);
    }

    /// Adds a Host that will become available for the Datacenter just
    /// at the time specified by the timestamp in the trace line,
    /// which is set as the host [startup time][Host#getStartTime()].
    ///
    /// @param host the Host to be added
    /// @return true if the host was added, false otherwise
    boolean addLaterAvailableHost(final Host host) {
        return laterAvailableHosts.add(host);
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
}
