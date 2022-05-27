/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.core.events;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Objects;

/**
 * An event which is passed between the entities in the simulation.
 *
 * @author Costas Simatos
 * @see CloudSim
 * @see SimEntity
 */
public final class CloudSimEvent implements SimEvent {
    /** @see #getSimulation() */
    private Simulation simulation;

    private final Type type;

    /**
     * The actual simulation time that this event was scheduled to (at which it should occur).
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

    private final CloudSimTag tag;

    private final Object data;

    /** @see #getSerial() */
    private long serial = -1;

    /**
     * Creates a {@link Type#SEND} CloudSimEvent.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param src the source entity which is sending the message
     * @param dest the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final double delay,
        final SimEntity src, final SimEntity dest,
        final CloudSimTag tag, final Object data)
    {
        this(Type.SEND, delay, src, dest, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param dest the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     */
    public CloudSimEvent(final double delay, final SimEntity dest, final CloudSimTag tag) {
        this(delay, dest, tag, null);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity
     * and the message is sent with no delay.
     *
     * @param dest the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(final SimEntity dest, final CloudSimTag tag, Object data) {
        this(0, dest, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param dest the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final double delay,
        final SimEntity dest, final CloudSimTag tag, final Object data)
    {
        this(Type.SEND, delay, dest, dest, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity,
     * the message has no delay and no data.
     *
     * @param dest the source entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     */
    public CloudSimEvent(
        final SimEntity dest, final CloudSimTag tag)
    {
        this(Type.SEND, 0, dest, dest, tag, null);
    }

    /**
     * Creates a CloudSimEvent where the destination entity and tag are not set yet.
     * Furthermore, there will be not data associated to the event.
     *
     * @param delay how many seconds after the current simulation time the event should be scheduled
     */
    public CloudSimEvent(final Type type, final double delay, final SimEntity src) {
        this(type, delay, src, SimEntity.NULL, CloudSimTag.NONE, null);
    }

    /**
     * Creates a CloudSimEvent cloning another given one.
     *
     * @param src the event to clone
     */
    public CloudSimEvent(final SimEvent src) {
        this(
            src.getType(), src.getTime(),
            src.getSource(), src.getDestination(), src.getTag(), src.getData());
    }

    /**
     * Creates a CloudSimEvent.
     * @param type the internal type of the event
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param src the source entity which is sending the message
     * @param dest the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final Type type, final double delay,
        final SimEntity src, final SimEntity dest,
        final CloudSimTag tag, final Object data)
    {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay can't be negative.");
        }

        this.type = type;
        this.setSource(src);
        this.setDestination(dest);
        this.setSimulation(src.getSimulation());
        this.time = simulation.clock() + delay;
        this.tag = tag;
        this.data = data;
    }

    @Override
    public void setSerial(final long serial) {
        this.serial = serial;
    }

    @Override
    public double getEndWaitingTime() {
        return endWaitingTime;
    }

    @Override
    public SimEvent setSimulation(final Simulation simulation) {
        this.simulation = Objects.requireNonNull(simulation);
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(final SimEvent that) {
        if (that == null || that == SimEvent.NULL) {
            return 1;
        }

        if (this == that) {
            return 0;
        }

        int res = Double.compare(time, that.getTime());
        if (res != 0) {
            return res;
        }

        res = this.tag.compareTo(that.getTag());
        if (res != 0) {
            return res;
        }

        return Long.compare(serial, that.getSerial());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final CloudSimEvent that = (CloudSimEvent) obj;
        return Double.compare(that.getTime(), getTime()) == 0 && getTag() == that.getTag() && getSerial() == that.getSerial();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTime(), getTag(), getSerial());
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
    public CloudSimTag getTag() {
        return tag;
    }

    @Override
    public int getPriority() {
        return tag.priority();
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public SimEvent setSource(final SimEntity source) {
        this.src = Objects.requireNonNull(source);
        return this;
    }

    @Override
    public SimEvent setDestination(final SimEntity destination) {
        this.dest = Objects.requireNonNull(destination);
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

    @Override
    public String toString() {
        return "Event tag = " + tag + " source = " + src.getName() +
            " target = " + dest.getName() + " time = " + time;
    }
}
