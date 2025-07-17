/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.resources;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.network.switches.Switch;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// An interface which defines the desired functionality of a storage system in a cloud environment
/// that performs operations on a file system, such as file inclusion, exclusion and renaming.
///
/// Classes that implement this interface should simulate the characteristics of different storage
/// systems by setting the capacity of the storage and the maximum transfer rate. The transfer rate
/// defines the time required to execute some common operations on the storage, e.g., storing,
/// retrieving and deleting a file.
///
/// @author Uros Cibej
/// @author Anthony Sulistio
/// @author Manoel Campos da Silva Filho
/// @link [Hard disk drive performance characteristics](https://en.wikipedia.org/wiki/Hard_disk_drive_performance_characteristics#Access_time)
public interface FileStorage extends Resource {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link FileStorage} objects.
     */
    FileStorage NULL = new FileStorageNull();

    Logger LOGGER = LoggerFactory.getLogger(HarddriveStorage.class.getSimpleName());

    /**
     * Default read latency of this storage in seconds.
     */
    double DEF_LATENCY_SECS = 0.00417;

    /**
     * Default average seek-time of the storage in seconds.
     */
    double DEF_SEEK_TIME_SECS = 0.009;

    /**
     * Default maximum transfer rate of this storage system in <b>Mega-bits/sec</b>,
     * i.e., the physical device reading speed.
     */
    int DEF_MAX_TRANSF_RATE_MBITS_SEC = 133 * 8;

    /**
     * Gets the maximum local transfer rate of the storage in <b>Mega-bits/sec</b>,
     * i.e., the physical device reading speed.
     *
     * @return the maximum transfer rate in Mega-bits/sec
     * @see #setMaxTransferRate(double)
     */
    double getMaxTransferRate();

    /**
     * Sets the maximum transfer rate of this storage system in <b>Mega-bits/sec</b>,
     * i.e., the physical device reading speed.
     *
     * <p>Despite disk transfer rate is usually defined in MBytes/sec,
     * it's being used Mbits/sec everywhere to avoid confusions,
     * since {@link Host}, {@link Vm}, {@link Switch}
     * and {@link SanStorage} use such a data unit.</p>
     *
     * @param maxTransferRate the maximum transfer rate in Mbits/sec
     * @throws IllegalArgumentException if the value is lower than 1
     * @return this instance
     */
    FileStorage setMaxTransferRate(double maxTransferRate);

    /**
     * Sets the read latency of this storage in seconds.
     *
     * @param latency the new latency in seconds
     * @throws IllegalArgumentException if the value is lower than 0
     * @return this instance
     */
    FileStorage setLatency(double latency);

    /**
     * @return the read latency of this storage in seconds.
     */
    double getLatency();

    /**
     * Gets the transfer time of a given file.
     *
     * @param fileSize the size of the file to compute the transfer time (in MByte)
     * @return the transfer time in seconds
     */
    double getTransferTime(int fileSize);
}
