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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InfoPacket class can be used to gather information from the network layer. An
 * InfoPacket traverses the network topology similar to a
 * {@link NetworkPacket},
 * but it collects information like bandwidths,
 * and Round Trip Time etc. It is the equivalent of ICMP in physical networks.
 * <p/>
 * You can set all the parameters to an InfoPacket that can be applied to a
 * NetworkPacket. So if you want to find out the kind of information that a
 * particular type of NetworkPacket is experiencing, set the size and network class
 * of an InfoPacket to the same as the NetworkPacket, and send it to the same
 * destination from the same source.
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @since CloudSim Toolkit 1.0
 *
 * @todo @author manoelcampos InfoPacket, HostPacket and NetworkPacket should implement at least
 * one common interface.
 */
public class InfoPacket implements Packet {
    /**
     * @see #getTag()
     */
    private int tag;

    /**
     * The packet name.
     */
    private final String name;

    /**
     * The size of the packet.
     */
    private long size;

    /**
     * The id of the packet.
     */
    private final int packetId;

    /**
     * The original sender id.
     */
    private final int srcId;

    /**
     * The destination id.
     */
    private int destId;

    /**
     * The lastHop hop.
     */
    private int lastHop;

    /**
     * The number of hops.
     */
    private int hopsNumber;

    /**
     * The original ping size.
     */
    private long pingSize;

    /**
     * The level of service type.
     */
    private int netServiceLevel;

    /**
     * The bandwidth bottleneck.
     */
    private double bandwidth;

    /**
     * The list of entity IDs. The entities are elements where the packet
     * traverses, such as Routers or Datacenters.
     */
    private final List<Integer> entities;

    /**
     * A list containing the time the packet arrived at every entity it has
     * traversed
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

    /**
     * The formatting for decimal points.
     */
    private DecimalFormat num;

    /**
     * Constructs a new Information packet.
     *
     * @param name Name of this packet
     * @param packetID The ID of this packet
     * @param size size of the packet
     * @param srcID The ID of the entity that sends out this packet
     * @param destID The ID of the entity to which this packet is destined
     * @param netServiceLevel the class of traffic this packet belongs to
     * @pre name != null
     * @post $none
     */
    public InfoPacket(String name, int packetID, long size, int srcID, int destID, int netServiceLevel) {
        this.name = name;
        packetId = packetID;
        srcId = srcID;
        destId = destID;
        this.size = size;
        this.netServiceLevel = netServiceLevel;
        this.entities = new ArrayList<>();
        this.entryTimes = new ArrayList<>();
        this.exitTimes = new ArrayList<>();
        this.baudRates = new ArrayList<>();

        lastHop = srcId;
        tag = CloudSimTags.INFOPKT_SUBMIT;
        bandwidth = -1;
        hopsNumber = 0;
        pingSize = size;
        num = new DecimalFormat("#0.000#");
    }


    /**
     * Returns the ID of this packet.
     *
     * @return packet ID
     * @pre $none
     * @post $none
     */
    @Override
    public int getId() {
        return packetId;
    }

    /**
     * Sets original size of ping request.
     *
     * @param size ping data size (in bytes)
     * @pre size >= 0
     * @post $none
     */
    public void setOriginalPingSize(long size) {
        pingSize = size;
    }

    /**
     * Gets original size of ping request.
     *
     * @return original size
     * @pre $none
     * @post $none
     */
    public long getOriginalPingSize() {
        return pingSize;
    }

    /**
     * Returns a human-readable information of this packet.
     *
     * @return description of this packet
     * @pre $none
     * @post $none
     */
    @Override
    public String toString() {
        if (name == null) {
            return "Empty InfoPacket that contains no ping information.";
        }

        int SIZE = 1000;   // number of chars
        StringBuffer sb = new StringBuffer(SIZE);
        sb.append("Ping information for " + name + "\n");
        sb.append("Entity Name\tEntry Time\tExit Time\t Bandwidth\n");
        sb.append("----------------------------------------------------------\n");

        String tab = "    ";  // 4 spaces
        for (int i = 0; i < entities.size(); i++) {
            int resID = entities.get(i).intValue();
            sb.append("Entity " + resID + "\t\t");

            String entry = getData(entryTimes, i);
            String exit = getData(exitTimes, i);
            String bw = getData(baudRates, i);

            sb.append(entry + tab + tab + exit + tab + tab + bw + "\n");
        }

        sb.append("\nRound Trip Time : " + num.format(getTotalResponseTime()));
        sb.append(" seconds");
        sb.append("\nNumber of Hops  : " + getNumberOfHops());
        sb.append("\nBottleneck Bandwidth : " + bandwidth + " bits/s");
        return sb.toString();
    }

    /**
     * Gets the data of a given index in a list.
     *
     * @param v a list
     * @param index the location in a list
     * @return the data
     * @pre v != null
     * @post index > 0
     */
    private String getData(List<Double> v, int index) {
        String result;
        try {
            Double obj = v.get(index);
            double id = obj;
            result = num.format(id);
        } catch (Exception e) {
            result = "    N/A";
        }

        return result;
    }

    /**
     * Gets the size of the packet.
     *
     * @return size of the packet.
     * @pre $none
     * @post $none
     */
    @Override
    public long getSize() {
        return size;
    }

