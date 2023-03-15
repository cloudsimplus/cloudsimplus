package org.cloudsimplus.resources;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class SanStorageTest {
    private static final String NO_PREVIOUS_SPACE = "The reserved file was added but its space was not previously reserved.";
    private static final int TOTAL_FILES_TO_CREATE = 5;
    private static final String NON_EXISTENT_FILE = "non-existent-file.txt";

    private static final long   CAPACITY = 100000L;
    private static final int    TOTAL_FILES = 10;
    private static final double NETWORK_LATENCY = 2;
    private static final double BANDWIDTH = 1000;
    private static final int    FILE_SIZE = 5000;
    private static final double FILE_TRANSFER_TIME = FILE_SIZE/ BANDWIDTH + NETWORK_LATENCY;
    private static final String FILE1 = "file1.txt";

    @Test
    public void testAddReservedFile() {
        final var san = createSanStorageBw(BANDWIDTH);
        final var file = new File(FILE1, FILE_SIZE);
        assertTrue(san.reserveSpace(file.getSize()));
        assertTrue(san.addReservedFile(file) > FILE_TRANSFER_TIME);
    }

    private static SanStorage createSanStorageBw(final double bandwidth){
        return createSanStorage(bandwidth, NETWORK_LATENCY);
    }

    private static SanStorage createSanStorage(){
        return createSanStorage(CAPACITY);
    }

    private static SanStorage createSanStorage(final long capacity){
        return createSanStorage(BANDWIDTH, NETWORK_LATENCY, capacity);
    }

    private static SanStorage createSanStorage(final double bandwidth, final double networkLatency){
        return createSanStorage(bandwidth, networkLatency, CAPACITY);
    }

    private static SanStorage createSanStorage(final double bandwidth, final double networkLatency, final long capacity){
        return new SanStorage(capacity, bandwidth, networkLatency);
    }

    @Test
    public void testGetMaxTransferRate1() {
        final double diskRateMbps = 10;
        final double bwMbps = diskRateMbps*100;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, san.getMaxTransferRate());
    }

    @Test
    public void testGetMaxTransferRate2() {
        final double bwMbps = 10;
        final double diskRateMbps = 10*bwMbps;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        assertEquals(diskRateMbps, san.getMaxTransferRate());
    }

    @Test
    public void testGetTransferTimeWhenDiskRateAndBwAreEqual() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 162;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenDiskReadTimeIsNegligible() {
        final double bwMbps = 10;
        final double diskRateMbps = 1000000;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 82.0008;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenNetworkTransferTimeIsNegligible() {
        final double bwMbps = 1000000;
        final double diskRateMbps = 10;
        final double latency = 0.0001;
        final var san = new SanStorage(CAPACITY, bwMbps, latency);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 80.0009;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenBwIsLowerThanDiskRate() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps*10;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 90;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenBwIsLowerThanDiskRateAndLatencyIsAlmostZero() {
        final double bwMbps = 10;
        final double diskRateMbps = bwMbps*10;
        final double latency = 0.0001;
        final var san = new SanStorage(CAPACITY, bwMbps, latency);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 88.0;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenDiskRateIsLowerThanBw() {
        final double diskRateMbps = 10;
        final double bwMbps = diskRateMbps*100;
        final var san = new SanStorage(CAPACITY, bwMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(diskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 82.8;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.01);
    }

    @Test
    public void testGetTransferTimeWhenBwIsEqualToDiskRate() {
        final double bwAndDiskRateMbps = 10;
        final var san = new SanStorage(CAPACITY, bwAndDiskRateMbps, NETWORK_LATENCY);
        san.setMaxTransferRate(bwAndDiskRateMbps);

        final int fileSizeMB = 100;
        //Includes storage device read time, network transfer time plus network latency
        final double expectedTime = 162;
        assertEquals(expectedTime, san.getTransferTime(fileSizeMB), 0.1);
    }

    @Test
    public void testNewNamedSanStorage() {
        final String name = "san1";
        final var san = new SanStorage(name, CAPACITY, BANDWIDTH, NETWORK_LATENCY);
        assertEquals(name, san.getName());
    }

    @Test
    public void testAddFileWhenParamIsFile() {
        final var san = createSanStorageBw(BANDWIDTH);
        assertThrows(NullPointerException.class, () -> san.addFile((File)null));
        final var validFile = new File(FILE1, FILE_SIZE);
        assertTrue(san.addFile(validFile) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testAddFileWhenParamIsList() {
        final var san = createSanStorageBw(BANDWIDTH);
        final var fileList = SanStorageTest.createFileList(TOTAL_FILES, FILE_SIZE);
        final double transferTimeOfAllFiles = FILE_TRANSFER_TIME * TOTAL_FILES;
        assertTrue(san.addFile(fileList) > transferTimeOfAllFiles);
    }

    @Test
    public void testDeleteFileWhenParamIsFile() {
        final var san = createSanStorageBw(BANDWIDTH);
        final var file = new File(FILE1, FILE_SIZE);
        san.addFile(file);
        assertTrue(san.deleteFile(file) > FILE_TRANSFER_TIME);
    }

    @Test
    public void testGetBandwidth() {
        final var san = createSanStorageBw(BANDWIDTH);
        assertEquals(BANDWIDTH, san.getBandwidth());
    }

    @Test
    public void testGetNetworkLatency() {
        final var san = createSanStorage(BANDWIDTH, NETWORK_LATENCY);
        assertEquals(NETWORK_LATENCY, san.getNetworkLatency());
    }

    /**
     * Creates a list of ficticious numbered File instances
     * @param totalFiles The number of files to be created
     * @param fileSize the size of each file
     * @return
     */
    public static List<File> createFileList(final int totalFiles, final int fileSize) {
        final var fileList = new ArrayList<File>();
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
        final var fileName = "file%d.txt".formatted(fileNumber);
        return new File(fileName, fileSize);
    }

    @Test
    public void testGetNumStoredFile1() {
        final var san = createSanStorageBw(BANDWIDTH);
        assertEquals(0, san.getNumStoredFile());

        final int totalFiles = 2;
        for(int i = 1; i <= totalFiles; i++){
            assertTrue(san.addFile(createNumberedFile(i, FILE_SIZE))>0);
        }
        assertEquals(totalFiles, san.getNumStoredFile());
    }

    @Test
    public void testIsFull() {
        final int numberOfFiles = (int)(CAPACITY/FILE_SIZE);
        final var san = createSanStorage(CAPACITY+FILE_SIZE);
        IntStream.range(0, numberOfFiles).forEach(id -> {
            san.addFile(createNumberedFile(id, FILE_SIZE));
            assertFalse(san.isFull());
        });

        san.addFile(createNumberedFile(numberOfFiles, FILE_SIZE));
        assertTrue(san.isFull());
    }

    @Test
    public void testGetNumStoredFile2() {
        final var san = createSanStorageBw(BANDWIDTH);
        assertEquals(0, san.getNumStoredFile());

        final int totalFiles = 4;
        san.addFile(createFileList(totalFiles, FILE_SIZE));
        assertEquals(totalFiles, san.getNumStoredFile());
    }

    @Test()
    public void testGetNumStoredFileWhenNullList() {
        final var san = createSanStorageBw(BANDWIDTH);
        assertThrows(NullPointerException.class, () -> san.addFile((List<File>) null));
    }


    @Test
    public void testReserveSpace1() {
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final long available = fileSize;
        final var san = createSanStorage(capacity);

        assertThrows(NullPointerException.class, () -> san.addFile((File)null));

        int fileNumber = 0;
        File file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(san.addFile(file) > 0.0);
        assertEquals(available, san.getAvailableResource());

        assertTrue(san.reserveSpace(fileSize));
        /*there isn't more available space for the new file,
        because the available space was reserved*/
        assertEquals(0, san.getAvailableResource());
        file = createNumberedFile(++fileNumber, fileSize);
        assertFalse(san.addFile(file) > 0.0);

        //adds the file that the space was previously reserved
        assertTrue(san.addReservedFile(file)> 0);

        assertFalse(san.reserveSpace(FILE_SIZE*10));
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
        final var san = createSanStorage(capacity);
        assertEquals(0, san.getAllocatedResource());

        int fileNumber = 0;
        File file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(san.addFile(file) > 0.0);
        assertEquals(available, san.getAvailableResource());

        //file larger than the available capacity
        assertEquals(0, san.addFile(new File("too-big-file.txt", FILE_SIZE*10)));

        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(san.reserveSpace(fileSize));
        file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(san.addReservedFile(file) > 0);
        assertEquals(0, san.getAvailableResource());
    }

    @Test()
    public void testAddFileWhenNullFile() {
        final var san = createSanStorage(1);
        assertThrows(NullPointerException.class, () -> san.addReservedFile(null));
    }


    @Test
    public void testAddReservedFileWhenSpaceNotPreReserved() {
        final var san = createSanStorage(CAPACITY);
        try{
            san.addReservedFile(new File(FILE1, 100));
            fail(NO_PREVIOUS_SPACE);
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(CAPACITY, san.getAvailableResource());
        }
    }

    @Test
    public void testAddReservedFileWhenFileAlreadyAdded() {
        final var san = createSanStorage(CAPACITY);
        final var file = new File(FILE1, 100);
        san.reserveSpace(file.getSize());
        san.addReservedFile(file);
        san.reserveSpace(file.getSize());
        assertFalse(san.addReservedFile(file) > 0);
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
        final var san = createSanStorage(capacity);
        assertEquals(0, san.getAllocatedResource());

        final File file = createNumberedFile(1, fileSize);
        try{
            san.addReservedFile(file);
            fail(NO_PREVIOUS_SPACE);
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(available, san.getAvailableResource());
            assertEquals(0, san.getAllocatedResource());
        }

        available = (long)fileSize;
        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(san.reserveSpace(fileSize));
        assertTrue(san.addReservedFile(file) > 0);
        assertEquals(available, san.getAvailableResource());
        assertEquals(available, san.getAllocatedResource());
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
        final var san = createSanStorage(capacity);
        assertEquals(0, san.getAllocatedResource());

        final File file = createNumberedFile(1, fileSize);
        assertTrue(san.reserveSpace(halfFileSize));
        try{
            san.addReservedFile(file);
            fail("The reserved file was added but its space was not totally reserved before.");
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added because not the entire space was previously
            reserved.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(available, san.getAvailableResource());
            assertEquals(available,  san.getAllocatedResource());
        }

        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(san.reserveSpace(halfFileSize));
        assertTrue(san.addReservedFile(file) > 0);
        assertEquals(0, san.getAvailableResource());
        assertEquals(capacity, san.getAllocatedResource());
    }

    @Test
    public void testHasPotentialAvailableSpace() {
        final long fileSize = CAPACITY;
        final var san = createSanStorage();
        assertTrue(san.hasPotentialAvailableSpace(fileSize));
        assertFalse(san.hasPotentialAvailableSpace(fileSize*100));
    }

    @Test
    public void testHasPotentialAvailableSpaceWhenInvalidValue() {
        final var san = createSanStorage();
        assertFalse(san.hasPotentialAvailableSpace(0));
        assertFalse(san.hasPotentialAvailableSpace(-1));
    }

    @Test
    public void testGetFileAfterAddFile() {
        final var san = createSanStorage();
        final var fileList = createListOfFilesAndAddToHardDrive(san);
        //try to add the same files
        assertFalse(san.addFile(fileList) > 0);

        //try to add already existing files, one by one
        fileList.forEach(file -> assertFalse(san.addFile(file) > 0));
        fileList.forEach(file -> assertEquals(Optional.of(file), san.getFile(file.getName())));
        assertEquals(Optional.empty(), san.getFile(NON_EXISTENT_FILE));
    }

    @Test
    public void testGetFileList() {
        final var san = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(san);
        san.addFile(fileList);
        assertEquals(fileList, san.getFileList());
    }

    @Test
    public void testGetFileWhenInvalidFile() {
        final var san = createSanStorage();
        assertThrows(IllegalArgumentException.class, () -> san.getFile("   "));
        assertThrows(IllegalArgumentException.class, () -> san.getFile(""));
        assertThrows(NullPointerException.class, () -> san.getFile(null));
    }

    /**
     * Adds and internally created file list to a Hard Drive
     * @param instance the Hard Drive to add the files
     * @return the list of created files
     */
    private List<File> createListOfFilesAndAddToHardDrive(SanStorage instance) {
        final var fileList = createFileList(TOTAL_FILES_TO_CREATE, FILE_SIZE);
        assertTrue(instance.addFile(fileList)>0);
        return fileList;
    }

    @Test
    public void testGetFileNameList() {
        final var san = createSanStorage();
        final List<String> fileNameList = new ArrayList<>();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(san);

        fileList.forEach(file -> fileNameList.add(file.getName()));
        assertEquals(fileNameList, san.getFileNameList());
    }

    @Test
    public void testDeleteFileWheParamString() {
        final var san = createSanStorage();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(san);

        fileList.forEach(file ->  assertEquals(Optional.of(file), san.deleteFile(file.getName())));

        assertThrows(IllegalArgumentException.class, () -> san.deleteFile(""));
        assertEquals(Optional.empty(), san.deleteFile(NON_EXISTENT_FILE));
    }

    @Test
    public void testContainsWhenParamString() {
        final var san = createSanStorage();
        final var fileList = createListOfFilesAndAddToHardDrive(san);

        fileList.forEach(file -> assertTrue(san.contains(file.getName())));

        assertFalse(san.contains(NON_EXISTENT_FILE));
        assertFalse(san.contains(""));
    }

    @Test
    public void testContainsWhenParamIsFile() {
        final var san = createSanStorage();
        final var fileList = createListOfFilesAndAddToHardDrive(san);

        fileList.forEach(file -> assertTrue(san.contains(file)));
        assertFalse(san.contains(new File(NON_EXISTENT_FILE, FILE_SIZE)));
    }

    @Test
    public void testRenameFile() {
        final var san = createSanStorage();
        final var fileList = createListOfFilesAndAddToHardDrive(san);
        for(final File file: fileList){
            final String oldName = file.getName();
            final String newName = "renamed-%s".formatted(oldName);
            assertTrue(san.contains(oldName));
            assertTrue(san.renameFile(file, newName));
            assertFalse(san.contains(oldName));

            final Optional<File> optionalResult = san.getFile(newName);
            assertEquals(Optional.of(file), optionalResult);
            assertEquals(file.getName(), optionalResult.get().getName());
        }

        final var file1 = new File(FILE1, 100);
        final var file2 = new File("file2.txt", 100);
        san.addFile(file1);
        san.addFile(file2);
        assertFalse(san.renameFile(file1, file2.getName()));

        final File notAddedFile = new File("file3.txt", 100);
        assertFalse(san.renameFile(notAddedFile, "new-name.txt"));
    }

    @Test
    public void testIsResourceAmountAvailable() {
        final var san = createSanStorage();

        assertTrue(san.isAmountAvailable(CAPACITY));
        final var file = new File(FILE1, (int)CAPACITY);
        assertTrue(san.addFile(file) > 0);
        assertFalse(san.isAmountAvailable(CAPACITY));
        assertTrue(san.deleteFile(file) > 0);
        assertTrue(san.isAmountAvailable(CAPACITY));
    }
}
