/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.core.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Objects;

/**
 * An event passed between the entities in the simulation.
 *
 * @author Costas Simatos
 * @see CloudSimPlus
 * @see SimEntity
 */
@Accessors @Getter @Setter
public final class CloudSimEvent implements SimEvent {
    @NonNull
    private Simulation simulation;

    private final Type type;

    private final double time;

    @Setter(AccessLevel.NONE)
    private double endWaitingTime;

    @NonNull
    private SimEntity source;

    @NonNull
    private SimEntity destination;

    private final int tag;

    private final Object data;

    private long serial = -1;

    /**
     * Creates a {@link Type#SEND} CloudSimEvent.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param source the source entity which is sending the message
     * @param destination the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message (according to {@link CloudSimTag} or other custom tags you create),
     *            which is used by the destination entity to perform operations based on the message type
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final double delay,
        final SimEntity source, final SimEntity destination,
        final int tag, final Object data)
    {
        this(Type.SEND, delay, source, destination, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param destination the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message (according to {@link CloudSimTag} or other custom tags you create),
     *            which is used by the destination entity to perform operations based on the message type
     */
    public CloudSimEvent(final double delay, final SimEntity destination, final int tag) {
        this(delay, destination, tag, null);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity
     * and the message is sent with no delay.
     *
     * @param destination the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message (according to {@link CloudSimTag} or other custom tags you create),
     *            which is used by the destination entity to perform operations based on the message type
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(final SimEntity destination, final int tag, Object data) {
        this(0, destination, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity.
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param destination the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message (according to {@link CloudSimTag} or other custom tags you create),
     *            which is used by the destination entity to perform operations based on the message type
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final double delay,
        final SimEntity destination, final int tag, final Object data)
    {
        this(Type.SEND, delay, destination, destination, tag, data);
    }

    /**
     * Creates a {@link Type#SEND} CloudSimEvent where the sender and destination are the same entity,
     * the message has no delay and no data.
     *
     * @param destination the source entity which has to receive the message
     * @param tag the tag that identifies the type of the message (according to {@link CloudSimTag} or other custom tags you create),
     *            which is used by the destination entity to perform operations based on the message type
     */
    public CloudSimEvent(
        final SimEntity destination, final int tag)
    {
        this(Type.SEND, 0, destination, destination, tag, null);
    }

    /**
     * Creates a CloudSimEvent where the destination entity and tag are not set yet.
     * Furthermore, there will be not data associated with the event.
     *
     * @param type the internal type of the event
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param source the source entity which is sending the message
     */
    public CloudSimEvent(final Type type, final double delay, final SimEntity source) {
        this(type, delay, source, SimEntity.NULL, CloudSimTag.NONE, null);
    }

    /**
     * Creates a CloudSimEvent cloning another given one.
     *
     * @param source the event to clone
     */
    public CloudSimEvent(final SimEvent source) {
        this(
            source.getType(), source.getTime(),
            source.getSource(), source.getDestination(), source.getTag(), source.getData());
    }

    /**
     * Creates a CloudSimEvent.
     * @param type the internal type of the event
     * @param delay how many seconds after the current simulation time the event should be scheduled
     * @param source the source entity which is sending the message
     * @param destination the destination entity which has to receive the message
     * @param tag the tag that identifies the type of the message
     *            (which is used by the destination entity to perform operations based on the message type)
     * @param data the data attached to the message, that depends on the message tag
     */
    public CloudSimEvent(
        final Type type, final double delay,
        final SimEntity source, final SimEntity destination,
        final int tag, final Object data)
    {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay can't be negative.");
        }

        this.type = type;
        this.setSource(source);
        this.setDestination(destination);
        this.setSimulation(source.getSimulation());
        this.time = simulation.clock() + delay;
        this.tag = tag;
        this.data = data;
    }

    @Override
    public int compareTo(final SimEvent that) {
        if (that == null || that == NULL) {
            return 1;
        }

        if (this == that) {
            return 0;
        }

        int res = Double.compare(time, that.getTime());
        if (res != 0) {
            return res;
        }

        res = Integer.compare(this.tag, that.getTag());
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
    public EventListener<? extends EventInfo> getListener() {
        return EventListener.NULL;
    }

    @Override
    public String toString() {
        return "Event tag = " + tag + " source = " + source.getName() +
            " target = " + destination.getName() + " time = " + time;
    }
}
