/**
 * Provides {@link org.cloudsimplus.hosts.Host} implementations
 * that represent a Physical Machine (PM) is used to run {@link org.cloudsimplus.vms.Vm}
 * from different cloud customers
 * (represented by a {@link org.cloudsimplus.brokers.DatacenterBroker}).
 *
 * <p>As each Host can run several VMs, the scheduling of such VMs on the Host's CPU
 * cores ({@link org.cloudsimplus.resources.Pe}) is defined by
 * a {@link org.cloudsimplus.schedulers.vm.VmScheduler}.
 * </p>
 *
 * <p>The most basic Host is the {@link org.cloudsimplus.hosts.HostSimple}.</p>
 *
 * <p>Specific Host implementations can be power- or network-aware, enabling the simulation
 * of power consumption and network communication. For more information
 * see {@link org.cloudsimplus.datacenters} package documentation.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.hosts;
