.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: java.util SortedMap

UtilizationHistory
==================

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public interface UtilizationHistory

   Stores resource utilization data for a specific \ :java:ref:`Machine`\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
NULL
^^^^

.. java:field::  UtilizationHistory NULL
   :outertype: UtilizationHistory

   An attribute that implements the Null Object Design Pattern for \ :java:ref:`UtilizationHistory`\  objects.

Methods
-------
addUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  void addUtilizationHistory(double time)
   :outertype: UtilizationHistory

   Adds a CPU utilization percentage history value related to the current simulation time, to the beginning of the History List. \ **The value is added only if the utilization history .**\

   :param time: the current simulation time

cpuUsageFromHostCapacity
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double cpuUsageFromHostCapacity(double time)
   :outertype: UtilizationHistory

   Computes the percentage of the CPU the VM is using, relative the Host's total MIPS CAPACITY. If the capacity is 1000 MIPS and the VM is using 250 MIPS, it's equivalent to 25% of the Host's capacity

   :param time: the time to get the VM CPU utilization
   :return: the relative VM CPU usage percent (from 0 to 1)

disable
^^^^^^^

.. java:method::  void disable()
   :outertype: UtilizationHistory

   Disables the history to avoid utilization data to be added to it. That allows to reduce memory usage since no utilization data will be collected.

enable
^^^^^^

.. java:method::  void enable()
   :outertype: UtilizationHistory

   Enables the history so that utilization data can be added to it.

getHistory
^^^^^^^^^^

.. java:method::  SortedMap<Double, Double> getHistory()
   :outertype: UtilizationHistory

   Gets a \ **read-only**\  CPU utilization percentage history map where each key is the time the utilization was collected and each value is the utilization percentage (between [0 and 1]). There will be at least one entry for each time multiple of the \ :java:ref:`Datacenter.getSchedulingInterval()`\ . \ **This way, it's required to set a Datacenter scheduling interval with the desired value.**\

getMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method::  int getMaxHistoryEntries()
   :outertype: UtilizationHistory

   Gets the maximum number of entries to store in the history.

getUtilizationMad
^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationMad()
   :outertype: UtilizationHistory

   Gets the utilization Median Absolute Deviation (MAD) in MIPS.

getUtilizationMean
^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationMean()
   :outertype: UtilizationHistory

   Gets the utilization mean in MIPS.

getUtilizationVariance
^^^^^^^^^^^^^^^^^^^^^^

.. java:method::  double getUtilizationVariance()
   :outertype: UtilizationHistory

   Gets the utilization variance in MIPS.

   :return: the utilization variance in MIPS

getVm
^^^^^

.. java:method::  Vm getVm()
   :outertype: UtilizationHistory

isEnabled
^^^^^^^^^

.. java:method::  boolean isEnabled()
   :outertype: UtilizationHistory

   Checks if the object is enabled to add data to the history.

powerConsumption
^^^^^^^^^^^^^^^^

.. java:method::  double powerConsumption(double time)
   :outertype: UtilizationHistory

   Computes the amount of power the VM is using, relative to the total Host's power consumption (in watt-sec).

   :param time: the time to get the VM power consumption
   :return: the relative VM power consumption in watt-sec

setMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setMaxHistoryEntries(int maxHistoryEntries)
   :outertype: UtilizationHistory

   Sets the maximum number of entries to store in the history.

   :param maxHistoryEntries: the value to set

