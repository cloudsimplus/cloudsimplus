/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.network.topologies;

import org.cloudbus.cloudsim.network.DelayMatrix;
import org.cloudbus.cloudsim.network.topologies.readers.TopologyReaderBrite;
import org.cloudbus.cloudsim.util.ResourceLoader;
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
 * <b>R</b>epresentative <b>I</b>nternet <b>T</b>opology g<b>E</b>nerator
 * <a href="http://www.cs.bu.edu/brite/">(http://www.cs.bu.edu/brite/)</a>,
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
 */
public final class BriteNetworkTopology implements NetworkTopology {
    private static final Logger LOGGER = LoggerFactory.getLogger(BriteNetworkTopology.class.getSimpleName());

    /**
     * The BRITE id to use for the next node to be created in the network.
     */
    private int nextIdx;

    private boolean networkEnabled;

    /**
     * A matrix containing the delay between every pair of nodes in the network.
     */
    private DelayMatrix delayMatrix;

    private double[][] bwMatrix;

    /**
     * The Topological Graph of the network.
     */
    private TopologicalGraph graph;

    /**
     * The entitiesMap between CloudSim entities and BRITE entities.
     * Each key is a CloudSim entity ID and each value the corresponding BRITE entity ID.
     */
    private Map<Long, Integer> entitiesMap;

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
     * Instantiates a Network Topology.
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
        final TopologyReaderBrite instance = new TopologyReaderBrite();
        graph = instance.readGraphFile(reader);
        generateMatrices();
    }

    /**
     * Generates the matrices used internally to set latency and bandwidth
     * between elements.
     */
    private void generateMatrices() {
        // creates the delay matrix
        delayMatrix = new DelayMatrix(getTopologicalGraph(), false);

        // creates the bw matrix
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

        final double[][] mtx = new double[nodes][nodes];

        // cleanup matrix
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                mtx[i][j] = 0.0;
            }
        }

        for (final TopologicalLink edge : graph.getLinksList()) {
            mtx[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkBw();
            if (!directed) {
                mtx[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkBw();
            }
        }

        return mtx;
    }


    @Override
    public void addLink(final long srcId, final long destId, final double bandwidth, final double latency) {
        if (getTopologicalGraph() == null) {
            graph = new TopologicalGraph();
        }

        if (entitiesMap == null) {
            entitiesMap = new HashMap<>();
        }

        // maybe add the nodes
        addNodeMapping(srcId);
        addNodeMapping(destId);

        // generate a new link
        getTopologicalGraph().addLink(new TopologicalLink(entitiesMap.get(srcId), entitiesMap.get(destId), (float) latency, (float) bandwidth));

        generateMatrices();
    }

    private void addNodeMapping(final long cloudSimEntityId) {
        if (entitiesMap.putIfAbsent(cloudSimEntityId, nextIdx) == null) {
            getTopologicalGraph().addNode(new TopologicalNode(nextIdx));
            nextIdx++;
        }
    }

    @Override
    public void mapNode(final long cloudSimEntityID, final int briteID) {
        if (!networkEnabled) {
            return;
        }

        if (entitiesMap.containsKey(cloudSimEntityID)) {
            LOGGER.warn("Network mapping: CloudSim entity {} already mapped.", cloudSimEntityID);
            return;
        }

        if (entitiesMap.containsValue(briteID)) {
            LOGGER.warn("BRITE node {} already in use.", briteID);
            return;
        }

        entitiesMap.put(cloudSimEntityID, briteID);
    }

    @Override
    public void unmapNode(final long cloudSimEntityID) {
        if (!networkEnabled) {
            return;
        }

        entitiesMap.remove(cloudSimEntityID);
    }

    @Override
    public double getDelay(final long srcID, final long destID) {
        if (!networkEnabled) {
            return 0.0;
        }

        try {
            return delayMatrix.getDelay(entitiesMap.getOrDefault(srcID, -1), entitiesMap.getOrDefault(destID, -1));
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    @Override
    public boolean isNetworkEnabled() {
        return networkEnabled;
    }

    @Override
    public TopologicalGraph getTopologicalGraph() {
        return graph;
    }

    /**
     * Gets a<b>copy</b> of the matrix containing the bandwidth between every pair of nodes in the
     * network.
     */
    public double[][] getBwMatrix() {
        return Arrays.copyOf(bwMatrix, bwMatrix.length);
    }
}
