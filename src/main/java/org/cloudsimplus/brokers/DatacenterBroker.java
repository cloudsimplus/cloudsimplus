/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.brokers;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a broker acting on behalf of a cloud customer.
 * It hides VM management such as vm creation, submission of cloudlets to VMs
 * and destruction of VMs.
 *
 * <p>
 * A broker implements the policies for selecting a VM to run a Cloudlet
 * and a Datacenter to run the submitted VMs.
 * </p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface DatacenterBroker extends SimEntity
    permits DatacenterBrokerAbstract, DatacenterBrokerNull
{
    Logger LOGGER = LoggerFactory.getLogger(DatacenterBroker.class.getSimpleName());

    /**
     * An attribute that implements the Null Object Design Pattern for {@link DatacenterBroker}
     * objects.
     */
    DatacenterBroker NULL = new DatacenterBrokerNull();

    /**
     * A default delay value to indicate that <b>NO</b> VM should be
     * immediately destroyed after becoming idle.
     *
     * @see #setVmDestructionDelayFunction(Function)
     */
    double DEF_VM_DESTRUCTION_DELAY = -1.0;

    /**
     * Specifies that an already submitted cloudlet, which is in the
     * {@link #getCloudletWaitingList() waiting list}, must run in a specific virtual machine.
     *
     * @param cloudlet the cloudlet to be bind to a given Vm
     * @param vm       the vm to bind the Cloudlet to
     * @return true if the Cloudlet was found in the waiting list and was bind to the given Vm;
     *         false if the Cloudlet was not found in such a list
     *         (that may mean it wasn't submitted yet or was already created)
     */
    boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm);

    /**
     * Gets the list of cloudlets submitted to the broker that are waiting to be created inside
     * some Vm yet.
     *
     * @param <T> the class of Cloudlets inside the list
     * @return the cloudlet waiting list
     */
    <T extends Cloudlet> List<T> getCloudletWaitingList();

    /**
     * Gets a <b>copy</b> of the list of cloudlets that have finished executing,
     * to avoid the original list to be changed.
     *
     * @param <T> the class of Cloudlets inside the list
     * @return the list of finished cloudlets
     */
    <T extends Cloudlet> List<T> getCloudletFinishedList();

    /**
     * Gets a VM from the waiting list.
     * @param index the index of the VM to get
     * @return the waiting VM
     */
    Vm getWaitingVm(int index);

    /**
     * Gets a List of VMs submitted to the broker that are waiting to be created inside
     * some Datacenter yet.
     *
     * @param <T> the class of VMs inside the list
     * @return the list of waiting VMs
     */
    <T extends Vm> List<T> getVmWaitingList();

    /**
     * Gets the list of VMs in execution, if they are running Cloudlets or not.
     * These VMs can receive new submitted Cloudlets.
     *
     * @param <T> the class of VMs inside the list
     * @return the list of running VMs
     * @see #getVmCreatedList()
     */
    <T extends Vm> List<T> getVmExecList();

    /**
     * {@return the total number of VMs submitted to the broker, including created, waiting and failed VMs}
     */
    int getVmsNumber();

    /**
     * Checks if a VM is idle VM and request it to be destroyed at the time defined by
     * the {@link #setVmDestructionDelayFunction(Function)}.
     * The request will be sent if the given delay function returns a value
     * greater than {@link #DEF_VM_DESTRUCTION_DELAY}.
     * Otherwise, it doesn't send the request, meaning the VM should not be destroyed according to a specific delay.
     *
     * @param vm the VM to destroy
     * @see #setVmDestructionDelayFunction(Function)
     * @return this broker instance
     */
    DatacenterBroker requestIdleVmDestruction(Vm vm);

    /**
     * Requests the broker shutdown if it's idle.
    */
    void requestShutdownWhenIdle();

    /**
     * Gets the list of all VMs created so far,
     * independently if they are running yet or were already destroyed.
     * This can be used at the end of the simulation to know
     * which VMs have executed.
     *
     * @param <T> the class of VMs inside the list
     * @return the list of created VMs
     * @see #getVmExecList()
     */
    <T extends Vm> List<T> getVmCreatedList();

    /**
     * Submits a single {@link Vm} or {@link VmGroup} to the broker. When a {@link VmGroup} is given,
     * it will try to place all VMs from the group into the same Host.
     *
     * @param vm the {@link Vm} or {@link VmGroup} to be submitted
     * @return this broker instance
     */
    DatacenterBroker submitVm(Vm vm);

    /**
     * Submits a list of {@link Vm} or {@link VmGroup} where their creation inside
     * a Host will be requested to some {@link Datacenter}.
     * The Datacenter that will be chosen to place a VM is
     * determined by the {@link #setDatacenterMapper(BiFunction)}.
     *
     * <p>When a {@link VmGroup} is given, it will try to place all VMs
     * from the group into the same Host.</p>
     *
     * @param list the list of {@link Vm} or {@link VmGroup} to request the creation
     * @return this broker instance
     * @see #submitVmList(List)
     */
    DatacenterBroker submitVmList(List<? extends Vm> list);

    /**
     * Submits a list of {@link Vm} or {@link VmGroup} to the broker so that their creation
     * inside some Host will be requested just after a given delay.
     * Just the VMs that don't have a delay already assigned will have its submission delay changed.
     * All VMs will be added to the {@link #getVmWaitingList()}.
     *
     * <p>When a {@link VmGroup} is given, it will try to place all VMs
     * from the group into the same Host.</p>
     *
     * @param list            the list of {@link Vm} or {@link VmGroup} to request the creation
     * @param submissionDelay the delay the broker has to include when requesting the creation of VMs
     * @return this broker instance
     * @see #submitVmList(java.util.List)
     * @see Vm#getSubmissionDelay()
     */
    DatacenterBroker submitVmList(List<? extends Vm> list, double submissionDelay);

    /**
     * Submits a single {@link Cloudlet} to the broker so that it requests their creation inside some VM.
     *
     * @param cloudlet the Cloudlet to be submitted
     * @return this broker instance
     * @see #submitCloudletList(List)
     */
    DatacenterBroker submitCloudlet(Cloudlet cloudlet);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation inside some VM,
     * following the submission delay specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @return this broker instance
     * @see #submitCloudletList(java.util.List, double)
     */
    DatacenterBroker submitCloudletList(List<? extends Cloudlet> list);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation
     * inside some VM just after a given delay.
     * Just the Cloudlets that don't have a delay already assigned will have its submission delay changed.
     * All cloudlets will be added to the {@link #getCloudletWaitingList()},
     * setting their submission delay to the specified value.
     *
     * @param list            the list of Cloudlets to request the creation
     * @param submissionDelay the delay the broker has to include when requesting the creation of Cloudlets
     * @return this broker instance
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     */
    DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, double submissionDelay);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation inside
     * a specific VM, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @param vm the VM to which all Cloudlets will be bound to
     * @return this broker instance
     * @see #submitCloudletList(java.util.List, double)
     */
    DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation
     * inside a specific VM just after a given delay.
     * Just the Cloudlets that don't have a delay already assigned will have its submission delay changed.
     * All cloudlets will be added to the {@link #getCloudletWaitingList()},
     * setting their submission delay to the specified value.
     *
     * @param list the list of Cloudlets to request the creation
     * @param vm the VM to which all Cloudlets will be bound to
     * @param submissionDelay the delay the broker has to include when requesting the creation of Cloudlets
     * @return this broker instance
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     */
    DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay);

    /**
     * Sets the {@link BiFunction} that selects and returns a Datacenter
     * to place submitted VMs.
     *
     * <p>It defines the policy to select a Datacenter to host a VM
     * that is waiting to be created. That Function receives as parameter the last selected Datacenter
     * and the VM trying to be created, then it should return either:
     * <ul>
     *    <li>the Datacenter for the next VMs in the waiting list</li>
     *    <li>or {@link Datacenter#NULL} if no suitable Datacenter was found</li>
     * </ul>
     *
     * The provided BiFunction is accountable to define when the
     * last Datacenter will be used for the waiting VMs or a next one will be tried.
     * </p>
     *
     * <p>When there are VMs in the waiting list, the provided Function
     * will be called. If it receives {@link Datacenter#NULL}, it indicates
     * that: (i) a Datacenter was never selected to place VMs or (ii) the previous selected
     * Datacenter has not enough resources for all the waiting VMs.
     * The Function you provide here should consider it when returning
     * the Datacenter where the creation of waiting VMs will be tried.
     * </p>
     *
     * @param datacenterMapper the datacenterMapper to set
     * @return this broker instance
     */
    DatacenterBroker setDatacenterMapper(BiFunction<Datacenter, Vm, Datacenter> datacenterMapper);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted VMs before requesting the creation of such VMs in
     * some Datacenter. After sorting, the VM creation requests will be sent
     * in the order of the sorted VM list.
     *
     * @param comparator the VM Comparator to set
     * @return this broker instance
     */
    DatacenterBroker setVmComparator(Comparator<Vm> comparator);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted Cloudlets before mapping each Cloudlet to a Vm.
     * After sorting, the Cloudlet mapping will follow
     * the order of the sorted Cloudlet list.
     *
     * @param comparator the Cloudlet Comparator to set
     * @return this broker instance
     */
    DatacenterBroker setCloudletComparator(Comparator<Cloudlet> comparator);

    /**
     * Sets a {@link Function} that maps a given Cloudlet to a Vm.
     * It defines the policy used to select a Vm to run a Cloudlet
     * that is waiting to be created.
     *
     * @param vmMapper the Vm mapper Function to set. Such a Function
     *                 must receive a Cloudlet and return the Vm where it will be run.
     *                 If the Function is unable to find a VM for a Cloudlet,
     *                 it must return {@link Vm#NULL}.
     * @return this broker instance
     */
    DatacenterBroker setVmMapper(Function<Cloudlet, Vm> vmMapper);

    /**
     * Defines if the broker has to try selecting the closest {@link Datacenter}
     * to place {@link Vm}s, based on their timezone.
     * The default behaviour is to ignore {@link Datacenter}s and {@link Vm}s
     * timezones.
     *
     * @param select true to try selecting the closest Datacenter, false to ignore distance
     * @return this broker instance
     */
    DatacenterBroker setSelectClosestDatacenter(boolean select);

    /**
     * Checks if the broker has to try selecting the closest {@link Datacenter}
     * to place {@link Vm}s, based on their timezone.
     * The default behaviour is to ignore {@link Datacenter}s and {@link Vm}s
     * timezones.
     *
     * @return true if the closest Datacenter selection is enabled, false if it's disabled
     * @see #setSelectClosestDatacenter(boolean)
     */
    boolean isSelectClosestDatacenter();

    /**
     * {@return  a <b>read-only</b> list of cloudlets created inside some Vm}
     */
    List<Cloudlet> getCloudletCreatedList();

    /**
     * Adds an {@link EventListener} that will be notified every time
     * all VMs in the waiting list are created (placed) in some Host.
     *
     * <p>Events are fired according to the following conditions:
     * <ul>
     *     <li>if all VMs are submitted before the simulation start and all those VMs are created after starting,
     *     then the event will be fired just once, in the entire simulation execution time, for every registered Listener;
     *     </li>
     *     <li>if new VMs are submitted during simulation execution, the event may be fired multiple times.
     *     For instance, consider new VMs are submitted during simulation execution at times 10 and 20.
     *     If for every submission time, all VMs could be created, then every Listener will be notified 2 times
     *     (one for VMs submitted at time 10 and other for those at time 20).
     *     </li>
     * </ul>
     *
     * If all VMs submitted at a given time cannot be created due to lack of suitable Hosts,
     * the event will not be fired for that submission.
     * </p>
     *
     * @param listener the Listener that will be notified
     * @return this broker instance
     * @see #getVmWaitingList()
     */
    DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener);

    /**
     * Removes an {@link EventListener} to stop it from being notified when
     * VMs in the waiting list are all created.
     *
     * @param listener the Listener that will be removed
     * @return this broker instance
     * @see #addOnVmsCreatedListener(EventListener)
     */
    DatacenterBroker removeOnVmsCreatedListener(EventListener<? extends EventInfo> listener);

    /**
     * Sets the delay after which an idle VM should be destroyed.
     * Using such a method, it defines the same delay for any VM that becomes idle.
     * If you need to define different delays for distinct VMs
     * use the {@link #setVmDestructionDelayFunction(Function)} method.
     *
     * @param delay the time (in seconds) to wait before destroying idle VMs.
     *              A negative value indicates that <b>NO</b> VM should be
     *              immediately destroyed after becoming idle
     * @return this broker instance
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     */
    DatacenterBroker setVmDestructionDelay(double delay);

    /**
     * Sets a {@link Function} to define the delay after which an idle VM should be destroyed.
     * The Function must receive a {@link Vm} and return the delay to wait (in seconds),
     * after the VM becomes idle, to destroy it.
     *
     * <p>By providing a {@link Function} to indicate when idle VMs should be destroyed
     * enables you to define different delays for every VM that becomes idle,
     * according to desired conditions.</p>
     *
     * <p>
     *     <b>WARNING:</b> The delay returned by the given function should be larger
     *     than the {@link Simulation#getMinTimeBetweenEvents()} to ensure VMs are gracefully shutdown.
     * </p>
     *
     * @param function the {@link Function} to set (if null is given, no idle VM will be automatically destroyed)
     * @return this broker instance
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     * @see #setVmDestructionDelay(double)
     */
    DatacenterBroker setVmDestructionDelayFunction(@Nullable Function<Vm, Double> function);

    /**
     * {@return the list of all submitted Cloudlets}
     */
    List<Cloudlet> getCloudletSubmittedList();

    /**
     * Gets a List of VMs submitted to the broker that have failed to be created inside
     * some Datacenter due to lack of suitable Hosts.
     * <b>VMs are just moved to that list if {@link VmCreation#isRetryFailedVms()} is not enabled.</b>
     *
     * @param <T> the class of VMs inside the list
     * @return the list of failed VMs
     * @see VmCreation#setRetryDelay(double)
     */
    <T extends Vm> List<T> getVmFailedList();

    /**
     * {@return true if the broker must be shut down after becoming idle, false otherwise}
     */
    boolean isShutdownWhenIdle();

    /**
     * Indicates if the broker must be shut down after becoming idle.
     * @param shutdownWhenIdle true to enable shutdown when idle, false to disable
     * @return this broker instance
     */
    DatacenterBroker setShutdownWhenIdle(boolean shutdownWhenIdle);

    /**
     * {@return the object that keeps track of the number of VM creation retries sent by the broker
     * and enables configuring creation retries}
     */
    VmCreation getVmCreation();

    /**
     * Changes the last selected datacenter so that
     * new VMs will be attempted to be placed in the given Datacenter
     * instead of the previous one.
     * @param lastSelectedDc the new Datacenter to try to place next arriving VMs
     * @return this broker instance
     */
    DatacenterBroker setLastSelectedDc(Datacenter lastSelectedDc);

    /**
     * {@return the last selected datacenter attempted to place arriving VMs}
     */
    Datacenter getLastSelectedDc();

    /**
     * {@return true if batch VM creation is enabled; false otherwise}
     * That indicates if VM creation will be requested to a Datacenter one-by-one
     * or in batch (in a single VM creation request).
     */
    boolean isBatchVmCreation();

    /**
     * Enables or disables batch VM creation.
     * @param enable true of false to enable or disable
     * @return this broker instance
     * @see #isBatchVmCreation()
     */
    DatacenterBroker setBatchVmCreation(boolean enable);
}
