/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.lists;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class VmListTest {

    private List<Vm> vmList;

    @Before
    public void setUp() throws Exception {
        vmList = new ArrayList<>();
    }

    @Test
    public void testGetById() {
        final Vm vm0 = VmSimpleTest.createVm(0, 1);
        final Vm vm1 = VmSimpleTest.createVm(1, 2);
        final Vm vm2 = VmSimpleTest.createVm(2, 2);

        vmList.add(vm0);
        vmList.add(vm1);
        vmList.add(vm2);

        assertSame(vm0, VmList.getById(vmList, 0));
        assertSame(vm1, VmList.getById(vmList, 1));
        assertSame(vm2, VmList.getById(vmList, 2));
    }

    @Test
    public void testGetById_EmptyList() {
        assertSame(Vm.NULL, VmList.getById(vmList, -1));
        assertSame(Vm.NULL, VmList.getById(vmList, 0));
        assertSame(Vm.NULL, VmList.getById(vmList, 1));
    }

    @Test
    public void testGetById_NotFoundVm() {
        final Vm vm0 = VmSimpleTest.createVm(0, 1);
        final Vm vm1 = VmSimpleTest.createVm(1, 2);
        final Vm vm2 = VmSimpleTest.createVm(2, 2);

        vmList.add(vm0);
        vmList.add(vm1);
        vmList.add(vm2);

        assertSame(Vm.NULL, VmList.getById(vmList, -1));
        assertSame(Vm.NULL, VmList.getById(vmList, vmList.size()));
        assertSame(Vm.NULL, VmList.getById(vmList, vmList.size()+1));
    }

    @Test
    public void testGetVMByIdAndUserId() {
        assertSame(Vm.NULL, VmList.getByIdAndUserId(vmList, 0, 0));
        assertSame(Vm.NULL, VmList.getByIdAndUserId(vmList, 1, 0));
        assertSame(Vm.NULL, VmList.getByIdAndUserId(vmList, 0, 1));
        assertSame(Vm.NULL, VmList.getByIdAndUserId(vmList, 1, 1));

        final DatacenterBroker broker0 = Mocks.createMockBroker(0, 4);
        final VmSimple vm1 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(0, broker0, 1);
        final VmSimple vm2 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(1, broker0, 1);

        final DatacenterBroker broker1 = Mocks.createMockBroker(1, 4);
        final VmSimple vm3 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(0, broker1, 1);
        final VmSimple vm4 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(1, broker1, 2);

        vmList.add(vm1);
        vmList.add(vm2);
        vmList.add(vm3);
        vmList.add(vm4);

        assertSame(vm1, VmList.getByIdAndUserId(vmList, 0, 0));
        assertSame(vm2, VmList.getByIdAndUserId(vmList, 1, 0));
        assertSame(vm3, VmList.getByIdAndUserId(vmList, 0, 1));
        assertSame(vm4, VmList.getByIdAndUserId(vmList, 1, 1));
    }
}
