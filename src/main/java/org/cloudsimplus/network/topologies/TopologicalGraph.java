/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.network.topologies;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a graph containing vertices (nodes) and edges (links),
 * used for input with a network-layer.
 *
 * <p>Graphical-Output Restrictions:
 * <ul>
 * <li>EdgeColors: GraphicalProperties.getColorEdge</li>
 * <li>NodeColors: GraphicalProperties.getColorNode</li>
 * </ul>
 * </p>
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class TopologicalGraph {
    /**
     * The links (edges) of the network graph.
     */
    private final List<TopologicalLink> linksList;

    /**
     * The nodes (vertices) of the network graph.
     */
    private final List<TopologicalNode> nodeList;

    /**
     * Creates an empty network topology graph.
     */
    public TopologicalGraph() {
        linksList = new LinkedList<>();
        nodeList = new LinkedList<>();
    }

    /**
     * Adds a link between two topological nodes.
     *
     * @param edge the topological link
     */
    public void addLink(final TopologicalLink edge) {
        linksList.add(Objects.requireNonNull(edge));
    }

    /**
     * Adds a Topological Node to this graph.
     *
     * @param node the topological node to add
     */
    public void addNode(final TopologicalNode node) {
        nodeList.add(Objects.requireNonNull(node));
    }

    /**
     * @return the number of nodes contained inside the topological-graph.
     */
    public int getNumberOfNodes() {
        return nodeList.size();
    }

    /**
     * @return the number of links contained inside the topological-graph.
     */
    public int getNumberOfLinks() {
        return linksList.size();
    }

    /**
     * @return a <b>read-only</b> List of all network-graph links.
     */
    public List<TopologicalLink> getLinksList() {
        return Collections.unmodifiableList(linksList);
    }

    /**
     * @return a <b>read-only</b> list of nodes from the network graph.
     */
    public List<TopologicalNode> getNodeList() {
        return Collections.unmodifiableList(nodeList);
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder(61);
        builder.append("topological-node-information: ").append(System.lineSeparator());

        for (final TopologicalNode node : nodeList) {
            builder.append("%d | %s%n".formatted(node.getId(), node.getWorldCoordinates()));
        }

        builder.append("%n%n node-link-information:%n".formatted());

        for (final TopologicalLink link : linksList) {
            builder.append(
                "from: %d to: %d delay: %.2f%n"
                .formatted(link.getSrcNodeID(), link.getDestNodeID(), link.getLinkDelay()));
        }

        return builder.toString();
    }
}
