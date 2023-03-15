package org.cloudsimplus.power.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.datacenters.Datacenter;

/**
 * Abstract implementation of a data center power model.
 * @since CloudSim Plus 6.0.0
 */
@Accessors(makeFinal = false)
public abstract class PowerModelDatacenter implements PowerModel {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerModelDatacenter}
     * objects.
     */
    public static final PowerModelDatacenterNull NULL = new PowerModelDatacenterNull();

    /**
     * The Datacenter this PowerModel is collecting power consumption measurements from.
     */
    @Getter @Setter @NonNull
    private Datacenter datacenter;
}
