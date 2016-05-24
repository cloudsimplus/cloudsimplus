package org.cloudbus.cloudsim.resources;

/**
 * An interface to represent a physical or virtual resource (like RAM, CPU or
 * Bandwidth) with features to manage resource capacity and allocation.
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity of the provisioner
 * @since CloudSim Toolkit 3.0.4
 */
public interface ResourceManageable<T extends Number> extends Resource<T> {

    /**
     * Sets the {@link #getCapacity() resource capacity}.
     *
     * @param newCapacity the new resource capacity
     * @return true if capacity > 0 and capacity >= current allocated resource,
     * false otherwise
     * @see #getAllocatedResource()
     */
    boolean setCapacity(T newCapacity);

    /**
     * Allocates a given amount of the resource, reducing that amount from the
     * total available resource.
     *
     * @param amountToAllocate the amount of resource to be allocated
     * @return true if amountToAllocate > 0 and there is enough resource to
     * allocate, false otherwise
     * @see #setAvailableResource(java.lang.Number)
     */
    boolean allocateResource(T amountToAllocate);

    /**
     * Sets the current total amount of allocated resource, changing it to the
     * given value. It doesn't increase the current allocated resource by the
     * given amount, instead, it changes the allocated resource to that
     * specified amount.
     *
     * @param newTotalAllocatedResource the new total amount of resource to
     * allocate, changing the allocate resource to this new amount.
     * @return true if newTotalAllocatedResource is not negative and there is
     * enough resource to allocate, false otherwise
     * @see #setAvailableResource(java.lang.Number)
     */
    boolean setAllocatedResource(T newTotalAllocatedResource);

    /**
     * Deallocates a given amount of the resource, adding up that amount to the
     * total available resource.
     *
     * @param amountToDeallocate the amount of resource to be deallocated
     * @return true if amountToDeallocate > 0 and there is enough resource to
     * deallocate, false otherwise
     * @see #setAvailableResource(java.lang.Number)
     */
    boolean deallocateResource(T amountToDeallocate);

    /**
     * Deallocates all allocated resources, restoring the total available
     * resource to the resource capacity.
     *
     * @return the amount of resource freed
     * @see #setAvailableResource(java.lang.Number)
     */
    T deallocateAllResources();

    /**
     * Checks if there is a specific amount of resource being used.
     *
     * @param amountToCheck the amount of resource to check if is used.
     * @return true if the specified amount is being used; false otherwise
     */
    boolean isResourceAmountBeingUsed(T amountToCheck);

    /**
     * Checks if it is possible to change the current allocated resource to a
     * new amount, depending on the available resource remaining.
     *
     * @param newTotalAllocatedResource the new total amount of resource to
     * allocate.
     *
     * @return true, if it is possible to allocate the new total resource; false
     * otherwise
     */
    boolean isSuitable(T newTotalAllocatedResource);

    /**
     * A property that implements the Null Object Design Pattern for
     * ResourceManageable&lt;Double&gt; objects.
     */
    ResourceManageable NULL_DOUBLE = new ResourceManageable<Double>() {
        @Override
        public boolean setCapacity(Double newCapacity) {
            return false;
        }

        @Override
        public boolean allocateResource(Double amountToAllocate) {
            return false;
        }

        @Override
        public boolean setAllocatedResource(Double newTotalAllocatedResource) {
            return false;
        }

        @Override
        public boolean deallocateResource(Double amountToDeallocate) {
            return false;
        }

        @Override
        public Double deallocateAllResources() {
            return 0.0;
        }

        @Override
        public boolean isResourceAmountBeingUsed(Double amountToCheck) {
            return false;
        }

        @Override
        public boolean isSuitable(Double newTotalAllocatedResource) {
            return false;
        }

        @Override
        public Double getCapacity() {
            return 0.0;
        }

        @Override
        public Double getAvailableResource() {
            return 0.0;
        }

        @Override
        public Double getAllocatedResource() {
            return 0.0;
        }

        @Override
        public boolean isResourceAmountAvailable(Double amountToCheck) {
            return false;
        }

        @Override
        public boolean isFull() {
            return false;
        }
    };

    /**
     * A property that implements the Null Object Design Pattern for
     * ResourceManageable&lt;Integer&gt; objects.
     */
    ResourceManageable NULL_INT = new ResourceManageable<Integer>() {
        @Override
        public boolean setCapacity(Integer newCapacity) {
            return false;
        }

        @Override
        public boolean allocateResource(Integer amountToAllocate) {
            return false;
        }

        @Override
        public boolean setAllocatedResource(Integer newTotalAllocatedResource) {
            return false;
        }

        @Override
        public boolean deallocateResource(Integer amountToDeallocate) {
            return false;
        }

        @Override
        public Integer deallocateAllResources() {
            return 0;
        }

        @Override
        public boolean isResourceAmountBeingUsed(Integer amountToCheck) {
            return false;
        }

        @Override
        public boolean isSuitable(Integer newTotalAllocatedResource) {
            return false;
        }

        @Override
        public Integer getCapacity() {
            return 0;
        }

        @Override
        public Integer getAvailableResource() {
            return 0;
        }

        @Override
        public Integer getAllocatedResource() {
            return 0;
        }

        @Override
        public boolean isResourceAmountAvailable(Integer amountToCheck) {
            return false;
        }

        @Override
        public boolean isFull() {
            return false;
        }
    };

    /**
     * A property that implements the Null Object Design Pattern for
     * ResourceManageable&lt;Long&gt; objects.
     */
    ResourceManageable NULL_LONG = new ResourceManageable<Long>() {
        @Override
        public boolean setCapacity(Long newCapacity) {
            return false;
        }

        @Override
        public boolean allocateResource(Long amountToAllocate) {
            return false;
        }

        @Override
        public boolean setAllocatedResource(Long newTotalAllocatedResource) {
            return false;
        }

        @Override
        public boolean deallocateResource(Long amountToDeallocate) {
            return false;
        }

        @Override
        public Long deallocateAllResources() {
            return 0L;
        }

        @Override
        public boolean isResourceAmountBeingUsed(Long amountToCheck) {
            return false;
        }

        @Override
        public boolean isSuitable(Long newTotalAllocatedResource) {
            return false;
        }

        @Override
        public Long getCapacity() {
            return 0L;
        }

        @Override
        public Long getAvailableResource() {
            return 0L;
        }

        @Override
        public Long getAllocatedResource() {
            return 0L;
        }

        @Override
        public boolean isResourceAmountAvailable(Long amountToCheck) {
            return false;
        }

        @Override
        public boolean isFull() {
            return false;
        }
    };

}
