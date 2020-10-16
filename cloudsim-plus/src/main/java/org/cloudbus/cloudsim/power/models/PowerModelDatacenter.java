package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerMeasurement;
import org.cloudbus.cloudsim.resources.Resource;


/**
 * Abstract implementation of a data center power model.
 */
public abstract class PowerModelDatacenter extends PowerModel {

    private Datacenter datacenter;

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
