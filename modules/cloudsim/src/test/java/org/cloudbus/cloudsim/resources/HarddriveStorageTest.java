package org.cloudbus.cloudsim.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HarddriveStorageTest {
    private static final Long CAPACITY = 1000L;
    private static final int FILE_SIZE = 100;
    private static final Long ZERO = 0L;
    private static final int TOTAL_FILES_TO_CREATE = 5;
    
    @Test(expected = IllegalArgumentException.class)
    public void testNewHarddriveStorage_onlyWhiteSpacesName() {
        new HarddriveStorage("   ", CAPACITY);
    }        

    @Test(expected = IllegalArgumentException.class)
    public void testNewHarddriveStorage_emptyName() {
        new HarddriveStorage("", CAPACITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewHarddriveStorage_nullName() {
        new HarddriveStorage(null, CAPACITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewHarddriveStorage_negativeSize() {
        new HarddriveStorage(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewHarddriveStorage_zeroSize() {
        new HarddriveStorage(0);
    }

    @Test
    public void testGetNumStoredFile1() {
        System.out.println("getNumStoredFile");
        HarddriveStorage instance = createHardDrive();
        assertEquals(0, instance.getNumStoredFile());
        
        final int totalFiles = 2;
        for(int i = 1; i <= totalFiles; i++){
            assertTrue(instance.addFile(createNumberedFile(i, FILE_SIZE))>0);
        }
        assertEquals(totalFiles, instance.getNumStoredFile());
    }
    
    @Test
    public void testIsFull() {
        System.out.println("testIsFull");
        int numberOfFiles = (int)(CAPACITY/FILE_SIZE);
        HarddriveStorage instance = createHardDrive(CAPACITY+FILE_SIZE);
        IntStream.range(0, numberOfFiles).forEach(i -> {
            instance.addFile(createNumberedFile(i, FILE_SIZE));
            assertFalse(instance.isFull());
        });
        
        instance.addFile(createNumberedFile(numberOfFiles, FILE_SIZE));
        assertTrue(instance.isFull());   
    }

    @Test
    public void testGetNumStoredFile2() {
        HarddriveStorage instance = createHardDrive();
        System.out.println("getNumStoredFile");
        assertEquals(0, instance.getNumStoredFile());
        
        final int totalFiles = 4;
        instance.addFile(createFileList(totalFiles, FILE_SIZE));
        assertEquals(totalFiles, instance.getNumStoredFile());
        
        final List<File> nullList = null;
        assertEquals(0, instance.addFile(nullList), 0.0);
    }

    @Test
    public void testGetCapacity() {
        HarddriveStorage instance = createHardDrive(CAPACITY);
        assertEquals(CAPACITY, instance.getCapacity());
    }

    /**
     * Creates a list of ficticious numbered File instances
     * @param totalFiles The number of files to be created
     * @param fileSize the size of each file
     * @return 
     */
    public static List<File> createFileList(final int totalFiles, final int fileSize) {
        List<File> fileList = new ArrayList<>();
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
        File file = new File(String.format("file%d.txt", fileNumber), fileSize);
        System.out.printf("File created: %s. Size: %d\n", file.getName(), file.getSize());
        return file;
    }
    
    @Test
    public void testReserveSpace1() {
        System.out.println("reserveSpace1");
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final Long available = (long)fileSize;
        HarddriveStorage instance = createHardDrive(capacity);
        
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
        assertEquals(ZERO, instance.getAvailableResource());
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
        System.out.println("testReserveSpaceNotReservedFile1");
        final int fileSize = FILE_SIZE;
        final long capacity = fileSize * 2;
        final Long available = (long)fileSize;
        HarddriveStorage instance = createHardDrive(capacity);
        assertEquals(ZERO, instance.getAllocatedResource());

        int fileNumber = 0;
        File file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(instance.addFile(file) > 0.0);
        assertEquals(available, instance.getAvailableResource());
        
        //a null file cannot be added
        assertEquals(0, instance.addReservedFile(null), 0.0);
        
        //file larger than the available capacity
        assertEquals(0, instance.addFile(new File("too-big-file.txt", FILE_SIZE*10)), 0.0);
        
        //accordingly, reserves space previously and then adds the reserved file
        assertTrue(instance.reserveSpace(fileSize));
        file = createNumberedFile(++fileNumber, fileSize);
        assertTrue(instance.addReservedFile(file) > 0);
        assertEquals(ZERO, instance.getAvailableResource());
    }

    @Test
    public void testAddReservedFile_spaceNotPreReserved() {
        HarddriveStorage instance = createHardDrive(CAPACITY);
        try{
            instance.addReservedFile(new File("file1.txt", 100));
            fail("The reserved file was added but its space was not previously reserved.");
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(CAPACITY, instance.getAvailableResource());
        }
    }
    
    @Test
    public void testAddReservedFile_tryToAddAlreadAddedReservedFile() {
        HarddriveStorage instance = createHardDrive(CAPACITY);
        final File file = new File("file1.txt", 100);
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
        System.out.println("testReserveSpaceNotReservedFile2");
        final int fileSize = FILE_SIZE;
        final Long capacity = fileSize * 2L;
        Long available = capacity;
        HarddriveStorage instance = createHardDrive(capacity);
        assertEquals(ZERO, instance.getAllocatedResource());

        File file = createNumberedFile(1, fileSize);
        try{
            instance.addReservedFile(file);
            fail("The reserved file was added but its space was not previously reserved.");
        } catch(Exception e){
            /*if the exception was thrown, indicates that the file
            was accordingly not added.
            Now, checks the available space to see if remains unchanged.*/
            assertEquals(available, instance.getAvailableResource());
            assertEquals(ZERO, instance.getAllocatedResource());
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
        System.out.println("testReserveSpaceNotReservedFile2");
        final int fileSize = FILE_SIZE;
        final int halfFileSize = fileSize/2;
        final Long capacity = (long)fileSize;
        final Long available = (long)halfFileSize;
        HarddriveStorage instance = createHardDrive(capacity);
        assertEquals(ZERO, instance.getAllocatedResource());

        File file = createNumberedFile(1, fileSize);
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
        assertEquals(ZERO, instance.getAvailableResource());
        assertEquals(capacity, instance.getAllocatedResource());
    }    
    
    @Test
    public void testHasPotentialAvailableSpace() {
        System.out.println("hasPotentialAvailableSpace");
        int fileSize = 100;
        HarddriveStorage instance = createHardDrive();
        assertTrue(instance.hasPotentialAvailableSpace(fileSize));
        assertFalse(instance.hasPotentialAvailableSpace(fileSize*1000));
    }

    @Test
    public void testHasPotentialAvailableSpace_invalidValue() {
        System.out.println("testHasPotentialAvailableSpace_invalidValue");
        HarddriveStorage instance = createHardDrive();
        assertFalse(instance.hasPotentialAvailableSpace(0));
        assertFalse(instance.hasPotentialAvailableSpace(-1));
    }

    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "hd1";
        HarddriveStorage instance = createHardDrive(CAPACITY, expResult);
        assertEquals(expResult, instance.getName());
    }

    @Test
    public void testSetLatency() {
        System.out.println("setLatency");
        HarddriveStorage instance = createHardDrive();
        double latency = 1;
        assertTrue(instance.setLatency(latency));
        assertEquals(latency, instance.getLatency(), 0);
        
        assertFalse(instance.setLatency(-1));
        assertEquals(latency, instance.getLatency(), 0);

        latency = 0;
        assertTrue(instance.setLatency(latency));
        assertEquals(latency, instance.getLatency(), 0);
    }

    @Test
    public void testSetMaxTransferRate() {
        System.out.println("setMaxTransferRate");
        HarddriveStorage instance = createHardDrive();
        int rate = 1;
        assertTrue(instance.setMaxTransferRate(rate));
        assertEquals(rate, instance.getMaxTransferRate(), 0);
        
        assertFalse(instance.setMaxTransferRate(-1));
        assertEquals(rate, instance.getMaxTransferRate(), 0);

        assertFalse(instance.setMaxTransferRate(0));
        assertEquals(rate, instance.getMaxTransferRate(), 0);
        
        rate = 2;
        assertTrue(instance.setMaxTransferRate(rate));
        assertEquals(rate, instance.getMaxTransferRate(), 0);
    }

    @Test
    public void testSetAvgSeekTime_double() {
        System.out.println("setAvgSeekTime");
        testSetAvgSeekTime(null);
    }

    @Test
    public void testSetAvgSeekTime_double_ContinuousDistribution() {
        System.out.println("setAvgSeekTime");
        final double anyValue = 2.4;
        testSetAvgSeekTime(new ExponentialDistr(anyValue));
    }

    /**
     * Private method called by the overloaded versions of the 
     * setAvgSeekTime method.
     * @param gen A random number generator. The parameter can be
     * null in order to use the simpler version of the setAvgSeekTime.
     */
    private void testSetAvgSeekTime(ContinuousDistribution gen) {
        HarddriveStorage instance = createHardDrive();
        double seekTime = 1;
        assertTrue(setAvgSeekTime(instance, seekTime, gen));
        assertEquals(seekTime, instance.getAvgSeekTime(), 0);
        
        assertFalse(setAvgSeekTime(instance, 0, gen));
        assertEquals(seekTime, instance.getAvgSeekTime(), 0);
        
        assertFalse(setAvgSeekTime(instance, -1, gen));
        assertEquals(seekTime, instance.getAvgSeekTime(), 0);
        
        seekTime = 2;
        assertTrue(setAvgSeekTime(instance, seekTime, gen));
        assertEquals(seekTime, instance.getAvgSeekTime(), 0);
    }

    private static boolean setAvgSeekTime(
            final HarddriveStorage instance, final double seekTime, 
            final ContinuousDistribution gen) {
        if(gen != null)
            return instance.setAvgSeekTime(seekTime, gen);
        
        return instance.setAvgSeekTime(seekTime);
    }

    @Test
    public void testGetFile_addFile() {
        System.out.println("getFile");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        //try to add the same files
        assertFalse(instance.addFile(fileList)>0);
        
        //try to add already existing files, one by one
        fileList.forEach(f -> assertFalse(instance.addFile(f)>0));

        fileList.forEach(f -> assertEquals(f, instance.getFile(f.getName())));
        
        assertEquals(null, instance.getFile("inexistent-file.txt"));
    }

    @Test
    public void testGetFileList() {
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        instance.addFile(fileList);        
        assertEquals(fileList, instance.getFileList());
    }

    @Test
    public void testGetFile_invalidFile() {
        HarddriveStorage instance = createHardDrive();
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
        System.out.println("getFileNameList");
        HarddriveStorage instance = createHardDrive();
        final List<String> fileNameList = new ArrayList<>();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        
        fileList.forEach(f -> fileNameList.add(f.getName()));
        assertEquals(fileNameList, instance.getFileNameList());
    }

    @Test
    public void testDeleteFile_String() {
        System.out.println("testDeleteFile_String");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        
        fileList.forEach(f ->  assertEquals(f, instance.deleteFile(f.getName())));
        
        assertEquals(null, instance.deleteFile(""));
        assertEquals(null, instance.deleteFile("inexistent-file.txt"));
    }

    @Test
    public void testDeleteFile_File() {
        System.out.println("testDeleteFile_File");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        
        fileList.forEach(f-> assertTrue(instance.deleteFile(f)>0));
        
        final File nullFile = null;
        assertEquals(0.0, instance.deleteFile(nullFile), 0.0);
    }

    @Test
    public void testContains_String() {
        System.out.println("contains");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        
        fileList.forEach(f -> assertTrue(instance.contains(f.getName())));
        
        assertFalse(instance.contains("inexistent-file.txt"));
        final String nullStr = null;
        assertFalse(instance.contains(nullStr));
        assertFalse(instance.contains(""));
    }

    @Test
    public void testContains_File() {
        System.out.println("contains");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        
        fileList.forEach(f -> assertTrue(instance.contains(f)));
        
        assertFalse(instance.contains(new File("inexistent-file.txt", FILE_SIZE)));
        final File nullFile = null;
        assertFalse(instance.contains(nullFile));
    }

    @Test
    public void testRenameFile() {
        System.out.println("renameFile");
        HarddriveStorage instance = createHardDrive();
        List<File> fileList = createListOfFilesAndAddToHardDrive(instance);
        for(File file: fileList){
            final String oldName = file.getName(), newName = String.format("renamed-%s", oldName);
            assertTrue(instance.contains(oldName));
            assertTrue(instance.renameFile(file, newName));
            assertFalse(instance.contains(oldName));
            
            final File result = instance.getFile(newName);
            assertEquals(file, result);
            assertEquals(file.getName(), result.getName());
        }
        
        File file1 = new File("file1.txt", 100), file2 = new File("file2.txt", 100);
        instance.addFile(file1);
        instance.addFile(file2);
        assertFalse(instance.renameFile(file1, file2.getName()));
        
        File notAddedFile = new File("file3.txt", 100);
        assertFalse(instance.renameFile(notAddedFile, "new-name.txt"));
    }

    @Test
    public void testIsResourceAmountAvailable() {
        System.out.println("isResourceAmountAvailable");
        HarddriveStorage instance = createHardDrive();
        final Long capacity = CAPACITY;
        
        assertTrue(instance.isResourceAmountAvailable(capacity));
        final File file = new File("file1.txt", capacity.intValue());
        assertTrue(instance.addFile(file)>0);
        assertFalse(instance.isResourceAmountAvailable(capacity));
        assertTrue(instance.deleteFile(file)>0);
        assertTrue(instance.isResourceAmountAvailable(capacity));
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
    
    private HarddriveStorage createHardDrive(final long capacity, String name) {
        if(name == null || name.trim().isEmpty())
            return new HarddriveStorage(capacity);
        return new HarddriveStorage(name, capacity);
    }
}
