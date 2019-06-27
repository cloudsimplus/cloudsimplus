package org.cloudbus.cloudsim.util;

/**
 * Utility class that provides a set of methods for unit conversion.
 *
 * @author Manoel Campos da Silva Filho
 * @see TimeUtil
 * @see MathUtil
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
     * The value of 1 KiloByte in Bytes or 1 Kilo-bit in bits.
     * It is declared as double because such a value is commonly used
     * in divisions. This way, it avoids explicit double casts
     * to ensure a double instead an integer division.
     */
    public static final double KILO = 1024;

    /**
     * The value of 1 MegaByte in Bytes or 1 Mega-bit in bits.
     * @see #KILO
     */
    public static final double MEGA = KILO * KILO;

    /**
     * The value of 1 GigaByte in Bytes or 1 Giga-bit in bits.
     * @see #MEGA
     */
    public static final double GIGA = MEGA * KILO;

    /**
     * The value of 1 TeraByte in Bytes or 1 Tera-bit in bits.
     * @see #GIGA
     */
    public static final double TERA = GIGA * KILO;

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
        return bytes / MEGA;
    }

    /**
     * Converts a value in bytes to GigaBytes (GB)
     * @param bytes the value in bytes
     * @return the value in GigaBytes (GB)
     */
    public static double bytesToGigaBytes(final double bytes){
        return bytes / GIGA;
    }

    /**
     * Converts a value in bytes to KiloBytes (KB)
     * @param bytes the value in bytes
     * @return the value in KiloBytes (KB)
     */
    public static double bytesToKiloBytes(final double bytes){
        return bytes / KILO;
    }

    /**
     * Converts a value in bytes to the most suitable unit, such as Kilobytes (KB), MegaBytes (MB) or
     * Gigabytes (GB)
     * @param bytes the value in bytes
     * @return the converted value concatenated with the unit converted to (KB, MB or GB)
     */
    public static String bytesToStr(final double bytes){
        if(bytes < KILO) {
            return String.format("%.0f bytes", bytes);
        }

        if(bytes < MEGA) {
            return String.format("%.1f KB", bytesToKiloBytes(bytes));
        }

        if(bytes < GIGA) {
            return String.format("%.1f MB", bytesToMegaBytes(bytes));
        }

        return String.format("%.1f GB", bytesToGigaBytes(bytes));
    }

    /**
     * Converts a value in bytes to Megabits (Mb)
     * @param bytes the value in bytes
     * @return the value in Megabits (Mb)
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
     * doesn't matter if the unit is Kilobits (Kb), Megabits (Mb), Gigabits (Gb), etc.
     *
     * @param bits the value in bites, Kb, Mb, Gb, etc
     * @return the value in bites, Kbytes, Mbytes, Gbytes and so on, according to the given value
     */
    public static double bitesToBytes(final double bits){
        return bits / 8.0;
    }

    /**
     * Converts a value in MegaBytes (MB) to bytes
     * @param megaBytes the value in MegaBytes (MB)
     * @return the value in bytes
     */
    public static double megaBytesToBytes(final double megaBytes){
        return megaBytes * MEGA;
    }

    /**
     * Converts any value in mega to giga,
     * doesn't matter if it's megabits or megabytes.
     *
     * @param mega the value in megabits or megabytes
     * @return the value in gigabits or gigabytes (according to the input value)
     */
    public static double megaToGiga(final double mega){
        return mega / KILO;
    }

    /**
     * Converts any value in mega to tera,
     * doesn't matter if it's megabits or megabytes.
     *
     * @param mega the value in megabits or megabytes
     * @return the value in terabits or terabytes (according to the input value)
     */
    public static double megaToTera(final double mega){
        return mega / MEGA;
    }

    /**
     * Converts any value in giga to mega,
     * doesn't matter if it's gigabits or gigabytes.
     *
     * @param giga the value in gigabits or gigabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double gigaToMega(final double giga){
        return giga * KILO;
    }

    /**
     * Converts any value in tera to mega,
     * doesn't matter if it's terabits or terabytes.
     *
     * @param tera the value in terabits or terabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double teraToMega(final double tera){
        return teraToGiga(tera) * KILO;
    }

    /**
     * Converts any value in tera to giga,
     * doesn't matter if it's terabits or terabytes.
     *
     * @param tera the value in terabits or terabytes
     * @return the value in gigabits or gigabytes (according to the input value)
     */
    public static double teraToGiga(final double tera){
        return tera * KILO;
    }

    /**
     * Converts a boolean value to int
     * @param bool the boolean value to convert
     * @return 1 if the boolean value is true, 0 otherwise.
     */
    public static int boolToInt(final boolean bool){
        return bool ? 1 : 0;
    }
}
