/**
 * Provides {@link org.cloudbus.cloudsim.datacenters.Datacenter} implementations,
 * that represents a physical Cloud Datacenter and contains a set of
 * {@link org.cloudbus.cloudsim.hosts.Host} that together provide the basic cloud infrastructure.
 *
 * <p>Each Datacenter has attributes that define its characteristics, such as the costs
 * to use different physical resources from Hosts.
 * These attributes are defined by a {@link org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics} object.
 * </p>
 *
 * <p>For each created Datacenter, a {@link org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy}
 * instance must be defined. This object decides which PM will host each {@link org.cloudbus.cloudsim.vms.Vm}.
 * The most basic VmAllocationPolicy is the
 * {@link org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple}.
 * </p>
 *
 * <p><b>All datacenter implementations are natively power-ware.</b>
 * Specific implementations can also be network-aware, enabling the simulation
 * of network communication. There are specific network-aware
 * versions for Hosts and VMs and a single kindle of such objects must be used for a
 * simulation. For instance a network-aware simulation must use
 * {@link org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter},
 * {@link org.cloudbus.cloudsim.hosts.network.NetworkHost},
 * {@link org.cloudbus.cloudsim.vms.network.NetworkVm} and
 * {@link org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.datacenters;
