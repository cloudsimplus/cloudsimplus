/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A Central Unit Processing (CPU) attached to a {@link Vm} and which can have multiple
 * cores ({@link Pe}s). It's a also called a Virtual CPU (vCPU).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public final class Processor extends ResourceManageableAbstract {
    public static final Processor NULL = new Processor();

    private Vm vm;

    /** @see #getMips() */
    private double mips;

    /** @see #getCloudletExecList() */
    private List<CloudletExecutionInfo> cloudletExecList;

    /**
     * Instantiates a Processor with zero capacity (zero PEs).
     */
    public Processor(){
        this(Vm.NULL, 0, 0);
    }

    /**
     * Instantiates a Processor.
     *
     * @param vm the {@link Vm} the processor will belong to
     * @param pesMips MIPS of each {@link Pe}
     * @param numberOfPes number of {@link Pe}s
     */
    public Processor(Vm vm, double pesMips, long numberOfPes) {
        super(numberOfPes);
        this.vm = vm;
        cloudletExecList = new ArrayList<>();
        setMips(pesMips);
    }

    /**
     * Instantiates a new Processor from a given MIPS list,
     * ignoring all elements having zero capacity.
     *
     * @param vm the {@link Vm} the processor will belong to
     * @param mipsList a list of {@link Pe Processing Elements (cores)} capacity
     * where all elements have the same capacity. This list represents
     * the capacity of each processor core.
     * @param cloudletExecList list of cloudlets currently executing in this processor.
     *
     * @return the new Processor
     */
    public static Processor fromMipsList(Vm vm, List<Double> mipsList,
                                         List<CloudletExecutionInfo> cloudletExecList) {
        if(Objects.isNull(mipsList)){
            throw new IllegalArgumentException("The mipsList cannot be null.");
        }

        mipsList = getNonZeroMipsElements(mipsList);

        double peMips = 0;
        if(!mipsList.isEmpty()){
            peMips = mipsList.get(0);

            if(mipsList.stream().distinct().count() > 1){
                throw new IllegalArgumentException(
                    String.format(
                        "mipsShare list doesn't have all elements with %.2f MIPS",
                        peMips));
            }
        }

        Processor p = new Processor(vm, peMips, mipsList.size());
        p.cloudletExecList = cloudletExecList;
        return p;
    }

    /**
     * Instantiates a new Processor from a given MIPS list,
     * ignoring all elements having zero capacity.
     *
     * @param vm the {@link Vm} the processor will belong to
     * @param mipsList a list of {@link Pe Processing Elements (cores)} capacity
     * where all elements have the same capacity. This list represents
     * the capacity of each processor core.
     *
     * @return the new Processor
     */
    public static Processor fromMipsList(Vm vm, List<Double> mipsList) {
        return Processor.fromMipsList(vm, mipsList, Collections.EMPTY_LIST);
    }

    private static List<Double> getNonZeroMipsElements(List<Double> mipsList) {
        return mipsList.stream().filter(mips -> mips > 0).collect(Collectors.toList());
    }

    /**
     * Gets the sum of MIPS from all {@link Pe}s.
     * @return
     */
    public double getTotalMips(){
        return getMips()* getCapacity();
    }

    /**
     * Gets the individual MIPS of each {@link Pe}.
     * @return
     */
    public double getMips() {
        return mips;
    }

    /**
     * Sets the individual MIPS of each {@link Pe}.
     * @param newMips the new MIPS of each PE
     * @pre newMips >= 0
     */
    public final void setMips(double newMips) {
        if(newMips < 0) {
            throw new IllegalArgumentException("MIPS cannot be negative");
        }

        this.mips = newMips;
    }

    /**
     * Gets the number of {@link Pe}s of the Processor
     * @return
     */
    @Override
    public long getCapacity() {
        return super.getCapacity();
    }

    /**
     * Gets the number of free PEs.
     * @return
     */
    @Override
    public long getAvailableResource() {
        return vm.getCloudletScheduler().getFreePes();
    }

    /**
     * Gets the number of used PEs.
     * @return
     */
    @Override
    public long getAllocatedResource() {
        return vm.getCloudletScheduler().getUsedPes();
    }

    @Override
    public double getPercentUtilization() {
        return vm.getCloudletScheduler().getRequestedCpuPercentUtilization(vm.getSimulation().clock());
    }

    /**
     * Sets the number of {@link Pe}s of the Processor
     * @param numberOfPes the number of PEs to set
     * @return
     */
    @Override
    public final boolean setCapacity(long numberOfPes) {
        if(numberOfPes < 0){
            throw new IllegalArgumentException("The Processsor's number of PEs cannot be negative.");
        }
        return super.setCapacity(numberOfPes);
    }

    /**
     * Gets the amount of MIPS available (free) for each Processor PE,
     * considering the currently executing cloudlets in this processor
     * and the number of PEs these cloudlets require.
     * This is the amount of MIPS that each Cloudlet is allowed to used,
     * considering that the processor is shared among all executing
     * cloudlets.
     *
     * <p>In the case of space shared schedulers,
     * there is no concurrency for PEs because some cloudlets
     * may wait in a queue until there is available PEs to be used
     * exclusively by them.</p>
     *
     * @return the amount of available MIPS for each Processor PE.
     * @TODO Splitting the capacity of a CPU core among different applications
     * is not in fact possible. This was just an oversimplification
     * performed by the CloudletSchedulerTimeShared that may affect
     * other schedulers such as the CloudletSchedulerCompletelyFair
     * that in fact performs tasks preemption.
     */
    public double getAvailableMipsByPe(){
        final long totalPesOfAllExecCloudlets = totalPesOfAllExecCloudlets();
        if(totalPesOfAllExecCloudlets > getCapacity()) {
            return getTotalMips() / totalPesOfAllExecCloudlets;
        }

        return getMips();
    }

    /**
     * Gets the total number of PEs of all cloudlets currently executing in this processor.
     * @return
     */
    private long totalPesOfAllExecCloudlets() {
        return cloudletExecList.stream()
            .map(CloudletExecutionInfo::getCloudlet)
            .mapToLong(Cloudlet::getNumberOfPes).sum();
    }

    /**
     * Gets a read-only list of cloudlets currently executing in this processor.
     * @return
     */
    public List<CloudletExecutionInfo> getCloudletExecList() {
        return Collections.unmodifiableList(cloudletExecList);
    }

    /**
     * Gets the {@link Vm} the processor belongs to.
     * @return
     */
    public Vm getVm() {
        return vm;
    }

    @Override
    public boolean allocateResource(long amountToAllocate) {
        throw new UnsupportedOperationException("The allocateResource method is not supported for the Processor because this is controlled by the CloudletScheduler.");
    }

    @Override
    public long deallocateAllResources() {
        throw new UnsupportedOperationException("The deallocateAllResources method is not supported for the Processor  because this is controlled by the CloudletScheduler.");
    }
}
