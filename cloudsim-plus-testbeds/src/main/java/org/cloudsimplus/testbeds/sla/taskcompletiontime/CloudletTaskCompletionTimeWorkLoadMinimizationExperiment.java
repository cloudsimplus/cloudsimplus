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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.testbeds.sla.taskcompletiontime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparingDouble;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.WorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWorkLoadMinimizationRunner.*;

import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;

/**
 * An experiment that tries to minimize task completion time,
 * where tasks are created from a workload file.
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
public class CloudletTaskCompletionTimeWorkLoadMinimizationExperiment extends SimulationExperiment {
    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;

    private List<Host> hostList;
    private List<Vm> vmList;

    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    private static final String SLA_CONTRACTS_LIST = "sla-files.txt";
    private Map<DatacenterBroker, SlaContract> contractsMap;

    /**
     * Sorts the Cloudlets before submitting them to the Broker, so that
     * Cloudlets with larger length will be mapped for a VM first than lower
     * ones.
     */
    private final Comparator<Cloudlet> sortCloudletsByLengthReversed = Comparator.comparingLong(Cloudlet::getLength).reversed();
    private ContinuousDistribution randVm;
    private ContinuousDistribution randMip;
    private double taskCompletionTimeSlaContract;

    private CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(final long seed) {
        this(0, null, seed);
    }

    CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(final int index, final ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
        randVm = new UniformDistr(getSeed()+1);
        randMip = new UniformDistr(getSeed()+2);
        contractsMap = new HashMap<>();
    }

    /**
     * Read all SLA contracts registered in the {@link #SLA_CONTRACTS_LIST}.
     * When the brokers are created, it is ensured the number of brokers
     * is equals to the number of SLA contracts in the {@link #SLA_CONTRACTS_LIST}.
     */
    private void readTheSlaContracts() {
        for (final String file: readContractList()) {
            SlaContract contract = SlaContract.getInstance(file);
            contractsMap.put(getFirstBroker(), contract);
        }
    }

    private List<String> readContractList() {
        return ResourceLoader
            .getBufferedReader(getClass(), SLA_CONTRACTS_LIST)
            .lines()
            .map(String::trim)
            .filter(l -> !l.isEmpty())
            .filter(l -> !l.startsWith("#"))
            .collect(toList());
    }

    private DatacenterBroker getFirstBroker() {
        return getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
    }

    @Override
    public final void printResults() {
        final DatacenterBroker broker0 = getFirstBroker();
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(comparingInt(Cloudlet::getId));

        new CloudletsTableBuilder(finishedCloudlets)
            .addColumn(7, new TextTableColumn("VM    ", "MIPS  "), c -> (long)c.getVm().getMips())
            .addColumn(new TextTableColumn("Wait Time", "Seconds"), c -> Math.ceil(c.getWaitingTime()))
            .build();
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        try {
            final WorkloadFileReader workloadFileReader =
                WorkloadFileReader.getInstance("METACENTRUM-2009-2.swf", 1);
            workloadFileReader.setPredicate(c -> c.getLength() > 1000);
            workloadFileReader.setMaxLinesToRead(CLOUDLETS);
            final List<Cloudlet> list = workloadFileReader.generateWorkload();
            System.out.printf("Created %d Cloudlets from the workload file\n", list.size());
            return list;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Selects a VM to run a Cloudlet that will minimize the Cloudlet response
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

    private double getExpectedCloudletCompletionTime(Cloudlet cloudlet, Vm vm) {
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
    private long getExpectedNumberOfFreeVmPes(Vm vm) {
        final long totalPesNumberForCloudletsOfVm
                = vm.getBroker().getCloudletCreatedList().stream()
                        .filter(c -> c.getVm().equals(vm))
                        .mapToLong(Cloudlet::getNumberOfPes)
                        .sum();

        return vm.getNumberOfPes() - totalPesNumberForCloudletsOfVm;
    }

    @Override
    protected List<Vm> createVms(DatacenterBroker broker) {
        vmList = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm();
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
        final int pes = VM_PES[i];

        final int m = (int) (randMip.sample() * VM_MIPS.length);
        final int mips = VM_MIPS[m];

        Vm vm = new VmSimple(id, mips, pes)
                .setRam(512).setBw(1000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
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
            pesList.add(new PeSimple(100000, new PeProvisionerSimple()));
        }

        final int id = hostList.size();
        Host h = new HostSimple(42480, 10000000, 10000000, pesList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        h.setId(id);
        return h;
    }

    @Override
    protected void createBrokers() {
        super.createBrokers();
        getFirstBroker().setVmMapper(this::selectVmForCloudlet);
        getFirstBroker().setCloudletComparator(sortCloudletsByLengthReversed);

        readTheSlaContracts();
    }

    @Override
    protected DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
    }

    /**
     * Computes the Task Completion Time average for all finished Cloudlets on this
     * experiment.
     *
     * @return the Task Completion Time average
     */
    double getAverageCloudletCompletionTime() {
        final SummaryStatistics cloudletCompletionTime = new SummaryStatistics();
        final DatacenterBroker broker = getFirstBroker();

        broker.getCloudletFinishedList().stream()
                .map(c -> c.getActualCpuTime() + c.getWaitingTime())
                .forEach(cloudletCompletionTime::addValue);

        System.out.printf(
                "\t\t\n Task Completion Time simulation: %.2f \n Task Completion Time contrato SLA: %.2f \n",
            cloudletCompletionTime.getMean(), getMaxTaskCompletionTime(broker));
        return cloudletCompletionTime.getMean();
    }

    double getPercentageOfCloudletsMeetingCompletionTime() {
        final DatacenterBroker broker = getFirstBroker();

        final double totalOfcloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
                .map(c -> c.getActualCpuTime() + c.getWaitingTime())
                .filter(rt -> rt <= getMaxTaskCompletionTime(broker))
                .count();

        final double percent = totalOfcloudletSlaSatisfied * 100 / broker.getCloudletFinishedList().size();
        System.out.printf("\n # Total of cloudlets satisfied SLA completion time of %.2f secs: %.0f de %d",
            getMaxTaskCompletionTime(broker), totalOfcloudletSlaSatisfied, broker.getCloudletFinishedList().size());
        System.out.printf("\n # Percentage of cloudlets that complied with the SLA Agreement:  %.2f %%", percent);

        System.out.println("\n\nCloudlets: " + broker.getVmCreatedList().size());

        return percent;
    }

    private double getMaxTaskCompletionTime(DatacenterBroker broker) {
        return contractsMap.get(broker).getTaskCompletionTimeMetric().getMaxDimension().getValue();
    }

    private double getSumPesVms() {
        return vmList.stream()
                .mapToDouble(Machine::getNumberOfPes)
                .sum();
    }

    private double getSumPesCloudlets() {
        return getCloudletList().stream()
                .mapToDouble(Cloudlet::getNumberOfPes)
                .sum();
    }

    /**
     * Gets the ratio of existing vPEs (VM PEs) divided by the number
     * of required PEs of all Cloudlets, which indicates
     * the mean number of vPEs that are available for each PE required
     * by a Cloudlet, considering all the existing Cloudlets.
     * For instance, if the ratio is 0.5, in average, two Cloudlets
     * requiring one vPE will share that same vPE.
     * @return the average of vPEs/CloudletsPEs ratio
     */
    double getRatioOfExistingVmPesToRequiredCloudletPes() {
        return getSumPesVms() / getSumPesCloudlets();
    }

     /**
     * Shows the wait time of cloudlets
     *
     * @param cloudlet list of cloudlets
     */
    public void waitTimeAverage(List<Cloudlet> cloudlet) {
        double waitTime = 0, quant = 0;
        for (Cloudlet cloudlets : cloudlet) {
            waitTime += cloudlets.getWaitingTime();
            quant++;
        }
        System.out.println("\n# The wait time is: " + waitTime/quant);
    }


    /**
     * A main method just for test purposes.
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        final CloudletTaskCompletionTimeWorkLoadMinimizationExperiment exp
            = new CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(1475098589732L);
        exp.setVerbose(true);
        exp.run();
        exp.getAverageCloudletCompletionTime();
        exp.getPercentageOfCloudletsMeetingCompletionTime();
        exp.getRatioOfExistingVmPesToRequiredCloudletPes();
        exp.waitTimeAverage(exp.getCloudletList());
    }
}
