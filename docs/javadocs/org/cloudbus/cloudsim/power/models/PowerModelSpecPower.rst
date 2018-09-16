PowerModelSpecPower
===================

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public abstract class PowerModelSpecPower extends PowerModelAbstract

   The abstract class of power models created based on data from \ `SPECpower benchmark <http://www.spec.org/power_ssj2008/>`_\ .

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <https://doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Methods
-------
getMaxPower
^^^^^^^^^^^

.. java:method:: @Override public double getMaxPower()
   :outertype: PowerModelSpecPower

getPowerData
^^^^^^^^^^^^

.. java:method:: protected abstract double getPowerData(int index)
   :outertype: PowerModelSpecPower

   Gets the power consumption for a given utilization percentage.

   :param index: the utilization percentage in the scale from [0 to 10], where 10 means 100% of utilization.
   :return: the power consumption for the given utilization percentage

getPowerInternal
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double getPowerInternal(double utilization) throws IllegalArgumentException
   :outertype: PowerModelSpecPower

