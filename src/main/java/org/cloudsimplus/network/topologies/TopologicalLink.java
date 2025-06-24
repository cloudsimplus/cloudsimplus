/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.network.topologies;

import lombok.Getter;

/// Represents a link (edge) of a network graph
/// where the network topology was defined
/// from a file in [BRITE format](http://www.cs.bu.edu/brite/user_manual/node29.html).
///
/// @author Thomas Hohnstein
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class TopologicalLink {

    /**
     * The BRITE id of the link's source node.
     */
    @Getter
    private final int srcNodeID;

    /**
     * The BRITE id of the destination node of the link.
     */
    @Getter
    private final int destNodeID;

    /**
     * The delay of the link (in seconds).
     */
    @Getter
    private final double linkDelay;

    /**
     * The bandwidth of the link (in Megabits/s).
     */
    @Getter
    private final double linkBw;

    /**
     * Creates a new Topological Link.
     * @param srcNode the BRITE id of link's source node.
     * @param destNode the BRITE id of the destination node of the link.
     * @param delay the link delay of the connection (in seconds).
     * @param bandwidth the link bandwidth (in Megabits/s)
     */
    public TopologicalLink(final int srcNode, final int destNode, final double delay, final double bandwidth) {
        srcNodeID = srcNode;
        destNodeID = destNode;
        linkDelay = delay;
        linkBw = bandwidth;
    }
}
