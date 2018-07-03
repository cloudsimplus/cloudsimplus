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

package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.core.Identifiable;

/**
 * Defines the structure for a network packet.
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @author Manoel Campos da Silva Filho
 * @param <T> the class of objects involved in the packet transmission,
 *           if they are Hosts, VMs, Switches, etc.
 *
 * @since CloudSim Toolkit 1.0
 */
public interface NetworkPacket<T extends Identifiable> {
    /**
     * Gets the size of the packet in bytes.
     *
     * @return
     * @pre $none
     * @post $none
     */
    long getSize();

    /**
     * Gets the entity that this packet is coming from (the sender).
     *
     * @return
     * @pre $none
     * @post $none
     */
    T getSource();

    /**
     * Sets the entity that this packet is coming from (the sender).
     * @param source the source ID to set
     */
    void setSource(T source);

    /**
     * Gets the entity that the packet is going to.
     *
     * @return
     * @pre $none
     * @post $none
     */
    T getDestination();

    /**
     * Sets the entity that the packet is going to (the receiver).
     *
     * @param destination the destination to set
     * @post $none
     */
    void setDestination(T destination);

    /**
     * Gets the time when the packet was sent.
     * @return
     */
    double getSendTime();

    /**
     * Sets the time when the packet was sent.
     * @param time the time to set
     */
    void setSendTime(double time);

    /**
     * Gets the time when the packet was received.
     * @return
     */
    double getReceiveTime();

    /**
     * Sets the time when the packet was received.
     * @param time the time to set
     */
    void setReceiveTime(double time);
}
