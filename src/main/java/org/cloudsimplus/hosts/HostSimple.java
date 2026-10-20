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
import org.cloudsimplus.provisioners.ResourceProvisionerSimple;
import org.cloudsimplus.resources.HarddriveStorage;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.vms.Vm;

import java.util.List;

/// A Host class that implements the most basic features of a Physical Machine
/// (PM) inside a [Datacenter]. It executes actions related to the management
/// of virtual machines (e.g., creation and destruction). A Host has a defined
/// policy for provisioning memory and bw, as well as an allocation policy for
/// PEs to [Virtual Machines][Vm]. A Host is associated with a Datacenter and
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
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    /// @see #setStartupDelay(double)
    public HostSimple(final long pesNumber, final double mips) {
        super(pesNumber, mips);
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
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    /// @param activate true to power the Host on, false to power off (see [#setStartupDelay(double)])
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setDefaultRamCapacity(long)
    /// @see #setDefaultBwCapacity(long)
    /// @see #setDefaultStorageCapacity(long)
    public HostSimple(final long pesNumber, final double mips, final boolean activate) {
        super(pesNumber, mips, activate);
    }

    /// Creates and instantaneously powers on a Host with the given parameters and a
    /// [VmSchedulerSpaceShared] as default.
    ///
    /// @param ramProvisioner the ram provisioner with capacity in Megabytes
    /// @param bwProvisioner the bw provisioner with capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    ///
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final long pesNumber, final double mips)
    {
        super(ramProvisioner, bwProvisioner, storage, pesNumber, mips);
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
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    ///
    /// @see HostSimple#HostSimple(long, long, HarddriveStorage, long, double)
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(final long ram, final long bw, final long storage, final long pesNumber, final double mips) {
        super(ram, bw, storage, pesNumber, mips);
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
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    ///
    /// @see HostSimple#HostSimple(long, long, long, long, double)
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    /// @see #setStartupDelay(double)
    public HostSimple(
        final long ram, final long bw,
        final HarddriveStorage storage, final long pesNumber, final double mips)
    {
        super(ram, bw, storage, pesNumber, mips);
    }

    /// Creates a Host without a pre-defined ID. It uses a [ResourceProvisionerSimple]
    /// for RAM and Bandwidth and also sets a [VmSchedulerSpaceShared] as default.
    /// The ID is automatically assigned when a List of Hosts is attached
    /// to a [Datacenter].
    ///
    /// @param ram the RAM capacity in Megabytes
    /// @param bw the Bandwidth (BW) capacity in Megabits/s
    /// @param storage the storage capacity in Megabytes
    /// @param pesNumber the number of PEs (CPU cores) of the Host
    /// @param mips the MIPS capacity of each PE
    /// @param activate true to power the Host on, false to power off (see [#setStartupDelay(double)])
    ///
    /// @see ChangeableId#setId(long)
    /// @see #setRamProvisioner(ResourceProvisioner)
    /// @see #setBwProvisioner(ResourceProvisioner)
    /// @see #setVmScheduler(VmScheduler)
    public HostSimple(
        final long ram, final long bw, final long storage,
        final long pesNumber, final double mips, final boolean activate)
    {
        super(ram, bw, new HarddriveStorage(storage), pesNumber, mips, activate);
    }

    /// @deprecated Use [#HostSimple(long, double)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(final List<Pe> peList) {
        super(peList);
    }

    /// @deprecated Use [#HostSimple(long, double, boolean)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(final List<Pe> peList, final boolean activate) {
        super(peList, activate);
    }

    /// @deprecated Use [#HostSimple(ResourceProvisioner, ResourceProvisioner, long, long, double)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final List<Pe> peList)
    {
        super(ramProvisioner, bwProvisioner, storage, peList);
    }

    /// @deprecated Use [#HostSimple(long, long, long, long, double)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(final long ram, final long bw, final long storage, final List<Pe> peList) {
        super(ram, bw, storage, peList);
    }

    /// @deprecated Use [#HostSimple(long, long, HarddriveStorage, long, double)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(
        final long ram, final long bw,
        final HarddriveStorage storage, final List<Pe> peList)
    {
        super(ram, bw, storage, peList);
    }

    /// @deprecated Use [#HostSimple(long, long, long, long, double, boolean)] instead.
    @Deprecated(forRemoval = true)
    public HostSimple(
        final long ram, final long bw, final long storage,
        final List<Pe> peList, final boolean activate)
    {
        super(ram, bw, new HarddriveStorage(storage), peList, activate);
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
