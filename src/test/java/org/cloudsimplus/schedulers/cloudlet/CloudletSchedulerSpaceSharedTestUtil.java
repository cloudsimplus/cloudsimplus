package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

/**
 * An utility class used by {@link CloudletSchedulerSpaceShared} tests.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
final class CloudletSchedulerSpaceSharedTestUtil {

    static final long SCHEDULER_MIPS = 1000;

    /**
     * A private constructor to avoid class instantiation.
     */
    private CloudletSchedulerSpaceSharedTestUtil(){/**/}

    private static CloudletSchedulerSpaceShared createCloudletSchedulerWithMipsList(int pes, long mips) {
        final CloudletSchedulerSpaceShared instance = createScheduler(mips, pes);
        instance.setCurrentMipsShare(new MipsShare(pes, mips));
        return instance;
    }

    /**
     * Creates a scheduler with a list of running cloudlets, where each Cloudlet has just one PE.
     *
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param numberOfVmPes number of PEs of the VM's scheduler
     * @param numberOfCloudlets number of Cloudlets to create
     * @return the new scheduler
     */
    /* default */ static CloudletSchedulerSpaceShared newSchedulerWithSingleCoreRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets) {
        return newSchedulerWithRunningCloudlets(mips, numberOfVmPes, numberOfCloudlets, 1);
    }

    /**
     * Creates a scheduler with a list of running cloudlets.
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param numberOfVmPes number of PEs of the VM's scheduler
     * @param numberOfCloudlets number of Cloudlets to create
     * @param numberOfCloudletPes the number of PEs for each Cloudlet
     * @return the new scheduler
     */
    /* default */ static CloudletSchedulerSpaceShared newSchedulerWithRunningCloudlets(long mips, int numberOfVmPes, int numberOfCloudlets, int numberOfCloudletPes) {
        final CloudletSchedulerSpaceShared instance = createCloudletSchedulerWithMipsList(numberOfVmPes, mips);

        for(int i = 0; i < numberOfCloudlets; i++) {
            final Cloudlet cloudlet = CloudletTestUtil.createCloudlet(i, mips, numberOfCloudletPes);
            cloudlet.registerArrivalInDatacenter();
            instance.cloudletSubmit(cloudlet);
        }

        return instance;
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler() {
        return createScheduler(2);
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler(final int pes) {
        return createScheduler(SCHEDULER_MIPS, pes);
    }

    private static CloudletSchedulerSpaceShared createScheduler(final long mips, final int pes) {
        return createScheduler(new VmSimple(mips, pes));
    }

    /* default */ static CloudletSchedulerSpaceShared createScheduler(final Vm vm) {
        final CloudletSchedulerSpaceShared scheduler = new CloudletSchedulerSpaceShared();
        scheduler.setVm(vm);
        scheduler.setCurrentMipsShare(new MipsShare(vm.getProcessor()));
        return scheduler;
    }
}
