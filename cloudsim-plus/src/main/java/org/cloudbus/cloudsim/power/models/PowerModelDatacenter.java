package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;

import java.util.Objects;

/**
 * Abstract implementation of a data center power model.
 * @since CloudSim Plus 6.0.0
 */
public abstract class PowerModelDatacenter implements PowerModel {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerModelDatacenter}
     * objects.
     */
    public static final PowerModelDatacenterNull NULL = new PowerModelDatacenterNull();

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
     */
    public final void setDatacenter(final Datacenter datacenter) {
        this.datacenter = Objects.requireNonNull(datacenter);
    }
}
