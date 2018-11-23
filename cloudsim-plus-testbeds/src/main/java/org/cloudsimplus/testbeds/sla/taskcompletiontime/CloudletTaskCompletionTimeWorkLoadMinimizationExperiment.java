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
package org.cloudsimplus.testbeds.sla.taskcompletiontime;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.util.ResourceLoader;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;
import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWorkLoadMinimizationRunner.*;

/**
 * An experiment that tries to minimize task completion time,
 * where tasks are created from a workload file.
 *
 * <p>For more details, check
 * <a href="http://www.di.ubi.pt/~mario/files/MScDissertation-RaysaOliveira.pdf">Raysa Oliveira's Master Thesis (only in Portuguese)</a>.</p>
 *
 * @author raysaoliveira
 */
public class CloudletTaskCompletionTimeWorkLoadMinimizationExperiment extends AbstractCloudletTaskCompletionTimeExperiment {
    private static final int HOSTS = 50;

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
        setHostsNumber(HOSTS);
        setVmsNumber(VMS);
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
            .filter(line -> !line.isEmpty())
            .filter(line -> !line.startsWith("#"))
            .collect(toList());
    }

    @Override
    public final void printResults() {
        final DatacenterBroker broker0 = getFirstBroker();
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(comparingLong(Cloudlet::getId));

        new CloudletsTableBuilder(finishedCloudlets)
            .addColumn(7, new TextTableColumn("VM    ", "MIPS  "), c -> (long)c.getVm().getMips())
            .addColumn(new TextTableColumn("Wait Time", "Seconds"), c -> Math.ceil(c.getWaitingTime()))
            .build();
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        final SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance("METACENTRUM-2009-2.swf", 1);
        reader.setPredicate(cloudlet -> cloudlet.getLength() > 1000);
        reader.setMaxLinesToRead(CLOUDLETS);
        final List<Cloudlet> list = reader.generateWorkload();
        System.out.printf("Created %d Cloudlets from the workload file\n", list.size());
        return list;
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
    protected Vm createVm() {
        final int id = createsVms++;
        final int pesId = (int) (randVm.sample() * VM_PES.length);
        final int mipsId = (int) (randMip.sample() * VM_MIPS.length);

        final int pes = VM_PES[pesId];
        final int mips = VM_MIPS[mipsId];

        final Vm vm = new VmSimple(id, mips, pes)
                .setRam(512).setBw(1000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }


    @Override
    protected void createBrokers() {
        super.createBrokers();
        getFirstBroker().setVmMapper(this::selectVmForCloudlet);
        getFirstBroker().setCloudletComparator(sortCloudletsByLengthReversed);

        readTheSlaContracts();
    }

    @Override
    protected double getTaskCompletionTimeAverage() {
        final double mean = super.getTaskCompletionTimeAverage();

        System.out.printf(
                "\t\t\n Task Completion Time simulation: %.2f \n SLA's Max Task Completion Time: %.2f \n",
            mean, getSlaMaxTaskCompletionTime(getFirstBroker()));
        return mean;
    }

    double getPercentageOfCloudletsMeetingCompletionTime() {
        final DatacenterBroker broker = getFirstBroker();

        final double totalOfCloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
                .map(c -> c.getActualCpuTime() + c.getWaitingTime())
                .filter(rt -> rt <= getSlaMaxTaskCompletionTime(broker))
                .count();

        final double percent = totalOfCloudletSlaSatisfied * 100 / broker.getCloudletFinishedList().size();
        System.out.printf("\n # Total of cloudlets satisfied SLA completion time of %.2f secs: %.0f de %d",
            getSlaMaxTaskCompletionTime(broker), totalOfCloudletSlaSatisfied, broker.getCloudletFinishedList().size());
        System.out.printf("\n # Percentage of cloudlets that complied with the SLA Agreement:  %.2f %%", percent);

        System.out.println("\n\nCloudlets: " + broker.getVmCreatedList().size());

        return percent;
    }

    private double getSlaMaxTaskCompletionTime(final DatacenterBroker broker) {
        return contractsMap.get(broker).getTaskCompletionTimeMetric().getMaxDimension().getValue();
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
     * A main method just for test purposes.
     *
     * @param args
     */
    public static void main(String[] args) {
        final CloudletTaskCompletionTimeWorkLoadMinimizationExperiment exp
            = new CloudletTaskCompletionTimeWorkLoadMinimizationExperiment(1475098589732L);
        exp.setVerbose(true);
        exp.run();
        exp.getTaskCompletionTimeAverage();
        exp.getPercentageOfCloudletsMeetingCompletionTime();
        exp.getRatioOfExistingVmPesToRequiredCloudletPes();
        exp.waitTimeAverage(exp.getCloudletList());
    }
}
