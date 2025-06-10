/*
 * ** Network and Service Differentiation Extensions to CloudSim 3.0 **
 *
 * Gokul Poduval & Chen-Khong Tham
 * Computer Communication Networks (CCN) Lab
 * Dept of Electrical & Computer Engineering
 * National University of Singapore
 * August 2004
 *
 * Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2004, The University of Melbourne, Australia and National
 * University of Singapore
 * InfoPacket.java - Implementation of a Information Packet.
 *
 */
package org.cloudsimplus.network;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.util.MathUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a ping (ICMP protocol) packet that can be used to gather information from the network layer.
 * An IcmpPacket traverses the network topology similar to a {@link HostPacket},
 * but it collects information like bandwidths, Round Trip Time (RTT), etc.
 *
 * <p>
 * You can set all the parameters to an IcmpPacket that can be applied to a
 * HostPacket. So if you want to find out the kind of information that a
 * particular type of HostPacket is experiencing, set the size and network class
 * of an IcmpPacket to the same as the HostPacket. Then, send it to the same
 * destination from the same source.
 * </p>
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @since CloudSim Toolkit 1.0
 */

@Getter @Setter
public class IcmpPacket implements NetworkPacket<SimEntity> {
    /**
     * A default value to indicate {@link #baudRate} was not set yet.
     */
    private static final int UNSET_BAUD_RATE = -1;

    /**
     * The packet direction that indicates if it is going or returning.
     * The direction can be {@link CloudSimTag#ICMP_PKT_SUBMIT}
     * or {@link CloudSimTag#ICMP_PKT_RETURN}.
     */
    private int tag;

    /**
     * The packet name.
     */
    private final String name;

    /**
     * The size of the packet (in bytes).
     */
    private long size;

    /**
     * The id of the packet.
     */
    private final int packetId;

    private SimEntity source;

    private SimEntity destination;

    /**
     * The entity that was the last hop where this packet has traversed.
     */
    @NonNull
    private SimEntity lastHop;

    /**
     * The network service type of this packet (zero by default).
     * It depends on the packet scheduler to determine
     * the priority of this service level.
     */
    private int netServiceLevel;

    /**
     * The bottleneck bandwidth (baud rate in bits/s)
     * between the source and the destination.
     */
    private double baudRate;

    /**
     * The baud rate (in bits/s) of each output link from entities where the packet traverses.
     */
    private final List<Double> baudRateList;

    /**
     * The list of entities where the packet traverses, such as Routers or Datacenters.
     */
    private final List<SimEntity> entities;

    /** @see #getDetailEntryTimes() */
    private final List<Double> entryTimes;

    /** @see #getDetailExitTimes() */
    private final List<Double> exitTimes;

    @Getter(AccessLevel.NONE)
    private final DecimalFormat num;

    private double sendTime;
    private double receiveTime;

    /**
     * Creates an ICMP packet.
     *
     * @param name            name of this packet
     * @param packetID        the ID of this packet
     * @param size            size of the packet
     * @param source          the entity that sends out this packet
     * @param destination     the entity to which this packet is destined
     * @param netServiceLevel the class of traffic this packet belongs to
     */
    public IcmpPacket(
        final String name, final int packetID, final long size,
        final SimEntity source, final SimEntity destination, final int netServiceLevel)
    {
        this.name = name;
        this.packetId = packetID;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.netServiceLevel = netServiceLevel;
        this.entities = new ArrayList<>();
        this.entryTimes = new ArrayList<>();
        this.exitTimes = new ArrayList<>();

        this.baudRateList = new ArrayList<>();
        this.baudRate = UNSET_BAUD_RATE;

        this.lastHop = this.source;
        this.tag = CloudSimTag.ICMP_PKT_SUBMIT;
        this.num = new DecimalFormat("#0.000#");
    }

    /**
     * @return the ID of this packet
     */
    public int getId() {
        return packetId;
    }

    /**
     * @return human-readable description of this packet.
     */
    @Override
    public String toString() {
        if (name == null) {
            return "Empty IcmpPacket that contains no ping information.";
        }

        final int SIZE = 1000;   // number of chars
        final StringBuilder sb = new StringBuilder(SIZE);
        sb.append("Ping information for ").append(name)
          .append("%nEntity Name\tEntry Time\tExit Time\t Bandwidth%n".formatted())
          .append("----------------------------------------------------------")
          .append(System.lineSeparator());

        final String tab = "    ";  // 4 spaces
        for (int i = 0; i < entities.size(); i++) {
            final long resID = entities.get(i).getId();
            final String entry = getData(entryTimes, i);
            final String exit = getData(exitTimes, i);
            final String bandwidth = getData(baudRateList, i);

            sb.append("Entity ").append(resID).append("\t\t")
              .append("%s%s%s%s%s%s%s%n".formatted(entry, tab, tab, exit, tab, tab, bandwidth));
        }

        sb.append(System.lineSeparator())
          .append("Round Trip Time : ")
          .append(num.format(getTotalResponseTime()))
          .append(" seconds%nNumber of Hops  : ".formatted())
          .append(getNumberOfHops())
          .append(System.lineSeparator())
          .append("Bottleneck Bandwidth : ")
          .append(baudRate).append(" bits/s");

        return sb.toString();
    }

