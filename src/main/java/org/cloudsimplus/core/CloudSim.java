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
import org.jspecify.annotations.Nullable;

/// An abstract class to manage Cloud Computing simulations,
/// providing all methods to start, pause and stop them.
/// It sends and processes all discrete events during the simulation time.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
@Accessors(makeFinal = false) // non-final accessors required for Mockito
non-sealed abstract class CloudSim implements Simulation {
    /// CloudSim Plus current version.
    public static final String VERSION = "CloudSim Plus 9.0.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(CloudSim.class.getSimpleName());

    @NonNull @Getter @Setter
    private NetworkTopology networkTopology;

    @Getter
    private final CloudInformationService cis;

    @Getter
    private final Calendar calendar;

    /// The time the simulation should be terminated (in seconds).
    private double terminationTime = -1;

    @Getter @Setter
    private double lastCloudletProcessingUpdate;

    /// The time the simulation is really expected to finish.
    /// This value is just used when the [#terminationTime] is set.
    /// In such a case, the researcher has defined the time he/she wants
    /// the simulation to finish. However, the simulation may keep
    /// running for a short period after this time to ensure crucial
    /// events are processed (such as the finalization of Cloudlets).
    /// For instance, when [Cloudlet#getLength()] is negative
    /// they keep running until certain events happens
    /// (check the mentioned method documentation for details).
    private double newTerminationTime = -1;

    @Getter
    private final double minTimeBetweenEvents;

    private final List<CloudSimEntity> entityList;

    /// The queues of events of this simulation.
    private final SimulationEventQueues events;

    /// Events sent before the simulation starts, in the order they must be added to the future queue.
    /// They are added right after the entities start, so that the entities' own start events come first.
    /// It's only created when some event is sent before the start.
    private @Nullable Deque<SimEvent> eventsSentBeforeStart;

    /// @see #clock()
    private double clock;

    @Getter
    private boolean running;

    @Getter
    private boolean paused;

    /// Indicates the time that the simulation has to be paused.
    /// -1 means no pause was requested.
    private double pauseAt = -1;

    @Getter
    private boolean abortRequested;

    @Getter
    private boolean aborted;

    /// Indicates if the simulation has ever started (even if it has stopped).
    /// If so, it can't run again.
    /// If you paused the simulation and want to resume it,
    /// you must use [#resume()] instead of [#start()].
    ///
    /// It differs from [#running] only after the simulation stops,
    /// when [#running] is false, but this attribute is still true.
    /// This way, events sent before the start are held until the simulation starts,
    /// while events sent after the stop are rejected.
    /// @see #schedule(SimEvent)
    private boolean everStarted;

    /// Creates a CloudSim simulation.
    /// Internally, it creates a [CloudInformationService].
    ///
    /// @see #CloudSim(double)
    public CloudSim(){
        this(0.1);
    }

    /// Creates a CloudSim simulation that tracks events happening in a time interval
    /// as little as the minTimeBetweenEvents parameter.
    /// Internally, it creates a [CloudInformationService].
    ///
    /// @param minTimeBetweenEvents the minimal period between events.
    /// Events within shorter periods after the last event are discarded.
    public CloudSim(final double minTimeBetweenEvents) {
        this.entityList = new ArrayList<>();
        this.events = new SimulationEventQueues();
        this.networkTopology = NetworkTopology.NULL;
        this.clock = 0;
        this.running = false;
        this.everStarted = false;

        // NOTE: the order for the lines below is important
        this.calendar = Calendar.getInstance();

        if (minTimeBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is: " + minTimeBetweenEvents);
        }

        this.minTimeBetweenEvents = minTimeBetweenEvents;
        this.cis = new CloudInformationService(this);
    }

    /// Finishes execution of running entities before terminating the simulation,
    /// then cleans up internal state.
    ///
    /// **Note:** Should be used only in the **synchronous** mode
    /// (after starting the simulation with [#startSync()]).
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

    /// Shuts down remaining entities before finishing the simulation.
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
        if(everStarted){
            throw new UnsupportedOperationException(
                "You can't run a simulation that has already run previously. " +
                "If you've paused the simulation and want to resume it, call the resume() method.");
        }

        LOGGER.info("{}================== Starting {} =================={}", System.lineSeparator(), VERSION,  System.lineSeparator());
        /* Set before starting the entities, so that the events they send in their startInternal()
         * go straight to the future queue, ahead of the events sent before the start. */
        this.everStarted = true;
        startEntitiesIfNotRunning();
        addEventsSentBeforeStartToFutureQueue();
    }

    /// Process all events happening up to a given time.
    ///
    /// @param until The interval for which the events should be processed (in seconds)
    /// @return true if some event was processed; false if no event was processed
    /// or a termination time was set, and the clock reached that time
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

    /// Notifies entities that simulation is ending,
    /// enabling them to send and process their last events.
    /// Then, waits for such events to be received and processed.
    private void notifyEndOfSimulationToEntities() {
        entityList.stream()
                  .filter(entity -> !entity.isFinished())
                  .forEach(e -> schedule(new CloudSimEvent(0, cis, e, CloudSimTag.SIMULATION_END, null)));
        LOGGER.info("{}: Processing last events before simulation shutdown.", clockStr());

        while (true) {
            if(!runClockTickAndProcessFutureEvents(Double.MAX_VALUE)){
                return;
            }
        }
    }

    private void printSimulationFinished() {
        final String msg1 = "Simulation finished at time %.2f".formatted(clock);
        final String extra = events.noFutureEvents() ? "" : ", before completing,";
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

    /// Updates the simulation clock and notify listeners if the clock has changed.
    /// @param newTime simulation time to set (in seconds)
    /// @return the old simulation time (in seconds)
    /// @see SimulationListeners#addOnClockTick(EventListener)
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
            events.addFutureEvent(evt);
        }

        if (entity.getId() == -1) { // Only add once!
            entity.setId(entityList.size());
            entityList.add(entity);
            if (entity instanceof Datacenter dc) {
                cis.addDatacenter(dc);
            }
        }
    }

    protected void removeFinishedEntity(final CloudSimEntity entity){
        if(!entity.isFinished()){
            final var msg = "Unfinished entity %s cannot be removed from the simulation entity list.";
            throw new IllegalStateException(msg.formatted(entity));
        }

        entityList.remove(entity);
    }

    /// Run one tick of the simulation, processing and removing the
    /// events in the future event queue that happen up to a given time.
    /// @param until the interval for which the events should be processed (in seconds)
    /// @return true if some event was processed, false otherwise
    private boolean runClockTickAndProcessFutureEvents(final double until) {
        executeRunnableEntities(until);
        if (events.noFutureEvents()) {
            return false;
        }

        final SimEvent first = events.firstFutureEvent();
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

    /// @return the minimum [Datacenter#getSchedulingInterval()]
    /// between all Datacenters or [#getMinTimeBetweenEvents()]
    /// in case no Datacenter has its scheduling interval set
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
        events.removeFutureEvent(firstEvent);

        while(!events.noFutureEvents()) {
            final SimEvent evt = events.firstFutureEvent();
            if(evt.getTime() != firstEvent.getTime())
                break;
            processEvent(evt);
            events.removeFutureEvent(evt);
        }
    }

    /// Gets the list of entities that are in [SimEntity.State#RUNNABLE] state
    /// and execute them.
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

    /// Adds an event to the future queue: the single place where events are queued.
    /// Entities send events through [SimEntity#schedule(SimEvent)] and the other `schedule*()` methods,
    /// which validate the delay and add the network delay. Here, the event time is kept as given.
    /// If the simulation hasn't started yet, the event is held and only sent when it starts.
    /// If the simulation has stopped, the event is rejected.
    ///
    /// @param evt the event to send
    /// @return true if the event was sent or held until the simulation starts;
    /// false if it was rejected because the simulation has stopped
    /// @see #isEventRejectedAfterStop(SimEvent)
    boolean schedule(@NonNull final SimEvent evt) {
        if (!everStarted) {
            eventsSentBeforeStart().addLast(evt);
            return true;
        }

        if (isEventRejectedAfterStop(evt)) {
            return false;
        }

        //Events with a negative tag have higher priority
        if(evt.getTag() < 0)
            events.addFutureEventFirst(evt);
        else events.addFutureEvent(evt);
        return true;
    }

    /// Adds a high-priority event to the future queue, following the same rules of [#schedule(SimEvent)].
    /// If the simulation hasn't started yet, the event is held ahead of the other held events.
    ///
    /// @param evt the event to send
    /// @return true if the event was sent or held until the simulation starts;
    /// false if it was rejected because the simulation has stopped
    boolean scheduleFirst(@NonNull final SimEvent evt) {
        if (!everStarted) {
            eventsSentBeforeStart().addFirst(evt);
            return true;
        }

        if (isEventRejectedAfterStop(evt)) {
            return false;
        }

        events.addFutureEventFirst(evt);
        return true;
    }

    /// Checks if an event must be rejected because the simulation has stopped
    /// (it has finished, was terminated or aborted), logging a warning in such a case.
    /// A [CloudSimTag#SIMULATION_END] event is never rejected,
    /// since it has to be processed to enable entities to shut down.
    ///
    /// @param evt the event to check
    /// @return true if the event was rejected, false if it can be added to the future queue
    private boolean isEventRejectedAfterStop(final SimEvent evt) {
        if (running || evt.getTag() == CloudSimTag.SIMULATION_END) {
            return false;
        }

        LOGGER.warn(
            "{}: Cannot send events after the simulation stops. Event {} from {} to {} rejected.",
            clockStr(), evt.getTag(), evt.getSource(), evt.getDestination());
        return true;
    }

    /// {@return the list of events sent before the simulation starts}
    /// It's created if it doesn't exist yet.
    private Deque<SimEvent> eventsSentBeforeStart() {
        if (eventsSentBeforeStart == null) {
            eventsSentBeforeStart = new ArrayDeque<>();
        }

        return eventsSentBeforeStart;
    }

    /// Adds the events sent before the simulation starts to the future queue,
    /// in the order they were sent, keeping the time they were scheduled to.
    /// This way, events at time 0 are processed at time 0, after the entities' own start events.
    private void addEventsSentBeforeStartToFutureQueue() {
        if (eventsSentBeforeStart == null) {
            return;
        }

        eventsSentBeforeStart.forEach(events::addFutureEvent);
        eventsSentBeforeStart = null;
    }

    /// Processes an event.
    /// @param evt the event to be processed
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
            case SEND -> events.processSendEvent(evt);
            case HOLD_DONE -> processHoldEvent(evt);
        }
    }

    private void processCreateEvent(final SimEvent evt) {
        addEntityDynamically((SimEntity) evt.getData());
    }

    /// Add a new entity to the simulation when the simulation is running.
    /// @param entity the new entity to add
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

    /// Holds an entity for some time.
    /// @param src   entity to be held
    /// @param delay How many seconds after the current time the entity has to be held
    protected void holdEntity(final SimEntity src, final long delay) {
        events.pauseEntity(src, delay);
    }

    private void checkIfSimulationPauseRequested() {
        if((isThereFutureEvtsAndNextOneHappensAfterTimeToPause() || isNotThereNextFutureEvtsAndIsTimeToPause()) && doPause()) {
            waitsForSimulationToBeResumedIfPaused();
        }
    }

    /// Effectively pauses the simulation after a pause request.
    /// @return true if the simulation was paused
    /// (it was running and not paused yet);
    /// false otherwise
    /// @see #pause()
    /// @see #pause(double)
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

    private boolean isThereFutureEvtsAndNextOneHappensAfterTimeToPause() {
        return !events.noFutureEvents() && clock <= pauseAt && isNextFutureEventHappeningAfterTimeToPause();
    }

    private boolean isNotThereNextFutureEvtsAndIsTimeToPause() {
        return events.noFutureEvents() && clock >= pauseAt;
    }

    @Override
    public SimulationEvents events() {
        return events;
    }

    @Override
    public boolean isTerminationTimeSet() {
        return terminationTime > 0.0;
    }

    private boolean isNextFutureEventHappeningAfterTimeToPause() {
        return events.firstFutureEvent().getTime() >= pauseAt;
    }

    @Override
    public void abort() {
        abortRequested = true;
        running = false;
    }

    /// @return the maximum number of events that have ever existed at the same time
    /// inside the future queue.
    public long getMaxEventsNumber() {
        return events.getMaxFutureEventsNumber();
    }

    /// @return the total number of events generated in the future queue
    public long getGeneratedEventsNumber() {
        return events.getGeneratedFutureEventsNumber();
    }

    /// @return true if there are no future events (the future queue is empty), false otherwise
    public boolean noFutureEvents(){
        return events.noFutureEvents();
    }
}
