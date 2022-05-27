/**
 * Provides {@link org.cloudbus.cloudsim.brokers.DatacenterBroker} classes that act on behalf of a cloud customer,
 * attending his/her requests for creation and destruction of
 * {@link org.cloudbus.cloudsim.cloudlets.Cloudlet Cloudlets} and
 * {@link org.cloudbus.cloudsim.vms.Vm VMs}, assigning such Cloudlets to specific VMs.
 * These brokers can implement decision making algorithms to prioritize submission of Cloudlets
 * to the cloud, to define how a VM is selected to run a given Cloudlets, etc.
 *
 * <p>The most basic implementation is the {@link org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple}
 * that uses a Round-robin algorithm to select a VM from a list to place a submitted Cloudlet,
 * which is called a Cloudlet to VM mapping.
 * Other class such as the {@link org.cloudbus.cloudsim.brokers.DatacenterBrokerHeuristic}
 * allows setting a {@link org.cloudsimplus.heuristics.Heuristic} to find an sub-optimal
 * mapping.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.brokers;
