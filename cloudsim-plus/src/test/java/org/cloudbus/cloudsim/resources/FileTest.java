package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.DataCloudTags;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class FileTest {
    private final String NAME = "file1.txt";
    private final int SIZE = 100;
    
    private File createFile(){
        return createFile(NAME);
    }
    
    private File createFile(final String name){
        return new File(name, SIZE);
    }

    private File createFile(final int size){
        return new File(NAME, size);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile_nullFileParameter() {
        File nullFile = null;
        new File(nullFile);
    }

    @Test()
    public void testCreateFile_FileParameter() {
        File originalFile = new File("test1.txt", 100);
        File copyFile = new File(originalFile);
        assertFalse(copyFile.isMasterCopy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_nullName() {
        new File(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_zeroSize() {
        new File("file1", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_negativeSize() {
        new File("file1", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile_emptyName() {
        new File("", 100);
    }

    @Test
    public void testMakeReplica() {
        System.out.println("makeReplica");
        File instance = createFile();
        File replica = instance.makeReplica();
        assertEquals(instance.getName(), replica.getName());
        assertEquals(instance.getSize(), replica.getSize());
        assertTrue(instance.isMasterCopy());
        assertFalse(replica.isMasterCopy());
    }

    @Test
    public void testMakeMasterCopy() {
        System.out.println("makeMasterCopy");
        File instance = createFile();
        File replica = instance.makeMasterCopy();
        assertEquals(instance.getName(), replica.getName());
        assertEquals(instance.getSize(), replica.getSize());
        assertTrue(instance.isMasterCopy());
        assertTrue(replica.isMasterCopy());
    }

    @Test
    public void testGetAttributeSize() {
        System.out.println("getAttributeSize");
        File instance = createFile(SIZE);
        int attributeSize =  DataCloudTags.PKT_SIZE + NAME.length();
        assertEquals(attributeSize, instance.getAttributeSize());
        
        final String owner = "Manoel Campos";
        instance.setOwnerName(owner);
        attributeSize += owner.length();
        assertEquals(attributeSize, instance.getAttributeSize());
    }

    @Test
    public void testSetDatacenterId() {
        System.out.println("setResourceID");
        File instance = createFile();
        final int datacenterId = 1;
        assertTrue(instance.setDatacenterId(datacenterId));
        assertEquals(datacenterId, instance.getDatacenterId(), 0);
    }

    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "a-randomly-chosen-file-name.txt";
        File instance = createFile();
        instance.setName(name);
        assertEquals(name, instance.getName());
    }

    @Test
    public void testIsValid_String() {
        System.out.println("isValid");
        assertTrue(File.isValid("new-file.txt"));
        assertTrue(File.isValid("file1.txt"));
        assertFalse(File.isValid(""));
        
        final String nullStr = null;
        assertFalse(File.isValid(nullStr));
        assertTrue(File.isValid("file with blank spaces.txt"));
        assertFalse(File.isValid("      "));
    }

    @Test
    public void testIsValid_File() {
        System.out.println("isValid");
        final File nullFile = null;
        assertFalse(File.isValid(nullFile));
    }

    @Test
    public void testSetOwnerName() {
        System.out.println("setOwnerName");
        String owner = "Manoel Campos";
        File instance = createFile();
        assertTrue(instance.setOwnerName(owner));
        assertEquals(owner, instance.getOwnerName());
    }

    @Test
    public void testSetSize() {
        System.out.println("setSize");
        final int fileSize = 512;
        File instance = createFile(SIZE);
        assertEquals(SIZE, instance.getSize());
        assertTrue(instance.setSize(fileSize));
        assertEquals(fileSize, instance.getSize());
        
        assertFalse(instance.setSize(-1));
        assertEquals(fileSize, instance.getSize());
        
        final int zero = 0;
        assertTrue(instance.setSize(zero));
        assertEquals(zero, instance.getSize());
    }

    @Test
    public void testSetUpdateTime() {
        System.out.println("setUpdateTime");
        final double time = 10;
        File instance = createFile();
        assertEquals(0, instance.getLastUpdateTime(), 0.0);
        
        assertTrue(instance.setUpdateTime(time));
        assertEquals(time, instance.getLastUpdateTime(), 0.0);
    }

    @Test
    public void testSetRegistrationID() {
        System.out.println("setRegistrationID");
        File instance = createFile();
        assertFalse(instance.isRegistered());

        final int id0 = 0;
        assertTrue(instance.setRegistrationID(0));
        assertEquals(id0, instance.getRegistrationID());
        assertTrue(instance.isRegistered());
        
        final int id1 = 1;
        assertTrue(instance.setRegistrationID(id1));
        assertEquals(id1, instance.getRegistrationID());
        assertTrue(instance.isRegistered());
        
        assertFalse(instance.setRegistrationID(-1));
        assertEquals(id1, instance.getRegistrationID());
        assertTrue(instance.isRegistered());
    }

    @Test
    public void testSetType() {
        System.out.println("setType");
        File instance = createFile();

        final int type1 = 1;
        assertTrue(instance.setType(type1));
        assertEquals(type1, instance.getType(), 0);

        final int type0 = 0;
        assertTrue(instance.setType(type0));
        assertEquals(type0, instance.getType(), 0);
        
        final int type2 = 2;
        assertTrue(instance.setType(type2));
        assertEquals(type2, instance.getType(), 0);

        assertFalse(instance.setType(-1));
        assertEquals(type2, instance.getType(), 0);
    }

    @Test
    public void testSetChecksum() {
        System.out.println("setChecksum");
        File instance = createFile();

        final int checksum1 = 1;
        assertTrue(instance.setChecksum(checksum1));
        assertEquals(checksum1, instance.getChecksum(), 0);

        final int checksum0 = 0;
        assertTrue(instance.setChecksum(checksum0));
        assertEquals(checksum0, instance.getChecksum(), 0);
        
        final int checksum2 = 2;
        assertTrue(instance.setChecksum(checksum2));
        assertEquals(checksum2, instance.getChecksum(), 0);

        assertFalse(instance.setChecksum(-1));
        assertEquals(checksum2, instance.getChecksum(), 0);   
    }

    @Test
    public void testSetCost() {
        System.out.println("setCost");
        final double cost = 10;
        File instance = createFile();
        
        assertTrue(instance.setCost(cost));
        assertEquals(cost, instance.getCost(), 0.0);
        assertFalse(instance.setCost(-1));
        
        final double zero = 0;
        assertTrue(instance.setCost(zero));
        assertEquals(zero, instance.getCost(), 0.0);
        
        final double newCost = 20;
        assertTrue(instance.setCost(newCost));
        assertEquals(newCost, instance.getCost(), 0.0);
    }

    @Test
    public void testSetMasterCopy() {
        System.out.println("setMasterCopy");
        File instance = createFile();
        assertTrue(instance.isMasterCopy());
        
        instance.setMasterCopy(false);
        assertFalse(instance.isMasterCopy());
        
        instance.setMasterCopy(true);
        assertTrue(instance.isMasterCopy());
    }


    @Test
    public void testSetReadOnly() {
        System.out.println("setReadOnly");
        File instance = createFile();
        assertFalse(instance.isReadOnly());
        
        instance.setReadOnly(true);
        assertTrue(instance.isReadOnly());
        
        instance.setReadOnly(false);
        assertFalse(instance.isReadOnly());
    }

    @Test
    public void testSetTransactionTime() {
        System.out.println("setTransactionTime");
        final double time1 = 1, zero = 0;
        File instance = createFile();
        assertEquals(zero, instance.getTransactionTime(), zero);
        
        assertTrue(instance.setTransactionTime(time1));
        assertEquals(time1, instance.getTransactionTime(), 0);

        assertTrue(instance.setTransactionTime(zero));
        assertEquals(zero, instance.getTransactionTime(), 0);

        final double time2 = 2;
        assertTrue(instance.setTransactionTime(time2));
        assertEquals(time2, instance.getTransactionTime(), 0);
        
        assertFalse(instance.setTransactionTime(-1));
        assertEquals(time2, instance.getTransactionTime(), 0);
    }

    @Test
    public void testToString() {
        System.out.println("toString");
        File instance = createFile(NAME);
        assertEquals(NAME, instance.toString());
    }
    
}
