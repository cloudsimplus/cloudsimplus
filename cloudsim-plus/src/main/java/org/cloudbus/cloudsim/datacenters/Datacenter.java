/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.resources.File;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.resources.FileStorage;

import java.util.List;

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
public interface Datacenter extends SimEntity {
    /**
     * A property that implements the Null Object Design Pattern for
     * {@link Datacenter} objects.
     */
    Datacenter NULL = new DatacenterNull();

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

    Host getHost(int index);

    /**
     * Gets the policy to be used by the Datacenter to allocate VMs into hosts.
     *
     * @return the VM allocation policy
     * @see VmAllocationPolicy
     */
    VmAllocationPolicy getVmAllocationPolicy();

    /**
     * Gets a <b>read-only</b> list all VMs from all Hosts of this Datacenter.
     *
     * @param <T> the class of VMs inside the list
     * @return the list all VMs from all Hosts
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
     * 5 seconds, by means of {@link Cloudlet#getFinishedLengthSoFar(Datacenter)}, it
     * will not return an updated value. By this way, one should set the
     * scheduling interval to 5 to get an updated result. As longer is the
     * interval, faster will be the simulation execution.
     *
     * @return the scheduling interval
     */
    double getSchedulingInterval();

    /**
     * Sets the scheduling delay to process each event received by the
     * Datacenter (in seconds).
     *
     * @param schedulingInterval the new scheduling interval
     * @return 
     * @see #getSchedulingInterval()
     */
    Datacenter setSchedulingInterval(double schedulingInterval);

    /**
     * Gets the Datacenter characteristics.
     *
     * @return the Datacenter characteristics
     */
    DatacenterCharacteristics getCharacteristics();

    /**
     * Gets a <b>read-only</b> list of storage devices of the Datacenter.
     *
     * @return the storage list
     */
    List<FileStorage> getStorageList();

    /**
     * Sets the list of storage devices of the Datacenter.
     *
     * @param storageList the new storage list
     * @return
     */
    Datacenter setStorageList(List<FileStorage> storageList);
}
