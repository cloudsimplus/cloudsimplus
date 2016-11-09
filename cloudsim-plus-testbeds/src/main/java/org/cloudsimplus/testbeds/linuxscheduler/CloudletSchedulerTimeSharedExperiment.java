package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudsimplus.testbeds.ExperimentRunner;

import java.util.function.Supplier;


/**
 * An experiment runs Cloudlets using a {@link CloudletSchedulerTimeShared} to get results
 * to compare to the {@link CloudletSchedulerCompletelyFair} scheduler experiment
 * implemented in {@link CloudletSchedulerExperiment}.
 *
 * <p>Check the super class {@link CloudletSchedulerExperiment}</p> to see the general
 * experiment configuration and goals.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletSchedulerExperiment
 */
final class CloudletSchedulerTimeSharedExperiment extends CloudletSchedulerExperiment {
    /**
     * Creates a simulation experiment.
     *
     * @param index  the index that identifies the current experiment run.
     * @param runner The {@link ExperimentRunner} that is in charge
     *               of executing this experiment a defined number of times and to collect
     */
    public CloudletSchedulerTimeSharedExperiment(int index, CloudletSchedulerTimeSharedRunner runner) {
        super(index, runner);
    }

    @Override
    protected Supplier<Vm> getVmSupplier(DatacenterBroker broker) {
        return () -> {
            return new VmSimple(getNumberOfCreatedVms(), broker.getId(),
                VM_MIPS, VM_PES, VM_RAM, VM_BW, VM_STORAGE, VMM,
                new CloudletSchedulerTimeShared());
        };
    }
}
