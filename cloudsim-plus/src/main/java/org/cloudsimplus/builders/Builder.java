package org.cloudsimplus.builders;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An abstract Builder for creation of CloudSim objects,
 * such as {@link Datacenter},
 * {@link Host},
 * {@link Vm}
 * {@link org.cloudbus.cloudsim.DatacenterBroker} and
 * {@link Cloudlet}.
 *
 * The builders helps in the creation of such objects,
 * by allowing to set standard attribute's values
 * in order to create several objects with the same characteristics.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class Builder {
    public void validateAmount(final int amount){
        if(amount <= 0)
            throw new RuntimeException("The amount has to be greather than 0.");
    }
}
