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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.TraceReaderAbstract;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Process "task events" trace files from
 * <a href="https://github.com/google/cluster-data/blob/master/ClusterData2011_2.md">Google Cluster Data</a>
 * to create {@link Cloudlet}s belonging to cloud customers (users).
 * Customers are represented as {@link DatacenterBroker} instances created from the trace file.
 * The trace files are the ones inside the task_events sub-directory of downloaded Google traces.
 * The instructions to download the traces are provided in the link above.
 *
 * <p>The class also creates the required brokers to represent the customers (users)
 * defined by the username field inside the trace file.</p>
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
public class GoogleTaskEventsTraceReader extends GoogleTraceReaderAbstract<Cloudlet> {
    /**
     * List of messages to send to the {@link DatacenterBroker} that owns each created Cloudlet.
     * Such events request a Cloudlet's status change or attributes change.
     * @see #addCloudletStatusChangeEvents(CloudSimEvent, TaskEvent)
     */
    protected final Map<Cloudlet, List<CloudSimEvent>> cloudletEvents;

    /** @see #getBrokerManager() */
    private final BrokerManager brokerManager;

    /** @see #getMaxCloudletsToCreate() */
    private int maxCloudletsToCreate;

    /** @see #setAutoSubmitCloudlets(boolean) */
    private boolean autoSubmitCloudlets;

    /**
     * @see #setCloudletCreationFunction(Function)
     */
    private Function<TaskEvent, Cloudlet> cloudletCreationFunction;

    private final CloudSim simulation;

    /**
     * Gets a {@link GoogleTaskEventsTraceReader} instance to read a "task events" trace file
     * inside the <b>application's resource directory</b>.
     *
     * @param simulation the simulation instance that the created tasks and brokers will belong to.
     * @param filePath the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param cloudletCreationFunction A {@link Function} that will be called for every {@link Cloudlet} to be created
     *                               from a line inside the trace file.
     *                               The {@link Function} will receive a {@link TaskEvent} object containing
     *                               the task data read from the trace and must return a new Cloudlet
     *                               according to such data.
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    public static GoogleTaskEventsTraceReader getInstance(
        final CloudSim simulation,
        final String filePath,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction)
    {
        final InputStream reader = ResourceLoader.newInputStream(filePath, GoogleTaskEventsTraceReader.class);
        return new GoogleTaskEventsTraceReader(simulation, filePath, reader, cloudletCreationFunction);
    }

    /**
     * Instantiates a {@link GoogleTaskEventsTraceReader} to read a "task events" file.
     *
     * @param simulation the simulation instance that the created tasks and brokers will belong to.
     * @param filePath               the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param cloudletCreationFunction A {@link Function} that will be called for every {@link Cloudlet} to be created
     *                               from a line inside the trace file.
     *                               The {@link Function} will receive a {@link TaskEvent} object containing
     *                               the task data read from the trace and must return a new Cloudlet
     *                               according to such data.
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    public GoogleTaskEventsTraceReader(
        final CloudSim simulation,
        final String filePath,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction) throws IOException
    {
        this(simulation, filePath, Files.newInputStream(Paths.get(filePath)), cloudletCreationFunction);
    }

    /**
     * Instantiates a {@link GoogleTaskEventsTraceReader} to read a "task events" from a given InputStream.
     *
     * @param simulation the simulation instance that the created tasks and brokers will belong to.
     * @param filePath               the workload trace <b>relative file name</b> in one of the following formats: <i>ASCII text, zip, gz.</i>
     * @param reader                 a {@link InputStream} object to read the file
     * @param cloudletCreationFunction A {@link Function} that will be called for every {@link Cloudlet} to be created
     *                               from a line inside the trace file.
     *                               The {@link Function} will receive a {@link TaskEvent} object containing
     *                               the task data read from the trace and must return a new Cloudlet
     *                               according to such data.
     * @throws IllegalArgumentException when the trace file name is null or empty
     * @throws UncheckedIOException     when the file cannot be accessed (such as when it doesn't exist)
     * @see #process()
     */
    protected GoogleTaskEventsTraceReader(
        final CloudSim simulation,
        final String filePath,
        final InputStream reader,
        final Function<TaskEvent, Cloudlet> cloudletCreationFunction)
    {
        super(filePath, reader);
        this.simulation = requireNonNull(simulation);
        this.cloudletCreationFunction = requireNonNull(cloudletCreationFunction);
        this.autoSubmitCloudlets = true;
        this.cloudletEvents = new HashMap<>();
        this.brokerManager = new BrokerManager(this);
        setMaxCloudletsToCreate(Integer.MAX_VALUE);
    }

