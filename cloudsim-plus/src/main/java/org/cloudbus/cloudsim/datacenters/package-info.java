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
 * <p>Specific Datacenter implementations can be power- or network-aware, enabling the simulation
 * of power consumption and network communication. There are specifc power- and networtk-aware
 * versions for Hosts and VMs and a single kindle of such objects must be used for a
 * simulation. For instance a power-aware simulation must use
 * {@link org.cloudbus.cloudsim.datacenters.power.PowerDatacenter},
 * {@link org.cloudbus.cloudsim.hosts.power.PowerHost}
 * and {@link org.cloudbus.cloudsim.vms.power.PowerVm}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.datacenters;
