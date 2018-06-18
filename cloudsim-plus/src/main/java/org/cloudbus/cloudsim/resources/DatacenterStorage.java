package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.DataCloudTags;

/**
 * Implements the storage logic for a Datacenter. It keeps a list of
 * storage devices as well as all basic storage related operations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Abderrahman Lahiaouni
 * @since CloudSim Plus 1.0
 */
public class DatacenterStorage {

	/** @see #getStorageList() */
    private List<FileStorage> storageList;

    /** @see #getDatacenter() */
	private Datacenter datacenter;

	public DatacenterStorage(){
    	this.storageList = new ArrayList<>();
    }

    /**
     * Checks whether the storageList has the given file.
     *
     * @param file a file to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean contains(final File file) {
        Objects.requireNonNull(file);
        return contains(file.getName());
    }

    /**
     * Checks whether the storageList has the given file.
     *
     * @param fileName a file name to be searched
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean contains(final String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        return storageList.stream().anyMatch(storage -> storage.contains(fileName));
    }

    /**
     * Gets the list of storage devices of the Datacenter
     * @return
     */
    public List<FileStorage> getStorageList() {
        return Collections.unmodifiableList(storageList);
    }

    /**
     * Sets the list of storage devices of the Datacenter.
     *
     * @param storageList the new storage list
     * @return
     */
    public final DatacenterStorage setStorageList(final List<FileStorage> storageList) {
        Objects.requireNonNull(storageList);
        this.storageList = storageList;
        setAllFilesOfAllStoragesToThisDatacenter();

        return this;
    }

    /**
     * Assigns all files of all storage devices to this Datacenter.
     */
    public void setAllFilesOfAllStoragesToThisDatacenter() {
        storageList.stream()
                .map(FileStorage::getFileList)
                .flatMap(List::stream)
                .forEach(file -> file.setDatacenter(this.getDatacenter()));
    }

    public Datacenter getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(final Datacenter datacenter) {
		this.datacenter = datacenter;
	}

	/**
     * Predict the total time to transfer a list of files.
     *
     * @param requiredFiles the files to be transferred
     * @return the total predicted time to transfer the files
     */
    public double predictFileTransferTime(final List<String> requiredFiles) {
        double totalTime = 0.0;

        for (final String fileName: requiredFiles) {
            totalTime += Math.max(timeToTransferFileFromStorage(fileName), 0);
        }

        return totalTime;
    }

    /**
     * Try to get a file from a storage device in the {@link #storageList}
     * and computes the time to transfer it from that device.
     *
     * @param fileName the name of the file to try finding and get the transfer time
     * @return the time to transfer the file or {@link FileStorage#FILE_NOT_FOUND} if not found.
     */
    private double timeToTransferFileFromStorage(final String fileName) {
        for (final FileStorage storage: getStorageList()) {
            final double transferTime = storage.getTransferTime(fileName);
            if (transferTime != FileStorage.FILE_NOT_FOUND) {
                return transferTime;
            }
        }

        return FileStorage.FILE_NOT_FOUND;
    }

    /**
     * Adds a file to the first storage device that has enough capacity
     * @param file the file to add
     * @return a tag from {@link DataCloudTags} informing the result of the operation
     */
    public int addFile(final File file) {
        Objects.requireNonNull(file);

        if (contains(file.getName())) {
            return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
        }

        // check storage space first
        if (getStorageList().isEmpty()) {
            return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
        }

        for (final FileStorage storage : getStorageList()) {
            if (storage.isResourceAmountAvailable((long) file.getSize())) {
                storage.addFile(file);
                return DataCloudTags.FILE_ADD_SUCCESSFUL;
            }
        }

        return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
    }
}
