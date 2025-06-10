/*
 * @(#)FloydWarshall.java	ver 1.2  6/20/2005
 *
 * Modified by Weishuai Yang (wyang@cs.binghamton.edu).
 * Originally written by Rahul Simha
 *
 */
package org.cloudsimplus.network;

import lombok.Getter;
import org.cloudsimplus.util.MathUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/// An implementation of the [Floyd-Warshall algorithm](https://en.wikipedia.org/wiki/Floyd-Warshall_algorithm)
/// to calculate the predecessor matrix and the delay between all pairs of nodes in a network.
/// The delay represents the distance between the two vertices,
/// working as the weight for the Floyd-Warshall algorithm.
///
/// @author Rahul Simha
/// @author Weishuai Yang
/// @since CloudSim Toolkit 1.0
public class FloydWarshall {

    /**
     * Number of vertices (network nodes).
     */
    @Getter
    private final int numVertices;

    /**
     * List with the indexes of all vertices, from 0 to {@link #numVertices} - 1.
     */
    private final List<Integer> vertices;

    /**
     * Weights when k is equal to -1 (used for dynamic programming).
     */
    private final double[][] dkMinusOne;

    /**
     * The predecessor matrix (used for dynamic programming).
     */
    private final int[][] pk;

    /**
     * Used for dynamic programming.
     */
    private final int[][] pkMinusOne;

    /**
     * Creates a matrix of network nodes.
     *
     * @param numVertices number of network nodes
     */
    public FloydWarshall(final int numVertices) {
        this.numVertices = MathUtil.nonNegative(numVertices, "Number of vertices");
        this.vertices = IntStream.range(0, numVertices).boxed().collect(toList());
        this.dkMinusOne = new double[numVertices][numVertices];
        this.pk = new int[numVertices][numVertices];
        this.pkMinusOne = new int[numVertices][numVertices];
    }

    /**
     * Computes the shortest path between a vertex to all the other ones,
     * for all existing vertices.
     * This is represented by the delay between every pair of vertices.
     *
     * @param originalDelayMatrix original delay matrix
     * @return the new delay matrix (dk)
     */
    public double[][] computeShortestPaths(final double[][] originalDelayMatrix) {
        savePreviousDelays(originalDelayMatrix);
        return computeShortestPaths();
    }

    /**
     * Computes the shortest path between a vertex to all the other ones,
     * for all existing vertices.
     * This is represented by the delay between every pair of vertices.
     *
     * @return the new delay matrix (dk)
     */
    private double[][] computeShortestPaths() {
        final double[][] dk = new double[numVertices][numVertices];

        for(final int k: vertices) {
            computeShortestPathForSpecificNumberOfHops(dk, k);
        }

        return dk;
    }

    /**
     * Computes the shortest path between a vertex to all the other ones,
     * for all existing vertices, limited to a maximum number of hops.
     * This is represented by the delay between every pair of vertices.
     *
     * @param dk the delay matrix to be updated
     * @param k maximum number of hops to try finding the shortest path between each vertex i and j
     */
    private void computeShortestPathForSpecificNumberOfHops(final double[][] dk, final int k) {
        for(final int i: vertices) {
            computeShortestPathFromVertexToAllVertices(dk, k, i);
        }

        updateMatrices((i,j) -> {
            dkMinusOne[i][j] = dk[i][j];
            pkMinusOne[i][j] = pk[i][j];
        });
    }

    /**
     * Iterates over the path from one vertex to all the other ones, for all existing vertices,
     * updating some matrices as defined by a {@link BiConsumer}.
     *
     * @param updater a {@link BiConsumer} that updates all elements of any matrices, where the parameters
     *                that this BiConsumer receives are the index i and j representing the path
     *                between two vertices, for all existing vertices
     */
    private void updateMatrices(final BiConsumer<Integer, Integer> updater){
        for(final int i: vertices) {
            for(final int j: vertices) {
                updater.accept(i, j);
            }
        }
    }

    /**
     * Computes the shortest path between only a specific vertex to all the other ones,
     * limited to a maximum number of hops, and then updating the delay matrix.
     * This is represented by the delay between every pair of vertices.
     *
     * @param dk the delay matrix to be updated
     * @param k maximum number of hops to try finding the shortest path between each vertex i and j
     * @param i the index of the vertex to compute its distance to all the other vertices
     */
    private void computeShortestPathFromVertexToAllVertices(final double[][] dk, final int k, final int i) {
        for(final int j: vertices) {
            pk[i][j] = -1;
            if (i != j) {
                if (dkMinusOne[i][j] <= dkMinusOne[i][k] + dkMinusOne[k][j]) {
                    dk[i][j] = dkMinusOne[i][j];
                    pk[i][j] = pkMinusOne[i][j];
                } else {
                    dk[i][j] = dkMinusOne[i][k] + dkMinusOne[k][j];
                    pk[i][j] = pkMinusOne[k][j];
                }
            }
        }
    }

    /**
     * Saves the delay matrix before updating.
     *
     * @param originalDelayMatrix the original delay matrix
     */
    private void savePreviousDelays(final double[][] originalDelayMatrix) {
        for(final int i: vertices) {
            for(final int j: vertices) {
                dkMinusOne[i][j] = Double.MAX_VALUE;
                pkMinusOne[i][j] = -1;
                if (originalDelayMatrix[i][j] != 0) {
                    dkMinusOne[i][j] = originalDelayMatrix[i][j];
                    pkMinusOne[i][j] = i;
                }
                // NOTE: we have set the value to infinity and will exploit this to avoid a comparison.
            }
        }
    }

    /**
     * @return a <b>copy</b> of the predecessor matrix.
     */
    public int[][] getPk() {
        return Arrays.copyOf(pk, pk.length);
    }
}
