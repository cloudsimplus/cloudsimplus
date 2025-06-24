package org.cloudsimplus.resources;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.util.DataCloudTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Implements the storage logic for a [Datacenter]. It keeps a list of
/// storage devices ([Disk Array](https://en.wikipedia.org/wiki/Disk_array)),
/// as well as all basic storage related operations.
/// This disk array can be, for instance, a list of [HarddriveStorage]
/// or [SanStorage].
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Abderrahman Lahiaouni
/// @since CloudSim Plus 2.3.5
public class DatacenterStorage {

	/** @see #getStorageList() */
    private List<SanStorage> storageList;

    @Getter @Setter
	private Datacenter datacenter;

    /// Creates a DatacenterStorage with an empty [storage list][#getStorageList()].
    public DatacenterStorage(){
    	this(new ArrayList<>());
    }

    /// Creates a DatacenterStorage with a given [storage list][#getStorageList()].
    /// @param storageList the storage list to set
    public DatacenterStorage(final List<SanStorage> storageList){
    	this.setStorageList(storageList);
    }

    /**
     * Checks whether the storageList has the given file.
     *
     * @param file a file to be searched
     * @return true if successful, false otherwise
     */
    public boolean contains(@NonNull final File file) {
        return contains(file.getName());
    }

    /**
     * Checks whether the storageList has the given file.
     *
     * @param fileName a file name to be searched
     * @return true if successful, false otherwise
     */
    public boolean contains(final String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }

        return storageList.stream().anyMatch(storage -> storage.contains(fileName));
    }

    /// @return the list of storage devices from the Datacenter,
    /// which is like a [Disk Array](https://en.wikipedia.org/wiki/Disk_array).
    public List<SanStorage> getStorageList() {
        return Collections.unmodifiableList(storageList);
    }

    /// Sets the list of storage devices from the Datacenter,
    /// which is like a [Disk Array](https://en.wikipedia.org/wiki/Disk_array).
    ///
    /// @param storageList the new storage list
    /// @return this instance
    public final DatacenterStorage setStorageList(@NonNull final List<SanStorage> storageList) {
        this.storageList = storageList;
        setAllFilesOfAllStoragesToThisDatacenter();

        return this;
    }

    /**
     * Assigns all files from all storage devices to this Datacenter.
     */
    private void setAllFilesOfAllStoragesToThisDatacenter() {
        storageList.stream()
                .map(SanStorage::getFileList)
                .flatMap(List::stream)
                .forEach(file -> file.setDatacenter(this.getDatacenter()));
    }

	/**
     * Predict the total time to transfer a list of files.
     *
     * @param requiredFiles the files to be transferred
     * @return the total predicted time to transfer the files (in seconds)
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
     * @return the time to transfer the file (in seconds) or {@link SanStorage#FILE_NOT_FOUND} if not found.
     */
    private double timeToTransferFileFromStorage(final String fileName) {
        for (final SanStorage storage: getStorageList()) {
            final double transferTime = storage.getTransferTime(fileName);
            if (transferTime != SanStorage.FILE_NOT_FOUND) {
                return transferTime;
            }
        }

        return SanStorage.FILE_NOT_FOUND;
    }

    /**
     * Adds a file to the first storage device that has enough capacity
     * @param file the file to add
     * @return a tag from {@link DataCloudTags} informing the result of the operation
     */
    public int addFile(@NonNull final File file) {
        if (contains(file.getName())) {
            return DataCloudTags.FILE_ADD_ERROR_EXIST_READ_ONLY;
        }

        // check storage space first
        if (getStorageList().isEmpty()) {
            return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
        }

        for (final SanStorage storage : getStorageList()) {
            if (storage.isAmountAvailable(file.getSize())) {
                storage.addFile(file);
                return DataCloudTags.FILE_ADD_SUCCESSFUL;
            }
        }

        return DataCloudTags.FILE_ADD_ERROR_STORAGE_FULL;
    }
}
