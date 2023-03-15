/**
 * Provides {@link org.cloudsimplus.selectionpolicies.VmSelectionPolicy}
 * implementations that define policies to be used by a {@link org.cloudsimplus.hosts.Host}
 * to select a {@link org.cloudsimplus.vms.Vm} to migrate from a list of VMs.
 *
 * <p>The order in which VMs are migrated may impact positive or negatively
 * some SLA metric. For instance, migrating VMs that are requiring more bandwidth
 * may reduce network congestion after such VMs are migrated and
 * will make more bandwidth available that will reduce the migration
 * time for subsequent migrating VMs.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.selectionpolicies;
