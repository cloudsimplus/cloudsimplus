.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

ExecutionInDatacenterInfo
=========================

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: final class ExecutionInDatacenterInfo

   Internal class that keeps track of Cloudlet's movement in different \ :java:ref:`Datacenters <Datacenter>`\ . Each time a cloudlet is run on a given Datacenter, the cloudlet's execution history on each Datacenter is registered at \ :java:ref:`CloudletAbstract.getLastExecutionInDatacenterInfo()`\

Fields
------
NULL
^^^^

.. java:field:: protected static final ExecutionInDatacenterInfo NULL
   :outertype: ExecutionInDatacenterInfo

Constructors
------------
ExecutionInDatacenterInfo
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor::  ExecutionInDatacenterInfo()
   :outertype: ExecutionInDatacenterInfo

Methods
-------
getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method::  double getActualCpuTime()
   :outertype: ExecutionInDatacenterInfo

   The total time the Cloudlet spent being executed in a Datacenter.

getArrivalTime
^^^^^^^^^^^^^^

.. java:method::  double getArrivalTime()
   :outertype: ExecutionInDatacenterInfo

   Cloudlet's submission (arrival) time to a Datacenter or \ :java:ref:`Cloudlet.NOT_ASSIGNED`\  if the Cloudlet was not assigned to a Datacenter yet.

getCostPerSec
^^^^^^^^^^^^^

.. java:method::  double getCostPerSec()
   :outertype: ExecutionInDatacenterInfo

   Cost per second a Datacenter charge to execute this Cloudlet.

getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: ExecutionInDatacenterInfo

   a Datacenter where the Cloudlet will be executed

getFinishedSoFar
^^^^^^^^^^^^^^^^

.. java:method::  long getFinishedSoFar()
   :outertype: ExecutionInDatacenterInfo

   Cloudlet's length finished so far (in MI).

getWallClockTime
^^^^^^^^^^^^^^^^

.. java:method::  double getWallClockTime()
   :outertype: ExecutionInDatacenterInfo

   The time this Cloudlet resides in a Datacenter (from arrival time until departure time, that may include waiting time).

setActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method::  void setActualCpuTime(double actualCpuTime)
   :outertype: ExecutionInDatacenterInfo

setArrivalTime
^^^^^^^^^^^^^^

.. java:method::  void setArrivalTime(double arrivalTime)
   :outertype: ExecutionInDatacenterInfo

setCostPerSec
^^^^^^^^^^^^^

.. java:method::  void setCostPerSec(double costPerSec)
   :outertype: ExecutionInDatacenterInfo

setDatacenter
^^^^^^^^^^^^^

.. java:method::  void setDatacenter(Datacenter datacenter)
   :outertype: ExecutionInDatacenterInfo

setFinishedSoFar
^^^^^^^^^^^^^^^^

.. java:method::  void setFinishedSoFar(long finishedSoFar)
   :outertype: ExecutionInDatacenterInfo

setWallClockTime
^^^^^^^^^^^^^^^^

.. java:method::  void setWallClockTime(double wallClockTime)
   :outertype: ExecutionInDatacenterInfo

