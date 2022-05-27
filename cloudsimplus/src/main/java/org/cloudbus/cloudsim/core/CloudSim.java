/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.events.*;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.util.Util;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.cloudbus.cloudsim.util.TimeUtil.secondsToStr;

/**
 * The main class of the simulation API, that manages Cloud Computing simulations,
 * providing all methods to start, pause and stop them.
 * It sends and processes all discrete events during the simulation time.
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
    public static final String VERSION = "CloudSim Plus 7.3.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(CloudSim.class.getSimpleName());

    private final CircularTimeQueue clockQueue;

    /** @see #getNetworkTopology() */
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

    private double lastCloudletProcessingUpdate;

    /**
     * The time the simulation is really expected to finish.
     * This value is just used when the {@link #terminationTime} is set.
     * In such a case, the researcher has defined the time he/she wants
     * the simulation to finish. However, the simulation may keep
     * running for a short period after this time to ensure crucial
     * events are processed (such as the finalization of Cloudlets).
     * For instance, when {@link Cloudlet#getLength()} is negative
     * they keep running until certain events happens
     * (check the mentioned method documentation for details).
     */
    private double newTerminationTime = -1;

    /** @see #getMinTimeBetweenEvents() */
    private final double minTimeBetweenEvents;

    /** @see #getEntityList() */
    private final List<CloudSimEntity> entities;

    /**
     * The queue of events that will be sent in a future simulation time.
     */
    private final FutureQueue future;

    /**
     * The deferred event queue.
     */
    private final DeferredQueue deferred;

    /** @see #clock() */
    private double clock;

    /** @see #isRunning() */
    private boolean running;

    /**
     * A map of entities and predicates that define the events a given entity is waiting for.
     * Received events are filtered based on the predicate associated with each entity
     * so that just the resulting events are sent to the entity.
     */
    private final Map<SimEntity, Predicate<SimEvent>> waitPredicates;

    /** @see #isPaused() */
    private boolean paused;

    /**
     * Indicates the time that the simulation has to be paused.
     * -1 means no pause was requested.
     */
    private double pauseAt = -1;

    /** @see #isAbortRequested() */
    private boolean abortRequested;

    /** @see #isAborted() */
    private boolean aborted;

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

    private boolean processEventsInParallel;

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
     * @param minTimeBetweenEvents the minimal period between events.
     * Events within shorter periods after the last event are discarded.
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
        this.clockQueue = new CircularTimeQueue(this);
    }

    /**
     * Finishes execution of running entities before terminating the simulation,
     * then cleans up internal state.
     *
     * <b>Note:</b> Should be used only in the <b>synchronous</b> mode
     * (after starting the simulation with {@link #startSync()}).
     */
    private void finish() {
        if(abortRequested){
            return;
        }

        notifyEndOfSimulationToEntities();
        LOGGER.info("Simulation: No more future events{}", System.lineSeparator());

        // Allow all entities to exit their body method
        if (!abortRequested) {
            //Uses indexed loop to avoid ConcurrentModificationException
            for (int i = 0; i < entities.size(); i++) {
                entities.get(i).run();
            }
        }

        shutdownEntities();
        running = false;

        printSimulationFinished();
    }

    /**
     * Shuts down remaining entities before finishing the simulation.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void shutdownEntities() {
        //Uses indexed loop to avoid ConcurrentModificationException
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).shutdown();
        }
    }

    @Override
    public double runFor(final double interval) {
        final double until = interval == Double.MAX_VALUE ? interval : this.clock + interval;

        if(!processEvents(until)){
            /* If no event happening up to the given time, increases the clock
             * so that when the runFor method is called again with the new clock,
             * some events may be processed that time.
             * If some event is processed, the clock is automatically increased.*/
            setClock(until);

            if(future.isEmpty()){
                finish();
            }
        }

        return clock;
    }

    @Override
    public double start() {
        aborted = false;
        startSync();

        while(processEvents(Double.MAX_VALUE)){
            //All the processing happens inside the method called above
        }

        finish();
        return clock;
    }

    @Override
    public void startSync() {
        if(alreadyRunOnce){
            throw new UnsupportedOperationException(
                "You can't run a simulation that has already run previously. " +
                "If you've paused the simulation and want to resume it, call the resume() method.");
        }

        LOGGER.info("{}================== Starting {} =================={}", System.lineSeparator(), VERSION,  System.lineSeparator());
        startEntitiesIfNotRunning();
        this.alreadyRunOnce = true;
    }

    private void notifyOnSimulationStartListeners() {
        if(!onSimulationStartListeners.isEmpty() && clock > 0) {
            notifyEventListeners(onSimulationStartListeners, clock);
            //Since the simulation starts just once, clear the listeners to avoid them to be notified again
            onSimulationStartListeners.clear();
        }
    }

    private void notifyEventListeners(final Set<EventListener<EventInfo>> listeners, final double clock) {
        listeners.forEach(listener -> listener.update(EventInfo.of(listener, clock)));
    }

    /**
     * Process all the events happening up to a given time are processed.
     *
     * @param until The interval for which the events should be processed (in seconds)
     * @return true if some event was processed, false if no event was processed
     *         or a termination time was set and the clock reached that time
     */
    private boolean processEvents(final double until) {
        if (!runClockTickAndProcessFutureEvents(until) && !isToWaitClockToReachTerminationTime()) {
            return false;
        }

        notifyOnSimulationStartListeners(); //it's ensured to run just once.
        if (logSimulationAborted()) {
            return false;
        }

        /* If it's time to terminate the simulation, sets a new termination time
         * so that events to finish Cloudlets with a negative length are received.
         * Cloudlets with a negative length must keep running
         * until a CLOUDLET_FINISH event is sent to the broker or the termination time is reached.
         */
        if (isTimeToTerminateSimulationUnderRequest()) {
            if(newTerminationTime != -1 && clock >= newTerminationTime){
                return false;
            }

            if(newTerminationTime == -1) {
                newTerminationTime = Math.max(terminationTime, clock) + minTimeBetweenEvents*2;
            }
        }

        checkIfSimulationPauseRequested();
        return true;
    }

    private boolean logSimulationAborted() {
        if (!abortRequested) {
            return false;
        }

        aborted = true;
        LOGGER.info(
            "{}================================================== Simulation aborted under request at time {} ==================================================",
            System.lineSeparator(), clock);
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
            .forEach(e -> sendNow(e, CloudSimTag.SIMULATION_END));
        LOGGER.info("{}: Processing last events before simulation shutdown.", clockStr());

        while (true) {
            if(!runClockTickAndProcessFutureEvents(Double.MAX_VALUE)){
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

        if(terminationTime > 0 && clock > lastCloudletProcessingUpdate + TimeUtil.minutesToSeconds(60)){
            LOGGER.warn(
                "Your simulation termination time was set to {}. Current time is {} but the last time a Cloudlet has processed was {} ago. "+
                "If you think your simulation is taking to long to finish, " +
                "maybe it's because you set a too long termination time and new events aren't arriving so far.",
                secondsToStr(terminationTime), secondsToStr(clock), secondsToStr(clock-lastCloudletProcessingUpdate));
        }
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

    /**
     * Gets the time defined to terminate the simulation or -1 if it was not set.
     * @return
     */
    public double getTerminationTime(){
        return terminationTime;
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
    public double clock() {
        return clock;
    }

    @Override
    public String clockStr() {
        return String.format("%.2f", clock);
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
     * Updates the simulation clock and notify listeners if the clock has changed.
     * @param newTime simulation time to set
     * @return the old simulation time
     *
     * @see #onClockTickListeners
     */
    private double setClock(final double newTime){
        final double oldTime = clock;
        this.clock = newTime;
        clockQueue.tryToUpdateListeners(previousTime -> notifyEventListeners(onClockTickListeners, previousTime));
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
            final var evt = new CloudSimEvent(SimEvent.Type.CREATE, 0, entity, SimEntity.NULL, CloudSimTag.NONE, entity);
            future.addEvent(evt);
        }

        if (entity.getId() == -1) { // Only add once!
            entity.setId(entities.size());
            entities.add(entity);
        }
    }

    protected void removeFinishedEntity(final CloudSimEntity entity){
        if(entity.isAlive()){
            final var msg = "Alive entity %s cannot be removed from the simulation entity list.";
            throw new IllegalStateException(String.format(msg, entity));
        }

        entities.remove(entity);
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future future event queue} that happen
     * up to a given time.
     * @param until The interval for which the events should be processed (in seconds)
     * @return true if some event was processed, false otherwise
     */
    private boolean runClockTickAndProcessFutureEvents(final double until) {
        executeRunnableEntities(until);
        if (future.isEmpty()) {
            return false;
        }

        final SimEvent first = future.first();
        if(first.getTime() <= until) {
            processFutureEventsHappeningAtSameTimeOfTheFirstOne(first);
            return true;
        }

        return false;
    }

    private boolean isToWaitClockToReachTerminationTime() {
        if (!isTerminationTimeSet()) {
            return false;
        }

        final double increment = minDatacentersSchedulingInterval();
        final String info = increment == minTimeBetweenEvents
            ? "using getMinTimeBetweenEvents() since a Datacenter schedulingInterval was not set"
            : "Datacenter.getSchedulingInterval()";

        /*If a termination time is set, even if there is no events to process,
        * the simulation must keep running waiting for dynamic events
        * (such as the dynamic arrival of VMs or Cloudlets).
        * Without increasing the time, the simulation stops due to lack of new events.*/
        LOGGER.info(
            "{}: Simulation: Waiting more events or the clock to reach {} (the termination time set). Checking new events in {} seconds ({})",
            clockStr(), terminationTime, increment, info);
        setClock(clock + increment);
        return true;

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

        while(!future.isEmpty()) {
            final SimEvent evt = future.first();
            if(evt.getTime() != firstEvent.getTime())
                break;
            processEvent(evt);
            future.remove(evt);
        }
    }

    /**
     * Gets the list of entities that are in {@link SimEntity.State#RUNNABLE}
     * and execute them.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void executeRunnableEntities(final double until) {
        /* Uses an indexed loop instead of anything else to avoid
        ConcurrencyModificationException when a HostFaultInjection is created inside a DC. */
        for (int i = 0; i < entities.size(); i++) {
            final CloudSimEntity ent = entities.get(i);
            if (ent.getState() == SimEntity.State.RUNNABLE) {
                ent.run(until);
            }
        }
    }

    private void sendNow(final SimEntity dest, final CloudSimTag tag) {
        sendNow(cis, dest, tag, null);
    }

    @Override
    public void sendNow(final SimEntity src, final SimEntity dest, final CloudSimTag tag, final Object data) {
        send(src, dest, 0, tag, data);
    }

    @Override
    public void send(final SimEntity src, final SimEntity dest, final double delay, final CloudSimTag tag, final Object data) {
        send(new CloudSimEvent(SimEvent.Type.SEND, delay, src, dest, tag, data));
    }

    @Override
    public void send(final SimEvent evt) {
        requireNonNull(evt);
        //Events with a negative tag have higher priority
        if(evt.getPriority() < 0)
            future.addEventFirst(evt);
        else future.addEvent(evt);
    }

    @Override
    public void sendFirst(final SimEntity src, final SimEntity dest, final double delay, final CloudSimTag tag, final Object data) {
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
    public SimEvent select(final SimEntity dest, final Predicate<SimEvent> predicate) {
        final SimEvent evt = findFirstDeferred(dest, predicate);
        if(evt != SimEvent.NULL) {
            deferred.remove(evt);
        }

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
     * @param dest id of entity that the event has to be sent to
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
            final var msg = "Past event detected. Event time: %.2f Simulation clock: %.2f";
            throw new IllegalArgumentException(String.format(msg, evt.getTime(), clock));
        }

        setClock(evt.getTime());
        processEventByType(evt);
        for (final var listener : onEventProcessingListeners) {
            listener.update(evt);
        }
    }

    /**
     * Checks if the simulation must {@link #processEventsInParallel process events in parallel}
     * and then synchronizes some code block encapsulated into a {@link Runnable} object
     * (or lambda expression). This ensures thread safety to call the code block.
     *
     * <p>For instance, if you want to change the simulation clock in a synchronous way,
     * call {@code sync(() -> setClock(NEW_TIME))}.
     * This gives the sync() method a lambda expression representing a {@link Runnable}.
     * </p>
     *
     * <p><b>WARNING:</b> this is a very experimental feature. It may result
     * in unexpected simulation behaviour. Use at your own risk.</p>
     *
     * @param runnable The {@link Runnable} that encapsulates the code block to be called synchronously.
     */
    private void sync(final Runnable runnable){
        if(processEventsInParallel)
            synchronized(this){ runnable.run(); }
        else runnable.run();
    }

    private void processEventByType(final SimEvent evt) {
        switch (evt.getType()) {
            case NULL -> throw new IllegalArgumentException("Event has a null type.");
            case CREATE -> processCreateEvent(evt);
            case SEND -> processSendEvent(evt);
            case HOLD_DONE -> processHoldEvent(evt);
        }
    }

    private void processCreateEvent(final SimEvent evt) {
        addEntityDynamically((SimEntity) evt.getData());
    }

    /**
     * Internal method used to add a new entity to the simulation when the
     * simulation is running.
     * <b>It should not be called from user simulations.</b>
     *
     * @param entity the new entity to add
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

        final var destEnt = (CloudSimEntity)evt.getDestination();
        if (destEnt.getState() != SimEntity.State.WAITING) {
            deferred.addEvent(evt);
            return;
        }

        final var eventPredicate = waitPredicates.get(destEnt);
        if (eventPredicate == null || eventPredicate.test(evt)) {
            destEnt.setEventBuffer(new CloudSimEvent(evt));
            destEnt.setState(SimEntity.State.RUNNABLE);
            waitPredicates.remove(destEnt);
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
        }

        pauseAt = time;
        LOGGER.info("{}: Pausing simulation under request", clockStr());
        return true;
    }

    @Override
    public boolean resume() {
        final boolean wasPaused = this.paused;
        this.paused = false;
        if(wasPaused){
            LOGGER.info("{}: Resuming simulation under request", clockStr());
        }

        if (pauseAt <= clock) {
            pauseAt = -1;
        }

        return wasPaused;
    }

    @Override
    public void pauseEntity(final SimEntity src, final double delay) {
        final var evt = new CloudSimEvent(SimEvent.Type.HOLD_DONE, delay, src);
        addHoldingFutureEvent(src, evt);
    }

    private void addHoldingFutureEvent(final SimEntity src, final SimEvent evt) {
        future.addEvent(evt);
        src.setState(SimEntity.State.HOLDING);
    }

    /**
     * Holds an entity for some time.
     * @param src   id of entity to be held
     * @param delay How many seconds after the current time the entity has to be held
     */
    protected void holdEntity(final SimEntity src, final long delay) {
        final var evt = new CloudSimEvent(SimEvent.Type.HOLD_DONE, delay, src);
        addHoldingFutureEvent(src, evt);
    }

    private void checkIfSimulationPauseRequested() {
        if((isThereFutureEvtsAndNextOneHappensAfterTimeToPause() || isNotThereNextFutureEvtsAndIsTimeToPause()) && doPause()) {
            waitsForSimulationToBeResumedIfPaused();
        }
    }

    /**
     * Effectively pauses the simulation after a pause request.
     * @return true if the simulation was paused
     *        (the simulation is running and was not paused yet);
     *        false otherwise
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
            Util.sleep(100);
        }

        pauseAt = -1;
    }

    @Override
    public long getNumberOfFutureEvents(final Predicate<SimEvent> predicate){
        return future.stream().filter(predicate).count();
    }

    @Override
    public boolean isThereAnyFutureEvt(final Predicate<SimEvent> predicate){
        return future.stream().anyMatch(predicate);
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

    @Override
    public void abort() {
        abortRequested = true;
        running = false;
    }

    @Override
    public boolean isAborted() {
        return aborted;
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
        return this.onSimulationPauseListeners.remove(requireNonNull(listener));
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

    @Override
    public double getLastCloudletProcessingUpdate() {
        return lastCloudletProcessingUpdate;
    }

    @Override
    public void setLastCloudletProcessingUpdate(final double lastCloudletProcessingUpdate) {
        this.lastCloudletProcessingUpdate = lastCloudletProcessingUpdate;
    }

    /**
     * Gets the maximum number of events that have ever existed at the same time
     * inside the {@link FutureQueue}.
     */
    public long getMaxEventsNumber() {
        return future.getMaxEventsNumber();
    }

    /** Gets the total number of events generated in the {@link FutureQueue} */
    public long getGeneratedEventsNumber() {
        return future.getSerial();
    }

    /**
     * Indicates if an abrupt termination was requested.
     * @see #abort()
     */
    @Override
    public boolean isAbortRequested() {
        return abortRequested;
    }
}
