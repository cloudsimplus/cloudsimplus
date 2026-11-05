/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import lombok.NonNull;
import org.cloudsimplus.core.events.SimEvent;
import org.jspecify.annotations.Nullable;

/// An interface that represents a simulation entity.
/// An entity handles events and can send events to other entities.
///
/// @author Marcos Dias de Assuncao
/// @author Manoel Campos da Silva Filho
/// @see CloudSimEntity
/// @since CloudSim Plus 1.0
public interface SimEntity extends Nameable, Runnable, Comparable<SimEntity> {
    /// Defines the event state.
    enum State {RUNNABLE, WAITING, HOLDING, FINISHED}

    /// An attribute that implements the Null Object Design Pattern for [SimEntity] objects.
    SimEntity NULL = (SimEntityNullBase) (comparable) -> 0;

    /// @return the time the entity was started (in seconds) or -1 if it hasn't started yet.
    double getStartTime();

    /// @return the time the entity was shutdown (in seconds),
    /// or -1 if the entity is not [#isFinished()] yet.
    double getShutdownTime();

    /// @return the entity state.
    State getState();

    /// Sets the entity state.
    ///
    /// @param state the new state to set
    SimEntity setState(@NonNull State state);

    /// @return true if the entity has started at some point, even if it's finished now; false otherwise.
    /// @see #isRunning()
    boolean hasStarted();

    /// @return true if the entity has started and it's not finished yet; false otherwise.
    /// @see #hasStarted()
    boolean isRunning();

    /// @return true if the entity is finished, false otherwise.
    boolean isFinished();

    /// @return the [CloudSimPlus] instance that represents the simulation to which the Entity belongs to.
    Simulation getSimulation();

    /// Processes events or services that are available for the entity. This
    /// method is invoked, by the [CloudSimPlus] class, whenever there is an
    /// event in the deferred queue that needs to be processed by the entity.
    ///
    /// @param evt information about the event just happened
    void processEvent(SimEvent evt);

    /*
     * Rules for the schedule*() overloads:
     * - "schedule" is the only name used to send events: every event goes to the
     *   simulation's future queue, even with no delay, and is processed after the current one.
     * - The number of arguments tells if there is a delay: every overload that takes
     *   a delay also takes the data (which may be null). Without that rule,
     *   (dest, delay, tag) and (dest, tag, data) would have the same arity and
     *   schedule(dest, TAG, 10) would silently call the delay overload,
     *   making TAG the delay and 10 the tag.
     *   EntityScheduleTest checks this rule.
     * - To send an event to itself, an entity uses the overloads without the destination.
     */

    /// Sends an event where all data required is defined inside the event instance.
    /// The event time is kept as given: no network delay is added.
    /// @param evt the event to send
    /// @return true if the event was sent (or held until the simulation starts);
    ///         false if the simulation has stopped or the destination isn't part of the simulation of this entity
    boolean schedule(SimEvent evt);

    /// Sends a high-priority event where all data required is defined inside the event instance.
    /// It will be processed before the other events with the same time.
    /// The event time is kept as given: no network delay is added.
    /// @param evt the event to send
    /// @return true if the event was sent (or held until the simulation starts);
    ///         false if the simulation has stopped or the destination isn't part of the simulation of this entity
    boolean scheduleFirst(SimEvent evt);

