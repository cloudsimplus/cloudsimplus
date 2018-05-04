/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import java.util.*;
import java.util.stream.Stream;

import org.cloudbus.cloudsim.core.events.*;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.util.Log;
import java.util.function.Predicate;

import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

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
    public static final String VERSION = "2.3.0";

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
     * The last time the OnClockTickListeners were updated.
     * @see #addOnClockTickListener(EventListener)
     */
    private double lastTimeClockTickListenersUpdated;

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
    private final Set<EventListener<EventInfo>> onSimulationPausedListeners;
    private final Set<EventListener<EventInfo>> onClockTickListeners;

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
        this.onSimulationPausedListeners = new HashSet<>();
        this.onClockTickListeners = new HashSet<>();

        // NOTE: the order for the lines below is important
        this.calendar = Calendar.getInstance();
        this.cis = new CloudInformationService(this);

        if (minTimeBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is:" + minTimeBetweenEvents);
        }

        this.minTimeBetweenEvents = minTimeBetweenEvents;

        this.lastTimeClockTickListenersUpdated = minTimeBetweenEvents;
        this.circularClockTimeQueue = new double[]{minTimeBetweenEvents, minTimeBetweenEvents};
    }

    @Override
    public double start() {
        if(alreadyRunOnce){
            throw new UnsupportedOperationException(
                "You can't run a simulation that has already run previously. If you've paused the simulation and want to resume it, you should call resume().");
        }

        Log.printConcatLine("Starting CloudSim Plus ", VERSION);
        startEntitiesIfNotRunning();
        this.alreadyRunOnce = true;

        while (running) {
            runClockTickAndProcessFutureEventQueue();
            if(abortRequested){
                printMessage("\n================== Simulation aborted under request at time "+ clock +" ==================");
                return clock;
            }

            if (terminateSimulationUnderRequest()) {
                break;
            }

            checkIfSimulationPauseRequested();
        }

        finishSimulation();
        printSimulationFinished();

        return clock;
    }

    private void printSimulationFinished() {
        final String msg1 = "Simulation finished at time "+ clock;
        final String extra = future.isEmpty() ? "" : ", before completing,";
        final String msg2 = isTimeToTerminateSimulationUnderRequest()
                                ? extra + " in reason of an explicit request to terminate() or terminateAt()"
                                : "";

        Log.printFormattedLine("\n================== %s%s ==================\n", msg1, msg2);
    }

    private boolean isTimeToTerminateSimulationUnderRequest() {
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
    public void addEntity(final CloudSimEntity e) {
        if (running) {
            //@todo src 1, dest 0? What did it mean? Probably nothing.
            final SimEvent evt = new CloudSimEvent(this, SimEvent.Type.CREATE, clock, e);
            future.addEvent(evt);
        }

        if (e.getId() == -1) { // Only add once!
            e.setId(entities.size());
            entities.add(e);
        }
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future future event queue}.
     */
    private void runClockTickAndProcessFutureEventQueue() {
        executeRunnableEntities();
        if (!future.isEmpty()) {
            future.stream().findFirst().ifPresent(this::processFutureEventsHappeningAtSameTimeOfTheFirstOne);
            return;
        }

        if(isTerminationTimeSet()){
            Log.printFormattedLine(
                "%.2f: Simulation: Waiting more events or the clock to reach %.2f (the termination time set).",
                clock, terminationTime);
            final double increment = minDatacentersSchedulingInterval();
            final String info = increment == minTimeBetweenEvents
                                                ? "using getMinTimeBetweenEvents() since a Datacenter schedulingInterval was not set"
                                                : "Datacenter.getSchedulingInterval()";
            Log.printFormattedLine("       Checking new events in %.2f seconds (%s)", increment, info);
            setClock(clock + increment);
        }
        else {
            running = false;
            printMessage("Simulation: No more future events");
        }
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

        final List<SimEvent> eventsToProcess = future.stream()
            .filter(e -> e.eventTime() == firstEvent.eventTime())
            .collect(toList());

        for(final SimEvent evt: eventsToProcess) {
            processEvent(evt);
            future.remove(evt);
        }
    }

    /**
     * Gets the list of entities that are in {@link SimEntity.State#RUNNABLE}
     * and execute them.
     */
    private void executeRunnableEntities() {
        final List<SimEntity> runnableEntities = entities.stream()
                .filter(ent -> ent.getState() == SimEntity.State.RUNNABLE)
                .collect(toList());

        runnableEntities.forEach(SimEntity::run);
    }

    @Override
    public void sendNow(final SimEntity src, final SimEntity dest, final int tag, final Object data) {
        send(src, dest, 0, tag, data);
    }

    @Override
    public void send(final SimEntity src, final SimEntity dest, final double delay, final int tag, final Object data) {
        validateDelay(delay);
        final SimEvent evt = new CloudSimEvent(this, SimEvent.Type.SEND, clock + delay, src, dest, tag, data);
        future.addEvent(evt);
    }

    @Override
    public void sendFirst(final SimEntity src, final SimEntity dest, final double delay, final int tag, final Object data) {
        validateDelay(delay);
        final SimEvent evt = new CloudSimEvent(this, SimEvent.Type.SEND, clock + delay, src, dest, tag, data);
        future.addEventFirst(evt);
    }

    private void validateDelay(final double delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Send delay can't be negative.");
        }
    }

    @Override
    public void wait(final CloudSimEntity src, final Predicate<SimEvent> p) {
        src.setState(SimEntity.State.WAITING);
        if (p != ANY_EVT) {
            // If a predicate has been used, store it in order to check incoming events that matches it
            waitPredicates.put(src, p);
        }
    }

    @Override
    public long waiting(final SimEntity dest, final Predicate<SimEvent> p) {
        return filterEventsToDestinationEntity(deferred, p, dest).count();
    }

    @Override
    public SimEvent select(final SimEntity dest, final Predicate<SimEvent> p) {
        final SimEvent evt = findFirstDeferred(dest, p);
        deferred.remove(evt);
        return evt;
    }

    @Override
    public SimEvent findFirstDeferred(final SimEntity dest, final Predicate<SimEvent> p) {
        return filterEventsToDestinationEntity(deferred, p, dest).findFirst().orElse(SimEvent.NULL);
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate
     * and are targeted to an specific entity.
     *
     * @param queue the queue to get the events from
     * @param p the event selection predicate
     * @param dest Id of entity that the event has to be sent to
     * @return a Stream of events from the queue
     */
    private Stream<SimEvent> filterEventsToDestinationEntity(final EventQueue queue, final Predicate<SimEvent> p, final SimEntity dest) {
        return filterEvents(queue, p.and(e -> e.getDestination() == dest));
    }

    @Override
    public SimEvent cancel(final SimEntity src, final Predicate<SimEvent> p) {
        final SimEvent evt = future.stream().filter(p.and(e -> e.getSource().equals(src))).findFirst().orElse(SimEvent.NULL);
        future.remove(evt);
        return evt;
    }

    @Override
    public boolean cancelAll(final SimEntity src, final Predicate<SimEvent> p) {
        final int previousSize = future.size();
        final List<SimEvent> cancelList = filterEventsFromSourceEntity(src, p, future).collect(toList());
        future.removeAll(cancelList);
        return previousSize < future.size();
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate
     * and from a source entity.
     *
     * @param src Id of entity that scheduled the event
     * @param p the event selection predicate
     * @param queue the queue to get the events from
     * @return a Stream of events from the queue
     */
    private Stream<SimEvent> filterEventsFromSourceEntity(final SimEntity src, final Predicate<SimEvent> p, final EventQueue queue) {
        return filterEvents(queue, p.and(e -> e.getSource().equals(src)));
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate.
     *
     * @param queue the queue to get the events from
     * @param p the event selection predicate
     * @return a Strem of events from the queue
     */
    private Stream<SimEvent> filterEvents(final EventQueue queue, final Predicate<SimEvent> p) {
        return queue.stream().filter(p);
    }

    /**
     * Processes an event.
     *
     * @param e the event to be processed
     */
    private void processEvent(final SimEvent e) {
        if (e.eventTime() < clock) {
            throw new IllegalArgumentException("Past event detected.");
        }
        setClock(e.eventTime());

        processEventByType(e);
        notifyOnEventProcessingListeners(e);
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
        if(clock > lastTimeClockTickListenersUpdated) {
            addCurrentTimeToCircularQueue();
            if (circularClockTimeQueue[0] < circularClockTimeQueue[1])
            {
                lastTimeClockTickListenersUpdated = circularClockTimeQueue[0];
                onClockTickListeners.forEach(l -> l.update(EventInfo.of(l, lastTimeClockTickListenersUpdated)));
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

    private void processEventByType(final SimEvent e) {
        switch (e.getType()) {
            case NULL:
                throw new IllegalArgumentException("Event has a null type.");
            case CREATE:
                processCreateEvent(e);
            break;
            case SEND:
                processSendEvent(e);
            break;
            case HOLD_DONE:
                processHoldEvent(e);
            break;
        }
    }

    private void processCreateEvent(final SimEvent e) {
        addEntityDynamically((SimEntity) e.getData());
    }

    /**
     * Internal method used to add a new entity to the simulation when the
     * simulation is running.
     *
     * <b>It should not be called from user simulations.</b>
     *
     * @param e The new entity
     */
    private void addEntityDynamically(final SimEntity e) {
        Objects.requireNonNull(e);
        printMessage("Adding: " + e.getName());
        e.start();
    }

    private void processHoldEvent(final SimEvent e) {
        if (e.getSource() == SimEntity.NULL) {
            throw new IllegalArgumentException("Null entity holding.");
        }

        e.getSource().setState(SimEntity.State.RUNNABLE);
    }

    private void processSendEvent(final SimEvent e) {
        if (e.getDestination() == SimEntity.NULL) {
            throw new IllegalArgumentException("Attempt to send to a null entity detected.");
        }

        final CloudSimEntity destEnt = entities.get(e.getDestination().getId());
        if (destEnt.getState() == SimEntity.State.WAITING) {
            final Predicate<SimEvent> p = waitPredicates.get(destEnt);
            if (p == null || e.getTag() == 9999 || p.test(e)) {
                destEnt.setEventBuffer(new CloudSimEvent(e));
                destEnt.setState(SimEntity.State.RUNNABLE);
                waitPredicates.remove(destEnt);
            } else {
                deferred.addEvent(e);
            }

            return;
        }

        deferred.addEvent(e);
    }

    /**
     * Notifies all registered listeners when a {@link SimEvent} is processed by the simulation.
     * @param e the processed event
     */
    private void notifyOnEventProcessingListeners(final SimEvent e) {
        onEventProcessingListeners.forEach(l -> l.update(e));
    }

    private void startEntitiesIfNotRunning() {
        if (running) {
            return;
        }

        running = true;
        entities.forEach(SimEntity::start);
        printMessage("Entities started.");
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
        final SimEvent evt = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(evt);
        src.setState(SimEntity.State.HOLDING);
    }

    @Override
    public void holdEntity(final SimEntity src, final long delay) {
        final SimEvent evt = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(evt);
        src.setState(SimEntity.State.HOLDING);
    }

    private boolean terminateSimulationUnderRequest() {
        if(!isTimeToTerminateSimulationUnderRequest()){
            return false;
        }

        terminate();
        setClock(terminationTime);
        return true;
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
            notifyOnSimulationPausedListeners();
            return true;
        }

        return false;
    }

    /**
     * Notifies all registered listeners when the simulation is paused.
     */
    private void notifyOnSimulationPausedListeners() {
        onSimulationPausedListeners.forEach(l -> l.update(EventInfo.of(l, clock)));
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

    private boolean isTerminationTimeSet() {
        return terminationTime > 0.0;
    }

    private boolean isNextFutureEventHappeningAfterTimeToPause() {
        return future.iterator().next().eventTime() >= pauseAt;
    }

    /**
     * Finishes execution of running entities before terminating the simulation.
     */
    private void finishSimulation() {
        // Allow all entities to exit their body method
        if (!abortRequested) {
            entities.stream()
                .filter(e -> e.getState() != SimEntity.State.FINISHED)
                .forEach(SimEntity::run);
        }

        entities.forEach(SimEntity::shutdownEntity);
        running = false;
    }

    @Override
    public void abort() {
        abortRequested = true;
    }

    /**
     * Prints a message about the progress of the simulation.
     *
     * @param message the message
     */
    private void printMessage(final String message) {
        Log.printLine(message);
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public final Simulation addOnSimulationPausedListener(final EventListener<EventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onSimulationPausedListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnSimulationPausedListener(final EventListener<EventInfo> listener) {
        return this.onSimulationPausedListeners.remove(listener);
    }

    @Override
    public final Simulation addOnEventProcessingListener(final EventListener<SimEvent> listener) {
        Objects.requireNonNull(listener);
        this.onEventProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnEventProcessingListener(final EventListener<SimEvent> listener) {
        return onEventProcessingListeners.remove(listener);
    }

    @Override
    public Simulation addOnClockTickListener(final EventListener<EventInfo> listener) {
        Objects.requireNonNull(listener);
        onClockTickListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnClockTickListener(final EventListener<? extends EventInfo> listener) {
        Objects.requireNonNull(listener);
        return onClockTickListeners.remove(listener);
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
