/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeSharedTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class CloudletSchedulerTimeSharedTest {
    private CloudletSchedulerTimeShared instance;

    @BeforeEach
    public void setUp(){
        instance = new CloudletSchedulerTimeShared();
    }

    @Test
    public void testGetCloudletWaitingListWhenEmpty() {
        assertTrue(instance.getCloudletWaitingList().isEmpty());
    }

    @Test
    public void testCloudletResumeWhenEmptyPausedList() {
        final int cloudletId = 0;
        final double expResult = 0.0;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletWaitingListWhenEmptyAfterResumingCloudlet() {
        final long cloudletLength = 1000;
        final var instance = createCloudletSchedulerWithMipsList(1, cloudletLength);
        final int cloudletId = 0;
        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));
        assertTrue(instance.getCloudletWaitingList().isEmpty());
    }

    @Test
    public void testCloudletResumeWhenCloudletNotInPausedList() {
        final int cloudletIdInTheList = 1;
        instance.getCloudletPausedList().add(createCloudletExecInfo(cloudletIdInTheList));
        final double expResult = 0.0;
        final int cloudletIdSearched = 2;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletIdSearched, 1, 1));
        assertEquals(expResult, result);
    }

    @Test
    public void testCloudletResumeWhenCloudletInPausedList() {
        final int cloudletId = 1;
        final int schedulerCores = 1;
        final long mips = 1000;
        final long cloudletLength = 10000;
        final var instance = createCloudletSchedulerWithMipsList(schedulerCores, mips);

        createCloudletAndAddItToPausedList(instance, cloudletId, cloudletLength);
        final double expResult = 10;
        final double result = instance.cloudletResume(new CloudletSimple(cloudletId, 1, 1));

        assertEquals(expResult, result);
    }

    @Test
    public void testIsThereEnoughFreeCoresForCloudletWhenEmptyList() {
        final int cloudletCores = 1;
        final Cloudlet cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletCores);
        //Vm processing was never updated, so the processing capacity is unknow yet
        assertFalse(instance.isThereEnoughFreeCoresForCloudlet(new CloudletExecution(cloudlet0)));
    }

    @Test
    public void testIsThereEnoughFreeCoresForCloudletWhenThereIsOneRunningCloudlet() {
        final int cloudletCores = 1;
        final int schedulerCores = 2;
        final long schedulerMips = 1000;
        final var cloudlet0 = CloudletTestUtil.createCloudlet(0, cloudletCores);
        final var instance = createCloudletSchedulerWithMipsList(schedulerCores, schedulerMips);
        instance.cloudletSubmit(cloudlet0);
        final double time0 = 0;
        instance.updateProcessing(time0, instance.getCurrentMipsShare());
        assertTrue(instance.isThereEnoughFreeCoresForCloudlet(new CloudletExecution(cloudlet0)));
    }

    /// When Cloudlets demand more cores than the VM has, removing one of them
    /// must keep all VM cores busy while the remaining Cloudlets still use them.
    @Test
    public void testVmCoresStayBusyWhenCloudletsDemandMoreCoresThanVmHas() {
        final int vmCores = 2;
        final var instance = newSchedulerWithSingleCoreRunningCloudlets(1000, vmCores, vmCores + 1);
        final var vmProcessor = instance.getVm().getProcessor();
        assertEquals(vmCores, vmProcessor.getBusyCores());

        instance.cloudletCancel(instance.getCloudletExecList().getFirst().getCloudlet());
        assertEquals(vmCores, vmProcessor.getBusyCores());
    }

    @Test
    public void testGetTotalCurrentAllocatedMipsForCloudlet() {
        final var ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getAllocatedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTotalCurrentRequestedMipsForCloudlet() {
        final var ce = new CloudletExecution(Cloudlet.NULL);
        final double time = 0.0;
        final double expResult = 0.0;
        final double result = instance.getRequestedMipsForCloudlet(ce, time);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfRam() {
        final int cloudlets = 2;
        final var model = new UtilizationModelFull();
        final var instance = newSchedulerWithRunningCloudlets(1000, 2, cloudlets, 1, model);

        final double expResult = 2.0; //200% of RAM usage
        final double result = instance.getCurrentRequestedRamPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCurrentRequestedUtilizationOfBw() {
        final long mips = 1000;
        final int vmCores = 1;
        final int cloudletsNumber = vmCores;
        final int cloudletCores = 1;
        final var instance = newSchedulerWithRunningCloudlets(mips, vmCores, cloudletsNumber, cloudletCores);

        final double expResult = 1.0;
        final double result = instance.getCurrentRequestedBwPercentUtilization();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetCloudletExecListWhenEmpty() {
        assertTrue(instance.getCloudletExecList().isEmpty());
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenMoreCloudletsThanCores() {
        final long mips = 1000;
        final int cores = 2;
        final int cloudletsNumber = 4;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, cores, cloudletsNumber);

        final double expected = 1.0;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenOneCoreForEachCloudlet() {
        final long mips = 1000;
        final int cores = 2;
        final int cloudletsNumber = cores;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, cores, cloudletsNumber);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanCoresHalfUsage() {
        final long mips = 1000;
        final int cores = 4;
        final int cloudletsNumber = 2;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, cores, cloudletsNumber);

        final double expected = 0.5;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanCoresThreeThirdUsage() {
        final long mips = 1000;
        final int cores = 4;
        final int cloudletsNumber = 3;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, cores, cloudletsNumber);

        final double expected = 0.75;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenLessCloudletsThanCoresNotFullUsage() {
        final long mips = 1000;
        final int cores = 5;
        final int cloudletsNumber = 4;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, cores, cloudletsNumber);

        final double expected = 0.8;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    @Test
    public void testGetTotalUtilizationOfCpuWhenDualCoresCloudlets() {
        final long mips = 1000;
        final int vmCores = 4;
        final int cloudlets = 2;
        final int cloudletCores = 2;

        final var instance = newSchedulerWithRunningCloudlets(mips, vmCores, cloudlets, cloudletCores);

        final double expected = 1;
        assertEquals(expected, instance.getRequestedCpuPercent(0));
    }

    /// Runs 2 cloudlets requiring just 1 core each one.
    /// These cloudlets are run in a CloudletScheduler having 2 cores,
    /// one for each cloudlet. The cloudlet length is equals to the capacity of
    /// each core, meaning that each cloudlet will finish in just one second.
    @Test
    public void testGetCloudletExecListWhenEmptyAfterFinishedCloudletsForTwoSchedulerCores() {
        final long mips = 1000;
        final int cloudlets = 2;
        final int vmCores = cloudlets;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, vmCores, cloudlets);

        final double time = 1.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertEquals(0, instance.getCloudletExecList().size());
    }

    /// Runs 2 cloudlets requiring just 1 core each one.
    /// These cloudlets are run in a CloudletScheduler having just 1 core
    /// that is shared between the cloudlets.
    /// The cloudlet length is equals to the capacity of
    /// each core, meaning that each cloudlet will finish in 2 seconds
    /// because there is just 1 core.
    @Test
    public void testGetCloudletExecListWhenEmptyAfterFinishedCloudletsForOneSchedulerCore() {
        final long mips = 1000;
        final int cloudlets = 2;
        final int vmCores = 1;

        final var instance = newSchedulerWithSingleCoreRunningCloudlets(mips, vmCores, cloudlets);
        final var broker = createBroker();
        instance.getCloudletExecList().forEach(ce -> ce.getCloudlet().setBroker(broker));

        final double time = 2.0;
        instance.updateProcessing(time, instance.getCurrentMipsShare());
        assertEquals(0, instance.getCloudletExecList().size());
    }

    /// Checks if VM RAM and BW used by a Cloudlet is released after
    /// it finishes and there is no other Cloudlet running.
    /// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/429">Issue #429</a>
    @Test
    public void testVmRamAndBwAreReleasedAfterLastCloudletFinishes() {
        final long mips = 1000;
        final int cloudlets = 1;
        final int vmCores = 1;
        final var ramBwModel = new UtilizationModelDynamic(0.5);

        final var instance = newSchedulerWithRunningCloudlets(mips, vmCores, cloudlets, 1, ramBwModel);
        final var vm = (VmSimple) instance.getVm();

        instance.updateProcessing(0.5, instance.getCurrentMipsShare());
        assertTrue(vm.getRam().getAllocatedResource() > 0);
        assertTrue(vm.getBw().getAllocatedResource() > 0);

        instance.updateProcessing(1.0, instance.getCurrentMipsShare());
        assertTrue(instance.getCloudletExecList().isEmpty());
        assertEquals(0, vm.getRam().getAllocatedResource());
        assertEquals(0, vm.getBw().getAllocatedResource());
    }

    private static DatacenterBrokerSimple createBroker() {
        final var simulation = CloudSimMocker.createMock(cloudsim -> cloudsim.clock(2));
        final var broker = new DatacenterBrokerSimple(simulation);
        return broker;
    }

    @Test
    public void testRemoveCloudletFromExecList() {
        final var cle = new CloudletExecution(Cloudlet.NULL);
        instance.addCloudletToExecList(cle);
        assertSame(cle, instance.removeCloudletFromExecList(cle));
        assertEquals(0, instance.getCloudletExecList().size());
    }

    @Test
    public void testRemoveCloudletFromExecListWhenCloudletNoFound() {
        final var cloudletNotAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOneCore(0));
        final var cloudletAdded = new CloudletExecution(CloudletTestUtil.createCloudletWithOneCore(1));
        final var list = new ArrayList<CloudletExecution>();
        list.add(cloudletAdded);
        instance.addCloudletToExecList(cloudletAdded);
        assertSame(CloudletExecution.NULL, instance.removeCloudletFromExecList(cloudletNotAdded));
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }

    @Test
    public void testAddCloudletToExecList() {
        final var cloudlet = new CloudletExecution(Cloudlet.NULL);
        final var list = new ArrayList<CloudletExecution>();
        list.add(cloudlet);
        instance.addCloudletToExecList(cloudlet);
        assertEquals(list.size(), instance.getCloudletExecList().size());
    }
}
