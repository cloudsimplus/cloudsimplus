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

    /**
     * The transfer time is limited to the minimum value between the disk rate
     * (defined by the HarddriveStorage super class) and the bandwidth.
     * If the HOST_BW is greater than the disk rate, the max transfer rate will
     * be limited to the disk rate.
     * If the disk rate is greater than the bandwidth, the max transfer rate will
     * be limited to the bandwidth.
     */
    @Test
    public void testGetMaxTransferRate() {
        final HarddriveStorage hd = new HarddriveStorage(CAPACITY);
        //creates a SAN with bw greater than the disk rate of the super class HarddriveStorage
        double bandwidth = hd.getMaxTransferRate() * 100;
        SanStorage instance = createSanStorage(bandwidth);
        final double diskRate = hd.getMaxTransferRate();
        assertEquals(diskRate, instance.getMaxTransferRate(), 0.0);

        //creates a SAN with bw lower than the disk rate of the super class HarddriveStorage
        bandwidth = hd.getMaxTransferRate() / 2;
        instance = createSanStorage(bandwidth);
        assertEquals(bandwidth, instance.getMaxTransferRate(), 0.0);
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
