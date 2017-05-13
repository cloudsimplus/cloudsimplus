PowerModelSqrt
==============

.. java:package:: org.cloudbus.cloudsim.power.models
   :noindex:

.. java:type:: public class PowerModelSqrt extends PowerModelAbstract

   Implements a power model where the power consumption is the square root of the resource usage.

   If you are using any algorithms, policies or workload included in the power package please cite the following paper:

   ..

   * \ `Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012 <http://dx.doi.org/10.1002/cpe.1867>`_\

   :author: Anton Beloglazov

Constructors
------------
PowerModelSqrt
^^^^^^^^^^^^^^

.. java:constructor:: public PowerModelSqrt(double maxPower, double staticPowerPercent)
   :outertype: PowerModelSqrt

   Instantiates a new power model sqrt.

   :param maxPower: the max power
   :param staticPowerPercent: the static power percent

Methods
-------
getConstant
^^^^^^^^^^^

.. java:method:: protected double getConstant()
   :outertype: PowerModelSqrt

   Gets the constant.

   :return: the constant

getMaxPower
^^^^^^^^^^^

.. java:method:: protected double getMaxPower()
   :outertype: PowerModelSqrt

   Gets the max power.

   :return: the max power

getPowerInternal
^^^^^^^^^^^^^^^^

.. java:method:: @Override protected double getPowerInternal(double utilization) throws IllegalArgumentException
   :outertype: PowerModelSqrt

getStaticPower
^^^^^^^^^^^^^^

.. java:method:: protected final double getStaticPower()
   :outertype: PowerModelSqrt

   Gets the static power.

   :return: the static power

setConstant
^^^^^^^^^^^

.. java:method:: protected final void setConstant(double constant)
   :outertype: PowerModelSqrt

   Sets the constant.

   :param constant: the new constant

setMaxPower
^^^^^^^^^^^

.. java:method:: protected final void setMaxPower(double maxPower)
   :outertype: PowerModelSqrt

   Sets the max power.

   :param maxPower: the new max power

setStaticPower
^^^^^^^^^^^^^^

.. java:method:: protected final void setStaticPower(double staticPower)
   :outertype: PowerModelSqrt

   Sets the static power.

   :param staticPower: the new static power

