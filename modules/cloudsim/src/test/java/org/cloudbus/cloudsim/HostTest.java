/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerDynamicWorkload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostTest {
    private static final int ID = 0;
    private static final long STORAGE = Consts.MILLION;
    private static final long HALF_STORAGE = STORAGE/2;
    private static final long A_QUARTER_STORAGE = STORAGE/4;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;
    
    private HostSimple host;

    public static HostSimple createHost(final int hostId, final int numberOfPes) {
        final List<Pe> peList = new ArrayList<>(numberOfPes);
        for(int i = 0; i < numberOfPes; i++)
            peList.add(new PeSimple(i, new PeProvisionerSimple(MIPS)));
        
        return new HostSimple(hostId, 
                new ResourceProvisionerSimple<>(new Ram(RAM)), 
                new ResourceProvisionerSimple<>(new Bandwidth(BW)), 
                STORAGE, peList, new VmSchedulerTimeShared(peList));
    }

    @Before
    public void setUp() throws Exception {
        host = createHost(ID, 2);
    }

    @Test
    public void testIsSuitableForVm() {
            VmSimple vm0 = VmTest.createVm(0, MIPS, 2, RAM, BW, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 2));
            VmSimple vm1 = VmTest.createVm(1, MIPS * 2, 1, RAM * 2, BW * 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS * 2, 2));

            assertTrue(host.isSuitableForVm(vm0));
            assertFalse(host.isSuitableForVm(vm1));
    }

    @Test
    public void testVmCreate() {
            VmSimple vm0 = VmTest.createVm(0, MIPS / 2, 1, RAM / 2, BW / 2, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS / 2, 1));
            VmSimple vm1 = VmTest.createVm(1, MIPS, 1, RAM, BW, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));
            VmSimple vm2 = VmTest.createVm(2, MIPS * 2, 1, RAM, BW, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS * 2, 1));
            VmSimple vm3 = VmTest.createVm(3, MIPS / 2, 2, RAM / 2, BW / 2, A_QUARTER_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS / 2, 2));

            assertTrue(host.vmCreate(vm0));
            assertFalse(host.vmCreate(vm1));
            assertFalse(host.vmCreate(vm2));
            assertTrue(host.vmCreate(vm3));
    }

    @Test
    public void testVmDestroy() {
            VmSimple vm = VmTest.createVm(0, MIPS, 1, RAM / 2, BW / 2, STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));

            assertTrue(host.vmCreate(vm));
            assertSame(vm, host.getVm(0, 0));
            assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

            host.vmDestroy(vm);
            assertNull(host.getVm(0, 0));
            assertEquals(0, host.getVmList().size());
            assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }

    @Test
    public void testVmDestroyAll() {
            VmSimple vm0 = VmTest.createVm(0, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));
            VmSimple vm1 = VmTest.createVm(1, MIPS, 1, RAM / 2, BW / 2, HALF_STORAGE, new CloudletSchedulerDynamicWorkload(MIPS, 1));

            assertTrue(host.vmCreate(vm0));
            assertSame(vm0, host.getVm(0, 0));
            assertEquals(MIPS, host.getVmScheduler().getAvailableMips(), 0);

            assertTrue(host.vmCreate(vm1));
            assertSame(vm1, host.getVm(1, 0));
            assertEquals(0, host.getVmScheduler().getAvailableMips(), 0);

            host.vmDestroyAll();
            assertNull(host.getVm(0, 0));
            assertNull(host.getVm(1, 0));
            assertEquals(0, host.getVmList().size());
            assertEquals(MIPS * 2, host.getVmScheduler().getAvailableMips(), 0);
    }

    /*
    private static final int PES_NUMBER = 2;
    private static final double CLOUDLET_LENGTH = 1000;
    private static final long CLOUDLET_FILE_SIZE = 300;
    private static final long CLOUDLET_OUTPUT_SIZE = 300;

    @Test
    public void testUpdateVmsProcessing() {
            UtilizationModelStochastic utilizationModel1 = new UtilizationModelStochastic();
            UtilizationModelStochastic utilizationModel2 = new UtilizationModelStochastic();

            VMGridlet gridlet1 = new VMGridlet(0, 0, GRIDLET_LENGTH, GRIDLET_FILE_SIZE, GRIDLET_OUTPUT_SIZE, PES_NUMBER, utilizationModel1, utilizationModel1, utilizationModel1);
            VMGridlet gridlet2 = new VMGridlet(0, 0, GRIDLET_LENGTH, GRIDLET_FILE_SIZE, GRIDLET_OUTPUT_SIZE, PES_NUMBER, utilizationModel2, utilizationModel2, utilizationModel2);

            gridlet1.setResourceParameter(0, 0, 0);
            gridlet2.setResourceParameter(0, 0, 0);

            int[] mipsShare = { (int) (GRIDLET_LENGTH / 2) };
            vmScheduler.setPEMips(mipsShare[0]);

            double utilization1 = utilizationModel1.getUtilization(0);
            double utilization2 = utilizationModel2.getUtilization(0);

            vmScheduler.gridletSubmit(gridlet1);
            vmScheduler.gridletSubmit(gridlet2);

            double actualCompletionTime = vmScheduler.updateVMProcessing(0, mipsShare);

            double completionTime1 = GRIDLET_LENGTH / (utilization1 * GRIDLET_LENGTH / 2);
            double completionTime2 = GRIDLET_LENGTH / (utilization2 * GRIDLET_LENGTH / 2);

            double expectedCompletiontime;
            if (completionTime1 < completionTime2) {
                    expectedCompletiontime = completionTime1;
            } else {
                    expectedCompletiontime = completionTime2;
            }

            assertEquals(expectedCompletiontime, actualCompletionTime, 0);

            actualCompletionTime = vmScheduler.updateVMProcessing(1, mipsShare);

            completionTime1 = 1 + (GRIDLET_LENGTH - utilization1 * GRIDLET_LENGTH / 2 * 1) / (utilizationModel1.getUtilization(1) * GRIDLET_LENGTH / 2);
            completionTime2 = 1 + (GRIDLET_LENGTH - utilization2 * GRIDLET_LENGTH / 2 * 1) / (utilizationModel2.getUtilization(1) * GRIDLET_LENGTH / 2);

            if (completionTime1 < completionTime2) {
                    expectedCompletiontime = completionTime1;
            } else {
                    expectedCompletiontime = completionTime2;
            }

            assertEquals(expectedCompletiontime, actualCompletionTime, 0);

            assertFalse(vmScheduler.isFinishedGridlets());

            assertEquals(0, vmScheduler.updateVMProcessing(GRIDLET_LENGTH, mipsShare), 0);

            assertTrue(vmScheduler.isFinishedGridlets());

    }

    @Test
    public void testUpdateVmsProcessing() {
            UtilizationModelStochastic utilizationModel1 = new UtilizationModelStochastic();
            UtilizationModelStochastic utilizationModel2 = new UtilizationModelStochastic();

            Cloudlet cloudlet1 = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                            utilizationModel1, utilizationModel1, utilizationModel1);

            Cloudlet cloudlet2 = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                            utilizationModel2, utilizationModel2, utilizationModel2);

            cloudlet1.setResourceParameter(0, 0, 0);
            cloudlet2.setResourceParameter(0, 0, 0);

            CloudletSchedulerSingleService vmScheduler = new CloudletSchedulerSingleService(PES_NUMBER, MIPS);

            int[] mipsShare = { (int) (CLOUDLET_LENGTH / 2) };
            vmScheduler.setCurrentMipsShare(mipsShare);
            //vmScheduler.setMips(mipsShare[0]);

            double utilization1 = utilizationModel1.getUtilization(0);
            double utilization2 = utilizationModel2.getUtilization(0);

            vmScheduler.cloudletSubmit(cloudlet1);
            vmScheduler.cloudletSubmit(cloudlet2);

            double actualCompletionTime = vmScheduler.updateVmProcessing(0, mipsShare);

            double completionTime1 = CLOUDLET_LENGTH / (utilization1 * CLOUDLET_LENGTH / 2);
            double completionTime2 = CLOUDLET_LENGTH / (utilization2 * CLOUDLET_LENGTH / 2);

            double expectedCompletiontime;
            if (completionTime1 < completionTime2) {
                    expectedCompletiontime = completionTime1;
            } else {
                    expectedCompletiontime = completionTime2;
            }

            assertEquals(expectedCompletiontime, actualCompletionTime, 0);

            actualCompletionTime = vmScheduler.updateVmProcessing(1, mipsShare);

            completionTime1 = 1 + (CLOUDLET_LENGTH - utilization1 * CLOUDLET_LENGTH / 2 * 1) / (utilizationModel1.getUtilization(1) * CLOUDLET_LENGTH / 2);
            completionTime2 = 1 + (CLOUDLET_LENGTH - utilization2 * CLOUDLET_LENGTH / 2 * 1) / (utilizationModel2.getUtilization(1) * CLOUDLET_LENGTH / 2);

            if (completionTime1 < completionTime2) {
                    expectedCompletiontime = completionTime1;
            } else {
                    expectedCompletiontime = completionTime2;
            }

            assertEquals(expectedCompletiontime, actualCompletionTime, 0);

            assertFalse(vmScheduler.isFinishedCloudlets());

            assertEquals(0, vmScheduler.updateVmProcessing(CLOUDLET_LENGTH, mipsShare), 0);

            assertTrue(vmScheduler.isFinishedCloudlets());

    }
    */
}
