package org.cloudsimplus.autoscaling.resources;

import org.cloudsimplus.autoscaling.VerticalVmScaling;

/**
 * A {@link ResourceScalingType} for which the capacity of the resource to be scaled will be gradually
 * resized according to the defined {@link VerticalVmScaling#getScalingFactor() scaling factor}.
 * This scaling type may not automatically move a Vm from an under or overload state,
 * since it will increase or decrease the resource capacity the specified fraction
 * at a time.
 * <p>This gradual resize may give the opportunity for the Vm workload to return
 * to the normal state, without requiring further scaling.
 * However, if the workload doesn't return quickly
 * to the normal and expected state, that may cause longer SLA violation time.</p>
 *
 * <p><b>This is the default type of scaling in case one is not defined.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class ResourceScalingGradual extends ResourceScalingTypeAbstract {
    @Override
    public long getResourceAmountToScale() {
        return (long)(getVmScaling().getVmResourceToScale().getCapacity() * getVmScaling().getScalingFactor());
    }
}
