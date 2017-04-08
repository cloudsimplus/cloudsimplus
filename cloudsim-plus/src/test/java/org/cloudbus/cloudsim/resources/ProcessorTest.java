package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.vms.Vm;
import org.easymock.EasyMock;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ProcessorTest {
    private static final double PE_MIPS = 1000;
    private static final int NUMBER_OF_PES = 2;

    @Test
    public void testFromMipsList_CheckCloudletList() {
        final List<Double> mipsList = createMipsList(1);
        final List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(1);

        final Processor processor = Processor.fromMipsList(Vm.NULL, mipsList, cloudletExecList);
        assertEquals("The cloudlet exec list is the same size as expected",
                cloudletExecList.size(), processor.getCloudletExecList().size());
    }

    private List<CloudletExecutionInfo> createCloudletExecList(int numberOfCloudlets) {
        final List<CloudletExecutionInfo> cloudletExecList = new ArrayList<>();
        final Cloudlet cloudlet = createMockCloudlet(numberOfCloudlets);

        IntStream.range(0, numberOfCloudlets).forEach(i ->
                cloudletExecList.add(new CloudletExecutionInfo(cloudlet)));
        return cloudletExecList;
    }

    private Cloudlet createMockCloudlet(int numberOfCloudlets) {
        final Cloudlet cloudlet = EasyMock.createMock(Cloudlet.class);
        EasyMock.expect(cloudlet.getNumberOfPes()).andReturn(1L).times(numberOfCloudlets*2);
        EasyMock.expect(cloudlet.registerArrivalInDatacenter()).andReturn(0.0).times(numberOfCloudlets);
        EasyMock.expect(cloudlet.getFinishedLengthSoFar()).andReturn(0L).times(numberOfCloudlets);
        EasyMock.replay(cloudlet);
        return cloudlet;
    }


    @Test
    public void testFromMips_EmptyCloudletExecList() {
        final List<Double> mipsList = createMipsList(1);
        final Processor result = Processor.fromMipsList(Vm.NULL, mipsList);
        assertTrue("The processor cloudlet exec list should be empty", result.getCloudletExecList().isEmpty());
    }

    private List<Double> createMipsList(int numberOfPes) {
        return IntStream.range(0, numberOfPes).mapToObj(i -> PE_MIPS).collect(toList());
    }

    @Test
    public void testGetTotalMipsCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS * NUMBER_OF_PES;
        assertEquals(expResult, instance.getTotalMips(), 0);
    }

    /**
     * Creates a processing using the default values.
     * @return the created processor
     */
    private static Processor createDefaultProcessor() {
        return new Processor(Vm.NULL, PE_MIPS, NUMBER_OF_PES);
    }

    @Test
    public void testGetCapacity() {
        final Processor instance = createDefaultProcessor();
        final double expResult = PE_MIPS;
        assertEquals(expResult, instance.getMips(), 0);
    }

    @Test
    public void testGetAvailableMipsByPe() {
        final List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        final List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(NUMBER_OF_PES*2);
        final Processor instance = Processor.fromMipsList(Vm.NULL, mipsList, cloudletExecList);
        final double expResult = (PE_MIPS*NUMBER_OF_PES) / cloudletExecList.size();
        assertEquals(expResult, instance.getAvailableMipsByPe(), 0.0);
    }

    @Test
    public void testGetNumberOfPes_FromDefaultConstructor() {
        final Processor instance = createDefaultProcessor();
        final int expResult = NUMBER_OF_PES;
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetNumberOfPes() {
        final int expResult = NUMBER_OF_PES*2;
        final Processor instance = createDefaultProcessor();
        instance.setCapacity(expResult);
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testSetCapacity() {
        final long expResult = (long)PE_MIPS*2;
        final Processor instance = createDefaultProcessor();
        instance.setMips(expResult);
        assertEquals(expResult, instance.getMips(), 0.0);
    }

    @Test
    public void testGetCloudletExecList() {
        final List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(2);
        final List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        final Processor instance = Processor.fromMipsList(Vm.NULL, mipsList, cloudletExecList);
        final Collection<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertEquals("The number of cloudlets in the exec list is not as expected",
                cloudletExecList.size(), result.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCloudletExecList_ReadOnlyList() {
        final List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(2);
        final List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        final Processor instance = Processor.fromMipsList(Vm.NULL, mipsList, cloudletExecList);
        instance.getCloudletExecList().add(null);
    }
}
