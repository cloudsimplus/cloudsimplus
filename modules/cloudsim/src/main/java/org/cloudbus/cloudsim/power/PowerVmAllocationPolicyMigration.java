package org.cloudbus.cloudsim.power;

/**
 * An interface to be implemented by VM allocation policy for Power-aware VMs.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface PowerVmAllocationPolicyMigration extends PowerVmAllocationPolicy {
    /**
     * Checks if host is over utilized.
     *
     * @param host the host
     * @return true, if the host is over utilized; false otherwise
     */
    boolean isHostOverUtilized(PowerHostSimple host);
}
