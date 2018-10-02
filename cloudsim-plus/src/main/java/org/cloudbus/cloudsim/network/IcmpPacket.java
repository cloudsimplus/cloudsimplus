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
package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a ping (ICMP protocol) packet that can be used to gather information from the network layer.
 * An IcmpPacket traverses the network topology similar to a {@link HostPacket},
 * but it collects information like bandwidths, and Round Trip Time etc.
 *
 * <p>
 * You can set all the parameters to an IcmpPacket that can be applied to a
 * HostPacket. So if you want to find out the kind of information that a
 * particular type of HostPacket is experiencing, set the size and network class
 * of an IcmpPacket to the same as the HostPacket, and send it to the same
 * destination from the same source.
 * </p>
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @since CloudSim Toolkit 1.0
 */
public class IcmpPacket implements NetworkPacket<SimEntity> {
    /**
     * @see #getTag()
     */
    private int tag;

    /**
     * The packet name.
     */
    private final String name;

    /**
     * @see #getSize()
     */
    private long size;

    /**
     * The id of the packet.
     */
    private final int packetId;

    /**
     * The original sender.
     */
    private SimEntity source;

    /**
     * The destination.
     */
    private SimEntity destination;

    /**
     * @see #getLastHop()
     */
    private SimEntity lastHop;

    /**
     * The level of service type.
     */
    private int netServiceLevel;

    /**
     * The bandwidth bottleneck.
     */
    private double bandwidth;

    /**
     * The list with IDs of entities where the packet
     * traverses, such as Routers or Datacenters.
     */
    private final List<SimEntity> entities;

    /**
     * A list containing the time the packet arrived at every entity it has traversed.
     */
    private final List<Double> entryTimes;

    /**
     * The list of exit times.
     */
    private final List<Double> exitTimes;

    /**
     * The baud rate of each output link of entities where the packet traverses.
     */
    private final List<Double> baudRates;

    private final DecimalFormat num;

    private double sendTime;
    private double receiveTime;

    /**
     * Constructs a new ICMP packet.
     *
     * @param name            Name of this packet
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
        packetId = packetID;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.netServiceLevel = netServiceLevel;
        this.entities = new ArrayList<>();
        this.entryTimes = new ArrayList<>();
        this.exitTimes = new ArrayList<>();
        this.baudRates = new ArrayList<>();

        lastHop = this.source;
        tag = CloudSimTags.ICMP_PKT_SUBMIT;
        bandwidth = -1;
        num = new DecimalFormat("#0.000#");
    }

    /**
     * Returns the ID of this packet
     *
     * @return packet ID
     */
    public int getId() {
        return packetId;
    }

    /**
     * Returns a human-readable information of this packet.
     *
     * @return description of this packet
     */
    @Override
    public String toString() {
        if (name == null) {
            return "Empty IcmpPacket that contains no ping information.";
        }

        final int SIZE = 1000;   // number of chars
        final StringBuilder sb = new StringBuilder(SIZE);
        sb.append("Ping information for ").append(name)
          .append("\nEntity Name\tEntry TiOme\tExit Time\t Bandwidth\n")
          .append("----------------------------------------------------------\n");

        final String tab = "    ";  // 4 spaces
        for (int i = 0; i < entities.size(); i++) {
            final long resID = entities.get(i).getId();
            final String entry = getData(entryTimes, i);
            final String exit = getData(exitTimes, i);
            final String bandwidth = getData(baudRates, i);

            sb.append("Entity ").append(resID).append("\t\t")
              .append(String.format("%s%s%s%s%s%s%s\n", entry, tab, tab, exit, tab, tab, bandwidth));
        }

        sb.append("\nRound Trip Time : ")
          .append(num.format(getTotalResponseTime()))
          .append(" seconds\nNumber of Hops  : ")
          .append(getNumberOfHops())
          .append("\nBottleneck Bandwidth : ")
          .append(bandwidth).append(" bits/s");

        return sb.toString();
    }

    /**
     * Gets the data of a given index in a list.
     *
     * @param v     a list
     * @param index the location in a list
     * @return the data
     * @pre v != null
     * @post index > 0
     */
    private String getData(final List<Double> v, final int index) {
        try {
            final double id = v.get(index);
            return num.format(id);
        } catch (Exception e) {
            return "    N/A";
        }
    }

    @Override
    public long getSize() {
        return size;
    }

    /**
     * Sets the size of the packet.
     *
     * @param size the size to set
     * @return <tt>true</tt> if a positive value was given, <tt>false</tt> otherwise
     * @pre size >= 0
     * @post $none
     */
    public boolean setSize(final long size) {
        if (size < 0) {
            return false;
        }

        this.size = size;
        return true;
    }

    @Override
    public SimEntity getSource() {
        return source;
    }

    @Override
    public void setSource(final SimEntity source) {
        this.source = source;
    }

    @Override
    public SimEntity getDestination() {
        return destination;
    }

    @Override
    public void setDestination(final SimEntity destination) {
        this.destination = destination;
    }

    @Override
    public double getSendTime() {
        return this.sendTime;
    }

    @Override
    public void setSendTime(final double time) {
        this.sendTime = time;
    }

    @Override
    public double getReceiveTime() {
        return this.receiveTime;
    }

