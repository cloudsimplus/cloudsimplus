/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implements a model, according to which a Cloudlet generates
 * random resource utilization every time frame.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public class UtilizationModelStochastic extends UtilizationModelAbstract {

    /**
     * The random generator.
     */
    private ContinuousDistribution randomGenerator;

    /**
     * @see #getHistory()
     */
    private Map<Double, Double> history;

    /**
     * Instantiates a new utilization model stochastic
     * that defines the resource utilization in percentage.
     */
    public UtilizationModelStochastic() {
        super();
        setHistory(new HashMap<>());
        setRandomGenerator(new UniformDistr());
    }

    /**
     * Instantiates a new utilization model stochastic
     * where the resource utilization is defined in the given unit.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     */
    public UtilizationModelStochastic(final Unit unit) {
        this();
        setUnit(unit);
    }

    /**
     * Instantiates a new utilization model stochastic using
     * a given seed and where the resource utilization is defined in the given unit.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param seed the seed to generate the pseudo random utilization values
     */
    public UtilizationModelStochastic(final Unit unit, final long seed) {
        this(seed);
        setUnit(unit);
    }

    /**
     * Instantiates a new utilization model stochastic with a specific seed.
     *
     * @param seed the seed to generate the pseudo random utilization values
     */
    public UtilizationModelStochastic(final long seed) {
        this(new UniformDistr(seed));
    }

    /**
     * Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG).
     *
     * @param prng the Pseudo Random Number Generator (PRNG) to generate utilization values
     */
    public UtilizationModelStochastic(final ContinuousDistribution prng) {
        super();
        setHistory(new HashMap<>());
        setRandomGenerator(prng);
    }

    @Override
    public double getUtilization(final double time) {
        Double utilization = getHistory().get(time);
        if (utilization != null) {
            return utilization;
        }

        utilization = Math.abs(randomGenerator.sample());
        getHistory().put(time, utilization);
        return utilization;
    }

    /**
     * Gets the utilization history map, where each key is a time and
     * each value is the resource utilization in that time.
     *
     * @return the utilization history
     */
    protected Map<Double, Double> getHistory() {
        return history;
    }

    /**
     * Sets the utilization history map, where each key is a time and
     * each value is the resource utilization in that time.
     *
     *
     * @param history the history to set
     */
    protected final void setHistory(final Map<Double, Double> history) {
        this.history = history;
    }

    /**
     * Save the utilization history to a file.
     *
     * @param filename the filename
     * @throws UncheckedIOException when the file cannot be accessed
     */
    public void saveHistory(final String filename) {
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(getHistory());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Load an utilization history from a file.
     *
     * @param filename the filename
     * @throws UncheckedIOException when the file cannot be accessed
     */
    @SuppressWarnings("unchecked")
    public void loadHistory(final String filename) {
        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            setHistory((Map<Double, Double>) ois.readObject());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the random number generator.
     *
     * @return the random number generator
     */
    public ContinuousDistribution getRandomGenerator() {
        return randomGenerator;
    }

    /**
     * Sets the random number generator.
     *
     * @param randomGenerator the new random number generator
     */
    public final void setRandomGenerator(final ContinuousDistribution randomGenerator) {
        this.randomGenerator = Objects.requireNonNull(randomGenerator);
    }

}
