package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;

/**
 * A {@link VerticalVmScaling} implementation that allows a {@link DatacenterBroker}
 * to perform on demand up or down scaling for some VM resource such as RAM, CPU or Bandwidth.
 *
 * <p>For each resource that is required to be scaled, a distinct VerticalVmScaling
 * instance must assigned to the VM to be scaled.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public class VerticalVmScalingSimple extends VmScalingAbstract implements VerticalVmScaling {
    private double scalingFactor;
    private Class<? extends ResourceManageable> resourceClassToScale;

    /**
     * Creates a VerticalVmScaling.
     *
     * @param resourceClassToScale the class of Vm resource that this scaling object will request up or down scaling
     *  (such as {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class).
     * @param scalingFactor the factor that will be used to scale a Vm resource up or down,
     * whether if such a resource is over or underloaded, according to the
     * defined predicates (a percentage value in scale from 0 to 1).
     * In the case of up scaling, the value 1 will scale the resource in 100%, doubling its capacity.
     */
    public VerticalVmScalingSimple(Class<? extends ResourceManageable> resourceClassToScale, double scalingFactor){
        super();
        this.setResourceClassToScale(resourceClassToScale);
        this.setScalingFactor(scalingFactor);
    }

    @Override
    public Class<? extends ResourceManageable> getResourceClassToScale() {
        return this.resourceClassToScale;
    }

    @Override
    public final VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale) {
        Objects.requireNonNull(resourceClassToScale);
        this.resourceClassToScale = resourceClassToScale;
        return this;
    }

    @Override
    public double getScalingFactor() {
        return scalingFactor;
    }

    @Override
    public final VerticalVmScaling setScalingFactor(double scalingFactor) {
        this.scalingFactor = (scalingFactor >= 0 ? scalingFactor : 0);
        return this;
    }

    @Override
    protected boolean requestUpScaling(double time) {
        final Vm vm = this.getVm();
        vm.getSimulation().sendNow(vm.getId(), vm.getBroker().getId(), CloudSimTags.VM_VERTICAL_SCALING, this);
        return true;
    }
}
