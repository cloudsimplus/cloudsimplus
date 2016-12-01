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

import org.cloudbus.cloudsim.core.CloudSimTags;

/**
 * Defines the structure for a network packet.
 *
 * @author Gokul Poduval
 * @author Chen-Khong Tham, National University of Singapore
 * @since CloudSim Toolkit 1.0
 */
public interface Packet {
    /**
	 * Returns a string describing this packet in detail.
	 *
	 * @return description of this packet
	 * @pre $none
	 * @post $none
	 */
	@Override
	String toString();

	/**
	 * Returns the size of this packet
	 *
	 * @return size of the packet
	 * @pre $none
	 * @post $none
	 */
	long getSize();

	/**
	 * Sets the size of this packet
	 *
	 * @param size size of the packet
	 * @return <tt>true</tt> if it is successful, <tt>false</tt> otherwise
	 * @pre size >= 0
	 * @post $none
	 */
	boolean setSize(long size);

	/**
	 * Returns the destination id of this packet.
	 *
	 * @return destination id
	 * @pre $none
	 * @post $none
	 */
	int getDestId();

	/**
	 * Returns the ID of this packet
	 *
	 * @return packet ID
	 * @pre $none
	 * @post $none
	 */
	int getId();

	/**
	 * Returns the ID of the source of this packet.
	 *
	 * @return source id
	 * @pre $none
	 * @post $none
	 */
	int getSrcId();

	/**
	 * Gets the network service type of this packet
	 *
	 * @return the network service type
	 * @pre $none
	 * @post $none
	 */
	int getNetServiceLevel();

	/**
	 * Sets the network service type of this packet.
	 * <p>
	 * By default, the service type is 0 (zero). It is depends on the packet scheduler to determine
	 * the priority of this service level.
	 *
	 * @param serviceType this packet's service type
	 * @pre serviceType >= 0
	 * @post $none
	 */
	void setNetServiceLevel(int serviceType);

	/**
	 * Gets an entity ID from the last hop that this packet has traversed.
	 *
	 * @return an entity ID
	 * @pre $none
	 * @post $none
	 */
	int getLastHop();

	/**
	 * Sets an entity ID from the last hop that this packet has traversed.
	 *
	 * @param lastHop an entity ID from the last hop
	 * @pre last > 0
	 * @post $none
	 */
	void setLastHop(int lastHop);

    /**
     * Gets the packet direction that indicates if it is going or returning.
     * The direction can be {@link CloudSimTags#INFOPKT_SUBMIT}
     * or {@link CloudSimTags#INFOPKT_RETURN}.
     *
     * @return
     * @pre $none
     * @post $none
     */
	int getTag();

    /**
     * Sets the packet direction that indicates if it is going or returning.
     * The direction can be {@link CloudSimTags#INFOPKT_SUBMIT}
     * or {@link CloudSimTags#INFOPKT_RETURN}.
     *
     * @param tag the direction to set
     * @return true if the tag is valid, false otherwise
     * @pre tag > 0
     * @post $none
     */
    boolean setTag(int tag);
}
