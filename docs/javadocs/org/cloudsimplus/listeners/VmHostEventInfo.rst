.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.vms Vm

VmHostEventInfo
===============

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface VmHostEventInfo extends VmEventInfo, HostEventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Vm`\  that is related to some \ :java:ref:`Host`\ .

   It can be used to notify Listeners when a Host is \ :java:ref:`Vm.addOnHostAllocationListener(EventListener)`\  allocated} to or \ :java:ref:`Vm.addOnHostDeallocationListener(EventListener)`\  deallocated} from a given Vm, when a Vm has its \ :java:ref:`Vm.addOnUpdateProcessingListener(EventListener)`\  processing updated by its Host}, etc.

   *

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Vm.addOnHostAllocationListener(EventListener)`, :java:ref:`Vm.addOnHostDeallocationListener(EventListener)`, :java:ref:`Vm.addOnUpdateProcessingListener(EventListener)`

Methods
-------
of
^^

.. java:method:: static VmHostEventInfo of(EventListener<? extends EventInfo> listener, Vm vm)
   :outertype: VmHostEventInfo

   Gets a VmHostEventInfo instance from the given parameters. The \ :java:ref:`Host <getHost()>`\  attribute is defined as the \ :java:ref:`Host`\  where the \ :java:ref:`Vm`\  is running and the \ :java:ref:`getTime()`\  is the current simulation time.

   :param listener: the listener to be notified about the event
   :param vm: \ :java:ref:`Vm`\  that fired the event

of
^^

.. java:method:: static VmHostEventInfo of(EventListener<? extends EventInfo> listener, Vm vm, Host host)
   :outertype: VmHostEventInfo

   Gets a VmHostEventInfo instance from the given parameters. The \ :java:ref:`getTime()`\  is the current simulation time.

   :param listener: the listener to be notified about the event
   :param vm: \ :java:ref:`Vm`\  that fired the event
   :param host: \ :java:ref:`Host`\  that the \ :java:ref:`Vm`\  is related to. Such a Host can be that one where the Vm is or was placed, or where the Vm was tried to be be created, depending on the fired event, such as the \ :java:ref:`Vm.addOnHostAllocationListener(EventListener)`\  OnHostAllocation} or \ :java:ref:`Vm.addOnHostDeallocationListener(EventListener)`\  OnHostDeallocation}

