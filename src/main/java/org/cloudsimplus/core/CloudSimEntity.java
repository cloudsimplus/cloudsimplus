/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.core.events.SimEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Represents a simulation entity. An entity handles events and can
 * send events to other entities.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class CloudSimEntity implements SimEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudSimEntity.class.getSimpleName());

    @Getter @EqualsAndHashCode.Include
    private long id;
    @Getter @NonNull @EqualsAndHashCode.Include
    private final Simulation simulation;

    @Getter
    private String name;

    @Getter
    private double startTime;

    @Getter
    private double shutdownTime;

    @Getter @Setter
    private State state;

    /**
     * The buffer for selected incoming events.
     */
    private SimEvent buffer;

    /**
     * Creates a new entity.
     *
     * @param simulation The {@link CloudSimPlus} instance that represents the simulation the Entity belongs to
     * @throws IllegalArgumentException when the entity name is invalid
     */
    public CloudSimEntity(@NonNull final Simulation simulation) {
        this.simulation = simulation;
        setId(-1);
        state = State.RUNNABLE;
        this.simulation.addEntity(this);
        this.startTime = -1;
        this.shutdownTime = -1;
    }

    /**
     * {@inheritDoc}.
     * It performs general initialization tasks that are common for every entity
     * and executes the specific entity startup code.
     *
     * @return {@inheritDoc}
     */
    @Override
    public final boolean start() {
        if(this.isStarted()){
            return false;
        }

        startInternal();
        this.startTime = simulation.clock();
        return true;
    }

    @Override
    public void shutdown() {
        if(this.state == State.FINISHED){
            return;
        }

        setState(State.FINISHED);
        this.shutdownTime = simulation.clock();

        /*
         * Since entities never get removed from the entity list, this can create
         * a memory leak with severe performance implications.
         * Here the finished entity is purged from that list
         * to improve the performance of large-scale experiments.
         */
        ((CloudSimPlus)simulation).removeFinishedEntity(this);
    }

    /**
     * Defines the logic to be performed by the entity when the simulation starts.
     */
    protected abstract void startInternal();

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
        if (canSendEvent(evt)) {
            simulation.send(evt);
            return true;
        }

        return false;
    }

    /**
     * If the simulation has finished and a {@link CloudSimTag#SIMULATION_END}
     * message is sent, it has to be processed to enable entities to shut down.
     */
    private boolean canSendEvent(final SimEvent evt) {
        if (simulation.isRunning() || evt.getTag() == CloudSimTag.SIMULATION_END) {
            return true;
        }

        LOGGER.warn(
            "{}: {}: Cannot send events before simulation starts or after it finishes. Trying to send message {} to {}",
            getSimulation().clockStr(), this, evt.getTag(), evt.getDestination());
        return false;
    }

    /**
     * Sends an event to another entity with no delay.
     *
     * @param dest the destination entity
     * @param tag  a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data the data to be sent with the event, according to the tag
     */
    public void scheduleNow(final SimEntity dest, final int tag, final Object data) {
        schedule(dest, 0, tag, data);
    }

    /**
     * Sends an event to another entity with <b>no</b> attached data and no delay.
     *
     * @param dest the destination entity
     * @param tag  a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     */
    public void scheduleNow(final SimEntity dest, final int tag) {
        schedule(dest, 0, tag, null);
    }

    /**
     * Sends a high-priority event to another entity with no delay.
     *
     * @param dest the destination entity
     * @param tag  a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data The data to be sent with the event, according to the tag
     */
    public void scheduleFirstNow(final SimEntity dest, final int tag, final Object data) {
        scheduleFirst(dest, 0, tag, data);
    }

    /**
     * Sends a high-priority event to another entity with <b>no</b> attached data and no delay.
     * @param dest the destination entity
     * @param tag  a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     */
    public void scheduleFirstNow(final SimEntity dest, final int tag) {
        scheduleFirst(dest, 0, tag, null);
    }

    /**
     * Sends a high-priority event to another entity and with <b>no</b> attached data.
     *
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     */
    public void scheduleFirst(final SimEntity dest, final double delay, final int tag) {
        scheduleFirst(dest, delay, tag, null);
    }

    /**
     * Sends a high-priority event to another entity.
     *
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data  The data to be sent with the event, according to the tag
     */
    public void scheduleFirst(final SimEntity dest, final double delay, final int tag, final Object data) {
        final var evt = new CloudSimEvent(delay, this, dest, tag, data);
        if (canSendEvent(evt)) {
            simulation.sendFirst(evt);
        }
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

        if (simulation.isRunning()) {
            simulation.pauseEntity(this, delay);
        }
    }

    /**
     * Extracts the first event matching a predicate waiting in the entity's
     * deferred queue.
     *
     * @param predicate The event selection predicate
     * @return the simulation event;
     *         or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent selectEvent(final Predicate<SimEvent> predicate) {
        if (simulation.isRunning()) {
            return simulation.select(this, predicate);
        }

        return SimEvent.NULL;
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
     * @return the simulation event;
     *         or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent getNextEvent(final Predicate<SimEvent> predicate) {
        if (simulation.isRunning()) {
            return selectEvent(predicate);
        }

        return SimEvent.NULL;
    }

    /**
     * Gets the first event waiting in the entity's deferred queue, or if there
     * are none, wait for an event to arrive.
     *
     * @return the simulation event;
     *         or {@link SimEvent#NULL} if not found or the simulation is not running
     */
    public SimEvent getNextEvent() {
        return getNextEvent(Simulation.ANY_EVT);
    }

    /**
     * Waits for an event matching a specific predicate. This method does not
     * check the entity's deferred queue.
     *
     * @param predicate the predicate to match
     */
    public void waitForEvent(final Predicate<SimEvent> predicate) {
        if (simulation.isRunning()) {
            simulation.wait(this, predicate);
            state = State.WAITING;
        }
    }

    @Override
    public void run() {
        run(Double.MAX_VALUE);
    }

    public void run(final double until) {
        var evt = requireNonNullElse(buffer, getNextEvent(e -> e.getTime() <= until));

        while (evt != SimEvent.NULL) {
            processEvent(evt);
            if (state != State.RUNNABLE) {
                break;
            }

            evt = getNextEvent(e -> e.getTime() <= until);
        }

        buffer = null;
    }

    @Override
    public SimEntity setName(@NonNull final String name) throws IllegalArgumentException {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Entity names cannot be empty.");
        }

        this.name = name;
        return this;
    }

    /**
     * Sets the entity id and defines its name based on it.
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
        this.name = "%s%d".formatted(getClass().getSimpleName(), id);
    }

    /**
     * Sets the event buffer.
     *
     * @param evt the new event buffer
     */
    protected void setEventBuffer(@NonNull final SimEvent evt) {
        this.buffer = evt;
    }

    /**
     * Sends an event/message to another entity by <b>delaying</b> the
     * simulation time from the current time, with a tag representing the event type.
     * @param dest the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent.
     *              If the delay is a negative number, then it will be changed to 0
     * @param tag a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data A reference to data to be sent with the event
     */
    protected void send(final SimEntity dest, double delay, final int tag, final Object data) {
        requireNonNull(dest);
        if (dest.getId() < 0) {
            LOGGER.error("{}.send(): invalid entity id {} for {}", getName(), dest.getId(), dest);
            return;
        }

        // if the delay is negative, then it doesn't make sense. So resets to 0.0
        if (delay < 0) {
            delay = 0;
        }

        if (Double.isInfinite(delay)) {
            throw new IllegalArgumentException("The specified delay is infinite value");
        }

        // only considers network delay when sending messages between different entities
        if (dest.getId() != getId()) {
            delay += getNetworkDelay(this, dest);
        }

        schedule(dest, delay, tag, data);
    }

    /**
     * Sends an event/message to another entity by <b>delaying</b> the
     * simulation time from the current time, with a tag representing the event type.
     * @param dest    the destination entity
     * @param delay   How many seconds after the current simulation time the event should be sent.
     *                If the delay is a negative number, then it will be changed to 0
     * @param tag a tag representing the type of event, according to the {@link CloudSimTag} or some custom one
     */
    protected void send(final SimEntity dest, final double delay, final int tag) {
        send(dest, delay, tag, null);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the
     * event type.
     * @param dest    the destination entity
     * @param tag a tag representing the type of event, according to the {@link CloudSimTag} or some custom one
     * @param data  the data to be sent with the event, according to the tag
     */
    protected void sendNow(final SimEntity dest, final int tag, final Object data) {
        send(dest, 0, tag, data);
    }

    /**
     * Sends an event/message to another entity, with a tag representing the event type.
     * @param dest    the destination entity
     * @param tag a tag representing the type of event, according to the {@link CloudSimTag} or some custom one
     */
    protected void sendNow(final SimEntity dest, final int tag) {
        send(dest, 0, tag, null);
    }

    /**
     * Gets the network delay to send a message from a given
     * source to a given destination.
     *
     * @param src source of the message
     * @param dst destination of the message
     * @return the delay to send a message from src to dst (in seconds)
     */
    private double getNetworkDelay(final SimEntity src, final SimEntity dst) {
        return getSimulation().getNetworkTopology().getDelay(src, dst);
    }

    @Override
    public boolean isStarted() {
        return startTime > -1;
    }

    @Override
    public boolean isAlive() {
        return state != State.FINISHED;
    }

    @Override
    public boolean isFinished() {
        return state == State.FINISHED;
    }

    @Override
    public int compareTo(final SimEntity entity) {
        return Long.compare(this.getId(), entity.getId());
    }
}
