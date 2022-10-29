package org.cloudbus.cloudsim.gp.resources;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;

public final class GpuSuitability {
    public static final GpuSuitability NULL = new GpuSuitability ();

    private boolean forGddram;
    private boolean forBw;
    private boolean forCores;

    private String reason;

    public GpuSuitability () { /**/ }

    public GpuSuitability (final String reason) {
        this.reason = Objects.requireNonNull(reason);
    }

    public void setSuitability (final GpuSuitability other) {
    	forCores = forCores && other.forCores;
    	forGddram = forGddram && other.forGddram;
        forBw = forBw && other.forBw;
    }

    public boolean forGddram () {
        return forGddram;
    }

    GpuSuitability setForGddram (final boolean suitable) {
        this.forGddram = suitable;
        return this;
    }

    public boolean forBw () {
        return forBw;
    }

    GpuSuitability setForBw (final boolean suitable) {
        this.forBw = suitable;
        return this;
    }

    public boolean forCores () {
        return forCores;
    }

    GpuSuitability setForCores (final boolean forCores) {
        this.forCores = forCores;
        return this;
    }

    public boolean fully () {
        return forGddram && forBw && forCores;
    }

    @Override
    public String toString(){
        if(fully())
            return "Gpu is fully suitable for the last requested Vgpu";

        if(reason != null)
            return reason;

        final StringBuilder builder = new StringBuilder("lack of");
        if(!forCores)
            builder.append(" Cores,");
        if(!forGddram)
        	builder.append(" GDDRAM,");
        if(!forBw)
            builder.append(" BW,");

        return builder.substring(0, builder.length()-1);
    }
}
