package org.cloudbus.cloudsim.resources;

/**
 * Represents the Bandwidth (BW) capacity of a PM or VM.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail.com>
 */
public final class Bandwidth extends AbstractResource<Long> {
    public Bandwidth(Long capacity) {
        super(capacity);
    }
}
