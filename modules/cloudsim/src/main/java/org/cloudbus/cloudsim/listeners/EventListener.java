package org.cloudbus.cloudsim.listeners;

/**
 *
 * An interface to define objects that listen to changes in a
 * given {@link Observable} object. By this way, the EventListener gets notified
 when the observed object has its state changed.
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> The class of the object being observed
 * @param <D> The class of the information to be given
 * to the listener when the observed object has its state changed.
 */
public interface EventListener<T, D> {
    /**
     * Get notified when the observed object (also called subject of observation) has changed.
     * This method has to be called by the observed objects to notify
     * its state change to the observers.
     * 
     * @param time The time the event occurred
     * @param observed The observed object that its state has been changed.
     * @param data The data about the state of the observed object.
     */
    void update(double time, T observed, D data);
   
    /**
     * A implementation of Null Object pattern that makes nothing (it doesn't
     * perform any operation on each existing method). 
     * The pattern is used to avoid NullPointerException's
     * and checking everywhere if a listener object is not null 
     * in order to call its methods.
     */
    public static final EventListener NULL = new EventListener<Object, Object>(){
        @Override
        public void update(double time, Object observed, Object data) {}
    };
}
