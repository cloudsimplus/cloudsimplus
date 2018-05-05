.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

EventInfo
=========

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface EventInfo

   A general interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given simulation entity such as a \ :java:ref:`Datacenter`\ , \ :java:ref:`Host`\ , \ :java:ref:`Vm`\ , \ :java:ref:`Cloudlet`\  and so on.

   There is not implementing class for such interfaces because instances of them are just Data Type Objects (DTO) that just store data and do not have business rules. Each interface that extends this one has a \ ``getInstance()``\  method to create an object from that interface. Such method uses the JDK8 static methods for interfaces to provide such a feature e reduce the number of classes, providing a simpler design.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`DatacenterEventInfo`, :java:ref:`HostEventInfo`, :java:ref:`VmEventInfo`, :java:ref:`CloudletEventInfo`

Methods
-------
getListener
^^^^^^^^^^^

.. java:method::  EventListener<? extends EventInfo> getListener()
   :outertype: EventInfo

   Gets the listener that was notified about the event.

getTime
^^^^^^^

.. java:method::  double getTime()
   :outertype: EventInfo

   Gets the time the event happened.

of
^^

.. java:method:: static EventInfo of(EventListener<? extends EventInfo> listener, double time)
   :outertype: EventInfo

   Gets a EventInfo instance from the given parameters.

   :param listener: the listener to be notified about the event
   :param time: the time the event happened

