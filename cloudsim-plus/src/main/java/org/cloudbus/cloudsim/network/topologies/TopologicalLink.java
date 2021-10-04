/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies;

/**
 * Represents a link (edge) of a network graph
 * where the network topology was defined
 * from a file in <a href="http://www.cs.bu.edu/brite/user_manual/node29.html">BRITE format</a>.
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologicalLink {

    /**
     * The BRITE id of the source node of the link.
     */
    private final int srcNodeID;

    /**
     * The BRITE id of the destination node of the link.
     */
    private final int destNodeID;

    /** @see #getLinkDelay() */
    private final double linkDelay;

    /** @see #getLinkBw() */
    private final double linkBw;

    /**
     * Creates a new Topological Link.
     * @param srcNode the BRITE id of the source node of the link.
     * @param destNode the BRITE id of the destination node of the link.
     * @param delay the link delay of the connection (in seconds).
     * @param bandwidth the link bandwidth (in Megabits/s)
     */
    public TopologicalLink(final int srcNode, final int destNode, final double delay, final double bandwidth) {
        linkDelay = delay;
        srcNodeID = srcNode;
        destNodeID = destNode;
        linkBw = bandwidth;
    }

    /**
     * Gets the BRITE id of the source node of the link.
     *
     * @return nodeID
     */
    public int getSrcNodeID() {
        return srcNodeID;
    }

    /**
     * Gets the BRITE id of the destination node of the link.
     *
     * @return nodeID
     */
    public int getDestNodeID() {
        return destNodeID;
    }

    /**
     * Gets the delay of the link.
     *
     * @return the link delay (in seconds)
     */
    public double getLinkDelay() {
        return linkDelay;
    }

    /**
     * Gets the bandwidth of the link.
     *
     * @return the bandwidth in Megabits/s.
     */
    public double getLinkBw() {
        return linkBw;
    }
}
