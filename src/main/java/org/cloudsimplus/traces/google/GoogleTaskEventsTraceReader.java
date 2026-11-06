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
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.traces.TraceReaderAbstract;
import org.cloudsimplus.util.ResourceLoader;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/// Process "task events" trace files from
/// [Google Cluster Data](https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md)
/// to create [Cloudlet]s belonging to cloud customers (users).
/// Customers are represented as [DatacenterBroker] instances created from the trace file.
/// The trace files are the ones inside the `task_events` subdirectory of downloaded Google traces.
/// The instructions to download the traces are provided in the link above.
///
/// The class also creates the required brokers to represent the customers (users)
/// defined by the username field inside the trace file.
///
/// A spreadsheet that makes it easier to understand the structure of trace files is provided
/// in `docs/google-cluster-data-samples.xlsx`.
///
/// The documentation for fields and values were obtained from the Google Cluster trace documentation in the link above.
/// It's strongly recommended to read such documentation before trying to use this class.
///
/// Check important details at [TraceReaderAbstract].
///
/// @see #process()
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
@Getter
public class GoogleTaskEventsTraceReader extends GoogleTraceReaderAbstract<Cloudlet> {
    /// List of messages to send to the [DatacenterBroker] that owns each created Cloudlet.
    /// Such events request a Cloudlet's status change or attributes change.
    /// @see #addCloudletStatusChangeEvents(CloudSimEvent, TaskEvent)
    @Getter(AccessLevel.NONE)
    protected final Map<Cloudlet, List<CloudSimEvent>> cloudletEvents;

    /// The manager that creates and provide access to [DatacenterBroker]s used by
    /// the trace reader.
    private final BrokerManager brokerManager;

    /// The maximum number of Cloudlets to create from the trace file.
    private int maxCloudletsToCreate;

    /// Checks if Cloudlets must be auto-submitted to the broker after created
    /// (default is true).
    @Setter
    private boolean autoSubmitCloudlets;

    /// A [Function] that will be called for every [Cloudlet] to be created
    /// from a line inside the trace file.
    /// The [Function] will receive a [TaskEvent] object containing
    /// the task data read from the trace and should the created Cloudlet.
    ///
    /// The provided function must instantiate the Host and define Host's CPU cores and RAM
    /// capacity according to the received parameters.
    /// For other Hosts' configurations (such as storage capacity), the provided
    /// function must define the value as desired, since the trace file
    /// doesn't have any other information for such resources.
    private Function<TaskEvent, Cloudlet> cloudletCreationFunction;

    private final CloudSimPlus simulation;

    /// Gets a [GoogleTaskEventsTraceReader] instance to read a "task events" trace file
    /// inside the **application's resource directory**.
    ///
    /// @param simulation the simulation instance that the created tasks and brokers will belong to.
    /// @param filePath the workload trace **relative file name** in one of the following formats: _ASCII text, zip, gz._
    /// @param cloudletCreationFunction A [Function] that will be called for every [Cloudlet] to be created
    ///                               from a line inside the trace file.
    ///                               The [Function] will receive a [TaskEvent] object containing
    ///                               the task data read from the trace and must return a new Cloudlet
    ///                               according to such data.
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
    /// @see #process()
    public static GoogleTaskEventsTraceReader getInstance(
        final CloudSimPlus simulation,
        final String filePath,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction)
    {
        final var is = ResourceLoader.newInputStream(filePath, GoogleTaskEventsTraceReader.class);
        return new GoogleTaskEventsTraceReader(simulation, filePath, is, cloudletCreationFunction);
    }

    /// Instantiates a [GoogleTaskEventsTraceReader] to read a "task events" file.
    ///
    /// @param simulation the simulation instance that the created tasks and brokers will belong to.
    /// @param filePath               the workload trace **relative file name** in one of the following formats: _ASCII text, zip, gz._
    /// @param cloudletCreationFunction A [Function] that will be called for every [Cloudlet] to be created
    ///                               from a line inside the trace file.
    ///                               The [Function] will receive a [TaskEvent] object containing
    ///                               the task data read from the trace and must return a new Cloudlet
    ///                               according to such data.
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
    /// @see #process()
    public GoogleTaskEventsTraceReader(
        final CloudSimPlus simulation,
        final String filePath,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction) throws IOException
    {
        this(simulation, filePath, Files.newInputStream(Paths.get(filePath)), cloudletCreationFunction);
    }

