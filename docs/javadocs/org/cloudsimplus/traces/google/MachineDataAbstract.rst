MachineDataAbstract
===================

.. java:package:: org.cloudsimplus.traces.google
   :noindex:

.. java:type:: abstract class MachineDataAbstract

   A base class that stores data to identify a machine. It has to be extended by classes that read trace files containing some machine data (such as the ID of a machine to be created or the ID of a machine where a task should run).

   :author: Manoel Campos da Silva Filho

Methods
-------
getMachineId
^^^^^^^^^^^^

.. java:method:: public long getMachineId()
   :outertype: MachineDataAbstract

   Gets the machineID that indicates the machine onto which the task was scheduled. If the field is empty, -1 is returned instead.

setMachineId
^^^^^^^^^^^^

.. java:method::  MachineDataAbstract setMachineId(long machineId)
   :outertype: MachineDataAbstract

