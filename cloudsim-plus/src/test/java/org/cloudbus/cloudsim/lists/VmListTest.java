/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;

import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.VmSimpleTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNull;
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
        Vm vm0 = VmSimpleTest.createVm(0, 1);
        Vm vm1 = VmSimpleTest.createVm(1, 2);
        Vm vm2 = VmSimpleTest.createVm(2, 2);

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
        Vm vm0 = VmSimpleTest.createVm(0, 1);
        Vm vm1 = VmSimpleTest.createVm(1, 2);
        Vm vm2 = VmSimpleTest.createVm(2, 2);

        vmList.add(vm0);
        vmList.add(vm1);
        vmList.add(vm2);

        assertSame(Vm.NULL, VmList.getById(vmList, -1));
        assertSame(Vm.NULL, VmList.getById(vmList, vmList.size()));
        assertSame(Vm.NULL, VmList.getById(vmList, vmList.size()+1));
    }

    @Test
    public void testGetVMByIdAndUserId() {
        assertNull(VmList.getByIdAndUserId(vmList, 0, 0));
        assertNull(VmList.getByIdAndUserId(vmList, 1, 0));
        assertNull(VmList.getByIdAndUserId(vmList, 0, 1));
        assertNull(VmList.getByIdAndUserId(vmList, 1, 1));

        final int user0 = 0;
        VmSimple vm1 = VmSimpleTest.createVm(user0, 1);
        VmSimple vm2 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(1, user0, 1);
        
        int user1 = 1;
        VmSimple vm3 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(0, user1, 1);
        VmSimple vm4 = VmSimpleTest.createVmWithSpecificNumberOfPEsForSpecificUser(1, user1, 2);

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
