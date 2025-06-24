/**
 * Provides classes to enable  <a href="https://en.wikipedia.org/wiki/Scalability">horizontal and vertical scaling</a>
 * of VMs in order to, respectively, adapt resource requirements to current workload
 * and to balance load across different VMs.
 *
 * <p>These scaling mechanisms require a {@link java.util.function.Predicate} that
 * define the condition to fire the scaling.
 * The {@link org.cloudsimplus.brokers.DatacenterBroker}, which the VM
 * belongs to, is accountable to evaluate the predicate and then
 * request the scaling mechanism to act.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.autoscaling;
