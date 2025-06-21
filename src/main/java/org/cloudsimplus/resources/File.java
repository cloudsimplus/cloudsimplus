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
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.util.MathUtil;

import java.time.LocalDateTime;

/**
 * Represents a file stored in a local disk or a {@link DatacenterStorage}.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
@Getter @Setter
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
     * The name of the file that is used for lookup.
     */
    private String name;

    /**
     * The Datacenter that will store the file.
     * When the file is added to a {@link FileStorage}
     * and such a storage is attached to a Datacenter,
     * the Datacenter sets itself for all files of that storage.
     */
    @NonNull
    private Datacenter datacenter;

    /**
     * The file attributes.
     */
    private FileAttribute attribute;

    /**
     * The last time (in second) in which operations were performed over this file.
     * This transaction time can be related to the operation of
     * adding, deleting, renaming or getting the file on a Datacenter's storage.
     */
    private double transactionTime;

    /**
     * Indicates if the file was deleted or not.
     */
    private boolean deleted;

    /// Creates a new file with a given size (in MBytes).
    /// **NOTE**: By default, a newly created file is set to a **master** copy.
    ///
    /// @param fileName file name
    /// @param fileSize file size in MBytes
    /// @throws IllegalArgumentException when one of the following scenarios occurs:
    ///                                (i) the file name is empty or null;
    ///                                (ii) the file size is zero or negative numbers.
    public File(final String fileName, final int fileSize) {
        datacenter = Datacenter.NULL;
        setName(fileName);
        setTransactionTime(0);
        createAttribute(fileSize);
    }

    /**
     * Copy constructor that creates a clone from a source file and sets the given file
     * as a <b>replica</b>.
     *
     * @param file the source file to create a copy and that will be set as a replica
     * @throws IllegalArgumentException when the source file is null
     */
    public File(final File file) throws IllegalArgumentException {
        this(file, false);
    }

    /**
     * Copy constructor that creates a clone from a source file and set the given file
     * as a <b>replica</b> or <b>master copy</b>.
     *
     * @param file the file to clone
     * @param masterCopy false to set the cloned file as a replica, true to set the cloned file as a master copy
     * @throws IllegalArgumentException
     */
    protected File(@NonNull final File file, final boolean masterCopy) throws IllegalArgumentException {
        this(file.getName(), file.getSize());
        this.setDatacenter(file.getDatacenter());

        this.deleted = file.deleted;
        file.getAttribute().copyValue(this.attribute);
        this.attribute.setMasterCopy(masterCopy);
    }

    /**
     * Check if a file object is valid or not, whether the given file object
     * itself and its file name are valid.
     *
     * @param file the file to be checked for validity
     * @throws NullPointerException if the given file is null
     * @throws IllegalArgumentException if the name of the file is blank or null
     */
    public static void validate(@NonNull final File file) {
        validate(file.getName());
    }

    /**
     * Check if the name of a file is valid or not.
     *
     * @param fileName the file name to be checked for validity
     * @return the given fileName if it's valid
     * @throws NullPointerException if the file name is null
     * @throws IllegalArgumentException if the file name is blank
     */
    public static String validate(@NonNull final String fileName) {
        if(fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be blank");
        }

        return fileName;
    }

    protected void createAttribute(final int fileSize) {
        this.attribute = new FileAttribute(this, fileSize);
    }

    /**
     * Clone the current file and set the cloned one as a <b>replica</b>.
     *
     * @return a clone of the current file (as a replica) or null if an error occurs
     */
    public File makeReplica() {
        return makeCopy();
    }

    /**
     * Clone the current file and make the new file a <b>master</b> copy as well.
     *
     * @return a clone of the current file (as a master-copy) or null if an error occurs
     */
    public File makeMasterCopy() {
        return makeCopy().setMasterCopy(true);
    }

    /**
     * Makes a copy of this file.
     *
     * @return a clone of the current file (as a replica) or null if an error occurs
     */
    private File makeCopy() {
        final File file = new File(name, attribute.getFileSize());

        this.attribute.copyValue(file.attribute);
        file.attribute.setMasterCopy(false);   // set this file as a replica

        return file;
    }

    /**
     * Gets the size of this object (in bytes). <br>
     * <b>NOTE</b>: This object size is NOT the actual file size. Moreover, this size is used for
     * transferring this object over a network.
     *
     * @return the object size (in bytes)
     */
    public int getAttributeSize() {
        return attribute.getAttributeSize();
    }

    /**
     * Sets the file name.
     *
     * @param name the file name
     */
    public final void setName(final String name) {
        this.name = validate(name);
    }

    /**
     * Sets the owner name of this file.
     *
     * @param name the owner name
     */
    public void setOwnerName(final String name) {
        attribute.setOwnerName(name);
    }

    /**
     * @return the owner name of this file or null if empty
     */
    public String getOwnerName() {
        return attribute.getOwnerName();
    }

    /**
     * @return the file size (in MBytes)
     */
    public int getSize() {
        return attribute.getFileSize();
    }

    /**
     * @return the file size (in bytes)
     */
    public int getSizeInByte() {
        return attribute.getFileSizeInByte();
    }

    /**
     * Sets the file size.
     *
     * @param fileSize the file size (in MBytes)
     */
    public void setSize(final int fileSize) {
        attribute.setFileSize(fileSize);
    }

    /**
     * Sets the last update time of this file (in seconds). <br>
     *
     * @param time the last update time (in seconds)
     * @return true if successful, false otherwise
     */
    public boolean setUpdateTime(final double time) {
        return attribute.setLastUpdateTime(time);
    }

    /**
     * @return the last update time (in seconds)
     */
    public double getLastUpdateTime() {
        return attribute.getLastUpdateTime();
    }

	/**
     * Sets the file registration ID (published by a Replica Catalogue entity).
     *
     * @param id registration ID
     * @return true if successful, false otherwise
     */
    public boolean setRegistrationID(final int id) {
        return attribute.setRegistrationId(id);
    }

    /**
     * @return the file registration ID.
     */
    public long getRegistrationID() {
        return attribute.getRegistrationID();
    }

    /**
     * Sets the file type (for instance, raw, tag, etc.).
     *
     * @param type a file type
     */
    public void setType(final int type) {
        attribute.setType(type);
    }

    /**
     * @return the file type
     */
    public int getType() {
        return attribute.getType();
    }

    /**
     * Sets the checksum of the file.
     *
     * @param checksum the checksum to set
     */
    public void setChecksum(final int checksum) {
        attribute.setChecksum(checksum);
    }

    /**
     * @return the file checksum
     */
    public int getChecksum() {
        return attribute.getChecksum();
    }

    /**
     * Sets the cost associated with the file.
     *
     * @param cost cost of this file
     */
    public void setCost(final double cost) {
        attribute.setCost(cost);
    }

    /**
     * @return the cost associated with the file.
     */
    public double getCost() {
        return attribute.getCost();
    }

    /**
     * @return the real file creation time, according to the current computer time.
     */
    public LocalDateTime getCreationTime() {
        return attribute.getCreationTime();
    }

    /**
     * Checks if the file is already registered to a Replica Catalogue.
     *
     * @return true if it is registered, false otherwise
     */
    public boolean isRegistered() {
        return attribute.isRegistered();
    }

    /**
     * Checks whether the file is a master-copy or replica.
     *
     * @return true if it is a master-copy; false if it's a replica.
     */
    public boolean isMasterCopy() {
        return attribute.isMasterCopy();
    }

    /**
     * Marks the file as a master-copy or replica.
     *
     * @param masterCopy true for master-copy, false for a replica
     */
    public File setMasterCopy(final boolean masterCopy) {
        attribute.setMasterCopy(masterCopy);
        return this;
    }

    /**
     * Sets the last time in which operations were performed over this file.
     * This transaction time can be related to the operation of adding,
     * deleting, renaming or getting the file on a Datacenter's storage.
     *
     * @param time the transaction time (in second)
     */
    public final void setTransactionTime(final double time) {
        this.transactionTime = MathUtil.nonNegative(time, "transactionTime");
    }

    @Override
    public String toString() {
        return name;
    }
}
