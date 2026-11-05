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

import static java.util.Objects.requireNonNullElseGet;
import org.jspecify.annotations.Nullable;

/// Represents a simulation entity. An entity handles events and can
/// send events to other entities.
///
/// @author Marcos Dias de Assuncao
/// @since CloudSim Toolkit 1.0
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class CloudSimEntity implements SimEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudSimEntity.class.getSimpleName());

    @Getter @EqualsAndHashCode.Include
    private long id;
    @Getter @NonNull @EqualsAndHashCode.Include
    private final Simulation simulation;

    @Getter
    private String name = "";

    @Getter
    private double startTime;

    @Getter
    private double shutdownTime;

    @Getter @Setter
    private State state;

    /// The buffer for selected incoming events.
    @Nullable
    private SimEvent buffer;

    /// Creates a new entity.
    ///
    /// @param simulation The [CloudSimPlus] instance that represents the simulation the Entity belongs to
    /// @throws IllegalArgumentException when the entity name is invalid
    public CloudSimEntity(@NonNull final Simulation simulation) {
        this.simulation = simulation;
        setId(-1);
        state = State.RUNNABLE;
        this.simulation.addEntity(this);
        this.startTime = -1;
        this.shutdownTime = -1;
    }

    /// {@inheritDoc}.
    /// It performs general initialization tasks that are common for every entity
    /// and executes the specific entity startup code.
    ///
    /// @return {@inheritDoc}
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

    /// Defines the logic to be performed by the entity when the simulation starts.
    protected abstract void startInternal();

    @Override
    public boolean schedule(final SimEntity dest, final double delay, final int tag, @Nullable final Object data) {
        return isValidDestination(dest) && schedule(newEvent(dest, delay, tag, data));
    }

    @Override
    public boolean scheduleFirst(final SimEntity dest, final double delay, final int tag, @Nullable final Object data) {
        return isValidDestination(dest) && scheduleFirst(newEvent(dest, delay, tag, data));
    }

    /// {@inheritDoc}
    /// Events sent before the simulation starts are held and only sent when it starts.
    /// Events sent after the simulation stops are rejected,
    /// as well as events to a destination that isn't part of the simulation of this entity.
    /// The other overloads also check the destination before building the event,
    /// so the network delay is never looked up for an invalid destination.
    ///
    /// @param evt {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public boolean schedule(@NonNull final SimEvent evt) {
        return isValidDestination(evt.getDestination()) && simulation instanceof CloudSim sim && sim.schedule(evt);
    }

    /// {@inheritDoc}
    /// It follows the same rules of [#schedule(SimEvent)].
    ///
    /// @param evt {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public boolean scheduleFirst(@NonNull final SimEvent evt) {
        return isValidDestination(evt.getDestination()) && simulation instanceof CloudSim sim && sim.scheduleFirst(evt);
    }

    private CloudSimEvent newEvent(final SimEntity dest, final double delay, final int tag, @Nullable final Object data) {
        return new CloudSimEvent(totalDelay(dest, delay), this, dest, tag, data);
    }

    /// Gets the delay for an event sent from this entity to a destination,
    /// the single place where the delay given by the caller is checked and the network delay is added.
    ///
    /// @param dest  the destination entity
    /// @param delay the delay given by the caller
    /// @return the given delay (0 if negative) plus the network delay when the destination is another entity
    /// @throws IllegalArgumentException when the delay is infinite
    double totalDelay(final SimEntity dest, final double delay) {
        if (Double.isInfinite(delay)) {
            throw new IllegalArgumentException("The specified delay is infinite value");
        }

        final double validDelay = nonNegativeDelay(delay);
        return dest == this ? validDelay : validDelay + getNetworkDelay(this, dest);
    }

    private double nonNegativeDelay(final double delay) {
        if (delay >= 0) {
            return delay;
        }

        LOGGER.warn("{}: negative event delay {} changed to 0.", getName(), delay);
        return 0;
    }

    /// Checks if the destination is part of the simulation of this entity.
    /// An entity from another simulation may have the same id, so both are checked.
    private boolean isValidDestination(@NonNull final SimEntity dest) {
        if (dest.getId() >= 0 && dest.getSimulation() == simulation) {
            return true;
        }

        LOGGER.error("{}: destination {} (id {}) isn't part of this simulation. Event not sent.", getName(), dest, dest.getId());
        return false;
    }

    /// Sets the entity to be inactive for a time period.
    ///
    /// @param delay the time period for which the entity will be inactive
    public void pause(final double delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Negative delay supplied.");
        }

        if (simulation.isRunning()) {
            simulation.pauseEntity(this, delay);
        }
    }

    /// Extracts the first event matching a predicate waiting in the entity's
    /// deferred queue.
    ///
    /// @param predicate The event selection predicate
    /// @return the simulation event;
    /// or [SimEvent#NULL] if not found or the simulation is not running
    public SimEvent selectEvent(final Predicate<SimEvent> predicate) {
        if (simulation.isRunning()) {
            return simulation.select(this, predicate);
        }

        return SimEvent.NULL;
    }

    /// Cancels the first event from the future event queue that matches a given predicate
    /// and that was submitted by this entity, then removes it from the queue.
    ///
    /// @param predicate the event selection predicate
    /// @return the removed event or [SimEvent#NULL] if not found
    public SimEvent cancelEvent(final Predicate<SimEvent> predicate) {
        return simulation.isRunning() ? simulation.cancel(this, predicate) : SimEvent.NULL;
    }

    /// Gets the first event matching a predicate from the deferred queue, or if
    /// none match, wait for a matching event to arrive.
    ///
    /// @param predicate The predicate to match
    /// @return the simulation event;
    /// or [SimEvent#NULL] if not found or the simulation is not running
    public SimEvent getNextEvent(final Predicate<SimEvent> predicate) {
        if (simulation.isRunning()) {
            return selectEvent(predicate);
        }

        return SimEvent.NULL;
    }

    /// Gets the first event waiting in the entity's deferred queue, or if there
    /// are none, wait for an event to arrive.
    ///
    /// @return the simulation event;
    /// or [SimEvent#NULL] if not found or the simulation is not running
    public SimEvent getNextEvent() {
        return getNextEvent(Simulation.ANY_EVT);
    }

    /// Waits for an event matching a specific predicate. This method does not
    /// check the entity's deferred queue.
    ///
    /// @param predicate the predicate to match
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
        var evt = requireNonNullElseGet(buffer, () -> getNextEvent(e -> e.getTime() <= until));

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

    /// Sets the entity id and defines its name based on it.
    ///
    /// @param id the new id
    protected final void setId(final int id) {
        this.id = id;
        setAutomaticName();
    }

    /// Sets an automatic generated name for the entity.
    private void setAutomaticName() {
        final long id = this.id >= 0 ? this.id : this.simulation.getNumEntities();
        this.name = "%s%d".formatted(getClass().getSimpleName(), id);
    }

    /// Sets the event buffer.
    ///
    /// @param evt the new event buffer
    protected void setEventBuffer(@NonNull final SimEvent evt) {
        this.buffer = evt;
    }

    /// Gets the network delay to send a message from a given
    /// source to a given destination.
    ///
    /// @param src source of the message
    /// @param dst destination of the message
    /// @return the delay to send a message from src to dst (in seconds)
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
