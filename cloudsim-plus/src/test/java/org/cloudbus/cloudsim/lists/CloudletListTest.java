package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletListTest {
    private static final int NUMBER_OF_CLOUDLETS = 6;
    private static final int CLOUDLET_LENGTH = 1000;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH_ARRAY[] = {300, 500, 200, 400, 600, 100, 900, 800, 700};
    private List<Cloudlet> simpleCloudletList;
    private List<NetworkCloudlet> networkCloudletList;

    @Before
    public void setUp() {
        this.simpleCloudletList = createSimpleCloudlets();
        this.networkCloudletList = createNetworkCloudlets();
    }

    /**
     * Creates the number of cloudlets defined by the length of the cloudletLengths array
     * @param cloudletLengths the length of each cloudlet to be created
     * @return
     */
    private List<Cloudlet> createSimpleCloudlets(int cloudletLengths[]){
        List<Cloudlet> l = new ArrayList<>(cloudletLengths.length);
        UtilizationModel um = UtilizationModel.NULL;
        for(int i = 0; i < cloudletLengths.length; i++){
            Cloudlet cloudlet =
                new CloudletSimple(i, cloudletLengths[i], CLOUDLET_PES)
                .setCloudletFileSize(100)
                .setCloudletOutputSize(100)
                .setUtilizationModel(um);
            l.add(cloudlet);
        }
        return l;
    }

    private List<Cloudlet> createSimpleCloudlets(){
        int cloudletLengths[] = new int[NUMBER_OF_CLOUDLETS];
        Arrays.fill(cloudletLengths, CLOUDLET_LENGTH);
        return createSimpleCloudlets(cloudletLengths);
    }

    /**
     * Creates the number of cloudlets defined by the length of the cloudletLengths array
     * @param cloudletLengths the length of each cloudlet to be created
     * @return
     */
    private List<NetworkCloudlet> createNetworkCloudlets(int cloudletLengths[]){
        List<NetworkCloudlet> l = new ArrayList<>(cloudletLengths.length);
        UtilizationModel um = UtilizationModel.NULL;
        for(int i = 0; i < cloudletLengths.length; i++){
            NetworkCloudlet cloudlet = new NetworkCloudlet(i, cloudletLengths[i], CLOUDLET_PES);
            cloudlet
                .setMemory(512)
                .setCloudletFileSize(100)
                .setCloudletOutputSize(100)
                .setUtilizationModel(um);
            l.add(cloudlet);
        }
        return l;
    }

    private List<NetworkCloudlet> createNetworkCloudlets(){
        int cloudletLengths[] = new int[NUMBER_OF_CLOUDLETS];
        Arrays.fill(cloudletLengths, CLOUDLET_LENGTH);
        return createNetworkCloudlets(cloudletLengths);
    }

    @Test
    public void testGetByIdSimpleCloudlet() {
        System.out.println("getById");
        int id = 0;
        Cloudlet expResult = simpleCloudletList.get(0);
        Cloudlet result = CloudletList.getById(simpleCloudletList, id);
        assertEquals(expResult, result);
        assertEquals(null, CloudletList.getById(simpleCloudletList, -1));
        assertEquals(null, CloudletList.getById(simpleCloudletList, NUMBER_OF_CLOUDLETS));
    }

    @Test
    public void testGetByIdNetworkCloudlet() {
        System.out.println("getById");
        int id = 0;
        Cloudlet expResult = networkCloudletList.get(0);
        Cloudlet result = CloudletList.getById(networkCloudletList, id);
        assertEquals(expResult, result);
        assertEquals(null, CloudletList.getById(networkCloudletList, -1));
        assertEquals(null, CloudletList.getById(networkCloudletList, NUMBER_OF_CLOUDLETS));
    }

    @Test
    public void testGetPositionByIdSimpleCloudlet() {
        System.out.println("getPositionById");
        int id = NUMBER_OF_CLOUDLETS-1;
        int expResult = id;
        int result = CloudletList.getPositionById(simpleCloudletList, id);
        assertEquals(expResult, result);
        assertEquals(CloudletList.NOT_FOUND_INDEX, CloudletList.getPositionById(simpleCloudletList, -1));
        assertEquals(CloudletList.NOT_FOUND_INDEX, CloudletList.getPositionById(simpleCloudletList, NUMBER_OF_CLOUDLETS));
    }

    @Test
    public void testGetPositionByIdNetworkCloudlet() {
        System.out.println("getPositionById");
        int id = NUMBER_OF_CLOUDLETS-1;
        int expResult = id;
        int result = CloudletList.getPositionById(networkCloudletList, id);
        assertEquals(expResult, result);
        assertEquals(CloudletList.NOT_FOUND_INDEX, CloudletList.getPositionById(networkCloudletList, -1));
        assertEquals(CloudletList.NOT_FOUND_INDEX, CloudletList.getPositionById(networkCloudletList, NUMBER_OF_CLOUDLETS));
    }

    @Test
    public void testSortSimpleCloudlets() {
        System.out.println("sort");
        List<Cloudlet> list = createSimpleCloudlets(CLOUDLET_LENGTH_ARRAY);
        CloudletList.sort(list);
        checkIfCloudletListIsInAccendingTotalLengthOrder(list, CLOUDLET_LENGTH_ARRAY);
    }

    @Test
    public void testSortSimpleCloudletsAlreadySorted() {
        System.out.println("sort");
        final int cloudletLengths[] = {100, 200, 300, 400, 500};
        List<Cloudlet> list = createSimpleCloudlets(cloudletLengths);
        CloudletList.sort(list);
        checkIfCloudletListIsInAccendingTotalLengthOrder(list, cloudletLengths);
    }

    @Test
    public void testSortSimpleCloudletsInvertedSorted() {
        System.out.println("sort");
        final int cloudletLengths[] = {500, 400, 300, 200, 100};
        List<Cloudlet> list = createSimpleCloudlets(cloudletLengths);
        CloudletList.sort(list);
        checkIfCloudletListIsInAccendingTotalLengthOrder(list, cloudletLengths);
    }

    @Test @Ignore("The test is being ignored because there are some issues with getCloudletLength of NetworkCloudlet. See the TODO there for more details.")
    public void testSortNetworkCloudlets() {
        System.out.println("sort");
        List<NetworkCloudlet> list = createNetworkCloudlets(CLOUDLET_LENGTH_ARRAY);
        CloudletList.sort(list);
        checkIfCloudletListIsInAccendingTotalLengthOrder(list, CLOUDLET_LENGTH_ARRAY);
    }

    private void checkIfCloudletListIsInAccendingTotalLengthOrder(List<? extends Cloudlet> list, int cloudletLengths[]) {
        for(int i = 0; i < cloudletLengths.length; i++){
            Cloudlet c = list.get(i);
            int expectedTotalLenght = (i+1)*100;
            String msg = String.format(
                    "The cloudlet with total length %d was expected at position %d",
                    expectedTotalLenght, i);
            assertEquals(msg, expectedTotalLenght, c.getCloudletTotalLength());
        }
    }
}
