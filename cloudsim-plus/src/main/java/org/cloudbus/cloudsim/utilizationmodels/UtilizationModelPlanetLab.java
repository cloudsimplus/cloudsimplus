package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.*;

/**
 * Defines the resource utilization model based on a
 * <a href="https://www.planet-lab.org">PlanetLab</a>
 * Datacenter workload (trace) file.
 */
public class UtilizationModelPlanetLab extends UtilizationModelAbstract {

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
     * The number of 5 minutes intervals inside one day (24 hours),
     * since the available PlanetLab traces store resource utilization collected every
     * 5 minutes along 24 hours.
     * This is default number of samples to try to read from the trace file.
     */
    private static final int DATA_SAMPLES = 289;

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file inside the <b>application's resource directory</b>.
     *
     * @param traceFilePath The <b>relative path</b> of a PlanetLab Datacenter trace file.
     * @param schedulingInterval the scheduling interval that defines the time interval in which precise utilization is be got
     * @throws NumberFormatException the number format exception
     * @see #getSchedulingInterval()
     */
    public static UtilizationModelPlanetLab getInstance(final String traceFilePath, final double schedulingInterval) {
        final InputStreamReader reader = new InputStreamReader(ResourceLoader.getInputStream(UtilizationModelPlanetLab.class, traceFilePath));
        return new UtilizationModelPlanetLab(reader, schedulingInterval, DATA_SAMPLES);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file.
     *
     * @param workloadFilePath The path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the scheduling interval that defines the time interval in which precise utilization is be got
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
     * @param workloadFilePath The path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the scheduling interval that defines the time interval in which precise utilization is be got
     * @param dataSamples number of samples to read from the workload file
     * @throws NumberFormatException the number format exception
     * @see #setSchedulingInterval(double)
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
     * @param schedulingInterval the scheduling interval that defines the time interval in which precise utilization is be got
     * @param dataSamples number of samples to read from the workload file
     * @throws NumberFormatException the number format exception
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
        final double[] utilization = createEmptyArray(Math.max(2, dataSamples));

        try (final BufferedReader input = new BufferedReader(reader)) {
            final int n = utilization.length;
            int i = 0;
            String line;
            while((line=input.readLine())!=null && i < n){
                utilization[i++] = Integer.valueOf(line) / 100.0;
            }
            utilization[n - 1] = utilization[n - 2];
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
            return utilization[(int)getUtilizationIndex(time)];
        }

        /* Otherwise, computes a utilization based the
        *  utilization between the a interval [start - end] for which
        *  we have the utilization stored in the trace. */
        final int prevIndex = getPrevUtilizationIndex(time);

        //elapsed time since the previous utilization index
        final double elapsedTimeSincePrevUsage = prevIndex * getSchedulingInterval();

        final double totalElapsedTime = time - elapsedTimeSincePrevUsage;
        return utilization[prevIndex] + getUtilizationPerSec(time)*totalElapsedTime;
    }

    private double getUtilizationPerSec(final double time) {
        final int prevIndex = getPrevUtilizationIndex(time);
        final int nextIndex = getNextUtilizationIndex(time);

        return (utilization[nextIndex] - utilization[prevIndex]) / getSecondsInsideInterval(prevIndex, nextIndex);
    }

    private double getSecondsInsideInterval(final int prevIndex, final int nextIndex) {
        return getIntervalSize(prevIndex, nextIndex) * getSchedulingInterval();
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
        return time / getSchedulingInterval();
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
        return (int)Math.ceil(getUtilizationIndex(time));
    }

    /**
     * Gets the number of {@link #utilization} samples between two indexes.
     * @param startIndex the start index in the interval
     * @param endIndex the end index in the interval
     * @return the number of samples inside such indexes interval
     */
    private int getIntervalSize(final int startIndex, final int endIndex) {
        /*@todo The interval size should add 1, but this is the original formula. It needs to be checked the impact in tests.*/
        return endIndex - startIndex;
    }

    /**
     * Gets the time interval (in seconds) in which precise utilization can be got from the workload file.
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
        this.schedulingInterval = schedulingInterval;
    }
}
