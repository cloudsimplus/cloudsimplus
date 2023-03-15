package org.cloudsimplus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class BytesConversionTest {
    private static final double ONE_MEGABYTE_IN_BYTES = 1048576;

    @Test
    public void kilo(){
        final double expectedBytes = 1024;
        assertEquals(expectedBytes, BytesConversion.KILO);
    }

    @Test
    public void mega(){
        final double expectedBytes = ONE_MEGABYTE_IN_BYTES;
        assertEquals(expectedBytes, BytesConversion.MEGA);
    }

    @Test
    public void giga(){
        final double expectedBytes = 1073741824;
        assertEquals(expectedBytes, BytesConversion.GIGA);
    }

    @Test
    public void tera(){
        final double expectedBytes = 1099511627776.0;
        assertEquals(expectedBytes, BytesConversion.TERA);
    }

    @Test
    public void bytesToMegaBytes(){
        final float expectedMegaBytes = 1;
        assertEquals(expectedMegaBytes, BytesConversion.bytesToMegaBytes(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToMegaBites(){
        final double expectedMegaBits = 8;
        assertEquals(expectedMegaBits, BytesConversion.bytesToMegaBits(ONE_MEGABYTE_IN_BYTES));
    }

    @Test
    public void bytesToBites(){
        final double expectedBits = 8192;
        assertEquals(expectedBits, BytesConversion.bytesToBits(1024));
    }

    @Test
    public void bitesToBytes(){
        final double expectedBytes = 128;
        assertEquals(expectedBytes, BytesConversion.bitsToBytes(1024));
    }

    @Test
    public void gigaToMega(){
        final double giga = 1;
        final double expectedMB = 1024;
        assertEquals(expectedMB, BytesConversion.gigaToMega(giga));
    }

    @Test
    public void teraToGiga(){
        final double tera = 1;
        final double expectedGB = 1024;
        assertEquals(expectedGB, BytesConversion.teraToGiga(tera));
    }

    @Test
    public void teraToMega(){
        final double tera = 1;
        final double expectedMB = 1048576;
        assertEquals(expectedMB, BytesConversion.teraToMega(tera));
    }
}
