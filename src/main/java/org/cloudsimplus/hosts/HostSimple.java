/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts;

import org.cloudsimplus.core.ChangeableId;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.HarddriveStorage;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.vms.Vm;


/// A Host class that implements the most basic features of a Physical Machine
/// (PM) inside a [Datacenter]. It executes actions related to the management
/// of virtual machines (e.g., creation and destruction). A Host has a defined
/// policy for provisioning memory and bw, as well as an allocation policy for
/// cores to [Virtual Machines][Vm]. A Host is associated with a Datacenter and
/// can run virtual machines.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class HostSimple extends HostAbstract {

    /// Creates and instantaneously powers on a Host without a pre-defined ID.
    /// It sets 10 GB of RAM, 1000 Mbps of Bandwidth and 500 GB of Storage.
    /// It uses a [ResourceProvisionerSimple] for RAM and Bandwidth.
    /// Finally, it sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    /// @see #setStartupDelay(double)
    public HostSimple(final CoreCapacity capacity) {
        super(capacity);
    }

    /// Creates a Host without a pre-defined ID,
    /// 10 GB of RAM, 1000 Mbps of Bandwidth and 500 GB of Storage
    /// and enabling the host to be powered on or not.
    ///
    /// It creates a [ResourceProvisionerSimple]
    /// for RAM and Bandwidth. Finally, it sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    /// @param activate true to power the Host on, false to power off (see [#setStartupDelay(double)])
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public HostSimple(final CoreCapacity capacity, final boolean activate) {
        super(capacity, activate);
    }

    /// Creates and instantaneously powers on a Host with the given parameters and a
    /// [VmSchedulerSpaceShared] as default.
    ///
    /// @param ramProvisioner the ram provisioner with capacity in Megabytes
    /// @param bwProvisioner the bw provisioner with capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    ///
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final CoreCapacity capacity)
    {
        super(ramProvisioner, bwProvisioner, storage, capacity);
    }

    /// Creates and instantaneously powers on a Host without a pre-defined ID.
    /// It uses a [ResourceProvisionerSimple]
    /// for RAM and Bandwidth and also sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param ram the RAM capacity in Megabytes
    /// @param bw the Bandwidth (BW) capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    ///
    /// @see HostSimple#HostSimple(long, long, HarddriveStorage, CoreCapacity)
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(final long ram, final long bw, final long storage, final CoreCapacity capacity) {
        super(ram, bw, storage, capacity);
    }

    /// Creates and instantaneously powers on a Host without a pre-defined ID.
    /// It uses a [ResourceProvisionerSimple]
    /// for RAM and Bandwidth and also sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param ram the RAM capacity in Megabytes
    /// @param bw the Bandwidth (BW) capacity in Megabits/s
    /// @param storage the storage device for the Host
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    ///
    /// @see HostSimple#HostSimple(long, long, long, CoreCapacity)
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(
        final long ram, final long bw,
        final HarddriveStorage storage, final CoreCapacity capacity)
    {
        super(ram, bw, storage, capacity);
    }

    /// Creates a Host without a pre-defined ID. It uses a [ResourceProvisionerSimple]
    /// for RAM and Bandwidth and also sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param ram the RAM capacity in Megabytes
    /// @param bw the Bandwidth (BW) capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param capacity the number of CPU cores of the Host and the MIPS capacity of each core
    /// @param activate true to power the Host on, false to power off (see [#setStartupDelay(double)])
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    public HostSimple(
        final long ram, final long bw, final long storage,
        final CoreCapacity capacity, final boolean activate)
    {
        super(ram, bw, new HarddriveStorage(storage), capacity, activate);
    }

    @Override
    public String toString() {
        final char dist = datacenter.getCharacteristics().getDistribution().symbol();
        final String dc =
                datacenter == null || Datacenter.NULL.equals(datacenter) ? "" :
                "/%cDC %d".formatted(dist, datacenter.getId());
        return "Host %d%s".formatted(getId(), dc);
    }

    @Override
    public int compareTo(final Host other) {
        if(this.equals(other)) {
            return 0;
        }

        return Long.compare(this.id, other.getId());
    }
}
