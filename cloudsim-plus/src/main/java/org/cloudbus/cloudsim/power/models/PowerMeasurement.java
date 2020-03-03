package org.cloudbus.cloudsim.power.models;

/**
 * Measurement produced by a {@link PowerModel}.
 *
 * A PowerMeasurement consists of a static and a dynamic fraction.
 */
public class PowerMeasurement {

    private double staticUsage;
    private double dynamicUsage;

    public PowerMeasurement(double staticUsage, double dynamicUsage) {
        this.staticUsage = staticUsage;
        this.dynamicUsage = dynamicUsage;
    }

    public PowerMeasurement() {
        this(0, 0);
    }

    public double getTotalUsage() {
        return staticUsage + dynamicUsage;
    }

    public double getStaticUsage() {
        return staticUsage;
    }

    public double getDynamicUsage() {
        return dynamicUsage;
    }

    public PowerMeasurement add(PowerMeasurement measurement) {
        return new PowerMeasurement(
            staticUsage + measurement.getStaticUsage(),
            dynamicUsage + measurement.getDynamicUsage()
        );
    }

    public PowerMeasurement multiply(double factor) {
        return new PowerMeasurement(
            staticUsage * factor,
            dynamicUsage * factor
        );
    }

}
