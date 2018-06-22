package org.cloudbus.cloudsim.util;

/**
 * Provides a set of methods for unit conversion.
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
     * The value of 1 GibaByte in Bytes.
     * @see #MEGABYTE
     */
    public static final double GIGABYTE = MEGABYTE * MEGABYTE;

    /**
     * The value of 1 TeraByte in Bytes.
     * @see #GIGABYTE
     */
    public static final double TERABYTE = GIGABYTE * GIGABYTE;

    /**
     * A private constructor to avoid class instantiation.
     */
    private Conversion(){}

    /**
     * Converts a value in bytes to MegaBytes (MB)
     * @param bytes the value in bytes
     * @return the value in MegaBytes (MB)
     */
    public static double bytesToMegaBytes(final double bytes){
        return bytes / MEGABYTE;
    }

    /**
     * Converts a value in bytes to Megabites (Mb)
     * @param bytes the value in bytes
     * @return the value in Megabites (Mb)
     */
    public static double bytesToMegaBits(final double bytes){
        return bytesToBits(bytesToMegaBytes(bytes));
    }

    /**
     * Converts any value in bytes to bits,
     * doesn't matter if the unit is Kilobytes (KB), Megabytes (MB), Gigabytes (GB), etc.
     *
     * @param bytes the value in bytes, KB, MB, GB, etc
     * @return the value in bites, Kbits, Mbits, Gbits and so on, according to the given value
     */
    public static double bytesToBits(final double bytes){
        return bytes * 8;
    }

    /**
     * Converts any value in bits to bytes,
     * doesn't matter if the unit is Kilobites (Kb), Megabites (Mb), Gigabites (Gb), etc.
     *
     * @param bits the value in bites, Kb, Mb, Gb, etc
     * @return the value in bites, Kbytes, Mbytes, Gbytes and so on, according to the given value
     */
    public static double bitesToBytes(final double bits){
        return bits / 8.0;
    }

    /**
     * Converts any value in giga to mega,
     * doesn't matter if it's gigabits or gigabytes.
     *
     * @param giga the value in gigabits or gigabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double gigaToMega(final double giga){
        return giga * MEGABYTE;
    }

    /**
     * Converts any value in tera to mega,
     * doesn't matter if it's terabits or terabytes.
     *
     * @param tera the value in terabits or terabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double teraToMega(final double tera){
        return gigaToMega(tera * GIGABYTE);
    }
}
