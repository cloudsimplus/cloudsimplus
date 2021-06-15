package org.cloudbus.cloudsim.resources;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class SanStorageTest {
    private static final String NO_PREVIOUS_SPACE = "The reserved file was added but its space was not previously reserved.";
    private static final int TOTAL_FILES_TO_CREATE = 5;
    private static final String INEXISTENT_FILE = "inexistent-file.txt";

    private static final long   CAPACITY = 100000L;
    private static final int    TOTAL_FILES = 10;
    private static final double NETWORK_LATENCY = 2;
    private static final double BANDWIDTH = 1000;
    private static final int    FILE_SIZE = 5000;
    private static final double FILE_TRANSFER_TIME = FILE_SIZE/ BANDWIDTH + NETWORK_LATENCY;
    private static final String FILE1 = "file1.txt";

    @Test
    public void testAddReservedFile() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        final File file = new File(FILE1, FILE_SIZE);
        assertTrue(instance.reserveSpace(file.getSize()));
        assertTrue(instance.addReservedFile(file) > FILE_TRANSFER_TIME);
    }

    private static SanStorage createSanStorage(){
        return createSanStorage(CAPACITY);
    }

    private static SanStorage createSanStorageBw(final double bandwidth){
        return createSanStorage(bandwidth, NETWORK_LATENCY);
    }

    private static SanStorage createSanStorage(final double bandwidth, final double networkLatency){
        return createSanStorage(bandwidth, networkLatency, CAPACITY);
    }

    private static SanStorage createSanStorage(final long capacity){
        return createSanStorage(BANDWIDTH, NETWORK_LATENCY, capacity);
    }

    private static SanStorage createSanStorage(final double bandwidth, final double networkLatency, final long capacity){
        return new SanStorage(capacity, bandwidth, networkLatency);
    }

    @Test
    public void testGetMaxTransferRate1() {
        final double diskRateMbps = 10;
        final double bwMbps = diskRateMbps*100;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, instance.getMaxTransferRate());
    }

    @Test
    public void testGetMaxTransferRate2() {
        final double bwMbps = 10;
        final double diskRateMbps = 10*bwMbps;
        final SanStorage instance = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, instance.getMaxTransferRate());
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
    public void testGetTransferTimeWhenBwIsLowerThanDiskRate() {
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
    public void testGetTransferTimeWhenBwIsLowerThanDiskRateAndLatencyIsAlmostZero() {
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
    public void testGetTransferTimeWhenDiskRateIsLowerThanBw() {
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
    public void testGetTransferTimeWhenBwIsEqualToDiskRate() {
        final double bwAndDiskRateMbps = 10;
        final SanStorage instance = new SanStorage(CAPACITY, bwAndDiskRateMbps, NETWORK_LATENCY);
        instance.setMaxTransferRate(bwAndDiskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 162;
        assertEquals(expectedTime, instance.getTransferTime(fileSizeMB), 0.1);
    }

    @Test
    public void testNewNamedSanStorage() {
        final String name = "san1";
        final SanStorage san = new SanStorage(name, CAPACITY, BANDWIDTH, NETWORK_LATENCY);
        assertEquals(name, san.getName());
    }

    @Test
    public void testAddFileWhenParamIsFile() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);

        //try invalid file
        File file = null;
        assertEquals(0, instance.addFile(file));

        //add a valid file
        file = new File(FILE1, FILE_SIZE);
        assertTrue(instance.addFile(file) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testAddFileWhenParamIsList() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        final List<File> list = SanStorageTest.createFileList(TOTAL_FILES, FILE_SIZE);
        final double transferTimeOfAllFiles = FILE_TRANSFER_TIME * TOTAL_FILES;
        assertTrue(instance.addFile(list) > transferTimeOfAllFiles);
    }

    @Test
    public void testDeleteFileWhenParamIsFile() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        final File file = new File(FILE1, FILE_SIZE);
        instance.addFile(file);
        assertTrue(instance.deleteFile(file) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testGetBandwidth() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        assertEquals(BANDWIDTH, instance.getBandwidth());
    }

    @Test
    public void testGetNetworkLatency() {
        final SanStorage instance = createSanStorage(BANDWIDTH, NETWORK_LATENCY);
        assertEquals(NETWORK_LATENCY, instance.getNetworkLatency());
    }

    /**
     * Creates a list of ficticious numbered File instances
     * @param totalFiles The number of files to be created
     * @param fileSize the size of each file
     * @return
     */
    public static List<File> createFileList(final int totalFiles, final int fileSize) {
        final List<File> fileList = new ArrayList<>();
        for(int i = 1; i <= totalFiles; i++){
            fileList.add(createNumberedFile(i, fileSize));
        }

        return fileList;
    }

    /**
     * Creates a fictitious File instance with a fileName "file%d", where %d is the fileNumber given.
     * @param fileNumber The number of the file
     * @param fileSize The size of the file
     * @return a File instance
     * @throws IllegalArgumentException
     */
    private static File createNumberedFile(final int fileNumber, final int fileSize) {
        return new File(String.format("file%d.txt", fileNumber), fileSize);
    }

    @Test
    public void testGetNumStoredFile1() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        assertEquals(0, instance.getNumStoredFile());

        final int totalFiles = 2;
        for(int i = 1; i <= totalFiles; i++){
            assertTrue(instance.addFile(createNumberedFile(i, FILE_SIZE))>0);
        }
        assertEquals(totalFiles, instance.getNumStoredFile());
    }

    @Test
    public void testIsFull() {
        final int numberOfFiles = (int)(CAPACITY/FILE_SIZE);
        final SanStorage instance = createSanStorage(CAPACITY+FILE_SIZE);
        IntStream.range(0, numberOfFiles).forEach(id -> {
            instance.addFile(createNumberedFile(id, FILE_SIZE));
            assertFalse(instance.isFull());
        });

        instance.addFile(createNumberedFile(numberOfFiles, FILE_SIZE));
        assertTrue(instance.isFull());
    }

    @Test
    public void testGetNumStoredFile2() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        assertEquals(0, instance.getNumStoredFile());

        final int totalFiles = 4;
        instance.addFile(createFileList(totalFiles, FILE_SIZE));
        assertEquals(totalFiles, instance.getNumStoredFile());
    }

    @Test()
    public void testGetNumStoredFileWhenNullList() {
        final SanStorage instance = createSanStorageBw(BANDWIDTH);
        Assertions.assertThrows(NullPointerException.class, () -> instance.addFile((List<File>) null));
    }


    @Test
    public void testReserveSpace1() {
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final long available = fileSize;
        final SanStorage instance = createSanStorage(capacity);

        //try to add invalid files
        File file = null;
        assertFalse(instance.addFile(file) > 0.0);

        int fileNumber = 0;
        file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(instance.addFile(file) > 0.0);
        assertEquals(available, instance.getAvailableResource());

        assertTrue(instance.reserveSpace(fileSize));
        /*there isn't more available space for the new file,
        because the available space was reserved*/
        assertEquals(0, instance.getAvailableResource());
        file = createNumberedFile(++fileNumber, fileSize);
        assertFalse(instance.addFile(file) > 0.0);

        //adds the file that the space was previously reserved
        assertTrue(instance.addReservedFile(file)> 0);

        assertFalse(instance.reserveSpace(FILE_SIZE*10));
    }

    /**
     * Try to add a reserved file which the space was not previously reserved.
     * An exception must be thrown in that case.
     */
    @Test
    public void testReserveSpaceNotReservedFile1() {
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final long available = fileSize;
        final SanStorage instance = createSanStorage(capacity);
        assertEquals(0, instance.getAllocatedResource());

        int fileNumber = 0;
        File file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(instance.addFile(file) > 0.0);
        assertEquals(available, instance.getAvailableResource());

        //file larger than the available capacity
        assertEquals(0, instance.addFile(new File("too-big-file.txt", FILE_SIZE*10)));

        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(instance.reserveSpace(fileSize));
        file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(instance.addReservedFile(file) > 0);
        assertEquals(0, instance.getAvailableResource());
    }

    @Test()
    public void testAddFileWhenNullFile() {
        final SanStorage instance = createSanStorage(1);
        Assertions.assertThrows(NullPointerException.class, () -> instance.addReservedFile(null));
    }


    @Test
    public void testAddReservedFileWhenSpaceNotPreReserved() {
        final SanStorage instance = createSanStorage(CAPACITY);
        try{
            instance.addReservedFile(new File(FILE1, 100));
            fail(NO_PREVIOUS_SPACE);
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(CAPACITY, instance.getAvailableResource());
        }
    }

    @Test
    public void testAddReservedFileWhenFileAlreadyAdded() {
        final SanStorage instance = createSanStorage(CAPACITY);
        final File file = new File(FILE1, 100);
        instance.reserveSpace(file.getSize());
        instance.addReservedFile(file);
        instance.reserveSpace(file.getSize());
        assertFalse(instance.addReservedFile(file) > 0);
    }

    /**
     * Try to add a reserved file which the space was not previously reserved.
     * An exception must be thrown in that case.
     */
    @Test
    public void testReserveSpaceNotReservedFile2() {
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2L;
        long available = capacity;
        final SanStorage instance = createSanStorage(capacity);
        assertEquals(0, instance.getAllocatedResource());

        final File file = createNumberedFile(1, fileSize);
        try{
            instance.addReservedFile(file);
            fail(NO_PREVIOUS_SPACE);
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(available, instance.getAvailableResource());
            assertEquals(0, instance.getAllocatedResource());
        }

        available = (long)fileSize;
        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(instance.reserveSpace(fileSize));
        assertTrue(instance.addReservedFile(file) > 0);
        assertEquals(available, instance.getAvailableResource());
        assertEquals(available, instance.getAllocatedResource());
    }

    /**
     * Try to add a reserved file which the space was not totally reserved
     * before.
     * An exception must be thrown in that case.
     */
    @Test
    public void testReserveSpaceNotReservedFile3() {
        final int fileSize = FILE_SIZE;
        final int halfFileSize = fileSize/2;
        final long capacity = fileSize;
        final long available = halfFileSize;
        final SanStorage instance = createSanStorage(capacity);
        assertEquals(0, instance.getAllocatedResource());

        final File file = createNumberedFile(1, fileSize);
        assertTrue(instance.reserveSpace(halfFileSize));
        try{
            instance.addReservedFile(file);
            fail("The reserved file was added but its space was not totally reserved before.");
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added because not the entire space was previously
            reserved.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(available, instance.getAvailableResource());
            assertEquals(available,  instance.getAllocatedResource());
        }

        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(instance.reserveSpace(halfFileSize));
        assertTrue(instance.addReservedFile(file) > 0);
        assertEquals(0, instance.getAvailableResource());
        assertEquals(capacity, instance.getAllocatedResource());
    }

    @Test
    public void testHasPotentialAvailableSpace() {
        final long fileSize = CAPACITY;
        final SanStorage instance = createSanStorage();
        assertTrue(instance.hasPotentialAvailableSpace(fileSize));
        assertFalse(instance.hasPotentialAvailableSpace(fileSize*100));
    }

    @Test
    public void testHasPotentialAvailableSpaceWhenInvalidValue() {
        final SanStorage instance = createSanStorage();
        assertFalse(instance.hasPotentialAvailableSpace(0));
        assertFalse(instance.hasPotentialAvailableSpace(-1));
    }


    @Test
    public void testGetFileAfterAddFile() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        //try to add the same files
        assertFalse(instance.addFile(fileList)>0);

        //try to add already existing files, one by one
        fileList.forEach(file -> assertFalse(instance.addFile(file)>0));
        fileList.forEach(file -> assertEquals(file, instance.getFile(file.getName())));
        assertEquals(null, instance.getFile(INEXISTENT_FILE));
    }

    @Test
    public void testGetFileList() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        instance.addFile(fileList);
        assertEquals(fileList, instance.getFileList());
    }

    @Test
    public void testGetFileWhenInvalidFile() {
        final SanStorage instance = createSanStorage();
        assertEquals(null, instance.getFile("   "));
        assertEquals(null, instance.getFile(""));
        assertEquals(null, instance.getFile(null));
    }

    /**
     * Adds and internally created file list to a Hard Drive
     * @param instance the Hard Drive to add the files
     * @return the list of created files
     */
    private List<File> createListOfFilesAndAddToHardDrive(SanStorage instance) {
        final List<File> fileList = createFileList(TOTAL_FILES_TO_CREATE, FILE_SIZE);
        assertTrue(instance.addFile(fileList)>0);
        return fileList;
    }

    @Test
    public void testGetFileNameList() {
        final SanStorage instance = createSanStorage();
        final List<String> fileNameList = new ArrayList<>();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> fileNameList.add(file.getName()));
        assertEquals(fileNameList, instance.getFileNameList());
    }

    @Test
    public void testDeleteFileWheParamString() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file ->  assertEquals(file, instance.deleteFile(file.getName())));

        assertEquals(null, instance.deleteFile(""));
        assertEquals(null, instance.deleteFile(INEXISTENT_FILE));
    }

    @Test
    public void testContainsWhenParamString() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> assertTrue(instance.contains(file.getName())));

        assertFalse(instance.contains(INEXISTENT_FILE));
        final String nullStr = null;
        assertFalse(instance.contains(nullStr));
        assertFalse(instance.contains(""));
    }

    @Test
    public void testContainsWhenParamIsFile() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> assertTrue(instance.contains(file)));

        assertFalse(instance.contains(new File(INEXISTENT_FILE, FILE_SIZE)));
        final File nullFile = null;
        assertFalse(instance.contains(nullFile));
    }

    @Test
    public void testRenameFile() {
        final SanStorage instance = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        for(final File file: fileList){
            final String oldName = file.getName();
            final String newName = String.format("renamed-%s", oldName);
            assertTrue(instance.contains(oldName));
            assertTrue(instance.renameFile(file, newName));
            assertFalse(instance.contains(oldName));

            final File result = instance.getFile(newName);
            assertEquals(file, result);
            assertEquals(file.getName(), result.getName());
        }

        final File file1 = new File(FILE1, 100), file2 = new File("file2.txt", 100);
        instance.addFile(file1);
        instance.addFile(file2);
        assertFalse(instance.renameFile(file1, file2.getName()));

        final File notAddedFile = new File("file3.txt", 100);
        assertFalse(instance.renameFile(notAddedFile, "new-name.txt"));
    }

    @Test
    public void testIsResourceAmountAvailable() {
        final SanStorage instance = createSanStorage();

        assertTrue(instance.isAmountAvailable(CAPACITY));
        final File file = new File(FILE1, (int)CAPACITY);
        assertTrue(instance.addFile(file) > 0);
        assertFalse(instance.isAmountAvailable(CAPACITY));
        assertTrue(instance.deleteFile(file) > 0);
        assertTrue(instance.isAmountAvailable(CAPACITY));
    }
}