    /**
     * Gets the element at a given index in a list.
     *
     * @param dataList  a list containing some data
     * @param index the location of the element in the list
     * @return the element from a given index
     */
    private String getData(final List<Double> dataList, final int index) {
        try {
            final double id = dataList.get(index);
            return num.format(id);
        } catch (final Exception e) {
            return "    N/A";
        }
    }

    /**
     * Sets the size of the packet.
     *
     * @param size the size to set (in bytes)
     */
    public void setSize(final long size) {
        this.size = MathUtil.nonNegative(size, "size");
    }

    /**
     * {@return the number of hops that the packet has traversed}
     * Since the packet takes a round trip, the same router may have been traversed twice.
     */
    public int getNumberOfHops() {
        final int PAIR = 2;
        return ((entities.size() - PAIR) + 1) / PAIR;
    }

    /**
     * Gets the total time that the packet has spent in the network (in seconds).
     * This is basically the Round-Trip Time (RTT).
     * Dividing this by half should be the approximate latency.
     * RTT is taken as the "final entry time" - "first exit time".
     *
     * @return total Round-Trip Time (RTT) in seconds
     */
    public double getTotalResponseTime() {
        try {
            final double startTime = exitTimes.stream().findFirst().orElse(0.0);
            final double receiveTime = entryTimes.stream().findFirst().orElse(0.0);
            return receiveTime - startTime;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Add an entity where the IcmpPacket traverses. This method should be
     * called by network entities that count as hops, for instance, Routers or
     * CloudResources. It should not be called by network links and other elements.
     *
     * @param entity the id of the hop that this IcmpPacket is traversing
     */
    public void addHop(final SimEntity entity) {
        entities.add(entity);
    }

    /**
     * Register the time the packet arrives at an entity such as a Router.
     * This method should be called by routers and other entities
     * when the IcmpPacket reaches them along with the current simulation time.
     *
     * @param time the current simulation time (in seconds)
     */
    public void addEntryTime(final double time) {
        entryTimes.add(Math.min(time, 0));
    }

    /**
     * Register the time the packet leaves an entity such as a Router.
     * This method should be called by routers and other entities
     * when the IcmpPacket is leaving them.
     *
     * @param time the current simulation time (in seconds)
     */
    public void addExitTime(final double time) {
        exitTimes.add(Math.min(time, 0));
    }

    /**
     * Register the baud rate (in bits/s) of the output link where the current entity that
     * holds the IcmpPacket will send it next. Every entity that the IcmpPacket
     * traverses should add the baud rate of the link on which this packet will
     * be sent out next.
     *
     * @param baudRate the entity's baud rate in bits/s
     */
    public void addBaudRate(final double baudRate) {
        baudRateList.add(baudRate);
        if (this.baudRate <= UNSET_BAUD_RATE || this.baudRate > baudRate) {
            this.baudRate = baudRate;
        }
    }

    /**
     * @return a <b>read-only</b> list of all the bandwidths (in bits/s) that this packet has traversed.
     */
    public List<Double> getDetailBaudRate() {
        return Collections.unmodifiableList(baudRateList);
    }

    /**
     * @return a <b>read-only</b> list of all entities that this packet has traversed,
     * that defines the hops it has made.
     */
    public List<SimEntity> getHopsList() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * @return a <b>read-only</b> list containing the time (in seconds)
     * the packet arrived at every entity it has traversed.
     */
    public List<Double> getDetailEntryTimes() {
        return Collections.unmodifiableList(entryTimes);
    }

    /**
     * @return a <b>read-only</b> list of all exit times (in seconds) from all entities
     * that the packet has traversed.
     */
    public List<Double> getDetailExitTimes() {
        return Collections.unmodifiableList(exitTimes);
    }

    /**
     * Sets the direction that indicates if the packet is going or returning.
     * The direction can be {@link CloudSimTag#ICMP_PKT_SUBMIT}
     * or {@link CloudSimTag#ICMP_PKT_RETURN}.
     *
     * @param tag the direction to set
     */
    public void setTag(final int tag) {
        if (CloudSimTag.between(tag, CloudSimTag.ICMP_PKT_SUBMIT, CloudSimTag.ICMP_PKT_RETURN)) {
            this.tag = tag;
            return;
        }

        final var fmt = "Tag must be between %s and %s";
        final var msg = fmt.formatted(CloudSimTag.ICMP_PKT_SUBMIT, CloudSimTag.ICMP_PKT_RETURN);
        throw new IllegalArgumentException(msg);
    }
}
