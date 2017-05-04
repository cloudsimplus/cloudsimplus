/**
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
package org.cloudsimplus.sla;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;

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
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.sla.readJsonFile.CpuUtilization;
import org.cloudsimplus.sla.readJsonFile.TaskTimeCompletion;
import org.cloudsimplus.sla.readJsonFile.SlaReader;

/**
 * Example of dynamic creation of VMS at runtime, respecting the CPU usage limit
 * and the free number of each VM, thus selecting an "ideal" VM for a given
 * cloudlet, which will then minimize Cloudlet TaskTimeCompletion.
 *
 * @author raysaoliveira
 */
public class DynamicVmCreationByCpuUtilizationAndFreePesOfVm {

    private static final int SCHEDULING_INTERVAL = 5;

    /**
     * List of PEs for each VM to be created.
     * The number of elements in this array represents the number of VMs to create.
     */
    private static final int[] VMS_PES_LIST = {2, 4, 4};

    /**
     * List of Length for each Cloudlet to be created.
     * The number of elements in this array represents the number of Cloudlets to create.
     */
    private static final long[] CLOUDLETS_LENGTHS = {40000, 10000, 14000, 50000};


    private final CloudSim simulation;

    /**
     * The interval to request the creation of new Cloudlets.
     */
    private static final int CLOUDLETS_CREATION_INTERVAL = SCHEDULING_INTERVAL * 3;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;
    private static final int VMS = 3;
    private static final long VM_MIPS = 1000;

    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    private static final long[] CLOUDLET_LENGTHS = {20000, 40000, 14000, 10000, 10000};
    private static final int[] VM_PES = {2, 4};
    private ContinuousDistribution randCloudlet, randVm;

    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = ResourceLoader.getResourcePath(DynamicVmCreationByCpuUtilizationAndFreePesOfVm.class, "SlaMetrics.json");
    private final double cpuUtilizationSlaContract;
    private double taskTimeCompletionSlaContract;

    private int totalOfcloudletSlaSatisfied;
    private List<Double> TaskTimesCompletion;

