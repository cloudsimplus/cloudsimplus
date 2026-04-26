/**
 * Provides classes to inject random faults during simulation runtime.
 *
 * <p>Two granularities are supported:</p>
 * <ul>
 *     <li>{@link org.cloudsimplus.faultinjection.HostFaultInjection} — hardware-level faults
 *         that fail Processing Elements ({@link org.cloudsimplus.resources.Pe}) of
 *         {@link org.cloudsimplus.hosts.Host}s.</li>
 *     <li>{@link org.cloudsimplus.faultinjection.VmFaultInjection} — software-level faults
 *         that crash an individual {@link org.cloudsimplus.vms.Vm} or fail a subset of the
 *         {@link org.cloudsimplus.cloudlets.Cloudlet}s running inside it, independently of
 *         the underlying Host hardware.</li>
 * </ul>
 *
 * @author raysaoliveira
 * @see org.cloudsimplus.faultinjection.HostFaultInjection
 * @see org.cloudsimplus.faultinjection.VmFaultInjection
 */
package org.cloudsimplus.faultinjection;
