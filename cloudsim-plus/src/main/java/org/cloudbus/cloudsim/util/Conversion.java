package org.cloudbus.cloudsim.util;

/**
 * An utility class that provides a set of methods for unit conversion.
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
     * The value of 1 KiloByte in Bytes or 1 Kilobit in bits.
     * It is declared as double because such a value is commonly used
     * in divisions. This way, it avoids explicit double casts
     * to ensure a double instead an integer division.
     */
    public static final double KILO = 1024;

    /**
     * The value of 1 MegaByte in Bytes or 1 Megabit in bits.
     * @see #KILO
     */
    public static final double MEGA = KILO * KILO;

    /**
     * The value of 1 GigaByte in Bytes or 1 Gigabit in bits.
     * @see #MEGA
     */
    public static final double GIGA = MEGA * KILO;

    /**
     * The value of 1 TeraByte in Bytes or 1 TeraBit in bits.
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
    public static String bytesToSuitableUnit(final double bytes){
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
     * Converts a value in MegaBytes (MB) to bytes
     * @param megaBytes the value in MegaBytes (MB)
     * @return the value in bytes
     */
    public static double megaBytesToBytes(final double megaBytes){
        return megaBytes * MEGA;
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
     * Converts any value in micro (μ) to milli (m) scale,
     * such as microseconds to milliseconds.
     *
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param micro the value in micro (μ) scale
     * @return the value in milli (m) scale
     */
    public static double microToMilli(final double micro){
        return micro/1000.0;
    }

    /**
     * Converts a value in microseconds (μ) to seconds.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param micro the value in microseconds (μ)
     * @return the value in seconds
     */
    public static double microToSeconds(final double micro) {
        return microToMilli(micro)/1000.0;
    }

    /**
     * Converts a value in minutes to seconds.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param minutes the value in minutes
     * @return the value in seconds
     */
    public static double minutesToSeconds(final double minutes) {
        return minutes*60.0;
    }

    /**
     * Converts a value in milliseconds to minutes.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param milli the value in milliseconds
     * @return the value in minutes
     */
    public static double millisecsToMinutes(final long milli) {
        return milli/(1000.0*60);
    }

    /**
     * Converts a value in seconds to minutes.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param seconds the value in seconds
     * @return the value in minutes
     */
    public static double secondsToMinutes(final double seconds) {
        return seconds/60.0;
    }

    /**
     * Converts a value in seconds to hours.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param seconds the value in seconds
     * @return the value in hours
     */
    public static double secondsToHours(final double seconds) {
        return secondsToMinutes(seconds)/60.0;
    }

    /**
     * Converts a value in seconds to days.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param seconds the value in seconds
     * @return the value in days
     */
    public static double secondsToDays(final double seconds) {
        return hoursToDays(secondsToHours(seconds));
    }

    /**
     * Converts a value in hours to days.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param hours the value in hours
     * @return the value in days
     */
    public static double hoursToDays(final double hours) {
        return hours/24.0;
    }

    /**
     * Converts a value in hours to seconds.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param hours the value in hours
     * @return the value in seconds
     */
    public static double hoursToSeconds(final double hours) {
        return minutesToSeconds(hours*60.0);
    }

    /**
     * Converts a value in days to seconds.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param days the value in days
     * @return the value in seconds
     */
    public static double daysToSeconds(final double days) {
        return hoursToSeconds(days*24.0);
    }

    /**
     * Converts a value in months to an <b>approximated</b> number of seconds,
     * since it considers every month has 30 days.
     *
     * <p>The existing {@link java.util.concurrent.TimeUnit}, {@link java.time.Duration}
     * and {@link java.time.Period} classes
     * don't provide the double precision required here.</p>
     *
     * @param months the value in months
     * @return the value in seconds
     */
    public static double monthsToSeconds(final double months) {
        return daysToSeconds(months*30.0);
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
