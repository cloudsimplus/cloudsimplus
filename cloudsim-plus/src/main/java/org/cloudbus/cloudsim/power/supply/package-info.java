/**
 * Provides classes and interfaces enabling a {@link org.cloudbus.cloudsim.hosts.Host}
 * to be power-aware.
 * Every host nas a {@link org.cloudbus.cloudsim.power.supply.PowerSupply} instance
 * that might define power consumption following a {@link org.cloudbus.cloudsim.power.models.PowerModel}.
 * However, a Host just provides power usage data if a {@link org.cloudbus.cloudsim.power.models.PowerModel} is set.
 *
 * <p>CloudSim Plus natively supports power-aware simulations
 * using regular objects such as {@link org.cloudbus.cloudsim.datacenters.DatacenterSimple},
 * {@link org.cloudbus.cloudsim.hosts.HostSimple} and
 * {@link org.cloudbus.cloudsim.vms.VmSimple}.
 * You just need to ensure a {@link org.cloudbus.cloudsim.power.models.PowerModel}
 * is set for each Host's {@link org.cloudbus.cloudsim.power.supply.PowerSupply}.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @see org.cloudbus.cloudsim.power.models
 */
package org.cloudbus.cloudsim.power.supply;
