/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * PowerDatacenterNonPowerAware is a class that represents a <b>non-power</b>
 * aware data center in the context of power-aware simulations.
 *
 * <br/>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:<br/>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class PowerDatacenterNonPowerAware extends PowerDatacenter {

    /**
     * Instantiates a new datacenter.
     *
     * @param name the datacenter name
     * @param characteristics the datacenter characteristics
     * @param schedulingInterval the scheduling interval
     * @param vmAllocationPolicy the vm provisioner
     * @param storageList the storage list
     *
     * @throws Exception the exception
     */
    public PowerDatacenterNonPowerAware(
            String name,
            DatacenterCharacteristics characteristics,
            VmAllocationPolicy vmAllocationPolicy,
            List<FileStorage> storageList,
            double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }

    @Override
    protected void updateCloudletProcessing() {
        if (getCloudletSubmitted() == -1 || getCloudletSubmitted() == CloudSim.clock()) {
            CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT));
            schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            return;
        }
        double currentTime = CloudSim.clock();
        double timeframePower = 0.0;

        if (currentTime > getLastProcessTime()) {
            double timeDiff = currentTime - getLastProcessTime();
            double minTime = Double.MAX_VALUE;

            Log.printLine("\n");

            for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
                Log.printFormattedLine("%.2f: Host #%d", CloudSim.clock(), host.getId());

                double hostPower = 0.0;

                try {
                    hostPower = host.getMaxPower() * timeDiff;
                    timeframePower += hostPower;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.printFormattedLine(
                        "%.2f: Host #%d utilization is %.2f%%",
                        CloudSim.clock(),
                        host.getId(),
                        host.getUtilizationOfCpu() * 100);
                Log.printFormattedLine(
                        "%.2f: Host #%d energy is %.2f W*sec",
                        CloudSim.clock(),
                        host.getId(),
                        hostPower);
            }

            Log.printFormattedLine("\n%.2f: Consumed energy is %.2f W*sec\n", CloudSim.clock(), timeframePower);

            Log.printLine("\n\n--------------------------------------------------------------\n\n");

            for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
                Log.printFormattedLine("\n%.2f: Host #%d", CloudSim.clock(), host.getId());

                double time = host.updateVmsProcessing(currentTime); // inform VMs to update
                // processing
                if (time < minTime) {
                    minTime = time;
                }
            }

            setPower(getPower() + timeframePower);

            checkCloudletsCompletionForAllHosts();

            /**
             * Remove completed VMs *
             */
            for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
                for (Vm vm : host.getCompletedVms()) {
                    getVmAllocationPolicy().deallocateHostForVm(vm);
                    getVmList().remove(vm);
                    Log.printLine("VM #" + vm.getId() + " has been deallocated from host #" + host.getId());
                }
            }

            Log.printLine();

            if (!isDisableMigrations()) {
                Map<Vm, Host> migrationMap
                        = getVmAllocationPolicy().optimizeAllocation(getVmList());

                for (Entry<Vm, Host> entry : migrationMap.entrySet()) {
                    Host targetHost = entry.getValue();
                    Host oldHost = entry.getKey().getHost();

                    if (oldHost == null) {
                        Log.printFormattedLine(
                            "%.2f: Migration of VM #%d to Host #%d is started",
                            CloudSim.clock(),
                            entry.getKey().getId(),
                            targetHost.getId());
                    } else {
                        Log.printFormattedLine(
                            "%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
                            CloudSim.clock(),
                            entry.getKey().getId(),
                            oldHost.getId(),
                            targetHost.getId());
                    }

                    targetHost.addMigratingInVm(entry.getKey());
                    incrementMigrationCount();

                    /* VM migration delay = RAM / bandwidth + C (C = 10 sec) */
                    send(
                        getId(),
                        entry.getKey().getRam() / ((double) entry.getKey().getBw() / 8000) + 10,
                        CloudSimTags.VM_MIGRATE, entry);
                }
            }

            // schedules an event to the next time
            if (minTime != Double.MAX_VALUE) {
                CloudSim.cancelAll(getId(), new PredicateType(CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT));
                // CloudSim.cancelAll(getId(), CloudSim.SIM_ANY);
                send(getId(), getSchedulingInterval(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            }

            setLastProcessTime(currentTime);
        }
    }

}
