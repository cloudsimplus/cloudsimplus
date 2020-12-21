package org.cloudbus.cloudsim.util;

/**
 * A class with general purpose utilities.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 */
public final class Util {
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
