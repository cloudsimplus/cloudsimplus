package org.cloudbus.cloudsim.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ConversionTest {
    private static final double ONE_KILOBIT_IN_BITS = 1024;
    private static final double ONE_KILOBYTE_IN_BYTES = 1024;
    private static final double ONE_MEGABYTE_IN_BYTES = 1024*1024;


    @Test
    public void bytesToMegaBytes(){
        final float expectedMegaBytes = 1;
        assertEquals(expectedMegaBytes, Conversion.bytesToMegaBytes(ONE_MEGABYTE_IN_BYTES), 0);
    }

    @Test
    public void bytesToMegaBites(){
        final double expectedMegaBits = 8;
        assertEquals(expectedMegaBits, Conversion.bytesToMegaBits(ONE_MEGABYTE_IN_BYTES), 0);
    }

    @Test
    public void bytesToBites(){
        final double expectedBits = 8192;
        assertEquals(expectedBits, Conversion.bytesToBits(ONE_KILOBYTE_IN_BYTES),0);
    }

    @Test
    public void bitesToBytes(){
        final double expectedBytes = 128;
        assertEquals(expectedBytes, Conversion.bitesToBytes(ONE_KILOBIT_IN_BITS),0);
    }

}
