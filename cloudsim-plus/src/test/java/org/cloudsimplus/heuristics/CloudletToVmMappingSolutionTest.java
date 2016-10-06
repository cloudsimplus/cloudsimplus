package org.cloudsimplus.heuristics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.CloudletSimpleTest;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimpleTest;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.junit.Assert;
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
        CloudletToVmMappingSolution instance =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);

        double expResult = 1.0/3.0;
        double result = instance.getFitness();
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
        CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);

        IntStream.range(0, numberOfCloudlets).forEach(i -> {
            Vm vm = VmSimpleTest.createVm(i, VM_MIPS, vmPes);

            long len = (long)(CLOUDLET_LEN*(i+1));
            Cloudlet cloudlet = CloudletSimpleTest.createCloudlet(i, len, cloudletPes);
            instance.bindCloudletToVm(cloudlet, vm);
        });

        return instance;
    }

    private Cloudlet createCloudlet(int id, int numberOfPes){
        Cloudlet cloudlet = new CloudletSimple(id, 10000, numberOfPes, 100, 100,
                UtilizationModel.NULL, UtilizationModel.NULL, UtilizationModel.NULL);
        cloudlet.setUserId(0);
        return cloudlet;
    }

    /**
     * Creates a set of cloudlets
     * @param numberOfCloudlets the number of cloudlets to create
     * @param initialId the id of the first cloudlet to be created, that will
     * be incremented for each next created cloudlet
     * @return the cloudlet list
     */
    private Set<Cloudlet> createCloudlets(int numberOfCloudlets, int initialId){
        Set<Cloudlet> set = new TreeSet<>();
        IntStream.range(0, numberOfCloudlets).forEach(i->set.add(createCloudlet(initialId+i, 1)));
        return set;
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
        HeuristicSolution o =
                createSolutionWithOneVmForEachCloudlet(NUMBER_OF_CLOUDLETS, PES, PES+1);
        int expResult = 0;
        int result = instance.compareTo(o);
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
        int expResult = -1;
        int result = instance.compareTo(o);
        assertEquals(
            String.format(
                "The instance was expected to be lower than the compared object. Instance fitness: %.2f Compared object fitness: %.2f",
                instance.getFitness(), o.getFitness()),
            expResult, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCloudletVmMap_TryToModifyReadonlyMap() {
        CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        Map<Cloudlet, Vm> result = instance.getResult();
        result.put(Cloudlet.NULL, Vm.NULL);
    }

    @Test
    public void testGetCloudletVmMap_NotNullMap() {
        CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        assertNotNull(instance.getResult());
    }

    @Test
    public void testCreateNeighbor_NotSameObject() {
        CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        CloudletToVmMappingSolution result = instance.createNeighbor();
        assertNotSame(
            "The neighbor solution must be a modified clone of the given instance, not the same object.",
            instance, result);
    }

    @Test
    public void testCreateNeighbor_NotEmpty() {
        CloudletToVmMappingSolution instance = createSolutionWithOneVmForEachCloudlet(2, 2);
        CloudletToVmMappingSolution result = instance.createNeighbor();
        assertFalse(
            "The cloned (neighbor) solution cannot be empty.",
            result.getResult().isEmpty());
    }

    @Test
    public void testCreateNeighbor_SameSize() {
        CloudletToVmMappingSolution instance = createSolutionWithOneVmForEachCloudlet(2, 2);
        CloudletToVmMappingSolution result = instance.createNeighbor();
        assertEquals(
            "The cloned (neighbor) solution has to have the same number of elements of the original solution.",
            instance.getResult().size(), result.getResult().size());
    }

    @Test
    public void testSwapVmsOfTwoMapEntries() {
        final int numberOfEntries = 2;
        Map.Entry<Cloudlet, Vm> originalEntries[] = new Map.Entry[numberOfEntries];
        Map.Entry<Cloudlet, Vm> swapedVmsEntries[] = new Map.Entry[numberOfEntries];
        Cloudlet[] cloudlets = new Cloudlet[numberOfEntries];
        for(int i = 0; i < numberOfEntries; i++){
            cloudlets[i] = CloudletSimpleTest.createCloudlet(i, i*1000, i);
        }

        Vm[] vms = createVms(numberOfEntries);

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

        CloudletToVmMappingSolution instance = new CloudletToVmMappingSolution(Heuristic.NULL);
        instance.swapVmsOfTwoMapEntries(originalEntries);

        Assert.assertArrayEquals(
            String.format(
                "The VMs of the given cloudlets were not swapped. It was expected the cloudlet %d to move to VM %d and cloudlet %d to move to VM %d.",
                swapedVmsEntries[0].getKey().getId(),
                swapedVmsEntries[0].getValue().getId(),
                swapedVmsEntries[1].getKey().getId(),
                swapedVmsEntries[1].getValue().getId()),
            swapedVmsEntries, originalEntries);
    }

}
