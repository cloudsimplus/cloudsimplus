/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.examples;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.HostDynamicWorkloadSimple;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;

/**
 * An example showing how to create a Datacenter with two hosts,
 * with one Vm in each one, and run 1 cloudlet in each Vm.
 * At the end, it shows the total CPU utilization of hosts
 * into a Datacenter.
 *
 * <p>Cloudlets run in VMs with different MIPS requirements and will
 * take different times to complete the execution, depending on the requested VM
 * performance.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HostsCpuUsageExample {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private List<HostDynamicWorkloadSimple> hostList;
    private DatacenterBroker broker;
    private CloudSim simulation;

    private static final int NUMBER_OF_VMS = 2;
    private static final int NUMBER_OF_HOSTS = 2;

    /**
     * Starts the example execution
     * @param args
     */
    public static void main(String[] args) {
        new HostsCpuUsageExample();
    }

    public HostsCpuUsageExample(){
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());

        simulation = new CloudSim();

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        broker = new DatacenterBrokerSimple(simulation);

        vmlist = new ArrayList<>(NUMBER_OF_VMS);
        cloudletList = new ArrayList<>(NUMBER_OF_VMS);

        final int pesNumber = 1; //number of cpus
        final int mips = 1000;
        for (int i = 1; i <= NUMBER_OF_VMS; i++) {
            Vm vm = createVm(pesNumber, mips*i,i-1);
            vmlist.add(vm);

            Cloudlet cloudlet = createCloudlet(pesNumber, i-1);
            cloudletList.add(cloudlet);
        }

        //Link each Cloudlet to a spacific VM
        for (int i = 0; i < NUMBER_OF_VMS; i++) {
            broker.bindCloudletToVm(cloudletList.get(i), vmlist.get(i));
        }

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        final double finishTime = simulation.start();

        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();

        showCpuUtilizationForAllHosts();

        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private Cloudlet createCloudlet(int pesNumber, int id) {
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        return new CloudletSimple(id, length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel);
    }

    private Vm createVm(int pesNumber, long mips, int id) {
        long size = 10000; //image size (MEGABYTE)
        int ram = 2048; //vm memory (MEGABYTE)
        long bw = 1000;
        return new VmSimple(id, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Shows CPU utilization of all hosts into a given Datacenter.
     */
    private void showCpuUtilizationForAllHosts() {
        Log.printLine("\nHosts CPU utilization history for the entire simulation period");
        int numberOfUsageHistoryEntries = 0;
        final double interval = 1;
        for (HostDynamicWorkloadSimple host : hostList) {
            double mipsByPe = host.getTotalMipsCapacity() / (double)host.getNumberOfPes();
            Log.printFormattedLine("Host %d: Number of PEs %2d, MIPS by PE %.0f", host.getId(), host.getNumberOfPes(), mipsByPe);
            for(HostStateHistoryEntry history: host.getStateHistory()){
                    numberOfUsageHistoryEntries++;
                    Log.printFormattedLine(
                            "\tTime: %2.0f CPU Utilization (MIPS): %.0f",
                            history.getTime(), history.getAllocatedMips());
            }
            Log.printLine("--------------------------------------------------");
        }
        if(numberOfUsageHistoryEntries == 0) {
            Log.printLine(" No CPU usage history was found");
        }
    }

    private Datacenter createDatacenter() {
        hostList = new ArrayList<>(NUMBER_OF_HOSTS);
        int pesNumber = 1;
        int mips = 1200;
        for (int i = 1; i <= 2; i++) {
            HostDynamicWorkloadSimple host = createHosts(pesNumber, mips*i, i-1);
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    private HostDynamicWorkloadSimple createHosts(int pesNumber, long mips, int hostId) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        long ram = 2048; //host memory (MEGABYTE)
        long storage = 1000000; //host storage (MEGABYTE)
        long bw = 10000; //Megabits/s

        HostDynamicWorkloadSimple host = new HostDynamicWorkloadSimple(ram, bw, storage, peList);
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

}
