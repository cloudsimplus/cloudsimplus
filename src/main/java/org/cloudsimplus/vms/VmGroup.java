/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.vms;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;

import java.util.List;

import static java.util.Objects.requireNonNull;

/// Represents a List of [Vm]s that form a group,
/// so that should be placed together at the same,
/// according to resource availability.
/// This way, such an object is not an actual [Vm],
/// but a mock used to check if all the resources
/// required by all VMs inside the group are available at a single [Host].
///
/// It assumes all VMs belong to the same [DatacenterBroker].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.6.0
public class VmGroup extends VmSimple {
    /**
     * The List of VMs belonging to this group.
     */
    @Getter
    private final List<Vm> vmList;

    /**
     * Creates a VmGroup for a List of VMs.
     * @param vmList the List of VMs to create the group
     */
    public VmGroup(@NonNull final List<Vm> vmList) {
        super(getMaxMips(vmList), getTotalPes(vmList));

        this.vmList = vmList;
        this.vmList.forEach(vm -> ((VmSimple)vm).setGroup(this));

        if(vmList.isEmpty()){
            throw new IllegalStateException("The List of VMs belonging to a " + VmGroup.class.getSimpleName() + " cannot be empty.");
        }

        setBroker(vmList.get(0).getBroker());
        setCloudletScheduler(CloudletScheduler.NULL);
        setVmm("None");
        setTotalRam();
        setTotalBw();
        setTotalStorage();
        setTimeZone(Double.MIN_VALUE);
    }

    /**
     * Creates a VmGroup for a List of VMs to be placed
     * at the datacenter closest to a given timezone.
     * All VMs will be changed to the given time zone.
     * @param vmList the List of VMs to create the group
     * @param timeZone the timezone of the Datacenter where it's expected the VMs to be placed
     *                 as close as possible
     * @see DatacenterBroker#setSelectClosestDatacenter(boolean)
     */
    public VmGroup(final List<Vm> vmList, final double timeZone) {
        this(vmList);
        setTimeZone(timeZone);
    }

    /**
     * Creates a VmGroup for a List of VMs to be placed
     * at the datacenter closest to a given timezone.
     * All VMs will be changed to the given time zone.
     * @param id the VmGroup ID
     * @param vmList the List of VMs to create the group
     * @param timeZone the time zone of the Datacenter where it's expected the VMs to be placed
     *                 as close as possible
     * @see DatacenterBroker#setSelectClosestDatacenter(boolean)
     */
    public VmGroup(final long id, final List<Vm> vmList, final double timeZone) {
        this(vmList, timeZone);
        setId(id);
    }

    @Override
    public double getHostCpuUtilization(final double time) {
        return vmList.stream().mapToDouble(vm -> vm.getHostCpuUtilization(time)).sum();
    }

    @Override
    public double getHostRamUtilization() {
        return vmList.stream().mapToDouble(Vm::getHostRamUtilization).sum();
    }

    @Override
    public double getHostBwUtilization() {
        return vmList.stream().mapToDouble(Vm::getHostBwUtilization).sum();
    }

    @Override
    public final Vm setTimeZone(final double timeZone) {
        if(timeZone != Double.MIN_VALUE) {
            super.setTimeZone(timeZone);
            vmList.forEach(vm -> vm.setTimeZone(timeZone));
        }

        return this;
    }

    /**
     * {@return the max MIPS capacity a VM inside the List is requiring}
     * @param vmList the List of VMs to create the group
     */
    private static double getMaxMips(final List<Vm> vmList){
        return requireNonNull(vmList).stream().mapToDouble(Vm::getMips).max().orElse(0.0);
    }

    /**
     * @return the total number of PEs from all VMs inside the List.
     */
    private static long getTotalPes(final List<Vm> vmList){
        return vmList.stream().mapToLong(Vm::getPesNumber).sum();
    }

    /**
     * Sets the RAM capacity of the VmGroup as the total RAM of all VMs.
     */
    private void setTotalRam() {
        final long total = vmList.stream().map(Vm::getRam).mapToLong(Resource::getCapacity).sum();
        setRam(total);
    }

    /**
     * Sets the BW capacity of the VmGroup as the total BW of all VMs.
     */
    private void setTotalBw() {
        final long total = vmList.stream().map(Vm::getBw).mapToLong(Resource::getCapacity).sum();
        setBw(total);
    }

    /**
     * Sets the Storage capacity of the VmGroup as the total Storage of all VMs.
     */
    private void setTotalStorage() {
        final long total = vmList.stream().map(Vm::getStorage).mapToLong(Resource::getCapacity).sum();
        setSize(total);
    }

    /**
     * @return the number of VMs inside this group.
     */
    public int size(){
        return vmList.size();
    }

    @Override
    public double updateProcessing(final double currentTime, final MipsShare mipsShare) {
        // The given mipsShare is ignored because we need to get the mipsShare for each VM inside the group
        double minNextEventDelay = Double.MAX_VALUE;
        for (final Vm vm : vmList) {
            final double nextEventDelay = vm.updateProcessing(currentTime, vm.getHost().getVmScheduler().getAllocatedMips(vm));
            minNextEventDelay = Math.min(minNextEventDelay, nextEventDelay);
        }

        return minNextEventDelay;
    }
}
