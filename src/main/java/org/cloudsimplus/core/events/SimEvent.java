/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core.events;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.listeners.EventInfo;

/**
 * Represents a simulation event which is passed between the entities
 * in a specific {@link Simulation} instance.
 *
 * @author Costas Simatos
 * @author Manoel Campos da Silva Filho
 * @see CloudSimEvent
 * @since CloudSim Plus 1.0
 */
public sealed interface SimEvent extends Comparable<SimEvent>, EventInfo
    permits CloudSimEvent, SimEventNull
{
    /**
     * An attribute that implements the Null Object Design Pattern for {@link SimEvent} objects.
     */
    SimEvent NULL = new SimEventNull();

    /**
     * Internal event types.
     */
    enum Type {NULL, SEND, HOLD_DONE, CREATE}

    /**
     * Sets the simulation the event belongs to
     * @param simulation the simulation instance to set
     */
    SimEvent setSimulation(Simulation simulation);

    /**
     * @return the internal type
     */
    Type getType();

    /**
     * @return the entity which received this event.
     */
    SimEntity getDestination();

    /**
     * @return the entity which scheduled this event.
     */
    SimEntity getSource();

    /**
     * @return the simulation time that this event was removed from the queue for service.
     */
    double getEndWaitingTime();

    /**
     * {@return the tag that classifies this event}
     * The meaning of such a tag depends on the entities that generate and receive the event.
     * Usually it is a constant value from the {@link CloudSimTag} or some custom tag
     * defined by new classes.
     */
    int getTag();

    /**
     * Gets the data object passed in this event.
     * The actual class of this data is defined by the entity that generates the event.
     * The value defined for the {@link #getTag()} is used by an entity receiving the event
     * to know what the class of the data associated with the event is.
     * After checking the event tag, the destination entity
     * can perform typecast to convert the data to the expected class.
     *
     * @return a reference to the data object
     */
    Object getData();

    /**
     * Sets the source entity of this event that defines its sender.
     *
     * @param source the source entity
     */
    SimEvent setSource(SimEntity source);

    /**
     * Sets the destination entity of this event that defines its destination.
     *
     * @param destination the destination entity
     */
    SimEvent setDestination(SimEntity destination);

    /**
     * {@return the serial number that defines the order of received events when multiple
     * events are generated at the same time}
     * If two events have the same {@link #getTag()}, to know what event is greater than another
     * (i.e., that happens after other), the {@link #compareTo(SimEvent)} makes use of this field.
     */
    long getSerial();

    /**
     * Sets the serial number that defines the order of received events when multiple
     * events are generated at the same time.
     *
     * @param serial the serial value to set
     */
    SimEvent setSerial(long serial);

    /**
     * @return the {@link CloudSimPlus} instance that represents the simulation for with the Entity is related to.
     */
    Simulation getSimulation();

    @Override int compareTo(SimEvent evt);
}
