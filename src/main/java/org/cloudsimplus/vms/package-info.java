/**
 *
 * Provides implementations of Virtual Machines ({@link org.cloudsimplus.vms.Vm})
 * which are a software package that emulate the architecture of a physical machine.
 * Each VM is executed by a Host and it is used to run applications ({@link org.cloudsimplus.cloudlets.Cloudlet}).
 * Both VMs and Cloudlets are owned by a specific cloud customer
 * (represented by a {@link org.cloudsimplus.brokers.DatacenterBroker}).
 *
 * <p>As each VM can run several Cloudlets, the scheduling of such Cloudlets on the VM's CPU
 * cores ({@link org.cloudsimplus.resources.Pe}) is defined by
 * a {@link org.cloudsimplus.schedulers.cloudlet.CloudletScheduler}.
 * </p>
 *
 * <p>The most basic Vm implementation is the {@link org.cloudsimplus.vms.VmSimple}.</p>
 *
 * <p>Specific Vm implementations can be power- or network-aware, enabling the simulation
 * of power consumption and network communication. For more information
 * see {@link org.cloudsimplus.datacenters} package documentation.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.vms;
