/**
 * Provides {@link org.cloudbus.cloudsim.cloudlets.Cloudlet} implementations,
 * that represent an application that will run inside a {@link org.cloudbus.cloudsim.vms.Vm}.
 * Each Cloudlet is abstractly defined in terms of its characteristics,
 * such as the number of Million Instructions (MI) to execute,
 * the number of required {@link org.cloudbus.cloudsim.resources.Pe}
 * and a {@link org.cloudbus.cloudsim.utilizationmodels.UtilizationModel} for CPU, RAM and bandwidth.
 *
 * <p>Each utilization model defines how a given resource will be used by the Cloudlet along the time.
 * Some basic utilization models implementations are provided, such as the
 * {@link org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull},
 * which indicates that a given available resource will be used 100% all the time.</p>
 *
 * <p>Specific Cloudlet implementations can be, for instance, network-aware, enabling the simulation
 * of network communication. For more information
 * see {@link org.cloudbus.cloudsim.datacenters} package documentation.</p>
 *
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudbus.cloudsim.cloudlets;
