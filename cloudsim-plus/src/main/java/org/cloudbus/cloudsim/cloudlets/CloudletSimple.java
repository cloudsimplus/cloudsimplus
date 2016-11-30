/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

/**
 * Cloudlet implements the basic features of an application/job/task to be executed
 * by a {@link Vm} on behalf of a given user. It stores, despite all the
 * information encapsulated in the Cloudlet, the ID of the VM running it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 * @see DatacenterBroker
 *
 * @todo @author manoelcampos Cloudlets doesn't have a priority attribute
 * to define which of them will be executed first.
 * For instance, considering a VM with just one PE
 * and several cloudlets, the execution order of cloudlets can be defined by
 * their priorities.
 */
public class CloudletSimple extends CloudletAbstract {
    /**
     * Creates a Cloudlet with no priority and file size and output size equal to 1.
     * To change these values, use the respective setters.
     *
     * @param id  the unique ID of this cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber the pes number
     * @pre id >= 0
     * @pre cloudletLength >= 0.0
     * @post $none
     */
    public CloudletSimple(final int id,  final long cloudletLength,  final int pesNumber) {
        super(id, cloudletLength, pesNumber);
    }

    /**
     * Creates a Cloudlet with the given parameters.
     *
     * @param id the unique ID of this cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param cloudletFileSize the file size (in bytes) of this cloudlet <tt>BEFORE</tt> submitting to a Datacenter
     * @param cloudletOutputSize the file size (in bytes) of this cloudlet <tt>AFTER</tt> finish executing by a VM
     * @param pesNumber the pes number
     * @param utilizationModelCpu the utilization model of CPU
     * @param utilizationModelRam the utilization model of RAM
     * @param utilizationModelBw  the utilization model of BW
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     *
     * @pre id >= 0
     * @pre cloudletLength >= 0.0
     * @pre cloudletFileSize >= 1
     * @pre cloudletOutputSize >= 1
     * @post $none
     */
    @Deprecated()
    public CloudletSimple(
        final int id,
        final long cloudletLength,
        final int pesNumber,
        final long cloudletFileSize,
        final long cloudletOutputSize,
        final UtilizationModel utilizationModelCpu,
        final UtilizationModel utilizationModelRam,
        final UtilizationModel utilizationModelBw)
    {
            this(id, cloudletLength, pesNumber);
            this.setCloudletFileSize(cloudletFileSize)
                .setCloudletOutputSize(cloudletOutputSize)
                .setUtilizationModelCpu(utilizationModelCpu)
                .setUtilizationModelRam(utilizationModelRam)
                .setUtilizationModelBw(utilizationModelBw);
    }

    @Override
    public String toString() {
        return String.format("Cloudlet %d", getId());
    }

    /**
     * <p>Compares this Cloudlet with another one, considering
     * the {@link #getCloudletTotalLength() total length of the Cloudlets's}.</p>
     *
     * @param o the Vm to be compared to
     * @return {@inheritDoc }
     * @see #getCloudletTotalLength()
     */
    @Override
    public int compareTo(Cloudlet o) {
        return Long.compare(this.getCloudletTotalLength(), o.getCloudletTotalLength());
    }

}
