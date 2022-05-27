/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import org.apache.commons.lang3.StringUtils;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.util.BytesConversion;

/**
 * An implementation of a Hard Drive (HD) storage device with a specific capacity (in Megabytes).
 * It simulates the behavior of a typical hard drive.
 * The default values for this storage are those of a
 * "<a href='https://www.seagate.com/files/staticfiles/maxtor/en_us/documentation/data_sheets/diamondmax_10_data_sheet.pdf'>Maxtor DiamondMax 10 ATA</a>"
 * hard disk with the following parameters:
 * <ul>
 *   <li>latency = 4.17 ms</li>
 *   <li>avg seek time = 9 m/s</li>
 *   <li>max transfer rate = 1064 Megabits/sec (133 MBytes/sec)</li>
 * </ul>
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class HarddriveStorage implements FileStorage {
    /** @see #getStorage() */
    private final SimpleStorage storage;

    /**
     * @see #getName()
     */
    private final String name;
    /**
     * A number generator required to randomize the seek time.
     */
    private ContinuousDistribution gen;

    /**
     * @see #getMaxTransferRate()
     */
    private double maxTransferRate;

    /**
     * @see #getLatency()
     */
    private double latency;

    /**
     * @see #getAvgSeekTime()
     */
    private double avgSeekTime;

    /**
     * Creates a new hard drive storage with a given name and capacity.
     *
     * @param name     the name of the new hard drive storage
     * @param capacity the capacity in MByte
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public HarddriveStorage(final String name, final long capacity) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Storage name cannot be empty.");
        }

        this.name = name;
        this.storage = new SimpleStorage(capacity);

        init();
    }

    /**
     * Creates a new hard drive storage with a given capacity. In this case the name of the storage
     * is a default name.
     *
     * @param capacity the capacity in MByte
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public HarddriveStorage(final long capacity) throws IllegalArgumentException {
        this("HarddriveStorage", capacity);
    }

    /**
     * Initializes the hard drive. The most common parameters, such
     * as latency, average seek time and maximum transfer rate are set.
     * The default values are set to simulate the "Maxtor DiamondMax 10 ATA" hard disk.
     */
    private void init() {
        setLatency(DEF_LATENCY_SECS);
        setAvgSeekTime(DEF_SEEK_TIME_SECS);
        setMaxTransferRate(DEF_MAX_TRANSF_RATE_MBITS_SEC);
    }

    /**
     * @return the name of the storage device
     */
    public String getName() {
        return name;
    }

    @Override
    public double getLatency() {
        return latency;
    }

    @Override
    public FileStorage setLatency(final double latency) {
        if (latency < 0) {
            throw new IllegalArgumentException("Latency must be greater than zero.");
        }

        this.latency = latency;
        return this;
    }

    @Override
    public double getMaxTransferRate() {
        return maxTransferRate;
    }

    @Override
    public FileStorage setMaxTransferRate(final double maxTransferRate) {
        if (maxTransferRate <= 0) {
            throw new IllegalArgumentException("Max transfer rate must be greater than zero.");
        }

        this.maxTransferRate = maxTransferRate;
        return this;
    }

    /**
     * Sets the average seek time of the storage in seconds.
     *
     * @param seekTime the average seek time in seconds
     * @return
     */
    public FileStorage setAvgSeekTime(final double seekTime) {
        return setAvgSeekTime(seekTime, null);
    }

    /**
     * Sets the average seek time and a new generator of seek times in seconds. The generator
     * determines a randomized seek time.
     *
     * @param seekTime the average seek time in seconds
     * @param gen      the ContinuousGenerator which generates seek times
     * @return
     */
    public FileStorage setAvgSeekTime(final double seekTime, final ContinuousDistribution gen) {
        if (seekTime < 0) {
            throw new IllegalArgumentException("Seek time cannot be negative.");
        }

        avgSeekTime = seekTime;
        this.gen = gen;
        return this;
    }

    /**
     * Gets the average seek time of the hard drive in seconds.
     *
     * @return the average seek time in seconds
     */
    public double getAvgSeekTime() {
        return avgSeekTime;
    }

    /**
     * Get the seek time for a file with the defined size. Given a file size in MByte, this method
     * returns a seek time for the file in seconds.
     *
     * @param fileSize the size of a file in MByte
     * @return the seek time in seconds
     */
    public double getSeekTime(final int fileSize) {
        double result = 0;

        if (gen != null) {
            result += gen.sample();
        }

        if (fileSize > 0 && storage.getCapacity() != 0) {
            result += fileSize / (double) storage.getCapacity();
        }

        return result;
    }

    @Override
    public double getTransferTime(final int fileSize) {
        //It's ensured the maxTransferRate cannot be zero.
        return getTransferTime(fileSize, getMaxTransferRate()) + getLatency();
    }

    /**
     * Gets the time to transfer a file (in MBytes)
     * according to a given transfer speed (in Mbits/sec).
     *
     * @param fileSize the size of the file to compute the transfer time (in MBytes)
     * @param speed    the speed (in MBits/sec) to compute the time to transfer the file
     * @return the transfer time in seconds
     */
    protected final double getTransferTime(final int fileSize, final double speed) {
        return BytesConversion.bytesToBits(fileSize) / speed;
    }

    @Override
    public long getCapacity() {
        return storage.getCapacity();
    }

    @Override
    public long getAvailableResource() {
        return storage.getAvailableResource();
    }

    @Override
    public long getAllocatedResource() {
        return storage.getAllocatedResource();
    }

    @Override
    public boolean isAmountAvailable(long amountToCheck) {
        return storage.isAmountAvailable(amountToCheck);
    }

    @Override
    public boolean isAmountAvailable(double amountToCheck) {
        return isAmountAvailable((long) amountToCheck);
    }

    @Override
    public boolean isFull() {
        return storage.isFull();
    }

    /**
     * The internal storage that just manages
     * the HD capacity and used space.
     * The {@link HarddriveStorage} (HD) does not extends such class
     * to avoid its capacity and available amount of space
     * to be changed indiscriminately.
     * The available space is update according to files added or removed
     * from the HD.
     */
    public SimpleStorage getStorage() {
        return storage;
    }

    @Override
    public String getUnit() {
        return storage.getUnit();
    }
}
