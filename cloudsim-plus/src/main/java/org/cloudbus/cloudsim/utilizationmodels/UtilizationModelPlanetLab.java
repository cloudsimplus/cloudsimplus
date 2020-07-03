package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

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
     * A {@link UnaryOperator} Function that will be used to map the utilization values
     * read from the trace value to a different value.
     * That Function is useful when you don't want to use the values from the trace as they are,
     * but you want to scale the values applying any mathematical operation over them.
     * For instance, you can provide a mapper Function that scale the values in 10 times,
     * by giving a Lambda Expression such as {@code value -> value * 10}
     * in the mapper parameter of some constructor.
     *
     * <p>If a mapper Function is not set, the values are used as read from the trace file,
     * without any change (except that the scale is always converted to [0..1]).</p>
     * @see #UtilizationModelPlanetLab(String, UnaryOperator)
     */
    private UnaryOperator<Double> mapper;

    /**
     * The number of 5 minutes intervals inside one day (24 hours),
     * since the available PlanetLab traces store resource utilization collected every
     * 5 minutes along 24 hours.
     * This is default number of samples to try to read from the trace file
     * if a different value isn't provided to the constructors.
     */
    public static final int DEF_DATA_SAMPLES = 288;

    /**
     * The default interval between each data line inside a
     * PlanetLab trace file (in seconds)
     */
    public static final int DEF_SCHEDULING_INTERVAL = 300;

    /**
     * @see #getSchedulingInterval()
     */
    private double schedulingInterval;

    /**
     * The resource utilization utilization for an entire day, in intervals of 5
     * minutes. The size of the array is defined according to the number of utilization samples
     * specified in the constructor.
     *
     * <p>If there is a {@link #mapper} Function set,
     * the values are returned and stored according to the operation performed
     * by such a Function. If no mapper Function is set, the values
     * are returned and stored as read from the trace file (always in scale from 0 to 1).</p>
     *
     * @see #readWorkloadFile(InputStreamReader, int)
     */
    private final double[] utilization;

    /**
     * Instantiates a new PlanetLab utilization model from a trace
     * file inside the <b>application's resource directory</b>,
     * considering that the interval between each data line inside a
     * PlanetLab trace file is the {@link #DEF_SCHEDULING_INTERVAL default one}.
     *
     * <p>It checks if the first line of the trace has a comment representing its number of lines.
     * In this case, it will be used to accordingly create an array
     * of that size to store the values read from the trace.
     * If the file doesn't have such a comment with a valid line number,
     * it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     * from the trace.</p>
     *
     * @param workloadFilePath the <b>relative path</b> of a PlanetLab Datacenter trace file.
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     */
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath) {
        return getInstance(workloadFilePath, DEF_SCHEDULING_INTERVAL);
    }

    /**
     * Instantiates a PlanetLab utilization model from a trace
     * file located <b>inside the application's resource directory</b>.
     *
     * <p>It checks if the first line of the trace has a comment representing its number of lines.
     * In this case, it will be used to accordingly create an array
     * of that size to store the values read from the trace.
     * If the file doesn't have such a comment with a valid line number,
     * it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     * from the trace.</p>
     *
     * @param workloadFilePath the <b>relative path</b> of a PlanetLab Datacenter trace file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     */
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath, final double schedulingInterval) {
        return new UtilizationModelPlanetLab(newReader(workloadFilePath), schedulingInterval, -1);
    }

    /**
     * Instantiates a PlanetLab utilization model from a trace
     * file located <b>inside the application's resource directory</b>.
     *
     * <p>It checks if the first line of the trace has a comment representing its number of lines.
     * In this case, it will be used to accordingly create an array
     * of that size to store the values read from the trace.
     * If the file doesn't have such a comment with a valid line number,
     * it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     * from the trace.</p>
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param mapper A {@link UnaryOperator} Function that will be used to map the utilization values
     * read from the trace value to a different value.
     * That Function is useful when you don't want to use the values from the trace as they are,
     * but you want to scale the values applying any mathematical operation over them.
     * For instance, you can provide a mapper Function that scale the values in 10 times,
     * by giving a Lambda Expression such as {@code value -> value * 10}.
     *
     * <p>If a mapper Function is not set, the values are used as read from the trace file,
     * without any change (except that the scale is always converted to [0..1]).</p>
     * @param mapper a {@link UnaryOperator} Function to set
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     * @see #getInstance(String)
     */
    public static UtilizationModelPlanetLab getInstance(final String workloadFilePath, final UnaryOperator<Double> mapper) throws NumberFormatException {
        return new UtilizationModelPlanetLab(newReader(workloadFilePath), DEF_SCHEDULING_INTERVAL, -1, mapper);
    }

    private static InputStreamReader newReader(final String workloadFilePath) {
        return ResourceLoader.newInputStreamReader(workloadFilePath, UtilizationModelPlanetLab.class);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file <b>outside</b> the application's resource directory.
     *
     * <p>It checks if the first line of the trace has a comment representing its number of lines.
     * In this case, it will be used to accordingly create an array
     * of that size to store the values read from the trace.
     * If the file doesn't have such a comment with a valid line number,
     * it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     * from the trace.</p>
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     * @see #getInstance(String)
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval) throws NumberFormatException
    {
        this(workloadFilePath, schedulingInterval, -1);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file <b>outside</b> the application's resource directory.
     *
     * <p>It checks if the first line of the trace has a comment representing its number of lines.
     * In this case, it will be used to accordingly create an array
     * of that size to store the values read from the trace.
     * If the file doesn't have such a comment with a valid line number,
     * it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     * from the trace.</p>
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param mapper A {@link UnaryOperator} Function that will be used to map the utilization values
     * read from the trace value to a different value.
     * That Function is useful when you don't want to use the values from the trace as they are,
     * but you want to scale the values applying any mathematical operation over them.
     * For instance, you can provide a mapper Function that scale the values in 10 times,
     * by giving a Lambda Expression such as {@code value -> value * 10}.
     *
     * <p>If a mapper Function is not set, the values are used as read from the trace file,
     * without any change (except that the scale is always converted to [0..1]).</p>
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     * @see #getInstance(String)
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final UnaryOperator<Double> mapper) throws NumberFormatException {
        this(newReader(workloadFilePath), DEF_SCHEDULING_INTERVAL, -1, mapper);
    }

    /**
     * Instantiates a new PlanetLab resource utilization model from a trace
     * file <b>outside</b> the application's resource directory.
     *
     * @param workloadFilePath the path of a PlanetLab Datacenter workload file.
     * @param schedulingInterval the time interval in which precise utilization can be got from the file
     * @param dataSamples number of samples to read from the workload file.
     *                    If -1 is given, it checks if the first line of the trace has a comment.
     *                    In this case, that comment is expected to represent the number of lines
     *                    inside the trace and it will be used to accordingly create an array
     *                    of that size to store the values read from the trace.
     *                    If the file doesn't have such a comment with a valid line number,
     *                    it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     *                    from the trace.
     * @throws NumberFormatException when a value inside the side is not a valid number
     * @see #getSchedulingInterval()
     * @see #getInstance(String)
     */
    public UtilizationModelPlanetLab(final String workloadFilePath, final double schedulingInterval, final int dataSamples) throws NumberFormatException {
        /*The default mapper Function doesn't change the value read from the trace file.
         Therefore, the value is used as is.*/
        this(newReader(workloadFilePath), schedulingInterval, dataSamples);
    }

    private UtilizationModelPlanetLab(
        final InputStreamReader sreader,
        final double schedulingInterval,
        final int dataSamples) throws NumberFormatException
    {
        this(sreader, schedulingInterval, dataSamples, UnaryOperator.identity());
    }

    private UtilizationModelPlanetLab(
        final InputStreamReader sreader,
        final double schedulingInterval,
        final int dataSamples,
        final UnaryOperator<Double> mapper) throws NumberFormatException
    {
        super();
        setSchedulingInterval(schedulingInterval);
        this.mapper = Objects.requireNonNull(mapper);
        utilization = readWorkloadFile(sreader, dataSamples);
    }

    /**
     * Reads the planet lab workload file in which each one of its lines
     * is a resource utilization percentage to be used for a different simulation time.
     * The number of the line represents the simulation time to which
     * the value in such a line will be used as a resource utilization percentage.
     * For instance, the line 0 represents a resource utilization percentage for
     * simulation time 0.
     *
     * @param sreader the {@link InputStreamReader} to read the file
     * @param dataSamples number of samples to read from the workload file.
     *                    If -1 is given, it checks if the first line of the trace has a comment.
     *                    In this case, that comment is expected to represent the number of lines
     *                    inside the trace and it will be used to accordingly create an array
     *                    of that size to store the values read from the trace.
     *                    If the file doesn't have such a comment with a valid line number,
     *                    it will be tried to read just {@link #DEF_DATA_SAMPLES} lines
     *                    from the trace.
     * @return an array containing the utilization values read from the trace file (in scale from 0 to 1)
     * @throws UncheckedIOException when the trace file cannot be read
     * @see #utilization
     */
    private double[] readWorkloadFile(final InputStreamReader sreader, int dataSamples) {
        Objects.requireNonNull(sreader);
        double[] utilization = {0};

        try (BufferedReader reader = new BufferedReader(sreader)) {
            int lineNum = 0;
            String line;
            while((line=reader.readLine())!=null && lineNum < utilization.length){
                if(lineNum == 0){
                    dataSamples = parseDataSamples(line, dataSamples);
                    utilization = createEmptyArray(dataSamples);
                }

                if(!isComment(line)) {
                    utilization[lineNum++] = mapper.apply(Double.parseDouble(line) / 100.0);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return utilization;
    }

    /**
     * Try to get the number of lines from the trace file (data samples).
     * @param line the first line read from the trace
     * @param dataSamples The number of lines to read.
     *                    If negative it means it will try to get the
     *                    number of lines directly from the file.
     *                    The trace may have its number of lines as a comment in the
     *                    first line of the file.
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
     * Gets the number of data samples actually read from the trace file.
     * @return
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
        //If the time requested is multiple of the scheduling interval, gets a precise value from the trace file
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
        /*@TODO The interval size should add 1, but this is the original formula.
                It needs to be checked the impact in tests.*/
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
