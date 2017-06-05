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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla.tasktimecompletion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;

import static org.cloudsimplus.sla.tasktimecompletion.CloudletTaskTimeCompletionMinimizationRunner.CLOUDLETS;
import static org.cloudsimplus.sla.tasktimecompletion.CloudletTaskTimeCompletionMinimizationRunner.CLOUDLET_LENGTHS;
import static org.cloudsimplus.sla.tasktimecompletion.CloudletTaskTimeCompletionMinimizationRunner.VMS;
import static org.cloudsimplus.sla.tasktimecompletion.CloudletTaskTimeCompletionMinimizationRunner.VM_PES;

import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 * An experiment of dynamic creation of vm at runtime, respecting the cpu usage
 * limit and the free number of each VM, thus selecting an "ideal" VM for a
 * given cloudlet, which will then minimize Cloudlet TaskTimeCompletion.
 *
 * @author raysaoliveira
 */
public final class CloudletTaskTimeCompletionMinimizationExperiment extends SimulationExperiment {

    private static final int SCHEDULING_INTERVAL = 5;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;
    private final SlaContract contract;

    private List<Host> hostList;
    private static List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private final ContinuousDistribution randCloudlet, randVm;

    private int createdCloudlets;
    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = "SlaMetrics.json";
    private double cpuUtilizationSlaContract;
    private double taskTimeCompletionSlaContract;

    /**
     * Sorts the Cloudlets before submitting them to the Broker, so that
     * Cloudlets with larger length will be mapped for a VM first than lower
     * ones.
     */
    private final Comparator<Cloudlet> sortCloudletsByLengthReversed = Comparator.comparingDouble(Cloudlet::getLength).reversed();

    private CloudletTaskTimeCompletionMinimizationExperiment(final long seed) {
        this(0, null, seed);
    }

