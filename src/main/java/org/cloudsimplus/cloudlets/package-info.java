/**
 * Provides {@link org.cloudsimplus.cloudlets.Cloudlet} implementations,
 * that represent an application which will run inside a {@link org.cloudsimplus.vms.Vm}.
 * Each Cloudlet is abstractly defined in terms of its characteristics,
 * such as the number of Million Instructions (MI) to execute (length),
 * the number of required {@link org.cloudsimplus.resources.Pe}s
 * and a {@link org.cloudsimplus.utilizationmodels.UtilizationModel} for CPU, RAM and bandwidth.
 *
 * <p>Each utilization model defines how the Cloudlet will use a given resource along the time.
 * Some basic utilization models implementations are provided, such as the
 * {@link org.cloudsimplus.utilizationmodels.UtilizationModelFull},
 * which indicates that a given available resource will be used 100% all the time.</p>
 *
 * <p>Specific Cloudlet implementations can be, for instance, network-aware, enabling the simulation
 * of network communication. For more information
 * see {@link org.cloudsimplus.datacenters} package documentation.</p>
 *
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.cloudlets;
