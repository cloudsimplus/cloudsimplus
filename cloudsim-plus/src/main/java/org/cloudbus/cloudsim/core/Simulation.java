package org.cloudbus.cloudsim.core;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.core.predicates.Predicate;
import org.cloudbus.cloudsim.core.predicates.PredicateAny;
import org.cloudbus.cloudsim.core.predicates.PredicateNone;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudsimplus.listeners.EventListener;

/**
 * An interface to be implemented by a class that manages simulation
 * execution, controlling all the simulation lifecycle.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudSim
 */
public interface Simulation {
    // The two standard predicates
    /**
     * A standard predicate that matches any event.
     */
    PredicateAny SIM_ANY = new PredicateAny();

    /**
     * A standard predicate that does not match any events.
     */
    PredicateNone SIM_NONE = new PredicateNone();


    /**
     * Abruptally terminate.
     */
    void abruptallyTerminate();

    /**
     * Adds a new entity to the simulation. Each {@link CloudSimEntity} object
     * register itself when it is instantiated.
     *
     * @param e The new entity
     */
    void addEntity(CloudSimEntity e);

    /**
     * Removes an event from the event queue.
     *
     * @param src Id of entity who scheduled the event.
     * @param p the p
     * @return the sim event
     */
    SimEvent cancel(int src, Predicate p);

    /**
     * Removes all events that match a given predicate from the future event
     * queue returns true if at least one event has been cancelled; false
     * otherwise.
     *
     * @param src Id of entity who scheduled the event.
     * @param p the p
     * @return true, if successful
     */
    boolean cancelAll(int src, Predicate p);

    /**
     * Get the current simulation time.
     *
     * @return the simulation time
     */
    double clock();

    /**
     * Find first deferred event matching a predicate.
     *
     * @param src Id of entity who scheduled the event.
     * @param p the p
     * @return the sim event
     */
    SimEvent findFirstDeferred(int src, Predicate p);

    /**
     * Internal method that allows the entities to terminate. This method should
     * <b>not</b> be used in user simulations.
     */
    void finishSimulation();

    /**
     * Gets a new copy of initial simulation Calendar.
     *
     * @return a new copy of Calendar object
     * @pre $none
     * @post $none
     */
    Calendar getCalendar();

    /**
     * Gets the entity ID of {@link CloudInformationService}.
     *
     * @return the Entity ID or if it is not found
     * @pre $none
     * @post $result >= -1
     */
    int getCloudInfoServiceEntityId();

    /**
     * Sends a request to Cloud Information Service (CIS) entity to get the list
     * of all Cloud Datacenter IDs.
     *
     * @return a List containing Datacenter IDs
     * @pre $none
     * @post $none
     */
    List<Integer> getDatacenterIdsList();

    /**
     * Get the entity with a given id.
     *
     * @param id the entity's unique id number
     * @return The entity, or if it could not be found
     */
    SimEntity getEntity(int id);

    /**
     * Get the entity with a given name.
     *
     * @param name The entity's name
     * @return The entity
     */
    SimEntity getEntity(String name);

    /**
     * Get the id of an entity with a given name.
     *
     * @param name The entity's name
     * @return The entity's unique id number
     */
    int getEntityId(String name);

    /**
     * Returns a read-only list of entities created for the simulation.
     *
     * @return
     */
    List<SimEntity> getEntityList();

    /**
     * Gets name of the entity given its entity ID.
     *
     * @param entityId the entity ID
     * @return the Entity name or if this object does not have one
     * @pre entityId > 0
     * @post $none
     */
    String getEntityName(int entityId);

    /**
     * Gets name of the entity given its entity ID.
     *
     * @param entityID the entity ID
     * @return the Entity name or if this object does not have one
     * @pre entityID > 0
     * @post $none
     */
    String getEntityName(Integer entityID);

    /**
     * Returns the minimum time between events. Events within shorter periods
     * after the last event are discarded.
     *
     * @return the minimum time between events.
     */
    double getMinTimeBetweenEvents();

    /**
     * Get the current number of entities in the simulation.
     *
     * @return The number of entities
     */
    int getNumEntities();

