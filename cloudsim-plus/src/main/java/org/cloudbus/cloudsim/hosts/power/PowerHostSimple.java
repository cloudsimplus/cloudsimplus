/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts.power;

import java.util.List;

import org.cloudbus.cloudsim.hosts.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;

/**
 * PowerHost class enables simulation of power-aware hosts.
 *
 * <br/>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:<br/>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerHostSimple extends HostDynamicWorkloadSimple implements PowerHost {

    /**
     * The power model used by the host.
     */
    private PowerModel powerModel;

    /**
     * Creates a PowerHost with the given parameters.
     *
     * @param id the id of the host
     * @param storage the storage capacity in MB
     * @param peList the host's PEs list
     *
     */
    public PowerHostSimple(int id, long storage, List<Pe> peList) {
        super(id, storage, peList);
        setPowerModel(PowerModel.NULL);
    }

    /**
     * Creates a PowerHost with the given parameters.
     *
     * @param id the id of the host
     * @param ramProvisioner the ram provisioner with capacity in MB
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in MB
     * @param peList the host's PEs list
     * @param vmScheduler the VM scheduler
     * @param powerModel the model of power consumption
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public PowerHostSimple(
            int id,
            ResourceProvisioner ramProvisioner,
            ResourceProvisioner bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler,
            PowerModel powerModel)
    {
        this(id, storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setVmScheduler(vmScheduler);
        setPowerModel(powerModel);
    }

    @Override
    public double getPower() {
        return getPower(getUtilizationOfCpu());
    }

    /**
     * Gets the power consumption of the host. For this moment it only computes the power consumed by PEs.
     *
     * @param utilization the utilization percentage (between [0 and 1]) of a
     * resource that is critical for power consumption
     * @return the power consumption
     */
    protected double getPower(double utilization) {
        double power = 0;
        try {
            power = getPowerModel().getPower(utilization);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return power;
    }

    /**
     * Gets the max power that can be consumed by the host.
     *
     * @return the max power
     */
    @Override
    public double getMaxPower() {
        double power = 0;
        try {
            power = getPowerModel().getPower(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return power;
    }

    /**
     * Gets the energy consumption using linear interpolation of the utilization
     * change.
     *
     * @param fromUtilization the initial utilization percentage
     * @param toUtilization the final utilization percentage
     * @param time the time
     * @return the energy
     */
    @Override
    public double getEnergyLinearInterpolation(double fromUtilization, double toUtilization, double time) {
        if (fromUtilization == 0) {
            return 0;
        }
        double fromPower = getPower(fromUtilization);
        double toPower = getPower(toUtilization);
        return (fromPower + (toPower - fromPower) / 2) * time;
    }

    @Override
    public final PowerHost setPowerModel(PowerModel powerModel) {
        this.powerModel = powerModel;
        return this;
    }

    @Override
    public PowerModel getPowerModel() {
        return powerModel;
    }

}
