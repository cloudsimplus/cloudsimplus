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
     * The utilization history map, where each key is a time and
     * each value is the resource utilization in that time.
     */
    private Map<Double, Double> historyMap;

    /**
     * The previous time the utilization was requested.
     * The value is used to improve performance of the {@link #getUtilization(double)} method,
     * which is crucial for large scale simulations.
     *
     * @see #getUtilization()
     * @see #getUtilization(double)
     */
    private double previousTime;

    /**
     * The max value already stored in the {@link #previousTime}.
     * The value is used to improve performance of the {@link #getUtilization(double)} method,
     * which is crucial for large scale simulations.
     */
    private double maxPreviousTime;

    /**
     * The utilization percentage for the {@link #previousTime}.
     * The value is used to improve performance of the {@link #getUtilization(double)} method,
     * which is crucial for large scale simulations.
     */
    private double previousUtilization;

    /** @see #isHistoryEnabled() */
    private boolean historyEnabled;

    /**
     * Instantiates a new utilization model stochastic
     * that defines the resource utilization in percentage.
     * The resource utilization history is disabled by default.
     *
     * @see #setUnit(Unit)
     * @see #enableHistory()
     */
    public UtilizationModelStochastic() {
        this(Unit.PERCENTAGE);
    }

    /**
     * Instantiates a new utilization model stochastic
     * where the resource utilization is defined in the given unit.
     * The resource utilization history is disabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @see #enableHistory()
     */
    public UtilizationModelStochastic(final Unit unit) {
        this(unit, -1);
    }

    /**
     * Instantiates a new utilization model stochastic
     * where the resource utilization is defined in the given unit.
     * The resource utilization history is disabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param seed the seed to initialize the random number generator.
     *             If -1 is passed, the current time will be used.
     * @see #enableHistory()
     */
    public UtilizationModelStochastic(final Unit unit, final long seed) {
        this(unit, new UniformDistr(seed));
    }

    /**
     * Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG)
     * It defines the resource utilization in percentage.
     * The resource utilization history is disabled by default.
     *
     * @param prng the Pseudo Random Number Generator (PRNG) to generate utilization values
     * @see #setUnit(Unit)
     * @see #enableHistory()
     */
    public UtilizationModelStochastic(final ContinuousDistribution prng) {
        this(Unit.PERCENTAGE, prng);
    }

    /**
     * Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG).
     * The resource utilization history is disabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param prng the Pseudo Random Number Generator (PRNG) to generate utilization values
     * @see #enableHistory()
     */
    public UtilizationModelStochastic(final Unit unit, final ContinuousDistribution prng) {
        super(unit);
        this.previousTime = -1;
        this.previousUtilization = -1;
        this.maxPreviousTime = -1;
        this.historyMap = new HashMap<>();
        setRandomGenerator(prng);
    }

    @Override
    public double getUtilization(final double time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time cannot be negative.");
        }

        if (time == this.previousTime) {
            return this.previousUtilization;
        }

        final double utilization = getOrGenerateUtilization(time);
        this.maxPreviousTime = Math.max(this.maxPreviousTime, time);
        this.previousTime = time;
        this.previousUtilization = utilization;
        return utilization;
    }

    private Double getOrGenerateUtilization(final double time) {
        if(time > this.maxPreviousTime){
            return generateUtilization(time);
        }

        final Double utilization = historyEnabled ? historyMap.get(time) : null;
        if (utilization == null) {
            return generateUtilization(time);
        }

        return utilization;
    }

    private double generateUtilization(final double time) {
        final double utilization = Math.abs(randomGenerator.sample());
        if(historyEnabled) {
            historyMap.put(time, utilization);
        }

        return utilization;
    }

    /**
     * Gets the utilization percentage for a given time from the internal {@link #historyMap}.
     *
     * @param time the time to get the utilization history for
     * @return the stored utilization percentage or <b>null</b> if it has never been generated
     * an utilization value for the given time
     */
    protected Double getUtilizationHistory(final double time) {
        return historyMap.get(time);
    }

    /**
     * Save the utilization history to a file.
     *
     * @param filename the filename
     * @throws UncheckedIOException when the file cannot be accessed
     */
    public void saveHistory(final String filename) {
        try (final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(historyMap);
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
            historyMap = (Map<Double, Double>) ois.readObject();
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

    /**
     * Checks if the history of resource utilization along simulation time
     * is to be kept or not.
     * @return true if the history is to be kept, false otherwise
     * @see #enableHistory()
     * @see #disableHistory()
     */
    public boolean isHistoryEnabled() {
        return historyEnabled;
    }

    /**
     * Enables or disables the resource utilization history,
     * so that utilization values is stored along all the simulation execution.
     * @param enableHistory true to enable the utilization history, false to disable
     * @return
     *
     * @see #enableHistory()
     * @see #disableHistory()
     */
    public UtilizationModelStochastic setHistoryEnabled(final boolean enableHistory) {
        this.historyEnabled = enableHistory;
        return this;
    }

    /**
     * Enables the resource utilization history,
     * so that utilization values is stored along all the simulation execution.
     * @return
     */
    public UtilizationModelStochastic enableHistory() {
        this.historyEnabled = true;
        return this;
    }

    /**
     * Disables the resource utilization history,
     * so that utilization values is <b>NOT</b> stored along all the simulation execution.
     * @return
     */
    public UtilizationModelStochastic disableHistory() {
        this.historyEnabled = false;
        return this;
    }
}
