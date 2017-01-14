package org.cloudbus.cloudsim.resources;

/**
 * A simple storage that just manages the device capacity and raw allocated (used) space.
 * It doesn't deals with files neither with file system operations such as
 * file inclusion or deletion.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public final class RawStorage extends ResourceAbstract {
    /**
     * Creates a new Storage device.
     * @param capacity the storage capacity in Megabytes
     */
    public RawStorage(long capacity) {
        super(capacity);
    }
}
