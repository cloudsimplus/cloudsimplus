package org.cloudsimplus.listeners;

/**
 * An interface which defines the basic methods
 * that an object representing data about
 * a happened event must have.
 *
 * Classes implementing this interface are to
 * pass information about an happened event to an {@link EventListener}
 * object.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface EventInfo {
    /**
     * Gets the time the event happened.
     *
     * @return
     */
   double getTime();
}
