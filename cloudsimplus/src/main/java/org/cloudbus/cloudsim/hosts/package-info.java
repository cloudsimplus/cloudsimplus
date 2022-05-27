/**
 * Provides {@link org.cloudbus.cloudsim.hosts.Host} implementations
 * that represent a Physical Machine (PM) is used to run {@link org.cloudbus.cloudsim.vms.Vm}
 * from different cloud customers
 * (represented by a {@link org.cloudbus.cloudsim.brokers.DatacenterBroker}).
 *
 * <p>As each Host can run several VMs, the scheduling of such VMs on the Host's CPU
 * cores ({@link org.cloudbus.cloudsim.resources.Pe}) is defined by
 * a {@link org.cloudbus.cloudsim.schedulers.vm.VmScheduler}.
 * </p>
 *
 * <p>The most basic Host is the {@link org.cloudbus.cloudsim.hosts.HostSimple}.</p>
 *
 * <p>Specific Host implementations can be power- or network-aware, enabling the simulation
 * of power consumption and network communication. For more information
 * see {@link org.cloudbus.cloudsim.datacenters} package documentation.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.hosts;
