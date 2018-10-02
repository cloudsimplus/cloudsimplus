/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.datacenters.Datacenter;

import static java.util.Objects.requireNonNull;

/**
 * A class for representing a physical file in a DataCloud environment
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
public class File {
    /**
     * Denotes that this file has not been registered to a Replica Catalogue.
     */
    public static final int NOT_REGISTERED = -1;

    /**
     * Denotes that the type of this file is unknown.
     */
    public static final int TYPE_UNKNOWN = 0;

    /**
     * Logical file name.
     */
    private String name;

    /**
     * @see #getDatacenter()
     */
    private Datacenter datacenter;

    /**
     * A file attribute.
     */
    private  FileAttribute attribute;

    /**
     * A transaction time for adding, deleting or getting the file.
     *
     * @see #setTransactionTime(double)
     */
    private double transactionTime;

    /**
     * @see #isDeleted()
     */
    private boolean deleted;

    /**
     * Creates a new DataCloud file with a given size (in MBytes). <br>
     * NOTE: By default, a newly-created file is set to a <b>master</b> copy.
     *
     * @param fileName file name
     * @param fileSize file size in MBytes
     * @throws IllegalArgumentException when one of the following scenarios occur:
     *                                  <ul>
     *                                  <li>the file name is empty or <tt>null</tt>
     *                                  <li>the file size is zero or negative numbers
     *                                  </ul>
     */
    public File(final String fileName, final int fileSize) {
    	if (fileSize <= 0) {
            throw new IllegalArgumentException("File(): Error - size <= 0.");
        }
        datacenter = Datacenter.NULL;
        setName(fileName);
        transactionTime = 0;
        createAttribute(fileSize);
    }

    /**
     * Copy constructor that creates a clone from a source file and set the given file
     * as a <b>replica</b>.
     *
     * @param file the source file to create a copy and that will be set as a replica
     * @throws IllegalArgumentException when the source file is <tt>null</tt>
     */
    public File(final File file) throws IllegalArgumentException {
        this(requireNonNull(file), false);
    }

    /**
     * Copy constructor that creates a clone from a source file and set the given file
     * as a <b>replica</b> or <b>master copy</b>.
     *
     * @param file the file to clone
     * @param masterCopy false to set the cloned file as a replica, true to set the cloned file as a master copy
     * @throws IllegalArgumentException
     */
    protected File(final File file, final boolean masterCopy) throws IllegalArgumentException {
        this(file.getName(), file.getSize());
        this.setDatacenter(file.getDatacenter());

        this.deleted = file.deleted;
        file.getAttribute().copyValue(this.attribute);
        this.attribute.setMasterCopy(masterCopy);
    }

    /**
     * Check if the name of a file is valid or not.
     *
     * @param fileName the file name to be checked for validity
     * @return <tt>true</tt> if the file name is valid, <tt>false</tt> otherwise
     */
    public static boolean isValid(final String fileName) {
        return FileAttribute.isValid(fileName);
    }

    /**
     * Check if a file object is valid or not. This method checks whether the given file object
     * itself and its file name are valid.
     *
     * @param file the file to be checked for validity
     * @return <tt>true</tt> if the file is valid, <tt>false</tt> otherwise
     */
    public static boolean isValid(final File file) {
        return file != null && isValid(file.getName());
    }

    protected void createAttribute(final int fileSize) {
        this.attribute = new FileAttribute(this, fileSize);
    }

    /**
     * Clone the current file and set the cloned one as a <b>replica</b>.
     *
     * @return a clone of the current file (as a replica) or <tt>null</tt> if an error occurs
     */
    public File makeReplica() {
        return makeCopy();
    }

    /**
     * Clone the current file and make the new file as a <b>master</b> copy as well.
     *
     * @return a clone of the current file (as a master copy) or <tt>null</tt> if an error occurs
     */
    public File makeMasterCopy() {
        final File file = makeCopy();
        if (file != null) {
            file.setMasterCopy(true);
        }

        return file;
    }

    /**
     * Makes a copy of this file.
     *
     * @return a clone of the current file (as a replica) or <tt>null</tt> if an error occurs
     */
    private File makeCopy() {
        final File file = new File(name, attribute.getFileSize());

        this.attribute.copyValue(file.attribute);
        file.attribute.setMasterCopy(false);   // set this file as a replica

        return file;
    }

    /**
     * Gets an attribute of this file.
     *
     * @return a file attribute
     */
    public FileAttribute getAttribute() {
        return attribute;
    }

    /**
     * Sets an attribute of this file.
     *
     * @param attribute file attribute
     */
    protected void setAttribute(FileAttribute attribute) {
        this.attribute = attribute;
    }

    /**
     * Gets the size of this object (in byte). <br/>
     * NOTE: This object size is NOT the actual file size. Moreover, this size is used for
     * transferring this object over a network.
     *
     * @return the object size (in byte)
     */
    public int getAttributeSize() {
        return attribute.getAttributeSize();
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the file name.
     *
     * @param name the file name
     */
    public final void setName(String name) {
        if (!isValid(name))
            throw new IllegalArgumentException("File name cannot be null or empty");
        this.name = name;
    }

    /**
     * Sets the owner name of this file.
     *
     * @param name the owner name
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setOwnerName(String name) {
        return attribute.setOwnerName(name);
    }

    /**
     * Gets the owner name of this file.
     *
     * @return the owner name or <tt>null</tt> if empty
     */
    public String getOwnerName() {
        return attribute.getOwnerName();
    }

    /**
     * Gets the file size (in MBytes).
     *
     * @return the file size (in MBytes)
     */
    public int getSize() {
        return attribute.getFileSize();
    }

    /**
     * Gets the file size (in bytes).
     *
     * @return the file size (in bytes)
     */
    public int getSizeInByte() {
        return attribute.getFileSizeInByte();
    }

    /**
     * Sets the file size (in MBytes).
     *
     * @param fileSize the file size (in MBytes)
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setSize(int fileSize) {
        return attribute.setFileSize(fileSize);
    }

    /**
     * Sets the last update time of this file (in seconds). <br/>
     * NOTE: This time is relative to the start time. Preferably use
     * {@link org.cloudbus.cloudsim.core.CloudSim#clock()} method.
     *
     * @param time the last update time (in seconds)
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setUpdateTime(double time) {
        return attribute.setUpdateTime(time);
    }

    /**
     * Gets the last update time (in seconds).
     *
     * @return the last update time (in seconds)
     */
    public double getLastUpdateTime() {
        return attribute.getLastUpdateTime();
    }

	/**
     * Sets the file registration ID (published by a Replica Catalogue entity).
     *
     * @param id registration ID
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setRegistrationID(int id) {
        return attribute.setRegistrationId(id);
    }

    /**
     * Gets the file registration ID.
     *
     * @return registration ID
     */
    public long getRegistrationID() {
        return attribute.getRegistrationID();
    }

    /**
     * Sets the file type (for instance, raw, tag, etc).
     *
     * @param type a file type
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setType(int type) {
        return attribute.setType(type);
    }

    /**
     * Gets the file type.
     *
     * @return file type
     */
    public int getType() {
        return attribute.getType();
    }

    /**
     * Sets the checksum of the file.
     *
     * @param checksum the checksum of this file
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setChecksum(int checksum) {
        return attribute.setChecksum(checksum);
    }

    /**
     * Gets the file checksum.
     *
     * @return file checksum
     */
    public int getChecksum() {
        return attribute.getChecksum();
    }

    /**
     * Sets the cost associated with the file.
     *
     * @param cost cost of this file
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setCost(double cost) {
        return attribute.setCost(cost);
    }

    /**
     * Gets the cost associated with the file.
     *
     * @return the cost of this file
     */
    public double getCost() {
        return attribute.getCost();
    }

    /**
     * Gets the file creation time (in millisecond).
     *
     * @return the file creation time (in millisecond)
     */
    public long getCreationTime() {
        return attribute.getCreationTime();
    }

    /**
     * Checks if the file is already registered to a Replica Catalogue.
     *
     * @return <tt>true</tt> if it is registered, <tt>false</tt> otherwise
     */
    public boolean isRegistered() {
        return attribute.isRegistered();
    }

    /**
     * Checks whether the file is a master copy or replica.
     *
     * @return <tt>true</tt> if it is a master copy or <tt>false</tt> otherwise
     */
    public boolean isMasterCopy() {
        return attribute.isMasterCopy();
    }

    /**
     * Marks the file as a master copy or replica.
     *
     * @param masterCopy a flag denotes <tt>true</tt> for master copy or <tt>false</tt> for a
     *                   replica
     */
    public void setMasterCopy(boolean masterCopy) {
        attribute.setMasterCopy(masterCopy);
    }

    /**
     * Checks if the file was deleted or not.
     *
     * @return <tt>true</tt> if it was deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the file as deleted or not.
     *
     * @param deleted <tt>true</tt> if it was deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Sets the current transaction time (in second) of this file. This transaction time can be
     * related to the operation of adding, deleting or getting the file on a Datacenter's storage.
     *
     * @param time the transaction time (in second)
     * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
     */
    public boolean setTransactionTime(double time) {
        if (time < 0) {
            return false;
        }

        transactionTime = time;
        return true;
    }

    /**
     * Gets the last transaction time of the file (in second).
     *
     * @return the transaction time (in second)
     */
    public double getTransactionTime() {
        return transactionTime;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Gets the Datacenter that stores the file.
     *
     * @return
     */
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter that will store the file.
     * When the file is added to a {@link FileStorage}
     * and such a storage is attached to a Datacenter,
     * the Datacenter sets itself for all files of that storage.
     *
     * @param datacenter the Datacenter that will store the file
     * @return
     */
    public final File setDatacenter(Datacenter datacenter) {
        this.datacenter = requireNonNull(datacenter);
        return this;
    }

}
