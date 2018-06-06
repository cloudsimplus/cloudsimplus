package org.cloudbus.cloudsim.resources;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class SanStorageTest {
    private static final long CAPACITY = 100000L;
    private static final int TOTAL_FILES = 10;
    private static final double NETWORK_LATENCY = 2;
    private static final double BW = 1000;
    private static final int FILE_SIZE = 5000;
    private static final double FILE_TRANSFER_TIME = FILE_SIZE/BW + NETWORK_LATENCY;
    private static final String FILE1 = "file1.txt";

    @Test
    public void testAddReservedFile() {
        final SanStorage instance = createSanStorage(BW);
        final File file = new File(FILE1, FILE_SIZE);
        assertTrue(instance.reserveSpace(file.getSize()));
        assertTrue(instance.addReservedFile(file) > FILE_TRANSFER_TIME);
    }

    private static SanStorage createSanStorage(final double bandwidth, final double networkLatency){
        return new SanStorage(CAPACITY, bandwidth, networkLatency);
    }

    private static SanStorage createSanStorage(final double bandwidth){
        return createSanStorage(bandwidth, NETWORK_LATENCY);
    }

    @Test
    public void testGetMaxTransferRate1() {
        final double diskRateMbps = 10;
        final double bwMbps = diskRateMbps*100;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, instance.getMaxTransferRate(), 0.0);
    }

    @Test
    public void testGetMaxTransferRate2() {
        final double bwMbps = 10;
        final double diskRateMbps = 10*bwMbps;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, instance.getMaxTransferRate(), 0.0);
    }

    @Test
    public void testGetTransferTimeWhenDiskRateAndBwAreEqual() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 162;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenDiskReadTimeIsNegligible() {
        final double bwMbps = 10;
        final double diskRateMbps = 1000000;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 82.0008;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenNetworkTransferTimeIsNegligible() {
        final double bwMbps = 1000000;
        final double diskRateMbps = 10;
        final double latency = 0.0001;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, latency);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 80.0009;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTime_whenBwIsLowerThanDiskRate() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps*10;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 90;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTime_whenBwIsLowerThanDiskRateAndLatencyIsAlmostZero() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps*10;
        final double latency = 0.0001;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, latency);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 88.0;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTime_whenDiskRateIsLowerThanBw() {
        final double diskRateMbps = 10;
        final double bwMbps = diskRateMbps*100;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 82.8;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTime_whenBwIsEqualToDiskRate() {
        final double bwAndDiskRateMbps = 10;
        final SanStorage instance = new SanStorage(CAPACITY, bwAndDiskRateMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(bwAndDiskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 162;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.1);
    }

    @Test
    public void testNew_namedSanStorage() {
        final String name = "san1";
        final SanStorage san = new SanStorage(name, CAPACITY, BW, NETWORK_LATENCY);
        assertEquals(name, san.getName());
    }

    @Test
    public void testAddFile_File() {
        final SanStorage instance = createSanStorage(BW);

        //try invalid file
        File file = null;
        assertEquals(0, instance.addFile(file), 0);

        //add a valid file
        file = new File(FILE1, FILE_SIZE);
        assertTrue(instance.addFile(file) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testAddFile_List() {
        final SanStorage instance = createSanStorage(BW);
        final List<File> list = HarddriveStorageTest.createFileList(TOTAL_FILES, FILE_SIZE);
        final double transferTimeOfAllFiles = FILE_TRANSFER_TIME * TOTAL_FILES;
        assertTrue(instance.addFile(list) > transferTimeOfAllFiles);
    }

    @Test
    public void testDeleteFile_File() {
        final SanStorage instance = createSanStorage(BW);
        final File file = new File(FILE1, FILE_SIZE);
        instance.addFile(file);
        assertTrue(instance.deleteFile(file) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testGetBandwidth() {
        final double bandwidth = BW;
        final SanStorage instance = createSanStorage(bandwidth);
        assertEquals(bandwidth, instance.getBandwidth(), 0.0);
    }

    @Test
    public void testGetNetworkLatency() {
        final double networkLatency = NETWORK_LATENCY;
        final SanStorage instance = createSanStorage(BW, networkLatency);
        assertEquals(networkLatency, instance.getNetworkLatency(), 0.0);
    }
}