    /**
     * Sets the size of the packet.
     *
     * @param size size of the packet
     * @return <tt>true</tt> if it is successful, <tt>false</tt> otherwise
     * @pre size >= 0
     * @post $none
     */
    @Override
    public boolean setSize(long size) {
        if (size < 0) {
            return false;
        }

        this.size = size;
        return true;
    }

    /**
     * Gets the id of the entity to which the packet is destined.
     *
     * @return the desination ID
     * @pre $none
     * @post $none
     */
    @Override
    public int getDestId() {
        return destId;
    }

    /**
     * Gets the id of the entity that sent out the packet.
     *
     * @return the source ID
     * @pre $none
     * @post $none
     */
    @Override
    public int getSrcId() {
        return srcId;
    }

    /**
     * Gets the number of hops that the packet has traversed. Since the
     * packet takes a round trip, the same router may have been traversed twice.
     *
     * @return
     * @pre $none
     * @post $none
     */
    public int getNumberOfHops() {
        int PAIR = 2;
        return ((hopsNumber - PAIR) + 1) / PAIR;
    }

    /**
     * Gets the total time that the packet has spent in the network. This is
     * basically the Round-Trip-Time (RTT). Dividing this by half should be the
     * approximate latency.
     * <p/>
     * RTT is taken as the "final entry time" - "first exit time".
     *
     * @return total round time
     * @pre $none
     * @post $none
     */
    public double getTotalResponseTime() {
        double time = 0;
        try {
            double startTime = exitTimes.stream().findFirst().orElse(0.0);
            double receiveTime = entryTimes.stream().findFirst().orElse(0.0);
            time = receiveTime - startTime;
        } catch (Exception e) {
            time = 0;
        }

        return time;
    }

    /**
     * Returns the bottleneck bandwidth between the source and the destination.
     *
     * @return the bottleneck bandwidth
     * @pre $none
     * @post $none
     */
    public double getBaudRate() {
        return bandwidth;
    }

    /**
     * Add an entity where the InfoPacket traverses. This method should be
     * called by network entities that count as hops, for instance Routers or
     * CloudResources. It should not be called by links etc.
     *
     * @param id the id of the hop that this InfoPacket is traversing
     * @pre id > 0
     * @post $none
     */
    public void addHop(int id) {
        hopsNumber++;
        entities.add(id);
    }

    /**
     * Register the time the packet arrives at an entity such as a Router or
     * CloudResource. This method should be called by routers and other entities
     * when the InfoPacket reaches them along with the current simulation time.
     *
     * @param time current simulation time, use
     * {@link org.cloudbus.cloudsim.core.CloudSim#clock()} to obtain this
     * @pre time >= 0
     * @post $none
     *
     */
    public void addEntryTime(double time) {
        if (time < 0) {
            time = 0.0;
        }

        entryTimes.add(time);
    }

    /**
     * Register the time the packet leaves an entity such as a Router or
     * CloudResource. This method should be called by routers and other entities
     * when the InfoPacket is leaving them. It should also supply the current
     * simulation time.
     *
     * @param time current simulation time, use
     * {@link org.cloudbus.cloudsim.core.CloudSim#clock()} to obtain this
     * @pre time >= 0
     * @post $none
     */
    public void addExitTime(double time) {
        if (time < 0) {
            time = 0.0;
        }

        exitTimes.add(time);
    }

    /**
     * Register the baud rate of the output link where the current entity that
     * holds the InfoPacket will send it next. Every entity that the InfoPacket
     * traverses should add the baud rate of the link on which this packet will
     * be sent out next.
     *
     * @param baudRate the entity's baud rate in bits/s
     * @pre baudRate > 0
     * @post $none
     */
    public void addBaudRate(double baudRate) {
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
    public List<Integer> getHopsList() {
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
     * Gets the entity ID of the lastHop hop that this packet has traversed.
     *
     * @return an entity ID
     * @pre $none
     * @post $none
     */
    @Override
    public int getLastHop() {
        return lastHop;
    }

    /**
     * Sets the entity ID of the lastHop hop that this packet has traversed.
     *
     * @param lastHop an entity ID from the lastHop hop
     * @pre last > 0
     * @post $none
     */
    @Override
    public void setLastHop(int lastHop) {
        this.lastHop = lastHop;
    }

    /**
     * Gets the network service type of the packet.
     *
     * @return the network service type
     * @pre $none
     * @post $none
     */
    public int getNetServiceLevel() {
        return netServiceLevel;
    }

    /**
     * Sets the network service type of the packet.
     *
     * @param netServiceLevel the packet's network service type
     * @pre netServiceLevel >= 0
     * @post $none
     */
    public void setNetServiceLevel(int netServiceLevel) {
        this.netServiceLevel = netServiceLevel;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public boolean setTag(int tag) {
        if(tag < CloudSimTags.INFOPKT_SUBMIT || tag > CloudSimTags.INFOPKT_RETURN){
            return false;
        }

        this.tag = tag;
        return true;
    }

    /**
     * Sets the destination ID for the packet.
     *
     * @param id this packet's destination ID
     * @pre id > 0
     * @post $none
     */
    public void setDestId(int id) {
        destId = id;
    }

}
