/// Provides classes to enable  [horizontal and vertical scaling](https://en.wikipedia.org/wiki/Scalability)
/// of VMs in order to, respectively, adapt resource requirements to current workload
/// and to balance load across different VMs.
///
/// These scaling mechanisms require a [java.util.function.Predicate] that
/// define the condition to fire the scaling.
/// The [org.cloudsimplus.brokers.DatacenterBroker], which the VM
/// belongs to, is accountable to evaluate the predicate and then
/// request the scaling mechanism to act.
///
/// @author Manoel Campos da Silva Filho
package org.cloudsimplus.autoscaling;
