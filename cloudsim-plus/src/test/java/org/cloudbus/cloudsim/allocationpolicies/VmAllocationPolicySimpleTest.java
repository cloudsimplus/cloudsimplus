package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimpleTest;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Manoel Campos da Silva Filho
 */
public class VmAllocationPolicySimpleTest {
    public static final int HOST_MIPS = 1000;
    public static final int HOST_RAM = 10000;
    public static final int HOST_BW = 100000;
    public static final int HOST_BASE_STORAGE = 1000;
    private VmAllocationPolicySimple policy;

    @Before
    public void setUp(){
        policy = createVmAllocationPolicy(new Integer[]{4, 2, 6, 5});
    }

    /**
     * Creates a VmAllocationPolicy.
     *
     * @param freePesByHost an array containing the number of free PEs for each host of the
     *                      allocation policy, that will be assigned to the freePesList.
     *                      This array will define the number of Hosts and its PEs.
     * @return
     */
    private VmAllocationPolicySimple createVmAllocationPolicy(Integer[] freePesByHost) {
        Map<Host, Integer> hostFreePesMap = new HashMap<>(freePesByHost.length);
        List<Host> hosts = new ArrayList<>(freePesByHost.length);
        for(int i = 1; i <= freePesByHost.length; i++) {
            Host host = HostSimpleTest.createHostSimple(
                i, freePesByHost[i-1], HOST_MIPS, HOST_RAM, HOST_BW, i* HOST_BASE_STORAGE);
            hostFreePesMap.put(host, host.getNumberOfPes());
            hosts.add(host);
        }

        VmAllocationPolicySimple policy = new VmAllocationPolicySimple();
        policy.setHostFreePesMap(hostFreePesMap);

        Datacenter datacenter = EasyMock.createMock(Datacenter.class);
        EasyMock.expect(datacenter.getHostList()).andReturn(hosts).anyTimes();
        EasyMock.replay(datacenter);
        policy.setDatacenter(datacenter);

        return policy;
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGiven_AllocateHostWithLessUsedPesToIt() {
        Vm vm = VmSimpleTest.createVm(0, 1000, 2);
        assertTrue(policy.allocateHostForVm(vm));

        Host allocatedHostForVm = policy.getVmTable().get(vm.getUid());
        Host hostWithLessPes = policy.getDatacenter().getHostList().get(2);
        assertEquals(hostWithLessPes, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGivenAndSelectedHostDoesntHaveStorage_AllocateOtherHost() {
        Host secondHostWithLessPes = policy.getDatacenter().getHostList().get(3);
        Vm vm = VmSimpleTest.createVm(
            0, 1000, 2, 1, 1,
            secondHostWithLessPes.getStorageCapacity(), CloudletScheduler.NULL);
        assertTrue(policy.allocateHostForVm(vm));

        Host allocatedHostForVm = policy.getVmTable().get(vm.getUid());
        assertEquals(secondHostWithLessPes, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGivenAndNoHostHasResourcesToRunIt() {
        Host secondHostWithLessPes = policy.getDatacenter().getHostList().get(3);
        Vm vm = VmSimpleTest.createVm(0, 1000, 10);
        assertFalse(policy.allocateHostForVm(vm));
    }


}
