package org.cloudbus.cloudsim.util;

/**
 *
 * Defines common constants, used throughout CloudSim.
 *
 * @author nikolay.grozev
 *
 */
public final class Consts {

    /**
     * A value that represents 100% in a scale from 0 to 1.
     */
    public static final double HUNDRED_PERCENT = 1.0;

    /** One million in absolute value,
     * usually used to convert to and from
     * Number of Instructions (I) and  Million Instructions (MI) units. */
    public static final int MILLION = 1000000;

    // ================== Time constants ==================
    /** One minute in seconds. */
    public static final int MINUTE = 60;

    /** One hour in seconds. */
    public static final int HOUR = 60 * MINUTE;

    /** One day in seconds. */
    public static final int DAY = 24 * HOUR;

    /** One week in seconds.
     */
    public static final int WEEK = 7 * DAY;

    // ================== OS constants ==================
    /** Constant for *nix Operating Systems. */
    public static final String NIX_OS = "Linux/Unix";

    /** Constant for Windows Operating Systems. */
    public static final String WINDOWS = "Windows";

    /**
     * A private constructor to avoid class instantiation.
     */
    private Consts(){}
}
