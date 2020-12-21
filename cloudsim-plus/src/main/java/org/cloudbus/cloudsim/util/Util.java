package org.cloudbus.cloudsim.util;

/**
 * A class with general purpose utilities.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 */
public final class Util {
    /** A private default constructor to avoid class instantiation. */
    private Util(){/**/}

    /**
     * Makes the current thread to sleep for a given amount ot milliseconds.
     * @param millis the time to sleep in milliseconds
     */
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
