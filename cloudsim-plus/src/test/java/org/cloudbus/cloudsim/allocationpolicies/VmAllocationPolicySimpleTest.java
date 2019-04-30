package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimpleTest;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Manoel Campos da Silva Filho
 */
public class VmAllocationPolicySimpleTest {
    public static final int HOST_MIPS = 1000;
    public static final int HOST_RAM = 10000;
    public static final int HOST_BW = 100000;
    public static final int HOST_BASE_STORAGE = 1000;
    private VmAllocationPolicySimple policy;

    @BeforeEach
    public void setUp(){
        policy = createVmAllocationPolicy(4, 2, 6, 5);
    }

    /**
     * Creates a VmAllocationPolicy.
     *
     * @param freePesByHost an array containing the number of free PEs for each host of the
     *                      allocation policy, that will be assigned to the freePesList.
     *                      This array will define the number of Hosts and its PEs.
     * @return
     */
    private VmAllocationPolicySimple createVmAllocationPolicy(int... freePesByHost) {
        final Map<Host, Long> hostFreePesMap = new HashMap<>(freePesByHost.length);
        final List<Host> hosts = new ArrayList<>(freePesByHost.length);
        for(int i = 1; i <= freePesByHost.length; i++) {
            final Host host = HostSimpleTest.createHostSimple(
                i-1, freePesByHost[i-1], HOST_MIPS, HOST_RAM, HOST_BW, i*HOST_BASE_STORAGE);
            hostFreePesMap.put(host, host.getNumberOfPes());
            hosts.add(host);
        }

        final VmAllocationPolicySimple policy = new VmAllocationPolicySimple();

        final Datacenter datacenter = EasyMock.createMock(Datacenter.class);
        EasyMock.expect(datacenter.getHostList()).andReturn(hosts).anyTimes();
        EasyMock.replay(datacenter);
        policy.setDatacenter(datacenter);

        return policy;
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGiven_AllocateHostWithLessUsedPesToIt() {
        final Vm vm = VmTestUtil.createVm(0, 1000, 2);
        assertTrue(policy.allocateHostForVm(vm));

        final Host allocatedHostForVm = vm.getHost();
        final Host hostWithLessPes = policy.getDatacenter().getHostList().get(2);
        assertEquals(hostWithLessPes, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGivenAndSelectedHostDoesntHaveStorage_AllocateOtherHost() {
        final Host hostWithMoreFreePes = policy.getDatacenter().getHostList().get(2);
        final Vm vm = VmTestUtil.createVm(
            0, 1000, 2, 1, 1,
            HOST_BASE_STORAGE, CloudletScheduler.NULL);
        assertTrue(policy.allocateHostForVm(vm), vm + " couldn't be allocated to " + hostWithMoreFreePes);

        final Host allocatedHostForVm = vm.getHost();
        assertEquals(hostWithMoreFreePes, allocatedHostForVm);
    }

    @Test
    public void allocateHostForVm_WhenOneVmIsGivenAndNoHostHasResourcesToRunIt() {
        final Vm vm = VmTestUtil.createVm(0, 1000, 10);
        assertFalse(policy.allocateHostForVm(vm));
    }
}
