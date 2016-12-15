/**
 * Provides processor schedulers implementations to enable multiple
 * processes to run on some CPU cores ({@link org.cloudbus.cloudsim.resources.Pe}).
 * Consider a process being a {@link org.cloudbus.cloudsim.vms.Vm} running inside a
 * {@link org.cloudbus.cloudsim.hosts.Host} (PM) or a
 * {@link org.cloudbus.cloudsim.cloudlets.Cloudlet} running inside a VM.
 *
 * <p>A scheduler is used manage the execution of VMs inside a PM
 * and Cloudlets inside a VM. Since a PM can host multiple VMs and a VM can host multiple Cloudlets,
 * such schedulers defined the policy used to allow sharing CPU time among such processes.
 * </p>
 *
 * <p>For Cloudlet and VM schedulers there are different implementations such
 * as time- and space-shared schedulers.
 * A time-shared is a multitasking scheduler that share CPU time among processes
 * if there are more processes than CPU cores.<br>
 * A space-shared is a non-multitasking scheduler that <b>DOES NOT</b> share CPU time
 * among processes. Thus, if there are more processes than CPU cores, some processes
 * will have to wait until other ones finish to start executing.</p>
 *
 * <p>The choice of a given scheduler usually depends on desired goals
 * and different implementations may provide more or less accuracy
 * in how the processes are scheduled, what usually impacts the simulation overhead.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.schedulers;
