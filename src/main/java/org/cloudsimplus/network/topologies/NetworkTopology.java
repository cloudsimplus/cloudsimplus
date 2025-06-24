/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network.topologies;

import org.cloudsimplus.core.SimEntity;

/**
 * Implements a network layer by reading the topology from a file in a specific format
 * that is defined by each implementing class.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Plus 1.0
 */
public interface NetworkTopology {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link NetworkTopology} objects.
     */
    NetworkTopology NULL = new NetworkTopologyNull();

    /**
     * Adds a new link in the network topology. The {@link SimEntity}s that
     * represent the source and destination of the link will be mapped to {@link BriteNetworkTopology BRITE}
     * entities.
     * @param src {@link SimEntity} that represents the link's source node
     * @param dest {@link SimEntity} that represents the link's destination node
     * @param bw link's bandwidth (in Megabits/s)
     * @param lat link's latency (in seconds)
     */
    void addLink(SimEntity src, SimEntity dest, double bw, double lat);

    void removeLink(SimEntity src, SimEntity dest);

    /**
     * Calculates the delay (in seconds) between two nodes.
     *
     * @param src {@link SimEntity} that represents the link's source node
     * @param dest {@link SimEntity} that represents the link's destination node
     * @return communication delay (in seconds) between the two nodes
     */
    double getDelay(SimEntity src, SimEntity dest);
}
