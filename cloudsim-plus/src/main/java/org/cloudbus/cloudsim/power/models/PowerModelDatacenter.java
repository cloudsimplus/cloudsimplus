package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * Abstract implementation of a data center power model.
 * @since CloudSim Plus 6.0.0
 */
public abstract class PowerModelDatacenter extends PowerModel {

    private Datacenter datacenter;

    /**
     * Gets the Datacenter this PowerModel is collecting power consumption measurements from.
     * @return
     */
    public Datacenter getDatacenter() {
        return datacenter;
    }

    /**
     * Sets the Datacenter this PowerModel will collect power consumption measurements from.
     * @param datacenter the Datacenter to set
     * @return
     */
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
