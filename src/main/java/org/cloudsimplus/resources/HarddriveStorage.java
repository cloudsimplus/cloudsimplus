/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.resources;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.util.BytesConversion;
import org.cloudsimplus.util.MathUtil;

/// An implementation of a Hard Drive (HD) storage device with a specific capacity (in Megabytes).
/// It simulates the behavior of a typical hard drive.
/// The default values for this storage are those of a
/// [Maxtor DiamondMax 10 ATA](https://www.seagate.com/files/staticfiles/maxtor/en_us/documentation/data_sheets/diamondmax_10_data_sheet.pdf)
/// hard disk with the following parameters:
///
/// - latency = 4.17 ms
/// - avg seek time = 9 m/s
/// - max transfer rate = 1064 Megabits/sec (133 MBytes/sec)
///
/// @author Uros Cibej
/// @author Anthony Sulistio
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
@Setter
public class HarddriveStorage implements FileStorage {
    /// The internal storage that just manages
    /// the HD capacity and used space.
    /// The [HarddriveStorage] (HD) does not extend such a class
    /// to avoid its capacity and available amount of space
    /// to be changed indiscriminately.
    /// The available space is updated according to files added or removed from the HD.
    @Getter
    private final SimpleStorage storage;

    /**
     * The name of the storage device
     */
    @Getter
    private final String name;

    /// An optional Pseudo Random Generator (PRNG) following a [ContinuousDistribution]
    /// to generate random delays for file seek time.
    /// Pass [#NULL] to stop random delays.
    @NonNull
    private ContinuousDistribution prng;

    @Getter
    private double maxTransferRate;

    @Getter
    private double latency;

    /**
     * {@return the average seek time of the hard drive in seconds}
     */
    @Getter
    private double avgSeekTime;

    /**
     * Creates a hard drive storage with a given name and capacity.
     *
     * @param name     the name of the new hard drive storage
     * @param capacity the capacity in MByte
     * @throws IllegalArgumentException when the name and the capacity are not valid
     * @see #setPrng(ContinuousDistribution)
     */
    public HarddriveStorage(@NonNull final String name, final long capacity) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Storage name cannot be empty.");
        }

        this.name = name;
        this.storage = new SimpleStorage(capacity);
        this.prng = ContinuousDistribution.NULL;

        init();
    }

    /**
     * Creates a hard drive storage with a given capacity. In this case, the name of the storage
     * is a default name.
     *
     * @param capacity the capacity in MByte
     * @throws IllegalArgumentException when the capacity is not valid
     */
    public HarddriveStorage(final long capacity) throws IllegalArgumentException {
        this("HarddriveStorage", capacity);
    }

    /**
     * Initializes the hard drive. The most common parameters such
     * as latency, average seek-time and maximum transfer rate are set.
     * The default values are used to simulate the "Maxtor DiamondMax 10 ATA" hard disk.
     */
    private void init() {
        setLatency(DEF_LATENCY_SECS);
        setAvgSeekTime(DEF_SEEK_TIME_SECS);
        setMaxTransferRate(DEF_MAX_TRANSF_RATE_MBITS_SEC);
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
    public FileStorage setMaxTransferRate(final double maxTransferRate) {
        if (maxTransferRate <= 0) {
            throw new IllegalArgumentException("Max transfer rate must be greater than zero.");
        }

        this.maxTransferRate = maxTransferRate;
        return this;
    }

    /**
     * Sets the average seek time of the storage.
     *
     * @param seekTime the average seek time (in seconds)
     * @return this instance
     */
    public HarddriveStorage setAvgSeekTime(final double seekTime) {
        this.avgSeekTime = MathUtil.nonNegative(seekTime, "seekTime");
        return this;
    }

    /// Get the seek time for a file with a defined size.
    /// Given a file size in MByte, this method returns a seek time for the file in seconds.
    /// If a [prng][#setPrng(ContinuousDistribution)] is set,
    /// it generates random delays for file seek time.
    ///
    /// @param fileSize the size of a file in MByte
    /// @return the seek time in seconds
    public double getSeekTime(final int fileSize) {
        if (fileSize > 0 && storage.getCapacity() != 0) {
            return prng.sample() + (fileSize / (double) storage.getCapacity());
        }

        return 0;
    }

    @Override
    public double getTransferTime(final int fileSize) {
        // It's ensured the maxTransferRate cannot be zero.
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

    @Override
    public String getUnit() {
        return storage.getUnit();
    }
}
