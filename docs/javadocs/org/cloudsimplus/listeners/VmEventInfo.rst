.. java:import:: org.cloudbus.cloudsim.vms Vm

VmEventInfo
===========

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface VmEventInfo extends EventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Vm`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`VmDatacenterEventInfo`, :java:ref:`CloudletVmEventInfo`

Methods
-------
getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: VmEventInfo

   Gets the \ :java:ref:`Vm`\  for which the event happened.

