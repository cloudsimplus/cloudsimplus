package org.cloudbus.cloudsim.resources;

/**
 * An abstract implementation of a {@link Resource}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public abstract class ResourceAbstract implements Resource {
    /** @see #getCapacity() */
    protected long capacity;

    public ResourceAbstract(final long capacity){
        if(!isCapacityValid(capacity)) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

        this.capacity = capacity;
    }

    private boolean isCapacityValid(final long capacity) {
        return capacity >= 0;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getAllocatedResource() {
        return getCapacity() - getAvailableResource();
    }

    @Override
    public boolean isResourceAmountAvailable(final long amountToCheck) {
        return getAvailableResource() >= amountToCheck;
    }

    @Override
    public boolean isResourceAmountAvailable(double amountToCheck) {
        return isResourceAmountAvailable((long)amountToCheck);
    }

    public boolean isResourceAmountBeingUsed(final long amountToCheck) {
        return getAllocatedResource() >= amountToCheck;
    }

    public boolean isSuitable(final long newTotalAllocatedResource) {
        if(newTotalAllocatedResource <= getAllocatedResource()) {
            return true;
        }

        final long allocationDifference = newTotalAllocatedResource - getAllocatedResource();
        return isResourceAmountAvailable(allocationDifference);
    }
}
