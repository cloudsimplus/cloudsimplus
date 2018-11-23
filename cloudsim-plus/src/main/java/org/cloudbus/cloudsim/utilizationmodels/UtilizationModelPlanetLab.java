package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Defines a resource utilization model based on a
 * <a href="https://www.planet-lab.org">PlanetLab</a>
 * Datacenter workload (trace) file.
 *
 * <p>
 * Each PlanetLab trace file available contains CPU utilization measured at every 5 minutes (300 seconds) inside PlanetLab VMs.
 * This value in seconds is commonly used for the {@link #getSchedulingInterval() scheduling interval} attribute
 * when instantiating an object of this class.
 * </p>
 */
public class UtilizationModelPlanetLab extends UtilizationModelAbstract {

    /**
     * The number of 5 minutes intervals inside one day (24 hours),
     * since the available PlanetLab traces store resource utilization collected every
     * 5 minutes along 24 hours.
     * This is default number of samples to try to read from the trace file
     * if a different value isn't provided to the constructors.
     */
    private static final int DATA_SAMPLES = 288;

    /**
     * @see #getSchedulingInterval()
     */
    private double schedulingInterval;

    /**
     * The resource utilization utilization for an entire day, in intervals of 5
     * minutes. The size of the array is defined according to the number of utilization samples
     * specified in the constructor.
     *
     * @see #DATA_SAMPLES
     */
    private final double[] utilization;

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file inside the <b>application's resource directory</b>.
     *
     * @param traceFilePath the <b>relative path</b> of a PlanetLab Datacenter trace file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @throws NumberFormatException the number format exception
     * @see #getSchedulingInterval()
     */
    public static UtilizationModelPlanetLab getInstance(final String traceFilePath, final double schedulingInterval) {
        final InputStreamReader reader = new InputStreamReader(ResourceLoader.getInputStream(traceFilePath, UtilizationModelPlanetLab.class));
        return new UtilizationModelPlanetLab(reader, schedulingInterval, DATA_SAMPLES);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file.
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @throws NumberFormatException the number format exception
     * @see #getSchedulingInterval()
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval) throws NumberFormatException
    {
        this(workloadFilePath, schedulingInterval, DATA_SAMPLES);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model with variable
     * utilization samples from a workload file.
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @param dataSamples number of samples to read from the workload file
     * @throws NumberFormatException the number format exception
     * @see #getSchedulingInterval()
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval, final int dataSamples) throws NumberFormatException
    {
        this(ResourceLoader.getFileReader(workloadFilePath), schedulingInterval, dataSamples);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model with variable
     * utilization samples from a workload file.
     *
     * @param reader the {@link InputStreamReader} to read the workload file
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @param dataSamples number of samples to read from the workload file
     * @throws NumberFormatException the number format exception
     * @see #getSchedulingInterval()
     */
    private UtilizationModelPlanetLab(final InputStreamReader reader, final double schedulingInterval, final int dataSamples) throws NumberFormatException
    {
        super();
        setSchedulingInterval(schedulingInterval);
        try {
            utilization = readWorkloadFile(reader, dataSamples);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads the planet lab workload file in which each one of its lines
     * is a resource utilization percentage to be used for a different simulation time.
     * The number of the line represents the simulation time to which
     * the value in such a line will be used as a resource utilization percentage.
     * For instance, the line 0 represents a resource utilization percentage for
     * simulation time 0.
     *
     * @param reader the {@link InputStreamReader} to read the file
     * @param dataSamples the number of lines to read
     * @return an array containing the lines read from the file
     * @throws IOException
     */
    private double[] readWorkloadFile(final InputStreamReader reader, final int dataSamples) throws IOException {
        Objects.requireNonNull(reader);
        final double[] utilization = createEmptyArray(Math.max(2, dataSamples));

        try (BufferedReader input = new BufferedReader(reader)) {
            int lineNum = 0;
            String line;
            while((line=input.readLine())!=null && !line.startsWith("#") && lineNum < utilization.length){
                utilization[lineNum++] = Double.parseDouble(line) / 100.0;
            }
        }

        return utilization;
    }

    private double[] createEmptyArray(final int size) {
        final double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i]=0;
        }

        return data;
    }

    @Override
    public double getUtilization(final double time) {
        //If the time requested is multiple of the scheduling interval, gets a precise value from the trace utilization
        if (time % getSchedulingInterval() == 0) {
            return utilization[(int) getUtilizationIndex(time)];
        }

        /* Otherwise, computes a utilization based the
         * utilization between the a interval [start - end] for which
         * we have the utilization stored in the trace. */
        final int prevIndex = getPrevUtilizationIndex(time);

        //Elapsed time since the previous utilization index
        final double elapsedTimeSincePrevUsage = prevIndex * getSchedulingInterval();

        final double totalElapsedTime = time - elapsedTimeSincePrevUsage;
        return utilization[prevIndex] + getUtilizationPerSec(time) * totalElapsedTime;
    }

    private double getUtilizationPerSec(final double time) {
        final int prevIndex = getPrevUtilizationIndex(time);
        final int nextIndex = getNextUtilizationIndex(time);

        return (utilization[nextIndex] - utilization[prevIndex]) / getSecondsInsideInterval(prevIndex, nextIndex);
    }

    protected final double getSecondsInsideInterval(final int prevIndex, final int nextIndex) {
        return getIntervalSize(prevIndex, nextIndex) * schedulingInterval;
    }

    /**
     * Gets the index of the {@link #utilization} inside the trace file that corresponds to a given time.
     * The trace file contains utilization according to a {@link #getSchedulingInterval()}.
     * Considering that the time given is multiple of this interval, this method
     * returns the exact index of the {@link #utilization} that contains the utilization for that time.
     *
     * @param time the time to get the index of the {@link #utilization} that contains the utilization
     *             for that time
     * @return the index of the {@link #utilization} containing the utilization for the time given
     */
    private double getUtilizationIndex(final double time) {
        /* Since the Cloudlet that owns this utilization model instance
         * may run longer than there is data in the trace file,
         * we need to implement kind of a circular list to read
         * the data from the file. This way, the modulo operation
         * ensures we start reading data from the beginning of the
         * file if its end is reached.
         */
        return (time / schedulingInterval) % utilization.length;
    }

    /**
     * Gets the previous index of the {@link #utilization} inside the trace file that corresponds to a given time.
     * The trace file contains utilization according to a {@link #getSchedulingInterval()}.
     * Considering that the time given isn't multiple of this interval, this method
     * returns the index of the {@link #utilization} containing the utilization for the previous time multiple of the scheduling interval.
     *
     * @param time the time to get the index of the {@link #utilization} that contains the utilization
     *             for that time
     * @return the index of the {@link #utilization} containing the utilization for the previous time multiple of the scheduling interval
     */
    private int getPrevUtilizationIndex(final double time) {
        return (int)Math.floor(getUtilizationIndex(time));
    }

    /**
     * Gets the previous index of the {@link #utilization} inside the trace file that corresponds to a given time.
     * The trace file contains utilization according to a {@link #getSchedulingInterval()}.
     * Considering that the time given isn't multiple of this interval, this method
     * returns the index of the {@link #utilization} containing the utilization for the next time multiple of the scheduling interval.
     *
     * @param time the time to get the index of the {@link #utilization} that contains the utilization
     *             for that time
     * @return the index of the {@link #utilization} containing the utilization for the next time multiple of the scheduling interval
     */
    private int getNextUtilizationIndex(final double time) {
        //Computes the modulo again since the Math.ceil may return an index higher than the size of the utilization array
        return (int)Math.ceil(getUtilizationIndex(time)) % utilization.length;
    }

    /**
     * Gets the number of {@link #utilization} samples between two indexes.
     *
     * <p>
     * Since the utilization array is implemented as a circular list,
     * when the last index is read, it restarts from the first index again.
     * Accordingly, we can have situations where the end index is the last
     * array element and the start index is the first or some subsequent index.
     * This way, computing the difference between the two indexes would return a negative value.
     * The method ensures that a positive value is returned, correctly
     * computing the size of the interval between the two indexes.
     * </p>
     *
     * <p>Consider that the trace file has 288 lines, indexed from line 0 to 287.
     * Think of the trace as a circular list with indexes 0, 1, 2, 3 ...... 286, 287, 0, 1, 2, 3 ...
     * If the start index is 286 and the end index 2, then the interval size is 4
     * (the number of indexes between 286 and 2).
     *
     * </p>
     * @param startIndex the start index in the interval
     * @param endIndex the end index in the interval
     * @return the number of samples inside such indexes interval
     */
    protected final int getIntervalSize(final int startIndex, final int endIndex) {
        /*@todo The interval size should add 1, but this is the original formula. It needs to be checked the impact in tests.*/
        final int index = endIndex - startIndex;

        return index >= 0 ? index : (utilization.length - startIndex) + endIndex;
    }

    /**
     * Gets the time interval (in seconds) in which precise
     * utilization can be got from the workload file.
     *
     * <p>That means if the {@link #getUtilization(double)} is called
     * passing any time that is multiple of this scheduling interval,
     * the utilization returned will be the value stored for that
     * specific time. Otherwise, the value will be an arithmetic mean
     * of the beginning and the ending of the interval in which
     * the given time is.</p>
     *
     * @return the scheduling interval in seconds
     */
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

    /**
     * Sets the scheduling interval.
     *
     * @param schedulingInterval the scheduling interval to set
     * @see #getSchedulingInterval()
     */
    public final void setSchedulingInterval(final double schedulingInterval) {
        if(schedulingInterval <= 0){
            throw new IllegalArgumentException("Scheduling interval must greater than 0. The given value is " + schedulingInterval);
        }

        this.schedulingInterval = schedulingInterval;
    }
}
