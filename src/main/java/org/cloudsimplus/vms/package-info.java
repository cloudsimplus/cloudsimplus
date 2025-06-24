/// Provides implementations of Virtual Machines ([org.cloudsimplus.vms.Vm])
/// which are a software package that emulate the architecture of a physical machine ([org.cloudsimplus.hosts.Host]).
/// Each VM is executed by a Host, and it is used to run applications ([org.cloudsimplus.cloudlets.Cloudlet]).
/// Both VMs and Cloudlets are owned by a specific cloud customer
/// (represented by a [org.cloudsimplus.brokers.DatacenterBroker]).
///
/// As each VM can run several Cloudlets, the scheduling of such Cloudlets on the VM's CPU
/// cores ([org.cloudsimplus.resources.Pe]) is defined by
/// a [org.cloudsimplus.schedulers.cloudlet.CloudletScheduler].
///
/// The most basic Vm implementation is the [org.cloudsimplus.vms.VmSimple].
///
/// Specific Vm implementations can be network-aware, enabling the simulation
/// of network communication. For more information,
/// see [org.cloudsimplus.datacenters] package documentation.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.vms;
