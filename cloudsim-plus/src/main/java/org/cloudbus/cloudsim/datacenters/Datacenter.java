package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.resources.FileStorage;

import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.core.Simulation;

/**
 * An interface to be implemented by each class that provides Datacenter
 * features. The interface implements the Null Object Design Pattern in order to
 * start avoiding {@link NullPointerException} when using the
 * {@link Datacenter#NULL} object instead of attributing {@code null} to
 * {@link Datacenter} variables.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Datacenter extends SimEntity {

    /**
     * Adds a file into the resource's storage before the experiment starts. If
     * the file is a master file, then it will be registered to the RC when the
     * experiment begins.
     *
     * @param file a DataCloud file
     * @return a tag number denoting whether this operation is a success or not
     */
    int addFile(File file);

    /**
     * Gets the host list.
     *
     * @param <T> The generic type
     * @return the host list
     */
    <T extends Host> List<T> getHostList();

    Host getHost(final int index);


    /**
     * Gets the policy to be used by the switches to allocate VMs into hosts.
     *
     * @return the VM allocation policy
     * @see VmAllocationPolicy
     */
    VmAllocationPolicy getVmAllocationPolicy();

    /**
     * Gets the list of VMs submitted to be ran in some host of this switches.
     *
     * @param <T> the class of VMs inside the list
     * @return the vm list
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
     * 5 seconds, by means of {@link Cloudlet#getCloudletFinishedSoFar(int)}, it
     * will not return an updated value. By this way, one should set the
     * scheduling interval to 5 to get an updated result. As longer is the
     * interval, faster will be the simulation execution.
     *
     * @return the scheduling interval
     */
    double getSchedulingInterval();

    /**
     * Sets the scheduling delay to process each event received by the
     * switches (in seconds).
     *
     * @param schedulingInterval the new scheduling interval
     * @see #getSchedulingInterval()
     */
    Datacenter setSchedulingInterval(double schedulingInterval);


    /**
     * Gets the switches characteristics.
     *
     * @return the switches characteristics
     */
    DatacenterCharacteristics getCharacteristics();

    /**
     * Gets a <b>read-only</b> list of storage devices of the switches.
     *
     * @return the storage list
     */
    List<FileStorage> getStorageList();

    /**
     * Sets the list of storage devices of the switches.
     *
     * @param storageList the new storage list
     * @return 
     */
    Datacenter setStorageList(List<FileStorage> storageList);

    /**
     * A property that implements the Null Object Design Pattern for
     * {@link Datacenter} objects.
     */
    Datacenter NULL = new Datacenter() {
        @Override public int compareTo(SimEntity o) { return 0; }
        @Override public int getId() {
            return -1;
        }
        @Override public String getName() { return ""; }
        @Override public int addFile(File file) {
            return 0;
        }
        @Override public List<Host> getHostList() {
            return Collections.emptyList();
        }
        @Override public VmAllocationPolicy getVmAllocationPolicy() {
            return VmAllocationPolicy.NULL;
        }
        @Override public List<Vm> getVmList() {
            return Collections.emptyList();
        }
        @Override public Host getHost(final int index) {
            return Host.NULL;
        }
        @Override public double getSchedulingInterval() {
            return 0;
        }
        @Override public Datacenter setSchedulingInterval(double schedulingInterval) { return Datacenter.NULL; }
        @Override public DatacenterCharacteristics getCharacteristics() {
            return DatacenterCharacteristics.NULL;
        }
        @Override public List<FileStorage> getStorageList() { return Collections.emptyList(); }
        @Override public Datacenter setStorageList(List<FileStorage> storageList) { return Datacenter.NULL; }
        @Override public boolean isStarted() { return false; }
        @Override public Simulation getSimulation() { return Simulation.NULL; }
        @Override public SimEntity setSimulation(Simulation simulation) { return this; }
        @Override public void processEvent(SimEvent ev) {}
        @Override public void run() {}
        @Override public void start() {}
        @Override public void shutdownEntity() {}
        @Override public SimEntity setName(String newName) throws IllegalArgumentException { return this; }
        @Override public String toString() { return "Datacenter.NULL"; }
    };
}
