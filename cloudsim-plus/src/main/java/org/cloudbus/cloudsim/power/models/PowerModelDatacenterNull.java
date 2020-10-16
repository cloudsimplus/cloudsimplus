package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link PowerModelDatacenter} objects.
 */
public class PowerModelDatacenterNull extends PowerModelDatacenter {

    @Override
    public PowerMeasurement getPowerMeasurement() {
        return new PowerMeasurement();
    }

}
