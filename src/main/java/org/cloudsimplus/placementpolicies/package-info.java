/// Provides classes that implement policies for a [org.cloudsimplus.datacenters.Datacenter]
/// to select a Host to **place** or **migrate** a VM, based on some criteria defined by each class.
/// Different policies can follow approaches such as best-fit, first-fit and so on.
///
/// **Each Datacenter must have its own instance of a [org.cloudsimplus.placementpolicies.VmPlacementPolicy].**
/// The most basic implementation is provided by the class [org.cloudsimplus.placementpolicies.VmPlacementPolicySimple].
/// Only classes that implement the [org.cloudsimplus.placementpolicies.migration.VmPlacementPolicyMigration]
/// interface are able to perform VM migration.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.placementpolicies;
