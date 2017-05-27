/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.lists;

import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * VmList is a collection of operations on lists of VMs.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public final class VmList {

    /**
     * A private constructor to avoid instantiating the class.
     */
    private VmList(){}

    /**
     * Gets a {@link Vm} with a given id.
     *
     * @param <T> the class of VMs inside the list
     * @param id ID of required VM
     * @param vmList list of existing VMs
     * @return a Vm with the given ID or {@link Vm#NULL} if not found
     * @pre $none
     * @post $none
     *
     * @TODO It may be considered the use of a HashMap in order to improve VM
     * search, instead of a List. The map key can be the vm id and the value the
     * VM itself. However, it has to be assessed the feasibility to have VMs
     * with the same ID and the need to find VMs by its id and user id, as in
     * the method {@link #getByIdAndUserId(java.util.List, int, int)}. The first
     * concern could be dealt by ensuring that all VMs have different ID (in
     * fact, I don't know if VM id uniqueness is a CloudSim requirement) and
     * creating a map by VM id. The second concern could be dealt by creating a
     * Map<UserID, List<VmIDs>>. The third concern is, that changing the
     * class of these lists may have a potential effect on the entire project
     * and in the creation of simulations that has to be priorly assessed.
     */
    public static <T extends Vm> T getById(List<T> vmList, int id) {
        return vmList.stream().filter(vm -> vm.getId() == id).findFirst().orElse((T)Vm.NULL);
    }

    /**
     * Gets a {@link Vm} with a given id and owned by a given user.
     *
     * @param <T> The generic type
     * @param vmList list of existing VMs
     * @param id ID of required VM
     * @param userId the user ID of the VM's owner
     * @return VmSimple with the given ID, $null if not found
     * @pre $none
     * @post $none
     */
    public static <T extends Vm> T getByIdAndUserId(List<T> vmList, int id, int userId) {
        return vmList.stream()
            .filter(vm -> vm.getId() == id && vm.getBroker().getId() == userId)
            .findFirst().orElse((T)Vm.NULL);
    }

    /**
     * Sort a given list of VMs by descending order of CPU utilization.
     *
     * @param vmList the vm list to be sorted
     * @param currentSimulationTime the current simulation time to get the current CPU utilization for each Vm
     */
    public static void sortByCpuUtilization(List<? extends Vm> vmList, double currentSimulationTime) {
        Comparator<Vm> comparator =
            Comparator.comparingDouble(vm -> vm.getTotalCpuMipsUsage(currentSimulationTime));
        vmList.sort(comparator.reversed());
    }


}
