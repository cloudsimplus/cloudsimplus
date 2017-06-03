/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;

import java.util.Objects;

/**
 * This class represents a simulation event which is passed between the entities
 * in the simulation.
 *
 * @author Costas Simatos
 * @see CloudSim
 * @see SimEntity
 */
public final class CloudSimEvent implements SimEvent {
    /**
     * @see #getSimulation()
     */
    private final Simulation simulation;

    private final Type type;

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
    private int src;

    /**
     * Id of entity that the event will be sent to.
     */
    private int dest;

    private final int tag;

    private final Object data;

    /**
     * @see #getSerial()
     */
    private long serial = -1;

    /**
     * Creates a blank event.
     * @param simulation the simulation to which the event belongs to
     */
    public CloudSimEvent(CloudSim simulation) {
        this.simulation = simulation;
        this.type = Type.NULL;
        this.time = -1L;
        this.endWaitingTime = -1.0;
        this.src = -1;
        this.dest = -1;
        this.tag = -1;
        this.data = null;
    }

    /**
     * Creates an CloudSimEvent cloning another given one.
     *
     * @param eventToClone the event to clone
     */
    public CloudSimEvent(SimEvent eventToClone) {
        this.simulation = eventToClone.getSimulation();
        this.type = eventToClone.getType();
        this.time = eventToClone.getTime();
        this.src = eventToClone.getSource();
        this.dest = eventToClone.getDestination();
        this.tag = eventToClone.getTag();
        this.data = eventToClone.getData();
    }

    public CloudSimEvent(CloudSim simulation, Type type, double time, int src, int dest, int tag, Object data) {
        this.simulation = simulation;
        this.type = type;
        this.time = time;
        this.src = src;
        this.dest = dest;
        this.tag = tag;
        this.data = data;
    }

    public CloudSimEvent(CloudSim simulation, Type type, double time, int src) {
        this.simulation = simulation;
        this.type = type;
        this.time = time;
        this.src = src;
        this.dest = -1;
        this.tag = -1;
        this.data = null;
    }

    @Override
    public void setSerial(long serial) {
        this.serial = serial;
    }

    /**
     * Sets the time that the event was removed from the queue to start service.
     *
     * @param endWaitingTime the end of waiting time to set
     */
    private void setEndWaitingTime(double endWaitingTime) {
        this.endWaitingTime = endWaitingTime;
    }

    @Override
    public String toString() {
        return "Event tag = " + tag + " source = " + simulation.getEntity(src).getName() + " target = "
                + simulation.getEntity(dest).getName() + " time = " + time;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(SimEvent event) {
        if (Objects.isNull(event)) {
            return 1;
        } else if (time < event.getTime()) {
            return -1;
        } else if (time > event.getTime()) {
            return 1;
        } else if (serial < event.getSerial()) {
            return -1;
        } else if (this == event) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getDestination() {
        return dest;
    }

    @Override
    public int getSource() {
        return src;
    }

    @Override
    public double eventTime() {
        return time;
    }

    @Override
    public double endWaitingTime() {
        return endWaitingTime;
    }

    @Override
    public int scheduledBy() {
        return src;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public SimEvent setSource(int source) {
        this.src = source;
        return this;
    }

    @Override
    public SimEvent setDestination(int destination) {
        this.dest = destination;
        return this;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public long getSerial() {
        return serial;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }
}
