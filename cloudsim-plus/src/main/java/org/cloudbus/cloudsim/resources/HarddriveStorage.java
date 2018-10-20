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
import org.cloudbus.cloudsim.util.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An implementation of a Hard Drive (HD) storage device. It simulates the behavior of a typical hard drive.
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
    public static final double DEF_LATENCY_SECS = 0.00417;
    public static final double DEF_SEEK_TIME_SECS = 0.009;
    private static final int   DEF_MAX_TRANSFER_RATE_MBPS = 133*8;

    private static final Logger LOGGER = LoggerFactory.getLogger(HarddriveStorage.class.getSimpleName());

    /** The internal storage that just manages
     * the HD capacity and used space.
     * The {@link HarddriveStorage} (HD) does not extends such class
     * to avoid its capacity and available amount of space
     * to be changed indiscriminately.
     * The available space is update according to files added or removed
     * from the HD.
     */
    private final Storage storage;

    /**
     * An storage just to control the amount of space previously allocated
     * to add reserved files. When the reserved files are effectively added
     * to the Hard Drive, the reserved space for the file is remove for
     * this attribute. The attribute is used to avoid adding a reserved file
     * that the space wasn't previously reserved, what results in
     * wrong allocated space.
     * @see #reserveSpace(int)
     * @see #addReservedFile(File)
     */
    private final Storage reservedStorage;

    /** @see #getFileNameList()   */
    private List<String> fileNameList;

    /** A list with all files stored on the hard drive. */
    private List<File> fileList;

    /** @see #getName()  */
    private final String name;

    /** A number generator required to randomize the seek time. */
    private ContinuousDistribution gen;

    /** @see #getMaxTransferRate()  */
    private double maxTransferRate;

    /** @see #getLatency()  */
    private double latency;

    /** @see #getAvgSeekTime()  */
    private double avgSeekTime;

    /**
     * Creates a new hard drive storage with a given name and capacity.
     *
     * @param name the name of the new hard drive storage
     * @param capacity the capacity in MByte
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public HarddriveStorage(final String name, final long capacity) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Storage name cannot be empty.");
        }

        this.fileList = new ArrayList<>();
        this.fileNameList = new ArrayList<>();
        this.storage = new Storage(capacity);
        this.reservedStorage = new Storage(capacity);
        this.name = name;

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
        setMaxTransferRate(DEF_MAX_TRANSFER_RATE_MBPS);
    }

    @Override
    public int getNumStoredFile() {
        return fileList.size();
    }

    @Override
    public boolean reserveSpace(int fileSize) {
        if(storage.allocateResource((long)fileSize)){
            reservedStorage.allocateResource((long)fileSize);
            return true;
        }

        return false;
    }

    @Override
    public double addReservedFile(final File file) {
        Objects.requireNonNull(file);

        if(!reservedStorage.isResourceAmountBeingUsed((long)file.getSize())){
            throw new IllegalStateException("The file size wasn't previously reserved in order to add a reserved file.");
        }

        final long fileSize = file.getSize();
        storage.deallocateResource(fileSize);
        reservedStorage.deallocateResource(fileSize);
        final double result = addFile(file);

        // if add file fails, then set the current size back to its old value
        if (result == 0.0) {
            storage.allocateResource(fileSize);
        }

        return result;
    }

    @Override
    public boolean hasPotentialAvailableSpace(final int fileSize) {
        if (fileSize <= 0) {
            return false;
        }

        if (storage.isAmountAvailable((long)fileSize)) {
            return true;
        }

        return getDeletedFilesTotalSize() > fileSize;
    }

    @Override
    public boolean hasFile(final String fileName) {
        return getFile(fileName) != null;
    }

    private int getDeletedFilesTotalSize() {
        return fileList.stream().filter(File::isDeleted).mapToInt(File::getSize).sum();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setLatency(final double latency) {
        if (latency < 0) {
            throw new IllegalArgumentException("Latency must be greater than zero.");
        }

        this.latency = latency;
    }

    @Override
    public double getLatency() {
        return latency;
    }

    @Override
    public void setMaxTransferRate(final double maxTransferRate) {
        if (maxTransferRate <= 0) {
            throw new IllegalArgumentException("Max transfer rate must be greater than zero.");
        }

        this.maxTransferRate = maxTransferRate;
    }

    @Override
    public double getMaxTransferRate() {
        return maxTransferRate;
    }

    /**
     * Sets the average seek time of the storage in seconds.
     *
     * @param seekTime the average seek time in seconds
     * @return <tt>true</tt> if the values is greater than zero and was set successfully,
     * <tt>false</tt> otherwise
     */
    public boolean setAvgSeekTime(final double seekTime) {
        return setAvgSeekTime(seekTime, null);
    }

    /**
     * Sets the average seek time and a new generator of seek times in seconds. The generator
     * determines a randomized seek time.
     *
     * @param seekTime the average seek time in seconds
     * @param gen the ContinuousGenerator which generates seek times
     * @return <tt>true</tt> if the values is greater than zero and was set successfully,
     * <tt>false</tt> otherwise
     */
    public boolean setAvgSeekTime(final double seekTime, final ContinuousDistribution gen) {
        if (seekTime <= 0.0) {
            return false;
        }

        avgSeekTime = seekTime;
        this.gen = gen;
        return true;
    }

    /**
     * Gets the average seek time of the hard drive in seconds.
     *
     * @return the average seek time in seconds
     */
    public double getAvgSeekTime() {
        return avgSeekTime;
    }

    @Override
    public File getFile(final String fileName) {
        if (!File.isValid(fileName)) {
            LOGGER.warn("{}.getFile(): Invalid file name {}.", name, fileName);
            return null;
        }

        int size = 0;

        // find the file in the disk
        for(final File currentFile: fileList) {
            size += currentFile.getSize();
            if (currentFile.getName().equals(fileName)) {
                // if the file is found, then determine the time taken to get it
                final double seekTime = getSeekTime(size);
                final double transferTime = getTransferTime(currentFile.getSize());

                // total time for this operation
                currentFile.setTransactionTime(seekTime + transferTime);
                return currentFile;
            }
        }

        return null;
    }

    @Override
    public List<String> getFileNameList() {
        return Collections.unmodifiableList(fileNameList);
    }

    @Override
    public List<File> getFileList(){
        return Collections.unmodifiableList(fileList);
    }

    /**
     * Get the seek time for a file with the defined size. Given a file size in MByte, this method
     * returns a seek time for the file in seconds.
     *
     * @param fileSize the size of a file in MByte
     * @return the seek time in seconds
     */
    private double getSeekTime(final int fileSize) {
        double result = 0;

        if (gen != null) {
            result += gen.sample();
        }

        if (fileSize > 0 && storage.getCapacity() != 0) {
            result += fileSize / (double)storage.getCapacity();
        }

        return result;
    }

    @Override
    public double getTransferTime(final String fileName) {
        final File file = getFile(fileName);
        if(file == null){
            return FILE_NOT_FOUND;
        }

        return getTransferTime(file);
    }

    @Override
    public double getTransferTime(final File file) {
        return getTransferTime(file.getSize());
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
     * @param speed the speed (in MBits/sec) to compute the time to transfer the file
     * @return the transfer time in seconds
     */
    protected final double getTransferTime(final int fileSize, final double speed){
        return Conversion.bytesToBits(fileSize)/speed;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>First, the method checks if there is enough space on the storage,
     * then it checks if the file with the same name is already taken to avoid duplicate filenames.
     *
     * @param file {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double addFile(final File file) {
        double result = 0.0;
        if (!File.isValid(file)) {
            LOGGER.warn("{}.addFile(): Invalid file {}", name, file);
            return result;
        }

       // check the capacity
        if (!storage.isAmountAvailable((long)file.getSize())) {
            LOGGER.error("{}.addFile(): Not enough space to store {}", name, file.getName());
            return result;
        }

        // check if the same file name is alredy taken
        if (!contains(file.getName())) {
            fileList.add(file);               // add the file into the HD
            fileNameList.add(file.getName());     // add the name to the name list
            storage.allocateResource((long)file.getSize());    // increment the current HD space
            result = getTotalFileAddTime(file);
            file.setTransactionTime(result);
        }
        return result;
    }

    /**
     * Gets the total time to add a file to the storage.
     * @param file the file to compute the total addition time
     * @return
     */
    private double getTotalFileAddTime(final File file) {
        final double seekTime = getSeekTime(file.getSize());
        final double transferTime = getTransferTime(file.getSize());
        return seekTime + transferTime;
    }

    @Override
    public double addFile(final List<File> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            LOGGER.debug("{}.addFile(): File list is empty.", getName());
            return 0.0;
        }

        return list.stream().mapToDouble(this::addFile).sum();
    }

    @Override
    public File deleteFile(final String fileName) {
        if (!File.isValid(fileName)) {
            return null;
        }

        final int i = fileNameList.indexOf(fileName);
        if(i != -1){
            final File file = fileList.get(i);
            final double result = deleteFile(file);
            file.setTransactionTime(result);
            return file;
        }

        return null;
    }

    @Override
    public double deleteFile(final File file) {
        // check if the file is valid or not
        if (!File.isValid(file)) {
            return 0.0;
        }

        // check if the file is in the storage
        if (contains(file)) {
            fileList.remove(file);            // remove the file HD
            fileNameList.remove(file.getName());  // remove the name from name list
            storage.deallocateResource((long)file.getSize());    // decrement the current HD space
            final double result = getTotalFileAddTime(file);  // total time
            file.setTransactionTime(result);
            return result;
        }

        return 0.0;
    }

    @Override
    public boolean contains(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            LOGGER.warn("{}.contains(): Invalid file name {}", name, fileName);
            return false;
        }

        return fileNameList.contains(fileName);
    }

    @Override
    public boolean contains(final File file) {
        if (!File.isValid(file)) {
            return false;
        }

        return contains(file.getName());
    }

    @Override
    public boolean renameFile(final File file, final String newName) {
        //check whether the new filename is conflicting with existing ones or not
        if (contains(newName)) {
            return false;
        }

        final String oldName = file.getName();
        // replace the file name in the file (physical) list
        final File renamedFile = getFile(oldName);
        if (renamedFile != null) {
            renamedFile.setName(newName);
            renamedFile.setTransactionTime(0);
            fileNameList.remove(oldName);
            fileNameList.add(newName);
            return true;
        }

        return false;
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
        return isAmountAvailable((long)amountToCheck);
    }

    @Override
    public boolean isFull() {
        return storage.isFull();
    }
}
