/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to create a dc with two hosts and run
 * two cloudlets on it. At the end, it shows the total CPU utilization of VMs
 * into a Datacenter.
 *
 * <p>
 * Cloudlets run in VMs with different MIPS requirements and will
 * take different times to complete the execution, depending on the requested VM
 * performance.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class VmsCpuUsageExample {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private DatacenterBroker broker;
    private static final int NUMBER_OF_VMS = 2;
    private static final int NUMBER_OF_HOSTS = 2;

    /**
     * Starts the example execution.
     * @param args
     */
    public static void main(String[] args) {
        new VmsCpuUsageExample();
    }

    public VmsCpuUsageExample() {
        Log.printLine("Starting VmsCpuUsageExample...");
        CloudSim simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter(simulation);

        broker = new DatacenterBrokerSimple(simulation);

        vmlist = new ArrayList<>(NUMBER_OF_VMS);
        cloudletList = new ArrayList<>(NUMBER_OF_VMS);

        int mips = 1000;
        int pesNumber = 1;
        for (int i = 1; i <= NUMBER_OF_VMS; i++) {
            Vm vm = createVm(pesNumber, mips * i, i - 1);
            vmlist.add(vm);
            Cloudlet cloudlet = createCloudlet(pesNumber, i-1);
            cloudletList.add(cloudlet);
            cloudlet.setVm(vm);
        }

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        final double finishTime = simulation.start();

        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(broker.getCloudletsFinishedList()).build();

        showCpuUtilizationForAllVms(finishTime);

        Log.printLine("VmsCpuUsageExample finished!");
    }

    private Cloudlet createCloudlet(int pesNumber, int id) {
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber);
        cloudlet.setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setBroker(broker);
        return cloudlet;
    }

    private Vm createVm(int pesNumber, int mips, int id) {
        long size = 10000; //image size (MB)
        int ram = 2048; //vm memory (MB)
        long bw = 1000;

        //create two VMs
        Vm vm = new VmSimple(id, mips, pesNumber);
        vm.setRam(ram).setBw(bw)
            .setSize(size)
            .setBroker(broker)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    private void showCpuUtilizationForAllVms(final double simulationFinishTime) {
        Log.printLine("\nHosts CPU utilization history for the entire simulation period");
        int numberOfUsageHistoryEntries = 0;
        for (Vm vm : vmlist) {
            Log.printFormattedLine("VM %d", vm.getId());
            if (vm.getStateHistory().isEmpty()) {
                Log.printLine("\tThere isn't any usage history");
                continue;
            }

            for (int clock = 0; clock <= simulationFinishTime; clock+=5) {
                final double vmCpuUsage = getVmCpuUtilizationInMips(vm, simulationFinishTime);
                if (vmCpuUsage > 0) {
                    numberOfUsageHistoryEntries++;
                    Log.printFormattedLine("\tTime: %2d CPU Utilization (MIPS): %.2f", clock, vmCpuUsage);
                }
            }
        }

        if (numberOfUsageHistoryEntries == 0) {
            Log.printLine(" No CPU usage history was found");
        }
    }

    private double getVmCpuUtilizationInMips(Vm vm, double time) {
        for (VmStateHistoryEntry state : vm.getStateHistory()) {
            /*subtract the two times just to avoid precision issues.
            For instance, the registered time in the history
            may be 160.1 while the desired time may be 160.0.
            This if ignores this difference and considers the times are equals.
            */
            if (Math.abs(state.getTime() - time) <= 0.1) {
                return state.getAllocatedMips();
            }
        }

        return 0;
    }

    private static Datacenter createDatacenter(CloudSim simulation) {
        List<Host> hostList = new ArrayList<>(NUMBER_OF_HOSTS);
        final int pesNumber = 1;
        final int mips = 1000;
        for (int i = 1; i <= NUMBER_OF_HOSTS; i++) {
            Host host = createHost(pesNumber, mips*i, i-1);
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    private static Host createHost(int pesNumber, double mips, int hostId) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 10000;

        return new HostDynamicWorkloadSimple(hostId, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }
}
