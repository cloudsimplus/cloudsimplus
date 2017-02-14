/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.brokers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.core.Simulation;

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
    /**
     * Specifies that an already submitted cloudlet, that is in the {@link #getCloudletsWaitingList() waiting list},
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
     * Gets the list of cloudlets submmited to the broker that are waiting to be created inside
     * some Vm yet.
     *
     * @param <T> the class of Cloudlets inside the list
     * @return the cloudlet waiting list
     */
    <T extends Cloudlet> List<T> getCloudletsWaitingList();

    /**
     * Gets the list of cloudlets that have finished executing.
     *
     * @param <T> the class of Cloudlets inside the list
     * @return the list of finished cloudlets
     */
    <T extends Cloudlet> List<T> getCloudletsFinishedList();

    Vm getWaitingVm(final int index);

    /**
     * Gets a List of VMs submitted to the broker that are waiting to be created inside
     * some Datacenter yet.
     *
     * @param <T> the class of VMs inside the list
     * @return the list of waiting VMs
     */
    <T extends Vm> List<T> getVmsWaitingList();

    /**
     * Gets the list of VMs created by the broker.
     *
     * @param <T> the class of VMs inside the list
     * @return the list of created VMs
     */
    <T extends Vm> List<T> getVmsCreatedList();

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
     * Sends a list of cloudlets for the broker to request its creation inside some VM, following the submission delay
     * specified in each cloudlet (if any).
     * All cloudlets will be added to the {@link #getCloudletsWaitingList()}.
     *
     * @param list the list of Cloudlets to request the creation
     * @pre list !=null
     * @post $none
     * @see #submitCloudletList(java.util.List, double)
     */
    void submitCloudletList(List<? extends Cloudlet> list);

    /**
     * Sends a list of cloudlets for the broker that their creation inside some VM will be requested just after a given delay.
     * Just the Cloudlets that don't have a delay already assigned will have its submission delay changed.
     * All cloudlets will be added to the {@link #getCloudletsWaitingList()},
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
     * Sends a list of VMs for the broker that their creation inside some Host will be requested just after a given delay.
     * Just the VMs that don't have a delay already assigned will have its submission delay changed.
     * All VMs will be added to the {@link #getVmsWaitingList()},
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
    boolean hasMoreCloudletsToBeExecuted();

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
     * Sets a {@link Function} that maps a given Cloudlet to a Vm.
     * It defines the policy used to select a Vm to host a Cloudlet
     * that is waiting to be created.
     *
     * @param vmMapper the Vm mapper function to set
     */
    void setVmMapper(Function<Cloudlet, Vm> vmMapper);
    
    /**
     * Gets a <b>read-only</b> list of cloudlets created inside some Vm.
     * @return the list of created Cloudlets
     */
    public Set<Cloudlet> getCloudletsCreatedList();

    /**
     * An attribute that implements the Null Object Design Pattern for {@link DatacenterBroker}
     * objects.
     */
    DatacenterBroker NULL = new DatacenterBroker() {
        @Override
        public int compareTo(SimEntity o) {
            return 0;
        }

        @Override
        public boolean isStarted() {
            return false;
        }

        @Override
        public Simulation getSimulation() {
            return Simulation.NULL;
        }

        @Override
        public SimEntity setSimulation(Simulation simulation) {
            return this;
        }

        @Override
        public void processEvent(SimEvent ev) {
        }

        @Override
        public void schedule(int dest, double delay, int tag) {
        }

        @Override
        public void run() {
        }

        @Override
        public void start() {
        }

        @Override
        public int getId() {
            return -1;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
            return false;
        }

        @Override
        public <T extends Cloudlet> List<T> getCloudletsWaitingList() {
            return Collections.emptyList();
        }

        @Override
        public <T extends Cloudlet> List<T> getCloudletsFinishedList() {
            return Collections.emptyList();
        }

        @Override
        public Vm getWaitingVm(int index) {
            return Vm.NULL;
        }

        @Override
        public <T extends Vm> List<T> getVmsWaitingList() {
            return Collections.emptyList();
        }

        @Override
        public <T extends Vm> List<T> getVmsCreatedList() {
            return Collections.emptyList();
        }

        @Override
        public void submitVm(Vm vm) {
        }

        @Override
        public void submitCloudlet(Cloudlet cloudlet) {
        }

        @Override
        public void submitCloudletList(List<? extends Cloudlet> list) {
        }

        @Override
        public void submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) {
        }

        @Override
        public void submitVmList(List<? extends Vm> list) {
        }

        @Override
        public void submitVmList(List<? extends Vm> list, double submissionDelay) {
        }

        @Override
        public boolean hasMoreCloudletsToBeExecuted() {
            return false;
        }

        @Override
        public void shutdownEntity() {
        }

        @Override
        public SimEntity setName(String newName) throws IllegalArgumentException {
            return this;
        }

        @Override
        public void setDatacenterSupplier(Supplier<Datacenter> datacenterSupplier) {
        }

        @Override
        public void setFallbackDatacenterSupplier(Supplier<Datacenter> fallbackDatacenterSupplier) {
        }

        @Override
        public void setVmMapper(Function<Cloudlet, Vm> vmMapper) {
        }

        @Override
        public Set<Cloudlet> getCloudletsCreatedList() {
            return Collections.EMPTY_SET;
        }
    };
}
