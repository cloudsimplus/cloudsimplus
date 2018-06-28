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
 * format</a>, the <a href="http://www.cs.bu.edu/brite/">Boston university
 * Representative Topology gEnerator</a>, and generates a topological network
 * from it. Information of this network is used to simulate latency in network
 * traffic of CloudSim.
 * <p/>
 * The topology file may contain more nodes than the number of entities in the
 * simulation. It allows users to increase the scale of the simulation without
 * changing the topology file. Nevertheless, each CloudSim entity must be mapped
 * to one (and only one) BRITE node to allow proper work of the network
 * simulation. Each BRITE node can be mapped to only one entity at a time.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public final class BriteNetworkTopology implements NetworkTopology {
    private static final Logger logger = LoggerFactory.getLogger(BriteNetworkTopology.class.getSimpleName());

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
     * The map between CloudSim entities and BRITE entities. Each key is a
     * CloudSim entity ID and each value the corresponding BRITE entity ID.
     */
    private Map<Integer, Integer> map;

    /**
     * Creates a network topology
     *
     */
    public BriteNetworkTopology() {
        map = new HashMap<>();
        bwMatrix = new double[0][0];
        graph = new TopologicalGraph();
        delayMatrix = new DelayMatrix();
    }

    /**
     * Creates a network topology if the file exists and can be successfully
     * parsed. File is written in the BRITE format and contains
     * topological information on simulation entities.
     *
     * @param filePath the path of the BRITE file
     * @pre fileName != null
     * @post $none
     */
    public BriteNetworkTopology(final String filePath) {
        this(ResourceLoader.getFileReader(filePath));
        logger.info("Topology file: {}", filePath);
    }

    private BriteNetworkTopology(final InputStreamReader streamReader) {
        this();
        // try to find the file
        final TopologyReaderBrite reader = new TopologyReaderBrite();
        graph = reader.readGraphFile(streamReader);
        generateMatrices();
    }

    /**
     * Instantiates a new Network Topology a file inside the <b>application's resource directory</b>.
     * @param fileName the <b>relative name</b> of the BRITE file
     * @return the BriteNetworkTopology instance.
     */
    public static BriteNetworkTopology getInstance(final String fileName){
        final InputStreamReader reader = new InputStreamReader(ResourceLoader.getInputStream(BriteNetworkTopology.class, fileName));
        return new BriteNetworkTopology(reader);
    }

    /**
     * Generates the matrices used internally to set latency and bandwidth
     * between elements.
     */
    private void generateMatrices() {
        // creates the delay matrix
        delayMatrix = new DelayMatrix(getTopologycalGraph(), false);

        // creates the bw matrix
        bwMatrix = createBwMatrix(getTopologycalGraph(), false);

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
    public void addLink(final int srcId, final int destId, final double bw, final double lat) {
        if (getTopologycalGraph() == null) {
            graph = new TopologicalGraph();
        }

        if (map == null) {
            map = new HashMap<>();
        }

        // maybe add the nodes
        if (!map.containsKey(srcId)) {
            getTopologycalGraph().addNode(new TopologicalNode(nextIdx));
            map.put(srcId, nextIdx);
            nextIdx++;
        }

        if (!map.containsKey(destId)) {
            getTopologycalGraph().addNode(new TopologicalNode(nextIdx));
            map.put(destId, nextIdx);
            nextIdx++;
        }

        // generate a new link
        getTopologycalGraph().addLink(new TopologicalLink(map.get(srcId), map.get(destId), (float) lat, (float) bw));

        generateMatrices();
    }

    @Override
    public void mapNode(final int cloudSimEntityID, final int briteID) {
        if (!networkEnabled) {
            return;
        }

        if (map.containsKey(cloudSimEntityID)) {
            logger.warn("Network mapping: CloudSim entity {} already mapped.", cloudSimEntityID);
            return;
        }

        if (map.containsValue(briteID)) {
            logger.warn("BRITE node {} already in use.", briteID);
            return;
        }

        map.put(cloudSimEntityID, briteID);
    }

    @Override
    public void unmapNode(final int cloudSimEntityID) {
        if (!networkEnabled) {
            return;
        }

        map.remove(cloudSimEntityID);
    }

    @Override
    public double getDelay(final int srcID, final int destID) {
        if (!networkEnabled) {
            return 0.0;
        }

        try {
            return delayMatrix.getDelay(map.getOrDefault(srcID, -1), map.getOrDefault(destID, -1));
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    @Override
    public boolean isNetworkEnabled() {
        return networkEnabled;
    }

    @Override
    public TopologicalGraph getTopologycalGraph() {
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
