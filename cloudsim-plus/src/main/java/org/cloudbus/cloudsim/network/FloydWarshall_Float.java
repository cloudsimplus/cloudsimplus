/*
 * @(#)FloydWarshall.java	ver 1.2  6/20/2005
 *
 * Modified by Weishuai Yang (wyang@cs.binghamton.edu).
 * Originally written by Rahul Simha
 *
 */
package org.cloudbus.cloudsim.network;

/**
 * FloydWarshall algorithm to calculate the predecessor matrix and the delay
 * between all pairs of nodes.
 *
 * @author Rahul Simha
 * @author Weishuai Yang
 * @version 1.2, 6/20/2005
 * @since CloudSim Toolkit 1.0
 */
public class FloydWarshall_Float {

    /**
     * Number of vertices (network nodes).
     */
    private int numVertices;

    /**
     * Matrices used in dynamic programming.
     */
    private float[][] dk, dk_minus_one;

    /**
     * The predecessor matrix. Matrix used by dynamic programming.
     */
    private int[][] pk;

    /**
     * Matrix used by dynamic programming.
     */
    private int[][] pk_minus_one;

    /**
     * Creates a matrix of network nodes.
     *
     * @param numVertices number of network nodes
     */
    public FloydWarshall_Float(int numVertices) {
        this.numVertices = numVertices;

        // Initialize dk matrices.
        dk = new float[numVertices][];
        dk_minus_one = new float[numVertices][];
        for (int i = 0; i < numVertices; i++) {
            dk[i] = new float[numVertices];
            dk_minus_one[i] = new float[numVertices];
        }

        // Initialize pk matrices.
        pk = new int[numVertices][];
        pk_minus_one = new int[numVertices][];
        for (int i = 0; i < numVertices; i++) {
            pk[i] = new int[numVertices];
            pk_minus_one[i] = new int[numVertices];
        }
    }

    /**
     * Calculates the delay between all pairs of nodes.
     *
     * @param adjMatrix original delay matrix
     * @return the delay matrix
     */
    public float[][] allPairsShortestPaths(float[][] adjMatrix) {
        // dk_minus_one = weights when k = -1
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (adjMatrix[i][j] != 0) {
                    dk_minus_one[i][j] = adjMatrix[i][j];
                    pk_minus_one[i][j] = i;
                } else {
                    dk_minus_one[i][j] = Float.MAX_VALUE;
                    pk_minus_one[i][j] = -1;
                }
                // NOTE: we have set the value to infinity and will exploit
                // this to avoid a comparison.
            }
        }

        for (int k = 0; k < numVertices; k++) {
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (i != j) {
                        // D_k[i][j] = min ( D_k-1[i][j], D_k-1[i][k] + D_k-1[k][j].
                        if (dk_minus_one[i][j] <= dk_minus_one[i][k] + dk_minus_one[k][j]) {
                            dk[i][j] = dk_minus_one[i][j];
                            pk[i][j] = pk_minus_one[i][j];
                        } else {
                            dk[i][j] = dk_minus_one[i][k] + dk_minus_one[k][j];
                            pk[i][j] = pk_minus_one[k][j];
                        }
                    } else {
                        pk[i][j] = -1;
                    }
                }
            }

            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    dk_minus_one[i][j] = dk[i][j];
                    pk_minus_one[i][j] = pk[i][j];
                }
            }

        }

        return dk;

    }

    /**
     * Gets predecessor matrix.
     *
     * @return predecessor matrix
     */
    public int[][] getPk() {
        return pk;
    }
}