    /**
     * Process the {@link #getFilePath() trace file} creating a Set of {@link Cloudlet}s
     * described in the file. <b>Each created Cloudlet is automatically submitted to its respective
     * broker.</b>
     *
     * <p>It returns the Set of all submitted {@link Cloudlet}s at any timestamp inside the trace file
     * (the timestamp is used to delay the Cloudlet submission).
     * </p>
     *
     * @return the Set of all submitted {@link Cloudlet}s for any timestamp inside the trace file.
     * @see BrokerManager#getBrokers()
     */
    @Override
    public final Collection<Cloudlet> process() {
        if(!autoSubmitCloudlets) {
            LOGGER.info("{}: Auto-submission of Cloudlets from trace file is disabled. Don't forget to submitted them to the broker.", getClass().getSimpleName());
        }

        return super.process();
    }

    /**
     * There is no pre-process requirements for this implementation.
     */
    @Override
    protected void preProcess(){/**/}

    @Override
    protected void postProcess(){
        if(simulation.isRunning())
            sendCloudletEvents();
        else simulation.addOnSimulationStartListener(info -> sendCloudletEvents());
    }

    private void sendCloudletEvents() {
        cloudletEvents.values().forEach(this::sendCloudletEvents);
    }

    protected void sendCloudletEvents(final List<CloudSimEvent> events) {
        events.forEach(evt -> evt.getSource().schedule(evt));
    }

    @Override
    protected boolean processParsedLineInternal() {
        final var eventType = TaskEventType.of(this);
        return eventType.process(this);
    }

    /**
     * Send a message to the broker to request change in a Cloudlet status,
     * using some tags from {@link CloudSimTag} such as {@link CloudSimTag#CLOUDLET_READY}.
     * @param tag a CLOUDLET tag from the {@link CloudSimTag} used to send a message to request the Cloudlet status change
     * @return true if the request was created, false otherwise
     */
    /* default */ boolean requestCloudletStatusChange(final CloudSimTag tag) {
        final TaskEvent taskEvent = TaskEvent.of(this);
        final DatacenterBroker broker = brokerManager.getBroker(taskEvent.getUserName());
        final double delay = taskEvent.getTimestamp();

        return findObject(taskEvent.getUniqueTaskId())
                .map(cloudlet -> addCloudletStatusChangeEvents(new CloudSimEvent(delay, broker, tag, cloudlet), taskEvent))
                .isPresent();
    }

