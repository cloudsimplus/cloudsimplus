/// Provides [org.cloudsimplus.builders.Builder] classes that
/// implement the [Builder Design Pattern](https://en.wikipedia.org/wiki/Builder_pattern)
/// to allow instantiating multiple simulation objects more easily.
///
/// Since that creating and setting up some simulation objects such as a
/// [org.cloudsimplus.datacenters.Datacenter] requires a considerable amount
/// of code, that usually becomes duplicated along different simulations,
/// the builder classes work as object factories that make it easier to create multiple
/// simulation objects with the same configuration.
///
/// The builders allow to set the parameters for creating a given object
/// such as a [org.cloudsimplus.hosts.Host], and then, after all parameters are set,
/// a single class can create as many objects with the same configuration as desired.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.builders;
