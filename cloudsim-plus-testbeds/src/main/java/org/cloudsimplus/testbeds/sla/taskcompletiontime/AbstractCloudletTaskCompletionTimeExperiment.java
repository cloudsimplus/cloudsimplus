package org.cloudsimplus.testbeds.sla.taskcompletiontime;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.AbstractMachine;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.testbeds.Experiment;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static org.cloudsimplus.testbeds.sla.taskcompletiontime.CloudletTaskCompletionTimeWorkLoadWithoutMinimizationRunner.VMS;

abstract class AbstractCloudletTaskCompletionTimeExperiment extends Experiment {
    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;

    protected AbstractCloudletTaskCompletionTimeExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
        setVmsByBrokerFunction(broker -> VMS);
        setHostsNumber(HOSTS);
    }

    protected DatacenterBroker getFirstBroker() {
        return getBrokerList().stream().findFirst().orElse(DatacenterBroker.NULL);
    }

    protected final void printBrokerFinishedCloudlets(final DatacenterBroker broker) {
        final List<Cloudlet> finishedCloudlets = broker.getCloudletFinishedList();
        final Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Gets the total number of vPEs (VM PEs) across all existing VMs.
     * @return
     */
    protected final double getSumPesVms() {
        return getVmList().stream()
            .mapToDouble(AbstractMachine::getNumberOfPes)
            .sum();
    }

    protected final double getSumPesCloudlets() {
        return getCloudletList().stream()
            .mapToDouble(Cloudlet::getNumberOfPes)
            .sum();
    }

    /**
     * Shows the average wait time of all cloudlets
     *
     * @param cloudletList list of cloudlets
     */
    protected final void waitTimeAverage(final List<Cloudlet> cloudletList) {
        final double averageWaitTime = cloudletList.stream().mapToDouble(Cloudlet::getWaitingTime).average().orElse(0);
        System.out.printf("%n# The wait time is: %f%n", averageWaitTime);
    }

    /**
     * Computes the Task Completion Time average for all finished Cloudlets on this
     * experiment.
     *
     * @return the Task Completion Time average
     */
    protected double getTaskCompletionTimeAverage() {
        final SummaryStatistics cloudletTaskCompletionTime = new SummaryStatistics();
        final DatacenterBroker broker = getBrokerList().stream()
            .findFirst()
            .orElse(DatacenterBroker.NULL);

        broker.getCloudletFinishedList().stream()
            .map(c -> c.getFinishTime() - c.getLastDatacenterArrivalTime())
            .forEach(cloudletTaskCompletionTime::addValue);

        return cloudletTaskCompletionTime.getMean();
    }

    protected final Host createHost(final int id) {
        final List<Pe> pesList = new ArrayList<>(HOST_PES);

        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(100000, new PeProvisionerSimple()));
        }

        final Host host = new HostSimple(40960, 10000000, 10000000, pesList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        host.setId(id);
        return host;
    }

    @Override
    protected final DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getSimulation());
    }

}
