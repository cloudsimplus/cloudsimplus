/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network.topologies;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a graph containing vertices (nodes) and edges (links),
 * used for input with a network-layer.
 * <p>
 * <p>Graphical-Output Restricions:
 * <ul>
 * <li>EdgeColors: GraphicalProperties.getColorEdge
 * <li>NodeColors: GraphicalProperties.getColorNode
 * </ul>
 * </p>
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologicalGraph {
    /**
     * The list of links of the network graph.
     */
    private final List<TopologicalLink> linksList;

    private final List<TopologicalNode> nodeList;

    /**
     * Creates an empty graph-object.
     */
    public TopologicalGraph() {
        linksList = new LinkedList<>();
        nodeList = new LinkedList<>();
    }

    /**
     * Adds an link between two topological nodes.
     *
     * @param edge the topological link
     */
    public void addLink(TopologicalLink edge) {
        linksList.add(edge);
    }

    /**
     * Adds an Topological Node to this graph.
     *
     * @param node the topological node to add
     */
    public void addNode(TopologicalNode node) {
        nodeList.add(node);
    }

    /**
     * Gets the number of nodes contained inside the topological-graph.
     *
     * @return number of nodes
     */
    public int getNumberOfNodes() {
        return nodeList.size();
    }

    /**
     * Gets the number of links contained inside the topological-graph.
     *
     * @return number of links
     */
    public int getNumberOfLinks() {
        return linksList.size();
    }

    /**
     * Gets a <b>read-only</b> List of all network-graph links.
     *
     * @return the List of network-graph links
     */
    public List<TopologicalLink> getLinksList() {
        return Collections.unmodifiableList(linksList);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(61);
        builder.append("topological-node-information: \n");

        for (final TopologicalNode node : nodeList) {
            builder.append(
                String.format("%d | %s\n",
                node.getNodeId(), node.getWorldCoordinates()));
        }

        builder.append("\n\n node-link-information:\n");

        for (final TopologicalLink link : linksList) {
            builder.append(
                String.format("from: %d to: %d delay: %.2f\n",
                link.getSrcNodeID(), link.getDestNodeID(), link.getLinkDelay()));
        }

        return builder.toString();
    }

    /**
     * Gets a <b>read-only</b> list of nodes of the network graph.
     */
    public List<TopologicalNode> getNodeList() {
        return Collections.unmodifiableList(nodeList);
    }
}
