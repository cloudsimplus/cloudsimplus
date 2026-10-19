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

import org.apache.commons.rng.UniformRandomProvider;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link UniformRandomProvider} that internally uses the {@link ThreadLocalRandom},
 * a very fast Pseudo-Random Number Generator (PRNG)
 * with higher performance than {@link java.util.Random}, mainly in concurrent environments.
 * The {@link ThreadLocalRandom} may also be faster than some PRNGs available in
 * <a href="https://commons.apache.org/proper/commons-rng/">Apache Commons RNG</a>,
 * despite it probably has worse statistical properties.
 *
 * <p>This generator has some drawbacks.
 * It only generates the seed internally and doesn't allow setting an explicit seed.
 * There is also no way to get the generated seed.
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
 * <p>The {@link UniformRandomProvider} interface just requires implementing {@link #nextLong()}.
 * The other methods are overridden to directly call the equivalent {@link ThreadLocalRandom} ones,
 * which is faster.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.9
 */
public final class JDKThreadLocalRandomGenerator implements UniformRandomProvider {
    private static final JDKThreadLocalRandomGenerator INSTANCE = new JDKThreadLocalRandomGenerator();

    /**
     * A private constructor to avoid class instantiation.
     * @see #getInstance()
     */
    private JDKThreadLocalRandomGenerator(){/**/}

    /**
     * {@return the single instance of this generator}
     * Since it just calls the {@link ThreadLocalRandom} of the current thread,
     * a single instance can be shared, even by different threads.
     */
    public static JDKThreadLocalRandomGenerator getInstance() {
        return INSTANCE;
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
}
