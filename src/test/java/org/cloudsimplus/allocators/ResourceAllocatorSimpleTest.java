/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.allocators;

import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.cloudsimplus.vms.VmTestUtil.createVm;
import static org.junit.jupiter.api.Assertions.*;

/// @author		Anton Beloglazov
/// @since		CloudSim Toolkit 2.0
public class ResourceAllocatorSimpleTest {
    private static final long CAPACITY = 1000;
    private static final long HALF_CAPACITY = CAPACITY / 2;
    private static final long QUARTER_OF_CAPACITY = CAPACITY / 4;

    /// @see #createSimpleAllocator()
    private ResourceAllocatorSimple allocator;

    @BeforeEach
    public void setUp() throws Exception {
        allocator = createSimpleAllocator();
    }

    /// Creates a allocator for any generic resource. It in fact doesn't matter
    /// if it is for HOST_RAM, CPU, HOST_BW or any other possible resource.
    private ResourceAllocatorSimple createSimpleAllocator() {
        return new ResourceAllocatorSimple(new Ram(CAPACITY), vm -> ((VmSimple)vm).getRam());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(CAPACITY, allocator.getCapacity());
    }

    @Test
    public void testGetAvailableResource() {
        assertEquals(CAPACITY, allocator.getFree());
    }

    @Test
    public void testGetTotalAllocatedResource() {
        final ResourceAllocator allocator = createSimpleAllocator();
        assertEquals(allocator.getCapacity(), allocator.getFree());
        assertEquals(0, allocator.getTotalAllocatedResource());

        final VmSimple vm = createVm(1, CAPACITY);
        final long allocatedResource = HALF_CAPACITY;
        allocator.allocateResourceForVm(vm, allocatedResource);
        assertEquals(allocatedResource, allocator.getTotalAllocatedResource());
    }

    @Test
    public void testIsSuitableForVm() {
        final VmSimple vm0 = createVm(0, HALF_CAPACITY);
        assertTrue(allocator.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
        assertTrue(allocator.isSuitableForVm(vm0, HALF_CAPACITY));
        assertTrue(allocator.isSuitableForVm(vm0, CAPACITY));
        assertFalse(allocator.isSuitableForVm(vm0, CAPACITY*2));

        allocator.allocateResourceForVm(vm0, HALF_CAPACITY);
        assertTrue(allocator.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
    }

    @Test
    public void testAllocateResourceForVm() {
        final VmSimple vm0 = createVm(0, HALF_CAPACITY);
        final VmSimple vm1 = createVm(1, CAPACITY);

        assertTrue(allocator.isSuitableForVm(vm0, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm0, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getFree());

        assertFalse(allocator.isSuitableForVm(vm1, CAPACITY));
        assertFalse(allocator.allocateResourceForVm(vm1, CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getFree());

        assertTrue(allocator.isSuitableForVm(vm1, QUARTER_OF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm1, QUARTER_OF_CAPACITY));
        assertEquals(QUARTER_OF_CAPACITY, allocator.getFree());

        assertTrue(allocator.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(0, allocator.getFree());
    }

    @Test
    public void testGetAllocatedResourceForVm() {
        final VmSimple vm1 = createVm(0, HALF_CAPACITY);
        final VmSimple vm2 = createVm(1, CAPACITY);

        assertTrue(allocator.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getAllocatedResourceForVm(vm1));

        assertFalse(allocator.isSuitableForVm(vm2, CAPACITY));
        assertFalse(allocator.allocateResourceForVm(vm2, CAPACITY));
        assertEquals(0, allocator.getAllocatedResourceForVm(vm2));

        assertTrue(allocator.isSuitableForVm(vm2, QUARTER_OF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm2, QUARTER_OF_CAPACITY));
        assertEquals(QUARTER_OF_CAPACITY, allocator.getAllocatedResourceForVm(vm2));

        assertTrue(allocator.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getAllocatedResourceForVm(vm2));
    }

    @Test
    public void testDeallocateResourceForVm() {
        final VmSimple vm1 = createVm(0, HALF_CAPACITY);
        final VmSimple vm2 = createVm(1, HALF_CAPACITY);

        assertEquals(0, vm1.getRam().getOccupied());
        assertEquals(0, vm2.getRam().getOccupied());

        assertTrue(allocator.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getFree());

        allocator.deallocateResourceForVm(vm1);
        assertEquals(CAPACITY, allocator.getFree());

        assertTrue(allocator.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertTrue(allocator.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(allocator.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(0, allocator.getFree());

        allocator.deallocateResourceForVm(vm1);
        allocator.deallocateResourceForVm(vm2);
        assertEquals(CAPACITY, allocator.getFree());
        assertEquals(0, vm1.getRam().getOccupied());
        assertEquals(0, vm2.getRam().getOccupied());
    }

    /// Checks that provisioning a resource to a VM doesn't make
    /// the VM resource 100% used, since no Cloudlet is using it yet.
    /// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/557">Issue #557</a>
    @Test
    public void testAllocateResourceForVmDoesNotMakeVmResourceUsed() {
        final VmSimple vm = createVm(0, HALF_CAPACITY);

        assertTrue(allocator.allocateResourceForVm(vm, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, allocator.getAllocatedResourceForVm(vm));
        assertEquals(HALF_CAPACITY, vm.getRam().getCapacity());
        assertEquals(0, vm.getRam().getOccupied());
    }

    /// Checks that re-allocating a resource to a VM (such as in vertical scaling)
    /// keeps the PM resource accounting right and doesn't change the VM capacity,
    /// which is set only when the Host provisions the allocated amount to the VM.
    @Test
    public void testReallocateResourceForVm() {
        final VmSimple vm = createVm(0, HALF_CAPACITY);
        assertTrue(allocator.allocateResourceForVm(vm, HALF_CAPACITY));

        assertTrue(allocator.allocateResourceForVm(vm, CAPACITY));
        assertEquals(0, allocator.getFree());
        assertEquals(CAPACITY, allocator.getAllocatedResourceForVm(vm));
        assertEquals(HALF_CAPACITY, vm.getRam().getCapacity());

        assertTrue(allocator.allocateResourceForVm(vm, QUARTER_OF_CAPACITY));
        assertEquals(CAPACITY - QUARTER_OF_CAPACITY, allocator.getFree());
        assertEquals(QUARTER_OF_CAPACITY, allocator.getAllocatedResourceForVm(vm));
        assertEquals(HALF_CAPACITY, vm.getRam().getCapacity());
    }
}
