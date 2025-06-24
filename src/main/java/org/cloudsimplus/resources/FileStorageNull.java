package org.cloudsimplus.resources;

/**
 * A class that implements the Null Object Design Pattern for {@link FileStorage} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see FileStorage#NULL
 * @since CloudSim Plus 6.3.0
 */
final class FileStorageNull implements FileStorage {
    @Override public double getMaxTransferRate() { return 0; }
    @Override public FileStorage setMaxTransferRate(double maxTransferRate) { return this; }
    @Override public FileStorage setLatency(double latency) { return this; }
    @Override public double getLatency() { return 0; }
    @Override public double getTransferTime(int fileSize) { return 0; }
    @Override public long getAvailableResource() { return 0; }
    @Override public long getAllocatedResource() { return 0; }
    @Override public boolean isAmountAvailable(long amountToCheck) { return false; }
    @Override public String getUnit() { return ""; }
    @Override public long getCapacity() { return 0; }
}
