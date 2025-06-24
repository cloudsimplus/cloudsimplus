/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.utilizationmodels;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

/// Defines a resource utilization model for [Cloudlet]s based on a
/// [PlanetLab](https://www.planet-lab.org) datacenter workload trace file.
///
/// Each PlanetLab trace available contains CPU utilization measured at every
/// 5 minutes (300 seconds) inside PlanetLab VMs.
/// This value in seconds is commonly used for the [scheduling interval][#getSchedulingInterval()]
/// attribute when instantiating an object of this class.
///
/// Check [this repository](https://github.com/cloudsimplus/planetlab-workload-traces) to get some trace files.
/// The [CloudSim Plus Examples](https://github.com/cloudsimplus/cloudsimplus-examples)
/// repository also provides some of these files.
public class UtilizationModelPlanetLab extends UtilizationModelAbstract {
    /// The number of 5-minutes intervals inside one day (24 hours),
    /// since the available PlanetLab traces store resource utilization collected every
    /// 5 minutes (the default data collection interval for PlanetLab trace files) along 24 hours.
    ///
    /// This constant is the default number of samples to try to read from the trace file
    /// if a different value isn't provided to the constructors.
    public static final int DEF_DATA_SAMPLES = 288;

    /**
     * The default interval between each data line inside a
     * PlanetLab trace file (in seconds)
     */
    public static final int DEF_SCHEDULING_INTERVAL = 300;

    /**
     * The resource utilization for an entire day, in intervals
     * defined by {@link #schedulingInterval}
     * (each line on available trace files represent resource utilization for a time
     * interval of 5 minutes).
     * The size of the array is defined according to the number of utilization samples
     * specified in the constructor.
     *
     * <p>If there is a {@link #mapper} Function set,
     * the values are returned and stored according to the operation performed
     * by such a Function. If no mapper Function is set, the values
     * are returned and stored as read from the trace file (always in scale from 0 to 1).</p>
     *
     * @see #readWorkloadFile(InputStreamReader, int)
     */
    protected final double[] utilization;

    /**
     * A {@link UnaryOperator} `Function` that will be used to map the utilization values
     * read from the trace value to a different value.
     * That Function is useful when you don't want to use the values from the trace as they are,
     * but you want to scale the values applying any mathematical operation over them.
     * For instance, you can provide a mapper `Function` that scale the values in 10 times,
     * by giving a Lambda Expression such as {@snippet : value -> value * 10}
     * in the mapper parameter of some constructor.
     *
     * <p>If a mapper Function is not set, the values are used as read from the trace file,
     * without any change (except that the scale is always converted to [0..1]).</p>
     * @see #UtilizationModelPlanetLab(String, UnaryOperator)
     */
    private final UnaryOperator<Double> mapper;

    /// The time interval (in seconds) in which precise
    /// utilization can be got from the workload file.
    ///
    /// That means if the [#getUtilization(double)] is called
    /// passing any time that is multiple of this scheduling interval,
    /// the utilization returned will be the value stored for that
    /// specific time. Otherwise, the value will be an arithmetic mean
    /// of the beginning and the ending of the interval in which
    /// the given time is.
    @Getter
    private double schedulingInterval;