    /// Instantiates a [GoogleTaskEventsTraceReader] to read a "task events" from a given InputStream.
    ///
    /// @param simulation the simulation instance that the created tasks and brokers will belong to.
    /// @param filePath               the workload trace **relative file name** in one of the following formats: _ASCII text, zip, gz._
    /// @param reader                 a [InputStream] object to read the file
    /// @param cloudletCreationFunction A [Function] that will be called for every [Cloudlet] to be created
    ///                               from a line inside the trace file.
    ///                               The [Function] will receive a [TaskEvent] object containing
    ///                               the task data read from the trace and must return a new Cloudlet
    ///                               according to such data.
    /// @throws IllegalArgumentException when the trace file name is null or empty
    /// @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
    /// @see #process()
    protected GoogleTaskEventsTraceReader(
        @NonNull final CloudSimPlus simulation,
        final String filePath,
        final InputStream reader,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction)
    {
        super(filePath);
        this.simulation = simulation;
        this.cloudletCreationFunction = requireNonNull(cloudletCreationFunction);
        this.autoSubmitCloudlets = true;
        this.cloudletEvents = new HashMap<>();
        this.brokerManager = new BrokerManager(this);
        setMaxCloudletsToCreate(Integer.MAX_VALUE);
    }

    /// Process the [trace file][#getFilePath()] creating a Set of [Cloudlet]s
    /// described in the file. **Each created Cloudlet is automatically submitted to its respective
    /// broker.**
    ///
    /// It returns the Set of all submitted [Cloudlet]s at any timestamp inside the trace file
    /// (the timestamp is used to delay the Cloudlet submission).
    ///
    /// @return the Set of all submitted [Cloudlet]s for any timestamp inside the trace file.
    /// @see BrokerManager#getBrokers()
    @Override
    public final Collection<Cloudlet> process() {
        if(!autoSubmitCloudlets) {
            LOGGER.info("{}: Auto-submission of Cloudlets from trace file is disabled. Don't forget to submitted them to the broker.", getClass().getSimpleName());
        }

        return super.process();
    }

    @Override
    protected void postProcess(){
        cloudletEvents.values().forEach(this::sendCloudletEvents);
        cloudletEvents.clear();
    }

    protected void sendCloudletEvents(final List<CloudSimEvent> events) {
        events.forEach(evt -> evt.getSource().schedule(evt));
    }

    @Override
    protected boolean processParsedLineInternal() {
        final var eventType = TaskEventType.of(this);
        return eventType.process(this);
    }

    /// Send a message to the broker to request change in a Cloudlet status,
    /// using some tags from [CloudSimTag] such as [CloudSimTag#CLOUDLET_READY].
    /// @param tag a CLOUDLET tag from the [CloudSimTag] used to send a message to request the Cloudlet status change
    /// @return true if the request was created, false otherwise
    /* default */ boolean requestCloudletStatusChange(final int tag) {
        final var taskEvent = TaskEvent.of(this);
        final var broker = brokerManager.getBroker(taskEvent.getUserName());
        final double delay = taskEvent.getTimestamp();

        return findObject(taskEvent.getUniqueTaskId())
                .map(cloudlet -> addCloudletStatusChangeEvents(new CloudSimEvent(delay, broker, tag, cloudlet), taskEvent))
                .isPresent();
    }

