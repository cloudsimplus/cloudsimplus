.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

CloudletEventInfo
=================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface CloudletEventInfo extends EventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Cloudlet`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`CloudletVmEventInfo`

Methods
-------
getCloudlet
^^^^^^^^^^^

.. java:method::  Cloudlet getCloudlet()
   :outertype: CloudletEventInfo

   Gets the \ :java:ref:`Cloudlet`\  for which the event happened.

