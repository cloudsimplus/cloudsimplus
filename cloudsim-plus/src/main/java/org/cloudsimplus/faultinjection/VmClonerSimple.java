package org.cloudsimplus.faultinjection;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A basic implementation of a {@link VmCloner}.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.2
 */
public class VmClonerSimple implements VmCloner {
    private UnaryOperator<Vm> vmClonerFunction;
    private Function<Vm, List<Cloudlet>> cloudletsClonerFunction;
    private int maxClonesNumber;
    private int clonedVmsNumber;

    /**
     * Creates a {@link Vm} cloner which makes the maximum of 1 Vm clone.
     *
     * @param vmClonerFunction the {@link UnaryOperator} to be used to clone {@link Vm}s.
     * @param cloudletsClonerFunction the {@link Function} to be used to clone Vm's {@link Cloudlet}s.
     * @see #setMaxClonesNumber(int)
     */
    public VmClonerSimple(final UnaryOperator<Vm> vmClonerFunction, final Function<Vm, List<Cloudlet>> cloudletsClonerFunction){
        this.maxClonesNumber = 1;
        setVmClonerFunction(vmClonerFunction);
        setCloudletsClonerFunction(cloudletsClonerFunction);
    }

    @Override
    public int getClonedVmsNumber() {
        return clonedVmsNumber;
    }

    @Override
    public Map.Entry<Vm, List<Cloudlet>> clone(Vm sourceVm) {
        Objects.requireNonNull(sourceVm);
        final Vm clonedVm = vmClonerFunction.apply(sourceVm);
        final List<Cloudlet> clonedCloudlets = cloudletsClonerFunction.apply(sourceVm);
        clonedCloudlets.stream().forEach(c -> c.setVm(clonedVm));
        clonedVmsNumber++;
        return new HashMap.SimpleEntry<>(clonedVm, clonedCloudlets);
    }

    @Override
    public final VmCloner setVmClonerFunction(final UnaryOperator<Vm> vmClonerFunction) {
        Objects.requireNonNull(vmClonerFunction);
        this.vmClonerFunction = vmClonerFunction;
        return this;
    }

    @Override
    public final VmCloner setCloudletsClonerFunction(final Function<Vm, List<Cloudlet>> cloudletsClonerFunction) {
        Objects.requireNonNull(cloudletsClonerFunction);
        this.cloudletsClonerFunction = cloudletsClonerFunction;
        return this;
    }

    @Override
    public int getMaxClonesNumber() {
        return maxClonesNumber;
    }

    @Override
    public boolean isMaxClonesNumberReached() {
        return clonedVmsNumber >= maxClonesNumber;
    }

    @Override
    public VmCloner setMaxClonesNumber(final int maxClonesNumber) {
        this.maxClonesNumber = maxClonesNumber;
        return this;
    }
}
