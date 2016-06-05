package org.cloudbus.cloudsim.builders;

/**
 * An abstract Builder for creation of CloudSim objects,
 * such as {@link org.cloudbus.cloudsim.Datacenter},
 * {@link org.cloudbus.cloudsim.Host},
 * {@link org.cloudbus.cloudsim.Vm}
 * {@link org.cloudbus.cloudsim.DatacenterBroker} and 
 * {@link org.cloudbus.cloudsim.Cloudlet}.
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
