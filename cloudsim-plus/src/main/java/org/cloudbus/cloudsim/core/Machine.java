package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface to be implemented by different kinds of Physical Machines (PMs).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public interface Machine extends AbstractMachine {
    /**
     * Computes the current relative percentage of the CPU the VM is using from the Machine's total MIPS Capacity.
     * If the capacity is 1000 MIPS and the VM is using 250 MIPS, it's equivalent to 25%
     * of the Machines's capacity.
     *
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    default double getRelativeCpuUtilization(final Vm vm) {
        return getExpectedRelativeCpuUtilization(vm, vm.getCpuPercentUtilization());
    }

    /**
     * Computes what would be the relative percentage of the CPU the VM is using from a Machine's total MIPS Capacity,
     * considering that the VM 's CPU load is at a given percentage.
     * @param vm the VM to get its relative percentage of CPU utilization
     * @param vmCpuUtilizationPercent the VM's CPU utilization percentage for a given time
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    default double getExpectedRelativeCpuUtilization(final Vm vm, final double vmCpuUtilizationPercent){
        return vmCpuUtilizationPercent * getRelativeMipsCapacityPercent(vm);
    }

    /**
     * Gets the percentage of the MIPS capacity a VM represents from the total Machine's MIPS capacity.
     *
     * @return the VM relative MIPS capacity percentage
     */
    default double getRelativeMipsCapacityPercent(final Vm vm) {
        return vm.getTotalMipsCapacity() / getTotalMipsCapacity();
    }

    /**
     * Computes the relative percentage of the RAM a VM is using from a Machine's total Capacity
     * for the current simulation time.
     *
     * @param vm the {@link Vm} to compute the relative utilization of RAM from this machine
     * @return the relative VM RAM usage percent (from 0 to 1)
     */
    default double getRelativeRamUtilization(final Vm vm){
        return vm.getRam().getPercentUtilization() * getRam().getPercentUtilization();
    }

    /**
     * Computes the relative percentage of the BW a VM is using from a Machine's total Capacity
     * for the current simulation time.
     *
     * @param vm the {@link Vm} to compute the relative utilization of BW from this machine
     * @return the relative VM BW usage percent (from 0 to 1)
     */
    default double getRelativeBwUtilization(final Vm vm){
        return vm.getBw().getPercentUtilization() * getBw().getPercentUtilization();
    }
}
