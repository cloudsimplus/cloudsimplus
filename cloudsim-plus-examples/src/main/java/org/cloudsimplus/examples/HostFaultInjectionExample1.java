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
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.faultinjection.VmClonerSimple;

/**
 * Example which shows how to inject random {@link Pe} faults into Hosts using
 * {@link HostFaultInjection} objects.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 */
public final class HostFaultInjectionExample1 {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 0;
    private static final double DATACENTER_COST_PER_CPU = 3.0;
    private static final double DATACENTER_COST_PER_RAM = 0.05;
    private static final double DATACENTER_COST_PER_STORAGE = 0.001;
    private static final double DATACENTER_COST_PER_BW = 0.0;

    private static final int HOST_MIPS_BY_PE = 1000;
    private static final int HOST_PES = 4;
    private static final long HOST_RAM = 500000; //host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;
    /*The average number of failures expected to happen each hour
    in a Poisson Process, which is also called event rate or rate parameter.*/
    public static final double MEAN_FAILURE_NUMBER_PER_HOUR = 0.02;
    private List<Host> hostList;

    /**
     * The percentage of host CPU usage that trigger VM migration due to over
     * utilization (in scale from 0 to 1, where 1 is 100%).
     */
    private static final double HOST_USAGE_THRESHOLD_VM_MIGRATION = 0.5;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MEGABYTE)
    private static final int VM_RAM = 10000; //vm memory (MEGABYTE)
    private static final long VM_BW = 100000;
    private static final int VM_PES = 2; //number of cpus

    private static final int CLOUDLET_PES = 2;
    private static final long CLOUDLET_LENGHT = 2800_000_000L;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * Number of Hosts to create for each Datacenter. The number of elements in
     * this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 10;
    private static final int VMS = 2;

    private static final int CLOUDLETS = 6;

    private final List<Vm> vmList = new ArrayList<>(VMS);
    private final List<Cloudlet> cloudletList = new ArrayList<>(CLOUDLETS);
    private CloudSim simulation;
    private final DatacenterBroker broker;

    HostFaultInjection fault;
    /**
     * The Poisson Random Number Generator used to generate failure times (in hours).
     */
    private PoissonDistr poisson;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new HostFaultInjectionExample1();
    }

    public HostFaultInjectionExample1() {
        Log.printConcatLine("Starting ", getClass().getSimpleName(), "...");

        simulation = new CloudSim();
        Datacenter datacenter = createDatacenter(HOSTS);

        broker = new DatacenterBrokerSimple(simulation);
        createAndSubmitVms();
        createAndSubmitCloudlets();
        createFaultInjectionForHosts(datacenter);

        simulation.start();
        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();

        System.out.printf(
            "Mean Number of Failures per Hour: %.3f (1 failure expected at each %.2f hours).\n",
            MEAN_FAILURE_NUMBER_PER_HOUR, poisson.getInterarrivalMeanTime());
        System.out.printf("# Number of Host faults: %d\n", fault.getNumberOfHostFaults());
        System.out.printf("# Number of VM faults (VMs destroyed): %d\n", fault.getNumberOfFaults());
        System.out.printf("# Time the simulations finished: %.4f hours\n", simulation.clockInHours());
        System.out.printf("# Mean Time To Repair Failures of VMs in minutes (MTTR): %.2f minute\n", fault.meanTimeToRepairVmFaultsInMinutes());
        System.out.printf("# Mean Time Between Failures (MTBF) affecting all VMs in minutes: %.2f minutes\n", fault.meanTimeBetweenVmFaultsInMinutes());
        System.out.printf("# Hosts MTBF: %.2f minutes\n", fault.meanTimeBetweenHostFaultsInMinutes());
        System.out.printf("# Availability: %.2f%%\n", fault.availability()*100);


        Log.printConcatLine(getClass().getSimpleName(), " finished!");
    }

    public void createAndSubmitVms() {
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
            vmList.add(vm);
        }
        broker.submitVmList(vmList);
    }

    public Vm createVm() {
        Vm vm = new VmSimple(vmList.size()+1, VM_MIPS, VM_PES);
        vm
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in {@link #CLOUDLETS} and submits
     * them to the created broker.
     *
     */
    public void createAndSubmitCloudlets() {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet c
                    = new CloudletSimple(cloudletList.size()+1, CLOUDLET_LENGHT, CLOUDLET_PES)
                            .setFileSize(CLOUDLET_FILESIZE)
                            .setOutputSize(CLOUDLET_OUTPUTSIZE)
                            .setUtilizationModel(utilizationModel);
            cloudletList.add(c);
        }

        broker.submitCloudletList(cloudletList);
    }

    private Datacenter createDatacenter(int numberOfHosts) {
        hostList = new ArrayList<>();
        for (int i = 0; i < numberOfHosts; i++) {
            hostList.add(createHost());
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                        .setCostPerSecond(DATACENTER_COST_PER_CPU)
                        .setCostPerMem(DATACENTER_COST_PER_RAM)
                        .setCostPerStorage(DATACENTER_COST_PER_STORAGE)
                        .setCostPerBw(DATACENTER_COST_PER_BW);

        Datacenter dc = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc
                .setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS)
                .setLog(false);
        return dc;
    }

    /**
     * Creates a Host.
     *
     * @return
     */
    public Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(HOST_MIPS_BY_PE, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    public List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter
     */
    private void createFaultInjectionForHosts(Datacenter datacenter) {
        //final long seed = System.currentTimeMillis();
        final long seed = 112717613L;
        this.poisson = new PoissonDistr(MEAN_FAILURE_NUMBER_PER_HOUR, seed);

        fault = new HostFaultInjection(datacenter, poisson);
        fault.setMaxTimeToGenerateFailureInHours(800);

        this.vmList.stream().forEach(vm -> fault.addVmCloner(broker, new VmClonerSimple(this::cloneVm, this::cloneCloudlets)));
    }

    /**
     * Clones a VM by creating another one with the same configurations of a
     * given VM.
     *
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     *
     * @see
     * #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private Vm cloneVm(Vm vm) {
        Vm clone = new VmSimple((long) vm.getMips(), (int) vm.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the vm to its clone,
        since the clone ID will be 10 times the id of its
        source VM.*/
        clone.setId(vm.getId() * 10);
        clone.setDescription("Clone of VM " + vm.getId());
        clone
                .setSize(vm.getStorage().getCapacity())
                .setBw(vm.getBw().getCapacity())
                .setRam(vm.getBw().getCapacity())
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        Log.printFormattedLine("\n\n#Cloning VM %d\n\tMips %.2f Number of Pes: %d ", vm.getId(), clone.getMips(), clone.getNumberOfPes());

        return clone;
    }

    /**
     * Clones each Cloudlet associated to a given VM. The method is called when
     * a VM is destroyed due to a Host failure and a snapshot from that VM (a
     * clone) is started into another Host. In this case, all the Cloudlets
     * which were running inside the destroyed VM will be recreated from scratch
     * into the VM clone, re-starting their execution from the beginning.
     *
     * @param sourceVm the VM to clone its Cloudlets
     * @return the List of cloned Cloudlets.
     * @see
     * #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private List<Cloudlet> cloneCloudlets(Vm sourceVm) {
        final List<Cloudlet> sourceVmCloudlets = sourceVm.getCloudletScheduler().getCloudletList();
        final List<Cloudlet> clonedCloudlets = new ArrayList<>(sourceVmCloudlets.size());
        for (Cloudlet cl : sourceVmCloudlets) {
            Cloudlet clone = cloneCloudlet(cl);
            clonedCloudlets.add(clone);
            Log.printFormattedLine("#Created Cloudlet Clone for VM %d (Cloudlet Clone Id: %d)", sourceVm.getId(), clone.getId());
        }

        return clonedCloudlets;
    }

    /**
     * Creates a clone from a given Cloudlet.
     *
     * @param source the Cloudlet to be cloned.
     * @return the cloned (new) cloudlet
     */
    private Cloudlet cloneCloudlet(Cloudlet source) {
        Cloudlet clone
                = new CloudletSimple(
                        source.getLength(),
                        (int) source.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the cloudlet to its clone,
        since the clone ID will be 10 times the id of its
        source cloudlet.*/
        clone.setId(source.getId() * 10);
        clone
                .setUtilizationModelBw(source.getUtilizationModelBw())
                .setUtilizationModelCpu(source.getUtilizationModelCpu())
                .setUtilizationModelRam(source.getUtilizationModelRam());
        return clone;
    }

}
