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
package org.cloudsimplus.migration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.allocationpolicies.power.PowerVmAllocationPolicyMigrationWorstFitStaticThreshold;
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
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudsimplus.sla.readJsonFile.SlaMetric;
import org.cloudsimplus.sla.readJsonFile.SlaReader;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

/**
 * This example shows how to do a migration using CpuUtilization threshold
 * established in the SLA Contract. When the CPU metric is violated the
 * migration is made.
 *
 * @author raysaoliveira
 */
public final class VmMigrationWhenCpuMetricIsViolatedExample {

    private static final int SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS = 5;

    private static final int HOST_MIPS_BY_PE = 1000;
    private static final int HOST_NUMBER_OF_PES = 2;
    private static final long HOST_RAM = 500000; //host memory (MB)
    private static final long HOST_STORAGE = 1000000; //host storage
    private static final long HOST_BW = 100000000L;

    private static final int VM_MIPS = 1000;
    private static final long VM_SIZE = 1000; //image size (MB)
    private static final int VM_RAM = 10000; //vm memory (MB)
    private static final long VM_BW = 100000;
    private static final int VM_PES_NUM = 1; //number of cpus

    private static final long CLOUDLET_LENGHT = 20000;
    private static final long CLOUDLET_FILESIZE = 300;
    private static final long CLOUDLET_OUTPUTSIZE = 300;

    /**
     * The percentage of CPU that a cloudlet will use when it starts executing
     * (in scale from 0 to 1, where 1 is 100%). For each cloudlet create, this
     * value is used as a base to define CPU usage.
     *
     * @see #createAndSubmitCloudlets(DatacenterBroker)
     */
    private static final double CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE = 0.6;

    /**
     * Defines the speed (in percentage) that CPU usage of a cloudlet will
     * increase during the simulation time. (in scale from 0 to 1, where 1 is
     * 100%).
     *
     * @see #createAndSubmitCloudletsWithDynamicCpuUtilization(double, double,
     * Vm, DatacenterBroker)
     */
    public static final double CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND = 0.05;

    private static final int NUMBER_OF_HOSTS_TO_CREATE = 3;
    private static final int NUMBER_OF_VMS_TO_CREATE = NUMBER_OF_HOSTS_TO_CREATE + 1;
    private static final int NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM = 1;

    private final List<Vm> vmlist = new ArrayList<>();
    private CloudSim simulation;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = ResourceLoader.getResourcePath(VmMigrationWhenCpuMetricIsViolatedExample.class, "SlaMetrics.json");

    /**
     * Attributes with minimum and maximum values of the CPU Utilization metric
     * to be set for allocation policy.
     */
    private double underCpuUtilizationThreshold;
    private double overCpuUtilizationThreshold;

