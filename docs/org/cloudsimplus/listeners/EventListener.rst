.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

EventListener
=============

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: @FunctionalInterface public interface EventListener<T extends EventInfo>

   An interface to define Observers (Listeners) that listen to specific changes in the state of a given observable object (Subject). By this way, the EventListener gets notified when the observed object has its state changed.

   The interface was defined allowing the Subject object to have more than one state to be observable. If the subject directly implements this interface, it will allow only one kind of state change to be observable. If the Subject has multiple state changes to be observed, it can define multiple EventListener attributes to allow multiple events to be observed.

   Such Listeners are used for many simulation entities such as \ :java:ref:`Vm`\  and \ :java:ref:`Cloudlet`\ . Check the documentation of such interfaces that provides some Listeners.

   :author: Manoel Campos da Silva Filho
   :param <T>: The class of the object containing information to be given to the listener when the expected event happens.

Fields
------
NULL
^^^^

.. java:field::  EventListener NULL
   :outertype: EventListener

   A implementation of Null Object pattern that makes nothing (it doesn't perform any operation on each existing method). The pattern is used to avoid NullPointerException's and checking everywhere if a listener object is not null in order to call its methods.

Methods
-------
update
^^^^^^

.. java:method::  void update(T info)
   :outertype: EventListener

   Gets notified when the observed object (also called subject of observation) has changed. This method has to be called by the observed objects to notify its state change to the listener.

   :param info: The data about the happened event.

