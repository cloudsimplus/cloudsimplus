/**
 * Provides {@link org.cloudsimplus.hosts.Host} implementations
 * that represent a Physical Machine (PM) used to run {@link org.cloudsimplus.vms.Vm}s
 * from different cloud customers
 * (represented by a {@link org.cloudsimplus.brokers.DatacenterBroker}).
 *
 * <p>As each Host can run several VMs, the scheduling of such VMs on the Host's CPU
 * cores ({@link org.cloudsimplus.resources.Pe}s) is defined by
 * a {@link org.cloudsimplus.schedulers.vm.VmScheduler}.
 * </p>
 *
 * <p>The most basic Host is the {@link org.cloudsimplus.hosts.HostSimple}.</p>
 *
 * <p>All Host implementations are power-aware, but there is a {@link org.cloudsimplus.hosts.network.NetworkHost}
 * that is also network-aware. Using such an implementation enables the simulation
 * of joint power consumption and network communication. For more details
 * see the {@link org.cloudsimplus.datacenters} and {@link org.cloudsimplus.datacenters.network} packages documentation.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.hosts;
