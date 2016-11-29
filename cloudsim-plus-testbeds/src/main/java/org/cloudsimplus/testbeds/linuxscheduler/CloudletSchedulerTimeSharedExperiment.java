package org.cloudsimplus.testbeds.linuxscheduler;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
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
            return new VmSimple(getNumberOfCreatedVms(), VM_MIPS, VM_PES)
                .setRam(VM_RAM).setBw(VM_BW).setSize(VM_STORAGE)
                .setCloudletScheduler(new CloudletSchedulerTimeShared())
                .setBroker(broker);
        };
    }
}
