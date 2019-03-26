package org.cloudbus.cloudsim.selectionpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A class that implements the Null Object Design Pattern for {@link VmSelectionPolicy}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmSelectionPolicy#NULL
 * @since CloudSim Plus 4.1.2
 */
final class VmSelectionPolicyNull implements VmSelectionPolicy {
    @Override public Vm getVmToMigrate(Host host) { return Vm.NULL; }
}
