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

import lombok.Getter;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link RandomGenerator} that internally uses the {@link ThreadLocalRandom},
 * a very fast Pseudo-Random Number Generator (PRNG)
 * with higher performance than {@link java.util.Random}, mainly in concurrent environments.
 * The {@link ThreadLocalRandom} also has
 * much better performance than PRNGs available in {@link org.apache.commons.math3},
 * despite it probably has worse statistical properties.
 *
 * <p>This generator has some drawbacks.
 * It only generates the seed internally and doesn't allow setting an explicit seed.
 * Calling the {@code setSeed()} methods will throw an {@link UnsupportedOperationException}
 * and there is no way to get the generated seed.
 * This latter issue makes it impossible to reproduce a
 * simulation experiment to verify the generated results if the seed is unknown.
 * </p>
 *
 * <p>
 * Finally, it doesn't allow applying the
 * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a>
 * to try reducing the variance of experiments using the generated numbers.
 * Classes such as {@link UniformDistr} provide such a feature if the
 * underlying PRNG allows setting a seed.
 * That is explained because the technique is applied when multiple runs of the same simulation
 * are executed.
 * In such a scenario, the second half of experiments have to use the seeds from the first half.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.9
 */
public final class JDKThreadLocalRandomGenerator implements RandomGenerator {
    @Getter
    private static final JDKThreadLocalRandomGenerator instance = new JDKThreadLocalRandomGenerator();

    /**
     * A private constructor to avoid class instantiation.
     * @see #getInstance()
     */
    private JDKThreadLocalRandomGenerator(){/**/}

    @Override
    public void setSeed(final int seed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSeed(final int[] seed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSeed(final long seed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        ThreadLocalRandom.current().nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    @Override
    public int nextInt(final int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    @Override
    public long nextLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    @Override
    public float nextFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    @Override
    public double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public double nextGaussian() {
        return ThreadLocalRandom.current().nextGaussian();
    }
}
