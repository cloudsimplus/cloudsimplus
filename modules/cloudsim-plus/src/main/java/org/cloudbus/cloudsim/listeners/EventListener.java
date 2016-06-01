package org.cloudbus.cloudsim.listeners;

/**
 *
 * An interface to define observer objects that listen to specific changes in 
 * the state of a given observable object, also called subject. 
 * By this way, the EventListener gets notified when
 * the observed object has its state changed.
 * The interface was defined allowing the
 * subject object to have more than one state
 * to be observable. If the subject directly implements
 * this interface, it will allow only one kind of
 * state change to be observable.
 * If the subject has multiple state changes to be observed,
 * it can define multiple properties of the EventListener class
 * to allow this multiple events to be observable.
 * See interfaces such as {@link org.cloudbus.cloudsim.Vm}
 * to get an overview of how this interface can be used.
 *
 * @author Manoel Campos da Silva Filho
 * @param <T> The class of the object containing information to be given to the
 * listener when the expected event happens.
 */
public interface EventListener<T extends EventInfo> {

    /**
     * Gets notified when the observed object (also called subject of
     * observation) has changed. This method has to be called by the observed
     * objects to notify its state change to the listener.
     *
     * @param event The data about the happened event.
     */
    void update(T event);

    /**
     * A implementation of Null Object pattern that makes nothing (it doesn't
     * perform any operation on each existing method). The pattern is used to
     * avoid NullPointerException's and checking everywhere if a listener object
     * is not null in order to call its methods.
     */
    EventListener NULL = (EventListener<EventInfo>) (EventInfo evt) -> {};
}
