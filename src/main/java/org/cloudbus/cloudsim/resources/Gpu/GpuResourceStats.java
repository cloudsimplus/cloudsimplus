package org.cloudbus.cloudsim.gp.resources;

import org.cloudbus.cloudsim.gp.core.GResourceStats;
import java.util.function.Function;
 
public class GpuResourceStats extends GResourceStats<Gpu> {
    public static final GpuResourceStats NULL = new GpuResourceStats(Gpu.NULL, gpu -> 0.0) 
    { @Override public boolean add(double time) { return false; }};

    public GpuResourceStats(final Gpu machine, 
    		final Function<Gpu, Double> resourceUtilizationFunction) {
        super(machine, resourceUtilizationFunction);
    }

    @Override
    public boolean add(final double time) {
        return super.add(time) && getMachine().isActive();
    }
}
