package org.cloudbus.cloudsim;

/**
 * 
 * Defines common constants, used throughout CloudSim.
 * 
 * @author nikolay.grozev
 * 
 */
public interface Consts {

    /** One million in absolute value,
     * usually used to convert to and from
     * Number of Instructions (I) and  Million Instructions (MI) units. */
    int MILLION = 1000000;

    // ================== Time constants ==================
    /** One minute in seconds. */
    int MINUTE = 60;
    
    /** One hour in seconds. */
    int HOUR = 60 * MINUTE;
    
    /** One day in seconds. */
    int DAY = 24 * HOUR;
    
    /** One week in seconds. 
     */
    int WEEK = 7 * DAY;

    // ================== OS constants ==================
    /** Constant for *nix Operating Systems. */
    String NIX_OS = "Linux/Unix";
    
    /** Constant for Windows Operating Systems. */
    String WINDOWS = "Windows";
}
