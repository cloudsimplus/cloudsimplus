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
    private static final String CLOUDSIMPLUS_VERSION_STRING = "1.0";

    /**
     * A constant to indicate that some entity was not found.
     */
    private static final int NOT_FOUND = -1;

    /**
     * @see #getNetworkTopology()
     */
    private NetworkTopology networkTopology;

    /**
     * The Cloud Information Service (CIS) entity.
     */
    private CloudInformationService cis;

    /**
     * The calendar.
     */
    private Calendar calendar;

    /**
     * The termination time.
     */
    private double terminateAt = -1;

    /**
     * @see #getMinTimeBetweenEvents()
     */
    private double minTimeBetweenEvents = 0.1;

    /**
     * @see #getEntityList()
     */
    private List<CloudSimEntity> entities;

    /**
     * The queue of events that will be sent in a future simulation time.
     */
    private FutureQueue future;

    /**
     * The deferred event queue.
     */
    private DeferredQueue deferred;

    /**
     * The current simulation clock.
     */
    private double clock;

    /**
     * @see #isRunning()
     */
    private boolean running;

    /**
     * @see #getEntitiesByName()
     */
    private Map<String, SimEntity> entitiesByName;

    /**
     * A map of the predicate that defines the events that a given entity is waiting.
     * Each key is the entity id and the value is the predicate.
     */
    private Map<SimEntity, Predicate> waitPredicates;

    /**
     * @see #isPaused()
     */
    private boolean paused = false;

    /**
     * Indicates the time that the simulation has to be paused.
     * -1 means no pause was requested.
     */
    private double pauseAt = -1;

    /**
     * Indicates if an abrupt termination was requested.
     * @see #abort()
     */
    private boolean abort = false;

    /**
     * Indicates if the simulation already run once.
     * If yes, it can't run again.
     * If you paused the simulation and wants to resume it,
     * you must use {@link #resume()} instead of {@link #start()}.
     */
    private boolean alreadyRunOnce;

    private List<EventListener<SimEvent>> onEventProcessingListeners;
    private List<EventListener<EventInfo>> onSimulationPausedListeners;
    private List<EventListener<EventInfo>> onClockTickListeners;

    /**
     * Creates a CloudSim simulation using a default calendar.
     * Internally it creates a CloudInformationService.
     *
     * @see CloudInformationService
     * @pre numUser >= 0
     * @post $none
     */
    public CloudSim(){
        this(null);
    }

    /**
     * Creates a CloudSim simulation with the given parameters.
     * Internally it creates a CloudInformationService.
     *
     * @param cal starting time for this simulation. If it is <tt>null</tt>,
     * then the time will be taken from <tt>Calendar.getInstance()</tt>
     * @throws RuntimeException
     *
     * @see CloudInformationService
     * @post $none
     */
    public CloudSim(Calendar cal) {
        Log.printFormattedLine("Initialising CloudSim Plus %s...", CloudSim.CLOUDSIMPLUS_VERSION_STRING);
        this.entities = new ArrayList<>();
        this.entitiesByName = new LinkedHashMap<>();
        this.future = new FutureQueue();
        this.deferred = new DeferredQueue();
        this.waitPredicates = new HashMap<>();
        this.networkTopology = NetworkTopology.NULL;
        this.clock = 0;
        this.running = false;
        this.alreadyRunOnce = false;
        this.onEventProcessingListeners = new ArrayList<>();
        this.onSimulationPausedListeners = new ArrayList<>();
        this.onClockTickListeners = new ArrayList<>();

        // NOTE: the order for the lines below is important
        this.calendar = (Objects.isNull(calendar) ? Calendar.getInstance() : calendar);
        this.cis = new CloudInformationService(this);
    }

    /**
     * Creates a CloudSim simulation with the given parameters.
     * Internally it creates a CloudInformationService.
     *
     * @param numUser this parameter is not being used anymore
     * @param cal starting time for this simulation. If it is <tt>null</tt>,
     * then the time will be taken from <tt>Calendar.getInstance()</tt>
     * @param traceFlag this parameter is not being used anymore
     * @param periodBetweenEvents the minimal period between events. Events
     * within shorter periods after the last event are discarded.
     * @see CloudInformationService
     * @pre numUser >= 0
     * @post $none
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public CloudSim(int numUser, Calendar cal, boolean traceFlag, double periodBetweenEvents) {
        this(cal);

        if (periodBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is:" + periodBetweenEvents);
        }

        minTimeBetweenEvents = periodBetweenEvents;
    }

    @Override
    public double start() {
        Log.printConcatLine("Starting CloudSim Plus version ", CLOUDSIMPLUS_VERSION_STRING);
        return run();
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
    public boolean terminateAt(double time) {
        if (time <= clock) {
            return false;
        } else {
            terminateAt = time;
        }

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
    public int getCloudInfoServiceEntityId() {
        return cis.getId();
    }

    @Override
    public Set<Datacenter> getDatacenterList() {
        return cis.getDatacenterList();
    }

    @Override
    public double clock() {
        return clock;
    }

    private void setClock(final double newTime){
        final double oldTime = clock;
        this.clock = newTime;
        notifyOnClockTickListenersIfClockChanged(oldTime, newTime);
    }

    /**
     * Notifies all Listeners of onClockTick event when the simulation clock changes.
     * @param oldClock the previous simulation clock time
     * @param newClock the new simulation clock time (that can be equals to the old time)
     */
    private void notifyOnClockTickListenersIfClockChanged(double oldClock, double newClock) {
        if((long)newClock > (long)oldClock || (oldClock == 0 && newClock > 0) ){
            EventInfo info = EventInfo.of(newClock);
            onClockTickListeners.forEach(l -> l.update(info));
        }
    }

    @Override
    public int getNumEntities() {
        return entities.size();
    }

    @Override
    public SimEntity getEntity(int id) {
        return entities.get(id);
    }

    @Override
    public SimEntity getEntity(String name) {
        return entitiesByName.get(name);
    }

    @Override
    public int getEntityId(String name) {
        SimEntity obj = entitiesByName.get(name);
        return (Objects.isNull(obj) ? NOT_FOUND : obj.getId());
    }

    @Override
    public String getEntityName(int entityId) {
        return getEntity(entityId).getName();
    }

    @Override
    public List<SimEntity> getEntityList() {
        return Collections.unmodifiableList(entities);
    }

    @Override
    public void addEntity(CloudSimEntity e) {
        if (running) {
            // Post an event to make this entity
            SimEvent evt = new CloudSimEvent(this, SimEvent.Type.CREATE, clock, 1, 0, 0, e);
            future.addEvent(evt);
        }

        if (e.getId() == -1) { // Only add once!
            e.setId(entities.size());
            entities.add(e);
            entitiesByName.put(e.getName(), e);
        }
    }

    @Override
    public boolean updateEntityName(final String oldName){
        SimEntity entity = entitiesByName.remove(oldName);
        if(!Objects.isNull(entity)){
            entitiesByName.put(entity.getName(), entity);
            return true;
        }

        return false;
    }

    /**
     * Internal method used to add a new entity to the simulation when the
     * simulation is running.
     *
     * <b>It should not be called from user simulations.</b>
     *
     * @param e The new entity
     */
    protected void addEntityDynamically(SimEntity e) {
        if (Objects.isNull(e)) {
            throw new IllegalArgumentException("Adding null entity.");
        } else {
            printMessage("Adding: " + e.getName());
        }

        e.start();
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future future event queue}.
     */
    private void runClockTickAndProcessFutureEventQueue() {
        executeRunnableEntities();

        if (future.isEmpty()) {
            running = false;
            printMessage("Simulation: No more future events");
        } else {
            // If there are more future events, then deal with them
            future.stream().findFirst().ifPresent(this::processAllFutureEventsHappeningAtSameTimeOfTheFirstOne);
        }
    }

    private void processAllFutureEventsHappeningAtSameTimeOfTheFirstOne(SimEvent firstEvent) {
        processEvent(firstEvent);
        future.remove(firstEvent);

        List<SimEvent> eventsToProcess = future.stream()
            .filter(e -> e.eventTime() == firstEvent.eventTime())
            .collect(toList());

        for(SimEvent evt: eventsToProcess) {
            processEvent(evt);
            future.remove(evt);
        }
    }

    /**
     * Gets the list of entities that are in {@link SimEntity.State#RUNNABLE}
     * and execute them.
     */
    private void executeRunnableEntities() {
        List<SimEntity> runableEntities = entities.stream()
                .filter(ent -> ent.getState() == SimEntity.State.RUNNABLE)
                .collect(toList());

        runableEntities.forEach(SimEntity::run);
    }

    @Override
    public void sendNow(int src, int dest, int tag, Object data) {
        send(src, dest, 0, tag, data);
    }

    @Override
    public void send(int src, int dest, double delay, int tag, Object data) {
        if (delay < 0) {
            throw new IllegalArgumentException("Send delay can't be negative.");
        }

        SimEvent e = new CloudSimEvent(this, SimEvent.Type.SEND, clock + delay, src, dest, tag, data);
        future.addEvent(e);
    }

    @Override
    public void sendFirst(int src, int dest, double delay, int tag, Object data) {
        if (delay < 0) {
            throw new IllegalArgumentException("Send delay can't be negative.");
        }

        SimEvent e = new CloudSimEvent(this, SimEvent.Type.SEND, clock + delay, src, dest, tag, data);
        future.addEventFirst(e);
    }

    @Override
    public void wait(CloudSimEntity src, Predicate<SimEvent> p) {
        src.setState(SimEntity.State.WAITING);
        if (p != SIM_ANY) {
            // If a predicate has been used, store it in order to check incomming events that matches it
            waitPredicates.put(src, p);
        }
    }

    @Override
    public long waiting(int dest, Predicate<SimEvent> p) {
        return filterEventsToDestinationEntity(deferred, p, dest).count();
    }

    @Override
    public SimEvent select(int dest, Predicate<SimEvent> p) {
        SimEvent evt = findFirstDeferred(dest, p);
        deferred.remove(evt);
        return evt;
    }

    @Override
    public SimEvent findFirstDeferred(int dest, Predicate<SimEvent> p) {
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
    private Stream<SimEvent> filterEventsToDestinationEntity(EventQueue queue, Predicate<SimEvent> p, int dest) {
        return filterEvents(queue, p.and(e -> e.getDestination() == dest));
    }

    @Override
    public SimEvent cancel(int src, Predicate<SimEvent> p) {
        SimEvent evt = future.stream().filter(p.and(e -> e.getSource() == src)).findFirst().orElse(SimEvent.NULL);
        future.remove(evt);
        return evt;
    }

    @Override
    public boolean cancelAll(int src, Predicate<SimEvent> p) {
        final int previousSize = future.size();
        List<SimEvent> cancelList = filterEventsFromSourceEntity(future, p, src).collect(toList());
        future.removeAll(cancelList);
        return previousSize < future.size();
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate
     * and from a source entity.
     *
     * @param queue the queue to get the events from
     * @param p the event selection predicate
     * @param src Id of entity that scheduled the event
     * @return a Stream of events from the queue
     */
    private Stream<SimEvent> filterEventsFromSourceEntity(EventQueue queue, Predicate<SimEvent> p, int src) {
        return filterEvents(queue, p.and(e -> e.getSource() == src));
    }

    /**
     * Gets a stream of events inside a specific queue that match a given predicate.
     *
     * @param queue the queue to get the events from
     * @param p the event selection predicate
     * @return a Strem of events from the queue
     */
    private Stream<SimEvent> filterEvents(EventQueue queue, Predicate<SimEvent> p) {
        return queue.stream().filter(p);
    }

    /**
     * Processes an event.
     *
     * @param e the event to be processed
     */
    private void processEvent(SimEvent e) {
        int dest, src;
        CloudSimEntity destEnt;
        // Update the system's clock
        if (e.eventTime() < clock) {
            throw new IllegalArgumentException("Past event detected.");
        }
        setClock(e.eventTime());
        notifyOnEventProcessingListeners(e);

        // Ok now process it
        switch (e.getType()) {
            case NULL:
                throw new IllegalArgumentException("Event has a null type.");

            case CREATE:
                SimEntity newEvent = (SimEntity) e.getData();
                addEntityDynamically(newEvent);
                break;

            case SEND:
                // Check for matching wait
                dest = e.getDestination();
                if (dest < 0) {
                    throw new IllegalArgumentException("Attempt to send to a null entity detected.");
                } else {
                    destEnt = entities.get(dest);
                    if (destEnt.getState() == SimEntity.State.WAITING) {
                        Predicate<SimEvent> p = waitPredicates.get(destEnt);
                        if ((Objects.isNull(p)) || (e.getTag() == 9999) || p.test(e)) {
                            destEnt.setEventBuffer(new CloudSimEvent(e));
                            destEnt.setState(SimEntity.State.RUNNABLE);
                            waitPredicates.remove(destEnt);
                        } else {
                            deferred.addEvent(e);
                        }
                    } else {
                        deferred.addEvent(e);
                    }
                }
                break;

            case HOLD_DONE:
                src = e.getSource();
                if (src < 0) {
                    throw new IllegalArgumentException("Null entity holding.");
                } else {
                    entities.get(src).setState(SimEntity.State.RUNNABLE);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Notifies all registered listeners when a {@link SimEvent} is processed by the simulation.
     * @param e the processed event
     */
    private void notifyOnEventProcessingListeners(SimEvent e) {
        onEventProcessingListeners.forEach(l -> l.update(e));
    }

    /**
     * Internal method used to start the simulation. This method should
     * <b>not</b> be used by user simulations.
     */
    private void runStart() {
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
    public boolean pause(double time) {
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
    public void pauseEntity(int src, double delay) {
        SimEvent e = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(e);
        entities.get(src).setState(SimEntity.State.HOLDING);
    }

    @Override
    public void holdEntity(int src, long delay) {
        SimEvent e = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(e);
        entities.get(src).setState(SimEntity.State.HOLDING);
    }

    /**
     * Starts the simulation execution. This should be called after all the
     * entities have been setup and added.
     * The method blocks until the simulation is ended.
     *
     * @return the last clock value
     * @throws RuntimeException when the simulation already run once. If you paused the simulation and wants to resume it,
     * you must use {@link #resume()} instead of {@link #start()}.
     */
    private double run()  {
        if(alreadyRunOnce){
            throw new RuntimeException("You can't run a simulation that already run previously. If you paused the simulation and want to resume it, you should call resume().");
        }

        if (!running) {
            runStart();
        }

        this.alreadyRunOnce = true;

        while (running) {
            runClockTickAndProcessFutureEventQueue();

            if (isThereRequestToTerminateSimulationAndItWasAttended()) {
                Log.printFormattedLine(
                    "\nSimulation finished at time %.2f, before completing, in reason of an explicit request to terminate() or terminateAt().\n", clock);
                break;
            }

            checkIfThereIsRequestToPauseSimulation();
        }

        double lastSimulationTime = clock();

        finishSimulation();
        printMessage("Simulation completed.");

        return lastSimulationTime;
    }

    private boolean isThereRequestToTerminateSimulationAndItWasAttended() {
        if(abort){
            return true;
        }

        // this block allows termination of simulation at a specific time
        if (isTerminationRequested() && clock >= terminateAt) {
            terminate();
            setClock(terminateAt);
            return true;
        }

        return false;
    }

    private void checkIfThereIsRequestToPauseSimulation() {
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
    public boolean doPause() {
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
        onSimulationPausedListeners.forEach(l -> l.update(EventInfo.of(clock)));
    }

    private boolean isPauseRequested() {
        return pauseAt > -1;
    }

    private void waitsForSimulationToBeResumedIfPaused() {
        while (paused) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pauseAt = -1;
    }

    private boolean isThereFutureEvtsAndNextOneHappensAfterTimeToPause() {
        return !future.isEmpty() && clock <= pauseAt && isNextFutureEventHappeningAfterTimeToPause();
    }

    private boolean isNotThereNextFutureEvtsAndIsTimeToPause() {
        return future.isEmpty() && clock >= pauseAt;
    }

    private boolean isTerminationRequested() {
        return terminateAt > 0.0;
    }

    private boolean isNextFutureEventHappeningAfterTimeToPause() {
        return future.iterator().next().eventTime() >= pauseAt;
    }

    /**
     * Finishes execution of running entities before terminating the simulation.
     */
    private void finishSimulation() {
        // Allow all entities to exit their body method
        if (!abort) {
            entities.stream()
                .filter(e -> e.getState() != SimEntity.State.FINISHED)
                .forEach(SimEntity::run);
        }

        entities.forEach(SimEntity::shutdownEntity);
        running = false;
    }

    @Override
    public void abort() {
        abort = true;
    }

    /**
     * Prints a message about the progress of the simulation.
     *
     * @param message the message
     */
    private void printMessage(String message) {
        Log.printLine(message);
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public boolean removeOnEventProcessingListener(EventListener<SimEvent> listener) {
        return onEventProcessingListeners.remove(listener);
    }

    @Override
    public final Simulation addOnSimulationPausedListener(EventListener<EventInfo> listener) {
        if(!Objects.isNull(listener)){
            this.onSimulationPausedListeners.add(listener);
        }

        return this;
    }

    @Override
    public boolean removeOnSimulationPausedListener(EventListener<EventInfo> listener) {
        return this.onSimulationPausedListeners.remove(listener);
    }

    @Override
    public final Simulation addOnEventProcessingListener(EventListener<SimEvent> listener) {
        if(!Objects.isNull(listener)) {
            this.onEventProcessingListeners.add(listener);
        }

        return this;
    }

    @Override
    public Simulation addOnClockTickListener(EventListener<EventInfo> listener) {
        if(!Objects.isNull(listener)) {
            onClockTickListeners.add(listener);
        }
        return this;
    }

    @Override
    public boolean removeOnClockTickListener(EventListener<EventInfo> listener) {
        return onClockTickListeners.remove(listener);
    }

    @Override
    public NetworkTopology getNetworkTopology() {
        return networkTopology;
    }

    @Override
    public void setNetworkTopology(NetworkTopology networkTopology) {
        this.networkTopology = networkTopology;
    }

    @Override
    public Map<String, SimEntity> getEntitiesByName() {
        return Collections.unmodifiableMap(entitiesByName);
    }
}
