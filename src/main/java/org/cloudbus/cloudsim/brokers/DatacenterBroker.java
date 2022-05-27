/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
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
public interface DatacenterBroker extends SimEntity {
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
     * <p>This is used as the value returned by the
     * {@link #getVmDestructionDelayFunction()} if a {@link Function} is not set.</p>
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
     * Gets the total number of VMs submitted to the broker, including created, waiting and failed VMs.
     *
     * @return
     */
    int getVmsNumber();

    /**
     * Checks if a VM is idle VM and request it to be destroyed at the time defined by
     * the {@link #getVmDestructionDelayFunction()}.
     * The request will be sent if the given delay function returns a value
     * greater than {@link #DEF_VM_DESTRUCTION_DELAY}.
     * Otherwise, it doesn't send the request, meaning the VM should not be destroyed according to a specific delay.
     *
     * @param vm the VM to destroy
     * @see #getVmDestructionDelayFunction()
     * @return
     */
    DatacenterBroker requestIdleVmDestruction(Vm vm);

    /**
     * Requests the broker shutdown if it's idle.
    */
    void requestShutdownWhenIdle();

    /**
     * Destroys the passed VM.
     *
     * @param vm Virtual machine to be destroyed
     * @return Cloudlets which were being queued or running on the target VM.
     *         Such cloudlets have their state reset.
     */
    List<Cloudlet> destroyVm(Vm vm);

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
     * it will try to place all VMs inside it into the same Host.
     *
     * @param vm the Vm to be submitted
     * @see VmGroup
     * @return
     */
    DatacenterBroker submitVm(Vm vm);

    /**
     * Submits a list of {@link Vm} or {@link VmGroup} that their creation inside
     * a Host will be requested to some {@link Datacenter}.
     * The Datacenter that will be chosen to place a VM is
     * determined by the {@link #setDatacenterMapper(BiFunction)}.
     *
     * <p>When a list of {@link VmGroup} is given, it will try to place all VMs
     * from the same group into the same Host.</p>
     *
     * @param list the list of VMs to request the creation
     * @see VmGroup
     * @return
     */
    DatacenterBroker submitVmList(List<? extends Vm> list);

    /**
     * Submits a list of {@link Vm} or {@link VmGroup} to the broker so that their creation
     * inside some Host will be requested just after a given delay.
     * Just the VMs that don't have a delay already assigned will have its submission delay changed.
     * All VMs will be added to the {@link #getVmWaitingList()}.
     *
     * <p>When a list of {@link VmGroup} is given,
     * it will try to place all VMs from the same group into the same Host.</p>
     *
     * @param list            the list of VMs to request the creation
     * @param submissionDelay the delay the broker has to include when requesting the creation of VMs
     * @see #submitVmList(java.util.List)
     * @see Vm#getSubmissionDelay()
     * @see VmGroup
     * @return
     */
    DatacenterBroker submitVmList(List<? extends Vm> list, double submissionDelay);

    /**
     * Submits a single {@link Cloudlet} to the broker.
     *
     * @param cloudlet the Cloudlet to be submitted
     * @return
     */
    DatacenterBroker submitCloudlet(Cloudlet cloudlet);

    /**
     * Sends a list of cloudlets to the broker so that it requests their
     * creation inside some VM, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @see #submitCloudletList(java.util.List, double)
     * @return
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
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     * @return
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
     * @see #submitCloudletList(java.util.List, double)
     * @return
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
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     * @return
     */
    DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay);

    /**
     * Sets the {@link BiFunction} that selects and returns a Datacenter
     * to place submitted VMs.
     *
     * <p>It defines the policy to select a Datacenter to host a VM
     * that is waiting to be created. It receives as parameter the last selected Datacenter,
     * the VM trying to be created and should return:
     * <ul>
     *    <li>the Datacenter for the next VMs in the waiting list</li>
     *    <li>or {@link Datacenter#NULL} if no suitable Datacenter was found</li>
     * </ul>
     *
     * The provided BiFunction is accountable to define when the
     * last used Datacenter will be used for waiting VMs
     * or a next one will be tried.
     * </p>
     *
     * <p>When there are VMs in the waiting list, the provided Function
     * will be called. If it receives {@link Datacenter#NULL} it indicates
     * that a Datacenter was never selected to place VMs or the previous selected
     * Datacenter has not enough resources for all the waiting VMs.
     * The Function you provide here should consider that when returning
     * the Datacenter where the creation of waiting VMs will be tried.
     * </p>
     *
     * @param datacenterMapper the datacenterMapper to set
     * @return
     */
    DatacenterBroker setDatacenterMapper(BiFunction<Datacenter, Vm, Datacenter> datacenterMapper);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted VMs before requesting the creation of such VMs in
     * some Datacenter. After sorting, the VM creation requests will be sent
     * in the order of the sorted VM list.
     *
     * @param comparator the VM Comparator to set
     * @return
     */
    DatacenterBroker setVmComparator(Comparator<Vm> comparator);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted Cloudlets before mapping each Cloudlet to a Vm.
     * After sorting, the Cloudlet mapping will follow
     * the order of the sorted Cloudlet list.
     *
     * @param comparator the Cloudlet Comparator to set
     */
    void setCloudletComparator(Comparator<Cloudlet> comparator);

