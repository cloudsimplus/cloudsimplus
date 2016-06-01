package org.cloudbus.cloudsim.resources;

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
 * @since CloudSim Toolkit 3.0.4
 */
public interface FileStorage extends Resource<Long> {
    /**
     * 
     * @return the name of the storage device
     */
    String getName();    
    
    /**
     * Gets the maximum transfer rate of the storage in MByte/sec.
     * 
     * @return the maximum transfer rate in MB/sec
     */
    double getMaxTransferRate();
    
    /**
     * Sets the maximum transfer rate of this storage system in MByte/sec.
     * 
     * @param rate the maximum transfer rate in MB/sec
     * @return <tt>true</tt> if the values is greater than zero and was set successfully, 
     * <tt>false</tt> otherwise
     */
    boolean setMaxTransferRate(int rate);
    
    /**
     * Gets the number of files stored on this device.
     * 
     * @return the number of stored files
     */
    int getNumStoredFile();

    /**
     * Makes reservation of space on the storage to store a file.
     * 
     * @param fileSize the size to be reserved in MB
     * @return <tt>true</tt> if reservation succeeded, <tt>false</tt> otherwise
     */
    boolean reserveSpace(int fileSize);

    /**
     * Adds a file for which the space has already been reserved. The time taken (in seconds) for
     * adding the specified file can also be found using
     * {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param file the file to be added
     * @return the time (in seconds) required to add the file
     */
    double addReservedFile(File file);
    
    /**
     * Gets the file with the specified name. The time taken (in seconds) for getting the specified
     * file can also be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param fileName the name of the needed file
     * @return the file with the specified filename; null if not found
     */
    File getFile(String fileName);
    
    /**
     * Gets a list with the names of all files stored on the device.
     * 
     * @return a List of file names
     */
    List<String> getFileNameList();

    /**
     * Adds a file to the storage. The time taken (in seconds) for adding the specified file can
     * also be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param file the file to be added
     * @return the time taken (in seconds) for adding the specified file or zero if 
     * there isn't available storage space.
     */
    double addFile(File file);

    /**
     * Adds a set of files to the storage. The time taken (in seconds) for adding each file can also
     * be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param list the files to be added
     * @return the time taken (in seconds) for adding the specified file or zero if the
     * file is invalid or there isn't available storage space.
     */
    double addFile(List<File> list);

    /**
     * Removes a file from the storage. The time taken (in seconds) for deleting the specified file
     * can be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param fileName the name of the file to be removed
     * @return the deleted file.
     */
    File deleteFile(String fileName);

    /**
     * Removes a file from the storage. The time taken (in seconds) for deleting the specified file
     * can also be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
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
     * can also be found using {@link org.cloudbus.cloudsim.File#getTransactionTime()}.
     * 
     * @param file the file we would like to rename
     * @param newName the new name of the file
     * @return <tt>true</tt> if the renaming succeeded, <tt>false</tt> otherwise
     */
    boolean renameFile(File file, String newName);    

    /**
     * Checks whether there is enough space on the storage for a certain file
     * 
     * @param fileSize to size of the file intended to be stored on the device
     * @return <tt>true</tt> if enough space available, <tt>false</tt> otherwise
    */
    boolean hasPotentialAvailableSpace(final int fileSize);    
}
