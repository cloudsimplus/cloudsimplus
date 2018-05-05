.. java:import:: org.cloudbus.cloudsim.cloudlets Cloudlet

.. java:import:: org.cloudbus.cloudsim.hosts Host

HostUpdatesVmsProcessingEventInfo
=================================

.. java:package:: org.cloudsimplus.listeners
   :noindex:

.. java:type:: public interface HostUpdatesVmsProcessingEventInfo extends HostEventInfo

   An interface that represents data to be passed to \ :java:ref:`EventListener`\  objects that are registered to be notified after a Host updates the processing of its VMs.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`Host.removeOnUpdateProcessingListener(EventListener)`

Methods
-------
getNextCloudletCompletionTime
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getNextCloudletCompletionTime()
   :outertype: HostUpdatesVmsProcessingEventInfo

   Gets the expected completion time of the next finishing cloudlet.

of
^^

.. java:method:: static HostUpdatesVmsProcessingEventInfo of(EventListener<? extends EventInfo> listener, Host host, double nextCloudletCompletionTime)
   :outertype: HostUpdatesVmsProcessingEventInfo

   Gets a \ ``HostUpdatesVmsProcessingEventInfo``\  instance from the given parameters.

   :param listener: the listener to be notified about the event
   :param host: the \ :java:ref:`Host`\  where the event happened
   :param nextCloudletCompletionTime: the expected time for completion of the next \ :java:ref:`Cloudlet`\

