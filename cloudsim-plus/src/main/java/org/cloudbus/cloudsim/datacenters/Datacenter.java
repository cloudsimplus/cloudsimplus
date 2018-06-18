/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import java.util.List;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.models.PowerAware;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface to be implemented by each class that provides Datacenter
 * features. The interface implements the Null Object Design Pattern in order to
 * start avoiding {@link NullPointerException} when using the
 * {@link Datacenter#NULL} object instead of attributing {@code null} to
 * {@link Datacenter} variables.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Datacenter extends SimEntity, PowerAware {
    /**
     * A property that implements the Null Object Design Pattern for
     * {@link Datacenter} objects.
     */
    Datacenter NULL = new DatacenterNull();
    /**
     * The default percentage of bandwidth allocated for VM migration, is
     * a value is not set.
     * @see #setBandwidthPercentForMigration(double)
     */
    double DEF_BANDWIDTH_PERCENT_FOR_MIGRATION = 0.5;

    /**
     * Gets an <b>unmodifiable</b> host list.
     *
     * @param <T> The generic type
     * @return the host list
     */
    <T extends Host> List<T> getHostList();

    Host getHost(int index);

    /**
     * Physically expands the Datacenter by adding a List of new Hosts (physical machines) to it.
     * Hosts can be added before or after the simulation has started.
     * If a Host is added during simulation execution,
     * in case VMs are added dynamically too, they
     * may be allocated to this new Host,
     * depending on the {@link VmAllocationPolicy}.
     *
     * <p>If an ID is not assigned to a Host, the method assigns one.</p>
     *
     * @param hostList the List of new hosts to be added
     * @return
     * @see #getVmAllocationPolicy()
     */
    <T extends Host> Datacenter addHostList(List<T> hostList);

    /**
     * Physically expands the Datacenter by adding a new Host (physical machine) to it.
     * Hosts can be added before or after the simulation has started.
     * If a Host is added during simulation execution,
     * in case VMs are added dynamically too, they
     * may be allocated to this new Host,
     * depending on the {@link VmAllocationPolicy}.
     *
     * <p>If an ID is not assigned to the given Host,
     * the method assigns one.</p>
     *
     * @param host the new host to be added
     * @return
     * @see #getVmAllocationPolicy()
     */
    <T extends Host> Datacenter addHost(T host);

    /**
     * Gets the policy to be used by the Datacenter to allocate VMs into hosts.
     *
     * @return the VM allocation policy
     * @see VmAllocationPolicy
     */
    VmAllocationPolicy getVmAllocationPolicy();

    /**
     * Gets a <b>read-only</b> list all VMs from all Hosts of this Datacenter.
     *
     * @param <T> the class of VMs inside the list
     * @return the list all VMs from all Hosts
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets the scheduling interval to process each event received by the
     * Datacenter (in seconds). This value defines the interval in which
     * processing of Cloudlets will be updated. The interval doesn't affect the
     * processing of such cloudlets, it only defines in which interval the processing
     * will be updated. For instance, if it is set a interval of 10 seconds, the
     * processing of cloudlets will be updated at every 10 seconds. By this way,
     * trying to get the amount of instructions the cloudlet has executed after
     * 5 seconds, by means of {@link Cloudlet#getFinishedLengthSoFar(Datacenter)}, it
     * will not return an updated value. By this way, one should set the
     * scheduling interval to 5 to get an updated result. As longer is the
     * interval, faster will be the simulation execution.
     *
     * @return the scheduling interval (in seconds)
     */
    double getSchedulingInterval();

    /**
     * Sets the scheduling delay to process each event received by the
     * Datacenter (in seconds).
     *
     * @param schedulingInterval the new scheduling interval (in seconds)
     * @return
     * @see #getSchedulingInterval()
     */
    Datacenter setSchedulingInterval(double schedulingInterval);

    /**
     * Gets the Datacenter characteristics.
     *
     * @return the Datacenter characteristics
     */
    DatacenterCharacteristics getCharacteristics();

    /**
     * Gets a storage of the Datacenter.
     *
     * @return the storage
     */
    DatacenterStorage getDatacenterStorage();

    /**
     * Sets storage of the Datacenter.
     *
     * @param datacenterStorage the new storage
     * @return
     */
    void setDatacenterStorage(DatacenterStorage datacenterStorage);

    /**
     * Gets the percentage of the bandwidth allocated to a Host to
     * migrate VMs. It's a value between [0 and 1] (where 1 is 100%).
     * The default value is 0.5, meaning only 50% of the bandwidth
     * will be allowed for migration, while the remaining
     * will be used for VM services.
     *
     * @return
     * @see #DEF_BANDWIDTH_PERCENT_FOR_MIGRATION
     */
    double getBandwidthPercentForMigration();

    /**
     * Sets the percentage of the bandwidth allocated to a Host to
     * migrate VMs. It's a value between [0 and 1] (where 1 is 100%).
     * The default value is 0.5, meaning only 50% of the bandwidth
     * will be allowed for migration, while the remaining
     * will be used for VM services.
     *
     * @param bandwidthPercentForMigration the bandwidth migration percentage to set
     */
    void setBandwidthPercentForMigration(double bandwidthPercentForMigration);

    /**
     * Gets an <b>estimation</b> of Datacenter power consumption in Watt-Second (Ws).
     * <p><b>To get actual power consumption, it's required to enable
     * {@link Host#getStateHistory() Host's StateHistory}
     * by calling {@link Host#enableStateHistory()}
     * and use each Host {@link PowerModel} to compute power usage
     * based on the CPU utilization got form the StateHistory.</b>
     * </p>
     *
     * @return th <b>estimated</b> power consumption in Watt-Second (Ws)
     */
    @Override
    double getPower();

}