    /// Adds the events to request changing the status and attributes of a Cloudlet to the
    /// list of events to send to the Cloudlet's broker.
    ///
    /// @param statusChangeSimEvt the [CloudSimEvent] to be sent requesting the change in a Cloudlet's status,
    /// where the data of the event is the Cloudlet to be changed.
    /// @param taskEvent the task event read from the trace file, containing
    /// the status and the attributes to change in the Cloudlet
    /// @return the Cloudlet to have its status and attributes changed
    private Cloudlet addCloudletStatusChangeEvents(final CloudSimEvent statusChangeSimEvt, final TaskEvent taskEvent){
        final var cloudlet = (Cloudlet)statusChangeSimEvt.getData();
        addEventToSend(cloudlet, statusChangeSimEvt);

        final var traceCloudlet = createTemporaryCloudlet(cloudlet, taskEvent);
        if(areCloudletAttributesDifferent(cloudlet, traceCloudlet)){
            final Runnable updater = () -> updateCloudletAttributes(cloudlet, traceCloudlet);
            addEventToSend(cloudlet, new CloudSimEvent(taskEvent.getTimestamp(), statusChangeSimEvt.getDestination(), CloudSimTag.CLOUDLET_UPDATE_ATTRIBUTES, updater));
        }

        return cloudlet;
    }

    /// Creates a temporary Cloudlet with the attributes from a trace line, which is just used to update an existing Cloudlet.
    /// The [cloudlet creation function][#getCloudletCreationFunction()] provided by the researcher
    /// is the only one that knows how to create a Cloudlet from a trace line.
    /// For instance, the class of the [UtilizationModel]s is chosen by the researcher
    /// and the [TaskEvent] only has the data used to instantiate them.
    ///
    /// @param cloudlet the Cloudlet to be updated
    /// @param taskEvent the task event read from the trace file
    /// @return the temporary Cloudlet
    private Cloudlet createTemporaryCloudlet(final Cloudlet cloudlet, final TaskEvent taskEvent) {
        final var traceCloudlet = createCloudlet(taskEvent);
        traceCloudlet.setId(cloudlet.getId());
        return traceCloudlet;
    }

    /// Updates the attributes of a Cloudlet that are different from the ones in the trace, logging the changes.
    /// The "resource request" fields in the trace define the max amount of a resource the Cloudlet is allowed to use.
    ///
    /// @param cloudlet the Cloudlet to be updated
    /// @param traceCloudlet the temporary Cloudlet having the new attributes
    private void updateCloudletAttributes(final Cloudlet cloudlet, final Cloudlet traceCloudlet) {
        final var changes = new StringBuilder();
        updatePriority(cloudlet, traceCloudlet, changes);
        updateFileSize(cloudlet, traceCloudlet, changes);
        updateRequiredCores(cloudlet, traceCloudlet, changes);
        updateMaxRamUsage(cloudlet, traceCloudlet, changes);

        final DatacenterBroker broker = cloudlet.getBroker();
        broker.LOGGER.trace("{}: {}: {} attributes updated: {}", getSimulation().clockStr(), broker.getName(), cloudlet, changes);
    }

    private void updatePriority(final Cloudlet cloudlet, final Cloudlet traceCloudlet, final StringBuilder changes) {
        if(traceCloudlet.getPriority() != cloudlet.getPriority()){
            appendChange(changes, "priority", cloudlet.getPriority(), traceCloudlet.getPriority());
            cloudlet.setPriority(traceCloudlet.getPriority());
        }
    }

    /// Updates the file size, but not the output size, since they aren't always related.
    /// That can be done by the researcher if needed.
    private void updateFileSize(final Cloudlet cloudlet, final Cloudlet traceCloudlet, final StringBuilder changes) {
        if(traceCloudlet.getFileSize() != cloudlet.getFileSize()){
            appendChange(changes, "file size", cloudlet.getFileSize(), traceCloudlet.getFileSize());
            cloudlet.setFileSize(traceCloudlet.getFileSize());
        }
    }

    private void updateRequiredCores(final Cloudlet cloudlet, final Cloudlet traceCloudlet, final StringBuilder changes) {
        if(traceCloudlet.getRequiredCores() != cloudlet.getRequiredCores()){
            appendChange(changes, "CPU cores", cloudlet.getRequiredCores(), traceCloudlet.getRequiredCores());
            cloudlet.setRequiredCores(traceCloudlet.getRequiredCores());
        }
    }

