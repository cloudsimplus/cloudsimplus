package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerAware;

/**
 * Computes current amount of power being consumed by the {@link Host}s of a {@link Datacenter}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.2.0
 */
public class DatacenterPowerSupply implements PowerAware {
    public static final DatacenterPowerSupply NULL = new DatacenterPowerSupply(Datacenter.NULL){
        @Override protected double computePowerUtilizationForTimeSpan(double lastDatacenterProcessTime) { return -1; }
        @Override public double getPower() { return -1; }
    };

    private Datacenter datacenter;

    /** @see #getPower() */
    private double power;

    public DatacenterPowerSupply(){}

    protected DatacenterPowerSupply(final Datacenter datacenter){
        this.datacenter = datacenter;
    }

    /**
     * Computes an <b>estimation</b> of total power consumed (in Watts-sec) by all Hosts of the Datacenter
     * since the last time the processing of Cloudlets in this Host was updated.
     * It also updates the {@link #getPower() Datacenter's total consumed power up to now}.
     *
     * @return the <b>estimated</b> total power consumed (in Watts-sec) by all Hosts in the elapsed time span
     */
    protected double computePowerUtilizationForTimeSpan(final double lastDatacenterProcessTime) {
        final double clock = datacenter.getSimulation().clock();
        if (clock - lastDatacenterProcessTime == 0) { //time span
            return 0;
        }

        double datacenterTimeSpanPowerUse = 0;
        for (final Host host : datacenter.getHostList()) {
            final double prevCpuUsage = host.getPreviousUtilizationOfCpu();
            final double cpuUsage = host.getCpuPercentUtilization();
            final double timeFrameHostEnergy =
                host.getPowerModel().getEnergyLinearInterpolation(prevCpuUsage, cpuUsage, clock - lastDatacenterProcessTime);
            datacenterTimeSpanPowerUse += timeFrameHostEnergy;
        }

        power += datacenterTimeSpanPowerUse;
        return datacenterTimeSpanPowerUse;
    }

    /**
     * Gets the total power consumed by the Datacenter up to now in Watt-Second (Ws).
     *
     * @return the total power consumption in Watt-Second (Ws)
     * @see #getPowerInKWatts()
     */
    @Override
    public double getPower() {
        return power;
    }

    protected DatacenterPowerSupply setDatacenter(final Datacenter datacenter) {
        this.datacenter = datacenter;
        return this;
    }
}
