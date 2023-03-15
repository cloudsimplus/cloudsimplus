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
package org.cloudsimplus.heuristics;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletTestUtil;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletToVmMappingSolutionTest {
    @Test
    public void testGetFitness() {
        final int PES = 2;
        final int CLOUDLETS = 3;
        final var solution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES, PES+1);

        final double expResult = 1.0/3.0;
        final double result = solution.getFitness();
        assertEquals(
            expResult, result, 0.01,
            "Fitness is not as expected for the cost %.2f".formatted(solution.getCost()));
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(final int cloudlets, final int cloudletAndVmPes) {
        return createSolutionWithOneVmForEachCloudlet(cloudlets, cloudletAndVmPes, cloudletAndVmPes);
    }

    private CloudletToVmMappingSolution createSolutionWithOneVmForEachCloudlet(
        final int cloudlets, final int cloudletPes, final int vmPes) {
        final int VM_MIPS = 1000;
        final long CLOUDLET_LEN = 10000;
        final var solution = new CloudletToVmMappingSolution(Heuristic.NULL);

        for (int i = 0; i < cloudlets; i++) {
            final Vm vm = VmTestUtil.createVm(i, VM_MIPS, vmPes);

            final long len = CLOUDLET_LEN * (i + 1);
            final var cloudlet = CloudletTestUtil.createCloudlet(i, len, cloudletPes);
            solution.bindCloudletToVm(cloudlet, vm);
        }

        return solution;
    }

    private Vm[] createVms(final int vmsNumber){
        final Vm[] array = new Vm[vmsNumber];
        for(int i = 0; i < vmsNumber; i++){
            array[i] = VmTestUtil.createVm(i, 1);
        }

        return array;
    }

    @Test
    public void testCompareToWhenInstanceIsGreater() {
        final int CLOUDLETS = 3;
        final int PES = 2;
        final var oneSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES);
        final var otherSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES, PES/2);
        final int expResult = 1;
        final int result = oneSolution.compareTo(otherSolution);
        final String msg =
            "The instance was expected to be greater than the compared object. Instance fitness: %f Compared object fitness: %f"
            .formatted(oneSolution.getFitness(), otherSolution.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test
    public void testCompareToWhenInstanceIsEquals() {
        final int CLOUDLETS = 3;
        final int PES = 2;
        final var oneSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES, PES+1);
        final var otherSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES, PES+1);
        final int expResult = 0;
        final int result = oneSolution.compareTo(otherSolution);
        final String msg =
            "The instances should be equals. Instance fitness: %f Compared object fitness: %f"
            .formatted(oneSolution.getFitness(), otherSolution.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test
    public void testCompareToWhenInstanceIsLower() {
        final int CLOUDLETS = 3;
        final int PES = 2;
        final var oneSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES, PES/2);
        final var otherSolution = createSolutionWithOneVmForEachCloudlet(CLOUDLETS, PES);
        final int expResult = -1;
        final int result = oneSolution.compareTo(otherSolution);
        final String msg =
            "The instance was expected to be lower than the compared object. Instance fitness: %.2f Compared object fitness: %.2f"
            .formatted(oneSolution.getFitness(), otherSolution.getFitness());
        assertEquals(expResult, result, msg);
    }

    @Test()
    public void testGetCloudletVmMapWhenModifyReadonlyMap() {
        final var solution = new CloudletToVmMappingSolution(Heuristic.NULL);
        final Map<Cloudlet, Vm> result = solution.getResult();
        assertThrows(UnsupportedOperationException.class, () -> result.put(Cloudlet.NULL, Vm.NULL));
    }

    @Test
    public void testGetCloudletVmMapWhenNotNullMap() {
        final var solution = new CloudletToVmMappingSolution(Heuristic.NULL);
        assertNotNull(solution.getResult());
    }

    @Test
    public void testSwapVmsOfTwoMapEntries() {
        final int entries = 2;
        final Cloudlet[] cloudlets = new Cloudlet[entries];

        for(int i = 1; i <= entries; i++){
            cloudlets[i-1] = CloudletTestUtil.createCloudlet(i, i*1000, i);
        }

        final Vm[] vms = createVms(entries);

        final var originalEntries   = new ArrayList<Map.Entry<Cloudlet, Vm>>(entries);
        final var swappedVmsEntries = new ArrayList<Map.Entry<Cloudlet, Vm>>(entries);
        for (int i = 0; i < entries; i++) {
            originalEntries.add(new SimpleEntry<>(cloudlets[i], vms[i]));

            /*After swapping VMs that are hosting the given cloudlets,
            cloudlet 0 that was in VM 0 will be placed into VM 1,
            and cloudlet 1 that was in VM 1 will be placed into VM 0.
            The line below makes this change in the index of the VM
            to swap.*/
            final int swappedVmIndex = (i + 1) % entries;
            swappedVmsEntries.add(new SimpleEntry<>(cloudlets[i], vms[swappedVmIndex])) ;
        }

        final var solution = new CloudletToVmMappingSolution(Heuristic.NULL);
        solution.swapVmsOfTwoMapEntries(originalEntries);

        final String msg =
            "The VMs of the given cloudlets were not swapped. It was expected the cloudlet %d to move to VM %d and cloudlet %d to move to VM %d."
            .formatted(
                swappedVmsEntries.get(0).getKey().getId(),
                swappedVmsEntries.get(0).getValue().getId(),
                swappedVmsEntries.get(1).getKey().getId(),
                swappedVmsEntries.get(1).getValue().getId());
        assertEquals(swappedVmsEntries, originalEntries, msg);
    }
}
