/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

import org.cloudsimplus.listeners.EventInfo;

/**
 * This class represents a simulation event which is passed between the entities
 * in the simulation.
 *
 * @author Costas Simatos
 * @see CloudSim
 * @see SimEntity
 */
public class SimEvent implements Cloneable, Comparable<SimEvent>, EventInfo {

    /**
     * Internal event type.
     */
    private final int type;

    /**
     * The time that this event was scheduled, at which it should occur.
     */
    private final double time;

    /**
     * Time that the event was removed from the queue to start service.
     */
    private double endWaitingTime;

    /**
     * Id of entity who scheduled the event.
     */
    private int sourceEntityId;

    /**
     * Id of entity that the event will be sent to.
     */
    private int destinationEntityId;

    /**
     * The user defined type of the event.
     */
    private final int tag;

    /**
     * Any data the event is carrying.
     *
     * @todo I would be used generics to define the type of the event data. But
     * this modification would incur several changes in the simulator core that
     * has to be assessed first.
     *
     */
    private final Object data;

    /**
     * An attribute to help CloudSim to identify the order of received events
     * when multiple events are generated at the same time. If two events have
     * the same {@link #time}, to know what event is greater than other (i.e.
     * that happens after other), the
     * {@link #compareTo(org.cloudbus.cloudsim.core.SimEvent)} makes use of this
     * field.
     */
    private long serial = -1;

    // Internal event types
    //@todo @author manoelcampos Should be an Enum
    public static final int ENULL = 0;

    public static final int SEND = 1;

    public static final int HOLD_DONE = 2;

    public static final int CREATE = 3;

    /**
     * Creates a blank event.
     */
    public SimEvent() {
        this.type = ENULL;
        this.time = -1L;
        this.endWaitingTime = -1.0;
        this.sourceEntityId = -1;
        this.destinationEntityId = -1;
        this.tag = -1;
        this.data = null;
    }

    SimEvent(int type, double time, int sourceEntityId, int destinationEntityId, int tag, Object data) {
        this.type = type;
        this.time = time;
        this.sourceEntityId = sourceEntityId;
        this.destinationEntityId = destinationEntityId;
        this.tag = tag;
        this.data = data;
    }

    SimEvent(int type, double time, int sourceEntityId) {
        this.type = type;
        this.time = time;
        this.sourceEntityId = sourceEntityId;
        this.destinationEntityId = -1;
        this.tag = -1;
        this.data = null;
    }

    protected void setSerial(long serial) {
        this.serial = serial;
    }

    /**
     * Sets the time that the event was removed from the queue to start service.
     *
     * @param endWaitingTime
     */
    protected void setEndWaitingTime(double endWaitingTime) {
        this.endWaitingTime = endWaitingTime;
    }

    @Override
    public String toString() {
        return "Event tag = " + tag + " source = " + CloudSim.getEntity(sourceEntityId).getName() + " destination = "
                + CloudSim.getEntity(destinationEntityId).getName();
    }

    /**
     * Gets the internal type
     *
     * @return
     */
    public int getType() {
        return type;
    }

    @Override
    public int compareTo(SimEvent event) {
        if (event == null) {
            return 1;
        } else if (time < event.time) {
            return -1;
        } else if (time > event.time) {
            return 1;
        } else if (serial < event.serial) {
            return -1;
        } else if (this == event) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Get the unique id number of the entity which received this event.
     *
     * @return the id number
     */
    public int getDestination() {
        return destinationEntityId;
    }

    /**
     * Get the unique id number of the entity which scheduled this event.
     *
     * @return the id number
     */
    public int getSource() {
        return sourceEntityId;
    }

    /**
     * Get the simulation time that this event was scheduled.
     *
     * @return The simulation time
     */
    public double eventTime() {
        return time;
    }

    /**
     * Get the simulation time that this event was removed from the queue for
     * service.
     *
     * @return The simulation time
     */
    public double endWaitingTime() {
        return endWaitingTime;
    }

    /**
     * Get the unique id number of the entity which scheduled this event.
     *
     * @return the id number
     */
    public int scheduledBy() {
        return sourceEntityId;
    }

    /**
     * Get the user-defined tag of this event.
     *
     * @return The tag
     */
    public int getTag() {
        return tag;
    }

    /**
     * Get the data passed in this event.
     *
     * @return A reference to the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @todo @author manoelcampos Should be used a clone constructor
     * @return
     */
    @Override
    public Object clone() {
        return new SimEvent(type, time, sourceEntityId, destinationEntityId, tag, data);
    }

    /**
     * Set the source entity of this event.
     *
     * @param source The unique id number of the source entity
     */
    public void setSource(int source) {
        this.sourceEntityId = source;
    }

    /**
     * Set the destination entity of this event.
     *
     * @param destination The unique id number of the destination entity
     */
    public void setDestination(int destination) {
        this.destinationEntityId = destination;
    }

    @Override
    public double getTime() {
        return time;
    }
}
