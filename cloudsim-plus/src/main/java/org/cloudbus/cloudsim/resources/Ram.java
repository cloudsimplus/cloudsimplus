package org.cloudbus.cloudsim.resources;

/**
 * Represents the RAM capacity of a PM or VM in Mebabytes.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class Ram extends AbstractResource {
    /**
     * Creates a new RAM resource.
     * @param capacity the RAM capacity in Megabytes
     */
    public Ram(final long capacity) {
        super(capacity);
    }
}
