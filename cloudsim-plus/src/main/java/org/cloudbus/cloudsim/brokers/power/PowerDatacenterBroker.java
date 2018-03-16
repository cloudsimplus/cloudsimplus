/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.brokers.power;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A power-aware {@link DatacenterBrokerSimple}.
 *
 * <br/>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br/>
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
public class PowerDatacenterBroker extends DatacenterBrokerSimple {

	/**
	 * Instantiates a new PowerDatacenterBroker.
	 *
	 * @param simulation The CloudSim instance that represents the simulation the Entity is related to
	 */
	public PowerDatacenterBroker(CloudSim simulation)  {
		super(simulation);
	}

	@Override
	protected boolean processVmCreateResponseFromDatacenter(SimEvent ev) {
        final Vm vm = (Vm) ev.getData();

		if (!vm.isCreated()) {
            Log.printConcatLine(getSimulation().clock() + ": " + getName() + ": Creation of VM #" + vm.getId()
                + " failed in Datacenter #" + vm.getHost().getDatacenter().getId());
		}
		return super.processVmCreateResponseFromDatacenter(ev);
	}

}
