package org.cloudbus.cloudsim.resources;

import org.apache.commons.lang3.StringUtils;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
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
public class HarddriveStorageTest {
    private static final String NO_PREVIOUS_SPACE = "The reserved file was added but its space was not previously reserved.";
    private static final int CAPACITY = 1000;
    private static final int FILE_SIZE = 100;
    private static final int TOTAL_FILES_TO_CREATE = 5;
    private static final String INEXISTENT_FILE = "inexistent-file.txt";
    private static final String FILE1 = "file1.txt";

    @Test()
    public void testNewHarddriveStorageWhenOnlyWhiteSpacesName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("   ", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenEmptyName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWheNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage(null, CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenNegativeSize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage(-1));
    }

    @Test
    public void testNewHarddriveStorageWhenZeroSize() {
        final int expResult = 0;
        final HarddriveStorage hd = new HarddriveStorage(expResult);
        assertEquals(expResult, hd.getCapacity());
    }

    @Test
    public void testGetNumStoredFile1() {
        final HarddriveStorage instance = createHardDrive();
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
        final HarddriveStorage instance = createHardDrive(CAPACITY+FILE_SIZE);
        IntStream.range(0, numberOfFiles).forEach(id -> {
            instance.addFile(createNumberedFile(id, FILE_SIZE));
            assertFalse(instance.isFull());
        });

        instance.addFile(createNumberedFile(numberOfFiles, FILE_SIZE));
        assertTrue(instance.isFull());
    }

    @Test
    public void testGetNumStoredFile2() {
        final HarddriveStorage instance = createHardDrive();
        assertEquals(0, instance.getNumStoredFile());

        final int totalFiles = 4;
        instance.addFile(createFileList(totalFiles, FILE_SIZE));
        assertEquals(totalFiles, instance.getNumStoredFile());
    }

    @Test()
    public void testGetNumStoredFileWhenNullList() {
        final HarddriveStorage instance = createHardDrive();
        Assertions.assertThrows(NullPointerException.class, () -> instance.addFile((List<File>) null));
    }

    @Test
    public void testGetCapacity() {
        final HarddriveStorage instance = createHardDrive(CAPACITY);
        assertEquals(CAPACITY, instance.getCapacity());
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
    public void testReserveSpace1() {
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final long available = (long)fileSize;
        final HarddriveStorage instance = createHardDrive(capacity);

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
        final long available = (long)fileSize;
        final HarddriveStorage instance = createHardDrive(capacity);
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
        final HarddriveStorage instance = createHardDrive(1);
        Assertions.assertThrows(NullPointerException.class, () -> instance.addReservedFile(null));
    }

    @Test
    public void testGetTransferTime() {
        final HarddriveStorage instance = createHardDrive(1);
        final int fileSizeInMB = 100;
        final int maxTransferRateInMbitsSec = 10;
        final int latencyInSec = 1;
        final int expectedSecs = 81;
        instance.setLatency(latencyInSec);
        instance.setMaxTransferRate(maxTransferRateInMbitsSec);

        assertEquals(expectedSecs, instance.getTransferTime(fileSizeInMB));
    }

    @Test
    public void testAddReservedFileWhenSpaceNotPreReserved() {
        final HarddriveStorage instance = createHardDrive(CAPACITY);
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
        final HarddriveStorage instance = createHardDrive(CAPACITY);
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
        final HarddriveStorage instance = createHardDrive(capacity);
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
        final long capacity = (long)fileSize;
        final long available = (long)halfFileSize;
        final HarddriveStorage instance = createHardDrive(capacity);
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
        final int fileSize = 100;
        final HarddriveStorage instance = createHardDrive();
        assertTrue(instance.hasPotentialAvailableSpace(fileSize));
        assertFalse(instance.hasPotentialAvailableSpace(fileSize*1000));
    }

    @Test
    public void testHasPotentialAvailableSpaceWhenInvalidValue() {
        final HarddriveStorage instance = createHardDrive();
        assertFalse(instance.hasPotentialAvailableSpace(0));
        assertFalse(instance.hasPotentialAvailableSpace(-1));
    }

    @Test
    public void testGetName() {
        final String expResult = "hd1";
        final HarddriveStorage instance = createHardDrive(CAPACITY, expResult);
        assertEquals(expResult, instance.getName());
    }

    @Test()
    public void testSetLatencyNegative() {
        final HarddriveStorage instance = createHardDrive();
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.setLatency(-1));
    }

    @Test
    public void testSetLatency0() {
        final HarddriveStorage instance = createHardDrive();
        final int expected = 0;
        instance.setLatency(expected);
        assertEquals(expected, instance.getLatency());
    }

    @Test
    public void testSetLatency1() {
        final HarddriveStorage instance = createHardDrive();
        final double latency = 1;
        instance.setLatency(latency);
        assertEquals(latency, instance.getLatency());
    }

    @Test
    public void testSetMaxTransferRate1() {
        final HarddriveStorage instance = createHardDrive();
        final int rate = 1;
        instance.setMaxTransferRate(rate);
        assertEquals(rate, instance.getMaxTransferRate());
    }

