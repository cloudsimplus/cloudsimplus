/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.*;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * The main class of the simulation API, that manages Cloud Computing simulations providing all methods to
 * start, pause and stop them. It sends and processes all discrete events during the simulation time.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class CloudSim implements Simulation {
    /**
     * CloudSim Plus current version.
     */
    public static final String VERSION = "4.0.8";

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudSim.class.getSimpleName());

    /**
     * An array that works as a circular queue with capacity for just 2 elements
     * (defined in the constructor). When a new element is added to the queue,
     * the first element is removed to open space for that new one.
     * This queue stores the last 2 simulation clock values.
     * It is used to know when it's time to notify listeners that
     * the simulation clock has incremented.
     *
     * <p>The head (value at index 0) of the queue is the oldest simulation time stored,
     * the tail (value at index 1) is the newest one.</p>
     *
     * <p>Such a structure is required because multiple events
     * can be received consecutively for the same simulation time.
     * When the head of the queue is lower than the tail,
     * it means the last event for that head time
     * was already processed and a more recent event
     * has just arrived.
     * </p>
     *
     * @see #notifyOnClockTickListenersIfClockChanged()
     */
    private final double[] circularClockTimeQueue;

    /**
     * The last time OnClockTickListeners were updated.
     * @see #addOnClockTickListener(EventListener)
     */
    private double lastClockTickListenersUpdate;

    /**
     * @see #getNetworkTopology()
     */
    private NetworkTopology networkTopology;

    /**
     * The Cloud Information Service (CIS) entity.
     */
    private final CloudInformationService cis;

    private final Calendar calendar;

    /**
     * The time the simulation should be terminated (in seconds).
     */
    private double terminationTime = -1;

    /**
     * @see #getMinTimeBetweenEvents()
     */
    private final double minTimeBetweenEvents;

    /**
     * @see #getEntityList()
     */
    private final List<CloudSimEntity> entities;

    /**
     * The queue of events that will be sent in a future simulation time.
     */
    private final FutureQueue future;

    /**
     * The deferred event queue.
     */
    private final DeferredQueue deferred;

    /**
     * @see #clock()
     */
    private double clock;

    /**
     * @see #isRunning()
     */
    private boolean running;

    /**
     * A map of entities and predicates that define the events a given entity is waiting for.
     * Received events are filtered based on the predicate associated with each entity
     * so that just the resulting events are sent to the entity.
     */
    private final Map<SimEntity, Predicate<SimEvent>> waitPredicates;

    /**
     * @see #isPaused()
     */
    private boolean paused;

    /**
     * Indicates the time that the simulation has to be paused.
     * -1 means no pause was requested.
     */
    private double pauseAt = -1;

    /**
     * Indicates if an abrupt termination was requested.
     * @see #abort()
     */
    private boolean abortRequested;

    /**
     * Indicates if the simulation already run once.
     * If yes, it can't run again.
     * If you paused the simulation and wants to resume it,
     * you must use {@link #resume()} instead of {@link #start()}.
     */
    private boolean alreadyRunOnce;

    private final Set<EventListener<SimEvent>> onEventProcessingListeners;
    private final Set<EventListener<EventInfo>> onSimulationPauseListeners;
    private final Set<EventListener<EventInfo>> onClockTickListeners;
    private final Set<EventListener<EventInfo>> onSimulationStartListeners;

    /**
     * Creates a CloudSim simulation.
     * Internally it creates a CloudInformationService.
     *
     * @see CloudInformationService
     * @see #CloudSim(double)
     */
    public CloudSim(){
        this(0.1);
    }

    /**
     * Creates a CloudSim simulation that tracks events happening in a time interval
     * as little as the minTimeBetweenEvents parameter.
     * Internally it creates a {@link CloudInformationService}.
     *
     * @param minTimeBetweenEvents the minimal period between events. Events
     * within shorter periods after the last event are discarded.
     * @see CloudInformationService
     */
    public CloudSim(final double minTimeBetweenEvents) {
        this.entities = new ArrayList<>();
        this.future = new FutureQueue();
        this.deferred = new DeferredQueue();
        this.waitPredicates = new HashMap<>();
        this.networkTopology = NetworkTopology.NULL;
        this.clock = 0;
        this.running = false;
        this.alreadyRunOnce = false;
        this.onEventProcessingListeners = new HashSet<>();
        this.onSimulationPauseListeners = new HashSet<>();
        this.onClockTickListeners = new HashSet<>();
        this.onSimulationStartListeners = new HashSet<>();

        // NOTE: the order for the lines below is important
        this.calendar = Calendar.getInstance();
        this.cis = new CloudInformationService(this);

        if (minTimeBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is: " + minTimeBetweenEvents);
        }

        this.minTimeBetweenEvents = minTimeBetweenEvents;

        this.lastClockTickListenersUpdate = minTimeBetweenEvents;
        this.circularClockTimeQueue = new double[]{minTimeBetweenEvents, minTimeBetweenEvents};
    }

    @Override
    public double start() {
        if(alreadyRunOnce){
            throw new UnsupportedOperationException(
                "You can't run a simulation that has already run previously. " +
                "If you've paused the simulation and want to resume it, call the resume() method.");
        }

        LOGGER.info("Starting CloudSim Plus {}", VERSION);
        startEntitiesIfNotRunning();
        this.alreadyRunOnce = true;

        if(!eventLoop()){
            return clock;
        }

        notifyEndOfSimulationToEntities();
        running = false;
        LOGGER.info("Simulation: No more future events{}", System.lineSeparator());

        finishSimulation();
        printSimulationFinished();

        return clock;
    }

    private void notifyOnSimulationStartListeners() {
        if(!onSimulationStartListeners.isEmpty() && clock > 0) {
            notifyEventListeners(onSimulationStartListeners, clock);
            //Since the simulation starts just once, clear the listeners to avoid them to be notified again
            onSimulationStartListeners.clear();
        }
    }

    private void notifyEventListeners(Set<EventListener<EventInfo>> onSimulationStartListeners, double clock) {
        onSimulationStartListeners.forEach(listener -> listener.update(EventInfo.of(listener, clock)));
    }

    /**
     * Keeps looking for new events to process, until the simulation naturally finishes,
     * is requested to finish at a given time or is aborted.
     *
     * @return true if the simulation was finished due to its natural end or
     *         under request to finish at a specific time;
     *         false if it was aborted.
     */
    private boolean eventLoop() {
        while (runClockTickAndProcessFutureEvents() || waitSimulationClockToReachTerminationTime()) {
            notifyOnSimulationStartListeners(); //it's ensured to run just once.
            if(abortRequested){
                LOGGER.info("{}================== Simulation aborted under request at time {} ==================", System.lineSeparator(), clock);
                return false;
            }

            if (isTimeToTerminateSimulationUnderRequest()) {
                setClock(terminationTime);
                return true;
            }

            checkIfSimulationPauseRequested();
        }

        return true;
    }

    /**
     * Notifies entities that simulation is ending,
     * enabling them to send and process their last events.
     * Then, waits such events to be received and processed.
     */
    private void notifyEndOfSimulationToEntities() {
        entities.stream()
            .filter(CloudSimEntity::isAlive)
            .forEach(e -> sendNow(e, CloudSimTags.END_OF_SIMULATION));
        LOGGER.info("{}: Processing last events before simulation shutdown.", clock);

        while (true) {
            if(!runClockTickAndProcessFutureEvents()){
                return;
            }
        }
    }

    private void printSimulationFinished() {
        final String msg1 = String.format("Simulation finished at time %.2f", clock);
        final String extra = future.isEmpty() ? "" : ", before completing,";
        final String msg2 = isTimeToTerminateSimulationUnderRequest()
                                ? extra + " in reason of an explicit request to terminate() or terminateAt()"
                                : "";

        LOGGER.info("{}================== {}{} =================={}", System.lineSeparator(), msg1, msg2, System.lineSeparator());
    }

    @Override
    public boolean isTimeToTerminateSimulationUnderRequest() {
        return isTerminationTimeSet() && clock >= terminationTime;
    }

    @Override
    public boolean terminate() {
        if(running) {
            running = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean terminateAt(final double time) {
        if (time <= clock) {
            return false;
        }

        terminationTime = time;
        return true;
    }

    @Override
    public double getMinTimeBetweenEvents() {
        return minTimeBetweenEvents;
    }

    @Override
    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public CloudInformationService getCloudInfoService() {
        return cis;
    }

    @Override
    public Set<Datacenter> getDatacenterList() {
        return cis.getDatacenterList();
    }

    @Override
    public double clock() {
        return clock;
    }

    @Override
    public double clockInMinutes() {
        return clock()/60.0;
    }

    @Override
    public double clockInHours() {
        return clock()/3600.0;
    }

    /**
     * Updates the simulation clock and notify listeners
     * if the clock has changed.
     * @param newTime simulation time to set
     * @return the old simulation time
     *
     * @see #onClockTickListeners
     */
    private double setClock(final double newTime){
        final double oldTime = clock;
        this.clock = newTime;

        notifyOnClockTickListenersIfClockChanged();
        return oldTime;
    }

    @Override
    public int getNumEntities() {
        return entities.size();
    }

    @Override
    public List<SimEntity> getEntityList() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void addEntity(final CloudSimEntity entity) {
        requireNonNull(entity);
        if (running) {
            final SimEvent evt = new CloudSimEvent(SimEvent.Type.CREATE, 0, entity, null, -1, entity);
            future.addEvent(evt);
        }

        if (entity.getId() == -1) { // Only add once!
            entity.setId(entities.size());
            entities.add(entity);
        }
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future future event queue}.
     * @return true if some event was processed, false otherwise
     */
    private boolean runClockTickAndProcessFutureEvents() {
        executeRunnableEntities();
        if (!future.isEmpty()) {
            future.stream().findFirst().ifPresent(this::processFutureEventsHappeningAtSameTimeOfTheFirstOne);
            return true;
        }

        return false;
    }

    private boolean waitSimulationClockToReachTerminationTime() {
        if(isTerminationTimeSet()){
            final double increment = minDatacentersSchedulingInterval();
            final String info = increment == minTimeBetweenEvents
                ? "using getMinTimeBetweenEvents() since a Datacenter schedulingInterval was not set"
                : "Datacenter.getSchedulingInterval()";

            LOGGER.info(
                "{}: Simulation: Waiting more events or the clock to reach {} (the termination time set).{}Checking new events in {} seconds ({})",
                clock, terminationTime, System.lineSeparator(),
                increment, info);
            setClock(clock + increment);
            return true;
        }
        return false;
    }

    /**
     * Gets the minimum {@link Datacenter#getSchedulingInterval()} defined
     * among all existing Datacenters.
     *
     * @return the minimum {@link Datacenter#getSchedulingInterval()}
     *         between all Datacenters or {@link #getMinTimeBetweenEvents()}
     *         in case no Datacenter has its scheduling interval set
     */
    private double minDatacentersSchedulingInterval() {
        return cis
                .getDatacenterList()
                .stream()
                .mapToDouble(Datacenter::getSchedulingInterval)
                .filter(interval -> interval > 0)
                .min().orElse(minTimeBetweenEvents);
    }

    private void processFutureEventsHappeningAtSameTimeOfTheFirstOne(final SimEvent firstEvent) {
        processEvent(firstEvent);
        future.remove(firstEvent);

        //Uses iterator to increase efficiency and avoid ConcurrentModificationException while removing
        for(final Iterator<SimEvent> it = future.iterator(); it.hasNext();){
            SimEvent evt = it.next();
            if(evt.getTime() == firstEvent.getTime()){
                processEvent(evt);
                it.remove();
            }
        }
    }

    /**
     * Gets the list of entities that are in {@link SimEntity.State#RUNNABLE}
     * and execute them.
     */
    private void executeRunnableEntities() {
        /*Uses an indexed for instead of anything else to avoid
        ConcurrencyModificationException when a HostFaultInjection is created inside a Datacenter*/
        for (int i = 0; i < entities.size(); i++) {
            CloudSimEntity ent = entities.get(i);
            if (ent.getState() == SimEntity.State.RUNNABLE) {
                ent.run();
            }
        }
    }

    private void sendNow(final SimEntity dest, final int tag) {
        sendNow(cis, dest, tag, null);
    }

    @Override
    public void sendNow(final SimEntity src, final SimEntity dest, final int tag, final Object data) {
        send(src, dest, 0, tag, data);
    }

    @Override
    public void send(final SimEntity src, final SimEntity dest, final double delay, final int tag, final Object data) {
        send(new CloudSimEvent(SimEvent.Type.SEND, delay, src, dest, tag, data));
    }

    @Override
    public void send(final SimEvent evt) {
        requireNonNull(evt);
        //Events with a negative tag have higher priority (except the "end of the simulation" event)
        if(evt.getTag() < 0 && evt.getTag() != CloudSimTags.END_OF_SIMULATION)
            future.addEventFirst(evt);
        else future.addEvent(evt);
    }

    @Override
    public void sendFirst(final SimEntity src, final SimEntity dest, final double delay, final int tag, final Object data) {
        sendFirst(new CloudSimEvent(SimEvent.Type.SEND, delay, src, dest, tag, data));
    }

    @Override
    public void sendFirst(SimEvent evt) {
        future.addEventFirst(evt);
    }

    @Override
    public void wait(final CloudSimEntity src, final Predicate<SimEvent> predicate) {
        src.setState(SimEntity.State.WAITING);
        if (predicate != ANY_EVT) {
            // If a predicate has been used, store it in order to check incoming events that matches it
            waitPredicates.put(src, predicate);
        }
    }

    @Override
    public long waiting(final SimEntity dest, final Predicate<SimEvent> predicate) {
        return filterEventsToDestinationEntity(deferred, predicate, dest).count();
    }

    @Override
    public SimEvent select(final SimEntity dest, final Predicate<SimEvent> predicate) {
        final SimEvent evt = findFirstDeferred(dest, predicate);
        deferred.remove(evt);
        return evt;
    }

    @Override
    public SimEvent findFirstDeferred(final SimEntity dest, final Predicate<SimEvent> predicate) {
        return filterEventsToDestinationEntity(deferred, predicate, dest).findFirst().orElse(SimEvent.NULL);
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate
     * and are targeted to an specific entity.
     *
     * @param queue the queue to get the events from
     * @param predicate the event selection predicate
     * @param dest Id of entity that the event has to be sent to
     * @return a Stream of events from the queue
     */
    private Stream<SimEvent> filterEventsToDestinationEntity(final EventQueue queue, final Predicate<SimEvent> predicate, final SimEntity dest) {
        return filterEvents(queue, predicate.and(evt -> evt.getDestination() == dest));
    }

    @Override
    public SimEvent cancel(final SimEntity src, final Predicate<SimEvent> predicate) {
        final SimEvent canceled =
                future.stream()
                      .filter(isEventSourceEqualsTo(predicate, src))
                      .findFirst()
                      .orElse(SimEvent.NULL);
        future.remove(canceled);
        return canceled;
    }

    @Override
    public boolean cancelAll(final SimEntity src, final Predicate<SimEvent> predicate) {
        final int previousSize = future.size();
        future.removeIf(isEventSourceEqualsTo(predicate, src));
        return previousSize < future.size();
    }

    private Predicate<SimEvent> isEventSourceEqualsTo(final Predicate<SimEvent> predicate, final SimEntity src) {
        return predicate.and(evt -> evt.getSource().equals(src));
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate.
     *
     * @param queue the queue to get the events from
     * @param predicate the event selection predicate
     * @return a Stream of events from the queue
     */
    private Stream<SimEvent> filterEvents(final EventQueue queue, final Predicate<SimEvent> predicate) {
        return queue.stream().filter(predicate);
    }

    /**
     * Processes an event.
     *
     * @param evt the event to be processed
     */
    private void processEvent(final SimEvent evt) {
        if (evt.getTime() < clock) {
            throw new IllegalArgumentException("Past event detected.");
        }
        setClock(evt.getTime());

        processEventByType(evt);
        onEventProcessingListeners.forEach(listener -> listener.update(evt));
    }

    /**
     * Notifies all Listeners about onClockTick event when the simulation clock changes.
     * If multiple events are received consecutively but for the same simulation time,
     * it will only notify the Listeners when the last event for that time is received.
     * It ensures when Listeners receive the notification, all the events
     * for such a simulation time were already processed and then,
     * the Listeners will have access to the most updated simulation state.
     */
    private void notifyOnClockTickListenersIfClockChanged() {
        if(clock > lastClockTickListenersUpdate) {
            addCurrentTimeToCircularQueue();
            if (circularClockTimeQueue[0] < circularClockTimeQueue[1])
            {
                lastClockTickListenersUpdate = circularClockTimeQueue[0];
                notifyEventListeners(onClockTickListeners, lastClockTickListenersUpdate);
            }
        }
    }

    /**
     * Makes the circular queue to rotate, removing the first time,
     * then adding the current clock time.
     */
    private void addCurrentTimeToCircularQueue() {
        circularClockTimeQueue[0] = circularClockTimeQueue[1];
        circularClockTimeQueue[1] = clock;
    }

    private void processEventByType(final SimEvent evt) {
        switch (evt.getType()) {
            case NULL:
                throw new IllegalArgumentException("Event has a null type.");
            case CREATE:
                processCreateEvent(evt);
            break;
            case SEND:
                processSendEvent(evt);
            break;
            case HOLD_DONE:
                processHoldEvent(evt);
            break;
        }
    }

    private void processCreateEvent(final SimEvent evt) {
        addEntityDynamically((SimEntity) evt.getData());
    }

    /**
     * Internal method used to add a new entity to the simulation when the
     * simulation is running.
     *
     * <b>It should not be called from user simulations.</b>
     *
     * @param entity The new entity
     */
    private void addEntityDynamically(final SimEntity entity) {
        requireNonNull(entity);
        LOGGER.trace("Adding: {}", entity.getName());
        entity.start();
    }

    private void processHoldEvent(final SimEvent evt) {
        if (evt.getSource() == SimEntity.NULL) {
            throw new IllegalArgumentException("Null entity holding.");
        }

        evt.getSource().setState(SimEntity.State.RUNNABLE);
    }

    private void processSendEvent(final SimEvent evt) {
        if (evt.getDestination() == SimEntity.NULL) {
            throw new IllegalArgumentException("Attempt to send to a null entity detected.");
        }

        final CloudSimEntity destEnt = (CloudSimEntity)evt.getDestination();
        if (destEnt.getState() == SimEntity.State.WAITING) {
            final Predicate<SimEvent> p = waitPredicates.get(destEnt);
            if (p == null || evt.getTag() == 9999 || p.test(evt)) {
                destEnt.setEventBuffer(new CloudSimEvent(evt));
                destEnt.setState(SimEntity.State.RUNNABLE);
                waitPredicates.remove(destEnt);
            } else {
                deferred.addEvent(evt);
            }

            return;
        }

        deferred.addEvent(evt);
    }

    private void startEntitiesIfNotRunning() {
        if (running) {
            return;
        }

        running = true;
        entities.forEach(SimEntity::start);
        LOGGER.info("Entities started.");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean pause() {
        return pause(clock);
    }

    @Override
    public boolean pause(final double time) {
        if (time < clock) {
            return false;
        } else {
            pauseAt = time;
            return true;
        }
    }

    @Override
    public boolean resume() {
        final boolean wasPaused = this.paused;
        this.paused = false;

        if (pauseAt <= clock) {
            pauseAt = -1;
        }

        return wasPaused;
    }

    @Override
    public void pauseEntity(final SimEntity src, final double delay) {
        final SimEvent evt = new CloudSimEvent(SimEvent.Type.HOLD_DONE, delay, src);
        addHoldingFutureEvent(src, evt);
    }

    private void addHoldingFutureEvent(SimEntity src, SimEvent evt) {
        future.addEvent(evt);
        src.setState(SimEntity.State.HOLDING);
    }

    @Override
    public void holdEntity(final SimEntity src, final long delay) {
        final SimEvent evt = new CloudSimEvent(SimEvent.Type.HOLD_DONE, delay, src);
        addHoldingFutureEvent(src, evt);
    }

    private void checkIfSimulationPauseRequested() {
        if((isThereFutureEvtsAndNextOneHappensAfterTimeToPause() || isNotThereNextFutureEvtsAndIsTimeToPause()) && doPause()) {
            waitsForSimulationToBeResumedIfPaused();
        }
    }

    /**
     * Effectively pauses the simulation after an pause request.
     * @return true if the simulation was paused (the simulation is running and was not paused yet), false otherwise
     * @see #pause()
     * @see #pause(double)
     */
    private boolean doPause() {
        if(running && isPauseRequested()) {
            paused=true;
            setClock(pauseAt);
            notifyEventListeners(onSimulationPauseListeners, clock);
            return true;
        }

        return false;
    }

    private boolean isPauseRequested() {
        return pauseAt > -1;
    }

    private void waitsForSimulationToBeResumedIfPaused() {
        while (paused) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        pauseAt = -1;
    }

    @Override
    public long getNumberOfFutureEvents(final Predicate<SimEvent> predicate){
        return future.stream()
                .filter(predicate)
                .count();
    }

    private boolean isThereFutureEvtsAndNextOneHappensAfterTimeToPause() {
        return !future.isEmpty() && clock <= pauseAt && isNextFutureEventHappeningAfterTimeToPause();
    }

    private boolean isNotThereNextFutureEvtsAndIsTimeToPause() {
        return future.isEmpty() && clock >= pauseAt;
    }

    @Override
    public boolean isTerminationTimeSet() {
        return terminationTime > 0.0;
    }

    private boolean isNextFutureEventHappeningAfterTimeToPause() {
        return future.iterator().next().getTime() >= pauseAt;
    }

    /**
     * Finishes execution of running entities before terminating the simulation.
     */
    private void finishSimulation() {
        final List<SimEntity> entitiesAlive = entities.stream().filter(CloudSimEntity::isAlive).collect(toList());
        // Allow all entities to exit their body method
        if (!abortRequested) {
            entitiesAlive.forEach(SimEntity::run);
        }

        entitiesAlive.forEach(SimEntity::shutdownEntity);
        running = false;
    }

    @Override
    public void abort() {
        abortRequested = true;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public final Simulation addOnSimulationPauseListener(final EventListener<EventInfo> listener) {
        this.onSimulationPauseListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public final Simulation addOnSimulationStartListener(final EventListener<EventInfo> listener) {
        this.onSimulationStartListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnSimulationPauseListener(final EventListener<EventInfo> listener) {
        return this.onSimulationPauseListeners.remove(listener);
    }

    @Override
    public final Simulation addOnEventProcessingListener(final EventListener<SimEvent> listener) {
        this.onEventProcessingListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnEventProcessingListener(final EventListener<SimEvent> listener) {
        return onEventProcessingListeners.remove(requireNonNull(listener));
    }

    @Override
    public Simulation addOnClockTickListener(final EventListener<EventInfo> listener) {
        onClockTickListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnClockTickListener(final EventListener<? extends EventInfo> listener) {
        return onClockTickListeners.remove(requireNonNull(listener));
    }

    @Override
    public NetworkTopology getNetworkTopology() {
        return networkTopology;
    }

    @Override
    public void setNetworkTopology(final NetworkTopology networkTopology) {
        this.networkTopology = networkTopology;
    }
}
