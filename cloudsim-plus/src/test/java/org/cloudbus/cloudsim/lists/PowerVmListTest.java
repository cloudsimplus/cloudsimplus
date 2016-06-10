package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudSim.class}) //to intercept and mock static method calls
public class PowerVmListTest {
    private static final int USER_ID=0;
    private static final double MIPS=1000;
    private static final int PES=1;
    private static final int RAM=512;
    private static final long BW=1000;
    private static final int STORAGE=1024;
    private static final String VMM="Xen";
    private static final int NUMBER_OF_VMS = 10;
    private static final int TIME = 0;

    @Before
    public void setUp(){
        PowerMock.mockStatic(CloudSim.class);
        EasyMock.expect(CloudSim.clock()).andReturn(0.0).anyTimes();
        PowerMock.replay(CloudSim.class);
    }

    private CloudletScheduler[] createCloudletSchedulerMocks(boolean ascendingCpuUtilization) {
        CloudletScheduler list[] = new CloudletScheduler[NUMBER_OF_VMS];
        IntStream.range(0, NUMBER_OF_VMS)
                .forEach(i -> {
                    list[i] = EasyMock.createMock(CloudletScheduler.class);
                    EasyMock.expect(list[i].getTotalUtilizationOfCpu(TIME))
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
        IntStream.range(0, cloudletSchedulerList.length).forEach(
                i -> list.add(
                    new PowerVm(i, 
                        USER_ID, MIPS, PES, RAM, BW, STORAGE, 0, 
                        VMM, cloudletSchedulerList[i], 1)));
        return list;
    }

    @Test
    public void testSortByCpuUtilizationWithVmsInIncreasingUtilizationOrder() {
        System.out.println("sortByCpuUtilization");
        final List<PowerVm> list = createPowerVmList(true);
        
        PowerVmList.sortByCpuUtilization(list);
        
        int i = NUMBER_OF_VMS;
        for(PowerVm vm: list) {
            i--;
            System.out.println("Vm "+vm.getId());
            String msg = String.format(
                    "It was expected that the PowerVm %d at the position %d",
                    vm.getId(), i);
            assertEquals(msg, i, vm.getId());
            EasyMock.verify(vm.getCloudletScheduler());
        };
    }

    @Test
    public void testSortByCpuUtilizationWithVmsInDecreasingUtilizationOrder() {
        System.out.println("sortByCpuUtilization");
        final List<PowerVm> list = createPowerVmList(false);
        PowerVmList.sortByCpuUtilization(list);
        
        int i = -1;
        for(PowerVm vm: list) {
            i++;
            System.out.println("Vm "+vm.getId());
            String msg = String.format(
                    "It was expected that the PowerVm %d at the position %d",
                    vm.getId(), i);
            assertEquals(msg, i, vm.getId());
            EasyMock.verify(vm.getCloudletScheduler());
        };
    }
    
}
