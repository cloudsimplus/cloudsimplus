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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Checks [CriticalityIndex] against values computed with NetworkX 3.6
/// and the Python reference implementation of the Centrality-Entropy Index.
///
/// @author Prawal Pokharel
public class CriticalityIndexTest {
    private static final double DELTA = 1e-6;

    /// gw -> orders, gw -> search, orders -> db, search -> db, orders -> pay, pay -> db, search -> cache.
    /// Indexes: gw 0, orders 1, search 2, db 3, pay 4, cache 5.
    static final List<List<Integer>> SHOP = List.of(
        List.of(1, 2), List.of(3, 4), List.of(3, 5), List.of(), List.of(3), List.of());

    @Test
    public void pageRankMatchesNetworkX() {
        final double[] expected = {0.09400896511154269, 0.1339625832024351, 0.1339625832024351,
                                   0.33617939529530844, 0.15094323659413933, 0.15094323659413933};
        assertArrayEquals(expected, CriticalityIndex.pageRank(SHOP));
    }

    @Test
    public void betweennessMatchesNetworkX() {
        final double[] expected = {0, 0.075, 0.075, 0, 0, 0};
        assertArrayEquals(expected, CriticalityIndex.betweenness(SHOP));
    }

    @Test
    public void closenessMatchesNetworkX() {
        final double[] expected = {0, 0.2, 0.2, 0.64, 0.26666666666666666, 0.26666666666666666};
        assertArrayEquals(expected, CriticalityIndex.closeness(SHOP));
    }

    @Test
    public void compositeCentralityMatchesReference() {
        final double[] expected = {0.3383749753642664, 0.6575108516102058, 0.6575108516102058,
                                   1.0, 0.5179784466253304, 0.39809693332442586};
        assertArrayEquals(expected, CriticalityIndex.centrality(SHOP));
    }

    @Test
    public void entropyOfEvenlySpreadDemandIsOne() {
        final double[] history = {0.05, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95};
        assertEquals(1.0, CriticalityIndex.entropy(history), DELTA);
    }

    @Test
    public void entropyOfConstantDemandIsZero() {
        assertEquals(0.0, CriticalityIndex.entropy(new double[]{0.5, 0.5, 0.5}), DELTA);
        assertEquals(0.0, CriticalityIndex.entropy(new double[0]), DELTA);
    }

    @Test
    public void entropyUsesTheSameBinEdgesAsNumpy() {
        // 0.30000000000000004 is above the 0.3 edge, so it falls in the same bin as 0.3
        final double[] history = {0.0, 1.0, 0.3, 0.30000000000000004, 0.7, 0.2};
        assertEquals(0.6778079184956498, CriticalityIndex.entropy(history), DELTA);
    }

    @Test
    public void emptyGraphHasNoCentrality() {
        assertEquals(0, CriticalityIndex.centrality(List.of()).length);
    }

    private static void assertArrayEquals(final double[] expected, final double[] actual) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], DELTA, "index " + i);
        }
    }
}
