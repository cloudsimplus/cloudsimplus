/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;

import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe.Status;

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
public interface Host extends Machine, Comparable<Host> {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    Host NULL = new HostNull();

    /**
     * Gets the Datacenter where the host is placed.
     *
     * @return the data center of the host
     */
    Datacenter getDatacenter();

    /**
     * Sets the Datacenter where the host is placed.
     *
     * @param datacenter the new data center to move the host
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Checks if the host active and is suitable for vm. If it has enough resources
     * to attend the VM.
     *
     * @param vm the vm
     * @return true, if is suitable for vm
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Checks if the Host is powered-on or not.
     * @return true if the Host is powered-on, false otherwise.
     */
    boolean isActive();

    /**
     * Sets the powered state of the Host, to indicate if it's powered on or off.
     * When a Host is powered off, no VMs will be submitted to it.
     *
     * <p>If it is set to powered off while VMs are running inside it,
     * it is simulated a scheduled shutdown, so that, all running
     * VMs will finish, but not more VMs will be submitted to this Host.</p>
     *
     * @param active true to set the Host as powered on, false as powered off
     * @return
     */
    Host setActive(boolean active);

    /**
     * Gets the list of VMs migrating into this host.
     *
     * @param <T> the generic type
     * @return the vms migrating in
     */
    <T extends Vm> Set<T> getVmsMigratingIn();

    /**
     * Try to add a VM migrating into the current host
     * if there is enough resources for it.
     * In this case, the resources are allocated
     * and the VM added to the {@link #getVmsMigratingIn()} List.
     * Otherwise, the VM is not added.
     *
     * @param vm the vm
     * @return true if the Vm was migrated in, false if the Host doesn't have enough resources to place the Vm
     */
    boolean addMigratingInVm(Vm vm);

    /**
     * Gets a <b>read-only</b> list of VMs migrating out from the Host.
     *
     * @return
     */
    Set<Vm> getVmsMigratingOut();

    /**
     * Adds a {@link Vm} to the list of VMs migrating out from the Host.
     * @param vm the vm to be added
     * @return true if the VM wasn't into the list and was added, false otherwise
     */
    boolean addVmMigratingOut(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating into the Host.
     * @param vm the vm to be added
     * @return
     */
    boolean removeVmMigratingIn(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating out from the Host.
     * @param vm the vm to be added
     * @return
     */
    boolean removeVmMigratingOut(Vm vm);

    /**
     * Reallocate VMs migrating into the host. Gets the VM in the migrating in queue
     * and allocate them on the host.
     */
    void reallocateMigratingInVms();

    /**
     * Gets total MIPS capacity of PEs which are not {@link Status#FAILED}.
     * @return the total MIPS of working PEs
     */
    @Override
    double getTotalMipsCapacity();

    /**
     * Removes a migrating in vm.
     *
     * @param vm the vm
     */
    void removeMigratingInVm(Vm vm);

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
     * Gets the total allocated MIPS for a VM along all its PEs.
     *
     * @param vm the vm
     * @return the allocated mips for vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets the list of all Processing Elements (PEs) of the host,
     * including failed PEs.
     *
     * @return the list of all Host PEs
     * @see #getWorkingPeList()
     */
    List<Pe> getPeList();

    /**
     * Gets the list of working Processing Elements (PEs) of the host,
     * <b>which excludes failed PEs</b>.
     *
     * @return the list working (non-failed) Host PEs
     */
    List<Pe> getWorkingPeList();

    /**
     * Gets the free pes number.
     *
     * @return the free pes number
     */
    int getNumberOfFreePes();

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
     * Gets the number of PEs that are working.
     * That is, the number of PEs that aren't FAIL.
     *
     * @return the number of working pes
     */
    long getNumberOfWorkingPes();

    /**
     * Gets the number of PEs that have failed.
     *
     * @return the number of failed pes
     */
    long getNumberOfFailedPes();

    /**
     * Gets the current amount of available MIPS at the host.
     *
     * @return the available amount of MIPS
     */
    double getAvailableMips();

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     *
     * @return max mips
     */
    double getMaxAvailableMips();

    /**
     * Gets the total free storage available at the host in Megabytes.
     *
     * @return the free storage
     */
    long getAvailableStorage();

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
     * Gets a <b>read-only</b> list of VMs assigned to the host.
     *
     * @param <T> The generic type
     * @return the read-only vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets a <b>read-only</b> list of all VMs which have been created into the host
     * during the entire simulation.
     *
     * @param <T> The generic type
     * @return the read-only vm created list
     */
    <T extends Vm> List<T> getVmCreatedList();

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
     * @return
     */
    Host setVmScheduler(VmScheduler vmScheduler);

    /**
     * Checks if the host is working properly or has failed.
     *
     * @return true, if the host PEs have failed; false otherwise
     */
    boolean isFailed();

    /**
     * Sets the Host state to "failed" or "working".
     *
     * @param failed true to set the Host to "failed", false to set to "working"
     * @return true if the Host status was changed, false otherwise
     */
    boolean setFailed(boolean failed);

    /**
     * Updates the processing of VMs running on this Host,
     * that makes the processing of cloudlets inside such VMs to be updated.
     *
     * @param currentTime the current time
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
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
    boolean createVm(Vm vm);

    /**
     * Destroys a VM running in the host and removes it from the {@link #getVmList()}.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
     */
    void destroyVm(Vm vm);

    /**
     * Try to allocate resources to a new temporary VM in the Host.
     * The method is used only to book resources for a given VM.
     * For instance, if is being chosen Hosts to migrate a set of VMs,
     * when a Host is selected for a given VM, using this method,
     * the resources are reserved and then, when the next
     * VM is selected for the same Host, the
     * reserved resources already were reduced from the available
     * amount. This way, it it was possible to place just one Vm into that Host,
     * with the booking, no other VM will be selected to that Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     * @todo https://github.com/manoelcampos/cloudsim-plus/issues/94
     */
    boolean createTemporaryVm(Vm vm);

    /**
     * Destroys a temporary VM created into the Host to book resources.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
     * @see #createTemporaryVm(Vm)
     * @todo https://github.com/manoelcampos/cloudsim-plus/issues/94
     */
    void destroyTemporaryVm(Vm vm);

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

    /**
     * Gets current utilization of CPU in percentage (between [0 and 1]),
     * considering the usage of all its PEs..
     *
     * @return
     */
    double getUtilizationOfCpu();

    /**
     * Gets the current total utilization of CPU in MIPS,
     * considering the usage of all its PEs.
     *
     * @return
     */
    double getUtilizationOfCpuMips();

    /**
     * Gets the current utilization of bw (in absolute values).
     *
     * @return
     */
    long getUtilizationOfBw();

    /**
     * Gets the current utilization of memory (in absolute values).
     *
     * @return
     */
    long getUtilizationOfRam();

}
