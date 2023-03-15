package org.cloudsimplus.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the class Ram.
 * Currently, testing this class is enough to test
 all the related classes ResourceBw, ResourceCpu, etc,
 once that these classes extend Ram
 but in fact don't include any new attribute or method.
 See the documentation of the class {@link Ram} for
 * more details.
 * @author Manoel Campos da Silva Filho
 */
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
        ram.allocateResource(HALF_CAPACITY);
        assertFalse(ram.isFull());
        ram.allocateResource(HALF_CAPACITY);
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
        instance.allocateResource(allocated);
        assertEquals(allocated, instance.getAllocatedResource());
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
        boolean result = instance.allocateResource(0);
        assertFalse(result);
        assertEquals(allocation, instance.getAllocatedResource());

        //try allocated an invalid amount
        result = instance.allocateResource(-1);
        assertFalse(result);
        assertEquals(allocation, instance.getAllocatedResource());

        //try allocated more than the total capacity
        result = instance.allocateResource(DOUBLE_CAPACITY);
        assertFalse(result);
        assertEquals(allocation, instance.getAllocatedResource());
    }

    @Test
    public void testAllocateResourceMultipleAllocations1() {
        final Ram instance = createResource();
        long allocation = 0;
        long totalAllocation = 0;
        assertEquals(allocation, instance.getAllocatedResource());

        //allocate a valid amount
        allocation = THREE_QUARTERS_CAPACITY;
        totalAllocation += allocation;
        boolean result = instance.allocateResource(allocation);
        assertTrue(result);
        assertEquals(totalAllocation, instance.getAllocatedResource());
        assertEquals(CAPACITY, instance.getCapacity());

        //try to allocate an amount not available anymore
        allocation = THREE_QUARTERS_CAPACITY;
        result = instance.allocateResource(allocation);
        assertFalse(result);
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getAllocatedResource());

        //try to allocate an available amount
        allocation = QUARTER_CAPACITY;
        totalAllocation += allocation;
        result = instance.allocateResource(allocation);
        assertTrue(result);
        assertEquals(totalAllocation, instance.getAllocatedResource());

        //try to allocate an amount not available anymore
        allocation = QUARTER_CAPACITY;
        result = instance.allocateResource(allocation);
        assertFalse(result);
        //the allocated amount has keep unchanged, with the same value of the first allocation
        assertEquals(totalAllocation, instance.getAllocatedResource());
    }

    @Test
    public void testAllocateResourceMultipleAllocations2() {
        final Ram instance = createResource();
        long totalAllocation = 0;
        long totalAvailable = CAPACITY;
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(totalAvailable, instance.getAvailableResource());
        assertEquals(0, instance.getAllocatedResource());

        final long allocation = QUARTER_CAPACITY;
        for(int i = 1; i <= 4; i++){
            //checks the available and allocated amount before allocation
            assertEquals(totalAvailable, instance.getAvailableResource());
            assertEquals(totalAllocation, instance.getAllocatedResource());
            assertTrue(instance.allocateResource(allocation));
            //checks the available and allocated amount after allocation
            totalAvailable -= allocation;
            totalAllocation += allocation;
        }
        assertEquals(0, instance.getAvailableResource());
        assertEquals(instance.getCapacity(), instance.getAllocatedResource());

        assertFalse(instance.allocateResource(allocation));
        //available and allocated amount has to be unchanged
        assertEquals(0, instance.getAvailableResource());
        assertEquals(instance.getCapacity(), instance.getAllocatedResource());

        //increase the capacity
        final long oldCapacity = CAPACITY;
        final long newCapacity = oldCapacity + allocation;
        assertTrue(instance.setCapacity(newCapacity));
        assertEquals(newCapacity, instance.getCapacity());
        assertEquals(allocation, instance.getAvailableResource());
        assertEquals(oldCapacity, instance.getAllocatedResource());

        //try a new allocation
        assertTrue(instance.allocateResource(allocation));
        assertEquals(0, instance.getAvailableResource());
        assertEquals(newCapacity, instance.getAllocatedResource());
    }

    @Test
    public void testSetAvailableResource() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getCapacity());
        assertEquals(CAPACITY, instance.getAvailableResource());
        assertEquals(0, instance.getAllocatedResource());

        //try an invalid amount of available resource
        long availableResource = -1;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getAvailableResource());

        /*Try to free more than the actual capacity*/
        availableResource = DOUBLE_CAPACITY;
        assertFalse(instance.setAvailableResource(availableResource));
        assertEquals(CAPACITY, instance.getAvailableResource());

        //no available resource
        availableResource = 0;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getAvailableResource());

        //all resource available
        availableResource = CAPACITY;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getAvailableResource());

        //Half of the capacity freely available
        availableResource = HALF_CAPACITY;
        assertTrue(instance.setAvailableResource(availableResource));
        assertEquals(availableResource, instance.getAvailableResource());
        assertEquals(availableResource, instance.getAllocatedResource());
        assertFalse(instance.setAvailableResource(-1));
    }

    @Test
    public void testGetAllocatedResource() {
        final Ram instance = createResource();
        long expResult = 0;
        long result = instance.getAllocatedResource();
        assertEquals(expResult, result);

        expResult = HALF_CAPACITY;
        instance.allocateResource(expResult);
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);

        // try to allocate 0 has to fail and the allocated resource cannot be changed
        long amountToAllocate = 0;
        assertFalse(instance.allocateResource(amountToAllocate));
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);

        // try to allocate more than available has to fail and the allocated resource cannot be changed
        amountToAllocate = CAPACITY;
        assertFalse(instance.allocateResource(amountToAllocate));
        result = instance.getAllocatedResource();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetAllocatedResource() {
        final Ram instance = createResource();
        assertEquals(0, instance.getAllocatedResource());

        long newTotalAllocatedResource = HALF_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());

        newTotalAllocatedResource = QUARTER_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());

        newTotalAllocatedResource = THREE_QUARTERS_CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(newTotalAllocatedResource, instance.getAllocatedResource());

        newTotalAllocatedResource = CAPACITY;
        final long expResult = CAPACITY;
        assertTrue(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(expResult, instance.getAllocatedResource());

        //it has to fail when trying to allocate more than the capacity
        //the allocated capacity has to stay unchanged
        newTotalAllocatedResource = DOUBLE_CAPACITY;
        assertFalse(instance.setAllocatedResource(newTotalAllocatedResource));
        assertEquals(expResult, instance.getAllocatedResource());
    }

    @Test
    public void testDeallocateResource() {
        final Ram instance = createResource();
        assertEquals(0, instance.getAllocatedResource());

        //try invalid values
        long amountToDeallocate = -1;
        assertFalse(instance.deallocateResource(amountToDeallocate));
        amountToDeallocate = 0;
        assertFalse(instance.deallocateResource(amountToDeallocate));

        //there is nothing to deallocate
        amountToDeallocate = HALF_CAPACITY;
        assertFalse(instance.deallocateResource(amountToDeallocate));

        assertTrue(instance.allocateResource(CAPACITY));
        assertTrue(instance.deallocateResource(HALF_CAPACITY));
        assertTrue(instance.deallocateResource(HALF_CAPACITY));

        final long allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertEquals(allocated, instance.getAllocatedResource());
        assertTrue(instance.deallocateResource(allocated));
        assertFalse(instance.deallocateResource(allocated));
    }

    @Test
    public void testSumAvailableResource() {
        final Ram instance = createResource();
        assertEquals(CAPACITY, instance.getAvailableResource());

        //try not change the amount of available resource
        assertTrue(instance.sumAvailableResource(0));
        assertEquals(CAPACITY, instance.getAvailableResource());

        //decrease available resource (use of a negative value)
        long amountToSum = -QUARTER_CAPACITY;
        final long expResult = THREE_QUARTERS_CAPACITY;
        assertTrue(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getAvailableResource());

        //try decreasing more than the capacity
        amountToSum = -DOUBLE_CAPACITY;
        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(expResult, instance.getAvailableResource());

        //use all resource (there will be no available amount)
        instance.deallocateAllResources();
        assertTrue(instance.allocateResource(CAPACITY));

        //increase available resource
        amountToSum = QUARTER_CAPACITY;
        long totalAvailable = 0;
        for(int i = 1; i <= 4; i++) {
            totalAvailable += amountToSum;
            assertTrue(instance.sumAvailableResource(amountToSum));
            assertEquals(totalAvailable, instance.getAvailableResource());
        }

        assertFalse(instance.sumAvailableResource(amountToSum));
        assertEquals(CAPACITY, instance.getAvailableResource());
    }

    @Test
    public void testDeallocateAllResources() {
        final Ram instance = createResource();
        final long deallocated = 0;
        assertEquals(deallocated, instance.getAllocatedResource());
        assertEquals(deallocated, instance.deallocateAllResources());

        final long allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.allocateResource(allocated));
        assertEquals(CAPACITY, instance.deallocateAllResources());
        assertEquals(0, instance.deallocateAllResources());
    }

    @Test
    public void testIsResourceAmountAvailable() {
        final Ram instance = createResource();
        assertTrue(instance.isAmountAvailable(HALF_CAPACITY));
        assertTrue(instance.isAmountAvailable(CAPACITY));

        long allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.isAmountAvailable(allocated));

        allocated = QUARTER_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertTrue(instance.isAmountAvailable(allocated));
        assertFalse(instance.isAmountAvailable(HALF_CAPACITY));
    }

    @Test
    public void testIsResourceAmountBeingUsed() {
        final Ram instance = createResource();
        assertEquals(0, instance.getAllocatedResource());
        assertTrue(instance.allocateResource(HALF_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(QUARTER_CAPACITY));
        assertTrue(instance.isResourceAmountBeingUsed(HALF_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(THREE_QUARTERS_CAPACITY));
        assertFalse(instance.isResourceAmountBeingUsed(CAPACITY));
    }

    @Test
    public void testIsSuitable() {
        final Ram instance = createResource();
        assertEquals(0, instance.getAllocatedResource());
        final long allocated = HALF_CAPACITY;
        assertTrue(instance.allocateResource(allocated));
        assertEquals(allocated, instance.getAllocatedResource());

        assertTrue(instance.isSuitable(CAPACITY));
        assertFalse(instance.isSuitable(CAPACITY*2));
    }
}
