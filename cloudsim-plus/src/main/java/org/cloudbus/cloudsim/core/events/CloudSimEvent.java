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
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

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
     * The entity who scheduled the event.
     */
    private SimEntity src;

    /**
     * The entity that the event will be sent to.
     */
    private SimEntity dest;

    private final int tag;

    private final Object data;

    /**
     * @see #getSerial()
     */
    private long serial = -1;

    /**
     * Creates a CloudSimEvent.
     *
     * @param src the event to clone
     */
    public CloudSimEvent(
        final CloudSim simulation, final Type type, final double time,
        final SimEntity src, final SimEntity dest, final int tag, final Object data)
    {
        this.simulation = simulation;
        this.type = type;
        this.time = time;
        this.src = src;
        this.dest = dest;
        this.tag = tag;
        this.data = data;
    }

    /**
     * Creates a CloudSimEvent.
     *
     */
    public CloudSimEvent(final CloudSim simulation, final Type type, final double time, final Object data) {
        this(simulation, type, time, SimEntity.NULL, SimEntity.NULL, -1, data);
    }

    /**
     * Creates a CloudSimEvent cloning another given one.
     *
     * @param src the event to clone
     */
    public CloudSimEvent(final SimEvent src) {
        this(
            (CloudSim)src.getSimulation(), src.getType(), src.getTime(),
            src.getSource(), src.getDestination(), src.getTag(), src.getData());
    }

    public CloudSimEvent(final CloudSim simulation, final Type type, final double time, final SimEntity src) {
        this(simulation, type, time, src, SimEntity.NULL, -1, null);
    }

    @Override
    public void setSerial(final long serial) {
        this.serial = serial;
    }

    /**
     * Sets the time that the event was removed from the queue to start service.
     *
     * @param endWaitingTime the end of waiting time to set
     */
    private void setEndWaitingTime(final double endWaitingTime) {
        this.endWaitingTime = endWaitingTime;
    }

    @Override
    public String toString() {
        return "Event tag = " + tag + " source = " + src.getName() +
               " target = " + dest.getName() + " time = " + time;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(final SimEvent event) {
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
    public SimEntity getDestination() {
        return dest;
    }

    @Override
    public SimEntity getSource() {
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
    public SimEntity scheduledBy() {
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
    public SimEvent setSource(final SimEntity source) {
        this.src = source;
        return this;
    }

    @Override
    public SimEvent setDestination(final SimEntity destination) {
        this.dest = destination;
        return this;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public EventListener<? extends EventInfo> getListener() {
        return EventListener.NULL;
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
