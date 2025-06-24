/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.SimpleStorage;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;

/**
 * Implements the basic features of a Virtual Machine (VM), which runs inside a
 * {@link Host} that may be shared among other VMs. It processes
 * {@link Cloudlet}s (applications). This processing happens according to a policy,
 * defined by the {@link CloudletScheduler}. Each VM has an owner/user represented
 * by a {@link DatacenterBroker}, which can submit cloudlets to the VM to execute them.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class VmSimple extends VmAbstract {
    /**
     * A copy constructor that creates a VM based on the configuration of another one.
     * The created VM will have the same MIPS capacity, number of PEs,
     * BW, RAM and size of the given VM, but a default {@link CloudletScheduler} and no {@link DatacenterBroker}.
     * @param sourceVm the VM to be cloned
     * @see #VmSimple(double, long)
     */
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
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber  amount of [Pe] (CPU cores)
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final double mipsCapacity, final long pesNumber) {
        this(-1, mipsCapacity, pesNumber);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// It is not defined an `id` for the Vm. The `id` is defined when the Vm is submitted to
    /// a [DatacenterBroker].
    ///
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber  amount of [Pe] (CPU cores)
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final double mipsCapacity, final long pesNumber, final CloudletScheduler cloudletScheduler) {
        super(-1, (long)mipsCapacity, pesNumber, cloudletScheduler);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    ///
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// It receives the amount of MIPS as a double value but converts it internally
    /// to a `long`. The method is just provided as a handy-way to create a Vm
    /// using a double value for MIPS that usually is generated from some computations.
    ///
    /// **NOTE:** The Vm will use a [CloudletSchedulerTimeShared] by default.
    /// If you need to change that, just call [#setCloudletScheduler(CloudletScheduler)].
    ///
    /// @param id           unique ID of the VM
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber  amount of [Pe] (CPU cores)
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final long id, final double mipsCapacity, final long pesNumber) {
        this(id, (long) mipsCapacity, pesNumber);
    }

    /// Creates a Vm with 1024 MEGA of RAM, 100 Megabits/s of Bandwidth and
    /// 1024 MEGA of Storage Size.
    ///
    /// To change these values, use the respective setters.
    /// While the Vm [is being instantiated][#isCreated()], such values can be changed freely.
    ///
    /// **NOTE:** The Vm will use a [CloudletSchedulerTimeShared] by default.
    /// If you need to change that, just call [#setCloudletScheduler(CloudletScheduler)].
    ///
    /// @param id           unique ID of the VM
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber  amount of [Pe] (CPU cores)
    /// @see #setRam(long)
    /// @see #setBw(long)
    /// @see #setStorage(SimpleStorage)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public VmSimple(final long id, final long mipsCapacity, final long pesNumber) {
        super(id, mipsCapacity, pesNumber);
    }

    @Override
    public String toString() {
        final String desc = StringUtils.isBlank(description) ? "" : " (%s)".formatted(description);
        final String type = this instanceof VmGroup ? "VmGroup" : "Vm";
        return "%s %d%s".formatted(type, getId(), desc);
    }

    /**
     * Compare this Vm with another one based on {@link #getTotalMipsCapacity()}.
     *
     * @param obj the Vm to compare to
     * @return {@inheritDoc}
     */
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
