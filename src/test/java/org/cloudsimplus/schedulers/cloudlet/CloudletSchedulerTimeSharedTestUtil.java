package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.VmSimple;
import org.mockito.Mockito;

/**
 * An utility class used by Vm tests.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
final class CloudletSchedulerTimeSharedTestUtil {
    /**
     * A private constructor to avoid class instantiation.
     */
    private CloudletSchedulerTimeSharedTestUtil(){/**/}

    /**
     * Creates a mock CloudletExecutionInfo.
     * @param id Cloudlet id
     * @return the created mock Cloudlet
     */
    /* default */ static CloudletExecution createCloudletExecInfo(final long id){
        final CloudletExecution cloudlet = Mockito.mock(CloudletExecution.class);
        Mockito.when(cloudlet.getId()).thenReturn(id);
        return cloudlet;
    }

    /* default */ static CloudletSchedulerTimeShared createCloudletSchedulerWithMipsList(final int pesNumber, final long mips) {
        final CloudletSchedulerTimeShared scheduler = new CloudletSchedulerTimeShared();
        scheduler.setVm(new VmSimple(0, mips, pesNumber));
        scheduler.setCurrentMipsShare(new MipsShare(pesNumber, mips));
        return scheduler;
    }

    /* default */ static void createCloudletAndAddItToPausedList(
        final CloudletSchedulerTimeShared instance, final int cloudletId, final long cloudletLength)
    {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet(cloudletId, cloudletLength, 1);
        cloudlet.setStatus(Cloudlet.Status.PAUSED);
        instance.getCloudletPausedList().add(new CloudletExecution(cloudlet));
    }

    /**
     * Creates a scheduler with a list of running cloudlets, where each Cloudlet has just one PE.
     *
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param vmPes number of PEs of the VM's scheduler
     * @param cloudlets number of Cloudlets to create
     * @return the new scheduler
     */
    /* default */ static CloudletSchedulerTimeShared newSchedulerWithSingleCoreRunningCloudlets(
        final long mips, final int vmPes, final int cloudlets)
    {
        return newSchedulerWithRunningCloudlets(mips, vmPes, cloudlets, 1);
    }

    /**
     * Creates a scheduler with a list of running cloudlets.
     * @param mips the MIPS capacity of each PE from the VM's scheduler
     * @param vmPes number of PEs of the VM's scheduler
     * @param cloudlets number of Cloudlets to create
     * @param cloudletPes the number of PEs for each Cloudlet
     * @return the new scheduler
     */
    /* default */ static CloudletSchedulerTimeShared newSchedulerWithRunningCloudlets(
        final long mips, final int vmPes, final int cloudlets, final int cloudletPes)
    {
        final UtilizationModel ramBwModel = new UtilizationModelDynamic(1.0/cloudlets);
        return newSchedulerWithRunningCloudlets(mips, vmPes, cloudlets, cloudletPes, ramBwModel);
    }

    /* default */ static CloudletSchedulerTimeShared newSchedulerWithRunningCloudlets(
        final long mips, final int vmPes, final int cloudlets, final int cloudletPes, final UtilizationModel ramBwModel)
    {
        final CloudletSchedulerTimeShared instance = createCloudletSchedulerWithMipsList(vmPes, mips);

        for(int i = 0; i < cloudlets; i++) {
            final var cloudlet = CloudletTestUtil.createCloudlet(i, mips, cloudletPes);
            cloudlet.setUtilizationModelRam(ramBwModel);
            cloudlet.setUtilizationModelBw(ramBwModel);
            cloudlet.registerArrivalInDatacenter();
            instance.cloudletSubmit(cloudlet);
        }

        return instance;
    }
}
