.. java:import:: org.cloudbus.cloudsim.hosts Host

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.resources Resource

.. java:import:: org.cloudbus.cloudsim.resources Resourceful

.. java:import:: org.cloudbus.cloudsim.vms Vm

Machine
=======

.. java:package:: org.cloudbus.cloudsim.core
   :noindex:

.. java:type:: public interface Machine extends ChangeableId, Resourceful

   Represents either a: (i) Physical Machine (PM) which implements the interface \ :java:ref:`Host`\ ; or (ii) Virtual Machine (VM), which implements the interface \ :java:ref:`Vm`\ .

   :author: Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  Machine NULL
   :outertype: Machine

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`Machine`\  objects.

Methods
-------
getBw
^^^^^

.. java:method::  Resource getBw()
   :outertype: Machine

   Gets the machine bandwidth (bw) capacity in Megabits/s.

   :return: the machine bw capacity

getIdleInterval
^^^^^^^^^^^^^^^

.. java:method::  double getIdleInterval()
   :outertype: Machine

   Gets the interval interval the Machine has been idle.

   :return: the idle time interval (in seconds) or 0 if the Machine is not idle

getLastBusyTime
^^^^^^^^^^^^^^^

.. java:method::  double getLastBusyTime()
   :outertype: Machine

   Gets the last time the Machine was running some process.

   :return: the last busy time (in seconds)

getMips
^^^^^^^

.. java:method::  double getMips()
   :outertype: Machine

   Gets the individual MIPS capacity of any machine's \ :java:ref:`Pe`\ , considering that all PEs have the same capacity.

   :return: the MIPS capacity of a single \ :java:ref:`Pe`\

getNumberOfPes
^^^^^^^^^^^^^^

.. java:method::  long getNumberOfPes()
   :outertype: Machine

   Gets the overall number of \ :java:ref:`Pe`\ s the machine has, that include PEs of all statuses, including failed PEs.

   :return: the machine's number of PEs

getRam
^^^^^^

.. java:method::  Resource getRam()
   :outertype: Machine

   Gets the machine memory resource in Megabytes.

   :return: the machine memory

getSimulation
^^^^^^^^^^^^^

.. java:method::  Simulation getSimulation()
   :outertype: Machine

   Gets the CloudSim instance that represents the simulation the Entity is related to.

getStorage
^^^^^^^^^^

.. java:method::  Resource getStorage()
   :outertype: Machine

   Gets the storage device of the machine with capacity in Megabytes.

   :return: the machine storage device

getTotalMipsCapacity
^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getTotalMipsCapacity()
   :outertype: Machine

   Gets total MIPS capacity of all PEs of the machine.

   :return: the total MIPS of all PEs

isIdle
^^^^^^

.. java:method::  boolean isIdle()
   :outertype: Machine

   Checks if the Machine is currently idle.

   :return: true if the Machine currently idle, false otherwise

isIdleEnough
^^^^^^^^^^^^

.. java:method::  boolean isIdleEnough(double time)
   :outertype: Machine

   Checks if the Machine has been idle for a given amount of time (in seconds).

   :param time: the time interval to check if the Machine has been idle (in seconds). If time is zero, it will be checked if the Machine is currently idle. If it's negative, even if the Machine is idle, it's considered that it isn't idle enough. This is useful if you don't want to perform any operation when the machine becomes idle (for instance, if idle machines might be shut down and a negative value is given, they won't).
   :return: true if the Machine has been idle as long as the given time, false if it's active of isn't idle long enough

validateCapacity
^^^^^^^^^^^^^^^^

.. java:method:: static void validateCapacity(double capacity)
   :outertype: Machine

   Validates a capacity for a machine resource.

   :param capacity: the capacity to check

