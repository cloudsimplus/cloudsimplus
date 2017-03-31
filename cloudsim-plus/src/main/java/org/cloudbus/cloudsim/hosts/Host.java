/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;

import java.util.List;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;

/**
 * An interface to be implemented by each class that provides
 * Physical Machines (Hosts) features.
 * The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link Host#NULL} object instead
 * of attributing {@code null} to {@link Host} variables.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Host extends Identificable, Resourceful, Comparable<Host> {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    Host NULL = new HostNull();

    /**
     * Adds a VM migrating into the current host.
     *
     * @param vm the vm
     * @return true if the Vm was migrated in, false if the Host doesn't have enough resources to place the Vm
     */
    boolean addMigratingInVm(Vm vm);

    /**
     * Allocates PEs for a VM.
     *
     * @param vm the vm
     * @param mipsShare the list of MIPS share to be allocated to the VM
     * @return $true if this policy allows a new VM in the host, $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocatePesForVm(Vm vm, List<Double> mipsShare);

    /**
     * Releases PEs allocated to a VM.
     *
     * @param vm the vm
     * @pre $none
     * @post $none
     */
    void deallocatePesForVm(Vm vm);

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the total free MIPS available at the host.
     *
     * @return the free mips
     */
    double getAvailableMips();

    /**
     * Gets the total free storage available at the host in Megabytes.
     *
     * @return the free storage
     */
    long getAvailableStorage();

    /**
     * Gets the host bw capacity in Megabits/s.
     *
     * @return the host bw capacity
     * @pre $none
     * @post $result > 0
     */
    Resource getBw();

    /**
     * Gets the bandwidth (BW) provisioner with capacity in Megabits/s.
     *
     * @return the bw provisioner
     */
    ResourceProvisioner getBwProvisioner();

    /**
     * Sets the bandwidth (BW) provisioner with capacity in Megabits/s.
     *
     * @param bwProvisioner the new bw provisioner
     */
    Host setBwProvisioner(ResourceProvisioner bwProvisioner);

    /**
     * Gets the Datacenter where the host is placed.
     *
     * @return the data center of the host
     */
    Datacenter getDatacenter();

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     *
     * @return max mips
     */
    double getMaxAvailableMips();

    /**
     * Gets the free pes number.
     *
     * @return the free pes number
     */
    int getNumberOfFreePes();

    /**
     * Gets the number of PEs that are working.
     * That is, the number of PEs that aren't FAIL.
     *
     * @return the number of working pes
     */
    long getNumberOfWorkingPes();

    /**
     * Gets the PEs number.
     *
     * @return the pes number
     */
    int getNumberOfPes();

    /**
     * Gets the Processing Elements (PEs) of the host, that
     * represent its CPU cores and thus, its processing capacity.
     *
     * @return the pe list
     */
    List<Pe> getPeList();

    /**
     * Gets the host memory resource in Megabytes.
     *
     * @return the host memory
     * @pre $none
     * @post $result > 0
     */
    Resource getRam();

    /**
     * Gets the ram provisioner with capacity in Megabytes.
     *
     * @return the ram provisioner
     */
    ResourceProvisioner getRamProvisioner();

    /**
     * Sets the ram provisioner with capacity in Megabytes.
     *
     * @param ramProvisioner the new ram provisioner
     */
    Host setRamProvisioner(ResourceProvisioner ramProvisioner);

    /**
     * Gets the storage device of the host with capacity in Megabytes.
     *
     * @return the host storage device
     * @pre $none
     * @post $result >= 0
     */
    Resource getStorage();

    /**
     * Gets the total allocated MIPS for a VM along all its PEs.
     *
     * @param vm the vm
     * @return the allocated mips for vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets the total mips.
     *
     * @return the total mips
     */
    long getTotalMips();

    /**
     * Gets a VM by its id and user.
     *
     * @param vmId the vm id
     * @param brokerId ID of VM's owner
     * @return the virtual machine object, $null if not found
     * @pre $none
     * @post $none
     */
    Vm getVm(int vmId, int brokerId);

    /**
     * Gets the list of VMs assigned to the host.
     *
     * @param <T> The generic type
     * @return the vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.
     *
     * @return the {@link VmScheduler}
     */
    VmScheduler getVmScheduler();

    /**
     * Sets the policy for allocation of host PEs to VMs in order to schedule VM
     * execution. The host also sets itself to the given scheduler.
     * It also sets the Host itself to the given scheduler.
     *
     * @param vmScheduler the vm scheduler to set
     */
    Host setVmScheduler(VmScheduler vmScheduler);


    /**
     * Gets the list of VMs migrating into this host.
     *
     * @param <T> the generic type
     * @return the vms migrating in
     */
    <T extends Vm> List<T> getVmsMigratingIn();

    /**
     * Checks if the host is working properly or has failed.
     *
     * @return true, if the host PEs have failed; false otherwise
     */
    boolean isFailed();

    /**
     * Checks if the host is suitable for vm. If it has enough resources
     * to attend the VM.
     *
     * @param vm the vm
     * @return true, if is suitable for vm
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Reallocate VMs migrating into the host. Gets the VM in the migrating in queue
     * and allocate them on the host.
     */
    void reallocateMigratingInVms();

    /**
     * Removes a migrating in vm.
     *
     * @param vm the vm
     */
    void removeMigratingInVm(Vm vm);

    /**
     * Sets the Datacenter where the host is placed.
     *
     * @param datacenter the new data center to move the host
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Sets the particular Pe status on the host.
     *
     * @param peId the pe id
     * @param status the new Pe status
     * @return <tt>true</tt> if the Pe status has set, <tt>false</tt> otherwise (Pe id might not
     *         be exist)
     * @pre peID >= 0
     * @post $none
     */
    boolean setPeStatus(int peId, Pe.Status status);

    /**
     * Updates the processing of VMs running on this Host,
     * that makes the processing of cloudlets inside such VMs to be updated.
     *
     * @param currentTime the current time
     * @return the predicted completion time of the earliest finishing cloudlet
     * (that is a future simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     * @pre currentTime >= 0.0
     * @post $none
     */
    double updateProcessing(double currentTime);

    /**
     * Try to allocate resources to a new VM in the Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean vmCreate(Vm vm);

    /**
     * Destroys a VM running in the host and removes it from the {@link #getVmList()}.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
     */
    void destroyVm(Vm vm);

    /**
     * Destroys all VMs running in the host and remove them from the {@link #getVmList()}.
     *
     * @pre $none
     * @post $none
     */
    void destroyAllVms();

    /**
     * Adds a listener object that will be notified every time when
     * the host updates the processing of all its {@link Vm VMs}.
     *
     * @param listener the OnUpdateProcessingListener to add
     * @return
     * @see #updateProcessing(double)
     */
    Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Removes a listener object from the OnUpdateProcessingListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     * @see #updateProcessing(double)
     */
    boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    boolean setFailed(boolean failed);

    /**
     * Gets the CloudSim instance that represents the simulation the Entity is related to.
     * @return
     * @see #setSimulation(Simulation)
     */
    Simulation getSimulation();

    /**
     * Sets the CloudSim instance that represents the simulation the Entity is related to.
     * Such attribute has to be set by the {@link Datacenter} that the host belongs to.
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @return
     */
    Host setSimulation(Simulation simulation);


    /**
     * Gets the {@link ResourceProvisioner}s that manages a Host resource
     * such as {@link Ram}, {@link Bandwidth} and {@link Pe}.
     * @param resourceClass the class of the resource to get its provisioner
     * @return the {@link ResourceProvisioner} for the given resource class
     */
    ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass);
}
