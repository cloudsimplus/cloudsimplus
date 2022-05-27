/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.topologies;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.network.DelayMatrix;
import org.cloudbus.cloudsim.network.topologies.readers.TopologyReaderBrite;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a network layer by reading the topology from a file in the
 * <a href="http://www.cs.bu.edu/brite/user_manual/node29.html">BRITE
 * format</a>, the <b>B</b>oston university
 * <b>R</b>epresentative <b>I</b>nternet <b>T</b>opology g<b>E</b>nerator,
 * and generates a topological network
 * from it. Information of this network is used to simulate latency in network
 * traffic of CloudSim.
 *
 * <p>The topology file may contain more nodes than the number of entities in the
 * simulation. It allows users to increase the scale of the simulation without
 * changing the topology file. Nevertheless, each CloudSim entity must be mapped
 * to one (and only one) BRITE node to allow proper work of the network
 * simulation. Each BRITE node can be mapped to only one entity at a time.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 * @see #getInstance(String)
 * @see <a href="http://www.cs.bu.edu/brite/">Brite Oficial Website (shut down)</a>
 * @see <a href="https://web.archive.org/web/20200119144536/http://www.cs.bu.edu:80/brite/">Web archieve of Brite Oficial Website</a>
 */
public final class BriteNetworkTopology implements NetworkTopology {
    private static final Logger LOGGER = LoggerFactory.getLogger(BriteNetworkTopology.class.getSimpleName());

    /**
     * The BRITE id to use for the next node to be created in the network.
     */
    private int nextIdx;

    private boolean networkEnabled;

    /**
     * A matrix containing the delay (in seconds) between every pair of nodes in the network.
     */
    private DelayMatrix delayMatrix;

    /** @see #getBwMatrix() */
    private double[][] bwMatrix;

    /** @see #getTopologicalGraph() */
    private TopologicalGraph graph;

    /**
     * The map between CloudSim entities and BRITE entities.
     * Each key is a CloudSim entity and each value the corresponding BRITE entity ID.
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
     * Instantiates a Network Topology if a given file exists and can be successfully
     * parsed. File is written in the BRITE format and contains
     * topological information on simulation entities.
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
     * topological information on simulation entities.
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
     * Generates the matrices used internally to set latency and bandwidth
     * between elements.
     */
    private void generateMatrices() {
        delayMatrix = new DelayMatrix(getTopologicalGraph(), false);
        bwMatrix = createBwMatrix(getTopologicalGraph(), false);
        networkEnabled = true;
    }

    /**
     * Creates the matrix containing the available bandwidth between every pair
     * of nodes.
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

        graph.addLink(new TopologicalLink(entitiesMap.get(src), entitiesMap.get(dest), latency, bandwidth));
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
     * @param briteID ID of the BRITE node that corresponds to the CloudSim
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
     * Un-maps a previously mapped {@link SimEntity} to a BRITE node in the network
     * topology.
     *
     * @param entity {@link SimEntity} being unmapped
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
            return delayMatrix.getDelay(entitiesMap.getOrDefault(src, -1), entitiesMap.getOrDefault(dest, -1));
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    /**
     * Checks if the network simulation is working. If there were some problem
     * during creation of network (e.g., during parsing of BRITE file) that does
     * not allow a proper simulation of the network, this method returns false.
     *
     * @return true if network simulation is working, $false otherwise
     */
    public boolean isNetworkEnabled() {
        return networkEnabled;
    }

    /**
     * Gets the Topological Graph of the network.
     * @return
     */
    public TopologicalGraph getTopologicalGraph() {
        return graph;
    }

    /**
     * Gets a <b>copy</b> of the matrix containing the bandwidth (in Megabits/s)
     * between every pair of {@link SimEntity}s in the network.
     */
    public double[][] getBwMatrix() {
        return Arrays.copyOf(bwMatrix, bwMatrix.length);
    }
}
