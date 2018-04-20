/**
 * Provides classes and interfaces enabling a {@link org.cloudbus.cloudsim.hosts.Host}
 * to compute power consumption using a {@link org.cloudbus.cloudsim.power.supply.PowerSupply}
 * that follows a {@link org.cloudbus.cloudsim.power.models.PowerModel}.
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
