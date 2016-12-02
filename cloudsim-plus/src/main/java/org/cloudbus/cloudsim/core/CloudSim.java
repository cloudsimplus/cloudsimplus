/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import java.util.*;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.core.events.DeferredQueue;
import org.cloudbus.cloudsim.core.events.FutureQueue;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.core.predicates.Predicate;
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
     * @see #getNetworkTopology()
     */
    private NetworkTopology networkTopology;

    /**
     * The CloudSim Plus current version.
     */
    private static final String CLOUDSIMPLUS_VERSION_STRING = "1.0";

    /**
     * The Constant NOT_FOUND.
     */
    private static final int NOT_FOUND = -1;

    /**
     * The id of CloudCloudSimShutdown entity.
     */
    @SuppressWarnings("unused")
    private CloudCloudSimShutdown shutdown;

    /**
     * The CIS object.
     */
    private CloudInformationService cis;

    /**
     * The trace flag.
     */
    @SuppressWarnings("unused")
    private boolean traceFlag = false;

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
     * The future event queue.
     */
    protected FutureQueue future;

    /**
     * The deferred event queue.
     */
    protected DeferredQueue deferred;

    /**
     * The current simulation clock.
     */
    private double clock;

    /**
     * Flag for checking if the simulation is running.
     */
    private boolean running;

    /**
     * The entities by name.
     */
    private Map<String, SimEntity> entitiesByName;

    /**
     * The wait predicates.
     */
    private Map<Integer, Predicate> waitPredicates;

    /**
     * The paused.
     */
    private boolean paused = false;

    /**
     * The pause at.
     */
    private long pauseAt = -1;

    /**
     * The abrupt terminate.
     */
    private boolean abruptTerminate = false;

    /**
     * @see #getOnEventProcessingListener()
     */
    private EventListener<SimEvent> onEventProcessingListener = EventListener.NULL;

    /**
     * Creates a CloudSim simulation using a default calendar and that does not track events.
     * <p>
     * Inside this method, it will create the following CloudSim entities:
     * <ul>
     * <li>CloudInformationService.
     * <li>CloudCloudSimShutdown
     * </ul>
     * <p>
     *
     * @param numUser the number of {@link DatacenterBroker} created. This parameters
     * indicates that {@link CloudCloudSimShutdown} first
     * waits for all user entities's END_OF_SIMULATION signal before issuing
     * terminate signal to other entities
     * @see CloudCloudSimShutdown
     * @see CloudInformationService
     * @pre numUser >= 0
     * @post $none
     */
    public CloudSim(int numUser){
        this(numUser, null, false);
    }

    /**
     * Creates a CloudSim simulation with the given parameters.
     * <p>
     * Inside this method, it will create the following CloudSim entities:
     * <ul>
     * <li>CloudInformationService.
     * <li>CloudCloudSimShutdown
     * </ul>
     * <p>
     *
     * @param numUsers the number of {@link DatacenterBroker} created. This parameters
     * indicates that {@link CloudCloudSimShutdown} first
     * waits for all user entities's END_OF_SIMULATION signal before issuing
     * terminate signal to other entities
     * @param cal starting time for this simulation. If it is <tt>null</tt>,
     * then the time will be taken from <tt>Calendar.getInstance()</tt>
     * @param traceFlag <tt>true</tt> if CloudSim trace need to be written
     * @throws RuntimeException
     *
     * @see CloudCloudSimShutdown
     * @see CloudInformationService
     * @pre numUsers >= 0
     * @post $none
     */
    public CloudSim(int numUsers, Calendar cal, boolean traceFlag) throws RuntimeException {
        Log.printLine("Initialising...");
        entities = new ArrayList<>();
        entitiesByName = new LinkedHashMap<>();
        future = new FutureQueue();
        deferred = new DeferredQueue();
        waitPredicates = new HashMap<>();
        networkTopology = NetworkTopology.NULL;
        clock = 0;
        running = false;

        // NOTE: the order for the below 3 lines are important
        this.traceFlag = traceFlag;

        this.calendar = (calendar == null ? Calendar.getInstance() : calendar);

        // creates a CloudCloudSimShutdown object
        this.shutdown = new CloudCloudSimShutdown(this, numUsers);
        cis = new CloudInformationService(this);
    }

    /**
     * Creates a CloudSim simulation with the given parameters and using a default Calendar.
     * <p>
     * Inside this method, it will create the following CloudSim entities:
     * <ul>
     * <li>CloudInformationService.
     * <li>CloudCloudSimShutdown
     * </ul>
     * <p>
     *
     * @param numUsers the number of {@link DatacenterBroker} created. This parameters
     * indicates that {@link CloudCloudSimShutdown} first
     * waits for all user entities's END_OF_SIMULATION signal before issuing
     * terminate signal to other entities
     * @param traceFlag <tt>true</tt> if CloudSim trace need to be written
     * @throws RuntimeException
     *
     * @see CloudCloudSimShutdown
     * @see CloudInformationService
     * @pre numUsers >= 0
     * @post $none
     */
    public CloudSim(int numUsers, boolean traceFlag) throws RuntimeException {
        this(numUsers, null, traceFlag);
    }

    /**
     * Creates a CloudSim simulation with the given parameters and that does not track events.
     * <p>
     * Inside this method, it will create the following CloudSim entities:
     * <ul>
     * <li>CloudInformationService.
     * <li>CloudCloudSimShutdown
     * </ul>
     * <p>
     *
     * @param numUser the number of {@link DatacenterBroker} created. This parameters
     * indicates that {@link CloudCloudSimShutdown} first
     * waits for all user entities's END_OF_SIMULATION signal before issuing
     * terminate signal to other entities
     * @param cal starting time for this simulation. If it is <tt>null</tt>,
     * then the time will be taken from <tt>Calendar.getInstance()</tt>
     * @see CloudCloudSimShutdown
     * @see CloudInformationService
     * @pre numUser >= 0
     * @post $none
     */
    public CloudSim(int numUser, Calendar cal){
        this(numUser, cal, false);
    }

    /**
     * Creates a CloudSim simulation with the given parameters.
     * <p>
     * Inside this method, it will create the following CloudSim entities:
     * <ul>
     * <li>CloudInformationService.
     * <li>CloudCloudSimShutdown
     * </ul>
     * <p>
     *
     * @param numUser the number of {@link DatacenterBroker} created. This parameters
     * indicates that {@link CloudCloudSimShutdown} first
     * waits for all user entities's END_OF_SIMULATION signal before issuing
     * terminate signal to other entities
     * @param cal starting time for this simulation. If it is <tt>null</tt>,
     * then the time will be taken from <tt>Calendar.getInstance()</tt>
     * @param traceFlag <tt>true</tt> if CloudSim trace need to be written
     * @param periodBetweenEvents - the minimal period between events. Events
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
    public CloudSim(int numUser, Calendar cal, boolean traceFlag, double periodBetweenEvents) throws RuntimeException{
        this(numUser, cal, traceFlag);

        if (periodBetweenEvents <= 0) {
            throw new IllegalArgumentException("The minimal time between events should be positive, but is:" + periodBetweenEvents);
        }

        minTimeBetweenEvents = periodBetweenEvents;
    }

    @Override
    public double start() throws RuntimeException {
        Log.printConcatLine("Starting CloudSim version ", CLOUDSIMPLUS_VERSION_STRING);
        return run();
    }

    @Override
    public void stop() throws RuntimeException {
        try {
            runStop();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("CloudSim.stopCloudSimulation() : "
                    + "Error - can't stop Cloud Simulation.");
        }
    }

    @Override
    public boolean terminate() {
        running = false;
        printMessage("Simulation: Reached termination time.");
        return true;
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
    public List<Integer> getDatacenterIdsList() {
        return cis.getDatacenterIdsList();
    }

    @Override
    public double clock() {
        return clock;
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
        if (obj == null) {
            return NOT_FOUND;
        } else {
            return obj.getId();
        }
    }

    @Override
    public String getEntityName(int entityId) {
        return getEntity(entityId).getName();
    }

    @Override
    public String getEntityName(Integer entityID) {
        if (entityID != null) {
            return getEntityName(entityID.intValue());
        }
        return null;
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
            int id = entities.size();
            e.setId(id);
            entities.add(e);
            entitiesByName.put(e.getName(), e);
        }
    }

    @Override
    public boolean updateEntityName(final String oldName){
        SimEntity entity = entitiesByName.remove(oldName);
        if(entity != null){
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
        if (e == null) {
            throw new IllegalArgumentException("Adding null entity.");
        } else {
            printMessage("Adding: " + e.getName());
        }
        e.startEntity();
    }

    /**
     * Run one tick of the simulation, processing and removing the
     * events in the {@link #future future event queue}.
     *
     * @return true if the event queue was empty at the beginning of the
     * method execution, false otherwise
     */
    private boolean runClockTickAndProcessFutureEventQueue() {
        executeRunnableEntities();

        // If there are more future events, then deal with them
        boolean queueWasEmpty = future.isEmpty();
        if (!queueWasEmpty) {
            future.stream().findFirst().ifPresent(this::processAllFutureEventsHappeningAtSameTimeOfTheFirstOne);
        } else {
            running = false;
            printMessage("Simulation: No more future events");
        }

        return queueWasEmpty;
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

        //dont use stream because the entities are being changed
        for(SimEntity ent: runableEntities) {
            ent.run();
        }
    }

    @Override
    public void runStop() {
        printMessage("Simulation completed.");
    }

    @Override
    public void hold(int src, long delay) {
        SimEvent e = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(e);
        entities.get(src).setState(SimEntity.State.HOLDING);
    }

    @Override
    public void pause(int src, double delay) {
        SimEvent e = new CloudSimEvent(this, SimEvent.Type.HOLD_DONE, clock + delay, src);
        future.addEvent(e);
        entities.get(src).setState(SimEntity.State.HOLDING);
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
    public void wait(int src, Predicate p) {
        entities.get(src).setState(SimEntity.State.WAITING);
        if (p != SIM_ANY) {
            // If a predicate has been used store it in order to check it
            waitPredicates.put(src, p);
        }
    }

    @Override
    public int waiting(int d, Predicate p) {
        int count = 0;
        SimEvent event;
        Iterator<SimEvent> iterator = deferred.iterator();
        while (iterator.hasNext()) {
            event = iterator.next();
            if ((event.getDestination() == d) && (p.match(event))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public SimEvent select(int src, Predicate p) {
        SimEvent ev = null;
        Iterator<SimEvent> iterator = deferred.iterator();
        while (iterator.hasNext()) {
            ev = iterator.next();
            if (ev.getDestination() == src && p.match(ev)) {
                iterator.remove();
                break;
            }
        }
        return ev;
    }

    @Override
    public SimEvent findFirstDeferred(int src, Predicate p) {
        SimEvent ev = null;
        Iterator<SimEvent> iterator = deferred.iterator();
        while (iterator.hasNext()) {
            ev = iterator.next();
            if (ev.getDestination() == src && p.match(ev)) {
                break;
            }
        }
        return ev;
    }

    @Override
    public SimEvent cancel(int src, Predicate p) {
        SimEvent ev = null;
        Iterator<SimEvent> iter = future.iterator();
        while (iter.hasNext()) {
            ev = iter.next();
            if (ev.getSource() == src && p.match(ev)) {
                iter.remove();
                break;
            }
        }

        return ev;
    }

    @Override
    public boolean cancelAll(int src, Predicate p) {
        SimEvent ev = null;
        int previousSize = future.size();
        Iterator<SimEvent> iter = future.iterator();
        while (iter.hasNext()) {
            ev = iter.next();
            if (ev.getSource() == src && p.match(ev)) {
                iter.remove();
            }
        }
        return previousSize < future.size();
    }

    /**
     * Processes an event.
     *
     * @param e the e
     */
    private void processEvent(SimEvent e) {
        int dest, src;
        CloudSimEntity dest_ent;
        // Update the system's clock
        if (e.eventTime() < clock) {
            throw new IllegalArgumentException("Past event detected.");
        }
        clock = e.eventTime();
        onEventProcessingListener.update(e);

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
                    int tag = e.getTag();
                    dest_ent = entities.get(dest);
                    if (dest_ent.getState() == SimEntity.State.WAITING) {
                        Integer destObj = dest;
                        Predicate p = waitPredicates.get(destObj);
                        if ((p == null) || (tag == 9999) || (p.match(e))) {
                            dest_ent.setEventBuffer(new CloudSimEvent(e));
                            dest_ent.setState(SimEntity.State.RUNNABLE);
                            waitPredicates.remove(destObj);
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

    @Override
    public void runStart() {
        running = true;
        // Start all the entities
        for (SimEntity ent : entities) {
            ent.startEntity();
        }

        printMessage("Entities started.");
    }

    @Override
    public boolean running() {
        return running;
    }

    @Override
    public boolean pause() {
        paused = true;
        return paused;
    }

    @Override
    public boolean pause(long time) {
        if (time <= clock) {
            return false;
        } else {
            pauseAt = time;
        }
        return true;
    }

    @Override
    public boolean resume() {
        paused = false;

        if (pauseAt <= clock) {
            pauseAt = -1;
        }

        return !paused;
    }

    /**
     * Starts the simulation execution. This should be called after all the
     * entities have been setup and added, and their ports linked.
     * The method blocks until the simulation is ended.
     *
     * @return the last clock value
     */
    private double run() {
        if (!running) {
            runStart();
        }

        while (true) {
            if (runClockTickAndProcessFutureEventQueue() || abruptTerminate) {
                break;
            }

            // this block allows termination of simulation at a specific time
            if (terminateAt > 0.0 && clock >= terminateAt) {
                terminate();
                clock = terminateAt;
                break;
            }

            if (pauseAt != -1
                    && ((future.size() > 0 && clock <= pauseAt && pauseAt <= future.iterator().next()
                    .eventTime()) || future.size() == 0 && pauseAt <= clock)) {
                pause();
                clock = pauseAt;
            }

            while (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        double lastSimulationTime = clock();

        finishSimulation();
        runStop();

        return lastSimulationTime;
    }

    @Override
    public void finishSimulation() {
        // Allow all entities to exit their body method
        if (!abruptTerminate) {
            for (CloudSimEntity ent : entities) {
                if (ent.getState() != SimEntity.State.FINISHED) {
                    ent.run();
                }
            }
        }

        for (SimEntity ent : entities) {
            ent.shutdownEntity();
        }
    }

    @Override
    public void abruptallyTerminate() {
        abruptTerminate = true;
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
    public EventListener<SimEvent> getOnEventProcessingListener() {
        return onEventProcessingListener;
    }

    @Override
    public Simulation setOnEventProcessingListener(EventListener<SimEvent> onEventProcessingListener) {
        if(onEventProcessingListener == null)
            onEventProcessingListener = EventListener.NULL;

        this.onEventProcessingListener = onEventProcessingListener;
        return this;
    }

    @Override
    public NetworkTopology getNetworkTopology() {
        return networkTopology;
    }

    @Override
    public void setNetworkTopology(NetworkTopology networkTopology) {
        this.networkTopology = networkTopology;
    }
}
