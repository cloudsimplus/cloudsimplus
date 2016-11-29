package org.cloudbus.cloudsim.core;

import org.cloudsimplus.listeners.EventInfo;

/**
 * An interface that represents a simulation event which is passed between the entities
 * in the simulation.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudSimEvent
 */
public interface SimEvent extends Cloneable, Comparable<SimEvent>, EventInfo {
    // Internal event types
    //@todo @author manoelcampos Should be an Enum
    int ENULL = 0;
    int SEND = 1;
    int HOLD_DONE = 2;
    int CREATE = 3;

    /**
     * Gets the internal type
     *
     * @return
     */
    int getType();

    /**
     * Get the unique id number of the entity which received this event.
     *
     * @return the id number
     */
    int getDestination();

    /**
     * Get the unique id number of the entity which scheduled this event.
     *
     * @return the id number
     */
    int getSource();

    /**
     * Get the simulation time that this event was scheduled.
     *
     * @return The simulation time
     */
    double eventTime();

    /**
     * Get the simulation time that this event was removed from the queue for
     * service.
     *
     * @return The simulation time
     */
    double endWaitingTime();

    /**
     * Get the unique id number of the entity which scheduled this event.
     *
     * @return the id number
     */
    int scheduledBy();

    /**
     * Get the user-defined tag of this event.
     *
     * @return The tag
     */
    int getTag();

    /**
     * Get the data passed in this event.
     *
     * @return A reference to the data
     */
    Object getData();

    /**
     * Set the source entity of this event.
     *
     * @param source The unique id number of the source entity
     * @return
     */
    SimEvent setSource(int source);

    /**
     * Set the destination entity of this event.
     *
     * @param destination The unique id number of the destination entity
     * @return
     */
    SimEvent setDestination(int destination);

    @Override
    double getTime();

    /**
     * An attribute to help CloudSim to identify the order of received events
     * when multiple events are generated at the same time. If two events have
     * the same {@link #getTag()}, to know what event is greater than other (i.e.
     * that happens after other), the
     * {@link #compareTo(SimEvent)} makes use of this field.
     * @return
     */
    long getSerial();

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     * @return
     */
    Simulation getSimulation();

    @Override
    int compareTo(SimEvent o);

    /**
     * An attribute that implements the Null Object Design Pattern for {@link SimEvent}
     * objects.
     */
    final SimEvent NULL = new SimEvent() {
        @Override public int getType() { return 0; }
        @Override public int getDestination() { return 0; }
        @Override public int getSource() { return 0; }
        @Override public double eventTime() { return 0; }
        @Override public double endWaitingTime() { return 0; }
        @Override public int scheduledBy() { return 0; }
        @Override public int getTag() { return 0; }
        @Override public Object getData() { return 0; }
        @Override public SimEvent setSource(int source) { return this; }
        @Override public SimEvent setDestination(int destination) { return this; }
        @Override public double getTime() { return 0; }
        @Override public int compareTo(SimEvent o) { return 0; }
        @Override public long getSerial() { return 0; }
        @Override public Simulation getSimulation() { return Simulation.NULL; }
    };

}
