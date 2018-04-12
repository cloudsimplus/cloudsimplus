/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.testbeds.sla.tasktimecompletion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparingDouble;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.*;
import org.cloudbus.cloudsim.cloudlets.*;
import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.*;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.*;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.vm.*;
import org.cloudbus.cloudsim.util.*;
import org.cloudbus.cloudsim.utilizationmodels.*;
import org.cloudbus.cloudsim.vms.*;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.testbeds.sla.tasktimecompletion.CloudletTaskCompletionTimeMinimizationRunner.*;

import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.*;

/**
 * An experiment of dynamic creation of vm at runtime, respecting the cpu usage
 * limit and the free number of each VM, thus selecting an "ideal" VM for a
 * given cloudlet, which will then minimize Cloudlet TaskTimeCompletion.
 *
 * @author raysaoliveira
 */
public final class CloudletTaskCompletionTimeMinimizationExperiment extends SimulationExperiment {
    private static final int SCHEDULING_INTERVAL = 5;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;

    private List<Host> hostList;
    private static List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private final ContinuousDistribution randCloudlet, randVm, randCloudletPes, randMipsVm;

    private int createdCloudlets;
    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    private static final String SLA_CONTRACTS_LIST = "sla-files.txt";
    private Map<DatacenterBroker, SlaContract> contractsMap;

    private double cpuUtilizationSlaContract;
    private double taskCompletionTimeSlaContract;

    /**
     * Sorts the Cloudlets before submitting them to the Broker, so that
     * Cloudlets with larger length will be mapped for a VM first than lower
     * ones.
     */
    private static final Comparator<Cloudlet> SORT_CLOUDLETS_BY_LENGTH_REVERSED = comparingDouble(Cloudlet::getLength).reversed();

    private CloudletTaskCompletionTimeMinimizationExperiment(final long seed) {
        this(0, null, seed);
    }

