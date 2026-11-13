/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.CustomerEntity;
import org.cloudsimplus.core.Simulation;

import static org.cloudsimplus.util.Conversion.HUNDRED_PERCENT;

/// An **immutable** [UtilizationModel] that makes a [Cloudlet] always utilize
/// a given allocated resource from its Vm at 100%, all the time.
/// The methods [#getUtilization(double)] and [#getUtilization()]
/// always return 1 any time they are called.
///
/// Since its utilization doesn't depend on any attribute, its setters do nothing,
/// and a single instance can be shared by all Cloudlets.
/// Use [UtilizationModel#FULL] instead of creating new instances.
///
/// This model may not be realistic for most of the scenarios.
/// This way, other implementations such as [UtilizationModelDynamic]
/// might be more suitable.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 2.0
/// @see UtilizationModelDynamic
/// @see UtilizationModelStochastic
/// @see UtilizationModelPlanetLab
public final class UtilizationModelFull implements UtilizationModel {
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public Unit getUnit() { return Unit.PERCENTAGE; }
    @Override public UtilizationModel setSimulation(final Simulation simulation) { return this; }
    @Override public double getUtilization(final double time) { return HUNDRED_PERCENT; }
    @Override public double getUtilization() { return HUNDRED_PERCENT; }
    @Override public boolean isOverCapacityRequestAllowed() { return false; }
    @Override public UtilizationModel setOverCapacityRequestAllowed(final boolean allow) { return this; }
    @Override public boolean isStateful() { return false; }
    @Override public void validateOwner(final CustomerEntity owner) {/**/}
    @Override public UtilizationModel registerOwner(final CustomerEntity owner) { return this; }
}
