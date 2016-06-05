package org.cloudbus.cloudsim;

import java.util.List;

/**
 * An interface to be implemented by Host classes that provide
 * dynamic workloads.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface HostDynamicWorkload extends Host {

    /**
     * Adds a host state history entry.
     *
     * @param time the time
     * @param allocatedMips the allocated mips
     * @param requestedMips the requested mips
     * @param isActive the is active
     */
    void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive);

    /**
     * Gets the list of completed vms.
     *
     * @return the completed vms
     */
    List<Vm> getCompletedVms();

    /**
     * Gets the max utilization percentage among by all PEs.
     *
     * @return the maximum utilization percentage
     */
    double getMaxUtilization();

    /**
     * Gets the max utilization percentage among by all PEs allocated to a VM.
     *
     * @param vm the vm
     * @return the max utilization percentage of the VM
     */
    double getMaxUtilizationAmongVmsPes(Vm vm);

    /**
     * Gets the previous utilization of CPU in mips.
     *
     * @return the previous utilization of CPU in mips
     */
    double getPreviousUtilizationMips();

    /**
     * Gets the previous utilization of CPU in percentage.
     *
     * @return the previous utilization of cpu in percents
     */
    double getPreviousUtilizationOfCpu();

    /**
     * Gets the host state history.
     *
     * @return the state history
     */
    List<HostStateHistoryEntry> getStateHistory();

    /**
     * Gets the utilization of CPU in MIPS.
     *
     * @return current utilization of CPU in MIPS
     */
    double getUtilizationMips();

    /**
     * Gets the utilization of bw (in absolute values).
     *
     * @return the utilization of bw
     */
    long getUtilizationOfBw();

    /**
     * Get current utilization of CPU in percentage.
     *
     * @return current utilization of CPU in percents
     */
    double getUtilizationOfCpu();

    /**
     * Get current utilization of CPU in MIPS.
     *
     * @return current utilization of CPU in MIPS
     * @todo This method only calls the  {@link #getUtilizationMips()}.
     * getUtilizationMips may be deprecated and its code copied here.
     */
    double getUtilizationOfCpuMips();

    /**
     * Gets the utilization of memory (in absolute values).
     *
     * @return the utilization of memory
     */
    int getUtilizationOfRam();
}
