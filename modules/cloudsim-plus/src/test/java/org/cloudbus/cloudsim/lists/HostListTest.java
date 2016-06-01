package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.HostSimpleTest;
import org.cloudbus.cloudsim.network.datacenter.NetworkHost;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Pe.Status;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HostListTest {
    private static final int NUMBER_OF_HOSTS = 10;
    private static final int PES = 1;
    private static final int RAM = 1024;
    private static final long BW = 10000;
    private static final double MIPS = 1000;
    private static final long STORAGE = Consts.MILLION;
    
    private List<HostSimple> hostSimpleList;
    private List<NetworkHost> networkHostList;
    
    @Before
    public void setUp() {
        this.hostSimpleList = createHostSimpleList();
        this.networkHostList = createNetworkHostList();
    }

    @Test
    public void testGetByIdHostSimple() {
        System.out.println("getById");
        final int id = 0;
        HostSimple expResult = hostSimpleList.get(id);
        assertEquals(expResult, HostList.getById(hostSimpleList, id));
        assertEquals(null, HostList.getById(hostSimpleList, NUMBER_OF_HOSTS));
        assertEquals(null, HostList.getById(hostSimpleList, -1));
    }

    @Test
    public void testGetByIdNetworkHost() {
        System.out.println("getById");
        final int id = 0;
        NetworkHost expResult = networkHostList.get(id);
        assertEquals(expResult, HostList.getById(networkHostList, id));
        assertEquals(null, HostList.getById(networkHostList, NUMBER_OF_HOSTS));
        assertEquals(null, HostList.getById(networkHostList, -1));
    }

    @Test
    public void testGetNumberOfPesHostSimple() {
        System.out.println("getNumberOfPes");
        int expResult = PES*NUMBER_OF_HOSTS;
        int result = HostList.getNumberOfPes(hostSimpleList);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNumberOfPesNetworkHost() {
        System.out.println("getNumberOfPes");
        int expResult = PES*NUMBER_OF_HOSTS;
        int result = HostList.getNumberOfPes(networkHostList);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNumberOfFreePes() {
        System.out.println("getNumberOfFreePes");
        createHostsAndCheckNumberOfPesByStatus(NUMBER_OF_HOSTS, 1, Status.FREE);
        createHostsAndCheckNumberOfPesByStatus(NUMBER_OF_HOSTS, 2, Status.FREE);
        
        List<HostSimple> list = createHostSimpleList(NUMBER_OF_HOSTS, 1);
        setStatusOfFirstPeOfHostsWithEvenId(list, Pe.Status.BUSY);
        checkNumberOfPesByStatus(list, NUMBER_OF_HOSTS/2, Status.FREE);
    }

    private void setStatusOfFirstPeOfHostsWithEvenId(List<HostSimple> list, Pe.Status statusToCheck) {
        list.stream()
                .filter(h -> h.getId()%2 != 0)
                .forEach(h -> h.setPeStatus(0, statusToCheck));
    }

    private void createHostsAndCheckNumberOfPesByStatus(
            final int numberOfHosts, final int numberOfPes,
            Pe.Status statusToCheck) {
        List<HostSimple> list = createHostSimpleList(numberOfHosts, numberOfPes);
        checkNumberOfPesByStatus(list, numberOfHosts*numberOfPes, statusToCheck);
    }

    private void checkNumberOfPesByStatus(List<HostSimple> list, 
            final int expectedNumberOfPesByStatus, Pe.Status statusToCheck) {
        int result = 0;
        switch(statusToCheck){
            case FREE: result = HostList.getNumberOfFreePes(list); break;
            case BUSY: result = HostList.getNumberOfBusyPes(list); break;
        }
        assertEquals(expectedNumberOfPesByStatus, result);
    }

    @Test
    public void testGetNumberOfBusyPes() {
        System.out.println("getNumberOfBusyPes");
        
        List<HostSimple> list = createHostSimpleList(NUMBER_OF_HOSTS, 2);
        checkNumberOfPesByStatus(list, 0, Status.BUSY);
        
        list = createHostSimpleList(NUMBER_OF_HOSTS, 1);
        checkNumberOfPesByStatus(list, 0, Status.BUSY);
        
        setStatusOfFirstPeOfHostsWithEvenId(list, Pe.Status.BUSY);
        checkNumberOfPesByStatus(list, NUMBER_OF_HOSTS/2, Status.BUSY);
    }

    @Test
    public void testGetHostWithFreePe_List() {
        System.out.println("getHostWithFreePe");
        HostSimple host0 = hostSimpleList.get(0);
        assertEquals(host0, HostList.getHostWithFreePe(hostSimpleList));

        host0.setPeStatus(0, Status.BUSY);
        HostSimple host1 = hostSimpleList.get(1);
        assertEquals(host1, HostList.getHostWithFreePe(hostSimpleList));   
        
        hostSimpleList.forEach(h -> {
            IntStream.range(0, PES).forEach(i -> h.setPeStatus(i, Status.BUSY));
        });
        assertEquals(null, HostList.getHostWithFreePe(hostSimpleList));   
    }

    @Test
    public void testGetHostWithFreePe_List_int() {
        System.out.println("getHostWithFreePe");
        final int numberOfFreePes = 4;
        List<HostSimple> list = createHostSimpleList(NUMBER_OF_HOSTS, numberOfFreePes);
        HostSimple host0 = list.get(0);
        assertEquals(host0, HostList.getHostWithFreePe(list, numberOfFreePes));
        
        host0.setPeStatus(0, Status.BUSY);
        HostSimple host1 = list.get(1);
        assertEquals(host1, HostList.getHostWithFreePe(list, numberOfFreePes));        
    }

    @Test
    public void testSetPeStatus() {
        System.out.println("setPeStatus");
        assertTrue(HostList.setPeStatus(hostSimpleList, Pe.Status.FREE, 0, 0));
        
        //PE doesn't exist
        assertFalse(HostList.setPeStatus(hostSimpleList, Pe.Status.FREE, 0, PES));

        //host doesn't exist
        assertFalse(HostList.setPeStatus(hostSimpleList, Pe.Status.FREE, NUMBER_OF_HOSTS, 0));        

        //host and PE don't exist
        assertFalse(HostList.setPeStatus(hostSimpleList, Pe.Status.FREE, NUMBER_OF_HOSTS, PES));        
    }

    private List<HostSimple> createHostSimpleList() {
        return createHostSimpleList(NUMBER_OF_HOSTS, PES);
    }

    private List<HostSimple> createHostSimpleList(int numberOfHosts, int numberOfPes) {
        List<HostSimple> list = new ArrayList<>();
        IntStream.range(0, numberOfHosts)
                .forEach(i -> list.add(HostSimpleTest.createHostSimple(i, numberOfPes)));    
        return list;
    }

    private List<NetworkHost> createNetworkHostList() {
        List<NetworkHost> list = new ArrayList<>();
        IntStream.range(0, NUMBER_OF_HOSTS)
                .forEach(i -> list.add(createNetworkHost(i)));    
        return list;
    }
    
    private NetworkHost createNetworkHost(final int hostId) {
        final List<Pe> peList = HostSimpleTest.createPes(PES, MIPS);

        return new NetworkHost(hostId,
                new ResourceProvisionerSimple<>(new Ram(RAM)),
                new ResourceProvisionerSimple<>(new Bandwidth(BW)),
                STORAGE, peList, new VmSchedulerTimeShared(peList));
    }    
    
}
