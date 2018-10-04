.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core CloudSimTags

.. java:import:: org.cloudbus.cloudsim.core CustomerEntity

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core UniquelyIdentifiable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.datacenters DatacenterCharacteristics

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners CloudletVmEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: org.cloudsimplus.traces.google GoogleTaskEventsTraceReader

.. java:import:: java.util List

Cloudlet.Status
===============

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type::  enum Status
   :outertype: Cloudlet

   Status of Cloudlets

Enum Constants
--------------
CANCELED
^^^^^^^^

.. java:field:: public static final Cloudlet.Status CANCELED
   :outertype: Cloudlet.Status

   The Cloudlet has been canceled.

FAILED
^^^^^^

.. java:field:: public static final Cloudlet.Status FAILED
   :outertype: Cloudlet.Status

   The Cloudlet has failed.

FAILED_RESOURCE_UNAVAILABLE
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final Cloudlet.Status FAILED_RESOURCE_UNAVAILABLE
   :outertype: Cloudlet.Status

   The cloudlet has failed due to a resource failure.

FROZEN
^^^^^^

.. java:field:: public static final Cloudlet.Status FROZEN
   :outertype: Cloudlet.Status

   The Cloudlet is in the waiting queue but it won't be automatically moved to the execution list (even if there are available PEs) until its status is changed to \ :java:ref:`QUEUED`\ . This status is used specifically for Cloudlets created from a trace file, such as a \ :java:ref:`Google Cluster trace <GoogleTaskEventsTraceReader>`\ , that explicitly defines when tasks must start running.

INEXEC
^^^^^^

.. java:field:: public static final Cloudlet.Status INEXEC
   :outertype: Cloudlet.Status

   The Cloudlet is in execution in a Vm.

INSTANTIATED
^^^^^^^^^^^^

.. java:field:: public static final Cloudlet.Status INSTANTIATED
   :outertype: Cloudlet.Status

   The Cloudlet has been just instantiated but not assigned to a Datacenter yet.

PAUSED
^^^^^^

.. java:field:: public static final Cloudlet.Status PAUSED
   :outertype: Cloudlet.Status

   The Cloudlet has been paused. It can be resumed by changing the status into \ ``RESUMED``\ .

QUEUED
^^^^^^

.. java:field:: public static final Cloudlet.Status QUEUED
   :outertype: Cloudlet.Status

   The Cloudlet has moved to a Vm but it is in the waiting queue.

READY
^^^^^

.. java:field:: public static final Cloudlet.Status READY
   :outertype: Cloudlet.Status

   The Cloudlet has been assigned to a Datacenter to be executed as planned.

RESUMED
^^^^^^^

.. java:field:: public static final Cloudlet.Status RESUMED
   :outertype: Cloudlet.Status

   The Cloudlet has been resumed from \ ``PAUSED``\  state.

SUCCESS
^^^^^^^

.. java:field:: public static final Cloudlet.Status SUCCESS
   :outertype: Cloudlet.Status

   The Cloudlet has been executed successfully.