    /**
     * Sets a {@link Function} that maps a given Cloudlet to a Vm.
     * It defines the policy used to select a Vm to host a Cloudlet
     * that is waiting to be created.
     *
     * @param vmMapper the Vm mapper Function to set. Such a Function
     *                 must receive a Cloudlet and return the Vm where it will be placed into.
     *                 If the Function is unable to find a VM for a Cloudlet,
     *                 it should return {@link Vm#NULL}.
     * @return
     */
    DatacenterBroker setVmMapper(Function<Cloudlet, Vm> vmMapper);

    /**
     * Defines if the broker has to try selecting the closest {@link Datacenter}
     * to place {@link Vm}s, based on their timezone.
     * The default behaviour is to ignore {@link Datacenter}s and {@link Vm}s
     * timezones.
     *
     * @param select true to try selecting the closest Datacenter to be selected, false to ignore distance
     * @return
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
     * Gets a <b>read-only</b> list of cloudlets created inside some Vm.
     * @return the list of created Cloudlets
     */
    List<Cloudlet> getCloudletCreatedList();

    /**
     * Adds an {@link EventListener} that will be notified every time
     * VMs in the waiting list are all created (placed) in some Host.
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
     * @return
     * @see #getVmWaitingList()
     */
    DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener);

    /**
     * Removes an {@link EventListener} to stop it to be notified when
     * VMs in the waiting list are all created.
     *
     * @param listener the Listener that will be removed
     * @return
     * @see #addOnVmsCreatedListener(EventListener)
     */
    DatacenterBroker removeOnVmsCreatedListener(EventListener<? extends EventInfo> listener);

    /**
     * Gets a {@link Function} which defines when an idle VM should be destroyed.
     * The Function receives a {@link Vm} and returns the delay to wait (in seconds),
     * after the VM becomes idle, to destroy it.
     *
     * @return
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     */
    Function<Vm, Double> getVmDestructionDelayFunction();

    /**
     * Sets the delay after which an idle VM should be destroyed.
     * Using such a method, it defines the same delay for any VM that becomes idle.
     * If you need to define different delays for distinct VMs
     * use the {@link #setVmDestructionDelayFunction(Function)} method.
     *
     * @param delay the time (in seconds) to wait before destroying idle VMs
     * @return
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     */
    DatacenterBroker setVmDestructionDelay(double delay);

    /**
     * Sets a {@link Function} to define the delay after which an idle VM should be destroyed.
     * The Function must receive a {@link Vm} and return the delay to wait (in seconds),
     * after the VM becomes idle, to destroy it.
     *
     * <p>By providing a {@link Function} to define when idle VMs should be destroyed
     * enables you to define different delays for every VM that becomes idle,
     * according to desired conditions.</p>
     *
     * <p>
     *     <b>WARNING:</b> The delay returned by the given function should be larger
     *     then the simulation minTimeBetweenEvents to ensure VMs are gracefully shutdown.
     * </p>
     *
     * @param function the {@link Function} to set (if null is given, no idle VM will be automatically destroyed)
     * @return
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     * @see #setVmDestructionDelay(double)
     */
    DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function);

    List<Cloudlet> getCloudletSubmittedList();

    /**
     * Gets a List of VMs submitted to the broker that have failed to be created inside
     * some Datacenter due to lack of suitable Hosts.
     * <b>VMs are just moved to that list if {@code retryFailedVms} is not enabled.</b>
     *
     * @param <T> the class of VMs inside the list
     * @return the list of failed VMs
     * @see #setFailedVmsRetryDelay(double)
     */
    <T extends Vm> List<T> getVmFailedList();

    /**
     * Checks if the broker has to retry allocating VMs
     * that couldn't be placed due to lack of suitable Hosts.
     * @return
     */
    boolean isRetryFailedVms();

    /**
     * Gets a delay (in seconds) for the broker to retry allocating VMs
     * that couldn't be placed due to lack of suitable active Hosts.
     *
     * @return
     * <ul>
     *  <li>a value larger than zero to indicate the broker will retry
     *  to place failed VM as soon as new VMs or Cloudlets
     *  are submitted or after the given delay.</li>
     *  <li>otherwise, to indicate failed VMs will be just added to the
     *  {@link #getVmFailedList()} and the user simulation have to deal with it.
     *  If the VM has an {@link Vm#addOnCreationFailureListener(EventListener) OnCreationFailureListener},
     *  it will be notified about the failure.</li>
     * </ul>
     */
    double getFailedVmsRetryDelay();

    /**
     * Sets a delay (in seconds) for the broker to retry allocating VMs
     * that couldn't be placed due to lack of suitable active Hosts.
     *
     * Setting the attribute as:
     * <ul>
     *  <li>larger than zero, the broker will retry to place failed VM as soon as new VMs or Cloudlets
     *  are submitted or after the given delay.</li>
     *  <li>otherwise, failed VMs will be just added to the {@link #getVmFailedList()}
     *  and the user simulation have to deal with it.
     *  If the VM has an {@link Vm#addOnCreationFailureListener(EventListener) OnCreationFailureListener},
     *  it will be notified about the failure.</li>
     * </ul>
     * @param failedVmsRetryDelay
     */
    void setFailedVmsRetryDelay(double failedVmsRetryDelay);

    /**
     * Checks if the broker must be shut down after becoming idle.
     * @return
     */
    boolean isShutdownWhenIdle();

    /**
     * Indicates if the broker must be shut down after becoming idle.
     * @return
     */
    DatacenterBroker setShutdownWhenIdle(boolean shutdownWhenIdle);
}