    CloudletTaskTimeCompletionMinimizationExperiment(int index, ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private CloudletTaskTimeCompletionMinimizationExperiment(int index, ExperimentRunner runner, long seed) {
        super(index, runner, seed);
        this.randCloudlet = new UniformDistr(getSeed());
        this.randVm = new UniformDistr(getSeed());
        try {
            this.contract = SlaContract.getInstanceFromResourcesDir(getClass(), METRICS_FILE);
            getCloudSim().addOnClockTickListener(this::printVmsCpuUsage);
        } catch (IOException ex) {
            Logger.getLogger(CloudletTaskTimeCompletionMinimizationExperiment.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private DatacenterBroker getFirstBroker() {
        return getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
    }

    private void printVmsCpuUsage(EventInfo eventInfo) {
        DatacenterBroker broker0 = getFirstBroker();
        broker0.getVmExecList().sort(Comparator.comparingInt(Vm::getId));

        broker0.getVmExecList().forEach(vm
                -> Log.printFormattedLine("####Time %.0f: Vm %d CPU usage: %.2f. SLA: %.2f.\n",
                        eventInfo.getTime(), vm.getId(),
                        vm.getCpuPercentUsage(), cpuUtilizationSlaContract)
        );
    }

    @Override
    public final void printResults() {
        DatacenterBroker broker0 = getFirstBroker();
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        Comparator<Cloudlet> sortByStartTime = comparingDouble(c -> c.getExecStartTime());
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        cloudletList = new ArrayList<>(CLOUDLETS);
        DatacenterBroker broker0 = getFirstBroker();
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudletList.add(createCloudlet(broker0));
        }

        return cloudletList;
    }

    private Cloudlet createCloudlet(DatacenterBroker broker) {
        final int id = createdCloudlets++;
        final int i = (int) (randCloudlet.sample() * CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];
        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(id, length, 2)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(utilization);
    }

    /**
     * Selects a VM to run a Cloudlet that will minimize the Cloudlet response
     * time.
     *
     * @param cloudlet the Cloudlet to select a VM to
     * @return the selected Vm
     */
    private Vm selectVmForCloudlet(Cloudlet cloudlet) {
        List<Vm> createdVms = cloudlet.getBroker().getVmExecList();
        Log.printLine("\t\tCreated VMs: " + createdVms);
        Comparator<Vm> sortByNumberOfFreePes
                = Comparator.comparingLong(vm -> getExpectedNumberOfFreeVmPes(vm));
        Comparator<Vm> sortByExpectedCloudletTaskTimeCompletion
                = Comparator.comparingDouble(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm));
        createdVms.sort(
                sortByNumberOfFreePes
                        .thenComparing(sortByExpectedCloudletTaskTimeCompletion)
                        .reversed());
        Vm mostFreePesVm = createdVms.stream().findFirst().orElse(Vm.NULL);

        Vm selectedVm = createdVms.stream()
                .filter(vm -> getExpectedNumberOfFreeVmPes(vm) >= cloudlet.getNumberOfPes())
                .filter(vm -> getExpectedCloudletTaskTimeCompletion(cloudlet, vm) <= taskTimeCompletionSlaContract)
                .findFirst().orElse(mostFreePesVm);

        return selectedVm;
    }

    private double getExpectedCloudletTaskTimeCompletion(Cloudlet cloudlet, Vm vm) {
        final double expectedTaskTimeCompletion = cloudlet.getLength() / vm.getMips();
        return expectedTaskTimeCompletion;
    }

    /**
     * Gets the expected amount of free PEs for a VM
     *
     * @param vm the VM to get the amount of free PEs
     * @return the number of PEs that are free or a negative value that indicate
     * there aren't free PEs (this negative number indicates the amount of
     * overloaded PEs)
     */
    private long getExpectedNumberOfFreeVmPes(Vm vm) {
        final long totalPesNumberForCloudletsOfVm
                = vm.getBroker().getCloudletCreatedList().stream()
                        .filter(c -> c.getVm().equals(vm))
                        .mapToLong(Cloudlet::getNumberOfPes)
                        .sum();

        final long numberOfVmFreePes
                = vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;

        Log.printFormattedLine(
                "\t\tTotal pes of cloudlets in VM " + vm.getId() + ": "
                + totalPesNumberForCloudletsOfVm + " -> vm pes: "
                + vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);
        return numberOfVmFreePes;
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        dc.getCharacteristics()
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        vmList = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
            //  createHorizontalVmScaling(vm);
            vmList.add(vm);
        }
        return vmList;
    }

    /**
     * Creates a Vm object.
     *
     * @return the created Vm
     */
    private Vm createVm() {
        DatacenterBroker broker0 = getFirstBroker();
        final int id = createsVms++;
        final int i = (int) (randVm.sample() * VM_PES.length);
        final int pes = VM_PES[i];

        Vm vm = new VmSimple(id, 1000, pes)
                .setRam(512).setBw(1000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
        return vm;
    }

    @Override
    protected List<Host> createHosts() {
        hostList = new ArrayList<>(HOSTS);
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }
        return hostList;
    }

    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(20480, 100000, 100000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    @Override
    protected DatacenterBroker createBroker() {
        DatacenterBroker broker0;
        broker0 = new DatacenterBrokerSimple(getCloudSim());
        broker0.setVmMapper(this::selectVmForCloudlet);
        broker0.setCloudletComparator(sortCloudletsByLengthReversed);
        return broker0;
    }

    /**
     * Computes the TaskTimeCompletion average for all finished Cloudlets on
     * this experiment.
     *
     * @return the TaskTimeCompletion average
     */
    double getCloudletsTaskTimeCompletionAverage() {
        SummaryStatistics cloudletTaskTimeCompletion = new SummaryStatistics();
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);
        broker.getCloudletFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .forEach(cloudletTaskTimeCompletion::addValue);

        Log.printFormattedLine(
                "\t\t\n TaskTimeCompletion simulation: %.2f \n TaskTimeCompletion contrato SLA: %.2f \n",
                cloudletTaskTimeCompletion.getMean(), taskTimeCompletionSlaContract);
        return cloudletTaskTimeCompletion.getMean();
    }

    double getPercentageOfCloudletsMeetingTaskTimeCompletion() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double totalOfcloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .filter(rt -> rt <= taskTimeCompletionSlaContract)
                .count();
        System.out.printf("\n ** Percentage of cloudlets that complied with "
                + "the SLA Agreement:  %.2f %%",
                ((totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size()));
        System.out.printf("\nTotal of cloudlets SLA satisfied: %.0f de %d", totalOfcloudletSlaSatisfied, broker.getCloudletFinishedList().size());
        return (totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size();
    }

    double getSumPesVms() {
        return vmList.stream()
                .mapToDouble(vm -> vm.getNumberOfPes())
                .sum();
    }

    double getSumPesCloudlets() {
        return cloudletList.stream()
                .mapToDouble(c -> c.getNumberOfPes())
                .sum();
    }

    /**
     * Gets the ratio of existing vPEs (VM PEs) divided by the number of
     * required PEs of all Cloudlets, which indicates the mean number of vPEs
     * that are available for each PE required by a Cloudlet, considering all
     * the existing Cloudlets. For instance, if the ratio is 0.5, in average,
     * two Cloudlets requiring one vPE will share that same vPE.
     *
     * @return the average of vPEs/CloudletsPEs ratio
     */
    double getRatioOfExistingVmPesToRequiredCloudletPes() {
        double sumPesVms = getSumPesVms();
        double sumPesCloudlets = getSumPesCloudlets();

        return sumPesVms / sumPesCloudlets;
    }

    /**
     * A main method just for test purposes.
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        final long seed = System.currentTimeMillis();
        CloudletTaskTimeCompletionMinimizationExperiment exp =
            new CloudletTaskTimeCompletionMinimizationExperiment(1475098589739L);
        exp.setVerbose(true);
        exp.run();
        exp.getCloudletsTaskTimeCompletionAverage();
        exp.getPercentageOfCloudletsMeetingTaskTimeCompletion();
    }
}
