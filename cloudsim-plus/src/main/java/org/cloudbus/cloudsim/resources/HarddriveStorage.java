/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.util.Log;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

/**
 * An implementation of a Hard Drive (HD) storage device. It simulates the behavior of a typical hard drive.
 * The default values for this storage are those of a "Maxtor DiamonMax 10 ATA" hard disk with the
 * following parameters:
 * <ul>
 *   <li>latency = 4.17 ms</li>
 *   <li>avg seek time = 9 m/s</li>
 *   <li>max transfer rate = 133 MEGABYTE/sec</li>
 * </ul>
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
public class HarddriveStorage implements FileStorage {
    /** The internal storage that just manages
     * the HD capacity and used space.
     * The {@link HarddriveStorage} (HD) does not extends such class
     * to avoid its capacity and available amount of space
     * to be changed indiscriminately.
     * The available space is update according to files added or removed
     * from the HD.
     */
    private Storage storage;

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
    private Storage reservedStorage;

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
        this.storage = new Storage(capacity);
        this.reservedStorage = new Storage(capacity);
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("HarddriveStorage(): Error - invalid storage name.");
        }

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
     * as latency, average seek time and maximum transfer rate are set. The default values are set
     * to simulate the "Maxtor DiamonMax 10 ATA" hard disk. Furthermore, the necessary lists are
     * created.
     */
    private void init() {
        fileList = new ArrayList<>();
        fileNameList = new ArrayList<>();
        gen = null;

        latency = 0.00417;     // 4.17 ms in seconds
        avgSeekTime = 0.009;   // 9 ms
        maxTransferRate = 133; // in MEGABYTE/sec
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
    public double addReservedFile(File file) {
        if (Objects.isNull(file)) {
            return 0;
        }

        if(!reservedStorage.isResourceAmountBeingUsed((long)file.getSize())){
            throw new RuntimeException("The file size wasn't previously reserved in order to add a reserved file.");
        }

        final long fileSize = file.getSize();
        storage.deallocateResource(fileSize);
        reservedStorage.deallocateResource(fileSize);
        double result = addFile(file);

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

        if (storage.isResourceAmountAvailable((long)fileSize)) {
            return true;
        }

        return getDeletedFilesTotalSize() > fileSize;
    }

    private int getDeletedFilesTotalSize() {
        return fileList.stream().filter(File::isDeleted).mapToInt(File::getSize).sum();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the latency of this hard drive in seconds.
     *
     * @param latency the new latency in seconds
     * @return <tt>true</tt> if the setting succeeded, <tt>false</tt> otherwise
     */
    public boolean setLatency(final double latency) {
        if (latency < 0) {
            return false;
        }

        this.latency = latency;
        return true;
    }

    /**
     * Gets the latency of this hard drive in seconds.
     *
     * @return the latency in seconds
     */
    public double getLatency() {
        return latency;
    }

    @Override
    public boolean setMaxTransferRate(final int rate) {
        if (rate <= 0) {
            return false;
        }

        maxTransferRate = rate;
        return true;
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
            Log.printConcatLine(name, ".getFile(): Warning - invalid " + "file name.");
            return null;
        }

        int size = 0;

        // find the file in the disk
        for(File currentFile: fileList) {
            size += currentFile.getSize();
            if (currentFile.getName().equals(fileName)) {
                // if the file is found, then determine the time taken to get it
                final double seekTime = getSeekTime(size);
                double transferTime = getTransferTime(currentFile.getSize());

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
     * Get the seek time for a file with the defined size. Given a file size in MEGABYTE, this method
     * returns a seek time for the file in seconds.
     *
     * @param fileSize the size of a file in MEGABYTE
     * @return the seek time in seconds
     */
    private double getSeekTime(final int fileSize) {
        double result = 0;

        if (!Objects.isNull(gen)) {
            result += gen.sample();
        }

        if (fileSize > 0 && storage.getCapacity() != 0) {
            result += (fileSize / (double)storage.getCapacity());
        }

        return result;
    }

    /**
     * Gets the transfer time of a given file.
     *
     * @param fileSize the size of the transferred file
     * @return the transfer time in seconds
     */
    private double getTransferTime(final int fileSize) {
        double result = 0;
        if (fileSize > 0 && storage.getCapacity() != 0) {
            result = (fileSize * maxTransferRate) / (double)storage.getCapacity();
        }

        return result;
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
            Log.printConcatLine(name, ".addFile(): Invalid file ", file);
            return result;
        }

       // check the capacity
        if (!storage.isResourceAmountAvailable((long)file.getSize())) {
            Log.printConcatLine(name, ".addFile(): Warning - not enough space to store ", file.getName());
            return result;
        }

        // check if the same file name is alredy taken
        if (!contains(file.getName())) {
            final double seekTime = getSeekTime(file.getSize());
            final double transferTime = getTransferTime(file.getSize());

            fileList.add(file);               // add the file into the HD
            fileNameList.add(file.getName());     // add the name to the name list
            storage.allocateResource((long)file.getSize());    // increment the current HD space
            result = seekTime + transferTime;  // add total time
            file.setTransactionTime(result);
        }
        return result;
    }

    @Override
    public double addFile(final List<File> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            Log.printConcatLine(getName(), ".addFile(): Warning - list is empty.");
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
            double result = deleteFile(file);
            file.setTransactionTime(result);
            return file;
        }

        return null;
    }

    @Override
    public double deleteFile(final File file) {
        double result = 0.0;
        // check if the file is valid or not
        if (!File.isValid(file)) {
            return result;
        }
        double seekTime = getSeekTime(file.getSize());
        double transferTime = getTransferTime(file.getSize());

        // check if the file is in the storage
        if (contains(file)) {
            fileList.remove(file);            // remove the file HD
            fileNameList.remove(file.getName());  // remove the name from name list
            storage.deallocateResource((long)file.getSize());    // decrement the current HD space
            result = seekTime + transferTime;  // total time
            file.setTransactionTime(result);
        }
        return result;
    }

    @Override
    public boolean contains(final String fileName) {
        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            Log.printConcatLine(name, ".contains(): Warning - invalid file name");
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
        if (!Objects.isNull(renamedFile)) {
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
    public boolean isResourceAmountAvailable(long amountToCheck) {
        return storage.isResourceAmountAvailable(amountToCheck);
    }

    @Override
    public boolean isResourceAmountAvailable(double amountToCheck) {
        return isResourceAmountAvailable((long)amountToCheck);
    }

    @Override
    public boolean isFull() {
        return storage.isFull();
    }
}
