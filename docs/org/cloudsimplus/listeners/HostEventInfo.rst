.. java:import:: org.cloudbus.cloudsim.hosts Host

HostEventInfo
=============

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface HostEventInfo extends EventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Host`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmHostEventInfo`, :java:ref:`HostUpdatesVmsProcessingEventInfo`

Methods
-------
getHost
^^^^^^^

.. java:method::  Host getHost()
   :outertype: HostEventInfo

   Gets the \ :java:ref:`Host`\  for which the event happened.

