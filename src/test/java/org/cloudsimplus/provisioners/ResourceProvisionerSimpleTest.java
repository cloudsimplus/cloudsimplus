/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.provisioners;

import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.cloudsimplus.vms.VmTestUtil.createVm;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class ResourceProvisionerSimpleTest {
    private static final long CAPACITY = 1000;
    private static final long HALF_CAPACITY = CAPACITY / 2;
    private static final long QUARTER_OF_CAPACITY = CAPACITY / 4;

    /** @see #createSimpleProvisioner()  */
    private ResourceProvisionerSimple provisioner;

    @BeforeEach
    public void setUp() throws Exception {
        provisioner = createSimpleProvisioner();
    }

    /**
     * Creates a provisioner for any generic resource. It in fact doesn't matter
     * if it is for HOST_RAM, CPU, HOST_BW or any other possible resource.
     */
    private ResourceProvisionerSimple createSimpleProvisioner() {
        return new ResourceProvisionerSimple(new Ram(CAPACITY), vm -> ((VmSimple)vm).getRam());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(CAPACITY, provisioner.getCapacity());
    }

    @Test
    public void testGetAvailableResource() {
        assertEquals(CAPACITY, provisioner.getAvailableResource());
    }

    @Test
    public void testGetTotalAllocatedResource() {
        final ResourceProvisioner provisioner = createSimpleProvisioner();
        assertEquals(provisioner.getCapacity(), provisioner.getAvailableResource());
        assertEquals(0, provisioner.getTotalAllocatedResource());

        final VmSimple vm = createVm(1, CAPACITY);
        final long allocatedResource = HALF_CAPACITY;
        provisioner.allocateResourceForVm(vm, allocatedResource);
        assertEquals(allocatedResource, provisioner.getTotalAllocatedResource());
    }

    @Test
    public void testIsSuitableForVm() {
        final VmSimple vm0 = createVm(0, HALF_CAPACITY);
        assertTrue(provisioner.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm0, HALF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm0, CAPACITY));
        assertFalse(provisioner.isSuitableForVm(vm0, CAPACITY*2));

        provisioner.allocateResourceForVm(vm0, HALF_CAPACITY);
        assertTrue(provisioner.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
    }

    @Test
    public void testAllocateResourceForVm() {
        final VmSimple vm0 = createVm(0, HALF_CAPACITY);
        final VmSimple vm1 = createVm(1, CAPACITY);

        assertTrue(provisioner.isSuitableForVm(vm0, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm0, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAvailableResource());

        assertFalse(provisioner.isSuitableForVm(vm1, CAPACITY));
        assertFalse(provisioner.allocateResourceForVm(vm1, CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAvailableResource());

        assertTrue(provisioner.isSuitableForVm(vm1, QUARTER_OF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, QUARTER_OF_CAPACITY));
        assertEquals(QUARTER_OF_CAPACITY, provisioner.getAvailableResource());

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(0, provisioner.getAvailableResource());
    }

    @Test
    public void testGetAllocatedResourceForVm() {
        final VmSimple vm1 = createVm(0, HALF_CAPACITY);
        final VmSimple vm2 = createVm(1, CAPACITY);

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm1));

        assertFalse(provisioner.isSuitableForVm(vm2, CAPACITY));
        assertFalse(provisioner.allocateResourceForVm(vm2, CAPACITY));
        assertEquals(0, provisioner.getAllocatedResourceForVm(vm2));

        assertTrue(provisioner.isSuitableForVm(vm2, QUARTER_OF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, QUARTER_OF_CAPACITY));
        assertEquals(QUARTER_OF_CAPACITY, provisioner.getAllocatedResourceForVm(vm2));

        assertTrue(provisioner.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm2));
    }

    @Test
    public void testDeallocateResourceForVm() {
        final VmSimple vm1 = createVm(0, HALF_CAPACITY);
        final VmSimple vm2 = createVm(1, HALF_CAPACITY);

        assertEquals(0, vm1.getRam().getAllocatedResource());
        assertEquals(0, vm2.getRam().getAllocatedResource());

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAvailableResource());

        provisioner.deallocateResourceForVm(vm1);
        assertEquals(CAPACITY, provisioner.getAvailableResource());

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(0, provisioner.getAvailableResource());

        provisioner.deallocateResourceForVm(vm1);
        provisioner.deallocateResourceForVm(vm2);
        assertEquals(CAPACITY, provisioner.getAvailableResource());
        assertEquals(0, vm1.getRam().getAllocatedResource());
        assertEquals(0, vm2.getRam().getAllocatedResource());
    }

    /**
     * Checks that provisioning a resource to a VM doesn't make
     * the VM resource 100% used, since no Cloudlet is using it yet.
     * @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/557">Issue #557</a>
     */
    @Test
    public void testAllocateResourceForVmDoesNotMakeVmResourceUsed() {
        final VmSimple vm = createVm(0, HALF_CAPACITY);

        assertTrue(provisioner.allocateResourceForVm(vm, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm));
        assertEquals(HALF_CAPACITY, vm.getRam().getCapacity());
        assertEquals(0, vm.getRam().getAllocatedResource());
    }

    /**
     * Checks that re-provisioning a resource to a VM (such as in vertical scaling)
     * keeps the PM resource accounting right and doesn't change
     * the amount used by the VM, when it fits the new capacity.
     */
    @Test
    public void testReallocateResourceForVmWhenVmResourceIsBeingUsed() {
        final VmSimple vm = createVm(0, HALF_CAPACITY);
        assertTrue(provisioner.allocateResourceForVm(vm, HALF_CAPACITY));
        vm.getRam().allocateResource(QUARTER_OF_CAPACITY);

        assertTrue(provisioner.allocateResourceForVm(vm, CAPACITY));
        assertEquals(0, provisioner.getAvailableResource());
        assertEquals(CAPACITY, vm.getRam().getCapacity());
        assertEquals(QUARTER_OF_CAPACITY, vm.getRam().getAllocatedResource());

        assertTrue(provisioner.allocateResourceForVm(vm, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAvailableResource());
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm));
        assertEquals(QUARTER_OF_CAPACITY, vm.getRam().getAllocatedResource());
    }

    /**
     * Checks that downscaling a VM resource below the amount used by the VM
     * clears such a usage, so that the new capacity can be set.
     */
    @Test
    public void testReallocateResourceForVmWhenVmResourceUsageIsHigherThanNewCapacity() {
        final VmSimple vm = createVm(0, HALF_CAPACITY);
        assertTrue(provisioner.allocateResourceForVm(vm, HALF_CAPACITY));
        vm.getRam().allocateResource(HALF_CAPACITY);

        assertTrue(provisioner.allocateResourceForVm(vm, QUARTER_OF_CAPACITY));
        assertEquals(CAPACITY - QUARTER_OF_CAPACITY, provisioner.getAvailableResource());
        assertEquals(QUARTER_OF_CAPACITY, vm.getRam().getCapacity());
        assertEquals(0, vm.getRam().getAllocatedResource());
    }
}