    /**
     * Adds the events to request to change the status and attributes of a Cloudlet to the
     * list of events to send to the Cloudlet's broker.
     *
     * @param statusChangeSimEvt the {@link CloudSimEvent} to be sent requesting the change in a Cloudlet's status,
     *                           where the data of the event is the Cloudlet to be changed.
     * @param taskEvent the task event read from the trace file, containing
     *                  the status and the attributes to change in the Cloudlet
     * @return
     * @TODO This method is too large and confusing, thus it needs to be refactored
     */
    private Cloudlet addCloudletStatusChangeEvents(final CloudSimEvent statusChangeSimEvt, final TaskEvent taskEvent){
        /*The actual Cloudlet that needs to have its status and/or attributes changed
         * by sending a request message to the broker.*/
        final Cloudlet cloudlet = (Cloudlet)statusChangeSimEvt.getData();

        addEventToSend(cloudlet, statusChangeSimEvt);

        /*
        Creates a temporary Cloudlet that will be discharged after the method finish.
        It is used just because the "cloudlet creation function" provided
        by the researcher is the only one that actually knows how to create a Cloudlet
        from the trace line.
        The TaskEvent object contains the data read from a trace line,
        but some Cloudlet attributes such as RAM and CPU UtilizationModel
        are instantiated based on a class chosen by the researcher.
        The UtilizationModel instance will just use the data read
        from the trace.
        This way, the temporary Cloudlet is created using the researcher's
        provided function so that the correct objects for such attributes
        are created and then set to the Cloudlet being updated.
         */
        final Cloudlet clone = createCloudlet(taskEvent);
        clone.setId(cloudlet.getId());

        /* If some attribute of the Cloudlet that needs to be changed,
         * sends a message requesting the change.*/
        if(!areCloudletAttributesDifferent(cloudlet, clone)){
            return cloudlet;
        }

        /* Defines a Runnable that will be executed when
         * the message is processed by the broker to update the Cloudlet attributes.
         * The "resource request" fields define the max amount of a resource the Cloudlet
         * is allowed to used and they are just used to define
         * initial number of CPUs, RAM utilization and Cloudlet file size/output size.
         * This way, when they change along the time in the trace,
         * these new values are ignored because
         */
        final Runnable attributesUpdateRunnable = () -> {
            final DatacenterBroker broker = cloudlet.getBroker();
            final StringBuilder builder = new StringBuilder();
            /* The output size doesn't always have a relation with file size.
             * This way, if the file size is changed, we don't change
             * the output size. This may be performed by the researcher if he/she needs.*/
            if(clone.getPriority() != cloudlet.getPriority()){
                builder.append("priority: ")
                    .append(cloudlet.getPriority()).append(VAL_SEPARATOR)
                    .append(clone.getPriority()).append(COL_SEPARATOR);
                cloudlet.setFileSize(clone.getFileSize());
            }

            if(clone.getNumberOfPes() != cloudlet.getNumberOfPes()){
                builder.append("PEs: ")
                    .append(cloudlet.getNumberOfPes()).append(VAL_SEPARATOR)
                    .append(clone.getNumberOfPes()).append(COL_SEPARATOR);
                cloudlet.setNumberOfPes(clone.getNumberOfPes());
            }

            //It's ensured when creating the Cloudlet that a UtilizationModelDynamic is used for RAM
            final UtilizationModelDynamic cloneRamUM = (UtilizationModelDynamic)clone.getUtilizationModelRam();
            final UtilizationModelDynamic cloudletRamUM = (UtilizationModelDynamic)cloudlet.getUtilizationModelRam();
            if(cloneRamUM.getMaxResourceUtilization() != cloudletRamUM.getMaxResourceUtilization()){
                builder.append("Max RAM Usage: ")
                    .append(formatPercentValue(cloudletRamUM.getMaxResourceUtilization())).append(VAL_SEPARATOR)
                    .append(formatPercentValue(cloneRamUM.getMaxResourceUtilization())).append("% | ");
                cloudletRamUM.setMaxResourceUtilization(cloneRamUM.getMaxResourceUtilization());
            }

            /* We don't need to check if some Cloudlet attribute was changed because
             * if this Runnable is executed is because something was.
             * An event to execute such Runnable is just sent in such a condition.*/
            broker.LOGGER.trace("{}: {}: {} attributes updated: {}", getSimulation().clockStr(), broker.getName(), cloudlet, builder);
        };

        /* The Runnable is the data of the event that is sent to the broker.
         * This way, it will be executed only when the event is processed.*/
        final CloudSimEvent attrsChangeSimEvt =
            new CloudSimEvent(
                taskEvent.getTimestamp(),
                statusChangeSimEvt.getDestination(),
                CloudSimTag.CLOUDLET_UPDATE_ATTRIBUTES, attributesUpdateRunnable);

        //Sends the event to change the Cloudlet attributes
        addEventToSend(cloudlet, attrsChangeSimEvt);

        return cloudlet;
    }

