/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;
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
     * Instantiates a new PowerHost.
     *
     * @param id the id of the host
     * @param ramProvisioner the ram provisioner
     * @param bwProvisioner the bw provisioner
     * @param storage the storage capacity
     * @param peList the host's PEs list
     * @param vmScheduler the VM scheduler
     * @param powerModel the model of power consumption
     */
    public PowerHostSimple(
            int id,
            ResourceProvisioner<Integer> ramProvisioner,
            ResourceProvisioner<Long> bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler,
            PowerModel powerModel) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
        setPowerModel(powerModel);
    }

    /**
     * Gets the power. For this moment only consumed by all PEs.
     *
     * @return the power
     */
    @Override
    public double getPower() {
        return getPower(getUtilizationOfCpu());
    }

    /**
     * Gets the current power consumption of the host. For this moment only
     * consumed by all PEs.
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

    /**
     * Sets the power model.
     *
     * @param powerModel the new power model
     */
    protected final void setPowerModel(PowerModel powerModel) {
        this.powerModel = powerModel;
    }

    /**
     * Gets the power model.
     *
     * @return the power model
     */
    @Override
    public PowerModel getPowerModel() {
        return powerModel;
    }

}
