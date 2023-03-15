package org.cloudsimplus.resources;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HarddriveStorageTest {
    private static final int CAPACITY = 1000;

    @Test()
    public void testNewHarddriveStorageWhenOnlyWhiteSpacesName() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("   ", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage("", CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWheNullName() {
        assertThrows(NullPointerException.class, () -> new HarddriveStorage(null, CAPACITY));
    }

    @Test()
    public void testNewHarddriveStorageWhenNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new HarddriveStorage(-1));
    }

    @Test
    public void testNewHarddriveStorageWhenZeroSize() {
        final int expResult = 0;
        final HarddriveStorage hd = new HarddriveStorage(expResult);
        assertEquals(expResult, hd.getCapacity());
    }

    @Test
    public void testGetCapacity() {
        final HarddriveStorage instance = createHardDrive(CAPACITY);
        assertEquals(CAPACITY, instance.getCapacity());
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
    public void testGetName() {
        final String expResult = "hd1";
        final HarddriveStorage instance = createHardDrive(CAPACITY, expResult);
        assertEquals(expResult, instance.getName());
    }

    @Test()
    public void testSetLatencyNegative() {
        final HarddriveStorage instance = createHardDrive();
        assertThrows(IllegalArgumentException.class, () -> instance.setLatency(-1));
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
        assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(-1));
    }

    @Test()
    public void testSetMaxTransferRate0() {
        final HarddriveStorage instance = createHardDrive();
        assertThrows(IllegalArgumentException.class, () -> instance.setMaxTransferRate(0));
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
