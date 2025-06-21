/// Provides classes that represent different physical and logical
/// [org.cloudsimplus.resources.Resource] used by simulation
/// objects such as Hosts and VMs.
///
/// There are different interfaces that enable the existence of
/// resources with different features, such as:
///
/// - if the capacity of the resource can be changed after defined;
/// - if the resource can be managed (meaning that some amount of it can be allocated or freed in runtime);
/// - etc.
///
/// The most basic resources are [org.cloudsimplus.resources.HarddriveStorage],
/// [org.cloudsimplus.resources.Ram],
/// [org.cloudsimplus.resources.Bandwidth],
/// [org.cloudsimplus.resources.Pe]
/// and [org.cloudsimplus.resources.File].
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.resources;
