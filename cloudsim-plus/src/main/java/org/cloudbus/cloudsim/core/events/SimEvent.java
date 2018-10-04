/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
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
public interface SimEvent extends Comparable<SimEvent>, EventInfo {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link SimEvent}
     * objects.
     */
    SimEvent NULL = new SimEventNull();

    /**
     * Internal event types
     */
    enum Type {NULL, SEND, HOLD_DONE, CREATE}

    /**
     * Sets the simulation the event belongs to
     * @param simulation the simulation instance to set
     * @return
     */
    SimEvent setSimulation(Simulation simulation);

    /**
     * Gets the internal type
     *
     * @return
     */
    Type getType();

    /**
     * Gets the entity which received this event.
     *
     * @return
     */
    SimEntity getDestination();

    /**
     * Gets the entity which scheduled this event.
     *
     * @return
     */
    SimEntity getSource();

    /**
     * Gets the simulation time that this event was removed from the queue for service.
     *
     * @return
     */
    double getEndWaitingTime();

    /**
     * Gets the entity which scheduled this event.
     *
     * @return
     */
    SimEntity scheduledBy();

    /**
     * Gets the user-defined tag of this event.
     * The meaning of such a tag depends on the entities that generate and receive the event.
     * Usually it is defined from a constant value defined in {@link CloudSimTags}.
     *
     * @return
     */
    int getTag();

    /**
     * Gets the data object passed in this event.
     * The actual class of this data is defined by the entity that generates the event.
     * The value defined for the {@link #getTag()} is used by an entity receiving the event
     * to know what is the class of the data associated to the event.
     * After checking what is the event tag, te destination entity then
     * can perform a typecast to convert the data to the expected class.
     *
     * @return a reference to the data object
     */
    Object getData();

    /**
     * Sets the source entity of this event, that defines its sender.
     *
     * @param source the unique id number of the source entity
     * @return
     */
    SimEvent setSource(SimEntity source);

    /**
     * Sets the destination entity of this event, that defines its destination.
     *
     * @param destination the unique id number of the destination entity
     * @return
     */
    SimEvent setDestination(SimEntity destination);

    /**
     * Gets the serial number that defines the order of received events when multiple
     * events are generated at the same time.
     * If two events have the same {@link #getTag()}, to know what event is greater than other (i.e.
     * that happens after other), the {@link #compareTo(SimEvent)} makes use of this field.
     *
     * @return
     */
    long getSerial();

    /**
     * Sets the serial number that defines the order of received events when multiple
     * events are generated at the same time.
     *
     * @param serial the serial value to set
     */
    void setSerial(long serial);

    /**
     * Gets the CloudSim instance that represents the simulation for with the Entity is related to.
     * @return
     */
    Simulation getSimulation();

    @Override int compareTo(SimEvent evt);
}
