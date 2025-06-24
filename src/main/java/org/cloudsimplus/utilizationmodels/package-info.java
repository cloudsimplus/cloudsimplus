/// Provides classes that model utilization of resources such as
/// [org.cloudsimplus.resources.Pe] (CPU), [org.cloudsimplus.resources.Ram] and
/// [org.cloudsimplus.resources.Bandwidth],
/// defining how a given resource is used by a [org.cloudsimplus.cloudlets.Cloudlet]
/// along the simulation time.
///
/// The most basic utilization model that can be used for any of the mentioned resources
/// is the [org.cloudsimplus.utilizationmodels.UtilizationModelFull], which may not be realistic
/// for most of the scenarios.
/// This way, other implementations such as [org.cloudsimplus.utilizationmodels.UtilizationModelDynamic]
/// might be more suitable.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.utilizationmodels;
