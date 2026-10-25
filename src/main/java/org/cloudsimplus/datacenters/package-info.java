/// Provides [org.cloudsimplus.datacenters.Datacenter] implementations
/// that represent a physical Cloud Datacenter and contains a set of
/// [org.cloudsimplus.hosts.Host]. They together form the basic cloud infrastructure.
///
/// Each Datacenter has attributes that define its characteristics, such as the costs
/// to use different physical resources from Hosts.
/// These attributes are defined by a [org.cloudsimplus.datacenters.DatacenterCharacteristics] object.
///
/// For each created Datacenter, a [org.cloudsimplus.placementpolicies.VmPlacementPolicy]
/// instance must be defined. This object decides which PM will host each [org.cloudsimplus.vms.Vm].
/// The most basic VmPlacementPolicy is the
/// [org.cloudsimplus.placementpolicies.VmPlacementPolicySimple].
///
/// **All datacenter implementations are natively power-ware.**
/// Specific implementations can also be network-aware, enabling the simulation
/// of network communication. There are specific network-aware
/// versions for Hosts and VMs, and a single kind of such objects must be used for a
/// simulation. For instance a network-aware simulation must use
/// [org.cloudsimplus.datacenters.network.NetworkDatacenter],
/// [org.cloudsimplus.hosts.network.NetworkHost],
/// [org.cloudsimplus.vms.network.NetworkVm] and
/// [org.cloudsimplus.cloudlets.network.NetworkCloudlet].
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.datacenters;
