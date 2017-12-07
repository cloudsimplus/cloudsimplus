/**
 * An experiment to assess whether SLA contracts for customers are being met or not
 * in case of Host failures. The experiment uses a {@link org.cloudsimplus.faultinjection.HostFaultInjection}
 * object to inject random Host failures. A {@link org.cloudsimplus.faultinjection.VmCloner} is used
 * to recovery failures.
 *
 * <p>The {@link org.cloudsimplus.hostfaultinjection.HostFaultInjectionRunner} is the main class that executes
 * the {@link org.cloudsimplus.hostfaultinjection.HostFaultInjectionExperiment} multiple
 * times, using different seeds, to assess meeting of SLA Contracts.</p>
 *
 * <p>The HostFaultInjectionExperiment uses the list of SLA Contracts defined
 * in {@link org.cloudsimplus.hostfaultinjection.HostFaultInjectionExperiment#SLA_CONTRACTS_LIST}.
 * See the comment into this file for more details.</p>
 *
 * @author raysaoliveira
 */
package org.cloudsimplus.hostfaultinjection;
