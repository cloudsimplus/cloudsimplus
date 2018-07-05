/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.examples.resourceusage;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to create a Datacenter with two hosts and run
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
    private static final int VMS_NUMBER = 2;
    private static final int HOSTS_NUMBER = 2;

    /**
     * Starts the example execution.
     * @param args
     */
    public static void main(String[] args) {
        new VmsCpuUsageExample();
    }

    public VmsCpuUsageExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        CloudSim simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter(simulation);

        broker = new DatacenterBrokerSimple(simulation);

        vmlist = new ArrayList<>(VMS_NUMBER);
        cloudletList = new ArrayList<>(VMS_NUMBER);

        int mips = 1000;
        int pesNumber = 1;
        for (int i = 1; i <= VMS_NUMBER; i++) {
            Vm vm = createVm(pesNumber, mips * i, i - 1);
            vmlist.add(vm);
            Cloudlet cloudlet = createCloudlet(pesNumber, i-1);
            cloudletList.add(cloudlet);
            cloudlet.setVm(vm);
        }

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        final double finishTime = simulation.start();

        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();

        showCpuUtilizationForAllVms(finishTime);

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private Cloudlet createCloudlet(int pesNumber, int id) {
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(0.25);
        UtilizationModel utilizationModelCpu = new UtilizationModelFull();

        Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber);
        cloudlet.setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(utilizationModelCpu)
            .setUtilizationModelBw(utilizationModelDynamic)
            .setUtilizationModelRam(utilizationModelDynamic);
        return cloudlet;
    }

    private Vm createVm(int pesNumber, int mips, int id) {
        long size = 10000; //image size (Megabyte)
        int ram = 2048; //vm memory (Megabyte)
        long bw = 1000;

        //create two VMs
        Vm vm = new VmSimple(id, mips, pesNumber);
        vm.setRam(ram).setBw(bw)
            .setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    private void showCpuUtilizationForAllVms(final double simulationFinishTime) {
        System.out.println("\nHosts CPU utilization history for the entire simulation period\n");
        int numberOfUsageHistoryEntries = 0;
        for (Vm vm : vmlist) {
            System.out.printf("VM %d\n", vm.getId());
            if (vm.getStateHistory().isEmpty()) {
                System.out.println("\tThere isn't any usage history");
                continue;
            }

            for (int clock = 0; clock <= simulationFinishTime; clock+=5) {
                final double vmCpuUsage = getVmCpuUtilizationInMips(vm, simulationFinishTime);
                if (vmCpuUsage > 0) {
                    numberOfUsageHistoryEntries++;
                    System.out.printf("\tTime: %2d CPU Utilization (MIPS): %.2f\n", clock, vmCpuUsage);
                }
            }
        }

        if (numberOfUsageHistoryEntries == 0) {
            System.out.println("No CPU usage history was found");
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
        List<Host> hostList = new ArrayList<>(HOSTS_NUMBER);
        final int pesNumber = 1;
        final int mips = 1000;
        for (int i = 1; i <= HOSTS_NUMBER; i++) {
            Host host = createHost(pesNumber, mips*i, i-1);
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private static Host createHost(int pesNumber, long mips, int hostId) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        final int ram = 2048; //host memory (Megabyte)
        final long storage = 1000000; //host storage
        final int bw = 10000;

        final Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        host.enableStateHistory();
        return host;
    }
}
