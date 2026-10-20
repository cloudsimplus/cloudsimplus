/// Provides [org.cloudsimplus.hosts.Host] implementations
/// that represent a Physical Machine (PM) used to run [org.cloudsimplus.vms.Vm]s
/// from different cloud customers
/// (represented by a [org.cloudsimplus.brokers.DatacenterBroker]).
///
/// As each Host can run several VMs, the scheduling of such VMs on the Host's CPU
/// cores ([org.cloudsimplus.resources.Pe]s) is defined by
/// a [org.cloudsimplus.schedulers.vm.VmScheduler].
///
/// The most basic Host is the [org.cloudsimplus.hosts.HostSimple].
///
/// All Host implementations are power-aware, but there is a [org.cloudsimplus.hosts.network.NetworkHost]
/// that is also network-aware. Using such an implementation enables the simulation
/// of joint power consumption and network communication. For more details
/// see the [org.cloudsimplus.datacenters] and [org.cloudsimplus.datacenters.network] packages documentation.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.hosts;
