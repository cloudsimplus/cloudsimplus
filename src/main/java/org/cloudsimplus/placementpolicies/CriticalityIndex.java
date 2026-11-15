/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.placementpolicies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Computes how critical each service of a dependency graph is, as a value
/// combining the service position in the graph, how variable its demand is,
/// and a criticality declared by the operator.
///
/// The score of a service `i` is `alpha * C(i) + beta * H(i) + gamma * R(i)`, where:
///
/// - `C(i)` is a composite centrality in \[0, 1\]:
///   `0.35 PageRank + 0.30 betweenness + 0.20 degree + 0.15 closeness`,
///   divided by the largest value in the graph;
/// - `H(i)` is the Shannon entropy of the service demand history
///   (utilization values in \[0, 1\] over 10 bins), divided by `log2(10)`;
/// - `R(i)` is the criticality in \[0, 1\] declared for the service.
///
/// This is the Centrality-Entropy Index (CEI) of P. Pokharel, IEMCON 2026.
/// [VmDependencyGraph] uses it only after [VmDependencyGraph#useCriticalityIndex()];
/// by default every service weighs 1.
/// The centrality measures follow the definitions used by NetworkX 3
/// (PageRank with damping 0.85, normalized betweenness, in-distance closeness
/// with the Wasserman-Faust correction), so results can be checked against it.
///
/// The graph is given as adjacency lists, where `callees.get(i)` has the indexes
/// of the services that service `i` depends on.
///
/// @author Prawal Pokharel
/// @since CloudSim Plus 9.0.0
public final class CriticalityIndex {
    /// Default weight of the centrality term.
    public static final double DEF_ALPHA = 0.40;

    /// Default weight of the demand entropy term.
    public static final double DEF_BETA = 0.35;

    /// Default weight of the declared criticality term.
    public static final double DEF_GAMMA = 0.25;

    private static final int ENTROPY_BINS = 10;
    private static final double DAMPING = 0.85;
    private static final int MAX_PAGERANK_ITERATIONS = 100;
    private static final double PAGERANK_TOLERANCE = 1e-6;

    private CriticalityIndex() {/**/}

    /// {@return the composite centrality of each service, in \[0, 1\]}
    /// @param callees for each service, the indexes of the services it depends on
    public static double[] centrality(final List<? extends List<Integer>> callees) {
        final int n = callees.size();
        if (n == 0) {
            return new double[0];
        }

        final double[] pageRank = pageRank(callees);
        final double[] betweenness = betweenness(callees);
        final double[] degree = degree(callees);
        final double[] closeness = closeness(callees);
        final var composite = new double[n];
        for (int i = 0; i < n; i++) {
            composite[i] = 0.35 * pageRank[i] + 0.30 * betweenness[i] + 0.20 * degree[i] + 0.15 * closeness[i];
        }

        final double max = Arrays.stream(composite).max().orElse(0);
        if (max > 0) {
            for (int i = 0; i < n; i++) {
                composite[i] /= max;
            }
        }

        return composite;
    }

    /// {@return the Shannon entropy of a demand history, divided by `log2(10)`, so it is in \[0, 1\]}
    /// Values are clipped to \[0, 1\] and counted in 10 equal bins, as `numpy.histogram` does.
    /// @param history utilization values, usually in \[0, 1\]
    public static double entropy(final double[] history) {
        if (history.length == 0) {
            return 0;
        }

        final var counts = new int[ENTROPY_BINS];
        for (final double value : history) {
            counts[bin(Math.clamp(value, 0.0, 1.0))]++;
        }

        double sum = 0;
        for (final int count : counts) {
            if (count > 0) {
                final double p = count / (double) history.length;
                sum -= p * Math.log(p) / Math.log(2);
            }
        }

        return Math.min(1.0, sum / (Math.log(ENTROPY_BINS) / Math.log(2)));
    }

    /// {@return the index of the bin a value in \[0, 1\] belongs to}
    /// Uses the same edge rules as `numpy.histogram` for equal-width bins:
    /// each bin includes its left edge, and the last bin also includes 1.0.
    private static int bin(final double value) {
        int idx = (int) (value * ENTROPY_BINS);
        if (idx == ENTROPY_BINS) {
            idx--;
        }

        if (value < edge(idx)) {
            idx--;
        } else if (idx < ENTROPY_BINS - 1 && value >= edge(idx + 1)) {
            idx++;
        }

        return idx;
    }

    private static double edge(final int i) {
        return i == ENTROPY_BINS ? 1.0 : i * (1.0 / ENTROPY_BINS);
    }

    /// {@return the criticality score `alpha * centrality + beta * entropy + gamma * declared`}
    public static double score(
        final double centrality, final double entropy, final double declared,
        final double alpha, final double beta, final double gamma)
    {
        return alpha * centrality + beta * entropy + gamma * declared;
    }

