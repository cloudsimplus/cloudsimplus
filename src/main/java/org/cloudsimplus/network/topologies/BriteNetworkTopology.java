/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.network.topologies;

import lombok.Getter;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.network.DelayMatrix;
import org.cloudsimplus.network.topologies.readers.TopologyReaderBrite;
import org.cloudsimplus.util.ResourceLoader;
import org.cloudsimplus.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// Implements a network layer by reading the topology from a file in the
/// [BRITE format](http://www.cs.bu.edu/brite/user_manual/node29.html),
/// the **B**oston university **R**epresentative **I**nternet **T**opology g**E**nerator,
/// and generates a topological network from it.
/// Information of this network is used to simulate latency in network traffic of CloudSim.
///
/// The topology file may contain more nodes than the number of entities in the
/// simulation. It allows users to increase the scale of the simulation without
/// changing the topology file. Nevertheless, each CloudSim entity must be mapped
/// to one (and only one) BRITE node to allow proper work of the network simulation.
/// Each BRITE node can be mapped to only one entity at a time.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
/// @see #getInstance(String)
/// @link [Brite Oficial Website](http://www.cs.bu.edu/brite/)
public final class BriteNetworkTopology implements NetworkTopology {
    private static final Logger LOGGER = LoggerFactory.getLogger(BriteNetworkTopology.class.getSimpleName());

    /**
     * The BRITE id to use for the next node to be created in the network.
     */
    private int nextIdx;

    /**
     * Checks if the network simulation is working. If there were some problem
     * during creation of the network (e.g., during parsing of a BRITE file) that does
     * not allow a proper simulation of the network, this method returns false.
     */
    @Getter
    private boolean networkEnabled;

    /**
     * A matrix containing the delay (in seconds) between every pair of nodes in the network.
     */
    private DelayMatrix delayMatrix;

    /** @see #getBwMatrix() */
    private double[][] bwMatrix;

    /**
     * The Topological Graph of the network.
     */
    @Getter
    private TopologicalGraph graph;

    /**
     * The map between CloudSim entities and BRITE entities.
     * Each key is a CloudSim entity, and each value is the corresponding BRITE entity ID.
     */
    private Map<SimEntity, Integer> entitiesMap;

    /**
     * Instantiates a Network Topology from a file inside the <b>application's resource directory</b>.
     * @param fileName the <b>relative name</b> of the BRITE file
     * @return the BriteNetworkTopology instance.
     */
    public static BriteNetworkTopology getInstance(final String fileName){
        final InputStreamReader reader = ResourceLoader.newInputStreamReader(fileName, BriteNetworkTopology.class);
        return new BriteNetworkTopology(reader);
    }

    /**
     * Instantiates an empty Network Topology.
     * @see #BriteNetworkTopology(String)
     * @see #BriteNetworkTopology(InputStreamReader)
     * @see #getInstance(String)
     */
    public BriteNetworkTopology() {
        entitiesMap = new HashMap<>();
        bwMatrix = new double[0][0];
        graph = new TopologicalGraph();
        delayMatrix = new DelayMatrix();
    }

    /**
     * Instantiates a Network Topology if a given file exists and can be successfully parsed.
     * The file is written in the BRITE format and contains
     * topological information of simulation entities.
     *
     * @param filePath the path of the BRITE file
     * @see #BriteNetworkTopology()
     * @see #BriteNetworkTopology(InputStreamReader)
     * @see #getInstance(String)
     */
    public BriteNetworkTopology(final String filePath) {
        this(ResourceLoader.newInputStreamReader(filePath));
        LOGGER.info("Topology file: {}", filePath);
    }

    /**
     * Creates a network topology from a given input stream reader.
     * The file is written in the BRITE format and contains
     * topological information of simulation entities.
     *
     * @param reader the reader for the topology file
     * @see #BriteNetworkTopology()
     * @see #BriteNetworkTopology(InputStreamReader)
     * @see #getInstance(String)
     */
    private BriteNetworkTopology(final InputStreamReader reader) {
        this();
        final var instance = new TopologyReaderBrite();
        graph = instance.readGraphFile(reader);
        generateMatrices();
    }