    /**
     * Sorts the Cloudlets before submitting them to the Broker, so that
     * Cloudlets with larger length will be mapped for a VM first than lower
     * ones.
     */
    private final Comparator<Cloudlet> sortCloudletsByLengthReversed = Comparator.comparingDouble((Cloudlet c) -> c.getLength()).reversed();

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Log.printFormattedLine(" Starting... ");
        new DynamicVmCreationByCpuUtilizationAndFreePesOfVm();
    }

    public DynamicVmCreationByCpuUtilizationAndFreePesOfVm() throws FileNotFoundException, IOException {

        final long seed = 1;
        randCloudlet = new UniformDistr(0, CLOUDLET_LENGTHS.length, seed);
        randVm = new UniformDistr(0, VM_PES.length, seed);
        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLETS_LENGTHS.length);

        simulation = new CloudSim();

        // Reading the sla contract and taking the metric values
        SlaReader slaReader = new SlaReader(METRICS_FILE);
        TaskTimeCompletion rt = new TaskTimeCompletion(slaReader);
        rt.checkTaskTimeCompletionSlaContract();
        taskTimeCompletionSlaContract = rt.getMaxValueTaskTimeCompletion();

        CpuUtilization cpu = new CpuUtilization(slaReader);
        cpu.checkCpuUtilizationSlaContract();
        cpuUtilizationSlaContract = cpu.getMaxValueCpuUtilization();

        //simulation.addOnClockTickListener(this::createNewCloudlets);
        simulation.addOnClockTickListener(this::printVmsCpuUsage);

        createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
       // broker0.setCloudletComparator(sortCloudletsByLengthReversed);
        //broker0.setVmMapper(this::selectVmForCloudlet);

        vmList.addAll(createListOfVms());

        createCloudletList();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        taskTimeCompletionCloudletSimulation(broker0);
        double percentage = (totalOfcloudletSlaSatisfied * 100) / cloudletList.size();
        System.out.println("\n ** Percentage of cloudlets that complied"
                + " with the SLA Agreement: " + percentage + " %");
        double totalCost = totalCostPrice(vmList);
        System.out.println("\t** Total cost (memory, bw, processing, storage) - " + totalCost);
        printSimulationResults();
    }

    private void printVmsCpuUsage(EventInfo eventInfo) {
        broker0.getVmsCreatedList().sort(Comparator.comparingInt(Vm::getId));

        System.out.println();
        broker0.getVmsCreatedList().forEach(vm
                -> System.out.printf("####Time %.0f: Vm %d CPU usage: %.2f. SLA: %.2f.\n",
                        eventInfo.getTime(), vm.getId(),
                        vm.getCurrentCpuPercentUse(), cpuUtilizationSlaContract)
        );
        System.out.println();
    }

    /**
     * Selects a VM to run a Cloudlet that will minimize the Cloudlet response
     * time.
     *
     * @param cloudlet the Cloudlet to select a VM to
     * @return the selected VM
     */
    private Vm selectVmForCloudlet(Cloudlet cloudlet) {
        List<Vm> createdVms = cloudlet.getBroker().getVmsCreatedList();
        System.out.println("\t\tCreated VMs: " + createdVms);
        Comparator<Vm> sortByNumberOfFreePes
                = Comparator.comparingLong(vm -> getExpectedNumberOfFreeVmPes(vm, false));
        Comparator<Vm> sortByExpectedCloudletTaskTimeCompletion
                = Comparator.comparingDouble(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm));
        createdVms.sort(
                sortByNumberOfFreePes
                        .thenComparing(sortByExpectedCloudletTaskTimeCompletion)
                        .reversed());
        Vm mostFreePesVm = createdVms.stream().findFirst().orElse(Vm.NULL);

        return createdVms.stream()
                .filter(vm -> getExpectedNumberOfFreeVmPes(vm, true) >= cloudlet.getNumberOfPes())
                .filter(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm) <= taskTimeCompletionSlaContract)
                .findFirst()
                .orElse(mostFreePesVm);
    }

    private double getExpectedCloudletTaskTimeCompletion(Cloudlet cloudlet, Vm vm) {
        return cloudlet.getLength() / vm.getMips();
    }

    /**
     * Gets the expected amount of free PEs for a VM
     *
     * @param vm the VM to get the amount of free PEs
     * @return the number of PEs that are free or a negative value that indicate
     * there aren't free PEs (this negative number indicates the amount of
     * overloaded PEs)
     */
    private long getExpectedNumberOfFreeVmPes(Vm vm, boolean printLog) {
        final long totalPesNumberForCloudletsOfVm
                = vm.getBroker().getCloudletsCreatedList().stream()
                        .filter(c -> c.getVm().equals(vm))
                        .mapToLong(Cloudlet::getNumberOfPes)
                        .sum();

        final long numberOfVmFreePes
                = vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;

        if (printLog) {
            System.out.println(
                "\t\tTotal pes of cloudlets in VM " + vm.getId() + ": " +
                totalPesNumberForCloudletsOfVm + " -> vm pes: " +
                vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);
        }
        return numberOfVmFreePes;
    }

    private Cloudlet createCloudlet(long length, int numberOfPes) {
        UtilizationModel utilization = new UtilizationModelFull();
        Cloudlet cloudlet
                = new CloudletSimple(
                        cloudletList.size(), length, numberOfPes)
                        .setFileSize(1024)
                        .setOutputSize(1024)
                        .setUtilizationModel(utilization)
                        .setBroker(broker0);
        return cloudlet;
    }

    private void createCloudletList() {
        for (long length : CLOUDLETS_LENGTHS) {
            cloudletList.add(createCloudlet(length, 2));
        }
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                        .setCostPerSecond(cost)
                        .setCostPerMem(costPerMem)
                        .setCostPerStorage(costPerStorage)
                        .setCostPerBw(costPerBw);

        Datacenter dc0 = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(4000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(20480, 10000, 10000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    /**
     * Creates a list of initial VMs in which each VM is able to scale
     * horizontally when it is overloaded.
     *
     * @return the list of scalable VMs
     * @see #VMS_PES_LIST
     */
    private List<Vm> createListOfVms() {
        List<Vm> newList = new ArrayList<>(VMS_PES_LIST.length);
        for (final int pes: VMS_PES_LIST) {
            newList.add(createVm(pes));
        }

        return newList;
    }

    /**
     * Creates a VM object.
     *
     * @return the created VM
     */
    private Vm createVm(int numberOfPes) {
        final int id = createsVms++;
        final int pes = VM_PES[(int) randVm.sample()];

        Vm vm = new VmSimple(id, VM_MIPS, numberOfPes)
                .setRam(512).setBw(1000).setSize(10000).setBroker(broker0)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        System.out.println("\n\t\t\t Vm: " + vm + " pes: " + pes);

        return vm;
    }

    private void printSimulationResults() {
        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    private void taskTimeCompletionCloudletSimulation(DatacenterBroker broker) throws IOException {
        double average = 0;
        TaskTimesCompletion = new ArrayList<>();
        for (Cloudlet c : broker.getCloudletsFinishedList()) {
            double taskTimeCompletion = c.getFinishTime() - c.getLastDatacenterArrivalTime();
            TaskTimesCompletion.add(taskTimeCompletion);
            average = taskTimeCompletionCloudletAverage(broker, TaskTimesCompletion);

            if (taskTimeCompletion <= taskTimeCompletionSlaContract) {
                totalOfcloudletSlaSatisfied++;
            }
        }
        System.out.printf("\t\t\n TaskTimeCompletion simulation (average) : %.2f \n TaskTimeCompletioncontrato SLA: %.2f "
                + "\n Total of cloudlets SLA satisfied: %d de %d cloudlets",
                average, taskTimeCompletionSlaContract, totalOfcloudletSlaSatisfied, broker.getCloudletsFinishedList().size());
    }

    private double taskTimeCompletionCloudletAverage(DatacenterBroker broker, List<Double> taskTimesCompletion) {
        int totalCloudlets = broker.getCloudletsFinishedList().size();
        double sum = 0;
        sum = taskTimesCompletion.stream()
                .map((taskTimeCompletion) -> taskTimeCompletion)
                .reduce(sum, (accumulator, _item) -> accumulator + _item);
        return sum / totalCloudlets;
    }

    /**
     * Calculates the cost price of resources (processing, bw, memory, storage)
     * of each or all of the Datacenter VMs()
     *
     * @param vmList
     */
    private double totalCostPrice(List<Vm> vmList) {
        VmCost vmCost;
        double totalCost = 0.0;
        for (Vm vm: vmList) {
            if (vm.getCloudletScheduler().hasFinishedCloudlets()) {
                vmCost = new VmCost(vm);
                totalCost += vmCost.getTotalCost();
                System.out.println("\t #price: " + totalCost);
         
            } else {
                Log.printFormattedLine(
                    "\tVm %d didn't execute any Cloudlet.", vm.getId());
            }
        }
        return totalCost;
    }
}
