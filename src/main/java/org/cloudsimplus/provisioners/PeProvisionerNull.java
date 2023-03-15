package org.cloudsimplus.provisioners;

import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link PeProvisioner} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see PeProvisioner#NULL
 */
final class PeProvisionerNull extends ResourceProvisionerNull implements PeProvisioner {
    @Override public void setPe(Pe pe) {/**/}
    @Override public double getUtilization() {
        return 0;
    }
    @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource) {
        return false;
    }
}
