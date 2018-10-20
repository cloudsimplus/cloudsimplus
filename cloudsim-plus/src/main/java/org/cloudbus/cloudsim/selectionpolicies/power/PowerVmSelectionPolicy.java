/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.selectionpolicies.power;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An abstract VM selection policy used to select VMs from a list of migratable VMs.
 * The selection is defined by sub classes.
 *
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public abstract class PowerVmSelectionPolicy {

	/**
	 * Gets a VM to migrate from a given host.
	 *
	 * @param host the host to get a Vm to migrate from
	 * @return the vm to migrate or {@link Vm#NULL} if there is not Vm to migrate
	 */
	public abstract Vm getVmToMigrate(Host host);
}
