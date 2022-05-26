/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.migration.VmAllocationPolicyMigration;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.Sizeable;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerAware;
import org.cloudbus.cloudsim.power.models.PowerModelDatacenter;
import org.cloudbus.cloudsim.resources.DatacenterStorage;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

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
public interface Datacenter extends SimEntity, PowerAware<PowerModelDatacenter>, TimeZoned, Sizeable {
    Logger LOGGER = LoggerFactory.getLogger(Datacenter.class.getSimpleName());

    /**
     * A property that implements the Null Object Design Pattern for
     * {@link Datacenter} objects.
     */
    Datacenter NULL = new DatacenterNull();

    /**
     * The default percentage ([0..1]) of bandwidth allocated for VM migration,
     * if a value is not set.
     * @see #setBandwidthPercentForMigration(double)
     */
    double DEF_BW_PERCENT_FOR_MIGRATION = 0.5;

    /**
     * Sends an event to request the migration of a {@link Vm} to a given target {@link Host}
     * on this Datacenter.
     * If you want VM migrations to be performed automatically,
     * use a {@link VmAllocationPolicyMigration}.
     *
     * @param sourceVm the VM to be migrated
     * @param targetHost the target Host to migrate the VM to
     * @see #requestVmMigration(Vm)
     * @see #getVmAllocationPolicy()
     */
    void requestVmMigration(Vm sourceVm, Host targetHost);

    /**
     * Sends an event to request the migration of a {@link Vm} to some suitable {@link Host}
     * on this Datacenter. A suitable Host will try to be found when the migration request message
     * is processed by the Datacenter.
     * If you want VM migrations to be performed automatically,
     * use a {@link VmAllocationPolicyMigration}.
     *
     * @param sourceVm the VM to be migrated
     * @see #requestVmMigration(Vm, Host)
     * @see #getVmAllocationPolicy()
     */
    void requestVmMigration(Vm sourceVm);

    /**
     * Gets an <b>unmodifiable</b> host list.
     *
     * @param <T> The generic type
     * @return the host list
     */
    <T extends Host> List<T> getHostList();

    /**
     * Gets a {@link Stream} containing the active Hosts inside the Datacenter.
     *
     * @return the active host Stream
     */
    Stream<? extends Host> getActiveHostStream();

    /**
     * Gets a Host in a given position inside the Host List.
     * @param index the position of the List to get the Host
     * @return
     */
    Host getHost(int index);

    /**
     * Gets the current number of Hosts that are powered on inside the Datacenter.
     * @return
     * @see Host#isActive()
     */
    long getActiveHostsNumber();

    /**
     * Gets the total number of existing Hosts in this Datacenter,
     * which indicates the Datacenter's size.
     * @return
     */
    long size();

    /**
     * Gets a Host from its id.
     * @param id the ID of the Host to get from the List.
     * @return the Host if found or {@link Host#NULL} otherwise
     */
    Host getHostById(long id);

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
     * Removes a Host from its Datacenter.
     *
     * @param host the new host to be removed from its assigned Datacenter
     * @return
     */
    <T extends Host> Datacenter removeHost(T host);

    /**
     * Gets the policy to be used by the Datacenter to allocate VMs into hosts.
     *
     * @return the VM allocation policy
     * @see VmAllocationPolicy
     */
    VmAllocationPolicy getVmAllocationPolicy();

    /**
     * Gets the scheduling interval to process each event received by the
     * Datacenter (in seconds). This value defines the interval in which
     * processing of Cloudlets will be updated. The interval doesn't affect the
     * processing of such cloudlets, it only defines in which interval the processing
     * will be updated. For instance, if it is set an interval of 10 seconds, the
     * processing of cloudlets will be updated at every 10 seconds.
     *
     * <p>The default value is zero, which indicates no scheduling interval
     * is set and the simulation state is updated only when a cloudlet is finished.
     * That ensures the highest performance but may not be desired
     * if you want to collect some simulation data in a defined time interval.</p>
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
     * @return the characteristics
     */
    DatacenterCharacteristics getCharacteristics();

    /**
     * Gets the storage of the Datacenter.
     *
     * @return the storage
     */
    DatacenterStorage getDatacenterStorage();

    /**
     * Sets the storage of the Datacenter.
     *
     * @param datacenterStorage the new storage
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
     * @see #DEF_BW_PERCENT_FOR_MIGRATION
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
     * Adds a {@link EventListener} object that will be notified every time
     * a new Host is available for the Datacenter during simulation runtime.
     * If the {@link #addHost(Host)} or {@link #addHostList(List)} is called
     * before the simulation starts, the listeners will not be notified.
     *
     * @param listener the event listener to add
     * @return
     */
    Datacenter addOnHostAvailableListener(EventListener<HostEventInfo> listener);

    /**
     * Adds a {@link EventListener} object that will be notified every time
     * a VM migration is finished either successfully or not.
     *
     * @param listener the event listener to add
     * @return
     */
    Datacenter addOnVmMigrationFinishListener(EventListener<DatacenterVmMigrationEventInfo> listener);

    /**
     * Checks if migrations are enabled.
     *
     * @return true, if migrations are enable; false otherwise
     */
    boolean isMigrationsEnabled();

    /**
     * Enable VM migrations.
     * @return
     * @see #getHostSearchRetryDelay()
     */
    Datacenter enableMigrations();

    /**
     * Disable VM migrations.
     * @return
     */
    Datacenter disableMigrations();

    /**
     * Gets the time interval before trying to find suitable Hosts to migrate VMs
     * from an under or overload Host again.
     * @return the Host search delay (in seconds)
     */
    double getHostSearchRetryDelay();

    /**
     * Sets the time interval before trying to find suitable Hosts to migrate VMs
     * from an under or overload Host again.
     * @param delay the new delay to set (in seconds).
     *              Give a positive value to define an actual delay or
     *              a negative value to indicate a new Host search for VM migration
     *              must be tried as soon as possible
     * @return
     */
    Datacenter setHostSearchRetryDelay(double delay);
}
