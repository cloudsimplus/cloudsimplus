/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.network.topologies;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents a topological network node that retrieves its information from a
 * topological-generated file (e.g., topology-generator).
 *
 * @author Thomas Hohnstein
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
@Getter @Setter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class TopologicalNode {
    /**
     * The BRITE id of the node inside the network.
     */
    @EqualsAndHashCode.Include
    private int id;

    /**
     * The name of the node.
     */
    @NonNull
    private String nodeName;

    /**
     * The x,y world coordinates of this network node.
     */
    @NonNull
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
     * @param id The BRITE id of the node inside the network
     */
    public TopologicalNode(final int id) {
        this(id, new Point2D());
    }

    /**
     * Creates a network topology node including world coordinates.
     *
     * @param id The BRITE id of the node inside the network
     * @param worldCoordinates  the x,y world coordinates of the node
     */
    public TopologicalNode(final int id, final Point2D worldCoordinates) {
        this(id, String.valueOf(id), worldCoordinates);
    }

    /**
     * Creates a network topology node including world coordinates and the node name.
     *
     * @param id   the BRITE id of the node inside the network
     * @param nodeName the name of the node inside the network
     * @param worldCoordinates    the x,y world coordinates of the node
     */
    public TopologicalNode(final int id, final String nodeName, final Point2D worldCoordinates) {
        this.setId(id);
        this.setNodeName(nodeName);
        this.setWorldCoordinates(worldCoordinates);
    }
}