    CloudletTaskCompletionTimeMinimizationExperiment(int index, ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private CloudletTaskCompletionTimeMinimizationExperiment(int index, ExperimentRunner runner, long seed) {
        super(index, runner, seed);
        this.randCloudlet = new UniformDistr(getSeed());
        this.randVm = new UniformDistr(getSeed()+2);
        this.randCloudletPes = new UniformDistr(getSeed()+3);
        this.randMipsVm = new UniformDistr(getSeed()+4);
        contractsMap = new HashMap<>();
    }

    /**
     * Read all SLA contracts registered in the {@link #SLA_CONTRACTS_LIST}.
     * When the brokers are created, it is ensured the number of brokers
     * is equals to the number of SLA contracts in the {@link #SLA_CONTRACTS_LIST}.
     */
    private void readTheSlaContracts() throws IOException {
        for (final String file: readContractList()) {
            SlaContract contract = SlaContract.getInstanceFromResourcesDir(getClass(), file);
            contractsMap.put(getFirstBroker(), contract);
        }
    }

    private List<String> readContractList() throws FileNotFoundException {
        return ResourceLoader
            .getBufferedReader(getClass(), SLA_CONTRACTS_LIST)
            .lines()
            .filter(l -> !l.startsWith("#"))
            .collect(toList());
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
        final DatacenterBroker broker0 = getFirstBroker();
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
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
        final int p = (int) (randCloudletPes.sample() * CLOUDLET_PES.length);
        final long length = CLOUDLET_LENGTHS[i];
        final long pes = CLOUDLET_PES[p];
        return new CloudletSimple(id, length, pes)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(new UtilizationModelFull());
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
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
        final int id = createsVms++;
        final int i = (int) (randVm.sample() * VM_PES.length);
        final int m = (int) (randMipsVm.sample() * MIPS_VM.length);
        final int pes = VM_PES[i];
        final int mips = MIPS_VM[m];


        Vm vm = new VmSimple(id, mips, pes)
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
        final List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(10000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(40960, 100000, 100000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

    @Override
    protected void createBrokers() {
        super.createBrokers();
        final DatacenterBroker broker0 = getFirstBroker();
        broker0.setVmMapper(this::selectVmForCloudlet);
        broker0.setCloudletComparator(SORT_CLOUDLETS_BY_LENGTH_REVERSED);

        try {
            readTheSlaContracts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Selects a VM to run a Cloudlet which minimizes the Cloudlet completion
     * time.
     *
     * @param cl the Cloudlet to select a VM to
     * @return the selected Vm
     */
    private Vm selectVmForCloudlet(Cloudlet cl) {
        final List<Vm> execVms = cl.getBroker().getVmExecList();

        final Comparator<Vm> sortByFreePesNumber = comparingLong(this::getExpectedNumberOfFreeVmPes);
        final Comparator<Vm> sortByExpectedCloudletCompletionTime = comparingDouble(vm -> getExpectedCloudletCompletionTime(cl, vm));
        execVms.sort(
            sortByExpectedCloudletCompletionTime.thenComparing(sortByFreePesNumber.reversed())
        );
        final Vm mostFreePesVm = execVms.stream().findFirst().orElse(Vm.NULL);

        taskCompletionTimeSlaContract = contractsMap.get(cl.getBroker()).getTaskCompletionTimeMetric().getMaxDimension().getValue();

        return execVms.stream()
            .filter(vm -> getExpectedNumberOfFreeVmPes(vm) >= cl.getNumberOfPes())
            .filter(vm -> getExpectedCloudletCompletionTime(cl, vm) <= taskCompletionTimeSlaContract)
            .findFirst()
            .orElse(mostFreePesVm);
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
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
        final long totalPesForCloudletsOfVm
            = vm.getBroker().getCloudletCreatedList().stream()
            .filter(c -> c.getVm().equals(vm))
            .mapToLong(Cloudlet::getNumberOfPes)
            .sum();

        final long numberOfVmFreePes = vm.getNumberOfPes() - totalPesForCloudletsOfVm;

        Log.printFormattedLine(
            "\t\tTotal pes of cloudlets in VM " + vm.getId() + ": "
                + totalPesForCloudletsOfVm + " -> vm pes: "
                + vm.getNumberOfPes() + " -> vm free pes: " + numberOfVmFreePes);

        return numberOfVmFreePes;
    }

    private double getExpectedCloudletCompletionTime(final Cloudlet cloudlet, final Vm vm) {
        return cloudlet.getLength() / vm.getMips();
    }

    /**
     * Computes the TaskTimeCompletion average for all finished Cloudlets on
     * this experiment.
     *
     * @return the TaskTimeCompletion average
     */
    double getCloudletsTaskTimeCompletionAverage() {
        final SummaryStatistics cloudletTaskTimeCompletion = new SummaryStatistics();
        final DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);
        broker.getCloudletFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .forEach(cloudletTaskTimeCompletion::addValue);

        taskCompletionTimeSlaContract = getTaskTimeCompletionFromContract(broker);

       /* Log.printFormattedLine(
                "\t\t\n TaskTimeCompletion simulation: %.2f \n TaskTimeCompletion contrato SLA: %.2f \n",
                cloudletTaskTimeCompletion.getMean(), taskCompletionTimeSlaContract);*/
        return cloudletTaskTimeCompletion.getMean();
    }

    private double getTaskTimeCompletionFromContract(DatacenterBroker broker) {
        return contractsMap.get(broker).getTaskCompletionTimeMetric().getMaxDimension().getValue();
    }

    double getPercentageOfCloudletsMeetingTaskTimeCompletion() {
        final DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);
        taskCompletionTimeSlaContract = contractsMap.get(broker).getTaskCompletionTimeMetric().getMaxDimension().getValue();
        double totalOfcloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .filter(rt -> rt <= taskCompletionTimeSlaContract)
                .count();
        return (totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size();
    }

    private double getSumPesVms() {
        return vmList.stream()
                .mapToDouble(Machine::getNumberOfPes)
                .sum();
    }

    private double getSumPesCloudlets() {
        return cloudletList.stream()
                .mapToDouble(Cloudlet::getNumberOfPes)
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
    public static void main(String[] args) {
        final CloudletTaskCompletionTimeMinimizationExperiment exp =
            new CloudletTaskCompletionTimeMinimizationExperiment(34475098589739L);
        exp.setVerbose(true);
        exp.run();
        exp.getCloudletsTaskTimeCompletionAverage();
        exp.getPercentageOfCloudletsMeetingTaskTimeCompletion();
        exp.getRatioOfExistingVmPesToRequiredCloudletPes();
    }
}
