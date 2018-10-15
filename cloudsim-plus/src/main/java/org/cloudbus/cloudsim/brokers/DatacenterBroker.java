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
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a broker acting on behalf of a cloud customer.
 * It hides VM management such as vm creation, submission of cloudlets to VMs
 * and destruction of VMs.
 * <p>
 * A broker implements the policies for selecting a VM to run a Cloudlet
 * and a Datacenter to run the submitted VMs.
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
     * <p>This is used as the default value returned by the {@link #getVmDestructionDelayFunction()}
     * if a {@link Function} is not set.</p>
     *
     * @see #setVmDestructionDelayFunction(Function)
     */
    double DEF_VM_DESTRUCTION_DELAY = -1.0;

    /**
     * Specifies that an already submitted cloudlet, which is in the {@link #getCloudletWaitingList() waiting list},
     * must run in a specific virtual machine.
     *
     * @param cloudlet the cloudlet to be bind to a given Vm
     * @param vm       the vm to bind the Cloudlet to
     * @return true if the Cloudlet was found in the waiting list and was bind to the given Vm, false it the
     * Cloudlet was not found in such a list (that may mean it wasn't submitted yet or was already created)
     * @pre cloudletId > 0
     * @pre id > 0
     * @post $none
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
     * Submits a single {@link Vm} to the broker.
     *
     * @param vm the Vm to be submitted
     */
    void submitVm(Vm vm);

    /**
     * Submits a single {@link Cloudlet} to the broker.
     *
     * @param cloudlet the Cloudlet to be submitted
     */
    void submitCloudlet(Cloudlet cloudlet);

    /**
     * Sends a list of cloudlets to the broker so that it requests their
     * creation inside some VM, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List, double)
     */
    void submitCloudletList(List<? extends Cloudlet> list);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation
     * inside some VM just after a given delay.
     * Just the Cloudlets that don't have a delay already assigned will have its submission delay changed.
     * All cloudlets will be added to the {@link #getCloudletWaitingList()},
     * setting their submission delay to the specified value.
     *
     * @param list            the list of Cloudlets to request the creation
     * @param submissionDelay the delay the broker has to include when requesting the creation of Cloudlets
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     */
    void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay);

    /**
     * Sends a list of cloudlets to the broker so that it requests their creation inside
     * a specific VM, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @param vm the VM to which all Cloudlets will be bound to
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List, double)
     */
    void submitCloudletList(List<? extends Cloudlet> list, Vm vm);

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
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     */
    void submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay);

    /**
     * Sends to the broker a list with VMs that their creation inside a Host will be requested to some
     * {@link Datacenter}. The Datacenter that will be chosen to place a VM is
     * determined by the {@link #setDatacenterSupplier(Supplier)}.
     *
     * @param list the list of VMs to request the creation
     * @pre list !=null
     * @post $none
     */
    void submitVmList(List<? extends Vm> list);

    /**
     * Sends a list of VMs for the broker so that their creation inside some Host will be requested just after a given delay.
     * Just the VMs that don't have a delay already assigned will have its submission delay changed.
     * All VMs will be added to the {@link #getVmWaitingList()},
     * setting their submission delay to the specified value.
     *
     * @param list            the list of VMs to request the creation
     * @param submissionDelay the delay the broker has to include when requesting the creation of VMs
     * @pre list !=null
     * @post $none
     * @see #submitVmList(java.util.List)
     * @see Vm#getSubmissionDelay()
     */
    void submitVmList(List<? extends Vm> list, double submissionDelay);

    /**
     * Indicates if there are more cloudlets waiting to
     * be executed yet.
     *
     * @return true if there are waiting cloudlets, false otherwise
     */
    boolean isThereWaitingCloudlets();

    /**
     * Sets the {@link Supplier} that selects and returns a Datacenter
     * to place submitted VMs.
     * <p>
     * <p>The supplier defines the policy to select a Datacenter to host a VM
     * that is waiting to be created.</p>
     *
     * @param datacenterSupplier the datacenterSupplier to set
     */
    void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier);

    /**
     * Sets the {@link Supplier} that selects and returns a fallback Datacenter
     * to place submitted VMs when the Datacenter selected
     * by the {@link #setDatacenterSupplier(java.util.function.Supplier) Datacenter Supplier}
     * failed to create all requested VMs.
     * <p>
     * <p>The supplier defines the policy to select a Datacenter to host a VM when
     * all VM creation requests were received but not all VMs could be created.
     * In this case, a different Datacenter has to be selected to request
     * the creation of the remaining VMs in the waiting list.</p>
     *
     * @param fallbackDatacenterSupplier the fallbackDatacenterSupplier to set
     */
    void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted VMs before requesting the creation of such VMs in
     * some Datacenter. After sorting, the VM creation requests will be sent
     * in the order of the sorted VM list.
     * @param comparator the VM Comparator to set
     */
    void setVmComparator(Comparator<Vm> comparator);

    /**
     * Sets a {@link Comparator} that will be used to sort every list
     * of submitted Cloudlets before mapping each Cloudlet to a Vm.
     * After sorting, the Cloudlet mapping will follow
     * the order of the sorted Cloudlet list.
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
     */
    void setVmMapper(Function<Cloudlet, Vm> vmMapper);

    /**
     * Gets a <b>read-only</b> list of cloudlets created inside some Vm.
     * @return the list of created Cloudlets
     */
    List<Cloudlet> getCloudletCreatedList();

    /**
     * Adds an {@link EventListener} that will be notified every time
     * VMs in the waiting list are all created.
     *
     * <p>Events are fired according to the following conditions:
     * <ul>
     *     <li>if all VMs are submitted before the simulation start and all those VMs are created after starting,
     *     then the event will be fired just once, during all simulation execution, for every registered Listener;
     *     </li>
     *     <li>if all VMs submitted at a given time cannot be created due to lack of suitable Hosts,
     *     the event will not be fired for that submission;
     *     </li>
     *     <li>if new VMs are submitted during simulation execution, the event may be fired multiple times.
     *     For instance, consider new VMs are submitted during simulation execution at times 10 and 20.
     *     If for every submission time, all VMs could be created, then every Listener will be notified 2 times
     *     (one for VMs submitted at time 10 and other for those at time 20).
     *     </li>
     * </ul>
     * </p>
     * @param listener the Listener that will be notified
     * @return
     * @see #getVmWaitingList()
     * @see #addOneTimeOnVmsCreatedListener(EventListener)
     */
    DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener);

    /**
     * Adds an {@link EventListener} that will be notified <b>just once</b>
     * when VMs in the waiting list are all created.
     * After the first notification, the Listener is removed from the registered Listeners
     * and no further notifications will be sent to that specific Listener.
     *
     * <p>Even if VMs were submitted at different simulation times and
     * all of them are created successfully (which means notifications are
     * expected at different times), this Listener will be notified just
     * when the first list of VMs is created and no subsequent notifications
     * will be sent when other List of VMs is created.</p>
     *
     * <p>For instance, consider new VMs are submitted during simulation execution at times 10 and 20.
     * If for every submission time, all VMs could be created, then this specific Listener is expected to be
     * notified 2 times (one for VMs submitted at time 10 and other for those at time 20).
     * However, after VMs submitted at time 10 are all created, the Listener is notified and
     * unregistered, so that it will get no next notifications.
     * </p>
     *
     * @param listener the Listener that will be notified
     * @return
     * @see #getVmWaitingList()
     * @see #addOnVmsCreatedListener(EventListener)
     */
    DatacenterBroker addOneTimeOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener);

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
     * Sets a {@link Function} to define when an idle VM should be destroyed.
     * The Function receives a {@link Vm} and returns the delay to wait (in seconds),
     * after the VM becomes idle, to destroy it.
     *
     * @param function the {@link Function} to set (if null is given, it sets the default Function)
     * @return
     * @see #DEF_VM_DESTRUCTION_DELAY
     * @see Vm#getIdleInterval()
     */
    DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function);

    /**
     * Defines the default policy used to select a Vm to host a Cloudlet
     * that is waiting to be created.
     * <br>It applies a Round-Robin policy to cyclically select
     * the next Vm from the list of waiting VMs.
     *
     * @param cloudlet the cloudlet that needs a VM to be placed into
     * @return the selected Vm for the cloudlet or {@link Vm#NULL} if
     * no suitable VM was found
     */
    Vm defaultVmMapper(Cloudlet cloudlet);

    List<Cloudlet> getCloudletSubmittedList();
}
