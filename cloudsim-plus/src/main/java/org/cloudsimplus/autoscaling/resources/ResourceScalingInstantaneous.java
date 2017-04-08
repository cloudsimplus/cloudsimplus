package org.cloudsimplus.autoscaling.resources;

import org.cloudbus.cloudsim.util.MathUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

import java.util.function.Function;

/**
 * A {@link ResourceScalingType} for which the capacity of the resource to be scaled will be instantaneously
 * resized to move the Vm from the under or overload state.
 * This way, the SLA violation time will be reduced.
 *
 * <p>This scaling type will resize the resource capacity in the following way:
 * <ul>
 *     <li>in underload conditions: it decreases the resource capacity to be equal to the current load of the resource
 *     being scaled;</li>
 *     <li>in overload conditions: it increases the resource capacity to be equal to the current load of the resource
 *     being scaled.</li>
 * </ul>
 *
 * Finally it adds an extra amount of resource, defined by the {@link VerticalVmScaling#getScalingFactor() scaling factor},
 * for safety. This extra amount added is to enable the resource usage to grow up to the scaling factor
 * without needing to resize the resource again. If it grows up to the scaling factor,
 * a new up scaling request will be sent.
 * </p>
 *
 * <p><b>If the scaling factor for this type of scaling is zero, it means that the scaling object
 * will always resize the resource to the exact amount that is being used.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class ResourceScalingInstantaneous extends ResourceScalingGradual {
    @Override
    public long getResourceAmountToScale() {
        Function<Vm, Double> thresholdFunction = getVmScaling().getResourceUsageThresholdFunction();
        /* Computes the size to which the resource has to be scaled to move it from the under or overload
        * state.*/
        final long newResourceSize =
            (long)Math.ceil(getVmScaling().getVmResourceToScale().getAllocatedResource() *
                MathUtil.HUNDRED_PERCENT / thresholdFunction.apply(getVmScaling().getVm()));

        /*Includes and additional resource amount for safety, according to the scaling factor.
        * This way, if the resource usage increases again up to this extra amount,
        * there is no need to re-scale the resource.
        * If the scale factor is zero, no extra safety amount is included.*/
        final long extraSafetyCapacity = super.getResourceAmountToScale();
        return newResourceSize + extraSafetyCapacity;
    }
}
