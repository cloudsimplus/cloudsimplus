/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.PoissonDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.faultinjection.VmClonerSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Example showing how to inject random {@link Pe} faults into Hosts using
 * {@link HostFaultInjection} objects.
 *
 * <p>It's considered that we have multiple VMs from a given customer (broker),
 * where all VMs are running the same services for load balancing and fault tolerance.
 * If a VM fails, since they are providing the same service and there are other equal VMs running,
 * there is no need for recovery.
 * If the failures don't demand recovery, no new VMs need to be started up, since no service was stopped.
 * </p>
 *
 * <p>If you run this example multiple times, it will give the same results.
 * Check {@link #createFaultInjectionForHosts(Datacenter)} to set
 * a different seed for the fault injection random number generator
 * to get different results at each run.</p>
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 */
public final class HostFaultInjectionExample1 {
    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 0;

    private static final int BROKERS = 1;

    /**
     * Number of Hosts to create for each Datacenter. The number of elements in
     * this array defines the number of Datacenters to be created.
     */
    private static final int HOSTS = 8;
    private static final int HOST_PES = 5;
    private static final int HOST_MIPS_BY_PE = 1000;
    private static final long HOST_RAM = 500000; //host memory (Megabyte)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    /**
     * The average number of failures expected to happen each hour
     * in a Poisson arrival process, which is also called event rate or rate parameter.
     * @see PoissonDistr */
    private static final double MEAN_FAILURE_NUMBER_PER_HOUR = 0.01;

    /** @see HostFaultInjection#setMaxTimeToFailInHours(double) */
    private static final double MAX_TIME_TO_FAIL_IN_HOURS = TimeUtil.daysToHours(30);

    private List<Host> hostList;

    private static final int VMS_BY_BROKER = 2;
    private static final int VM_PES = 2; //number of cpus
    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (Megabyte)
    private static final int VM_RAM = 10000; //vm memory (Megabyte)
    private static final long VM_BW = 100000;

    private static final int CLOUDLETS_BY_BROKER = 6;
    private static final int CLOUDLET_PES = 2;

    /**
     * The length has to be large to ensure the simulation will run for long enough,
     * so that faults will affect all VMs from certain broker.
     */
    private static final long CLOUDLET_LENGTH = 1_000_000_000L;

    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUT_SIZE = 300;

    private final CloudSim simulation;
    private final List<DatacenterBrokerSimple> brokerList;

    private int createdVms;
    private int createdCloudlets;

    private final HostFaultInjection fault;

    /**
     * The Poisson Random Number Generator used to generate failure times (in hours).
     */
    private PoissonDistr poisson;

    public static void main(String[] args) {
        new HostFaultInjectionExample1();
    }

    private HostFaultInjectionExample1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        final Datacenter datacenter = createDatacenter();

        brokerList = IntStream.range(0, BROKERS).mapToObj(i -> new DatacenterBrokerSimple(simulation)).toList();
        createVmsAndCloudlets();
        fault = createFaultInjectionForHosts(datacenter);

        simulation.start();
        printResults();

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void printResults() {
        brokerList.forEach(broker -> new CloudletsTableBuilder(broker.getCloudletFinishedList()).setTitle(broker.toString()).build());

        final int k = poisson.getK();
        final double interArrival = poisson.getInterArrivalMeanTime();

        System.out.printf("%n# Total simulation time: %s%n", TimeUtil.secondsToStr(simulation.clock()));
        System.out.printf("# Number of Host faults: %d%n", fault.getHostFaultsNumber());
        System.out.printf(
            "# Mean Number of Failures per Hour: %.3f (%.3f x %.0f = %d failure expected at each %.0f hours).%n",
            MEAN_FAILURE_NUMBER_PER_HOUR, MEAN_FAILURE_NUMBER_PER_HOUR, interArrival, k, interArrival);
        System.out.printf("# Number of faults affecting all VMs from a broker: %d%n", fault.getTotalFaultsNumber());
        System.out.printf("# Mean Time To Repair Failures of VMs (MTTR): %.2f minutes%n", fault.meanTimeToRepairVmFaultsInMinutes());
        System.out.printf("# Mean Time Between Failures (MTBF) affecting all VMs: %.2f minutes%n", fault.meanTimeBetweenVmFaultsInMinutes());
        System.out.printf("# Hosts MTBF: %.2f minutes%n", fault.meanTimeBetweenHostFaultsInMinutes());
        System.out.printf("# Availability: %.4f%%%n%n", fault.availability()*100);
    }

    private void createVmsAndCloudlets() {
        for (final var broker : brokerList) {
            createAndSubmitVms(broker);
            createAndSubmitCloudlets(broker);
        }
    }

    public void createAndSubmitVms(final DatacenterBroker broker) {
        final List<Vm> list = new ArrayList<>(VMS_BY_BROKER);

        for (int i = 0; i < VMS_BY_BROKER; i++) {
            list.add(createVm());
        }

        broker.submitVmList(list);
    }

    public Vm createVm() {
        final Vm vm = new VmSimple(++createdVms, VM_MIPS, VM_PES);
        vm
            .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        return vm;
    }

    /**
     * Creates the number of Cloudlets defined in {@link #CLOUDLETS_BY_BROKER} and submits
     * them to the created broker.
     */
    public void createAndSubmitCloudlets(final DatacenterBroker broker) {
        final List<Cloudlet> cloudletList = new ArrayList<>(CLOUDLETS_BY_BROKER);
        final var utilizationModelDynamic = new UtilizationModelDynamic(0.1);
        final var utilizationModelFull = new UtilizationModelFull();
        for (int i = 0; i < CLOUDLETS_BY_BROKER; i++) {
            Cloudlet c
                = new CloudletSimple(++createdCloudlets, CLOUDLET_LENGTH, CLOUDLET_PES)
                        .setFileSize(CLOUDLET_FILESIZE)
                        .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                        .setUtilizationModelCpu(utilizationModelFull)
                        .setUtilizationModelBw(utilizationModelDynamic)
                        .setUtilizationModelBw(utilizationModelDynamic);
            cloudletList.add(c);
        }

        broker.submitCloudletList(cloudletList);
    }

    private Datacenter createDatacenter() {
        hostList = new ArrayList<>();
        for (int id = 1; id <= HOSTS; id++) {
            hostList.add(createHost(id));
        }
        System.out.println();

        final var dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    public Host createHost(final int id) {
        final var pesList = createPeList(HOST_PES, HOST_MIPS_BY_PE);
        final var ramProvisioner = new ResourceProvisionerSimple();
        final var bwProvisioner = new ResourceProvisionerSimple();
        final var vmScheduler = new VmSchedulerTimeShared();
        final var host = new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, pesList);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(vmScheduler)
            .setId(id);

        return host;
    }

    public List<Pe> createPeList(final int pesNumber, final long mips) {
        final List<Pe> list = new ArrayList<>(pesNumber);
        for (int i = 0; i < pesNumber; i++) {
            list.add(new PeSimple(mips));
        }

        return list;
    }

    /**
     * Creates the fault injection for host
     *
     * @param datacenter
     * @return
     */
    private HostFaultInjection createFaultInjectionForHosts(final Datacenter datacenter) {
        //Use the system time to get random results every time you run the simulation
        //final long seed = System.currentTimeMillis();
        final long seed = 112717613L;
        this.poisson = new PoissonDistr(MEAN_FAILURE_NUMBER_PER_HOUR, seed);

        final var fault = new HostFaultInjection(datacenter, poisson);
        fault.setMaxTimeToFailInHours(MAX_TIME_TO_FAIL_IN_HOURS);

        for (final var broker : brokerList) {
            fault.addVmCloner(broker, new VmClonerSimple(this::cloneVm, this::cloneCloudlets));
        }

        return fault;
    }

    /**
     * Clones a VM by creating another one with the same configurations of a
     * given VM.
     *
     * @param vm the VM to be cloned
     * @return the cloned (new) VM.
     *
     * @see #createFaultInjectionForHosts(org.cloudbus.cloudsim.datacenters.Datacenter)
     */
    private Vm cloneVm(final Vm vm) {
        final Vm clone = new VmSimple(vm.getMips(), vm.getNumberOfPes());
        /*It' not required to set an ID for the clone.
        It is being set here just to make it easy to
        relate the ID of the vm to its clone,
        since the clone ID will be 10 times the id of its
        source VM.*/
        clone.setId(vm.getId() * 10);
        clone.setDescription("Clone of Vm " + vm.getId());
        clone
            .setSize(vm.getStorage().getCapacity())
            .setBw(vm.getBw().getCapacity())
            .setRam(vm.getBw().getCapacity())
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        System.out.printf(
            "%n# %s: Cloning %s as Vm %d -> MIPS: %.0f PEs Number: %d%n",
            vm.getBroker(), vm, clone.getId(), clone.getMips(), clone.getNumberOfPes());

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
    private List<Cloudlet> cloneCloudlets(final Vm sourceVm) {
        final var sourceVmCloudletList = sourceVm.getCloudletScheduler().getCloudletList();
        final var clonedCloudletList = new ArrayList<Cloudlet>(sourceVmCloudletList.size());
        for (final var sourceCloudlet : sourceVmCloudletList) {
            final var clonedCloudlet = cloneCloudlet(sourceCloudlet);
            clonedCloudletList.add(clonedCloudlet);
            System.out.printf(
                "# %s: Cloning %s as Cloudlet %d%n",
                sourceVm.getBroker(), sourceCloudlet, clonedCloudlet.getId(), sourceVm);
        }

        return clonedCloudletList;
    }

    /**
     * Creates a clone from a given Cloudlet.
     *
     * @param source the Cloudlet to be cloned.
     * @return the cloned (new) cloudlet
     */
    private Cloudlet cloneCloudlet(Cloudlet source) {
        Cloudlet clone = new CloudletSimple(source.getLength(), source.getNumberOfPes());
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
