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
package org.cloudsimplus.examples;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A example showing how to submit {@link VmGroup groups of VMs} to a broker
 * to try placing all VMs belonging to a group into the same Host.
 * It creates two groups but there is no Host
 * with enough capacity to place all VMs from the second group.
 * This way, only VMs from the first group are created.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 */
public class VmGroupPlacementExample1 {
    private static final int HOSTS = 4;

    /**
     * Number of {@link VmGroup} to create.
     */
    private static final int GROUPS = 2;

    /**
     * Number of {@link Vm}s in each {@link VmGroup}.
     */
    private static final int VMS_BY_GROUP = 2;


    private static final int BASE_HOST_RAM = 1000;
    private static final int BASE_HOST_BW  = 1000;
    private static final int BASE_HOST_STORAGE = 100_000;
    private static final int HOST_MIPS = 1000;

    private static final int VM_PES = 2;
    private static final int VM_RAM = 1200;
    private static final int VM_BW = 1200;
    private static final int VM_STORAGE = 10_000;

    private static final int CLOUDLET_LENGTH = 10_000;

    private final CloudSim simulation;
    private final DatacenterBroker broker0;

    /**
     * A {@link VmGroup} containing a List of VM to try to place them into the same Host.
     */
    private final List<VmGroup> vmGroupList;
    private final List<Cloudlet> cloudletList;
    private final Datacenter datacenter0;

    public static void main(String[] args) {
        new VmGroupPlacementExample1();
    }

    private VmGroupPlacementExample1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        cloudletList = new ArrayList<>();
        vmGroupList = createVmGroupList();
        for (VmGroup group : vmGroupList) {
            createCloudlets(group);
        }

        //You can submit either a List of Vm or a List of VmGroup.
        broker0.submitVmList(vmGroupList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(cl -> cl.getVm().getId()));
        new CloudletsTableBuilder(finishedCloudlets)
            .addColumn(7, new TextTableColumn("      VmGroup"), cl -> cl.getVm().getGroup())
            .build();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 1; i <= HOSTS; i++) {
            Host host = createHost(i, i);
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost(final long id, final int pes) {
        final List<Pe> peList = new ArrayList<>(pes);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < pes; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(HOST_MIPS));
        }

        //Host resources will be defined increasingly, according to the Host id.
        final long ram = BASE_HOST_RAM * id; //in Megabytes
        final long bw = BASE_HOST_BW * id; //in Megabits/s
        final long storage = BASE_HOST_STORAGE * id; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        final Host host = new HostSimple(ram, bw, storage, peList);
        host.setId(id);
        return host;
    }

    /**
     * Creates a list of {@link VmGroup}s.
     * Each group contains a List of VMs to try to place them into the same Host.
     */
    private List<VmGroup> createVmGroupList() {
        final List<VmGroup> groupList = new ArrayList<>(GROUPS);
        for (int i = 0; i < GROUPS; i++) {
            groupList.add(new VmGroup(createVms()));
        }

        return groupList;
    }

    private List<Vm> createVms() {
        final List<Vm> vmList = new ArrayList<>(VMS_BY_GROUP);
        for (int i = 0; i < VMS_BY_GROUP; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(HOST_MIPS, VM_PES);
            vm.setRam(VM_RAM).setBw(VM_BW).setSize(VM_STORAGE);
            vmList.add(vm);
        }

        return vmList;
    }

    /**
     * Creates a list of Cloudlets for a given group of VMs.
     * @param group
     */
    private void createCloudlets(final VmGroup group) {
        //UtilizationModel defining the Cloudlets use only 10% of RAM and BW all the time
        final UtilizationModel utilizationModelRamBw = new UtilizationModelDynamic(0.1);
        final UtilizationModel utilizationModelCpu = new UtilizationModelFull();

        for (Vm vm : group.getVmList()) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, VM_PES);
            cloudlet.setSizes(1024)
                    .setUtilizationModelCpu(utilizationModelCpu)
                    .setUtilizationModelRam(utilizationModelRamBw)
                    .setUtilizationModelBw(utilizationModelRamBw)
                    .setVm(vm);
            cloudletList.add(cloudlet);
        }
    }
}
