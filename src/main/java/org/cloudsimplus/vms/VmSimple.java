/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.SimpleStorage;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;

/// Implements the basic features of a Virtual Machine (VM), which runs inside a
/// [Host] that may be shared among other VMs. It processes
/// [Cloudlet]s (applications). This processing happens according to a policy,
/// defined by the [CloudletScheduler]. Each VM has an owner/user represented
/// by a [DatacenterBroker], which can submit cloudlets to the VM to execute them.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class VmSimple extends VmAbstract {
    /// A copy constructor that creates a VM based on the configuration of another one.
    /// The created VM will have the same MIPS capacity, number of CPU cores,
    /// BW, RAM and size of the given VM, but a default [CloudletScheduler] and no [DatacenterBroker].
    /// @param sourceVm the VM to be cloned
    /// @see #VmSimple(CoreCapacity)
    public VmSimple(final Vm sourceVm) {
        super(sourceVm);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// It is not defined an `id` for the Vm. The `id` is defined when the Vm is submitted to
    /// a [DatacenterBroker].
    ///
    /// **NOTE:** The Vm will use a [CloudletSchedulerTimeShared] by default.
    /// If you need to change that, just call [#setCloudletScheduler(CloudletScheduler)].
    ///
    /// @param capacity the number of CPU cores of the VM and the MIPS capacity of each core
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final CoreCapacity capacity) {
        this(-1, capacity);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// It is not defined an `id` for the Vm. The `id` is defined when the Vm is submitted to
    /// a [DatacenterBroker].
    ///
    /// @param capacity the number of CPU cores of the VM and the MIPS capacity of each core
    /// @param cloudletScheduler the scheduler that defines how the VM runs its Cloudlets
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final CoreCapacity capacity, final CloudletScheduler cloudletScheduler) {
        super(-1, capacity, cloudletScheduler);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// **NOTE:** The Vm will use a [CloudletSchedulerTimeShared] by default.
    /// If you need to change that, just call [#setCloudletScheduler(CloudletScheduler)].
    ///
    /// @param id unique ID of the VM
    /// @param capacity the number of CPU cores of the VM and the MIPS capacity of each core
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final long id, final CoreCapacity capacity) {
        super(id, capacity);
    }

    @Override
    public String toString() {
        final String desc = StringUtils.isBlank(description) ? "" : " (%s)".formatted(description);
        final String type = this instanceof VmGroup ? "VmGroup" : "Vm";
        return "%s %d%s".formatted(type, getId(), desc);
    }

    /// Compare this Vm with another one based on [#getTotalMipsCapacity()].
    ///
    /// @param obj the Vm to compare to
    /// @return {@inheritDoc}
    @Override
    public int compareTo(@NonNull final Vm obj) {
        if(this.equals(obj)) {
            return 0;
        }

        return Double.compare(getTotalMipsCapacity(), obj.getTotalMipsCapacity()) +
               Long.compare(this.getId(), obj.getId()) +
               this.getBroker().compareTo(obj.getBroker());
    }
}
