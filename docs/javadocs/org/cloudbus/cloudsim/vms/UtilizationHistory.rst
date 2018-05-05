.. java:import:: org.cloudbus.cloudsim.core Machine

.. java:import:: org.cloudbus.cloudsim.datacenters Datacenter

.. java:import:: java.util List

UtilizationHistory
==================

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: public interface UtilizationHistory

   Stores resource utilization data for a specific \ :java:ref:`Machine`\ .

   :author: Anton Beloglazov, Manoel Campos da Silva Filho

Fields
------
DEF_MAX_HISTORY_ENTRIES
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field::  int DEF_MAX_HISTORY_ENTRIES
   :outertype: UtilizationHistory

   The maximum number of entries that will be stored.

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

.. java:method::  List<Double> getHistory()
   :outertype: UtilizationHistory

   Gets a \ **read-only**\  CPU utilization percentage history (between [0 and 1], where 1 is 100%). Each value into the returned array is the CPU utilization percentage for a time interval equal to the \ :java:ref:`Datacenter.getSchedulingInterval()`\ .

   \ **The values are stored in the reverse chronological order.**\

getMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method::  int getMaxHistoryEntries()
   :outertype: UtilizationHistory

   Gets the maximum number of entries to store in the history.

getPreviousTime
^^^^^^^^^^^^^^^

.. java:method::  double getPreviousTime()
   :outertype: UtilizationHistory

   Gets the previous time that cloudlets were processed.

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

isEnabled
^^^^^^^^^

.. java:method::  boolean isEnabled()
   :outertype: UtilizationHistory

   Checks if the object is enabled to add data to the history.

setMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method::  void setMaxHistoryEntries(int maxHistoryEntries)
   :outertype: UtilizationHistory

   Sets the maximum number of entries to store in the history.

   :param maxHistoryEntries: the value to set

setPreviousTime
^^^^^^^^^^^^^^^

.. java:method::  void setPreviousTime(double previousTime)
   :outertype: UtilizationHistory

   Sets the previous time that cloudlets were processed.

   :param previousTime: the new previous time

