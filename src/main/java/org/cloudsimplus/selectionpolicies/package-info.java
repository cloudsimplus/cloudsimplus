/// Provides [org.cloudsimplus.selectionpolicies.VmSelectionPolicy]
/// implementations that define policies to be used by a [org.cloudsimplus.hosts.Host]
/// to select a [org.cloudsimplus.vms.Vm] to migrate.
///
/// The order in which VMs are migrated may impact positive or negatively
/// some SLA metric. For instance, migrating VMs that are requiring more bandwidth
/// may reduce network congestion after such VMs are migrated.
/// That will make more bandwidth available, which will reduce the migration
/// time for subsequent migrating VMs.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.selectionpolicies;