    /// Sends an event from the entity to itself with no delay and no data.
    /// @param tag a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    default boolean schedule(final int tag) {
        return schedule(tag, null);
    }

    /// Sends an event from the entity to itself with no delay.
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    default boolean schedule(final int tag, @Nullable final Object data) {
        return schedule(0.0, tag, data);
    }

    /// Sends an event from the entity to itself.
    /// @param delay how many seconds after the current simulation time the event should be sent.
    ///              A negative delay is changed to 0 (with a warning).
    /// @param tag   a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data  the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    default boolean schedule(final double delay, final int tag, @Nullable final Object data) {
        return schedule(this, delay, tag, data);
    }

    /// Sends an event to another entity with no delay and no data.
    /// The network delay between the entities is added (see [Simulation#getNetworkTopology()]).
    /// @param dest the destination entity
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    default boolean schedule(final SimEntity dest, final int tag) {
        return schedule(dest, tag, null);
    }

    /// Sends an event to another entity with no delay.
    /// The network delay between the entities is added (see [Simulation#getNetworkTopology()]).
    /// @param dest the destination entity
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    default boolean schedule(final SimEntity dest, final int tag, @Nullable final Object data) {
        return schedule(dest, 0.0, tag, data);
    }

    /// Sends an event to another entity.
    /// When the destination is another entity, the network delay between them is added
    /// (see [Simulation#getNetworkTopology()]).
    /// @param dest  the destination entity
    /// @param delay how many seconds after the current simulation time the event should be sent.
    ///              A negative delay is changed to 0 (with a warning).
    /// @param tag   a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data  the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts);
    ///         false if the simulation has stopped or the destination isn't part of the simulation of this entity
    boolean schedule(SimEntity dest, double delay, int tag, @Nullable Object data);

    /// Sends a high-priority event from the entity to itself with no delay and no data.
    /// @param tag a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    /// @see #scheduleFirst(SimEntity, double, int, Object)
    default boolean scheduleFirst(final int tag) {
        return scheduleFirst(tag, null);
    }

    /// Sends a high-priority event from the entity to itself with no delay.
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    /// @see #scheduleFirst(SimEntity, double, int, Object)
    default boolean scheduleFirst(final int tag, @Nullable final Object data) {
        return scheduleFirst(0.0, tag, data);
    }

    /// Sends a high-priority event from the entity to itself.
    /// @param delay how many seconds after the current simulation time the event should be sent.
    ///              A negative delay is changed to 0 (with a warning).
    /// @param tag   a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data  the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    /// @see #scheduleFirst(SimEntity, double, int, Object)
    default boolean scheduleFirst(final double delay, final int tag, @Nullable final Object data) {
        return scheduleFirst(this, delay, tag, data);
    }

    /// Sends a high-priority event to another entity with no delay and no data.
    /// @param dest the destination entity
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    /// @see #scheduleFirst(SimEntity, double, int, Object)
    default boolean scheduleFirst(final SimEntity dest, final int tag) {
        return scheduleFirst(dest, tag, null);
    }

    /// Sends a high-priority event to another entity with no delay.
    /// @param dest the destination entity
    /// @param tag  a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts); false if the simulation has stopped
    /// @see #scheduleFirst(SimEntity, double, int, Object)
    default boolean scheduleFirst(final SimEntity dest, final int tag, @Nullable final Object data) {
        return scheduleFirst(dest, 0.0, tag, data);
    }

    /// Sends a high-priority event to another entity.
    /// It will be processed before the other events with the same time.
    /// It follows the same rules of [#schedule(SimEntity, double, int, Object)].
    /// @param dest  the destination entity
    /// @param delay how many seconds after the current simulation time the event should be sent.
    ///              A negative delay is changed to 0 (with a warning).
    /// @param tag   a tag representing the type of event, according to the [CloudSimTag] or some custom one.
    /// @param data  the data to be sent with the event, according to the tag (may be null)
    /// @return true if the event was sent (or held until the simulation starts);
    ///         false if the simulation has stopped or the destination isn't part of the simulation of this entity
    boolean scheduleFirst(SimEntity dest, double delay, int tag, @Nullable Object data);

    /// Starts the run loop to process events fired during the simulation. The events
    /// that will be processed are defined in the
    /// [#processEvent(SimEvent)] method.
    @Override void run();

    /// Starts the entity during simulation start.
    /// This method is invoked by the [CloudSimPlus] class when the simulation is started.
    /// @return true if the entity started successfully; false if it was already started
    boolean start();

    /// Shuts down the entity. This method is invoked by [CloudSimPlus]
    /// before the simulation finishes. If you want to save data in log files,
    /// this is the method in which the corresponding code may be placed.
    void shutdown();

    /// Sets the Entity name.
    ///
    /// @param newName the new name
    /// @return this entity instance
    /// @throws IllegalArgumentException when the entity name is null or empty
    SimEntity setName(String newName) throws IllegalArgumentException;
}