    /**
     * Generates the matrices used internally to set latency and bandwidth between elements.
     */
    private void generateMatrices() {
        delayMatrix = new DelayMatrix(graph, false);
        bwMatrix = createBwMatrix(graph, false);
        networkEnabled = true;
    }

    /**
     * Creates the matrix containing the available bandwidth between every pair of nodes.
     *
     * @param graph topological graph describing the topology
     * @param directed true if the graph is directed; false otherwise
     * @return the bandwidth graph
     */
    private double[][] createBwMatrix(final TopologicalGraph graph, final boolean directed) {
        final int nodes = graph.getNumberOfNodes();
        final double[][] matrix = Util.newSquareMatrix(nodes);

        for (final TopologicalLink edge : graph.getLinksList()) {
            matrix[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkBw();
            if (!directed) {
                matrix[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkBw();
            }
        }

        return matrix;
    }

    @Override
    public void addLink(final SimEntity src, final SimEntity dest, final double bandwidth, final double latency) {
        if (graph == null) {
            graph = new TopologicalGraph();
        }

        if (entitiesMap == null) {
            entitiesMap = new HashMap<>();
        }

        addNodeMapping(src);
        addNodeMapping(dest);

        final var link = new TopologicalLink(entitiesMap.get(src), entitiesMap.get(dest), latency, bandwidth);
        graph.addLink(link);
        generateMatrices();
    }

    @Override
    public void removeLink(final SimEntity src, final SimEntity dest) {
        throw new UnsupportedOperationException("Removing links is not yet supported on BriteNetworkTopologies");
    }

    /**
     * Adds a new node in the network topology graph if it's absent.
     * @param entity the CloudSim entity to check if there isn't a BRITE mapping yet.
     */
    private void addNodeMapping(final SimEntity entity) {
        if (entitiesMap.putIfAbsent(entity, nextIdx) == null) {
            graph.addNode(new TopologicalNode(nextIdx));
            nextIdx++;
        }
    }

    /**
     * Maps a {@link SimEntity} to a BRITE node in the network topology.
     * @param entity {@link SimEntity} being mapped
     * @param briteID ID of the BRITE node that corresponds to the CloudSim entity
     */
    public void mapNode(final SimEntity entity, final int briteID) {
        if (!networkEnabled) {
            return;
        }

        if (entitiesMap.containsKey(entity)) {
            LOGGER.warn("Network mapping: CloudSim entity {} already mapped.", entity);
            return;
        }

        if (entitiesMap.containsValue(briteID)) {
            LOGGER.warn("BRITE node {} already in use.", briteID);
            return;
        }

        entitiesMap.put(entity, briteID);
    }

    /**
     * Removes a previous map between a {@link SimEntity} and a BRITE node in the network topology.
     * @param entity {@link SimEntity} to unmap
     */
    public void unmapNode(final SimEntity entity) {
        if (!networkEnabled) {
            return;
        }

        entitiesMap.remove(entity);
    }

    @Override
    public double getDelay(final SimEntity src, final SimEntity dest) {
        if (!networkEnabled) {
            return 0.0;
        }

        try {
            final int srcEntityBriteId = entitiesMap.getOrDefault(src, -1);
            final int destEntityBriteId = entitiesMap.getOrDefault(dest, -1);
            return delayMatrix.getDelay(srcEntityBriteId, destEntityBriteId);
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    /**
     * Gets a <b>copy</b> of the matrix containing the bandwidth (in Megabits/s)
     * between every pair of {@link SimEntity}s in the network.
     */
    public double[][] getBwMatrix() {
        return Arrays.copyOf(bwMatrix, bwMatrix.length);
    }
}
