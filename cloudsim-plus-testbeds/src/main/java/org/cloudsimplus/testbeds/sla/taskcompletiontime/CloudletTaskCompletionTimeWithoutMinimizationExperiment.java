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
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmCost;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWithoutMinimizationRunner.*;

/**
 *
 * @author raysaoliveira
 */
public class CloudletTaskCompletionTimeWithoutMinimizationExperiment extends AbstractCloudletTaskCompletionTimeExperiment {
    private static final int SCHEDULING_INTERVAL = 5;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 12;
    private final SlaContract contract;

    private final ContinuousDistribution randCloudlet, randVm;

    private int createdCloudlets;
    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    private static final String METRICS_FILE = "SlaMetrics.json";

    private CloudletTaskCompletionTimeWithoutMinimizationExperiment(final long seed) {
        this(0, null, seed);
    }

    CloudletTaskCompletionTimeWithoutMinimizationExperiment(final int index, final ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private CloudletTaskCompletionTimeWithoutMinimizationExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
        setHostsNumber(HOSTS);
        setVmsNumber(VMS);
        randCloudlet = new UniformDistr(getSeed());
        randVm = new UniformDistr(getSeed()+1);
        this.contract = SlaContract.getInstance(METRICS_FILE);
        getCloudSim().addOnClockTickListener(this::printVmsCpuUsage);
    }

    @Override
    public void printResults() {
        printBrokerFinishedCloudlets(getFirstBroker());
    }

    private void printVmsCpuUsage(EventInfo eventInfo) {
        DatacenterBroker broker0 = getFirstBroker();
        broker0.getVmExecList().sort(Comparator.comparingLong(Vm::getId));

        broker0.getVmExecList().forEach(vm
                -> System.out.printf("#### Time %.0f: Vm %d CPU usage: %.2f. SLA: %.2f.\n",
                        eventInfo.getTime(), vm.getId(),
                        vm.getCpuPercentUsage(), getCustomerMaxCpuUtilization())
        );
    }

    private double getCustomerMaxCpuUtilization() {
        return contract.getCpuUtilizationMetric().getMaxDimension().getValue();
    }

    @Override
    protected Vm createVm() {
        final int id = createsVms++;
        final int pesId = (int) (randVm.sample() * VM_PES.length);
        final int pes = VM_PES[pesId];

        Vm vm = new VmSimple(id, 1000, pes)
            .setRam(512).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        final List<Cloudlet> cloudletList = new ArrayList<>(CLOUDLETS);
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudletList.add(createCloudlet());
        }

        return cloudletList;
    }

    private Cloudlet createCloudlet() {
        final int id = createdCloudlets++;
        final int i = (int) (randCloudlet.sample() * CLOUDLET_LENGTHS.length);
        final long length = CLOUDLET_LENGTHS[i];

        return new CloudletSimple(id, length, 2)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(new UtilizationModelFull());
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        dc.getCharacteristics()
                .setCostPerSecond(3.0)
                .setCostPerMem(0.05)
                .setCostPerStorage(0.001)
                .setCostPerBw(0.0);
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }


    @Override
    protected double getTaskCompletionTimeAverage() {
        final double mean = super.getTaskCompletionTimeAverage();

        System.out.printf(
                "\t\t\nTaskCompletionTime simulation: %.2f \n SLA's Task Completion Time: %.2f \n",
                mean, getSlaMaxTaskCompletionTime());
        return mean;
    }

    private double getSlaMaxTaskCompletionTime() {
        return contract.getTaskCompletionTimeMetric().getMaxDimension().getValue();
    }

    double getPercentageOfCloudletsMeetingTaskCompletionTime() {
        DatacenterBroker broker = getBrokerList().stream()
                .findFirst()
                .orElse(DatacenterBroker.NULL);

        double totalOfcloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
                .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
                .filter(rt -> rt <= getSlaMaxTaskCompletionTime())
                .count();

        System.out.printf("\n ** Percentage of cloudlets that complied with "
                + "the SLA Agreement:  %.2f %%",
                ((totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size()));
        System.out.printf("\nTotal of cloudlets SLA satisfied: %.0f de %d", totalOfcloudletSlaSatisfied, broker.getCloudletFinishedList().size());
        return (totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size();
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
     * Calculates the cost price of resources (processing, bw, memory, storage)
     * of each or all of the Datacenter VMs()
     *
     */
    double getTotalCostPrice() {
        VmCost vmCost;
        double totalCost = 0.0;
        for (Vm vm : getVmList()) {
            if (vm.getCloudletScheduler().hasFinishedCloudlets()) {
                vmCost = new VmCost(vm);
                totalCost += vmCost.getTotalCost();
            } else {
                System.out.printf(
                        "\t%s didn't execute any Cloudlet.\n", vm);
            }
        }
        return totalCost;
    }

    /**
     * A main method just for test purposes.
     *
     * @param args
     */
    public static void main(String[] args) {
        CloudletTaskCompletionTimeWithoutMinimizationExperiment exp
                = new CloudletTaskCompletionTimeWithoutMinimizationExperiment(1);
        exp.setVerbose(true);
        exp.run();
        exp.getTaskCompletionTimeAverage();
        exp.getPercentageOfCloudletsMeetingTaskCompletionTime();
    }
}
