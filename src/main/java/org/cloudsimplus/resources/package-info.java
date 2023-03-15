/**
 * Provides classes that represent different physical and logical
 * {@link org.cloudsimplus.resources.Resource} used by simulation
 * objects such as Hosts and VMs.
 *
 * <p>There are different interfaces that enable the existence of
 * resources with different features such as if the capacity
 * of the resource can be changed after defined,
 * if the resource can be managed (meaning that
 * some amount of it can be allocated or freed
 * in runtime), etc.</p>
 *
 * <p>The most basic resources are {@link org.cloudsimplus.resources.HarddriveStorage},
 * {@link org.cloudsimplus.resources.Ram},
 * {@link org.cloudsimplus.resources.Bandwidth},
 * {@link org.cloudsimplus.resources.Pe}
 * and {@link org.cloudsimplus.resources.File}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.resources;
