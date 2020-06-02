/**
 * Provides classes that implement policies for a {@link org.cloudbus.cloudsim.datacenters.Datacenter}
 * to select a Host to <b>place</b> or <b>migrate</b> a VM, based on some criteria defined by each class.
 * Different policies can follow approaches such as best-fit, worst-fit and so on.
 *
 * <p>
 *     <b>Each Datacenter must have its own instance of a {@link org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy}.</b>
 *     The most basic implementation is provided by the class {@link org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple}.
 *     Only classes that implement the {@link org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration}
 *     interface are able to perform VM migration.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.allocationpolicies;
