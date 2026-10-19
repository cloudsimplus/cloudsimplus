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

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/// Tests all [ContinuousDistribution] implementations based on [ContinuousDistributionAbstract].
/// The expected mean and variance were checked against the Apache Commons Math 3 distributions
/// (which CloudSim Plus used before) with the same parameters,
/// to make sure the order of the distribution parameters didn't change.
///
/// The `@TestFactory` methods create a test for each distribution.
/// The assertions are inside the lambda expressions that those tests run,
/// which PMD doesn't recognize.
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class ContinuousDistributionTest {
    private static final long SEED = 12345;
    private static final double DELTA = 1e-6;
    private static final int SAMPLES = 20_000;

    /// A distribution to test, created from a seed, and its expected mean and variance.
    private record Case(String name, LongFunction<ContinuousDistribution> factory, double mean, double variance) {
        ContinuousDistribution create(final long seed) {
            return factory.apply(seed);
        }
    }

    private static final double LOG_NORMAL_SCALE = 0.4;
    private static final double LOG_NORMAL_SHAPE = 0.7;
    private static final double LOG_NORMAL_SHAPE_SQUARED = LOG_NORMAL_SHAPE * LOG_NORMAL_SHAPE;

    private static final List<Case> CASES = List.of(
        new Case("Uniform", seed -> new UniformDistr(1, 4, seed), 2.5, 0.75),
        new Case("Normal", seed -> new NormalDistr(2, 0.5, seed), 2, 0.25),
        new Case("Exponential", seed -> new ExponentialDistr(2, seed), 2, 4),
        new Case("Gamma", seed -> new GammaDistr(3, 1.5, seed), 4.5, 6.75),
        new Case("LogNormal", seed -> new LogNormalDistr(LOG_NORMAL_SHAPE, LOG_NORMAL_SCALE, seed),
            Math.exp(LOG_NORMAL_SCALE + LOG_NORMAL_SHAPE_SQUARED / 2),
            (Math.exp(LOG_NORMAL_SHAPE_SQUARED) - 1) * Math.exp(2 * LOG_NORMAL_SCALE + LOG_NORMAL_SHAPE_SQUARED)),
        new Case("Pareto", seed -> new ParetoDistr(3, 2, seed), 3, 3),
        new Case("Weibull", seed -> new WeibullDistr(1.5, 3, seed), 2.708236, 3.381213)
    );

    private static Stream<DynamicTest> forEachCase(final String testName, final CaseTest test) {
        return CASES.stream().map(c -> dynamicTest(c.name() + ": " + testName, () -> test.run(c)));
    }

    @FunctionalInterface
    private interface CaseTest {
        void run(Case c);
    }

    @TestFactory
    Stream<DynamicTest> meanAndVariance() {
        return forEachCase("mean and variance", c -> {
            final var distribution = c.create(SEED);
            assertEquals(c.mean(), distribution.getMean(), DELTA, "mean");
            assertEquals(c.variance(), distribution.getVariance(), DELTA, "variance");
        });
    }

    @TestFactory
    Stream<DynamicTest> sameSeedGeneratesSameNumbers() {
        return forEachCase("same seed", c -> assertArrayEquals(samples(c.create(SEED), 10), samples(c.create(SEED), 10)));
    }

    @TestFactory
    Stream<DynamicTest> differentSeedsGenerateDifferentNumbers() {
        return forEachCase("different seeds", c -> assertFalse(
            java.util.Arrays.equals(samples(c.create(SEED), 10), samples(c.create(SEED + 1), 10))));
    }

    @TestFactory
    Stream<DynamicTest> samplesFollowTheDistribution() {
        return forEachCase("samples follow the distribution", c -> {
            final var distribution = c.create(SEED);
            final double[] samples = samples(distribution, SAMPLES);

            assertTrue(DoubleStream.of(samples).allMatch(x -> x >= distribution.getSupportLowerBound() && x <= distribution.getSupportUpperBound()),
                "All samples must be inside the distribution support");

            // The sample mean must be close to the distribution mean (5 standard errors is a very safe margin)
            final double sampleMean = DoubleStream.of(samples).average().orElseThrow();
            final double standardError = Math.sqrt(distribution.getVariance() / SAMPLES);
            assertEquals(distribution.getMean(), sampleMean, 5 * standardError, "sample mean");
        });
    }

    @TestFactory
    Stream<DynamicTest> antitheticVariates() {
        return forEachCase("antithetic variates", c -> {
            final var original = c.create(SEED);
            final var antithetic = c.create(SEED).setApplyAntitheticVariates(true);
            assertTrue(antithetic.isApplyAntitheticVariates());
            for (int i = 0; i < 10; i++) {
                assertEquals(1 - original.sample(), antithetic.sample(), 1e-12);
            }
        });
    }

    @TestFactory
    Stream<DynamicTest> getSeed() {
        return forEachCase("seed", c -> assertEquals(SEED, c.create(SEED).getSeed()));
    }

    @TestFactory
    Stream<DynamicTest> negativeSeedIsRejected() {
        return forEachCase("negative seed", c -> assertThrows(IllegalArgumentException.class, () -> c.create(-1)));
    }

    @Test
    void distributionPropertiesAreDelegated() {
        final var uniform = new UniformDistr(1, 4, SEED);
        assertEquals(1, uniform.getSupportLowerBound());
        assertEquals(4, uniform.getSupportUpperBound());
        assertEquals(1.0 / 3.0, uniform.density(2), DELTA);
        assertEquals(0.5, uniform.cumulativeProbability(2.5), DELTA);
        assertEquals(2.5, uniform.inverseCumulativeProbability(0.5), DELTA);
    }

    @Test
    void uniformDefaultRangeIsFromZeroToOne() {
        assertTrue(DoubleStream.of(samples(new UniformDistr(SEED), 1000)).allMatch(x -> x >= 0 && x < 1));
    }

    @Test
    void acceptsAnotherRandomGenerator() {
        final var uniform = new UniformDistr(0, JDKThreadLocalRandomGenerator.getInstance());
        assertTrue(DoubleStream.of(samples(uniform, 1000)).allMatch(x -> x >= 0 && x < 1));
    }

    @Test
    void lomaxSamplesAreShifted() {
        final double location = 2;
        final double shift = 1.5;
        final var lomax = new LomaxDistr(3, location, shift, SEED);
        final var pareto = new ParetoDistr(3, location, SEED);
        for (int i = 0; i < 10; i++) {
            assertEquals(pareto.sample() - shift, lomax.sample(), 1e-12);
        }
    }

    @Test
    void lomaxRejectsShiftGreaterThanLocation() {
        assertThrows(IllegalArgumentException.class, () -> new LomaxDistr(3, 2, 2.5, SEED));
    }

    @Test
    void invalidParametersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new UniformDistr(4, 1, SEED));
        assertThrows(IllegalArgumentException.class, () -> new NormalDistr(0, -1, SEED));
        assertThrows(IllegalArgumentException.class, () -> new ExponentialDistr(0, SEED));
    }

    @Test
    void nullDistribution() {
        assertEquals(0, ContinuousDistribution.NULL.sample());
        assertEquals(0, ContinuousDistribution.NULL.getMean());
    }

    private static double[] samples(final ContinuousDistribution distribution, final int count) {
        return DoubleStream.generate(distribution::sample).limit(count).toArray();
    }
}
