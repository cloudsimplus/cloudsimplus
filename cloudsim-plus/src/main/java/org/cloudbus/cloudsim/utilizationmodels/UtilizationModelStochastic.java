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
    public UtilizationModelStochastic(Unit unit) {
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
    public UtilizationModelStochastic(Unit unit, long seed) {
        this(seed);
        setUnit(unit);
    }

    /**
     * Instantiates a new utilization model stochastic.
     *
     * @param seed the seed to generate the pseudo random utilization values
     */
    public UtilizationModelStochastic(long seed) {
        super();
        setHistory(new HashMap<>());
        setRandomGenerator(new UniformDistr(seed));
    }

    @Override
    public double getUtilization(double time) {
        if (getHistory().containsKey(time)) {
            return getHistory().get(time);
        }

        final double utilization = getRandomGenerator().sample();
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
    protected final void setHistory(Map<Double, Double> history) {
        this.history = history;
    }

    /**
     * Save the utilization history to a file.
     *
     * @param filename the filename
     * @throws IOException when the file cannot be accessed
     */
    public void saveHistory(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(getHistory());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Load an utilization history from a file.
     *
     * @param filename the filename
     * @throws IOException when the file cannot be accessed
     */
    @SuppressWarnings("unchecked")
    public void loadHistory(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
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
    public final void setRandomGenerator(ContinuousDistribution randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

}
