/**
 * Provides classes that represent different physical and logical
 * {@link org.cloudbus.cloudsim.resources.Resource} used by simulation
 * objects such as Hosts and VMs.
 *
 * <p>There are different interfaces that enable the existence of
 * resources with different features such as if the capacity
 * of the resource can be changed after defined,
 * if the resource can be managed (meaning that
 * some amount of it can be allocated or freed
 * in runtime), etc.</p>
 *
 * <p>The most basic resources are {@link org.cloudbus.cloudsim.resources.HarddriveStorage},
 * {@link org.cloudbus.cloudsim.resources.Ram},
 * {@link org.cloudbus.cloudsim.resources.Bandwidth},
 * {@link org.cloudbus.cloudsim.resources.Pe}
 * and {@link org.cloudbus.cloudsim.resources.File}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.resources;
