package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.MocksHelper;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

/**
 * An utility class used by Vm tests.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
public final class VmTestUtil {
    static final long BW = 10000;
    static final long SIZE = 1000;
    static final String VMM = "Xen";
    static final double MIPS = 1000;
    static final int RAM = 1024;
    static final int ID = 1;
    static final int PES_NUMBER = 2;

    /**
     * A private constructor to avoid class instantiation.
     */
    private VmTestUtil(){/**/}

    /**
     * Creates a VM with the 1 PE and half mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static Vm createVmWithOnePeAndHalfMips(final int vmId) {
        return createVm(vmId, MIPS / 2, 1, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with 1 PE and the total mips capacity defined in
     * {@link #MIPS}.
     *
     * @param vmId the id of the VM
     * @return
     */
    public static Vm createVmWithOnePeAndTotalMips(final int vmId) {
        return createVm(vmId, MIPS, 1, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with the given numberOfPes and default configuration for
     * HOST_MIPS, HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId, final int numberOfPes) {
        return createVm(vmId, MIPS, numberOfPes);
    }

    /**
     * Creates a VM with the default configuration defined in the Test Class'
     * constants.
     *
     * @param cloudletScheduler
     * @return
     */
    public static VmSimple createVm(CloudletScheduler cloudletScheduler) {
        return createVm(ID, MIPS, PES_NUMBER, RAM, BW, SIZE, cloudletScheduler);
    }

    /**
     * Creates a VM with the given mips and numberOfPes and default
     * configuration for HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param mips
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVm(final int vmId, final double mips, final int numberOfPes) {
        return createVm(vmId, mips, numberOfPes, RAM, BW, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with 1 PE.
     *
     * @param vmId id of the VM
     * @param capacity a capacity that will be set to all resources, such as CPU, HOST_RAM, HOST_BW, etc.
     * @return
     */
    public static VmSimple createVm(final int vmId, long capacity) {
        return createVm(vmId, capacity, 1, capacity, capacity, capacity, CloudletScheduler.NULL);
    }

    public static VmSimple createVm(final int vmId,
                                    final double mips, final int numberOfPes,
                                    final long ram, final long bw, final long storage)
    {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, numberOfPes);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(CloudletScheduler.NULL)
                .setBroker(broker);
        return vm;
    }

    public static VmSimple createVm(final int vmId,
                                    final double mips, final int numberOfPes,
                                    final long ram, final long bw, final long storage,
                                    final CloudletScheduler scheduler)
    {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(0).anyTimes());
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, numberOfPes);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(scheduler)
                .setBroker(broker);
        return vm;
    }

    /**
     * Creates a VM with the given numberOfPes for a given user and default
     * configuration for HOST_MIPS, HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param broker
     * @param numberOfPes
     * @return
     */
    public static VmSimple createVmWithSpecificNumberOfPEsForSpecificUser(
        final int vmId, final DatacenterBroker broker, final int numberOfPes) {
        final VmSimple vm = createVm(vmId, MIPS, numberOfPes, RAM, BW, SIZE, CloudletScheduler.NULL);
        vm.setBroker(broker);
        return vm;
    }
}
