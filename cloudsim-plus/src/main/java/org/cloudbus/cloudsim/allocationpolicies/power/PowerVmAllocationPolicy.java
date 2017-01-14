/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.vms.power.PowerVm;

/**
 * An interface to be implemented by each class that represents a policy used by
 * a {@link PowerDatacenter} to choose a {@link PowerHost} to place or migrate a
 * given {@link PowerVm} considering the Host power consumption.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface PowerVmAllocationPolicy extends VmAllocationPolicy{
    /**
     * Finds the first host that has enough resources to host a given VM.
     *
     * @param vm the vm to find a host for it
     * @return the first host found that can host the VM or {@link PowerHost#NULL} if no suitable
     * Host was found for Vm
     */
    PowerHost findHostForVm(Vm vm);
}