    /**
     * Gets the {@link EventListener} object that will be notified when any event
     * is processed by CloudSim.
     *
     * @return the EventListener.
     * @see #processEvent(SimEvent)
     */
    EventListener<SimEvent> getOnEventProcessingListener();

    /**
     * Used to hold an entity for some time.
     *
     * @param src Id of entity who scheduled the event
     * @param delay How many seconds after the current time the entity has to be held
     */
    void hold(int src, long delay);

    /**
     * Checks if is paused.
     *
     * @return true, if is paused
     */
    boolean isPaused();

    /**
     * Used to pause an entity for some time.
     *
     * @param src Id of entity who scheduled the event
     * @param delay the time period for which the entity will be inactive
     */
    void pause(int src, double delay);

    /**
     * This method is called if one wants to pause the simulation.
     *
     * @return true, if successful otherwise.
     */
    boolean pause();

    /**
     * This method is called if one wants to pause the simulation at a given
     * time.
     *
     * @param time the time at which the simulation has to be paused
     * @return true, if successful otherwise.
     */
    boolean pause(long time);

    /**
     * This method is called if one wants to resume the simulation that has
     * previously been paused.
     *
     * @return if the simulation has been restarted or or otherwise.
     */
    boolean resume();

    /**
     * Internal method used to start the simulation. This method should
     * <b>not</b> be used by user simulations.
     */
    void runStart();

    /**
     * Internal method used to stop the simulation. This method should
     * <b>not</b> be used directly.
     */
    void runStop();

    /**
     * Check if the simulation is still running. This method should be used by
     * entities to check if they should continue executing.
     *
     * @return if the simulation is still running, otherwise
     */
    boolean running();

    /**
     * Selects an event matching a predicate.
     *
     * @param src Id of entity who scheduled the event.
     * @param p the p
     * @return the sim event
     */
    SimEvent select(int src, Predicate p);

    /**
     * Sends an event from one entity to another.
     *
     * @param src Id of entity who scheduled the event.
     * @param dest Id of entity that the event will be sent to
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag the tag
     * @param data the data
     */
    void send(int src, int dest, double delay, int tag, Object data);

    /**
     * Used to send an event from one entity to another, with priority in the
     * queue.
     *
     * @param src Id of entity who scheduled the event.
     * @param dest Id of entity that the event will be sent to
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag the tag
     * @param data the data
     */
    void sendFirst(int src, int dest, double delay, int tag, Object data);

    /**
     * Sends an event from one entity to another without delaying
     * the message.
     *
     * @param src Id of entity who scheduled the event.
     * @param dest Id of entity that the event will be sent to
     * @param tag the tag
     * @param data the data
     */
    void sendNow(int src, int dest, int tag, Object data);

    /**
     * Sets the {@link EventListener} object that will be notified when any event
     * is processed by CloudSim.
     *
     * @param onEventProcessingListener the event listener to be set
     * @see #getOnEventProcessingListener()
     */
    Simulation setOnEventProcessingListener(EventListener<SimEvent> onEventProcessingListener);

    /**
     * Starts the execution of CloudSim simulation. It waits for complete
     * execution of all entities, i.e. until all entities threads reach
     * non-RUNNABLE state or there are no more events in the future event queue.
     * <p>
     * <b>Note</b>: This method should be called after all the entities have
     * been setup and added.
     *
     * @return the last clock time
     * @throws RuntimeException when creating this entity
     * before initialising CloudSim package or this entity name is <tt>null</tt>
     * or empty.
     * @pre $none
     * @post $none
     */
    double start() throws RuntimeException;

    /**
     * Stops Cloud Simulation (based on {@link #runStop()}). This
     * should be only called if any of the user defined entities
     * <b>explicitly</b> want to terminate simulation during execution.
     *
     * @throws RuntimeException This happens when creating this entity before
     * initialising CloudSim package or this entity name is <tt>null</tt> or
     * empty
     * @see #CloudSim(int, Calendar, boolean)
     * @see #runStop()
     * @pre $none
     * @post $none
     */
    void stop() throws RuntimeException;

    /**
     * This method is called if one wants to terminate the simulation.
     *
     * @return true, if successful; false otherwise.
     */
    boolean terminate();

    /**
     * This method is called if one wants to terminate the simulation at a given
     * time.
     *
     * @param time the time at which the simulation has to be terminated
     * @return true, if successful otherwise.
     */
    boolean terminateAt(double time);

