package org.cloudbus.cloudsim.resources;

/**
 * Represents the RAM capacity of a PM or VM.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail.com>
 */
public final class Ram extends AbstractResource<Integer> {
    public Ram(Integer capacity) {
        super(capacity);
    }   
}
