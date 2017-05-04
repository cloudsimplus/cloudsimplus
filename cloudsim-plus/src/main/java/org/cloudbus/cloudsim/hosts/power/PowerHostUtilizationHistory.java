/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.hosts.power;

import java.util.List;

import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudbus.cloudsim.resources.Pe;

import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A host that stores its CPU utilization percentage history. The history is used by VM allocation
 * and selection policies.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:</p>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerHostUtilizationHistory extends PowerHostSimple {
    /**
     * Creates a PowerHostUtilizationHistory.
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     */
    public PowerHostUtilizationHistory(long ram, long bw, long storage, List<Pe> peList) {
        super(ram, bw, storage, peList);
    }

	/**
	 * Creates a PowerHostUtilizationHistory with the given parameters.
	 *
	 * @param id the host id
     * @param ramProvisioner the ram provisioner with capacity in MEGABYTE
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in MEGABYTE
	 * @param peList the host's PEs list
	 * @param vmScheduler the vm scheduler
	 * @param powerModel the power consumption model
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
	 */
	@Deprecated
    public PowerHostUtilizationHistory(
			int id,
			ResourceProvisioner ramProvisioner,
			ResourceProvisioner bwProvisioner,
			long storage,
			List<Pe> peList,
			VmScheduler vmScheduler,
			PowerModel powerModel)
    {
		this(ramProvisioner.getCapacity(), bwProvisioner.getCapacity(), storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setVmScheduler(vmScheduler);
        setPowerModel(powerModel);

    }

	/**
	 * Gets the host CPU utilization percentage history.
     * @return 
	 */
	public double[] getUtilizationHistory() {
		double[] utilizationHistory = new double[PowerVm.MAX_HISTORY_ENTRIES];
		double hostMips = getTotalMipsCapacity();
		for (final PowerVm vm : this.<PowerVm>getVmList()) {
			for (int i = 0; i < vm.getUtilizationHistory().size(); i++) {
				utilizationHistory[i] += vm.getUtilizationHistory().get(i) * vm.getMips() / hostMips;
			}
		}
		return MathUtil.trimZeroTail(utilizationHistory);
	}

}