    /// Updates the max RAM usage. [#createCloudlet(TaskEvent)] ensures Cloudlets use a [UtilizationModelDynamic] for RAM.
    private void updateMaxRamUsage(final Cloudlet cloudlet, final Cloudlet traceCloudlet, final StringBuilder changes) {
        final var traceRamModel = (UtilizationModelDynamic)traceCloudlet.getUtilizationModelRam();
        final var ramModel = (UtilizationModelDynamic)cloudlet.getUtilizationModelRam();
        final double newMaxUsage = traceRamModel.getMaxResourceUtilization();
        if(newMaxUsage != ramModel.getMaxResourceUtilization()){
            appendChange(changes, "Max RAM Usage", formatPercentValue(ramModel.getMaxResourceUtilization()), formatPercentValue(newMaxUsage) + "%");
            ramModel.setMaxResourceUtilization(newMaxUsage);
        }
    }

    private static void appendChange(final StringBuilder changes, final String attribute, final Object oldValue, final Object newValue) {
        changes.append(attribute).append(": ")
               .append(oldValue).append(VAL_SEPARATOR)
               .append(newValue).append(COL_SEPARATOR);
    }

    /// Adds a Cloudlet event to the map of events to send
    /// @param cloudlet the Cloudlet which an event to be sent
    /// @param evt the event to be sent
    private void addEventToSend(final Cloudlet cloudlet, final CloudSimEvent evt) {
        cloudletEvents
            .compute(cloudlet, (key, list) -> list == null ? new LinkedList<>() : list)
            .add(evt);
    }

    protected Cloudlet createCloudlet(final TaskEvent taskEvent) {
        final Cloudlet cloudlet = cloudletCreationFunction.apply(taskEvent);
        if(cloudlet.getUtilizationModelRam() instanceof UtilizationModelDynamic) {
            return cloudlet;
        }

        throw new IllegalStateException(
            "Since the 'task events' trace file provides a field defining the max RAM the Cloudlet can request (RESOURCE_REQUEST_FOR_RAM), " +
                "it's required to use a UtilizationModelDynamic for the Cloudlet's RAM utilization model, " +
                "so that the UtilizationModelDynamic.maxResourceUtilization attribute can be changed when defined in the trace file.");

    }

    private boolean areCloudletAttributesDifferent(final Cloudlet cloudlet1, final Cloudlet cloudlet2) {
        return cloudlet2.getPriority() != cloudlet1.getPriority() ||
               cloudlet2.getFileSize() != cloudlet1.getFileSize() ||
               cloudlet2.getRequiredCores() != cloudlet1.getRequiredCores() ||
               cloudlet2.getUtilizationOfCpu() != cloudlet1.getUtilizationOfCpu() ||
               cloudlet2.getUtilizationOfRam() != cloudlet1.getUtilizationOfRam();
    }

    /// Sets the maximum number of Cloudlets to create from the trace file.
    ///
    /// Consider the number of lines in the file may be greater than the number
    /// of Cloudlets that will be created from it (since there may be lines
    /// representing different cloudlet requests, such as creation, execution, pause, destruction, etc.).
    /// By using the [#setMaxLinesToRead(int)] it may not ensure that only a given number of Cloudlets will be created.
    /// @param maxCloudletsToCreate the maximum number of Cloudlets to create from the file.
    ///                             Use [Double#MAX_VALUE] to disable this configuration.
    /// @see #setMaxLinesToRead(int)
    public GoogleTaskEventsTraceReader setMaxCloudletsToCreate(final int maxCloudletsToCreate) {
        if(maxCloudletsToCreate <= 0) {
            throw new IllegalArgumentException("Maximum number of Cloudlets to create must be greater than 0. If you want to create all Cloudlets from the entire file, provide Integer.MAX_VALUE.");
        }

        this.maxCloudletsToCreate = maxCloudletsToCreate;
        return this;
    }

    /// Checks if the maximum number of Cloudlets to create was not reached.
    /// @return true to indicate the Cloudlet is allowed to be created, false otherwise.
    protected boolean allowCloudletCreation() {
        return availableObjectsCount() < getMaxCloudletsToCreate();
    }
}
