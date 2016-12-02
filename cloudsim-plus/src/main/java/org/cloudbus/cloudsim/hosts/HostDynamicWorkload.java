package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.vms.Vm;

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
     * @return
     */
    List<Vm> getCompletedVms();

    /**
     * Gets the max utilization percentage among by all PEs.
     *
     * @return
     */
    double getMaxUtilization();

    /**
     * Gets the max utilization percentage among by all PEs allocated to a VM.
     *
     * @param vm the vm
     * @return
     */
    double getMaxUtilizationAmongVmsPes(Vm vm);

    /**
     * Gets the previous utilization of CPU in mips.
     *
     * @return
     */
    double getPreviousUtilizationMips();

    /**
     * Gets the previous utilization of CPU in percentage.
     *
     * @return
     */
    double getPreviousUtilizationOfCpu();

    /**
     * Gets a <b>read-only</b> host state history.
     *
     * @return the state history
     */
    List<HostStateHistoryEntry> getStateHistory();

    /**
     * Gets the current utilization of bw (in absolute values).
     *
     * @return
     */
    long getUtilizationOfBw();

    /**
     * Gets current utilization of CPU in percentage.
     *
     * @return
     */
    double getUtilizationOfCpu();

    /**
     * Gets the current utilization of CPU in MIPS.
     *
     * @return
     */
    double getUtilizationOfCpuMips();

    /**
     * Gets the current utilization of memory (in absolute values).
     *
     * @return
     */
    long getUtilizationOfRam();
}
