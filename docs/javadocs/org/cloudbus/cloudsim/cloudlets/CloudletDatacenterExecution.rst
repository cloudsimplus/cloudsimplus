.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

CloudletDatacenterExecution
===========================

.. java:package:: org.cloudbus.cloudsim.cloudlets
   :noindex:

.. java:type:: final class CloudletDatacenterExecution

   Internal class that keeps track of Cloudlet's movement in different \ :java:ref:`Datacenters <Datacenter>`\ . Each time a cloudlet is run on a given Datacenter, the cloudlet's execution history on each Datacenter is registered at \ :java:ref:`CloudletAbstract.getLastExecutionInDatacenterInfo()`\

Fields
------
NULL
^^^^

.. java:field:: protected static final CloudletDatacenterExecution NULL
   :outertype: CloudletDatacenterExecution

Constructors
------------
CloudletDatacenterExecution
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor::  CloudletDatacenterExecution()
   :outertype: CloudletDatacenterExecution

Methods
-------
getActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method::  double getActualCpuTime()
   :outertype: CloudletDatacenterExecution

   The total time the Cloudlet spent being executed in a Datacenter.

getArrivalTime
^^^^^^^^^^^^^^

.. java:method::  double getArrivalTime()
   :outertype: CloudletDatacenterExecution

   Cloudlet's submission (arrival) time to a Datacenter or \ :java:ref:`Cloudlet.NOT_ASSIGNED`\  if the Cloudlet was not assigned to a Datacenter yet.

getCostPerSec
^^^^^^^^^^^^^

.. java:method::  double getCostPerSec()
   :outertype: CloudletDatacenterExecution

   Cost per second a Datacenter charge to execute this Cloudlet.

getDatacenter
^^^^^^^^^^^^^

.. java:method::  Datacenter getDatacenter()
   :outertype: CloudletDatacenterExecution

   a Datacenter where the Cloudlet will be executed

getFinishedSoFar
^^^^^^^^^^^^^^^^

.. java:method::  long getFinishedSoFar()
   :outertype: CloudletDatacenterExecution

   Cloudlet's length finished so far (in MI).

getWallClockTime
^^^^^^^^^^^^^^^^

.. java:method::  double getWallClockTime()
   :outertype: CloudletDatacenterExecution

   The time this Cloudlet resides in a Datacenter (from arrival time until departure time, that may include waiting time).

setActualCpuTime
^^^^^^^^^^^^^^^^

.. java:method::  void setActualCpuTime(double actualCpuTime)
   :outertype: CloudletDatacenterExecution

setArrivalTime
^^^^^^^^^^^^^^

.. java:method::  void setArrivalTime(double arrivalTime)
   :outertype: CloudletDatacenterExecution

setCostPerSec
^^^^^^^^^^^^^

.. java:method::  void setCostPerSec(double costPerSec)
   :outertype: CloudletDatacenterExecution

setDatacenter
^^^^^^^^^^^^^

.. java:method::  void setDatacenter(Datacenter datacenter)
   :outertype: CloudletDatacenterExecution

setFinishedSoFar
^^^^^^^^^^^^^^^^

.. java:method::  void setFinishedSoFar(long finishedSoFar)
   :outertype: CloudletDatacenterExecution

setWallClockTime
^^^^^^^^^^^^^^^^

.. java:method::  void setWallClockTime(double wallClockTime)
   :outertype: CloudletDatacenterExecution

