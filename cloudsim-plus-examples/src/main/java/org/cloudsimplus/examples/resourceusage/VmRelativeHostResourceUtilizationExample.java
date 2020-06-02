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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing the relative percentage of each resource
 * every VM is using from its Host.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class VmRelativeHostResourceUtilizationExample {
    /**
     * @see Datacenter#getSchedulingInterval()
     */
    private static final int  SCHEDULING_INTERVAL = 2;

    private static final int  HOSTS = 1;
    private static final int  HOST_PES = 16;
    private static final long HOST_RAM = 10_000; //in Megabytes
    private static final long HOST_BW = 100_000; //in Megabits/s
    private static final int  HOST_MIPS = 1000;

    private static final int  VMS = 2;
    private static final int  VM_PES = 4;
    private static final long VM_RAM = HOST_RAM/VMS; //in Megabytes
    private static final long VM_BW = HOST_BW/VMS; //in Megabits/s

    private static final int  CLOUDLETS = VMS;
    private static final int  CLOUDLET_PES = VM_PES;
    private static final int  CLOUDLET_LENGTH = 10_000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new VmRelativeHostResourceUtilizationExample();
    }

    private VmRelativeHostResourceUtilizationExample() {
        Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        simulation.addOnClockTickListener(this::onClockTick);
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private void onClockTick(final EventInfo info) {
        for (Host host : datacenter0.getHostList()) {
            System.out.printf("Host %-2d Time: %.0f%n", host.getId(), info.getTime());
            for (Vm vm : host.getVmList()) {
                System.out.printf(
                    "\tVm %2d: Host's CPU utilization: %5.0f%% | Host's RAM utilization: %5.0f%% | Host's BW utilization: %5.0f%%%n",
                    vm.getId(), vm.getHostCpuUtilization()*100, vm.getHostRamUtilization()*100, vm.getHostBwUtilization()*100);
            }
            System.out.printf(
                "Host %-2d Total Utilization:         %5.0f%% |                         %5.0f%% |                        %5.0f%%%n%n",
                host.getId(),host.getCpuPercentUtilization()*100,
                host.getRam().getPercentUtilization()*100,
                host.getBw().getPercentUtilization()*100);
        }
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList).setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS));
        }

        final long storage = 1000000; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(HOST_RAM, HOST_BW, storage, peList);
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            final Vm vm = new VmSimple(HOST_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(10000);
            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        //UtilizationModel defining the Cloudlets use only 50% of RAM and BW all the time
        final UtilizationModelDynamic utilizationModelRamAndBw = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
            cloudlet.setUtilizationModelCpu(new UtilizationModelFull())
                    .setUtilizationModelBw(utilizationModelRamAndBw)
                    .setUtilizationModelRam(utilizationModelRamAndBw)
                    .setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }
}