    /**
     * Adds a Cloudlet event to the map of events to send
     * @param cloudlet
     * @param evt
     */
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
        return cloudlet2.getFileSize() != cloudlet1.getFileSize() ||
               cloudlet2.getNumberOfPes() != cloudlet1.getNumberOfPes() ||
               cloudlet2.getUtilizationOfCpu() != cloudlet1.getUtilizationOfCpu() ||
               cloudlet2.getUtilizationOfRam() != cloudlet1.getUtilizationOfRam();
    }

    /**
     * Gets a {@link Function} that will be called for every {@link Cloudlet} to be created
     * from a line inside the trace file.
     * @return
     * @see #setCloudletCreationFunction(Function)
     */
    protected Function<TaskEvent, Cloudlet> getCloudletCreationFunction() {
        return cloudletCreationFunction;
    }

    /**
     * Sets a {@link Function} that will be called for every {@link Cloudlet} to be created
     * from a line inside the trace file.
     * The {@link Function} will receive a {@link TaskEvent} object containing
     * the task data read from the trace and should the created Cloudlet.
     * The provided function must instantiate the Host and defines Host's CPU cores and RAM
     * capacity according the received parameters.
     * For other Hosts configurations (such as storage capacity), the provided
     * function must define the value as desired, since the trace file
     * doesn't have any other information for such resources.
     *
     * @param cloudletCreationFunction the {@link Function} to set
     */
    public void setCloudletCreationFunction(final Function<TaskEvent, Cloudlet> cloudletCreationFunction) {
        this.cloudletCreationFunction = requireNonNull(cloudletCreationFunction);
    }

    public CloudSim getSimulation() {
        return simulation;
    }

    /**
     * Gets the maximum number of Cloudlets to create from the trace file.
     * @return
     * @see #setMaxCloudletsToCreate(int)
     */
    public int getMaxCloudletsToCreate() {
        return maxCloudletsToCreate;
    }

    /**
     * Sets the maximum number of Cloudlets to create from the trace file.
     * Since the number of lines in the file may be greater to the number
     * of Cloudlets that will be created from it (since there may be lines
     * representing different cloudlet requests, such as creation, execution, pause, destruction, etc),
     * using the {@link #setMaxLinesToRead(int)} may not ensure only a given number of
     * Cloudlets will be created.
     * @param maxCloudletsToCreate the maximum number of Cloudlets to create from the file.
     *                             Use {@link Integer#MAX_VALUE} to disable this configuration.
     * @return
     * @see #setMaxLinesToRead(int)
     */
    public GoogleTaskEventsTraceReader setMaxCloudletsToCreate(final int maxCloudletsToCreate) {
        if(maxCloudletsToCreate <= 0) {
            throw new IllegalArgumentException("Maximum number of Cloudlets to create must be greater than 0. If you want to create all Cloudlets from the entire file, provide Integer.MAX_VALUE.");
        }

        this.maxCloudletsToCreate = maxCloudletsToCreate;
        return this;
    }

    /**
     * Checks if the maximum number of Cloudlets to create was not reached.
     * @return true to indicate the Cloudlet is allowed to be created, false otherwise.
     */
    protected boolean allowCloudletCreation() {
        return availableObjectsCount() < getMaxCloudletsToCreate();
    }

    /**
     * Checks if Cloudlets will be auto-submitted to the broker after created
     * (default is true).
     * @return true if auto-submit is enabled, false otherwise
     */
    public boolean isAutoSubmitCloudlets() {
        return autoSubmitCloudlets;
    }

    /**
     * Sets if Cloudlets must be auto-submitted to the broker after created
     * (default is true).
     * @param autoSubmitCloudlets true to auto-submit, false otherwise
     */
    public GoogleTaskEventsTraceReader setAutoSubmitCloudlets(final boolean autoSubmitCloudlets) {
        this.autoSubmitCloudlets = autoSubmitCloudlets;
        return this;
    }

    /**
     * Gets the manager that creates and provide access to {@link DatacenterBroker}s used by
     * the trace reader.
     * @return
     */
    public BrokerManager getBrokerManager() {
        return brokerManager;
    }
}
