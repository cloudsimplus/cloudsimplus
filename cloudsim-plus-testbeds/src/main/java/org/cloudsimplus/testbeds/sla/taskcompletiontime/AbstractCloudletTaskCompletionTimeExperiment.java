package org.cloudsimplus.testbeds.sla.taskcompletiontime;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.testbeds.ExperimentRunner;
import org.cloudsimplus.testbeds.SimulationExperiment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;

public abstract class AbstractCloudletTaskCompletionTimeExperiment extends SimulationExperiment {
    private static final int HOST_PES = 32;
    private int hostsNumber;
    private int vmsNumber;

    protected AbstractCloudletTaskCompletionTimeExperiment(final int index, final ExperimentRunner runner, final long seed) {
        super(index, runner, seed);
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
            .mapToDouble(Machine::getNumberOfPes)
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
        System.out.println("\n# The wait time is: " + averageWaitTime);
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


    @Override
    protected final List<Vm> createVms(final DatacenterBroker broker) {
        List<Vm> vmList = new ArrayList<>(vmsNumber);
        for (int i = 0; i < vmsNumber; i++) {
            Vm vm = createVm();
            vmList.add(vm);
        }
        return vmList;
    }

    @Override
    protected final List<Host> createHosts() {
        final List<Host> hostList = new ArrayList<>(hostsNumber);
        for (int i = 0; i < hostsNumber; i++) {
            hostList.add(createHost());
        }
        return hostList;
    }

    @Override
    protected final DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(getCloudSim());
    }

    protected final Host createHost() {
        final List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(100000, new PeProvisionerSimple()));
        }

        final Host h = new HostSimple(40960, 10000000, 10000000, pesList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
        return h;
    }

    protected abstract Vm createVm();

    protected final void setHostsNumber(final int hostsNumber) {
        this.hostsNumber = hostsNumber;
    }

    protected final void setVmsNumber(final int vmsNumber) {
        this.vmsNumber = vmsNumber;
    }
}
