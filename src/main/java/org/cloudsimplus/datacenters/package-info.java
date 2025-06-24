/**
 * Provides {@link org.cloudsimplus.datacenters.Datacenter} implementations
 * that represent a physical Cloud Datacenter and contains a set of
 * {@link org.cloudsimplus.hosts.Host}. They together form the basic cloud infrastructure.
 *
 * <p>Each Datacenter has attributes that define its characteristics, such as the costs
 * to use different physical resources from Hosts.
 * These attributes are defined by a {@link org.cloudsimplus.datacenters.DatacenterCharacteristics} object.
 * </p>
 *
 * <p>For each created Datacenter, a {@link org.cloudsimplus.allocationpolicies.VmAllocationPolicy}
 * instance must be defined. This object decides which PM will host each {@link org.cloudsimplus.vms.Vm}.
 * The most basic VmAllocationPolicy is the
 * {@link org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple}.
 * </p>
 *
 * <p><b>All datacenter implementations are natively power-ware.</b>
 * Specific implementations can also be network-aware, enabling the simulation
 * of network communication. There are specific network-aware
 * versions for Hosts and VMs, and a single kind of such objects must be used for a
 * simulation. For instance a network-aware simulation must use
 * {@link org.cloudsimplus.datacenters.network.NetworkDatacenter},
 * {@link org.cloudsimplus.hosts.network.NetworkHost},
 * {@link org.cloudsimplus.vms.network.NetworkVm} and
 * {@link org.cloudsimplus.cloudlets.network.NetworkCloudlet}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.datacenters;
