.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.core Simulation

.. java:import:: org.cloudbus.cloudsim.core UniquelyIdentificable

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: org.cloudbus.cloudsim.utilizationmodels UtilizationModel

.. java:import:: org.cloudbus.cloudsim.vms Vm

.. java:import:: org.cloudsimplus.listeners CloudletVmEventInfo

.. java:import:: org.cloudsimplus.listeners EventListener

.. java:import:: java.text DecimalFormat

.. java:import:: java.util ArrayList

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Objects

CloudletAbstract.ExecutionInDatacenterInfo
==========================================

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: protected static class ExecutionInDatacenterInfo
   :outertype: CloudletAbstract

   Internal class that keeps track of Cloudlet's movement in different \ :java:ref:`Datacenters <Datacenter>`\ . Each time a cloudlet is run on a given Datacenter, the cloudlet's execution history on each Datacenter is registered at \ :java:ref:`getLastExecutionInDatacenterInfo()`\

Fields
------
NULL
^^^^

.. java:field:: static final ExecutionInDatacenterInfo NULL
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

actualCpuTime
^^^^^^^^^^^^^

.. java:field::  double actualCpuTime
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   The total time the Cloudlet spent being executed in a Datacenter.

arrivalTime
^^^^^^^^^^^

.. java:field::  double arrivalTime
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   Cloudlet's submission (arrival) time to a Datacenter or \ :java:ref:`NOT_ASSIGNED`\  if the Cloudlet was not assigned to a Datacenter yet.

costPerSec
^^^^^^^^^^

.. java:field::  double costPerSec
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   Cost per second a Datacenter charge to execute this Cloudlet.

dc
^^

.. java:field::  Datacenter dc
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   a Datacenter where the Cloudlet will be executed

finishedSoFar
^^^^^^^^^^^^^

.. java:field::  long finishedSoFar
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   Cloudlet's length finished so far (in MI).

wallClockTime
^^^^^^^^^^^^^

.. java:field::  double wallClockTime
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

   The time this Cloudlet resides in a Datacenter (from arrival time until departure time, that may include waiting time).

Constructors
------------
ExecutionInDatacenterInfo
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor::  ExecutionInDatacenterInfo()
   :outertype: CloudletAbstract.ExecutionInDatacenterInfo

