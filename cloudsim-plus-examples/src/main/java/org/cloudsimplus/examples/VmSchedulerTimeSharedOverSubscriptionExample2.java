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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.power.PowerDatacenter;
import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import sun.java2d.loops.ProcessPath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing the usage of the {@link VmSchedulerTimeSharedOverSubscription}
 * which allows over-subscription of Host's PEs.
 * When running VMs request more MIPS than available into a Host,
 * they will be allowed to run but with a reduced amount of MIPS.
 *
 * <p>This example creates 1 VM for each Host PE
 * and one cloudlet for each VM. However, each
 * cloudlet requests 150% of the MIPS capacity from the VM.
 * This way, the VM requests more MIPS from the Host than
 * the VM's total MIPS capacity.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class VmSchedulerTimeSharedOverSubscriptionExample2 {
    private static final int HOST_PES     = 4;
    private static final int VM_PES       = 1;
    private static final int CLOUDLET_PES = 1;

    private static final int VM_MIPS   = 1000;
    private static final int HOST_MIPS = VM_MIPS*2;

    private static final int CLOUDLET_LENGTH = VM_MIPS*10;

    private static final int HOSTS     = 1;
    private static final int VMS       = HOST_PES;
    private static final int CLOUDLETS = VMS;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<PowerHost> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private static final int SCHEDULING_INTERVAL = 1;

    public static void main(String[] args) {
        new VmSchedulerTimeSharedOverSubscriptionExample2();
    }

    public VmSchedulerTimeSharedOverSubscriptionExample2() {
        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLETS);

        simulation = new CloudSim();

        createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        new CloudletsTableBuilder(broker0.getCloudletsFinishedList())
            .addColumn(5, new TextTableColumn("Host MIPS", "total"), c -> c.getVm().getHost().getTotalMipsCapacity())
            .addColumn(8, new TextTableColumn("VM MIPS", "total"), c -> c.getVm().getTotalMipsCapacity())
            .addColumn(9, new TextTableColumn("  VM MIPS", "requested"), this::getVmRequestedMips)
            .addColumn(10, new TextTableColumn("  VM MIPS", "allocated"), this::getVmAllocatedMips)
            .build();

        System.out.println("\nHosts CPU usage History");
        hostList.forEach(this::printHostHistory);
    }

    private void printHostHistory(PowerHost h) {
        System.out.printf("Host: %d\n", h.getId());
        System.out.println("------------------------------------------------------------------------------------------");
        h.getStateHistory().stream().forEach(System.out::print);
        System.out.println();
    }

    private double getVmRequestedMips(Cloudlet c) {
        if(c.getVm().getStateHistory().isEmpty()){
            return 0;
        }

        return c.getVm().getStateHistory().get(c.getVm().getStateHistory().size()-1).getRequestedMips();
    }

    private double getVmAllocatedMips(Cloudlet c) {
        if(c.getVm().getStateHistory().isEmpty()){
            return 0;
        }

        return c.getVm().getStateHistory().get(c.getVm().getStateHistory().size()-1).getAllocatedMips();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for(int h = 0; h < HOSTS; h++) {
            PowerHost host = createHost();
            hostList.add(host);
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        Datacenter dc0 = new PowerDatacenter(simulation, characteristics, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private PowerHost createHost() {
        List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS, new PeProvisionerSimple()));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        PowerHost host = new PowerHostUtilizationHistory(ram, bw, storage, peList);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    /**
     * Creates a list of VMs for the {@link #broker0}.
     */
    private void createVms() {
        for (int i = 0; i < VMS; i++) {
            Vm vm =
                new PowerVm(VM_MIPS, VM_PES)
                    .setRam(512).setBw(1000).setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
    }

    /**
     * Creates a list of Cloudlets for the {@link #broker0}.
     */
    private void createCloudlets() {
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < CLOUDLETS; i++) {
            /*
             * The CPU usage is set as 150% of the available VM MIPS,
             * that will make the Cloudlet request more MIPS from the Host
             * than the VM has available, making the VM request more MIPS
             * from the Host.
             */
            UtilizationModelDynamic utilizationModelCpu = new UtilizationModelDynamic(1.5);
            Cloudlet cloudlet =
                new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModelRam(utilizationModel)
                    .setUtilizationModelBw(utilizationModel)
                    .setUtilizationModelCpu(utilizationModelCpu);
            cloudletList.add(cloudlet);
        }
    }

}
