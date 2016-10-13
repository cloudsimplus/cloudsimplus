package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.easymock.EasyMock;
import org.junit.Test;
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
        List<Double> mipsList = createMipsList(1);
        List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(1);

        Processor processor = Processor.fromMipsList(mipsList, cloudletExecList);
        assertEquals("The cloudlet exec list is the same size as expected",
                cloudletExecList.size(), processor.getCloudletExecList().size());
    }

    private List<CloudletExecutionInfo> createCloudletExecList(int numberOfCloudlets) {
        List<CloudletExecutionInfo> cloudletExecList = new ArrayList<>();
        Cloudlet cloudlet = createMockCloudlet(numberOfCloudlets);

        IntStream.range(0, numberOfCloudlets).forEach(i ->
                cloudletExecList.add(new CloudletExecutionInfo(cloudlet)));
        return cloudletExecList;
    }

    private Cloudlet createMockCloudlet(int numberOfCloudlets) {
        Cloudlet cloudlet = EasyMock.createMock(Cloudlet.class);
        EasyMock.expect(cloudlet.getNumberOfPes()).andReturn(1).times(numberOfCloudlets*2);
        EasyMock.expect(cloudlet.registerArrivalOfCloudletIntoDatacenter()).andReturn(0.0).times(numberOfCloudlets);
        EasyMock.expect(cloudlet.getCloudletFinishedSoFar()).andReturn(0L).times(numberOfCloudlets);
        EasyMock.replay(cloudlet);
        return cloudlet;
    }


    @Test
    public void testFromMips_EmptyCloudletExecList() {
        List<Double> mipsList = createMipsList(1);
        Processor result = Processor.fromMipsList(mipsList);
        assertTrue("The processor cloudlet exec list should be empty", result.getCloudletExecList().isEmpty());
    }

    private List<Double> createMipsList(int numberOfPes) {
        List<Double> mipsList = new ArrayList<>();
        IntStream.range(0, numberOfPes).forEach(i->mipsList.add(PE_MIPS));
        return mipsList;
    }

    @Test
    public void testGetTotalMipsCapacity() {
        Processor instance = createDefaultProcessor();
        Double expResult = PE_MIPS * NUMBER_OF_PES;
        assertEquals(expResult, instance.getTotalMipsCapacity());
    }

    /**
     * Creates a processing using the default values.
     * @return the created processor
     */
    private static Processor createDefaultProcessor() {
        return new Processor(PE_MIPS, NUMBER_OF_PES);
    }

    @Test
    public void testGetCapacity() {
        Processor instance = createDefaultProcessor();
        Double expResult = PE_MIPS;
        assertEquals(expResult, instance.getCapacity());
    }

    @Test
    public void testGetAvailableMipsByPe() {
        List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(NUMBER_OF_PES*2);
        Processor instance = Processor.fromMipsList(mipsList, cloudletExecList);
        double expResult = (PE_MIPS*NUMBER_OF_PES) / cloudletExecList.size();
        assertEquals(expResult, instance.getAvailableMipsByPe(), 0.0);
    }

    @Test
    public void testGetNumberOfPes_FromDefaultConstructor() {
        Processor instance = createDefaultProcessor();
        int expResult = NUMBER_OF_PES;
        assertEquals(expResult, instance.getNumberOfPes());
    }

    @Test
    public void testSetNumberOfPes() {
        int expResult = NUMBER_OF_PES*2;
        Processor instance = createDefaultProcessor();
        instance.setNumberOfPes(expResult);
        assertEquals(expResult, instance.getNumberOfPes());
    }

    @Test
    public void testSetCapacity() {
        double expResult = PE_MIPS*2;
        Processor instance = createDefaultProcessor();
        instance.setCapacity(expResult);
        assertEquals(expResult, instance.getCapacity(), 0.0);
    }

    @Test
    public void testGetCloudletExecList() {
        List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(2);
        List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        Processor instance = Processor.fromMipsList(mipsList, cloudletExecList);
        Collection<CloudletExecutionInfo> result = instance.getCloudletExecList();
        assertEquals("The number of cloudlets in the exec list is not as expected",
                cloudletExecList.size(), result.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCloudletExecList_ReadOnlyList() {
        List<CloudletExecutionInfo> cloudletExecList = createCloudletExecList(2);
        List<Double> mipsList = createMipsList(NUMBER_OF_PES);
        Processor instance = Processor.fromMipsList(mipsList, cloudletExecList);
        instance.getCloudletExecList().add(null);
    }

}
