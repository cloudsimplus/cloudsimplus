package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.util.DataCloudTags;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class FileTest {
    private static final String OWNER = "Manoel Campos";
    private static final String NAME = "file1.txt";
    private static final int SIZE = 100;

    private File createFile(){
        return createFile(NAME);
    }

    private File createFile(final String name){
        return new File(name, SIZE);
    }

    private File createFile(final int size){
        return new File(NAME, size);
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorWhenFileParameterIsNull() {
        final File nullFile = null;
        new File(nullFile);
    }

    @Test()
    public void testCreateFileWithFileParameter() {
        final File originalFile = new File(NAME, 100);
        final File copyFile = new File(originalFile);
        assertFalse(copyFile.isMasterCopy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWhenNameIsNull() {
        new File(null, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWhenZeroSize() {
        new File(NAME, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWhenNegativeSize() {
        new File(NAME, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFileWhenEmptyName() {
        new File("", 100);
    }

    @Test
    public void testMakeReplica() {
        final File instance = createFile();
        final File replica = instance.makeReplica();
        assertEquals(instance.getName(), replica.getName());
        assertEquals(instance.getSize(), replica.getSize());
        assertTrue(instance.isMasterCopy());
        assertFalse(replica.isMasterCopy());
    }

    @Test
    public void testMakeMasterCopy() {
        final File instance = createFile();
        final File replica = instance.makeMasterCopy();
        assertEquals(instance.getName(), replica.getName());
        assertEquals(instance.getSize(), replica.getSize());
        assertTrue(instance.isMasterCopy());
        assertTrue(replica.isMasterCopy());
    }

    @Test
    public void testGetAttributeSize() {
        final File instance = createFile(SIZE);
        int attributeSize =  DataCloudTags.PKT_SIZE + NAME.length();
        assertEquals(attributeSize, instance.getAttributeSize());

        final String owner = OWNER;
        instance.setOwnerName(owner);
        attributeSize += owner.length();
        assertEquals(attributeSize, instance.getAttributeSize());
    }

    @Test(expected = NullPointerException.class)
    public void testSetDatacenterToNull() {
        final File instance = createFile();
        instance.setDatacenter(null);
    }

    @Test
    public void testSetName() {
        final String name = "a-randomly-chosen-file-name.txt";
        final File instance = createFile();
        instance.setName(name);
        assertEquals(name, instance.getName());
    }

    @Test
    public void testIsValidWhenParamString() {
        assertTrue(File.isValid("new-file.txt"));
        assertTrue(File.isValid(NAME));
        assertFalse(File.isValid(""));

        final String nullStr = null;
        assertFalse(File.isValid(nullStr));
        assertTrue(File.isValid("file with blank spaces.txt"));
        assertFalse(File.isValid("      "));
    }

    @Test
    public void testIsValidWhenParamNullFile() {
        final File nullFile = null;
        assertFalse(File.isValid(nullFile));
    }

    @Test
    public void testSetOwnerName() {
        final String owner = OWNER;
        final File instance = createFile();
        assertTrue(instance.setOwnerName(owner));
        assertEquals(owner, instance.getOwnerName());
    }

    @Test
    public void testSetSize() {
        final int fileSize = 512;
        final File instance = createFile(SIZE);
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
        final double time = 10;
        final File instance = createFile();
        assertEquals(0, instance.getLastUpdateTime(), 0.0);

        assertTrue(instance.setUpdateTime(time));
        assertEquals(time, instance.getLastUpdateTime(), 0.0);
    }

    @Test
    public void testSetRegistrationID() {
        final File instance = createFile();
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
        final File instance = createFile();

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
        final File instance = createFile();

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
        final double cost = 10;
        final File instance = createFile();

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
        final File instance = createFile();
        assertTrue(instance.isMasterCopy());

        instance.setMasterCopy(false);
        assertFalse(instance.isMasterCopy());

        instance.setMasterCopy(true);
        assertTrue(instance.isMasterCopy());
    }


    @Test
    public void testSetReadOnly() {
        final File instance = createFile();
        assertFalse(instance.isDeleted());

        instance.setDeleted(true);
        assertTrue(instance.isDeleted());

        instance.setDeleted(false);
        assertFalse(instance.isDeleted());
    }

    @Test
    public void testSetTransactionTime() {
        final double time1 = 1, zero = 0;
        final File instance = createFile();
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
        final File instance = createFile(NAME);
        assertEquals(NAME, instance.toString());
    }
}
