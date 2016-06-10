/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.Ram;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cloudbus.cloudsim.VmSimple;
import org.junit.Before;
import org.junit.Test;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class ResourceProvisionerSimpleTest {
    private static final Integer ZERO = 0;
    private static final Integer CAPACITY = 1000;
    private static final Integer HALF_CAPACITY = CAPACITY / 2;
    private static final Integer QUARTER_OF_CAPACITY = CAPACITY / 4;

    /** @see #createSimpleProvisioner()  */
    private ResourceProvisionerSimple<Integer> provisioner;

    @Before
    public void setUp() throws Exception {
        provisioner = createSimpleProvisioner();
    }

    /**
     * Creates a provisioner for any generic resource. It in fact doesn't matter
     * if it is for RAM, CPU, BW or any other possible resource.
     */
    private ResourceProvisionerSimple<Integer> createSimpleProvisioner() {
        return new ResourceProvisionerSimple(new Ram(CAPACITY));
    }

    private ResourceProvisionerSimple<Integer> createSimpleProvisioner(ResourceManageable resource) {
        return new ResourceProvisionerSimple(resource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProvisioner_null() {
        createSimpleProvisioner(null);
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
        ResourceProvisioner<Integer> p = createSimpleProvisioner();
        assertEquals(p.getCapacity(), p.getAvailableResource());
        assertEquals(ZERO, p.getTotalAllocatedResource());

        VmSimple vm = createVm(1, CAPACITY);
        final Integer allocatedResource = HALF_CAPACITY;
        p.allocateResourceForVm(vm, allocatedResource);
        assertEquals(allocatedResource, p.getTotalAllocatedResource());
    }

    /**
     * Creates a VM
     * @param vmId
     * @param capacity a capacity that will be set to all resources, such as 
     * CPU, RAM, BW, etc.
     * @return 
     */
    private static VmSimple createVm(final int vmId, Integer capacity) {
        return new VmSimple(
                vmId, 1, capacity, 1, capacity, capacity, 
                capacity, "", CloudletScheduler.NULL);
    }

    @Test
    public void testIsSuitableForVm() {
        VmSimple vm0 = createVm(0, HALF_CAPACITY);
        assertTrue(provisioner.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm0, HALF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm0, CAPACITY));
        assertFalse(provisioner.isSuitableForVm(vm0, CAPACITY*2));
        
        provisioner.allocateResourceForVm(vm0, HALF_CAPACITY);
        assertTrue(provisioner.isSuitableForVm(vm0, QUARTER_OF_CAPACITY));
    }
    
    @Test
    public void testAllocateResourceForVm() {
        VmSimple vm0 = createVm(0, HALF_CAPACITY);
        VmSimple vm1 = createVm(1, CAPACITY);

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
        assertEquals(ZERO, provisioner.getAvailableResource());
    }

    @Test
    public void testGetAllocatedResourceForVm() {
        VmSimple vm1 = createVm(0, HALF_CAPACITY);
        VmSimple vm2 = createVm(1, CAPACITY);

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm1));

        assertFalse(provisioner.isSuitableForVm(vm2, CAPACITY));
        assertFalse(provisioner.allocateResourceForVm(vm2, CAPACITY));
        assertEquals(ZERO, provisioner.getAllocatedResourceForVm(vm2));

        assertTrue(provisioner.isSuitableForVm(vm2, QUARTER_OF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, QUARTER_OF_CAPACITY));
        assertEquals(QUARTER_OF_CAPACITY, provisioner.getAllocatedResourceForVm(vm2));

        assertTrue(provisioner.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAllocatedResourceForVm(vm2));
    }

    @Test
    public void testDeallocateResourceForVm() {
        VmSimple vm1 = createVm(0, HALF_CAPACITY);
        VmSimple vm2 = createVm(1, HALF_CAPACITY);

        assertEquals(0, vm1.getCurrentAllocatedRam());
        assertEquals(0, vm2.getCurrentAllocatedRam());

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertEquals(HALF_CAPACITY, provisioner.getAvailableResource());

        provisioner.deallocateResourceForVm(vm1);
        assertEquals(CAPACITY, provisioner.getAvailableResource());

        assertTrue(provisioner.isSuitableForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm1, HALF_CAPACITY));
        assertTrue(provisioner.isSuitableForVm(vm2, HALF_CAPACITY));
        assertTrue(provisioner.allocateResourceForVm(vm2, HALF_CAPACITY));
        assertEquals(ZERO, provisioner.getAvailableResource());

        provisioner.deallocateResourceForVm(vm1);
        provisioner.deallocateResourceForVm(vm2);
        assertEquals(CAPACITY, provisioner.getAvailableResource());
        assertEquals(0, vm1.getCurrentAllocatedRam());
        assertEquals(0, vm2.getCurrentAllocatedRam());
    }
}
