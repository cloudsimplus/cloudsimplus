.. java:import:: java.util Collections

.. java:import:: java.util SortedMap

UtilizationHistoryNull
======================

.. java:package:: org.cloudbus.cloudsim.vms
   :noindex:

.. java:type:: final class UtilizationHistoryNull implements UtilizationHistory

   A class that implements the Null Object Design Pattern for \ :java:ref:`UtilizationHistory`\  objects.

   :author: Manoel Campos da Silva Filho

   **See also:** :java:ref:`UtilizationHistory.NULL`

Methods
-------
addUtilizationHistory
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void addUtilizationHistory(double time)
   :outertype: UtilizationHistoryNull

disable
^^^^^^^

.. java:method:: @Override public void disable()
   :outertype: UtilizationHistoryNull

enable
^^^^^^

.. java:method:: @Override public void enable()
   :outertype: UtilizationHistoryNull

getHistory
^^^^^^^^^^

.. java:method:: @Override public SortedMap<Double, Double> getHistory()
   :outertype: UtilizationHistoryNull

getMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public int getMaxHistoryEntries()
   :outertype: UtilizationHistoryNull

getUtilizationMad
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationMad()
   :outertype: UtilizationHistoryNull

getUtilizationMean
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationMean()
   :outertype: UtilizationHistoryNull

getUtilizationVariance
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public double getUtilizationVariance()
   :outertype: UtilizationHistoryNull

getVm
^^^^^

.. java:method:: @Override public Vm getVm()
   :outertype: UtilizationHistoryNull

isEnabled
^^^^^^^^^

.. java:method:: @Override public boolean isEnabled()
   :outertype: UtilizationHistoryNull

setMaxHistoryEntries
^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void setMaxHistoryEntries(int maxHistoryEntries)
   :outertype: UtilizationHistoryNull

