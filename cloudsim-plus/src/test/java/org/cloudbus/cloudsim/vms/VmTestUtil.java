package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
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
    /* default */ static final long BANDWIDTH = 10000;
    /* default */ static final long SIZE = 1000;
    /* default */ static final String VMM = "Xen";
    /* default */ static final double MIPS = 1000;
    /* default */ static final int RAM = 1024;
    /* default */ static final int ID = 1;
    /* default */ static final int PES_NUMBER = 2;

    /**
     * A private constructor to avoid class instantiation.
     */
    private VmTestUtil(){/**/}

    /**
     * Creates a VM with the given numberOfPes and default configuration for
     * HOST_MIPS, HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param pesNumber
     * @return
     */
    public static VmSimple createVm(final int vmId, final int pesNumber) {
        return createVm(vmId, MIPS, pesNumber);
    }

    public static VmSimple createVm(final int vmId, final int pesNumber, final DatacenterBroker broker) {
        final VmSimple vm = createVm(vmId, pesNumber);
        vm.setBroker(broker);
        return vm;
    }

    /**
     * Creates a VM with the default configuration defined in the Test Class'
     * constants.
     *
     * @param cloudletScheduler
     * @return
     */
    public static VmSimple createVm(final CloudletScheduler cloudletScheduler) {
        return createVm(ID, MIPS, PES_NUMBER, RAM, BANDWIDTH, SIZE, cloudletScheduler);
    }

    /**
     * Creates a VM with the given mips and numberOfPes and default
     * configuration for HOST_RAM, HOST_BW and Storage.
     *
     * @param vmId
     * @param mips
     * @param pesNumber
     * @return
     */
    public static VmSimple createVm(final int vmId, final double mips, final int pesNumber) {
        return createVm(vmId, mips, pesNumber, RAM, BANDWIDTH, SIZE, CloudletScheduler.NULL);
    }

    /**
     * Creates a VM with 1 PE.
     *
     * @param vmId id of the VM
     * @param capacity a capacity that will be set to all resources, such as CPU, HOST_RAM, HOST_BW, etc.
     * @return
     */
    public static VmSimple createVm(final int vmId, final long capacity) {
        return createVm(vmId, capacity, 1, capacity, capacity, capacity, CloudletScheduler.NULL);
    }

    public static VmSimple createVm(
        final int vmId,
        final double mips, final int pesNumber,
        final long ram, final long bw, final long storage, CloudSim cloudsim)
    {
        final DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, pesNumber);
        vm.setRam(ram).setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(CloudletScheduler.NULL)
                .setBroker(broker);
        return vm;
    }

    public static VmSimple createVm(
        final int vmId,
        final double mips, final int pesNumber,
        final long ram, final long bw, final long storage,
        final CloudletScheduler scheduler)
    {
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0).anyTimes();
            mocker.clockStr().anyTimes();
        });
        final DatacenterBroker broker = MocksHelper.createMockBroker(cloudsim);
        final VmSimple vm = new VmSimple(vmId, mips, pesNumber);
        vm.setRam(ram).setBw(bw)
           .setSize(storage)
           .setCloudletScheduler(scheduler)
           .setBroker(broker);
        return vm;
    }
}
