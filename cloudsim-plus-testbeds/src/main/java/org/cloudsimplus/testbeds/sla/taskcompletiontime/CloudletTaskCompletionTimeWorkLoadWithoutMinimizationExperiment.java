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
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.util.SwfWorkloadFileReader;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.slametrics.SlaContract;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.List;

import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner.VMS;
import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner.VM_PES;

/**
 * @author raysaoliveira
 */
public class CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment extends AbstractCloudletTaskCompletionTimeExperiment {

    private static final int SCHEDULING_INTERVAL = 5;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 12;

    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    private static final String METRICS_FILE = "SlaMetrics.json";

    private ContinuousDistribution randVm;
    private SlaContract contract;

    private CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment(final long seed) {
        this(0, null, seed);
    }

    CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment(final int index, final ExperimentRunner runner) {
        this(index, runner, -1);
    }

    private CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
        this.randVm = new UniformDistr(1475098589732L + 1);
        this.contract = SlaContract.getInstance(METRICS_FILE);
        setHostsNumber(HOSTS);
        setVmsNumber(VMS);
    }

    @Override
    public void printResults() {
        printBrokerFinishedCloudlets(getFirstBroker());
    }

    @Override
    protected List<Cloudlet> createCloudlets() {
        final SwfWorkloadFileReader reader = SwfWorkloadFileReader.getInstance("METACENTRUM-2009-2.swf", 1);
        reader.setMaxLinesToRead(70);
        return reader.generateWorkload();
    }

    @Override
    protected DatacenterSimple createDatacenter() {
        DatacenterSimple dc = super.createDatacenter();
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }


    @Override
    protected Vm createVm() {
        final int id = createsVms++;
        final int pesId = (int) (randVm.sample() * VM_PES.length);
        final int pes = VM_PES[pesId];

        Vm vm = new VmSimple(id, 1000, pes)
            .setRam(512).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
        return vm;
    }

    double getPercentageOfCloudletsMeetingTaskCompletionTime() {
        final DatacenterBroker broker = getBrokerList().stream()
            .findFirst()
            .orElse(DatacenterBroker.NULL);

        final double totalOfcloudletSlaSatisfied = broker.getCloudletFinishedList().stream()
            .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
            .filter(rt -> rt <= getCustomerMaxTaskCompletionTime())
            .count();
        return (totalOfcloudletSlaSatisfied * 100) / broker.getCloudletFinishedList().size();
    }

    private double getCustomerMaxTaskCompletionTime() {
        return contract.getTaskCompletionTimeMetric().getMaxDimension().getValue();
    }

    /**
     * Gets the ratio of existing vPEs (VM PEs) divided by the number
     * of required PEs of all Cloudlets, which indicates
     * the mean number of vPEs that are available for each PE required
     * by a Cloudlet, considering all the existing Cloudlets.
     * For instance, if the ratio is 0.5, in average, two Cloudlets
     * requiring one vPE will share that same vPE.
     *
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
        final CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment exp
            = new CloudletTaskCompletionTimeWorkLoadWithoutMinimizationExperiment(1);
        exp.setVerbose(true);
        exp.run();
        exp.getTaskCompletionTimeAverage();
        exp.getPercentageOfCloudletsMeetingTaskCompletionTime();
        exp.getRatioOfExistingVmPesToRequiredCloudletPes();
        exp.waitTimeAverage(exp.getCloudletList());
    }
}
