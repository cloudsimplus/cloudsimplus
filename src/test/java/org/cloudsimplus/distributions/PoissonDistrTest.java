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
package org.cloudsimplus.distributions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoissonDistrTest {
    private static final double DELTA = 1e-15;

    /// The expected value was computed with the previous implementation of the
    /// Poisson Probability Mass Function: `λ^k · e^(-λ) / k!`.
    @Test
    void eventsArrivalProbability() {
        final var poisson = new PoissonDistr(0.4, 1);
        poisson.setK(3);
        assertEquals(0.0071500805, poisson.eventsArrivalProbability(), 1e-10);
    }

    @Test
    void eventsArrivalProbabilityForOneEvent() {
        final double lambda = 0.4;
        final var poisson = new PoissonDistr(lambda, 1);
        assertEquals(lambda * Math.exp(-lambda), poisson.eventsArrivalProbability(), DELTA);
    }

    /// The previous implementation failed for k > 20, since the factorial overflowed a long.
    /// The expected value was computed with Python's arbitrary-precision integers:
    /// `math.exp(-20) * 20**25 / math.factorial(25)`.
    @Test
    void eventsArrivalProbabilityForManyEvents() {
        final var poisson = new PoissonDistr(20, 1);
        poisson.setK(25);
        assertEquals(0.04458764910826788, poisson.eventsArrivalProbability(), 1e-12);
    }

    @Test
    void invalidLambdaIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PoissonDistr(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new PoissonDistr(-1, 1));
    }

    @Test
    void negativeSeedIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PoissonDistr(1, -1));
    }
}
