package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.function.Supplier;

/**
 * An experiment that shows how the oversimplied time-shared CloudletScheduler implementation provided by
 * the {@link CloudletSchedulerTimeShared} CloudSim class increases task completion time
 * of all Cloudlets and ignores Cloudlets priorities.
 *
 * <p>It also shows how a more realistic scheduler such as the {@link CloudletSchedulerCompletelyFair}
 * provided by CloudSim Plus is concerned in Cloudlets priorities and gets overall reduction of
 * task completion time. This scheduler is an simplified implementation of the
 * <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a> used by Linux Kernel.</p>
 *
 * <p>Check the super class {@link CloudletSchedulerExperiment}</p> to see the general
 * experiment configuration and goals.
 *
 * @author Manoel Campos da Silva Filho
 */
final class CompletelyFairSchedulerExperiment extends CloudletSchedulerExperiment {

    /**
     * Creates a simulation experiment.
     *
     * @param index  the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge
     *               of executing this experiment a defined number of times and to collect
     */
    public CompletelyFairSchedulerExperiment(int index, CompletelyFairSchedulerRunner runner) {
        super(index, runner);
    }

    @Override
    protected Supplier<Vm> getVmSupplier(DatacenterBroker broker) {
        return () -> {
            return new VmSimple(getNumberOfCreatedVms(), VM_MIPS, VM_PES)
                    .setRam(VM_RAM).setBw(VM_BW).setSize(VM_STORAGE)
                    .setCloudletScheduler(new CloudletSchedulerCompletelyFair())
                    .setBroker(broker);
        };
    }

}
