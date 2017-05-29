/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.util.Log;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a simulation entity. An entity handles events and can
 * send events to other entities.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudSimEntity implements SimEntity {
    /**
     * @see #isStarted()
     */
    private boolean started;

    /**
     * @see #getSimulation()
     */
    private Simulation simulation;

    private String name;

    /**
     * The entity id.
     */
    private int id;

    /**
     * The buffer for selected incoming events.
     */
    private SimEvent buffer;

    /**
     * The entity's current state.
     */
    private State state;

    private boolean log;

    /**
     * Creates a new entity.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @throws IllegalArgumentException when the entity name is invalid
     */
    public CloudSimEntity(Simulation simulation) {
        setSimulation(simulation);
        id = -1;
        state = State.RUNNABLE;
        name = String.format("%s%d", getClass().getSimpleName(), this.simulation.getNumEntities());
        this.simulation.addEntity(this);
        this.started = false;
        this.log = true;
    }

    /**
     * Gets the name of this entity.
     *
     * @return The entity's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the unique id number assigned to this entity.
     *
     * @return The id number
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}.
     * It performs general initialization tasks that are common for every entity
     * and executes the specific entity startup code by calling {@link #startEntity()}.
     *
     * @see #startEntity()
     */
    @Override
    public void start() {
        startEntity();
        this.setStarted(true);
    }

    /**
     * Defines the logic to be performed by the entity when the simulation starts.
     */
    protected abstract void startEntity();

    /**
     * Sends an event to another entity by id number, with data. Note that the
     * tag <code>9999</code> is reserved.
     *
     * @param dest  The unique id number of the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     * @param data  The data to be sent with the event.
     */
    public void schedule(int dest, double delay, int tag, Object data) {
        if (!simulation.isRunning()) {
            return;
        }
        simulation.send(id, dest, delay, tag, data);
    }

    @Override
    public void schedule(int dest, double delay, int tag) {
        schedule(dest, delay, tag, null);
    }

    /**
     * Sends an event to another entity through a port with a given name, with
     * data. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The name of the port to send the event through
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     * @param data  The data to be sent with the event.
     */
    public void schedule(String dest, double delay, int tag, Object data) {
        schedule(simulation.getEntityId(dest), delay, tag, data);
    }

    /**
     * Sends an event to another entity through a port with a given name, with
     * <b>no</b> data. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The name of the port to send the event through
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     */
    public void schedule(String dest, double delay, int tag) {
        schedule(dest, delay, tag, null);
    }

    /**
     * Sends an event to another entity by id number, with data but no delay.
     * Note that the tag <code>9999</code> is reserved.
     *
     * @param dest The unique id number of the destination entity
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleNow(int dest, int tag, Object data) {
        schedule(dest, 0, tag, data);
    }

    /**
     * Sends an event to another entity by id number and with <b>no</b> data and
     * no delay. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest The unique id number of the destination entity
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleNow(int dest, int tag) {
        schedule(dest, 0, tag, null);
    }

    /**
     * Sends an event to another entity through a port with a given name, with
     * data but no delay. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest The name of the port to send the event through
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleNow(String dest, int tag, Object data) {
        schedule(simulation.getEntityId(dest), 0, tag, data);
    }

    /**
     * Send an event to another entity through a port with a given name, with
     * <b>no</b> data and no delay. Note that the tag <code>9999</code> is
     * reserved.
     *
     * @param dest The name of the port to send the event through
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleNow(String dest, int tag) {
        schedule(dest, 0, tag, null);
    }

    /**
     * Sends a high priority event to another entity by id number, with data.
     * Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The unique id number of the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     * @param data  The data to be sent with the event.
     */
    public void scheduleFirst(int dest, double delay, int tag, Object data) {
        if (!simulation.isRunning()) {
            return;
        }
        simulation.sendFirst(id, dest, delay, tag, data);
    }

    /**
     * Sends a high priority event to another entity by id number and with
     * <b>no</b> data. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The unique id number of the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     */
    public void scheduleFirst(int dest, double delay, int tag) {
        scheduleFirst(dest, delay, tag, null);
    }

    /**
     * Sends a high priority event to another entity through a port with a given
     * name, with data. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The name of the port to send the event through
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     * @param data  The data to be sent with the event.
     */
    public void scheduleFirst(String dest, double delay, int tag, Object data) {
        scheduleFirst(simulation.getEntityId(dest), delay, tag, data);
    }

    /**
     * Sends a high priority event to another entity through a port with a given
     * name, with <b>no</b>
     * data. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest  The name of the port to send the event through
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     */
    public void scheduleFirst(String dest, double delay, int tag) {
        scheduleFirst(dest, delay, tag, null);
    }

    /**
     * Sends a high priority event to another entity by id number, with data and
     * no delay. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest The unique id number of the destination entity
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleFirstNow(int dest, int tag, Object data) {
        scheduleFirst(dest, 0, tag, data);
    }

    /**
     * Sends a high priority event to another entity by id number and with
     * <b>no</b> data and no delay. Note that the tag <code>9999</code> is
     * reserved.
     *
     * @param dest The unique id number of the destination entity
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleFirstNow(int dest, int tag) {
        scheduleFirst(dest, 0, tag, null);
    }

    /**
     * Sends a high priority event to another entity through a port with a given
     * name, with data and no delay. Note that the tag <code>9999</code> is
     * reserved.
     *
     * @param dest The name of the port to send the event through
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleFirstNow(String dest, int tag, Object data) {
        scheduleFirst(simulation.getEntityId(dest), 0, tag, data);
    }

    /**
     * Sends a high priority event to another entity through a port with a given
     * name, with <b>no</b>
     * data and no delay. Note that the tag <code>9999</code> is reserved.
     *
     * @param dest The name of the port to send the event through
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleFirstNow(String dest, int tag) {
        scheduleFirst(dest, 0, tag, null);
    }

    /**
     * Sets the entity to be inactive for a time period.
     *
     * @param delay the time period for which the entity will be inactive
     */
    public void pause(double delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Negative delay supplied.");
        }
        if (!simulation.isRunning()) {
            return;
        }
        simulation.pauseEntity(id, delay);
    }

    /**
     * Counts how many events matching a predicate are waiting in the entity's
     * deferred queue.
     *
     * @param p The event selection predicate
     * @return The count of matching events
     */
    public long numEventsWaiting(Predicate<SimEvent> p) {
        return simulation.waiting(id, p);
    }

    /**
     * Counts how many events are waiting in the entity's deferred queue.
     *
     * @return The count of events
     */
    public long numEventsWaiting() {
        return simulation.waiting(id, Simulation.SIM_ANY);
    }

    /**
     * Extracts the first event matching a predicate waiting in the entity's
     * deferred queue.
     *
     * @param p The event selection predicate
     * @return the simulation event
     */
    public SimEvent selectEvent(Predicate<SimEvent> p) {
        if (!simulation.isRunning()) {
            return null;
        }

        return simulation.select(id, p);
    }

    /**
     * Cancels the first event from the future event queue that matches a given predicate
     * and that was submitted by this entity, then removes it from the queue.
     *
     * @param p the event selection predicate
     * @return the removed event or {@link SimEvent#NULL} if not found
     */
    public SimEvent cancelEvent(Predicate<SimEvent> p) {
        return (simulation.isRunning() ? simulation.cancel(id, p) : SimEvent.NULL);
    }

    /**
     * Gets the first event matching a predicate from the deferred queue, or if
     * none match, wait for a matching event to arrive.
     *
     * @param p The predicate to match
     * @return the simulation event
     */
    public SimEvent getNextEvent(Predicate<SimEvent> p) {
        if (!simulation.isRunning()) {
            return null;
        }

        if (numEventsWaiting(p) > 0) {
            return selectEvent(p);
        }

        return null;
    }

    /**
     * Gets the first event waiting in the entity's deferred queue, or if there
     * are none, wait for an event to arrive.
     *
     * @return the simulation event
     */
    public SimEvent getNextEvent() {
        return getNextEvent(Simulation.SIM_ANY);
    }

    /**
     * Waits for an event matching a specific predicate. This method does not
     * check the entity's deferred queue.
     *
     * @param p The predicate to match
     */
    public void waitForEvent(Predicate<SimEvent> p) {
        if (!simulation.isRunning()) {
            return;
        }

        simulation.wait(this, p);
        state = State.WAITING;
    }

    @Override
    public void run() {
        SimEvent ev = buffer != null ? buffer : getNextEvent();

        while (ev != null) {
            processEvent(ev);
            if (state != State.RUNNABLE) {
                break;
            }

            ev = getNextEvent();
        }

        buffer = null;
    }

    /**
     * Gets a clone of the entity. This is used when independent replications
     * have been specified as an output analysis method. Clones or backups of
     * the entities are made in the beginning of the simulation in order to
     * reset the entities for each subsequent replication. This method should
     * not be called by the user.
     *
     * @return A clone of the entity
     * @throws CloneNotSupportedException when the entity doesn't support
     *                                    cloning
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        CloudSimEntity copy = (CloudSimEntity) super.clone();
        copy.setName(name);
        copy.setSimulation(simulation);
        copy.setEventBuffer(null);
        return copy;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public final SimEntity setSimulation(Simulation simulation) {
        if(Objects.isNull(simulation)){
            simulation = Simulation.NULL;
        }

        this.simulation = simulation;
        return this;
    }

    @Override
    public SimEntity setName(final String newName) throws IllegalArgumentException {
        if (Objects.isNull(newName)) {
            throw new IllegalArgumentException("Entity names can't be null.");
        }
        if (newName.contains(" ")) {
            throw new IllegalArgumentException("Entity names can't contain spaces.");
        }

        if (newName.trim().equals("")) {
            throw new IllegalArgumentException("Entity names can't be empty.");
        }

        final String oldName = this.name;
        this.name = newName;
        simulation.updateEntityName(oldName);
        return this;
    }

    /**
     * Gets the entity state.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the event buffer.
     *
     * @return the event buffer
     */
    protected SimEvent getEventBuffer() {
        return buffer;
    }

    /**
     * Sets the entity state.
     *
     * @param state the new state
     */
    public SimEntity setState(State state) {
        this.state = state;
        return this;
    }

    /**
     * Sets the entity id.
     *
     * @param id the new id
     */
    protected void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the event buffer.
     *
     * @param e the new event buffer
     */
    protected void setEventBuffer(SimEvent e) {
        buffer = e;
    }

    // --------------- EVENT / MESSAGE SEND WITH NETWORK DELAY METHODS ------------------

    /**
     * Sends an event/message to another entity by <tt>delaying</tt> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param destEntityId    the id number of the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @param data        A reference to data to be sent with the event
     * @pre entityID > 0
     * @pre delay >= 0.0
     * @pre data != null
     * @post $none
     */
    protected void send(int destEntityId, double delay, int cloudSimTag, Object data) {
        if (destEntityId < 0) {
            return;
        }

        // if delay is -ve, then it doesn't make sense. So resets to 0.0
        if (delay < 0) {
            delay = 0;
        }

        if (Double.isInfinite(delay)) {
            throw new IllegalArgumentException("The specified delay is infinite value");
        }

        if (destEntityId < 0) {
            Log.printConcatLine(getName(), ".send(): Error - " + "invalid entity id ", destEntityId);
            return;
        }

        final int srcId = getId();
        if (destEntityId != srcId) {// only delay messages between different entities
            delay += getNetworkDelay(srcId, destEntityId);
        }

        schedule(destEntityId, delay, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity by <tt>delaying</tt> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param destEntityId    the id number of the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @pre entityID > 0
     * @pre delay >= 0.0
     * @post $none
     */
    protected void send(int destEntityId, double delay, int cloudSimTag) {
        send(destEntityId, delay, cloudSimTag, null);
    }

    /**
     * Sends an event/message to another entity by <tt>delaying</tt> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param destEntityName  the name of the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @param data        A reference to data to be sent with the event
     * @pre entityName != null
     * @pre delay >= 0.0
     * @pre data != null
     * @post $none
     */
    protected void send(String destEntityName, double delay, int cloudSimTag, Object data) {
        send(simulation.getEntityId(destEntityName), delay, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity by <tt>delaying</tt> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param destEntityName  the name of the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @pre entityName != null
     * @pre delay >= 0.0
     * @post $none
     */
    protected void send(String destEntityName, double delay, int cloudSimTag) {
        send(destEntityName, delay, cloudSimTag, null);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param destEntityId    the id number of the destination entity
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @param data        A reference to data to be sent with the event
     * @pre entityID > 0
     * @pre delay >= 0.0
     * @pre data != null
     * @post $none
     */
    protected void sendNow(int destEntityId, int cloudSimTag, Object data) {
        send(destEntityId, 0, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param destEntityId    the id number of the destination entity
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @pre entityID > 0
     * @pre delay >= 0.0
     * @post $none
     */
    protected void sendNow(int destEntityId, int cloudSimTag) {
        send(destEntityId, 0, cloudSimTag, null);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param destEntityName  the name of the destination entity
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @param data        A reference to data to be sent with the event
     * @pre entityName != null
     * @pre delay >= 0.0
     * @pre data != null
     * @post $none
     */
    protected void sendNow(String destEntityName, int cloudSimTag, Object data) {
        send(simulation.getEntityId(destEntityName), 0, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param destEntityName  the name of the destination entity
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @pre entityName != null
     * @pre delay >= 0.0
     * @post $none
     */
    protected void sendNow(String destEntityName, int cloudSimTag) {
        send(destEntityName, 0, cloudSimTag, null);
    }

    /**
     * Gets the network delay associated to the sent of a message from a given
     * source to a given destination.
     *
     * @param src source of the message
     * @param dst destination of the message
     * @return delay to send a message from src to dst
     * @pre src >= 0
     * @pre dst >= 0
     */
    private double getNetworkDelay(int src, int dst) {
        return getSimulation().getNetworkTopology().getDelay(src, dst);
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * Defines if the entity has already started or not.
     *
     * @param started the start state to set
     */
    protected void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public int compareTo(SimEntity o) {
        return Integer.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CloudSimEntity that = (CloudSimEntity) o;

        if (id != that.id) return false;
        return simulation.equals(that.simulation);
    }

    @Override
    public int hashCode() {
        int result = simulation.hashCode();
        result = 31 * result + id;
        return result;
    }

    @Override
    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public void println(String msg){
        if(log){
            Log.printLine(msg);
        }
    }
}
