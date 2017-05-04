package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.vms.power.PowerVm;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class PowerVmListTest {
    private static final int USER_ID=0;
    private static final long MIPS=1000;
    private static final int PES=1;
    private static final int RAM=512;
    private static final long BW=1000;
    private static final int STORAGE=1024;
    private static final int NUMBER_OF_VMS = 10;
    private static final int TIME = 0;

    private CloudletScheduler[] createCloudletSchedulerMocks(boolean ascendingCpuUtilization) {
        CloudletScheduler list[] = new CloudletScheduler[NUMBER_OF_VMS];
        IntStream.range(0, NUMBER_OF_VMS)
                .forEach(i -> {
                    list[i] = EasyMock.createMock(CloudletScheduler.class);
                    EasyMock.expect(list[i].getRequestedCpuPercentUtilization(TIME))
                            .andReturn(
                                    expectedCpuUtilizationPercentageForVm(
                                            i, ascendingCpuUtilization))
                            .anyTimes();
                    list[i].setVm(EasyMock.anyObject());
                    EasyMock.expectLastCall().once();

                    EasyMock.replay(list[i]);
                });

        return list;
    }

    private double expectedCpuUtilizationPercentageForVm(int vmIndex, boolean ascendingCpuUtilization) {
        final double utilization = vmIndex*(1.0/NUMBER_OF_VMS);
        if(ascendingCpuUtilization)
            return utilization;

        return 1 - utilization;
    }

    private List<PowerVm> createPowerVmList(boolean ascendingCpuUtilization) {
        CloudletScheduler cloudletSchedulerList[] =
                createCloudletSchedulerMocks(ascendingCpuUtilization);

        final List<PowerVm> list = new ArrayList<>();
        for(int i = 0; i < cloudletSchedulerList.length; i++){
                PowerVm vm = new PowerVm(i, MIPS, PES);
                vm
                  .setRam(RAM).setBw(BW).setSize(STORAGE)
                  .setCloudletScheduler(cloudletSchedulerList[i])
                  .setBroker(Mocks.createMockBroker(USER_ID));
                list.add(vm);
        }
        return list;
    }

    @Test
    public void testSortByCpuUtilizationWithVmsInIncreasingUtilizationOrder() {
        final List<PowerVm> list = createPowerVmList(true);

        VmList.sortByCpuUtilization(list, 0);

        int i = NUMBER_OF_VMS;
        for(final PowerVm vm: list) {
            i--;
            String msg = String.format(
                    "It was expected that the PowerVm %d at the position %d",
                    vm.getId(), i);
            assertEquals(msg, i, vm.getId());
            EasyMock.verify(vm.getCloudletScheduler());
        };
    }

    @Test
    public void testSortByCpuUtilizationWithVmsInDecreasingUtilizationOrder() {
        final List<PowerVm> list = createPowerVmList(false);
        VmList.sortByCpuUtilization(list, 0);

        int i = -1;
        for(PowerVm vm: list) {
            i++;
            String msg = String.format(
                    "It was expected that the PowerVm %d at the position %d",
                    vm.getId(), i);
            assertEquals(msg, i, vm.getId());
            EasyMock.verify(vm.getCloudletScheduler());
        };
    }

}
