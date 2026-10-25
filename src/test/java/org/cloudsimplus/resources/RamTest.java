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
package org.cloudsimplus.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// Unit tests for the class Ram.
/// Currently, testing this class is enough to test
/// all the related classes ResourceBw, ResourceCpu, etc,
/// once that these classes extend Ram
/// but in fact don't include any new attribute or method.
/// See the documentation of the class [Ram] for
/// more details.
/// @author Manoel Campos da Silva Filho
public class RamTest {
    private static final long CAPACITY = 1000;
    private static final long DOUBLE_CAPACITY = CAPACITY*2;

    private static final long HALF_CAPACITY = CAPACITY/2;
    private static final long QUARTER_CAPACITY = CAPACITY/4;
    private static final long THREE_QUARTERS_CAPACITY = QUARTER_CAPACITY * 3;

    private Ram createResource() {
        return new Ram(CAPACITY);
    }

    @Test
    public void testIsFull() {
        final Ram ram = createResource();
        assertFalse(ram.isFull());
        ram.occupy(HALF_CAPACITY);
        assertFalse(ram.isFull());
        ram.occupy(HALF_CAPACITY);
        assertTrue(ram.isFull());
    }

    @Test
    public void testGetAndSetCapacityPriorToAllocateResource() {
        long expResult = CAPACITY;
        final Ram instance = createResource();
        long result = instance.getCapacity();
        assertEquals(expResult, result);

        expResult = HALF_CAPACITY;
        instance.setCapacity(expResult);
        result = instance.getCapacity();
        assertEquals(expResult, result);

        expResult = DOUBLE_CAPACITY;
        instance.setCapacity(expResult);
        result = instance.getCapacity();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetAndSetCapacityAfterToAllocateResource() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());

        //reduce the capacity
        assertTrue(instance.setCapacity(HALF_CAPACITY));

        assertTrue(instance.setCapacity(0));

        //try an invalid capacity
        assertFalse(instance.setCapacity(-1));

        //restore the original capacity
        assertTrue(instance.setCapacity(CAPACITY));

        //allocate resource and try to reduce the capacity below to the amount allocated
        final long allocated = HALF_CAPACITY;
        instance.occupy(allocated);
        assertEquals(allocated, instance.getOccupied());
        assertFalse(instance.setCapacity(QUARTER_CAPACITY));

