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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/// A [Storage Area Network (SAN)](https://en.wikipedia.org/wiki/Storage_area_network)
/// composed of a set of hard disks connected in a LAN.
/// The capacity of individual disks is abstracted,
/// thus only the overall capacity of the SAN is considered.
///
/// **WARNING**: Effects of network contention are
/// not considered in the simulation. So, time for file transfer is underestimated in the presence of
/// a high network load.
///
/// @author Rodrigo N. Calheiros
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
/// TODO See the warning in class documentation.
public class SanStorage extends HarddriveStorage {
    public static final double FILE_NOT_FOUND = -1;

    /**
     * A storage just to control the amount of space previously allocated
     * to add reserved files. When the reserved files are effectively added
     * to the Hard Drive, the reserved space for the file is removed for
     * this attribute. The attribute is used to avoid adding a reserved file
     * that the space wasn't previously reserved, which results in
     * a wrong allocated space.
     *
     * @see #reserveSpace(int)
     * @see #addReservedFile(File)
     */
    private final SimpleStorage reservedStorage;

    /**
     * The bandwidth of the SAN network (in Megabits/s).
     */
    @Getter
    private double bandwidth;

    /**
     * The SAN's network latency (in seconds).
     */
    @Getter
    private double networkLatency;

    /** @see #getFileNameList() */
    private final List<String> fileNameList;

    /** @see #getFileList() */
    private final List<File> fileList;

    /**
     * Creates a SAN with a given capacity, latency, and bandwidth of the network connection.
     *
     * @param capacity       total storage capacity of the SAN (in MB)
     * @param bandwidth      network bandwidth (in Megabits/s)
     * @param networkLatency network latency (in seconds)
     * @throws IllegalArgumentException when capacity is not valid
     */
    public SanStorage(final long capacity, final double bandwidth, final double networkLatency) throws IllegalArgumentException {
        this("SanStorage" + capacity, capacity, bandwidth, networkLatency);
    }

    /**
     * Creates a SAN with a given capacity, latency, and bandwidth of the network connection
     * and with a specific name.
     *
     * @param name           the name of the new storage device
     * @param capacity       Storage device capacity (in MB)
     * @param bandwidth      Network bandwidth (in Megabits/s)
     * @param networkLatency Network latency (in seconds)
     * @throws IllegalArgumentException when the name or the capacity are not valid
     */
    public SanStorage(final String name, final long capacity, final double bandwidth, final double networkLatency) {
        super(name, capacity);
        this.setBandwidth(bandwidth);
        this.setNetworkLatency(networkLatency);

        this.fileList = new ArrayList<>();
        this.fileNameList = new ArrayList<>();
        this.reservedStorage = new SimpleStorage(capacity);
    }

    /// Adds a file for which the space has already been reserved.
    /// The time taken (in seconds) for adding the specified file can also be found using [File#getTransactionTime()].
    ///
    /// @param file the file to be added
    /// @return the time (in seconds) required for adding the file
    public double addReservedFile(@NonNull final File file) {
        if (!reservedStorage.isResourceAmountBeingUsed(file.getSize())) {
            throw new IllegalStateException("The file size wasn't previously reserved in order to add a reserved file.");
        }

        final long fileSize = file.getSize();
        getStorage().deallocateResource(fileSize);
        reservedStorage.deallocateResource(fileSize);
        final double time = addFile(file);

        // if the file addition fails, then set the current size back to its old value
        if (time == 0.0) {
            getStorage().allocateResource(fileSize);
            return time;
        }

        return time + getTransferTime(file);
    }

    /// Adds a set of files to the storage. The time taken (in seconds) for adding each file can also
    /// be found using [File#getTransactionTime()].
    ///
    /// @param list the files to be added
    /// @return the time taken (in seconds) for adding the specified file;
    ///         or zero if the file is invalid or there isn't available storage space.
    public double addFile(@NonNull final List<File> list) {
        if (list.isEmpty()) {
            LOGGER.debug("{}.addFile(): File list is empty.", getName());
            return 0.0;
        }

        return list.stream().mapToDouble(this::addFile).sum();
    }

