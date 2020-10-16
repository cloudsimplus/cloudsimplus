package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link PowerModelHost} objects.
 */
public class PowerModelHostNull extends PowerModelHost {

    @Override
    public PowerMeasurement getPowerMeasurement() {
        return new PowerMeasurement();
    }

    @Override
    public double getPower(double utilizationFraction) throws IllegalArgumentException {
        return 0;
    }
}
