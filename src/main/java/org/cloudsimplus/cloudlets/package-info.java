/// Provides [org.cloudsimplus.cloudlets.Cloudlet] implementations,
/// that represent an application which will run inside a [org.cloudsimplus.vms.Vm].
/// Each Cloudlet is abstractly defined in terms of its characteristics,
/// such as the number of Million Instructions (MI) to execute (length),
/// the number of required CPU cores
/// and a [org.cloudsimplus.utilizationmodels.UtilizationModel] for CPU, RAM and bandwidth.
///
/// Each utilization model defines how the Cloudlet will use a given resource along the time.
/// Some basic utilization models implementations are provided, such as the
/// [org.cloudsimplus.utilizationmodels.UtilizationModelFull],
/// which indicates that a given available resource will be used 100% all the time.
///
/// Specific Cloudlet implementations can be, for instance, network-aware, enabling the simulation
/// of network communication. For more information
/// see [org.cloudsimplus.datacenters] package documentation.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.cloudlets;