    /// Adds a file to the storage. The time taken (in seconds) for adding the specified file can
    /// also be found using [File#getTransactionTime()].
    ///
    /// @param file the file to be added
    /// @return the time taken (in seconds) for adding the specified file;
    ///         or zero if there isn't available storage space.
    public double addFile(final File file) {
        double time = 0.0;
        File.validate(file);

        if (!getStorage().isAmountAvailable(file.getSize())) {
            LOGGER.error("{}.addFile(): Not enough space to store {}", getName(), file.getName());
        } else if (!contains(file.getName())) { // check if the same file name is already taken
            fileList.add(file);                 // add the file into the HD
            fileNameList.add(file.getName());
            getStorage().allocateResource(file.getSize()); // increment the current HD space
            time = getTotalFileAddTime(file);
            file.setTransactionTime(time);
        }

        if (time == 0) {
            return time;
        }

        return time + getTransferTime(file);
    }

    /**
     * Sets the bandwidth of the SAN network.
     *
     * @param bandwidth the bandwidth to set (in Megabits/s)
     * @throws IllegalArgumentException when the bandwidth is lower or equal to zero
     */
    public final void setBandwidth(final double bandwidth) {
        if (bandwidth <= 0) {
            throw new IllegalArgumentException("Bandwidth must be higher than zero");
        }

        this.bandwidth = bandwidth;
    }

