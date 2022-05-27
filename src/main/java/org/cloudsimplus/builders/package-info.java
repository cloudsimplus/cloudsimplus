/**
 * Provides {@link org.cloudsimplus.builders.Builder} classes that
 * implement the <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder Design Pattern</a>
 * to allow instantiating multiple simulation objects more easily.
 *
 * <p>Since that creating and setting up some simulation objects such as a
 * {@link org.cloudbus.cloudsim.datacenters.Datacenter} requires a considerable amount
 * of code, that usually becomes duplicated along different simulations,
 * the builder classes  work as object factories that make it easier to create multiple
 * simulation objects with the same configuration.</p>
 *
 * <p>The builders allow to set the parameters for creating a given object
 * such as a Host, and then, after all parameters are set,
 * a single class can create as many objects with the same configuration as desired.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.builders;
