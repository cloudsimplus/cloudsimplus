/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.events.*;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.network.topologies.NetworkTopology;
import org.cloudsimplus.util.TimeUtil;
import org.cloudsimplus.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An abstract class to manage Cloud Computing simulations,
 * providing all methods to start, pause and stop them.
 * It sends and processes all discrete events during the simulation time.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Accessors(makeFinal = false) // non-final accessors required for Mockito
non-sealed abstract class CloudSim implements Simulation {
    /**
     * CloudSim Plus current version.
     */
    public static final String VERSION = "CloudSim Plus 9.0.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(CloudSim.class.getSimpleName());

    @NonNull @Getter @Setter
    private NetworkTopology networkTopology;

    @Getter
    private final CloudInformationService cis;

    @Getter
    private final Calendar calendar;

    /**
     * The time the simulation should be terminated (in seconds).
     */
    private double terminationTime = -1;

    @Getter @Setter
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

    @Getter
    private final double minTimeBetweenEvents;

    private final List<CloudSimEntity> entityList;

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

    @Getter
    private boolean running;

    /**
     * A map of entities and predicates that define the events a given entity is waiting for.
     * Received events are filtered based on the predicate associated with each entity
     * so that just the resulting events are sent to the entity.
     */
    private final Map<SimEntity, Predicate<SimEvent>> waitPredicates;

    @Getter
    private boolean paused;

    /**
     * Indicates the time that the simulation has to be paused.
     * -1 means no pause was requested.
     */
    private double pauseAt = -1;

    @Getter
    private boolean abortRequested;

    @Getter
    private boolean aborted;

    /**
     * Indicates if the simulation already ran once.
     * If so, it can't run again.
     * If you paused the simulation and want to resume it,
     * you must use {@link #resume()} instead of {@link #start()}.
     */
    private boolean alreadyRunOnce;

    /**
     * Creates a CloudSim simulation.
     * Internally, it creates a {@link CloudInformationService}.
     *
     * @see #CloudSim(double)
     */
    public CloudSim(){
        this(0.1);
    }

    /**
     * Creates a CloudSim simulation that tracks events happening in a time interval
     * as little as the minTimeBetweenEvents parameter.
     * Internally, it creates a {@link CloudInformationService}.
     *
     * @param minTimeBetweenEvents the minimal period between events.
     * Events within shorter periods after the last event are discarded.
     */
    public CloudSim(final double minTimeBetweenEvents) {
        this.entityList = new ArrayList<>();
        this.future = new FutureQueue();
        this.deferred = new DeferredQueue();
        this.waitPredicates = new HashMap<>();
        this.networkTopology = NetworkTopology.NULL;
        this.clock = 0;
        this.running = false;
        this.alreadyRunOnce = false;

        // NOTE: the order for the lines below is important
        this.calendar = Calendar.getInstance();

        if (minTimeBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is: " + minTimeBetweenEvents);
        }

        this.minTimeBetweenEvents = minTimeBetweenEvents;
        this.cis = new CloudInformationService(this);
    }

    /**
     * Finishes execution of running entities before terminating the simulation,
     * then cleans up internal state.
     *
     * <p><b>Note:</b> Should be used only in the <b>synchronous</b> mode
     * (after starting the simulation with {@link #startSync()}).</p>
     */
    protected void finish() {
        if(abortRequested){
            return;
        }

        notifyEndOfSimulationToEntities();
        LOGGER.info("Simulation: No more future events{}", System.lineSeparator());

        // Allow all entities to exit their body method
        if (!abortRequested) {
            //Uses indexed loop to avoid ConcurrentModificationException
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).run();
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
        for (int i = 0; i < entityList.size(); i++) {
            entityList.get(i).shutdown();
        }
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

    /**
     * Process all events happening up to a given time.
     *
     * @param until The interval for which the events should be processed (in seconds)
     * @return true if some event was processed; false if no event was processed
     *         or a termination time was set, and the clock reached that time
     */
    protected boolean processEvents(final double until) {
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

    protected abstract void notifyOnSimulationStartListeners();

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
     * Then, waits for such events to be received and processed.
     */
    private void notifyEndOfSimulationToEntities() {
        entityList.stream()
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
        final String msg1 = "Simulation finished at time %.2f".formatted(clock);
        final String extra = future.isEmpty() ? "" : ", before completing,";
        final String msg2 = isTimeToTerminateSimulationUnderRequest()
            ? extra + " in reason of an explicit request to terminate() or terminateAt()"
            : "";

        if(terminationTime > 0 && clock > lastCloudletProcessingUpdate + TimeUtil.minutesToSeconds(60)){
            LOGGER.warn(
                "Your simulation termination time was set to {}. Current time is {} but the last time a Cloudlet has processed was {} ago. "+
                "If you think your simulation is taking to long to finish, " +
                "maybe it's because you set a too long termination time and new events aren't arriving so far.",
                TimeUtil.secondsToStr(terminationTime), TimeUtil.secondsToStr(clock), TimeUtil.secondsToStr(clock-lastCloudletProcessingUpdate));
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

    @Override
    public double clock() {
        return clock;
    }

    @Override
    public String clockStr() {
        return "%.2f".formatted(clock);
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
     * @param newTime simulation time to set (in seconds)
     * @return the old simulation time (in seconds)
     * @see #addOnClockTickListener(EventListener)
     */
    protected double setClock(final double newTime){
        final double oldTime = clock;
        this.clock = newTime;
        return oldTime;
    }

    @Override
    public int getNumEntities() {
        return entityList.size();
    }

    @Override
    public List<SimEntity> getEntityList() {
        return Collections.unmodifiableList(entityList);
    }

    @Override
    public void addEntity(@NonNull final CloudSimEntity entity) {
        if (running) {
            final var evt = new CloudSimEvent(SimEvent.Type.CREATE, 0, entity, SimEntity.NULL, CloudSimTag.NONE, entity);
            future.addEvent(evt);
        }

        if (entity.getId() == -1) { // Only add once!
            entity.setId(entityList.size());
            entityList.add(entity);
        }
    }

    protected void removeFinishedEntity(final CloudSimEntity entity){
        if(entity.isAlive()){
            final var msg = "Alive entity %s cannot be removed from the simulation entity list.";
            throw new IllegalStateException(msg.formatted(entity));
        }

        entityList.remove(entity);
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future} event queue that happen up to a given time.
     * @param until the interval for which the events should be processed (in seconds)
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
     * Gets the list of entities that are in {@link SimEntity.State#RUNNABLE} state
     * and execute them.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void executeRunnableEntities(final double until) {
        /* Uses an indexed loop instead of anything else to avoid
        ConcurrencyModificationException when a HostFaultInjection is created inside a DC. */
        for (int i = 0; i < entityList.size(); i++) {
            final CloudSimEntity ent = entityList.get(i);
            if (ent.getState() == SimEntity.State.RUNNABLE) {
                ent.run(until);
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
    public void send(@NonNull final SimEvent evt) {
        //Events with a negative tag have higher priority
        if(evt.getTag() < 0)
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
     * and are targeted to a specific entity.
     *
     * @param queue the queue to get the events from
     * @param predicate the event selection predicate
     * @param dest entity that the event has to be sent to
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
     * @param evt the event to be processed
     */
    protected void processEvent(final SimEvent evt) {
        if (evt.getTime() < clock) {
            final var msg = "Past event detected. Event time: %.2f Simulation clock: %.2f";
            throw new IllegalArgumentException(msg.formatted(evt.getTime(), clock));
        }

        setClock(evt.getTime());
        processEventByType(evt);
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
     * Add a new entity to the simulation when the simulation is running.
     * @param entity the new entity to add
     */
    private void addEntityDynamically(@NonNull final SimEntity entity) {
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
        entityList.forEach(SimEntity::start);
        LOGGER.info("Entities started.");
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
     * @param src   entity to be held
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
     *        (it was running and not paused yet);
     *        false otherwise
     * @see #pause()
     * @see #pause(double)
     */
    protected boolean doPause() {
        if(running && isPauseRequested()) {
            this.paused=true;
            setClock(this.pauseAt);
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

    /**
     * @return the maximum number of events that have ever existed at the same time
     * inside the {@link FutureQueue}.
     */
    public long getMaxEventsNumber() {
        return future.getMaxEventsNumber();
    }

    /** @return the total number of events generated in the {@link FutureQueue} */
    public long getGeneratedEventsNumber() {
        return future.getSerial();
    }

    /**
     * @return true if there are no future events (the future queue is empty), false otherwise
     */
    public boolean noFutureEvents(){
        return future.isEmpty();
    }
}