    private double responseTimeSlaContract;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        new VmMigrationWhenCpuMetricIsViolatedExample();
    }
    private  List<Cloudlet> cloudletList;

    public VmMigrationWhenCpuMetricIsViolatedExample() throws FileNotFoundException, IOException {
        Log.printConcatLine("Starting ", VmMigrationWhenCpuMetricIsViolatedExample.class.getSimpleName(), "...");
        simulation = new CloudSim();

        searchCpuUtilizationMetricInSlaContract();
        cloudletList = new ArrayList<>(NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM);

        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        broker.setVmMapper(this::selectVmForCloudlet);

        createAndSubmitVms(broker);

        createAndSubmitCloudlets(broker);

        simulation.start();

        responseTimeCloudletSimulation(broker);

        new CloudletsTableBuilder(broker.getCloudletsFinishedList()).build();

        Log.printConcatLine(VmMigrationWhenCpuMetricIsViolatedExample.class.getSimpleName(), " finished!");
    }

    public void createAndSubmitCloudlets(DatacenterBroker broker) {
        double initialCloudletCpuUtilizationPercentage = CLOUDLET_INITIAL_CPU_UTILIZATION_PERCENTAGE;
        final int numberOfCloudlets = NUMBER_OF_VMS_TO_CREATE - 1;
        for (int i = 0; i < numberOfCloudlets; i++) {
            createAndSubmitCloudletsWithStaticCpuUtilization(
                    initialCloudletCpuUtilizationPercentage, vmlist.get(i), broker);
            initialCloudletCpuUtilizationPercentage += 0.15;
        }
        //Create one last cloudlet which CPU usage increases dynamically
        Vm lastVm = vmlist.get(vmlist.size() - 1);
        sortCloudletListByLength(broker);
        createAndSubmitCloudletsWithDynamicCpuUtilization(0.2, 1, lastVm, broker);
    }

    public void createAndSubmitVms(DatacenterBroker broker) {
        for (int i = 0; i < NUMBER_OF_VMS_TO_CREATE; i++) {
            PowerVm vm = createVm(broker);
            vmlist.add(vm);
        }
        broker.submitVmList(vmlist);
    }

    /**
     *
     * @param broker
     * @return
     */
    public PowerVm createVm(DatacenterBroker broker) {
        PowerVm vm = new PowerVm(vmlist.size(), VM_MIPS, VM_PES_NUM);
        vm.setSchedulingInterval(1)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE)
                .setBroker(broker)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

        Log.printConcatLine(
                "#Requested creation of VM ", vm.getId(), " with ", VM_MIPS, " MIPS x ", VM_PES_NUM);
        return vm;
    }

    public List<Cloudlet> createAndSubmitCloudlets(
            double cloudletInitialCpuUsagePercent,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker,
            boolean progressiveCpuUsage) {
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        int cloudletId;
        for (int i = 0; i < NUMBER_OF_CLOUDLETS_TO_CREATE_BY_VM; i++) {
            cloudletId = hostingVm.getId() + i;
            UtilizationModelDynamic cpuUtilizationModel;
            if (progressiveCpuUsage) {
                cpuUtilizationModel
                        = new UtilizationModelDynamic(cloudletInitialCpuUsagePercent)
                                .setUtilizationUpdateFunction(this::getCpuUtilizationIncrement);
            } else {
                cpuUtilizationModel = new UtilizationModelDynamic(cloudletInitialCpuUsagePercent);
            }
            cpuUtilizationModel.setMaxResourceUtilization(maxCloudletCpuUtilizationPercentage);

            Cloudlet c
                    = new CloudletSimple(
                            cloudletId, CLOUDLET_LENGHT, VM_PES_NUM)
                            .setFileSize(CLOUDLET_FILESIZE)
                            .setOutputSize(CLOUDLET_OUTPUTSIZE)
                            .setUtilizationModelCpu(cpuUtilizationModel)
                            .setUtilizationModelRam(utilizationModelFull)
                            .setUtilizationModelBw(utilizationModelFull);
            c.setBroker(broker);
            cloudletList.add(c);
        }



        broker.submitCloudletList(cloudletList);
        for (Cloudlet c : cloudletList) {
            broker.bindCloudletToVm(c, hostingVm);
        }

        return cloudletList;
    }

    /**
     * Increments the CPU resource utilization, that is defined in percentage
     * values.
     *
     * @return the new resource utilization after the increment
     */
    private double getCpuUtilizationIncrement(UtilizationModelDynamic um) {
        return um.getUtilization() + um.getTimeSpan() * CLOUDLET_CPU_USAGE_INCREMENT_PER_SECOND;
    }

    public List<Cloudlet> createAndSubmitCloudletsWithDynamicCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            double maxCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                maxCloudletCpuUtilizationPercentage, hostingVm, broker, true);
    }

    public List<Cloudlet> createAndSubmitCloudletsWithStaticCpuUtilization(
            double initialCloudletCpuUtilizationPercentage,
            Vm hostingVm,
            DatacenterBroker broker) {
        return createAndSubmitCloudlets(
                initialCloudletCpuUtilizationPercentage,
                initialCloudletCpuUtilizationPercentage,
                hostingVm, broker, false);
    }

    private Datacenter createDatacenter() {
        ArrayList<PowerHost> hostList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS_TO_CREATE; i++) {
            hostList.add(createHost(i, HOST_NUMBER_OF_PES, HOST_MIPS_BY_PE));
            Log.printConcatLine("#Created host ", i, " with ", HOST_MIPS_BY_PE, " mips x ", HOST_NUMBER_OF_PES);
        }
        Log.printLine();

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList);

        PowerVmAllocationPolicyMigrationWorstFitStaticThreshold allocationPolicy
                = new PowerVmAllocationPolicyMigrationWorstFitStaticThreshold(
                        new PowerVmSelectionPolicyMinimumUtilization(),
                        overCpuUtilizationThreshold);
        allocationPolicy.setUnderUtilizationThreshold(underCpuUtilizationThreshold);

        PowerDatacenter dc = new PowerDatacenter(simulation, characteristics, allocationPolicy);
        dc.setMigrationsEnabled(true).setSchedulingInterval(SCHEDULE_TIME_TO_PROCESS_DATACENTER_EVENTS);
        return dc;
    }

    /**
     *
     * @param id
     * @param numberOfPes
     * @param mipsByPe
     * @return
     *
     * @todo @author manoelcampos Using the {@link VmSchedulerSpaceShared} its
     * getting NullPointerException, probably due to lack of CPU for all VMs. It
     * has to be created an IT test to check this problem.
     *
     * @todo @author manoelcampos The method
     * {@link DatacenterBroker#getCloudletsFinishedList()} returns an empty list
     * when using null null null null     {@link PowerDatacenter},
     * {@link PowerHost} and {@link PowerVm}.
     */
    public static PowerHostUtilizationHistory createHost(int id, int numberOfPes, long mipsByPe) {
        List<Pe> peList = createPeList(numberOfPes, mipsByPe);
        PowerHostUtilizationHistory host = new PowerHostUtilizationHistory(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
        host.setPowerModel(new PowerModelLinear(1000, 0.7))
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    public static List<Pe> createPeList(int numberOfPEs, long mips) {
        List<Pe> list = new ArrayList<>(numberOfPEs);
        for (int i = 0; i < numberOfPEs; i++) {
            list.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return list;
    }

    private void searchCpuUtilizationMetricInSlaContract() throws FileNotFoundException {
        SlaReader reader = new SlaReader(METRICS_FILE);
        List<SlaMetric> metrics = reader.getContract().getMetrics();
        metrics.stream()
                .filter(m -> m.isCpuUtilization())
                .findFirst()
                .ifPresent(this::getCpuUtilizationThreshold);

    }

    private void getCpuUtilizationThreshold(SlaMetric metric) {
        double minValue
                = metric.getDimensions().stream()
                        .filter(d -> d.isValueMin())
                        .map(d -> d.getValue())
                        .findFirst().orElse(Double.MIN_VALUE);

        double maxValue
                = metric.getDimensions().stream()
                        .filter(d -> d.isValueMax())
                        .map(d -> d.getValue())
                        .findFirst().orElse(Double.MAX_VALUE);

        underCpuUtilizationThreshold = minValue / 100;
        overCpuUtilizationThreshold = maxValue / 100;

    }

    private void responseTimeCloudletSimulation(DatacenterBroker broker) throws IOException {
        double average = 0;
        List<Double> responseTimes = new ArrayList<>();
        for (Cloudlet c : broker.getCloudletsFinishedList()) {
            double responseTime = c.getFinishTime() - c.getLastDatacenterArrivalTime();
            responseTimes.add(responseTime);
            average = responseTimeCloudletAverage(broker, responseTimes);

        }
        System.out.printf("\t\t\n Response Time simulation (average) : %.2f \n", average);
    }

    private double responseTimeCloudletAverage(DatacenterBroker broker, List<Double> responseTimes) {
        int totalCloudlets = broker.getCloudletsFinishedList().size();
        double sum = 0;
        sum = responseTimes.stream()
                .map((responseTime) -> responseTime)
                .reduce(sum, (accumulator, _item) -> accumulator + _item);
        return sum / totalCloudlets;
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
        Comparator<Vm> sortByExpectedCloudletResponseTime
                = Comparator.comparingDouble(vm -> getExpectedCloudletResponseTime(cloudlet, vm));
        createdVms.sort(
                sortByNumberOfFreePes
                        .thenComparing(sortByExpectedCloudletResponseTime)
                        .reversed());
        Vm mostFreePesVm = createdVms.stream().findFirst().orElse(Vm.NULL);

        Vm selectedVm = createdVms.stream()
                .filter(vm -> getExpectedNumberOfFreeVmPes(vm, true) >= cloudlet.getNumberOfPes())
                .filter(vm -> getExpectedCloudletResponseTime(cloudlet, vm) <= responseTimeSlaContract)
                .findFirst()
                .orElse(mostFreePesVm);

        return selectedVm;
    }

    private double getExpectedCloudletResponseTime(Cloudlet cloudlet, Vm vm) {
        double expectedResponseTime = cloudlet.getLength() / vm.getMips();
        return expectedResponseTime;
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

        final long numberOfVmFreePes = vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;

        if (printLog) {
            System.out.println("\t\tTotal pes of cloudlets in VM " + vm.getId() + ": " + totalPesNumberForCloudletsOfVm + " -> vm pes: " + vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);
        }
        return numberOfVmFreePes;
    }

    private void sortCloudletListByLength(DatacenterBroker broker) {
        //sort the cloudlet list by expected response time
        Comparator<Cloudlet> sortByExpectedCloudletByLength
                = Comparator.comparingDouble(cloudlet -> cloudlet.getLength());

        cloudletList.sort(sortByExpectedCloudletByLength.reversed());
        System.out.println("\n\n\n ->>>> CLOUDLET SORTED: " + cloudletList);
    }
}
