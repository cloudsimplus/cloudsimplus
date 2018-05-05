.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmDatacenterEventInfo
=====================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface VmDatacenterEventInfo extends VmEventInfo, DatacenterEventInfo

   An interface that represent data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Vm`\  running inside a \ :java:ref:`Datacenter`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Vm.addOnCreationFailureListener(EventListener)`

Methods
-------
of
^^

.. java:method:: static VmDatacenterEventInfo of(EventListener<? extends EventInfo> listener, Vm vm)
   :outertype: VmDatacenterEventInfo

   Gets a VmDatacenterEventInfo instance from the given parameters. The \ :java:ref:`Datacenter <getDatacenter()>`\  attribute is defined as the \ :java:ref:`Datacenter`\  where the \ :java:ref:`Vm`\  is running and the \ :java:ref:`getTime()`\  is the current simulation time..

   :param listener: the listener to be notified about the event
   :param vm: the \ :java:ref:`Vm`\  that fired the event

of
^^

.. java:method:: static VmDatacenterEventInfo of(EventListener<? extends EventInfo> listener, Vm vm, Datacenter datacenter)
   :outertype: VmDatacenterEventInfo

   Gets a VmDatacenterEventInfo instance from the given parameters. The \ :java:ref:`getTime()`\  is the current simulation time.

   :param listener: the listener to be notified about the event
   :param vm: the \ :java:ref:`Vm`\  that fired the event
   :param datacenter: \ :java:ref:`Datacenter`\  that the \ :java:ref:`Vm`\  is related to. Such a Datacenter can be that one where the Vm is or was placed, or where the Vm was tried to be be created, depending on the fired event, such as the \ :java:ref:`Vm.addOnCreationFailureListener(EventListener)`\  OnVmCreationFailure}

