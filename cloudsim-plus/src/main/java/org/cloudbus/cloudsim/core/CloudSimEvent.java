/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core;

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
    private final CloudSim simulation;

    protected final int type;

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

    protected final int tag;

    protected final Object data;

    /**
     * @see #getSerial()
     */
    private long serial = -1;

    /**
     * Creates a blank event.
     * @param simulation
     */
    public CloudSimEvent(CloudSim simulation) {
        this.simulation = simulation;
        this.type = ENULL;
        this.time = -1L;
        this.endWaitingTime = -1.0;
        this.src = -1;
        this.dest = -1;
        this.tag = -1;
        this.data = null;
    }

    CloudSimEvent(CloudSim simulation, int type, double time, int src, int dest, int tag, Object data) {
        this.simulation = simulation;
        this.type = type;
        this.time = time;
        this.src = src;
        this.dest = dest;
        this.tag = tag;
        this.data = data;
    }

    CloudSimEvent(CloudSim simulation, int type, double time, int src) {
        this.simulation = simulation;
        this.type = type;
        this.time = time;
        this.src = src;
        this.dest = -1;
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
        return "Event tag = " + tag + " source = " + simulation.getEntity(src).getName() + " destination = "
                + simulation.getEntity(dest).getName();
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int compareTo(SimEvent event) {
        if (event == null) {
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

    /**
     * @todo @author manoelcampos Should be used a clone constructor
     * @return
     */
    @Override
    public Object clone() {
        return new CloudSimEvent(simulation, type, time, src, dest, tag, data);
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
    public CloudSim getSimulation() {
        return simulation;
    }
}
