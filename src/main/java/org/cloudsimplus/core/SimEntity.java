/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core;

import lombok.NonNull;
import org.cloudsimplus.core.events.SimEvent;

/**
 * An interface that represents a simulation entity.
 * An entity handles events and can send events to other entities.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @see CloudSimEntity
 * @since CloudSim Plus 1.0
 */
public interface SimEntity extends Nameable, Runnable, Comparable<SimEntity> {
    /**
     * Defines the event state.
     */
    enum State {RUNNABLE, WAITING, HOLDING, FINISHED}

    /**
     * An attribute that implements the Null Object Design Pattern for {@link SimEntity} objects.
     */
    SimEntity NULL = (SimEntityNullBase) (comparable) -> 0;

    /**
     * @return the time the entity was started (in seconds) or -1 if it hasn't started yet.
     */
    double getStartTime();

    /**
     * @return the time the entity was shutdown (in seconds),
     *         or -1 if the entity {@link #isAlive()} yet.
     */
    double getShutdownTime();

    /**
     * @return the entity state.
     */
    State getState();

    /**
     * Sets the entity state.
     *
     * @param state the new state to set
     */
    SimEntity setState(@NonNull State state);

    /**
     * @return true if the entity already was started, false otherwise.
     */
    boolean isStarted();

    /**
     * @return true if the entity is alive, i.e, it's not finished; false otherwise.
     */
    // TODO: isStarted and isAlive seems to be redundant
    boolean isAlive();

    /**
     * @return true if the entity is finished, false otherwise.
     */
    boolean isFinished();

    /**
     * @return the {@link CloudSimPlus} instance that represents the simulation to which the Entity belongs to.
     */
    Simulation getSimulation();

    /**
     * Processes events or services that are available for the entity. This
     * method is invoked, by the {@link CloudSimPlus} class, whenever there is an
     * event in the deferred queue that needs to be processed by the entity.
     *
     * @param evt information about the event just happened
     */
    void processEvent(SimEvent evt);

    /**
     * Sends an event where all data required is defined inside the event instance.
     * @param evt the event to send
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(SimEvent evt);

    /**
     * Sends an event from the entity to itself with no delay.
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    default boolean schedule(int tag) {
        return schedule(0, tag);
    }

    /**
     * Sends an event from the entity to itself.
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data  The data to be sent with the event, according to the tag
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(double delay, int tag, Object data);

    /**
     * Sends an event from the entity to itself with no data.
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(double delay, int tag);

    /**
     * Sends an event to another entity.
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data  The data to be sent with the event, according to the tag
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(SimEntity dest, double delay, int tag, Object data);

    /**
     * Sends an event to another entity with <b>no</b> attached data.
     * @param dest the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(SimEntity dest, double delay, int tag);

    /**
     * Sends an event from the entity to itself with <b>no</b> delay.
     * @param tag   a tag representing the type of event, according to the {@link CloudSimTag} or some custom one.
     * @param data  The data to be sent with the event, according to the tag
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(int tag, Object data);

    /**
     * Starts the run loop to process events fired during the simulation. The events
     * that will be processed are defined in the
     * {@link #processEvent(SimEvent)} method.
     */
    @Override void run();

    /**
     * Starts the entity during simulation start.
     * This method is invoked by the {@link CloudSimPlus} class when the simulation is started.
     * @return true if the entity started successfully; false if it was already started
     */
    boolean start();

    /**
     * Shuts down the entity. This method is invoked by {@link CloudSimPlus}
     * before the simulation finishes. If you want to save data in log files,
     * this is the method in which the corresponding code may be placed.
     */
    void shutdown();

    /**
     * Sets the Entity name.
     *
     * @param newName the new name
     * @return this entity instance
     * @throws IllegalArgumentException when the entity name is null or empty
     */
    SimEntity setName(String newName) throws IllegalArgumentException;
}