    /**
     * Sets an entity's state to be waiting. The predicate used to wait for an
     * event is now passed to Sim_system. Only events that satisfy the predicate
     * will be passed to the entity. This is done to avoid unnecessary context
     * switches.
     *
     * @param src Id of entity who scheduled the event.
     * @param p the p
     */
    void wait(int src, Predicate p);


    /**
     * Removes an entity with and old name from the {@link #entitiesByName} map
     * and adds it again using its new name.
     *
     * @param oldName the name the entity had before
     * @return true if the entity was found and changed into the list, false otherwise
     */
    boolean updateEntityName(final String oldName);

    /**
     * Checks if events for a specific entity are present in the deferred event
     * queue.
     *
     * @param d the d
     * @param p the p
     * @return the int
     */
    int waiting(int d, Predicate p);

    /**
     * Gets the network topology used for Network simulations.
     * @return
     */
    NetworkTopology getNetworkTopology();

    /**
     * Sets the network topology used for Network simulations.
     * @param networkTopology the network topology to set
     */
    void setNetworkTopology(NetworkTopology networkTopology);

    Simulation NULL = new Simulation() {
        @Override public void abruptallyTerminate() {}
        @Override public void addEntity(CloudSimEntity e) {}
        @Override public SimEvent cancel(int src, Predicate p) { return SimEvent.NULL; }
        @Override public boolean cancelAll(int src, Predicate p) {
            return false;
        }
        @Override public double clock() {
            return 0;
        }
        @Override public SimEvent findFirstDeferred(int src, Predicate p) { return SimEvent.NULL; }
        @Override public void finishSimulation() {}
        @Override public Calendar getCalendar() {
            return Calendar.getInstance();
        }
        @Override public int getCloudInfoServiceEntityId() {
            return 0;
        }
        @Override public List<Integer> getDatacenterIdsList() {
            return Collections.EMPTY_LIST;
        }
        @Override public SimEntity getEntity(int id) { return SimEntity.NULL; }
        @Override public SimEntity getEntity(String name) { return SimEntity.NULL; }
        @Override public int getEntityId(String name) {
            return 0;
        }
        @Override public List<SimEntity> getEntityList() {
            return Collections.EMPTY_LIST;
        }
        @Override public String getEntityName(int entityId) {
            return "";
        }
        @Override public String getEntityName(Integer entityID) {
            return "";
        }
        @Override public double getMinTimeBetweenEvents() {
            return 0;
        }
        @Override public int getNumEntities() {
            return 0;
        }
        @Override public EventListener<SimEvent> getOnEventProcessingListener() {
            return EventListener.NULL;
        }
        @Override public void hold(int src, long delay) {}
        @Override public boolean isPaused() {
            return false;
        }
        @Override public void pause(int src, double delay) {}
        @Override public boolean pause() {
            return false;
        }
        @Override public boolean pause(long time) {
            return false;
        }
        @Override public boolean resume() {
            return false;
        }
        @Override public void runStart() {}
        @Override public void runStop() {}
        @Override public boolean running() {
            return false;
        }
        @Override public SimEvent select(int src, Predicate p) {
            return SimEvent.NULL;
        }
        @Override public void send(int src, int dest, double delay, int tag, Object data) {}
        @Override public void sendFirst(int src, int dest, double delay, int tag, Object data) {}
        @Override public void sendNow(int src, int dest, int tag, Object data) {}
        @Override public Simulation setOnEventProcessingListener(EventListener<SimEvent> onEventProcessingListener) { return this; }
        @Override public double start() throws RuntimeException { return 0; }
        @Override public void stop() throws RuntimeException {}
        @Override public boolean terminate() {
            return false;
        }
        @Override public boolean terminateAt(double time) {
            return false;
        }
        @Override public void wait(int src, Predicate p) {}
        @Override public int waiting(int d, Predicate p) {
            return 0;
        }
        @Override public NetworkTopology getNetworkTopology() { return NetworkTopology.NULL; }
        @Override public void setNetworkTopology(NetworkTopology networkTopology) {}
        @Override public boolean updateEntityName(String oldName) {
            return false;
        }
    };

}
