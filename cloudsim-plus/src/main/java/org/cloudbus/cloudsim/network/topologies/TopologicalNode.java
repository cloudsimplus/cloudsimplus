/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies;

import java.util.Objects;

/**
 * Represents an topological network node that retrieves its information from a
 * topological-generated file (eg. topology-generator)
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologicalNode {

    private int nodeId;

    private String nodeName;

    private Point2D worldCoordinates;

    /**
     * Creates a network topology node with ID equals to zero.
     */
    public TopologicalNode(){
        this(0);
    }

    /**
     * Creates a network topology node with a specific ID.
     *
     * @param nodeId The BRITE id of the node inside the network
     */
    public TopologicalNode(final int nodeId) {
        this(nodeId, new Point2D());
    }

    /**
     * Creates a network topology node including world-coordinates.
     *
     * @param nodeId The BRITE id of the node inside the network
     * @param worldCoordinates  the x,y world-coordinates of the Node
     */
    public TopologicalNode(final int nodeId, final Point2D worldCoordinates) {
        this(nodeId, String.valueOf(nodeId), worldCoordinates);
    }

    /**
     * Creates a network topology node including world-coordinates and the nodeName.
     *
     * @param nodeId   The BRITE id of the node inside the network
     * @param nodeName The name of the node inside the network
     * @param worldCoordinates    the x,y world-coordinates of the Node
     */
    public TopologicalNode(final int nodeId, final String nodeName, final Point2D worldCoordinates) {
        this.worldCoordinates = Objects.requireNonNull(worldCoordinates);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    /**
     * Gets the BRITE id of the node inside the network.
     *
     * @return the nodeId
     */
    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(final int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Gets the name of the node
     *
     * @return name of the node
     */
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }
    /**
     * Gets the x,y world coordinates of this network-node.
     *
     * @return the x,y world coordinates
     */
    public Point2D getWorldCoordinates() {
        return worldCoordinates;
    }

    public void setWorldCoordinates(final Point2D worldCoordinates) {
        this.worldCoordinates = worldCoordinates;
    }

}
