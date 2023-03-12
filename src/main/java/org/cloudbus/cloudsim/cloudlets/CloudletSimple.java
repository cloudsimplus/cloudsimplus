/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import lombok.NonNull;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Cloudlet implements the basic features of an application/job/task to be executed
 * by a {@link Vm} on behalf of a given user.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 * @see DatacenterBroker
 */
public class CloudletSimple extends CloudletAbstract {
    /**
     * Creates a Cloudlet with no priority or id.
     * The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param numberOfPes number of PEs that Cloudlet will require
     * @param utilizationModel a {@link UtilizationModel} to define how the Cloudlet uses CPU, RAM and BW.
     *                         To define an independent utilization model for each resource,
     *                         call the respective setters.
     *
     * @see #setUtilizationModelCpu(UtilizationModel)
     * @see #setUtilizationModelRam(UtilizationModel)
     * @see #setUtilizationModelBw(UtilizationModel)
     */
    public CloudletSimple(final long length, final int numberOfPes, final UtilizationModel utilizationModel) {
        super(length, numberOfPes, utilizationModel);
    }

    /**
     * Creates a Cloudlet with no priority or id.
     * The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param numberOfPes number of PEs that Cloudlet will require
     */
    public CloudletSimple(final long length, final int numberOfPes) {
        super(length, numberOfPes);
    }

    /**
     * Creates a Cloudlet with no priority or id.
     * The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param numberOfPes number of PEs that Cloudlet will require
     */
    public CloudletSimple(final long length, final long numberOfPes) {
        super(length, numberOfPes);
    }

    /**
     * Creates a Cloudlet with no priority, file size and output size equal to 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param id  the unique ID of this cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param numberOfPes the pes number
     */
    public CloudletSimple(final long id, final long length, final long numberOfPes) {
        super(id, length, numberOfPes);
    }

    @Override
    public String toString() {
        return "Cloudlet %d".formatted(getId());
    }

    /**
     * Compare this Cloudlet with another one based on {@link #getLength()}.
     *
     * @param other the Cloudlet to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull final Cloudlet other) {
        if(this.equals(other)) {
            return 0;
        }

        return Double.compare(getLength(), other.getLength()) +
            Long.compare(this.getId(), other.getId()) +
            this.getBroker().compareTo(other.getBroker());
    }
}