    /// Creates a PlanetLab utilization model from a trace
    /// file inside the **application's resource directory**,
    /// considering that the interval between each data line inside a
    /// PlanetLab trace file is the [default one][#DEF_SCHEDULING_INTERVAL].
    ///
    /// It checks if the first line of the trace has a comment representing its number of lines.
    /// In this case, it will be used to accordingly create an array
    /// of that size to store the values read from the trace.
    /// If the file doesn't have such a comment with a valid line number,
    /// it will be tried to read just [#DEF_DATA_SAMPLES] lines from the trace.
    ///
    /// @param workloadFilePath the **relative path** of a PlanetLab datacenter trace file.
    /// @throws NumberFormatException when a value inside the side is not a valid number
    /// @see #getSchedulingInterval()
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath) {
        return getInstance(workloadFilePath, DEF_SCHEDULING_INTERVAL);
    }

    /// Creates a PlanetLab utilization model from a trace
    /// file located **inside the application's resource directory**.
    ///
    /// It checks if the first line of the trace has a comment representing its number of lines.
    /// In this case, it will be used to accordingly create an array
    /// of that size to store the values read from the trace.
    /// If the file doesn't have such a comment with a valid line number,
    /// it will be tried to read just [#DEF_DATA_SAMPLES] lines from the trace.
    ///
    /// @param workloadFilePath the **relative path** of a PlanetLab datacenter trace file.
    /// @param schedulingInterval the time interval in which precise utilization can be got from the file
    /// @throws NumberFormatException when a value inside the side is not a valid number
    /// @see #getSchedulingInterval()
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath, final double schedulingInterval) {
        return new UtilizationModelPlanetLab(newReader(workloadFilePath), schedulingInterval, -1);
    }

    /// Creates a PlanetLab utilization model from a trace
    /// file located **inside the application's resource directory**.
    ///
    /// It checks if the first line of the trace has a comment representing its number of lines.
    /// In this case, it will be used to accordingly create an array
    /// of that size to store the values read from the trace.
    /// If the file doesn't have such a comment with a valid line number,
    /// it will be tried to read just [#DEF_DATA_SAMPLES] lines from the trace.
    ///
    /// @param workloadFilePath the path of a PlanetLab datacenter workload file.
    /// @param mapper A [UnaryOperator] `Function` that will be used to map the utilization values
    /// read from the trace value to a different value.
    /// That `Function` is useful when you don't want to use the values from the trace as they are,
    /// but you want to scale the values applying any mathematical operation over them.
    /// For instance, you can provide a mapper `Function` that scales the values in 10 times,
    /// by giving a Lambda Expression such as `value -> value * 10`.
    ///
    /// If a mapper `Function` is not set, the values are used as read from the trace file,
    /// without any change (except that the scale is always converted to [0..1]).
    /// @throws NumberFormatException when a value inside the side is not a valid number
    /// @see #getSchedulingInterval()
    /// @see #getInstance(String)
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath, final UnaryOperator<Double> mapper) throws NumberFormatException {
        return new UtilizationModelPlanetLab(newReader(workloadFilePath), DEF_SCHEDULING_INTERVAL, -1, mapper);
    }

    /// Creates a PlanetLab resource utilization model from a trace
    /// file **outside** the application's resource directory.
    ///
    /// It checks if the first line of the trace has a comment representing its number of lines.
    /// In this case, it will be used to accordingly create an array
    /// of that size to store the values read from the trace.
    /// If the file doesn't have such a comment with a valid line number,
    /// it will be tried to read just [#DEF_DATA_SAMPLES] lines from the trace.
    ///
    /// @param workloadFilePath the path of a PlanetLab Datacenter workload file.
    /// @param schedulingInterval the time interval in which precise utilization can be got from the file
    /// @throws NumberFormatException when a value inside the side is not a valid number
    /// @see #getSchedulingInterval()
    /// @see #getInstance(String)
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval) throws NumberFormatException
    {
        this(workloadFilePath, schedulingInterval, -1);
    }

    /**
     * Creates a PlanetLab resource utilization model from a trace
     * file <b>outside</b> the application's resource directory.
     *
     * @param workloadFilePath the path of a PlanetLab datacenter workload file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @param dataSamples number of samples to read from the workload file.
     *                    If -1 is given, it checks if the first line of the trace has a comment.
     *                    In this case, that comment is expected to represent the number of lines
     *                    inside the trace, and it will be used to accordingly create an array
     *                    of that size to store the values read from the trace.
     *                    If the file doesn't have such a comment with a valid line number,
     *                    it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     *                    from the trace.
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     * @see #getInstance(String)
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval, final int dataSamples) throws NumberFormatException {
        /* The default mapper Function doesn't change the value read from the trace file.
         Therefore, the value is used as it is. */
        this(newReader(workloadFilePath), schedulingInterval, dataSamples);
    }

    private UtilizationModelPlanetLab(
        final InputStreamReader reader,
        final double schedulingInterval,
        final int dataSamples) throws NumberFormatException
    {
        this(reader, schedulingInterval, dataSamples, UnaryOperator.identity());
    }

    /// Creates a PlanetLab resource utilization model from a trace
    /// file **outside** the application's resource directory.
    ///
    /// It checks if the first line of the trace has a comment representing its number of lines.
    /// In this case, it will be used to accordingly create an array
    /// of that size to store the values read from the trace.
    /// If the file doesn't have such a comment with a valid line number,
    /// it will be tried to read just [#DEF_DATA_SAMPLES] lines
    /// from the trace.
    ///
    /// @param workloadFilePath the path of a PlanetLab datacenter workload file.
    /// @param mapper A [UnaryOperator] Function that will be used to map the utilization values
    /// read from the trace value to a different value.
    /// That Function is useful when you don't want to use the values from the trace as they are,
    /// but you want to scale the values applying any mathematical operation over them.
    /// For instance, you can provide a mapper `Function` that scales the values in 10 times,
    /// by giving a Lambda Expression such as `value -> value * 10`.
    ///
    /// If a mapper `Function` is not set, the values are used as read from the trace file,
    /// without any change (except that the scale is always converted to [0..1]).
    /// @throws NumberFormatException when a value inside the side is not a valid number
    /// @see #getSchedulingInterval()
    /// @see #getInstance(String)
    public UtilizationModelPlanetLab(final String workloadFilePath, final UnaryOperator<Double> mapper) throws NumberFormatException {
        this(newReader(workloadFilePath), DEF_SCHEDULING_INTERVAL, -1, mapper);
    }

    /**
     * Creates a PlanetLab UtilizationModel
     * where the utilization data is provided directly utilization array parameter.
     * This can be used when you don't want to load the utilization data from a file.
     *
     * @param utilization the resource utilization data for an entire day, in intervals
     *        defined by a scheduling interval.
     *        If the data inside the array represents resource utilization in intervals
     *        of 5 minutes, your scheduling interval must be 300 seconds.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @param mapper A {@link UnaryOperator} `Function` that will be used to map the utilization values
     * read from the trace value to a different value.
     * That `Function` is useful when you don't want to use the values from the trace as they are,
     * but you want to scale the values applying any mathematical operation over them.
     * For instance, you can provide a mapper `Function` that scales the values in 10 times,
     * by giving a Lambda Expression such as {@code value -> value * 10}.
     *
     * <p>If a mapper `Function` is not set, the values are used as read from the trace file,
     * without any change (except that the scale is always converted to [0..1]).</p>
     * @throws NumberFormatException when a value inside the side is not a valid number
     */
    public UtilizationModelPlanetLab(
        final double[] utilization,
        final double schedulingInterval,
        final UnaryOperator<Double> mapper) throws NumberFormatException
    {
        super();
        setSchedulingInterval(schedulingInterval);
        this.mapper = Objects.requireNonNull(mapper);

        Objects.requireNonNull(utilization, "Utilization array cannot be null.");
        if(utilization.length <= 1){
            throw new IllegalArgumentException("The number of utilization samples must be greater than 1.");
        }
        this.utilization = utilization;
    }

    private UtilizationModelPlanetLab(
        final InputStreamReader reader,
        final double schedulingInterval,
        final int dataSamples,
        final UnaryOperator<Double> mapper) throws NumberFormatException
    {
        super();
        setSchedulingInterval(schedulingInterval);
        this.mapper = Objects.requireNonNull(mapper);
        utilization = readWorkloadFile(reader, dataSamples);
    }

    private static InputStreamReader newReader(final String workloadFilePath) {
        return ResourceLoader.newInputStreamReader(workloadFilePath, UtilizationModelPlanetLab.class);
    }

    /**
     * Reads the planet lab workload file in which each one of its lines
     * is a resource utilization percentage to be used for a different simulation time.
     * The number of the line represents the simulation time to which
     * the value in such a line will be used as a resource utilization percentage.
     * For instance, line 0 represents a resource utilization percentage for
     * simulation time 0.
     *
     * @param reader the {@link InputStreamReader} to read the file
     * @param dataSamples number of samples to read from the workload file.
     *                    If -1 is given, it checks if the first line of the trace has a comment.
     *                    In this case, that comment is expected to represent the number of lines
     *                    inside the trace, and it will be used to accordingly create an array
     *                    of that size to store the values read from the trace.
     *                    If the file doesn't have such a comment with a valid line number,
     *                    it will be tried to read just {@link #DEF_DATA_SAMPLES} lines from the trace.
     * @return an array containing the utilization values read from the trace file (in scale from 0 to 1)
     * @throws UncheckedIOException when the trace file cannot be read
     * @see #utilization
     */
    @SneakyThrows(IOException.class)
    private double[] readWorkloadFile(@NonNull final InputStreamReader reader, int dataSamples) {
        double[] utilization = {0};

        try (var buffer = new BufferedReader(reader)) {
            int lineNum = 0;
            String line;
            while((line=buffer.readLine())!=null && lineNum < utilization.length){
                if(lineNum == 0){
                    dataSamples = parseDataSamples(line, dataSamples);
                    utilization = createEmptyArray(dataSamples);
                }

                if(!isComment(line)) {
                    utilization[lineNum++] = mapper.apply(Double.parseDouble(line) / 100.0);
                }
            }
        }

        return utilization;
    }

    /**
     * Try to get the number of lines from the trace file (data samples).
     * @param line the first line read from the trace
     * @param dataSamples The number of lines to read.
     *                    If it's negative, that means it will try to get the
     *                    number of lines directly from the file.
     *                    The trace may have its number of lines as a comment in the first line of the file.
     * @return the given data sample if it's a positive number;
     *         the default data sample if the given value is negative
     *         and the file doesn't contain the number of lines in the first line;
     *         the number of lines read from the file
     */
    private int parseDataSamples(final String line, int dataSamples) {
        if(dataSamples < 0){
            dataSamples = isComment(line) ? MathUtil.parseInt(line.substring(1), DEF_DATA_SAMPLES) : DEF_DATA_SAMPLES;
        }

        return Math.max(2, dataSamples);
    }

    /**
     * @return the number of data samples actually read from the trace file.
     */
    public int getDataSamples(){
        return utilization.length;
    }

    private boolean isComment(final String line) {
        return line.startsWith("#");
    }

    private double[] createEmptyArray(final int size) {
        final double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i]=0;
        }

        return data;
    }

    @Override
    protected double getUtilizationInternal(final double time) {
    	/* If the time requested is multiple of the scheduling interval,
    	gets a precise value from the trace file. */
        if (Math.round(time) % getSchedulingInterval() == 0) {
            return utilization[(int) getUtilizationIndex(time)];
        }

        /* Otherwise, computes a utilization based the
         * utilization mean between the interval [prevIndex to nextIndex]
         * for which we have the utilization stored in the trace. */
        final int prevIndex = getPrevUtilizationIndex(time);
        final int nextIndex = getNextUtilizationIndex(time);

        return (utilization[prevIndex] + utilization[nextIndex]) / 2.0;
    }

    /// {@return the index of the utilization inside the trace file that corresponds to a given time}
    /// The trace file contains utilization according to a [#getSchedulingInterval()].
    /// Considering that the time given is multiple of this interval, this method
    /// returns the exact index of the [#utilization] that contains the utilization for that time.
    ///
    /// @param time the time to get the index of the [#utilization] that contains the utilization for that time
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

    /// {@return the index of the utilization inside the trace file
    /// that corresponds to the previous time multiple of the scheduling interval}
    ///
    /// The trace file contains utilization according to a [#getSchedulingInterval()].
    /// Considering that the time given isn't multiple of this interval, this method
    /// returns the index of the [#utilization] containing the utilization for the
    /// previous time multiple of the scheduling interval.
    ///
    /// @param time the time to get the index of the [#utilization] that contains the utilization for that time
    private int getPrevUtilizationIndex(final double time) {
        return (int)Math.floor(getUtilizationIndex(time));
    }

    /// {@return the index of the utilization inside the trace file that
    /// corresponds to the next time multiple of the scheduling interval}
    ///
    /// The trace file contains utilization according to a [#getSchedulingInterval()].
    /// Considering that the time given isn't multiple of this interval, this method
    /// returns the index of the [#utilization] containing the utilization
    /// for the next time multiple of the scheduling interval.
    ///
    /// @param time the time to get the index of the [#utilization] that contains the utilization for that time
    private int getNextUtilizationIndex(final double time) {
        //Computes the modulo again since the Math.ceil may return an index higher than the size of the utilization array
        return (int)Math.ceil(getUtilizationIndex(time)) % utilization.length;
    }

    /// {@return the number of utilization samples between two indexes}
    ///
    /// Since the utilization array is implemented as a circular list,
    /// when the last index is read, it restarts from the first index again.
    /// Accordingly, we can have situations where the end index is the last
    /// array element and the start index is the first or some subsequent index.
    /// This way, computing the difference between the two indexes would return a negative value.
    /// The method ensures that a positive value is returned, correctly
    /// computing the size of the interval between the two indexes.
    ///
    /// Consider that the trace file has 288 lines, indexed from line 0 to 287.
    /// Think of the trace as a circular list with indexes 0, 1, 2, 3 ...... 286, 287, 0, 1, 2, 3 ...
    /// If the start index is 286 and the end index 2, then the interval size is 4
    /// (the number of indexes between 286 and 2).
    ///
    /// @param startIndex the start index in the interval
    /// @param endIndex the end index in the interval
    protected final int getIntervalSize(final int startIndex, final int endIndex) {
        /*TODO The interval size should add 1, but this is the original formula.
               It needs to be checked the impact in tests.*/
        final int index = endIndex - startIndex;

        return index >= 0 ? index : (utilization.length - startIndex) + endIndex;
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
