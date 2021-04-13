package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.hosts.Host;

import java.util.function.Function;

/**
 * Computes resource utilization statistics for a specific resource on a given {@link Host}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.1.0
 */
public class HostResourceStats extends AbstractResourceStats<Host> {
    public static final HostResourceStats NULL = new HostResourceStats(Host.NULL, host -> 0.0) { @Override public boolean add(double time) { return false; }};

    /**
     * Creates a HostResourceStats to collect resource utilization statistics for a Host.
     * @param machine the Host where the statistics will be collected
     * @param resourceUtilizationFunction a {@link Function} that receives a Host
     *                                    and returns the current resource utilization for that Host
     */
    public HostResourceStats(final Host machine, final Function<Host, Double> resourceUtilizationFunction) {
        super(machine, resourceUtilizationFunction);
    }

    /**
     * {@inheritDoc}.
     * The method is automatically called when the Host processing is updated.
     * @param time {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean add(final double time) {
        return super.add(time) && getMachine().isActive();
    }
}
