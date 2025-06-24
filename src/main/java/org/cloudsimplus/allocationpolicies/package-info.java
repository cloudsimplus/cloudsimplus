/**
 * Provides classes that implement policies for a {@link org.cloudsimplus.datacenters.Datacenter}
 * to select a Host to <b>place</b> or <b>migrate</b> a VM, based on some criteria defined by each class.
 * Different policies can follow approaches such as best-fit, first-fit and so on.
 *
 * <p>
 *     <b>Each Datacenter must have its own instance of a {@link org.cloudsimplus.allocationpolicies.VmAllocationPolicy}.</b>
 *     The most basic implementation is provided by the class {@link org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple}.
 *     Only classes that implement the {@link org.cloudsimplus.allocationpolicies.migration.VmAllocationPolicyMigration}
 *     interface are able to perform VM migration.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.allocationpolicies;
