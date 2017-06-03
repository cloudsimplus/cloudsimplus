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
package org.cloudsimplus.heuristics;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletSimpleTest;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.junit.Test;
import static org.junit.Assert.*;

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
            String.format("Fitness is not as expected for the cost %.2f", instance.getCost()),
            expResult, result, 0.01);
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(
            int numberOfCloudlets, int cloudletAndVmPes) {
        return createSolutionWithOneVmForEachCloudlet(
                numberOfCloudlets, cloudletAndVmPes, cloudletAndVmPes);
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(
            int numberOfCloudlets, int cloudletPes, int vmPes) {
        final int VM_MIPS = 1000;
        final int CLOUDLET_LEN = 10000;
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);

        IntStream.range(0, numberOfCloudlets).forEach(i -> {
            final Vm vm = VmSimpleTest.createVm(i, VM_MIPS, vmPes);

            final long len = (long)(CLOUDLET_LEN*(i+1));
            final Cloudlet cloudlet = CloudletSimpleTest.createCloudlet(i, len, cloudletPes);
            instance.bindCloudletToVm(cloudlet, vm);
        });

        return instance;
    }

    private Cloudlet createCloudlet(int id, int numberOfPes){
        return new CloudletSimple(id, 10000, numberOfPes)
          .setFileSize(100)
          .setOutputSize(100);
    }

    private Vm[] createVms(int numberOfVms){
        Vm[] array = new Vm[numberOfVms];
        for(int i = 0; i < numberOfVms; i++){
            array[i] = VmSimpleTest.createVm(i, 1);
        }
        return array;
    }

    @Test
    public void testCompareTo_InstanceIsGreater() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES);

        HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES/2);
        int expResult = 1;
        int result = instance.compareTo(o);
        assertEquals(
            String.format(
                "The instance was expected to be greater than the compared object. Instance fitness: %f Compared object fitness: %f",
                instance.getFitness(), o.getFitness()),
            expResult, result);
    }

    @Test
    public void testCompareTo_InstanceIsEquals() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);
        final HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);
        final int expResult = 0;
        final int result = instance.compareTo(o);
        assertEquals(
            String.format(
                "The instances should be equals. Instance fitness: %f Compared object fitness: %f",
                instance.getFitness(), o.getFitness()),
        expResult, result);
    }

    @Test
    public void testCompareTo_InstanceIsLower() {
        final int NUMBER_OF_CLOUDLETS = 3;
        final int PES = 2;
        CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES/2);
        HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES);
        final int expResult = -1;
        final int result = instance.compareTo(o);
        assertEquals(
            String.format(
                "The instance was expected to be lower than the compared object. Instance fitness: %.2f Compared object fitness: %.2f",
                instance.getFitness(), o.getFitness()),
            expResult, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCloudletVmMap_TryToModifyReadonlyMap() {
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        final Map<Cloudlet, Vm> result = instance.getResult();
        result.put(Cloudlet.NULL, Vm.NULL);
    }

    @Test
    public void testGetCloudletVmMap_NotNullMap() {
        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        assertNotNull(instance.getResult());
    }

    @Test
    public void testSwapVmsOfTwoMapEntries() {
        final int numberOfEntries = 2;
        final Map.Entry<Cloudlet, Vm> originalEntries[] = new Map.Entry[numberOfEntries];
        final Map.Entry<Cloudlet, Vm> swapedVmsEntries[] = new Map.Entry[numberOfEntries];
        final Cloudlet[] cloudlets = new Cloudlet[numberOfEntries];
        for(int i = 1; i <= numberOfEntries; i++){
            cloudlets[i-1] = CloudletSimpleTest.createCloudlet(i, i*1000, i);
        }

        final Vm[] vms = createVms(numberOfEntries);

        IntStream.range(0, numberOfEntries).forEach(i->{
            originalEntries[i] = new HashMap.SimpleEntry(cloudlets[i], vms[i]);
            /*After swapping VMs that are hosting given cloudlets,
            cloudlet 0 that was in VM 0 will be placed into VM 1,
            and cloudlet 1 that was in VM 1 will be placed into VM 0.
            The line below makes this change in the index of the VM
            to swap.*/
            int swapedVmIndex = (i+1)%numberOfEntries;
            swapedVmsEntries[i] = new HashMap.SimpleEntry(cloudlets[i], vms[swapedVmIndex]);
        });

        final CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        instance.swapVmsOfTwoMapEntries(originalEntries);

        assertArrayEquals(
            String.format(
                "The VMs of the given cloudlets were not swapped. It was expected the cloudlet %d to move to VM %d and cloudlet %d to move to VM %d.",
                swapedVmsEntries[0].getKey().getId(),
                swapedVmsEntries[0].getValue().getId(),
                swapedVmsEntries[1].getKey().getId(),
                swapedVmsEntries[1].getValue().getId()),
            swapedVmsEntries, originalEntries);
    }
}
