package org.cloudsimplus.resources;

import org.cloudsimplus.provisioners.PeProvisioner;
import org.cloudsimplus.provisioners.PeProvisionerSimple;

/**
 * A class that implements the Null Object Design Pattern for {@link Pe} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Pe#NULL
 */
final class PeNull implements Pe {
    @Override public long getAvailableResource() {
        return 0;
    }
    @Override public long getAllocatedResource() {
        return 0;
    }
    @Override public boolean isAmountAvailable(long amountToCheck) {
        return false;
    }
    @Override public boolean isAmountAvailable(double amountToCheck) { return false; }
    @Override public String getUnit() { return "Unit"; }
    @Override public boolean isFull() {
        return false;
    }
    @Override public long getId() {
        return -1;
    }
    @Override public long getCapacity() {
        return 0;
    }
    @Override public PeProvisioner getPeProvisioner() {
        return new PeProvisionerSimple();
    }
    @Override public Status getStatus() {
        return Status.FAILED;
    }
    @Override public boolean setCapacity(long mipsCapacity) { return false; }
    @Override public boolean setCapacity(double mipsCapacity) { return false; }
    @Override public boolean sumCapacity(long amountToSum) { return false; }
    @Override public boolean addCapacity(long capacityToAdd) { return false; }
    @Override public boolean removeCapacity(long capacityToRemove) { return false; }
    @Override public Pe setPeProvisioner(PeProvisioner peProvisioner) {
        return Pe.NULL;
    }
    @Override public boolean allocateResource(long amountToAllocate) {
        return false;
    }
    @Override public boolean setAllocatedResource(long newTotalAllocatedResource) {
        return false;
    }
    @Override public boolean deallocateResource(long amountToDeallocate) {
        return false;
    }
    @Override public boolean deallocateAndRemoveResource(long amountToDeallocate) { return false; }
    @Override public long deallocateAllResources() {
        return 0;
    }
    @Override public boolean isResourceAmountBeingUsed(long amountToCheck) {
        return false;
    }
    @Override public boolean isSuitable(long newTotalAllocatedResource) {
        return false;
    }
    @Override public Pe setStatus(Status status) { return this; }
    @Override public boolean isWorking() { return false; }
    @Override public boolean isFailed() { return true; }
    @Override public boolean isFree() { return false; }
    @Override public boolean isBusy() { return false; }
    @Override public Pe setId(long id) {return this;}
}
