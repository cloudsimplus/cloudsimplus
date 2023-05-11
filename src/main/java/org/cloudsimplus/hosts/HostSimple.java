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

import static java.util.Objects.requireNonNull;

/**
 * A Host class that implements the most basic features of a Physical Machine
 * (PM) inside a {@link Datacenter}. It executes actions related to management
 * of virtual machines (e.g., creation and destruction). A host has a defined
 * policy for provisioning memory and bw, as well as an allocation policy for
 * PEs to {@link Vm Virtual Machines}. A host is associated to a Datacenter and
 * can host virtual machines.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class HostSimple extends HostAbstract {

    /**
     * Creates and powers on a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage.
     * It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param peList the host's {@link Pe} list
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList) {
        super(peList);
    }

    /**
     * Creates a Host without a pre-defined ID,
     * 10GB of RAM, 1000Mbps of Bandwidth and 500GB of Storage
     * and enabling the host to be powered on or not.
     *
     * <p>It creates a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth. Finally, it sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.</p>
     *
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     * @see #setDefaultRamCapacity(long)
     * @see #setDefaultBwCapacity(long)
     * @see #setDefaultStorageCapacity(long)
     */
    public HostSimple(final List<Pe> peList, final boolean activate) {
        super(peList, activate);
    }

    /**
     * Creates and powers on a Host with the given parameters and a
     * {@link VmSchedulerSpaceShared} as default.
     *
     * @param ramProvisioner the ram provisioner with capacity in Megabytes
     * @param bwProvisioner the bw provisioner with capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's PEs list
     *
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(
        final ResourceProvisioner ramProvisioner,
        final ResourceProvisioner bwProvisioner,
        final long storage,
        final List<Pe> peList)
    {
        super(ramProvisioner, bwProvisioner, storage, peList);
    }

    /**
     * Creates and powers on a Host without a pre-defined ID.
     * It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     *
     * @see HostSimple#HostSimple(long, long, HarddriveStorage, List)
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(final long ram, final long bw, final long storage, final List<Pe> peList) {
        super(ram, bw, storage, peList);
    }

    /**
     * Creates and powers on a Host without a pre-defined ID.
     * It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage device for the Host
     * @param peList the host's {@link Pe} list
     *
     * @see HostSimple#HostSimple(long, long, long, List)
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
    public HostSimple(
        final long ram, final long bw,
        final HarddriveStorage storage, final List<Pe> peList)
    {
        super(ram, bw, storage, peList);
    }

    /**
     * Creates a Host without a pre-defined ID. It uses a {@link ResourceProvisionerSimple}
     * for RAM and Bandwidth and also sets a {@link VmSchedulerSpaceShared} as default.
     * The ID is automatically set when a List of Hosts is attached
     * to a {@link Datacenter}.
     *
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     * @param activate define the Host activation status: true to power on, false to power off
     *
     * @see ChangeableId#setId(long)
     * @see #setRamProvisioner(ResourceProvisioner)
     * @see #setBwProvisioner(ResourceProvisioner)
     * @see #setVmScheduler(VmScheduler)
     */
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
        if(this.equals(requireNonNull(other))) {
            return 0;
        }

        return Long.compare(this.id, other.getId());
    }
}
