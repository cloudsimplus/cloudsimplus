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
 * @todo See the warning in class documentation.
 * 
 * @author Rodrigo N. Calheiros
 * @since CloudSim Toolkit 1.0
 */
public class SanStorage extends HarddriveStorage {
    /** @see #getBandwidth() */
    private final double bandwidth;

    /** @see #getNetworkLatency()  */
    private final double networkLatency;

    /**
     * Creates a new SAN with a given capacity, latency, and bandwidth of the network connection.
     * 
     * @param capacity Total storage capacity of the SAN
     * @param bandwidth Network bandwidth
     * @param networkLatency Network latency
     * @throws IllegalArgumentException when the name and the capacity are not valid
     */
    public SanStorage(final long capacity, final double bandwidth, final double networkLatency) throws IllegalArgumentException {
        super(capacity);
        this.bandwidth = bandwidth;
        this.networkLatency = networkLatency;
    }

    /**
     * Creates a new SAN with a given capacity, latency, and bandwidth of the network connection
     * and with a specific name.
     * 
     * @param name the name of the new storage device
     * @param capacity Storage device capacity
     * @param bandwidth Network bandwidth
     * @param networkLatency Network latency
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
        return time + getFileTransferTimePlusNetworkLatency(file);
    }

    /**
     * Gets the maximum transfer rate of the SAN in MByte/sec.
     * It is defined as the minimum value between the disk rate and the SAN bandwidth.
     * Even the bandwidth being faster the the disk rate, the max transfer rate
     * is limited by the disk speed.
     * 
     * @return the max transfer in MB/sec
     */
    @Override
    public double getMaxTransferRate() {
        final double diskRate = super.getMaxTransferRate();

        // the max transfer rate is the minimum between
        // the network bandwidth and the disk rate
        return Math.min(diskRate, getBandwidth());
    }

    @Override
    public double addFile(final File file) {
        final double time = super.addFile(file);
        if(time > 0)
            return time + getFileTransferTimePlusNetworkLatency(file);
        
        return time;
    }

    private double getFileTransferTimePlusNetworkLatency(final File file) {
        return (file.getSize() * getBandwidth()) + getNetworkLatency();
    }

    @Override
    public double deleteFile(final File file) {
        final double time = super.deleteFile(file);
        return time + getFileTransferTimePlusNetworkLatency(file);
    }

    /**
     * Get the bandwidth of the SAN network. 
     * @return the bandwidth
     */
    public double getBandwidth() {
        return bandwidth;
    }

    /**
     * Gets the SAN's network latency. 
     * @return the SAN's network latency
     */
    public double getNetworkLatency() {
        return networkLatency;
    }
}
