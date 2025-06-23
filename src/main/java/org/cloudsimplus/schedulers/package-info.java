/// Provides processor schedulers implementations to enable multiple
/// processes to run on some CPU cores ([org.cloudsimplus.resources.Pe]).
/// Consider a process being a [org.cloudsimplus.vms.Vm] running inside a
/// [org.cloudsimplus.hosts.Host] (PM) or a
/// [org.cloudsimplus.cloudlets.Cloudlet] running inside a VM.
///
/// A scheduler is used to manage the execution of VMs inside a PM
/// and Cloudlets inside a VM. Since a PM can host multiple VMs and a VM can run multiple Cloudlets,
/// such schedulers defined the policy used to allow sharing CPU time between such processes.
///
/// For Cloudlet and VM schedulers there are different implementations such as time- and space-shared ones.
/// A time-shared is a multitasking scheduler that shares CPU time among processes,
/// if there are more processes than CPU cores.
/// A space-shared is a non-multitasking scheduler that **DOES NOT** share CPU time
/// among processes. Thus, if there are more processes than CPU cores, some processes
/// will have to wait until other ones finish.
///
/// The choice of a given scheduler usually depends on desired goals,
/// and different implementations may provide more or less accuracy
/// in how the processes are scheduled. That usually impacts the simulation overhead.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.schedulers;
