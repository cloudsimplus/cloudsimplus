/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.utilizationmodels;

import static org.cloudsimplus.util.Conversion.HUNDRED_PERCENT;

/// A [UtilizationModel] that makes a Cloudlet always utilize
/// a given allocated resource from its Vm at 100%, all the time.
/// The methods [#getUtilization(double)] and [#getUtilization()]
/// always return 1 any time they are called.
///
/// This model may not be realistic for most of the scenarios.
/// This way, other implementations such as [UtilizationModelDynamic]
/// might be more suitable.
///
/// @author Anton Beloglazov
/// @since CloudSim Toolkit 2.0
/// @see UtilizationModelDynamic
/// @see UtilizationModelStochastic
/// @see UtilizationModelPlanetLab
public class UtilizationModelFull extends UtilizationModelAbstract {
    /**
     * Gets the utilization percentage (in scale from [0 to 1]) of resource at a given simulation time.
     *
     * @param time the time to get the resource usage.
     * @return always 1 (100% of utilization), regardless the time given (which is completely ignored).
     */
    @Override
    protected final double getUtilizationInternal(final double time) {
        return HUNDRED_PERCENT;
    }
}
