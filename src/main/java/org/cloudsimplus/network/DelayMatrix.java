/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.network;

import org.cloudsimplus.network.topologies.TopologicalGraph;
import org.cloudsimplus.network.topologies.TopologicalLink;
import org.cloudsimplus.util.Util;

/**
 * Represents a matrix containing the delay (in seconds) between every pair or nodes
 * inside a network topology. It stores every distance between connected nodes.
 *
 * @author Thomas Hohnstein
 * @since CloudSim Toolkit 1.0
 */
public class DelayMatrix {
    /**
     * A value to indicate that the delay for a given pair of nodes was not set.
     */
    private static final double DELAY_NOT_SET = Double.MAX_VALUE;

    /**
	 * Matrix holding the delay between any pair of nodes (in seconds).
	 */
    private double[][] mDelayMatrix;

	/**
	 * Number of nodes in the distance-aware-topology.
	 */
    private int mTotalNodeNum;

    /**
     * Creates an empty Delay Matrix with no columns or rows.
     */
	public DelayMatrix() {
        mDelayMatrix = new double[0][0];
	}

	/**
	 * Creates a Delay Matrix for a given network topology graph.
	 *
	 * @param graph the network topological graph
	 * @param directed indicates if a directed matrix should be computed (true) or not (false)
	 */
	public DelayMatrix(final TopologicalGraph graph, final boolean directed) {
		createDelayMatrix(graph, directed);
		calculateShortestPath();
	}

	/**
     * Gets the delay between two nodes.
     *
	 * @param srcID the id of the source node
	 * @param destID the id of the destination node
	 * @return the delay between the given two nodes (in seconds)
	 */
	public double getDelay(final int srcID, final int destID) {
		if (srcID > mTotalNodeNum || destID > mTotalNodeNum) {
			throw new ArrayIndexOutOfBoundsException("srcID or destID is higher than highest stored nodeID!");
		}

		return mDelayMatrix[srcID][destID];
	}

	/**
	 * Creates all internal necessary network-distance structures from the given graph.
     * For similarity, we assume all communication distances are symmetrical,
     * thus leading to an undirected network.
	 *
	 * @param graph the network topological graph
	 * @param directed indicates if a directed matrix should be computed (true) or not (false)
	 */
	private void createDelayMatrix(final TopologicalGraph graph, final boolean directed) {
		mTotalNodeNum = graph.getNumberOfNodes();
		mDelayMatrix = Util.newSquareMatrix(mTotalNodeNum, DELAY_NOT_SET);

        for (final TopologicalLink edge : graph.getLinksList()) {
			mDelayMatrix[edge.getSrcNodeID()][edge.getDestNodeID()] = edge.getLinkDelay();
			if (!directed) {
				// according to symmetry to all communication-paths
				mDelayMatrix[edge.getDestNodeID()][edge.getSrcNodeID()] = edge.getLinkDelay();
			}
		}
	}

	/**
     * Calculates connection delays between every pair or nodes
	 * and the shortest path between them.
	 */
	private void calculateShortestPath() {
		final var floyd = new FloydWarshall(mTotalNodeNum);
		mDelayMatrix = floyd.computeShortestPaths(mDelayMatrix);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder(100);
		builder.append("DelayMatrix: %n".formatted());

		for (int column = 0; column < mTotalNodeNum; ++column) {
			builder.append('\t').append(column);
		}

		for (int row = 0; row < mTotalNodeNum; ++row) {
			builder.append(System.lineSeparator()).append(row);

			for (int col = 0; col < mTotalNodeNum; ++col) {
				if (mDelayMatrix[row][col] == DELAY_NOT_SET)
					builder.append("\t-");
				else builder.append('\t').append(mDelayMatrix[row][col]);
			}
		}

		return builder.toString();
	}
}
