.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

DatacenterEventInfo
===================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface DatacenterEventInfo extends EventInfo

   An interface that represent data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Datacenter`\ .

   :author: Manoel Campos da Silva Filho

Methods
-------
getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: DatacenterEventInfo

   Gets the \ :java:ref:`Datacenter`\  for which the event happened.

