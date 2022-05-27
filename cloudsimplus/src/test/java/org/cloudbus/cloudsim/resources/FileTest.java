package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.util.DataCloudTags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test()
    public void testCopyConstructorWhenFileParameterIsNull() {
        assertThrows(NullPointerException.class, () -> new File(null));
    }

    @Test()
    public void testCreateFileWithFileParameter() {
        final File originalFile = new File(NAME, 100);
        final File copyFile = new File(originalFile);
        assertFalse(copyFile.isMasterCopy());
    }

    @Test()
    public void testCreateWhenNameIsNull() {
        assertThrows(NullPointerException.class, () -> new File(null, 100));
    }

    @Test()
    public void testCreateWhenZeroSize() {
        assertThrows(IllegalArgumentException.class, () -> new File(NAME, 0));
    }

    @Test()
    public void testCreateWhenNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new File(NAME, -1));
    }

    @Test()
    public void testCreateFileWhenEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new File("", 100));
    }

    @Test
    public void testMakeReplica() {
        final File instance = createFile();
        final File replica = instance.makeReplica();
        assertAll(
            () -> assertEquals(instance.getName(), replica.getName()),
            () -> assertEquals(instance.getSize(), replica.getSize()),
            () -> assertTrue(instance.isMasterCopy()),
            () -> assertFalse(replica.isMasterCopy())
        );
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

    @Test()
    public void testSetDatacenterToNull() {
        final File instance = createFile();
        assertThrows(NullPointerException.class, () -> instance.setDatacenter(null));
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
        final String newFileName = "new-file.txt";
        assertEquals(newFileName, File.validateFileName(newFileName));
        assertEquals(NAME, File.validateFileName(NAME));
        assertThrows(IllegalArgumentException.class, () -> File.validateFileName(""));

        assertThrows(NullPointerException.class, () -> File.validateFileName(null));
        final String nameWithSpaces = "file with blank spaces.txt";
        assertEquals(nameWithSpaces, File.validateFileName(nameWithSpaces));
        assertThrows(IllegalArgumentException.class, () -> File.validateFileName("      "));
    }

    @Test
    public void testIsValidWhenParamNullFile() {
        assertThrows(NullPointerException.class, () -> File.validate(null));
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
        assertEquals(0, instance.getLastUpdateTime());

        assertTrue(instance.setUpdateTime(time));
        assertEquals(time, instance.getLastUpdateTime());
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
        assertEquals(type1, instance.getType());

        final int type0 = 0;
        assertTrue(instance.setType(type0));
        assertEquals(type0, instance.getType());

        final int type2 = 2;
        assertTrue(instance.setType(type2));
        assertEquals(type2, instance.getType());

        assertFalse(instance.setType(-1));
        assertEquals(type2, instance.getType());
    }

    @Test
    public void testSetChecksum() {
        final File instance = createFile();

        final int checksum1 = 1;
        assertTrue(instance.setChecksum(checksum1));
        assertEquals(checksum1, instance.getChecksum());

        final int checksum0 = 0;
        assertTrue(instance.setChecksum(checksum0));
        assertEquals(checksum0, instance.getChecksum());

        final int checksum2 = 2;
        assertTrue(instance.setChecksum(checksum2));
        assertEquals(checksum2, instance.getChecksum());

        assertFalse(instance.setChecksum(-1));
        assertEquals(checksum2, instance.getChecksum());
    }

    @Test
    public void testSetCost() {
        final double cost = 10;
        final File instance = createFile();

        assertTrue(instance.setCost(cost));
        assertEquals(cost, instance.getCost());
        assertFalse(instance.setCost(-1));

        final double zero = 0;
        assertTrue(instance.setCost(zero));
        assertEquals(zero, instance.getCost());

        final double newCost = 20;
        assertTrue(instance.setCost(newCost));
        assertEquals(newCost, instance.getCost());
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
        final double time1 = 1;
        final double zero = 0;
        final File instance = createFile();
        assertEquals(zero, instance.getTransactionTime());

        assertTrue(instance.setTransactionTime(time1));
        assertEquals(time1, instance.getTransactionTime());

        assertTrue(instance.setTransactionTime(zero));
        assertEquals(zero, instance.getTransactionTime());

        final double time2 = 2;
        assertTrue(instance.setTransactionTime(time2));
        assertEquals(time2, instance.getTransactionTime());

        assertFalse(instance.setTransactionTime(-1));
        assertEquals(time2, instance.getTransactionTime());
    }

    @Test
    public void testToString() {
        final File instance = createFile(NAME);
        assertEquals(NAME, instance.toString());
    }
}
