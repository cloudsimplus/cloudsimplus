package org.cloudbus.cloudsim.util;

/**
 * A class that provides a set of methods for unit conversion.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class Conversion {

    /**
     * A value that represents 100% in a scale from 0 to 1.
     */
    public static final double HUNDRED_PERCENT = 1.0;

    /** One million in absolute value,
     * usually used to convert to and from
     * Number of Instructions (I) and  Million Instructions (MI) units. */
    public static final int MILLION = 1_000_000;

    /**
     * The value of 1 KiloByte in Bytes.
     * It is declared as double because such a value is commonly used
     * in divisions. By this way, it avoids explicit double casts
     * to ensure a double instead an integer division.
     */
    public static final double KILOBYTE = 1024;

    /**
     * The value of 1 MegaByte in Bytes.
     * @see #KILOBYTE
     */
    public static final double MEGABYTE = KILOBYTE * KILOBYTE;

    /**
     * Converts a value in bytes to MegaBytes (MB)
     * @param bytes the value in bytes
     * @return the value in MegaBytes (MB)
     */
    public static double bytesToMegaBytes(double bytes){
        return bytes / MEGABYTE;
    }

    /**
     * Converts a value in bytes to Megabites (Mb)
     * @param bytes the value in bytes
     * @return the value in Megabites (Mb)
     */
    public static double bytesToMegaBites(double bytes){
        return bytesToMegaBytes(bytes) * 8;
    }

    /**
     * A private constructor to avoid class instantiation.
     */
    private Conversion(){}
}
