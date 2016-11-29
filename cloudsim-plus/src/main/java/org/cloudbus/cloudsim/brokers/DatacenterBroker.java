package org.cloudbus.cloudsim.brokers;

import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * Represents a broker acting on behalf of a cloud customer.
 * It hides VM management such as vm creation, submission of cloudlets to VMs
 * and destruction of VMs.
 *
 * A broker implements the policies for selecting a VM to run a Cloudlet
 * and a Datacenter to run the submitted VMs.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface DatacenterBroker extends SimEntity {
    /**
     * Specifies that an already submitted cloudlet, that is in the {@link #getCloudletsWaitingList() waiting list},
     * must run in a specific virtual machine.
     *
     * @param cloudlet the cloudlet to be bind to a given Vm
     * @param vm the vm to bind the Cloudlet to
     * @return true if the Cloudlet was found in the waiting list and was bind to the given Vm, false it the
     * Cloudlet was not found in such a list (that may mean it wasn't submitted yet or was already created)
     * @pre cloudletId > 0
     * @pre id > 0
     * @post $none
     */
    boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm);

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

    Vm getWaitingVm(final int index);

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

    /**
     * Defines the policy to select a VM to host a given cloudlet
     * that is waiting to be created.
     *
     * @param cloudlet the cloudlet that needs a VM to be placed into
     * @return the selected Vm for the cloudlet or {@link Vm#NULL} if
     * no suitable VM was found
     */
    Vm selectVmForWaitingCloudlet(Cloudlet cloudlet);

    /**
     * Defines the policy to select a Datacenter to host a VM
     * that is waiting to be created.
     *
     * @return id of the Datacenter selected to request the creating
     * of waiting VMs or -1 if no suitable Datacenter was found
     */
    int selectDatacenterForWaitingVms();

    /**
     * Defines the policy to select a Datacenter to host a VM when
     * all VM creation requests were received but not all VMs could be created.
     * In this case, a different datacenter has to be selected to request
     * the creation of the remaining VMs in the waiting list.
     *
     * @return id of the Datacenter selected to try creating
     * the remaining VMs or -1 if no suitable Datacenter was found
     *
     * @see #selectDatacenterForWaitingVms()
     */
    int selectFallbackDatacenterForWaitingVms();

	/**
	 * An attribute that implements the Null Object Design Pattern for {@link DatacenterBroker}
	 * objects.
	 */
	DatacenterBroker NULL = new DatacenterBroker() {
        @Override public Simulation getSimulation() { return Simulation.NULL; }
        @Override public SimEntity setSimulation(Simulation simulation) { return this;}
        @Override public void processEvent(SimEvent ev) {}
        @Override public void run() {}
        @Override public int getId() { return -1; }
		@Override public String getName() { return ""; }
		@Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) { return false; }
		@Override public <T extends Cloudlet> List<T> getCloudletsWaitingList() { return Collections.emptyList(); }
		@Override public <T extends Cloudlet> List<T> getCloudletsFinishedList() { return Collections.emptyList(); }
		@Override public Vm getWaitingVm(int index) { return Vm.NULL; }
		@Override public <T extends Vm> List<T> getVmsWaitingList() { return Collections.emptyList(); }
		@Override public <T extends Vm> List<T> getVmsCreatedList() { return Collections.emptyList(); }
		@Override public void submitCloudletList(List<? extends Cloudlet> list) {}
		@Override public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {}
		@Override public void submitVmList(List<? extends Vm> list) {}
		@Override public boolean hasMoreCloudletsToBeExecuted() { return false; }
		@Override public Vm selectVmForWaitingCloudlet(Cloudlet cloudlet) { return Vm.NULL; }
		@Override public int selectDatacenterForWaitingVms() { return 0; }
		@Override public int selectFallbackDatacenterForWaitingVms() { return 0; }
        @Override public void startEntity() {}
        @Override public void shutdownEntity() {}
        @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
    };
}
