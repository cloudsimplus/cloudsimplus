package org.cloudbus.cloudsim.vms;

import java.util.function.Function;

/**
 * Computes resource utilization statistics for a specific resource on a given {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class VmResourceStats extends AbstractResourceStats<Vm> {
    public static final VmResourceStats NULL = new VmResourceStats(Vm.NULL, vm -> 0.0) { @Override public boolean add(double time) { return false; }};

    /**
     * Creates a VmResourceStats to collect resource utilization statistics for a VM.
     * @param machine the VM where the statistics will be collected
     * @param resourceUtilizationFunction a {@link Function} that receives a VM
     *                                    and returns the current resource utilization for that VM
     */
    public VmResourceStats(final Vm machine, final Function<Vm, Double> resourceUtilizationFunction) {
        super(machine, resourceUtilizationFunction);
    }
}
