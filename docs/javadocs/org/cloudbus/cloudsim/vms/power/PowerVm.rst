.. java:import:: java.util Collections

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: org.cloudbus.cloudsim.brokers DatacenterBroker

.. java:import:: org.cloudbus.cloudsim.resources Pe

.. java:import:: org.cloudbus.cloudsim.schedulers.cloudlet CloudletScheduler

.. java:import:: org.cloudbus.cloudsim.util MathUtil

.. java:import:: org.cloudbus.cloudsim.vms VmSimple

PowerVm
=======

.. java:package:: org.cloudbus.cloudsim.vms.power
   :noindex:

.. java:type:: public class PowerVm extends VmSimple

   A class of VM that stores its CPU utilization percentage history. The history is used by VM allocation and selection policies.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Fields
------
MAX_HISTORY_ENTRIES
^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final int MAX_HISTORY_ENTRIES
   :outertype: PowerVm

   The maximum number of entries that will be stored.

Constructors
------------
PowerVm
^^^^^^^

.. java:constructor:: public PowerVm(int id, long mipsCapacity, int numberOfPes)
   :outertype: PowerVm

   Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size. To change these values, use the respective setters. While the Vm \ :java:ref:`is not created inside a Host <isCreated()>`\ , such values can be changed freely.

   :param id: unique ID of the VM
   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

PowerVm
^^^^^^^

.. java:constructor:: public PowerVm(long mipsCapacity, int numberOfPes)
   :outertype: PowerVm

   Creates a Vm with 1024 MEGABYTE of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGABYTE of Storage Size and no ID (which will be defined when the VM is submitted to a \ :java:ref:`DatacenterBroker`\ ). To change these values, use the respective setters. While the Vm \ :java:ref:`is not created inside a Host <isCreated()>`\ , such values can be changed freely.

   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)

PowerVm
^^^^^^^

.. java:constructor:: @Deprecated public PowerVm(int id, DatacenterBroker broker, long mipsCapacity, int numberOfPes, int ramCapacity, long bwCapacity, long size, int priority, String vmm, CloudletScheduler cloudletScheduler, double schedulingInterval)
   :outertype: PowerVm

   Instantiates a new PowerVm.

   :param id: unique ID of the VM
   :param broker: ID of the VM's owner, that is represented by the id of the \ :java:ref:`DatacenterBroker`\
   :param mipsCapacity: the mips capacity of each Vm \ :java:ref:`Pe`\
   :param numberOfPes: amount of \ :java:ref:`Pe`\  (CPU cores)
   :param ramCapacity: amount of ram in Megabytes
   :param bwCapacity: amount of bandwidth to be allocated to the VM (in Megabits/s)
   :param size: size the VM image in Megabytes (the amount of storage it will use, at least initially).
   :param priority: the priority
   :param vmm: Virtual Machine Monitor that manages the VM lifecycle
   :param cloudletScheduler: scheduler that defines the execution policy for Cloudlets inside this Vm
   :param schedulingInterval: not used anymore

Methods
-------
addUtilizationHistoryValue
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void addUtilizationHistoryValue(double utilization)
   :outertype: PowerVm

   Adds a CPU utilization percentage history value.

   :param utilization: the CPU utilization percentage to add

getPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: public double getPreviousTime()
   :outertype: PowerVm

   Gets the previous time that cloudlets were processed.

getUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Double> getUtilizationHistory()
   :outertype: PowerVm

   Gets a \ **read-only**\  CPU utilization percentage history (between [0 and 1], where 1 is 100%).

getUtilizationMad
^^^^^^^^^^^^^^^^^

.. java:method:: public double getUtilizationMad()
   :outertype: PowerVm

   Gets the utilization Median Absolute Deviation (MAD) in MIPS.

getUtilizationMean
^^^^^^^^^^^^^^^^^^

.. java:method:: public double getUtilizationMean()
   :outertype: PowerVm

   Gets the utilization mean in MIPS.

getUtilizationVariance
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public double getUtilizationVariance()
   :outertype: PowerVm

   Gets the utilization variance in MIPS.

   :return: the utilization variance in MIPS

setPreviousTime
^^^^^^^^^^^^^^^

.. java:method:: public void setPreviousTime(double previousTime)
   :outertype: PowerVm

   Sets the previous time that cloudlets were processed.

   :param previousTime: the new previous time

updateProcessing
^^^^^^^^^^^^^^^^

.. java:method:: @Override public double updateProcessing(double currentTime, List<Double> mipsShare)
   :outertype: PowerVm