    /**
     * Sets the latency of the SAN network.
     *
     * @param networkLatency the latency to set (in seconds)
     * @throws IllegalArgumentException when the latency is lower or equal to zero
     */
    public final void setNetworkLatency(final double networkLatency) {
        if (networkLatency <= 0) {
            throw new IllegalArgumentException("Latency must be higher than zero");
        }

        this.networkLatency = networkLatency;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the number of files stored on this device.
     */
    public int getNumStoredFile() {
        return fileList.size();
    }

    /**
     * Makes reservation of space on the device to store a file.
     *
     * @param fileSize the size to be reserved (in MByte)
     * @return true if reservation succeeded, false otherwise
     */
    public boolean reserveSpace(final int fileSize) {
        if (getStorage().allocateResource(fileSize)) {
            reservedStorage.allocateResource(fileSize);
            return true;
        }

        return false;
    }

    /**
     * Checks whether there is enough space on the storage for a certain file,
     * according to its size.
     *
     * @param fileSize size of the file intended to be stored on the device (in MB)
     * @return true if there is enough space available, false otherwise
     */
    public boolean hasPotentialAvailableSpace(final long fileSize) {
        if (fileSize <= 0) {
            return false;
        }

        if (getStorage().isAmountAvailable(fileSize)) {
            return true;
        }

        return getDeletedFilesTotalSize() > fileSize;
    }

    /**
     * Checks if the storage device has a specific file.
     *
     * @param fileName the name of the file to check if it's inside this storage device
     * @return true if the storage device has the file, false otherwise.
     */
    public boolean hasFile(final String fileName) {
        return getFile(fileName).isPresent();
    }

    private int getDeletedFilesTotalSize() {
        return fileList.stream().filter(File::isDeleted).mapToInt(File::getSize).sum();
    }

    /// Gets the file with the specified name. The time taken (in seconds) for getting the specified
    /// file can also be found using [File#getTransactionTime()].
    ///
    /// @param fileName the name of the file to get
    /// @return an [Optional] containing the file if it was found; or an empty Optional otherwise
    public Optional<File> getFile(final String fileName) {
        File.validate(fileName);

        int size = 0;

        // find the file in the disk
        for (final File currentFile : fileList) {
            size += currentFile.getSize();
            if (currentFile.getName().equals(fileName)) {
                // if the file is found, then determine the time taken to get it
                final double seekTime = getSeekTime(size);
                final double transferTime = getTransferTime(currentFile.getSize());

                // total time for this operation
                currentFile.setTransactionTime(seekTime + transferTime);
                return Optional.of(currentFile);
            }
        }

        return Optional.empty();
    }

    /**
     * @return a <b>read-only</b> list with the names of all files stored on the device.
     */
    public List<String> getFileNameList() {
        return Collections.unmodifiableList(fileNameList);
    }

    /**
     * @return a <b>read-only</b> list with all files stored on the device.
     */
    public List<File> getFileList() {
        return Collections.unmodifiableList(fileList);
    }

    /**
     * Gets the transfer time of a given file.
     *
     * @param file the file to compute the transfer time (where its size is defined in MB)
     * @return the transfer time (in seconds)
     */
    public double getTransferTime(@NonNull final File file) {
        return getTransferTime(file.getSize());
    }

    /**
     * {@inheritDoc}
     * The network latency is added to the transfer time.
     *
     * @param fileSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getTransferTime(final int fileSize) {
        // Gets the time to read from the local storage device (such as an HD or SSD).
        final double storageDeviceReadTime = super.getTransferTime(fileSize);

        // Gets the time to transfer the file through the network
        final double networkTransferTime = getTransferTime(fileSize, bandwidth);

        return storageDeviceReadTime + networkTransferTime + getNetworkLatency();
    }

    /// Gets the transfer time for a given file.
    ///
    /// @param fileName the name of the file to compute the transfer time (where its size is defined in MB)
    /// @return the transfer time (in seconds);
    ///         or [#FILE_NOT_FOUND] if the file was not found in this storage device
    public double getTransferTime(final String fileName) {
        return getFile(fileName).map(this::getTransferTime).orElse(FILE_NOT_FOUND);
    }

    /**
     * Gets the total time to add a file to the storage.
     *
     * @param file the file to compute the total addition time (where its size is defined in MB)
     * @return the total file addition time (in seconds)
     */
    private double getTotalFileAddTime(@NonNull final File file) {
        final double seekTime = getSeekTime(file.getSize());
        final double transferTime = getTransferTime(file.getSize());
        return seekTime + transferTime;
    }

    /// Removes a file from the storage. The time taken (in seconds) for deleting the specified file
    /// can be found using [File#getTransactionTime()].
    ///
    /// @param fileName the name of the file to be removed
    /// @return an [Optional] containing the deleted file if it is found;
    ///         or an empty Optional otherwise.
    public Optional<File> deleteFile(final String fileName) {
        File.validate(fileName);

        final int index = fileNameList.indexOf(fileName);
        if (index != -1) {
            final File file = fileList.get(index);
            final double result = deleteFile(file);
            file.setTransactionTime(result);
            return Optional.of(file);
        }

        return Optional.empty();
    }

    /// Removes a file from the storage. The time taken (in seconds) for deleting the specified file
    /// can also be found using [File#getTransactionTime()].
    ///
    /// @param file the file to be removed
    /// @return the time taken (in seconds) for deleting the specified file
    public double deleteFile(final File file) {
        File.validate(file);

        double time = 0.0;
        if (contains(file)) {
            fileList.remove(file);
            fileNameList.remove(file.getName());
            getStorage().deallocateResource(file.getSize());
            time = this.getTotalFileAddTime(file);
            file.setTransactionTime(time);
        }

        if(time == 0) {
            return time;
        }

        return time + getTransferTime(file);
    }

    /// Renames a file on the storage. The time taken (in seconds) for renaming the specified file
    /// can also be found using [File#getTransactionTime()].
    ///
    /// @param file    the file to be renamed
    /// @param newName the new name of the file
    /// @return true if the renaming succeeded, false otherwise
    public boolean renameFile(@NonNull final File file, final String newName) {
        //check whether the new filename is conflicting with existing ones or not
        if (contains(newName)) {
            return false;
        }

        final String oldName = file.getName();
        // Search for the file and renames if it's found
        return getFile(oldName)
            .map(fileFound -> {
                fileFound.setName(newName);
                fileFound.setTransactionTime(0);
                fileNameList.remove(oldName);
                fileNameList.add(newName);
                return fileFound;
            }).isPresent();
    }

    /**
     * Checks whether a file is saved in the storage or not.
     *
     * @param file the file we are looking for
     * @return true if the file is in the storage, false otherwise
     */
    public boolean contains(@NonNull final File file) {
        return contains(file.getName());
    }

    /**
     * Checks whether a file is saved in the storage or not.
     *
     * @param fileName the name of the file we are looking for
     * @return true if the file is in the storage, false otherwise
     */
    public boolean contains(@NonNull final String fileName) {
        return fileNameList.contains(fileName);
    }
}
