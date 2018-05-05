.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.vms Vm

CloudletVmEventInfo
===================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface CloudletVmEventInfo extends CloudletEventInfo, VmEventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified when some events happen for a given \ :java:ref:`Cloudlet`\  running inside a \ :java:ref:`Vm`\ .

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Cloudlet.addOnUpdateProcessingListener(EventListener)`, :java:ref:`Cloudlet.addOnFinishListener(EventListener)`

Methods
-------
of
^^

.. java:method:: static CloudletVmEventInfo of(EventListener<? extends EventInfo> listener, Cloudlet cloudlet, Vm vm)
   :outertype: CloudletVmEventInfo

   Gets a CloudletVmEventInfo instance from the given parameters. The \ :java:ref:`getTime()`\  is the current simulation time.

   :param listener: the listener to be notified about the event
   :param cloudlet: the \ :java:ref:`Cloudlet`\  that fired the event
   :param vm: the \ :java:ref:`Vm`\  where the Cloudlet is or was running into, depending on the fired event, such as the \ :java:ref:`OnUpdateCloudletProcessing <Cloudlet.addOnUpdateProcessingListener(EventListener)>`\  or \ :java:ref:`OnCloudletFinish <Cloudlet.addOnFinishListener(EventListener)>`\

of
^^

.. java:method:: static CloudletVmEventInfo of(EventListener<? extends EventInfo> listener, double time, Cloudlet cloudlet)
   :outertype: CloudletVmEventInfo

   Gets a CloudletVmEventInfo instance from the given parameters. The \ :java:ref:`Vm <getVm()>`\  attribute is defined as the \ :java:ref:`Vm`\  where the \ :java:ref:`Cloudlet`\  is running.

   :param time: the time the event happened
   :param cloudlet: the \ :java:ref:`Cloudlet`\  that fired the event

   **See also:** :java:ref:`.of(EventListener,Cloudlet,Vm)`

of
^^

.. java:method:: static CloudletVmEventInfo of(EventListener<? extends EventInfo> listener, Cloudlet cloudlet)
   :outertype: CloudletVmEventInfo

   Gets a CloudletVmEventInfo instance from the given parameters. The \ :java:ref:`Vm <getVm()>`\  attribute is defined as the \ :java:ref:`Vm`\  where the \ :java:ref:`Cloudlet`\  is running and the \ :java:ref:`getTime()`\  is the current simulation time.

   :param cloudlet: the \ :java:ref:`Cloudlet`\  that fired the event

   **See also:** :java:ref:`.of(EventListener,Cloudlet,Vm)`

of
^^

.. java:method:: static CloudletVmEventInfo of(EventListener<? extends EventInfo> listener, double time, Cloudlet cloudlet, Vm vm)
   :outertype: CloudletVmEventInfo

   Gets a CloudletVmEventInfo instance from the given parameters.

   :param listener: the listener to be notified about the event
   :param time: the time the event happened
   :param cloudlet: the \ :java:ref:`Cloudlet`\  that fired the event
   :param vm: the \ :java:ref:`Vm`\  where the Cloudlet is or was running into, depending on the fired event, such as the \ :java:ref:`OnUpdateCloudletProcessing <Cloudlet.addOnUpdateProcessingListener(EventListener)>`\  or \ :java:ref:`OnCloudletFinish <Cloudlet.addOnFinishListener(EventListener)>`\

