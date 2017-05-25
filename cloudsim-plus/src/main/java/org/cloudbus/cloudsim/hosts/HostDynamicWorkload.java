/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/**
 * An interface to be implemented by Host classes that provide
 * dynamic workloads.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface HostDynamicWorkload extends Host {

    /**
     * Gets a <b>read-only</b> host state history.
     *
     * @return the state history
     */
    List<HostStateHistoryEntry> getStateHistory();

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
     * Gets the list of VMs that finished executing.
     *
     * @return
     */
    List<Vm> getFinishedVms();

    /**
     * Gets the max utilization percentage among (between [0 and 1], where 1 is 100%) by all PEs.
     *
     * @return the max utilization percentage (between [0 and 1])
     */
    double getMaxUtilization();

    /**
     * Gets the max utilization percentage (between [0 and 1]) among by all PEs allocated to a VM.
     *
     * @param vm the vm
     * @return the max utilization percentage (between [0 and 1])
     */
    double getMaxUtilizationAmongVmsPes(Vm vm);

    /**
     * Gets the previous utilization of CPU in percentage (between [0 and 1]).
     *
     * @return
     */
    double getPreviousUtilizationOfCpu();

    /**
     * Gets the previous utilization of CPU in MIPS.
     *
     * @return
     */
    double getPreviousUtilizationMips();
}
