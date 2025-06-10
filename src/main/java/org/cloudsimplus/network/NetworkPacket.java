/*
 * Gokul Poduval & Chen-Khong Tham
 * Computer Communication Networks (CCN) Lab
 * Dept of Electrical & Computer Engineering
 * National University of Singapore
 * August 2004
 *
 * Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2004, The University of Melbourne, Australia and National
 * University of Singapore
 * Packet.java - Interface of a Network Packet.
 *
 */

package org.cloudsimplus.network;

import org.cloudsimplus.core.Identifiable;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.network.switches.Switch;
import org.cloudsimplus.vms.Vm;

/**
 * Defines the structure for a network packet.
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of objects involved in the packet transmission.
 *           if they are {@link Host}s, {@link Vm}s, {@link Switch}es, etc.
 *
 * @since CloudSim Toolkit 1.0
 */
public interface NetworkPacket<T extends Identifiable> {
    /**
     * @return size of the packet in bytes.
     */
    long getSize();

    /**
     * @return the entity that this packet is coming from (the sender).
     */
    T getSource();

    /**
     * Sets the entity that this packet is coming from (the sender).
     * @param source the source ID to set
     * @return this packet
     */
    NetworkPacket setSource(T source);

    /**
     * @return the entity that the packet is going to.
     */
    T getDestination();

    /**
     * Sets the entity that the packet is going to (the receiver).
     *
     * @param destination the destination to set
     * @return this packet
     */
    NetworkPacket setDestination(T destination);

    /**
     * @return the time (in seconds) when the packet was sent.
     */
    double getSendTime();

    /**
     * Sets the time when the packet was sent.
     * @param time the time to set (in seconds)
     * @return this packet
     */
    NetworkPacket setSendTime(double time);

    /**
     * @return the time (in seconds) when the packet was received.
     */
    double getReceiveTime();

    /**
     * Sets the time when the packet was received.
     * @param time the time to set (in seconds)
     * @return this packet
     */
    NetworkPacket setReceiveTime(double time);
}
