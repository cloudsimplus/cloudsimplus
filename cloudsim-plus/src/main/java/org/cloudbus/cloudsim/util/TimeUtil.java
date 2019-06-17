package org.cloudbus.cloudsim.util;

/**
 * Utility class that provides some methods to deal with time units.
 * It's not used the {@link java.time.Duration}
 * and {@link java.time.Period} classes because
 * they don't work with double type.
 * Therefore, it's not possible for them to deal with
 * time fractions, such as 2.5 hours.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 * @see MathUtil
 */
public final class TimeUtil {
    /** Number of seconds in one minute. */
    private static final double MINUTE_SECS = 60;
    private static final double HOUR_SECS   = 60 * MINUTE_SECS;
    private static final double DAY_SECS    = 24 * HOUR_SECS;
    private static final double MONTH_SECS  = 30 * DAY_SECS;
    private static final double YEAR_SECS   = 12 * MONTH_SECS;

    /** A private default constructor to avoid class instantiation. */
    private TimeUtil(){}

    /**
     * Converts a given amount of seconds to the most suitable unit, i.e.,
     * the highest unit that results in the lower converted value.
     * For instance, if a value such as 80400 seconds is given, it will be converted to
     * 1 day. It is not converted to hour, for instance, because it will return 24 (hours):
     * a value which is higher than 1 (day).
     *
     * @param seconds the number of seconds to convert to a suitable unit
     * @return a String containing the converted value followed by the name of the converted unit
     *         (e.g. "2.6 days")
     */
    public static String secondsToStr(final double seconds) {
        if(seconds < 60) {
            return convertSeconds(seconds, 1, "second");
        }

        if(seconds < HOUR_SECS) {
            return convertSeconds(seconds, 60, "minute");
        }

        if(seconds < DAY_SECS) {
            return convertSeconds(seconds, HOUR_SECS, "hour");
        }

        if(seconds < MONTH_SECS) {
            return convertSeconds(seconds, DAY_SECS, "day");
        }

        if(seconds < YEAR_SECS) {
            return convertSeconds(seconds, MONTH_SECS, "month");
        }

        return convertSeconds(seconds, YEAR_SECS, "year");
    }

    /**
     * Converts an amount of seconds to a time unit defined by a conversion factor.
     * @param seconds the amount of seconds to convert to a time unit
     * @param conversionFactor the conversion factor used to divide the number of seconds to convert to the desired unit
     * @param unit the name of the unit to convert to (such as second, minute, hour, day, month or year)
     * @return a String containing the value converted to the given unit, followed by the name of the unit
     */
    private static String convertSeconds(final double seconds, final double conversionFactor, final String unit){
        final double convertedTime = seconds/conversionFactor;
        return String.format("%.2f %s", convertedTime, convertedTime >= 2 ? unit+"s" : unit);
    }

    /**
     * Gets the computer actual time in seconds.
     * @return
     */
    public static double currentTimeSecs() {
        return System.currentTimeMillis()/1000.0;
    }

    /**
     * Gets the elapsed time from the given time in seconds.
     * @param startTimeSeconds the start time in seconds
     * @return the elapsed time in seconds
     */
    public static double elapsedSeconds(final double startTimeSeconds){
        return currentTimeSecs() - startTimeSeconds;
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
     * Converts a value in hours to minutes.
     * <p>The existing {@link java.util.concurrent.TimeUnit} and {@link java.time.Duration} classes
     * don't provide the double precision required here.</p>
     *
     * @param hours the value in hours
     * @return the value in minutes
     */
    public static double hoursToMinutes(final double hours) {
        return hours*60.0;
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
}
