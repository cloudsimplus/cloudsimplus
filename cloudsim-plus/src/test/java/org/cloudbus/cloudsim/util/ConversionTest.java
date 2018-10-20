package org.cloudbus.cloudsim.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ConversionTest {
    private static final double ONE_MEGABYTE_IN_BYTES = 1048576;

    @Test
    public void kilo(){
        final double expectedBytes = 1024;
        assertEquals(expectedBytes, Conversion.KILO);
    }

    @Test
    public void mega(){
        final double expectedBytes = ONE_MEGABYTE_IN_BYTES;
        assertEquals(expectedBytes, Conversion.MEGA);
    }

    @Test
    public void giga(){
        final double expectedBytes = 1073741824;
        assertEquals(expectedBytes, Conversion.GIGA);
    }

    @Test
    public void tera(){
        final double expectedBytes = 1099511627776.0;
        assertEquals(expectedBytes, Conversion.TERA);
    }

    @Test
    public void bytesToMegaBytes(){
        final float expectedMegaBytes = 1;
        assertEquals(expectedMegaBytes, Conversion.bytesToMegaBytes(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToMegaBites(){
        final double expectedMegaBits = 8;
        assertEquals(expectedMegaBits, Conversion.bytesToMegaBits(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToBites(){
        final double expectedBits = 8192;
        assertEquals(expectedBits, Conversion.bytesToBits(1024));
    }

    @Test
    public void bitesToBytes(){
        final double expectedBytes = 128;
        assertEquals(expectedBytes, Conversion.bitesToBytes(1024));
    }

    @Test
    public void gigaToMega(){
        final double giga = 1;
        final double expectedMB = 1024;
        assertEquals(expectedMB, Conversion.gigaToMega(giga));
    }

    @Test
    public void teraToGiga(){
        final double tera = 1;
        final double expectedGB = 1024;
        assertEquals(expectedGB, Conversion.teraToGiga(tera));
    }

    @Test
    public void teraToMega(){
        final double tera = 1;
        final double expectedMB = 1048576;
        assertEquals(expectedMB, Conversion.teraToMega(tera));
    }

    @Test
    public void microToMilli1(){
        final double micro = 1;
        final double expectedMilli = 0.001;
        assertEquals(expectedMilli, Conversion.microToMilli(micro));
    }

    @Test
    public void microToMilli1000(){
        final double micro = 1000;
        final double expectedMilli = 1;
        assertEquals(expectedMilli, Conversion.microToMilli(micro));
    }
}
