package org.cloudsimplus.power.models;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeasurement;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link PowerModelHost} objects.
 * @since CloudSim Plus 6.0.0
 */
class PowerModelHostNull extends PowerModelHost {
    @Override public PowerMeasurement getPowerMeasurement() { return new PowerMeasurement(); }
    @Override public double getPowerInternal(double utilizationFraction) { return 0; }
    @Override public Host getHost() { return Host.NULL; }
}
