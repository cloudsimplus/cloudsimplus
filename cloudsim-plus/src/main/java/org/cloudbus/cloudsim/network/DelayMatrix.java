/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.network;

import org.cloudbus.cloudsim.network.topologies.TopologicalGraph;
import org.cloudbus.cloudsim.network.topologies.TopologicalLink;

/**
 * This class represents a delay matrix between every pair or nodes
 * inside a network topology, storing every distance between connected nodes.
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class DelayMatrix {

	/**
	 * Matrix holding delay information between any two nodes.
	 */
    private double[][] mDelayMatrix;

	/**
	 * Number of nodes in the distance-aware-topology.
	 */
    private int mTotalNodeNum;

	public DelayMatrix() {
        mDelayMatrix = new double[0][0];
	}

	/**
	 * Creates an correctly initialized double-Delay-Matrix.
	 *
	 * @param graph the network topological graph
	 * @param directed indicates if an directed matrix should be computed (true) or not (false)
	 */
	public DelayMatrix(TopologicalGraph graph, boolean directed) {

		// lets pre-initialize the Delay-Matrix
		createDelayMatrix(graph, directed);

		// now its time to calculate all possible connection-delays
		calculateShortestPath();
	}

	/**
         * Gets the delay between two nodes.
         *
	 * @param srcID the id of the source node
	 * @param destID the id of the destination node
	 * @return the delay between the given two nodes
	 */
	public double getDelay(int srcID, int destID) {
		// check the nodeIDs against internal array-boundaries
		if (srcID > mTotalNodeNum || destID > mTotalNodeNum) {
			throw new ArrayIndexOutOfBoundsException("srcID or destID is higher than highest stored node-ID!");
		}

		return mDelayMatrix[srcID][destID];
	}

	/**
	 * Creates all internal necessary network-distance structures from the given graph.
         * For similarity, we assume all communication-distances are symmetrical,
         * thus leading to an undirected network.
	 *
	 * @param graph the network topological graph
	 * @param directed indicates if an directed matrix should be computed (true) or not (false)
	 */
	private void createDelayMatrix(TopologicalGraph graph, boolean directed) {

		// number of nodes inside the network
		mTotalNodeNum = graph.getNumberOfNodes();

		mDelayMatrix = new double[mTotalNodeNum][mTotalNodeNum];

		// cleanup the complete distance-matrix with "0"s
		for (int row = 0; row < mTotalNodeNum; ++row) {
			for (int col = 0; col < mTotalNodeNum; ++col) {
				mDelayMatrix[row][col] = Double.MAX_VALUE;
			}
		}


        for (final TopologicalLink edge : graph.getLinksList()) {
			mDelayMatrix[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkDelay();
			if (!directed) {
				// according to symmetry to all communication-paths
				mDelayMatrix[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkDelay();
			}
		}
	}

	/**
	 * Calculates the shortest path between all pairs of nodes.
	 */
	private void calculateShortestPath() {
		final FloydWarshall floyd = new FloydWarshall(mTotalNodeNum);
		mDelayMatrix = floyd.computeShortestPaths(mDelayMatrix);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(100);

		builder.append(
		    "just a simple printout of the distance-aware-topology-class\ndelay-matrix is:\n");

		for (int column = 0; column < mTotalNodeNum; ++column) {
			builder.append('\t').append(column);
		}

		for (int row = 0; row < mTotalNodeNum; ++row) {
			builder.append('\n').append(row);

			for (int col = 0; col < mTotalNodeNum; ++col) {
				if (mDelayMatrix[row][col] == Double.MAX_VALUE) {
					builder.append("\t-");
				} else {
					builder.append('\t').append(mDelayMatrix[row][col]);
				}
			}
		}

		return builder.toString();
	}
}
