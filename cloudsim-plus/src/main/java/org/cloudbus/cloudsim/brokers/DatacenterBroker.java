package org.cloudbus.cloudsim.brokers;

import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * DatacentreBroker represents a broker acting on behalf of a user. It hides VM
 * management, as vm creation, submission of cloudlets to VMs and destruction of
 * VMs.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface DatacenterBroker {
    int getId();

    String getName();

    /**
     * Specifies that an already submitted cloudlet, that is in the {@link #getCloudletsWaitingList() waiting list},
     * must run in a specific virtual machine.
     *
     * @param cloudletId ID of the cloudlet being bount to a vm
     * @param vmId the vm id
     * @pre cloudletId > 0
     * @pre id > 0
     * @post $none
     * @todo @author manoelcampos This method would receive a Cloudlet object
     * because it is just setting the vmId cloudlet attribute.
     * When the method is called prior to call {@link DatacenterBroker#submitCloudletList(java.util.List)},
     * it tries to locate the cloudlet in the submitted list
     * and, when it doesn't exist yet, it is thrown a NullPointerException.
     * At leat, an overloaded version of the method would be created
     * and this one would try to find the cloudlet and, when
     * it is not found, thrown an specific exception
     * asking if the cloudlet already was submitted.
     */
    void bindCloudletToVm(int cloudletId, int vmId);

    /**
     * Gets the list of cloudlets submmited to the broker that are waiting to be created inside
     * some Vm yet.
     *
     * @param <T>
     * @return the cloudlet waiting list
     */
    <T extends Cloudlet> List<T> getCloudletsWaitingList();

    /**
     * Gets the list of cloudlets that have finished executing.
     *
     * @param <T>
     * @return the list of finished cloudlets
     */
    <T extends Cloudlet> List<T> getCloudletsFinishedList();

    Vm getVm(final int index);

    /**
     * Gets the list of VMs submitted to the broker that are waiting to be created inside
     * some Datacenter yet.
     *
     * @param <T>
     * @return the list of waiting VMs
     */
    <T extends Vm> List<T> getVmsWaitingList();

    /**
     * Gets the list of VMs created by the broker.
     *
     * @param <T>
     * @return the list of created VMs
     */
    <T extends Vm> List<T> getVmsCreatedList();

    /**
     * Sends a list of cloudlets to the broker for further
     * creation of each one in some Vm, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletsWaitingList()}.
     *
     * @param list the list
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List, double)
     */
    void submitCloudletList(List<? extends Cloudlet>  list);

    /**
     * Sends a list of cloudlets to the broker that will
     * be created into some Vm after a given delay.
     * All cloudlets will be added to the {@link #getCloudletsWaitingList()},
     * setting their submission delay to the specified value.
     *
     * @param list the list
     * @param submissionDelay the delay the broker has to include when submitting the Cloudlets
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List)
     * @see Cloudlet#getSubmissionDelay()
     */
    void submitCloudletList(List<? extends Cloudlet>  list, double submissionDelay);


    /**
     * Sends to the broker the list with virtual machines that must be
     * created.
     *
     * @param list the list
     * @pre list !=null
     * @post $none
     */
    void submitVmList(List<? extends Vm>  list);

    /**
     * Indicates if there are more cloudlets waiting to
     * be executed yet.
     *
     * @return true if there are waiting cloudlets, false otherwise
     */
    boolean hasMoreCloudletsToBeExecuted();

}
