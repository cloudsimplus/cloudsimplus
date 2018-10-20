/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletTestUtil;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletToVmMappingSolutionTest {
    @Test
    public void testGetFitness() {
        final int PES = 2;
        final int NUMBER_OF_CLOUDLETS = 3;
        final CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);

        final double expResult = 1.0/3.0;
        final double result = instance.getFitness();
        assertEquals(
            expResult, result, 0.01,
            String.format("Fitness is not as expected for the cost %.2f", instance.getCost()));
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(
        final int numberOfCloudlets, final int cloudletAndVmPes) {
        return createSolutionWithOneVmForEachCloudlet(
                numberOfCloudlets, cloudletAndVmPes, cloudletAndVmPes);
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(
        final int numberOfCloudlets, final int cloudletPes, final int vmPes) {
        final int VM_MIPS = 1000;
        final int CLOUDLET_LEN = 10000;
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);

        for (int i = 0; i < numberOfCloudlets; i++) {
            final Vm vm = VmTestUtil.createVm(i, VM_MIPS, vmPes);

            final long len = (long) (CLOUDLET_LEN * (i + 1));
            final Cloudlet cloudlet = CloudletTestUtil.createCloudlet(i, len, cloudletPes);
            instance.bindCloudletToVm(cloudlet, vm);
        }

        return instance;
    }

    private Vm[] createVms(final int numberOfVms){
        Vm[] array = new Vm[numberOfVms];
        for(int i = 0; i < numberOfVms; i++){
            array[i] = VmTestUtil.createVm(i, 1);
        }
        return array;
    }

    @Test
    public void testCompareToWhenInstanceIsGreater() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        final CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES);

        final HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES/2);
        final int expResult = 1;
        final int result = instance.compareTo(o);
        final String msg = String.format(
            "The instance was expected to be greater than the compared object. Instance fitness: %f Compared object fitness: %f",
            instance.getFitness(), o.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test
    public void testCompareToWhenInstanceIsEquals() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        final CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);
        final HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);
        final int expResult = 0;
        final int result = instance.compareTo(o);
        final String msg = String.format(
            "The instances should be equals. Instance fitness: %f Compared object fitness: %f",
            instance.getFitness(), o.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test
    public void testCompareToWhenInstanceIsLower() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        final CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES/2);
        final HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES);
        final int expResult = -1;
        final int result = instance.compareTo(o);
        final String msg = String.format(
            "The instance was expected to be lower than the compared object. Instance fitness: %.2f Compared object fitness: %.2f",
            instance.getFitness(), o.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test()
    public void testGetCloudletVmMapWhenModifyReadonlyMap() {
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        final Map<Cloudlet, Vm> result = instance.getResult();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> result.put(Cloudlet.NULL, Vm.NULL));
    }

    @Test
    public void testGetCloudletVmMapWhenNotNullMap() {
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        assertNotNull(instance.getResult());
    }

    @Test
    public void testSwapVmsOfTwoMapEntries() {
        final int numberOfEntries = 2;
        final Cloudlet[] cloudlets = new Cloudlet[numberOfEntries];

        for(int i = 1; i <= numberOfEntries; i++){
            cloudlets[i-1] = CloudletTestUtil.createCloudlet(i, i*1000, i);
        }

        final Vm[] vms = createVms(numberOfEntries);

        final List<Map.Entry<Cloudlet, Vm>> originalEntries = new ArrayList<>(numberOfEntries);
        final List<Map.Entry<Cloudlet, Vm>> swappedVmsEntries = new ArrayList<>(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            originalEntries.add(new AbstractMap.SimpleEntry<>(cloudlets[i], vms[i]));

            /*After swapping VMs that are hosting the given cloudlets,
            cloudlet 0 that was in VM 0 will be placed into VM 1,
            and cloudlet 1 that was in VM 1 will be placed into VM 0.
            The line below makes this change in the index of the VM
            to swap.*/
            final int swappedVmIndex = (i + 1) % numberOfEntries;
            swappedVmsEntries.add(new AbstractMap.SimpleEntry<>(cloudlets[i], vms[swappedVmIndex])) ;
        }

        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        instance.swapVmsOfTwoMapEntries(originalEntries);

        final String msg = String.format(
            "The VMs of the given cloudlets were not swapped. It was expected the cloudlet %d to move to VM %d and cloudlet %d to move to VM %d.",
            swappedVmsEntries.get(0).getKey().getId(),
            swappedVmsEntries.get(0).getValue().getId(),
            swappedVmsEntries.get(1).getKey().getId(),
            swappedVmsEntries.get(1).getValue().getId());
        assertEquals(swappedVmsEntries, originalEntries, msg);
    }
}
