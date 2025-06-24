package org.cloudsimplus.power.models;

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.power.PowerMeasurement;

/**
 * A class that implements the Null Object Design Pattern for {@link PowerModelDatacenter} objects.
 * @since CloudSim Plus 6.0.0
 */
class PowerModelDatacenterNull extends PowerModelDatacenter {
    @Override public PowerMeasurement getPowerMeasurement() {
        return new PowerMeasurement();
    }
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
}
