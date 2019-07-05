/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Represents a List of VMs that form a group,
 * so that should be placed together at the same,
 * according to resource availability.
 * This way, such an object is not an actual {@link Vm},
 * but a mock used to check if all the resources
 * required by all VMs inside the group are available at a
 * single Host.
 *
 * <p>It assumes all VMs belong to the same {@link DatacenterBroker}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 */
public class VmGroup extends VmSimple {
    private final List<Vm> vmList;

    /**
     * Creates a VmGroup for a List of VMs.
     * @param vmList the List of VMs to create the group
     */
    public VmGroup(final List<Vm> vmList) {
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
     * All VMs will be changed to the given timezone.
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
     * All VMs will be changed to the given timezone.
     * @param id the VmGroup ID
     * @param vmList the List of VMs to create the group
     * @param timeZone the timezone of the Datacenter where it's expected the VMs to be placed
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
     * Gets the max MIPS capacity a VM inside the List is requiring.
     * @param vmList the List of VMs to create the group
     * @return
     */
    private static double getMaxMips(final List<Vm> vmList){
        return requireNonNull(vmList).stream().mapToDouble(Vm::getMips).max().orElse(0.0);
    }

    /**
     * Gets the total number from all VMs inside the List.
     */
    private static long getTotalPes(final List<Vm> vmList){
        return vmList.stream().mapToLong(Vm::getNumberOfPes).sum();
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
     * Gets the List of VMs belonging to this group.
     * @return
     */
    public List<Vm> getVmList() {
        return vmList;
    }

    /**
     * Gets the number of VMs in this group.
     * @return
     */
    public int size(){
        return vmList.size();
    }

    @Override
    public double updateProcessing(final double currentTime, final List<Double> mipsShare) {
        //The given mipsShare is ignore because we need to get the mipsShare for each VM inside the group
        double minNextEventDelay = Double.MAX_VALUE;
        for (final Vm vm : vmList) {
            final double nextEventDelay = vm.updateProcessing(currentTime, vm.getHost().getVmScheduler().getAllocatedMips(vm));
            minNextEventDelay = Math.min(minNextEventDelay, nextEventDelay);
        }

        return minNextEventDelay;
    }
}
