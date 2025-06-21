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
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.util.Conversion;
import org.cloudsimplus.util.DataCloudTags;
import org.cloudsimplus.util.MathUtil;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Stores information related to a {@link File}.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @since CloudSim Toolkit 1.0
 */
@Getter
public class FileAttribute {

    /**
     * File ID given by a Replica Catalogue.
     */
    private long id;

    /**
     * The owner name of the file.
     */
    @Setter @NonNull
    private String ownerName;

    /**
     * The file type (just for tagging purposes).
     */
    @Setter
    private int type;

    /**
     * The file size (in MBytes).
     */
    private int fileSize;

    /**
     * The file checksum.
     */
    @Setter
    private int checksum;

    /**
     * The last time the file was updated (in seconds)
     */
    private double lastUpdateTime;

    /**
     * The real file creation time, according to the current computer time.
     */
    @Setter
    private LocalDateTime creationTime;

    /**
     * The monetary cost ($) of storing this file on the cloud infrastructure.
     */
    private double cost;

    /**
     * Checks whether the file is a master-copy or replica.
     */
    @Setter
    private boolean masterCopy;

    /**
     * The file that this attribute object is related to
     */
    @NonNull
    private final File file;

    /**
     * Creates a new FileAttribute object.
     *
     * @param file the file that this attribute object is related to
     * @param fileSize the size for the File (in MBytes)
     */
    public FileAttribute(final File file, final int fileSize) {
        this.file = file;

        creationTime = LocalDateTime.now();

        ownerName = "";
        id = File.NOT_REGISTERED;
        type = File.TYPE_UNKNOWN;
        masterCopy = true;
        setFileSize(fileSize);
    }

    /**
     * Copy the values of this instance into a given FileAttribute object.
     *
     * @param destinationAttr the destination FileAttribute object to copy the
     * current object data to
     */
    public void copyValue(final FileAttribute destinationAttr) {
        Objects.requireNonNull(destinationAttr);
        destinationAttr.setFileSize(fileSize);
        destinationAttr.setOwnerName(ownerName);
        destinationAttr.setLastUpdateTime(lastUpdateTime);
        destinationAttr.setRegistrationId(id);
        destinationAttr.setType(type);
        destinationAttr.setChecksum(checksum);
        destinationAttr.setCost(cost);
        destinationAttr.setMasterCopy(masterCopy);
        destinationAttr.setCreationTime(creationTime);
    }

    /**
     * {@return the size of the object (in bytes)}<br>
     * <b>NOTE</b>: This object size is NOT the actual file size.
     * Moreover, this size is used for transferring this object over a network.
     */
    public int getAttributeSize() {
        int length = DataCloudTags.PKT_SIZE;
        if (ownerName != null) {
            length += ownerName.length();
        }

        length += file.getName().length();

        return length;
    }

    /**
     * Sets the file size.
     *
     * @param fileSize the file size (in MBytes)
     */
    public final void setFileSize(final int fileSize) {
        if(fileSize <= 0)
            throw new IllegalArgumentException("File size must be higher than 0");

        this.fileSize = fileSize;
    }

    /**
     * @return the file size (in bytes)
     */
    public int getFileSizeInByte() {
        return fileSize * Conversion.MILLION;   // 1e6
        // return size * 1048576; // 1e6 - more accurate
    }

    /**
     * Sets the last update time of the file (in seconds). <br>
     * <b>NOTE</b>: This time is relative to the start time. Preferably use
     * {@link CloudSimPlus#clock()} method.
     *
     * @param time the last update time (in seconds)
     * @return true if successful, false otherwise
     */
    public boolean setLastUpdateTime(final double time) {
        MathUtil.nonNegative(time, "lastUpdateTime");
        if (time < lastUpdateTime) {
            return false;
        }

        lastUpdateTime = time;
        return true;
    }

    /**
     * Sets the file registration ID (published by a Replica Catalogue entity).
     *
     * @param id the registration ID to set
     * @return true if successful, false otherwise
     */
    public boolean setRegistrationId(final long id) {
        if (id < 0) {
            return false;
        }

        this.id = id;
        return true;
    }

    /**
     * @return the file registration ID.
     */
    public long getRegistrationID() {
        return id;
    }

    /**
     * Sets the monetary cost ($) of storing this file on the cloud infrastructure.
     *
     * @param cost monetary cost ($) to set
     */
    public void setCost(final double cost) {
        this.cost = MathUtil.nonNegative(cost, "cost");
    }

    /**
     * Checks if the file is already registered to a Replica Catalogue.
     *
     * @return true if it is registered, false otherwise
     */
    public boolean isRegistered() {
        return id != File.NOT_REGISTERED;
    }
}