    /// PageRank with uniform teleport and dangling-node redistribution (NetworkX semantics).
    static double[] pageRank(final List<? extends List<Integer>> callees) {
        final int n = callees.size();
        double[] rank = new double[n];
        Arrays.fill(rank, 1.0 / n);
        for (int iteration = 0; iteration < MAX_PAGERANK_ITERATIONS; iteration++) {
            final double[] last = rank;
            rank = new double[n];
            double dangling = 0;
            for (int i = 0; i < n; i++) {
                final var out = callees.get(i);
                if (out.isEmpty()) {
                    dangling += last[i];
                    continue;
                }

                final double share = DAMPING * last[i] / out.size();
                for (final int j : out) {
                    rank[j] += share;
                }
            }

            double error = 0;
            for (int i = 0; i < n; i++) {
                rank[i] += DAMPING * dangling / n + (1 - DAMPING) / n;
                error += Math.abs(rank[i] - last[i]);
            }

            if (error < n * PAGERANK_TOLERANCE) {
                return rank;
            }
        }

        return rank;
    }

    /// Brandes betweenness for an unweighted directed graph,
    /// normalized by `(n-1)(n-2)`.
    static double[] betweenness(final List<? extends List<Integer>> callees) {
        final int n = callees.size();
        final var result = new double[n];
        final var sigma = new double[n];
        final var dist = new int[n];
        final var delta = new double[n];
        final var order = new int[n];
        final var inDegree = new int[n];
        callees.forEach(out -> out.forEach(j -> inDegree[j]++));
        /* The predecessors of a service on shortest paths are some of its callers,
         * so an array as long as its in-degree is enough. */
        final var predecessors = new int[n][];
        for (int i = 0; i < n; i++) {
            predecessors[i] = new int[inDegree[i]];
        }

        final var predCount = new int[n];
        for (int s = 0; s < n; s++) {
            Arrays.fill(sigma, 0);
            Arrays.fill(dist, -1);
            Arrays.fill(delta, 0);
            Arrays.fill(predCount, 0);
            sigma[s] = 1;
            dist[s] = 0;
            int visited = 0;
            final var queue = new ArrayDeque<Integer>();
            queue.add(s);
            while (!queue.isEmpty()) {
                final int v = queue.poll();
                order[visited++] = v;
                for (final int w : callees.get(v)) {
                    if (dist[w] < 0) {
                        dist[w] = dist[v] + 1;
                        queue.add(w);
                    }

                    if (dist[w] == dist[v] + 1) {
                        sigma[w] += sigma[v];
                        predecessors[w][predCount[w]++] = v;
                    }
                }
            }

            for (int k = visited - 1; k >= 0; k--) {
                final int w = order[k];
                for (int p = 0; p < predCount[w]; p++) {
                    final int v = predecessors[w][p];
                    delta[v] += sigma[v] / sigma[w] * (1 + delta[w]);
                }

                if (w != s) {
                    result[w] += delta[w];
                }
            }
        }

        if (n > 2) {
            final double scale = 1.0 / ((n - 1) * (n - 2));
            for (int i = 0; i < n; i++) {
                result[i] *= scale;
            }
        }

        return result;
    }

    /// {@return the in-degree plus out-degree of each service, divided by `n - 1`}
    static double[] degree(final List<? extends List<Integer>> callees) {
        final int n = callees.size();
        final var result = new double[n];
        if (n < 2) {
            return result;
        }

        for (int i = 0; i < n; i++) {
            result[i] += callees.get(i).size();
            for (final int j : callees.get(i)) {
                result[j]++;
            }
        }

        for (int i = 0; i < n; i++) {
            result[i] /= n - 1;
        }

        return result;
    }

    /// Closeness from incoming distances, with the Wasserman-Faust correction
    /// for graphs that are not strongly connected (NetworkX default).
    static double[] closeness(final List<? extends List<Integer>> callees) {
        final int n = callees.size();
        final var result = new double[n];
        final var callers = new ArrayList<List<Integer>>(n);
        for (int i = 0; i < n; i++) {
            callers.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            for (final int j : callees.get(i)) {
                callers.get(j).add(i);
            }
        }

        final var dist = new int[n];
        for (int target = 0; target < n; target++) {
            Arrays.fill(dist, -1);
            dist[target] = 0;
            final var queue = new ArrayDeque<Integer>();
            queue.add(target);
            long total = 0;
            int reached = 0;
            while (!queue.isEmpty()) {
                final int v = queue.poll();
                for (final int u : callers.get(v)) {
                    if (dist[u] < 0) {
                        dist[u] = dist[v] + 1;
                        total += dist[u];
                        reached++;
                        queue.add(u);
                    }
                }
            }

            if (total > 0 && n > 1) {
                result[target] = (reached / (double) total) * (reached / (double) (n - 1));
            }
        }

        return result;
    }
}
