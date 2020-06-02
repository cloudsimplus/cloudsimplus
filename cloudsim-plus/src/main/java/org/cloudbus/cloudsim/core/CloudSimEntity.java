/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.apache.commons.lang3.StringUtils;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudSimEntity.class.getSimpleName());

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
    private long id;

    /**
     * The buffer for selected incoming events.
     */
    private SimEvent buffer;

    /**
     * The entity's current state.
     */
    private State state;

    /**
     * Creates a new entity.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @throws IllegalArgumentException when the entity name is invalid
     */
    public CloudSimEntity(final Simulation simulation) {
        setSimulation(simulation);
        setId(-1);
        state = State.RUNNABLE;
        this.simulation.addEntity(this);
        this.started = false;
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
    public long getId() {
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

    @Override
    public void shutdownEntity() {
        setState(State.FINISHED);
    }

    /**
     * Defines the logic to be performed by the entity when the simulation starts.
     */
    protected abstract void startEntity();

    @Override
    public boolean schedule(final SimEntity dest, final double delay, final int tag, final Object data) {
        return schedule(new CloudSimEvent(delay, this, dest, tag, data));
    }

    @Override
    public boolean schedule(final double delay, final int tag, final Object data) {
        return schedule(this, delay, tag, data);
    }

    @Override
    public boolean schedule(final double delay, final int tag) {
        return schedule(this, delay, tag, null);
    }

    @Override
    public boolean schedule(final SimEntity dest, final double delay, final int tag) {
        return schedule(dest, delay, tag, null);
    }

    @Override
    public boolean schedule(final int tag, final Object data) {
        return schedule(this, 0, tag, data);
    }

    @Override
    public boolean schedule(final SimEvent evt) {
        if (!canSendEvent(evt)) {
            return false;
        }
        simulation.send(evt);
        return true;
    }

    private boolean canSendEvent(final SimEvent evt) {
        /**
         * If the simulation has finished and an  {@link CloudSimTags#END_OF_SIMULATION}
         * message is sent, it has to be processed to enable entities to shutdown.
         */
        if (!simulation.isRunning() && evt.getTag() != CloudSimTags.END_OF_SIMULATION) {
            LOGGER.warn(
                "{}: {}: Cannot send events before simulation starts or after it finishes. Trying to send message {} to {}",
                getSimulation().clockStr(), this, evt.getTag(), evt.getDestination());
            return false;
        }

        return true;
    }

    /**
     * Sends an event to another entity with no delay.
     *
     * @param dest the destination entity
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleNow(final SimEntity dest, final int tag, final Object data) {
        schedule(dest, 0, tag, data);
    }

    /**
     * Sends an event to another entity with <b>no</b> attached data and no delay.
     *
     * @param dest the destination entity
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleNow(final SimEntity dest, final int tag) {
        schedule(dest, 0, tag, null);
    }

    /**
     * Sends a high priority event to another entity with no delay.
     *
     * @param dest the destination entity
     * @param tag  An user-defined number representing the type of event.
     * @param data The data to be sent with the event.
     */
    public void scheduleFirstNow(final SimEntity dest, final int tag, final Object data) {
        scheduleFirst(dest, 0, tag, data);
    }

    /**
     * Sends a high priority event to another entity with <b>no</b> attached data and no delay.
     * @param dest the destination entity
     * @param tag  An user-defined number representing the type of event.
     */
    public void scheduleFirstNow(final SimEntity dest, final int tag) {
        scheduleFirst(dest, 0, tag, null);
    }

    /**
     * Sends a high priority event to another entity and with <b>no</b> attached data.
     *
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     */
    public void scheduleFirst(final SimEntity dest, final double delay, final int tag) {
        scheduleFirst(dest, delay, tag, null);
    }

    /**
     * Sends a high priority event to another entity.
     *
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   An user-defined number representing the type of event.
     * @param data  The data to be sent with the event.
     */
    public void scheduleFirst(final SimEntity dest, final double delay, final int tag, final Object data) {
        final CloudSimEvent evt = new CloudSimEvent(delay, this, dest, tag, data);
        if (!canSendEvent(evt)) {
            return;
        }

        simulation.sendFirst(evt);
    }

    /**
     * Sets the entity to be inactive for a time period.
     *
     * @param delay the time period for which the entity will be inactive
     */
    public void pause(final double delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Negative delay supplied.");
        }

        if (!simulation.isRunning()) {
            return;
        }

        simulation.pauseEntity(this, delay);
    }

    /**
     * Extracts the first event matching a predicate waiting in the entity's
     * deferred queue.
     *
     * @param predicate The event selection predicate
     * @return the simulation event; or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent selectEvent(final Predicate<SimEvent> predicate) {
        if (!simulation.isRunning()) {
            return SimEvent.NULL;
        }

        return simulation.select(this, predicate);
    }

    /**
     * Cancels the first event from the future event queue that matches a given predicate
     * and that was submitted by this entity, then removes it from the queue.
     *
     * @param predicate the event selection predicate
     * @return the removed event or {@link SimEvent#NULL} if not found
     */
    public SimEvent cancelEvent(final Predicate<SimEvent> predicate) {
        return simulation.isRunning() ? simulation.cancel(this, predicate) : SimEvent.NULL;
    }

    /**
     * Gets the first event matching a predicate from the deferred queue, or if
     * none match, wait for a matching event to arrive.
     *
     * @param predicate The predicate to match
     * @return the simulation event; or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent getNextEvent(final Predicate<SimEvent> predicate) {
        if (!simulation.isRunning()) {
            return SimEvent.NULL;
        }

        return selectEvent(predicate);
    }

    /**
     * Gets the first event waiting in the entity's deferred queue, or if there
     * are none, wait for an event to arrive.
     *
     * @return the simulation event; or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent getNextEvent() {
        return getNextEvent(Simulation.ANY_EVT);
    }

    /**
     * Waits for an event matching a specific predicate. This method does not
     * check the entity's deferred queue.
     *
     * @param predicate The predicate to match
     */
    public void waitForEvent(final Predicate<SimEvent> predicate) {
        if (!simulation.isRunning()) {
            return;
        }

        simulation.wait(this, predicate);
        state = State.WAITING;
    }

    @Override
    public void run() {
        run(Double.MAX_VALUE);
    }

    public void run(final double until) {
        SimEvent evt = buffer == null ? getNextEvent(e -> e.getTime() <= until) : buffer;

        while (evt != SimEvent.NULL) {
            processEvent(evt);
            if (state != State.RUNNABLE) {
                break;
            }

            evt = getNextEvent(e -> e.getTime() <= until);
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
     * @throws CloneNotSupportedException when the entity doesn't support cloning
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        final CloudSimEntity copy = (CloudSimEntity) super.clone();
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
    public final SimEntity setSimulation(final Simulation simulation) {
        this.simulation = Objects.requireNonNull(simulation);
        return this;
    }

    @Override
    public SimEntity setName(final String name) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Entity names cannot be empty.");
        }

        if (name.contains(" ")) {
            throw new IllegalArgumentException("Entity names cannot contain spaces.");
        }

        this.name = name;
        return this;
    }

    @Override
    public State getState() {
        return state;
    }

    /**
     * Sets the entity state.
     *
     * @param state the new state
     */
    @Override
    public SimEntity setState(final State state) {
        this.state = state;
        return this;
    }

    /**
     * Sets the entity id and defines its name based on such ID.
     *
     * @param id the new id
     */
    protected final void setId(final int id) {
        this.id = id;
        setAutomaticName();
    }

    /**
     * Sets an automatic generated name for the entity.
     */
    private void setAutomaticName() {
        final long id = this.id >= 0 ? this.id : this.simulation.getNumEntities();
        this.name = String.format("%s%d", getClass().getSimpleName(), id);
    }

    /**
     * Sets the event buffer.
     *
     * @param evt the new event buffer
     */
    protected void setEventBuffer(final SimEvent evt) {
        buffer = evt;
    }

    // --------------- EVENT / MESSAGE SEND WITH NETWORK DELAY METHODS ------------------

    /**
     * Sends an event/message to another entity by <b>delaying</b> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param dest the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an event/message
     * @param data        A reference to data to be sent with the event
     */
    protected void send(final SimEntity dest, double delay, final int cloudSimTag, final Object data) {
        Objects.requireNonNull(dest);
        if (dest.getId() < 0) {
            LOGGER.error("{}.send(): invalid entity id {} for {}", getName(), dest.getId(), dest);
            return;
        }

        // if delay is negative, then it doesn't make sense. So resets to 0.0
        if (delay < 0) {
            delay = 0;
        }

        if (Double.isInfinite(delay)) {
            throw new IllegalArgumentException("The specified delay is infinite value");
        }

        // only considers network delay when sending messages between different entities
        if (dest.getId() != getId()) {
            delay += getNetworkDelay(getId(), dest.getId());
        }

        schedule(dest, delay, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity by <b>delaying</b> the
     * simulation time from the current time, with a tag representing the event
     * type.
     *
     * @param dest    the destination entity
     * @param delay       How many seconds after the current simulation time the event should be sent.
     *                    If delay is a negative number, then it will be changed to 0
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     */
    protected void send(final SimEntity dest, final double delay, final int cloudSimTag) {
        send(dest, delay, cloudSimTag, null);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param dest    the destination entity
     * @param cloudSimTag an user-defined number representing the type of an
     *                    event/message
     * @param data        A reference to data to be sent with the event
     */
    protected void sendNow(final SimEntity dest, final int cloudSimTag, final Object data) {
        send(dest, 0, cloudSimTag, data);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     *
     * @param dest    the destination entity
     * @param cloudSimTag an user-defined number representing the type of an event/message
     */
    protected void sendNow(final SimEntity dest, final int cloudSimTag) {
        send(dest, 0, cloudSimTag, null);
    }

    /**
     * Gets the network delay associated to the sent of a message from a given
     * source to a given destination.
     *
     * @param src source of the message
     * @param dst destination of the message
     * @return delay to send a message from src to dst
     */
    private double getNetworkDelay(final long src, final long dst) {
        return getSimulation().getNetworkTopology().getDelay(src, dst);
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isAlive() {
        return state != State.FINISHED;
    }

    @Override
    public boolean isFinished() {
        return state == State.FINISHED;
    }

    /**
     * Defines if the entity has already started or not.
     *
     * @param started the start state to set
     */
    protected void setStarted(final boolean started) {
        this.started = started;
    }

    @Override
    public int compareTo(final SimEntity entity) {
        return Long.compare(this.getId(), entity.getId());
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final CloudSimEntity that = (CloudSimEntity) object;

        if (id != that.id) return false;
        return simulation.equals(that.simulation);
    }

    @Override
    public int hashCode() {
        int result = simulation.hashCode();
        result = 31 * result + Long.hashCode(id);
        return result;
    }
}