        //try to increase the resource capacity
        assertTrue(instance.setCapacity(DOUBLE_CAPACITY));
    }

    @Test
    public void testAllocateResourceInvalidAllocations() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        final long allocation = 0;

        //try allocated an invalid amount
        boolean result = instance.occupy(0);
        assertFalse(result);
        assertEquals(allocation, instance.getOccupied());

        //try allocated an invalid amount
        result = instance.occupy(-1);
        assertFalse(result);
        assertEquals(allocation, instance.getOccupied());

        //try allocated more than the total capacity
        result = instance.occupy(DOUBLE_CAPACITY);
        assertFalse(result);
        assertEquals(allocation, instance.getOccupied());
    }

    @Test
    public void testAllocateResourceMultipleAllocations1() {
        final Ram instance = createResource();
        long allocation = 0;
        long totalAllocation = 0;
        assertEquals(allocation, instance.getOccupied());

        //allocate a valid amount
        allocation = THREE_QUARTERS_CAPACITY;
        totalAllocation += allocation;
        boolean result = instance.occupy(allocation);
        assertTrue(result);
        assertEquals(totalAllocation, instance.getOccupied());
        assertEquals(CAPACITY, instance.getCapacity());

        //try to allocate an amount not available anymore
        allocation = THREE_QUARTERS_CAPACITY;
        result = instance.occupy(allocation);
        assertFalse(result);
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getOccupied());

        //try to allocate an available amount
        allocation = QUARTER_CAPACITY;
        totalAllocation += allocation;
        result = instance.occupy(allocation);
        assertTrue(result);
        assertEquals(totalAllocation, instance.getOccupied());

        //try to allocate an amount not available anymore
        allocation = QUARTER_CAPACITY;
        result = instance.occupy(allocation);
        assertFalse(result);
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getOccupied());
    }

    @Test
    public void testAllocateResourceMultipleAllocations2() {
        final Ram instance = createResource();
        long totalAllocation = 0;
        long totalAvailable = CAPACITY;
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(totalAvailable, instance.getFree());
        assertEquals(0, instance.getOccupied());

        final long allocation = QUARTER_CAPACITY;
        for(int i = 1; i <= 4; i++){
            //checks the available and allocated amount before allocation
            assertEquals(totalAvailable, instance.getFree());
            assertEquals(totalAllocation, instance.getOccupied());
            assertTrue(instance.occupy(allocation));
            //checks the available and allocated amount after allocation
            totalAvailable -= allocation;
            totalAllocation += allocation;
        }
        assertEquals(0, instance.getFree());
        assertEquals(instance.getCapacity(), instance.getOccupied());

        assertFalse(instance.occupy(allocation));
        //available and allocated amount has to be unchanged
        assertEquals(0, instance.getFree());
        assertEquals(instance.getCapacity(), instance.getOccupied());

        //increase the capacity
        final long oldCapacity = CAPACITY;
        final long newCapacity = oldCapacity + allocation;
        assertTrue(instance.setCapacity(newCapacity));
        assertEquals(newCapacity, instance.getCapacity());
        assertEquals(allocation, instance.getFree());
        assertEquals(oldCapacity, instance.getOccupied());

        //try a new allocation
        assertTrue(instance.occupy(allocation));
        assertEquals(0, instance.getFree());
        assertEquals(newCapacity, instance.getOccupied());
    }

    @Test
    public void testSetAvailableResource() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(CAPACITY, instance.getFree());
        assertEquals(0, instance.getOccupied());

        //try an invalid amount of available resource
        long availableResource = -1;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getFree());

        /*Try to free more than the actual capacity*/
        availableResource = DOUBLE_CAPACITY;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getFree());

        //no available resource
        availableResource = 0;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getFree());

        //all resource available
        availableResource = CAPACITY;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getFree());

        //Half of the capacity freely available
        availableResource = HALF_CAPACITY;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getFree());
        assertEquals(availableResource, instance.getOccupied());
        assertFalse(instance.setAvailableResource(-1));
    }

    @Test
    public void testGetAllocatedResource() {
        final Ram instance = createResource();
        long expResult = 0;
        long result = instance.getOccupied();
        assertEquals(expResult, result);

        expResult = HALF_CAPACITY;
        instance.occupy(expResult);
        result = instance.getOccupied();
        assertEquals(expResult, result);

        // try to allocate 0 has to fail and the allocated resource cannot be changed
        long amountToAllocate = 0;
        assertFalse(instance.occupy(amountToAllocate));
        result = instance.getOccupied();
        assertEquals(expResult, result);

        // try to allocate more than available has to fail and the allocated resource cannot be changed
        amountToAllocate = CAPACITY;
        assertFalse(instance.occupy(amountToAllocate));
        result = instance.getOccupied();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetAllocatedResource() {
        final Ram instance = createResource();
        assertEquals(0, instance.getOccupied());

        long newTotalAllocatedResource = HALF_CAPACITY;
        assertTrue(instance.setOccupied(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getOccupied());

        newTotalAllocatedResource = QUARTER_CAPACITY;
        assertTrue(instance.setOccupied(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getOccupied());

        newTotalAllocatedResource = THREE_QUARTERS_CAPACITY;
        assertTrue(instance.setOccupied(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getOccupied());

        newTotalAllocatedResource = CAPACITY;
        final long expResult = CAPACITY;
        assertTrue(instance.setOccupied(newTotalAllocatedResource));
        assertEquals(expResult, instance.getOccupied());

        //it has to fail when trying to allocate more than the capacity
        //the allocated capacity has to stay unchanged
        newTotalAllocatedResource = DOUBLE_CAPACITY;
        assertFalse(instance.setOccupied(newTotalAllocatedResource));
        assertEquals(expResult, instance.getOccupied());
    }

    @Test
    public void testDeallocateResource() {
        final Ram instance = createResource();
        assertEquals(0, instance.getOccupied());

        //try invalid values
        long amountToDeallocate = -1;
        assertFalse(instance.free(amountToDeallocate));
        amountToDeallocate = 0;
        assertFalse(instance.free(amountToDeallocate));

        //there is nothing to deallocate
        amountToDeallocate = HALF_CAPACITY;
        assertFalse(instance.free(amountToDeallocate));

        assertTrue(instance.occupy(CAPACITY));
        assertTrue(instance.free(HALF_CAPACITY));
        assertTrue(instance.free(HALF_CAPACITY));

        final long allocated = HALF_CAPACITY;
        assertTrue(instance.occupy(allocated));
        assertEquals(allocated, instance.getOccupied());
        assertTrue(instance.free(allocated));
        assertFalse(instance.free(allocated));
    }

    @Test
    public void testSumAvailableResource() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getFree());

        //try not change the amount of available resource
        assertTrue(instance.sumAvailableResource(0));
        assertEquals(CAPACITY, instance.getFree());

        //decrease available resource (use of a negative value)
        long amountToSum = -QUARTER_CAPACITY;
        final long expResult = THREE_QUARTERS_CAPACITY;
        assertTrue(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getFree());

        //try decreasing more than the capacity
        amountToSum = -DOUBLE_CAPACITY;
        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getFree());

        //use all resource (there will be no available amount)
        instance.freeAll();
        assertTrue(instance.occupy(CAPACITY));

        //increase available resource
        amountToSum = QUARTER_CAPACITY;
        long totalAvailable = 0;
        for(int i = 1; i <= 4; i++) {
            totalAvailable += amountToSum;
            assertTrue(instance.sumAvailableResource(amountToSum));
            assertEquals(totalAvailable, instance.getFree());
        }

        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(CAPACITY, instance.getFree());
    }

    @Test
    public void testDeallocateAllResources() {
        final Ram instance = createResource();
        final long deallocated = 0;
        assertEquals(deallocated, instance.getOccupied());
        assertEquals(deallocated, instance.freeAll());

        final long allocated = HALF_CAPACITY;
        assertTrue(instance.occupy(allocated));
        assertTrue(instance.occupy(allocated));
        assertEquals(CAPACITY, instance.freeAll());
        assertEquals(0, instance.freeAll());
    }

    @Test
    public void testIsResourceAmountAvailable() {
        final Ram instance = createResource();
        assertTrue(instance.isAmountAvailable(HALF_CAPACITY));
        assertTrue(instance.isAmountAvailable(CAPACITY));

        long allocated = HALF_CAPACITY;
        assertTrue(instance.occupy(allocated));
        assertTrue(instance.isAmountAvailable(allocated));

        allocated = QUARTER_CAPACITY;
        assertTrue(instance.occupy(allocated));
        assertTrue(instance.isAmountAvailable(allocated));
        assertFalse(instance.isAmountAvailable(HALF_CAPACITY));
    }

    @Test
    public void testIsResourceAmountBeingUsed() {
        final Ram instance = createResource();
        assertEquals(0, instance.getOccupied());
        assertTrue(instance.occupy(HALF_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(QUARTER_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(HALF_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(THREE_QUARTERS_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(CAPACITY));
    }

    @Test
    public void testIsSuitable() {
        final Ram instance = createResource();
        assertEquals(0, instance.getOccupied());
        final long allocated = HALF_CAPACITY;
        assertTrue(instance.occupy(allocated));
        assertEquals(allocated, instance.getOccupied());

        assertTrue(instance.isSuitable(CAPACITY));
        assertFalse(instance.isSuitable(CAPACITY*2));
    }
}
