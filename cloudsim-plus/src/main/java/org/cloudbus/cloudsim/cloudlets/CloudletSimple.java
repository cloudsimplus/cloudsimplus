/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Cloudlet implements the basic features of an application/job/task to be executed
 * by a {@link Vm} on behalf of a given user. It stores, despite all the
 * information encapsulated in the Cloudlet, the ID of the VM running it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 * @see DatacenterBroker
 */
public class CloudletSimple extends CloudletAbstract {
    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber      number of PEs that Cloudlet will require
     */
    public CloudletSimple(final long length, final int pesNumber) {
        super(length, pesNumber);
    }

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber      number of PEs that Cloudlet will require
     */
    public CloudletSimple(final long length, final long pesNumber) {
        super(length, pesNumber);
    }

    /**
     * Creates a Cloudlet with no priority and file size and output size equal to 1.
     * To change these values, use the respective setters.
     *  @param id  the unique ID of this cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber the pes number
     */
    public CloudletSimple(final long id, final long length, final long pesNumber) {
        super(id, length, pesNumber);
    }

    @Override
    public String toString() {
        return String.format("Cloudlet %d", getId());
    }

    /**
     * Compare this Cloudlet with another one based on {@link #getLength()}.
     *
     * @param o the Cloudlet to compare to
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(final Cloudlet o) {
        return Long.compare(getLength(), o.getLength());
    }
}