    @Test()
    public void testSetMaxTransferRateNegative() {
        final HarddriveStorage instance = createHardDrive();
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(-1));
    }

    @Test()
    public void testSetMaxTransferRate0() {
        final HarddriveStorage instance = createHardDrive();
        Assertions.assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(0));
    }

    @Test
    public void testSetAvgSeekTimeWhenDouble() {
        testSetAvgSeekTime(null);
    }

    @Test
    public void testSetAvgSeekTimeWhenDoubleContinuousDistribution() {
        final double anyValue = 2.4;
        testSetAvgSeekTime(new ExponentialDistr(anyValue));
    }

    /**
     * Private method called by the overloaded versions of the
     * setAvgSeekTime method.
     * @param gen A random number generator. The parameter can be
     * null in order to use the simpler version of the setAvgSeekTime.
     */
    private void testSetAvgSeekTime(final ContinuousDistribution gen) {
        final HarddriveStorage instance = createHardDrive();
        final double seekTime = 1;
        assertAll(
            () -> assertTrue(setAvgSeekTime(instance, seekTime, gen)),
            () -> assertEquals(seekTime, instance.getAvgSeekTime()),
            () -> assertFalse(setAvgSeekTime(instance, 0, gen)),
            () -> assertEquals(seekTime, instance.getAvgSeekTime()),
            () -> assertFalse(setAvgSeekTime(instance, -1, gen)),
            () -> assertEquals(seekTime, instance.getAvgSeekTime())
        );
    }

    private static boolean setAvgSeekTime(
            final HarddriveStorage instance, final double seekTime,
            final ContinuousDistribution gen) {
        if(gen != null) {
            return instance.setAvgSeekTime(seekTime, gen);
        }

        return instance.setAvgSeekTime(seekTime);
    }

    @Test
    public void testGetFileAfterAddFile() {
        final HarddriveStorage instance = createHardDrive();
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
        final HarddriveStorage instance = createHardDrive();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        instance.addFile(fileList);
        assertEquals(fileList, instance.getFileList());
    }

    @Test
    public void testGetFileWhenInvalidFile() {
        final HarddriveStorage instance = createHardDrive();
        assertEquals(null, instance.getFile("   "));
        assertEquals(null, instance.getFile(""));
        assertEquals(null, instance.getFile(null));
    }

    /**
     * Adds and internally created file list to a Hard Drive
     * @param instance the Hard Drive to add the files
     * @return the list of created files
     */
    private List<File> createListOfFilesAndAddToHardDrive(HarddriveStorage instance) {
        final List<File> fileList = createFileList(TOTAL_FILES_TO_CREATE, FILE_SIZE);
        assertTrue(instance.addFile(fileList)>0);
        return fileList;
    }

    @Test
    public void testGetFileNameList() {
        final HarddriveStorage instance = createHardDrive();
        final List<String> fileNameList = new ArrayList<>();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> fileNameList.add(file.getName()));
        assertEquals(fileNameList, instance.getFileNameList());
    }

    @Test
    public void testDeleteFileWheParamString() {
        final HarddriveStorage instance = createHardDrive();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file ->  assertEquals(file, instance.deleteFile(file.getName())));

        assertEquals(null, instance.deleteFile(""));
        assertEquals(null, instance.deleteFile(INEXISTENT_FILE));
    }

    @Test
    public void testDeleteFileWhenParamIsFile() {
        final HarddriveStorage instance = createHardDrive();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> assertTrue(instance.deleteFile(file)>0));

        final File nullFile = null;
        assertEquals(0.0, instance.deleteFile(nullFile));
    }

    @Test
    public void testContainsWhenParamString() {
        final HarddriveStorage instance = createHardDrive();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> assertTrue(instance.contains(file.getName())));

        assertFalse(instance.contains(INEXISTENT_FILE));
        final String nullStr = null;
        assertFalse(instance.contains(nullStr));
        assertFalse(instance.contains(""));
    }

    @Test
    public void testContainsWhenParamIsFile() {
        final HarddriveStorage instance = createHardDrive();
        final List<File> fileList = createListOfFilesAndAddToHardDrive(instance);

        fileList.forEach(file -> assertTrue(instance.contains(file)));

        assertFalse(instance.contains(new File(INEXISTENT_FILE, FILE_SIZE)));
        final File nullFile = null;
        assertFalse(instance.contains(nullFile));
    }

    @Test
    public void testRenameFile() {
        final HarddriveStorage instance = createHardDrive();
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
        final HarddriveStorage instance = createHardDrive();
        final int capacity = CAPACITY;

        assertTrue(instance.isAmountAvailable(capacity));
        final File file = new File(FILE1, capacity);
        assertTrue(instance.addFile(file)>0);
        assertFalse(instance.isAmountAvailable(capacity));
        assertTrue(instance.deleteFile(file)>0);
        assertTrue(instance.isAmountAvailable(capacity));
    }

    /**
     * Creates a hard drive with the {@link #CAPACITY} capacity.
     * @return
     */
    private HarddriveStorage createHardDrive() {
        return createHardDrive(CAPACITY);
    }

    private HarddriveStorage createHardDrive(final long capacity) {
        return createHardDrive(capacity, "");
    }

    private HarddriveStorage createHardDrive(final long capacity, final String name) {
        if(StringUtils.isBlank(name)) {
            return new HarddriveStorage(capacity);
        }

        return new HarddriveStorage(name, capacity);
    }
}
