/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.core.events.SimEvent;

/**
 * An interface that represents a simulation entity. An entity handles events and can
 * send events to other entities.
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
     * An attribute that implements the Null Object Design Pattern for {@link SimEntity}
     * objects.
     */
    SimEntity NULL = (SimEntityNullBase) (comparable) -> 0;

    /**
     * Gets the time the entity was started.
     * @return the entity start time or -1 if it haven't started yet.
     */
    double getStartTime();

    /**
     * Gets the time the entity was shutdown (in seconds).
     * If the entity {@link #isAlive()} yet,
     * the method returns -1.
     * @return
     */
    double getShutdownTime();

    /**
     * Gets the entity state.
     *
     * @return the state
     */
    State getState();

    /**
     * Sets the entity state.
     *
     * @param state the state to set
     */
    SimEntity setState(State state);

    /**
     * Checks if the entity already was started or not.
     * @return
     */
    boolean isStarted();

    /**
     * Checks if the entity is alive, i.e, it's not finished.
     * @return
     */
    boolean isAlive();

    /**
     * Checks if the entity is finished or not.
     * @return
     */
    boolean isFinished();

    /**
     * Gets the CloudSim instance that represents the simulation to each the Entity belongs to.
     * @return
     */
    Simulation getSimulation();

    /**
     * Sets the CloudSim instance that represents the simulation the Entity belongs to.
     * @param simulation The simulation instance the Entity is related to
     * @return
     */
    SimEntity setSimulation(Simulation simulation);

    /**
     * Processes events or services that are available for the entity. This
     * method is invoked by the {@link CloudSim} class whenever there is an
     * event in the deferred queue, which needs to be processed by the entity.
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
     * @param tag   a tag representing the type of event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    default boolean schedule(CloudSimTag tag) {
        return schedule(0, tag);
    }

    /**
     * Sends an event from the entity to itself.
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event.
     * @param data  The data to be sent with the event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(double delay, CloudSimTag tag, Object data);

    /**
     * Sends an event from the entity to itself with no data.
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(double delay, CloudSimTag tag);

    /**
     * Sends an event to another entity.
     * @param dest  the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event.
     * @param data  The data to be sent with the event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(SimEntity dest, double delay, CloudSimTag tag, Object data);

    /**
     * Sends an event to another entity with <b>no</b> attached data.
     * @param dest the destination entity
     * @param delay How many seconds after the current simulation time the event should be sent
     * @param tag   a tag representing the type of event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(SimEntity dest, double delay, CloudSimTag tag);

    /**
     * Sends an event from the entity to itself with <b>no</b> delay.
     * @param tag   a tag representing the type of event.
     * @param data  The data to be sent with the event.
     * @return true if the event was sent; false if the simulation was not started yet
     */
    boolean schedule(CloudSimTag tag, Object data);

    /**
     * The run loop to process events fired during the simulation. The events
     * that will be processed are defined in the
     * {@link #processEvent(SimEvent)} method.
     *
     * @see #processEvent(SimEvent)
     */
    @Override void run();

    /**
     * Starts the entity during simulation start.
     * This method is invoked by the {@link CloudSim} class when the simulation is started.
     * @return true if the entity started successfully; false if it was already started
     */
    boolean start();

    /**
     * Shuts down the entity. This method is invoked by the {@link CloudSim}
     * before the simulation finishes. If you want to save data in log files
     * this is the method in which the corresponding code would be placed.
     */
    void shutdown();

    /**
     * Sets the Entity name.
     *
     * @param newName the new name
     * @return
     * @throws IllegalArgumentException when the entity name is null or empty
     */
    SimEntity setName(String newName) throws IllegalArgumentException;
}
