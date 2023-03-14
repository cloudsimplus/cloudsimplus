/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.utilizationmodels;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.StatisticalDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implements a model, according to which a Cloudlet generates
 * random resource utilization every time frame,
 * using a {@link ContinuousDistribution}.
 *
 * <p>The class may return different utilization values
 * for the same requested time.
 * For performance reasons, this behaviour is dependent of the {@link #isHistoryEnabled()}
 * and {@link #isAlwaysGenNewRandUtilization()}.
 * </p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public class UtilizationModelStochastic extends UtilizationModelAbstract {

    /**
     * The Random Number Generator (RNG).
     */
    @Getter @Setter @NonNull
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

    /**
     * Indicates if the resource utilization history is enabled or not,
     * so that utilization values are stored along all the simulation execution.
     *
     * <p>If utilization history is disable, more pseudo-random numbers will be generated,
     * decreasing simulation performance.
     * Changing this attribute is a trade-off between memory and CPU utilization:
     * <ul>
     *     <li>enabling reduces CPU utilization but increases RAM utilization;</li>
     *     <li>disabling reduces RAM utilization but increases CPU utilization.</li>
     * </ul>
     * </p>
     */
    @Getter @Setter
    private boolean historyEnabled;

    /**
     * Indicates if at every time the {@link #getUtilization()} or {@link #getUtilization(double)}
     * methods are called, a new randomly generated utilization will be returned or not.
     * This attribute is false by default, meaning that consecutive utilization requests
     * for the same time may return the same previous generated utilization value.
     * Check the documentation in the return section at the end for details.
     *
     * <p>Using one instance of this utilization model for every Cloudlet
     * in a large simulation scenario may be very expensive in terms of simulation
     * time and memory consumption. This way, the researcher may want to use a single
     * utilization model instance for every Cloudlet.
     * The side effect is that, if this attribute is false (the default),
     * it will usually return the same utilization value for the same requested time
     * for distinct Cloudlets. That commonly is not what the researcher wants.
     * He/she usually wants that every Cloudlet has an independent resource utilization.
     * </p>
     *
     * <p>To reduce simulation time and memory consumption, you can use a single utilization
     * model instance for a given Cloudlet resource (such as CPU) and set this attribute to false.
     * This way, it will always generate different utilization values for every time
     * an utilization is requested (even if the same previous time is given).</p>
     */
    @Getter @Setter
    private boolean alwaysGenNewRandUtilization;

    /**
     * Instantiates a utilization model stochastic
     * that defines the resource utilization in percentage.
     * The resource utilization history is enabled by default.
     *
     * @see #setUnit(Unit)
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic() {
        this(Unit.PERCENTAGE);
    }

    /**
     * Instantiates a utilization model stochastic
     * where the resource utilization is defined in the given unit.
     * The resource utilization history is enabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic(final Unit unit) {
        this(unit, StatisticalDistribution.defaultSeed());
    }

    /**
     * Instantiates a utilization model stochastic
     * where the resource utilization is defined in the given unit.
     * The resource utilization history is enabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param seed the seed to initialize the random number generator.
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic(final Unit unit, final long seed) {
        this(unit, new UniformDistr(seed));
    }

    /**
     * Instantiates a utilization model stochastic
     * that defines the resource utilization in percentage.
     * The resource utilization history is enabled by default.
     *
     * @param seed the seed to initialize the random number generator.
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic(final long seed) {
        this(Unit.PERCENTAGE, new UniformDistr(seed));
    }

    /**
     * Instantiates a utilization model stochastic based on a given Pseudo Random Number Generator (PRNG).
     * It defines the resource utilization in percentage.
     * The resource utilization history is enabled by default.
     *
     * @param prng the Pseudo Random Number Generator (PRNG) to generate utilization values
     * @see #setUnit(Unit)
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic(final ContinuousDistribution prng) {
        this(Unit.PERCENTAGE, prng);
    }

    /**
     * Instantiates a new utilization model stochastic based on a given Pseudo Random Number Generator (PRNG).
     * The resource utilization history is enabled by default.
     *
     * @param unit the {@link Unit} that determines how the resource is used (for instance, if
     *             resource usage is defined in percentage of the Vm resource or in absolute values)
     * @param prng the Pseudo Random Number Generator (PRNG) to generate utilization values
     * @see #setHistoryEnabled(boolean)
     * @see #isAlwaysGenNewRandUtilization()
     */
    public UtilizationModelStochastic(final Unit unit, @NonNull final ContinuousDistribution prng) {
        super(unit);
        this.previousTime = -1;
        this.previousUtilization = -1;
        this.maxPreviousTime = -1;
        this.historyEnabled = true;
        this.historyMap = new HashMap<>();
        setRandomGenerator(prng);
    }

    /**
     * {@inheritDoc}
     * @see <a href="https://github.com/manoelcampos/cloudsim-plus/issues/197">Issue #197 for more details</a>
     * @param time {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected double getUtilizationInternal(final double time) {
        if (time == this.previousTime && !alwaysGenNewRandUtilization) {
            return this.previousUtilization;
        }

        final double utilization = getOrGenerateUtilization(time);
        this.maxPreviousTime = Math.max(this.maxPreviousTime, time);
        this.previousTime = time;
        this.previousUtilization = utilization;
        return utilization;
    }

    private Double getOrGenerateUtilization(final double time) {
        if(time > this.maxPreviousTime || alwaysGenNewRandUtilization){
            return generateUtilization(time);
        }

        final Double utilization = historyEnabled ? historyMap.get(time) : null;
        return utilization == null ? generateUtilization(time) : utilization;
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
     * @throws IOException when the file cannot be accessed
     */
    @SneakyThrows(IOException.class)
    public void saveHistory(final String filename) {
        try (var oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(historyMap);
        }
    }

    /**
     * Load a utilization history from a file.
     *
     * @param filename the filename
     * @throws IOException when the file cannot be accessed
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IOException.class, ClassNotFoundException.class})
    public void loadHistory(final String filename) {
        try (var ois = new ObjectInputStream(new FileInputStream(filename))) {
            historyMap = (Map<Double, Double>) ois.readObject();
        }
    }
}
