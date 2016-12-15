package org.cloudbus.cloudsim.resources;

/**
 * Represents the Bandwidth (BW) capacity of a PM or VM in Megabits/s.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class Bandwidth extends ResourceAbstract {
    /**
     * Creates a new Bandwidth resource.
     * @param capacity the bandwidth capacity in in Megabits/s
     */
    public Bandwidth(final long capacity) {
        super(capacity);
    }
}
