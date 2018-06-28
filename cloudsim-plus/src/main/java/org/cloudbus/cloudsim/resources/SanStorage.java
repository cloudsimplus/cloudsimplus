/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

/**
 * SanStorage represents a Storage Area Network (SAN) composed of a set of
 * hard disks connected in a LAN.
 * Capacity of individual disks are abstracted, thus only the overall capacity of the SAN is
 * considered. <p/>
 *
 * <tt>WARNING</tt>: This class is not yet fully functional. Effects of network contention are
 * not considered in the simulation. So, time for file transfer is underestimated in the presence of
 * high network load.
 *
 * @TODO See the warning in class documentation.
 *
 * @author Rodrigo N. Calheiros
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class SanStorage extends HarddriveStorage {
    /** @see #getBandwidth() */
    private double bandwidth;

    /** @see #getNetworkLatency()  */
    private double networkLatency;

    /**
     * Creates a new SAN with a given capacity, latency, and bandwidth of the network connection.
     *
     * @param capacity Total storage capacity of the SAN
     * @param bandwidth Network bandwidth (in Megabits/s)
     * @param networkLatency Network latency (in seconds)
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public SanStorage(final long capacity, final double bandwidth, final double networkLatency) throws IllegalArgumentException {
        super(capacity);
        this.setBandwidth(bandwidth);
        this.setNetworkLatency(networkLatency);
    }

    /**
     * Creates a new SAN with a given capacity, latency, and bandwidth of the network connection
     * and with a specific name.
     *
     * @param name the name of the new storage device
     * @param capacity Storage device capacity
     * @param bandwidth Network bandwidth (in Megabits/s)
     * @param networkLatency Network latency (in seconds)
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public SanStorage(final String name, final long capacity, final double bandwidth, final double networkLatency){
        super(name, capacity);
        this.bandwidth = bandwidth;
        this.networkLatency = networkLatency;
    }

    @Override
    public double addReservedFile(final File file) {
        final double time = super.addReservedFile(file);
        return time + getTransferTime(file);
    }

    @Override
    public double addFile(final File file) {
        final double time = super.addFile(file);
        if(time > 0)
            return time + getTransferTime(file);

        return time;
    }

    /**
     * {@inheritDoc}
     * The network latency is added to the transfer time.
     * @param fileSize {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getTransferTime(final int fileSize) {
        //Gets the time to read the from from the local storage device (such as an HD or SSD).
        final double storageDeviceReadTime = super.getTransferTime(fileSize);

        //Gets the time to transfer the file through the network
        final double networkTransferTime = getTransferTime(fileSize, bandwidth);

        return storageDeviceReadTime + networkTransferTime + getNetworkLatency();
    }

    @Override
    public double deleteFile(final File file) {
        final double time = super.deleteFile(file);
        return time + getTransferTime(file);
    }

    /**
     * Gets the bandwidth of the SAN network (in Megabits/s).
     * @return the bandwidth (in Megabits/s)
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the bandwidth of the SAN network (in Megabits/s).
     * @param bandwidth the bandwidth to set (in Megabits/s)
     * @throws IllegalArgumentException when the bandwidth is lower or equal to zero
     */
    public final void setBandwidth(final double bandwidth) {
        if(bandwidth <= 0){
            throw new IllegalArgumentException("Bandwidth must be higher than zero");
        }
        this.bandwidth = bandwidth;
    }

    /**
     * Gets the SAN's network latency (in seconds).
     * @return the SAN's network latency (in seconds)
     */
    public double getNetworkLatency() {
        return networkLatency;
    }

    /**
     * Sets the latency of the SAN network (in seconds).
     * @param networkLatency the latency to set (in seconds)
     * @throws IllegalArgumentException when the latency is lower or equal to zero
     */
    public final void setNetworkLatency(final double networkLatency) {
        if(networkLatency <= 0){
            throw new IllegalArgumentException("Latency must be higher than zero");
        }
        this.networkLatency = networkLatency;
    }

    @Override
    public String toString() {
        return getName();
    }
}