    @Override
    public void setReceiveTime(final double time) {
        this.receiveTime = time;
    }

    /**
     * Gets the number of hops that the packet has traversed. Since the
     * packet takes a round trip, the same router may have been traversed twice.
     *
     * @return
     */
    public int getNumberOfHops() {
        final int PAIR = 2;
        return ((entities.size() - PAIR) + 1) / PAIR;
    }

    /**
     * Gets the total time that the packet has spent in the network. This is
     * basically the Round-Trip Time (RTT). Dividing this by half should be the
     * approximate latency.
     * <p/>
     * RTT is taken as the "final entry time" - "first exit time".
     *
     * @return total round-trip time
     * @pre $none
     * @post $none
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
     * Gets the bottleneck bandwidth between the source and the destination.
     *
     * @return the bottleneck bandwidth
     * @pre $none
     * @post $none
     */
    public double getBaudRate() {
        return bandwidth;
    }

    /**
     * Add an entity where the IcmpPacket traverses. This method should be
     * called by network entities that count as hops, for instance Routers or
     * CloudResources. It should not be called by links etc.
     *
     * @param entity the id of the hop that this IcmpPacket is traversing
     * @post $none
     */
    public void addHop(final SimEntity entity) {
        entities.add(entity);
    }

    /**
     * Register the time the packet arrives at an entity such as a Router or
     * CloudResource. This method should be called by routers and other entities
     * when the IcmpPacket reaches them along with the current simulation time.
     *
     * @param time current simulation time, use
     *             {@link org.cloudbus.cloudsim.core.CloudSim#clock()} to obtain this
     * @pre time >= 0
     * @post $none
     */
    public void addEntryTime(final double time) {
        entryTimes.add(Math.min(time, 0));
    }

    /**
     * Register the time the packet leaves an entity such as a Router or
     * CloudResource. This method should be called by routers and other entities
     * when the IcmpPacket is leaving them. It should also supply the current
     * simulation time.
     *
     * @param time current simulation time, use
     *             {@link org.cloudbus.cloudsim.core.CloudSim#clock()} to obtain this
     * @pre time >= 0
     * @post $none
     */
    public void addExitTime(final double time) {
        exitTimes.add(Math.min(time, 0));
    }

    /**
     * Register the baud rate of the output link where the current entity that
     * holds the IcmpPacket will send it next. Every entity that the IcmpPacket
     * traverses should add the baud rate of the link on which this packet will
     * be sent out next.
     *
     * @param baudRate the entity's baud rate in bits/s
     * @pre baudRate > 0
     * @post $none
     */
    public void addBaudRate(final double baudRate) {
        baudRates.add(baudRate);
        if (bandwidth < 0 || baudRate < bandwidth) {
            bandwidth = baudRate;
        }
    }

    /**
     * Gets a <b>read-only</b> list of all the bandwidths that this packet has traversed.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public List<Double> getDetailBaudRate() {
        return Collections.unmodifiableList(baudRates);
    }

    /**
     * Gets a <b>read-only</b> list of all entities that this packet has traversed,
     * that defines the hops it has made.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public List<SimEntity> getHopsList() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Gets a <b>read-only</b> list of all entry times that the packet has traversed.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public List<Double> getDetailEntryTimes() {
        return Collections.unmodifiableList(entryTimes);
    }

    /**
     * Gets a <b>read-only</b> list of all exit times from all entities that the packet has
     * traversed.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public List<Double> getDetailExitTimes() {
        return Collections.unmodifiableList(exitTimes);
    }

    /**
     * Gets the entity that was the last hop where this packet has traversed.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public SimEntity getLastHop() {
        return lastHop;
    }

    /**
     * Sets the entity that was the last hop where this packet has traversed.
     *
     * @param entity the entity to set as the last hop
     * @post $none
     */
    public void setLastHop(final SimEntity entity) {
        this.lastHop = entity;
    }

    /**
     * Gets the network service type of this packet
     *
     * @return the network service type
     * @pre $none
     * @post $none
     */
    public int getNetServiceLevel() {
        return netServiceLevel;
    }

    /**
     * Sets the network service type of this packet.
     * <p>
     * By default, the service type is 0 (zero). It is depends on the packet scheduler to determine
     * the priority of this service level.
     *
     * @param netServiceLevel the service level to set
     */
    public void setNetServiceLevel(final int netServiceLevel) {
        this.netServiceLevel = netServiceLevel;
    }

    /**
     * Gets the packet direction that indicates if it is going or returning.
     * The direction can be {@link CloudSimTags#ICMP_PKT_SUBMIT}
     * or {@link CloudSimTags#ICMP_PKT_RETURN}.
     *
     * @return
     */
    public int getTag() {
        return tag;
    }

    /**
     * Sets the packet direction that indicates if it is going or returning.
     * The direction can be {@link CloudSimTags#ICMP_PKT_SUBMIT}
     * or {@link CloudSimTags#ICMP_PKT_RETURN}.
     *
     * @param tag the direction to set
     * @return true if the tag is valid, false otherwise
     */
    public boolean setTag(final int tag) {
        if (tag < CloudSimTags.ICMP_PKT_SUBMIT || tag > CloudSimTags.ICMP_PKT_RETURN) {
            return false;
        }

        this.tag = tag;
        return true;
    }

}
