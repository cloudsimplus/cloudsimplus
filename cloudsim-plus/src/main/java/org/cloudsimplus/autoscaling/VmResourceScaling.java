package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.resources.Resource;


/**
 * Performs the scaling of a given {@link Resource}.
 */
interface ResourceScaling {
    /**
     * Scales a resource attached to a {@link Machine}.
     * @param resource the resource to be scaled.
     * @return true if the resource was scaled, false otherwise
     */
    boolean scale(Resource resource);
}

final class ResourceScalingNull implements ResourceScaling {
    @Override
    public boolean scale(Resource resource) {
        return false;
    }
}
