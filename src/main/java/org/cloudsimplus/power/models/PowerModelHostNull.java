package org.cloudsimplus.power.models;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeasurement;

/**
 * A class that implements the Null Object Design Pattern for {@link PowerModelHost} objects.
 * @since CloudSim Plus 6.0.0
 */
final class PowerModelHostNull implements PowerModelHost {
    @Override public PowerMeasurement getPowerMeasurement() { return new PowerMeasurement(); }
    @Override public double getPower(double utilizationFraction) { return 0; }
    @Override public PowerModelHost setStartupPower(double power) { return this; }
    @Override public PowerModelHost setShutDownPower(double power) { return this; }
    @Override public void addStartupTotals() {/**/}
    @Override public void addShutDownTotals() {/**/}
    @Override public Host getHost() { return Host.NULL; }
    @Override public double getStartupPower() { return 0; }
    @Override public double getShutDownPower() { return 0; }
    @Override public double getTotalStartupPower() { return 0; }
    @Override public double getTotalShutDownPower() { return 0; }
    @Override public double getTotalStartupTime() { return 0; }
    @Override public double getTotalShutDownTime()  { return 0; }
    @Override public int getTotalStartups() { return 0; }
    @Override public PowerModelHost setHost(Host host) { return this; }
}
