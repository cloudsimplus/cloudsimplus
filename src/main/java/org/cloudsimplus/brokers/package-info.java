/// Provides [org.cloudsimplus.brokers.DatacenterBroker] classes that act on behalf of a cloud customer,
/// attending his/her requests for creation and destruction of
/// [Cloudlets][org.cloudsimplus.cloudlets.Cloudlet] and
/// [VMs][org.cloudsimplus.vms.Vm], assigning such Cloudlets to specific VMs.
/// These brokers can implement decision-making algorithms to prioritize submission of Cloudlets
/// to the cloud, to define how a VM is selected to run given Cloudlets, etc.
///
/// The most basic implementation is the [org.cloudsimplus.brokers.DatacenterBrokerSimple]
/// that uses a Round-robin algorithm to select a VM from a list to place a submitted Cloudlet,
/// which is called a Cloudlet to VM mapping.
/// Other class such as the [org.cloudsimplus.brokers.DatacenterBrokerHeuristic]
/// allows setting a [org.cloudsimplus.heuristics.Heuristic] to find a suboptimal
/// mapping.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.brokers;
