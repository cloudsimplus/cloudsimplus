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

/**
 * Defines the structure for a network packet.
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 */
public interface NetworkPacket {
    /**
     * Gets the size of the packet.
     *
     * @return
     * @pre $none
     * @post $none
     */
    long getSize();

    /**
     * Gets the ID of the entity that this packet is coming from (the sender).
     *
     * @return
     * @pre $none
     * @post $none
     */
    int getSourceId();

    /**
     * Sets the ID of the entity that this packet is coming from (the sender).
     * @param sourceId the source ID to set
     */
    void setSourceId(int sourceId);

    /**
     * Gets the ID of the entity that the packet is going to.
     *
     * @return
     * @pre $none
     * @post $none
     */
    int getDestinationId();

    /**
     * Sets the ID of the entity that the packet is going to (the receiver).
     *
     * @param destinationId the destination ID to set
     * @pre destinationId > 0
     * @post $none
     */
    void setDestinationId(int destinationId);

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
