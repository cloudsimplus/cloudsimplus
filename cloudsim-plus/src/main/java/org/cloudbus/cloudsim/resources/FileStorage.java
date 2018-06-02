/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/**
 * An interface which defines the desired functionality of a storage system in a Data Cloud
 * that performs operations on a file system, such as file inclusion, exclusion
 * and renaming.
 * Classes that implement this interface should simulate the characteristics of different storage
 * systems by setting the capacity of the storage and the maximum transfer rate. The transfer rate
 * defines the time required to execute some common operations on the storage, e.g. storing a file,
 * getting a file and deleting a file.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 */
public interface FileStorage extends Resource {
    /**
     *
     * @return the name of the storage device
     */
    String getName();

    /**
     * Gets the maximum transfer rate of the storage in <b>MBytes/sec</b>.
     *
     *
     * <p>Different from bandwidth in {@link Host}, {@link Vm} and {@link Switch},
     * the FileStorage max transfer rate is defined in MBytes/sec instead of
     * MBits/sec. That is the usual unit used for storage transfer speed.</p>
     *
     * @return the maximum transfer rate in <b>MBytes/sec</b>
     */
    double getMaxTransferRate();

    /**
     * Sets the latency of this hard drive in seconds.
     *
     * @param latency the new latency in seconds
     * @throws IllegalArgumentException if the value is lower than 0
     */
    void setLatency(double latency);

    /**
     * Gets the latency of this hard drive in seconds.
     *
     * @return the latency in seconds
     */
    double getLatency();

    /**
     * Sets the maximum transfer rate of this storage system in <b>MBytes/sec</b>.
     *
     * @param maxTransferRate the maximum transfer rate in <b>MBytes/sec</b>
     * @throws IllegalArgumentException if the value is lower than 1
     */
    void setMaxTransferRate(int maxTransferRate);

    /**
     * Gets the number of files stored on this device.
     *
     * @return the number of stored files
     */
    int getNumStoredFile();

    /**
     * Makes reservation of space on the storage to store a file.
     *
     * @param fileSize the size to be reserved (in MByte)
     * @return <tt>true</tt> if reservation succeeded, <tt>false</tt> otherwise
     */
    boolean reserveSpace(int fileSize);

    /**
     * Adds a file for which the space has already been reserved. The time taken (in seconds) for
     * adding the specified file can also be found using
     * {@link File#getTransactionTime()}.
     *
     * @param file the file to be added
     * @return the time (in seconds) required to add the file
     */
    double addReservedFile(File file);

    /**
     * Gets the file with the specified name. The time taken (in seconds) for getting the specified
     * file can also be found using {@link File#getTransactionTime()}.
     *
     * @param fileName the name of the needed file
     * @return the file with the specified filename; null if not found
     */
    File getFile(String fileName);

    /**
     * Gets a <b>read-only</b> list with the names of all files stored on the device.
     *
     * @return a List of file names
     */
    List<String> getFileNameList();

    /**
     * Gets a <b>read-only</b> list with all files stored on the device.
     *
     * @return a List of files
     */
    List<File> getFileList();

    /**
     * Gets the transfer time of a given file.
     *
     * @param file the file to compute the transfer time where its size is defined in MByte
     * @return the transfer time in seconds
     */
    double getTransferTime(File file);

    /**
     * Gets the transfer time of a given file.
     *
     * @param fileSize the size of the file to compute the transfer time (in MByte)
     * @return the transfer time in seconds
     */
    double getTransferTime(int fileSize);

    /**
     * Adds a file to the storage. The time taken (in seconds) for adding the specified file can
     * also be found using {@link File#getTransactionTime()}.
     *
     * @param file the file to be added
     * @return the time taken (in seconds) for adding the specified file or zero if
     * there isn't available storage space.
     */
    double addFile(File file);

    /**
     * Adds a set of files to the storage. The time taken (in seconds) for adding each file can also
     * be found using {@link File#getTransactionTime()}.
     *
     * @param list the files to be added
     * @return the time taken (in seconds) for adding the specified file or zero if the
     * file is invalid or there isn't available storage space.
     */
    double addFile(List<File> list);

    /**
     * Removes a file from the storage. The time taken (in seconds) for deleting the specified file
     * can be found using {@link File#getTransactionTime()}.
     *
     * @param fileName the name of the file to be removed
     * @return the deleted file.
     */
    File deleteFile(String fileName);

    /**
     * Removes a file from the storage. The time taken (in seconds) for deleting the specified file
     * can also be found using {@link File#getTransactionTime()}.
     *
     * @param file the file to be removed
     * @return the time taken (in seconds) for deleting the specified file
     */
    double deleteFile(File file);

    /**
     * Checks whether a file exists in the storage or not.
     *
     * @param fileName the name of the file we are looking for
     * @return <tt>true</tt> if the file is in the storage, <tt>false</tt> otherwise
     */
    boolean contains(String fileName);

    /**
     * Checks whether a file is stored in the storage or not.
     *
     * @param file the file we are looking for
     * @return <tt>true</tt> if the file is in the storage, <tt>false</tt> otherwise
     */
    boolean contains(File file);

    /**
     * Renames a file on the storage. The time taken (in seconds) for renaming the specified file
     * can also be found using {@link File#getTransactionTime()}.
     *
     * @param file the file we would like to rename
     * @param newName the new name of the file
     * @return <tt>true</tt> if the renaming succeeded, <tt>false</tt> otherwise
     */
    boolean renameFile(File file, String newName);

    /**
     * Checks whether there is enough space on the storage for a certain file
     *
     * @param fileSize size of the file intended to be stored on the device (in MByte)
     * @return <tt>true</tt> if enough space available, <tt>false</tt> otherwise
    */
    boolean hasPotentialAvailableSpace(int fileSize);
}
